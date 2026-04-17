/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.employeemaster;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.PopupUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class EmployeeMasterController implements Initializable {

    private EmployeeMasterDBOperation dbOp;
    private String sLastSelectedId;
    private final String EMPLOYEE_ID = "EMPLOYEE_ID";
    public Stage dialog;
    
    @FXML
    private TabPane tpScreen;
    @FXML
    private Tab tabMainScreen;
    @FXML
    private ComboBox<String> cbSpouseType;
    @FXML
    private TextField txtEmployeeId;
    @FXML
    private TextField txtName;
    @FXML
    private DatePicker dpJoinedDate;
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
    private Label lbDailyOrMonthlySalaryAmount;
    @FXML
    private ComboBox<String> cbJobType;
    @FXML
    private ComboBox<String> cbSalaryType;
    @FXML
    private TextField txtSalaryAmount;
    @FXML
    private TextField txtDailyAllowanceAmount;
    @FXML
    private ComboBox<String> cbEmployeeType;
    @FXML
    private ComboBox<String> cbStatus;
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
    private TextField txtFilter;
    @FXML
    private TableView<AllDetailsBean> tbAllDetails;
    @FXML
    private TextField txtCompanyId;
    @FXML
    private Button btAddToFilter;
    @FXML
    private Button btShowAllRecords;
    @FXML
    private Button btShowFilteredRecords;
    @FXML
    public TextField txtImageLocation;
    @FXML
    private ImageView ivUserImg;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            dbOp = new EmployeeMasterDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EmployeeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }            
        
        doInitOperation();
        
        nodeAddToFilter.getChildren().remove(dpAddToFilter);
        nodeAddToFilter.getChildren().remove(cbAddToFilter);
                
        txtName.requestFocus();
    }    

    private void doInitOperation() {

        txtCompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
        try {
            txtEmployeeId.setText(dbOp.getId(EMPLOYEE_ID));
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        dpJoinedDate.setValue(LocalDate.now());   
        setSpouseTypeValues();    
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.EMPLOYEE_MODULE_SCREEN, "ALLOW_ADD") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btSaveBill.setDisable(false);
            } else {
                btSaveBill.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.EMPLOYEE_MODULE_SCREEN, "ALLOW_VIEW") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                tgOff.setDisable(false);
                btShowAllRecords.setDisable(false);
                btAddToFilter.setDisable(false);
                btShowFilteredRecords.setDisable(false);
            } else {
                tgOff.setDisable(true);
                btShowAllRecords.setDisable(true);
                btAddToFilter.setDisable(true);
                btShowFilteredRecords.setDisable(true);                
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
    }
    
    public void setSpouseTypeValues() {
        
        cbSpouseType.getItems().removeAll(cbSpouseType.getItems());
        RadioButton selectedRadioButton = (RadioButton) rgGenderGroup.getSelectedToggle();        
        if(null != selectedRadioButton.getText()) switch (selectedRadioButton.getText()) {
            case "Male":
                cbSpouseType.getItems().add("S/O");
                cbSpouseType.getItems().add("F/O");
                cbSpouseType.getItems().add("H/O");
                cbSpouseType.getSelectionModel().select(0);
                break;
            case "Female":
                cbSpouseType.getItems().add("W/O");
                cbSpouseType.getItems().add("D/O");
                cbSpouseType.getItems().add("M/O");
                cbSpouseType.getSelectionModel().select(0);
                break;
            default:
                cbSpouseType.getItems().add("S/O");
                cbSpouseType.getItems().add("F/O");
                cbSpouseType.getItems().add("H/O");
                cbSpouseType.getItems().add("D/O");
                cbSpouseType.getItems().add("M/O");
                cbSpouseType.getItems().add("W/O");
                cbSpouseType.getSelectionModel().select(0);
                break;
        }        
    }
    
    @FXML
    private void txtEmployeeIdOnAction(ActionEvent event) {
        
        try {
            String sId = txtEmployeeId.getText();
            sLastSelectedId = sId;
            clearAllHeader();
            
            HashMap<String, String> headerValues = dbOp.getAllHeaderValues(sId);
            
            if(headerValues != null)
            {
                setAllHeaderValuesToFields(headerValues);
            } else {
                PopupUtil.showErrorAlert("Sorry invalid employee id.");
                sLastSelectedId = "";
                clearAllHeader();
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setAllHeaderValuesToFields(HashMap<String, String> headerValues)
    {
        txtEmployeeId.setText(headerValues.get("ID"));
        dpJoinedDate.setValue(LocalDate.parse(headerValues.get("JOINED_DATE"), CommonConstants.DATETIMEFORMATTER));
        txtName.setText(headerValues.get("NAME"));
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
        setSpouseTypeValues();
        cbSpouseType.setValue(headerValues.get("SPOUSE_TYPE"));
        txtSpouseName.setText(headerValues.get("SPOUSE_NAME"));      
        txtDoorNo.setText(headerValues.get("DOOR_NUMBER"));
        txtStreetName.setText(headerValues.get("STREET"));
        txtArea.setText(headerValues.get("AREA"));
        txtCity.setText(headerValues.get("CITY"));
        txtMobileNumber.setText(headerValues.get("MOBILE_NUMBER"));
        cbJobType.setValue(headerValues.get("JOB_TYPE"));
        cbSalaryType.setValue(headerValues.get("SALARY_TYPE"));
        txtSalaryAmount.setText(headerValues.get("SALARY_AMOUNT"));
        txtDailyAllowanceAmount.setText(headerValues.get("DAILY_ALLOWANCE_AMOUNT"));
        cbEmployeeType.setValue(headerValues.get("EMPLOYEE_TYPE"));
        cbStatus.setValue(headerValues.get("STATUS"));
        txtNote.setText(headerValues.get("NOTE")); 
        
        String sImgPath = headerValues.get("IMG");
        txtImageLocation.setText(sImgPath);  
        if(!sImgPath.isEmpty()) {
            FileInputStream customerFIS = null;
            try {
                customerFIS = new FileInputStream(sImgPath);
                ivUserImg.setImage(new Image(customerFIS));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EmployeeMasterController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    customerFIS.close();
                } catch (IOException ex) {
                    Logger.getLogger(EmployeeMasterController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
    private void capitalizeCharOnPressed(KeyEvent e) {
        
        if(e.getCode() == KeyCode.BACK_SPACE){ 
            
            TextField txt_TextField = (TextField) e.getSource(); 
            
            String sText;
            int caretPos = txt_TextField.getCaretPosition();
            
            if(txt_TextField.getCaretPosition() == 0) {
                StringBuilder sb = new StringBuilder(txt_TextField.getText());
                sText = sb.toString();
                txt_TextField.setText(sText);
                txt_TextField.positionCaret(0);
            } else {
                StringBuilder sb = new StringBuilder(txt_TextField.getText());
                sb.deleteCharAt(txt_TextField.getCaretPosition() - 1);
                sText = sb.toString();
                txt_TextField.setText(sText);
                txt_TextField.positionCaret(caretPos-2);
            }            
            e.consume();
        }
    }
    
    @FXML
    private void allowNumberOnlyOnType(KeyEvent e) {
        
        if(!e.getCharacter().matches("[0-9]")){ 
            e.consume();
        }
    }
    
    private void allowNumberAndDotOnType(KeyEvent e) {
        
        TextField txt_TextField = (TextField) e.getSource();                      
        if(!("0123456789.".contains(e.getCharacter()))){ 
            e.consume();
        }
    }
    
    @FXML
    private void rbToggleChanged(MouseEvent event) {
        setSpouseTypeValues();
    }

    @FXML
    private void cbJobTypeOnAction(ActionEvent event) {
    }

    @FXML
    private void cbSalaryTypeOnAction(ActionEvent event) {
        
        String sSalaryType = cbSalaryType.getSelectionModel().getSelectedItem();        
        if("MONTHLY".equals(sSalaryType)) {
            lbDailyOrMonthlySalaryAmount.setText("Monthly Salary Amount:");
        } else {
            lbDailyOrMonthlySalaryAmount.setText("Daily Salary Amount:");
        }        
    }

    @FXML
    private void cbEmployeeTypeOnAction(ActionEvent event) {
    }

    @FXML
    private void cbStatusOnAction(ActionEvent event) {
    }

    @FXML
    private void saveModeON(ActionEvent event) {
        
        sLastSelectedId = "";
        clearAllHeader();
        btSaveBill.setDisable(false);
        btUpdateBill.setDisable(true);
        txtEmployeeId.setEditable(false);
        txtEmployeeId.setMouseTransparent(true);
        txtEmployeeId.setFocusTraversable(false);                
        txtName.setEditable(true);
        txtName.setMouseTransparent(false);
        txtName.setFocusTraversable(true);           
        doInitOperation();
        txtName.requestFocus();
    }

    @FXML
    private void saveModeOFF(ActionEvent event) {
        
        sLastSelectedId = "";
        clearAllHeader();
        btSaveBill.setDisable(true);
        btClearAll.setDisable(true);
        btUpdateBill.setDisable(false);
        txtEmployeeId.setText("");        
        txtEmployeeId.setEditable(true);
        txtEmployeeId.setMouseTransparent(false);
        txtEmployeeId.setFocusTraversable(true);        
        txtName.setEditable(false);
        txtName.setMouseTransparent(true);
        txtName.setFocusTraversable(false);     
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.EMPLOYEE_MODULE_SCREEN, "ALLOW_UPDATE") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btUpdateBill.setDisable(false);
            } else {
                btUpdateBill.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        txtEmployeeId.setText(CommonConstants.EMP_ID_PREFIX);
        txtEmployeeId.requestFocus();      
        txtEmployeeId.positionCaret(txtEmployeeId.getText().length());
    }

    @FXML
    private void btSaveBillClicked(ActionEvent event) {
        
        String sEmployeeId = txtEmployeeId.getText().toUpperCase();
        String sJoinedDate = CommonConstants.DATETIMEFORMATTER.format(dpJoinedDate.getValue());
        String sName = txtName.getText().toUpperCase();
        String sGender = ((RadioButton) rgGenderGroup.getSelectedToggle()).getText().toUpperCase();
        String sSpouseType = cbSpouseType.getValue().toUpperCase();
        String sSpouseName = txtSpouseName.getText().toUpperCase();
        String sDoorNo = txtDoorNo.getText().toUpperCase();
        String sStreetName = txtStreetName.getText().toUpperCase();
        String sArea = txtArea.getText().toUpperCase();
        String sCity = txtCity.getText().toUpperCase();
        String sMobileNumber = txtMobileNumber.getText().toUpperCase();
        String sNote = txtNote.getText().toUpperCase();        
        String sSalaryAmount = txtSalaryAmount.getText().toUpperCase();
        String sDailyAllowanceAmount = txtDailyAllowanceAmount.getText().toUpperCase();
        String sJobType = cbJobType.getValue().toUpperCase();
        String sSalaryType = cbSalaryType.getValue().toUpperCase();
        String sEmployeeType = cbEmployeeType.getValue().toUpperCase();
        String sStatus = cbStatus.getValue().toUpperCase();             
        
        double dSalaryAmount = Double.parseDouble(!("".equals(sSalaryAmount))? sSalaryAmount : "0");
        double dDailyAllowanceAmount = Double.parseDouble(!("".equals(sDailyAllowanceAmount))? sDailyAllowanceAmount : "0");
        
        if(isValidHeaderValues(sEmployeeId, sName, sSalaryAmount, sDailyAllowanceAmount)) {
            try {
                if(dbOp.isvalidNameToSave(sName)) {
                    
                    if(dbOp.saveRecord(sEmployeeId, sJoinedDate, sName, sGender, sSpouseType, sSpouseName, sDoorNo, sStreetName, sArea, sCity, sMobileNumber, dSalaryAmount, dDailyAllowanceAmount, sJobType, sSalaryType, sEmployeeType, sStatus, sNote)) {
                        dbOp.setNextId(EMPLOYEE_ID, CommonConstants.EMP_ID_PREFIX + (Integer.parseInt(sEmployeeId.replace(CommonConstants.EMP_ID_PREFIX, ""))+1));
                        txtName.setText("");
                        try {
                            txtEmployeeId.setText(dbOp.getId(EMPLOYEE_ID));
                        } catch (SQLException ex) {
                            Logger.getLogger(EmployeeMasterController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        PopupUtil.showInfoAlert("New employee added successfully with id."+"("+sEmployeeId+")");                        
                    }
                } else {
                    PopupUtil.showErrorAlert("Sorry same name already exists.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(EmployeeMasterController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(EmployeeMasterController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            PopupUtil.showErrorAlert("All mandatory fields should be filled.");
        }
    }

    public boolean isValidHeaderValues(String sBillNumber, String sName, String sSalaryAmount, 
                                        String sDailyAllowance)
    {
        if(!sBillNumber.isEmpty() && !sName.isEmpty() 
                && !sSalaryAmount.isEmpty() && !sDailyAllowance.isEmpty()) {
            return Double.parseDouble(sSalaryAmount) >= 0 && Double.parseDouble(sDailyAllowance) >= 0;
        } else {
            return false;
        }
    }
    
    @FXML
    private void btUpdateBillClicked(ActionEvent event) {
        
        if(!"".equals(sLastSelectedId)) 
        {
            txtEmployeeId.setText(sLastSelectedId);
            String sEmployeeId = txtEmployeeId.getText().toUpperCase();
            String sJoinedDate = CommonConstants.DATETIMEFORMATTER.format(dpJoinedDate.getValue());
            String sName = txtName.getText().toUpperCase();
            String sGender = ((RadioButton) rgGenderGroup.getSelectedToggle()).getText().toUpperCase();
            String sSpouseType = cbSpouseType.getValue().toUpperCase();
            String sSpouseName = txtSpouseName.getText().toUpperCase();
            String sDoorNo = txtDoorNo.getText().toUpperCase();
            String sStreetName = txtStreetName.getText().toUpperCase();
            String sArea = txtArea.getText().toUpperCase();
            String sCity = txtCity.getText().toUpperCase();
            String sMobileNumber = txtMobileNumber.getText().toUpperCase();
            String sNote = txtNote.getText().toUpperCase();        
            String sSalaryAmount = txtSalaryAmount.getText().toUpperCase();
            String sDailyAllowanceAmount = txtDailyAllowanceAmount.getText().toUpperCase();
            String sJobType = cbJobType.getValue().toUpperCase();
            String sSalaryType = cbSalaryType.getValue().toUpperCase();
            String sEmployeeType = cbEmployeeType.getValue().toUpperCase();
            String sStatus = cbStatus.getValue().toUpperCase();             
            
            String sImagePath = !txtImageLocation.getText().isEmpty() ? txtImageLocation.getText() : null;
            
            double dSalaryAmount = Double.parseDouble(!("".equals(sSalaryAmount))? sSalaryAmount : "0");
            double dDailyAllowanceAmount = Double.parseDouble(!("".equals(sDailyAllowanceAmount))? sDailyAllowanceAmount : "0");

            if(isValidHeaderValues(sEmployeeId, sName, sSalaryAmount, sDailyAllowanceAmount)) {
                try {
                    if(dbOp.updateRecord(sEmployeeId, sJoinedDate, sName, sGender, sSpouseType, sSpouseName, 
                            sDoorNo, sStreetName, sArea, sCity, sMobileNumber, dSalaryAmount, dDailyAllowanceAmount, 
                            sJobType, sSalaryType, sEmployeeType, sStatus, sNote, sImagePath)) {
                        PopupUtil.showInfoAlert("Saved changes to Employee with id ("+ sEmployeeId +") successfully.");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(EmployeeMasterController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                PopupUtil.showErrorAlert("All mandatory fields should be filled.");
            }
        } else {
            PopupUtil.showErrorAlert("Sorry not any employee selected to update.");
        }
    }

    @FXML
    private void btClearAllClicked(ActionEvent event) {
        clearAllHeader();
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
            DataTable allDetailValues = dbOp.getAllDetailsValues(null);
            setAllDetailValuesToField(allDetailValues);
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    public void setAllDetailValuesToField(DataTable allDetailValues) {

        tbAllDetails.getItems().removeAll(tbAllDetails.getItems());
        for(int i=0; i<allDetailValues.getRowCount(); i++) {            
            String sId = allDetailValues.getRow(i).getColumn(0).toString();
            String sJoinedDate = allDetailValues.getRow(i).getColumn(1).toString();
            String sEmployeeType = allDetailValues.getRow(i).getColumn(2).toString();
            String sName = allDetailValues.getRow(i).getColumn(3).toString();
            String sGender = allDetailValues.getRow(i).getColumn(4).toString();
            String sSpouseType = allDetailValues.getRow(i).getColumn(5).toString();
            String sSpouseName = allDetailValues.getRow(i).getColumn(6).toString();
            String sMobileNumber = allDetailValues.getRow(i).getColumn(7).toString();
            String sSalaryType = allDetailValues.getRow(i).getColumn(8).toString();
            String sSalary = allDetailValues.getRow(i).getColumn(9).toString();
            String sDailyAllowance = allDetailValues.getRow(i).getColumn(10).toString();
            String sJobType = allDetailValues.getRow(i).getColumn(11).toString();
            String sStatus = allDetailValues.getRow(i).getColumn(12).toString();
            tbAllDetails.getItems().add(new AllDetailsBean(sId, sJoinedDate, sEmployeeType, sName, sGender, sSpouseType, sSpouseName, sMobileNumber, sSalaryType, sSalary, sDailyAllowance, sJobType, sStatus));
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
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {
        int index = tbAllDetails.getSelectionModel().getSelectedIndex();
        
        if (event.getClickCount() == 2 && (index >= 0) ) {
            
            String sId = tbAllDetails.getItems().get(index).getSId();            
            tgOff.setSelected(true);
            saveModeOFF(null);
            txtEmployeeId.setText(sId);
            txtEmployeeIdOnAction(null);
            tpScreen.getSelectionModel().select(tabMainScreen);
        }
    }
    
    public void clearAllHeader()
    {
        txtCompanyId.setText(CommonConstants.ACTIVE_COMPANY_ID);
        txtName.setText("");
        dpJoinedDate.setValue(LocalDate.now());
        txtSpouseName.setText("");
        txtDoorNo.setText("");
        txtStreetName.setText("");
        txtArea.setText("");
        txtCity.setText("");
        txtMobileNumber.setText("");
        txtNote.setText("");     
        txtSalaryAmount.setText("");
        txtDailyAllowanceAmount.setText("");
        cbJobType.setValue("REGULAR");
        cbSalaryType.setValue("MONTHLY");
        cbEmployeeType.setValue("CHAIRMAN");
        cbStatus.setValue("ACTIVE");        
    }

    @FXML
    private void ivCustomerImageClicked(MouseEvent event) {
    }

    @FXML
    private void btChooseImageClicked(ActionEvent event) {
        dialog = new Stage();
        FileChooser chooser = new FileChooser();
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setX(CommonConstants.SCREEN_X);
        stage.setY(CommonConstants.SCREEN_Y);        
        File file = chooser.showOpenDialog(stage);
        if(file != null) {
            FileInputStream customerFIS = null;
            try {
                txtImageLocation.setText(file.getAbsolutePath());                
                customerFIS = new FileInputStream(file.getAbsolutePath());
                ivUserImg.setImage(new Image(customerFIS));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EmployeeMasterController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    customerFIS.close();
                } catch (IOException ex) {
                    Logger.getLogger(EmployeeMasterController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }        
    }
    
}
