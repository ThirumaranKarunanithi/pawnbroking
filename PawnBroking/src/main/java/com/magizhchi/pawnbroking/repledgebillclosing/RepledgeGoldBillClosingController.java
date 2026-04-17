/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.repledgebillclosing;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.companybillclosing.GoldBillClosingController;
import java.io.IOException;
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
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
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
public class RepledgeGoldBillClosingController implements Initializable {

    public Stage dialog;
    private RepledgeBillClosingDBOperation dbOp;
    private String sLastSelectedRepledgeBillId = null;
    private String sLastSelectedRepledgeName = null;
    private String sLastSelectedCompanyBillNumber = null;
    private String sLastSelectedDate = null;
    private String sLastSelectedAmount = null;
    
    private String sMultipleRepBillIds = null;
    private String sMultipleCompanyBillNumber = null;
    private String sReduceType = "";
    private String sMinimumType = "";
    public DataTable dtRepledgeNames;
    private final ArrayList<String> alFilterDBColumnName = new ArrayList<>();
    private final ArrayList<String> alFilterName = new ArrayList<>();
    private final ArrayList<String> alFilterValue = new ArrayList<>();
    private DataTable otherSettingValues = null;    
    final String goldBillClosingScreen = "/com/magizhchi/pawnbroking/companybillclosing/GoldBillClosing.fxml";
    
    boolean byQrCodeScan = false;
    
