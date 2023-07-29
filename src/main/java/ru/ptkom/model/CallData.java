package ru.ptkom.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Entity
@Table(name = "cdr")
public class CallData implements Serializable {

    @Transient
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    @Transient
    private final static Byte STRING_SIZE = 13;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private ModuleTDM sourceModule;
    private ModuleTDM destinationModule;
    private LocalDateTime callStartDate;
    private Short callDurationSeconds;
    private String numberA;
    private String numberB;
    private Byte connectionType;
    private Short callDurationMinutes;
    private Short inputDestinationNumber;
    private Short outputDestinationNumber;
    private Byte ssCode;
    private Byte callingNumberCategory;
    private String callDurationMinutesAndSeconds;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "from_operator_id", referencedColumnName = "id")
    private Operator fromOperator;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "to_operator_id", referencedColumnName = "id")
    private Operator toOperator;

    public CallData(String callDataRow) {
        List<String> valuesOfRow = Stream.of(callDataRow.split(";"))
                                    .map(String::trim)
                                    .collect(Collectors.toList());
        if (valuesOfRow.size() == STRING_SIZE) {
            this.sourceModule = new ModuleTDM(valuesOfRow.get(0));
            this.destinationModule = new ModuleTDM(valuesOfRow.get(1));
            this.callStartDate = LocalDateTime.parse(valuesOfRow.get(2), FORMATTER);
            this.callDurationSeconds = Short.parseShort(valuesOfRow.get(3));
            this.numberA = valuesOfRow.get(4);
            this.numberB = valuesOfRow.get(5);
            this.connectionType = Byte.parseByte(valuesOfRow.get(6));
            this.callDurationMinutes = Short.parseShort(valuesOfRow.get(7));
            this.inputDestinationNumber = Short.parseShort(valuesOfRow.get(9));
            this.outputDestinationNumber = Short.parseShort(valuesOfRow.get(8));
            this.ssCode = Byte.parseByte(valuesOfRow.get(10));
            this.callingNumberCategory = Byte.parseByte(valuesOfRow.get(11));
            this.callDurationMinutesAndSeconds = valuesOfRow.get(12);
            this.fromOperator = null;
            this.toOperator = null;
        } else {
            throw new IllegalArgumentException("Unable to parse string, quantity of elements not equal to required value");
        }
    }

    public CallData() {}

    public Long getId() {
        return id;
    }

    public ModuleTDM getSourceModule() {
        return sourceModule;
    }

    public void setSourceModule(ModuleTDM sourceModule) {
        this.sourceModule = sourceModule;
    }

    public ModuleTDM getDestinationModule() {
        return destinationModule;
    }

    public void setDestinationModule(ModuleTDM destinationModule) {
        this.destinationModule = destinationModule;
    }

    public LocalDateTime getCallStartDate() {
        return callStartDate;
    }

    public void setCallStartDate(LocalDateTime callStartDate) {
        this.callStartDate = callStartDate;
    }

    public Short getCallDurationSeconds() {
        return callDurationSeconds;
    }

    public void setCallDurationSeconds(Short callDurationSeconds) {
        this.callDurationSeconds = callDurationSeconds;
    }

    public String getNumberA() {
        return numberA;
    }

    public void setNumberA(String numberA) {
        this.numberA = numberA;
    }

    public String getNumberB() {
        return numberB;
    }

    public void setNumberB(String numberB) {
        this.numberB = numberB;
    }

    public Byte getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(Byte connectionType) {
        this.connectionType = connectionType;
    }

    public Short getCallDurationMinutes() {
        return callDurationMinutes;
    }

    public void setCallDurationMinutes(Short callDurationMinutes) {
        this.callDurationMinutes = callDurationMinutes;
    }

    public Short getInputDestinationNumber() {
        return inputDestinationNumber;
    }

    public void setInputDestinationNumber(Short inputDestinationNumber) {
        this.inputDestinationNumber = inputDestinationNumber;
    }

    public Short getOutputDestinationNumber() {
        return outputDestinationNumber;
    }

    public void setOutputDestinationNumber(Short outputDestinationNumber) {
        this.outputDestinationNumber = outputDestinationNumber;
    }

    public Byte getSsCode() {
        return ssCode;
    }

    public void setSsCode(Byte ssCode) {
        this.ssCode = ssCode;
    }

    public Byte getCallingNumberCategory() {
        return callingNumberCategory;
    }

    public void setCallingNumberCategory(Byte callingNumberCategory) {
        this.callingNumberCategory = callingNumberCategory;
    }

    public String getCallDurationMinutesAndSeconds() {
        return callDurationMinutesAndSeconds;
    }

    public void setCallDurationMinutesAndSeconds(String callDurationMinutesAndSeconds) {
        this.callDurationMinutesAndSeconds = callDurationMinutesAndSeconds;
    }

    public Operator getFromOperator() {
        return fromOperator;
    }

    public void setFromOperator(Operator fromOperator) {
        this.fromOperator = fromOperator;
    }

    public Operator getToOperator() {
        return toOperator;
    }

    public void setToOperator(Operator toOperator) {
        this.toOperator = toOperator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallData callData = (CallData) o;
        return sourceModule.equals(callData.sourceModule) && destinationModule.equals(callData.destinationModule) && callStartDate.equals(callData.callStartDate) && callDurationSeconds.equals(callData.callDurationSeconds) && numberA.equals(callData.numberA) && numberB.equals(callData.numberB) && connectionType.equals(callData.connectionType) && callDurationMinutes.equals(callData.callDurationMinutes) && inputDestinationNumber.equals(callData.inputDestinationNumber) && outputDestinationNumber.equals(callData.outputDestinationNumber) && ssCode.equals(callData.ssCode) && callingNumberCategory.equals(callData.callingNumberCategory) && callDurationMinutesAndSeconds.equals(callData.callDurationMinutesAndSeconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceModule, destinationModule, callStartDate, callDurationSeconds, numberA, numberB, connectionType, callDurationMinutes, inputDestinationNumber, outputDestinationNumber, ssCode, callingNumberCategory, callDurationMinutesAndSeconds);
    }
}


