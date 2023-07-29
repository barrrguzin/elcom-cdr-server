package ru.ptkom.service;

import ru.ptkom.model.CallData;

import java.io.InputStream;
import java.util.List;

public interface CDRUploaderService {

    List<CallData> openFileOnRemoteSMBFolderAndGetCallDataList(String fileName);
    List<CallData> openFileFromClientApplicationAndGetCallDataList(InputStream file);
}