    @FXML
    private TabPane tpScreen;
    @FXML
    private Tab tabMainScreen;
    @FXML
    private TextField txtRepledgeBillId;
    @FXML
    private TextField txtRepledgeId;
    @FXML
    private ComboBox<String> cbRepledgeName;
    @FXML
    private TextField txtRepledgeBillNumber;
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
    private Label txtSpouseType;
    @FXML
    private TextField txtCustomerName;
    @FXML
    private ToggleGroup rgGenderGroup;
    @FXML
    private TextField txtSpouseName;
    @FXML
    private TextField txtMobileNumber;
    @FXML
    private TextArea txtItems;
    @FXML
    private TextField txtGrossWeight;
    @FXML
    private TextField txtNetWeight;
    @FXML
    private TextField txtPurity;
    @FXML
    private TextField txtAmount;
    @FXML
    private TextField txtInterest;
    @FXML
    private TextField txtTakenAmount;
    @FXML
    private TextField txtToGiveAmount;
    @FXML
    private TextField txtGivenAmount;
    @FXML
    private ComboBox<String> cbStatus;
    @FXML
    private TextArea txtRepledgeNote;
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
    private Label lbActualTotalDaysOrMonths;
    @FXML
    private Label lbMinimumDaysOrMonths;
    @FXML
    private Label lbToReduceDaysOrMonths;
    @FXML
    private TextField txtInterestType;
    @FXML
    private TextField txtActualTotalDaysOrMonths;
    @FXML
    private TextField txtMinimumDaysOrMonths;
    @FXML
    private TextField txtToReduceDaysOrMonths;
    @FXML
    private Label lbTakenDaysOrMonths;
    @FXML
    private TextField txtTakenDaysOrMonths;
    @FXML
    private TextField txtRepledgeOpenedDate;
    @FXML
    private DatePicker dpRepledgeClosingDate;
    @FXML
    private TextField txtRepledgePreStatus;
    @FXML
    private HBox hSaveModeButtons;
    @FXML
    private Tab tabAdvanceAmountDetails1;
    @FXML
    private TableView<CreditDebitBean> tbCreditDetails;
    @FXML
    private TextField txtCreditCount;
    @FXML
    private TextField txtCreditAmount;
    @FXML
    private TableView<CreditDebitBean> tbDebitDetails;
    @FXML
    private TextField txtDebitCount;
    @FXML
    private TextField txtDebitAmount;
    @FXML
    private Button btAddToFilter;
    @FXML
    private Button btShowAllRecords;
    @FXML
    private Button btShowFilteredRecords;
    @FXML
    private TextField txtLatestRebilledNumber;
    @FXML
    private TextField txtLatestRebilledAmount;
    @FXML
    private Button btOpenBCScreen;
    @FXML
    private TextField txtSuspenseDate;
    @FXML
    private TextField txtCompanyBillNumberQrCode;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
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
            /*if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(0).toString())) {
                dpRepledgeClosingDate.setMouseTransparent(false);
                dpRepledgeClosingDate.setFocusTraversable(true);
            } else {
                dpRepledgeClosingDate.setMouseTransparent(true);
                dpRepledgeClosingDate.setFocusTraversable(false);
            }*/
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }

        dpRepledgeClosingDate.setValue(LocalDate.now()); 
        txtRepledgeBillId.setText(CommonConstants.REP_BILL_ID_PREFIX);
        setRepledgeNames();
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, 
                    CommonConstants.OPERATION_TAB, 
                    CommonConstants.REPLEDGE_BILL_CLOSING_SCREEN, "ALLOW_ADD") 
                    || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btSaveBill.setDisable(false);
            } else {
                btSaveBill.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.REPLEDGE_BILL_CLOSING_SCREEN, "ALLOW_VIEW") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
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
            
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                txtCompanyBillNumber.setText(billRowAndNumber[1]);
            }        
            
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        nodeAddToFilter.getChildren().remove(dpAddToFilter);
        nodeAddToFilter.getChildren().remove(cbAddToFilter);            
        
        Platform.runLater(() -> {
            txtCompanyBillNumber.requestFocus();
            txtCompanyBillNumber.positionCaret(txtCompanyBillNumber.getText().length());
        });        
        
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
    private void cbRepledgeNameOnAction(ActionEvent event) {
        
        int index = cbRepledgeName.getSelectionModel().getSelectedIndex();
        String sRepledgeId = dtRepledgeNames.getRow(index).getColumn(0).toString();                
        txtRepledgeId.setText(sRepledgeId);        
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
                    PopupUtil.showErrorAlert(event, "Sorry invalid repledge bill id.");
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
                    PopupUtil.showErrorAlert(event, "Sorry invalid repledge bill id.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
                    PopupUtil.showErrorAlert(event, "Sorry invalid repledge bill number in "+ cbRepledgeName.getValue() +".");
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
                    PopupUtil.showErrorAlert(event, "Sorry invalid repledge bill number in "+ cbRepledgeName.getValue() +".");
                }
            } catch (SQLException ex) {
                Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }        
        }
    }

    @FXML
    private void txtCompanyBillNumberOnAction(ActionEvent event) throws SQLException {
        
        String sCompanyBillNumber = txtCompanyBillNumber.getText();        
        String sOperationName = null;
        
        String sBarcode[] = sCompanyBillNumber.split("<->");
        if(sBarcode.length == 3) {
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                sCompanyBillNumber = 
                        sBarcode[0]
                                .replaceFirst(billRowAndNumber[1], 
                                        "");
                if(sBarcode[1].contains("RBC")) {
                    sOperationName = sBarcode[2];
                    byQrCodeScan = true;
                } else {
                    byQrCodeScan = false;
                }
            }    
        }

        btClearAllClicked(null);
        
        if(tgOn.isSelected()) {
            try {
                String sRepIds = dbOp.getRepIds(sCompanyBillNumber);
                if(sRepIds != null) {
                    if(!sRepIds.contains(",")) {
                        HashMap<String, String> headerValues = dbOp.getAllHeaderValuesByCompanyBillNumber(
                                sCompanyBillNumber, "GOLD");
                        if(headerValues != null) {
                            if(headerValues.get("STATUS").equals("REBILLED-MULTIPLE")) {
                                this.sMultipleRepBillIds =  dbOp.getRepIds(headerValues.get("REBILLED_TO"));
                                this.sMultipleCompanyBillNumber = headerValues.get("REBILLED_TO");
                            }
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
                            if(sOperationName != null ) {
                                if(sOperationName.equals("CLOSE")) {
                                    cbStatus.setValue("RECEIVED");
                                } else if(sOperationName.equals("GET SUSPENSE")) {
                                    cbStatus.setValue("SUSPENSE");
                                }
                            }                            
                            
                            sReduceType = sReduceDatas[1];
                            sMinimumType = sMinimumDatas[1];                    
                            cbRepledgeName.setMouseTransparent(true);
                            cbRepledgeName.setFocusTraversable(false);                      
                            
                            if(byQrCodeScan) {
                                Platform.runLater(() -> {
                                    txtCompanyBillNumberQrCode.requestFocus();
                                    txtCompanyBillNumberQrCode.positionCaret(txtCompanyBillNumberQrCode.getText().length());
                                });        
                            }
                        } else {
                            btClearAllClicked(null);
                            PopupUtil.showErrorAlert(event, "Sorry invalid company bill number.");
                        }
                    } else {                    
                        dialog = new Stage();
                        dialog.initModality(Modality.WINDOW_MODAL);        

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("RepledgeBillClosingDialog.fxml"));
                        Parent root = null;
                        try {            
                            root = (Parent) loader.load();            
                        } catch (IOException ex) {
                            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        RepledgeBillClosingDialogUIController gon = (RepledgeBillClosingDialogUIController) 
                                loader.getController();
                        gon.setParent(this, sCompanyBillNumber , sRepIds);
                        gon.setInitValues(sRepIds);
                        Scene scene = new Scene(root);        
                        dialog.setScene(scene);
                        Screen screen = Screen.getPrimary();
                        Rectangle2D bounds = screen.getVisualBounds();
                        dialog.setX(100);
                        dialog.setY(150);            
                        dialog.setTitle("");
                        dialog.setResizable(false);
                        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
                        dialog.showAndWait();

                    }
                } else {
                    btClearAllClicked(null);
                    PopupUtil.showErrorAlert(event, "Sorry invalid company bill number.");
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
                    PopupUtil.showErrorAlert(event, "Sorry invalid company bill number.");
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
        
        try {
            DataTable companyMISValues = dbOp.getRebilledRepValues(companyValues.get("REPLEDGE_BILL_ID"));
            if(companyMISValues.getRowCount() >= 1) {
                txtLatestRebilledNumber.setText(companyMISValues.getRow(0).getColumn(2).toString());
                txtLatestRebilledAmount.setText(companyMISValues.getRow(0).getColumn(3).toString());
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }        
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
                        
                        if(!DateRelatedCalculations.isMonthsGreaterThanDayMinimum(sStartDate, lTakenDays, 
                                Integer.parseInt(sMinimumDatas[0]))) {
                            sTakenDays = sMinimumDatas[0];
                        }
                    }
                    
                    txtTakenDaysOrMonths.setText(sTakenDays);
                    
                } else if("DAYS".equals(sReduceDatas[1])) {
                    
                    lbToReduceDaysOrMonths.setText("To Reduce Days:");
                    txtToReduceDaysOrMonths.setText(sReduceDatas[0]);
                    long lTakenDays = DateRelatedCalculations.getDifferenceDaysWithDayReduction(sStartDate, 
                            lTotalDays, Integer.parseInt(sReduceDatas[0]));
                    sTakenDays = Long.toString(lTakenDays);
                    
                    if("MONTHS FROM OPENING MONTH".equals(sMinimumDatas[1])) {
                        lbMinimumDaysOrMonths.setText("Minimum Months:");
                        txtMinimumDaysOrMonths.setText(sMinimumDatas[0]);
                        
                        if(!DateRelatedCalculations.isMonthsGreaterThanMonthMinimum(sStartDate, lTakenDays, 
                                Integer.parseInt(sMinimumDatas[0]))) {
                            sTakenDays = Long.toString(DateRelatedCalculations.getDaysInMonths(sStartDate, 
                                    Integer.parseInt(sMinimumDatas[0])));
                        }
                    } else if("DAYS".equals(sMinimumDatas[1])) {
                        lbMinimumDaysOrMonths.setText("Minimum Days:");
                        txtMinimumDaysOrMonths.setText(sMinimumDatas[0]);
                        
                        if(!DateRelatedCalculations
                                .isMonthsGreaterThanDayMinimum(sStartDate, lTakenDays, Integer.parseInt(sMinimumDatas[0]))) {
                            sTakenDays = sMinimumDatas[0];
                        }
                    }
                    
                    txtTakenDaysOrMonths.setText(sTakenDays);
                }
            }
            
            String sFormula = dbOp.getFormula(sRepledgeId, 
                    Double.parseDouble(repledgeValues.get("REPLEDGE_AMOUNT")), "GOLD");
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
            String sToGive = Double.toString((Double.parseDouble(repledgeValues.get("REPLEDGE_AMOUNT")) 
                    + Double.parseDouble(sTakenAmount)));
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
            if(repledgeValues.get("SUSPENSE_DATE") != null) {
                txtSuspenseDate.setText(repledgeValues.get("SUSPENSE_DATE"));
            }
            if(tgOn.isSelected()) {
                cbStatus.setValue("RECEIVED");
            } else {
                cbStatus.setValue(repledgeValues.get("REPLEDGE_STATUS"));
            }
            
            setAllCreditValuesToTable(repledgeValues.get("REPLEDGE_BILL_NUMBER"));
            setAllDebitValuesToTable(repledgeValues.get("REPLEDGE_BILL_NUMBER"));
            closeDateRestriction();
        } catch (SQLException | ScriptException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setAllCreditValuesToTable(String sReBillNumber) {
    
        try {
            DataTable creditDetailValues = dbOp.getCreditTableValues(sReBillNumber, "GOLD");
            
            int count = 0;
            double totalAmount = 0.0;
            
            tbCreditDetails.getItems().removeAll(tbCreditDetails.getItems());

            for(int i=0; i<creditDetailValues.getRowCount(); i++) {            
                String sSlNo = creditDetailValues.getRow(i).getColumn(0).toString();
                String sId = creditDetailValues.getRow(i).getColumn(1).toString();
                String sDate = creditDetailValues.getRow(i).getColumn(2).toString();
                String sAmountToBe = creditDetailValues.getRow(i).getColumn(3).toString();
                String sAmount = creditDetailValues.getRow(i).getColumn(4).toString();

                double dAmountToBe = Double.parseDouble(sAmountToBe);
                double dAmount = Double.parseDouble(sAmount);
                tbCreditDetails.getItems().add(new CreditDebitBean(sSlNo, sId, sDate, dAmountToBe, dAmount));
                count++;
                totalAmount += dAmount;
            }        
            
            txtCreditCount.setText(Integer.toString(count));
            txtCreditAmount.setText(Double.toString(totalAmount));
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setAllDebitValuesToTable(String sReBillNumber) {
    
        try {
            DataTable debitDetailValues = dbOp.getDebitTableValues(sReBillNumber, "GOLD");
            
            int count = 0;
            double totalAmount = 0.0;
            
            tbDebitDetails.getItems().removeAll(tbDebitDetails.getItems());

            for(int i=0; i<debitDetailValues.getRowCount(); i++) {            
                String sSlNo = debitDetailValues.getRow(i).getColumn(0).toString();
                String sId = debitDetailValues.getRow(i).getColumn(1).toString();
                String sDate = debitDetailValues.getRow(i).getColumn(2).toString();
                String sAmountToBe = debitDetailValues.getRow(i).getColumn(3).toString();
                String sAmount = debitDetailValues.getRow(i).getColumn(4).toString();

                double dAmountToBe = Double.parseDouble(sAmountToBe);
                double dAmount = Double.parseDouble(sAmount);
                tbDebitDetails.getItems().add(new CreditDebitBean(sSlNo, sId, sDate, dAmountToBe, dAmount));
                count++;
                totalAmount += dAmount;
            }        
            
            txtDebitCount.setText(Integer.toString(count));
            txtDebitAmount.setText(Double.toString(totalAmount));
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
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
    private void rbToggleChanged(MouseEvent event) {
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
        btSaveBill.setDisable(false);
        btClearAll.setDisable(true);
        btUpdateBill.setDisable(true);
        txtGivenAmount.setEditable(true);
        txtGivenAmount.setMouseTransparent(false);
        txtGivenAmount.setFocusTraversable(true);         
        cbStatus.getSelectionModel().select(0);
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.REPLEDGE_BILL_CLOSING_SCREEN, "ALLOW_ADD") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btSaveBill.setDisable(false);
            } else {
                btSaveBill.setDisable(true);
            }
            
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                txtCompanyBillNumber.setText(billRowAndNumber[1]);
            }                    
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        Platform.runLater(() -> {
            txtCompanyBillNumber.requestFocus();
            txtCompanyBillNumber.positionCaret(txtCompanyBillNumber.getText().length());
        });                        
    }

    @FXML
    private void saveModeOFF(ActionEvent event) {
        
        btClearAllClicked(null);
        btSaveBill.setDisable(true);
        btClearAll.setDisable(true);
        btUpdateBill.setDisable(false);
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
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.REPLEDGE_BILL_CLOSING_SCREEN, "ALLOW_UPDATE") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btUpdateBill.setDisable(false);
            } else {
                btUpdateBill.setDisable(true);
            }
            
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                txtCompanyBillNumber.setText(billRowAndNumber[1]);
            }                    
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        Platform.runLater(() -> {
            txtCompanyBillNumber.requestFocus();
            txtCompanyBillNumber.positionCaret(txtCompanyBillNumber.getText().length());
        });                
    }

    @FXML
    private void btSaveBillClicked(ActionEvent event) {
        
        if(sLastSelectedRepledgeBillId != null) {
            
            txtRepledgeBillId.setText(sLastSelectedRepledgeBillId);
            txtCompanyBillNumber.setText(sLastSelectedCompanyBillNumber);
            
            String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpRepledgeClosingDate.getValue());
            String sGiven = txtGivenAmount.getText().toUpperCase();
            
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(
                            CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, 
                            sBillClosingDate))
            {
                     /* && !sBillClosingDate.equals(sLastSelectedDate) 
                        && !sGiven.equals(sLastSelectedAmount))
                        || (sBillClosingDate.equals(sLastSelectedDate) 
                        && sGiven.equals(sLastSelectedAmount))) */                
                if(DateRelatedCalculations.isFirstDateIsLesserOrEqualToSecondDate(
                        txtRepledgeOpenedDate.getText(), sBillClosingDate))
                {
                    
                    String sStatus = cbStatus.getValue().toUpperCase();

                    if(sStatus.equals("SUSPENSE")) {
                        if(!txtRepledgePreStatus.getText().equals("SUSPENSE")) {
                            try {
                                dbOp.updateSuspense(sLastSelectedRepledgeBillId, sBillClosingDate, sStatus, "GOLD");
                                dbOp.updateCompanyBillPhysicalLocation(sLastSelectedRepledgeBillId, "GOLD");
                                PopupUtil.showInfoAlert(event, "Bill " + sLastSelectedRepledgeBillId +" updated successfully.");  
                            } catch (Exception ex) {
                                Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            PopupUtil.showInfoAlert(event, "Bill " + sLastSelectedRepledgeBillId +" is already in suspense status.");  
                        }
                    } else {

                        String sInterestType = txtInterestType.getText().toUpperCase();
                        String sTotalDaysOrMonths = txtActualTotalDaysOrMonths.getText().toUpperCase();
                        String sMinimumDaysOrMonths = txtMinimumDaysOrMonths.getText().toUpperCase();
                        String sToReduceDaysOrMonths = txtToReduceDaysOrMonths.getText().toUpperCase();
                        String sTakenDaysOrMonths = txtTakenDaysOrMonths.getText().toUpperCase();
                        String sTakenAmount = txtTakenAmount.getText().toUpperCase();
                        String sToGive = txtToGiveAmount.getText().toUpperCase();                                        
                        String sNote = txtNote.getText().toUpperCase();        

                        int iMinimumDaysOrMonths = Integer.parseInt(!("".equals(sMinimumDaysOrMonths))? sMinimumDaysOrMonths : "0");
                        int iReducedDaysOrMonths = Integer.parseInt(!("".equals(sToReduceDaysOrMonths))? sToReduceDaysOrMonths : "0");
                        double dTakenDaysOrMonths = Double.parseDouble(!("".equals(sTakenDaysOrMonths))? sTakenDaysOrMonths : "0");
                        double dTakenAmount = Double.parseDouble(!("".equals(sTakenAmount))? sTakenAmount : "0");  
                        double dToGive = Double.parseDouble(!("".equals(sToGive))? sToGive : "0"); 
                        double dGiven = Double.parseDouble(!("".equals(sGiven))? sGiven : "0");     

                        try {
                            if(isValidHeaderValues(dGiven)) {                                     
                                    if(tgOn.isSelected() 
                                            ? dbOp.closeBill(sLastSelectedRepledgeBillId, sInterestType, sBillClosingDate, 
                                                    sTotalDaysOrMonths, iMinimumDaysOrMonths, iReducedDaysOrMonths, 
                                                    dTakenDaysOrMonths, dTakenAmount, dToGive, dGiven, sStatus, sNote, 
                                                    sReduceType, sMinimumType, "GOLD") 
                                            : dbOp.updateBill(sLastSelectedRepledgeBillId, sInterestType, sBillClosingDate, 
                                                    sTotalDaysOrMonths, iMinimumDaysOrMonths, iReducedDaysOrMonths, 
                                                    dTakenDaysOrMonths, dTakenAmount, dToGive, dGiven, sStatus, sNote, 
                                                    sReduceType, sMinimumType, "GOLD")) {
                                        String sStatusToCompanyBilling = txtCompanyBillStatus.getText().toUpperCase();
                                        dbOp.updateCompanyBillPhysicalLocation(sLastSelectedRepledgeBillId, "GOLD");
                                        
                                        if("RECEIVED".equals(sStatus)) {
                                            String[] lastBill = dbOp.getLastBillNumberAndStatus(sLastSelectedRepledgeBillId);
                                            if(lastBill != null 
                                                    && lastBill[0] != null 
                                                    && (lastBill[2].equals("LOCKED"))) {
                                                sStatusToCompanyBilling = "OPENED";
                                                dbOp.updateCompanyBillStatusToOpened(sLastSelectedRepledgeBillId, 
                                                        sStatusToCompanyBilling, "GOLD");
                                            }

                                            if((sMultipleRepBillIds != null && sMultipleRepBillIds.contains(","))
                                                    || (lastBill != null && lastBill[3].contains(","))) {                                            
                                                StringBuilder sIds = new StringBuilder();
                                                sMultipleRepBillIds = lastBill[3];
                                                for(String sRepBillId : sMultipleRepBillIds.split(",")) {
                                                    if(!sLastSelectedRepledgeBillId.equals(sRepBillId) 
                                                            && sRepBillId.contains("REP")
                                                            && dbOp.isRepBillIdStausOpenedOrGiven(sRepBillId)){
                                                        sIds.append(sRepBillId);
                                                        sIds.append(",");
                                                    }
                                                }
                                                sIds.deleteCharAt(sIds.lastIndexOf(","));
                                                sMultipleRepBillIds = sIds.toString();
                                                dbOp.updateCompanyBillToNewRepBillId(sMultipleCompanyBillNumber, 
                                                        sMultipleRepBillIds, "GOLD", sLastSelectedRepledgeBillId);
                                            }
                                            dbOp.updateCompanyBillToEmpty(sLastSelectedRepledgeBillId, "GOLD");
                                        }
                                        if(tgOn.isSelected()) {
                                            PopupUtil.showInfoAlert(event, "Bill " + sLastSelectedRepledgeBillId +" closed successfully.");  
                                        } else {
                                            PopupUtil.showInfoAlert(event, "Bill " + sLastSelectedRepledgeBillId +" updated successfully.");  
                                        }
                                        btClearAllClicked(null);

                                        String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
                                        if(billRowAndNumber != null) {
                                            txtCompanyBillNumber.setText(billRowAndNumber[1]);
                                        }            
                                        Platform.runLater(() -> {
                                            txtCompanyBillNumber.requestFocus();
                                            txtCompanyBillNumber.positionCaret(txtCompanyBillNumber.getText().length());
                                        });                                        
                                    } else {
                                        PopupUtil.showErrorAlert(event, "Problem in closing bill.");
                                    }
                            } else {
                                PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry you cannot close repledge bill before the opened date.");
                }
                
            } else {
                PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
            }     
        } else {
            PopupUtil.showErrorAlert(event, "Not any bill number was selected to close bill.");
        }       
    }

    public boolean isValidHeaderValues(double dGiven)
    {
        return dGiven > 0;
    }
    
    @FXML
    private void btUpdateBillClicked(ActionEvent event) {
        
        btSaveBillClicked(null);
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
        
        String[] billRowAndNumber = null;
        try {
            billRowAndNumber = dbOp.getGoldCurrentBillNumber();
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(billRowAndNumber != null) {
            txtCompanyBillNumber.setText(billRowAndNumber[1]);
        }         
        
        Platform.runLater(() -> {
            txtCompanyBillNumber.requestFocus();
            txtCompanyBillNumber.positionCaret(txtCompanyBillNumber.getText().length());
        });                
        
    }


    @FXML
    private void cbAllDetailsFilterOnAction(ActionEvent event) {
        
        int sIndex = cbAllDetailsFilter.getSelectionModel().getSelectedIndex();
        
        if(sIndex >= 0) 
        {
            String sFilterName = cbAllDetailsFilter.getItems().get(sIndex);
            if(null != sFilterName) switch (sFilterName) {
                case "REPLEDGE DATE":
                case "COMPANY BILL DATE":
                    nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    nodeAddToFilter.getChildren().remove(cbAddToFilter);
                    if(!nodeAddToFilter.getChildren().contains(dpAddToFilter))
                        nodeAddToFilter.getChildren().add(1, dpAddToFilter);
                    break;
                case "REPLEDGE NAME":
                    nodeAddToFilter.getChildren().remove(dpAddToFilter);
                    nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    if(!nodeAddToFilter.getChildren().contains(cbAddToFilter))
                        nodeAddToFilter.getChildren().add(1, cbAddToFilter);
                    cbAddToFilter.getItems().removeAll(cbAddToFilter.getItems());
                    DataTable repledgeNames;
                    try {
                        repledgeNames = dbOp.getAllRepledgeNames();
                        for(int i=0; i<repledgeNames.getRowCount(); i++) {
                            cbAddToFilter.getItems().add(repledgeNames.getRow(i).getColumn(1).toString());
                        }
                        cbAddToFilter.getSelectionModel().select(0);
                    } catch (SQLException ex) {
                        Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
                    }   break;
                case "REPLEDGE STATUS":
                    nodeAddToFilter.getChildren().remove(dpAddToFilter);
                    nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    if(!nodeAddToFilter.getChildren().contains(cbAddToFilter))
                        nodeAddToFilter.getChildren().add(1, cbAddToFilter);
                    cbAddToFilter.getItems().removeAll(cbAddToFilter.getItems());
                    cbAddToFilter.getItems().addAll("OPENED", "GIVEN");
                    cbAddToFilter.getSelectionModel().select(0);
                    break;
                case "COMPANY BILL STATUS":
                    nodeAddToFilter.getChildren().remove(dpAddToFilter);
                    nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    if(!nodeAddToFilter.getChildren().contains(cbAddToFilter))
                        nodeAddToFilter.getChildren().add(1, cbAddToFilter);
                    cbAddToFilter.getItems().removeAll(cbAddToFilter.getItems());
                    cbAddToFilter.getItems().addAll("OPENED", "LOCKED");
                    cbAddToFilter.getSelectionModel().select(0);
                    break;
                default:
                    nodeAddToFilter.getChildren().remove(dpAddToFilter);
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
            }else if(nodeAddToFilter.getChildren().contains(dpAddToFilter)) {
                sFilterValue = CommonConstants.DATETIMEFORMATTER.format(dpAddToFilter.getValue());
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
            case "REPLEDGE BILL ID":
                return "RB.REPLEDGE_BILL_ID";
            case "REPLEDGE NAME":
                return "RB.REPLEDGE_NAME";
            case "REPLEDGE BILL NO":
                return "RB.REPLEDGE_BILL_NUMBER";
            case "REPLEDGE DATE":
                return "RB.CLOSING_DATE";
            case "REPLEDGE AMOUNT":
                return "RB.AMOUNT";
            case "REPLEDGE STATUS":
                return "RB.STATUS";
            case "COMPANY BILL NO":
                return "CB.BILL_NUMBER";
            case "COMPANY BILL DATE":
                return "CB.OPENING_DATE";
            case "COMPANY AMOUNT":
                return "CB.AMOUNT";
            case "COMPANY BILL STATUS":
                return "CB.STATUS";
            default:
                return null;
        }
    }
    
    @FXML
    private void showAllRecordsClicked(ActionEvent event) {
        
        try {
            DataTable allDetailValues = dbOp.getAllDetailsValues("GOLD", null);
            setAllDetailValuesToField(allDetailValues);
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public void setAllDetailValuesToField(DataTable allDetailValues) {

        tbAllDetails.getItems().removeAll(tbAllDetails.getItems());
        for(int i=0; i<allDetailValues.getRowCount(); i++) {            
            String sRepledgeBillId = allDetailValues.getRow(i).getColumn(0).toString();
            String sRepledgeName = allDetailValues.getRow(i).getColumn(1).toString();
            String sRepledgeBillNumber = allDetailValues.getRow(i).getColumn(2).toString();
            String sRepledgeDate = allDetailValues.getRow(i).getColumn(3).toString();
            String sRepledgeAmount = allDetailValues.getRow(i).getColumn(4).toString();
            String sRepledgeStatus = allDetailValues.getRow(i).getColumn(5).toString();
            String sCompanyBillNumber = allDetailValues.getRow(i).getColumn(6).toString();
            String sCompanyDate = allDetailValues.getRow(i).getColumn(7).toString();
            String sCompanyAmount = allDetailValues.getRow(i).getColumn(8).toString();
            String sCompanyStatus = allDetailValues.getRow(i).getColumn(9).toString();
            tbAllDetails.getItems().add(new AllDetailsBean(sRepledgeBillId, sRepledgeName, sRepledgeBillNumber, sRepledgeDate, sRepledgeAmount, sRepledgeStatus, sCompanyBillNumber, sCompanyDate, sCompanyAmount, sCompanyStatus));
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
                case "RB.CLOSING_DATE":
                case "CB.CLOSING_DATE":
                    sFilterScript += "AND TO_CHAR(" + alFilterDBColumnName.get(i) + ", 'dd-MM-YYYY') ::TEXT LIKE ? ";
                    break;
                case "CB.STATUS":
                    sFilterScript += "AND " + alFilterDBColumnName.get(i) + " = ?::COMPANY_BILL_STATUS ";
                    break;
                case "RB.STATUS":
                    sFilterScript += "AND " + alFilterDBColumnName.get(i) + " = ?::REPLEDGE_BILL_STATUS ";
                    break;
                default:
                    sFilterScript += "AND " + alFilterDBColumnName.get(i) + "::TEXT LIKE ? ";
                    break;
            }
        }

        DataTable allDetailValues;
        try {
            String[] sValsArray = new String[alFilterValue.size()];
            allDetailValues = dbOp.getAllDetailsValues("GOLD", sFilterScript, alFilterValue.toArray(sValsArray));
            setAllDetailValuesToField(allDetailValues);
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }                   
    }

    @FXML
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {
        
        if(!hSaveModeButtons.isDisable()) {
            int index = tbAllDetails.getSelectionModel().getSelectedIndex();

            if (event.getClickCount() == 2 && (index >= 0) ) {

                String sRepledgeBillId = tbAllDetails.getItems().get(index).getSRepledgeBillId();            
                tgOff.setSelected(true);
                saveModeOFF(null);
                txtRepledgeBillId.setText(sRepledgeBillId);
                txtRepledgeBillIdOnAction(null);
                tpScreen.getSelectionModel().select(tabMainScreen);
            }                
        }
    }

    public void tbDialogTablesOnMouseClicked(String sRepledgeBillId, 
            String sCompanyBillNumber, String sRepBillIds) {        
        this.sMultipleRepBillIds = sRepBillIds;
        this.sMultipleCompanyBillNumber = sCompanyBillNumber;
        tgOn.setSelected(true);
        saveModeON(null);
        txtCompanyBillNumber.setText(sCompanyBillNumber);
        txtRepledgeBillId.setText(sRepledgeBillId);
        txtRepledgeBillIdOnAction(null);
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
    
    public void closeBill(String sRepledgeBillId, boolean onlyForView) {
    
        tgOn.setSelected(true);
        saveModeON(null);
        txtRepledgeBillId.setText(sRepledgeBillId);
        txtRepledgeBillIdOnAction(null);
        btSaveBill.setDisable(onlyForView);
        tpScreen.getSelectionModel().select(tabMainScreen);
    }

    public void viewBill(String sRepledgeBillId) {
    
        tgOff.setSelected(true);
        saveModeOFF(null);
        txtRepledgeBillId.setText(sRepledgeBillId);
        txtRepledgeBillIdOnAction(null);
        btUpdateBill.setDisable(true);
        hSaveModeButtons.setDisable(true);
        //dpRepledgeClosingDate.setMouseTransparent(true);
        //dpRepledgeClosingDate.setFocusTraversable(false);
        tpScreen.getSelectionModel().select(tabMainScreen);
    }
    
    public void viewClosedBill(String sRepledgeBillId) {
    
        tgOff.setSelected(true);
        saveModeOFF(null);
        txtRepledgeBillId.setText(sRepledgeBillId);
        txtRepledgeBillIdOnAction(null);
        tpScreen.getSelectionModel().select(tabMainScreen);
    }

    @FXML
    private void btOpenBCScreenClicked(ActionEvent event) {
        
        if(sLastSelectedRepledgeBillId != null) {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(goldBillClosingScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(RepledgeGoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }

            GoldBillClosingController gon = (GoldBillClosingController) loader.getController();
            if(txtCompanyBillStatus.getText().equals("OPENED") || txtCompanyBillStatus.getText().equals("LOCKED")) {
                gon.closeBill(txtCompanyBillNumber.getText(), true);
            } else {
                gon.viewBill(txtCompanyBillNumber.getText());
            }

            dialog.setTitle("Gold Bill Closing");      
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            dialog.setX(0);
            dialog.setY(5);
            dialog.setWidth(bounds.getWidth());
            dialog.setHeight(bounds.getHeight()-5);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.showAndWait();       
        }
    }
    
    private void closeDateRestriction() throws SQLException {
        
        String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpRepledgeClosingDate.getValue());

        if(otherSettingValues.getRowCount() > 0 && 
            !Boolean.valueOf(otherSettingValues.getRow(0).getColumn(0).toString())) {

            if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                    DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                    sBillClosingDate))
            {
                if(tgOn.isSelected()) {
                    if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, 
                            CommonConstants.OPERATION_TAB, 
                            CommonConstants.REPLEDGE_BILL_CLOSING_SCREEN, "ALLOW_ADD") 
                            || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                        btSaveBill.setDisable(false);
                    } else {
                        btSaveBill.setDisable(true);
                    }                    
                } else {
                    if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, 
                            CommonConstants.OPERATION_TAB, 
                            CommonConstants.REPLEDGE_BILL_CLOSING_SCREEN, "ALLOW_UPDATE") 
                            || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                        
                            String sClosingDate = dbOp.getRepBillClosingDate(txtRepledgeBillId.getText());
                            String sClosedDate = CommonConstants.DATETIMEFORMATTER
                                    .format(LocalDate.parse(sClosingDate, CommonConstants.DATETIMEFORMATTER));
                            if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                                    DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                                    sClosedDate)) {
                                btUpdateBill.setDisable(false);
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

    @FXML
    private void txtGivenAmountOnAction(ActionEvent event) {
    }

    @FXML
    private void txtCompanyBillNumberQrCodeOnAction(ActionEvent event) throws SQLException {

        String sCompanyBillNumber = txtCompanyBillNumber.getText();   
        String sCompanyBillNumberQrCode = txtCompanyBillNumberQrCode.getText();        
        
        String sBarcode[] = sCompanyBillNumberQrCode.split("-");
        if(sBarcode.length == 3) {
            if(sBarcode[0].equals(sCompanyBillNumber) && sBarcode[1].equals("G") && sBarcode[2].equals("PACK")
                    && !btSaveBill.isDisable()) {
                btSaveBillClicked(null);
            }
        }        
    }
    
    /*private String[] getLastBillNumberAndStatus(String billNumber) throws SQLException {
        String[] nextBill = new String[3];
        String nextBillNumber = billNumber;
        do {
            nextBill = dbOp.getLastBillNumberAndStatus(nextBillNumber);
            nextBillNumber = nextBill[1];          
            System.out.println(nextBillNumber);
        } while(nextBillNumber != null);
        return nextBill;
    }*/
}
