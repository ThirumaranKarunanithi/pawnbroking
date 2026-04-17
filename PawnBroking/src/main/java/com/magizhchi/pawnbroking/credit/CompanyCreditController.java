/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.credit;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.companybillopening.GoldBillOpeningController;
import com.magizhchi.pawnbroking.debit.CompanyDebitController;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class CompanyCreditController implements Initializable {

    private CompanyCreditDBOperation dbOp;
    private String sLastSelectedId = null;
    private String sReduceForId = null;
    private String sEOLastSelectedId = null;
    private final String BILLING_SCREEN_NAME = "COMPANY_BILL_CREDIT";
    private final String OTHER_SCREEN_NAME = "COMPANY_OTHER_CREDIT";
    String operationListScreen = "/com/magizhchi/pawnbroking/operationlist/OperationList.fxml";
    private DataTable otherSettingValues;
    
    @FXML
    private TabPane tpScreen;
    @FXML
    private TextField txtDebitId;
    @FXML
    private TextField txtCompanyId;
    @FXML
    private TextField txtCompanyName;
    @FXML
    private DatePicker dpDebittedDate;
    @FXML
    private ComboBox<String> cbBillMaterialType;
    @FXML
    private TextField txtBillNumber;
    @FXML
    private TextField txtCompanyBillStatus;
    @FXML
    private TextField txtCompanyOpenedDate;
    @FXML
    private TextField txtCompanyClosedDate;
    @FXML
    private TextField txtItems;
    @FXML
    private Label lbSpouseType;
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
    private TextField txtGrossWeight;
    @FXML
    private TextField txtNetWeight;
    @FXML
    private TextArea txtNote;
    @FXML
    private TextField txtCompanyBillAmount;
    @FXML
    private TextField txtToGiveAmount;
    @FXML
    private TextField txtGivenAmount;
    @FXML
    private TextField txtToGetAmount;
    @FXML
    private TextField txtGotAmount;
    @FXML
    private TextField txtToDebit;
    @FXML
    private TextField txtDebittedAmount;
    @FXML
    private Label lbMessage;
    @FXML
    private Button btSaveBill;
    @FXML
    private Button btClearAll;
    @FXML
    private TextField txtEODebitId;
    @FXML
    private TextField txtEOCompanyId;
    @FXML
    private TextField txtEOCompanyName;
    @FXML
    private DatePicker dpEODebittedDate;
    @FXML
    private ComboBox<String> cbEOExpenseType;
    @FXML
    private TextField txtEOName;
    @FXML
    private TextArea txtEOReason;
    @FXML
    private TextArea txtEONote;
    @FXML
    private TextField txtEOInvoiceNo;
    @FXML
    private TextField txtEOInvoiceAmount;
    @FXML
    private TextField txtEODebittedAmount;
    @FXML
    private Label lbMessage1;
    @FXML
    private Button btEOSaveBill;
    @FXML
    private Button btEOClearAll;
    @FXML
    private HBox hSaveModeButtons;
    @FXML
    private ToggleButton tgOn;
    @FXML
    private ToggleGroup ViewModeGroup;
    @FXML
    private ToggleButton tgOff;
    @FXML
    private Button btUpdateBill;
    @FXML
    private HBox hSaveModeButtons1;
    @FXML
    private ToggleButton tgEOOn;
    @FXML
    private ToggleGroup ViewModeGroup1;
    @FXML
    private ToggleButton tgEOOff;
    @FXML
    private Button btEOUpdateBill;
    @FXML
    private ComboBox<String> cbAction;
    @FXML
    private ComboBox<String> cbStatus;
    @FXML
    private TextField txtReduceForIdPrefix;
    @FXML
    private TextField txtReduceForId;
    @FXML
    private Tab tabBillAmount;
    @FXML
    private ComboBox<String> cbEOExpenseOrLiability;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            
            try {
                dbOp = new CompanyCreditDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(CompanyCreditController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            setCompanyExpenseTypeValues();
            
            txtDebitId.setText(dbOp.getId(BILLING_SCREEN_NAME));
            txtCompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
            txtCompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
            dpDebittedDate.setValue(LocalDate.now());

            txtEODebitId.setText(dbOp.getId(OTHER_SCREEN_NAME));
            txtEOCompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
            txtEOCompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
            dpEODebittedDate.setValue(LocalDate.now());
            
            txtReduceForIdPrefix.setText(CommonConstants.COMP_DEBIT_CB_PREFIX);
            txtReduceForIdPrefix.setEditable(false);
            txtReduceForIdPrefix.setMouseTransparent(true);
            txtReduceForIdPrefix.setFocusTraversable(false);                                    

            txtReduceForId.setEditable(false);
            txtReduceForId.setMouseTransparent(true);
            txtReduceForId.setFocusTraversable(false);                                    
            
        } catch (SQLException ex) {
            Logger.getLogger(CompanyCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            otherSettingValues = dbOp.getOtherSettingsValues();   
            
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                    CommonConstants.COMPANY_INCOME_SCREEN, "ALLOW_ADD") 
                    || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btSaveBill.setDisable(false);
                btEOSaveBill.setDisable(false);
            } else {
                btSaveBill.setDisable(true);
                btEOSaveBill.setDisable(true);
            }
            
            /*if(dbOp.allowToChangeDate()) {
                dpDebittedDate.setMouseTransparent(false);
                dpDebittedDate.setFocusTraversable(true);                
                dpEODebittedDate.setMouseTransparent(false);
                dpEODebittedDate.setFocusTraversable(true);                
            } else {
                dpDebittedDate.setMouseTransparent(true);
                dpDebittedDate.setFocusTraversable(false);                
                dpEODebittedDate.setMouseTransparent(true);
                dpEODebittedDate.setFocusTraversable(false);                
            }*/
            
            closeDateRestriction();
            closeEODateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }                
    }    

    @FXML
    private void capitalizeCharOnType(KeyEvent e) {
        Platform.runLater(() -> {
            TextField txt_TextField = (TextField) e.getSource();
            int caretPos = txt_TextField.getCaretPosition();
            txt_TextField.setText(txt_TextField.getText().toUpperCase());
            txt_TextField.positionCaret(caretPos);
        });
    }

    @FXML
    private void capitalizeCharOnPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.BACK_SPACE) {
            Platform.runLater(() -> {
                TextField txt_TextField = (TextField) e.getSource();
                int caretPos = txt_TextField.getCaretPosition();
                txt_TextField.setText(txt_TextField.getText().toUpperCase());
                txt_TextField.positionCaret(caretPos);
            });
        }
    }

    @FXML
    private void dpDebittedDateTextChanged(ActionEvent event) {
        try {
            closeDateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(CompanyCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void txtBillNumberOnAction(ActionEvent event) {
        
        String sBillNumber = txtBillNumber.getText();
        String sMaterialType = cbBillMaterialType.getValue();
        btClearAllClicked(null);
        txtBillNumber.setText(sBillNumber);
        
        try {
            HashMap<String, String> headerValues = dbOp.getAllBillingValues(sBillNumber, sMaterialType);
            if(headerValues != null)
            {
                setAllHeaderValuesToFields(headerValues);
                sLastSelectedId = sBillNumber;
                
            } else {
                PopupUtil.showErrorAlert(event, "Sorry invalid bill number.");
                btClearAllClicked(null);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CompanyCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    @FXML
    private void allowNumberAndDotOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();                      
        if(!("0123456789.-".contains(e.getCharacter()))){ 
            e.consume();
        }                
    }

    @FXML
    private void rbToggleChanged(MouseEvent event) {
    }

    @FXML
    private void allowNumberOnlyOnType(KeyEvent event) {
    }

    public void setAllHeaderValuesToFields(HashMap<String, String> headerValues)
    {
            txtBillNumber.setText(headerValues.get("BILL_NUMBER"));
            String sStatus = headerValues.get("STATUS");
            String sClosedDate =  headerValues.get("CLOSING_DATE");
            String sToGetAmount = headerValues.get("TOGET_AMOUNT");
            String sGotAmount = headerValues.get("GOT_AMOUNT");
            
            if("CLOSED".equals(sStatus) || "DELIVERED".equals(sStatus)  || "REBILLED".contains(sStatus)) {
                txtToDebit.setText(Double.toString(Double.parseDouble(sToGetAmount) - Double.parseDouble(sGotAmount)));
            } else {
                sClosedDate = "NOT YET CLOSED";
                sToGetAmount = "NOT YET CLOSED";
                sGotAmount = "NOT YET CLOSED";
                txtToDebit.setText(Double.toString(Double.parseDouble(headerValues.get("TOGIVE_AMOUNT")) - Double.parseDouble(headerValues.get("GIVEN_AMOUNT"))));
            }
            
            txtCompanyBillStatus.setText(sStatus);
            txtCompanyOpenedDate.setText(headerValues.get("OPENING_DATE"));
            txtCompanyClosedDate.setText(sClosedDate);
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
            lbSpouseType.setText(headerValues.get("SPOUSE_TYPE"));
            txtSpouseName.setText(headerValues.get("SPOUSE_NAME"));
            txtDoorNo.setText(headerValues.get("DOOR_NUMBER"));
            txtStreetName.setText(headerValues.get("STREET"));
            txtArea.setText(headerValues.get("AREA"));
            txtCity.setText(headerValues.get("CITY"));
            txtMobileNumber.setText(headerValues.get("MOBILE_NUMBER"));
            txtItems.setText(headerValues.get("ITEMS"));
            txtGrossWeight.setText(headerValues.get("GROSS_WEIGHT"));
            txtNetWeight.setText(headerValues.get("NET_WEIGHT"));
            txtNote.setText(headerValues.get("NOTE"));
            txtCompanyBillAmount.setText(headerValues.get("AMOUNT"));
            txtToGiveAmount.setText(headerValues.get("TOGIVE_AMOUNT"));
            txtGivenAmount.setText(headerValues.get("GIVEN_AMOUNT"));
            txtToGetAmount.setText(sToGetAmount);
            txtGotAmount.setText(sGotAmount);
        try {
            closeDateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(CompanyCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btSaveBillClicked(ActionEvent event) {
                
        if(sLastSelectedId != null) {
            
            String sDebittedDate = CommonConstants.DATETIMEFORMATTER.format(dpDebittedDate.getValue());
            
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sDebittedDate))
            {
            
                String sId = txtDebitId.getText();
                String sMaterialType = cbBillMaterialType.getValue();
                String sStatus = txtCompanyBillStatus.getText().toUpperCase();
                String sBillAmount = txtCompanyBillAmount.getText().toUpperCase();
                String sToDebit = txtToDebit.getText().toUpperCase();
                String sDebit = txtDebittedAmount.getText().toUpperCase();
                String sNote = txtNote.getText().toUpperCase();
                String sIdAction = cbAction.getValue();
                String sIdStatus = cbStatus.getValue();
                String sCreditIdFor = "";
                if(!"NEW".equals(sIdStatus)) {
                    sCreditIdFor = txtReduceForIdPrefix.getText().toUpperCase() + txtReduceForId.getText().toUpperCase();
                }

                double dBillAmount = Double.parseDouble(!("".equals(sBillAmount))? sBillAmount : "0");
                double dToDebit = Double.parseDouble(!("".equals(sToDebit))? sToDebit : "0");  
                double dDebit = Double.parseDouble(!("".equals(sDebit))? sDebit : "0");  

                if(isValidHeaderValues(sDebit)) {
                    try {
                        if(dbOp.saveBillDebit(sId, sMaterialType, sLastSelectedId, sStatus, dBillAmount, sDebittedDate, dToDebit, dDebit, sNote)) {
                            dbOp.setNextId(BILLING_SCREEN_NAME, CommonConstants.COMP_CREDIT_CB_PREFIX + (Integer.parseInt(sId.replace(CommonConstants.COMP_CREDIT_CB_PREFIX, ""))+1));
                            PopupUtil.showInfoAlert(event, "Amount Rs."+dDebit+" credited for "+sMaterialType+" Bill No: "+ sLastSelectedId +".");
                            txtDebitId.setText(dbOp.getId(BILLING_SCREEN_NAME));
                            btClearAllClicked(null);
                        } 
                    } catch (Exception ex) {
                        Logger.getLogger(CompanyCreditController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
                    txtEODebittedAmount.requestFocus();
                }
            } else {
                PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
            }                     
        } else {
            PopupUtil.showErrorAlert(event, "Not any bill number was selected to debit amount.");
        }
    }

    public boolean isValidHeaderValues(String sDebittedAmount)
    {
        if(!sDebittedAmount.isEmpty()) {
            return Double.parseDouble(sDebittedAmount) > 0 || Double.parseDouble(sDebittedAmount) < 0;
        } else {
            return false;
        }
    }

    public boolean isValidCOHeaderValues(String sName, String sReason, String sDebittedAmount)
    {        
        if(!sName.isEmpty() && !sReason.isEmpty() && !sDebittedAmount.isEmpty()) {
            return Double.parseDouble(sDebittedAmount) > 0 || Double.parseDouble(sDebittedAmount) < 0;
        } else {
            return false;
        }
    }
    
    public void clearAllHeader()
    {
        dpDebittedDate.setValue(LocalDate.now());
        txtCompanyBillStatus.setText("");
        txtCompanyClosedDate.setText("");
        txtCompanyOpenedDate.setText("");
        txtCustomerName.setText("");
        lbSpouseType.setText("");
        txtSpouseName.setText("");
        txtDoorNo.setText("");
        txtStreetName.setText("");
        txtArea.setText("");
        txtCity.setText("");
        txtMobileNumber.setText("");
        txtItems.setText("");
        txtGrossWeight.setText("");
        txtNetWeight.setText("");
        txtNote.setText("");        
        txtCompanyBillAmount.setText("");
        txtToGiveAmount.setText("");
        txtGivenAmount.setText("");
        txtToGetAmount.setText("");
        txtGotAmount.setText("");
        txtToDebit.setText("");
        txtDebittedAmount.setText("");
    }

    public void clearEOAllHeader()
    {
        dpDebittedDate.setValue(LocalDate.now());
        txtEOName.setText("");
        txtEOReason.setText("");
        txtEONote.setText("");        
        txtEOInvoiceNo.setText("");
        txtEOInvoiceAmount.setText("");
        txtEODebittedAmount.setText("");
    }
    
    @FXML
    private void btClearAllClicked(ActionEvent event) {
        
        txtBillNumber.setText("");
        clearAllHeader();
        sLastSelectedId = null;        
    }

    @FXML
    private void dpEODebittedDateTextChanged(ActionEvent event) {
        try {
            closeEODateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(CompanyCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btEOSaveBillClicked(ActionEvent event) {

        String sDebittedDate = CommonConstants.DATETIMEFORMATTER.format(dpEODebittedDate.getValue());

        if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sDebittedDate))
        {
        
            String sId = txtEODebitId.getText();
            String sIncomeOrLiability = cbEOExpenseOrLiability.getValue();
            String sExpenseType = cbEOExpenseType.getValue();
            String sName = txtEOName.getText().toUpperCase();
            String sReason = txtEOReason.getText().toUpperCase();
            String sNote = txtEONote.getText().toUpperCase();
            String sInvoiceNo = txtEOInvoiceNo.getText().toUpperCase();
            String sInvoiceAmount = txtEOInvoiceAmount.getText().toUpperCase();
            String sDebittedAmount = txtEODebittedAmount.getText().toUpperCase();

            double dInvoiceAmount = Double.parseDouble(!("".equals(sInvoiceAmount))? sInvoiceAmount : "0");
            double dDebittedAmount = Double.parseDouble(!("".equals(sDebittedAmount))? sDebittedAmount : "0");  

            if(isValidCOHeaderValues(sName, sReason, sDebittedAmount)) {
                try {
                    if(dbOp.saveCOBillDebit(sId, sDebittedDate, sExpenseType, sName, 
                            sReason, sNote, sInvoiceNo, dInvoiceAmount, 
                            dDebittedAmount, sIncomeOrLiability)) {
                        dbOp.setNextId(OTHER_SCREEN_NAME, CommonConstants.COMP_CREDIT_CO_PREFIX + (Integer.parseInt(sId.replace(CommonConstants.COMP_CREDIT_CO_PREFIX, ""))+1));
                        PopupUtil.showInfoAlert(event, "Amount Rs."+sDebittedAmount+" credited for "+sExpenseType+".");
                        txtEODebitId.setText(dbOp.getId(OTHER_SCREEN_NAME));
                        btEOClearAllClicked(null);
                    } 
                } catch (Exception ex) {
                    Logger.getLogger(CompanyCreditController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
                txtDebittedAmount.requestFocus();
            }
        } else {
            PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
        }                             
    }

    @FXML
    private void btEOClearAllClicked(ActionEvent event) {
        clearEOAllHeader();
    }

    @FXML
    private void txtDebitIdOnAction(ActionEvent event) {
        if(tgOff.isSelected()) 
        {
            String sDebitId = txtDebitId.getText();
            sLastSelectedId = sDebitId;
            clearAllHeader();
            
            try {
                HashMap<String, String> headerValues = dbOp.getAllBillingHeaderValues(sDebitId);

                if(headerValues != null)
                {
                    txtDebitId.setText(sDebitId);       
                    dpDebittedDate.setValue(LocalDate.parse(headerValues.get("DEBITTED_DATE"), CommonConstants.DATETIMEFORMATTER));
                    txtToGiveAmount.setText(headerValues.get("TO_BE_DEBITTED_AMOUNT"));
                    txtGivenAmount.setText(headerValues.get("DEBITTED_AMOUNT"));
                    setAllHeaderValuesToFields(headerValues);
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry invalid debit id.");
                    clearAllHeader();
                }

            } catch (SQLException ex) {
                Logger.getLogger(CompanyCreditController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }

    @FXML
    private void saveModeON(ActionEvent event) {
        sLastSelectedId = null;
        clearAllHeader();
        doAllSaveModeONWork();
        try {
            closeDateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(CompanyCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doAllSaveModeONWork() {
    
        try {
            btSaveBill.setDisable(false);
            btUpdateBill.setDisable(true);
            txtDebitId.setEditable(false);
            txtDebitId.setMouseTransparent(true);
            txtDebitId.setFocusTraversable(false);
            txtBillNumber.setEditable(true);
            txtBillNumber.setMouseTransparent(false);
            txtBillNumber.setFocusTraversable(true);    
            cbBillMaterialType.setMouseTransparent(false);
            cbBillMaterialType.setFocusTraversable(true);                
            cbStatus.setMouseTransparent(false);
            cbStatus.setFocusTraversable(true);    
            
            txtDebitId.setText(dbOp.getId(BILLING_SCREEN_NAME));
            txtCompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
            txtCompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
            dpDebittedDate.setValue(LocalDate.now());
                    
            txtGivenAmount.requestFocus();
        } catch (SQLException ex) {
            Logger.getLogger(CompanyCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void saveModeOFF(ActionEvent event) {
        sLastSelectedId = null;
        clearAllHeader();
        doAllSaveModeOFFWork();
        try {
            closeDateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(CompanyCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doAllSaveModeOFFWork() {
    
        btSaveBill.setDisable(true);
        btClearAll.setDisable(true);
        btUpdateBill.setDisable(false);
        txtDebitId.setText("");        
        txtDebitId.setEditable(true);
        txtDebitId.setMouseTransparent(false);
        txtDebitId.setFocusTraversable(true);    
        txtDebitId.setText(CommonConstants.COMP_CREDIT_CB_PREFIX);
        txtBillNumber.setEditable(false);
        txtBillNumber.setMouseTransparent(true);
        txtBillNumber.setFocusTraversable(false);
        cbBillMaterialType.setMouseTransparent(true);
        cbBillMaterialType.setFocusTraversable(false);    
        cbStatus.setMouseTransparent(true);
        cbStatus.setFocusTraversable(false);    
        
        txtDebitId.requestFocus(); 
        txtDebitId.positionCaret(txtDebitId.getText().length());
    }

    @FXML
    private void btUpdateBillClicked(ActionEvent event) {
        
        if(sLastSelectedId != null) {
            
            String sDebittedDate = CommonConstants.DATETIMEFORMATTER.format(dpDebittedDate.getValue());
            
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sDebittedDate))
            {
            
                String sId = txtDebitId.getText();
                String sMaterialType = cbBillMaterialType.getValue();
                String sStatus = txtCompanyBillStatus.getText().toUpperCase();
                String sBillAmount = txtCompanyBillAmount.getText().toUpperCase();
                String sToDebit = txtToDebit.getText().toUpperCase();
                String sDebit = txtDebittedAmount.getText().toUpperCase();
                String sNote = txtNote.getText().toUpperCase();
                String sIdAction = cbAction.getValue();
                String sIdStatus = cbStatus.getValue();
                String sCreditIdFor = "";
                if(!"NEW".equals(sIdStatus)) {
                    sCreditIdFor = txtReduceForIdPrefix.getText().toUpperCase() + txtReduceForId.getText().toUpperCase();
                }

                double dBillAmount = Double.parseDouble(!("".equals(sBillAmount))? sBillAmount : "0");
                double dToDebit = Double.parseDouble(!("".equals(sToDebit))? sToDebit : "0");  
                double dDebit = Double.parseDouble(!("".equals(sDebit))? sDebit : "0");  

                if(isValidHeaderValues(sDebit)) {
                    try {
                        if(dbOp.updateBillDebit(sId, sDebittedDate, dDebit, sMaterialType, sLastSelectedId, sStatus, dBillAmount, dToDebit, sNote)) {
                            PopupUtil.showInfoAlert(event, "Bill Updated successfully.");  
                        } 
                    } catch (Exception ex) {
                        Logger.getLogger(CompanyCreditController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
                }
            } else {
                PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
            }                     
        } else {
            PopupUtil.showErrorAlert(event, "Not any bill number was selected to debit amount.");
        }
    }

    @FXML
    private void txtEODebitIdOnAction(ActionEvent event) {
        
        if(tgEOOff.isSelected()) 
        {
            String sEODebitId = txtEODebitId.getText();
            sEOLastSelectedId = sEODebitId;
            clearEOAllHeader();
            
            try {
                HashMap<String, String> headerValues = dbOp.getAllEOHeaderValues(sEODebitId);

                if(headerValues != null)
                {
                    txtEODebitId.setText(headerValues.get("DEBIT_ID"));       
                    txtEOCompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
                    txtEOCompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
                    dpEODebittedDate.setValue(LocalDate.parse(headerValues.get("DEBITTED_DATE"), CommonConstants.DATETIMEFORMATTER));
                    cbEOExpenseType.setValue(headerValues.get("EXPENSE_TYPE"));
                    txtEOName.setText(headerValues.get("NAME"));       
                    txtEOReason.setText(headerValues.get("REASON"));       
                    txtEONote.setText(headerValues.get("NOTE"));    
                    cbEOExpenseOrLiability.setValue(headerValues.get("income_or_liability"));
                    txtEOInvoiceNo.setText(headerValues.get("INVOICE_NUMBER"));       
                    txtEOInvoiceAmount.setText(headerValues.get("INVOICE_AMOUNT"));             
                    txtEODebittedAmount.setText(headerValues.get("DEBITTED_AMOUNT"));
                    closeEODateRestriction();
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry invalid debit id.");
                    clearEOAllHeader();
                }

            } catch (SQLException ex) {
                Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }

    @FXML
    private void saveModeEOON(ActionEvent event) {
        sEOLastSelectedId = null;
        clearEOAllHeader();
        doEOAllSaveModeONWork();  
        try {
            closeEODateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(CompanyCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doEOAllSaveModeONWork() {
    
        try {
            btEOSaveBill.setDisable(false);
            btEOClearAll.setDisable(false);
            btEOUpdateBill.setDisable(true);
            txtEODebitId.setEditable(false);
            txtEODebitId.setMouseTransparent(true);
            txtEODebitId.setFocusTraversable(false);
            cbEOExpenseType.getSelectionModel().select(0);
            cbEOExpenseType.setMouseTransparent(false);
            cbEOExpenseType.setFocusTraversable(true);
            
            txtEOName.setEditable(true);
            txtEOName.setMouseTransparent(false);
            txtEOName.setFocusTraversable(true);    

            txtEOReason.setEditable(true);
            txtEOReason.setMouseTransparent(false);
            txtEOReason.setFocusTraversable(true);    
            
            txtEODebitId.setText(dbOp.getId(OTHER_SCREEN_NAME));
            txtEOCompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
            txtEOCompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
            dpEODebittedDate.setValue(LocalDate.now());
                    
            txtEODebittedAmount.requestFocus();
        } catch (SQLException ex) {
            Logger.getLogger(CompanyCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void saveModeEOOFF(ActionEvent event) {
        btEOSaveBill.setDisable(false);
        clearEOAllHeader();
        doEOAllSaveModeOFFWork();
        try {
            closeEODateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(CompanyCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doEOAllSaveModeOFFWork() {
    
        btEOSaveBill.setDisable(true);
        btEOClearAll.setDisable(true);
        btEOUpdateBill.setDisable(false);
        txtEODebitId.setText("");        
        txtEODebitId.setEditable(true);
        txtEODebitId.setMouseTransparent(false);
        txtEODebitId.setFocusTraversable(true);    
        cbEOExpenseType.getSelectionModel().select(0);
        cbEOExpenseType.setEditable(false);
        cbEOExpenseType.setMouseTransparent(true);
        cbEOExpenseType.setFocusTraversable(false);        
        txtEODebitId.setText(CommonConstants.COMP_CREDIT_CO_PREFIX);
        
        txtEOName.setEditable(false);
        txtEOName.setMouseTransparent(true);
        txtEOName.setFocusTraversable(false);    

        txtEOReason.setEditable(false);
        txtEOReason.setMouseTransparent(true);
        txtEOReason.setFocusTraversable(false);    
        
        txtEODebitId.requestFocus(); 
        txtEODebitId.positionCaret(txtEODebitId.getText().length());
    }

    @FXML
    private void btEOUpdateBillClicked(ActionEvent event) {
        
        if(sEOLastSelectedId != null) {
                    
            String sDebittedDate = CommonConstants.DATETIMEFORMATTER.format(dpEODebittedDate.getValue());

            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sDebittedDate))
            {
                String sId = txtEODebitId.getText();                
                String sName = txtEOName.getText().toUpperCase();
                String sReason = txtEOReason.getText().toUpperCase();
                String sNote = txtEONote.getText().toUpperCase();
                String sInvoiceNo = txtEOInvoiceNo.getText().toUpperCase();
                String sInvoiceAmount = txtEOInvoiceAmount.getText().toUpperCase();
                String sDebittedAmount = txtEODebittedAmount.getText().toUpperCase();

                double dInvoiceAmount = Double.parseDouble(!("".equals(sInvoiceAmount))? sInvoiceAmount : "0");
                double dDebittedAmount = Double.parseDouble(!("".equals(sDebittedAmount))? sDebittedAmount : "0");  

                if(isValidCOHeaderValues(sName, sReason, sDebittedAmount)) {
                    try {
                        if(dbOp.updateCOBillDebit(sId, sDebittedDate, sNote, sInvoiceNo, dInvoiceAmount, dDebittedAmount)) {
                            PopupUtil.showInfoAlert(event, "Bill Updated successfully.");                    
                        } 
                    } catch (Exception ex) {
                        Logger.getLogger(CompanyCreditController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
                    txtDebittedAmount.requestFocus();
                }
            } else {
                PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
            }  
        } else {
            PopupUtil.showErrorAlert(event, "Not any debit id was selected to update details.");
        }                            
    }

    @FXML
    private void cbActionOnAction(ActionEvent event) {
    }

    @FXML
    private void cbStatusOnAction(ActionEvent event) {
        Platform.runLater(()->{
            int index = cbStatus.getSelectionModel().getSelectedIndex();
            if(index == 0) {              //STATUS IS NEW
                txtReduceForId.setText("");
                sReduceForId = null;
                txtReduceForId.setEditable(false);
                txtReduceForId.setMouseTransparent(true);
                txtReduceForId.setFocusTraversable(false);      
                txtBillNumber.setEditable(true);
                txtBillNumber.setMouseTransparent(false);
                txtBillNumber.setFocusTraversable(true);                      
                cbBillMaterialType.setMouseTransparent(false);
                cbBillMaterialType.setFocusTraversable(true);      
                cbAction.getSelectionModel().select("PENDING TILL REDUCE THIS");
            } else if(index == 1) {      // STATUS IS REDUCE FOR  
                txtReduceForId.setEditable(true);
                txtReduceForId.setMouseTransparent(false);
                txtReduceForId.setFocusTraversable(true);     
                txtBillNumber.setEditable(false);
                txtBillNumber.setMouseTransparent(true);
                txtBillNumber.setFocusTraversable(false);     
                cbBillMaterialType.setMouseTransparent(true);
                cbBillMaterialType.setFocusTraversable(false);      
                cbAction.getSelectionModel().select("DO NOT CONSIDER");
            }
        });                
    }

    @FXML
    private void txtReduceForIdOnAction(ActionEvent event) {
        if(tgOn.isSelected()) 
        {
            String sCreditId = txtReduceForIdPrefix.getText() + txtReduceForId.getText();
            sReduceForId = sCreditId;
            try {
                HashMap<String, String> headerValues = dbOp.getReducedForCompanyBillDebitValues(sCreditId);

                if(headerValues != null)
                {
                    txtToDebit.setText(headerValues.get("TO_BE_DEBITTED_AMOUNT"));
                    txtDebittedAmount.setText(headerValues.get("DEBITTED_AMOUNT"));
                    setAllHeaderValuesToFields(headerValues);
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry invalid credit number.");
                    sReduceForId = null;
                }
            } catch (SQLException ex) {
                Logger.getLogger(CompanyDebitController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {            
        }                
    }

    @FXML
    private void cbEOExpenseTypeOnAction(ActionEvent e) {
        int sIndex = cbEOExpenseType.getSelectionModel().getSelectedIndex();
        CommonConstants.CURRENT_OPERATION = CommonConstants.COMPANY_INCOMES;
        Platform.runLater(()->{        
            if(cbEOExpenseType.getItems().get(sIndex).equals("ADD ITEMS")) {
                Stage dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);        
                FXMLLoader loader = new FXMLLoader(getClass().getResource(operationListScreen));
                Parent root = null;
                try {            
                    root = (Parent) loader.load();            
                } catch (IOException ex) {
                    Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                }

                dialog.setTitle("Operation List Module");      
                dialog.setX(CommonConstants.SCREEN_X);
                dialog.setY(CommonConstants.SCREEN_Y);
                dialog.setWidth(CommonConstants.SCREEN_WIDTH);
                dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
                dialog.setResizable(false);
                Scene scene = new Scene(root);        
                dialog.setScene(scene);
                dialog.initOwner(((Node)e.getSource()).getScene().getWindow() );
                dialog.showAndWait(); 
                setCompanyExpenseTypeValues();
            }
        });    
    }
    
    public void setCompanyExpenseTypeValues() {
        Platform.runLater(()->{
            try {
                cbEOExpenseType.getItems().removeAll(cbEOExpenseType.getItems());
                List<String> lst = dbOp.getCompanyExpenseList(CommonConstants.COMPANY_INCOMES);
                if(!lst.isEmpty()) {
                    lst.stream().forEach((str) -> {          
                        cbEOExpenseType.getItems().add(str);
                    });              
                }
                cbEOExpenseType.getItems().add("ADD ITEMS");
                if(!cbEOExpenseType.getItems().get(0).equals("ADD ITEMS")) {
                    cbEOExpenseType.setValue(cbEOExpenseType.getItems().get(0));
                }
            } catch (SQLException ex) {
                Logger.getLogger(CompanyDebitController.class.getName()).log(Level.SEVERE, null, ex);
            }                
        });
    }
    
    private void closeDateRestriction() throws SQLException {
        
        String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpDebittedDate.getValue());
                
        if(sBillClosingDate != null && otherSettingValues.getRowCount() > 0 && 
            !Boolean.valueOf(otherSettingValues.getRow(0).getColumn(0).toString())) {

            if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                    DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                    sBillClosingDate))
            {                
                if(tgOn.isSelected()) {
                    if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                        CommonConstants.COMPANY_INCOME_SCREEN, "ALLOW_ADD") 
                        || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                        btSaveBill.setDisable(false);
                    } else {
                        btSaveBill.setDisable(true);
                    }
                } else {
                    if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                        CommonConstants.COMPANY_INCOME_SCREEN, "ALLOW_VIEW") 
                        || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                        
                            String sOpeningDate = dbOp.getCompanyCreditOpenedDate(txtDebitId.getText());
                            if(sOpeningDate != null) {
                                String sOpenedDate = CommonConstants.DATETIMEFORMATTER
                                        .format(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
                                if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                                        DateRelatedCalculations.getNextDateWithFormatted(
                                                CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                                        sOpenedDate)) {
                                    btUpdateBill.setDisable(false);
                                }
                            }
                    } else {
                        btUpdateBill.setDisable(true);
                    }                    
                }                    
            } else {
                if(tgOn.isSelected()) {
                    btSaveBill.setDisable(true);
                } else {
                    btUpdateBill.setDisable(true);
                }
            }
        }    
    }            

    private void closeEODateRestriction() throws SQLException {
        
        String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpEODebittedDate.getValue());
                
        if(sBillClosingDate != null && otherSettingValues.getRowCount() > 0 && 
            !Boolean.valueOf(otherSettingValues.getRow(0).getColumn(0).toString())) {

            if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                    DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                    sBillClosingDate))
            {                
                if(tgEOOn.isSelected()) {
                    if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                        CommonConstants.COMPANY_INCOME_SCREEN, "ALLOW_ADD") 
                        || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                        btEOSaveBill.setDisable(false);
                    } else {
                        btEOSaveBill.setDisable(true);
                    }
                } else {
                    if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                        CommonConstants.COMPANY_INCOME_SCREEN, "ALLOW_VIEW") 
                        || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                        
                            String sOpeningDate = dbOp.getEOCompanyCreditOpenedDate(txtEODebitId.getText());
                            if(sOpeningDate != null) {
                                String sOpenedDate = CommonConstants.DATETIMEFORMATTER
                                        .format(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
                                if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                                        DateRelatedCalculations.getNextDateWithFormatted(
                                                CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                                        sOpenedDate)) {
                                    btEOUpdateBill.setDisable(false);
                                }
                            }
                    } else {
                        btEOUpdateBill.setDisable(true);
                    }                    
                }                    
            } else {
                if(tgEOOn.isSelected()) {
                    btEOSaveBill.setDisable(true);
                } else {
                    btEOUpdateBill.setDisable(true);
                }
            }
        }    
    }            

    @FXML
    private void cbEOExpenseOrLiabilityOnAction(ActionEvent event) {
    }

}
