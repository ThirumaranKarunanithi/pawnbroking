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
public class TodaysClosedJewelAccountBean {
    
    private final SimpleStringProperty sSlNo;
    private final SimpleStringProperty sBillNumber;
    private final SimpleStringProperty sOpenedDate;
    private final SimpleStringProperty sClosedDate;
    private final SimpleDoubleProperty dAmount;
    private final SimpleDoubleProperty dInterest;
    private final SimpleDoubleProperty dInterestAmt;
    private final SimpleDoubleProperty dAdvAmount;
    private final SimpleStringProperty sToGet;
    private final SimpleStringProperty sGot;    
    private final SimpleStringProperty sClosedTime;
    private final SimpleStringProperty sCreatedUser;
    private final SimpleBooleanProperty bChecked;
    private final SimpleStringProperty sStatus;

    public TodaysClosedJewelAccountBean(String sSlNo, String sBillNumber, 
            String sOpenedDate, String sClosedDate, double dAmount, 
            double dInterest, double dInterestAmt, double dAdvAmount, String sToGet, 
            String sGot, String sClosedTime, String sCreatedUser, boolean bChecked, String sStatus) {
        
        this.sSlNo = new SimpleStringProperty(sSlNo);
        this.sBillNumber = new SimpleStringProperty(sBillNumber);
        this.sOpenedDate = new SimpleStringProperty(sOpenedDate);
        this.sClosedDate = new SimpleStringProperty(sClosedDate);
        this.dAmount = new SimpleDoubleProperty(dAmount);
        this.dInterest = new SimpleDoubleProperty(dInterest);
        this.dInterestAmt = new SimpleDoubleProperty(dInterestAmt);
        this.dAdvAmount = new SimpleDoubleProperty(dAdvAmount);
        this.sToGet = new SimpleStringProperty(sToGet);
        this.sGot = new SimpleStringProperty(sGot);
        this.sClosedTime = new SimpleStringProperty(sClosedTime);
        this.sCreatedUser = new SimpleStringProperty(sCreatedUser);
        this.bChecked = new SimpleBooleanProperty(bChecked);
        this.sStatus = new SimpleStringProperty(sStatus);
    }

    public String getSSlNo() {
        return sSlNo.get();
    }

    public String getSBillNumber() {
        return sBillNumber.get();
    }

    public String getSOpenedDate() {
        return sOpenedDate.get();
    }

    public String getSClosedDate() {
        return sClosedDate.get();
    }

    public double getDAmount() {
        return dAmount.get();
    }

    public double getDInterest() {
        return dInterest.get();
    }

    public double getDInterestAmt() {
        return dInterestAmt.get();
    }

    public double getDAdvAmount() {
        return dAdvAmount.get();
    }

    public String getSToGet() {
        return sToGet.get();
    }

    public String getSGot() {
        return sGot.get();
    }

    public String getSClosedTime() {
        return sClosedTime.get();
    }

    public String getSCreatedUser() {
        return sCreatedUser.get();
    }

    public void setSSlNo(String sSlNo) {
        this.sSlNo.set(sSlNo);
    }

    public void setSBillNumber(String sBillNumber) {
        this.sBillNumber.set(sBillNumber);
    }

    public void setSOpenedDate(String sOpenedDate) {
        this.sOpenedDate.set(sOpenedDate);
    }

    public void setSClosedDate(String sClosedDate) {
        this.sClosedDate.set(sClosedDate);
    }

    public void getDAmount(double dAmount) {
        this.dAmount.set(dAmount);
    }

    public void getDInterest(double dInterest) {
        this.dInterest.set(dInterest);
    }

    public void getDInterestAmt(double dInterestAmt) {
        this.dInterestAmt.set(dInterestAmt);
    }

    public void getDAdvAmount(double dAdvAmount) {
        this.dAdvAmount.set(dAdvAmount);
    }

    public void setSToGet(String sToGet) {
        this.sToGet.set(sToGet);
    }

    public void setSGot(String sGot) {
        this.sGot.set(sGot);
    }

    public void setSClosedTime(String sClosedTime) {
        this.sClosedTime.set(sClosedTime);
    }

    public void setSCreatedUser(String sCreatedUser) {
        this.sCreatedUser.set(sCreatedUser);
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

    public String getSStatus() {
        return sStatus.get();
    }

    public void setSStatus(String sStatus) {
        this.sStatus.set(sStatus);
    }
    
}
