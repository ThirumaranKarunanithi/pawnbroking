/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.account;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class TodaysJewelAccountBean {
    
    private final SimpleStringProperty sSlNo;
    private final SimpleStringProperty sBillNumber;
    private final SimpleDoubleProperty dAmount;
    private final SimpleStringProperty sDate;
    private final SimpleStringProperty sStatus;
    private final SimpleStringProperty sItems;
    private final SimpleBooleanProperty bChecked;

    public TodaysJewelAccountBean(String sSlNo, String sBillNumber, double dAmount, String sDate, 
            String sStatus, String sItems, boolean bChecked) {
        
        this.sSlNo = new SimpleStringProperty(sSlNo);
        this.sBillNumber = new SimpleStringProperty(sBillNumber);
        this.dAmount = new SimpleDoubleProperty(dAmount);
        this.sDate = new SimpleStringProperty(sDate);
        this.sStatus = new SimpleStringProperty(sStatus);
        this.sItems = new SimpleStringProperty(sItems);
        this.bChecked = new SimpleBooleanProperty(bChecked);
    }

    public String getSSlNo() {
        return sSlNo.get();
    }

    public void setSSlNo(String sSlNo) {
        this.sSlNo.set(sSlNo);
    }

    public String getSBillNumber() {
        return sBillNumber.get();
    }

    public void setSBillNumber(String sBillNumber) {
        this.sBillNumber.set(sBillNumber);
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

    public String getSStatus() {
        return sStatus.get();
    }

    public void setSStatus(String sStatus) {
        this.sStatus.set(sStatus);
    }

    public String getSItems() {
        return sItems.get();
    }
    
    public void setSItems(String sItems) {
        this.sItems.set(sItems);
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
