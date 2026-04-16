/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.noticegeneration;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class AllDetailsBean {
    
    private final SimpleStringProperty billNumber;
    private final SimpleStringProperty customerName;
    private final SimpleStringProperty spouseType;
    private final SimpleStringProperty spouseName;
    private final SimpleStringProperty doorNo;
    private final SimpleStringProperty street;
    private final SimpleStringProperty area;
    private final SimpleStringProperty city;
    private final SimpleBooleanProperty posted;
    private final SimpleBooleanProperty delivered;
    private final SimpleStringProperty fineReceived;
    private final SimpleStringProperty billClosedDate;
    private final SimpleStringProperty billOpenedDate;
    private final SimpleStringProperty amount;
    private final SimpleStringProperty mobileNumber;
    
    private final SimpleBooleanProperty multiBills;
    private final SimpleStringProperty noOfBillsForThisCustomer;
    
    private final SimpleStringProperty materialType;
    
    public AllDetailsBean(String billNumber, 
            String customerName, 
            String spouseType, 
            String spouseName, 
            String doorNo, 
            String street, 
            String area, 
            String city, 
            boolean posted, 
            boolean delivered, 
            String fineReceived, 
            String billClosedDate,
            String billOpenedDate,
            String amount,
            String mobileNumber,
            boolean multiBills,
            String noOfBillsForThisCustomer,
            String materialType) {
        
        this.billNumber = new SimpleStringProperty(billNumber);
        this.customerName = new SimpleStringProperty(customerName);
        this.spouseType = new SimpleStringProperty(spouseType);
        this.spouseName = new SimpleStringProperty(spouseName);
        this.doorNo = new SimpleStringProperty(doorNo);
        this.street = new SimpleStringProperty(street);
        this.area = new SimpleStringProperty(area);
        this.city = new SimpleStringProperty(city);
        this.posted = new SimpleBooleanProperty(posted);
        this.delivered = new SimpleBooleanProperty(delivered);
        this.fineReceived = new SimpleStringProperty(fineReceived);
        this.billClosedDate = new SimpleStringProperty(billClosedDate);
        this.multiBills = new SimpleBooleanProperty(multiBills);
        this.billOpenedDate = new SimpleStringProperty(billOpenedDate);
        this.amount = new SimpleStringProperty(amount);
        this.noOfBillsForThisCustomer = new SimpleStringProperty(noOfBillsForThisCustomer);
        this.mobileNumber = new SimpleStringProperty(mobileNumber);
        this.materialType = new SimpleStringProperty(materialType);
    }
    
    public String getBillNumber() {
        return billNumber.get();
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public String getSpouseType() {
        return spouseType.get();
    }

    public String getSpouseName() {
        return spouseName.get();
    }

    public String getDoorNo() {
        return doorNo.get();
    }

    public String getStreet() {
        return street.get();
    }

    public String getArea() {
        return area.get();
    }

    public String getCity() {
        return city.get();
    }

    public boolean getPosted() {
        return posted.get();
    }

    public boolean getDelivered() {
        return delivered.get();
    }

    public String getFineReceived() {
        return fineReceived.get();
    }

    public String getBillClosedDate() {
        return billClosedDate.get();
    }

    public String getBillOpenedDate() {
        return billOpenedDate.get();
    }

    public String getAmount() {
        return amount.get();
    }
    
    public boolean getMultiBills() {
        return multiBills.get();
    }

    public String getNoOfBillsForThisCustomer() {
        return noOfBillsForThisCustomer.get();
    }

    public String getMobileNumber() {
        return mobileNumber.get();
    }

    public String getMaterialType() {
        return materialType.get();
    }
    
    public void setBillNumber(String billNumber) {
        this.billNumber.set(billNumber);
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public void setSpouseType(String spouseType) {
        this.spouseType.set(spouseType);
    }

    public void setSpouseName(String spouseName) {
        this.spouseName.set(spouseName);
    }

    public void setDoorNo(String doorNo) {
        this.doorNo.set(doorNo);
    }

    public void setStreet(String street) {
        this.street.set(street);
    }

    public void setArea(String area) {
        this.area.set(area);
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public void setPosted(boolean posted) {
        this.posted.set(posted);
    }

    public void setDelivered(boolean delivered) {
        this.delivered.set(delivered);
    }

    public void setFineReceived(String fineReceived) {
        this.fineReceived.set(fineReceived);
    }

    public void setBillClosedDate(String billClosedDate) {
        this.billClosedDate.set(billClosedDate);
    }

    public void setBillOpenedDate(String billOpenedDate) {
        this.billOpenedDate.set(billOpenedDate);
    }

    public void setAmount(String amount) {
        this.amount.set(amount);
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber.set(mobileNumber);
    }
    
    public void setNoOfBillsForThisCustomer(String noOfBillsForThisCustomer) {
        this.noOfBillsForThisCustomer.set(noOfBillsForThisCustomer);
    }
    
    public void setMultiBills(boolean multiBills) {
        this.multiBills.set(multiBills);
    }

    public void setMaterialType(String materialType) {
        this.materialType.set(materialType);
    }
    
    public boolean isSameRecord(AllDetailsBean bean) {
        return bean.getCustomerName().equals(this.getCustomerName())
                && bean.getSpouseType().equals(this.getSpouseType())
                && bean.getSpouseName().equals(this.getSpouseName())
                && bean.getDoorNo().equals(this.getDoorNo())
                && bean.getStreet().equals(this.getStreet())
                && bean.getArea().equals(this.getArea())
                && bean.getCity().equals(this.getCity())
                && bean.getMobileNumber().equals(this.getMobileNumber());
    }
}
