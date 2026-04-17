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
public class CompanyBillAdvanceAmountAllDetailsBean {
    
    private final SimpleStringProperty sSlNo;
    private final SimpleStringProperty sDate;
    private final SimpleStringProperty sBillNumber;
    private final SimpleStringProperty sStatus;
    private final SimpleStringProperty sAmount;
    private final SimpleStringProperty sPaidAmount;
    private final SimpleStringProperty sTotalAmount;
    private final SimpleStringProperty sCreatedUser;

    public CompanyBillAdvanceAmountAllDetailsBean(String sSlNo, String sDate, 
            String sBillNumber, String sStatus,
            String sAmount, String sPaidAmount, String sTotalAmount, String sCreatedUser) {
        
        this.sSlNo = new SimpleStringProperty(sSlNo);
        this.sDate = new SimpleStringProperty(sDate);
        this.sBillNumber = new SimpleStringProperty(sBillNumber);
        this.sStatus = new SimpleStringProperty(sStatus);
        this.sAmount = new SimpleStringProperty(sAmount);
        this.sPaidAmount = new SimpleStringProperty(sPaidAmount);        
        this.sTotalAmount = new SimpleStringProperty(sTotalAmount);
        this.sCreatedUser = new SimpleStringProperty(sCreatedUser);
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
    
    public String getSAmount() {
        return sAmount.get();
    }
    
    public String getSPaidAmount() {
        return sPaidAmount.get();
    }

    public String getSTotalAmount() {
        return sTotalAmount.get();
    }

    public String getSCreatedUser() {
        return sCreatedUser.get();
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
    
    public void setSAmount(String sAmount) {
        this.sAmount.set(sAmount);
    }
        
    public void setSPaidAmount(String sPaidAmount) {
        this.sPaidAmount.set(sPaidAmount);
    }

    public void setSTotalAmount(String sTotalAmount) {
        this.sTotalAmount.set(sTotalAmount);
    }

    public void setSCreatedUser(String sCreatedUser) {
        this.sCreatedUser.set(sCreatedUser);
    }
   
}
