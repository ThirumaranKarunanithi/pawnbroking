/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.account;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class TodaysAccountJewelRepledgeBean {
    
    private final SimpleStringProperty sSlNo;
    private final SimpleStringProperty sRepledgeBillNumber;
    private final SimpleStringProperty sRepledgeName;
    private final SimpleStringProperty sRepledgeDate;
    private final SimpleDoubleProperty sRepledgeAmount;
    private final SimpleStringProperty sCompanyBillNumber;
    private final SimpleStringProperty sCompanyDate;
    private final SimpleDoubleProperty sCompanyAmount;
    private final SimpleStringProperty sRepledgeStatus;
    private final SimpleStringProperty sRepledgeBillId;
    private final SimpleStringProperty sSuspenseDate;

    public TodaysAccountJewelRepledgeBean(String sSlNo, String sRepledgeBillNumber, 
            String sRepledgeName, String sRepledgeDate,
            double sRepledgeAmount, String sCompanyBillNumber, 
            String sCompanyDate, double sCompanyAmount, 
            String sRepledgeStatus, String sRepledgeBillId,
            String sSuspenseDate) {
        
        this.sSlNo = new SimpleStringProperty(sSlNo);
        this.sRepledgeBillNumber = new SimpleStringProperty(sRepledgeBillNumber);
        this.sRepledgeName = new SimpleStringProperty(sRepledgeName);
        this.sRepledgeDate = new SimpleStringProperty(sRepledgeDate);
        this.sRepledgeAmount = new SimpleDoubleProperty(sRepledgeAmount);
        this.sCompanyBillNumber = new SimpleStringProperty(sCompanyBillNumber);
        this.sCompanyDate = new SimpleStringProperty(sCompanyDate);
        this.sCompanyAmount = new SimpleDoubleProperty(sCompanyAmount);
        this.sRepledgeStatus = new SimpleStringProperty(sRepledgeStatus);        
        this.sRepledgeBillId = new SimpleStringProperty(sRepledgeBillId);
        this.sSuspenseDate = new SimpleStringProperty(sSuspenseDate);
    }

    public String getSSlNo() {
        return sSlNo.get();
    }

    public void setSSlNo(String sSlNo) {
        this.sSlNo.set(sSlNo);
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
    
    public double getSRepledgeAmount() {
        return sRepledgeAmount.get();
    }
    
    public String getSRepledgeStatus() {
        return sRepledgeStatus.get();
    }

    public String getSCompanyBillNumber() {
        return sCompanyBillNumber.get();
    }
    
    public double getSCompanyAmount() {
        return sCompanyAmount.get();
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
    
    public void setSRepledgeAmount(double sRepledgeAmount) {
        this.sRepledgeAmount.set(sRepledgeAmount);
    }
    
    public void setSRepledgeStatus(String sRepledgeStatus) {
        this.sRepledgeStatus.set(sRepledgeStatus);
    }    
    
    public void setSCompanyBillNumber(String sCompanyBillNumber) {
        this.sCompanyBillNumber.set(sCompanyBillNumber);
    }
    
    public void setSCompanyAmount(double sCompanyAmount) {
        this.sCompanyAmount.set(sCompanyAmount);
    }

    public void setSCompanyDate(String sCompanyDate) {
        this.sCompanyDate.set(sCompanyDate);
    }    

    public void setSSuspenseDate(String sSuspenseDate) {
        this.sSuspenseDate.set(sSuspenseDate);
    }    
    
    public String getSSuspenseDate() {
        return sSuspenseDate.get();
    }
    
}
