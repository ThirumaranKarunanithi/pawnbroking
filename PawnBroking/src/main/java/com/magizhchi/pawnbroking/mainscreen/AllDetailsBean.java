/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.mainscreen;

import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.Util;
import com.magizhchi.pawnbroking.stockdetails.StockDetailsDBOperation;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author Tiru
 */
public class AllDetailsBean {
    
    private StockDetailsDBOperation dbOp;
    
    private SimpleStringProperty sRepledgeBillId;
    private SimpleStringProperty sBillNumber;
    private SimpleStringProperty sCustomerName;
    private SimpleStringProperty sArea;
    private SimpleStringProperty sItems;
    private SimpleDoubleProperty sGrWt;
    private SimpleStringProperty sRatePerGm;
    private SimpleStringProperty sOpeningDate;
    private SimpleDoubleProperty sAmount;
    private SimpleStringProperty sInterest;    
    private SimpleStringProperty sClosingDate;
    private SimpleStringProperty sStatus;
    private SimpleStringProperty sInterestedAmount;
    private SimpleStringProperty sTotalInterestedAmount;
    private SimpleStringProperty sMobileNumber;
    private SimpleBooleanProperty bChecked;
    
    private SimpleStringProperty sDocumentCharge;
    private SimpleStringProperty sTotalAdvanceAmount;
    
    private SimpleStringProperty sMaterialType;
    
    public AllDetailsBean(String sRepledgeBillId, String sBillNumber, 
            String sCustomerName, String sArea,
            String sItems, double sGrWt, String sRatePerGm, 
            String sOpeningDate, double sAmount, String sInterest, String sClosingDate, 
            String sStatus, String sInterestedAmount, String sTotalInterestedAmount, 
            String sDocumentCharge, String sTotalAdvanceAmount, String sMaterialType, String sMoblieNumber, boolean bChecked,
            StockDetailsDBOperation dbOp) {
        
        
        this.sRepledgeBillId = new SimpleStringProperty(sRepledgeBillId);
        this.sBillNumber = new SimpleStringProperty(sBillNumber);
        this.sCustomerName = new SimpleStringProperty(sCustomerName);
        this.sArea = new SimpleStringProperty(sArea);
        this.sItems = new SimpleStringProperty(sItems);
        this.sGrWt = new SimpleDoubleProperty(sGrWt);
        this.sRatePerGm = new SimpleStringProperty(sRatePerGm);
        this.sOpeningDate = new SimpleStringProperty(sOpeningDate);
        this.sAmount = new SimpleDoubleProperty(sAmount);
        this.sInterest = new SimpleStringProperty(sInterest);
        this.sClosingDate = new SimpleStringProperty(sClosingDate);
        this.sStatus = new SimpleStringProperty(sMaterialType);
        this.sDocumentCharge = new SimpleStringProperty(sDocumentCharge);
        this.sTotalAdvanceAmount = new SimpleStringProperty(sTotalAdvanceAmount);
        this.sInterestedAmount = new SimpleStringProperty(sInterestedAmount);
        this.sTotalInterestedAmount = new SimpleStringProperty(sTotalInterestedAmount); 
        this.sMaterialType = new SimpleStringProperty(sMaterialType);
        this.sMobileNumber = new SimpleStringProperty(sMoblieNumber);
        this.bChecked = new SimpleBooleanProperty(bChecked);
        this.dbOp = dbOp;
    }

