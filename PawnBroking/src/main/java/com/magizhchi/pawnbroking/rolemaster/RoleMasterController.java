/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.rolemaster;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.PopupUtil;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import java.util.ArrayList;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class RoleMasterController implements Initializable {

    public RoleMasterDBOperation dbOp;
    private final String ROLE_ID = "ROLE_ID";
    private String sLastSelectedId = null;
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
    private TextField txtName;
    @FXML
    private Label lbMsg;
    @FXML
    private ToggleButton tgOn;
    @FXML
    private ToggleGroup ViewModeGroup;
    @FXML
    private ToggleButton tgOff;
    @FXML
    private Button btSaveHeader;
    @FXML
    private Button btUpdateHeader;
    @FXML
    private Button btClearAll;
    @FXML
    private TableView<RoleMasterBean> tbMaster;
    @FXML
    private Button btSaveMaster;
    @FXML
    private TableView<RoleMasterBean> tbOperation;
    @FXML
    private ComboBox<String> cbAllDetailsFilter;
    @FXML
    private TextField txtAddToFilter;
    @FXML
    private TextField txtFilter;
    @FXML
    private TableView<AllDetailsBean> tbAllDetails;
    @FXML
    private ComboBox<String> cbStatus;
    @FXML
    private TextArea txtNote;
    @FXML
    private Button btAddMaster;
    @FXML
    private Button btAddOperation;
    @FXML
    private Button btSaveOperation;
    @FXML
    private Button btAddSpecialOptions;
    @FXML
    private Button btSaveSpecialOptions;
    @FXML
    private TableView<RoleMasterBean> tbSpecialOptions;
    @FXML
    private HBox nodeAddToFilter;
    @FXML
    private ComboBox<String> cbAddToFilter;
    @FXML
    private HBox hSaveModeButtons;
    @FXML
    private Button btAddToFilter;
    @FXML
    private Button btShowAllRecords;
    @FXML
    private Button btShowFilteredRecords;
    @FXML
    private TableView<RoleMasterBean> tbReoprts;
    @FXML
    private Button btAddReports;
    @FXML
    private Button btSaveReports;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            dbOp = new RoleMasterDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        try {
            txtId.setText(dbOp.getId(ROLE_ID));
        } catch (SQLException ex) {
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.ROLE_MODULE_SCREEN, "ALLOW_ADD") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btSaveHeader.setDisable(false);
            } else {
                btSaveHeader.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.ROLE_MODULE_SCREEN, "ALLOW_VIEW") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                tgOff.setDisable(false);
            } else {
                tgOff.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        nodeAddToFilter.getChildren().remove(cbAddToFilter);
    }    

    @FXML
    private void txtIdOnAction(ActionEvent event) {
        
        String sId = txtId.getText();
        sLastSelectedId = sId;
        txtName.setText("");
        txtNote.setText("");

        try {
            HashMap<String, String> headerValues = dbOp.getAllHeaderValues(sId);
            DataTable masterTableValues = dbOp.getTableValues(sId, CommonConstants.MASTER_TAB);
            DataTable operationTableValues = dbOp.getTableValues(sId, CommonConstants.OPERATION_TAB);
            DataTable specialOptionTableValues = dbOp.getTableValues(sId, CommonConstants.SPECIAL_OPTIONS_TAB);
            DataTable reportsTableValues = dbOp.getTableValues(sId, CommonConstants.REPORTS_TAB);
            
            if(headerValues != null)
            {
                setAllHeaderValuesToFields(headerValues);
                setTableValues(masterTableValues, tbMaster);
                setTableValues(operationTableValues, tbOperation);
                setTableValues(specialOptionTableValues, tbSpecialOptions);
                setTableValues(reportsTableValues, tbReoprts);
                
                doMasterUpdateModeWork();
                doOperationUpdateModeWork();      
                doSpecialOptionUpdateModeWork();
                doReportsUpdateModeWork();
            } else {
                PopupUtil.showErrorAlert("Sorry invalid id.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doMasterUpdateModeWork() {
        
        btAddMaster.setDisable(false);
        btSaveMaster.setDisable(false);
    }

    public void doOperationUpdateModeWork() {
        
        btAddOperation.setDisable(false);
        btSaveOperation.setDisable(false);
    }

    public void doSpecialOptionUpdateModeWork() {
        
        btAddSpecialOptions.setDisable(false);
        btSaveSpecialOptions.setDisable(false);
    }

    public void doReportsUpdateModeWork() {
        
        btAddReports.setDisable(false);
        btSaveReports.setDisable(false);
    }
    
    public void setAllHeaderValuesToFields(HashMap<String, String> headerValues)
    {
        txtId.setText(headerValues.get("ID"));
        txtName.setText(headerValues.get("NAME"));
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
    private void saveModeON(ActionEvent event) {
        
        sLastSelectedId = ""; 
        doHeaderSaveModeWork();
        doMasterSaveModeWork();
        doOperationSaveModeWork();
        doSpecialOptionsSaveModeWork();
        doReportsSaveModeWork();
    }

    public void doHeaderSaveModeWork() 
    {
        clearAllHeader();
        txtId.setEditable(false);
        txtId.setMouseTransparent(true);
        txtId.setFocusTraversable(false);     
        txtName.setEditable(true);
        txtName.setMouseTransparent(false);
        txtName.setFocusTraversable(true);     
        btUpdateHeader.setDisable(true);
        btSaveHeader.setDisable(false);
        try {
            txtId.setText(dbOp.getId(ROLE_ID));
        } catch (SQLException ex) {
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.ROLE_MODULE_SCREEN, "ALLOW_ADD") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btSaveHeader.setDisable(false);
            } else {
                btSaveHeader.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        txtName.requestFocus();
    }
    
    public void doMasterSaveModeWork() {
    
        btAddMaster.setDisable(true);
        btSaveMaster.setDisable(true);
        tbMaster.getItems().remove(0, tbMaster.getItems().size());
    }

    public void doOperationSaveModeWork() {
    
        btAddOperation.setDisable(true);
        btSaveOperation.setDisable(true);
        tbOperation.getItems().remove(0, tbOperation.getItems().size());
    }

    public void doSpecialOptionsSaveModeWork() {
    
        btAddSpecialOptions.setDisable(true);
        btSaveSpecialOptions.setDisable(true);
        tbSpecialOptions.getItems().remove(0, tbSpecialOptions.getItems().size());
    }

    public void doReportsSaveModeWork() {
    
        btAddReports.setDisable(true);
        btSaveReports.setDisable(true);
        tbReoprts.getItems().remove(0, tbReoprts.getItems().size());
    }
    
    @FXML
    private void saveModeOFF(ActionEvent event) {
        
        doHeaderUpdateModeWork();
    }

    public void doHeaderUpdateModeWork() {
    
        txtName.setText("");
        txtNote.setText("");
        txtId.setText(CommonConstants.ROL_ID_PREFIX);
        txtId.setEditable(true);
        txtId.setMouseTransparent(false);
        txtId.setFocusTraversable(true);     
        txtName.setEditable(false);
        txtName.setMouseTransparent(true);
        txtName.setFocusTraversable(false);     
        btSaveHeader.setDisable(true);   
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.MASTER_TAB, CommonConstants.ROLE_MODULE_SCREEN, "ALLOW_UPDATE") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btUpdateHeader.setDisable(false);
            } else {
                btUpdateHeader.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        txtId.requestFocus();        
        txtId.positionCaret(txtId.getText().length());
    }
    
    @FXML
    private void btSaveHeaderClicked(ActionEvent event) {
        
        sLastSelectedId = "";
        String sId = txtId.getText().toUpperCase();
        String sName = txtName.getText().toUpperCase();
        String sStatus = cbStatus.getValue().toUpperCase();
        String sNote = txtNote.getText().toUpperCase();       
     
        if(isValidHeaderValues(sName))
        {
            try {
                if(dbOp.isvalidNameToSave(sName))
                {
                    if(dbOp.saveRecord(sId, sName, sStatus, sNote)) 
                    {
                        dbOp.setNextId(ROLE_ID, CommonConstants.ROL_ID_PREFIX + (Integer.parseInt(sId.replace(CommonConstants.ROL_ID_PREFIX, ""))+1));
                        txtName.setText("");
                        txtNote.setText("");
                        try {
                            txtId.setText(dbOp.getId(ROLE_ID));
                        } catch (SQLException ex) {
                            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        PopupUtil.showInfoAlert("Role created successfully with id."+"("+sId+")");
                    }
                } else {
                    PopupUtil.showErrorAlert("Sorry same name already exists.");
                }
            } catch (Exception ex) {
                Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            PopupUtil.showErrorAlert("All mandatory fields should be filled.");
        }        
    }

    public boolean isValidHeaderValues(String sName)
    {
        return !sName.isEmpty();
    }
    
    @FXML
    private void btUpdateHeaderClicked(ActionEvent event) {
        
        if(sLastSelectedId != null) {
            
            String sId = txtId.getText().toUpperCase();
            String sName = txtName.getText().toUpperCase();
            String sStatus = cbStatus.getValue().toUpperCase();
            String sNote = txtNote.getText().toUpperCase();       

            if(isValidHeaderValues(sName))
            {
                try {
                    if(dbOp.updateRecord(sId, sName, sStatus, sNote)) 
                    {
                        PopupUtil.showInfoAlert("Role Id "+"("+sId+")"+" details updated successfully.");
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                PopupUtil.showErrorAlert("All mandatory fields should be filled.");
            }
        } else {
            PopupUtil.showErrorAlert("No any repledge selected to update.");
        }        
    }

    @FXML
    private void btClearAllClicked(ActionEvent event) {        
    }


    @FXML
    private void btSaveMasterClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        ObservableList<RoleMasterBean> tableValues = tbMaster.getItems();
                
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAll(sId, CommonConstants.MASTER_TAB)) {
                    if(dbOp.saveRecords(sId, tableValues, CommonConstants.MASTER_TAB)) {
                        PopupUtil.showInfoAlert("Roles for master saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
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
                return "ID";
            case "Role Name":
                return "NAME";
            case "Status":
                return "STATUS";
            default:
                return null;
        }
    }
    
      public void setAllDetailValuesToField(DataTable allDetailValues) {

        tbAllDetails.getItems().removeAll(tbAllDetails.getItems());
        for(int i=0; i<allDetailValues.getRowCount(); i++) {            
            String sId = allDetailValues.getRow(i).getColumn(0).toString();
            String sRoleName = allDetailValues.getRow(i).getColumn(1).toString();
            String sStatus = allDetailValues.getRow(i).getColumn(2).toString();
            tbAllDetails.getItems().add(new AllDetailsBean(sId, sRoleName, sStatus));
        }        
    } 

    @FXML
    private void showAllRecordsClicked(ActionEvent event) {
        try {
            DataTable allDetailValues = dbOp.getAllDetailsValues(null);
            setAllDetailValuesToField(allDetailValues);
        } catch (SQLException ex) {
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }

    @FXML
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {
         if(!hSaveModeButtons.isDisable()) {
            int index = tbAllDetails.getSelectionModel().getSelectedIndex();

            if (event.getClickCount() == 2 && (index >= 0) ) {
                viewRole(tbAllDetails.getItems().get(index).getSId());
            }                
        }
    }
    
     public void viewRole(String sBillNumber) {
    
         tgOff.setSelected(true);
        saveModeOFF(null);
        txtId.setText(sBillNumber);
        txtIdOnAction(null);
        tpScreen.getSelectionModel().select(tabMainScreen);
    }
    @FXML
    private void btAddMasterClicked(ActionEvent event) {
        
        try {            
            DataTable screens = dbOp.getScreens(CommonConstants.MASTER_TAB);
            if(tbMaster.getItems().isEmpty()) {
                setTableValues(screens, tbMaster);
            } else {
                for(int i=0; i<screens.getRowCount(); i++) {            
                    String sScreenName = screens.getRow(i).getColumn(0).toString();
                    boolean boolAdd = false;
                    boolean boolView = false;
                    boolean boolUpdate = false;
                    tbMaster.getItems().add(new RoleMasterBean(sScreenName, boolAdd, boolView, boolUpdate));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setTableValues(DataTable tableValues, TableView table) {

        table.getItems().remove(0, table.getItems().size());
        for(int i=0; i<tableValues.getRowCount(); i++) {            
            String sScreenName = tableValues.getRow(i).getColumn(0).toString();
            boolean boolAdd = Boolean.parseBoolean(tableValues.getRow(i).getColumn(1).toString());
            boolean boolView = Boolean.parseBoolean(tableValues.getRow(i).getColumn(2).toString());
            boolean boolUpdate = Boolean.parseBoolean(tableValues.getRow(i).getColumn(3).toString());
            table.getItems().add(new RoleMasterBean(sScreenName, boolAdd, boolView, boolUpdate));
        }
    }
    
    @FXML
    private void btAddOperationClicked(ActionEvent event) {
        try {            
            DataTable screens = dbOp.getScreens(CommonConstants.OPERATION_TAB);
            
            if(tbOperation.getItems().isEmpty()) {
                setTableValues(screens, tbOperation);
            } else {
                for(int i=0; i<screens.getRowCount(); i++) {            
                    String sScreenName = screens.getRow(i).getColumn(0).toString();
                    boolean boolAdd = false;
                    boolean boolView = false;
                    boolean boolUpdate = false;
                    tbOperation.getItems().add(new RoleMasterBean(sScreenName, boolAdd, boolView, boolUpdate));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btSaveOperationClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        ObservableList<RoleMasterBean> tableValues = tbOperation.getItems();
                
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAll(sId, CommonConstants.OPERATION_TAB)) {
                    if(dbOp.saveRecords(sId, tableValues, CommonConstants.OPERATION_TAB)) {
                        PopupUtil.showInfoAlert("Roles for operation saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                        
    }
    
    public void clearAllHeader()
    {
        txtName.setText("");
        cbStatus.setValue("ACTIVE");        
        txtNote.setText("");        
    }

    @FXML
    private void btAddSpecialOptionsClicked(ActionEvent event) {
        try {            
            DataTable screens = dbOp.getScreens(CommonConstants.SPECIAL_OPTIONS_TAB);
            
            if(tbSpecialOptions.getItems().isEmpty()) {
                setTableValues(screens, tbSpecialOptions);
            } else {
                for(int i=0; i<screens.getRowCount(); i++) {            
                    String sScreenName = screens.getRow(i).getColumn(0).toString();
                    boolean boolAdd = false;
                    boolean boolView = false;
                    boolean boolUpdate = false;
                    tbSpecialOptions.getItems().add(new RoleMasterBean(sScreenName, boolAdd, boolView, boolUpdate));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btSaveSpecialOptionsClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        ObservableList<RoleMasterBean> tableValues = tbSpecialOptions.getItems();
                
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAll(sId, CommonConstants.SPECIAL_OPTIONS_TAB)) {
                    if(dbOp.saveRecords(sId, tableValues, CommonConstants.SPECIAL_OPTIONS_TAB)) {
                        PopupUtil.showInfoAlert("Roles for special options saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                        
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
    private void btAddReportsClicked(ActionEvent event) {
        try {            
            DataTable screens = dbOp.getScreens(CommonConstants.REPORTS_TAB);
            
            if(tbReoprts.getItems().isEmpty()) {
                setTableValues(screens, tbReoprts);
            } else {
                for(int i=0; i<screens.getRowCount(); i++) {            
                    String sScreenName = screens.getRow(i).getColumn(0).toString();
                    boolean boolAdd = false;
                    boolean boolView = false;
                    boolean boolUpdate = false;
                    tbReoprts.getItems().add(new RoleMasterBean(sScreenName, boolAdd, boolView, boolUpdate));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    @FXML
    private void btSaveReportsClicked(ActionEvent event) {
        
        txtId.setText(sLastSelectedId);
        String sId = txtId.getText().toUpperCase();
        ObservableList<RoleMasterBean> tableValues = tbReoprts.getItems();
                
        try {
            if(dbOp.isIdAlreadyExists(sId)) {
                if(dbOp.deleteAll(sId, CommonConstants.REPORTS_TAB)) {
                    if(dbOp.saveRecords(sId, tableValues, CommonConstants.REPORTS_TAB)) {
                        PopupUtil.showInfoAlert("Roles for reports saved successfully to the id("+sId+").");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RoleMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                                
    }
    

}
