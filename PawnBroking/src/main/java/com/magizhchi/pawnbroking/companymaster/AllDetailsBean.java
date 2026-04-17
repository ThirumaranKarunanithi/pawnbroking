/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companymaster;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class AllDetailsBean {
    
    private final SimpleStringProperty sId;
    private final SimpleStringProperty sName;
    private final SimpleStringProperty sLCHolderName;
    private final SimpleStringProperty sLCNumber;
    private final SimpleStringProperty sArea;
    private final SimpleStringProperty sLandlineNumber;
    private final SimpleStringProperty sStatus;

    public AllDetailsBean(String sId, String sName, String sLCHolderName, 
            String sLCNumber, String sArea, String sLandlineNumber, String sStatus) {
        
        this.sId = new SimpleStringProperty(sId);
        this.sName = new SimpleStringProperty(sName);
        this.sLCHolderName = new SimpleStringProperty(sLCHolderName);
        this.sLCNumber = new SimpleStringProperty(sLCNumber);
        this.sArea = new SimpleStringProperty(sArea);
        this.sLandlineNumber = new SimpleStringProperty(sLandlineNumber);  
        this.sStatus = new SimpleStringProperty(sStatus);  
    }

    public String getSId() {
        return sId.get();
    }
    
    public String getSName() {
        return sName.get();
    }

    public String getSLCHolderName() {
        return sLCHolderName.get();
    }
    
    public String getSLCNumber() {
        return sLCNumber.get();
    }
    
    public String getSArea() {
        return sArea.get();
    }
    
    public String getSLandlineNumber() {
        return sLandlineNumber.get();
    }

    public String getSStatus() {
        return sStatus.get();
    }
    
    public void setSId(String sId) {
        this.sId.set(sId);
    }
    
    public void setSName(String sName) {
        this.sName.set(sName);
    }

    public void setSLCHolderName(String sLCHolderName) {
        this.sLCHolderName.set(sLCHolderName);
    }
    
    public void setSLCNumber(String sLCNumber) {
        this.sLCNumber.set(sLCNumber);
    }
    
    public void setSArea(String sArea) {
        this.sArea.set(sArea);
    }
    
    public void setSLandlineNumber(String sLandlineNumber) {
        this.sLandlineNumber.set(sLandlineNumber);
    }    

    public void setSStatus(String sStatus) {
        this.sStatus.set(sStatus);
    }    
    
}
