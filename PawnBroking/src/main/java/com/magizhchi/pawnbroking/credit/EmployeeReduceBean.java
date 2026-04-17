/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.credit;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class EmployeeReduceBean {
    
    private final SimpleStringProperty sSlNo;
    private final SimpleStringProperty sId;
    private final SimpleStringProperty sDate;
    private final SimpleStringProperty sReason;
    private final SimpleDoubleProperty dAmount;
    private final SimpleBooleanProperty bChecked;

    public EmployeeReduceBean(String sSlNo, String sId, String sDate, String sReason, double dAmount, boolean bChecked) {
        
        this.sSlNo = new SimpleStringProperty(sSlNo);
        this.sId = new SimpleStringProperty(sId);
        this.sDate = new SimpleStringProperty(sDate);
        this.sReason = new SimpleStringProperty(sReason);
        this.dAmount = new SimpleDoubleProperty(dAmount);
        this.bChecked = new SimpleBooleanProperty(bChecked);
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

    public void setSId(String sBillNumber) {
        this.sId.set(sBillNumber);
    }
    
    public double getDAmount() {
        return dAmount.get();
    }

    public void setDAmount(double dAmount) {
        this.dAmount.set(dAmount);
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
