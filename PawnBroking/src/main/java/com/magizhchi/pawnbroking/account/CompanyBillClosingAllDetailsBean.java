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
public class CompanyBillClosingAllDetailsBean {
    
    private final SimpleStringProperty sSlNo;
    private final SimpleStringProperty sDate;
    private final SimpleStringProperty sBillNumber;    
    private final SimpleStringProperty sName;
    private final SimpleStringProperty sItems;
    private final SimpleStringProperty sInterest;
    private final SimpleStringProperty sAmount;
    private final SimpleStringProperty sInterestedAmt;
    private final SimpleStringProperty sToGive;
    private final SimpleStringProperty sGiven;
    private final SimpleStringProperty sCreatedDate;
    private final SimpleStringProperty sCreatedUser;
    private final SimpleDoubleProperty dTotAdvanceAmtPaid;
    
    public CompanyBillClosingAllDetailsBean(String sSlNo, String sDate, 
            String sBillNumber, String sName, String sItems, String sInterest, 
            String sAmount, String sInterestedAmt, String sToGive, String sGiven, 
            String sCreatedDate, String sCreatedUser, double dTotAdvanceAmtPaid) {
        
        this.sSlNo = new SimpleStringProperty(sSlNo);
        this.sDate = new SimpleStringProperty(sDate);
        this.sBillNumber = new SimpleStringProperty(sBillNumber);        
        this.sName = new SimpleStringProperty(sName);
        this.sItems = new SimpleStringProperty(sItems);
        this.sInterest = new SimpleStringProperty(sInterest);
        this.sAmount = new SimpleStringProperty(sAmount);
        this.sInterestedAmt = new SimpleStringProperty(sInterestedAmt);
        this.sToGive = new SimpleStringProperty(sToGive);        
        this.sGiven = new SimpleStringProperty(sGiven);
        this.sCreatedDate = new SimpleStringProperty(sCreatedDate);
        this.sCreatedUser = new SimpleStringProperty(sCreatedUser);
        this.dTotAdvanceAmtPaid = new SimpleDoubleProperty(dTotAdvanceAmtPaid);
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
    
    public String getSName() {
        return sName.get();
    }

    public String getSItems() {
        return sItems.get();
    }

    public String getSInterest() {
        return sInterest.get();
    }
    
    public String getSAmount() {
        return sAmount.get();
    }
    
    public String getSInterestedAmt() {
        return sInterestedAmt.get();
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
    
    public String getSCreatedDate() {
        return sCreatedDate.get();
    }

    public double getDTotAdvanceAmtPaid() {
        return dTotAdvanceAmtPaid.get();
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
    
    public void setSName(String sName) {
        this.sName.set(sName);
    }
        
    public void setSItems(String sItems) {
        this.sItems.set(sItems);
    }

    public void setSInterest(String sInterest) {
        this.sInterest.set(sInterest);
    }
    
    public void setSAmount(String sAmount) {
        this.sAmount.set(sAmount);
    }

    public void setSInterestedAmt(String sInterestedAmt) {
        this.sInterestedAmt.set(sInterestedAmt);
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

    public void setSCreatedDate(String sCreatedDate) {
        this.sCreatedDate.set(sCreatedDate);
    }

    public void setDTotAdvanceAmtPaid(double dTotAdvanceAmtPaid) {
        this.dTotAdvanceAmtPaid.set(dTotAdvanceAmtPaid);
    }
    
}
