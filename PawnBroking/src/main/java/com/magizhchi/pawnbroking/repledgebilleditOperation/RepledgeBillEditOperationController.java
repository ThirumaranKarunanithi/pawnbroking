/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.repledgebilleditOperation;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.repledgebillclosing.RepledgeBillClosingDBOperation;
import com.magizhchi.pawnbroking.repledgebillclosing.RepledgeGoldBillClosingController;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class RepledgeBillEditOperationController implements Initializable {

    private RepledgeBillClosingDBOperation dbOp;
    private String sLastSelectedRepledgeBillId = null;
    private String sLastSelectedRepledgeName = null;
    private String sLastSelectedCompanyBillNumber = null;
    private String sLastSelectedDate = null;
    private String sLastSelectedAmount = null;
    
    private String sReduceType = "";
    private String sMinimumType = "";
    public DataTable dtRepledgeNames;
    private final ArrayList<String> alFilterDBColumnName = new ArrayList<>();
    private final ArrayList<String> alFilterName = new ArrayList<>();
    private final ArrayList<String> alFilterValue = new ArrayList<>();
    private DataTable otherSettingValues = null;    

    @FXML
    private TabPane tpScreen;
    @FXML
    private Tab tabMainScreen;
    @FXML
    private Label txtSpouseType;
    @FXML
    private TextField txtCustomerName;
    @FXML
    private ToggleGroup rgGenderGroup;
    @FXML
    private TextField txtSpouseName;
    @FXML
    private TextField txtToGiveAmount;
    @FXML
    private TextField txtGivenAmount;
    @FXML
    private TextField txtRepledgePreStatus;
    @FXML
    private ComboBox<String> cbStatus;
    @FXML
    private TextArea txtRepledgeNote;
    @FXML
    private Label lbScreenMessage;
    @FXML
    private HBox hSaveModeButtons;
    @FXML
    private ToggleButton tgOn;
    @FXML
    private ToggleGroup ViewModeGroup;
    @FXML
    private ToggleButton tgOff;
    @FXML
    private Button btDelete;
    @FXML
    private Button btUnCloseBill;
    @FXML
    private Button btClearAll;
    @FXML
    private TextField txtRepledgeBillId;
    @FXML
    private TextField txtRepledgeId;
    @FXML
    private ComboBox<String> cbRepledgeName;
    @FXML
    private TextField txtRepledgeBillNumber;
    @FXML
    private DatePicker dpRepledgeClosingDate;
    @FXML
    private TextField txtRepledgeOpenedDate;
    @FXML
    private TextField txtCompanyBillNumber;
    @FXML
    private TextField txtCompanyBillStatus;
    @FXML
    private TextField txtCompanyOpenedDate;
    @FXML
    private TextField txtCompanyBillAmount;
    @FXML
    private TextArea txtNote;
    @FXML
    private TextField txtMobileNumber;
    @FXML
    private TextField txtItems;
    @FXML
    private TextField txtGrossWeight;
    @FXML
    private TextField txtNetWeight;
    @FXML
    private TextField txtPurity;
    @FXML
    private TextField txtInterestType;
    @FXML
    private TextField txtActualTotalDaysOrMonths;
    @FXML
    private TextField txtMinimumDaysOrMonths;
    @FXML
    private TextField txtToReduceDaysOrMonths;
    @FXML
    private TextField txtAmount;
    @FXML
    private TextField txtInterest;
    @FXML
    private TextField txtTakenDaysOrMonths;
    @FXML
    private TextField txtTakenAmount;
    @FXML
    private Label lbActualTotalDaysOrMonths;
    @FXML
    private Label lbMinimumDaysOrMonths;
    @FXML
    private Label lbToReduceDaysOrMonths;
    @FXML
    private Label lbTakenDaysOrMonths;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            dbOp = new RepledgeBillClosingDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            otherSettingValues = dbOp.getOtherSettingsValues("GOLD");
            if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(0).toString())) {
                dpRepledgeClosingDate.setMouseTransparent(false);
                dpRepledgeClosingDate.setFocusTraversable(true);
            } else {
                dpRepledgeClosingDate.setMouseTransparent(true);
                dpRepledgeClosingDate.setFocusTraversable(false);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }

        dpRepledgeClosingDate.setValue(LocalDate.now()); 
        txtRepledgeBillId.setText(CommonConstants.REP_BILL_ID_PREFIX);
        setRepledgeNames();
        
    }    

    public void setRepledgeNames() {
        
        try {
            cbRepledgeName.getItems().removeAll(cbRepledgeName.getItems());
            dtRepledgeNames = dbOp.getAllRepledgeNames();
            for(int i=0; i<dtRepledgeNames.getRowCount(); i++) {          
                cbRepledgeName.getItems().add(dtRepledgeNames.getRow(i).getColumn(1).toString());
            }
            if(dtRepledgeNames.getRowCount() > 0) {
                txtRepledgeId.setText(dtRepledgeNames.getRow(0).getColumn(0).toString());
                cbRepledgeName.getSelectionModel().select(0);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }                
        
    }
    
    @FXML
    private void txtRepledgeBillIdOnAction(ActionEvent event) {
        
        String sRepledgeBillId = txtRepledgeBillId.getText();
        btClearAllClicked(null);
        
        if(tgOn.isSelected()) {
            try {
                HashMap<String, String> headerValues = dbOp.getAllHeaderValuesByRepledgeBillId(sRepledgeBillId, "GOLD");
                if(headerValues != null) {
                    String sRepledgeId = headerValues.get("REPLEDGE_ID");
                    String sInterestType = dbOp.getInterestType(sRepledgeId);
                    String[] sReduceDatas = dbOp.getReduceOrMinimumDaysOrMonths(sRepledgeId, "GOLD", "REDUCTION");
                    String[] sMinimumDatas = dbOp.getReduceOrMinimumDaysOrMonths(sRepledgeId, "GOLD", "MINIMUM");
                    
                    setAllCompanyValuesToFields(headerValues);
                    setAllRepledgeValuesToFields(sRepledgeId, headerValues, sInterestType, sReduceDatas, sMinimumDatas);
                    sLastSelectedRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID");  
                    sLastSelectedRepledgeName = headerValues.get("REPLEDGE_NAME");  
                    sLastSelectedCompanyBillNumber = headerValues.get("BILL_NUMBER");  
                    sLastSelectedDate = headerValues.get("REPLEDGE_CLOSING_DATE");  
                    sReduceType = sReduceDatas[1];
                    sMinimumType = sMinimumDatas[1];
                    cbRepledgeName.setMouseTransparent(true);
                    cbRepledgeName.setFocusTraversable(false);                      
                } else {
                    btClearAllClicked(null);
                    PopupUtil.showErrorAlert("Sorry invalid repledge bill id.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  else if(tgOff.isSelected()) {
            try {
                HashMap<String, String> headerValues = dbOp.getAllClosedHeaderValuesByRepledgeBillId(sRepledgeBillId, "GOLD");
                if(headerValues != null) {
                    String sRepledgeId = headerValues.get("REPLEDGE_ID");
                    String sInterestType = dbOp.getInterestType(sRepledgeId);
                    String[] sReduceDatas = dbOp.getReduceOrMinimumDaysOrMonths(sRepledgeId, "GOLD", "REDUCTION");
                    String[] sMinimumDatas = dbOp.getReduceOrMinimumDaysOrMonths(sRepledgeId, "GOLD", "MINIMUM");
                    
                    setAllCompanyValuesToFields(headerValues);
                    setAllRepledgeValuesToFields(sRepledgeId, headerValues, sInterestType, sReduceDatas, sMinimumDatas);
                    dpRepledgeClosingDate.setValue(LocalDate.parse(headerValues.get("REPLEDGE_CLOSING_DATE"), CommonConstants.DATETIMEFORMATTER));
                    sLastSelectedRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID");  
                    sLastSelectedRepledgeName = headerValues.get("REPLEDGE_NAME");  
                    sLastSelectedCompanyBillNumber = headerValues.get("BILL_NUMBER");  
                    sLastSelectedDate = headerValues.get("REPLEDGE_CLOSING_DATE");  
                    sLastSelectedAmount = headerValues.get("GIVEN_AMOUNT");
                    
                    sReduceType = sReduceDatas[1];
                    sMinimumType = sMinimumDatas[1];
                    cbRepledgeName.setMouseTransparent(true);
                    cbRepledgeName.setFocusTraversable(false);                      
                } else {
                    btClearAllClicked(null);
                    PopupUtil.showErrorAlert("Sorry invalid repledge bill id.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }

    public void setAllCompanyValuesToFields(HashMap<String, String> companyValues) {
    
        txtCompanyBillNumber.setText(companyValues.get("BILL_NUMBER")); 
        txtCompanyBillStatus.setText(companyValues.get("STATUS"));        
        txtCompanyOpenedDate.setText(companyValues.get("OPENING_DATE"));
        txtCompanyBillAmount.setText(companyValues.get("AMOUNT"));
        txtCustomerName.setText(companyValues.get("CUSTOMER_NAME"));
        String sGender = companyValues.get("GENDER");
        if(null != sGender) switch (sGender) {
            case "MALE":
                rgGenderGroup.getToggles().get(0).setSelected(true);
                break;
            case "FEMALE":
                rgGenderGroup.getToggles().get(1).setSelected(true);
                break;
            case "OTHER":
                rgGenderGroup.getToggles().get(2).setSelected(true);
                break;
            default:
                break;
        }
        txtSpouseType.setText(companyValues.get("SPOUSE_TYPE"));
        txtSpouseName.setText(companyValues.get("SPOUSE_NAME"));      
        txtMobileNumber.setText(companyValues.get("MOBILE_NUMBER"));
        txtItems.setText(companyValues.get("ITEMS"));
        txtGrossWeight.setText(companyValues.get("GROSS_WEIGHT"));
        txtNetWeight.setText(companyValues.get("NET_WEIGHT"));
        txtPurity.setText(companyValues.get("PURITY"));       
        txtNote.setText(companyValues.get("NOTE"));
    }
    
    public void setAllRepledgeValuesToFields(String sRepledgeId, HashMap<String, String> repledgeValues, String sInterestType, String[] sReduceDatas, String[] sMinimumDatas) {
        
        try {
            txtRepledgeBillId.setText(repledgeValues.get("REPLEDGE_BILL_ID"));
            txtRepledgeId.setText(repledgeValues.get("REPLEDGE_ID"));
            cbRepledgeName.setValue(repledgeValues.get("REPLEDGE_NAME"));
            txtRepledgeBillNumber.setText(repledgeValues.get("REPLEDGE_BILL_NUMBER"));
            txtRepledgeOpenedDate.setText(repledgeValues.get("REPLEDGE_OPENING_DATE"));
            txtAmount.setText(repledgeValues.get("REPLEDGE_AMOUNT"));
            txtInterest.setText(repledgeValues.get("REPLEDGE_INTEREST"));
            txtRepledgeNote.setText(repledgeValues.get("REPLEDGE_NOTE"));
            
            txtInterestType.setText(sInterestType);
            
            String sTakenMonths = "0";
            String sTakenDays = "0";
            String sStartDate = txtRepledgeOpenedDate.getText();
            String sEndDate = CommonConstants.DATETIMEFORMATTER.format(dpRepledgeClosingDate.getValue());
            long lTotalDays = DateRelatedCalculations.getDifferenceDays(sStartDate, sEndDate);
            
            if("MONTH".equals(sInterestType)) {                
                
                //lTotalDays = lTotalDays>0 ? lTotalDays - 1 : 0;
                long[] lActualTotalMonths = DateRelatedCalculations.getDifferenceMonthsChettinad(sStartDate, sEndDate);
                lbActualTotalDaysOrMonths.setText("Actual Total Months:");
                txtActualTotalDaysOrMonths.setText(Long.toString(lActualTotalMonths[0]) + " Months and " + Long.toString(lActualTotalMonths[1]) + " Days.");
                lbTakenDaysOrMonths.setText("Taken Months:");
                
                if("MONTHS FROM TOTAL MONTH".equals(sReduceDatas[1])) {
                    try {
                        lbToReduceDaysOrMonths.setText("To Reduce Months:");
                        txtToReduceDaysOrMonths.setText(sReduceDatas[0]);
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithTotalMonthReduction(lActualTotalMonths, sReduceDatas);
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(CommonConstants.DATETIMEFORMATTER.format(dpRepledgeClosingDate.getValue()), sRepledgeId, Double.valueOf(lTakenMonths[1]), "GOLD") : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        sTakenMonths = Double.toString(dTakenMonths);
                        txtTakenDaysOrMonths.setText(sTakenMonths);                        
                    } catch (SQLException ex) {
                        Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }                    
                } else if("MONTHS FROM OPENING MONTH".equals(sReduceDatas[1])) {
                    try {
                        lbToReduceDaysOrMonths.setText("To Reduce Months:");
                        txtToReduceDaysOrMonths.setText(sReduceDatas[0]);
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithMonthReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(CommonConstants.DATETIMEFORMATTER.format(dpRepledgeClosingDate.getValue()), sRepledgeId, Double.valueOf(lTakenMonths[1]), "GOLD") : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        sTakenMonths = Double.toString(dTakenMonths);
                        txtTakenDaysOrMonths.setText(sTakenMonths);                        
                    } catch (SQLException ex) {
                        Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if("DAYS".equals(sReduceDatas[1])) {
                    try {
                        lbToReduceDaysOrMonths.setText("To Reduce Days:");
                        txtToReduceDaysOrMonths.setText(sReduceDatas[0]);
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithDayReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(CommonConstants.DATETIMEFORMATTER.format(dpRepledgeClosingDate.getValue()), sRepledgeId, Double.valueOf(lTakenMonths[1]), "GOLD") : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        sTakenMonths = Double.toString(dTakenMonths);
                        txtTakenDaysOrMonths.setText(sTakenMonths);                        
                    } catch (SQLException ex) {
                        Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if("MONTHS FROM OPENING MONTH".equals(sMinimumDatas[1])) {                    
                    lbMinimumDaysOrMonths.setText("Minimum Months:");
                    txtMinimumDaysOrMonths.setText(sMinimumDatas[0]);
                       
                } else if("DAYS".equals(sMinimumDatas[1])) {                    
                    lbMinimumDaysOrMonths.setText("Minimum Days:");
                    txtMinimumDaysOrMonths.setText(sMinimumDatas[0]);
                }

            } else if("DAY".equals(sInterestType)) {
                
                lbActualTotalDaysOrMonths.setText("Actual Total Days:");
                txtActualTotalDaysOrMonths.setText(Long.toString(lTotalDays));
                lbTakenDaysOrMonths.setText("Taken Days:");
                if("MONTHS FROM OPENING MONTH".equals(sReduceDatas[1])) {
                    
                    lbToReduceDaysOrMonths.setText("To Reduce Months:");
                    txtToReduceDaysOrMonths.setText(sReduceDatas[0]);
                    long lTakenDays = DateRelatedCalculations.getDifferenceDaysWithMonthReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                    sTakenDays = Long.toString(lTakenDays);
                    
                    if("MONTHS FROM OPENING MONTH".equals(sMinimumDatas[1])) {
                        lbMinimumDaysOrMonths.setText("Minimum Months:");
                        txtMinimumDaysOrMonths.setText(sMinimumDatas[0]);
                        
                        if(!DateRelatedCalculations.isMonthsGreaterThanMonthMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {
                            sTakenDays = Long.toString(DateRelatedCalculations.getDaysInMonths(sStartDate, Integer.parseInt(sMinimumDatas[0])));
                        }
                    } else if("DAYS".equals(sMinimumDatas[1])) {
                        lbMinimumDaysOrMonths.setText("Minimum Days:");
                        txtMinimumDaysOrMonths.setText(sMinimumDatas[0]);
                        
                        if(!DateRelatedCalculations.isMonthsGreaterThanDayMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {
                            sTakenDays = sMinimumDatas[0];
                        }
                    }
                    
                    txtTakenDaysOrMonths.setText(sTakenDays);
                    
                } else if("DAYS".equals(sReduceDatas[1])) {
                    
                    lbToReduceDaysOrMonths.setText("To Reduce Days:");
                    txtToReduceDaysOrMonths.setText(sReduceDatas[0]);
                    long lTakenDays = DateRelatedCalculations.getDifferenceDaysWithDayReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                    sTakenDays = Long.toString(lTakenDays);
                    
                    if("MONTHS FROM OPENING MONTH".equals(sMinimumDatas[1])) {
                        lbMinimumDaysOrMonths.setText("Minimum Months:");
                        txtMinimumDaysOrMonths.setText(sMinimumDatas[0]);
                        
                        if(!DateRelatedCalculations.isMonthsGreaterThanMonthMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {
                            sTakenDays = Long.toString(DateRelatedCalculations.getDaysInMonths(sStartDate, Integer.parseInt(sMinimumDatas[0])));
                        }
                    } else if("DAYS".equals(sMinimumDatas[1])) {
                        lbMinimumDaysOrMonths.setText("Minimum Days:");
                        txtMinimumDaysOrMonths.setText(sMinimumDatas[0]);
                        
                        if(!DateRelatedCalculations.isMonthsGreaterThanDayMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {
                            sTakenDays = sMinimumDatas[0];
                        }
                    }
                    
                    txtTakenDaysOrMonths.setText(sTakenDays);
                }
            }
            
            String sFormula = dbOp.getFormula(sRepledgeId, Double.parseDouble(repledgeValues.get("REPLEDGE_AMOUNT")), "GOLD");
            String[][] replacements = {{"AMOUNT", repledgeValues.get("REPLEDGE_AMOUNT")},
                {"INTEREST", repledgeValues.get("REPLEDGE_INTEREST")},
                {"DOCUMENT_CHARGE", repledgeValues.get("REPLEDGE_DOCUMENT_CHARGE")},
                {"TAKEN_MONTHS", sTakenMonths},
                {"TAKEN_DAYS", sTakenDays}};
            for(String[] replacement: replacements) {
                sFormula = sFormula.replace(replacement[0], replacement[1]);
            }
            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
            String sTakenAmount = String.valueOf(Math.round(Double.parseDouble(engine.eval(sFormula).toString())));
            String sToGive = Double.toString((Double.parseDouble(repledgeValues.get("REPLEDGE_AMOUNT")) + Double.parseDouble(sTakenAmount)));
            txtTakenAmount.setText(sTakenAmount);
            txtToGiveAmount.setText(sToGive);
            if(tgOn.isSelected()) {
                txtGivenAmount.setText(sToGive);
            } else {
                txtGivenAmount.setText(repledgeValues.get("GIVEN_AMOUNT"));
            }
            txtGivenAmount.requestFocus();
            txtGivenAmount.positionCaret(txtGivenAmount.getText().length());
            txtRepledgePreStatus.setText(repledgeValues.get("REPLEDGE_STATUS"));
            if(tgOn.isSelected()) {
                cbStatus.setValue("CLOSED");
            } else {
                cbStatus.setValue(repledgeValues.get("REPLEDGE_STATUS"));
            }
            
        } catch (SQLException | ScriptException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
        private void capitalizeCharOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();
        int caretPos = txt_TextField.getCaretPosition();
        txt_TextField.setText(txt_TextField.getText().toUpperCase());
        txt_TextField.positionCaret(caretPos);
    }

    @FXML
    private void cbRepledgeNameOnAction(ActionEvent event) {
        int index = cbRepledgeName.getSelectionModel().getSelectedIndex();
        String sRepledgeId = dtRepledgeNames.getRow(index).getColumn(0).toString();                
        txtRepledgeId.setText(sRepledgeId);                
    }

    @FXML
    private void txtRepledgeBillNumberOnAction(ActionEvent event) {
        
        String sRepledgeId = txtRepledgeId.getText();
        String sRepledgeBillNumber = txtRepledgeBillNumber.getText();
        btClearAllClicked(null);        

        if(tgOn.isSelected()) {
            try {
                HashMap<String, String> headerValues = dbOp.getAllHeaderValuesByRepledgeBillNumber(sRepledgeId, sRepledgeBillNumber, "GOLD");
                if(headerValues != null) {
                    String sInterestType = dbOp.getInterestType(sRepledgeId);
                    String[] sReduceDatas = dbOp.getReduceOrMinimumDaysOrMonths(sRepledgeId, "GOLD", "REDUCTION");
                    String[] sMinimumDatas = dbOp.getReduceOrMinimumDaysOrMonths(sRepledgeId, "GOLD", "MINIMUM");
                    setAllCompanyValuesToFields(headerValues);
                    setAllRepledgeValuesToFields(sRepledgeId, headerValues, sInterestType, sReduceDatas, sMinimumDatas);
                    sLastSelectedRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID"); 
                    sLastSelectedRepledgeName = headerValues.get("REPLEDGE_NAME");  
                    sLastSelectedCompanyBillNumber = headerValues.get("BILL_NUMBER");  
                    sLastSelectedDate = headerValues.get("REPLEDGE_CLOSING_DATE");  
                    
                    sReduceType = sReduceDatas[1];
                    sMinimumType = sMinimumDatas[1];
                    cbRepledgeName.setMouseTransparent(true);
                    cbRepledgeName.setFocusTraversable(false);                      
                } else {
                    btClearAllClicked(null);
                    PopupUtil.showErrorAlert("Sorry invalid repledge bill number in "+ cbRepledgeName.getValue() +".");
                }
            } catch (SQLException ex) {
                Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }        
        }  else if(tgOff.isSelected()) {
            try {
                HashMap<String, String> headerValues = dbOp.getAllClosedHeaderValuesByRepledgeBillNumber(sRepledgeId, sRepledgeBillNumber, "GOLD");
                if(headerValues != null) {
                    String sInterestType = dbOp.getInterestType(sRepledgeId);
                    String[] sReduceDatas = dbOp.getReduceOrMinimumDaysOrMonths(sRepledgeId, "GOLD", "REDUCTION");
                    String[] sMinimumDatas = dbOp.getReduceOrMinimumDaysOrMonths(sRepledgeId, "GOLD", "MINIMUM");
                    setAllCompanyValuesToFields(headerValues);
                    setAllRepledgeValuesToFields(sRepledgeId, headerValues, sInterestType, sReduceDatas, sMinimumDatas);
                    dpRepledgeClosingDate.setValue(LocalDate.parse(headerValues.get("REPLEDGE_CLOSING_DATE"), CommonConstants.DATETIMEFORMATTER));
                    sLastSelectedRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID"); 
                    sLastSelectedRepledgeName = headerValues.get("REPLEDGE_NAME");  
                    sLastSelectedCompanyBillNumber = headerValues.get("BILL_NUMBER"); 
                    sLastSelectedDate = headerValues.get("REPLEDGE_CLOSING_DATE");  
                    sLastSelectedAmount = headerValues.get("GIVEN_AMOUNT");
                    
                    sReduceType = sReduceDatas[1];
                    sMinimumType = sMinimumDatas[1];
                    cbRepledgeName.setMouseTransparent(true);
                    cbRepledgeName.setFocusTraversable(false);                      
                } else {
                    btClearAllClicked(null);
                    PopupUtil.showErrorAlert("Sorry invalid repledge bill number in "+ cbRepledgeName.getValue() +".");
                }
            } catch (SQLException ex) {
                Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }        
        }        
    }

    public void clearAllBillDetails()
    {
        txtCompanyBillNumber.setText("");
        txtCompanyBillStatus.setText("");
        txtCompanyOpenedDate.setText("");
        txtCompanyBillAmount.setText("");
        txtCustomerName.setText("");
        txtSpouseType.setText("Spouse Type:");
        txtSpouseName.setText("");
        txtMobileNumber.setText("");
        txtItems.setText("");
        txtGrossWeight.setText("");
        txtNetWeight.setText("");
        txtPurity.setText("");
        txtNote.setText("");        
        txtAmount.setText("");
        txtInterest.setText("");
        txtTakenAmount.setText("");
        txtToGiveAmount.setText("");
        txtGivenAmount.setText("");   
        txtInterestType.setText("");   
        txtActualTotalDaysOrMonths.setText("");   
        txtMinimumDaysOrMonths.setText("");   
        txtToReduceDaysOrMonths.setText("");   
        txtTakenDaysOrMonths.setText("");
        txtRepledgeOpenedDate.setText("");
        txtRepledgePreStatus.setText("");
    }    
    
    @FXML
    private void dpRepledgeClosingDateTextChanged(ActionEvent event) {
        
        Platform.runLater(()->{
            if(sLastSelectedRepledgeBillId != null) {            
                try {
                    String sRepledgeId = txtRepledgeId.getText();
                    HashMap<String, String> headerValues = tgOn.isSelected() ? dbOp.getAllHeaderValuesByCompanyBillNumber(sLastSelectedCompanyBillNumber, "GOLD") : dbOp.getAllClosedHeaderValuesByCompanyBillNumber(sLastSelectedCompanyBillNumber, "GOLD");
                    String sInterestType = dbOp.getInterestType(sRepledgeId);
                    String[] sReduceDatas = dbOp.getReduceOrMinimumDaysOrMonths(sRepledgeId, "GOLD", "REDUCTION");
                    String[] sMinimumDatas = dbOp.getReduceOrMinimumDaysOrMonths(sRepledgeId, "GOLD", "MINIMUM");                

                    if(headerValues != null)
                    {
                        setAllRepledgeValuesToFields(sRepledgeId, headerValues, sInterestType, sReduceDatas, sMinimumDatas);
                    } 
                } catch (SQLException ex) {
                    Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                }            
            }      
        });        
    }

    @FXML
    private void txtCompanyBillNumberOnAction(ActionEvent event) {
        
        String sCompanyBillNumber = txtCompanyBillNumber.getText();        
        btClearAllClicked(null);
        
        if(tgOn.isSelected()) {
            try {
                HashMap<String, String> headerValues = dbOp.getAllHeaderValuesByCompanyBillNumber(sCompanyBillNumber, "GOLD");
                if(headerValues != null) {
                    String sRepledgeId = headerValues.get("REPLEDGE_ID");                       
                    String sInterestType = dbOp.getInterestType(sRepledgeId);
                    String[] sReduceDatas = dbOp.getReduceOrMinimumDaysOrMonths(sRepledgeId, "GOLD", "REDUCTION");
                    String[] sMinimumDatas = dbOp.getReduceOrMinimumDaysOrMonths(sRepledgeId, "GOLD", "MINIMUM");

                    setAllCompanyValuesToFields(headerValues);
                    setAllRepledgeValuesToFields(sRepledgeId, headerValues, sInterestType, sReduceDatas, sMinimumDatas);
                    sLastSelectedRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID");  
                    sLastSelectedRepledgeName = headerValues.get("REPLEDGE_NAME");  
                    sLastSelectedCompanyBillNumber = headerValues.get("BILL_NUMBER");  
                    sLastSelectedDate = headerValues.get("REPLEDGE_CLOSING_DATE");  
                    
                    sReduceType = sReduceDatas[1];
                    sMinimumType = sMinimumDatas[1];                    
                    cbRepledgeName.setMouseTransparent(true);
                    cbRepledgeName.setFocusTraversable(false);                      
                } else {
                    btClearAllClicked(null);
                    PopupUtil.showErrorAlert("Sorry invalid company bill number.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if(tgOff.isSelected()) {
            try {
                HashMap<String, String> headerValues = dbOp.getAllClosedHeaderValuesByCompanyBillNumber(sCompanyBillNumber, "GOLD");
                if(headerValues == null) {
                    headerValues = dbOp.getAllClosedHeaderValuesByCompanyReBillNumber(sCompanyBillNumber, "GOLD");
                }
                if(headerValues != null) {
                    String sRepledgeId = headerValues.get("REPLEDGE_ID");                       
                    String sInterestType = dbOp.getInterestType(sRepledgeId);
                    String[] sReduceDatas = dbOp.getReduceOrMinimumDaysOrMonths(sRepledgeId, "GOLD", "REDUCTION");
                    String[] sMinimumDatas = dbOp.getReduceOrMinimumDaysOrMonths(sRepledgeId, "GOLD", "MINIMUM");

                    setAllCompanyValuesToFields(headerValues);
                    setAllRepledgeValuesToFields(sRepledgeId, headerValues, sInterestType, sReduceDatas, sMinimumDatas);
                    dpRepledgeClosingDate.setValue(LocalDate.parse(headerValues.get("REPLEDGE_CLOSING_DATE"), CommonConstants.DATETIMEFORMATTER));
                    sLastSelectedRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID");  
                    sLastSelectedRepledgeName = headerValues.get("REPLEDGE_NAME");  
                    sLastSelectedCompanyBillNumber = headerValues.get("BILL_NUMBER");  
                    sLastSelectedDate = headerValues.get("REPLEDGE_CLOSING_DATE");  
                    sLastSelectedAmount = headerValues.get("GIVEN_AMOUNT");
                    
                    sReduceType = sReduceDatas[1];
                    sMinimumType = sMinimumDatas[1];                    
                    cbRepledgeName.setMouseTransparent(true);
                    cbRepledgeName.setFocusTraversable(false);                      
                } else {
                    btClearAllClicked(null);
                    PopupUtil.showErrorAlert("Sorry invalid company bill number.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }

    @FXML
    private void rbToggleChanged(MouseEvent event) {
    }

    @FXML
    private void allowNumberOnlyOnType(KeyEvent e) {
        if(!e.getCharacter().matches("[0-9]")){ 
            e.consume();
        }        
    }

    @FXML
    private void allowNumberAndDotOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();                      
        if(!("0123456789.".contains(e.getCharacter()))){ 
            e.consume();
        }        
    }

    @FXML
    private void txtAmountOnPress(KeyEvent event) {
    }

    @FXML
    private void txtAmountOnType(KeyEvent event) {
    }

    @FXML
    private void saveModeON(ActionEvent event) {
        btClearAllClicked(null);
        btClearAllClicked(null);
        btClearAll.setDisable(true);
        btUnCloseBill.setDisable(true);
        txtGivenAmount.setEditable(true);
        txtGivenAmount.setMouseTransparent(false);
        txtGivenAmount.setFocusTraversable(true);         
        cbStatus.getSelectionModel().select(0);        
    }

    @FXML
    private void saveModeOFF(ActionEvent event) {

        btClearAllClicked(null);
        btClearAll.setDisable(true);
        btUnCloseBill.setDisable(false);
        if(otherSettingValues != null) {
            if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(1).toString())) {
                txtGivenAmount.setEditable(true);
                txtGivenAmount.setMouseTransparent(false);
                txtGivenAmount.setFocusTraversable(true); 
            } else {
                txtGivenAmount.setEditable(false);
                txtGivenAmount.setMouseTransparent(true);
                txtGivenAmount.setFocusTraversable(false); 
            }
        }        
    }

    @FXML
    private void btSaveBillClicked(ActionEvent event) {
        
        if(sLastSelectedRepledgeBillId != null) {
            
            String sBillOpeningDate = txtRepledgeOpenedDate.getText();
            
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sBillOpeningDate))
            {

                try {
                    
                    String sBillNumber = txtCompanyBillNumber.getText().toUpperCase();
                    String sMaterialType = "GOLD";
                    
                    dbOp.deleteRepledgeBillDebitTable(sBillNumber, sMaterialType);
                    dbOp.deleteRepledgeBillCreditTable(sBillNumber, sMaterialType);
                    dbOp.deleteRepledgeBillingTable(sBillNumber, sMaterialType);
                    dbOp.updateCompanyBillToEmpty(sLastSelectedRepledgeBillId, "GOLD");
                    
                    PopupUtil.showInfoAlert("Company Bill number: "+sBillNumber+" in "+sMaterialType+" has deleted successfully.");
                    
                    btClearAllClicked(null);
                    txtCompanyBillNumber.requestFocus();
                    
                } catch (Exception ex) {
                    Logger.getLogger(RepledgeBillEditOperationController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                PopupUtil.showErrorAlert("Sorry this bill opening date account was closed.");
            }
        } else {
            PopupUtil.showErrorAlert("Not any bill number was selected to close bill.");
        }        
    }

    @FXML
    private void btUnCloseBillClicked(ActionEvent event) {
    }

    @FXML
    private void btClearAllClicked(ActionEvent event) {
        
        sLastSelectedRepledgeBillId = null;  
        sLastSelectedRepledgeName = null;
        sLastSelectedCompanyBillNumber = null; 

        clearAllBillDetails();
        txtRepledgeBillId.setText(CommonConstants.REP_BILL_ID_PREFIX);
        cbRepledgeName.setMouseTransparent(false);
        cbRepledgeName.setFocusTraversable(true);                      
        txtRepledgeBillNumber.setText("");
        txtCompanyBillNumber.setText("");        
    }
    
}
