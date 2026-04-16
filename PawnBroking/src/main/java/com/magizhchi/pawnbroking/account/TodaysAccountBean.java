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
public class TodaysAccountBean {
    
    private final SimpleStringProperty sOperation;
    private final SimpleDoubleProperty dCount;
    private final SimpleDoubleProperty dDebit;
    private final SimpleDoubleProperty dCredit;
    private final SimpleStringProperty sCreditCombo;
    
    public TodaysAccountBean(String sOperation, double dCount, double dDebit, double dCredit, String sCreditCombo) {
        
        this.sOperation = new SimpleStringProperty(sOperation);
        this.dCount = new SimpleDoubleProperty(dCount);
        this.dDebit = new SimpleDoubleProperty(dDebit);
        this.dCredit = new SimpleDoubleProperty(dCredit);
        this.sCreditCombo = new SimpleStringProperty(sCreditCombo);
    }

    public String getSOperation() {
        return sOperation.get();
    }

    public void setSOperation(String sOperation) {
        this.sOperation.set(sOperation);
    }
    
    public double getDCount() {
        return dCount.get();
    }

    public void setDCount(double dCount) {
        this.dCount.set(dCount);
    }

    public double getDDebit() {
        return dDebit.get();
    }

    public void setDDebit(double dDebit) {
        this.dDebit.set(dDebit);
    }

    public double getDCredit() {
        return dCredit.get();
    }

    public void setDCredit(double dCredit) {
        this.dCredit.set(dCredit);
    }

    public String getSCreditCombo() {
        return sCreditCombo.get();
    }

    public void setSCreditCombo(String sCreditCombo) {
        this.sCreditCombo.set(sCreditCombo);
    }
    
}
