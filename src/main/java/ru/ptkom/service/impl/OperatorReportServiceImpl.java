package ru.ptkom.service.impl;

import org.springframework.stereotype.Service;
import ru.ptkom.dao.CallDataDAO;
import ru.ptkom.dao.OperatorDAO;
import ru.ptkom.model.CallData;
import ru.ptkom.model.Operator;
import ru.ptkom.service.ConfigurationFIleService;
import ru.ptkom.service.MaliService;
import ru.ptkom.service.OperatorReportService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class OperatorReportServiceImpl implements OperatorReportService {

    private static String INCOMING;
    private static String OUTGOING;
    private static final String UNDEFINED_PLACEHOLDER = "Оператор не определен";
    private static final String ERROR = "Unable to make report of all operators";

    private final CallDataDAO callDataDAO;
    private final OperatorDAO operatorDAO;
    private final ConfigurationFIleService configurationFIleService;
    private final MaliService maliService;

    public OperatorReportServiceImpl(CallDataDAO callDataDAO, OperatorDAO operatorDAO, ConfigurationFIleService configurationFIleService, MaliService maliService) {
        this.callDataDAO = callDataDAO;
        this.operatorDAO = operatorDAO;
        this.configurationFIleService = configurationFIleService;
        INCOMING = configurationFIleService.getKeyOfIncomingReport();
        OUTGOING = configurationFIleService.getKeyOfOutgoingReport();
        this.maliService = maliService;
    }

    @Override
    public void sendReportToEmailByOperatorsList(Set<String> emails, List<Operator> operators, LocalDate startDate, LocalDate endDate) {
        CompletableFuture.runAsync(() -> maliService.sendReportByEmail(emails, getReportByOperatorList(operators, startDate, endDate), startDate.toString(), endDate.toString()));
    }

    @Override
    public void sendReportToEmailByOperatorsList(Set<String> emails, List<Operator> operators, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        CompletableFuture.runAsync(() -> maliService.sendReportByEmail(emails, getReportByOperatorList(operators, startDateTime, endDateTime), startDateTime.toString(), endDateTime.toString()));
    }

    @Override
    public void sendReportByEmailByOperatorIdsList(Set<String> emails, List<Long> operatorsIds, LocalDate startDate, LocalDate endDate) {
        try {
            CompletableFuture.runAsync(() -> maliService.sendReportByEmail(emails, getReportByOperatorIdList(operatorsIds, startDate, endDate), startDate.toString(), endDate.toString())).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendReportByEmailByOperatorIdsList(Set<String> emails, List<Long> operatorsIds, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        CompletableFuture.runAsync(() -> maliService.sendReportByEmail(emails, getReportByOperatorIdList(operatorsIds, startDateTime, endDateTime), startDateTime.toString(), endDateTime.toString()));
    }

    @Override
    public Map<String, Map<String, Long>> getReportOfAllOperator(LocalDate startDate, LocalDate endDate) {
        List<Operator> operators = operatorDAO.getOperators();
        return getReportByOperatorList(operators, startDate, endDate);
    }

    @Override
    public Map<String, Map<String, Long>> getReportOfAllOperator(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Operator> operators = operatorDAO.getOperators();
        return getReportByOperatorList(operators, startDateTime, endDateTime);
    }

    @Override
    public Map<String, Map<String, Long>> getReportByOperatorIdList(List<Long> operatorIds, LocalDate startDate, LocalDate endDate) {
        List<Operator> operators = operatorDAO.getOperatorsByIdList(operatorIds);
        return getReportByOperatorList(operators, startDate, endDate);
    }

    @Override
    public Map<String, Map<String, Long>> getReportByOperatorIdList(List<Long> operatorIds, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Operator> operators = operatorDAO.getOperatorsByIdList(operatorIds);
        return getReportByOperatorList(operators, startDateTime, endDateTime);
    }

    @Override
    public Map<String, Map<String, Long>> getReportByOperatorList(List<Operator> operators, LocalDate startDate, LocalDate endDate) {
        List<CompletableFuture<Map<String, Map<String, Long>>>> operatorCallDurationFutureList = operators.stream()
                .map(operator -> getOperatorCallDurationSumFuture(operator, startDate, endDate))
                .toList();
        Map<String, Map<String, Long>> result = setOutputMap(operatorCallDurationFutureList);
        result.put(INCOMING, sortByValue(result.get(INCOMING)));
        result.put(OUTGOING, sortByValue(result.get(OUTGOING)));
        return result;
    }

    @Override
    public Map<String, Map<String, Long>> getReportByOperatorList(List<Operator> operators, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<CompletableFuture<Map<String, Map<String, Long>>>> operatorCallDurationFutureList = operators.stream()
                .map(operator -> getOperatorCallDurationSumFuture(operator, startDateTime, endDateTime))
                .toList();
        Map<String, Map<String, Long>> result = setOutputMap(operatorCallDurationFutureList);
        result.put(INCOMING, sortByValue(result.get(INCOMING)));
        result.put(OUTGOING, sortByValue(result.get(OUTGOING)));
        return result;
    }

    @Override
    public String getOperatorReportById(Long id, LocalDate startDate, LocalDate endDate) {
        return null;
    }

    private  Map<String, Map<String, Long>> setOutputMap(List<CompletableFuture<Map<String, Map<String, Long>>>> operatorCallDurationFutureList) {
        Map<String, Map<String, Long>> result = new HashMap<>();
        result.put(INCOMING, new HashMap<String, Long>());
        result.put(OUTGOING, new HashMap<String, Long>());
        operatorCallDurationFutureList.stream().map(this::waitCompletableFutureEndAndGetResult)
                .filter(Objects::nonNull)
                .forEach(partialResult -> {
                    result.get(INCOMING).putAll(partialResult.get(INCOMING));
                    result.get(OUTGOING).putAll(partialResult.get(OUTGOING));
                });
        return result;
    }

    private CompletableFuture<Map<String, Map<String, Long>>> getOperatorCallDurationSumFuture(Operator operator, LocalDate startDate, LocalDate endDate) {
        Long id = operator.getId();
        return CompletableFuture.supplyAsync(() -> {
            List<CallData> operatorCallData = callDataDAO.getCallDataOfOperatorByPeriod(id, startDate, endDate);
            Map<String, Long> incomingCallSumByOperator = waitCompletableFutureEndAndGetResult(makeOperatorIncomingCallsReport(operatorCallData, id));
            Map<String, Long> outgoingCallSumByOperator = waitCompletableFutureEndAndGetResult(makeOperatorOutgoingCallsReport(operatorCallData, id));
            return setTemporaryResult(operator, incomingCallSumByOperator, outgoingCallSumByOperator);
        });
    }

    private CompletableFuture<Map<String, Map<String, Long>>> getOperatorCallDurationSumFuture(Operator operator, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Long id = operator.getId();
        return CompletableFuture.supplyAsync(() -> {
            List<CallData> operatorCallData = callDataDAO.getCallDataOfOperatorByPeriod(id, startDateTime, endDateTime);
            Map<String, Long> incomingCallSumByOperator = waitCompletableFutureEndAndGetResult(makeOperatorIncomingCallsReport(operatorCallData, id));
            Map<String, Long> outgoingCallSumByOperator = waitCompletableFutureEndAndGetResult(makeOperatorOutgoingCallsReport(operatorCallData, id));
            return setTemporaryResult(operator, incomingCallSumByOperator, outgoingCallSumByOperator);
        });
    }

    private Map<String, Map<String, Long>> setTemporaryResult(Operator operator, Map<String, Long> incomingCallSumByOperator, Map<String, Long> outgoingCallSumByOperator) {
        String key = operator.getName();
        Map<String, Map<String, Long>> temporaryResult = new HashMap<>();
        Optional<Long> incomingCallSecondSumByOperator = Optional.ofNullable(incomingCallSumByOperator.get(key));
        Optional<Long> outgoingCallSecondSumByOperator = Optional.ofNullable(outgoingCallSumByOperator.get(key));
        incomingCallSumByOperator.put(key, incomingCallSecondSumByOperator.orElse(0L)/60);
        temporaryResult.put(INCOMING, incomingCallSumByOperator);
        outgoingCallSumByOperator.put(key, outgoingCallSecondSumByOperator.orElse(0L)/60);
        temporaryResult.put(OUTGOING, outgoingCallSumByOperator);
        return temporaryResult;
    }

    private <T> T waitCompletableFutureEndAndGetResult(CompletableFuture<T> completableFuture) {
        try {
            return completableFuture.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<Map<String, Long>> makeOperatorOutgoingCallsReport(List<CallData> operatorCallData, Long id) {
        return CompletableFuture.supplyAsync(() -> {
            return operatorCallData.stream()
                    .filter(row -> {
                        if (row.getToOperator() != null) {
                            return row.getToOperator().getId().equals(id);
                        } else {
                            return false;
                        }
                    })
                    .collect(Collectors.groupingBy(
                            row -> row.getToOperator().getName(),
                            Collectors.summingLong(CallData::getCallDurationSeconds)
                    ));
        });
    }

    private CompletableFuture<Map<String, Long>> makeOperatorIncomingCallsReport(List<CallData> operatorCallData, Long id) {
        return CompletableFuture.supplyAsync(() -> {
            return operatorCallData.stream()
                    .filter(row -> {
                        if (row.getFromOperator() != null) {
                            return row.getFromOperator().getId().equals(id);
                        } else {
                            return false;
                        }
                    })
                    .collect(Collectors.groupingBy(
                            row -> row.getFromOperator().getName(),
                            Collectors.summingLong(CallData::getCallDurationSeconds)
                    ));
        });
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Deprecated
    public Map<String, Map<String, Long>> getReportByOperatorsListByCompletableFuture(List<Operator> operators, LocalDate startDate, LocalDate endDate) {
        long start = System.currentTimeMillis();
        Map<String, Map<String, Long>> result = new ConcurrentHashMap<>();
        result.put(INCOMING, new ConcurrentHashMap<String, Long>());
        result.put(OUTGOING, new ConcurrentHashMap<String, Long>());
        operators.stream().map(operator -> {
            Long id = operator.getId();
            return CompletableFuture.supplyAsync(() -> callDataDAO.getCallDataOfOperatorByPeriod(id, startDate, endDate)).thenComposeAsync(operatorsCallData -> {
                return combineOperatorsIncomingAndOutgoingCalls(operator, makeOperatorIncomingCallsReport(operatorsCallData, id), makeOperatorOutgoingCallsReport(operatorsCallData, id));
            }).thenApplyAsync(temporaryResult -> {
                result.get(INCOMING).putAll(temporaryResult.get(INCOMING));
                result.get(OUTGOING).putAll(temporaryResult.get(OUTGOING));
                return true;
            }).exceptionally(e -> false);
        }).forEach(this::waitCompletableFutureEndAndGetResult);
        System.out.println("End: " + (System.currentTimeMillis()-start));
        return result;
    }

    @Deprecated
    public Map<String, Map<String, Long>> getReportOfAllOperatorLegacy(LocalDate startDate, LocalDate endDate) {
        long start = System.currentTimeMillis();

        List<CallData> callDataByPeriod = callDataDAO.getCallDataByPeriod(startDate, endDate);
        Map<String, Map<String, Long>> result = new ConcurrentHashMap<>();
        Thread handleOutgoingCalls = new Thread(() -> {
            result.put(OUTGOING, summarizeCallsDurationOnOutgoingCallsByOperator(callDataByPeriod));
        });
        Thread handleIncomingCalls = new Thread(() -> {
            result.put(INCOMING, summarizeCallsDurationOnIncomingCallsByOperator(callDataByPeriod));
        });
        handleIncomingCalls.start();
        handleOutgoingCalls.start();
        try {
            handleIncomingCalls.join();
            handleOutgoingCalls.join();

            System.out.println(System.currentTimeMillis()-start);
            return result;
        } catch (InterruptedException e) {
            throw new RuntimeException(ERROR);
        }
    }

    @Deprecated
    private CompletableFuture<Map<String, Map<String, Long>>> combineOperatorsIncomingAndOutgoingCalls(Operator operator, CompletableFuture<Map<String, Long>> incomingCallsSecondsSum, CompletableFuture<Map<String, Long>> outgoingCallsSecondsSum) {
        return incomingCallsSecondsSum.thenCombineAsync(outgoingCallsSecondsSum, (incoming, outgoing) -> {
            return setTemporaryResult(operator, incoming, outgoing);
        });
    }

    @Deprecated
    private Map<String, Long> summarizeCallsDurationOnOutgoingCallsByOperator(List<CallData> callData) {
        Map<String, Long> outgoing = callData.stream().parallel()
                .collect(Collectors.groupingBy(
                        row -> {
                            if (row.getToOperator() != null && row.getSsCode() == 0 && row.getConnectionType() != 0) {
                                return row.getToOperator().getName();
                            } else {
                                return UNDEFINED_PLACEHOLDER;
                            }
                        },
                        Collectors.summingLong(CallData::getCallDurationSeconds)));

        outgoing.keySet().forEach(key -> outgoing.put(key, outgoing.get(key) / 60));
        return sortByValue(outgoing);
    }

    @Deprecated
    private Map<String, Long> summarizeCallsDurationOnIncomingCallsByOperator(List<CallData> callData) {
        Map<String, Long> incoming = callData.stream().parallel()
                .collect(Collectors.groupingBy(
                        row -> {
                            if (row.getFromOperator() != null && row.getSsCode() == 0 && row.getConnectionType() != 0) {
                                return row.getFromOperator().getName();
                            } else {
                                return UNDEFINED_PLACEHOLDER;
                            }
                        },
                        Collectors.summingLong(CallData::getCallDurationSeconds)));
        incoming.keySet().forEach(key -> incoming.put(key, incoming.get(key) / 60));
        return sortByValue(incoming);
    }
}