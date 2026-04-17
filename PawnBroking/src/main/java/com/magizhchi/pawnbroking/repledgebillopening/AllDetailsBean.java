/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.repledgebillopening;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class AllDetailsBean {
    
    private final SimpleStringProperty sRepledgeBillId;
    private final SimpleStringProperty sRepledgeName;
    private final SimpleStringProperty sRepledgeBillNumber;
    private final SimpleStringProperty sRepledgeDate;
    private final SimpleStringProperty sRepledgeAmount;
    private final SimpleStringProperty sRepledgeStatus;
    private final SimpleStringProperty sCompanyBillNumber;
    private final SimpleStringProperty sCompanyDate;
    private final SimpleStringProperty sCompanyAmount;
    private final SimpleStringProperty sCompanyStatus;

    public AllDetailsBean(String sRepledgeBillId, String sRepledgeName, String sRepledgeBillNumber, String sRepledgeDate,
            String sRepledgeAmount, String sRepledgeStatus, String sCompanyBillNumber, 
            String sCompanyDate, String sCompanyAmount, String sCompanyStatus) {
        
        this.sRepledgeBillId = new SimpleStringProperty(sRepledgeBillId);
        this.sRepledgeName = new SimpleStringProperty(sRepledgeName);
        this.sRepledgeBillNumber = new SimpleStringProperty(sRepledgeBillNumber);
        this.sRepledgeDate = new SimpleStringProperty(sRepledgeDate);
        this.sRepledgeAmount = new SimpleStringProperty(sRepledgeAmount);
        this.sRepledgeStatus = new SimpleStringProperty(sRepledgeStatus);        
        this.sCompanyBillNumber = new SimpleStringProperty(sCompanyBillNumber);
        this.sCompanyAmount = new SimpleStringProperty(sCompanyAmount);
        this.sCompanyStatus = new SimpleStringProperty(sCompanyStatus);
        this.sCompanyDate = new SimpleStringProperty(sCompanyDate);
    }

    public String getSRepledgeBillId() {
        return sRepledgeBillId.get();
    }
    
    public String getSRepledgeName() {
        return sRepledgeName.get();
    }

    public String getSRepledgeBillNumber() {
        return sRepledgeBillNumber.get();
    }
    
    public String getSRepledgeDate() {
        return sRepledgeDate.get();
    }
    
    public String getSRepledgeAmount() {
        return sRepledgeAmount.get();
    }
    
    public String getSRepledgeStatus() {
        return sRepledgeStatus.get();
    }

    public String getSCompanyBillNumber() {
        return sCompanyBillNumber.get();
    }
    
    public String getSCompanyAmount() {
        return sCompanyAmount.get();
    }

    public String getSCompanyStatus() {
        return sCompanyStatus.get();
    }
        
    public String getSCompanyDate() {
        return sCompanyDate.get();
    }
    
    public void setSRepledgeBillId(String sRepledgeBillId) {
        this.sRepledgeBillId.set(sRepledgeBillId);
    }
    
    public void setSRepledgeName(String sRepledgeName) {
        this.sRepledgeName.set(sRepledgeName);
    }

    public void setSRepledgeBillNumber(String sRepledgeBillNumber) {
        this.sRepledgeBillNumber.set(sRepledgeBillNumber);
    }
    
    public void setSRepledgeDate(String sRepledgeDate) {
        this.sRepledgeDate.set(sRepledgeDate);
    }
    
    public void setSRepledgeAmount(String sRepledgeAmount) {
        this.sRepledgeAmount.set(sRepledgeAmount);
    }
    
    public void setSRepledgeStatus(String sRepledgeStatus) {
        this.sRepledgeStatus.set(sRepledgeStatus);
    }    
    
    public void setSCompanyBillNumber(String sCompanyBillNumber) {
        this.sCompanyBillNumber.set(sCompanyBillNumber);
    }
    
    public void setSCompanyAmount(String sCompanyAmount) {
        this.sCompanyAmount.set(sCompanyAmount);
    }

    public void setSCompanyStatus(String sCompanyStatus) {
        this.sCompanyStatus.set(sCompanyStatus);
    }

    public void setSCompanyDate(String sCompanyDate) {
        this.sCompanyDate.set(sCompanyDate);
    }    
    
}
