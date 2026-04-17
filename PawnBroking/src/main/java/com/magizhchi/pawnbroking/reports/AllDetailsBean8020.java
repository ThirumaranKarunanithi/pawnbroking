/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.reports;

import com.magizhchi.pawnbroking.common.Util;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class AllDetailsBean8020 {
    
    private SimpleStringProperty sSlNo;
    private SimpleStringProperty sCustomerDetails;
    private SimpleStringProperty sMobileNumber;
    private SimpleDoubleProperty sAmount;    
    private SimpleFloatProperty sPercent;
    private SimpleFloatProperty sRunningTotal;    
    private SimpleIntegerProperty sClass;

    public AllDetailsBean8020(String sSlNo, String sCustomerDetails, 
            String sMobileNumber, 
            double sAmount, 
            float sPercent, float sRunningTotal, 
            int sClass) {
        this.sSlNo = new SimpleStringProperty(sSlNo);
        this.sCustomerDetails = new SimpleStringProperty(sCustomerDetails);
        this.sMobileNumber = new SimpleStringProperty(sMobileNumber);
        this.sAmount = new SimpleDoubleProperty(sAmount);
        this.sPercent = new SimpleFloatProperty(sPercent);
        this.sRunningTotal = new SimpleFloatProperty(sRunningTotal);
        this.sClass = new SimpleIntegerProperty(sClass);
    }        

    public String getSSlNo() {
        return sSlNo.get();
    }

    public void setSSlNo(String sSlNo) {
        this.sSlNo.set(sSlNo);
    }
    
    public String getSCustomerDetails() {
        return sCustomerDetails.get();
    }

    public void setSCustomerDetails(String sMonths) {
        this.sCustomerDetails.set(sMonths);
    }

    public String getSMobileNumber() {
        return sMobileNumber.get();
    }

    public void setSMobileNumber(String sMobileNumber) {
        this.sMobileNumber.set(sMobileNumber);
    }

    public String getSAmount() {
        return Util.format(sAmount.get());
    }

    public void setSAmount(double sAmount) {
        this.sAmount.set(sAmount);
    }

    public float getSPercent() {
        return sPercent.get();
    }

    public void setSPercent(float sPercent) {
        this.sPercent.set(sPercent);
    }

    public float getSRunningTotal() {
        return sRunningTotal.get();
    }

    public void setSRunningTotal(float sRunningTotal) {
        this.sRunningTotal.set(sRunningTotal);
    }

    public int getSClass() {
        return sClass.get();
    }

    public void setSClass(int sClass) {
        this.sClass.set(sClass);
    }
    
}
