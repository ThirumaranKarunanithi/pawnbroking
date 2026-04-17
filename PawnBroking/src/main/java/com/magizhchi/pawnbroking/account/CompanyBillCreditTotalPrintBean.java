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
public class CompanyBillCreditTotalPrintBean {
    
    private String materialType;
    private int totalCount;        
    private double totalAmount;
    private double totalInterest;
    private double totalAdvanceAmtPaidAlready;
    private double totalOtherCharges;
    private double totalLess;

    public CompanyBillCreditTotalPrintBean() {
    
    }

    public CompanyBillCreditTotalPrintBean(String materialType, int totalCount, double totalAmount, double totalInterest, double totalAdvanceAmtPaidAlready, double totalOtherCharges, double totalLess) {
        this.materialType = materialType;
        this.totalCount = totalCount;
        this.totalAmount = totalAmount;
        this.totalInterest = totalInterest;
        this.totalAdvanceAmtPaidAlready = totalAdvanceAmtPaidAlready;
        this.totalOtherCharges = totalOtherCharges;
        this.totalLess = totalLess;
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

    public double getTotalAdvanceAmtPaidAlready() {
        return totalAdvanceAmtPaidAlready;
    }

    public void setTotalAdvanceAmtPaidAlready(double totalAdvanceAmtPaidAlready) {
        this.totalAdvanceAmtPaidAlready = totalAdvanceAmtPaidAlready;
    }

    public double getTotalOtherCharges() {
        return totalOtherCharges;
    }

    public void setTotalOtherCharges(double totalOtherCharges) {
        this.totalOtherCharges = totalOtherCharges;
    }

    public double getTotalLess() {
        return totalLess;
    }

    public void setTotalLess(double totalLess) {
        this.totalLess = totalLess;
    }
    
    
    
}
