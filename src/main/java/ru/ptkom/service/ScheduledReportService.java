package ru.ptkom.service;

import ru.ptkom.model.ReportTemplate;

public interface ScheduledReportService {

    void setPeriodicallyReport(ReportTemplate reportTemplate);
}
