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
public class CompanyBillCreditPrintBean {
    
    private String materialType;   
    private String billNumber;   
    private double amount;
    private double interest;
    private double advanceAmtPaid;
    private double otherCharges;
    private double less;
    private char companyCopy;
    private char customerCopy;
    private char packingCopy;
    private String reason;

    public CompanyBillCreditPrintBean() {
    
    }

    public CompanyBillCreditPrintBean(String materialType, String billNumber, double amount, double interest, double advanceAmtPaid, double otherCharges, double less, char companyCopy, char customerCopy, char packingCopy, String reason) {
        this.materialType = materialType;
        this.billNumber = billNumber;
        this.amount = amount;
        this.interest = interest;
        this.advanceAmtPaid = advanceAmtPaid;
        this.otherCharges = otherCharges;
        this.less = less;
        this.companyCopy = companyCopy;
        this.customerCopy = customerCopy;
        this.packingCopy = packingCopy;
        this.reason = reason;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public double getAdvanceAmtPaid() {
        return advanceAmtPaid;
    }

    public void setAdvanceAmtPaid(double advanceAmtPaid) {
        this.advanceAmtPaid = advanceAmtPaid;
    }

    public double getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(double otherCharges) {
        this.otherCharges = otherCharges;
    }

    public double getLess() {
        return less;
    }

    public void setLess(double less) {
        this.less = less;
    }

    public char getCompanyCopy() {
        return companyCopy;
    }

    public void setCompanyCopy(char companyCopy) {
        this.companyCopy = companyCopy;
    }

    public char getCustomerCopy() {
        return customerCopy;
    }

    public void setCustomerCopy(char customerCopy) {
        this.customerCopy = customerCopy;
    }

    public char getPackingCopy() {
        return packingCopy;
    }

    public void setPackingCopy(char packingCopy) {
        this.packingCopy = packingCopy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    
    
}
