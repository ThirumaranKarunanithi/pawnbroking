/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.noticegeneration;

/**
 *
 * @author Tiru
 */
public class PrintBean {
    
    private String SLNO;
    private String SBillNumber;
    private String SOpeningDate;
    private double DAmount;

    public PrintBean getEmptyValObject() {            
        SBillNumber = "";
        SOpeningDate = "";
        DAmount = 0;
        return this;
    }
    
    public String getSLNO() {
        return SLNO;
    }

    public void setSLNO(String SLNO) {
        this.SLNO = SLNO;
    }
    
    public double getDTotInterest() {
        return DTotInterest;
    }

    public void setDTotInterest(double DTotInterest) {
        this.DTotInterest = DTotInterest;
    }
    private double DTotInterest;

    public double getDAmount() {
        return DAmount;
    }

    public void setDAmount(double DAmount) {
        this.DAmount = DAmount;
    }

    public PrintBean() {
    
    }

    public String getSBillNumber() {
        return SBillNumber;
    }

    public void setSBillNumber(String SBillNumber) {
        this.SBillNumber = SBillNumber;
    }

    public String getSOpeningDate() {
        return SOpeningDate;
    }

    public void setSOpeningDate(String SOpeningDate) {
        this.SOpeningDate = SOpeningDate;
    }
}
