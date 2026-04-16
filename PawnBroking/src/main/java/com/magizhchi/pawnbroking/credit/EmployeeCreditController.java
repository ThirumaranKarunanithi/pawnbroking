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
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
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
public class EmployeeCreditController implements Initializable {

    private String sAALastSelectedId = null;
    private String sOALastSelectedId = null;

    private EmployeeCreditDBOperation dbOp;
    public DataTable dtEmployeeNames;
    public Stage dialog;
    public DataTable dtReduceList = null;
    private DataTable otherSettingValues;
    
    @FXML
    private TabPane tpScreen;
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
    private TextField txtAAGivenAmount;
    @FXML
    private Label lbAAMessage;
    @FXML
    private Button btAASaveBill;
    @FXML
    private Button btAAClearAll;
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
    private Label lbOAMessage;
    @FXML
    private Button btOASaveBill;
    @FXML
    private TextField txtAATotalAdvanceAmount;
    @FXML
    private TextField txtAASelectedCount;
    @FXML
    private TextField txtAASelectedAmount;
    @FXML
    private Button btAASelectAdvanceAmount;
    @FXML
    private HBox hSaveModeButtons1;
    @FXML
    private ToggleButton tgAAOn;
    @FXML
    private ToggleGroup ViewModeGroup1;
    @FXML
    private ToggleButton tgAAOff;
    @FXML
    private Button btAAUpdateBill;
    @FXML
    private HBox hSaveModeButtons11;
    @FXML
    private ToggleButton tgOAOn;
    @FXML
    private ToggleGroup ViewModeGroup11;
    @FXML
    private ToggleButton tgOAOff;
    @FXML
    private Button btOAUpdateBill;
    @FXML
    private Button btOAClearAll;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            
            try {
                dbOp = new EmployeeCreditDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            txtAADebitId.setText(dbOp.getAAId());
            txtAACompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
            txtAACompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
            dpAADebittedDate.setValue(LocalDate.now());

            txtOADebitId.setText(dbOp.getOAId());
            txtOACompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
            txtOACompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
            dpOADebittedDate.setValue(LocalDate.now());
            
            setEmployeeNames();
                        
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            otherSettingValues = dbOp.getOtherSettingsValues();   
            
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.EMPLOYEE_INCOME_SCREEN, "ALLOW_ADD") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btAASaveBill.setDisable(false);
                btOASaveBill.setDisable(false);
            } else {
                btAASaveBill.setDisable(true);
                btOASaveBill.setDisable(true);
            }
            
            /*if(dbOp.allowToChangeDate()) {
                dpAADebittedDate.setMouseTransparent(false);
                dpAADebittedDate.setFocusTraversable(true);                
                dpOADebittedDate.setMouseTransparent(false);
                dpOADebittedDate.setFocusTraversable(true);                
            } else {
                dpAADebittedDate.setMouseTransparent(true);
                dpAADebittedDate.setFocusTraversable(false);                
                dpOADebittedDate.setMouseTransparent(true);
                dpOADebittedDate.setFocusTraversable(false);                            
            }*/
                       
            closeAADateRestriction();
            closeOADateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    

    public void setEmployeeNames() {
        
        try {
            
            cbAAEMployeeName.getItems().removeAll(cbAAEMployeeName.getItems());
            cbOAEMployeeName.getItems().removeAll(cbAAEMployeeName.getItems());
            
            dtEmployeeNames = dbOp.getAllEmployeeNames();
            
            for(int i=0; i<dtEmployeeNames.getRowCount(); i++) {          
                cbAAEMployeeName.getItems().add(dtEmployeeNames.getRow(i).getColumn(1).toString());
                cbOAEMployeeName.getItems().add(dtEmployeeNames.getRow(i).getColumn(1).toString());
            }
            
            txtAAEmployeeId.setText(dtEmployeeNames.getRow(0).getColumn(0).toString());
            txtOAEmployeeId.setText(dtEmployeeNames.getRow(0).getColumn(0).toString());
            
            cbAAEMployeeName.getSelectionModel().select(0);
            cbOAEMployeeName.getSelectionModel().select(0);
            
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
    private void rbToggleChanged(MouseEvent event) {
    }

    @FXML
    private void allowNumberOnlyOnType(KeyEvent event) {
    }

    @FXML
    private void allowNumberAndDotOnType(KeyEvent e) {
        
        TextField txt_TextField = (TextField) e.getSource();                      
        if(!("0123456789.-".contains(e.getCharacter()))){ 
            e.consume();
        }                
    }
    
    @FXML
    private void cbAAEmployeeNameOnAction(ActionEvent event) {
        
        try {
            int index = cbAAEMployeeName.getSelectionModel().getSelectedIndex();
            String sRepledgeId = dtEmployeeNames.getRow(index).getColumn(0).toString();
            txtAAEmployeeId.setText(sRepledgeId);
            if(!"".equals(sRepledgeId)) {
                if(dbOp.isAASameDateEntryAvailable(sRepledgeId, CommonConstants.DATETIMEFORMATTER
                        .format(dpAADebittedDate.getValue()))) {
                    lbAAMessage.setText("NOTE: Already advance amount credited for this month to " 
                            + cbAAEMployeeName.getSelectionModel().getSelectedItem() + ".");
                } else {
                    lbAAMessage.setText("");
                }
                dtReduceList = dbOp.getAllReduceList(txtAAEmployeeId.getText());
                setAAAllEmployeeValuesToFields(dbOp.getAllEmployeeValues(sRepledgeId));
            } else {
                clearAAAll();
            }
            closeAADateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    public void setAAAllEmployeeValuesToFields(HashMap<String, String> companyValues) {
    
        if(tgAAOff.isSelected()) {
            txtAAEmployeeId.setText(companyValues.get("ID"));
            cbAAEMployeeName.setValue(companyValues.get("NAME"));
        } else {
            txtAAGivenAmount.setText("");
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
        txtAAGivenAmount.setText("0");       
        txtAAEmployeType.setText(companyValues.get("EMPLOYEE_TYPE"));
        txtAAStatus.setText(companyValues.get("STATUS"));      
        
        int iSlNo = 0;
        double dReduceAmount = 0.0;
        
        for(int i=0; i<dtReduceList.getRowCount(); i++) {
            if(Boolean.parseBoolean(dtReduceList.getRow(i).getColumn(5).toString())) {
                iSlNo++;
                dReduceAmount += Double.parseDouble(dtReduceList.getRow(i).getColumn(4).toString());
            }
        }
        txtAATotalAdvanceAmount.setText(Double.toString(dReduceAmount));
        txtAASelectedCount.setText(Integer.toString(iSlNo));
        txtAASelectedAmount.setText(Double.toString(dReduceAmount));
        txtAAGivenAmount.requestFocus();
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
            Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btAASaveBillClicked(ActionEvent event) {
        
        if(!cbAAEMployeeName.getSelectionModel().isSelected(0)) 
        {
            String sDebittedDate = CommonConstants.DATETIMEFORMATTER.format(dpAADebittedDate.getValue());
            
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sDebittedDate))
            {                        
                String sId = txtAADebitId.getText();
                String sEmployeeId = txtAAEmployeeId.getText();
                String sEmployeeName = cbAAEMployeeName.getValue().toUpperCase();
                String sDebittedAmount = txtAAGivenAmount.getText().toUpperCase();
                String sNote = txtAANote.getText().toUpperCase();        

                double dDebittedAmount = Double.parseDouble(!("".equals(sDebittedAmount))? sDebittedAmount : "0");

                if(isValidHeaderValues(sDebittedAmount)) {
                    try {
                        if(dbOp.saveAdvanceAmountCredit(sId, sEmployeeId, sEmployeeName, sDebittedDate, dDebittedAmount, sNote)) {

                            for(int i=0; i<dtReduceList.getRowCount(); i++) {
                                if(Boolean.parseBoolean(dtReduceList.getRow(i).getColumn(5).toString())) {
                                    dbOp.updateSAAllAdvanceAmountToReducedAction(sId, dtReduceList.getRow(i).getColumn(1).toString(), sEmployeeId);
                                }
                            }

                            dbOp.setAANextId(Integer.parseInt(sId.replace(CommonConstants.EMP_CREDIT_AA_PREFIX, ""))+1);
                            PopupUtil.showInfoAlert(event, "Amount Rs."+sDebittedAmount+" credited for "+sEmployeeName);
                            txtAADebitId.setText(dbOp.getAAId());
                            cbAAEMployeeName.getSelectionModel().select(0);
                            cbAAEMployeeName.getSelectionModel().select(sEmployeeName);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    PopupUtil.showErrorAlert("All mandatory fields should be filled properly.");
                    txtAAGivenAmount.requestFocus();
                }
            } else {
                PopupUtil.showErrorAlert("Sorry this date account was closed.");
            }                                                                                                     
        } else {
            PopupUtil.showInfoAlert(event, "Any of employee should be selected to credit amount.");
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
    private void btAAClearAllClicked(ActionEvent event) {
        cbAAEMployeeName.getSelectionModel().select(0);
    }

    @FXML
    private void btAASelectAdvanceAmountOnAction(ActionEvent event) {
        
        showReduceDialog(event, dtReduceList);

        int iCount = 0;
        double dReducedAmount = 0.0;
        for(int i=0; i<dtReduceList.getRowCount(); i++) {
            if(Boolean.parseBoolean(dtReduceList.getRow(i).getColumn(5).toString())) {
                iCount++;
                dReducedAmount += Double.parseDouble(dtReduceList.getRow(i).getColumn(4).toString());
            }
        }
        txtAASelectedCount.setText(Integer.toString(iCount));
        txtAASelectedAmount.setText(Double.toString(dReducedAmount));
        
        if(dReducedAmount <= 0) {
            txtAAGivenAmount.setText("0");
            txtAAGivenAmount.setEditable(false);
            txtAAGivenAmount.setMouseTransparent(true);
            txtAAGivenAmount.setFocusTraversable(false);                
        } else {
            txtAAGivenAmount.setEditable(true);
            txtAAGivenAmount.setMouseTransparent(false);
            txtAAGivenAmount.setFocusTraversable(true);          
            txtAAGivenAmount.requestFocus();
        }
    }
    
    private void showReduceDialog(ActionEvent event, DataTable dtReduceList) {
        
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
    private void cbOAEmployeeNameOnAction(ActionEvent event) {
        Platform.runLater(()->{
        try {
            int index = cbOAEMployeeName.getSelectionModel().getSelectedIndex();
            String sRepledgeId = dtEmployeeNames.getRow(index).getColumn(0).toString();
            txtOAEmployeeId.setText(sRepledgeId);
            if(!"".equals(sRepledgeId)) {
                if(dbOp.isOASameDateEntryAvailable(sRepledgeId, CommonConstants.DATETIMEFORMATTER.format(dpOADebittedDate.getValue()))) {
                    lbOAMessage.setText("NOTE: Already other amount credited for "+ cbOAEMployeeName.getSelectionModel().getSelectedItem() +" in the same day.");
                } else {
                    lbOAMessage.setText("");
                }
                setOAAllEmployeeValuesToFields(dbOp.getAllEmployeeValues(sRepledgeId));                
            } else {
                clearOAAll();
            }
            closeOADateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        });
    }

    public void setOAAllEmployeeValuesToFields(HashMap<String, String> companyValues) {
    
        if(tgOAOff.isSelected()) {
            txtOAEmployeeId.setText(companyValues.get("ID"));
            cbOAEMployeeName.setValue(companyValues.get("NAME"));
        } else {
            txtOAGivenAmount.setText("");
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
        txtOAReason.setText("");
        txtOAGivenAmount.setText("");       
        txtOAEmployeType.setText("");
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
            Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                            dbOp.setOANextId(Integer.parseInt(sId.replace(CommonConstants.EMP_CREDIT_OA_PREFIX, ""))+1);
                            PopupUtil.showInfoAlert(event, "Amount Rs."+sDebittedAmount+" credited for "+sEmployeeName);
                            txtOADebitId.setText(dbOp.getOAId());
                            cbOAEMployeeName.getSelectionModel().select(0);
                            cbOAEMployeeName.getSelectionModel().select(sEmployeeName);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
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
    private void btOAClearAllClicked(ActionEvent event) {
        cbOAEMployeeName.getSelectionModel().select(0);
    }

    @FXML
    private void saveModeAAON(ActionEvent event) {
        sAALastSelectedId = null;
        clearAAAll();
        doAAAllSaveModeONWork();        
    }

    public void doAAAllSaveModeONWork() {
    
        try {
            btAASaveBill.setDisable(false);
            btAAUpdateBill.setDisable(true);
            txtAADebitId.setEditable(false);
            txtAADebitId.setMouseTransparent(true);
            txtAADebitId.setFocusTraversable(false);
            cbAAEMployeeName.getSelectionModel().select(0);
            cbAAEMployeeName.setMouseTransparent(false);
            cbAAEMployeeName.setFocusTraversable(true);
            
            txtAADebitId.setText(dbOp.getAAId());
            txtAACompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
            txtAACompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
            dpAADebittedDate.setValue(LocalDate.now());
            closeAADateRestriction();
            txtAAGivenAmount.requestFocus();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void saveModeAAOFF(ActionEvent event) {
        sAALastSelectedId = null;
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
        cbAAEMployeeName.getSelectionModel().select(0);
        cbAAEMployeeName.setEditable(false);
        cbAAEMployeeName.setMouseTransparent(true);
        cbAAEMployeeName.setFocusTraversable(false);        
        txtAADebitId.setText(CommonConstants.EMP_CREDIT_AA_PREFIX);
        try {
            closeAADateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
        txtAADebitId.requestFocus(); 
        txtAADebitId.positionCaret(txtAADebitId.getText().length());
    }

    @FXML
    private void btAAUpdateBillClicked(ActionEvent event) {
    }

    @FXML
    private void saveModeOAON(ActionEvent event) {
        sOALastSelectedId = null;
        clearOAAll();
        doOAAllSaveModeONWork();        
    }

    public void doOAAllSaveModeONWork() {
    
        try {
            btOASaveBill.setDisable(false);
            btOAUpdateBill.setDisable(true);
            txtOADebitId.setEditable(false);
            txtOADebitId.setMouseTransparent(true);
            txtOADebitId.setFocusTraversable(false);
            cbOAEMployeeName.getSelectionModel().select(0);
            cbOAEMployeeName.setMouseTransparent(false);
            cbOAEMployeeName.setFocusTraversable(true);
            
            txtOADebitId.setText(dbOp.getOAId());
            txtOACompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
            txtOACompanyName.setText(CommonConstants.ACTIVE_COMPANY_NAME);
            dpOADebittedDate.setValue(LocalDate.now());
                   
            closeOADateRestriction();
            txtOAGivenAmount.setText("0");
            txtOAGivenAmount.requestFocus();

        } catch (SQLException ex) {
            Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void saveModeOAOFF(ActionEvent event) {
        sOALastSelectedId = null;
        clearOAAll();
        doOAAllSaveModeOFFWork();
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
        txtOADebitId.setText(CommonConstants.EMP_CREDIT_OA_PREFIX);
        try {
            closeOADateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
        }
        txtOADebitId.requestFocus(); 
        txtOADebitId.positionCaret(txtOADebitId.getText().length());
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
            if(isValidHeaderValues(sGivenAmount) && !"".equals(sReason))  
            {
                if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sDebittedDate)) 
                {
                    try {
                        if(dbOp.updateOARecord(sOALastSelectedId, sDebittedDate, sNote, dGivenAmount, sReason)) {
                            PopupUtil.showInfoAlert(event, "Bill Updated successfully.");
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
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
                    dpAADebittedDate.setValue(LocalDate.parse(headerValues.get("CREDITED_DATE"), CommonConstants.DATETIMEFORMATTER));
                    txtAAGivenAmount.setText(headerValues.get("CREDITED_AMOUNT"));
                    txtAANote.setText(headerValues.get("NOTE"));
                    setAAAllEmployeeValuesToFields(dbOp.getAllEmployeeValues(headerValues.get("EMPLOYEE_ID")));
                } else {
                    PopupUtil.showErrorAlert("Sorry invalid credit number.");
                    clearAAAll();
                }
                closeAADateRestriction();
            } catch (SQLException ex) {
                Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }

    @FXML
    private void txtOADebitIdOnAction(ActionEvent event) {
        
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
                    dpOADebittedDate.setValue(LocalDate.parse(headerValues.get("CREDITED_DATE"), CommonConstants.DATETIMEFORMATTER));
                    txtOANote.setText(headerValues.get("NOTE"));
                    txtOAReason.setText(headerValues.get("REASON"));
                    setOAAllEmployeeValuesToFields(dbOp.getAllEmployeeValues(headerValues.get("EMPLOYEE_ID")));
                    txtOAGivenAmount.setText(headerValues.get("CREDITED_AMOUNT"));
                } else {
                    PopupUtil.showErrorAlert("Sorry invalid credit number.");
                    clearOAAll();
                }
                closeOADateRestriction();
            } catch (SQLException ex) {
                Logger.getLogger(EmployeeCreditController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
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
    
}
