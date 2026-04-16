/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companyadvanceamount;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class AdvanceAmountBean {
    
    private final SimpleStringProperty sBillNumber;
    private final SimpleStringProperty sDate;    
    private final SimpleDoubleProperty dBillAmount;
    private final SimpleDoubleProperty dPaidAmount;
    private final SimpleDoubleProperty dTotalAmount;

    public AdvanceAmountBean(String sBillNumber, String sDate, double dBillAmount, double dPaidAmount, double dTotalAmount) {
        
        this.sBillNumber = new SimpleStringProperty(sBillNumber);
        this.sDate = new SimpleStringProperty(sDate);        
        this.dBillAmount = new SimpleDoubleProperty(dBillAmount);
        this.dPaidAmount = new SimpleDoubleProperty(dPaidAmount);
        this.dTotalAmount = new SimpleDoubleProperty(dTotalAmount);
    }

    public String getSBillNumber() {
        return sBillNumber.get();
    }
    
    public String getSDate() {
        return sDate.get();
    }
    
    public void setSBillNumber(String sBillNumber) {
        this.sBillNumber.set(sBillNumber);
    }
    
    public void setSDate(String sDate) {
        this.sDate.set(sDate);
    }    
    
    public double getDBillAmount() {
        return dBillAmount.get();
    }

    public void setDBillAmount(double dBillAmount) {
        this.dBillAmount.set(dBillAmount);
    }

    public double getDPaidAmount() {
        return dPaidAmount.get();
    }

    public void setDPaidAmount(double dPaidAmount) {
        this.dPaidAmount.set(dPaidAmount);
    }

    public double getDTotalAmount() {
        return dTotalAmount.get();
    }

    public void setDTotalAmount(double dTotalAmount) {
        this.dTotalAmount.set(dTotalAmount);
    }

}
