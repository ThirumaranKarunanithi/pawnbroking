/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companymaster;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class MonthSettingBean {
    
    private final SimpleStringProperty sFromDate;
    private final SimpleStringProperty sToDate;    
    private final SimpleDoubleProperty dFrom;
    private final SimpleDoubleProperty dTo;
    private final SimpleDoubleProperty dAsMonth;

    public MonthSettingBean(String sFromDate, String sToDate, double dFrom, double dTo, double dAsMonth) {
        
        this.sFromDate = new SimpleStringProperty(sFromDate);
        this.sToDate = new SimpleStringProperty(sToDate);        
        this.dFrom = new SimpleDoubleProperty(dFrom);
        this.dTo = new SimpleDoubleProperty(dTo);
        this.dAsMonth = new SimpleDoubleProperty(dAsMonth);
    }

    public String getSFromDate() {
        return sFromDate.get();
    }
    
    public void setSFromDate(String sFromDate) {
        this.sFromDate.set(sFromDate);
    }
    
    public String getSToDate() {
        return sToDate.get();
    }    

    public void setSToDate(String sToDate) {
        this.sToDate.set(sToDate);
    }    
    
    public double getDAsMonth() {
        return dAsMonth.get();
    }

    public void setDAsMonth(double dAsMonth) {
        this.dAsMonth.set(dAsMonth);
    }

    public double getDTo() {
        return dTo.get();
    }

    public void setDTo(double dTo) {
        this.dTo.set(dTo);
    }

    public double getDFrom() {
        return dFrom.get();
    }

    public void setDFrom(double dFrom) {
        this.dFrom.set(dFrom);
    }

}