    public final void setAllCalcValues() {
        
        double dTakenDaysOrMonths = 0;
        String sTakenMonths = "0";
        String sTakenDays = "0";
        String sStartDate = this.sOpeningDate.get();
        String sEndDate = this.sClosingDate.get();
        long lTotalDays = DateRelatedCalculations.getDifferenceDays(sStartDate, sEndDate);    
        
        //if(this.sRepledgeBillId.get().equals("")) {
            
            try {
                String sInterestType = dbOp.getInterestType();
                String[] sReduceDatas = dbOp.getReduceOrMinimumDaysOrMonths(sMaterialType.get(), "REDUCTION");
                String[] sMinimumDatas = dbOp.getReduceOrMinimumDaysOrMonths(sMaterialType.get(), "MINIMUM");
                String[] sNoticeValues = dbOp.getNoticeValues();
                String[] sFineValues = dbOp.getFineCharges(getSMaterialType());
                
                
            if("MONTH".equals(sInterestType)) {
                //lTotalDays = lTotalDays>0 ? lTotalDays - 1 : 0;
                long[] lActualTotalMonths = DateRelatedCalculations.getDifferenceMonthsChettinad(sStartDate, sEndDate);
                
                if("MONTHS FROM TOTAL MONTH".equals(sReduceDatas[1])) {
                    long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithTotalMonthReduction(lActualTotalMonths, sReduceDatas);
                    double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(this.sClosingDate.get(), Double.valueOf(lTakenMonths[1]), sMaterialType.get()) : 0;
                    double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                    sTakenMonths = Double.toString(dTakenMonths);
                } else if("MONTHS FROM OPENING MONTH".equals(sReduceDatas[1])) {
                    long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithMonthReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                    double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(this.sClosingDate.get(), Double.valueOf(lTakenMonths[1]), sMaterialType.get()) : 0;
                    double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                    sTakenMonths = Double.toString(dTakenMonths);
                } else if("DAYS".equals(sReduceDatas[1])) {
                    long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithDayReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                    double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(this.sClosingDate.get(), Double.valueOf(lTakenMonths[1]), sMaterialType.get()) : 0;
                    double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                    sTakenMonths = Double.toString(dTakenMonths);
                }
                dTakenDaysOrMonths = Double.parseDouble(sTakenMonths);
            } else if("DAY".equals(sInterestType)) {
                                
                if("MONTHS FROM OPENING MONTH".equals(sReduceDatas[1])) {
                    
                    long lTakenDays = DateRelatedCalculations.getDifferenceDaysWithMonthReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                    sTakenDays = Long.toString(lTakenDays);
                    
                    if("MONTHS FROM OPENING MONTH".equals(sMinimumDatas[1])) {                    

                        if(!DateRelatedCalculations.isMonthsGreaterThanMonthMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {                           
                            sTakenDays = Long.toString(DateRelatedCalculations.getDaysInMonths(sStartDate, Integer.parseInt(sMinimumDatas[0])));
                        }
                    } else if("DAYS".equals(sMinimumDatas[1])) {                    

                        if(!DateRelatedCalculations.isMonthsGreaterThanDayMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {                            
                            sTakenDays = sMinimumDatas[0];
                        }
                    }
                    dTakenDaysOrMonths = Double.parseDouble(sTakenDays);
                } else if("DAYS".equals(sReduceDatas[1])) {
                    
                    long lTakenDays = DateRelatedCalculations.getDifferenceDaysWithDayReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                    sTakenDays = Long.toString(lTakenDays);

                    if("MONTHS FROM OPENING MONTH".equals(sMinimumDatas[1])) {                    

                        if(!DateRelatedCalculations.isMonthsGreaterThanMonthMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {                           
                            sTakenDays = Long.toString(DateRelatedCalculations.getDaysInMonths(sStartDate, Integer.parseInt(sMinimumDatas[0])));
                        }
                    } else if("DAYS".equals(sMinimumDatas[1])) {                    

                        if(!DateRelatedCalculations.isMonthsGreaterThanDayMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {                            
                            sTakenDays = sMinimumDatas[0];
                        }
                    }
                    dTakenDaysOrMonths = Double.parseDouble(sTakenDays);
                }
            }
            String sFormula = dbOp.getCompFormula(this.sClosingDate.get(), this.sAmount.get(), sMaterialType.get());
            String sFineFormula = sFormula;
            String[][] replacements = {{"AMOUNT", Double.toString(this.sAmount.get())},
                                        {"INTEREST", this.sInterest.get()},
                                        {"DOCUMENT_CHARGE", this.sDocumentCharge.get()},
                                        {"TAKEN_MONTHS", sTakenMonths},
                                        {"TAKEN_DAYS", sTakenDays}};
            for(String[] replacement: replacements) {
                sFormula = sFormula.replace(replacement[0], replacement[1]);
            }
            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
            String sTakenAmount = String.valueOf(Math.round(Double.parseDouble(engine.eval(sFormula).toString())));
            
            // other charges section
            double dNoticeAmount = 0;
            if(DateRelatedCalculations.isFirstDateIsLesserOrEqualToSecondDate(sStartDate, sNoticeValues[0])) {
                dNoticeAmount = Double.parseDouble(sNoticeValues[1]);
            }                         
            double dStartingFineDaysOrMOnths = Double.parseDouble(sFineValues[1]);
            double dEndingFineDaysOrMOnths = Double.parseDouble(sFineValues[2]);       
            double dFineCharge = 0;
            if(sInterestType.equals(sFineValues[0]) 
                    && Util.isBetween(dTakenDaysOrMonths, dStartingFineDaysOrMOnths, dEndingFineDaysOrMOnths)) {
                String[][] fineReplacements = {{"AMOUNT", Double.toString(getSAmount())},
                                            {"INTEREST", sFineValues[3]},
                                            {"DOCUMENT_CHARGE", "0"},
                                            {"TAKEN_MONTHS", sTakenMonths},
                                            {"TAKEN_DAYS", sTakenDays}};
                for(String[] replacement: fineReplacements) {
                    sFineFormula = sFineFormula.replace(replacement[0], replacement[1]);
                }
                ScriptEngine fineEngine = new ScriptEngineManager().getEngineByExtension("js");
                dFineCharge = Double.valueOf(Math.round(Double.parseDouble(fineEngine.eval(sFineFormula).toString())));            
            }            
            
            double dTotalOtherCharges = dNoticeAmount + dFineCharge;
            
            String sToGet = Double.toString((this.sAmount.get() 
                    + Double.parseDouble(sTakenAmount) + dTotalOtherCharges) 
                    - Double.parseDouble(this.sTotalAdvanceAmount.get()));
            this.sInterestedAmount.set(sTakenAmount);
            this.sTotalInterestedAmount.set(sToGet);
            
            } catch (SQLException ex) {
                Logger.getLogger(AllDetailsBean.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ScriptException ex) {
                Logger.getLogger(AllDetailsBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        //}
            
        //}
    }

    /**
     * @return the sRepledgeBillId
     */
    public String getSCustomerName() {
        return sCustomerName.get();
    }

    /**
     * @param sCustomerName the sRepledgeBillId to set
     */
    public void setSCustomerName(String sCustomerName) {
        this.sCustomerName.set(sCustomerName);
    }

    /**
     * @return the sRepledgeBillId
     */
    public String getSMobileNumber() {
        return sMobileNumber.get();
    }

    /**
     * @param sCustomerName the sRepledgeBillId to set
     */
    public void setSMobileNumber(String sMobileNumber) {
        this.sMobileNumber.set(sMobileNumber);
    }

    /**
     * @return the sRepledgeBillId
     */
    public String getSArea() {
        return sArea.get();
    }

    /**
     * @param sArea the sRepledgeBillId to set
     */
    public void setSArea(String sArea) {
        this.sArea.set(sArea);
    }
    
    /**
     * @return the sRepledgeBillId
     */
    public String getSRepledgeBillId() {
        return sRepledgeBillId.get();
    }

    /**
     * @param sRepledgeBillId the sRepledgeBillId to set
     */
    public void setSRepledgeBillId(String sRepledgeBillId) {
        this.sRepledgeBillId.set(sRepledgeBillId);
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
    public double getSGrWt() {
        return sGrWt.get();
    }

    /**
     * @param sGrWt
     */
    public void setSGrWt(double sGrWt) {
        this.sGrWt.set(sGrWt);
    }

    /**
     * @return the sGrWt
     */
    public String getSRatePerGm() {
        return sRatePerGm.get();
    }

    /**
     * @param sRatePerGm
     */
    public void setSRatePerGm(String sRatePerGm) {
        this.sRatePerGm.set(sRatePerGm);
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
     * @return the sInterest
     */
    public String getSInterest() {
        return sInterest.get();
    }

    /**
     * @param sInterest the sInterest to set
     */
    public void setSInterest(String sInterest) {
        this.sInterest.set(sInterest);
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

    /**
     * @return the sStatus
     */
    public String getSStatus() {
        return sStatus.get();
    }

    /**
     * @param sStatus the sStatus to set
     */
    public void setSStatus(String sStatus) {
        this.sStatus.set(sStatus);
    }

    /**
     * @return the sInterestedAmount
     */
    public String getSInterestedAmount() {
        return sInterestedAmount.get();
    }

    /**
     * @param sInterestedAmount the sInterestedAmount to set
     */
    public void setSInterestedAmount(String sInterestedAmount) {
        this.sInterestedAmount.set(sInterestedAmount);
    }

    /**
     * @return the sTotalInterestedAmount
     */
    public String getSTotalInterestedAmount() {
        return sTotalInterestedAmount.get();
    }

    /**
     * @param sTotalInterestedAmount the sTotalInterestedAmount to set
     */
    public void setSTotalInterestedAmount(String sTotalInterestedAmount) {
        this.sTotalInterestedAmount.set(sTotalInterestedAmount);
    }

    /**
     * @return the sMaterialType
     */
    public String getSMaterialType() {
        return sMaterialType.get();
    }

    /**
     * @param sMaterialType the sMaterialType to set
     */
    public void setSMaterialType(String sMaterialType) {
        this.sMaterialType.set(sMaterialType);
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
