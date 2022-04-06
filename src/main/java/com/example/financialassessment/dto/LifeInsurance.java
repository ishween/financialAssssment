package com.example.financialassessment.dto;

import javax.persistence.*;
import java.util.Date;

//@Entity
//@Table(name = "life_insurance", schema = "financialAssessment")
public class LifeInsurance {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    @Column(name = "id", nullable = false)
    private Long id;

//    @Column(name = "type", nullable = false)
    private String type;

//    @Column(name = "name", nullable = false)
    private String name;

//    @Column(name = "policy_term", nullable = false)
    private Long policyTerm;

//    @Column(name = "premium_amount", nullable = false)
    private Long premiumAmount;

//    @Column(name = "premium_paying_term", nullable = false)
    private Long premiumPayingTerm;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPolicyTerm() {
        return policyTerm;
    }

    public void setPolicyTerm(Long policyTerm) {
        this.policyTerm = policyTerm;
    }

    public Long getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(Long premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public Long getPremiumPayingTerm() {
        return premiumPayingTerm;
    }

    public void setPremiumPayingTerm(Long premiumPayingTerm) {
        this.premiumPayingTerm = premiumPayingTerm;
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
