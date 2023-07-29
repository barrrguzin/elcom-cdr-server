package ru.ptkom.model;

import ru.ptkom.model.enums.ReportInterval;
import ru.ptkom.model.enums.ReportPeriod;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;

@Entity
@Table(name = "report_template")
public class ReportTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Operator> operators;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "emails_to_report", joinColumns = @JoinColumn(name = "report_template_id"))
    private Set<String> emails;

    private ReportPeriod period;

    private Boolean startFromCustomDate;

    private Byte hour = 5;

    private Byte dayOfMonth = 1;

    private DayOfWeek dayOfWeek = DayOfWeek.MONDAY;



    public ReportTemplate(Set<String> emails, ReportPeriod period) {
        setEmails(emails);
        setPeriod(period);
    }

    public ReportTemplate() {}



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Operator> getOperators() {
        return operators;
    }

    public void setOperators(Set<Operator> operators) {
        this.operators = operators;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(Set<String> emails) {
        if (emails.size() > 0) {
            this.emails = emails;
        }
    }

    public ReportPeriod getPeriod() {
        return period;
    }

    public void setPeriod(ReportPeriod period) {
        if (period != null) {
            this.period = period;
        } else {
            throw new IllegalArgumentException("Period value can be null");
        }
    }

    public Byte getHour() {
        return hour;
    }

    public void setHour(Byte hour) {
        if (0 <= hour && hour < 24) {
            this.hour = hour;
        } else {
            throw new IllegalArgumentException("Hour value is not in range 0 - 23");
        }
    }

    public Byte getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(Byte dayOfMonth) {
        if (1 <= dayOfMonth && dayOfMonth <= 31) {
            this.dayOfMonth = dayOfMonth;
        } else {
            throw new IllegalArgumentException("Day of month value is not in range 1 - 31");
        }
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Boolean getStartFromCustomDate() {
        return startFromCustomDate;
    }

    public void setStartFromCustomDate(Boolean startFromCustomDate) {
        this.startFromCustomDate = startFromCustomDate;
    }

    public LocalDateTime getReportPeriodStartDateTime() {
        if (this.period.equals(ReportPeriod.DAILY)) {
            if (startFromCustomDate) {
                return LocalDateTime.now().withMinute(0).minusDays(1);
            } else {
                return LocalDate.now().minusDays(1).atStartOfDay();
            }
        } else if (this.period.equals(ReportPeriod.WEEKLY)) {
            if (startFromCustomDate) {
                return LocalDateTime.now().withMinute(0).minusDays(7);
            } else {
                return LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY)).minusDays(7).atStartOfDay();
            }
        } else if (this.period.equals(ReportPeriod.MONTHLY)) {
            if (startFromCustomDate) {
                return LocalDateTime.now().withMinute(0).minusMonths(1);
            } else {
                return LocalDate.now().minusMonths(1).withDayOfMonth(1).atStartOfDay();
            }
        } else {
            throw new IllegalStateException("Period can not be null, but it is");
        }
    }

    public LocalDateTime getReportPeriodEndDateTime() {
        if (this.period.equals(ReportPeriod.DAILY)) {
            return getReportPeriodStartDateTime().plusDays(1);
        } else if (this.period.equals(ReportPeriod.WEEKLY)) {
            return getReportPeriodStartDateTime().plusDays(7);
        } else if (this.period.equals(ReportPeriod.MONTHLY)) {
            return getReportPeriodStartDateTime().plusMonths(1);
        } else {
            throw new IllegalStateException("Period can not be null, but it is");
        }
    }
}
