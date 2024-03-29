package ru.ptkom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.ptkom.service.CDRDatabaseStorageService;

import java.io.IOException;
import java.io.InputStream;

@RestController
@CrossOrigin
public class CallHistoryUploaderController {

    private static final Logger log = LoggerFactory.getLogger(CallHistoryUploaderController.class);

    private final CDRDatabaseStorageService cdrDatabaseStorageService;

    public CallHistoryUploaderController(CDRDatabaseStorageService cdrDatabaseStorageService) {
        this.cdrDatabaseStorageService = cdrDatabaseStorageService;
        log.info("CallHistoryUploaderController got");
    }

    @GetMapping(value = "/force-daily-upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getGet() {
        cdrDatabaseStorageService.automaticallyUploadCallDataToDataBase();
        return ResponseEntity.ok("Got it");
    }

    @PostMapping(value = "/upload")
    public ResponseEntity uploadCDR(@RequestBody MultipartFile cdr) {
        try {
            InputStream fileData = cdr.getInputStream();
            cdrDatabaseStorageService.manuallyUploadCallDataToDataBase(fileData);
            return ResponseEntity.ok("File got!");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
