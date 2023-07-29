package ru.ptkom.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
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

    private Long DEFAULT_OFFSET;
    private Long DEFAULT_QUANTITY;
    private LocalDate DEFAULT_START_DATE;

    private final CDRDatabaseStorageService cdrDatabaseStorageService;
    private final ConfigurationFIleService configurationFIleService;

    public CallHistoryController(CDRDatabaseStorageService cdrDatabaseStorageService, ConfigurationFIleService configurationFIleService) {
        this.cdrDatabaseStorageService = cdrDatabaseStorageService;
        this.configurationFIleService = configurationFIleService;
        DEFAULT_OFFSET = configurationFIleService.getDefaultOffsetValue();
        DEFAULT_QUANTITY = configurationFIleService.getDefaultOnPageQuantityValue();
        DEFAULT_START_DATE = configurationFIleService.getDefaultStartDateValue();
        System.err.println("CallHistoryController got");
    }

    @GetMapping(value = "/cdr")
    public ResponseEntity getFullCDR(@RequestParam(required = false) Optional<Long> quantity, @RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<CallData> filter) {
        return ResponseEntity.ok(cdrDatabaseStorageService.getCallDataFromDatabase(offset.orElse(DEFAULT_OFFSET), quantity.orElse(DEFAULT_QUANTITY)));
    }

    @GetMapping(value = "/history/{number}")
    public ResponseEntity getCallHistory(@PathVariable(required = true) String number, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDate, @RequestParam(required = false) Optional<LocalDate> finishDate, @RequestParam(required = false) Optional<Long> offset, @RequestParam(required = false) Optional<Long> quantity) {
        return ResponseEntity.ok(cdrDatabaseStorageService.getCallHistory(number, startDate.orElse(DEFAULT_START_DATE), finishDate.orElse(LocalDate.now()), offset.orElse(DEFAULT_OFFSET), quantity.orElse(DEFAULT_QUANTITY)));
    }

    @GetMapping(value = "/history/download/{number}", produces = "application/octet-stream")
    public ResponseEntity<byte[]> getCallHistoryInFile(@PathVariable(required = true) String number, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDate, @RequestParam(required = false) Optional<LocalDate> finishDate) {
        return ResponseEntity.ok(cdrDatabaseStorageService.getCallHistoryInFile(number, startDate.orElse(DEFAULT_START_DATE), finishDate.orElse(LocalDate.now())));
    }
}
