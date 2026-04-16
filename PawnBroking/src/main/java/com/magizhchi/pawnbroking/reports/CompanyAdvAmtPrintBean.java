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
public class CompanyAdvAmtPrintBean {
    
    private String materialType;
    private String billNumber;      
    private String customerName;
    private String openingDate;
    private double amount;
    private double paidAmt;

    public CompanyAdvAmtPrintBean() {
    
    }

    public CompanyAdvAmtPrintBean(String billNumber, String customerName, String openingDate, double amount, double paidAmt) {
        this.billNumber = billNumber;
        this.customerName = customerName;
        this.openingDate = openingDate;
        this.amount = amount;
        this.paidAmt = paidAmt;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(String openingDate) {
        this.openingDate = openingDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getPaidAmt() {
        return paidAmt;
    }

    public void setPaidAmt(double paidAmt) {
        this.paidAmt = paidAmt;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }
    
    
}
