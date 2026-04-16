/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.reports;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class ParticularBean {
    
    private SimpleStringProperty particulars;
    private SimpleStringProperty amount;

    public ParticularBean(String particulars, String amount) {
        this.particulars = new SimpleStringProperty(particulars);
        this.amount = new SimpleStringProperty(amount);
    }        

    public String getParticulars() {
        return particulars.get();
    }

    public void setParticulars(String particulars) {
        this.particulars.set(particulars);
    }

    public String getAmount() {
        return amount.get();
    }

    public void setAmount(String amount) {
        this.amount.set(amount);
    }
}
