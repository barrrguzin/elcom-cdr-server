package ru.ptkom.service.impl;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ptkom.model.Operator;
import ru.ptkom.model.ReportTemplate;
import ru.ptkom.model.enums.ReportPeriod;
import ru.ptkom.service.MaliService;
import ru.ptkom.service.OperatorReportService;
import ru.ptkom.service.ScheduledReportService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@EnableScheduling
@Service
public class ScheduledReportServiceImpl implements ScheduledReportService {

    private final OperatorReportService operatorReportService;
    private final MaliService mailService;
    private static Set<ReportTemplate> templateQuery;

    public ScheduledReportServiceImpl(OperatorReportService operatorReportService, MaliService mailService) {
        this.operatorReportService = operatorReportService;
        this.mailService = mailService;
    }

    @Scheduled(cron = "@hourly")
    private void checkReportQuery() {
        for (ReportTemplate reportTemplate : templateQuery) {
            if (reportTemplate.getPeriod() == ReportPeriod.DAILY) {
                checkTimeAndMakeDailyReport(reportTemplate);
            } else if (reportTemplate.getPeriod() == ReportPeriod.WEEKLY) {
                checkTimeAndMakeWeeklyReport(reportTemplate);
            } else if (reportTemplate.getPeriod() == ReportPeriod.MONTHLY) {
                checkTimeAndMakeMonthlyReport(reportTemplate);
            }
        }
    }

    @Override
    public void setPeriodicallyReport(ReportTemplate reportTemplate) {

    }

    private void checkTimeAndMakeDailyReport(ReportTemplate reportTemplate) {
        if (reportTemplate.getHour() == LocalTime.now().getHour()) {
            makeAsyncReport(reportTemplate);
        }
    }

    private void checkTimeAndMakeWeeklyReport(ReportTemplate reportTemplate) {
        if (reportTemplate.getDayOfWeek() == LocalDate.now().getDayOfWeek()) {
            if (reportTemplate.getHour() == LocalTime.now().getHour()) {
                makeAsyncReport(reportTemplate);
            }
        }
    }

    private void checkTimeAndMakeMonthlyReport(ReportTemplate reportTemplate) {
        if (reportTemplate.getDayOfMonth() == LocalDate.now().getDayOfMonth()) {
            if (reportTemplate.getHour() == LocalTime.now().getHour()) {
                makeAsyncReport(reportTemplate);
            }
        }
    }

    private CompletableFuture<Void> makeAsyncReport(ReportTemplate reportTemplate) {
        return CompletableFuture.runAsync(() -> makeReport(reportTemplate));
    }

    private void makeReport(ReportTemplate reportTemplate) {
        List<Operator> operators = reportTemplate.getOperators().stream().toList();
        var reportData = operatorReportService.getReportByOperatorList(operators, reportTemplate.getReportPeriodStartDateTime(), reportTemplate.getReportPeriodEndDateTime());
        mailService.sendReportByEmail(reportTemplate.getEmails(), reportData, reportTemplate.getReportPeriodStartDateTime().toString(), reportTemplate.getReportPeriodEndDateTime().toString());
    }
}
