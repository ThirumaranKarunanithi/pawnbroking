/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companybillclosing;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class OtherChargesBean {
    
    private final SimpleStringProperty sReason;
    private final SimpleDoubleProperty dAmountToBe;
    private final SimpleDoubleProperty dAmount;

    public OtherChargesBean(String sReason, double dAmountToBe, double dAmount) {
        
        this.sReason = new SimpleStringProperty(sReason);
        this.dAmountToBe = new SimpleDoubleProperty(dAmountToBe);
        this.dAmount = new SimpleDoubleProperty(dAmount);
    }

    public String getSReason() {
        return sReason.get();
    }

    public void setSReason(String sSlNo) {
        this.sReason.set(sSlNo);
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
