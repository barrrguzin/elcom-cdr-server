package ru.ptkom.service.impl;


import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ptkom.dao.CallDataDAO;
import ru.ptkom.dao.OperatorDAO;
import ru.ptkom.model.CallData;
import ru.ptkom.repository.CallDataRepository;
import ru.ptkom.service.CDRDatabaseStorageService;
import ru.ptkom.service.CDRUploaderService;


import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@EnableScheduling
@EnableAsync
@Service
public class CDRDatabaseStorageServiceImpl implements CDRDatabaseStorageService {

    private final static String FILE_EXTENTION = ".txt";
    private final static Character CSV_SPLITTER = ';';

    private final CDRUploaderService cdrUploaderService;
    private final CallDataDAO callDataDAO;
    private final OperatorDAO operatorDAO;

    public CDRDatabaseStorageServiceImpl(CDRUploaderService cdrUploaderService, CallDataRepository callDataRepository, CallDataDAO callDataDAO, OperatorDAO operatorDAO) {
        this.cdrUploaderService = cdrUploaderService;
        this.callDataDAO = callDataDAO;
        this.operatorDAO = operatorDAO;
    }

    @Scheduled(cron = "0 5 * * * ?") //Каждый день в 5 утра
    @Async
    public void automaticallyUploadCallDataToDataBase() {
        List<CallData> cdr = getCallDataFromRemoteFolder();
        uploadCallDataToDataBase(cdr);
    }

    @Override
    public void manuallyUploadCallDataToDataBase(InputStream file) {
        List<CallData> cdr = getCallDataFromInputStream(file);
        uploadCallDataToDataBase(cdr);
    }

    @Override
    public List<CallData> getCallDataFromDatabase(Long cursor, Long quantity) {
        return callDataDAO.getCallDataRowsFromDatabase(cursor, quantity);
    }

    @Override
    public List<CallData> getCallHistory(String number, LocalDate startDate, LocalDate finishDate, Long offset, Long quantity) {
        return callDataDAO.getCallHistoryByNumber(number, startDate, finishDate, offset, quantity);
    }

    @Override
    public byte[] getCallHistoryInFile(String number, LocalDate startDate, LocalDate finishDate) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Call start time;Call duration;Calling number;Called number").append('\n');
        for (CallData row : callDataDAO.getAllCallHistoryByNumber(number, startDate, finishDate)) {
            stringBuilder.append(row.getCallStartDate()).append(CSV_SPLITTER).append(row.getCallDurationMinutesAndSeconds()).append(CSV_SPLITTER).append(row.getNumberA()).append(CSV_SPLITTER).append(row.getNumberB()).append(CSV_SPLITTER).append('\n');
        }
        return stringBuilder.toString().getBytes();
    }

    private Optional<List<Date>> findPeriodsWithLostDataOnDataBase() {
        //Метод должен проверять базу данных на то, чтобы присутствовали записи на каждый день, если в период между первой и последней записью в базе есть пропущенные дни, метод их отображает
        return null;
    }

    private String getFileNameFromYesterdayDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");
        LocalDate date = LocalDate.now().minusDays(1);
        return date.format(formatter) + FILE_EXTENTION;
    }

    private List<CallData> getCallDataFromRemoteFolder(String fileName) {
        return cdrUploaderService.openFileOnRemoteSMBFolderAndGetCallDataList(fileName);
    }

    private List<CallData> getCallDataFromRemoteFolder() {
        return cdrUploaderService.openFileOnRemoteSMBFolderAndGetCallDataList(getFileNameFromYesterdayDate());
    }

    private List<CallData> getCallDataFromInputStream(InputStream file) {
        return cdrUploaderService.openFileFromClientApplicationAndGetCallDataList(file);
    }

    private void uploadCallDataToDataBase(List<CallData> cdr) {
        callDataDAO.removeDuplicatesAndDefineOperatorAndSaveCallDataToDataBase(cdr, operatorDAO.getOperators());
    }
}