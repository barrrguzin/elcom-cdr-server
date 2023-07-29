package ru.ptkom.service;

import java.util.Map;
import java.util.Set;

public interface MaliService {

    void sendReportByEmail(Set<String> emails, Map<String, Map<String, Long>> reportData, String startDateTime, String endDateTime);

}
