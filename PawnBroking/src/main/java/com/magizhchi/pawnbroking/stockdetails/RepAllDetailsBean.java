/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.stockdetails;

import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
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
public class RepAllDetailsBean {
    
    private StockDetailsDBOperation dbOp;
    
    private SimpleStringProperty sRepledgeBillId;
    private SimpleStringProperty sRepledgeId;
    private SimpleStringProperty sRepledgeName;
    private SimpleStringProperty sCompBillNumber;
    private SimpleDoubleProperty sRepAmount;
    private SimpleStringProperty sRepInterest;
    private SimpleStringProperty sRepDocumentCharge;
    private SimpleStringProperty sRepOpeningDate;    
    private SimpleStringProperty sRepClosingDate;
    private SimpleStringProperty sInterestedAmount;
    private SimpleStringProperty sTotalInterestedAmount;           
    private SimpleBooleanProperty bChecked;
    
    private String sMaterialType;
    
    public RepAllDetailsBean(String sRepledgeBillId, String sRepledgeId, String sRepledgeName, String sCompBillNumber, 
            double sRepAmount, String sRepInterest, String sRepOpeningDate, String sRepClosingDate, 
            String sInterestedAmount, String sTotalInterestedAmount, 
            String sRepDocumentCharge, String sMaterialType, boolean bChecked, 
            StockDetailsDBOperation dbOp) {
        
        
        this.sRepledgeBillId = new SimpleStringProperty(sRepledgeBillId);
        this.sRepledgeId = new SimpleStringProperty(sRepledgeId);
        this.sRepledgeName = new SimpleStringProperty(sRepledgeName);
        this.sCompBillNumber = new SimpleStringProperty(sCompBillNumber);
        this.sRepAmount = new SimpleDoubleProperty(sRepAmount);
        this.sRepInterest = new SimpleStringProperty(sRepInterest);
        this.sRepDocumentCharge = new SimpleStringProperty(sRepDocumentCharge); 
        this.sRepOpeningDate = new SimpleStringProperty(sRepOpeningDate);
        this.sRepClosingDate = new SimpleStringProperty(sRepClosingDate);
        this.sInterestedAmount = new SimpleStringProperty(sInterestedAmount);
        this.sTotalInterestedAmount = new SimpleStringProperty(sTotalInterestedAmount);                 
        this.bChecked = new SimpleBooleanProperty(bChecked);
        
        this.sMaterialType = sMaterialType;
        this.dbOp = dbOp;
        
        setAllCalcValues();
    }

