package ru.ptkom.dao;

import org.springframework.stereotype.Component;
import ru.ptkom.model.CallData;
import ru.ptkom.model.Operator;
import ru.ptkom.repository.CallDataRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class CallDataDAO {

    private final static DateTimeFormatter MAIN_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final CallDataRepository callDataRepository;

    public CallDataDAO(CallDataRepository callDataRepository) {
        this.callDataRepository = callDataRepository;
    }


    public List<CallData> getCallDataByPeriod(LocalDate startDate, LocalDate endDate) {
        try {
            var rows = callDataRepository.findByCallStartDateBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay().minusNanos(1)).orElseThrow(Exception::new);
            return rows;
        } catch (Exception e) {
            throw new RuntimeException("Unable to get list of call data with defined operators on: " + startDate + " - " + endDate);
        }
    }

    public List<CallData> getCallDataOfOperatorByPeriod(Long id, LocalDate startDate, LocalDate endDate) {
        try {
            return callDataRepository.findOperatorsTraffic(id, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay().minusNanos(1)).orElseThrow(Exception::new);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<CallData> getCallDataOfOperatorByPeriod(Long id, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        try {
            return callDataRepository.findOperatorsTraffic(id, startDateTime, endDateTime).orElseThrow(Exception::new);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<CallData> getCallDataRowsFromDatabase(Long offset, Long quantity) {
        try {
            return callDataRepository.findLastRows(offset, quantity).orElseThrow(Exception::new);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get " + quantity + " rows with offset " + offset + " from data base");
        }
    }

    public List<CallData> getCallHistoryByNumber(String number, LocalDate startDate, LocalDate finishDate, Long offset, Long quantity) {
        try {
            return callDataRepository.findCallHistoryByNumberAndPeriod(number, startDate.atStartOfDay(), finishDate.plusDays(1).atStartOfDay().minusNanos(1), offset, quantity).orElseThrow(Exception::new);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get " + quantity + " rows with offset " + offset + " from data base. Number: " + number + "; Period: " + startDate + " - " + finishDate);
        }
    }

    public List<CallData> getAllCallHistoryByNumber(String number, LocalDate startDate, LocalDate finishDate) {
        try {
            return callDataRepository.findAllCallHistoryByNumberAndPeriod(number, startDate.atStartOfDay(), finishDate.plusDays(1).atStartOfDay().minusNanos(1)).orElseThrow(Exception::new);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get call history from data base. Number: " + number + "; Period: " + startDate + " - " + finishDate);
        }
    }

    public void saveCallDataToDataBase(List<CallData> cdr) {
        callDataRepository.saveAll(cdr);
    }

    public void removeDuplicatesAndSaveCallDataToDataBase(List<CallData> cdr) {
        callDataRepository.saveAll(getListOfCallDataWithoutDuplicates(cdr));
    }

    public void removeDuplicatesAndDefineOperatorAndSaveCallDataToDataBase(List<CallData> cdr, List<Operator> operators) {
        List<CallData> callDataRowsWithDefinerOperators = getListOfCallDataWithoutDuplicates(cdr).stream().map(row -> {
            row.setFromOperator(
                operators.stream().filter(operator -> operator.getLines().contains(row.getSourceModule())).findAny().orElse(null));
            return row;
        }).map(row -> {
            row.setToOperator(
                    operators.stream().filter(operator -> operator.getLines().contains(row.getDestinationModule())).findAny().orElse(null));
            return row;
        }).toList();
        callDataRepository.saveAll(callDataRowsWithDefinerOperators);
    }

    private List<CallData> getListOfCallDataWithoutDuplicates(List<CallData> cdr) {
        List<CallData> callsInDaysFoundOnUploadedCDR = cdr
                .stream()
                .map(CallData::getCallStartDate)
                .map(localDateTime -> localDateTime.format(MAIN_FORMATTER))
                .distinct()
                .map(this::findCallsOnDate)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap(List::stream)
                .toList();
        return cdr.stream().filter(row -> !callsInDaysFoundOnUploadedCDR.contains(row)).toList();
    }

    private Optional<List<CallData>> findCallsOnDate(String dateString) {
        LocalDate date = LocalDate.parse(dateString, MAIN_FORMATTER);
        return callDataRepository.findByCallStartDateBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay().minusNanos(1));
    }

}
