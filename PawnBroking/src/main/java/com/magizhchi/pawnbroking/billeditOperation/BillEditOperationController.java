/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.billeditOperation;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.common.Util;
import com.magizhchi.pawnbroking.companyadvanceamount.AdvanceAmountBean;
import com.magizhchi.pawnbroking.companybillclosing.BillClosingDBOperation;
import com.magizhchi.pawnbroking.companymaster.CompanyMasterController;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class BillEditOperationController implements Initializable {
    
    public BillClosingDBOperation dbOp;
    private String sLastSelectedId = null;
    private String sRepledgeBillId = null;
    private String sReduceType = null;
    private String sMinimumType = null;
    private String sRebilledTo = null;
    private final ArrayList<String> alFilterDBColumnName = new ArrayList<>();
    private final ArrayList<String> alFilterName = new ArrayList<>();
    private final ArrayList<String> alFilterValue = new ArrayList<>();
    public Stage dialog;
    private DataTable otherSettingValues = null;
    
    @FXML
    public TabPane tpScreen;
    @FXML
    private Tab tabMainScreen;
    @FXML
    private ComboBox<String> cbSpouseType;
    @FXML
    private TextField txtBillNumber;
    @FXML
    private DatePicker dpBillClosingDate;
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
    private TextField txtMobileNumber;
    @FXML
    private TextField txtItems;
    @FXML
    private TextField txtInterestType;
    @FXML
    private TextField txtActualTotalDaysOrMonths;
    @FXML
    private TextField txtToReduceDaysOrMonths;
    @FXML
    private TextArea txtNote;
    @FXML
    private TextField txtAmount;
    @FXML
    private TextField txtInterest;
    @FXML
    private TextField txtTakenAmount;
    @FXML
    private TextField txtToGetAmount;
    @FXML
    public TextField txtGotAmount;
    @FXML
    private ToggleButton tgOn;
    @FXML
    private ToggleGroup ViewModeGroup;
    @FXML
    private ToggleButton tgOff;
    @FXML
    private Button btClearAll;
    @FXML
    private Label lbActualTotalDaysOrMonths;
    @FXML
    private Label lbToReduceDaysOrMonths;
    @FXML
    private Label lbTakenDaysOrMonths;
    @FXML
    private TextField txtTakenDaysOrMonths;
    @FXML
    private TextField txtPreStatus;
    @FXML
    private TextField txtGrossWeight;
    @FXML
    private TextField txtNetWeight;
    @FXML
    private TextField txtPurity;
    @FXML
    private TextField txtTotalAdvanceAmountPaid;
    @FXML
    public TextField txtAdvanceReceiptDetailTotalAmount;
    @FXML
    public TableView<AdvanceAmountBean> tbAdvanceReceiptDetails;
    @FXML
    private TextField txtMinimumDaysOrMonths;
    @FXML
    private Label lbMinimumDaysOrMonths;
    @FXML
    private TextField txtRepledgeBillId;
    @FXML
    private TextField txtRepledgeName;
    @FXML
    private TextField txtRepledgeBillNumber;
    @FXML
    private TextField txtRepledgeBillAmount;
    @FXML
    private TextField txtReBilledFrom;
    @FXML
    private TextField txtReBilledTo;
    @FXML
    private TextField txtRepledgeBillStatus;
    @FXML
    private TextField txtCompanyBillNumber;
    @FXML
    private Label lbScreenMessage;
    @FXML
    public Tab tabAdvanceAmountDetails;
    @FXML
    private HBox hSaveModeButtons;
    @FXML
    public ComboBox<String> cbBillMaterialType;
    @FXML
    private Button btDelete;
    @FXML
    private Button btChangeAADate;
    @FXML
    private Button btUnCloseBill;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            dbOp = new BillClosingDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        try {
            
            lbScreenMessage.setVisible(false);
            otherSettingValues = dbOp.getOtherSettingsValues(cbBillMaterialType.getValue());
            if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(0).toString())) {
                dpBillClosingDate.setMouseTransparent(false);
                dpBillClosingDate.setFocusTraversable(true);
            } else {
                dpBillClosingDate.setMouseTransparent(true);
                dpBillClosingDate.setFocusTraversable(false);
            }
            dpBillClosingDate.setValue(LocalDate.now());
            dpBillOpeningDate.setValue(LocalDate.now());

            txtBillNumber.requestFocus();
        } catch (SQLException ex) {
            Logger.getLogger(BillEditOperationController.class.getName()).log(Level.SEVERE, null, ex);
        }              

        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.BILL_EDIT_OPERATION_SCREEN, "ALLOW_VIEW") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                txtBillNumber.setEditable(true);
                txtBillNumber.setMouseTransparent(false);
                txtBillNumber.setFocusTraversable(true);
            } else {
                txtBillNumber.setEditable(false);
                txtBillNumber.setMouseTransparent(true);
                txtBillNumber.setFocusTraversable(false);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }            
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.BILL_EDIT_OPERATION_SCREEN, "ALLOW_UPDATE") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btDelete.setDisable(false);
            } else {
                btDelete.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
    }    

    @FXML
    private void txtBillNumberOnAction(ActionEvent event) {
        
        String sBillNumber = txtBillNumber.getText();
        btClearAllClicked(null);
        txtBillNumber.setText(sBillNumber);
        try {
            HashMap<String, String> headerValues = tgOn.isSelected() ? dbOp.getAllBillingValuesToClose(sBillNumber, cbBillMaterialType.getValue()) : dbOp.getAllClosedBillingValues(sBillNumber, cbBillMaterialType.getValue());
            String sInterestType = dbOp.getInterestType();
            String[] sReduceDatas = dbOp.getReduceOrMinimumDaysOrMonths(cbBillMaterialType.getValue(), "REDUCTION");
            String[] sMinimumDatas = dbOp.getReduceOrMinimumDaysOrMonths(cbBillMaterialType.getValue(), "MINIMUM");
            
            if(headerValues != null)
            {
                if(tgOff.isSelected())
                    dpBillClosingDate.setValue(LocalDate.parse(headerValues.get("CLOSING_DATE"), CommonConstants.DATETIMEFORMATTER));
                setAllHeaderValuesToFields(headerValues, sInterestType, sReduceDatas, sMinimumDatas);
                sLastSelectedId = sBillNumber;
                sReduceType = sReduceDatas[1];
                sMinimumType = sMinimumDatas[1];
                sRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID");
            } else {
                PopupUtil.showErrorAlert("Sorry invalid bill number.");
                btClearAllClicked(null);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BillEditOperationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setAllDetailValuesToFieldInAdvanceReceiptTable(DataTable allDetailValues) {

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
    
    public void setAllHeaderValuesToFields(HashMap<String, String> headerValues, String sInterestType, String[] sReduceDatas, String[] sMinimumDatas)
    {
        try {

            txtBillNumber.setText(headerValues.get("BILL_NUMBER"));
            dpBillOpeningDate.setValue(LocalDate.parse(headerValues.get("OPENING_DATE"), CommonConstants.DATETIMEFORMATTER));
            txtCustomerName.setText(headerValues.get("CUSTOMER_NAME"));
            String sGender = headerValues.get("GENDER");
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
            txtTotalAdvanceAmountPaid.setText(headerValues.get("TOTAL_ADVANCE_AMOUNT_PAID"));
            
            String sTakenMonths = "0";
            String sTakenDays = "0";
            String sStartDate = CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue());
            String sEndDate = CommonConstants.DATETIMEFORMATTER.format(dpBillClosingDate.getValue());
            long lTotalDays = DateRelatedCalculations.getDifferenceDays(sStartDate, sEndDate);
            
            if("MONTH".equals(sInterestType)) {
                
                long[] lActualTotalMonths = DateRelatedCalculations.getDifferenceMonths(sStartDate, lTotalDays);
                lbActualTotalDaysOrMonths.setText("Actual Total Months:");
                txtActualTotalDaysOrMonths.setText(Long.toString(lActualTotalMonths[0]) + " Months and " + Long.toString(lActualTotalMonths[1]) + " Days.");
                lbTakenDaysOrMonths.setText("For Months:");                               
                
                if("MONTHS FROM OPENING MONTH".equals(sReduceDatas[1])) {
                    try {
                        lbToReduceDaysOrMonths.setText("To Reduce Months:");
                        txtToReduceDaysOrMonths.setText(sReduceDatas[0]);
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithMonthReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(CommonConstants.DATETIMEFORMATTER.format(dpBillClosingDate.getValue()), Double.valueOf(lTakenMonths[1]), cbBillMaterialType.getValue()) : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        sTakenMonths = Double.toString(dTakenMonths);
                        txtTakenDaysOrMonths.setText(sTakenMonths);                        
                    } catch (SQLException ex) {
                        Logger.getLogger(BillEditOperationController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if("DAYS".equals(sReduceDatas[1])) {
                    try {
                        lbToReduceDaysOrMonths.setText("To Reduce Days:");
                        txtToReduceDaysOrMonths.setText(sReduceDatas[0]);
                        long[] lTakenMonths = DateRelatedCalculations.getDifferenceMonthsWithDayReduction(sStartDate, lTotalDays, Integer.parseInt(sReduceDatas[0]));
                        double dRemainingDaysAsMonths = (Double.valueOf(lActualTotalMonths[0]) > 0) ? dbOp.getRemainingDaysAsMonths(CommonConstants.DATETIMEFORMATTER.format(dpBillClosingDate.getValue()), Double.valueOf(lTakenMonths[1]), cbBillMaterialType.getValue()) : 0;
                        double dTakenMonths = Double.valueOf(lTakenMonths[0]) + dRemainingDaysAsMonths;
                        sTakenMonths = Double.toString(dTakenMonths);
                        txtTakenDaysOrMonths.setText(sTakenMonths);                        
                    } catch (SQLException ex) {
                        Logger.getLogger(BillEditOperationController.class.getName()).log(Level.SEVERE, null, ex);
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
                lbTakenDaysOrMonths.setText("For Days:");               
                
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
            
            String sFormula = dbOp.getFormula(CommonConstants.DATETIMEFORMATTER.format(dpBillClosingDate.getValue()), Double.parseDouble(headerValues.get("AMOUNT")), cbBillMaterialType.getValue());
            String[][] replacements = {{"AMOUNT", headerValues.get("AMOUNT")},
                                        {"INTEREST", headerValues.get("INTEREST")},
                                        {"DOCUMENT_CHARGE", headerValues.get("DOCUMENT_CHARGE")},
                                        {"TAKEN_MONTHS", sTakenMonths},
                                        {"TAKEN_DAYS", sTakenDays}};
            for(String[] replacement: replacements) {
                sFormula = sFormula.replace(replacement[0], replacement[1]);
            }
            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
            String sTakenAmount = engine.eval(sFormula).toString();
            String sToGet = Double.toString((Double.parseDouble(headerValues.get("AMOUNT")) + Double.parseDouble(sTakenAmount)) - Double.parseDouble(headerValues.get("TOTAL_ADVANCE_AMOUNT_PAID")));
            txtTakenAmount.setText(sTakenAmount);
            txtToGetAmount.setText(sToGet);
            if(tgOn.isSelected()) {
                txtGotAmount.setText(sToGet);
            } else {
                txtGotAmount.setText(headerValues.get("GOT_AMOUNT"));
            }
            txtGotAmount.requestFocus();
            txtGotAmount.positionCaret(txtGotAmount.getText().length());
                        
            DataTable advanceAmountDetailValues = dbOp.getAdvanceAmountTableValues(headerValues.get("BILL_NUMBER"), cbBillMaterialType.getValue());
            txtAdvanceReceiptDetailTotalAmount.setText(headerValues.get("TOTAL_ADVANCE_AMOUNT_PAID"));
            setAllDetailValuesToFieldInAdvanceReceiptTable(advanceAmountDetailValues);
            
            txtReBilledFrom.setText(headerValues.get("REBILLED_FROM"));
            txtReBilledTo.setText(headerValues.get("REBILLED_TO"));
            HashMap<String, String> repledgeValues = dbOp.getAllHeaderValuesByRepledgeBillId(headerValues.get("REPLEDGE_BILL_ID"), cbBillMaterialType.getValue());
            if(repledgeValues != null) {
                txtRepledgeBillId.setText(repledgeValues.get("REPLEDGE_BILL_ID"));
                txtRepledgeName.setText(repledgeValues.get("REPLEDGE_NAME"));
                txtRepledgeBillNumber.setText(repledgeValues.get("REPLEDGE_BILL_NUMBER"));
                txtCompanyBillNumber.setText(repledgeValues.get("BILL_NUMBER"));
                txtRepledgeBillAmount.setText(repledgeValues.get("REPLEDGE_AMOUNT"));
                txtRepledgeBillStatus.setText(repledgeValues.get("REPLEDGE_STATUS"));
                lbScreenMessage.setText("NOTE: THIS BILL IS IN '"+ repledgeValues.get("REPLEDGE_NAME") +"', IN THE NUMBER '"+ repledgeValues.get("REPLEDGE_BILL_NUMBER") +"'.");
                lbScreenMessage.setVisible(true);
            } else {
                lbScreenMessage.setText("");
                lbScreenMessage.setVisible(false);
            }
            
            sRebilledTo = headerValues.get("REBILLED_TO");
            sRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID");
        } catch (SQLException | ScriptException ex) {
            Logger.getLogger(BillEditOperationController.class.getName()).log(Level.SEVERE, null, ex);
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
    private void saveModeON(ActionEvent event) {
        
        btClearAllClicked(null);
        btClearAll.setDisable(true);
        btUnCloseBill.setDisable(true);
        txtBillNumber.setText("");        
        sLastSelectedId = null;        
        txtGotAmount.setEditable(true);
        txtGotAmount.setMouseTransparent(false);
        txtGotAmount.setFocusTraversable(true);   
        txtBillNumber.requestFocus();
    }

    @FXML
    private void saveModeOFF(ActionEvent event) {
        
        btClearAllClicked(null);
        btClearAll.setDisable(true);
        btUnCloseBill.setDisable(false);
        txtBillNumber.setText("");        
        sLastSelectedId = null;
        if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(1).toString())) {
            txtGotAmount.setEditable(true);
            txtGotAmount.setMouseTransparent(false);
            txtGotAmount.setFocusTraversable(true);  
        } else {
            txtGotAmount.setEditable(false);
            txtGotAmount.setMouseTransparent(true);
            txtGotAmount.setFocusTraversable(false);  
        }        
        txtBillNumber.requestFocus();                
    }

    @FXML
    private void btSaveBillClicked(ActionEvent event) {
        
    
        PopupUtil.showPasswordField(Util.getPassWordFor(""));
        
        if(PopupUtil.isRightPassord && sLastSelectedId != null) {
            
            String sBillOpeningDate = CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue());
            
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sBillOpeningDate))
            {

                try {
                    
                    String sBillNumber = txtBillNumber.getText().toUpperCase();
                    String sMaterialType = cbBillMaterialType.getValue();
                    
                    dbOp.deleteCompanyBillDebitTable(sBillNumber, sMaterialType);
                    dbOp.deleteCompanyBillCreditTable(sBillNumber, sMaterialType);
                    dbOp.deleteRepledgeBillingTable(sBillNumber, sMaterialType);
                    dbOp.deleteCompanyBillingTable(sBillNumber, sMaterialType);
                    
                    PopupUtil.showInfoAlert("Bill number: "+sBillNumber+" in "+sMaterialType+" has deleted successfully.");
                    
                    btClearAllClicked(null);
                    txtBillNumber.requestFocus();
                    
                } catch (Exception ex) {
                    Logger.getLogger(BillEditOperationController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                PopupUtil.showErrorAlert("Sorry this bill opening date account was closed.");
            }
        } else {
            PopupUtil.showErrorAlert("Not any bill number was selected to close bill.");
        }
    }

    public boolean isValidHeaderValues(double dGot)
    {
        return dGot > 0;
    }
    
    private void btUpdateBillClicked(ActionEvent event) {        
        btSaveBillClicked(null);
    }

    @FXML
    private void btClearAllClicked(ActionEvent event) {
        txtBillNumber.setText("");
        clearAllHeader();
        sLastSelectedId = null;
        sRepledgeBillId = null;
        sRebilledTo = null;
        sReduceType = null;
        sMinimumType = null;
        sRebilledTo = null;
    }
    
    public void clearAllHeader()
    {
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
        txtMinimumDaysOrMonths.setText("");
        lbToReduceDaysOrMonths.setText("To Reduce Days/Months:");
        txtToReduceDaysOrMonths.setText("");
        txtNote.setText("");        
        txtAmount.setText("");
        txtInterest.setText("");
        lbTakenDaysOrMonths.setText("Taken Days/Months:");
        txtTakenDaysOrMonths.setText("");
        txtTakenAmount.setText("");    
        txtTotalAdvanceAmountPaid.setText("");
        txtToGetAmount.setText("");
        txtGotAmount.setText("");
        txtPreStatus.setText("");

        txtAdvanceReceiptDetailTotalAmount.setText("");
        tbAdvanceReceiptDetails.getItems().removeAll(tbAdvanceReceiptDetails.getItems());
        txtReBilledFrom.setText("");
        txtReBilledTo.setText("");
        txtRepledgeBillId.setText("");
        txtRepledgeName.setText("");
        txtRepledgeBillNumber.setText("");
        txtCompanyBillNumber.setText("");
        txtRepledgeBillAmount.setText("");
        txtRepledgeBillStatus.setText("");
        
        lbScreenMessage.setText("");
        lbScreenMessage.setVisible(false);
    }

    @FXML
    private void dpBillClosingDateTextChanged(ActionEvent event) {
        if(sLastSelectedId != null) {            
            try {
                HashMap<String, String> headerValues = tgOn.isSelected() ? dbOp.getAllBillingValuesToClose(sLastSelectedId, cbBillMaterialType.getValue()) : dbOp.getAllClosedBillingValues(sLastSelectedId, cbBillMaterialType.getValue());
                String sInterestType = dbOp.getInterestType();
                String[] sReduceDatas = dbOp.getReduceOrMinimumDaysOrMonths(cbBillMaterialType.getValue(), "REDUCTION");
                String[] sMinimumDatas = dbOp.getReduceOrMinimumDaysOrMonths(cbBillMaterialType.getValue(), "MINIMUM");

                if(headerValues != null)
                {
                    setAllHeaderValuesToFields(headerValues, sInterestType, sReduceDatas, sMinimumDatas);
                } 
            } catch (SQLException ex) {
                Logger.getLogger(BillEditOperationController.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
    }

    @FXML
    private void btChangeAADateClicked(ActionEvent event) {
        
        int index = tbAdvanceReceiptDetails.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("BillEditOperationDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            BillEditOperationDialogUIController gon = (BillEditOperationDialogUIController) loader.getController();
            gon.setParent(this, false);
            gon.setInitValues();
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
        } else {
            PopupUtil.showInfoAlert("Any of a row in a table should be selected.");
        }        
    }

    @FXML
    private void btUnCloseBillClicked(ActionEvent event) {
        
        PopupUtil.showPasswordField(Util.getPassWordFor(""));
        
        if(PopupUtil.isRightPassord && sLastSelectedId != null) {
            
            String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpBillClosingDate.getValue());
            
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sBillClosingDate))
            {
                if(DateRelatedCalculations.isFirstDateIsLesserOrEqualToSecondDate(CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue()), sBillClosingDate))
                {
                    try {
                        HashMap<String, String> headerValues = dbOp.getAllClosedBillingValues(sLastSelectedId, cbBillMaterialType.getValue());
                        if(tgOff.isSelected() && headerValues != null)
                        {
                            String sInterestType = null;
                            String sClosingDate = null;
                            String sTotalDaysOrMonths = null;
                            double dTakenAmount = Double.valueOf(0);
                            double dToGet = Double.valueOf(0);
                            double dGot = Double.valueOf(0);
                            String sStatus = "OPENED";
                            int iReducedDaysOrMonths = Integer.valueOf(0);
                            double dTakenDaysOrMonths = Double.valueOf(0);
                            int iMinimumDaysOrMonths = Integer.valueOf(0);
                            String sReduceType = null;
                            String sMinimumType = null;                            
                            String sJewelType = cbBillMaterialType.getValue();
                            
                            double dNoticeCharge = Double.valueOf(0);
                            double dFineInterest = Double.valueOf(0);
                            double dFineCharge = Double.valueOf(0);
                            double dOtherCharges = Double.valueOf(0);
                            double dDiscount = Double.valueOf(0);
                            
                            if(dbOp.unCloseBill(sLastSelectedId, sInterestType, sClosingDate, 
                                    sTotalDaysOrMonths, iMinimumDaysOrMonths, iReducedDaysOrMonths, 
                                    dTakenDaysOrMonths, dTakenAmount, dToGet, dGot, sStatus, sReduceType, 
                                    sMinimumType, sJewelType, dNoticeCharge, dFineInterest, dFineCharge, dOtherCharges, dDiscount)) 
                            {
                                PopupUtil.showInfoAlert("Bill " + sLastSelectedId +" unclosed successfully.");  
                            }
                        } else {
                            PopupUtil.showErrorAlert("Sorry invalid bill number to unclose.");
                            btClearAllClicked(null);
                        }                        
                    } catch (SQLException ex) {
                        Logger.getLogger(BillEditOperationController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(BillEditOperationController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    PopupUtil.showErrorAlert("Sorry you cannot close the bill before the opened date.");
                }
            } else {
                PopupUtil.showErrorAlert("Sorry this bill closing date account was closed.");
            }
        } else {
            PopupUtil.showErrorAlert("Not any bill number was selected to close bill.");
        }
        
    }
}
