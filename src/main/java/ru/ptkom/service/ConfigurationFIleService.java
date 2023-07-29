package ru.ptkom.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ConfigurationFIleService {

    @Value("${default.offset}")
    private static Long defaultOffsetValue;

    @Value("${default.onPageQuantity}")
    private static Long defaultOnPageQuantityValue;

    private static final LocalDate defaultStartDateValue = LocalDate.ofYearDay(1986, 116);

    private static final String KEY_OF_INCOMING_REPORT = "incoming";
    private static final String KEY_OF_OUTGOING_REPORT = "outgoing";

    public Long getDefaultOffsetValue() {
        return defaultOffsetValue;
    }

    public Long getDefaultOnPageQuantityValue() {
        return defaultOnPageQuantityValue;
    }

    public LocalDate getDefaultStartDateValue() {
        return defaultStartDateValue;
    }

    public String getKeyOfIncomingReport() {return KEY_OF_INCOMING_REPORT;}

    public String getKeyOfOutgoingReport() {return KEY_OF_OUTGOING_REPORT;}
}
