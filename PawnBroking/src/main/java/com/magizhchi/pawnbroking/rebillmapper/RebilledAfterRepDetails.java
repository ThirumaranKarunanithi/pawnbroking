/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.rebillmapper;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class RebilledAfterRepDetails {
    
    private final SimpleStringProperty sOldCompBillNumber;
    private final SimpleStringProperty sRepAmt;
    private final SimpleStringProperty sNewCompBillNumber;
    private final SimpleStringProperty sCompAmt;
    private final SimpleStringProperty sRepOpenedDate;
    private final SimpleStringProperty sTotAdvAmt;
    private final SimpleStringProperty sRepName;
    private final SimpleStringProperty sRepBillNo;
    private final SimpleStringProperty sRatePerGm;
    private final SimpleStringProperty sGrossWt;
    private final SimpleStringProperty sItems;
    private final SimpleStringProperty sRepBillId;
    
    public RebilledAfterRepDetails(String sOldCompBillNumber, String sRepAmt, String sNewCompBillNumber, 
            String sCompAmt, String sRepOpenedDate, 
            String sTotAdvAmt, String sRepName, 
            String sRepBillNo, String sRatePerGm, String sGrossWt, String sItems, String sRepBillId) {
        
        this.sOldCompBillNumber = new SimpleStringProperty(sOldCompBillNumber);
        this.sRepAmt = new SimpleStringProperty(sRepAmt);
        this.sNewCompBillNumber = new SimpleStringProperty(sNewCompBillNumber);
        this.sCompAmt = new SimpleStringProperty(sCompAmt);
        this.sRepOpenedDate = new SimpleStringProperty(sRepOpenedDate);
        this.sTotAdvAmt = new SimpleStringProperty(sTotAdvAmt);        
        this.sRepName = new SimpleStringProperty(sRepName);
        this.sRepBillNo = new SimpleStringProperty(sRepBillNo);
        this.sRatePerGm = new SimpleStringProperty(sRatePerGm);
        this.sGrossWt = new SimpleStringProperty(sGrossWt);
        this.sItems = new SimpleStringProperty(sItems);
        this.sRepBillId = new SimpleStringProperty(sRepBillId);
    }

    public String getSOldCompBillNumber() {
        return sOldCompBillNumber.get();
    }

    public void setSOldCompBillNumber(String sOldCompBillNumber) {
        this.sOldCompBillNumber.set(sOldCompBillNumber);
    }

    public String getSRepAmt() {
        return sRepAmt.get();
    }

    public void setSRepAmt(String sRepAmt) {
        this.sRepAmt.set(sRepAmt);
    }
    
    public String getSNewCompBillNumber() {
        return sNewCompBillNumber.get();
    }

    public void setSNewCompBillNumber(String sNewCompBillNumber) {
        this.sNewCompBillNumber.set(sNewCompBillNumber);
    }
    
    public String getSCompAmt() {
        return sCompAmt.get();
    }

    public void setSCompAmt(String sCompAmt) {
        this.sCompAmt.set(sCompAmt);
    }
    
    public String getSRepOpenedDate() {
        return sRepOpenedDate.get();
    }

    public void setSRepOpenedDate(String sRepOpenedDate) {
        this.sRepOpenedDate.set(sRepOpenedDate);
    }
    
    public String getSTotAdvAmt() {
        return sTotAdvAmt.get();
    }

    public void setSTotAdvAmt(String sTotAdvAmt) {
        this.sTotAdvAmt.set(sTotAdvAmt);
    }
    
    public String getSRepName() {
        return sRepName.get();
    }

    public void setSRepName(String sRepName) {
        this.sRepName.set(sRepName);
    }
    
    public String getSRepBillNo() {
        return sRepBillNo.get();
    }

    public void setSRepBillNo(String sRepBillNo) {
        this.sRepBillNo.set(sRepBillNo);
    }
    
    public String getSRatePerGm() {
        return sRatePerGm.get();
    }

    public void setSRatePerGm(String sRatePerGm) {
        this.sRatePerGm.set(sRatePerGm);
    }
    
    public String getSGrossWt() {
        return sGrossWt.get();
    }

    public void setSGrossWt(String sGrossWt) {
        this.sGrossWt.set(sGrossWt);
    }
    
    public String getSItems() {
        return sItems.get();
    }
    
    public void setSItems(String sItems) {
        this.sItems.set(sItems);
    }

    public String getSRepBillId() {
        return sRepBillId.get();
    }
    
    public void setSRepBillId(String sRepBillId) {
        this.sRepBillId.set(sRepBillId);
    }
    
}
