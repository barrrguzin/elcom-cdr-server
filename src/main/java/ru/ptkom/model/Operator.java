package ru.ptkom.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "operator")
public class Operator implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String visibleName;

    private Float incomingCallMinutePrice;

    private Float outgoingCallMinutePrice;

    @ElementCollection(targetClass = ModuleTDM.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "operator_lines", joinColumns = @JoinColumn(name = "operator_id"))
    private Set<ModuleTDM> lines;

//    @ElementCollection(targetClass = Short.class, fetch = FetchType.EAGER)
//    @CollectionTable(name = "operator_inputDestinations", joinColumns = @JoinColumn(name = "operator_id"))
    private Short inputDestination;
//    @ElementCollection(targetClass = Short.class, fetch = FetchType.EAGER)
//    @CollectionTable(name = "operator_outputDestinations", joinColumns = @JoinColumn(name = "operator_id"))
    private Short outputDestination;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<ReportTemplate> reports;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ModuleTDM> getLines() {
        return lines;
    }

    public void setLines(Set<ModuleTDM> lines) {
        this.lines = lines;
    }

    public Short getInputDestination() {
        return inputDestination;
    }

    public void setInputDestination(Short inputDestination) {
        this.inputDestination = inputDestination;
    }

    public Short getOutputDestination() {
        return outputDestination;
    }

    public void setOutputDestination(Short outputDestination) {
        this.outputDestination = outputDestination;
    }

    public String getVisibleName() {
        return visibleName;
    }

    public void setVisibleName(String visibleName) {
        this.visibleName = visibleName;
    }

    public Float getIncomingCallMinutePrice() {
        return incomingCallMinutePrice;
    }

    public void setIncomingCallMinutePrice(Float incomingCallMinutePrice) {
        this.incomingCallMinutePrice = incomingCallMinutePrice;
    }

    public Float getOutgoingCallMinutePrice() {
        return outgoingCallMinutePrice;
    }

    public void setOutgoingCallMinutePrice(Float outgoingCallMinutePrice) {
        this.outgoingCallMinutePrice = outgoingCallMinutePrice;
    }

    public Set<ReportTemplate> getReports() {
        return reports;
    }
}