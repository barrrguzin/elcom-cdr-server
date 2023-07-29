package ru.ptkom.service;

import com.hierynomus.msfscc.FileAttributes;
import ru.ptkom.model.CallData;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

public interface CDRDatabaseStorageService {

    void automaticallyUploadCallDataToDataBase(); //To force update

    void manuallyUploadCallDataToDataBase(InputStream file);

    List<CallData> getCallDataFromDatabase(Long cursor, Long quantity);

    List<CallData> getCallHistory(String number, LocalDate startDate, LocalDate finishDate, Long offset, Long quantity);

    byte[] getCallHistoryInFile(String number, LocalDate startDate, LocalDate finishDate);
}
