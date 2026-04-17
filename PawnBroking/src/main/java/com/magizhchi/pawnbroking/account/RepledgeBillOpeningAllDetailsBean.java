/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.account;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class RepledgeBillOpeningAllDetailsBean {
    
    private final SimpleStringProperty sSlNo;
    private final SimpleStringProperty sRepledgeBillId;
    private final SimpleStringProperty sDate;
    private final SimpleStringProperty sStatus;
    private final SimpleStringProperty sRepledgeName;
    private final SimpleStringProperty sRepledgeBillNumber;
    private final SimpleStringProperty sBillNumber;
    private final SimpleStringProperty sAmount;
    private final SimpleStringProperty sInterest;
    private final SimpleStringProperty sToGive;
    private final SimpleStringProperty sGiven;
    private final SimpleStringProperty sCreatedUser;

    public RepledgeBillOpeningAllDetailsBean(String sSlNo, String sRepledgeBillId, 
            String sDate, String sStatus, String sRepledgeName, String sRepledgeBillNumber, 
            String sBillNumber, String sAmount, String sInterest, String sToGive, String sGiven, String sCreatedUser) {
        
        this.sSlNo = new SimpleStringProperty(sSlNo);
        this.sRepledgeBillId = new SimpleStringProperty(sRepledgeBillId);
        this.sDate = new SimpleStringProperty(sDate);
        this.sStatus = new SimpleStringProperty(sStatus);
        this.sRepledgeName = new SimpleStringProperty(sRepledgeName);
        this.sRepledgeBillNumber = new SimpleStringProperty(sRepledgeBillNumber);
        this.sBillNumber = new SimpleStringProperty(sBillNumber);
        this.sAmount = new SimpleStringProperty(sAmount);
        this.sInterest = new SimpleStringProperty(sInterest);
        this.sToGive = new SimpleStringProperty(sToGive);        
        this.sGiven = new SimpleStringProperty(sGiven);
        this.sCreatedUser = new SimpleStringProperty(sCreatedUser);
    }

    public String getSSlNo() {
        return sSlNo.get();
    }

    public String getSRepledgeBillId() {
        return sRepledgeBillId.get();
    }
    
    public String getSDate() {
        return sDate.get();
    }
    
    public String getSStatus() {
        return sStatus.get();
    }

    public String getSRepledgeName() {
        return sRepledgeName.get();
    }
    
    public String getSRepledgeBillNumber() {
        return sRepledgeBillNumber.get();
    }

    public String getSBillNumber() {
        return sBillNumber.get();
    }

    public String getSAmount() {
        return sAmount.get();
    }

    public String getSInterest() {
        return sInterest.get();
    }
    
    public String getSToGive() {
        return sToGive.get();
    }

    public String getSGiven() {
        return sGiven.get();
    }

    public String getSCreatedUser() {
        return sCreatedUser.get();
    }
    

    public void setSSlNo(String sSlNo) {
        this.sDate.set(sSlNo);
    }

    public void setSRepledgeBillId(String sRepledgeBillId) {
        this.sRepledgeBillId.set(sRepledgeBillId);
    }
    
    public void setSDate(String sDate) {
        this.sDate.set(sDate);
    }
        
    public void setSStatus(String sStatus) {
        this.sStatus.set(sStatus);
    }

    public void setSRepledgeName(String sRepledgeName) {
        this.sRepledgeName.set(sRepledgeName);
    }

    public void setSRepledgeBillNumber(String sRepledgeBillNumber) {
        this.sRepledgeBillNumber.set(sRepledgeBillNumber);
    }
    
    public void setSBillNumber(String sBillNumber) {
        this.sBillNumber.set(sBillNumber);
    }
    

    public void setSAmount(String sAmount) {
        this.sAmount.set(sAmount);
    }

    public void setSInterest(String sInterest) {
        this.sInterest.set(sInterest);
    }
    
    public void setSToGive(String sToGive) {
        this.sToGive.set(sToGive);
    }

    public void setSGiven(String sGiven) {
        this.sGiven.set(sGiven);
    }

    public void setSCreatedUser(String sCreatedUser) {
        this.sCreatedUser.set(sCreatedUser);
    }
   
}
