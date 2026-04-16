/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.reports;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class CompanyBillOpeningAllDetailsBean {
    
    private final SimpleStringProperty sSlNo;
    private final SimpleStringProperty sDate;
    private final SimpleStringProperty sBillNumber;   
    private final SimpleStringProperty sStatus;
    private final SimpleStringProperty sName;
    private final SimpleStringProperty sItems;
    private final SimpleStringProperty sAmount;
    private final SimpleStringProperty sInterest;
    private final SimpleStringProperty sInterestedAmt;
    private final SimpleStringProperty sDocumentCharge;
    private final SimpleStringProperty sToGive;
    private final SimpleStringProperty sGiven;
    private final SimpleStringProperty sCreatedUser;
    private final SimpleStringProperty sCreatedTime;

    public CompanyBillOpeningAllDetailsBean(String sSlNo, String sDate, 
            String sBillNumber, String sStatus, String sName, String sItems, 
            String sAmount, String sInterest, String sInterestedAmt, String sDocumentCharge, 
            String sToGive, String sGiven, String sCreatedUser, String sCreatedTime) {
        
        this.sSlNo = new SimpleStringProperty(sSlNo);
        this.sDate = new SimpleStringProperty(sDate);
        this.sBillNumber = new SimpleStringProperty(sBillNumber);
        this.sStatus = new SimpleStringProperty(sStatus);
        this.sName = new SimpleStringProperty(sName);
        this.sItems = new SimpleStringProperty(sItems);
        this.sAmount = new SimpleStringProperty(sAmount);
        this.sInterest = new SimpleStringProperty(sInterest);
        this.sInterestedAmt = new SimpleStringProperty(sInterestedAmt);
        this.sDocumentCharge = new SimpleStringProperty(sDocumentCharge);        
        this.sToGive = new SimpleStringProperty(sToGive);        
        this.sGiven = new SimpleStringProperty(sGiven);
        this.sCreatedUser = new SimpleStringProperty(sCreatedUser);
        this.sCreatedTime = new SimpleStringProperty(sCreatedTime);
    }

    public String getSSlNo() {
        return sSlNo.get();
    }
    
    public String getSDate() {
        return sDate.get();
    }
    
    public String getSBillNumber() {
        return sBillNumber.get();
    }

    public String getSStatus() {
        return sStatus.get();
    }
    
    public String getSName() {
        return sName.get();
    }

    public String getSItems() {
        return sItems.get();
    }

    public String getSAmount() {
        return sAmount.get();
    }

    public String getSInterest() {
        return sInterest.get();
    }

    public String getSInterestedAmt() {
        return sInterestedAmt.get();
    }

    public String getSDocumentCharge() {
        return sDocumentCharge.get();
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
    
    public String getSCreatedTime() {
        return sCreatedTime.get();
    }

    
    public void setSSlNo(String sSlNo) {
        this.sDate.set(sSlNo);
    }
    
    public void setSDate(String sDate) {
        this.sDate.set(sDate);
    }
        
    public void setSBillNumber(String sBillNumber) {
        this.sBillNumber.set(sBillNumber);
    }

    public void setSStatus(String sStatus) {
        this.sStatus.set(sStatus);
    }
    
    public void setSName(String sName) {
        this.sName.set(sName);
    }
        
    public void setSItems(String sItems) {
        this.sItems.set(sItems);
    }

    public void setSAmount(String sAmount) {
        this.sAmount.set(sAmount);
    }
        
    public void setSInterest(String sInterest) {
        this.sInterest.set(sInterest);
    }

    public void setSInterestedAmt(String sInterestedAmt) {
        this.sInterestedAmt.set(sInterestedAmt);
    }

    public void setSDocumentCharge(String sDocumentCharge) {
        this.sDocumentCharge.set(sDocumentCharge);
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

    public void setSCreatedTime(String sCreatedTime) {
        this.sCreatedTime.set(sCreatedTime);
    }
    
}
