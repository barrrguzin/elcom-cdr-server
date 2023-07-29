package ru.ptkom.service;

import ru.ptkom.model.Operator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface OperatorReportService {

    void sendReportToEmailByOperatorsList(Set<String> emails, List<Operator> operators, LocalDate startDate, LocalDate endDate);

    void sendReportToEmailByOperatorsList(Set<String> emails, List<Operator> operators, LocalDateTime startDateTime, LocalDateTime endDateTime);

    void sendReportByEmailByOperatorIdsList(Set<String> emails, List<Long> operatorsIds, LocalDate startDate, LocalDate endDate);

    void sendReportByEmailByOperatorIdsList(Set<String> emails, List<Long> operatorsIds, LocalDateTime startDateTime, LocalDateTime endDateTime);

    Map<String, Map<String, Long>> getReportOfAllOperator(LocalDate startDate, LocalDate endDate);

    Map<String, Map<String, Long>> getReportOfAllOperator(LocalDateTime startDateTime, LocalDateTime endDateTime);

    Map<String, Map<String, Long>> getReportByOperatorList(List<Operator> operators, LocalDate startDate, LocalDate endDate);

    Map<String, Map<String, Long>> getReportByOperatorList(List<Operator> operators, LocalDateTime startDateTime, LocalDateTime endDateTime);

    Map<String, Map<String, Long>> getReportByOperatorIdList(List<Long> operatorIds, LocalDate startDate, LocalDate endDate);

    Map<String, Map<String, Long>> getReportByOperatorIdList(List<Long> operatorIds, LocalDateTime startDateTime, LocalDateTime endDateTime);

    String getOperatorReportById(Long id, LocalDate startDate, LocalDate endDate);
}
