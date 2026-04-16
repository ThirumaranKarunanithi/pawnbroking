/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.reports;

/**
 *
 * @author Tiru
 */
public class Report8020SummaryPrintBean {
    
    private int IClass;
    private String SNumOfCust;
    private String DAmount;    

    public Report8020SummaryPrintBean() {
    }

    public Report8020SummaryPrintBean(int IClass, String SNumOfCust, String DAmount) {
        this.IClass = IClass;
        this.SNumOfCust = SNumOfCust;
        this.DAmount = DAmount;
    }

    public int getIClass() {
        return IClass;
    }

    public void setIClass(int IClass) {
        this.IClass = IClass;
    }

    public String getSNumOfCust() {
        return SNumOfCust;
    }

    public void setSNumOfCust(String SNumOfCust) {
        this.SNumOfCust = SNumOfCust;
    }

    public String getDAmount() {
        return DAmount;
    }

    public void setDAmount(String DAmount) {
        this.DAmount = DAmount;
    }

    
}
