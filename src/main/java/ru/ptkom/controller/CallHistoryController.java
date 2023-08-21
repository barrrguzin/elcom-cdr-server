package ru.ptkom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ptkom.model.CallData;
import ru.ptkom.service.CDRDatabaseStorageService;
import ru.ptkom.service.ConfigurationFIleService;

import java.time.LocalDate;
import java.util.Optional;


@RestController
@CrossOrigin
public class CallHistoryController {

    private static final Logger log = LoggerFactory.getLogger(CallHistoryController.class);

    private Long defaultOffset;
    private Long defaultQuantity;
    private LocalDate defaultStartDate;

    private final CDRDatabaseStorageService cdrDatabaseStorageService;
    private final ConfigurationFIleService configurationFIleService;

    public CallHistoryController(CDRDatabaseStorageService cdrDatabaseStorageService, ConfigurationFIleService configurationFIleService) {
        this.cdrDatabaseStorageService = cdrDatabaseStorageService;
        this.configurationFIleService = configurationFIleService;
        initializeConfigurationProperties();
        log.info("CallHistoryController got");
    }

    private void initializeConfigurationProperties() {
        defaultOffset = configurationFIleService.getDefaultOffsetValue();
        defaultQuantity = configurationFIleService.getDefaultOnPageQuantityValue();
        defaultStartDate = configurationFIleService.getDefaultStartDateValue();
    }

    @GetMapping(value = "/cdr")
    public ResponseEntity getFullCDR(@RequestParam(required = false) Optional<Long> quantity, @RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<CallData> filter) {
        return ResponseEntity.ok(cdrDatabaseStorageService.getCallDataFromDatabase(offset.orElse(defaultOffset), quantity.orElse(defaultQuantity)));
    }

    @GetMapping(value = "/history/{number}")
    public ResponseEntity getCallHistory(@PathVariable(required = true) String number, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDate, @RequestParam(required = false) Optional<LocalDate> finishDate, @RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Long> quantity) {
        return ResponseEntity.ok(cdrDatabaseStorageService.getCallHistory(number, startDate.orElse(defaultStartDate), finishDate.orElse(LocalDate.now()), offset.orElse(defaultOffset), quantity.orElse(defaultQuantity)));
    }

    @GetMapping(value = "/history/download/{number}", produces = "application/octet-stream")
    public ResponseEntity<byte[]> getCallHistoryInFile(@PathVariable(required = true) String number, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDate, @RequestParam(required = false) Optional<LocalDate> finishDate) {
        return ResponseEntity.ok(cdrDatabaseStorageService.getCallHistoryInFile(number, startDate.orElse(defaultStartDate), finishDate.orElse(LocalDate.now())));
    }
}
