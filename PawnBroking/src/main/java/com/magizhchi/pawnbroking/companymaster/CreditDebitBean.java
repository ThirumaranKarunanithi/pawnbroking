/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companymaster;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class CreditDebitBean {
    
    private final SimpleStringProperty sSlNo;
    private final SimpleStringProperty sId;
    private final SimpleStringProperty sType;
    private final SimpleStringProperty sDate;
    private final SimpleStringProperty sReason;
    private final SimpleDoubleProperty dAmount;

    public CreditDebitBean(String sSlNo, String sId, String sType, String sDate, String sReason, double dAmount) {
        
        this.sSlNo = new SimpleStringProperty(sSlNo);
        this.sId = new SimpleStringProperty(sId);
        this.sType = new SimpleStringProperty(sType);
        this.sDate = new SimpleStringProperty(sDate);
        this.sReason = new SimpleStringProperty(sReason);
        this.dAmount = new SimpleDoubleProperty(dAmount);
    }

    public String getSSlNo() {
        return sSlNo.get();
    }

    public void setSSlNo(String sSlNo) {
        this.sSlNo.set(sSlNo);
    }
    
    public String getSId() {
        return sId.get();
    }

    public void setSId(String sId) {
        this.sId.set(sId);
    }

    public String getSType() {
        return sType.get();
    }

    public void setSType(String sType) {
        this.sType.set(sType);
    }
    
    public String getSDate() {
        return sDate.get();
    }

    public void setSDate(String sDate) {
        this.sDate.set(sDate);
    }
    
    public String getSReason() {
        return sReason.get();
    }

    public void setSReason(String sReason) {
        this.sReason.set(sReason);
    }

    public double getDAmount() {
        return dAmount.get();
    }

    public void setDAmount(double dAmount) {
        this.dAmount.set(dAmount);
    }
}