    public final void setAllCalcValues() {
        
        String sTakenMonths = "0";
        String sTakenDays = "0";
        String sStartDate = this.sRepOpeningDate.get();
        String sEndDate = this.sRepClosingDate.get();
        long lTotalDays = DateRelatedCalculations.getDifferenceDays(sStartDate, sEndDate);    
        
        if(!this.sRepledgeBillId.get().equals("") && !this.sRepledgeId.get().equals("")) {
            
            try {
                String sInterestType = dbOp.getRepInterestType(this.sRepledgeId.get());
                String[] sReduceDatas = dbOp.getRepReduceOrMinimumDaysOrMonths(this.sRepledgeId.get(), 
                        sMaterialType, "REDUCTION");
                String[] sMinimumDatas = dbOp.getRepReduceOrMinimumDaysOrMonths(this.sRepledgeId.get(), 
                        sMaterialType, "MINIMUM");
                
            if("MONTH".equals(sInterestType)) {
                
                //lTotalDays = lTotalDays>0 ? lTotalDays - 1 : 0;
                long[] lActualTotalMonths = DateRelatedCalculations.getDifferenceMonthsChettinad(sStartDate, sEndDate);
                
                if("MONTHS FROM TOTAL MONTH".equals(sReduceDatas[1])) {
                    long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithTotalMonthReduction(lActualTotalMonths, sReduceDatas);
                    double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? 
                            dbOp.getRepRemainingDaysAsMonths(this.sRepledgeId.get(), 
                                    this.sRepClosingDate.get(), Double.valueOf(lTakenMonths[1]), sMaterialType) : 0;
                    double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                    sTakenMonths = Double.toString(dTakenMonths);
                } else if("MONTHS FROM OPENING MONTH".equals(sReduceDatas[1])) {
                    long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithMonthReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                    double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? 
                            dbOp.getRepRemainingDaysAsMonths(this.sRepledgeId.get(), 
                                    this.sRepClosingDate.get(), Double.valueOf(lTakenMonths[1]), sMaterialType) : 0;
                    double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                    sTakenMonths = Double.toString(dTakenMonths);
                } else if("DAYS".equals(sReduceDatas[1])) {
                    long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithDayReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                    double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? 
                            dbOp.getRepRemainingDaysAsMonths(this.sRepledgeId.get(), this.sRepClosingDate.get(), 
                                    Double.valueOf(lTakenMonths[1]), sMaterialType) : 0;
                    double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                    sTakenMonths = Double.toString(dTakenMonths);
                }
                
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
                }
            }
            String sFormula = dbOp.getRepFormula(this.sRepledgeId.get(), this.sRepAmount.get(), sMaterialType);
            String[][] replacements = {{"AMOUNT", Double.toString(this.sRepAmount.get())},
                                        {"INTEREST", this.sRepInterest.get()},
                                        {"DOCUMENT_CHARGE", this.sRepDocumentCharge.get()},
                                        {"TAKEN_MONTHS", sTakenMonths},
                                        {"TAKEN_DAYS", sTakenDays}};
            for(String[] replacement: replacements) {
                sFormula = sFormula.replace(replacement[0], replacement[1]);
            }
            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
            String sTakenAmount = String.valueOf(Math.round(Double.parseDouble(engine.eval(sFormula).toString())));
            String sToGet = Double.toString(this.sRepAmount.get() + Double.parseDouble(sTakenAmount));
            this.sInterestedAmount.set(sTakenAmount);
            this.sTotalInterestedAmount.set(sToGet);
            
            } catch (SQLException ex) {
                Logger.getLogger(RepAllDetailsBean.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ScriptException ex) {
                Logger.getLogger(RepAllDetailsBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
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
     * @return the sRepledgeId
     */
    public String getSRepledgeId() {
        return sRepledgeId.get();
    }

    /**
     * @param sRepledgeId the sBillNumber to set
     */
    public void setSRepledgeId(String sRepledgeId) {
        this.sRepledgeId.set(sRepledgeId);
    }

    /**
     * @return the sRepledgeName
     */
    public String getSRepledgeName() {
        return sRepledgeName.get();
    }

    /**
     * @param sRepledgeName the sRepledgeName to set
     */
    public void setSRepledgeName(String sRepledgeName) {
        this.sRepledgeName.set(sRepledgeName);
    }

    /**
     * @return the sCompBillNumber
     */
    public String getSCompBillNumber() {
        return sCompBillNumber.get();
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
    
    /**
     * @param sCompBillNumber the sCompBillNumber to set
     */
    public void setSCompBillNumber(String sCompBillNumber) {
        this.sCompBillNumber.set(sCompBillNumber);
    }

    /**
     * @return the sRepAmount
     */
    public double getSRepAmount() {
        return sRepAmount.get();
    }

    /**
     * @param sAmount the sAmount to set
     */
    public void setSRepAmount(double sAmount) {
        this.sRepAmount.set(sAmount);
    }

    /**
     * @return the sRepInterest
     */
    public String getSRepInterest() {
        return sRepInterest.get();
    }

    /**
     * @param sRepInterest the sRepInterest to set
     */
    public void setSRepInterest(String sRepInterest) {
        this.sRepInterest.set(sRepInterest);
    }

    /**
     * @return the sRepDocumentCharge
     */
    public String getSRepDocumentCharge() {
        return sRepDocumentCharge.get();
    }

    /**
     * @param sRepDocumentCharge the sRepDocumentCharge to set
     */
    public void setSRepDocumentCharge(String sRepDocumentCharge) {
        this.sRepDocumentCharge.set(sRepDocumentCharge);
    }
    
    /**
     * @return the sRepOpeningDate
     */
    public String getSRepOpeningDate() {
        return sRepOpeningDate.get();
    }

    /**
     * @param sRepOpeningDate the sRepOpeningDate to set
     */
    public void setSRepOpeningDate(String sRepOpeningDate) {
        this.sRepOpeningDate.set(sRepOpeningDate);
    }

    /**
     * @return the sRepClosingDate
     */
    public String getSRepClosingDate() {
        return sRepClosingDate.get();
    }

    /**
     * @param sRepClosingDate the sRepClosingDate to set
     */
    public void setSRepClosingDate(String sRepClosingDate) {
        this.sRepClosingDate.set(sRepClosingDate);
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

}
