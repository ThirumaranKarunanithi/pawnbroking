/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companymaster;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Tiru
 */
public class NumberGeneratorBean {
    
    private final IntegerProperty iRowNo;
    private final StringProperty sPrefix;   
    private final LongProperty lFrom;
    private final LongProperty lTo;
    
    
    public NumberGeneratorBean(int iRowNo, String sPrefix, long lFrom, long lTo) {
        
        this.iRowNo = new SimpleIntegerProperty(iRowNo);
        this.sPrefix = new SimpleStringProperty(sPrefix);
        this.lFrom = new SimpleLongProperty(lFrom);
        this.lTo = new SimpleLongProperty(lTo);
    }    

    public int getIRowNo() {
        return iRowNo.get();
    }

    public String getSPrefix() {
        return sPrefix.get();
    }

    public long getLFrom() {
        return lFrom.get();
    }

    public long getLTo() {
        return lTo.get();
    }
    
    public void setIRowNo(int iRowNo) {
        this.iRowNo.set(iRowNo);
    }
    
    public void setSPrefix(String sPrefix) {
        this.sPrefix.set(sPrefix);
    }

    public void setLFrom(long lFrom) {
        this.lFrom.set(lFrom);
    }

    public void setLTo(long lTo) {
        this.lTo.set(lTo);
    }
    
}
