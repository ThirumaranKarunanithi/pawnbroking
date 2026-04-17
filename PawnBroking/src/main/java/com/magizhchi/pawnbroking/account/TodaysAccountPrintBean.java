/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.account;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class TodaysAccountPrintBean {
    
    private String sOperation;
    private int dCount;
    private double dDebit;
    private double dCredit;
    private String sCreditCombo;

    public TodaysAccountPrintBean() {} 
    
    public TodaysAccountPrintBean(String sOperation, int dCount, double dDebit, double dCredit, String sCreditCombo) {
        this.sOperation = sOperation;
        this.dCount = dCount;
        this.dDebit = dDebit;
        this.dCredit = dCredit;
        this.sCreditCombo = sCreditCombo;
    }

    public String getsOperation() {
        return sOperation;
    }

    public void setsOperation(String sOperation) {
        this.sOperation = sOperation;
    }

    public int getdCount() {
        return dCount;
    }

    public void setdCount(int dCount) {
        this.dCount = dCount;
    }

    public double getdDebit() {
        return dDebit;
    }

    public void setdDebit(double dDebit) {
        this.dDebit = dDebit;
    }

    public double getdCredit() {
        return dCredit;
    }

    public void setdCredit(double dCredit) {
        this.dCredit = dCredit;
    }

    public String getsCreditCombo() {
        return sCreditCombo;
    }

    public void setsCreditCombo(String sCreditCombo) {
        this.sCreditCombo = sCreditCombo;
    }
    
    
}
