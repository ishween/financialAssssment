package com.example.financialassessment.dto;

import javax.persistence.*;
import java.util.Date;

//@Entity
//@Table(name = "ppf", schema = "financialAssessment")
public class PPF {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    @Column(name = "id", nullable = false)
    private Long id;

//    @Column(name = "frequency", nullable = false)
    private String frequency;

//    @Column(name = "invested", nullable = false)
    private Long invested;

//    @Column(name = "date_of_investment", nullable = false)
    private Date dateOfInvestment;

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

    public Long getInvested() {
        return invested;
    }

    public void setInvested(Long invested) {
        this.invested = invested;
    }

    public Date getDateOfInvestment() {
        return dateOfInvestment;
    }

    public void setDateOfInvestment(Date dateOfInvestment) {
        this.dateOfInvestment = dateOfInvestment;
    }

}
