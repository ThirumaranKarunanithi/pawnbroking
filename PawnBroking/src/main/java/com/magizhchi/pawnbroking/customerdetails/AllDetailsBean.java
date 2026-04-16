/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.customerdetails;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class AllDetailsBean {
    
    private final SimpleIntegerProperty sStockBills;
    private final SimpleIntegerProperty sOpenedBills;
    private final SimpleStringProperty sClosedBills;
    private final SimpleStringProperty sName;
    private final SimpleStringProperty sGender;
    private final SimpleStringProperty sSpouseType;
    private final SimpleStringProperty sSpouseName;
    private final SimpleStringProperty sDoorNumber;
    private final SimpleStringProperty sStreet;
    private final SimpleStringProperty sArea;
    private final SimpleStringProperty sCity;
    private final SimpleStringProperty sMobileNumber;  
    private final SimpleStringProperty sCustomerId;  
    
    public AllDetailsBean(int sStockBills, int sOpenedBills, String sClosedBills, String sName, String sGender, 
            String sSpouseType, String sSpouseName, 
            String sDoorNumber, String sStreet, String sArea, String sCity,
            String sMobileNumber, String sCustomerId) {
        
        this.sStockBills = new SimpleIntegerProperty(sStockBills);
        this.sOpenedBills = new SimpleIntegerProperty(sOpenedBills);
        this.sClosedBills = new SimpleStringProperty(sClosedBills);
        this.sName = new SimpleStringProperty(sName);
        this.sGender = new SimpleStringProperty(sGender);
        this.sSpouseType = new SimpleStringProperty(sSpouseType);        
        this.sSpouseName = new SimpleStringProperty(sSpouseName);
        this.sDoorNumber = new SimpleStringProperty(sDoorNumber);
        this.sStreet = new SimpleStringProperty(sStreet);
        this.sArea = new SimpleStringProperty(sArea);
        this.sCity = new SimpleStringProperty(sCity);
        this.sMobileNumber = new SimpleStringProperty(sMobileNumber);      
        this.sCustomerId = new SimpleStringProperty(sCustomerId);      
    }

    public int getSStockBills() {
        return sStockBills.get();
    }
    
    public int getSOpenedBills() {
        return sOpenedBills.get();
    }

    public String getSClosedBills() {
        return sClosedBills.get();
    }
    
    public String getSName() {
        return sName.get();
    }
    
    public String getSGender() {
        return sGender.get();
    }
    
    public String getSSpouseType() {
        return sSpouseType.get();
    }

    public String getSSpouseName() {
        return sSpouseName.get();
    }
    
    public String getSStreet() {
        return sStreet.get();
    }

    public String getSArea() {
        return sArea.get();
    }

    public String getSCity() {
        return sCity.get();
    }
    
    public String getSMobileNumber() {
        return sMobileNumber.get();
    }
    
    public String getSDoorNumber() {
        return sDoorNumber.get();
    }

    public String getSCustomerId() {
        return sCustomerId.get();
    }
    
    public void setSName(String sName) {
        this.sName.set(sName);
    }
    
    public void setSGender(String sGender) {
        this.sGender.set(sGender);
    }
    
    public void setSSpouseType(String sSpouseType) {
        this.sSpouseType.set(sSpouseType);
    }    
    
    public void setSSpouseName(String sSpouseName) {
        this.sSpouseName.set(sSpouseName);
    }
    
    public void setSStreet(String sStreet) {
        this.sStreet.set(sStreet);
    }

    public void setSArea(String sArea) {
        this.sArea.set(sArea);
    }

    public void setSCity(String sCity) {
        this.sCity.set(sCity);
    }
    
    public void setSMobileNumber(String sMobileNumber) {
        this.sMobileNumber.set(sMobileNumber);
    }
    
    public void setSDoorNumber(String sDoorNumber) {
        this.sDoorNumber.set(sDoorNumber);
    }
    
    public void setSStockBills(int sStockBills) {
        this.sStockBills.set(sStockBills);
    }    

    public void setSOpenedBills(int sOpenedBills) {
        this.sOpenedBills.set(sOpenedBills);
    }    

    public void setSClosedBills(String sClosedBills) {
        this.sClosedBills.set(sClosedBills);
    }    

    public void setSCustomerId(String sCustomerId) {
        this.sCustomerId.set(sCustomerId);
    }    
    
}
