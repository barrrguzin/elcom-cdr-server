package ru.ptkom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.ptkom.configuration.WebSecurityConfiguration;
import ru.ptkom.dao.OperatorDAO;
import ru.ptkom.model.Operator;
import ru.ptkom.service.OperatorReportService;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@RestController
public class OperatorController {

    private static final Logger log = LoggerFactory.getLogger(OperatorController .class);

    private final OperatorDAO operatorDAO;

    public OperatorController(OperatorDAO operatorDAO, OperatorReportService operatorReportService) {
        this.operatorDAO = operatorDAO;
    }

    @PostMapping(value = "/operator")
    public ResponseEntity addOperator(@RequestBody Operator operator) {
        operatorDAO.saveOperator(operator);
        return ResponseEntity.ok(operator.getName() + " add");
    }

    @DeleteMapping(value = "/operator/{id}")
    public ResponseEntity deleteOperator(@PathVariable(required = true) Long id) {
        operatorDAO.deleteOperatorById(id);
        return ResponseEntity.ok("Operator deleted by ID: " + id);
    }

    @PatchMapping(value = "/operator")
    public ResponseEntity updateOperator(@RequestBody Operator operator) {
        operatorDAO.updateOperator(operator);
        return ResponseEntity.ok(operator.getName() + " updated");
    }

    @GetMapping(value = "/operator")
    public ResponseEntity<List<Operator>> getOperatorList() {
        return ResponseEntity.ok(operatorDAO.getOperators());
    }

    @GetMapping(value = "/operator/{id}")
    public ResponseEntity<Operator> getOperator(@PathVariable(required = true) Long id) {
        return ResponseEntity.ok(operatorDAO.getOperator(id));
    }
}
