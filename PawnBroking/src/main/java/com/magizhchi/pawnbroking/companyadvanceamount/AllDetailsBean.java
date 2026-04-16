/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companyadvanceamount;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class AllDetailsBean {
    
    private final SimpleStringProperty sBillNumber;
    private final SimpleStringProperty sDate;
    private final SimpleDoubleProperty sAmount;
    private final SimpleStringProperty sName;
    private final SimpleStringProperty sGender;
    private final SimpleStringProperty sSpouseType;
    private final SimpleStringProperty sSpouseName;
    private final SimpleStringProperty sStreet;
    private final SimpleStringProperty sArea;
    private final SimpleStringProperty sMobileNumber;
    private final SimpleStringProperty sItems;
    private final SimpleStringProperty sGrossWeight;
    private final SimpleStringProperty sNetWeight;
    private final SimpleStringProperty sPurity;
    private final SimpleStringProperty sStatus;
    private final SimpleStringProperty sNote;

    private final SimpleDoubleProperty sTotalAdvanceAmountPaid;
    private final SimpleDoubleProperty sNumberOfTimesPaid;
    private final SimpleStringProperty sPaidDate;
    private final SimpleDoubleProperty sPaidAmount;
    
    private final SimpleStringProperty sRepledgeBillId;
    
    public AllDetailsBean(String sBillNumber, String sDate, double sAmount, String sName, String sGender, 
            String sSpouseType, String sSpouseName, String sStreet, String sArea, String sMobileNumber, String sItems, 
            String sGrossWeight, String sNetWeight, String sPurity, String sStatus, String sNote, String sRepledgeBillId,
            double sTotalAdvanceAmountPaid, double sNumberOfTimesPaid, 
            String sPaidDate, double sPaidAmount) {
        
        this.sBillNumber = new SimpleStringProperty(sBillNumber);
        this.sDate = new SimpleStringProperty(sDate);
        this.sAmount = new SimpleDoubleProperty(sAmount);
        this.sName = new SimpleStringProperty(sName);
        this.sGender = new SimpleStringProperty(sGender);
        this.sSpouseType = new SimpleStringProperty(sSpouseType);        
        this.sSpouseName = new SimpleStringProperty(sSpouseName);
        this.sStreet = new SimpleStringProperty(sStreet);
        this.sArea = new SimpleStringProperty(sArea);
        this.sMobileNumber = new SimpleStringProperty(sMobileNumber);
        this.sItems = new SimpleStringProperty(sItems);
        this.sGrossWeight = new SimpleStringProperty(sGrossWeight);     
        this.sNetWeight = new SimpleStringProperty(sNetWeight);     
        this.sPurity = new SimpleStringProperty(sPurity);     
        this.sStatus = new SimpleStringProperty(sStatus);
        this.sNote = new SimpleStringProperty(sNote);        
        
        this.sTotalAdvanceAmountPaid = new SimpleDoubleProperty(sTotalAdvanceAmountPaid);        
        this.sNumberOfTimesPaid = new SimpleDoubleProperty(sNumberOfTimesPaid);
        this.sPaidDate = new SimpleStringProperty(sPaidDate);      
        this.sPaidAmount = new SimpleDoubleProperty(sPaidAmount);
        
        this.sRepledgeBillId = new SimpleStringProperty(sRepledgeBillId);        
    }

    public String getSBillNumber() {
        return sBillNumber.get();
    }
    
    public String getSDate() {
        return sDate.get();
    }

    public double getSAmount() {
        return sAmount.get();
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
    
    public String getSStreet() {
        return sStreet.get();
    }

    public String getSArea() {
        return sArea.get();
    }
    
    public String getSMobileNumber() {
        return sMobileNumber.get();
    }
    
    public String getSItems() {
        return sItems.get();
    }
    
    public String getSGrossWeight() {
        return sGrossWeight.get();
    }

    public String getSNetWeight() {
        return sNetWeight.get();
    }

    public String getSPurity() {
        return sPurity.get();
    }
    
    public String getSStatus() {
        return sStatus.get();
    }
    
    public String getSNote() {
        return sNote.get();
    }

    public String getSRepledgeBillId() {
        return sRepledgeBillId.get();
    }
    
    public double getSTotalAdvanceAmountPaid() {
        return sTotalAdvanceAmountPaid.get();
    }
    
    public double getSNumberOfTimesPaid() {
        return sNumberOfTimesPaid.get();
    }
    
    public String getSPaidDate() {
        return sPaidDate.get();
    }

    public double getSPaidAmount() {
        return sPaidAmount.get();
    }
    
    public void setSBillNumber(String sBillNumber) {
        this.sBillNumber.set(sBillNumber);
    }
    
    public void setSDate(String sDate) {
        this.sDate.set(sDate);
    }

    public void setSAmount(double sAmount) {
        this.sAmount.set(sAmount);
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
    
    public void setSStreet(String sStreet) {
        this.sStreet.set(sStreet);
    }

    public void setSArea(String sArea) {
        this.sArea.set(sArea);
    }
    
    public void setSMobileNumber(String sMobileNumber) {
        this.sMobileNumber.set(sMobileNumber);
    }
    
    public void setSItems(String sItems) {
        this.sItems.set(sItems);
    }
    
    public void setSGrossWeight(String sGrossWeight) {
        this.sGrossWeight.set(sGrossWeight);
    }    

    public void setSNetWeight(String sNetWeight) {
        this.sNetWeight.set(sNetWeight);
    }    

    public void setSPurity(String sPurity) {
        this.sPurity.set(sPurity);
    }    
    
    public void setSStatus(String sStatus) {
        this.sStatus.set(sStatus);
    }
    
    public void setSNote(String sNote) {
        this.sNote.set(sNote);
    }    

    public void setSRepledgeBillId(String sRepledgeBillId) {
        this.sRepledgeBillId.set(sRepledgeBillId);
    }    
    
    public void setSTotalAdvanceAmountPaid(double sTotalAdvanceAmountPaid) {
        this.sTotalAdvanceAmountPaid.set(sTotalAdvanceAmountPaid);
    }

    public void setSNumberOfTimesPaid(double sNumberOfTimesPaid) {
        this.sNumberOfTimesPaid.set(sNumberOfTimesPaid);
    }
    
    public void setSPaidDate(String sPaidDate) {
        this.sPaidDate.set(sPaidDate);
    }    
    
    public void setSPaidAmount(double sPaidAmount) {
        this.sPaidAmount.set(sPaidAmount);
    }    
    
}
