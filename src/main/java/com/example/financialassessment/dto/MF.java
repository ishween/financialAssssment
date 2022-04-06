package com.example.financialassessment.dto;

import javax.persistence.*;
import java.util.Date;

//@Entity
//@Table(name = "mf", schema = "financialAssessment")
public class MF {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    @Column(name = "id", nullable = false)
    private Long id;

//    @Column(name = "type", nullable = false)
    private String type;

//    @Column(name = "frequency", nullable = false)
    private String frequency;

//    @Column(name = "invested", nullable = false)
    private Long invested;

//    @Column(name = "market_value", nullable = false)
    private Long marketValue;

//    @Column(name = "date_of_investment", nullable = false)
    private Date dateOfInvestment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Long getInvested() {
        return invested;
    }

    public void setInvested(Long invested) {
        this.invested = invested;
    }

    public Long getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(Long marketValue) {
        this.marketValue = marketValue;
    }

    public Date getDateOfInvestment() {
        return dateOfInvestment;
    }

    public void setDateOfInvestment(Date dateOfInvestment) {
        this.dateOfInvestment = dateOfInvestment;
    }
}
