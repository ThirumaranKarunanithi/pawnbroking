/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.reports;

/**
 *
 * @author tiruk
 */
public class RepledgeNameGroup {
    
    private int slNo;
    private String repledgeName;
    private int count;
    private double openCapitalAmount;
    private double openTakenAmount;
    private double closeCapitalAmount;
    private double closeInterestGivenAmount;
    private double closeGivenAmount;

    public RepledgeNameGroup(int slNo, String repledgeName, int count, double openCapitalAmount, double openTakenAmount) {
        this.slNo = slNo;
        this.repledgeName = repledgeName;
        this.count = count;
        this.openCapitalAmount = openCapitalAmount;
        this.openTakenAmount = openTakenAmount;
    }

    public RepledgeNameGroup(int slNo, String repledgeName, int count, double closeCapitalAmount, double closeInterestGivenAmount, double closeGivenAmount) {
        this.slNo = slNo;
        this.repledgeName = repledgeName;
        this.count = count;
        this.closeCapitalAmount = closeCapitalAmount;
        this.closeInterestGivenAmount = closeInterestGivenAmount;
        this.closeGivenAmount = closeGivenAmount;
    }

    

    public int getSlNo() {
        return slNo;
    }

    public void setSlNo(int slNo) {
        this.slNo = slNo;
    }

    public String getRepledgeName() {
        return repledgeName;
    }

    public void setRepledgeName(String repledgeName) {
        this.repledgeName = repledgeName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getOpenCapitalAmount() {
        return openCapitalAmount;
    }

    public void setOpenCapitalAmount(double openCapitalAmount) {
        this.openCapitalAmount = openCapitalAmount;
    }

    public double getOpenTakenAmount() {
        return openTakenAmount;
    }

    public void setOpenTakenAmount(double openTakenAmount) {
        this.openTakenAmount = openTakenAmount;
    }

    public double getCloseCapitalAmount() {
        return closeCapitalAmount;
    }

    public void setCloseCapitalAmount(double closeCapitalAmount) {
        this.closeCapitalAmount = closeCapitalAmount;
    }

    public double getCloseInterestGivenAmount() {
        return closeInterestGivenAmount;
    }

    public void setCloseInterestGivenAmount(double closeInterestGivenAmount) {
        this.closeInterestGivenAmount = closeInterestGivenAmount;
    }

    public double getCloseGivenAmount() {
        return closeGivenAmount;
    }

    public void setCloseGivenAmount(double closeGivenAmount) {
        this.closeGivenAmount = closeGivenAmount;
    }

    
}
