/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.account;

/**
 *
 * @author Tiru
 */
public class CompanyBillDebitTotalPrintBean {
    
    private String materialType;
    private int totalCount;        
    private double totalAmount;
    private double totalInterest;
    private double totalDocCharge;

    public CompanyBillDebitTotalPrintBean() {
    
    }
    
    public CompanyBillDebitTotalPrintBean(String materialType, int totalCount, double totalAmount, double totalInterest, double totalDocCharge) {
        this.materialType = materialType;
        this.totalCount = totalCount;
        this.totalAmount = totalAmount;
        this.totalInterest = totalInterest;
        this.totalDocCharge = totalDocCharge;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(double totalInterest) {
        this.totalInterest = totalInterest;
    }

    public double getTotalDocCharge() {
        return totalDocCharge;
    }

    public void setTotalDocCharge(double totalDocCharge) {
        this.totalDocCharge = totalDocCharge;
    }            
                    
}
