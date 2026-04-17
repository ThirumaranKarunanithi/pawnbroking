/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companyadvanceamount;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.companybillclosing.SilverBillClosingController;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class SilverAdvanceAmountController implements Initializable {
    
    public AdvanceAmountDBOperation dbOp;
    public String sLastSelectedId = null;
    private String sRepledgeBillId = null;
    private String sReduceType = null;
    private String sMinimumType = null;
    public Stage dialog;
    
    @FXML
    private TabPane tpScreen;
    @FXML
    private Tab tabMainScreen;
    @FXML
    private ComboBox<String> cbSpouseType;
    @FXML
    private TextField txtBillNumber;
    @FXML
    private DatePicker dpBillOpeningDate;
    @FXML
    private TextField txtCustomerName;
    @FXML
    private ToggleGroup rgGenderGroup;
    @FXML
    private TextField txtSpouseName;
    @FXML
    private TextField txtDoorNo;
    @FXML
    private TextField txtStreetName;
    @FXML
    private TextField txtArea;
    @FXML
    private TextField txtCity;
    @FXML
    private Label lbActualTotalDaysOrMonths;
    @FXML
    private Label lbToReduceDaysOrMonths;
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
    private TextField txtToReduceDaysOrMonths;
    @FXML
    private TextArea txtNote;
    @FXML
    private Label lbTakenDaysOrMonths;
    @FXML
    private TextField txtAmount;
    @FXML
    private TextField txtInterest;
    @FXML
    private TextField txtTakenDaysOrMonths;
    @FXML
    private TextField txtTakenAmount;
    @FXML
    private TextField txtAdvanceAmountPaidAlready;
    @FXML
    public TextField txtGotAdvanceAmount;
    @FXML
    private TextField txtTotalAdvanceAmountPaid;
    @FXML
    private TextField txtPreStatus;
    @FXML
    private Button btPayAdvanceAmount;
    @FXML
    public TextField txtAdvanceReceiptDetailTotalAmount;
    @FXML
    public TableView<AdvanceAmountBean> tbAdvanceReceiptDetails;
    @FXML
    public DatePicker dpPaidDate;
    @FXML
    private Label lbMinimumDaysOrMonths;
    @FXML
    private TextField txtMinimumDaysOrMonths;
    @FXML
    private Button btClearAll;
    @FXML
    private Button btEditDuePaid;
    @FXML
    private Button btSaveDuePaid;
    @FXML
    private HBox nodeAddToFilter;
    @FXML
    private ComboBox<String> cbAllDetailsFilter;
    @FXML
    private DatePicker dpAddToFilter;
    @FXML
    private ComboBox<String> cbAddToFilter;
    @FXML
    private TextField txtAddToFilter;
    @FXML
    private Button btAddToFilter;
    @FXML
    private Button btShowAllRecords;
    @FXML
    private TextField txtFilter;
    @FXML
    private Button btShowFilteredRecords;
    @FXML
    private TableView<AllDetailsBean> tbAllDetails;
    @FXML
    private Button btShowDenomination;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            dbOp = new AdvanceAmountDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SilverAdvanceAmountController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            DataTable otherSettingValues = dbOp.getOtherSettingsValues("SILVER");
            if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(0).toString())) {
                dpPaidDate.setMouseTransparent(false);
                dpPaidDate.setFocusTraversable(true);
            } else {
                dpPaidDate.setMouseTransparent(true);
                dpPaidDate.setFocusTraversable(false);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SilverBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
        dpPaidDate.setValue(LocalDate.now());
        dpBillOpeningDate.setValue(LocalDate.now());
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.SILVER_ADVANCE_AMOUNT_SCREEN, "ALLOW_VIEW") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                txtBillNumber.setEditable(true);
                txtBillNumber.setMouseTransparent(false);
                txtBillNumber.setFocusTraversable(true);
            } else {
                txtBillNumber.setEditable(false);
                txtBillNumber.setMouseTransparent(true);
                txtBillNumber.setFocusTraversable(false);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SilverAdvanceAmountController.class.getName()).log(Level.SEVERE, null, ex);
        }            
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.SILVER_ADVANCE_AMOUNT_SCREEN, "ALLOW_UPDATE") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btPayAdvanceAmount.setDisable(false);
            } else {
                btPayAdvanceAmount.setDisable(true);
            }
            
            String[] billRowAndNumber = dbOp.getSilverCurrentBillNumber();
            if(billRowAndNumber != null) {
                txtBillNumber.setText(billRowAndNumber[1]);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(SilverAdvanceAmountController.class.getName()).log(Level.SEVERE, null, ex);
        }  
        
        Platform.runLater(() -> {
            txtBillNumber.requestFocus();
            txtBillNumber.positionCaret(txtBillNumber.getText().length());
        });        
        
        nodeAddToFilter.getChildren().remove(dpAddToFilter);
        nodeAddToFilter.getChildren().remove(cbAddToFilter);
        
    }    

    @FXML
    private void txtBillNumberOnAction(ActionEvent event) {
        
        String sBillNumber = txtBillNumber.getText();
        String val = txtBillNumber.getText();
        if(val != null & !val.isEmpty()) {
            try {
                String[] billVals = val.split("-");
                String[] billRowAndNumber = dbOp.getSilverCurrentBillNumber();

                if(billRowAndNumber != null && billVals.length > 1) {
                    int index = billRowAndNumber[1].length();
                    sBillNumber = billVals[0].substring(index);
                } else {
                    sBillNumber = billVals[0];
                }            
            } catch (SQLException ex) {
                Logger.getLogger(SilverBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        btClearAllClicked(null);
        txtBillNumber.setText(sBillNumber);
        try {
            HashMap<String, String> headerValues = dbOp.getAllBillingValuesToPay(sBillNumber, "SILVER");
            String sInterestType = dbOp.getInterestType();
            String[] sReduceDatas = dbOp.getReduceOrMinimumDaysOrMonths("SILVER", "REDUCTION");
            String[] sMinimumDatas = dbOp.getReduceOrMinimumDaysOrMonths("SILVER", "MINIMUM");
            
            if(headerValues != null)
            {
                setAllHeaderValuesToFields(headerValues, sInterestType, sReduceDatas, sMinimumDatas);
                sLastSelectedId = sBillNumber;
                sReduceType = sReduceDatas[1];
                sMinimumType = sMinimumDatas[1];
                sRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID");
                
                Platform.runLater(() -> {
                    txtGotAdvanceAmount.requestFocus();
                    txtGotAdvanceAmount.positionCaret(txtGotAdvanceAmount.getText().length());
                });                 
            } else {
                PopupUtil.showErrorAlert("Sorry invalid bill number.");
                btClearAllClicked(null);
                
                String[] billRowAndNumber = dbOp.getSilverCurrentBillNumber();
                if(billRowAndNumber != null) {
                    txtBillNumber.setText(billRowAndNumber[1]);
                }            
                Platform.runLater(() -> {
                    txtBillNumber.requestFocus();
                    txtBillNumber.positionCaret(txtBillNumber.getText().length());
                });                        
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(SilverBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

 public void setAllHeaderValuesToFields(HashMap<String, String> headerValues, String sInterestType, String[] sReduceDatas, String[] sMinimumDatas)
    {
        try {

            txtBillNumber.setText(headerValues.get("BILL_NUMBER"));
            dpBillOpeningDate.setValue(LocalDate.parse(headerValues.get("OPENING_DATE"), CommonConstants.DATETIMEFORMATTER));
            txtCustomerName.setText(headerValues.get("CUSTOMER_NAME"));
            String sGender = headerValues.get("GENDER");
            if("MALE".equals(sGender)) {
                rgGenderGroup.getToggles().get(0).setSelected(true);
            } else if("FEMALE".equals(sGender)) {
                rgGenderGroup.getToggles().get(1).setSelected(true);
            } else if("OTHER".equals(sGender)) {
                rgGenderGroup.getToggles().get(2).setSelected(true);
            }
            cbSpouseType.setValue(headerValues.get("SPOUSE_TYPE"));
            txtSpouseName.setText(headerValues.get("SPOUSE_NAME"));
            txtDoorNo.setText(headerValues.get("DOOR_NUMBER"));
            txtStreetName.setText(headerValues.get("STREET"));
            txtArea.setText(headerValues.get("AREA"));
            txtCity.setText(headerValues.get("CITY"));
            txtMobileNumber.setText(headerValues.get("MOBILE_NUMBER"));
            txtItems.setText(headerValues.get("ITEMS"));
            txtGrossWeight.setText(headerValues.get("GROSS_WEIGHT"));
            txtNetWeight.setText(headerValues.get("NET_WEIGHT"));
            txtPurity.setText(headerValues.get("PURITY"));
            txtAmount.setText(headerValues.get("AMOUNT"));
            txtInterest.setText(headerValues.get("INTEREST"));
            txtNote.setText(headerValues.get("NOTE"));
            txtInterestType.setText(sInterestType);
            txtPreStatus.setText(headerValues.get("STATUS"));
            txtAdvanceAmountPaidAlready.setText(headerValues.get("TOTAL_ADVANCE_AMOUNT_PAID"));
            txtTotalAdvanceAmountPaid.setText(headerValues.get("TOTAL_ADVANCE_AMOUNT_PAID"));
            txtGotAdvanceAmount.setText("");
            
            String sTakenMonths = "0";
            String sTakenDays = "0";
            String sStartDate = CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue());
            String sEndDate = CommonConstants.DATETIMEFORMATTER.format(dpPaidDate.getValue());
            long lTotalDays = DateRelatedCalculations.getDifferenceDays(sStartDate, sEndDate);
            
            if("MONTH".equals(sInterestType)) {
                
                //lTotalDays = lTotalDays>0 ? lTotalDays - 1 : 0;
                long[] lActualTotalMonths = DateRelatedCalculations.getDifferenceMonths(sStartDate, lTotalDays);
                lbActualTotalDaysOrMonths.setText("Actual Total Months:");
                txtActualTotalDaysOrMonths.setText(Long.toString(lActualTotalMonths[0]) + " Months and " + Long.toString(lActualTotalMonths[1]) + " Days.");
                lbTakenDaysOrMonths.setText("Taken Months:");                               
                
                if("MONTHS FROM TOTAL MONTH".equals(sReduceDatas[1])) {
                    try {
                        lbToReduceDaysOrMonths.setText("To Reduce Months:");
                        txtToReduceDaysOrMonths.setText(sReduceDatas[0]);
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithTotalMonthReduction(lActualTotalMonths, sReduceDatas);
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(Double.valueOf(lTakenMonths[1]), "SILVER") : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        sTakenMonths = Double.toString(dTakenMonths);
                        txtTakenDaysOrMonths.setText(sTakenMonths);                        
                    } catch (SQLException ex) {
                        Logger.getLogger(SilverBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if("MONTHS FROM OPENING MONTH".equals(sReduceDatas[1])) {
                    try {
                        lbToReduceDaysOrMonths.setText("To Reduce Months:");
                        txtToReduceDaysOrMonths.setText(sReduceDatas[0]);
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithMonthReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(Double.valueOf(lTakenMonths[1]), "SILVER") : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        sTakenMonths = Double.toString(dTakenMonths);
                        txtTakenDaysOrMonths.setText(sTakenMonths);                        
                    } catch (SQLException ex) {
                        Logger.getLogger(SilverBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if("DAYS".equals(sReduceDatas[1])) {
                    try {
                        lbToReduceDaysOrMonths.setText("To Reduce Days:");
                        txtToReduceDaysOrMonths.setText(sReduceDatas[0]);
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithDayReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(Double.valueOf(lTakenMonths[1]), "SILVER") : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        sTakenMonths = Double.toString(dTakenMonths);
                        txtTakenDaysOrMonths.setText(sTakenMonths);                        
                    } catch (SQLException ex) {
                        Logger.getLogger(SilverBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
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
            
            String sFormula = dbOp.getFormula(Double.parseDouble(headerValues.get("AMOUNT")), "SILVER");
            String[][] replacements = {{"AMOUNT", headerValues.get("AMOUNT")},
                                        {"INTEREST", headerValues.get("INTEREST")},
                                        {"DOCUMENT_CHARGE", headerValues.get("DOCUMENT_CHARGE")},
                                        {"TAKEN_MONTHS", sTakenMonths},
                                        {"TAKEN_DAYS", sTakenDays}};
            for(String[] replacement: replacements) {
                sFormula = sFormula.replace(replacement[0], replacement[1]);
            }
            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
            String sTakenAmount = String.valueOf(Math.round(Double.parseDouble(engine.eval(sFormula).toString())));
            txtTakenAmount.setText(sTakenAmount);
            
            txtAdvanceReceiptDetailTotalAmount.setText(headerValues.get("TOTAL_ADVANCE_AMOUNT_PAID"));
            DataTable advanceAmountDetailValues = dbOp.getAdvanceAmountTableValues(headerValues.get("BILL_NUMBER"), "SILVER");
            setAllDetailValuesToField(advanceAmountDetailValues);
        } catch (SQLException | ScriptException ex) {
            Logger.getLogger(SilverBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }    
 
    public void setAllDetailValuesToField(DataTable allDetailValues) {

        tbAdvanceReceiptDetails.getItems().removeAll(tbAdvanceReceiptDetails.getItems());
        for(int i=0; i<allDetailValues.getRowCount(); i++) {            
            String sBillNumber = allDetailValues.getRow(i).getColumn(0).toString();
            String sDate = allDetailValues.getRow(i).getColumn(1).toString();
            String sBillAmount = allDetailValues.getRow(i).getColumn(2).toString();
            String sPaidAmount = allDetailValues.getRow(i).getColumn(3).toString();
            String sTotalAmount = allDetailValues.getRow(i).getColumn(4).toString();
            
            double dBillAmount = Double.parseDouble(sBillAmount);
            double dPaidAmount = Double.parseDouble(sPaidAmount);
            double dTotalAmount = Double.parseDouble(sTotalAmount);
            tbAdvanceReceiptDetails.getItems().add(new AdvanceAmountBean(sBillNumber, sDate, dBillAmount, dPaidAmount, dTotalAmount));
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
    private void rbToggleChanged(MouseEvent event) {
    }

    @FXML
    private void allowNumberOnlyOnType(KeyEvent event) {
    }

    @FXML
    private void allowNumberAndDotOnType(KeyEvent e) {
        
        TextField txt_TextField = (TextField) e.getSource();                      
        if(!("0123456789.".contains(e.getCharacter()))){ 
            e.consume();
        }        
    }

    @FXML
    private void btPayAdvanceAmountClicked(ActionEvent event) {
        
        if(sLastSelectedId != null) {
            
            String sPaidDate = CommonConstants.DATETIMEFORMATTER.format(dpPaidDate.getValue());
            
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sPaidDate))
            {
                if(DateRelatedCalculations.isFirstDateIsLesserOrEqualToSecondDate(CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue()), sPaidDate))
                {
                    String sBillAmount = txtAmount.getText();
                    String sPaidAmount = txtGotAdvanceAmount.getText();
                    String sTotalAmount = txtTotalAdvanceAmountPaid.getText();

                    double dBillAmount = Double.parseDouble(!("".equals(sBillAmount))? sBillAmount : "0");
                    double dPaidAmount = Double.parseDouble(!("".equals(sPaidAmount))? sPaidAmount : "0");
                    double dTotalAmount = Double.parseDouble(!("".equals(sTotalAmount))? sTotalAmount : "0");

                    if(isValidHeaderValues(dPaidAmount)) {
                        try {
                            if(dbOp.saveRecord(sLastSelectedId, sPaidDate, dBillAmount, dPaidAmount, dTotalAmount, "SILVER")) {
                                if(dbOp.updateTotalAdvanceAmount(sLastSelectedId, txtNote.getText(), dTotalAmount, "SILVER")) { 
                                    txtBillNumber.setText(sLastSelectedId);
                                    txtBillNumberOnAction(null);
                                    PopupUtil.showInfoAlert("Advance amount payed successfully.");
                                } else {
                                    PopupUtil.showErrorAlert("Problem in paying amount.");
                                }
                            } else {
                                PopupUtil.showErrorAlert("Problem in paying amount.");
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(SilverAdvanceAmountController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        PopupUtil.showErrorAlert("All mandatory fields should be filled properly.");
                    }
                } else {
                    PopupUtil.showErrorAlert("Sorry you cannot pay advance amount before the opened date.");
                }
            } else {
                PopupUtil.showErrorAlert("Sorry this advance amount date account was closed.");
            }          
        } else {
            PopupUtil.showErrorAlert("Not any bill number was selected to close bill.");
        }
    }

    public boolean isValidHeaderValues(double dPaidAmount)
    {
        return dPaidAmount > 0;
    }
    

    @FXML
    private void txtAdvanceReceiptDetailTotalAmountOnAction(ActionEvent event) {
    }

    @FXML
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {
    }
    
    @FXML
    private void dpPaidDateTextChanged(ActionEvent event) {
        if(sLastSelectedId != null) {            
            try {
                HashMap<String, String> headerValues = dbOp.getAllBillingValuesToPay(sLastSelectedId, "SILVER");
                String sInterestType = dbOp.getInterestType();
                String[] sReduceDatas = dbOp.getReduceOrMinimumDaysOrMonths("SILVER", "REDUCTION");
                String[] sMinimumDatas = dbOp.getReduceOrMinimumDaysOrMonths("SILVER", "MINIMUM");

                if(headerValues != null)
                {
                    setAllHeaderValuesToFields(headerValues, sInterestType, sReduceDatas, sMinimumDatas);
                } 
            } catch (SQLException ex) {
                Logger.getLogger(SilverBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
    }
    
    public void clearAllHeader()
    {
        dpPaidDate.setValue(LocalDate.now());
        dpBillOpeningDate.setValue(LocalDate.now());
        txtCustomerName.setText("");
        txtSpouseName.setText("");
        txtDoorNo.setText("");
        txtStreetName.setText("");
        txtArea.setText("");
        txtCity.setText("");
        txtMobileNumber.setText("");
        txtItems.setText("");
        txtGrossWeight.setText("");
        txtNetWeight.setText("");
        txtPurity.setText("");
        txtInterestType.setText("");
        lbActualTotalDaysOrMonths.setText("Actual Total Days/Months:");
        txtActualTotalDaysOrMonths.setText("");
        lbToReduceDaysOrMonths.setText("To Reduce Days/Months:");
        txtToReduceDaysOrMonths.setText("");
        lbMinimumDaysOrMonths.setText("Minimum Days/Months:");
        txtMinimumDaysOrMonths.setText("");
        txtNote.setText("");        
        txtAmount.setText("");
        txtInterest.setText("");
        lbTakenDaysOrMonths.setText("Taken Days/Months:");
        txtTakenDaysOrMonths.setText("");
        txtTakenAmount.setText("");    
        txtAdvanceAmountPaidAlready.setText("");
        txtGotAdvanceAmount.setText("");
        txtTotalAdvanceAmountPaid.setText("");
        txtPreStatus.setText("");
        txtAdvanceReceiptDetailTotalAmount.setText("");
        tbAdvanceReceiptDetails.getItems().removeAll(tbAdvanceReceiptDetails.getItems());
    }

    @FXML
    private void txtGotAdvanceAmountOnPress(KeyEvent e) {
        if (e.getCode() == KeyCode.BACK_SPACE) {
            Platform.runLater(() -> {
                String sPaidAlreadyAmount = txtAdvanceAmountPaidAlready.getText();
                String sGotAmount = txtGotAdvanceAmount.getText().trim();
                try {
                    double dPaidAlreadyAmount = sPaidAlreadyAmount.isEmpty() ? 0 : Double.parseDouble(sPaidAlreadyAmount);
                    double dGotAmount = sGotAmount.isEmpty() ? 0 : Double.parseDouble(sGotAmount);
                    txtTotalAdvanceAmountPaid.setText(Double.toString(dPaidAlreadyAmount + dGotAmount));
                } catch (NumberFormatException ex) {
                    txtTotalAdvanceAmountPaid.setText(sPaidAlreadyAmount);
                }
            });
        }
    }

    @FXML
    private void txtGotAdvanceAmountOnTyped(KeyEvent e) {
        if (!("0123456789.".contains(e.getCharacter()))) {
            e.consume();
            return;
        }
        Platform.runLater(() -> {
            String sPaidAlreadyAmount = txtAdvanceAmountPaidAlready.getText();
            String sGotAmount = txtGotAdvanceAmount.getText().trim();
            try {
                double dPaidAlreadyAmount = sPaidAlreadyAmount.isEmpty() ? 0 : Double.parseDouble(sPaidAlreadyAmount);
                double dGotAmount = sGotAmount.isEmpty() ? 0 : Double.parseDouble(sGotAmount);
                txtTotalAdvanceAmountPaid.setText(Double.toString(dPaidAlreadyAmount + dGotAmount));
            } catch (NumberFormatException ex) {
                txtTotalAdvanceAmountPaid.setText(sPaidAlreadyAmount);
            }
        });
    }

    @FXML
    private void btClearAllClicked(ActionEvent event) {
        txtBillNumber.setText("");
        clearAllHeader();
        sLastSelectedId = null;
        sRepledgeBillId = null;
        sReduceType = null;
        sMinimumType = null;
    }

    @FXML
    private void btEditDuePaidClicked(ActionEvent event) {
        
        int index = tbAdvanceReceiptDetails.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            AdvanceAmountBean bean = tbAdvanceReceiptDetails.getItems().get(index);
            String sPaidDate = bean.getSDate();
            
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sPaidDate))
            {

                dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);        

                FXMLLoader loader = new FXMLLoader(getClass().getResource("SilverAdvanceAmountEditDialog.fxml"));
                Parent root = null;
                try {            
                    root = (Parent) loader.load();            
                } catch (IOException ex) {
                    Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
                }

                SilverAdvanceAmountEditDialogUIController gon = (SilverAdvanceAmountEditDialogUIController) loader.getController();
                gon.setParent(this, false);
                gon.setInitValues();
                Scene scene = new Scene(root);        
                dialog.setScene(scene);
                dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
                dialog.showAndWait();
                
            } else {
                PopupUtil.showErrorAlert("Sorry this advance amount date account was closed.");
            }           
        } else {
            PopupUtil.showInfoAlert("Any of a row in table should be selected.");
        }        
    }

    @FXML
    private void btSaveDuePaidClicked(ActionEvent event) {
        
        txtBillNumber.setText(sLastSelectedId);
        double dTotalAmount = Double.parseDouble(txtAdvanceReceiptDetailTotalAmount.getText());
        String sBillNumber = txtBillNumber.getText().toUpperCase();
        ObservableList<AdvanceAmountBean> tableValues = tbAdvanceReceiptDetails.getItems();
        
        try {
            if(dbOp.deleteAllSelectedBillAdvAmtVals(sBillNumber, "SILVER")) {
                if(dbOp.saveBillAdvAmtVals(sBillNumber, tableValues, "SILVER")) {
                    if(dbOp.updateTotalAdvanceAmount(sLastSelectedId, txtNote.getText(), dTotalAmount, "SILVER")) { 
                        txtBillNumber.setText(sLastSelectedId);
                        txtBillNumberOnAction(null);                        
                        PopupUtil.showInfoAlert("Advance amount values are updated successfully to the bill "+sLastSelectedId+".");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(GoldAdvanceAmountController.class.getName()).log(Level.SEVERE, null, ex);
        }                        
    }

    @FXML
    private void cbAllDetailsFilterOnAction(ActionEvent event) {
    }

    @FXML
    private void btAddToFilterClicked(ActionEvent event) {
    }

    @FXML
    private void showAllRecordsClicked(ActionEvent event) {
        try {
            DataTable allDetailValues = dbOp.getAllDetailsValues("SILVER", null);
            setAllAllDetailValuesToField(allDetailValues);
        } catch (SQLException ex) {
            Logger.getLogger(GoldAdvanceAmountController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public void setAllAllDetailValuesToField(DataTable allDetailValues) {

        tbAllDetails.getItems().removeAll(tbAllDetails.getItems());
        for(int i=0; i<allDetailValues.getRowCount(); i++) {            
            String sBillNumber = allDetailValues.getRow(i).getColumn(0).toString();
            String sDate = allDetailValues.getRow(i).getColumn(1).toString();
            String sAmount = allDetailValues.getRow(i).getColumn(2).toString();
            String sName = allDetailValues.getRow(i).getColumn(3).toString();
            String sGender = allDetailValues.getRow(i).getColumn(4).toString();
            String sSpouseType = allDetailValues.getRow(i).getColumn(5).toString();
            String sSpouseName = allDetailValues.getRow(i).getColumn(6).toString();
            String sStreet = allDetailValues.getRow(i).getColumn(7).toString();
            String sArea = allDetailValues.getRow(i).getColumn(8).toString();
            String sMobileNumber = allDetailValues.getRow(i).getColumn(9).toString();
            String sItems = allDetailValues.getRow(i).getColumn(10).toString();
            String sGrossWeight = allDetailValues.getRow(i).getColumn(11).toString();
            String sNetWeight = allDetailValues.getRow(i).getColumn(12).toString();
            String sPurity = allDetailValues.getRow(i).getColumn(13).toString();
            String sStatus = allDetailValues.getRow(i).getColumn(14).toString();
            String sNote = allDetailValues.getRow(i).getColumn(15).toString();
            String sRepledgeBillId = allDetailValues.getRow(i).getColumn(16).toString();   
            String sTotalAdvAmtPaid = allDetailValues.getRow(i).getColumn(17).toString();   
            String sPaidDate = allDetailValues.getRow(i).getColumn(18).toString();   
            String sPaidAmount = allDetailValues.getRow(i).getColumn(19).toString();   
            tbAllDetails.getItems().add(new AllDetailsBean(sBillNumber, sDate, Double.parseDouble(sAmount), 
                    sName, sGender, sSpouseType, sSpouseName, sStreet, sArea, sMobileNumber, sItems, sGrossWeight, 
                    sNetWeight, sPurity, sStatus, sNote, sRepledgeBillId, 
                    Double.parseDouble(sTotalAdvAmtPaid), 0, sPaidDate, Double.parseDouble(sPaidAmount)));
        }        
    }    

    @FXML
    private void btFilterUndoOnAction(ActionEvent event) {
    }

    @FXML
    private void btFilterClearAllClicked(ActionEvent event) {
    }

    @FXML
    private void showFilteredRecordsClicked(ActionEvent event) {
    }

    @FXML
    private void btOpenInBillClosingClicked(ActionEvent event) {
    }

    @FXML
    private void txtGotAdvanceAmountOnAction(ActionEvent event) {
        
        double dGotAmt = Double.parseDouble(txtGotAdvanceAmount.getText());
        String paidDate = CommonConstants.DATETIMEFORMATTER.format(dpPaidDate.getValue());
        try {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("AvailableBalanceDialog.fxml"));
            Parent root = null;
            try {
                root = (Parent) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            AvailableBalanceDialogUIController gon = (AvailableBalanceDialogUIController) loader.getController();
            List<AvailableBalanceBean> currencyList =
                    dbOp.getDenominationValues(CommonConstants.S_ADVANCE_AMOUNT_OPERATION, 
                            sLastSelectedId, paidDate, txtGotAdvanceAmount.getText());
            
            gon.setParent(this, txtGotAdvanceAmount.getText(), paidDate, true);
            gon.setInitValues(currencyList);
            Scene scene = new Scene(root);
            dialog.setScene(scene);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            dialog.setX(180);
            dialog.setY(100);
            dialog.setHeight(520);
            dialog.setTitle("Available Balance");
            dialog.setResizable(false);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
        } catch (SQLException ex) {
            Logger.getLogger(SilverAdvanceAmountController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @FXML
    private void btShowDenominationClicked(ActionEvent event) {
        
        int index = tbAdvanceReceiptDetails.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            AdvanceAmountBean bean = tbAdvanceReceiptDetails.getItems().get(index);
            String sPaidAmt = String.valueOf(bean.getDPaidAmount());
            String sPaidDate = bean.getSDate();

            try {
                dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("AvailableBalanceDialog.fxml"));
                Parent root = null;
                try {
                    root = (Parent) loader.load();
                } catch (IOException ex) {
                    Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
                }

                AvailableBalanceDialogUIController gon = (AvailableBalanceDialogUIController) loader.getController();
                List<AvailableBalanceBean> currencyList =
                        dbOp.getDenominationValues(CommonConstants.S_ADVANCE_AMOUNT_OPERATION, 
                                sLastSelectedId, sPaidDate, sPaidAmt);

                gon.setParent(this, sPaidAmt, sPaidDate, false);
                gon.setInitValues(currencyList);
                Scene scene = new Scene(root);
                dialog.setScene(scene);
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getVisualBounds();
                dialog.setX(180);
                dialog.setY(100);
                dialog.setHeight(520);
                dialog.setTitle("Available Balance");
                dialog.setResizable(false);
                dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
                dialog.showAndWait();
            } catch (SQLException ex) {
                Logger.getLogger(SilverAdvanceAmountController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }

    public void viewBill(String sBillNumber, boolean onlyForView) {
        viewBillWork(sBillNumber, onlyForView);
    }
    
    private void viewBillWork(String sBillNumber, boolean onlyForView) {
        txtBillNumber.setText(sBillNumber);
        txtBillNumberOnAction(null);
        dpBillOpeningDate.setMouseTransparent(onlyForView);
        dpBillOpeningDate.setFocusTraversable(!onlyForView);            
        tpScreen.getSelectionModel().select(tabMainScreen);            
    }    
}
