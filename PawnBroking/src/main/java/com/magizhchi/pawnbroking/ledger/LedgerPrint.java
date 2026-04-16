/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.ledger;

/**
 *
 * @author Tiru
 */
public class LedgerPrint {
    
    private String billNumber;
    private String openingDate;
    private String customerName;
    private String spouseType;
    private String spouseName;
    private String address;
    private double amount;
    private String items;
    private String grWt;
    private String closingDate;

    public LedgerPrint() {
    }
    
    public LedgerPrint(String billNumber, String openingDate, String customerName, String spouseType, String spouseName, String address, double amount, String items, String grWt, String closingDate) {
        this.billNumber = billNumber;
        this.openingDate = openingDate;
        this.customerName = customerName;
        this.spouseType = spouseType;
        this.spouseName = spouseName;
        this.address = address;
        this.amount = amount;
        this.items = items;
        this.grWt = grWt;
        this.closingDate = closingDate;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(String openingDate) {
        this.openingDate = openingDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSpouseType() {
        return spouseType;
    }

    public void setSpouseType(String spouseType) {
        this.spouseType = spouseType;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getGrWt() {
        return grWt;
    }

    public void setGrWt(String grWt) {
        this.grWt = grWt;
    }

    public String getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(String closingDate) {
        this.closingDate = closingDate;
    }

    
}
