/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.account;

import javafx.scene.image.Image;

/**
 *
 * @author Tiru
 */
public class CompanyBillDebitPrintBean {
    
    private String materialType;      
    private String billNumber;      
    private double totalAmount;
    private double totalInterest;
    private double totalDocCharge;
    private double toGiveAmount;
    private double givenAmount;
    private String customerImage;
    private String jewelImage;
    private String needAttention;
    private int ratePerGram;
    
    public CompanyBillDebitPrintBean() {
    
    }

    public CompanyBillDebitPrintBean(String materialType, String billNumber, double totalAmount, double totalInterest, double totalDocCharge, double toGiveAmount, double givenAmount, String customerImage, String jewelImage, String needAttention, int ratePerGram) {
        this.materialType = materialType;
        this.billNumber = billNumber;
        this.totalAmount = totalAmount;
        this.totalInterest = totalInterest;
        this.totalDocCharge = totalDocCharge;
        this.toGiveAmount = toGiveAmount;
        this.givenAmount = givenAmount;
        this.customerImage = customerImage;
        this.jewelImage = jewelImage;
        this.needAttention = needAttention;
        this.ratePerGram = ratePerGram;
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

    public double getToGiveAmount() {
        return toGiveAmount;
    }

    public void setToGiveAmount(double toGiveAmount) {
        this.toGiveAmount = toGiveAmount;
    }

    public double getGivenAmount() {
        return givenAmount;
    }

    public void setGivenAmount(double givenAmount) {
        this.givenAmount = givenAmount;
    }

    public String getCustomerImage() {
        return customerImage;
    }

    public void setCustomerImage(String customerImage) {
        this.customerImage = customerImage;
    }

    public String getJewelImage() {
        return jewelImage;
    }

    public void setJewelImage(String jewelImage) {
        this.jewelImage = jewelImage;
    }

    public String getNeedAttention() {
        return needAttention;
    }

    public void setNeedAttention(String needAttention) {
        this.needAttention = needAttention;
    }

    public int getRatePerGram() {
        return ratePerGram;
    }

    public void setRatePerGram(int ratePerGram) {
        this.ratePerGram = ratePerGram;
    }

    
    
}
