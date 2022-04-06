package com.example.financialassessment.dto;

import javax.persistence.*;
import java.util.Date;

//@Entity
//@Table(name = "health_insurance", schema = "financialAssessment")
public class HealthInsurance {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    @Column(name = "id", nullable = false)
    private Long id;

//    @Column(name = "frequency", nullable = false)
    private String frequency;

//    @Column(name = "Company_ame", nullable = false)
    private String CompanyName;

//    @Column(name = "amount", nullable = false)
    private Long amount;

//    @Column(name = "sum_insured", nullable = false)
    private Long sumInsured;

//    @Column(name = "start_date", nullable = false)
    private Date startDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(Long sumInsured) {
        this.sumInsured = sumInsured;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }


}
