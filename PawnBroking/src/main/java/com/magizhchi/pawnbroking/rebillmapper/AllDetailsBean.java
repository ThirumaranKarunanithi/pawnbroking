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
public class AllDetailsBean {
    
    private final SimpleStringProperty sMaterial;
    private final SimpleStringProperty sOperation;
    private final SimpleStringProperty sBillNumber;
    private final SimpleStringProperty sOpeningDate;    
    private final SimpleStringProperty sClosingDate;
    private final SimpleStringProperty sName;  
    private final SimpleStringProperty sAmount;  
    private final SimpleStringProperty sStatus;
    private final SimpleStringProperty sRepledgeBillId;
    
    public AllDetailsBean( 
     String sMaterial,
     String sOperation,
     String sBillNumber,     
     String sOpeningDate,     
     String sClosingDate,     
     String sName,   
     String sAmount,   
     String sStatus,
     String sRepledgeBillId) {

        this.sMaterial = new SimpleStringProperty(sMaterial);
        this.sOperation = new SimpleStringProperty(sOperation);
        this.sBillNumber = new SimpleStringProperty(sBillNumber);
        this.sOpeningDate = new SimpleStringProperty(sOpeningDate);        
        this.sClosingDate = new SimpleStringProperty(sClosingDate);
        this.sName = new SimpleStringProperty(sName);   
        this.sAmount = new SimpleStringProperty(sAmount);   
        this.sStatus = new SimpleStringProperty(sStatus);
        this.sRepledgeBillId = new SimpleStringProperty(sRepledgeBillId);
    }

    public String getSMaterial() {
        return sMaterial.get();
    }

    public void setSMaterial(String sMaterial) {
        this.sMaterial.set(sMaterial);
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

    public String getSClosingDate() {
        return sClosingDate.get();
    }

    public void setSClosingDate(String sClosingDate) {
        this.sClosingDate.set(sClosingDate);
    }
    
    public String getSName() {
        return sName.get();
    }

    public void setSName(String sName) {
        this.sName.set(sName);
    }
    
    public String getSAmount() {
        return sAmount.get();
    }

    public void setSAmount(String sAmount) {
        this.sAmount.set(sAmount);
    }
    
            
    public String getSStatus() {
        return sStatus.get();
    }

    public void setSStatus(String sStatus) {
        this.sStatus.set(sStatus);
    }

    public String getSRepledgeBillId() {
        return sRepledgeBillId.get();
    }

    public void setSRepledgeBillId(String sRepledgeBillId) {
        this.sRepledgeBillId.set(sRepledgeBillId);
    }
    
}
