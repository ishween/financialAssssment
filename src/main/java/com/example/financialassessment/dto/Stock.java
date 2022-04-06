package com.example.financialassessment.dto;

import javax.persistence.*;
import java.util.Date;

//@Entity
//@Table(name = "stock", schema = "financialAssessment")
public class Stock {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    @Column(name = "id", nullable = false)
    private Long id;

//    @Column(name = "name", nullable = false)
    String name;

//    @Column(name = "amount", nullable = false)
    Long amount;

//    @Column(name = "market_value", nullable = false)
    Long marketValue;

//    @Column(name = "date_of_investment", nullable = false)
    Date dateOfInvestment;

//    @Column(name = "frequency", nullable = false)
    String frequency;

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

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
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

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

}
