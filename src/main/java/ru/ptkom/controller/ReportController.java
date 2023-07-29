package ru.ptkom.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ptkom.service.OperatorReportService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@RestController
@CrossOrigin
public class ReportController {


    private final OperatorReportService operatorReportService;

    public ReportController(OperatorReportService operatorReportService) {
        this.operatorReportService = operatorReportService;
    }


    @GetMapping(value = "/report/all")
    public ResponseEntity getAllOperatorsReport(@RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(operatorReportService.getReportOfAllOperator(startDate, endDate));
    }

    @GetMapping(value = "/report")
    public ResponseEntity getOperatorReportByList(@RequestParam(required = true) List<Long> operatorIds, @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(operatorReportService.getReportByOperatorIdList(operatorIds, startDate, endDate));
    }

    @GetMapping(value = "/report/{id}")
    public ResponseEntity getOperatorReport(@PathVariable(required = true) Long id, @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(operatorReportService.getReportByOperatorIdList(Stream.of(id).toList(), startDate, endDate));
    }

    @GetMapping(value = "/report/mail")
    public ResponseEntity getReportToEmailByOperatorList(@RequestParam(required = true) Set<String> emails, @RequestParam(required = true) List<Long> operatorIds, @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        operatorReportService.sendReportByEmailByOperatorIdsList(emails, operatorIds, startDate, endDate);
        return ResponseEntity.ok("OK");
    } //http://localhost:8080/api/report/mail?emails=it@ptkom.ru,aosunitskii@ptkom.ru&operatorIds=1,3,14,15,16&startDate=2023-06-01&endDate=2023-06-30
}
