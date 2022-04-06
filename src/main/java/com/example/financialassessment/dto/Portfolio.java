package com.example.financialassessment.dto;


import javax.persistence.*;
import java.util.Date;


//@Entity
//@Table(name = "portfolio", schema = "financialAssessment")
public class Portfolio {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    @Column(name = "id", nullable = false)
    protected Long id;

//    @Column(name = "first_name", nullable = false)
    protected String firstName;

//    @Column(name = "last_name", nullable = false)
    protected String lastName;

//    @Column(name = "email", nullable = false)
    protected String email;

//    @Column(name = "gender", nullable = false)
    protected String gender;

//    @Column(name = "married", nullable = false)
    protected boolean married;

//    @Column(name = "dob", nullable = false)
    protected Date dob;

//    @Column(name = "profession", nullable = false)
    protected String profession;

//    @Column(name = "mobile_no", nullable = false)
    protected String mobileNo;

//    @Column(name = "address", nullable = false)
    protected String address;

//    @Column(name = "income", nullable = false)
    protected String income;

//    @Column(name = "expense", nullable = false)
    protected String expense;

//    @Column(name = "risk_profile", nullable = false)
    protected String riskProfile;

//    @Column(name = "life_goals", nullable = false)
    protected String lifeGoals;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Portfolio() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isMarried() {
        return married;
    }

    public void setMarried(boolean married) {
        this.married = married;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getExpense() {
        return expense;
    }

    public void setExpense(String expense) {
        this.expense = expense;
    }

    public String getRiskProfile() {
        return riskProfile;
    }

    public void setRiskProfile(String riskProfile) {
        this.riskProfile = riskProfile;
    }

    public String getLifeGoals() {
        return lifeGoals;
    }

    public void setLifeGoals(String lifeGoals) {
        this.lifeGoals = lifeGoals;
    }

}
