/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.usermaster;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.PasswordUtils;
import com.magizhchi.pawnbroking.common.PopupUtil;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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
import org.bytedeco.opencv.opencv_core.DeviceInfo;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class UserMasterController implements Initializable {    
    
    public UserMasterDBOperation dbOp;
    private final String USER_ID = "USER_ID";
    private String sLastSelectedId = null;
    String key = "";
    int quality = 60;
    int timeout = 10000;
    
    public DataTable dtEmployeeNames;
    public DataTable dtRoleNames;
    private final ArrayList<String> alFilterDBColumnName = new ArrayList<>();
    private final ArrayList<String> alFilterName = new ArrayList<>();
    private final ArrayList<String> alFilterValue = new ArrayList<>();
    
    @FXML
    private TabPane tpScreen;
    @FXML
    private Tab tabMainScreen;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtUserName;
    @FXML
    private PasswordField pwdPassword;
    @FXML
    private PasswordField pwdConfirmPassword;
    @FXML
    private TextField txtEmployeeId;
    @FXML
    private ComboBox<String> cbEMployeeName;
    @FXML
    private TextField txtRoleId;
    @FXML
    private ComboBox<String> cbRoleName;
    @FXML
    private ComboBox<String> cbStatus;
    @FXML
    private TextArea txtNote;
    @FXML
    private Label lbMsg1;
    @FXML
    private ToggleButton tgOn;
    @FXML
    private ToggleGroup ViewModeGroup1;
    @FXML
    private ToggleButton tgOff;
    @FXML
    private Button btSaveHeader;
    @FXML
    private Button btUpdateHeader;
    @FXML
    private Button btClearAll;
    @FXML
    private ComboBox<String> cbAllDetailsFilter;
    @FXML
    private TextField txtAddToFilter;
    @FXML
    private TextField txtFilter;
    @FXML
    private TableView<AllDetailsBean> tbAllDetails;
    @FXML
    private ComboBox<String> cbAddToFilter;
    @FXML
    private HBox nodeAddToFilter;
    @FXML
    private HBox hSaveModeButtons;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            dbOp = new UserMasterDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        try {
            txtId.setText(dbOp.getId(USER_ID));
        } catch (SQLException ex) {
            Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.USER_MODULE_SCREEN, "ALLOW_ADD") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btSaveHeader.setDisable(false);
            } else {
                btSaveHeader.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.USER_MODULE_SCREEN, "ALLOW_VIEW") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                tgOff.setDisable(false);
            } else {
                tgOff.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
                
        nodeAddToFilter.getChildren().remove(cbAddToFilter);
        setEmployeeNames();
        setRoleNames();
    }    

    public void setEmployeeNames() {
        
        try {
            
            cbEMployeeName.getItems().removeAll(cbEMployeeName.getItems());            
            dtEmployeeNames = dbOp.getAllEmployeeNames();
            
            for(int i=0; i<dtEmployeeNames.getRowCount(); i++) {          
                cbEMployeeName.getItems().add(dtEmployeeNames.getRow(i).getColumn(1).toString());
            }
            
            txtEmployeeId.setText(dtEmployeeNames.getRow(0).getColumn(0).toString());            
            cbEMployeeName.getSelectionModel().select(0);
            
        } catch (SQLException ex) {
            Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                
        
    }

    public void setRoleNames() {
        
        try {
            
            cbRoleName.getItems().removeAll(cbRoleName.getItems());            
            dtRoleNames = dbOp.getAllRoleNames();
            
            for(int i=0; i<dtRoleNames.getRowCount(); i++) {          
                cbRoleName.getItems().add(dtRoleNames.getRow(i).getColumn(1).toString());
            }
            
            txtRoleId.setText(dtRoleNames.getRow(0).getColumn(0).toString());            
            cbRoleName.getSelectionModel().select(0);
            
        } catch (SQLException ex) {
            Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                
        
    }
    
    @FXML
    private void txtIdOnAction(ActionEvent event) {
        
        String sId = txtId.getText();
        sLastSelectedId = sId;
        btClearAllClicked(null);
        try {
            HashMap<String, String> headerValues = dbOp.getAllHeaderValues(sId);
            
            if(headerValues != null)
            {
                setAllHeaderValuesToFields(headerValues);
            } else {
                PopupUtil.showErrorAlert("Sorry invalid id.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public void setAllHeaderValuesToFields(HashMap<String, String> headerValues)
    {
        txtId.setText(headerValues.get("ID"));
        txtUserName.setText(headerValues.get("USER_NAME"));
        pwdPassword.setText(headerValues.get("USER_PASSWORD"));
        txtEmployeeId.setText(headerValues.get("EMP_ID"));
        cbEMployeeName.setValue(headerValues.get("EMP_NAME"));
        txtRoleId.setText(headerValues.get("ROLE_ID"));
        cbRoleName.setValue(headerValues.get("ROLE_NAME"));
        cbStatus.setValue(headerValues.get("STATUS"));
        txtNote.setText(headerValues.get("NOTE"));    
    }
    
    @FXML
        private void capitalizeCharOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();
        int caretPos = txt_TextField.getCaretPosition();
        txt_TextField.setText(txt_TextField.getText().toUpperCase());
        txt_TextField.positionCaret(caretPos);
    }

    @FXML
    private void capitalizeCharOnPressed(KeyEvent event) {
    }

    @FXML
    private void cbEmployeeNameOnAction(ActionEvent event) {

        int index = cbEMployeeName.getSelectionModel().getSelectedIndex();
        String sRepledgeId = dtEmployeeNames.getRow(index).getColumn(0).toString();
        txtEmployeeId.setText(sRepledgeId);
        clearAll();
    }

    public void clearAll() {
        txtNote.setText("");        
    }
    
    @FXML
    private void cbRoleNameOnAction(ActionEvent event) {

        int index = cbRoleName.getSelectionModel().getSelectedIndex();
        String sRepledgeId = dtRoleNames.getRow(index).getColumn(0).toString();
        txtRoleId.setText(sRepledgeId);
        clearAll();        
    }

    @FXML
    private void saveModeON(ActionEvent event) {
        doHeaderSaveModeWork();
    }

    public void doHeaderSaveModeWork() {
    
        btClearAllClicked(null);
        txtNote.setText("");
        txtId.setEditable(false);
        txtId.setMouseTransparent(true);
        txtId.setFocusTraversable(false);     
        txtUserName.setEditable(true);
        txtUserName.setMouseTransparent(false);
        txtUserName.setFocusTraversable(true);     
        cbEMployeeName.setMouseTransparent(false);
        cbEMployeeName.setFocusTraversable(true);     
        btUpdateHeader.setDisable(true);
        
        try {
            txtId.setText(dbOp.getId(USER_ID));
        } catch (SQLException ex) {
            Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.USER_MODULE_SCREEN, "ALLOW_ADD") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btSaveHeader.setDisable(false);
            } else {
                btSaveHeader.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        txtUserName.requestFocus();        
    }
    
    @FXML
    private void saveModeOFF(ActionEvent event) {
        
        doHeaderUpdateModeWork();
    }

    public void doHeaderUpdateModeWork() {
    
        btClearAllClicked(null);
        txtNote.setText("");
        txtId.setText(CommonConstants.USR_ID_PREFIX);
        txtId.setEditable(true);
        txtId.setMouseTransparent(false);
        txtId.setFocusTraversable(true);     
        txtUserName.setEditable(false);
        txtUserName.setMouseTransparent(true);
        txtUserName.setFocusTraversable(false);     
        cbEMployeeName.setMouseTransparent(true);
        cbEMployeeName.setFocusTraversable(false);     
        
        btSaveHeader.setDisable(true);   
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.USER_MODULE_SCREEN, "ALLOW_UPDATE") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btUpdateHeader.setDisable(false);
            } else {
                btUpdateHeader.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        txtId.requestFocus();        
        txtId.positionCaret(txtId.getText().length());
    }
    
    @FXML
    private void btSaveHeaderClicked(ActionEvent event) {
        
        
            sLastSelectedId = "";
            String sId = txtId.getText().toUpperCase();
            String sUserName = txtUserName.getText().toUpperCase().trim();
            String sPassword = pwdPassword.getText().toUpperCase().trim();
            String sConfirmPassword = pwdConfirmPassword.getText().toUpperCase().trim();
            String sEmpId = txtEmployeeId.getText().toUpperCase().trim();
            String sRoleId = txtRoleId.getText().toUpperCase().trim();
            String sStatus = cbStatus.getValue().toUpperCase().trim();
            String sNote = txtNote.getText().toUpperCase();
            
            if(isValidHeaderValues(sUserName, sPassword, sConfirmPassword, sEmpId, sRoleId))
            {
            if(sPassword.equals(sConfirmPassword))
            {
            try {
            if(dbOp.isvalidNameToSave(sUserName))
            {
            String salt = PasswordUtils.getSalt(25);
            String mySecurePassword = PasswordUtils.generateSecurePassword(sPassword, salt);
            
            if(dbOp.saveRecord(sId, sUserName, mySecurePassword, sEmpId, sRoleId, sStatus, sNote, salt))
            {
            dbOp.setNextId(USER_ID, CommonConstants.USR_ID_PREFIX + (Integer.parseInt(sId.replace(CommonConstants.USR_ID_PREFIX, ""))+1));
            clearAll();
            try {
            txtId.setText(dbOp.getId(USER_ID));
            } catch (SQLException ex) {
            Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
            }
            PopupUtil.showInfoAlert("User created successfully with id."+"("+sId+")");
            btClearAllClicked(null);
            }
            } else {
            PopupUtil.showErrorAlert("Sorry same name already exists.");
            }
            } catch (Exception ex) {
            Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
            }
            } else {
            PopupUtil.showErrorAlert("Password and Confirm Password should be same.");
            }
            } else {
            PopupUtil.showErrorAlert("All mandatory fields should be filled.");
            } 
        
        /*try {    
            String sId = txtId.getText().toUpperCase();
            String sUserName = txtUserName.getText().toUpperCase().trim();
            String sPassword = pwdPassword.getText().toUpperCase().trim();
            String sConfirmPassword = pwdConfirmPassword.getText().toUpperCase().trim();
            String sEmpId = txtEmployeeId.getText().toUpperCase().trim();
            String sRoleId = txtRoleId.getText().toUpperCase().trim();
            String sStatus = cbStatus.getValue().toUpperCase().trim();
            String sNote = txtNote.getText().toUpperCase();
            
            Login login = new Login(sId, sUserName, CommonConstants.SOFTWARE_ID, CommonConstants.ACTIVE_COMPANY_ID,
                    sEmpId, sRoleId, sStatus, sNote, CommonConstants.USERID, CommonConstants.ACTIVE_MACHINE.getMacAddr());
            CreateTextLogin createTextLogin = new CreateTextLogin(login, sPassword, sConfirmPassword);
            LoginTextManager loginManager = new LoginTextManager();
            LoginReturn returnBean = loginManager.createNewLogin(createTextLogin);
            if(returnBean.isIsMessageAvailable()) {
                dbOp.setNextId(USER_ID, CommonConstants.USR_ID_PREFIX + (Integer.parseInt(sId.replace(CommonConstants.USR_ID_PREFIX, ""))+1));
                clearAll();
                txtId.setText(dbOp.getId(USER_ID));
                PopupUtil.showInfoAlert(returnBean.getMsg());
                btClearAllClicked(null);
            }
        } catch (Exception ex) {
            Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    public boolean isValidHeaderValues(String sUserName, String sPassword, String sConfirmPassword, String sEmpId, String sRoleId)
    {
        return !sUserName.isEmpty() && !sPassword.isEmpty() && !sConfirmPassword.isEmpty() && !sEmpId.isEmpty() && !sRoleId.isEmpty();
    }
    
    @FXML
    private void btUpdateHeaderClicked(ActionEvent event) {
        
        if(sLastSelectedId != null) {
            
            String sId = txtId.getText().toUpperCase();
            String sUserName = txtUserName.getText().toUpperCase().trim();
            String sPassword = pwdPassword.getText().toUpperCase().trim();
            String sConfirmPassword = pwdConfirmPassword.getText().toUpperCase().trim();
            String sEmpId = txtEmployeeId.getText().toUpperCase().trim();
            String sRoleId = txtRoleId.getText().toUpperCase().trim();
            String sStatus = cbStatus.getValue().toUpperCase().trim();
            String sNote = txtNote.getText().toUpperCase();       

            if(isValidHeaderValues(sUserName, sPassword, sConfirmPassword, sEmpId, sRoleId))
            {
                if(sPassword.equals(sConfirmPassword)) 
                {
                    String salt = PasswordUtils.getSalt(25);
                    String mySecurePassword = PasswordUtils.generateSecurePassword(sPassword, salt);
                    
                    try {
                        if(dbOp.updateRecord(sId, sUserName, mySecurePassword, sEmpId, sRoleId, sStatus, sNote, salt)) 
                        {
                            PopupUtil.showInfoAlert("User Id "+"("+sId+")"+" details updated successfully.");
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
                    }                   
                } else {
                    PopupUtil.showErrorAlert("Password and Confirm Password should be same.");
                }
            } else {
                PopupUtil.showErrorAlert("All mandatory fields should be filled.");
            }                   
        } else {
            PopupUtil.showErrorAlert("No any user selected to update.");
        }                  
    }

    @FXML
    private void btClearAllClicked(ActionEvent event) {
        
        clearAll();
        txtUserName.setText("");
        pwdPassword.setText("");
        pwdConfirmPassword.setText("");        
        cbStatus.getSelectionModel().select(0);
        cbEMployeeName.getSelectionModel().select(0);
        cbRoleName.getSelectionModel().select(0);                
    }

    @FXML
    private void cbAllDetailsFilterOnAction(ActionEvent event) {

        int sIndex = cbAllDetailsFilter.getSelectionModel().getSelectedIndex();
        if(sIndex >= 0) 
        {
            String sFilterName = cbAllDetailsFilter.getItems().get(sIndex);
            if(null != sFilterName) switch (sFilterName) {
                case "Status":
                    nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    if(!nodeAddToFilter.getChildren().contains(cbAddToFilter))
                        nodeAddToFilter.getChildren().add(1, cbAddToFilter);
                    cbAddToFilter.getItems().removeAll(cbAddToFilter.getItems());
                    cbAddToFilter.getItems().addAll("ACTIVE", "BLOCKED");
                    cbAddToFilter.getSelectionModel().select(0);
                    break;
                default:
                    nodeAddToFilter.getChildren().remove(cbAddToFilter);
                    if(!nodeAddToFilter.getChildren().contains(txtAddToFilter))
                        nodeAddToFilter.getChildren().add(1, txtAddToFilter);
                    break;
            }
        }        
    }
    
    @FXML
    private void btAddToFilterClicked(ActionEvent event) {
        
        int sIndex = cbAllDetailsFilter.getSelectionModel().getSelectedIndex();
        
        if(sIndex >= 0) 
        {
            String sFilterName = cbAllDetailsFilter.getItems().get(sIndex);
            String sFilterValue = "";
            if(nodeAddToFilter.getChildren().contains(txtAddToFilter)) {
                sFilterValue = txtAddToFilter.getText();
            }else if(nodeAddToFilter.getChildren().contains(cbAddToFilter)) {
                sFilterValue = cbAddToFilter.getItems().get(cbAddToFilter.getSelectionModel().getSelectedIndex());
            }
            alFilterDBColumnName.add(getDBColumnNameFor(sFilterName));
            alFilterName.add(sFilterName);
            alFilterValue.add(sFilterValue);
            if(txtFilter.getText().length() > 0) {
                txtFilter.setText(txtFilter.getText() + ", "+sFilterName+": "+sFilterValue);
            } else {
                txtFilter.setText(sFilterName+": "+sFilterValue);
            }
            cbAllDetailsFilter.getItems().remove(sFilterName);
            txtAddToFilter.setText("");
        }                        
    }

    public String getDBColumnNameFor(String filterName) 
    {
        switch (filterName)
        {        
            case "Id":
                return "UM.ID";
            case "User Name":
                return "UM.USER_NAME";
            case "Employee Name":
                return "E.NAME";
            case "Role Name":
                return "RM.NAME";
            case "Status":
                return "UM.STATUS";
            default:
                return null;
        }
    }
    
    @FXML
    private void showAllRecordsClicked(ActionEvent event) {
        
        try {
            DataTable allDetailValues = dbOp.getAllDetailsValues(null);
            setAllDetailValuesToField(allDetailValues);
        } catch (SQLException ex) {
            Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }            
    }

    public void setAllDetailValuesToField(DataTable allDetailValues) {

        tbAllDetails.getItems().removeAll(tbAllDetails.getItems());
        for(int i=0; i<allDetailValues.getRowCount(); i++) {            
            String sId = allDetailValues.getRow(i).getColumn(0).toString();
            String sUserName = allDetailValues.getRow(i).getColumn(1).toString();
            String sEmpName = allDetailValues.getRow(i).getColumn(2).toString();
            String sRoleName = allDetailValues.getRow(i).getColumn(3).toString();
            String sStatus = allDetailValues.getRow(i).getColumn(4).toString();
            tbAllDetails.getItems().add(new AllDetailsBean(sId, sUserName, sEmpName, sRoleName, sStatus));
        }        
    }    
    
    @FXML
    private void btFilterUndoOnAction(ActionEvent event) {
         
        cbAllDetailsFilter.getItems().add(alFilterName.get(alFilterName.size()-1));
        alFilterName.remove(alFilterName.size() - 1);
        alFilterValue.remove(alFilterValue.size() - 1);
        alFilterDBColumnName.remove(alFilterDBColumnName.size() - 1);
        String sToSetFilter = "";            
        for(int i=0; i<alFilterName.size(); i++)
        {
            if(sToSetFilter.length() > 0) {
                sToSetFilter += ", " + alFilterName.get(i) + ": " + alFilterValue.get(i);
            } else {
                sToSetFilter += alFilterName.get(i) + ": " + alFilterValue.get(i);
            }
        }
        txtFilter.setText(sToSetFilter);  
    }

    @FXML
    private void btFilterClearAllClicked(ActionEvent event) {
         for(int i=0; i<alFilterName.size(); i++) {        
            cbAllDetailsFilter.getItems().add(alFilterName.get(i));
        }
        
        alFilterName.removeAll(alFilterName);
        alFilterValue.removeAll(alFilterValue);
        alFilterDBColumnName.removeAll(alFilterDBColumnName);
        
        txtFilter.setText(""); 
    }

    @FXML
    private void showFilteredRecordsClicked(ActionEvent event) {
        
        String sFilterScript = "";
        
        for(int i=0; i<alFilterDBColumnName.size(); i++) {            
            if(null != alFilterDBColumnName.get(i)) switch (alFilterDBColumnName.get(i)) {
                case "Status":
                    sFilterScript += "AND " + alFilterDBColumnName.get(i) + " = ?::REPLEDGE_STATUS ";
                    break;
                default:
                    sFilterScript += "AND " + alFilterDBColumnName.get(i) + "::TEXT LIKE ? ";
                    break;
            }
        }

        DataTable allDetailValues;
        try {
            String[] sValsArray = new String[alFilterValue.size()];
            allDetailValues = dbOp.getAllDetailsValues(sFilterScript, alFilterValue.toArray(sValsArray));
            setAllDetailValuesToField(allDetailValues);
        } catch (SQLException ex) {
            Logger.getLogger(UserMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                   
    }

    @FXML
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {          
        if(!hSaveModeButtons.isDisable()) {
            int index = tbAllDetails.getSelectionModel().getSelectedIndex();

            if (event.getClickCount() == 2 && (index >= 0) ) {
                viewUser(tbAllDetails.getItems().get(index).getSId());
            }                
        }
    }
    
    public void viewUser(String sBillNumber) {
    
        tgOff.setSelected(true);
        saveModeOFF(null);
        txtId.setText(sBillNumber);
        txtIdOnAction(null);
        tpScreen.getSelectionModel().select(tabMainScreen);
    }

    @FXML
    private void btFingerPrintClicked(ActionEvent event) {
        
        /*DeviceInfo deviceInfo = new DeviceInfo();
        int ret = mfs100.Init();
        if (ret == 0) {
            deviceInfo = mfs100.GetDeviceInfo();
            if(mfs100.IsConnected()) {
                //if(startCapture()) {
                    startCapture();                    
                    //matchISO();
                //} else {
                
                //}                
            } else {
            
            }
        } else {
            
        } */      
    }    
    
}
