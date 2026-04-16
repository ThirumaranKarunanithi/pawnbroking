/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.repledgemaster;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class AllDetailsBean {
    
    private final SimpleStringProperty sId;
    private final SimpleStringProperty sName;
    private final SimpleStringProperty sArea;
    private final SimpleStringProperty sLandlineNumber;
    private final SimpleStringProperty sInterestType;
    private final SimpleStringProperty sStatus;

    public AllDetailsBean(String sId, String sName, String sArea, String sLandlineNumber, String sInterestType, String sStatus) {
        
        this.sId = new SimpleStringProperty(sId);
        this.sName = new SimpleStringProperty(sName);
        this.sArea = new SimpleStringProperty(sArea);
        this.sLandlineNumber = new SimpleStringProperty(sLandlineNumber);        
        this.sInterestType = new SimpleStringProperty(sInterestType);
        this.sStatus = new SimpleStringProperty(sStatus);
    }

    public String getSId() {
        return sId.get();
    }
    
    public String getSName() {
        return sName.get();
    }
    
    public String getSArea() {
        return sArea.get();
    }
    
    public String getSLandlineNumber() {
        return sLandlineNumber.get();
    }

    public String getSInterestType() {
        return sInterestType.get();
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
    
    public void setSArea(String sArea) {
        this.sArea.set(sArea);
    }
    
    public void setSLandlineNumber(String sLandlineNumber) {
        this.sLandlineNumber.set(sLandlineNumber);
    }    

    public void setSInterestType(String sInterestType) {
        this.sInterestType.set(sInterestType);
    }
    
    public void setSStatus(String sStatus) {
        this.sStatus.set(sStatus);
    }
}
