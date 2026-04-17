/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.debit;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.PopupUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class EmployeeDebitController implements Initializable {

    private EmployeeDebitDBOperation dbOp;
    private SimpleDateFormat forMonthYearFormat = new SimpleDateFormat("MMM-YYYY");
    public DataTable dtEmployeeNames;
    public Stage dialog;
    public DataTable dtReduceList = null;
    private String sLastSelectedId = null;    
    private String sSALastSelectedId = null;
    private String sOALastSelectedId = null;
    private DataTable otherSettingValues;
    
    
    private String sAALastSelectedId = null;
    private String sAALastSelectedDate = null;
    private String sAALastSelectedAmount = null;
    
    @FXML
    private TabPane tpScreen;
    @FXML
    private Label lbSpouseType;
    @FXML
    private TextField txtDebitId;
    @FXML
    private TextField txtCompanyId;
    @FXML
    private TextField txtCompanyName;
    @FXML
    private TextField txtEmployeeId;
    @FXML
    private ComboBox<String> cbEMployeeName;
    @FXML
    private DatePicker dpDebittedDate;
    @FXML
    private TextField txtJoinedDate;
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
    private TextArea txtNote;
    @FXML
    private TextField txtJobType;
    @FXML
    private TextField txtSalaryType;
    @FXML
    private TextField txtEmployeType;
    @FXML
    private TextField txtStatus;
    @FXML
    private TextField txtAmountToGive;
    @FXML
    private TextField txtGivenAmount;
    @FXML
    private HBox hSaveModeButtons;
    @FXML
    private ToggleButton tgOn;
    @FXML
    private ToggleGroup ViewModeGroup;
    @FXML
    private ToggleButton tgOff;
    @FXML
    private Button btSaveBill;
    @FXML
    private Button btUpdateBill;
    @FXML
    private Button btClearAll;
    @FXML
    private Label lbAASpouseType;
    @FXML
    private TextField txtAADebitId;
    @FXML
    private TextField txtAACompanyId;
    @FXML
    private TextField txtAACompanyName;
    @FXML
    private TextField txtAAEmployeeId;
    @FXML
    private ComboBox<String> cbAAEMployeeName;
    @FXML
    private DatePicker dpAADebittedDate;
    @FXML
    private TextField txtAAJoinedDate;
    @FXML
    private ToggleGroup rgAAGenderGroup;
    @FXML
    private TextField txtAASpouseName;
    @FXML
    private TextField txtAADoorNo;
    @FXML
    private TextField txtAAStreetName;
    @FXML
    private TextField txtAAArea;
    @FXML
    private TextField txtAACity;
    @FXML
    private TextField txtAAMobileNumber;
    @FXML
    private TextArea txtAANote;
    @FXML
    private TextField txtAAJobType;
    @FXML
    private TextField txtAASalaryType;
    @FXML
    private TextField txtAAEmployeType;
    @FXML
    private TextField txtAAStatus;
    @FXML
    private TextField txtAASalaryAmount;
    @FXML
    private TextArea txtAAReason;
    @FXML
    private TextField txtAAGivenAmount;
    @FXML
    private ComboBox<String> cbAAAction;
    @FXML
    private HBox hSaveModeButtons1;
    @FXML
    private ToggleButton tgAAOn;
    @FXML
    private ToggleGroup ViewModeGroup1;
    @FXML
    private ToggleButton tgAAOff;
    @FXML
    private Button btAASaveBill;
    @FXML
    private Button btAAUpdateBill;
    @FXML
    private Button btAAClearAll;
    @FXML
    private Label lbSASpouseType;
    @FXML
    private TextField txtSADebitId;
    @FXML
    private TextField txtSACompanyId;
    @FXML
    private TextField txtSACompanyName;
    @FXML
    private TextField txtSAEmployeeId;
    @FXML
    private ComboBox<String> cbSAEMployeeName;
    @FXML
    private DatePicker dpSADebittedDate;
    @FXML
    private TextField txtSAJoinedDate;
    @FXML
    private ToggleGroup rgSAGenderGroup;
    @FXML
    private TextField txtSASpouseName;
    @FXML
    private TextField txtSADoorNo;
    @FXML
    private TextField txtSAStreetName;
    @FXML
    private TextField txtSAArea;
    @FXML
    private TextField txtSACity;
    @FXML
    private TextField txtSAMobileNumber;
    @FXML
    private TextArea txtSANote;
    @FXML
    private TextField txtSAJobType;
    @FXML
    private TextField txtSASalaryType;
    @FXML
    private TextField txtSAEmployeType;
    @FXML
    private TextField txtSAStatus;
    @FXML
    private TextField txtSASalaryAmount;
    @FXML
    private TextField txtSATotalAdvanceAmount;
    @FXML
    private TextField txtSAReducedAdvanceAmount;
    @FXML
    private ToggleGroup rgSAActionGroup;
    @FXML
    private TextField txtSAAmountToGive;
    @FXML
    private TextField txtSAGivenAmount;
    @FXML
    private HBox hSaveModeButtons2;
    @FXML
    private ToggleButton tgSAOn;
    @FXML
    private ToggleGroup ViewModeGroup2;
    @FXML
    private ToggleButton tgSAOff;
    @FXML
    private Button btSASaveBill;
    @FXML
    private Button btSAUpdateBill;
    @FXML
    private Button btSAClearAll;
    @FXML
    private Label lbOASpouseType;
    @FXML
    private TextField txtOADebitId;
    @FXML
    private TextField txtOACompanyId;
    @FXML
    private TextField txtOACompanyName;
    @FXML
    private TextField txtOAEmployeeId;
    @FXML
    private ComboBox<String> cbOAEMployeeName;
    @FXML
    private DatePicker dpOADebittedDate;
    @FXML
    private TextField txtOAJoinedDate;
    @FXML
    private ToggleGroup rgOAGenderGroup;
    @FXML
    private TextField txtOASpouseName;
    @FXML
    private TextField txtOADoorNo;
    @FXML
    private TextField txtOAStreetName;
    @FXML
    private TextField txtOAArea;
    @FXML
    private TextField txtOACity;
    @FXML
    private TextField txtOAMobileNumber;
    @FXML
    private TextArea txtOANote;
    @FXML
    private TextField txtOAJobType;
    @FXML
    private TextField txtOASalaryType;
    @FXML
    private TextField txtOAEmployeType;
    @FXML
    private TextField txtOAStatus;
    @FXML
    private TextField txtOASalaryAmount;
    @FXML
    private TextField txtOADailyAllowanceAmount;
    @FXML
    private TextArea txtOAReason;
    @FXML
    private TextField txtOAGivenAmount;
    @FXML
    private HBox hSaveModeButtons3;
    @FXML
    private ToggleButton tgOAOn;
    @FXML
    private ToggleGroup ViewModeGroup3;
    @FXML
    private ToggleButton tgOAOff;
    @FXML
    private Button btOASaveBill;
    @FXML
    private Button btOAUpdateBill;
    @FXML
    private Button btOAClearAll;
    @FXML
    private Label lbMessage;
    @FXML
    private Label lbAAMessage;
    @FXML
    private Label lbSAMessage;
    @FXML
    private Label lbOAMessage;
    @FXML
    private ComboBox<String> cbSalMonth;
    @FXML
    private ComboBox<String> cbSalYear;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            dbOp = new EmployeeDebitDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
        doInitOperation();
        doAAInitOperation();
        doSAInitOperation();
        doOAInitOperation();
        setEmployeeNames();
        
        try {
            otherSettingValues = dbOp.getOtherSettingsValues();   
            
            /*if(dbOp.allowToChangeDate()) {
                dpDebittedDate.setMouseTransparent(false);
                dpDebittedDate.setFocusTraversable(true);                
                dpAADebittedDate.setMouseTransparent(false);
                dpAADebittedDate.setFocusTraversable(true);                
                dpSADebittedDate.setMouseTransparent(false);
                dpSADebittedDate.setFocusTraversable(true);                
                dpOADebittedDate.setMouseTransparent(false);
                dpOADebittedDate.setFocusTraversable(true);                
            } else {
                dpDebittedDate.setMouseTransparent(true);
                dpDebittedDate.setFocusTraversable(false);                
                dpAADebittedDate.setMouseTransparent(true);
                dpAADebittedDate.setFocusTraversable(false);                
                dpSADebittedDate.setMouseTransparent(true);
                dpSADebittedDate.setFocusTraversable(false);                
                dpOADebittedDate.setMouseTransparent(true);
                dpOADebittedDate.setFocusTraversable(false);                            
            }*/
            closeDateRestriction();
            closeAADateRestriction();
            closeSADateRestriction();
            closeOADateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    public void doInitOperation() {    
        try {
            txtDebitId.setText(dbOp.getId());
            txtCompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
            txtCompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
            dpDebittedDate.setValue(LocalDate.now());
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doAAInitOperation() {    
        try {
            cbAAAction.setDisable(true);
            txtAADebitId.setText(dbOp.getAAId());
            txtAACompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
            txtAACompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
            dpAADebittedDate.setValue(LocalDate.now());
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doSAInitOperation() {    
        try {
            txtSADebitId.setText(dbOp.getSAId());
            txtSACompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
            txtSACompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
            dpSADebittedDate.setValue(LocalDate.now());

            Calendar sCurCal = Calendar.getInstance();
            sCurCal.add(Calendar.MONTH, -1);
            Date curDate = sCurCal.getTime();
            String[] sCurMon = forMonthYearFormat.format(curDate).toUpperCase().split("-");                
            cbSalMonth.setValue(sCurMon[0]);
            cbSalYear.setValue(sCurMon[1]);
            cbSalYear.getItems().add(sCurMon[1]);
            cbSalYear.getItems().add(Integer.toString(Integer.parseInt(sCurMon[1]) + 1));
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doOAInitOperation() {    
        try {
            txtOADebitId.setText(dbOp.getOAId());
            txtOACompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
            txtOACompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
            dpOADebittedDate.setValue(LocalDate.now());
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setEmployeeNames() {
        
        try {
            
            cbEMployeeName.getItems().removeAll(cbEMployeeName.getItems());
            cbAAEMployeeName.getItems().removeAll(cbAAEMployeeName.getItems());
            cbSAEMployeeName.getItems().removeAll(cbAAEMployeeName.getItems());
            cbOAEMployeeName.getItems().removeAll(cbAAEMployeeName.getItems());
            
            dtEmployeeNames = dbOp.getAllEmployeeNames();
            
            for(int i=0; i<dtEmployeeNames.getRowCount(); i++) {          
                cbEMployeeName.getItems().add(dtEmployeeNames.getRow(i).getColumn(1).toString());
                cbAAEMployeeName.getItems().add(dtEmployeeNames.getRow(i).getColumn(1).toString());
                cbSAEMployeeName.getItems().add(dtEmployeeNames.getRow(i).getColumn(1).toString());
                cbOAEMployeeName.getItems().add(dtEmployeeNames.getRow(i).getColumn(1).toString());
            }
            
            txtEmployeeId.setText(dtEmployeeNames.getRow(0).getColumn(0).toString());
            txtAAEmployeeId.setText(dtEmployeeNames.getRow(0).getColumn(0).toString());
            txtSAEmployeeId.setText(dtEmployeeNames.getRow(0).getColumn(0).toString());
            txtOAEmployeeId.setText(dtEmployeeNames.getRow(0).getColumn(0).toString());
            
            cbEMployeeName.getSelectionModel().select(0);
            cbAAEMployeeName.getSelectionModel().select(0);
            cbSAEMployeeName.getSelectionModel().select(0);
            cbOAEMployeeName.getSelectionModel().select(0);
            
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
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
    private void cbEmployeeNameOnAction(ActionEvent event) {
        Platform.runLater(()->{
        try {
            int index = cbEMployeeName.getSelectionModel().getSelectedIndex();
            String sRepledgeId = dtEmployeeNames.getRow(index).getColumn(0).toString();
            txtEmployeeId.setText(sRepledgeId);
            if(!"".equals(sRepledgeId)) {
                if(dbOp.isSameDateEntryAvailable(sRepledgeId, CommonConstants.DATETIMEFORMATTER.format(dpDebittedDate.getValue()))) {
                    lbMessage.setText("NOTE: Already today's allowance amount was given to "+ cbEMployeeName.getSelectionModel().getSelectedItem() +".");
                } else {
                    lbMessage.setText("");
                }
                setAllEmployeeValuesToFields(dbOp.getAllEmployeeValues(sRepledgeId));
                closeDateRestriction();
            } else {
                clearAll();
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        });
    }

    public void setAllEmployeeValuesToFields(HashMap<String, String> companyValues) {
        
        if(tgOff.isSelected()) {
            txtEmployeeId.setText(companyValues.get("ID"));
            cbEMployeeName.setValue(companyValues.get("NAME"));
        } else {
            txtGivenAmount.setText("0"); 
        }
        txtJoinedDate.setText(companyValues.get("JOINED_DATE")); 
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
        lbSpouseType.setText(companyValues.get("SPOUSE_TYPE"));
        txtSpouseName.setText(companyValues.get("SPOUSE_NAME"));      
        txtDoorNo.setText(companyValues.get("DOOR_NUMBER"));
        txtStreetName.setText(companyValues.get("STREET"));
        txtArea.setText(companyValues.get("AREA"));
        txtCity.setText(companyValues.get("CITY"));
        txtMobileNumber.setText(companyValues.get("MOBILE_NUMBER"));
        txtJobType.setText(companyValues.get("JOB_TYPE"));
        txtSalaryType.setText(companyValues.get("SALARY_TYPE"));
        txtAmountToGive.setText(companyValues.get("DAILY_ALLOWANCE_AMOUNT"));              
        txtEmployeType.setText(companyValues.get("EMPLOYEE_TYPE"));
        txtStatus.setText(companyValues.get("STATUS"));       
        txtGivenAmount.requestFocus();
        txtGivenAmount.positionCaret(txtGivenAmount.getText().length());
    }
    
    @FXML
    private void dpDebittedDateTextChanged(ActionEvent event) {
        String sEmployeeName = cbEMployeeName.getValue().toUpperCase();
        cbEMployeeName.getSelectionModel().select(0);
        cbEMployeeName.getSelectionModel().select(sEmployeeName);      
        try {
            closeDateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
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
        
        if(!("0123456789.".contains(e.getCharacter()))){ 
            e.consume();
        }
    }

    @FXML
    private void saveModeON(ActionEvent event) {
        sLastSelectedId = null;
        clearAll();
        doAllSaveModeONWork();
        try {
            closeDateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doAllSaveModeONWork() {
    
        btSaveBill.setDisable(false);
        btUpdateBill.setDisable(true);
        txtDebitId.setEditable(false);
        txtDebitId.setMouseTransparent(true);
        txtDebitId.setFocusTraversable(false);  
        cbEMployeeName.getSelectionModel().select(0);
        cbEMployeeName.setMouseTransparent(false);
        cbEMployeeName.setFocusTraversable(true);    
        
        doInitOperation();
        txtGivenAmount.requestFocus();        
    }
    
    @FXML
    private void saveModeOFF(ActionEvent event) {
        sLastSelectedId = null;
        clearAll();
        doAllSaveModeOFFWork();
        try {
            closeDateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
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
        cbEMployeeName.getSelectionModel().select(0);
        cbEMployeeName.setEditable(false);
        cbEMployeeName.setMouseTransparent(true);
        cbEMployeeName.setFocusTraversable(false);        
        txtDebitId.setText(CommonConstants.EMP_DEBIT_DA_PREFIX);
        
        txtDebitId.requestFocus(); 
        txtDebitId.positionCaret(txtDebitId.getText().length());
    }
    
    @FXML
    private void btSaveBillClicked(ActionEvent event) {
        if(!cbEMployeeName.getSelectionModel().isSelected(0)) 
        {
            String sDebittedDate = CommonConstants.DATETIMEFORMATTER.format(dpDebittedDate.getValue());
            
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sDebittedDate))
            {            
                String sId = txtDebitId.getText();
                String sEmployeeId = txtEmployeeId.getText();
                String sEmployeeName = cbEMployeeName.getValue().toUpperCase();
                String sDebittedAmount = txtGivenAmount.getText().toUpperCase();
                String sNote = txtNote.getText().toUpperCase();        

                double dDebittedAmount = Double.parseDouble(!("".equals(sDebittedAmount))? sDebittedAmount : "0");

                if(isValidHeaderValues(sDebittedAmount)) {
                    try {
                        if(dbOp.saveDailyAllowanceDebit(sEmployeeId, sEmployeeName, 
                                sDebittedDate, dDebittedAmount, 
                                sNote, sId)) {
                            dbOp.setNextId(Integer.parseInt(sId.replace(CommonConstants.EMP_DEBIT_DA_PREFIX, ""))+1);
                            PopupUtil.showInfoAlert(event, "Amount Rs."+sDebittedAmount+" debitted for "+sEmployeeName);
                            txtDebitId.setText(dbOp.getId());
                            cbEMployeeName.getSelectionModel().select(0);
                            cbEMployeeName.getSelectionModel().select(sEmployeeName);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
                    txtGivenAmount.requestFocus();
                }
            } else {
                PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
            }                                     
        } else {
            PopupUtil.showInfoAlert(event, "Any of employee should be selected to debit amount.");
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
    
    public boolean isValidHeaderValues(String sDebittedAmount, String sReason)
    {
        if(!sDebittedAmount.isEmpty() && !sReason.isEmpty() ) {
            return Double.parseDouble(sDebittedAmount) > 0 || Double.parseDouble(sDebittedAmount) < 0;
        } else {
            return false;
        }
    }
    
    
    @FXML
    private void btUpdateBillClicked(ActionEvent event) {
        
        String sDebittedDate = CommonConstants.DATETIMEFORMATTER.format(dpDebittedDate.getValue());
        txtDebitId.setText(sLastSelectedId);            
        String sGivenAmount = txtGivenAmount.getText().toUpperCase();
        String sNote = txtNote.getText().toUpperCase();
        double dGivenAmount = Double.parseDouble(!("".equals(sGivenAmount))? sGivenAmount : "0");
        
        if(sLastSelectedId != null) {
            if(isValidUpdateValues(sLastSelectedId, sDebittedDate, sGivenAmount)) 
            {
                if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sDebittedDate)) 
                {
                    try {
                        if(dbOp.updateRecord(sLastSelectedId, sDebittedDate, sNote, dGivenAmount)) {
                            PopupUtil.showInfoAlert(event, "Bill Updated successfully.");
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
                } 
            } else {
                PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
            }
        } else {
            PopupUtil.showErrorAlert(event, "Any one of a daily allowance amount debit id should be given properly.");
        }  
    }

    public boolean isValidUpdateValues(String sId, String sDebittedDate, String sGivenAmount)
    {
        if(!sId.isEmpty() 
            && !sDebittedDate.isEmpty() 
            && !sGivenAmount.isEmpty()) {
            return Double.parseDouble(sGivenAmount) > 0;
        } else {
            return false;
        }
    }

    @FXML
    private void btClearAllClicked(ActionEvent event) {
        clearAll();
    }

    public void clearAll() {
        
        txtJoinedDate.setText("");
        rgGenderGroup.getToggles().get(0).setSelected(true);
        txtSpouseName.setText("");      
        txtDoorNo.setText("");
        txtStreetName.setText("");
        txtArea.setText("");
        txtCity.setText("");
        txtMobileNumber.setText("");
        txtJobType.setText("");
        txtSalaryType.setText("");
        txtAmountToGive.setText("");
        txtGivenAmount.setText("");       
        txtEmployeType.setText("");
        txtStatus.setText("");       
        txtNote.setText("");
        lbMessage.setText("");
    }
    
    @FXML
    private void cbAAEmployeeNameOnAction(ActionEvent event) {
        Platform.runLater(()->{
        try {
            int index = cbAAEMployeeName.getSelectionModel().getSelectedIndex();
            String sRepledgeId = dtEmployeeNames.getRow(index).getColumn(0).toString();
            txtAAEmployeeId.setText(sRepledgeId);
            if(!"".equals(sRepledgeId)) {
                if(dbOp.isAASameDateEntryAvailable(sRepledgeId, CommonConstants.DATETIMEFORMATTER.format(dpAADebittedDate.getValue()))) {
                    lbAAMessage.setText("NOTE: Already advance amount paid for this month to "+ cbAAEMployeeName.getSelectionModel().getSelectedItem() +".");
                } else {
                    lbAAMessage.setText("");
                }
                setAAAllEmployeeValuesToFields(dbOp.getAllEmployeeValues(sRepledgeId));
                closeAADateRestriction();
            } else {
                clearAAAll();
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }                
        });
    }

    public void setAAAllEmployeeValuesToFields(HashMap<String, String> companyValues) {
    
        if(tgAAOff.isSelected()) {
            txtAAEmployeeId.setText(companyValues.get("ID"));
            cbAAEMployeeName.setValue(companyValues.get("NAME"));
        } else {
            txtAAGivenAmount.setText("0"); 
            txtAAReason.setText("");
        }

        txtAAJoinedDate.setText(companyValues.get("JOINED_DATE")); 
        String sGender = companyValues.get("GENDER");
        if(null != sGender) switch (sGender) {
            case "MALE":
                rgAAGenderGroup.getToggles().get(0).setSelected(true);
                break;
            case "FEMALE":
                rgAAGenderGroup.getToggles().get(1).setSelected(true);
                break;
            case "OTHER":
                rgAAGenderGroup.getToggles().get(2).setSelected(true);
                break;
            default:
                break;
        }
        lbAASpouseType.setText(companyValues.get("SPOUSE_TYPE"));
        txtAASpouseName.setText(companyValues.get("SPOUSE_NAME"));      
        txtAADoorNo.setText(companyValues.get("DOOR_NUMBER"));
        txtAAStreetName.setText(companyValues.get("STREET"));
        txtAAArea.setText(companyValues.get("AREA"));
        txtAACity.setText(companyValues.get("CITY"));
        txtAAMobileNumber.setText(companyValues.get("MOBILE_NUMBER"));
        txtAAJobType.setText(companyValues.get("JOB_TYPE"));
        txtAASalaryType.setText(companyValues.get("SALARY_TYPE"));
        txtAASalaryAmount.setText(companyValues.get("SALARY_AMOUNT"));            
        txtAAEmployeType.setText(companyValues.get("EMPLOYEE_TYPE"));
        txtAAStatus.setText(companyValues.get("STATUS"));       
        txtAAGivenAmount.requestFocus();
        txtAAGivenAmount.positionCaret(txtGivenAmount.getText().length());
    }
    
    public void clearAAAll() {
        
        txtAAJoinedDate.setText("");
        rgAAGenderGroup.getToggles().get(0).setSelected(true);
        lbAASpouseType.setText("");
        txtAASpouseName.setText("");      
        txtAADoorNo.setText("");
        txtAAStreetName.setText("");
        txtAAArea.setText("");
        txtAACity.setText("");
        txtAAMobileNumber.setText("");
        txtAAJobType.setText("");
        txtAASalaryType.setText("");
        txtAASalaryAmount.setText("");
        txtAAGivenAmount.setText("");       
        txtAAEmployeType.setText("");
        txtAAReason.setText("");
        txtAAStatus.setText("");       
        txtAANote.setText("");
        lbAAMessage.setText("");
    }
    
    @FXML
    private void dpAADebittedDateTextChanged(ActionEvent event) {
        String sEmployeeName = cbAAEMployeeName.getValue().toUpperCase();
        cbAAEMployeeName.getSelectionModel().select(0);
        cbAAEMployeeName.getSelectionModel().select(sEmployeeName);
        try {
            closeAADateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void saveModeAAON(ActionEvent event) {
        sAALastSelectedId = null;
        sAALastSelectedDate = null;
        sAALastSelectedAmount = null;
        
        clearAAAll();
        doAAAllSaveModeONWork();   
        try {
            closeAADateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doAAAllSaveModeONWork() {
    
        btAASaveBill.setDisable(false);
        btAAUpdateBill.setDisable(true);
        txtAADebitId.setEditable(false);
        txtAADebitId.setMouseTransparent(true);
        txtAADebitId.setFocusTraversable(false);  
        cbAAEMployeeName.getSelectionModel().select(0);
        cbAAEMployeeName.setMouseTransparent(false);
        cbAAEMployeeName.setFocusTraversable(true);    
        
        doAAInitOperation();
        txtAAGivenAmount.requestFocus();        
    }
    
    @FXML
    private void saveModeAAOFF(ActionEvent event) {
        sAALastSelectedId = null;
        sAALastSelectedDate = null;
        sAALastSelectedAmount = null;
        
        clearAAAll();
        doAAAllSaveModeOFFWork();
    }

    public void doAAAllSaveModeOFFWork() {
    
        btAASaveBill.setDisable(true);
        btAAClearAll.setDisable(true);
        btAAUpdateBill.setDisable(false);
        txtAADebitId.setText("");        
        txtAADebitId.setEditable(true);
        txtAADebitId.setMouseTransparent(false);
        txtAADebitId.setFocusTraversable(true);    
        txtAADebitId.setText(CommonConstants.EMP_DEBIT_AA_PREFIX);
        
        txtAADebitId.requestFocus(); 
        txtAADebitId.positionCaret(txtAADebitId.getText().length());
    }
    
    @FXML
    private void btAASaveBillClicked(ActionEvent event) 
    {
        if(!cbAAEMployeeName.getSelectionModel().isSelected(0)) 
        {            
            String sDebittedDate = CommonConstants.DATETIMEFORMATTER.format(dpAADebittedDate.getValue());
            
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sDebittedDate))
            {            
                String sId = txtAADebitId.getText();
                String sEmployeeId = txtAAEmployeeId.getText();
                String sEmployeeName = cbAAEMployeeName.getValue().toUpperCase();
                String sReason = txtAAReason.getText().toUpperCase();
                String sDebittedAmount = txtAAGivenAmount.getText().toUpperCase();
                String sNote = txtAANote.getText().toUpperCase();        
                String sAction = cbAAAction.getValue().toUpperCase();

                double dDebittedAmount = Double.parseDouble(!("".equals(sDebittedAmount))? sDebittedAmount : "0");


                if(isValidHeaderValues(sDebittedAmount, sReason)) {
                    try {
                        if(dbOp.saveAdvanceAmountDebit(sId, sEmployeeId, sEmployeeName, sDebittedDate, dDebittedAmount, sNote, sAction, sReason)) {
                            dbOp.setAANextId(Integer.parseInt(sId.replace(CommonConstants.EMP_DEBIT_AA_PREFIX, ""))+1);
                            PopupUtil.showInfoAlert(event, "Amount Rs."+sDebittedAmount+" debitted for "+sEmployeeName);
                            txtAADebitId.setText(dbOp.getAAId());
                            cbAAEMployeeName.getSelectionModel().select(0);
                            cbAAEMployeeName.getSelectionModel().select(sEmployeeName);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
                    txtAAGivenAmount.requestFocus();
                }
            } else {
                PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
            }                                                     
        } else {
            PopupUtil.showInfoAlert(event, "Any of employee should be selected to debit amount.");
        }                
    }

    @FXML
    private void btAAUpdateBillClicked(ActionEvent event) 
    {
        String sDebittedDate = CommonConstants.DATETIMEFORMATTER.format(dpAADebittedDate.getValue());
        txtAADebitId.setText(sAALastSelectedId);         
        String sEmployeeId = txtAAEmployeeId.getText();
        String sEmployeeName = cbAAEMployeeName.getValue().toUpperCase();        
        String sGivenAmount = txtAAGivenAmount.getText().toUpperCase();
        String sNote = txtAANote.getText().toUpperCase();
        String sReason = txtAAReason.getText().toUpperCase();
        String sAction = cbAAAction.getValue().toUpperCase();
        
        double dGivenAmount = Double.parseDouble(!("".equals(sGivenAmount))? sGivenAmount : "0");
        
        if(sAALastSelectedId != null) 
        {
            if(isValidUpdateValues(sAALastSelectedId, sDebittedDate, sGivenAmount, sReason)) 
            {
                if((DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, 
                        sDebittedDate) && !sDebittedDate.equals(sAALastSelectedDate) 
                        && !sGivenAmount.equals(sAALastSelectedAmount))
                        || (sDebittedDate.equals(sAALastSelectedDate) 
                        && sGivenAmount.equals(sAALastSelectedAmount))) 
                {
                    try {
                        if(dbOp.updateAARecord(sAALastSelectedId, sDebittedDate, sNote, 
                                dGivenAmount, sReason, sAction, sEmployeeId, sEmployeeName)) {
                            PopupUtil.showInfoAlert(event, "Bill Updated successfully.");
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
                } 
            } else {
                PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
            }
        } else {
            PopupUtil.showErrorAlert(event, "Any one of a advance amount debit id should be given properly.");
        }  
    }

    @FXML
    private void btAAClearAllClicked(ActionEvent event) {
        clearAAAll();
    }

    @FXML
    private void cbSAEmployeeNameOnAction(ActionEvent event) {
        Platform.runLater(()->{
            if(tgSAOn.isSelected()) {
        try {            
            int index = cbSAEMployeeName.getSelectionModel().getSelectedIndex();
            String sEmployeeId = dtEmployeeNames.getRow(index).getColumn(0).toString();
            txtSAEmployeeId.setText(sEmployeeId);
            rgSAActionGroup.getToggles().get(1).setSelected(true);
            dtReduceList = null;
            if(!"".equals(sEmployeeId)) {
                if(dbOp.isSASameDateEntryAvailable(sEmployeeId, CommonConstants.DATETIMEFORMATTER.format(dpSADebittedDate.getValue()))) {
                    lbSAMessage.setText("NOTE: Already salary paid for this month to "+ cbSAEMployeeName.getSelectionModel().getSelectedItem() +".");
                } else {
                    lbSAMessage.setText("");
                }
                setSAAllEmployeeValuesToFields(dbOp.getAllEmployeeValues(sEmployeeId), dbOp.getTotalAdavanceAmount(sEmployeeId));
                closeSADateRestriction();
            } else {
                clearSAAll();
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }                       
            }
        });
    }

    public void clearSAAll() {
        
        txtSAJoinedDate.setText("");
        rgSAGenderGroup.getToggles().get(0).setSelected(true);
        lbSASpouseType.setText("");
        txtSASpouseName.setText("");      
        txtSADoorNo.setText("");
        txtSAStreetName.setText("");
        txtSAArea.setText("");
        txtSACity.setText("");
        txtSAMobileNumber.setText("");
        txtSAJobType.setText("");
        txtSASalaryType.setText("");
        txtSASalaryAmount.setText("");
        txtSAGivenAmount.setText("");       
        txtSAEmployeType.setText("");
        txtSAStatus.setText("");       
        txtSANote.setText("");
        txtSATotalAdvanceAmount.setText("");
        txtSAReducedAdvanceAmount.setText("");
        txtSAAmountToGive.setText("");
        txtSAGivenAmount.setText("");
        lbSAMessage.setText("");
    }
    
    public void setSAAllEmployeeValuesToFields(HashMap<String, String> companyValues, String totalAdvanceAmount) {
    
        if(tgSAOff.isSelected()) {
            txtSAEmployeeId.setText(companyValues.get("ID"));
            cbSAEMployeeName.setValue(companyValues.get("NAME"));
            txtSATotalAdvanceAmount.setText(totalAdvanceAmount);
            if(((RadioButton) rgSAActionGroup.getSelectedToggle()).getText().toUpperCase().equals("REDUCE")) {
                txtSAReducedAdvanceAmount.setText(totalAdvanceAmount);
            }
        } else {
            txtSAGivenAmount.setText("0"); 
            txtSAReducedAdvanceAmount.setText("0.0");
            if(((RadioButton) rgSAActionGroup.getSelectedToggle()).getText().toUpperCase().equals("REDUCE")) {
                String sAmountToGive = Double.toString(Double.parseDouble(companyValues.get("SALARY_AMOUNT")) - Double.parseDouble(totalAdvanceAmount));
                txtSAAmountToGive.setText(sAmountToGive);
            } else {
                txtSAAmountToGive.setText(companyValues.get("SALARY_AMOUNT"));
            }            
        }

        txtSAJoinedDate.setText(companyValues.get("JOINED_DATE")); 
        String sGender = companyValues.get("GENDER");
        if(null != sGender) switch (sGender) {
            case "MALE":
                rgSAGenderGroup.getToggles().get(0).setSelected(true);
                break;
            case "FEMALE":
                rgSAGenderGroup.getToggles().get(1).setSelected(true);
                break;
            case "OTHER":
                rgSAGenderGroup.getToggles().get(2).setSelected(true);
                break;
            default:
                break;
        }
        lbSASpouseType.setText(companyValues.get("SPOUSE_TYPE"));
        txtSASpouseName.setText(companyValues.get("SPOUSE_NAME"));      
        txtSADoorNo.setText(companyValues.get("DOOR_NUMBER"));
        txtSAStreetName.setText(companyValues.get("STREET"));
        txtSAArea.setText(companyValues.get("AREA"));
        txtSACity.setText(companyValues.get("CITY"));
        txtSAMobileNumber.setText(companyValues.get("MOBILE_NUMBER"));
        txtSAJobType.setText(companyValues.get("JOB_TYPE"));
        txtSASalaryType.setText(companyValues.get("SALARY_TYPE"));
        txtSASalaryAmount.setText(companyValues.get("SALARY_AMOUNT"));
        txtSAEmployeType.setText(companyValues.get("EMPLOYEE_TYPE"));
        txtSAStatus.setText(companyValues.get("STATUS")); 
        
        txtSATotalAdvanceAmount.setText(totalAdvanceAmount);                        
        txtSAGivenAmount.requestFocus();
    }
    
    @FXML
    private void dpSADebittedDateTextChanged(ActionEvent event) {
        String sEmployeeName = cbSAEMployeeName.getValue().toUpperCase();
        cbSAEMployeeName.getSelectionModel().select(0);
        cbSAEMployeeName.getSelectionModel().select(sEmployeeName);       
        try {
            closeSADateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void rbSAActionToggleChanged(MouseEvent event) {
        if(!cbSAEMployeeName.getSelectionModel().isSelected(0)) 
        {
            if(((RadioButton) rgSAActionGroup.getSelectedToggle()).getText().toUpperCase().equals("REDUCE")) {
                if(dtReduceList == null) {
                    try {
                        dtReduceList = dbOp.getAllReduceList(txtSAEmployeeId.getText());
                    } catch (SQLException ex) {
                        Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                showReduceDialog(event, dtReduceList);
                
                double dReducedAmount = 0.0;
                for(int i=0; i<dtReduceList.getRowCount(); i++) {
                    if(Boolean.parseBoolean(dtReduceList.getRow(i).getColumn(5).toString())) {
                        dReducedAmount += Double.parseDouble(dtReduceList.getRow(i).getColumn(4).toString());
                    }
                }
                txtSAReducedAdvanceAmount.setText(Double.toString(dReducedAmount));
                String sAmountToGive = Double.toString(Double.parseDouble(txtSASalaryAmount.getText()) - dReducedAmount);
                txtSAAmountToGive.setText(sAmountToGive);
            } else {
                txtSAReducedAdvanceAmount.setText("0.0");
                txtSAAmountToGive.setText(txtSASalaryAmount.getText());
            }
            txtSAGivenAmount.requestFocus();
        }        
    }

    private void showReduceDialog(MouseEvent event, DataTable dtReduceList) {
        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeeAdvanceAmountReductionDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        EmployeeAdvanceAmountReductionDialogUIController gon = (EmployeeAdvanceAmountReductionDialogUIController) loader.getController();
        gon.setParent(this);
        gon.setInitValus(dtReduceList);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        dialog.setX(100);
        dialog.setY(100);            
        dialog.setTitle("Advance Amount List");
        dialog.setResizable(false);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();
    }
    
    @FXML
    private void saveModeSAON(ActionEvent event) {
        sSALastSelectedId = null;
        clearSAAll();
        doSAAllSaveModeONWork();
    }

    public void doSAAllSaveModeONWork() {
    
        btSASaveBill.setDisable(false);
        btSAUpdateBill.setDisable(true);
        txtSADebitId.setEditable(false);
        txtSADebitId.setMouseTransparent(true);
        txtSADebitId.setFocusTraversable(false);  
        cbSAEMployeeName.getSelectionModel().select(0);
        cbSAEMployeeName.setMouseTransparent(false);
        cbSAEMployeeName.setFocusTraversable(true);    
        
        doSAInitOperation();
        txtSAGivenAmount.requestFocus();        
    }

    @FXML
    private void saveModeSAOFF(ActionEvent event) {
        sSALastSelectedId = null;
        clearSAAll();
        doSAAllSaveModeOFFWork();
        try {
            closeSADateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doSAAllSaveModeOFFWork() {
    
        btSASaveBill.setDisable(true);
        btSAClearAll.setDisable(true);
        btSAUpdateBill.setDisable(false);
        txtSADebitId.setText("");        
        txtSADebitId.setEditable(true);
        txtSADebitId.setMouseTransparent(false);
        txtSADebitId.setFocusTraversable(true);    
        cbSAEMployeeName.getSelectionModel().select(0);
        cbSAEMployeeName.setEditable(false);
        cbSAEMployeeName.setMouseTransparent(true);
        cbSAEMployeeName.setFocusTraversable(false);        
        txtSADebitId.setText(CommonConstants.EMP_DEBIT_SA_PREFIX);
        
        txtSADebitId.requestFocus(); 
        txtSADebitId.positionCaret(txtSADebitId.getText().length());
    }

    @FXML
    private void btSASaveBillClicked(ActionEvent event) 
    {
        if(!cbSAEMployeeName.getSelectionModel().isSelected(0)) 
        {
            String sDebittedDate = CommonConstants.DATETIMEFORMATTER.format(dpSADebittedDate.getValue());
            
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sDebittedDate))
            {            
                String sId = txtSADebitId.getText();
                String sEmployeeId = txtSAEmployeeId.getText();
                String sEmployeeName = cbSAEMployeeName.getValue().toUpperCase();
                String sTotalAdvanceAmount = txtSATotalAdvanceAmount.getText();
                String sAction = ((RadioButton) rgSAActionGroup.getSelectedToggle()).getText().toUpperCase();
                String sAmountToGive = txtSAAmountToGive.getText();
                String sDebittedAmount = txtSAGivenAmount.getText().toUpperCase();
                String sNote = txtSANote.getText().toUpperCase();        
                String sSalForMonth = cbSalMonth.getValue().toUpperCase() + "-" + cbSalYear.getValue();

                double dTotalAdvanceAmount = Double.parseDouble(!("".equals(sTotalAdvanceAmount))? sTotalAdvanceAmount : "0");
                double dAmountToGive = Double.parseDouble(!("".equals(sAmountToGive))? sAmountToGive : "0");
                double dDebittedAmount = Double.parseDouble(!("".equals(sDebittedAmount))? sDebittedAmount : "0");

                if(isValidHeaderValues(sDebittedAmount)) {
                    try {
                        if(dbOp.saveSalaryAmountDebit(sId, sEmployeeId, sEmployeeName, sDebittedDate, 
                                dTotalAdvanceAmount, dAmountToGive, dDebittedAmount, 
                                sSalForMonth, sNote, sAction)) {
                            if("REDUCE".equals(sAction)) {
                                for(int i=0; i<dtReduceList.getRowCount(); i++) {
                                    if(Boolean.parseBoolean(dtReduceList.getRow(i).getColumn(5).toString())) {
                                        dbOp.updateSAAllAdvanceAmountToReducedAction(sId, dtReduceList.getRow(i).getColumn(1).toString(), sEmployeeId);
                                    }
                                }
                            }
                            dbOp.setSANextId(Integer.parseInt(sId.replace(CommonConstants.EMP_DEBIT_SA_PREFIX, ""))+1);
                            PopupUtil.showInfoAlert(event, "Amount Rs."+sDebittedAmount+" debitted for "+sEmployeeName);
                            txtSADebitId.setText(dbOp.getSAId());
                            cbSAEMployeeName.getSelectionModel().select(0);
                            cbSAEMployeeName.getSelectionModel().select(sEmployeeName);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
                    txtAAGivenAmount.requestFocus();
                }
            } else {
                PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
            }                                                                     
        } else {
            PopupUtil.showInfoAlert(event, "Any of employee should be selected to debit amount.");
        }                
    }

    @FXML
    private void btSAUpdateBillClicked(ActionEvent event) {
        
        String sDebittedDate = CommonConstants.DATETIMEFORMATTER.format(dpSADebittedDate.getValue());
        txtSADebitId.setText(sSALastSelectedId);            
        String sGivenAmount = txtSAGivenAmount.getText().toUpperCase();
        String sNote = txtSANote.getText().toUpperCase();
        String sSalForMonth = cbSalMonth.getValue().toUpperCase() + "-" + cbSalYear.getValue();
        
        double dGivenAmount = Double.parseDouble(!("".equals(sGivenAmount))? sGivenAmount : "0");
        
        if(sSALastSelectedId != null) 
        {
            if(isValidUpdateValues(sSALastSelectedId, sDebittedDate, sGivenAmount)) 
            {
                if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sDebittedDate)) 
                {
                    try {
                        if(dbOp.updateSARecord(sSALastSelectedId, sDebittedDate, sNote, dGivenAmount, sSalForMonth)) {
                            PopupUtil.showInfoAlert(event, "Bill Updated successfully.");
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
                } 
            } else {
                PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
            }
        } else {
            PopupUtil.showErrorAlert(event, "Any one of a salary amount debit id should be given properly.");
        }  
    }

    @FXML
    private void btSAClearAllClicked(ActionEvent event) {
        clearSAAll();
    }

    @FXML
    private void cbOAEmployeeNameOnAction(ActionEvent event) {
    
            Platform.runLater(()->{
        try {
            int index = cbOAEMployeeName.getSelectionModel().getSelectedIndex();
            String sRepledgeId = dtEmployeeNames.getRow(index).getColumn(0).toString();
            txtOAEmployeeId.setText(sRepledgeId);
            if(!"".equals(sRepledgeId)) {
                if(dbOp.isOASameDateEntryAvailable(sRepledgeId, CommonConstants.DATETIMEFORMATTER.format(dpOADebittedDate.getValue()))) {
                    lbOAMessage.setText("NOTE: Already other amount debitted for "+ cbOAEMployeeName.getSelectionModel().getSelectedItem() +" in the same day.");
                } else {
                    lbOAMessage.setText("");
                }
                setOAAllEmployeeValuesToFields(dbOp.getAllEmployeeValues(sRepledgeId));
                closeOADateRestriction();
            } else {
                clearOAAll();
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        });
    }
    
    public void setOAAllEmployeeValuesToFields(HashMap<String, String> companyValues) {
    
        if(tgOAOff.isSelected()) {
            txtOAEmployeeId.setText(companyValues.get("ID"));
            cbOAEMployeeName.setValue(companyValues.get("NAME"));
        } else {
            txtOAGivenAmount.setText("0"); 
            txtOAReason.setText("");
        }

        txtOAJoinedDate.setText(companyValues.get("JOINED_DATE")); 
        String sGender = companyValues.get("GENDER");
        if(null != sGender) switch (sGender) {
            case "MALE":
                rgOAGenderGroup.getToggles().get(0).setSelected(true);
                break;
            case "FEMALE":
                rgOAGenderGroup.getToggles().get(1).setSelected(true);
                break;
            case "OTHER":
                rgOAGenderGroup.getToggles().get(2).setSelected(true);
                break;
            default:
                break;
        }
        lbOASpouseType.setText(companyValues.get("SPOUSE_TYPE"));
        txtOASpouseName.setText(companyValues.get("SPOUSE_NAME"));      
        txtOADoorNo.setText(companyValues.get("DOOR_NUMBER"));
        txtOAStreetName.setText(companyValues.get("STREET"));
        txtOAArea.setText(companyValues.get("AREA"));
        txtOACity.setText(companyValues.get("CITY"));
        txtOAMobileNumber.setText(companyValues.get("MOBILE_NUMBER"));
        txtOAJobType.setText(companyValues.get("JOB_TYPE"));
        txtOASalaryType.setText(companyValues.get("SALARY_TYPE"));
        txtOASalaryAmount.setText(companyValues.get("SALARY_AMOUNT"));
        txtOADailyAllowanceAmount.setText(companyValues.get("DAILY_ALLOWANCE_AMOUNT"));
        txtOAEmployeType.setText(companyValues.get("EMPLOYEE_TYPE"));
        txtOAStatus.setText(companyValues.get("STATUS"));       
        txtOAGivenAmount.requestFocus();
        txtOAGivenAmount.positionCaret(txtOAGivenAmount.getText().length());
    }
    
    public void clearOAAll() {
        
        txtOAJoinedDate.setText("");
        rgOAGenderGroup.getToggles().get(0).setSelected(true);
        lbOASpouseType.setText("");
        txtOASpouseName.setText("");      
        txtOADoorNo.setText("");
        txtOAStreetName.setText("");
        txtOAArea.setText("");
        txtOACity.setText("");
        txtOAMobileNumber.setText("");
        txtOAJobType.setText("");
        txtOASalaryType.setText("");
        txtOASalaryAmount.setText("");
        txtOADailyAllowanceAmount.setText("");
        txtOAGivenAmount.setText("");       
        txtOAEmployeType.setText("");
        txtOAReason.setText("");
        txtOAStatus.setText("");       
        txtOANote.setText("");
        lbOAMessage.setText("");
    }
    
    @FXML
    private void dpOADebittedDateTextChanged(ActionEvent event) {
        String sEmployeeName = cbOAEMployeeName.getValue().toUpperCase();
        cbOAEMployeeName.getSelectionModel().select(0);
        cbOAEMployeeName.getSelectionModel().select(sEmployeeName);     
        try {
            closeOADateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void saveModeOAON(ActionEvent event) {
        sOALastSelectedId = null;
        clearOAAll();
        doOAAllSaveModeONWork();      
        try {
            closeOADateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doOAAllSaveModeONWork() {
    
        btOASaveBill.setDisable(false);
        btOAUpdateBill.setDisable(true);
        txtOADebitId.setEditable(false);
        txtOADebitId.setMouseTransparent(true);
        txtOADebitId.setFocusTraversable(false);  
        cbOAEMployeeName.getSelectionModel().select(0);
        cbOAEMployeeName.setMouseTransparent(false);
        cbOAEMployeeName.setFocusTraversable(true);    
        
        doOAInitOperation();
        txtOAGivenAmount.requestFocus();        
    }

    @FXML
    private void saveModeOAOFF(ActionEvent event) {
        sOALastSelectedId = null;
        clearOAAll();
        doOAAllSaveModeOFFWork();
        try {
            closeOADateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doOAAllSaveModeOFFWork() {
    
        btOASaveBill.setDisable(true);
        btOAClearAll.setDisable(true);
        btOAUpdateBill.setDisable(false);
        txtOADebitId.setText("");        
        txtOADebitId.setEditable(true);
        txtOADebitId.setMouseTransparent(false);
        txtOADebitId.setFocusTraversable(true);    
        cbOAEMployeeName.getSelectionModel().select(0);
        cbOAEMployeeName.setEditable(false);
        cbOAEMployeeName.setMouseTransparent(true);
        cbOAEMployeeName.setFocusTraversable(false);        
        txtOADebitId.setText(CommonConstants.EMP_DEBIT_OA_PREFIX);
        
        txtOADebitId.requestFocus(); 
        txtOADebitId.positionCaret(txtOADebitId.getText().length());
    }

    @FXML
    private void btOASaveBillClicked(ActionEvent event) {
        if(!cbOAEMployeeName.getSelectionModel().isSelected(0)) 
        {
            String sDebittedDate = CommonConstants.DATETIMEFORMATTER.format(dpOADebittedDate.getValue());
            
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sDebittedDate))
            {            
                String sId = txtOADebitId.getText();
                String sEmployeeId = txtOAEmployeeId.getText();
                String sEmployeeName = cbOAEMployeeName.getValue().toUpperCase();
                String sDebittedAmount = txtOAGivenAmount.getText().toUpperCase();
                String sNote = txtOANote.getText().toUpperCase();        
                String sReason = txtOAReason.getText().toUpperCase();

                double dDebittedAmount = Double.parseDouble(!("".equals(sDebittedAmount))? sDebittedAmount : "0");

                if(isValidHeaderValues(sDebittedAmount) && !"".equals(sReason)) {
                    try {
                        if(dbOp.saveOtherAmountDebit(sId, sEmployeeId, sEmployeeName, sDebittedDate, dDebittedAmount, sNote, sReason)) {
                            dbOp.setOANextId(Integer.parseInt(sId.replace(CommonConstants.EMP_DEBIT_OA_PREFIX, ""))+1);
                            PopupUtil.showInfoAlert(event, "Amount Rs."+sDebittedAmount+" debitted for "+sEmployeeName);
                            txtOADebitId.setText(dbOp.getOAId());
                            cbOAEMployeeName.getSelectionModel().select(0);
                            cbOAEMployeeName.getSelectionModel().select(sEmployeeName);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
                    txtOAGivenAmount.requestFocus();
                }
            } else {
                PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
            }                                                                                     
        } else {
            PopupUtil.showInfoAlert(event, "Any of employee should be selected to debit amount.");
        }                
    }

    @FXML
    private void btOAUpdateBillClicked(ActionEvent event) {
    
        String sDebittedDate = CommonConstants.DATETIMEFORMATTER.format(dpOADebittedDate.getValue());
        txtOADebitId.setText(sOALastSelectedId);            
        String sGivenAmount = txtOAGivenAmount.getText().toUpperCase();
        String sNote = txtOANote.getText().toUpperCase();
        String sReason = txtOAReason.getText().toUpperCase();
        
        double dGivenAmount = Double.parseDouble(!("".equals(sGivenAmount))? sGivenAmount : "0");
        
        if(sOALastSelectedId != null) 
        {
            if(isValidUpdateValues(sOALastSelectedId, sDebittedDate, sGivenAmount, sReason)) 
            {
                if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sDebittedDate)) 
                {
                    try {
                        if(dbOp.updateOARecord(sOALastSelectedId, sDebittedDate, sNote, dGivenAmount, sReason)) {
                            PopupUtil.showInfoAlert(event, "Bill Updated successfully.");
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
                } 
            } else {
                PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
            }
        } else {
            PopupUtil.showErrorAlert(event, "Any one of a advance amount debit id should be given properly.");
        }  
    }

    @FXML
    private void btOAClearAllClicked(ActionEvent event) {
        clearOAAll();
    }

    @FXML
    private void txtDebitIdOnAction(ActionEvent event) {
        
        if(tgOff.isSelected()) 
        {
            String sDebitId = txtDebitId.getText();
            sLastSelectedId = sDebitId;
            clearAll();
            
            try {
                HashMap<String, String> headerValues = dbOp.getAllHeaderValues(sDebitId);

                if(headerValues != null)
                {
                    txtDebitId.setText(headerValues.get("ID"));       
                    dpDebittedDate.setValue(LocalDate.parse(headerValues.get("DEBITTED_DATE"), CommonConstants.DATETIMEFORMATTER));
                    txtGivenAmount.setText(headerValues.get("DEBITTED_AMOUNT"));
                    setAllEmployeeValuesToFields(dbOp.getAllEmployeeValues(headerValues.get("EMPLOYEE_ID")));
                    closeDateRestriction();
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry invalid bill number.");
                    clearAll();
                }

            } catch (SQLException ex) {
                Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void txtAADebitIdOnAction(ActionEvent event) {
        if(tgAAOff.isSelected()) 
        {
            String sDebitId = txtAADebitId.getText();
            sAALastSelectedId = sDebitId;
            clearAAAll();
            
            try {
                HashMap<String, String> headerValues = dbOp.getAAAllHeaderValues(sDebitId);

                if(headerValues != null)
                {
                    txtAADebitId.setText(headerValues.get("ID"));                 
                    dpAADebittedDate.setValue(LocalDate.parse(headerValues.get("DEBITTED_DATE"), CommonConstants.DATETIMEFORMATTER));
                    txtAAGivenAmount.setText(headerValues.get("DEBITTED_AMOUNT"));
                    cbAAAction.setValue(headerValues.get("DEBITTED_ACTION"));
                    txtAANote.setText(headerValues.get("NOTE"));
                    txtAAReason.setText(headerValues.get("REASON"));
                    setAAAllEmployeeValuesToFields(dbOp.getAllEmployeeValues(headerValues.get("EMPLOYEE_ID")));
                    
                    sAALastSelectedDate = headerValues.get("DEBITTED_DATE");
                    sAALastSelectedAmount = headerValues.get("DEBITTED_AMOUNT");
                    closeAADateRestriction();
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry invalid bill number.");
                    clearAAAll();
                }

            } catch (SQLException ex) {
                Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean isValidUpdateValues(String sId, String sDebittedDate, String sGivenAmount, String sReason) {
        if(!sId.isEmpty() 
            && !sDebittedDate.isEmpty() 
            && !sGivenAmount.isEmpty()
            && !sReason.isEmpty()) {
            return Double.parseDouble(sGivenAmount) >= 0;
        } else {
            return false;
        }
    }

    @FXML
    private void txtSADebitIdOnAction(ActionEvent event) {
        if(tgSAOff.isSelected()) 
        {
            String sDebitId = txtSADebitId.getText();
            sSALastSelectedId = sDebitId;
            clearSAAll();
            
            try {
                HashMap<String, String> headerValues = dbOp.getSAAllHeaderValues(sDebitId);

                if(headerValues != null)
                {
                    txtSADebitId.setText(headerValues.get("ID"));                 
                    dpSADebittedDate.setValue(LocalDate.parse(headerValues.get("DEBITTED_DATE"), CommonConstants.DATETIMEFORMATTER));
                    txtSAGivenAmount.setText(headerValues.get("DEBITTED_AMOUNT"));
                    String sAction = headerValues.get("DEBITTED_ACTION");
                    if(null != sAction) switch (sAction) {
                        case "REDUCE":
                            rgSAActionGroup.getToggles().get(0).setSelected(true);
                            break;
                        default:
                            rgSAActionGroup.getToggles().get(1).setSelected(true);
                            break;
                    }
                    txtSANote.setText(headerValues.get("NOTE"));
                    txtSAAmountToGive.setText(headerValues.get("AMOUNT_TO_GIVE"));
                    String salMonthYear[] = headerValues.get("SAL_FOR_MONTH_YEAR")!=null 
                            ? headerValues.get("SAL_FOR_MONTH_YEAR").split("-") : null;
                    if(salMonthYear != null) {
                        cbSalMonth.setValue(salMonthYear[0]);
                        cbSalYear.setValue(salMonthYear[1]);
                    }
                    setSAAllEmployeeValuesToFields(dbOp.getAllEmployeeValues(headerValues.get("EMPLOYEE_ID")), 
                            headerValues.get("TOTAL_ADVANCE_AMOUNT"));
                    closeSADateRestriction();
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry invalid bill number.");
                    clearAAAll();
                }
            } catch (SQLException ex) {
                Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }

    @FXML
    private void txtOADebitIdOnAction(ActionEvent event) 
    {
        
        if(tgOAOff.isSelected()) 
        {
            String sDebitId = txtOADebitId.getText();
            sOALastSelectedId = sDebitId;
            clearOAAll();
            
            try {
                HashMap<String, String> headerValues = dbOp.getOAAllHeaderValues(sDebitId);

                if(headerValues != null)
                {
                    txtOADebitId.setText(headerValues.get("ID"));                 
                    dpOADebittedDate.setValue(LocalDate.parse(headerValues.get("DEBITTED_DATE"), CommonConstants.DATETIMEFORMATTER));
                    txtOAGivenAmount.setText(headerValues.get("DEBITTED_AMOUNT"));
                    txtOANote.setText(headerValues.get("NOTE"));
                    txtOAReason.setText(headerValues.get("REASON"));
                    setOAAllEmployeeValuesToFields(dbOp.getAllEmployeeValues(headerValues.get("EMPLOYEE_ID")));
                    closeOADateRestriction();
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry invalid bill number.");
                    clearOAAll();
                }

            } catch (SQLException ex) {
                Logger.getLogger(EmployeeDebitController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    @FXML
    private void cbSalYearOnAction(ActionEvent event) {
    }
    
    private void closeDateRestriction() throws SQLException {
        
        //if(tpScreen.getSelectionModel().isSelected(1)) {
            
            String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpDebittedDate.getValue());

            if(sBillClosingDate != null && otherSettingValues.getRowCount() > 0 && 
                !Boolean.valueOf(otherSettingValues.getRow(0).getColumn(0).toString())) {

                if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                        DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                        sBillClosingDate))
                {                
                    if(tgOn.isSelected()) {
                        if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                            CommonConstants.EMPLOYEE_INCOME_SCREEN, "ALLOW_ADD") 
                            || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                            btSaveBill.setDisable(false);                        
                        } else {
                            btSaveBill.setDisable(true);
                        }
                    } else {
                        if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                            CommonConstants.EMPLOYEE_INCOME_SCREEN, "ALLOW_VIEW") 
                            || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {

                                String sOpeningDate = dbOp.getEmployeeDailyAllowanceAmtOpenedDate(txtDebitId.getText());
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
        //}
    }          
    
    private void closeAADateRestriction() throws SQLException {
        
        //if(tpScreen.getSelectionModel().isSelected(1)) {
            
            String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpAADebittedDate.getValue());

            if(sBillClosingDate != null && otherSettingValues.getRowCount() > 0 && 
                !Boolean.valueOf(otherSettingValues.getRow(0).getColumn(0).toString())) {

                if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                        DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                        sBillClosingDate))
                {                
                    if(tgAAOn.isSelected()) {
                        if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                            CommonConstants.EMPLOYEE_INCOME_SCREEN, "ALLOW_ADD") 
                            || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                            btAASaveBill.setDisable(false);                        
                        } else {
                            btAASaveBill.setDisable(true);
                        }
                    } else {
                        if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                            CommonConstants.EMPLOYEE_INCOME_SCREEN, "ALLOW_VIEW") 
                            || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {

                                String sOpeningDate = dbOp.getEmployeeAdvAmtOpenedDate(txtAADebitId.getText());
                                if(sOpeningDate != null) {
                                    String sOpenedDate = CommonConstants.DATETIMEFORMATTER
                                            .format(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
                                    if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                                            DateRelatedCalculations.getNextDateWithFormatted(
                                                    CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                                            sOpenedDate)) {
                                        btAAUpdateBill.setDisable(false);
                                    }
                                }
                        } else {
                            btAAUpdateBill.setDisable(true);
                        }                    
                    }                

                } else {
                    if(tgAAOn.isSelected()) {
                        btAASaveBill.setDisable(true);
                    } else {
                        btAAUpdateBill.setDisable(true);
                    }               
                }
            }    
        //}
    }          

    private void closeOADateRestriction() throws SQLException {
        
        //if(tpScreen.getSelectionModel().isSelected(1)) {
            String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpOADebittedDate.getValue());

            if(sBillClosingDate != null && otherSettingValues.getRowCount() > 0 && 
                !Boolean.valueOf(otherSettingValues.getRow(0).getColumn(0).toString())) {

                if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                        DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                        sBillClosingDate))
                {                
                    if(tgOAOn.isSelected()) {
                        if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                            CommonConstants.EMPLOYEE_INCOME_SCREEN, "ALLOW_ADD") 
                            || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                            btOASaveBill.setDisable(false);                        
                        } else {
                            btOASaveBill.setDisable(true);
                        }
                    } else {
                        if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                            CommonConstants.EMPLOYEE_INCOME_SCREEN, "ALLOW_VIEW") 
                            || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {

                                String sOpeningDate = dbOp.getEmployeeOtherAmtOpenedDate(txtOADebitId.getText());
                                if(sOpeningDate != null) {
                                    String sOpenedDate = CommonConstants.DATETIMEFORMATTER
                                            .format(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
                                    if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                                            DateRelatedCalculations.getNextDateWithFormatted(
                                                    CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                                            sOpenedDate)) {
                                        btOAUpdateBill.setDisable(false);
                                    }
                                }
                        } else {
                            btOAUpdateBill.setDisable(true);
                        }                    
                    }                

                } else {
                    if(tgOAOn.isSelected()) {
                        btOASaveBill.setDisable(true);
                    } else {
                        btOAUpdateBill.setDisable(true);
                    }               
                }
            }    
        //}
    }
    
    private void closeSADateRestriction() throws SQLException {
        
        //if(tpScreen.getSelectionModel().isSelected(1)) {
            String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpSADebittedDate.getValue());

            if(sBillClosingDate != null && otherSettingValues.getRowCount() > 0 && 
                !Boolean.valueOf(otherSettingValues.getRow(0).getColumn(0).toString())) {

                if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                        DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                        sBillClosingDate))
                {                
                    if(tgSAOn.isSelected()) {
                        if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                            CommonConstants.EMPLOYEE_INCOME_SCREEN, "ALLOW_ADD") 
                            || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                            btSASaveBill.setDisable(false);                        
                        } else {
                            btSASaveBill.setDisable(true);
                        }
                    } else {
                        if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                            CommonConstants.EMPLOYEE_INCOME_SCREEN, "ALLOW_VIEW") 
                            || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {

                                String sOpeningDate = dbOp.getEmployeeSalAmtOpenedDate(txtSADebitId.getText());
                                if(sOpeningDate != null) {
                                    String sOpenedDate = CommonConstants.DATETIMEFORMATTER
                                            .format(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
                                    if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                                            DateRelatedCalculations.getNextDateWithFormatted(
                                                    CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                                            sOpenedDate)) {
                                        btSAUpdateBill.setDisable(false);
                                    }
                                }
                        } else {
                            btSAUpdateBill.setDisable(true);
                        }                    
                    }                

                } else {
                    if(tgSAOn.isSelected()) {
                        btSASaveBill.setDisable(true);
                    } else {
                        btSAUpdateBill.setDisable(true);
                    }               
                }
            }    
        //}
    }        
    
}
