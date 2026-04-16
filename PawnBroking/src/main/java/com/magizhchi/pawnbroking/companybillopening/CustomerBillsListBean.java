/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companybillopening;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class CustomerBillsListBean {
    
    private SimpleStringProperty repledgeBillId;
    private SimpleStringProperty materialType;
    private SimpleStringProperty billNumber;
    private SimpleStringProperty date;    
    private SimpleStringProperty items;
    private SimpleStringProperty grossWeight;
    private SimpleStringProperty amount;    
    private SimpleStringProperty noticedDate;    
    private SimpleStringProperty compId;
    
    public CustomerBillsListBean(String repledgeBillId, String materialType, 
            String billNumber, String date, 
            String items, String grossWeight, 
            String amount, String noticedDate,
            String compId) {
        this.repledgeBillId = new SimpleStringProperty( repledgeBillId);
        this.materialType = new SimpleStringProperty( materialType);
        this.billNumber = new SimpleStringProperty( billNumber);
        this.date = new SimpleStringProperty( date);
        this.items = new SimpleStringProperty( items);
        this.grossWeight = new SimpleStringProperty( grossWeight);
        this.amount = new SimpleStringProperty( amount);
        this.noticedDate = new SimpleStringProperty( noticedDate);
        this.compId = new SimpleStringProperty(compId);
    }
    
    public String getRepledgeBillId() {
        return repledgeBillId.get();
    }

    public void setRepledgeBillId(String repledgeBillId) {
        this.repledgeBillId.set(repledgeBillId);
    }

    public String getMaterialType() {
        return materialType.get();
    }

    public void setMaterialType(String materialType) {
        this.materialType.set(materialType);
    }

    public String getBillNumber() {
        return billNumber.get();
    }

    public void setBillNumber(String billNumber) {
        this.billNumber.set(billNumber);
    }
    
    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getItems() {
        return items.get();
    }

    public void setItems(String items) {
        this.items.set(items);
    }

    public String getGrossWeight() {
        return grossWeight.get();
    }

    public void setGrossWeight(String grossWeight) {
        this.grossWeight.set(grossWeight);
    }

    public String getAmount() {
        return amount.get();
    }

    public void setAmount(String amount) {
        this.amount.set(amount);
    }

    public String getNoticedDate() {
        return noticedDate.get();
    }

    public void setNoticedDate(String noticedDate) {
        this.noticedDate.set(noticedDate);
    }    

    public String getCompId() {
        return compId.get();
    }

    public void setCompId(String compId) {
        this.compId.set(compId);
    }
    
    
    
}
