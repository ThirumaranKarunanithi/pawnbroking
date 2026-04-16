/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.repledgebillopening;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.PopupUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
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
public class RepledgeGoldBillOpeningController implements Initializable {

    private String sMultipleRepBillIds = null;
    public Stage dialog;
    public RepledgeBillOpeningDBOperation dbOp;
    private String sLastSelectedRepledgeBillId = null;
    private String sLastSelectedRepledgeName = null;
    private String sLastSelectedCompanyBillNumber = null;
    public DataTable dtRepledgeNames;
    private DataTable otherSettingValues;
    private final ArrayList<String> alFilterDBColumnName = new ArrayList<>();
    private final ArrayList<String> alFilterName = new ArrayList<>();
    private final ArrayList<String> alFilterValue = new ArrayList<>();
    private final String REPLEDGE_BILL_ID = "REPLEDGE_BILL_ID";
    
    
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
    private DatePicker dpRepledgeOpeningDate;
    @FXML
    private TextField txtCompanyBillNumber;
    @FXML
    private TextField txtCompanyBillStatus;
    @FXML
    private TextField txtCompanyOpenedDate;
    @FXML
    private TextField txtCompanyBillAmount;
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
    private TextArea txtItems;
    @FXML
    private TextField txtGrossWeight;
    @FXML
    private TextField txtNetWeight;
    @FXML
    private TextField txtPurity;
    @FXML
    private TextArea txtNote;
    @FXML
    private TextField txtAmount;
    @FXML
    private TextField txtInterest;
    @FXML
    private TextField txtDocumentCharge;
    @FXML
    private TextField txtTakenAmount;
    @FXML
    private TextField txtToGiveAmount;
    @FXML
    private TextField txtGivenAmount;
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
    private Label txtSpouseType;
    @FXML
    private TextArea txtRepledgeNote;
    @FXML
    private HBox hSaveModeButtons;
    @FXML
    private Button btAddToFilter;
    @FXML
    private Button btShowAllRecords;
    @FXML
    private Button btShowFilteredRecords;
    @FXML
    private DatePicker dpBillCLosingAcceptedDate;
    @FXML
    private ToggleGroup rgAcceptedDateGroup;
    @FXML
    private Button btUpdateRepBillNo;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
            
        try {
            dbOp = new RepledgeBillOpeningDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }

