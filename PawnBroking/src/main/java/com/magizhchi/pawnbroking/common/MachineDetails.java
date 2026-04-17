/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.common;

/**
 *
 * @author Tiru
 */
public class MachineDetails {
    
    private String machineName;
    private String macAddr;
    private String softwareInstalledDate;
    private int numberOfCompaniesCanCreate;
    private boolean isPaidForJewelSaleSoftware;
    private String language;
    private String fontName;
    private String dbPassWord;
    
    public MachineDetails(String machineName, String macAddr, String softwareInstalledDate, 
            int numberOfCompaniesCanCreate, boolean isPaidForJewelSaleSoftware,
            String language, String fontName, String dbPassword) {
        this.machineName = machineName;
        this.macAddr = macAddr;
        this.softwareInstalledDate = softwareInstalledDate;
        this.numberOfCompaniesCanCreate = numberOfCompaniesCanCreate;
        this.isPaidForJewelSaleSoftware = isPaidForJewelSaleSoftware;
        this.language = language;
        this.fontName = fontName;
        this.dbPassWord = dbPassword;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }
    
    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getSoftwareInstalledDate() {
        return softwareInstalledDate;
    }

    public void setSoftwareInstalledDate(String softwareInstalledDate) {
        this.softwareInstalledDate = softwareInstalledDate;
    }
    
    public int getNumberOfCompaniesCanCreate() {
        return numberOfCompaniesCanCreate;
    }

    public void setNumberOfCompaniesCanCreate(int numberOfCompaniesCanCreate) {
        this.numberOfCompaniesCanCreate = numberOfCompaniesCanCreate;
    }

    public boolean isIsPaidForJewelSaleSoftware() {
        return isPaidForJewelSaleSoftware;
    }

    public void setIsPaidForJewelSaleSoftware(boolean isPaidForJewelSaleSoftware) {
        this.isPaidForJewelSaleSoftware = isPaidForJewelSaleSoftware;
    }
        
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getDbPassWord() {
        return dbPassWord;
    }

    public void setDbPassWord(String dbPassWord) {
        this.dbPassWord = dbPassWord;
    }
    
    
}
