package com.example.financialassessment.dto;

import javax.persistence.*;
import java.util.Date;

//@Entity
//@Table(name = "fd", schema = "financialAssessment")
public class FD {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    @Column(name = "id", nullable = false)
    private Long id;

//    @Column(name = "name", nullable = false)
    private String name;

//    @Column(name = "invested", nullable = false)
    private Long invested;

//    @Column(name = "investment_rate", nullable = false)
    private Long investmentRate;

//    @Column(name = "start_date", nullable = false)
    private Date startDate;

//    @Column(name = "maturity_date", nullable = false)
    private Date maturityDate;

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

    public Long getInvested() {
        return invested;
    }

    public void setInvested(Long invested) {
        this.invested = invested;
    }

    public Long getInvestmentRate() {
        return investmentRate;
    }

    public void setInvestmentRate(Long investmentRate) {
        this.investmentRate = investmentRate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }
}
