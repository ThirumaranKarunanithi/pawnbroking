/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.repledgebillclosing;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class CreditDebitBean {
    
    private final SimpleStringProperty sSlNo;
    private final SimpleStringProperty sId;
    private final SimpleStringProperty sDate;
    private final SimpleDoubleProperty dAmountToBe;
    private final SimpleDoubleProperty dAmount;

    public CreditDebitBean(String sSlNo, String sId, String sDate, double dAmountToBe, double dAmount) {
        
        this.sSlNo = new SimpleStringProperty(sSlNo);
        this.sId = new SimpleStringProperty(sId);
        this.sDate = new SimpleStringProperty(sDate);
        this.dAmountToBe = new SimpleDoubleProperty(dAmountToBe);
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

    public String getSDate() {
        return sDate.get();
    }

    public void setSDate(String sDate) {
        this.sDate.set(sDate);
    }
    
    public double getDAmountToBe() {
        return dAmountToBe.get();
    }

    public void setDAmountToBe(double dAmountToBe) {
        this.dAmountToBe.set(dAmountToBe);
    }

    public double getDAmount() {
        return dAmount.get();
    }

    public void setDAmount(double dAmount) {
        this.dAmount.set(dAmount);
    }
}
