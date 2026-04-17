/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.employeemaster;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class AllDetailsBean {
    
    private final SimpleStringProperty sId;
    private final SimpleStringProperty sJoinedDate;
    private final SimpleStringProperty sEmployeeType;
    private final SimpleStringProperty sName;
    private final SimpleStringProperty sGender;
    private final SimpleStringProperty sSpouseType;
    private final SimpleStringProperty sSpouseName;
    private final SimpleStringProperty sMobileNumber;
    private final SimpleStringProperty sSalaryType;
    private final SimpleStringProperty sSalary;
    private final SimpleStringProperty sDailyAllowance;
    private final SimpleStringProperty sJobType;
    private final SimpleStringProperty sStatus;

    public AllDetailsBean(String sId, String sJoinedDate, String sEmployeeType, String sName, String sGender, 
            String sSpouseType, String sSpouseName, String sMobileNumber, 
            String sSalaryType, String sSalary, String sDailyAllowance, String sJobType, String sStatus) {
        
        this.sId = new SimpleStringProperty(sId);
        this.sJoinedDate = new SimpleStringProperty(sJoinedDate);
        this.sEmployeeType = new SimpleStringProperty(sEmployeeType);
        this.sName = new SimpleStringProperty(sName);
        this.sGender = new SimpleStringProperty(sGender);
        this.sSpouseType = new SimpleStringProperty(sSpouseType);        
        this.sSpouseName = new SimpleStringProperty(sSpouseName);
        this.sMobileNumber = new SimpleStringProperty(sMobileNumber);
        this.sSalaryType = new SimpleStringProperty(sSalaryType);
        this.sSalary = new SimpleStringProperty(sSalary);     
        this.sDailyAllowance = new SimpleStringProperty(sDailyAllowance);     
        this.sJobType = new SimpleStringProperty(sJobType);     
        this.sStatus = new SimpleStringProperty(sStatus);
    }

    public String getSId() {
        return sId.get();
    }
    
    public String getSJoinedDate() {
        return sJoinedDate.get();
    }

    public String getSEmployeeType() {
        return sEmployeeType.get();
    }
    
    public String getSName() {
        return sName.get();
    }
    
    public String getSGender() {
        return sGender.get();
    }
    
    public String getSSpouseType() {
        return sSpouseType.get();
    }

    public String getSSpouseName() {
        return sSpouseName.get();
    }    
    
    public String getSMobileNumber() {
        return sMobileNumber.get();
    }
    
    public String getSSalaryType() {
        return sSalaryType.get();
    }
    
    public String getSSalary() {
        return sSalary.get();
    }

    public String getSDailyAllowance() {
        return sDailyAllowance.get();
    }

    public String getSJobType() {
        return sJobType.get();
    }
    
    public String getSStatus() {
        return sStatus.get();
    }
    
    public void setSId(String sId) {
        this.sId.set(sId);
    }
    
    public void setSJoinedDate(String sJoinedDate) {
        this.sJoinedDate.set(sJoinedDate);
    }

    public void setSEmployeeType(String sEmployeeType) {
        this.sEmployeeType.set(sEmployeeType);
    }
    
    public void setSName(String sName) {
        this.sName.set(sName);
    }
    
    public void setSGender(String sGender) {
        this.sGender.set(sGender);
    }
    
    public void setSSpouseType(String sSpouseType) {
        this.sSpouseType.set(sSpouseType);
    }    
    
    public void setSSpouseName(String sSpouseName) {
        this.sSpouseName.set(sSpouseName);
    }
    
    public void setSMobileNumber(String sMobileNumber) {
        this.sMobileNumber.set(sMobileNumber);
    }
    
    public void setSSalaryType(String sSalaryType) {
        this.sSalaryType.set(sSalaryType);
    }
    
    public void setSSalary(String sSalary) {
        this.sSalary.set(sSalary);
    }    

    public void setSDailyAllowance(String sDailyAllowance) {
        this.sDailyAllowance.set(sDailyAllowance);
    }    

    public void setSJobType(String sJobType) {
        this.sJobType.set(sJobType);
    }    
    
    public void setSStatus(String sStatus) {
        this.sStatus.set(sStatus);
    }
    
}
