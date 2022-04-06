package com.example.financialassessment.dto;

import javax.persistence.*;
import java.util.Date;

//@Entity
//@Table(name = "bonds", schema = "financialAssessment")
public class Bonds {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    @Column(name = "id", nullable = false)
    private Long id;

//    @Column(name = "name", nullable = false)
    private String name;

//    @Column(name = "date_of_investment", nullable = false)
    private Date dateOfInvestment;

//    @Column(name = "maturity_date", nullable = false)
    private Date maturityDate;

//    @Column(name = "invested", nullable = false)
    private Long invested;

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

    public Date getDateOfInvestment() {
        return dateOfInvestment;
    }

    public void setDateOfInvestment(Date dateOfInvestment) {
        this.dateOfInvestment = dateOfInvestment;
    }

    public Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public Long getInvested() {
        return invested;
    }

    public void setInvested(Long invested) {
        this.invested = invested;
    }

}
