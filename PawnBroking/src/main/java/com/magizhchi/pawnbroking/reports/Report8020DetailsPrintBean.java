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
public class Report8020DetailsPrintBean {
    
    private String SSlNo;
    private String SCustomerDetails;
    private String DAmount;
    private float FPercent;
    private int IClass;

    public Report8020DetailsPrintBean() {
    }

    public Report8020DetailsPrintBean(String SSlNo, String SCustomerDetails, String DAmount, float FPercent, int IClass) {
        this.SSlNo = SSlNo;
        this.SCustomerDetails = SCustomerDetails;
        this.DAmount = DAmount;
        this.FPercent = FPercent;
        this.IClass = IClass;
    }

    public String getSSlNo() {
        return SSlNo;
    }

    public void setSSlNo(String SSlNo) {
        this.SSlNo = SSlNo;
    }

    public String getSCustomerDetails() {
        return SCustomerDetails;
    }

    public void setSCustomerDetails(String SCustomerDetails) {
        this.SCustomerDetails = SCustomerDetails;
    }

    public String getDAmount() {
        return DAmount;
    }

    public void setDAmount(String DAmount) {
        this.DAmount = DAmount;
    }

    public float getFPercent() {
        return FPercent;
    }

    public void setFPercent(float FPercent) {
        this.FPercent = FPercent;
    }

    public int getIClass() {
        return IClass;
    }

    public void setIClass(int IClass) {
        this.IClass = IClass;
    }
    
    
}
