/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.ledger;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class AllDetailsBean {
    
    private final SimpleStringProperty sBillNumber;
    private final SimpleStringProperty sOpeningDate;
    private final SimpleStringProperty sName;
    private final SimpleStringProperty sSpouseType;
    private final SimpleStringProperty sSpouseName;
    private final SimpleStringProperty sAdress;
    private final SimpleDoubleProperty sAmount;
    private final SimpleStringProperty sItems;
    private final SimpleStringProperty sGrossWeight;
    private final SimpleStringProperty sClosingDate;

    private SimpleBooleanProperty bChecked;
    
    public AllDetailsBean(String sBillNumber, String sOpeningDate, 
            String sName, String sSpouseType, String sSpouseName, String sAdress, 
            double sAmount, String sItems, String sGrossWeight, String sClosingDate, boolean bChecked) {
        this.sBillNumber = new SimpleStringProperty(sBillNumber);
        this.sOpeningDate = new SimpleStringProperty(sOpeningDate);
        this.sName = new SimpleStringProperty(sName);
        this.sSpouseType = new SimpleStringProperty(sSpouseType);
        this.sSpouseName = new SimpleStringProperty(sSpouseName);
        this.sAdress = new SimpleStringProperty(sAdress);
        this.sAmount = new SimpleDoubleProperty(sAmount);
        this.sItems = new SimpleStringProperty(sItems);
        this.sGrossWeight = new SimpleStringProperty(sGrossWeight);
        this.sClosingDate = new SimpleStringProperty(sClosingDate);
        this.bChecked = new SimpleBooleanProperty(bChecked);
    }
    
        /**
     * @return the sBillNumber
     */
    public String getSBillNumber() {
        return sBillNumber.get();
    }

    /**
     * @param sBillNumber the sBillNumber to set
     */
    public void setSBillNumber(String sBillNumber) {
        this.sBillNumber.set(sBillNumber);
    }

        /**
     * @return the sBillNumber
     */
    public String getSName() {
        return sName.get();
    }

    /**
     * @param sName
     */
    public void setSName(String sName) {
        this.sName.set(sName);
    }

        /**
     * @return the sBillNumber
     */
    public String getSSpouseType() {
        return sSpouseType.get();
    }

    /**
     * @param sSpouseType
     */
    public void setSSpouseType(String sSpouseType) {
        this.sSpouseType.set(sSpouseType);
    }

        /**
     * @return the sBillNumber
     */
    public String getSSpouseName() {
        return sSpouseName.get();
    }

    /**
     * @param sSpouseName
     */
    public void setSSpouseName(String sSpouseName) {
        this.sSpouseName.set(sSpouseName);
    }

        /**
     * @return the sBillNumber
     */
    public String getSAdress() {
        return sAdress.get();
    }

    /**
     * @param sAdress
     */
    public void setSAdress(String sAdress) {
        this.sAdress.set(sAdress);
    }
    
    /**
     * @return the sItems
     */
    public String getSItems() {
        return sItems.get();
    }

    /**
     * @param sItems the sItems to set
     */
    public void setSItems(String sItems) {
        this.sItems.set(sItems);
    }

    /**
     * @return the sGrWt
     */
    public String getSGrossWeight() {
        return sGrossWeight.get();
    }

    /**
     * @param sGrWt
     */
    public void setSGrossWeight(String sGrWt) {
        this.sGrossWeight.set(sGrWt);
    }

    /**
     * @return the sOpeningDate
     */
    public String getSOpeningDate() {
        return sOpeningDate.get();
    }

    /**
     * @param sOpeningDate the sOpeningDate to set
     */
    public void setSOpeningDate(String sOpeningDate) {
        this.sOpeningDate.set(sOpeningDate);
    }

    /**
     * @return the sAmount
     */
    public double getSAmount() {
        return sAmount.get();
    }

    /**
     * @param sAmount the sAmount to set
     */
    public void setSAmount(double sAmount) {
        this.sAmount.set(sAmount);
    }
    
    /**
     * @return the sClosingDate
     */
    public String getSClosingDate() {
        return sClosingDate.get();
    }

    /**
     * @param sClosingDate the sClosingDate to set
     */
    public void setSClosingDate(String sClosingDate) {
        this.sClosingDate.set(sClosingDate);
    }
    
    public boolean isBChecked() {
        return this.bCheckedProperty().get();
    }

    public SimpleBooleanProperty bCheckedProperty() {
        return bChecked;
    }

    public boolean getBCheckedProperty() {
        return this.bCheckedProperty().get();
    }    
    
    public void setBChecked(boolean bChecked) {
        this.bChecked.set(bChecked);
    }
    
}
