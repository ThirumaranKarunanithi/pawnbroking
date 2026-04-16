/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.customerdetails;

import com.magizhchi.pawnbroking.account.TodaysAccountController;
import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.Customer;
import com.magizhchi.pawnbroking.common.DataRow;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.NoticeUtil;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.common.WebCamWork;
import com.magizhchi.pawnbroking.companybillopening.GoldBillOpeningController;
import com.magizhchi.pawnbroking.companymaster.CompanyMasterController;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javax.imageio.ImageIO;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.bytedeco.javacv.FrameGrabber;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class CustomerDetailsController implements Initializable {

    private CustomerDetailsDBOperation dbOp;
    private DataTable fromCustomerNames = null;
    private DataTable toCustomerNames = null;
    private DataTable toReferredCustomer = null;
    private boolean isFromBillNo = false;
    private int selectedIndexFromRecords = -1;
    public static boolean isOpenCustomerImgAvailable = false;
    private DataTable otherSettingValues = null;
    private String customerDetails = "CUSTOMERS";
    private File tempFile = new File(CommonConstants.TEMP_FILE_LOCATION);
    private File customerFolder = new File(tempFile, customerDetails);
    boolean isNewIdGenerated = false;
    
    @FXML
    private TabPane tpScreen;
    @FXML
    private TextField txtTotalNumOfCustomers;
    @FXML
    private TableView<AllDetailsBean> tbAllCustomerDetails;
    @FXML
    private Label lbHeading;
    @FXML
    private Tab tabMainScreen;
    @FXML
    private ComboBox<String> txtFromCustomerName;
    @FXML
    private TextField txtFromGender;
    @FXML
    private TextField txtFromSpouseType;
    @FXML
    private TextField txtFromSpouseName;
    @FXML
    private TextField txtFromDoorNo;
    @FXML
    private TextField txtFromStreetName;
    @FXML
    private TextField txtFromArea;
    @FXML
    private TextField txtFromCity;
    @FXML
    private TextField txtFromMobileNumber;
    @FXML
    private ComboBox<String> txtToCustomerName;
    @FXML
    private ComboBox<String> cbToSpouseType;
    @FXML
    private TextField txtToSpouseName;
    @FXML
    private TextField txtToDoorNo;
    @FXML
    private ComboBox<String> txtToStreetName;
    @FXML
    private TextField txtToArea;
    @FXML
    private TextField txtToCity;
    @FXML
    private TextField txtToMobileNumber;
    @FXML
    private Label lbScreenMessage;
    @FXML
    private HBox hSaveModeButtons;
    @FXML
    private ToggleButton tgEditCustomer;
    @FXML
    private ToggleGroup ViewModeGroup;
    @FXML
    private ToggleButton tgMergeCustomer;
    @FXML
    private Button btSaveBill;
    @FXML
    private Button btClearAll;
    @FXML
    private ToggleGroup rgGenderGroup;
    @FXML
    private Tab tabAllCustomerDetails;
    @FXML
    private Tab tabMainScreen1;
    @FXML
    private TextField txtReCustromerName;
    @FXML
    private ComboBox<String> cbReSpouseType;
    @FXML
    private TextField txtReSpouseName;
    @FXML
    private TextField txtReDoorNo;
    @FXML
    private ComboBox<String> txtReStreetName;
    @FXML
    private TextField txtReArea;
    @FXML
    private TextField txtReCity;
    @FXML
    private TextField txtReMobileNumber;
    @FXML
    private Button btReSaveBill;
    @FXML
    private Button btReClearAll;
    @FXML
    private ToggleGroup rgReGenderGroup;
    @FXML
    private TextField txtReCustromerId;
    @FXML
    private TextField txtFromStatus;
    @FXML
    private ComboBox<String> cbToStatus;
    @FXML
    private Label lbCustomerStatus3;
    @FXML
    private TextField txtFromIdProof;
    @FXML
    private TextField txtFromIdNumber;
    @FXML
    private TextField txtFromRecomendedBy;
    @FXML
    private Label lbCustomerStatus31;
    @FXML
    private ComboBox<String> cbToIdProof;
    @FXML
    private TextField txtToIdNumber;
    @FXML
    private ComboBox<String> txtToReferredCustomer;
    @FXML
    private Button btCaptureCustomerImg;
    @FXML
    private ImageView ivCustomerBill;
    @FXML
    private TextField txtFromCustomerId;
    @FXML
    private TextField txtToCustomerId;
    @FXML
    private TextField txtFromMobileNumber2;
    @FXML
    private TextField txtToMobileNumber2;
    @FXML
    private TextField txtFromOccupation;
    @FXML
    private Label lbCustomerStatus2;
    @FXML
    private TextField txtToOccupation;
    @FXML
    private Label lbCustomerStatus21;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            dbOp = new CustomerDetailsDBOperation(CommonConstants.DB, CommonConstants.IP, 
                    CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            otherSettingValues = dbOp.getOtherSettingsValues("GOLD");
            
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, 
                    CommonConstants.SPECIAL_OPTIONS_TAB, CommonConstants.CUSTOMER_DETAILS, "ALLOW_ADD") 
                    || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btSaveBill.setDisable(false);
                btSaveBill.setDisable(false);
            } else {
                btSaveBill.setDisable(true);
                btSaveBill.setDisable(true);
            }            
        } catch (SQLException ex) {
            Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, 
                    CommonConstants.SPECIAL_OPTIONS_TAB, CommonConstants.ALL_CUSTOMER_DETAILS, "ALLOW_VIEW") 
                    || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            } else {
                tpScreen.getTabs().remove(tabAllCustomerDetails);
            }
            
            if(CommonConstants.ACTIVE_COMPANY_TYPE.equals(CommonConstants.RE)) {
                
            } else {
                tpScreen.getTabs().remove(tabMainScreen1);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setSpouseTypeValues(true);
        setReSpouseTypeValues(true);
        
        txtFromCustomerName.getEditor().setOnKeyTyped((KeyEvent e) -> {
                
            boolean isEnter = "\r".equals(e.getCharacter());
            String pattern= "^[a-zA-Z0-9 ]*$";

            if(e.getCharacter().matches(pattern)) {
                setTxtFromCustomerNameValues(txtFromCustomerName.getEditor().getText().toUpperCase()+e.getCharacter().toUpperCase());
                txtFromCustomerName.show();
            } else if(isEnter && !isFromBillNo) { 
                selectedIndexFromRecords = txtFromCustomerName.getSelectionModel().getSelectedIndex();
                if(selectedIndexFromRecords >=0) {
                    setFromValues(selectedIndexFromRecords, fromCustomerNames);
                    if(tgEditCustomer.isSelected()) 
                        setToValues(selectedIndexFromRecords, fromCustomerNames);
                } else {
                }                                        
            } else {
                e.consume();
            }
            isFromBillNo = false;
        });

        txtFromCustomerName.getEditor().setOnKeyPressed((KeyEvent e) -> {                

            String sText;

            if(txtFromCustomerName.getEditor().getCaretPosition() == 0) {
                StringBuilder sb = new StringBuilder(txtFromCustomerName.getEditor().getText());
                sText = sb.toString();
            } else {
                StringBuilder sb = new StringBuilder(txtFromCustomerName.getEditor().getText());
                sb.deleteCharAt(txtFromCustomerName.getEditor().getCaretPosition() - 1);
                sText = sb.toString();
            }

            if(e.getCode() == KeyCode.BACK_SPACE) 
            {
                if(!"".equals(sText)) 
                {
                    setTxtFromCustomerNameValues(sText.toUpperCase());
                    txtFromCustomerName.show();
                } else {
                    setTxtFromCustomerNameValues(null);
                    txtFromCustomerName.show();                    
                }        
            } else if(e.getCode() == KeyCode.DOWN) { 
                txtFromCustomerName.show();
            } else {
                e.consume();
            }                                                   
        });        

        txtToCustomerName.getEditor().setOnKeyTyped((KeyEvent e) -> {
                
            boolean isEnter = "\r".equals(e.getCharacter());
            String pattern= "^[a-zA-Z0-9 ]*$";

            if(e.getCharacter().matches(pattern)) {
                setTxtToCustomerNameValues(txtToCustomerName.getEditor().getText().toUpperCase()+e.getCharacter().toUpperCase());
                txtToCustomerName.show();
            } else if(isEnter && !isFromBillNo) { 
                int sIndex = txtToCustomerName.getSelectionModel().getSelectedIndex();
                if(sIndex >=0) {
                        setToValues(sIndex, toCustomerNames);
                } else {
                }                                        
            } else {
                e.consume();
            }            
            isFromBillNo = false;
        });

        txtToCustomerName.getEditor().setOnKeyPressed((KeyEvent e) -> {                

            String sText;

            if(txtToCustomerName.getEditor().getCaretPosition() == 0) {
                StringBuilder sb = new StringBuilder(txtToCustomerName.getEditor().getText());
                sText = sb.toString();
            } else {
                StringBuilder sb = new StringBuilder(txtToCustomerName.getEditor().getText());
                sb.deleteCharAt(txtToCustomerName.getEditor().getCaretPosition() - 1);
                sText = sb.toString();
            }

            if(e.getCode() == KeyCode.BACK_SPACE) 
            {
                if(!"".equals(sText)) 
                {
                    setTxtToCustomerNameValues(sText.toUpperCase());
                    txtToCustomerName.show();
                } else {
                    setTxtToCustomerNameValues(null);
                    txtToCustomerName.show();                    
                }        
            } else if(e.getCode() == KeyCode.DOWN) { 
                txtToCustomerName.show();
            } else {
                e.consume();
            }                                                   
        });        

        txtToReferredCustomer.getEditor().setOnKeyTyped((KeyEvent e) -> {
                
            boolean isEnter = "\r".equals(e.getCharacter());
            String pattern= "^[a-zA-Z0-9 ]*$";

            if(e.getCharacter().matches(pattern)) {
                setTxtToRefCustomerNameValues(txtToReferredCustomer.getEditor().getText().toUpperCase()+e.getCharacter().toUpperCase());
                txtToReferredCustomer.show();
            } else if(isEnter && !isFromBillNo) { 
                int sIndex = txtToReferredCustomer.getSelectionModel().getSelectedIndex();
                if(sIndex >=0) {
                        setToReferredCustomerValues(sIndex, toReferredCustomer);
                } else {
                }                                        
            } else {
                e.consume();
            }
            isFromBillNo = false;
        });

        txtToReferredCustomer.getEditor().setOnKeyPressed((KeyEvent e) -> {                

            String sText;

            if(txtToReferredCustomer.getEditor().getCaretPosition() == 0) {
                StringBuilder sb = new StringBuilder(txtToReferredCustomer.getEditor().getText());
                sText = sb.toString();
            } else {
                StringBuilder sb = new StringBuilder(txtToReferredCustomer.getEditor().getText());
                sb.deleteCharAt(txtToReferredCustomer.getEditor().getCaretPosition() - 1);
                sText = sb.toString();
            }

            if(e.getCode() == KeyCode.BACK_SPACE) 
            {
                if(!"".equals(sText)) 
                {
                    setTxtToRefCustomerNameValues(sText.toUpperCase());
                    txtToReferredCustomer.show();
                } else {
                    setTxtToRefCustomerNameValues(null);
                    txtToReferredCustomer.show();                    
                }        
            } else if(e.getCode() == KeyCode.DOWN) { 
                txtToReferredCustomer.show();
            } else {
                e.consume();
            }                                                   
        });        
        
        try {
            txtReCustromerId.setText(dbOp.getId("RE CUSTOMER ID", "REPLEDGE"));
        } catch (SQLException ex) {
            Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        cbToStatus.getItems().add("ACTIVE");
        cbToStatus.getItems().add("BLOCKED");
        cbToStatus.getSelectionModel().select(0);
        
        isOpenCustomerImgAvailable = false;
        ivCustomerBill.setImage(CommonConstants.noImage);
    }    
    
    public void setTxtFromCustomerNameValues(String sCustomerName) {
        Platform.runLater(()->{
            try {
                txtFromCustomerName.getItems().removeAll(txtFromCustomerName.getItems());
                fromCustomerNames = dbOp.getFilteredCustomerNames(sCustomerName);
                for(int i=0; i<fromCustomerNames.getRowCount(); i++) {          
                    txtFromCustomerName.getItems().add(fromCustomerNames.getRow(i).getColumn(0).toString());
                }                
            } catch (SQLException ex) {
                Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
            }                
        });
    }

    public void setTxtToCustomerNameValues(String sCustomerName) {
        Platform.runLater(()->{
            try {
                txtToCustomerName.getItems().removeAll(txtToCustomerName.getItems());
                toCustomerNames = dbOp.getFilteredCustomerNames(sCustomerName);
                for(int i=0; i<toCustomerNames.getRowCount(); i++) {          
                    txtToCustomerName.getItems().add(toCustomerNames.getRow(i).getColumn(0).toString());
                }                
            } catch (SQLException ex) {
                Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
            }                
        });
    }

    public void setTxtToRefCustomerNameValues(String sCustomerName) {
        Platform.runLater(()->{
            try {
                txtToReferredCustomer.getItems().removeAll(txtToReferredCustomer.getItems());
                toReferredCustomer = dbOp.getFilteredCustomerNames(sCustomerName);
                for(int i=0; i<toReferredCustomer.getRowCount(); i++) {          
                    txtToReferredCustomer.getItems().add(toReferredCustomer.getRow(i).getColumn(0).toString());
                }                
            } catch (SQLException ex) {
                Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
            }                
        });
    }
    
    public void setFromValues(int sIndex, DataTable customerNames) {
        
        txtFromCustomerName.setValue(customerNames.getRow(sIndex).getColumn(1).toString());
        txtFromGender.setText(customerNames.getRow(sIndex).getColumn(2).toString());
        txtFromSpouseType.setText(customerNames.getRow(sIndex).getColumn(3).toString());
        txtFromSpouseName.setText(customerNames.getRow(sIndex).getColumn(4).toString());      
        txtFromDoorNo.setText(customerNames.getRow(sIndex).getColumn(5).toString());
        txtFromStreetName.setText(customerNames.getRow(sIndex).getColumn(6).toString());
        txtFromArea.setText(customerNames.getRow(sIndex).getColumn(7).toString());
        txtFromCity.setText(customerNames.getRow(sIndex).getColumn(8).toString());
        txtFromMobileNumber.setText(customerNames.getRow(sIndex).getColumn(9).toString());
        if(customerNames.getRow(sIndex).getColumn(10).toString().equals("BLOCKED")) {
            txtFromStatus.setText(customerNames.getRow(sIndex).getColumn(10).toString());
        } else {
            txtFromStatus.setText("ACTIVE");
        }
        txtFromMobileNumber2.setText(customerNames.getRow(sIndex).getColumn(11) != null 
                ? customerNames.getRow(sIndex).getColumn(11).toString() : "");
        txtFromIdProof.setText(customerNames.getRow(sIndex).getColumn(12) != null 
                ? customerNames.getRow(sIndex).getColumn(12).toString() : "");
        txtFromIdNumber.setText(customerNames.getRow(sIndex).getColumn(13) != null 
                ? customerNames.getRow(sIndex).getColumn(13).toString() : "");
        txtFromRecomendedBy.setText(customerNames.getRow(sIndex).getColumn(14) != null 
                ? customerNames.getRow(sIndex).getColumn(14).toString() : "");
        txtFromCustomerId.setText(customerNames.getRow(sIndex).getColumn(15) != null 
                ? customerNames.getRow(sIndex).getColumn(15).toString() : "");
        txtFromOccupation.setText(customerNames.getRow(sIndex).getColumn(16) != null 
                ? customerNames.getRow(sIndex).getColumn(16).toString() : "");        
    }
    
    public void clearFromValues() {
        
        txtFromCustomerName.setValue("");
        txtFromGender.setText("");
        txtFromSpouseType.setText("");
        txtFromSpouseName.setText("");      
        txtFromDoorNo.setText("");
        txtFromStreetName.setText("");
        txtFromArea.setText("");
        txtFromCity.setText("");
        txtFromMobileNumber.setText("");
        selectedIndexFromRecords = -1;
    }

    public void setToValues(int sIndex, DataTable customerNames) {
        
        txtToCustomerName.setValue(customerNames.getRow(sIndex).getColumn(1).toString());
        String sGender = customerNames.getRow(sIndex).getColumn(2).toString();
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
        cbToSpouseType.setValue(customerNames.getRow(sIndex).getColumn(3).toString());
        txtToSpouseName.setText(customerNames.getRow(sIndex).getColumn(4).toString());      
        txtToDoorNo.setText(customerNames.getRow(sIndex).getColumn(5).toString());
        txtToStreetName.setValue(customerNames.getRow(sIndex).getColumn(6).toString());
        txtToArea.setText(customerNames.getRow(sIndex).getColumn(7).toString());
        txtToCity.setText(customerNames.getRow(sIndex).getColumn(8).toString());
        txtToMobileNumber.setText(customerNames.getRow(sIndex).getColumn(9).toString());
        if(customerNames.getRow(sIndex).getColumn(10).toString().equals("BLOCKED")) {
            cbToStatus.setValue(customerNames.getRow(sIndex).getColumn(10).toString());
        } else {
            cbToStatus.setValue("ACTIVE");
        }        
        txtToMobileNumber2.setText(customerNames.getRow(sIndex).getColumn(11) != null 
                ? customerNames.getRow(sIndex).getColumn(11).toString() : "");
        cbToIdProof.setValue(customerNames.getRow(sIndex).getColumn(12) != null 
                ? customerNames.getRow(sIndex).getColumn(12).toString() : "");
        txtToIdNumber.setText(customerNames.getRow(sIndex).getColumn(13) != null 
                ? customerNames.getRow(sIndex).getColumn(13).toString() : "");
        txtToReferredCustomer.setValue(customerNames.getRow(sIndex).getColumn(14) != null 
                ? customerNames.getRow(sIndex).getColumn(14).toString() : "");
        txtToCustomerId.setText(customerNames.getRow(sIndex).getColumn(15) != null 
                ? customerNames.getRow(sIndex).getColumn(15).toString() : "");
        txtToOccupation.setText(customerNames.getRow(sIndex).getColumn(16) != null 
                ? customerNames.getRow(sIndex).getColumn(16).toString() : "");
        
        if(!txtToCustomerId.getText().isEmpty()) {
            try {
                setImageDetailsValuesToTheField(txtToCustomerId.getText());
            } catch (IOException ex) {
                Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setToReferredCustomerValues(int sIndex, DataTable customerNames) {        
        String name = customerNames.getRow(sIndex).getColumn(1).toString();
        String spouseType = customerNames.getRow(sIndex).getColumn(3).toString();
        String spouseName = customerNames.getRow(sIndex).getColumn(4).toString();
        txtToReferredCustomer.setValue(name + " " + spouseType + " " + spouseName);
    }
    
    public void clearToValues() {
        
        txtToCustomerName.setValue("");
        cbToSpouseType.setValue("");
        txtToSpouseName.setText("");      
        txtToDoorNo.setText("");
        txtToStreetName.setValue("");
        txtToArea.setText("");
        txtToCity.setText("");
        txtToMobileNumber.setText("");
    }
    
    public void setSpouseTypeValues(boolean selectFirstVal) {
        
        Platform.runLater(()->{
            cbToSpouseType.getItems().removeAll(cbToSpouseType.getItems());
            RadioButton selectedRadioButton = (RadioButton) rgGenderGroup.getSelectedToggle();        
            if(null != selectedRadioButton.getText()) switch (selectedRadioButton.getText()) {
                case "Male":
                    cbToSpouseType.getItems().add("S/O");
                    cbToSpouseType.getItems().add("F/O");
                    cbToSpouseType.getItems().add("H/O");
                    break;
                case "Female":
                    cbToSpouseType.getItems().add("W/O");
                    cbToSpouseType.getItems().add("D/O");
                    cbToSpouseType.getItems().add("M/O");
                    break;
                default:
                    cbToSpouseType.getItems().add("S/O");
                    cbToSpouseType.getItems().add("F/O");
                    cbToSpouseType.getItems().add("H/O");
                    cbToSpouseType.getItems().add("D/O");
                    cbToSpouseType.getItems().add("M/O");
                    cbToSpouseType.getItems().add("W/O");
                    break;
            }        
            if(selectedRadioButton.getText() != null && selectFirstVal) {
                cbToSpouseType.getSelectionModel().select(0);
            } else {
                
            }
        });
    }
    
    public void setReSpouseTypeValues(boolean selectFirstVal) {
        
        Platform.runLater(()->{
            cbReSpouseType.getItems().removeAll(cbReSpouseType.getItems());
            RadioButton selectedRadioButton = (RadioButton) rgReGenderGroup.getSelectedToggle();        
            if(null != selectedRadioButton.getText()) switch (selectedRadioButton.getText()) {
                case "Male":
                    cbReSpouseType.getItems().add("S/O");
                    cbReSpouseType.getItems().add("F/O");
                    cbReSpouseType.getItems().add("H/O");
                    break;
                case "Female":
                    cbReSpouseType.getItems().add("W/O");
                    cbReSpouseType.getItems().add("D/O");
                    cbReSpouseType.getItems().add("M/O");
                    break;
                default:
                    cbReSpouseType.getItems().add("S/O");
                    cbReSpouseType.getItems().add("F/O");
                    cbReSpouseType.getItems().add("H/O");
                    cbReSpouseType.getItems().add("D/O");
                    cbReSpouseType.getItems().add("M/O");
                    cbReSpouseType.getItems().add("W/O");
                    break;
            }        
            if(selectedRadioButton.getText() != null && selectFirstVal) {
                cbReSpouseType.getSelectionModel().select(0);
            } else {
                
            }
        });
    }

    @FXML
        private void capitalizeCharOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();
        int caretPos = txt_TextField.getCaretPosition();
        txt_TextField.setText(txt_TextField.getText().toUpperCase());
        txt_TextField.positionCaret(caretPos);
    }

    @FXML
    private void btShowAllRecordsClicked(ActionEvent event) {
        try {
            DataTable allDetailValues = dbOp.getAllDetailsValues();
            setAllDetailValuesToField(allDetailValues);
            lbHeading.setText("ALL CUSTOMER'S LIST");
        } catch (SQLException ex) {
            Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public void setAllDetailValuesToField(DataTable allDetailValues) {
        
        int count = 0;
        tbAllCustomerDetails.getItems().removeAll(tbAllCustomerDetails.getItems());
        for(int i=0; i<allDetailValues.getRowCount(); i++) {            
            String sStockBills = allDetailValues.getRow(i).getColumn(0).toString();
            String sOpenedBills = allDetailValues.getRow(i).getColumn(1).toString();
            String sName = allDetailValues.getRow(i).getColumn(2).toString();
            String sGender = allDetailValues.getRow(i).getColumn(3).toString();
            String sSpouseType = allDetailValues.getRow(i).getColumn(4).toString();
            String sSpouseName = allDetailValues.getRow(i).getColumn(5).toString();
            String sDoorNumber = allDetailValues.getRow(i).getColumn(6).toString();
            String sStreet = allDetailValues.getRow(i).getColumn(7).toString();
            String sArea = allDetailValues.getRow(i).getColumn(8).toString();
            String sCity = allDetailValues.getRow(i).getColumn(9).toString();
            String sMobileNumber = allDetailValues.getRow(i).getColumn(10).toString();
            String sCustomerId = allDetailValues.getRow(i).getColumn(11) != null 
                    ? allDetailValues.getRow(i).getColumn(11).toString() : "";
            
            int dStockBills = Integer.parseInt(sStockBills);
            int dOpenedBills = Integer.parseInt(sOpenedBills);
            count++;
            tbAllCustomerDetails.getItems().add(new AllDetailsBean(dStockBills, dOpenedBills, null, 
                    sName, sGender, sSpouseType, sSpouseName, sDoorNumber, sStreet, sArea, sCity, 
                    sMobileNumber, sCustomerId));                    
        }        
        txtTotalNumOfCustomers.setText(Integer.toString(count));
    }    

    @FXML
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {
        int index = tbAllCustomerDetails.getSelectionModel().getSelectedIndex();

        if (event.getClickCount() == 2 && (index >= 0) ) {
            viewEditCustomer(tbAllCustomerDetails.getItems().get(index));
        }                
    }

    public void viewEditCustomer(AllDetailsBean bean) {
    
        String customerName = bean.getSName();
        String gender = bean.getSGender();
        String spouseType = bean.getSSpouseType();
        String spouseName = bean.getSSpouseName();
        String doorNumber = bean.getSDoorNumber();
        String street = bean.getSStreet();
        String area = bean.getSArea();
        String city = bean.getSCity();
        String mobileNumber = bean.getSMobileNumber();
        selectedIndexFromRecords = 0;        
        try {
            tgEditCustomer.setSelected(true);
            fromCustomerNames = dbOp.getCustomerDetails(customerName, gender, spouseType,
                    spouseName, doorNumber, street, area, city, mobileNumber);
            setFromValues(selectedIndexFromRecords, fromCustomerNames);
            if(tgEditCustomer.isSelected()) {
                setToValues(selectedIndexFromRecords, fromCustomerNames);
            }            
            tpScreen.getSelectionModel().select(tabMainScreen);
        } catch (SQLException ex) {
            Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void viewEditCustomer(String customerName, String gender, String spouseType,
            String spouseName, String doorNumber, String street, String area, String city,
            String mobileNumber) {
    
        selectedIndexFromRecords = 0;        
        try {
            tgEditCustomer.setSelected(true);
            fromCustomerNames = dbOp.getCustomerDetails(customerName, gender, spouseType,
                    spouseName, doorNumber, street, area, city, mobileNumber);
            setFromValues(selectedIndexFromRecords, fromCustomerNames);
            if(tgEditCustomer.isSelected()) {
                setToValues(selectedIndexFromRecords, fromCustomerNames);
            }            
            tpScreen.getSelectionModel().select(tabMainScreen);
        } catch (SQLException ex) {
            Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void btShowActiveCustomersClicked(ActionEvent event) {
        try {
            DataTable allDetailValues = dbOp.getActiveValues();
            setAllDetailValuesToField(allDetailValues);
            lbHeading.setText("ACTIVE CUSTOMER'S LIST");
        } catch (SQLException ex) {
            Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }                
    }

    @FXML
    private void btShowInActiveCustomersClicked(ActionEvent event) {
        try {
            DataTable allDetailValues = dbOp.getInActiveValues();
            setAllDetailValuesToField(allDetailValues);
            lbHeading.setText("IN-ACTIVE CUSTOMER'S LIST");
        } catch (SQLException ex) {
            Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }                
    }

    @FXML
    private void txtFromCustomerNameOnAction(ActionEvent event) {
    }

    @FXML
    private void allowNumberOnlyOnType(KeyEvent event) {
    }

    @FXML
    private void txtToCustomerNameOnAction(ActionEvent event) {
    }

    @FXML
    private void rbToToggleChanged(MouseEvent event) {
        setSpouseTypeValues(true);
    }

    @FXML
    private void capitalizeCharOnPressed(KeyEvent event) {
    }

    @FXML
    private void tgEditCustomerClicked(ActionEvent event) {
        
        clearFromValues();
        clearToValues();
        setSpouseTypeValues(false);
        
        cbToSpouseType.setMouseTransparent(false);
        cbToSpouseType.setFocusTraversable(true);                
        txtToSpouseName.setEditable(true);
        txtToSpouseName.setMouseTransparent(false);
        txtToSpouseName.setFocusTraversable(true);                
        txtToDoorNo.setEditable(true);
        txtToDoorNo.setMouseTransparent(false);
        txtToDoorNo.setFocusTraversable(true);                
        txtToStreetName.setEditable(true);
        txtToStreetName.setMouseTransparent(false);
        txtToStreetName.setFocusTraversable(true);                
        txtToArea.setEditable(true);
        txtToArea.setMouseTransparent(false);
        txtToArea.setFocusTraversable(true);                
        txtToCity.setEditable(true);
        txtToCity.setMouseTransparent(false);
        txtToCity.setFocusTraversable(true);                
        txtToMobileNumber.setEditable(true);
        txtToMobileNumber.setMouseTransparent(false);
        txtToMobileNumber.setFocusTraversable(true);                
        cbToIdProof.setEditable(false);
        cbToIdProof.setMouseTransparent(false);
        cbToIdProof.setFocusTraversable(true);                
        txtToIdNumber.setEditable(true);
        txtToIdNumber.setMouseTransparent(false);
        txtToIdNumber.setFocusTraversable(true);                
        txtToReferredCustomer.setEditable(true);
        txtToReferredCustomer.setMouseTransparent(false);
        txtToReferredCustomer.setFocusTraversable(true);                
        
    }

    @FXML
    private void tgMergeCustomerClicked(ActionEvent event) {
        clearToValues();

        cbToSpouseType.setMouseTransparent(true);
        cbToSpouseType.setFocusTraversable(false);                
        txtToSpouseName.setEditable(false);
        txtToSpouseName.setMouseTransparent(true);
        txtToSpouseName.setFocusTraversable(false);                
        txtToDoorNo.setEditable(false);
        txtToDoorNo.setMouseTransparent(true);
        txtToDoorNo.setFocusTraversable(false);                
        txtToStreetName.setEditable(false);
        txtToStreetName.setMouseTransparent(true);
        txtToStreetName.setFocusTraversable(false);                
        txtToArea.setEditable(false);
        txtToArea.setMouseTransparent(true);
        txtToArea.setFocusTraversable(false);                
        txtToCity.setEditable(false);
        txtToCity.setMouseTransparent(true);
        txtToCity.setFocusTraversable(false);                
        txtToMobileNumber.setEditable(false);
        txtToMobileNumber.setMouseTransparent(true);
        txtToMobileNumber.setFocusTraversable(false);                
        cbToIdProof.setEditable(false);
        cbToIdProof.setMouseTransparent(true);
        cbToIdProof.setFocusTraversable(false);                
        txtToIdNumber.setEditable(false);
        txtToIdNumber.setMouseTransparent(true);
        txtToIdNumber.setFocusTraversable(false);                
        txtToReferredCustomer.setEditable(false);
        txtToReferredCustomer.setMouseTransparent(true);
        txtToReferredCustomer.setFocusTraversable(false);                
    }

    @FXML
    private void btSaveBillClicked(ActionEvent event) {
        
        try {
            if(selectedIndexFromRecords > -1) {
                String sToCustomerId = txtToCustomerId.getText();
                final DataRow fromDataRowToEdit = fromCustomerNames.getRow(selectedIndexFromRecords);
                final DataRow toDataRowToEdit = new DataRow();                
                toDataRowToEdit.addColumn(txtToCustomerName.getValue().toUpperCase());
                toDataRowToEdit.addColumn(((RadioButton) rgGenderGroup.getSelectedToggle()).getText().toUpperCase());
                toDataRowToEdit.addColumn(cbToSpouseType.getValue().toUpperCase());
                toDataRowToEdit.addColumn(txtToSpouseName.getText());
                toDataRowToEdit.addColumn(txtToDoorNo.getText());
                toDataRowToEdit.addColumn(txtToStreetName.getValue().toUpperCase());
                toDataRowToEdit.addColumn(txtToArea.getText());
                toDataRowToEdit.addColumn(txtToCity.getText());
                toDataRowToEdit.addColumn(txtToMobileNumber.getText());
                if(cbToStatus.getValue().equals("BLOCKED")) {
                    toDataRowToEdit.addColumn(cbToStatus.getValue());
                } else {
                    toDataRowToEdit.addColumn("");
                }
                toDataRowToEdit.addColumn(txtToMobileNumber2.getText());
                toDataRowToEdit.addColumn(cbToIdProof.getValue().toUpperCase());
                toDataRowToEdit.addColumn(txtToIdNumber.getText());
                toDataRowToEdit.addColumn(txtToReferredCustomer.getEditor().getText().toUpperCase());
                toDataRowToEdit.addColumn(txtToCustomerId.getText());
                toDataRowToEdit.addColumn(txtToOccupation.getText());
                
                if(CommonConstants.ACTIVE_COMPANY_TYPE.equals(CommonConstants.RE)) {
                    int updatedRows = dbOp.updateReRecord(fromDataRowToEdit, toDataRowToEdit);                
                }
                int updatedRows = dbOp.updateRecord(fromDataRowToEdit, toDataRowToEdit);    
                if(isNewIdGenerated) {
                    dbOp.setNextCustomerId(Integer.toString(Integer.parseInt(sToCustomerId)+1));
                }
                if(sToCustomerId != null && !sToCustomerId.isEmpty()) {
                    saveImages();
                }                
                if(updatedRows > 0) {
                    PopupUtil.showInfoAlert("Totally " +updatedRows + " rows updated successfully.");
                    isNewIdGenerated = false;
                }else{
                    PopupUtil.showInfoAlert("Something went wrong.");
                }
            } else {
                PopupUtil.showInfoAlert("From details cannot be empty.");
            }
        } catch (Exception ex) {
            Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saveImages() throws FileNotFoundException, IOException {
        
        String customerFolderName = txtToCustomerId.getText();                
        File exactCustomerFolder = new File(customerFolder, customerFolderName);
        
        if(isOpenCustomerImgAvailable) {              
            if(!exactCustomerFolder.exists()) {
                exactCustomerFolder.mkdir();
            }
            File custTemp = new File(exactCustomerFolder, CommonConstants.CUSTOMER_IMAGE_NAME);
            File cfile = new File(custTemp.getAbsolutePath());
            BufferedImage bImage = SwingFXUtils.fromFXImage(ivCustomerBill.getImage(), null);
            ImageIO.write(bImage, "png", cfile);
            bImage.flush();
        }
    }
    
    @FXML
    private void btClearAllClicked(ActionEvent event) {
        clearFromValues();
        clearToValues();
    }

    @FXML
    private void rbReToggleChanged(MouseEvent event) {
        setReSpouseTypeValues(true);
    }

    @FXML
    private void btReSaveBillClicked(ActionEvent event) {
        
        try {
            String sCustomerId = txtReCustromerId.getText().toUpperCase();
            String sCustomerName = txtReCustromerName.getText().toUpperCase();
            String sGender = ((RadioButton) rgReGenderGroup.getSelectedToggle()).getText().toUpperCase();
            String sSpouseType = cbReSpouseType.getValue().toUpperCase();
            String sSpouseName = txtReSpouseName.getText().toUpperCase();                                
            String sDoorNo = txtReDoorNo.getText().toUpperCase();
            String sStreetName = txtReStreetName.getValue().toUpperCase();
            String sArea = txtReArea.getText().toUpperCase();
            String sCity = txtReCity.getText().toUpperCase();
            String sMobileNumber = txtReMobileNumber.getText().toUpperCase();
            
            try {
                if(dbOp.saveRecord(sCustomerId, sCustomerName, sGender,
                        sSpouseType, sSpouseName, sDoorNo,
                        sStreetName, sArea, sCity,
                        sMobileNumber))
                {
                    clearReValues();
                    dbOp.setNextId("RE CUSTOMER ID", CommonConstants.CUSTOMER_ID_PREFIX 
                            + (Integer.parseInt(sCustomerId.replace(CommonConstants.CUSTOMER_ID_PREFIX, ""))+1));
                    txtReCustromerId.setText("");
                    try {
                        txtReCustromerId.setText(dbOp.getId("RE CUSTOMER ID", "REPLEDGE"));
                    } catch (SQLException ex) {
                        Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    PopupUtil.showInfoAlert("New customer created successfully with id."+"("+sCustomerId+")");
                }
            } catch (Exception ex) {
                Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception ex) {
            Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    public void clearReValues() {        
        txtReCustromerName.setText("");
        cbReSpouseType.setValue("");
        txtReSpouseName.setText("");      
        txtReDoorNo.setText("");
        txtReStreetName.setValue("");
        txtReArea.setText("");
        txtReCity.setText("");
        txtReMobileNumber.setText("");
    }
    
    @FXML
    private void btReClearAllClicked(ActionEvent event) {
        clearReValues();
    }

    @FXML
    private void btPrintCustomersClicked(ActionEvent event) {
        
        if(!tbAllCustomerDetails.getItems().isEmpty()) {
        
            try {
                String sFileName = CommonConstants.REPORT_LOCATION 
                        + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                        + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                        + "\\customerdetails.jasper";                        
                
                List<CustomerPrintBean> ParamList = new ArrayList<>();	            
                for(AllDetailsBean bean : tbAllCustomerDetails.getItems()) {                
                    CustomerPrintBean nBean = new CustomerPrintBean();
                    nBean.setCustomerName(bean.getSName());
                    nBean.setSpouseType(bean.getSSpouseType());
                    nBean.setSpouseName(bean.getSSpouseName());
                    nBean.setDoorNo(bean.getSDoorNumber() != null ? bean.getSDoorNumber() : "");
                    nBean.setStreet(bean.getSStreet() != null ? bean.getSStreet() : "");
                    nBean.setArea(bean.getSArea());
                    nBean.setCity(bean.getSCity());
                    nBean.setMobileNumber(bean.getSMobileNumber());
                    ParamList.add(nBean);
                }

                JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(ParamList);

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("BillCalcCollectionBeanParam", tableList);
                parameters.put("TODAYSDATE", DateRelatedCalculations.getTodaysDate());

                parameters.put("ACTIVE_INACTIVE_ALL_LABEL", lbHeading.getText());
                parameters.put("ACTIVE_INACTIVE_ALL_COUNT", Integer.parseInt((txtTotalNumOfCustomers.getText())));

                JasperPrint print = null;
                NoticeUtil noticeUtil = new NoticeUtil();
                noticeUtil.generateNoticeOperation(sFileName, parameters);

            } catch (JRException ex) {
                PopupUtil.showErrorAlert(ex.getMessage());
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        } else {
            PopupUtil.showInfoAlert("Not any details filled to print the records. ");
        }
        
    }

    @FXML
    private void txtToReferredCustomerOnAction(ActionEvent event) {
    }

    private String getCustomerIdFor() {
        try {
            String idProof = cbToIdProof.getValue();
            String idNumber = txtToIdNumber.getText();
            String customerId = dbOp.getCustomerIdFor(idProof, idNumber);
            return customerId;
        } catch (SQLException ex) {
            Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }    
        return null;
    }
    
    private boolean isValidNewCustomer() {
        String customerId = getCustomerIdFor();
        if(customerId == null || customerId.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
    
    @FXML
    private void btCaptureCustomerImgClicked(ActionEvent event) {
        
        if(isValidNewCustomer()) {
                    
            if(txtToIdNumber.getText() != null 
                    && !txtToIdNumber.getText().isEmpty()) {            
                if(txtToCustomerId.getText() == null 
                        || txtToCustomerId.getText().isEmpty()) {
                    try {
                        txtToCustomerId.setText(dbOp.getCustomerId());
                        isNewIdGenerated = true;
                    } catch (SQLException ex) {
                        Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                isOpenCustomerImgAvailable = false;
                String sCustomerCamName = otherSettingValues.getRow(0).getColumn(10).toString();
                if(!
                        sCustomerCamName.equals(CompanyMasterController.DO_NOT_TAKE_PICTURE)) {                
                    try {                
                        File compCustomerFolder = new File(customerFolder, CommonConstants.OPEN_CUSTOMER_IMAGE_NAME);
                        WebCamWork.captureImageFrom(sCustomerCamName, compCustomerFolder.getAbsolutePath());
                        try (FileInputStream fis = new FileInputStream(compCustomerFolder.getAbsolutePath())) {
                            final Image img = new Image(fis);
                            ivCustomerBill.setImage(img);
                            isOpenCustomerImgAvailable = true;
                        } catch (IOException ex) {
                            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (FrameGrabber.Exception ex) {
                        PopupUtil.showInfoAlert(event, "Invalid camera name was selected. "); 
                    }                
                } else {
                    PopupUtil.showInfoAlert(event, "Not any camera was selected to take pic. ");        
                }        
            } else {
                PopupUtil.showInfoAlert(event, "Customer image can be taken only for the verified customer. ");        
            }
        } else {
            try {
                Customer customer = dbOp.getCustomerNameBy(getCustomerIdFor());
                if(customer!= null && customer.getId() != null) { 
                    PopupUtil.showInfoAlert("Same customer id is available in the Customer Id:" 
                            + customer.getId() + " - " + customer.getName() 
                            + customer.getSpouseType() + customer.getSpouseName());
                } else if(customer!= null) {
                    PopupUtil.showInfoAlert("Same customer id is available in the Customer " 
                            + customer.getName() 
                            + customer.getSpouseType() + customer.getSpouseName());                
                }
            } catch (SQLException ex) {
                Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void ivCustomerImageClicked(MouseEvent event) {
    }
    
    public void setImageDetailsValuesToTheField(String id) throws FileNotFoundException, IOException {
           
        File exactCustomerFolder = new File(customerFolder, id);
        
        File custTemp = new File(exactCustomerFolder, CommonConstants.CUSTOMER_IMAGE_NAME);
        if(custTemp.exists()) {                      
            try (FileInputStream fis = new FileInputStream(custTemp)){
                final Image img = new Image(fis);
                ivCustomerBill.setImage(img);
            }
        } else {                      
            ivCustomerBill.setImage(CommonConstants.noImage);
        }
    }
    
}
