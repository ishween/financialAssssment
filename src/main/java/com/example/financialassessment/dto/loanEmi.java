package com.example.financialassessment.dto;

import javax.persistence.*;
import java.util.Date;

//@Entity
//@Table(name = "loan_emi", schema = "financialAssessment")
public class loanEmi {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    @Column(name = "id", nullable = false)
    private Long id;

//    @Column(name = "amount", nullable = false)
    private Long amount;

//    @Column(name = "investment_left", nullable = false)
    protected Long investmentLeft;

//    @Column(name = "amount_of_installment", nullable = false)
    protected Long amountOfInstallment;

//    @Column(name = "start_date", nullable = false)
    private Date startDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getInvestmentLeft() {
        return investmentLeft;
    }

    public void setInvestmentLeft(Long investmentLeft) {
        this.investmentLeft = investmentLeft;
    }

    public Long getAmountOfInstallment() {
        return amountOfInstallment;
    }

    public void setAmountOfInstallment(Long amountOfInstallment) {
        this.amountOfInstallment = amountOfInstallment;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

}
