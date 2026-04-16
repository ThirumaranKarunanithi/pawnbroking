/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companyadvanceamount;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Tiru
 */
public class AvailableBalanceBean {
    
    private final SimpleDoubleProperty dRupee;
    private final SimpleIntegerProperty dNumberOfNotes;
    private final SimpleDoubleProperty dTotalAmount;
    
    public AvailableBalanceBean(double dRupee, int dNumberOfNotes, double dTotalAmount) {
        
        this.dRupee = new SimpleDoubleProperty(dRupee);
        this.dNumberOfNotes = new SimpleIntegerProperty(dNumberOfNotes);
        this.dTotalAmount = new SimpleDoubleProperty(dTotalAmount);
    }
    
    public double getDRupee() {
        return dRupee.get();
    }

    public void setDRupee(double dRupee) {
        this.dRupee.set(dRupee);
    }

    public int getDNumberOfNotes() {
        return dNumberOfNotes.get();
    }

    public void setDNumberOfNotes(int dNumberOfNotes) {
        this.dNumberOfNotes.set(dNumberOfNotes);
    }

    public double getDTotalAmount() {
        return dTotalAmount.get();
    }

    public void setDTotalAmount(double dTotalAmount) {
        this.dTotalAmount.set(dTotalAmount);
    }

}
