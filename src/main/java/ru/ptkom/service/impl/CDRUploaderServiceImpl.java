package ru.ptkom.service.impl;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ptkom.model.CallData;
import ru.ptkom.service.CDRUploaderService;
import ru.ptkom.service.ConfigurationFIleService;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CDRUploaderServiceImpl implements CDRUploaderService {

    private static final Logger log = LoggerFactory.getLogger(CDRUploaderServiceImpl.class);

    private final ConfigurationFIleService configurationFIleService;

    private static final String SPLIT = "\\";

    private String smbDomain;
    private String smbUsername;
    private String smbPassword;
    private String smbSharedFolder;
    private String smbIpAddress;
    private String pathToFolder;

    public CDRUploaderServiceImpl(ConfigurationFIleService configurationFIleService) {
        this.configurationFIleService = configurationFIleService;
        initializeConfigurationProperties();
        log.info("CDRUploaderService got");
    }

    private void initializeConfigurationProperties() {
        smbDomain = configurationFIleService.getActiveDirectoryDomain();
        smbUsername = configurationFIleService.getSmbUsername();
        smbPassword = configurationFIleService.getSmbPassword();
        smbSharedFolder = configurationFIleService.getSmbSharedFolder();
        smbIpAddress = configurationFIleService.getSmbIpAddress();
        pathToFolder = configurationFIleService.getSmbPathToFolder();
    }

    public List<CallData> openFileOnRemoteSMBFolderAndGetCallDataList(String fileName) {
        return getCallDataListFromInputStream(openRemoteFile(pathToFolder + SPLIT + fileName));
    }


    public List<CallData> openFileFromClientApplicationAndGetCallDataList(InputStream file) {
        return getCallDataListFromInputStream(file);
    }


    private List<CallData> getCallDataListFromInputStream(InputStream file) {
        return new BufferedReader(new InputStreamReader(file))
                .lines().map(CallData::new)
                .collect(Collectors.toList());
    }


    private InputStream openRemoteFile(String fileName) {
        DiskShare share = connectToRemoteFolder();
        InputStream inputStream = share.openFile(fileName,
                Collections.singleton(AccessMask.GENERIC_READ),
                Collections.singleton(FileAttributes.FILE_ATTRIBUTE_NORMAL),
                Collections.singleton(SMB2ShareAccess.FILE_SHARE_READ),
                SMB2CreateDisposition.FILE_OPEN,
                Collections.singleton(SMB2CreateOptions.FILE_RANDOM_ACCESS))
                .getInputStream();
        try {
            return inputStream;
        } finally {
            try {
                inputStream.close();
                share.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private DiskShare connectToRemoteFolder() {
        SMBClient client = new SMBClient();
        try {
            Connection connection = client.connect(smbIpAddress);
            AuthenticationContext ac = new AuthenticationContext(smbUsername, smbPassword.toCharArray(), smbDomain);
            Session session = connection.authenticate(ac);
            return (DiskShare) session.connectShare(smbSharedFolder);
        } catch (IOException e) {
            throw new RuntimeException("Unable to connect remote folder \\\\"+ smbIpAddress +"\\"+ smbSharedFolder + " by user "+ smbDomain +"\\"+smbUsername);
        }
    }
}
