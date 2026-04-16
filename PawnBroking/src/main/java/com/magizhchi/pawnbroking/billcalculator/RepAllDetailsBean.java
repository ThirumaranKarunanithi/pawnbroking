/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.billcalculator;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class RepAllDetailsBean {
    
    private final SimpleStringProperty sBOrA;
    private final SimpleStringProperty sOperation;
    private final SimpleStringProperty sBillNumber;
    private final SimpleStringProperty sOpeningDate;
    private final SimpleStringProperty sRepBillNumber;
    private final SimpleStringProperty sRepName;
    private final SimpleStringProperty sMaterial;
    private final SimpleStringProperty sItems;
    private final SimpleStringProperty sAmount;    
    private final SimpleStringProperty sInterest;
    private final SimpleStringProperty sInterestedAmt;
    private final SimpleStringProperty sTotalInterestedAmt;
    private final SimpleStringProperty sRepId;
    private final SimpleStringProperty sRepBillId;
    private final SimpleStringProperty sActualBillNumber;
    private final SimpleStringProperty sStatus;
    private SimpleBooleanProperty bChecked;
    
    public RepAllDetailsBean( String sBOrA,
     String sOperation,
     String sBillNumber,
     String sOpeningDate,
     String sRepBillNumber,
     String sRepName,
     String sMaterial,
     String sItems,
     String sAmount,    
     String sInterest,
     String sInterestedAmt,
     String sTotalInterestedAmt,
     String sRepId,
     String sRepBillId,
     String sActualBillNumber,
     String sStatus,
     boolean bChecked) {

        this.sBOrA = new SimpleStringProperty(sBOrA);
        this.sOperation = new SimpleStringProperty(sOperation);
        this.sBillNumber = new SimpleStringProperty(sBillNumber);
        this.sOpeningDate = new SimpleStringProperty(sOpeningDate);
        this.sRepBillNumber = new SimpleStringProperty(sRepBillNumber);
        this.sRepName = new SimpleStringProperty(sRepName);
        this.sMaterial = new SimpleStringProperty(sMaterial);
        this.sItems = new SimpleStringProperty(sItems);
        this.sAmount = new SimpleStringProperty(sAmount);    
        this.sInterest = new SimpleStringProperty(sInterest);
        this.sInterestedAmt = new SimpleStringProperty(sInterestedAmt);
        this.sTotalInterestedAmt = new SimpleStringProperty(sTotalInterestedAmt);        
        this.sRepId = new SimpleStringProperty(sRepName);
        this.sRepBillId = new SimpleStringProperty(sRepBillId);
        this.sActualBillNumber = new SimpleStringProperty(sActualBillNumber);
        this.sStatus = new SimpleStringProperty(sStatus);
        this.bChecked = new SimpleBooleanProperty(bChecked);
    }

    public String getSRepId() {
        return sRepId.get();
    }

    public void setSRepId(String sRepId) {
        this.sRepId.set(sRepId);
    }

    public String getSRepBillId() {
        return sRepBillId.get();
    }

    public void setSRepBillId(String sRepBillId) {
        this.sRepBillId.set(sRepBillId);
    }

    public String getSActualBillNumber() {
        return sActualBillNumber.get();
    }
    
    public void setSActualBillNumber(String sActualBillNumber) {
        this.sActualBillNumber.set(sActualBillNumber);
    }
    
    public String getSBOrA() {
        return sBOrA.get();
    }

    public void setSBOrA(String sBOrA) {
        this.sBOrA.set(sBOrA);
    }
    
    public String getSOperation() {
        return sOperation.get();
    }

    public void setSOperation(String sOperation) {
        this.sOperation.set(sOperation);
    }
    
    public String getSBillNumber() {
        return sBillNumber.get();
    }

    public void setSBillNumber(String sBillNumber) {
        this.sBillNumber.set(sBillNumber);
    }
    
    public String getSOpeningDate() {
        return sOpeningDate.get();
    }

    public void setSOpeningDate(String sOpeningDate) {
        this.sOpeningDate.set(sOpeningDate);
    }
    
    public String getSRepBillNumber() {
        return sRepBillNumber.get();
    }

    public void setSRepBillNumber(String sRepBillNumber) {
        this.sRepBillNumber.set(sRepBillNumber);
    }
    
    public String getSRepName() {
        return sRepName.get();
    }

    public void setSRepName(String sRepName) {
        this.sRepName.set(sRepName);
    }
    
    public String getSMaterial() {
        return sMaterial.get();
    }

    public void setSMaterial(String sMaterial) {
        this.sMaterial.set(sMaterial);
    }
    
    public String getSItems() {
        return sItems.get();
    }

    public void setSItems(String sItems) {
        this.sItems.set(sItems);
    }
    
    public String getSAmount() {
        return sAmount.get();
    }

    public void setSAmount(String sAmount) {
        this.sAmount.set(sAmount);
    }
    
    public String getSInterest() {
        return sInterest.get();
    }

    public void setSInterest(String sInterest) {
        this.sInterest.set(sInterest);
    }
    
    public String getSInterestedAmt() {
        return sInterestedAmt.get();
    }

    public void setSInterestedAmt(String sInterestedAmt) {
        this.sInterestedAmt.set(sInterestedAmt);
    }
    
    public String getSTotalInterestedAmt() {
        return sTotalInterestedAmt.get();
    }

    public void setSTotalInterestedAmt(String sTotalInterestedAmt) {
        this.sTotalInterestedAmt.set(sTotalInterestedAmt);
    }

    public String getSStatus() {
        return sStatus.get();
    }

    public void setSStatus(String sStatus) {
        this.sStatus.set(sStatus);
    }
    
    public boolean isBChecked() {
        return this.bCheckedProperty().get();
    }

    public SimpleBooleanProperty bCheckedProperty() {
        return bChecked;
    }

    public boolean getBCheckedProperty() {
        return this.bCheckedProperty().get();
    }    
    
    public void setBChecked(boolean bChecked) {
        this.bChecked.set(bChecked);
    }
    
}