        txtRepledgeBillNumber.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            txtRepledgeBillNumberTextChanged();
        });

        cbStatus.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            Platform.runLater(() -> {
                cbStatusOnAction();
            });
        });
            
        try {
            txtRepledgeBillId.setText(dbOp.getId(REPLEDGE_BILL_ID));
            otherSettingValues = dbOp.getOtherSettingsValues("GOLD");            
            /*if(otherSettingValues != null) {
                if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(0).toString())) {
                    dpRepledgeOpeningDate.setMouseTransparent(false);
                    dpRepledgeOpeningDate.setFocusTraversable(true);
                } else {
                    dpRepledgeOpeningDate.setMouseTransparent(true);
                    dpRepledgeOpeningDate.setFocusTraversable(false);
                }
            }*/
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }

        dpRepledgeOpeningDate.setValue(LocalDate.now());

        setRepledgeNames();
        setBillClosingAcceptedValues(true);
        dpBillCLosingAcceptedDate.setMouseTransparent(false);
        dpBillCLosingAcceptedDate.setFocusTraversable(true);

        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                    CommonConstants.REPLEDGE_BILL_OPENING_SCREEN, "ALLOW_ADD") 
                    || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btSaveBill.setDisable(false);
            } else {
                btSaveBill.setDisable(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                    CommonConstants.REPLEDGE_BILL_OPENING_SCREEN, "ALLOW_VIEW") 
                    || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
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
            Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        nodeAddToFilter.getChildren().remove(dpAddToFilter);
        nodeAddToFilter.getChildren().remove(cbAddToFilter);

        Platform.runLater(() -> {
            txtCompanyBillNumber.requestFocus();
            txtCompanyBillNumber.positionCaret(txtCompanyBillNumber.getText().length());
        });        
    }    

    public void setBillClosingAcceptedValues(boolean selectFirstVal) {
        
        Platform.runLater(()->{            
            RadioButton selectedRadioButton = (RadioButton) rgAcceptedDateGroup.getSelectedToggle();    
            String closingDate = null;
            if(null != selectedRadioButton.getText()) switch (selectedRadioButton.getText()) {
                case "1Y":
                    closingDate = DateRelatedCalculations.getAfterYearOrMonDateWithFormatted(CommonConstants.DATETIMEFORMATTER.format(dpRepledgeOpeningDate.getValue()), 
                                    Calendar.YEAR, 1);
                    break;
                case "9M":
                    closingDate = DateRelatedCalculations.getAfterYearOrMonDateWithFormatted(CommonConstants.DATETIMEFORMATTER.format(dpRepledgeOpeningDate.getValue()), 
                                    Calendar.MONTH, 9);
                    break;
                case "6M":
                    closingDate = DateRelatedCalculations.getAfterYearOrMonDateWithFormatted(CommonConstants.DATETIMEFORMATTER.format(dpRepledgeOpeningDate.getValue()), 
                                    Calendar.MONTH, 6);
                    break;
                case "3M":
                    closingDate = DateRelatedCalculations.getAfterYearOrMonDateWithFormatted(CommonConstants.DATETIMEFORMATTER.format(dpRepledgeOpeningDate.getValue()), 
                                    Calendar.MONTH, 3);
                    break;
                case "1M":
                    closingDate = DateRelatedCalculations.getAfterYearOrMonDateWithFormatted(CommonConstants.DATETIMEFORMATTER.format(dpRepledgeOpeningDate.getValue()), 
                                    Calendar.MONTH, 1);
                    break;
                default:
                    closingDate = DateRelatedCalculations.getAfterYearOrMonDateWithFormatted(CommonConstants.DATETIMEFORMATTER.format(dpRepledgeOpeningDate.getValue()), 
                                    Calendar.YEAR, 1);
                    break;
            }        
            dpBillCLosingAcceptedDate.setValue(LocalDate.parse(closingDate, CommonConstants.DATETIMEFORMATTER));
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
            Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }                
        
    }
    
    @FXML
    private void cbRepledgeNameOnAction(ActionEvent event) {
        
        int index = cbRepledgeName.getSelectionModel().getSelectedIndex();
        String sRepledgeId = dtRepledgeNames.getRow(index).getColumn(0).toString();                
        txtRepledgeId.setText(sRepledgeId);                
        String sAmount = txtAmount.getText();
        if(!"".equals(sAmount)) {
            double dAmount = Double.parseDouble(sAmount);
            setAmountRelatedText(sRepledgeId, dAmount, "GOLD");
        }
    }
    
    @FXML
    private void txtCompanyBillNumberOnAction(ActionEvent event) throws SQLException {
        
        String sCompanyBillNumber = txtCompanyBillNumber.getText();  
        String sInstructedRepName = null;
        String sInstructedAmount = null;
        
        String sBarcode[] = sCompanyBillNumber.split("<->");
        if(sBarcode.length == 4) {
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                sCompanyBillNumber = sBarcode[0].replaceFirst(billRowAndNumber[1], "");
                if(sBarcode[1].contains("RBO")) {
                    sInstructedRepName = sBarcode[2];
                    sInstructedAmount = sBarcode[3];
                }
            }    
        }
        btClearAllClicked(null);
        
        if(tgOn.isSelected()) {
            try {
                txtRepledgeBillId.setText(dbOp.getId(REPLEDGE_BILL_ID));               
                String sAlreadyExistsRBName = dbOp.getRepledgeBillNameForCompanyBillNumber(sCompanyBillNumber, "GOLD");            
                if(sAlreadyExistsRBName == null) 
                {
                    HashMap<String, String> companyValues = dbOp.getAllCompanyValues(sCompanyBillNumber, "GOLD");
                    if(companyValues != null)
                    {
                            setAllCompanyValuesToFields(companyValues);
                            sLastSelectedRepledgeBillId = txtRepledgeBillId.getText();
                            sLastSelectedRepledgeName = cbRepledgeName.getValue();
                            sLastSelectedCompanyBillNumber = sCompanyBillNumber;
                            if(sInstructedRepName != null) {
                                cbRepledgeName.setValue(sInstructedRepName);
                            }
                            
                            txtAmount.setText(companyValues.get("AMOUNT"));
                            if(sInstructedAmount != null) {
                                txtAmount.setText(sInstructedAmount);
                            }
                            double dAmount = Double.parseDouble(txtAmount.getText());
                            String sRepledgeId = txtRepledgeId.getText();
                            setAmountRelatedText(sRepledgeId, dAmount, "GOLD");
                            txtRepledgeBillNumber.requestFocus();
                    } else {
                        btClearAllClicked(null);
                        PopupUtil.showErrorAlert(event, "Sorry invalid bill number.");
                    }
                } else {
                    btClearAllClicked(null);
                    PopupUtil.showErrorAlert(event, "This bill number already exists in "+sAlreadyExistsRBName+".");
                }
                txtRepledgeBillId.setText(dbOp.getId(REPLEDGE_BILL_ID));
            } catch (SQLException ex) {
                Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if(tgOff.isSelected()) {
            try {
                String sRepIds = dbOp.getRepIds(sCompanyBillNumber);
                if(sRepIds != null && !sRepIds.contains(",")) {
                    HashMap<String, String> headerValues = dbOp.getAllHeaderValuesByCompanyBillNumber(sCompanyBillNumber, "GOLD");
                    if(headerValues != null) {
                        setAllCompanyValuesToFields(headerValues);
                        setAllRepledgeValuesToFields(headerValues);
                        sLastSelectedRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID");  
                        sLastSelectedRepledgeName = headerValues.get("REPLEDGE_NAME");  
                        sLastSelectedCompanyBillNumber = headerValues.get("BILL_NUMBER");  
                        if(cbStatus.getValue().equals("GIVEN")) {
                            btUpdateRepBillNo.setDisable(true);
                        }
                        if(otherSettingValues != null) {
                            if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(1).toString())) {
                                cbRepledgeName.setMouseTransparent(false);
                                cbRepledgeName.setFocusTraversable(true);                      
                            } else {
                                cbRepledgeName.setMouseTransparent(true);
                                cbRepledgeName.setFocusTraversable(false);                      
                            }
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
                    gon.setParent(this);
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
                } catch (SQLException ex) {
                Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
        closeDateRestriction();
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
        txtDoorNo.setText(companyValues.get("DOOR_NUMBER"));
        txtStreetName.setText(companyValues.get("STREET"));
        txtArea.setText(companyValues.get("AREA"));
        txtCity.setText(companyValues.get("CITY"));
        txtMobileNumber.setText(companyValues.get("MOBILE_NUMBER"));
        txtItems.setText(companyValues.get("ITEMS"));
        txtGrossWeight.setText(companyValues.get("GROSS_WEIGHT"));
        txtNetWeight.setText(companyValues.get("NET_WEIGHT"));
        txtPurity.setText(companyValues.get("PURITY"));       
        txtNote.setText(companyValues.get("NOTE"));
    }
    
    public void setAllRepledgeValuesToFields(HashMap<String, String> repledgeValues) {
        
        txtRepledgeBillId.setText(repledgeValues.get("REPLEDGE_BILL_ID"));
        txtRepledgeId.setText(repledgeValues.get("REPLEDGE_ID"));
        cbRepledgeName.setValue(repledgeValues.get("REPLEDGE_NAME"));
        txtRepledgeBillNumber.setText(repledgeValues.get("REPLEDGE_BILL_NUMBER"));
        dpRepledgeOpeningDate.setValue(LocalDate.parse(repledgeValues.get("REPLEDGE_OPENING_DATE"), CommonConstants.DATETIMEFORMATTER));
        txtAmount.setText(repledgeValues.get("REPLEDGE_AMOUNT"));
        txtInterest.setText(repledgeValues.get("REPLEDGE_INTEREST"));
        txtDocumentCharge.setText(repledgeValues.get("REPLEDGE_DOCUMENT_CHARGE"));
        txtTakenAmount.setText(repledgeValues.get("REPLEDGE_OPEN_TAKEN_AMOUNT"));
        txtToGiveAmount.setText(repledgeValues.get("REPLEDGE_OPEN_TOGET_AMOUNT"));
        txtGivenAmount.setText(repledgeValues.get("REPLEDGE_GOT_AMOUNT"));
        cbStatus.setValue(repledgeValues.get("REPLEDGE_STATUS"));
        txtNote.setText(repledgeValues.get("REPLEDGE_NOTE"));
        
        if(repledgeValues.get("REPLEDGE_ACC_CLOSING_DATE") != null) {
            String sAccClosingDate = repledgeValues.get("REPLEDGE_ACC_CLOSING_DATE");
            dpBillCLosingAcceptedDate.setValue(LocalDate.parse(sAccClosingDate, CommonConstants.DATETIMEFORMATTER));

            String sAccDateOption = DateRelatedCalculations.getAcceptedClosingDuration(
                    repledgeValues.get("REPLEDGE_OPENING_DATE"), sAccClosingDate);
            if(null != sAccDateOption) switch (sAccDateOption) {
                case "1Y":
                    rgAcceptedDateGroup.getToggles().get(0).setSelected(true);
                    break;
                case "9M":
                    rgAcceptedDateGroup.getToggles().get(1).setSelected(true);
                    break;
                case "6M":
                    rgAcceptedDateGroup.getToggles().get(2).setSelected(true);
                    break;
                case "3M":
                    rgAcceptedDateGroup.getToggles().get(3).setSelected(true);
                    break;
                case "1M":
                    rgAcceptedDateGroup.getToggles().get(4).setSelected(true);
                    break;
                default:
                    rgAcceptedDateGroup.getToggles().get(0).setSelected(true);
                    break;
            }
        }
    }
    
    private void cbStatusOnAction() {
        
    }
    
    @FXML
    private void rbToggleChanged(MouseEvent event) {
    }
    
    @FXML
        private void capitalizeCharOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();
        int caretPos = txt_TextField.getCaretPosition();
        txt_TextField.setText(txt_TextField.getText().toUpperCase());
        txt_TextField.positionCaret(caretPos);
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
    private void txtAmountOnPress(KeyEvent e) {
        if (e.getCode() == KeyCode.BACK_SPACE) {
            Platform.runLater(() -> {
                String sAmount = txtAmount.getText().trim();
                if (!sAmount.isEmpty()) {
                    try {
                        double dAmount = Double.parseDouble(sAmount);
                        if (dAmount > 0) {
                            String sRepledgeId = txtRepledgeId.getText();
                            setAmountRelatedText(sRepledgeId, dAmount, "GOLD");
                        } else {
                            txtInterest.setText("");
                            txtDocumentCharge.setText("");
                            txtTakenAmount.setText("");
                            txtToGiveAmount.setText("");
                            txtGivenAmount.setText("");
                        }
                    } catch (NumberFormatException ex) {
                        txtInterest.setText("");
                        txtDocumentCharge.setText("");
                        txtTakenAmount.setText("");
                        txtToGiveAmount.setText("");
                        txtGivenAmount.setText("");
                    }
                } else {
                    txtInterest.setText("");
                    txtDocumentCharge.setText("");
                    txtTakenAmount.setText("");
                    txtToGiveAmount.setText("");
                    txtGivenAmount.setText("");
                }
            });
        }
    }

    @FXML
    private void txtAmountOnType(KeyEvent e) {
        if (!("0123456789.".contains(e.getCharacter()))) {
            e.consume();
            return;
        }
        Platform.runLater(() -> {
            String sAmount = txtAmount.getText().trim();
            if (!sAmount.isEmpty()) {
                try {
                    double dAmount = Double.parseDouble(sAmount);
                    if (dAmount > 0) {
                        String sRepledgeId = txtRepledgeId.getText();
                        setAmountRelatedText(sRepledgeId, dAmount, "GOLD");
                    }
                } catch (NumberFormatException ex) { /* ignore partial input */ }
            }
        });
    }

    public void setAmountRelatedText(String sRepledgeId, double dAmount, String sMaterialType) {
    
        try {

            String sInterest = dbOp.getInterest(CommonConstants.DATETIMEFORMATTER.format(dpRepledgeOpeningDate.getValue()), sRepledgeId, dAmount, "GOLD").trim();
            String sDocumentCharge = dbOp.getDocumentCharge(CommonConstants.DATETIMEFORMATTER.format(dpRepledgeOpeningDate.getValue()), sRepledgeId, dAmount, "GOLD").trim();
            String sFormula = dbOp.getFormula(CommonConstants.DATETIMEFORMATTER.format(dpRepledgeOpeningDate.getValue()), sRepledgeId, dAmount, "GOLD");
            
            String[][] replacements = {{"AMOUNT", String.valueOf(dAmount)}, 
                                       {"INTEREST", sInterest},
                                       {"DOCUMENT_CHARGE", sDocumentCharge}};            

            for(String[] replacement: replacements) {
                sFormula = sFormula.replace(replacement[0], replacement[1]);
            }

            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");         
            String sTakenAmount = String.valueOf(Math.round(Double.parseDouble(engine.eval(sFormula).toString())));
            String sToGive = Double.toString(dAmount - Double.parseDouble(sTakenAmount));
            
            txtInterest.setText(sInterest);
            txtDocumentCharge.setText(sDocumentCharge);
            txtTakenAmount.setText(sTakenAmount);
            txtToGiveAmount.setText(sToGive);
            txtGivenAmount.setText(sToGive);
        } catch (SQLException | ScriptException ex) {
            Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @FXML
    private void saveModeON(ActionEvent event) {
        
        btClearAllClicked(null);
        txtRepledgeBillId.setText("");
        txtRepledgeBillNumber.setText("");
        try {
            txtRepledgeBillId.setText(dbOp.getId(REPLEDGE_BILL_ID));
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }
        btSaveBill.setDisable(false);
        btUpdateBill.setDisable(true);
        btUpdateRepBillNo.setDisable(true);
        txtRepledgeBillId.setEditable(false);
        txtRepledgeBillId.setMouseTransparent(true);
        txtRepledgeBillId.setFocusTraversable(false); 
        txtAmount.setEditable(true);
        txtAmount.setMouseTransparent(false);
        txtAmount.setFocusTraversable(true); 
        txtGivenAmount.setEditable(true);
        txtGivenAmount.setMouseTransparent(false);
        txtGivenAmount.setFocusTraversable(true); 
        cbStatus.getItems().addAll("OPENED");
        cbStatus.setValue("OPENED");
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.REPLEDGE_BILL_OPENING_SCREEN, "ALLOW_ADD") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btSaveBill.setDisable(false);
            } else {
                btSaveBill.setDisable(true);
            }
            
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                txtCompanyBillNumber.setText(billRowAndNumber[1]);
            }                    
            
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
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
        btUpdateBill.setDisable(false);
        btUpdateRepBillNo.setDisable(false);
        txtRepledgeBillId.setText("");
        txtRepledgeBillNumber.setText("");
        txtRepledgeBillId.setText(CommonConstants.REP_BILL_ID_PREFIX);
        txtRepledgeBillId.setEditable(true);
        txtRepledgeBillId.setMouseTransparent(false);
        txtRepledgeBillId.setFocusTraversable(true);    
        if(otherSettingValues != null) {
            if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(2).toString())) {
                txtAmount.setEditable(true);
                txtAmount.setMouseTransparent(false);
                txtAmount.setFocusTraversable(true); 
                txtGivenAmount.setEditable(true);
                txtGivenAmount.setMouseTransparent(false);
                txtGivenAmount.setFocusTraversable(true); 
            } else {
                txtAmount.setEditable(false);
                txtAmount.setMouseTransparent(true);
                txtAmount.setFocusTraversable(false); 
                txtGivenAmount.setEditable(false);
                txtGivenAmount.setMouseTransparent(true);
                txtGivenAmount.setFocusTraversable(false); 
            }
        }
        cbStatus.getItems().removeAll(cbStatus.getItems());
        
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.REPLEDGE_BILL_OPENING_SCREEN, "ALLOW_UPDATE") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btUpdateBill.setDisable(false);
                btUpdateRepBillNo.setDisable(false);
            } else {
                btUpdateBill.setDisable(true);
                btUpdateRepBillNo.setDisable(true);
            }
            
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                txtCompanyBillNumber.setText(billRowAndNumber[1]);
            }                    
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        Platform.runLater(() -> {
            txtCompanyBillNumber.requestFocus();
            txtCompanyBillNumber.positionCaret(txtCompanyBillNumber.getText().length());
        });        
    }

    @FXML
    private void txtRepledgeBillIdOnAction(ActionEvent event) {
        
        if(tgOff.isSelected()) {
            String sRepledgeBillId = txtRepledgeBillId.getText();
            btClearAllClicked(null);

            try {

                HashMap<String, String> headerValues = dbOp.getAllHeaderValuesByRepledgeBillId(sRepledgeBillId, "GOLD");
                if(headerValues != null) {
                    setAllCompanyValuesToFields(headerValues);
                    setAllRepledgeValuesToFields(headerValues);
                    sLastSelectedRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID");  
                    sLastSelectedRepledgeName = headerValues.get("REPLEDGE_NAME");  
                    sLastSelectedCompanyBillNumber = headerValues.get("BILL_NUMBER");  
                    if(cbStatus.getValue().equals("GIVEN")) {
                        btUpdateRepBillNo.setDisable(true);
                    }                    
                    if(otherSettingValues != null) {
                        if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(1).toString())) {
                            cbRepledgeName.setMouseTransparent(false);
                            cbRepledgeName.setFocusTraversable(true);                      
                        } else {
                            cbRepledgeName.setMouseTransparent(true);
                            cbRepledgeName.setFocusTraversable(false);                      
                        }
                    }
                } else {
                    btClearAllClicked(null);
                    PopupUtil.showErrorAlert(event, "Sorry invalid repledge bill id.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            closeDateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void txtRepledgeBillNumberOnAction(ActionEvent event) {
    
        if(tgOff.isSelected()) {
            String sRepledgeId = txtRepledgeId.getText();
            String sRepledgeBillNumber = txtRepledgeBillNumber.getText();
            btClearAllClicked(null);        

            try {

                HashMap<String, String> headerValues = dbOp.getAllHeaderValuesByRepledgeBillNumber(sRepledgeId, sRepledgeBillNumber, "GOLD");
                if(headerValues != null) {
                    setAllCompanyValuesToFields(headerValues);
                    setAllRepledgeValuesToFields(headerValues);
                    sLastSelectedRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID"); 
                    sLastSelectedRepledgeName = headerValues.get("REPLEDGE_NAME");  
                    sLastSelectedCompanyBillNumber = headerValues.get("BILL_NUMBER");  
                    if(cbStatus.getValue().equals("GIVEN")) {
                        btUpdateRepBillNo.setDisable(true);
                    }                    
                    if(otherSettingValues != null) {
                        if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(1).toString())) {
                            cbRepledgeName.setMouseTransparent(false);
                            cbRepledgeName.setFocusTraversable(true);                      
                        } else {
                            cbRepledgeName.setMouseTransparent(true);
                            cbRepledgeName.setFocusTraversable(false);                      
                        }
                    }
                } else {
                    btClearAllClicked(null);
                    PopupUtil.showErrorAlert(event, "Sorry invalid repledge bill number in "+ cbRepledgeName.getValue() +".");
                }
            } catch (SQLException ex) {
                Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }        
        }
        try {
            closeDateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void btSaveBillClicked(ActionEvent event) {
        
        try {
            HashMap<String, String> companyValues = dbOp.getAllCompanyValues(sLastSelectedCompanyBillNumber, "GOLD");
            if(sLastSelectedCompanyBillNumber != null && companyValues != null)
            {
                txtCompanyBillNumber.setText(sLastSelectedCompanyBillNumber); 
                String sRepledgeOpeningDate = CommonConstants.DATETIMEFORMATTER.format(dpRepledgeOpeningDate.getValue());
                
                if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE,
                        sRepledgeOpeningDate))
                {
                    if(DateRelatedCalculations.isFirstDateIsLesserOrEqualToSecondDate(txtCompanyOpenedDate.getText(),
                            sRepledgeOpeningDate))
                    {
                        
                        String sRepledgeBillId = txtRepledgeBillId.getText();
                        String sRepledgeId = txtRepledgeId.getText();
                        String sRepledgeName = cbRepledgeName.getValue().toUpperCase();
                        String sRepledgeBillNumber = txtRepledgeBillNumber.getText();
                        String sCompanyBillNumber = txtCompanyBillNumber.getText().toUpperCase();
                        String sAmount = txtAmount.getText().toUpperCase();
                        String sInterest = txtInterest.getText().toUpperCase();
                        String sDocumentCharge = txtDocumentCharge.getText().toUpperCase();
                        String sTakenAmount = txtTakenAmount.getText().toUpperCase();
                        String sToGetAmount = txtToGiveAmount.getText().toUpperCase();
                        String sGotAmount = txtGivenAmount.getText().toUpperCase();
                        String sStatus = cbStatus.getValue().toUpperCase();
                        String sNote = txtRepledgeNote.getText().toUpperCase();
                        String sCompanyOpeningDate = txtCompanyOpenedDate.getText();
                        String sAcceptedDate = CommonConstants.DATETIMEFORMATTER.format(dpBillCLosingAcceptedDate.getValue());
                        
                        double dAmount = Double.parseDouble(!("".equals(sAmount))? sAmount : "0");
                        double dInterest = Double.parseDouble(!("".equals(sInterest))? sInterest : "0");
                        double dDocumentCharge = Double.parseDouble(!("".equals(sDocumentCharge))? sDocumentCharge : "0");
                        double dTakenAmount = Double.parseDouble(!("".equals(sTakenAmount))? sTakenAmount : "0");
                        double dToGetAmount = Double.parseDouble(!("".equals(sToGetAmount))? sToGetAmount : "0");
                        double dGotAmount = Double.parseDouble(!("".equals(sGotAmount))? sGotAmount : "0");
                        
                        try {
                            if(sLastSelectedCompanyBillNumber != null
                                    && isValidHeaderValues(sRepledgeBillId, sRepledgeId, sRepledgeName,
                                            sRepledgeOpeningDate, sCompanyOpeningDate, sCompanyBillNumber,
                                            sAmount, sGotAmount)) {
                                
                                if(dbOp.saveRecord(sRepledgeBillId, sRepledgeId, sRepledgeName, 
                                        sRepledgeBillNumber, sRepledgeOpeningDate, sCompanyBillNumber, 
                                        sStatus, sNote, dAmount, dInterest, dDocumentCharge, dTakenAmount, 
                                        dToGetAmount, dGotAmount, sAcceptedDate, "GOLD")) {
                                                             
                                    dbOp.updateRepledgeBillIdToCompanyBilling(sCompanyBillNumber, sRepledgeBillId, "GOLD");
                                    if(dbOp.setNextId(REPLEDGE_BILL_ID, CommonConstants.REP_BILL_ID_PREFIX + (Integer.parseInt(sRepledgeBillId.replace(CommonConstants.REP_BILL_ID_PREFIX, ""))+1))) {
                                        txtRepledgeBillId.setText("");
                                        txtRepledgeBillId.setText(dbOp.getId(REPLEDGE_BILL_ID));
                                    }
                                    PopupUtil.showInfoAlert("Bill created successfully");
                                    btClearAllClicked(null);
                                    txtRepledgeBillId.setText(dbOp.getId(REPLEDGE_BILL_ID));
                                    cbStatus.getSelectionModel().select("OPENED");
                                    
                                    String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
                                    if(billRowAndNumber != null) {
                                        txtCompanyBillNumber.setText(billRowAndNumber[1]);
                                    }
                                    Platform.runLater(() -> {
                                        txtCompanyBillNumber.requestFocus();
                                        txtCompanyBillNumber.positionCaret(txtCompanyBillNumber.getText().length());
                                    });
                                }
                                
                            } else {
                                PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly. \n Note: Repledge date should be lesser than bill opened date.");                                        
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        PopupUtil.showErrorAlert(event, "Sorry you cannot open repledge bill before the company bill opened date.");
                    }
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
                }
            } else {
                PopupUtil.showErrorAlert(event, "Sorry not any company bill number given properly.");
                btClearAllClicked(null);
                try {
                    txtRepledgeBillId.setText(dbOp.getId(REPLEDGE_BILL_ID));
                } catch (SQLException ex) {
                    Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isValidHeaderValues(String sRepledgeBillId, String sRepledgeId, String sRepledgeName, 
                                        String sRepledgeOpeningDate, String sCompanyOpeningDate, 
                                        String sCompanyBillNumber, String sAmount, String sGotAmount)
    {
        if(!sRepledgeBillId.isEmpty() && !sRepledgeId.isEmpty() && !sRepledgeName.isEmpty() 
                && !sRepledgeOpeningDate.isEmpty() 
                && !sCompanyBillNumber.isEmpty() 
                && !sAmount.isEmpty() && !sGotAmount.isEmpty()) {
            return Double.parseDouble(sAmount) > 0 && DateRelatedCalculations.getDifferenceDays(sCompanyOpeningDate, sRepledgeOpeningDate) >= 0;
        } else {
            return false;
        }
    }
    
    @FXML
    private void btUpdateBillClicked(ActionEvent event) {
        
        if(sLastSelectedCompanyBillNumber != null && sLastSelectedRepledgeBillId != null) 
        {
            txtRepledgeBillId.setText(sLastSelectedRepledgeBillId);
            txtCompanyBillNumber.setText(sLastSelectedCompanyBillNumber);
            if(otherSettingValues != null) {
                if(!Boolean.valueOf(otherSettingValues.getRow(0).getColumn(1).toString())) {
                    cbRepledgeName.setValue(sLastSelectedRepledgeName);
                }
            }
            String sRepledgeBillId = txtRepledgeBillId.getText();
            String sRepledgeId = txtRepledgeId.getText();
            String sRepledgeName = cbRepledgeName.getValue().toUpperCase();
            String sRepledgeBillNumber = txtRepledgeBillNumber.getText();
            String sRepledgeOpeningDate = CommonConstants.DATETIMEFORMATTER
                    .format(dpRepledgeOpeningDate.getValue());
            String sCompanyBillNumber = txtCompanyBillNumber.getText().toUpperCase();
            String sAmount = txtAmount.getText().toUpperCase();
            String sInterest = txtInterest.getText().toUpperCase();
            String sDocumentCharge = txtDocumentCharge.getText().toUpperCase();
            String sTakenAmount = txtTakenAmount.getText().toUpperCase();
            String sToGetAmount = txtToGiveAmount.getText().toUpperCase();
            String sGotAmount = txtGivenAmount.getText().toUpperCase();      
            String sStatus = cbStatus.getValue().toUpperCase();
            String sNote = txtRepledgeNote.getText().toUpperCase();        
            String sCompanyOpeningDate = txtCompanyOpenedDate.getText();
            String sAcceptedDate = CommonConstants.DATETIMEFORMATTER
                    .format(dpBillCLosingAcceptedDate.getValue());
            
            double dAmount = Double.parseDouble(!("".equals(sAmount))? sAmount : "0");
            double dInterest = Double.parseDouble(!("".equals(sInterest))? sInterest : "0");
            double dDocumentCharge = Double.parseDouble(!("".equals(sDocumentCharge))? sDocumentCharge : "0");              
            double dTakenAmount = Double.parseDouble(!("".equals(sTakenAmount))? sTakenAmount : "0");  
            double dToGetAmount = Double.parseDouble(!("".equals(sToGetAmount))? sToGetAmount : "0"); 
            double dGotAmount = Double.parseDouble(!("".equals(sGotAmount))? sGotAmount : "0");      

            //if(!sRepledgeBillNumber.isEmpty()) {
                if(isValidHeaderValues(sRepledgeBillId, sRepledgeId, sRepledgeName, sRepledgeOpeningDate, sCompanyOpeningDate, sCompanyBillNumber, sAmount, sGotAmount)) {
                    try {
                        if(dbOp.updateRecordToRepledgeBilling(sRepledgeBillId, sRepledgeId, sRepledgeName, 
                                sRepledgeBillNumber, sRepledgeOpeningDate, sStatus, sNote, dAmount, dInterest, 
                                dDocumentCharge, dTakenAmount, dToGetAmount, dGotAmount, sAcceptedDate)) {
                            PopupUtil.showInfoAlert("Bill Updated successfully.");
                            btClearAllClicked(null);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly. \n Note: Repledge date should be lesser than bill opened date.");
                }  
            //} else {
                //PopupUtil.showInfoAlert("Repledge bill number cannot be empty to update the bill.");
            //}
        } else {
            PopupUtil.showInfoAlert(event, "Not any bill selected properly.");
            btClearAllClicked(null);
        }
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
            Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
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
                        Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
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
                    cbAddToFilter.getItems().addAll("OPENED", "LOCKED", "CLOSED");
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
                return "RB.OPENING_DATE";
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
            Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
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
            if(null != alFilterDBColumnName.get(i)) 
                switch (alFilterDBColumnName.get(i)) {
                case "RB.OPENING_DATE":
                case "CB.OPENING_DATE":
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
            Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
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

    public void clearAllBillDetails()
    {
        txtCompanyBillNumber.setText("");
        txtCompanyBillStatus.setText("");
        txtCompanyOpenedDate.setText("");
        txtCompanyBillAmount.setText("");
        txtCustomerName.setText("");
        txtSpouseType.setText("Spouse Type:");
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
        txtNote.setText("");        
        txtAmount.setText("");
        txtInterest.setText("");
        txtDocumentCharge.setText("");
        txtTakenAmount.setText("");
        txtToGiveAmount.setText("");
        txtGivenAmount.setText("");                
    }    

    private void txtRepledgeBillNumberTextChanged() {
        
        String sRepledgeBillId = txtRepledgeBillId.getText();
        String sRepledgeId = txtRepledgeId.getText();
        String sRepledgeName = cbRepledgeName.getValue();
        String sRepledgeBillNumber = txtRepledgeBillNumber.getText();
        
        if(!sRepledgeBillNumber.isEmpty()) {
            try {
                if(dbOp.isvalidBillNumberToSave(sRepledgeBillNumber, sRepledgeId, sRepledgeBillId, "GOLD")) {
                    cbStatus.getItems().removeAll(cbStatus.getItems());
                    cbStatus.getItems().add("GIVEN");
                    cbStatus.getSelectionModel().select("GIVEN");
                } else {
                    PopupUtil.showErrorAlert("Already same repledge bill number "+ sRepledgeBillNumber +" is available in "+ sRepledgeName +".");
                    cbStatus.getItems().removeAll(cbStatus.getItems());
                    cbStatus.getItems().add("OPENED");
                    cbStatus.getSelectionModel().select("OPENED");
                    txtRepledgeBillNumber.setText("");
                }
            } catch (SQLException ex) {
                Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            cbStatus.getItems().removeAll(cbStatus.getItems());
            cbStatus.getItems().add("OPENED");
            cbStatus.getSelectionModel().select("OPENED");
        }
    }

    public void viewBill(String sRepledgeBillId) {
    
        tgOff.setSelected(true);
        saveModeOFF(null);
        txtRepledgeBillId.setText(sRepledgeBillId);
        txtRepledgeBillIdOnAction(null);
        btUpdateBill.setDisable(true);
        btUpdateRepBillNo.setDisable(true);
        hSaveModeButtons.setDisable(true);
        btClearAll.setDisable(true);
        dpRepledgeOpeningDate.setEditable(false);
        dpRepledgeOpeningDate.setMouseTransparent(true);
        dpRepledgeOpeningDate.setFocusTraversable(false);         
        tpScreen.getSelectionModel().select(tabMainScreen);        
    }
    
    public void viewOpenedBill(String sRepledgeBillId) {
    
        tgOff.setSelected(true);
        saveModeOFF(null);
        txtRepledgeBillId.setText(sRepledgeBillId);
        txtRepledgeBillIdOnAction(null);
        tpScreen.getSelectionModel().select(tabMainScreen);
    }

    public void tbDialogTablesOnMouseClicked(String sRepledgeBillId) {        
        this.sMultipleRepBillIds = sRepledgeBillId;
        tgOff.setSelected(true);
        saveModeOFF(null);
        txtRepledgeBillId.setText(sRepledgeBillId);
        txtRepledgeBillIdOnAction(null);                        
    }    

    @FXML
    private void dpBillClosingAcceptedDateOnAction(ActionEvent event) {
    }

    @FXML
    private void rbBillClosingAcceptedToggleChanged(MouseEvent event) {
        setBillClosingAcceptedValues(true);
    }

    @FXML
    private void dpRepledgeOpeningDateOnAction(ActionEvent event) {
        try {
            closeDateRestriction();
            setBillClosingAcceptedValues(true);
        } catch (SQLException ex) {
            Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void isExistingOpenedDateAccIsClosed() {
        
    }

    private void closeDateRestriction() throws SQLException {
        
        String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpRepledgeOpeningDate.getValue());
        
        if(otherSettingValues.getRowCount() > 0 && 
            !Boolean.valueOf(otherSettingValues.getRow(0).getColumn(0).toString())) {

            if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                    DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                    sBillClosingDate))
            {
                if(tgOn.isSelected()) {
                    if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                        CommonConstants.REPLEDGE_BILL_OPENING_SCREEN, "ALLOW_ADD") 
                        || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                        btSaveBill.setDisable(false);
                    } else {
                        btSaveBill.setDisable(true);
                    }
                } else {
                    if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                        CommonConstants.REPLEDGE_BILL_OPENING_SCREEN, "ALLOW_VIEW") 
                        || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                        
                            String sOpeningDate = dbOp.getRepBillOpenedDate(txtRepledgeBillId.getText());
                            String sOpenedDate = CommonConstants.DATETIMEFORMATTER
                                    .format(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
                            if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                                    DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                                    sOpenedDate)) {
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
    private void btUpdateRepBillNoClicked(ActionEvent event) {
        
        if(sLastSelectedCompanyBillNumber != null 
                && sLastSelectedRepledgeBillId != null) 
        {
            txtRepledgeBillId.setText(sLastSelectedRepledgeBillId);
            txtCompanyBillNumber.setText(sLastSelectedCompanyBillNumber);
            String sRepledgeId = txtRepledgeId.getText();
            String sRepledgeName = cbRepledgeName.getValue().toUpperCase();
            String sRepledgeBillNumber = txtRepledgeBillNumber.getText();
            String sStatus = cbStatus.getValue().toUpperCase();
            
            if(sRepledgeBillNumber != null && !sRepledgeBillNumber.isEmpty()) {
                try {
                    if(dbOp.updateRepledgeBillNumber(
                            sLastSelectedRepledgeBillId, 
                            sRepledgeId, sRepledgeName,
                            sRepledgeBillNumber, sStatus)) {
                        PopupUtil.showInfoAlert("Repledge Bill number Updated successfully.");
                        btClearAllClicked(null);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(RepledgeGoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                PopupUtil.showInfoAlert("Repledge bill number cannot be empty to update the bill.");
            }  
        } else {
            PopupUtil.showInfoAlert(event, "Not any bill selected properly.");
            btClearAllClicked(null);
        }        
    }
}
