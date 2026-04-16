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
public class AllDetailsBean {
    
    private final SimpleStringProperty sBOrA;
    private final SimpleStringProperty sOperation;
    private final SimpleStringProperty sBillNumber;
    private final SimpleStringProperty sName;
    private final SimpleStringProperty sOpeningDate;
    private final SimpleStringProperty sMaterial;
    private final SimpleStringProperty sItems;
    private final SimpleStringProperty sGrWt;
    private final SimpleStringProperty sAmount;    
    private final SimpleStringProperty sInterest;
    private final SimpleStringProperty sInterestedAmt;
    private final SimpleStringProperty sTotalInterestedAmt;
    private final SimpleStringProperty sCombo;
    private final SimpleStringProperty sRepledgeBillId;
    private SimpleBooleanProperty bChecked;
    
    private final SimpleStringProperty sRatePerGm;
    
    public AllDetailsBean( String sBOrA,
     String sOperation,
     String sBillNumber,     
     String sOpeningDate,
     String sMaterial,
     String sItems,     
     String sAmount,    
     String sInterest,
     String sInterestedAmt,
     String sTotalInterestedAmt,
     String sCombo,
     String sRepledgeBillId,
     String sName,
     String sGrWt,
     boolean bChecked,
     String sRatePerGm) {

        this.sBOrA = new SimpleStringProperty(sBOrA);
        this.sOperation = new SimpleStringProperty(sOperation);
        this.sBillNumber = new SimpleStringProperty(sBillNumber);
        this.sName = new SimpleStringProperty(sName);
        this.sOpeningDate = new SimpleStringProperty(sOpeningDate);
        this.sMaterial = new SimpleStringProperty(sMaterial);
        this.sItems = new SimpleStringProperty(sItems);
        this.sGrWt = new SimpleStringProperty(sGrWt);
        this.sAmount = new SimpleStringProperty(sAmount);    
        this.sInterest = new SimpleStringProperty(sInterest);
        this.sInterestedAmt = new SimpleStringProperty(sInterestedAmt);
        this.sTotalInterestedAmt = new SimpleStringProperty(sTotalInterestedAmt); 
        this.sCombo = new SimpleStringProperty(sCombo); 
        this.sRepledgeBillId = new SimpleStringProperty(sRepledgeBillId); 
        this.bChecked = new SimpleBooleanProperty(bChecked);
        this.sRatePerGm = new SimpleStringProperty(sRatePerGm); 
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

    public String getSName() {
        return sName.get();
    }

    public void setSName(String sName) {
        this.sName.set(sName);
    }
    
    public String getSOpeningDate() {
        return sOpeningDate.get();
    }

    public void setSOpeningDate(String sOpeningDate) {
        this.sOpeningDate.set(sOpeningDate);
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

    public String getSGrWt() {
        return sGrWt.get();
    }

    public void setSGrWt(String sGrWt) {
        this.sGrWt.set(sGrWt);
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

    public String getSCombo() {
        return sCombo.get();
    }

    public void setSCombo(String sCombo) {
        this.sCombo.set(sCombo);
    }

    public String getSRepledgeBillId() {
        return sRepledgeBillId.get();
    }

    public void setSRepledgeBillId(String sRepledgeBillId) {
        this.sRepledgeBillId.set(sRepledgeBillId);
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

    public String getSRatePerGm() {
        return sRatePerGm.get();
    }

    public void setSRatePerGm(String sRatePerGm) {
        this.sRatePerGm.set(sRatePerGm);
    }
    
}
