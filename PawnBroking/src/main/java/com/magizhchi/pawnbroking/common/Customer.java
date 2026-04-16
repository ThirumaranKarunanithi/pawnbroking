/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.common;

/**
 *
 * @author tiruk
 */
public class Customer {
    
    private String id;
    private String name;
    private String gender;
    private String spouseType;
    private String spouseName;
    private String doorNo;
    private String street;
    private String area;
    private String city;
    private String mobileNumber;
    private String mobileNumber2;
    private String idProof;
    private String idNumber;
    private String referredByName;
    private String referredById;

    public Customer() {
    }
    
    public Customer(String id, String name, String gender, String spouseType, String spouseName, String doorNo, String street, String area, String city, String mobileNumber, String mobileNumber2, String idProof, String idNumber, String referredByName, String referredById) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.spouseType = spouseType;
        this.spouseName = spouseName;
        this.doorNo = doorNo;
        this.street = street;
        this.area = area;
        this.city = city;
        this.mobileNumber = mobileNumber;
        this.mobileNumber2 = mobileNumber2;
        this.idProof = idProof;
        this.idNumber = idNumber;
        this.referredByName = referredByName;
        this.referredById = referredById;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getDoorNo() {
        return doorNo;
    }

    public void setDoorNo(String doorNo) {
        this.doorNo = doorNo;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileNumber2() {
        return mobileNumber2;
    }

    public void setMobileNumber2(String mobileNumber2) {
        this.mobileNumber2 = mobileNumber2;
    }

    public String getIdProof() {
        return idProof;
    }

    public void setIdProof(String idProof) {
        this.idProof = idProof;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getReferredByName() {
        return referredByName;
    }

    public void setReferredByName(String referredByName) {
        this.referredByName = referredByName;
    }

    public String getReferredById() {
        return referredById;
    }

    public void setReferredById(String referredById) {
        this.referredById = referredById;
    }
    
    
}
