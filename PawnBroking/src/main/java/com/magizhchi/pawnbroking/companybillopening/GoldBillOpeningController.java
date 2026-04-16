/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companybillopening;

import com.github.sarxos.webcam.Webcam;
import com.magizhchi.pawnbroking.account.TodaysAccountController;
import com.magizhchi.pawnbroking.account.TodaysAccountJewelRepledgeBean;
import com.magizhchi.pawnbroking.billcalculator.BillCalculatorController;
import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.ConvertNumberToWord;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.NoticeUtil;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.common.Util;
import com.magizhchi.pawnbroking.common.WebCamWork;
import com.magizhchi.pawnbroking.companyadvanceamount.AdvanceAmountBean;
import com.magizhchi.pawnbroking.companybillclosing.GoldBillClosingController;
import com.magizhchi.pawnbroking.companybillclosing.SilverBillClosingController;
import com.magizhchi.pawnbroking.companymaster.CompanyMasterController;
import com.magizhchi.pawnbroking.customerdetails.CustomerDetailsController;
import com.magizhchi.pawnbroking.repledgebillclosing.RepledgeGoldBillClosingController;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
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
import javafx.scene.control.TableRow;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.bytedeco.javacv.FrameGrabber;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class GoldBillOpeningController implements Initializable {

    public Stage dialog;
    public BillOpeningDBOperation dbOp;
    public String sLastSelectedId = null;
    private String sRepledgeBillId = null;
    private String sRebilledFrom = null;
    public String sRebilledNewAmt = null;
    public String sNewPurity = null;
    public String sNewWeight = null;
    public String sRebilledDate = null;
    private String sStatus = null;
    private String sCurrentBillRowNumber;
    private String sCurrentBillPrefix;
    private int iCurrentBillNumber;
    private final ArrayList<String> alItemsList = new ArrayList<>();
    private final ArrayList<String> alFilterDBColumnName = new ArrayList<>();
    private final ArrayList<String> alFilterName = new ArrayList<>();
    private final ArrayList<String> alFilterValue = new ArrayList<>();
    final String goldBillClosingScreen = "/com/magizhchi/pawnbroking/companybillclosing/GoldBillClosing.fxml";
    final String silverBillClosingScreen = "/com/magizhchi/pawnbroking/companybillclosing/SilverBillClosing.fxml";
    final String billCalculatorScreen = "/com/magizhchi/pawnbroking/billcalculator/BillCalculator.fxml";
    private final String reGoldBillClosingScreen = "/com/magizhchi/pawnbroking/repledgebillclosing/RepledgeGoldBillClosing.fxml";
    private final String customerDetailsScreen = "/com/magizhchi/pawnbroking/customerdetails/CustomerDetails.fxml";
    String jewelItemMasterScreen = "/com/magizhchi/pawnbroking/itemmaster/ItemMaster.fxml";
    private GoldBillClosingController billClosingParent = null;
    private boolean isRebillOperation = false; 
    private boolean isMultiRebillOperation = false; 
    private String sMultiRebillAmtTxt;
    private String sMultiRebillAmt;
    private boolean isAutoBillGeneration = true;
    private boolean isEntryMode = false;
    private DataTable otherSettingValues = null;
    private DataTable customerNames = null;
    private DataTable streetNames = null;
    
    private boolean isFromDoorNo = false;
    private boolean isFromBillNo = false;
    private boolean isFromMobileNo = false;
    private boolean isAutoBillOpen = false;
    
    public static boolean isOpenCustomerImgAvailable = false;
    public static boolean isOpenJewelImgAvailable = false;
    public static boolean isOpenUserImgAvailable = false;
    
    private boolean doRatePerGmWork = true;
    private int lastSelectedIndex = -1;
    //private WebCamWork webCamWork = new WebCamWork();
    
    private Stage goldBillOpeningScreenStage;
    private String customerDetails = "CUSTOMERS";
    private File tempFile = new File(CommonConstants.TEMP_FILE_LOCATION);
    private File compFolder = new File(tempFile, CommonConstants.ACTIVE_COMPANY_ID);
    private File materialFolder = new File(compFolder, "GOLD");
    private File customerFolder = new File(tempFile, customerDetails);
    
    
    @FXML
    public TabPane tpScreen;
    @FXML
    private Tab tabMainScreen;
    @FXML
    private ComboBox<String> cbSpouseType;
    @FXML
    private TextField txtBillNumber;
    @FXML
    private DatePicker dpBillOpeningDate;
    @FXML
    private ComboBox<String> txtCustomerName;
    @FXML
    private ToggleGroup rgGenderGroup;
    @FXML
    private TextField txtSpouseName;
    @FXML
    private TextField txtDoorNo;
    @FXML
    private ComboBox<String> txtStreetName;
    @FXML
    private TextField txtArea;
    @FXML
    private TextField txtCity;
    @FXML
    private ComboBox<String> cbItemType;
    @FXML
    private TextField txtMobileNumber;
    @FXML
    private TextField txtNumberOfItems;
    @FXML
    private TextArea txtItems;
    @FXML
    private ComboBox<String> cbStatus;
    @FXML
    private TextArea txtNote;
    @FXML
    public TextField txtAmount;
    @FXML
    private TextField txtInterest;
    @FXML
    private TextField txtDocumentCharge;
    @FXML
    private TextField txtTakenAmount;
    @FXML
    private TextField txtToGiveAmount;
    @FXML
    public TextField txtGivenAmount;
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
    private ComboBox<String> cbAllDetailsFilter;
    @FXML
    private TextField txtAddToFilter;
    @FXML
    private TextField txtFilter;
    @FXML
    private TableView<AllDetailsBean> tbAllDetails;
    @FXML
    private HBox nodeAddToFilter;
    @FXML
    private DatePicker dpAddToFilter;
    @FXML
    private ComboBox<String> cbAddToFilter;
    @FXML
    public TextField txtGrossWeight;
    @FXML
    private TextField txtNetWeight;
    @FXML
    public TextField txtPurity;
    @FXML
    private HBox hSaveModeButtons;
    @FXML
    private Button btAddToFilter;
    @FXML
    private Button btShowAllRecords;
    @FXML
    private Button btShowFilteredRecords;
    @FXML
    private HBox nodeCustomerName;
    @FXML
    private ComboBox<String> cbCustomerName;
    @FXML
    private Label lbScreenMessage;
    @FXML
    private TextField txtSuggestionAmt;
    @FXML
    private DatePicker dpBillCLosingAcceptedDate;
    @FXML
    private ToggleGroup rgAcceptedDateGroup;
    @FXML
    private TableView<CustomerBillsListBean> tbCutomerBillsList;
    @FXML
    private TextField txtRatePerGm;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button btShowInBillCalc;
    @FXML
    private Tab tabAdvanceAmountDetails;
    @FXML
    public TextField txtAdvanceReceiptDetailTotalAmount;
    @FXML
    private TableView<AdvanceAmountBean> tbAdvanceReceiptDetails;
    @FXML
    private TextArea txtRepledgeBillId;
    @FXML
    private TextField txtRepledgeName;
    @FXML
    private TextField txtRepledgeBillNumber;
    @FXML
    private TextField txtCompanyBillNumber;
    @FXML
    private TextField txtRepledgeOpenedDate;
    @FXML
    private TextField txtRepledgeBillAmount;
    @FXML
    private TextField txtRepledgeBillStatus;
    @FXML
    private TextArea txtReBilledFrom;
    @FXML
    private TextField txtReBilledTo;
    @FXML
    private TextField txtNomineeName;
    @FXML
    private TableView<TodaysAccountJewelRepledgeBean> tbRepList;
    @FXML
    private Button btEditCustomerDetails;
    @FXML
    private TextField txtCustomerStatus;
    @FXML
    private Label lbBillNo;
    @FXML
    private Label lbDate;
    @FXML
    private Label lbName;
    @FXML
    private Label lbGender;
    @FXML
    private Label lbAddress;
    @FXML
    private Label lbMobileNo;
    @FXML
    private Label lbCustomerStatus;
    @FXML
    private VBox HBLabelLeftSide;
    @FXML
    private RadioButton rbMale;
    @FXML
    private RadioButton rbFemale;
    @FXML
    private RadioButton rbOther;
    @FXML
    private Label lbNominee;
    @FXML
    private Label lbItems;
    @FXML
    private Label lbWeight;
    @FXML
    private Label lbPurity;
    @FXML
    private Label lbRatePerGm;
    @FXML
    private Label lbSuggestionAmt;
    @FXML
    private Label lbNote;
    @FXML
    private Label lbAccClosingDate;
    @FXML
    private Label lbAmount;
    @FXML
    private Label lbInterest;
    @FXML
    private Label lbDocumentCharge;
    @FXML
    private Label lbTakenAmt;
    @FXML
    private Label lbToGive;
    @FXML
    private Label lbGivenAmt;
    @FXML
    private Label lbStatus;
    @FXML
    private Button btPrintPackingCard;
    @FXML
    private TextField txtCreatedEmpName;
    @FXML
    private TextField txtCreatedTime;
    @FXML
    private ImageView ivCustomerBill;
    @FXML
    private Button btCaptureCustomerImg;
    @FXML
    private ImageView ivJewelBill;
    @FXML
    private Button btCaptureJewelImg;
    @FXML
    private ImageView ivUserBill;
    @FXML
    private Button btPrintCompanyCopy;
    @FXML
    private Button btPrintCustomerCopy;
    @FXML
    private Label lbSuggestionAmt1;
    @FXML
    private TextField txtTotalValue;
    @FXML
    private Label lbCustomerStatus1;
    @FXML
    private Label lbCustomerStatus2;
    @FXML
    private Label lbCustomerStatus3;
    @FXML
    private TextField txtIdProof;
    @FXML
    private TextField txtIdNumber;
    @FXML
    private TextField txtRecomendedBy;
    @FXML
    private TextField txtInterestPerMonth;
    @FXML
    private Button btDenom;
    @FXML
    private ImageView ivCustomerDetails;
    @FXML
    private TextField txtMobileNumber2;
    @FXML
    private TextField txtCustomerVerifyStatus;
    @FXML
    private TextField txtCustomerId;
    @FXML
    private TextField txtOccupation;
    @FXML
    private Label lbStatus1;
    @FXML
    private TextField txtPhysicalLocation;
    @FXML
    private ComboBox<String> cbCustomerImageCameraName;
    @FXML
    private ComboBox<String> cbJewelImageCameraName;
    @FXML
    private ComboBox<String> cbUserImageCameraName;
    @FXML
    private Button btPrintReciept;

    private void setTamilFont() {
        HBLabelLeftSide.setPrefWidth(180);
        lbBillNo.setText("eph; vz;:");
        lbDate.setText("Njjp:");
        lbName.setText("ngaH:");
        lbGender.setText("ghypzk;:");
        lbAddress.setText("Kfthp:");
        lbMobileNo.setText("njhiyNgrp vz;:");
        lbCustomerStatus.setText("customer");
        rbMale.setText("Mz;");
        rbFemale.setText("ngz;");
        
        lbNominee.setText("ehkpdp ngaH:");
        lbItems.setText("nghUs;fspd; tpguk;:");
        lbWeight.setText("vil: ");
        lbPurity.setText("kr;rk;:");
        lbRatePerGm.setText("kr;rk;:");
        lbSuggestionAmt.setText("sdsd");
        lbNote.setText("Fwpg;G:");
        
        lbAccClosingDate.setText("xg;Gf;nfhz;l Njjp:");
        lbAmount.setText("fld; &gha;:");
        lbInterest.setText("tl;b:");
        lbDocumentCharge.setText("tl;b:");
        lbTakenAmt.setText("tl;b:");
        lbToGive.setText("tl;b:");
        lbGivenAmt.setText("tl;b:");
    }
    
    public void setParent(Stage goldBillOpeningScreenStage) {
        
        this.goldBillOpeningScreenStage = goldBillOpeningScreenStage;
        this.goldBillOpeningScreenStage.setOnCloseRequest((WindowEvent e) -> {

        });
        
    }
    
    public void shortCutKeysCode() {
        try {
                anchorPane.addEventFilter( KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
                                {
                                    Robot  eventRobot = new Robot();
                                    @Override
                                    public void handle( KeyEvent KV )
                                    {
                                        switch ( KV.getCode() )
                                        {
                                            case ENTER :
                                            {
                                                /*if ( (!(KV.getTarget() instanceof TextField)) || (!(KV.getTarget() instanceof Button)))
                                                {
                                                    eventRobot.keyPress( java.awt.event.KeyEvent.VK_TAB );
                                                    eventRobot.keyRelease( java.awt.event.KeyEvent.VK_TAB );
                                                    KV.consume();
                                                }*/
                                                break;
                                            }
                                            case TAB :
                                            {
                                                if ( ! (KV.getTarget() instanceof TextField))
                                                {
                                                    KV.consume();
                                                }
                                                break;
                                            }
                                            case O :
                                            {
                                                if(KV.isControlDown()) {
                                                    tgOff.setSelected(true);
                                                    saveModeOFF(null);
                                                }
                                                break;
                                            }                                            
                                            case N :
                                            {
                                                if(KV.isControlDown()) {
                                                    tgOn.setSelected(true);
                                                    saveModeON(null);
                                                }
                                                break;
                                            }                                            
                                            case B :
                                            {
                                                if(KV.isControlDown() && txtBillNumber.isEditable()) {
                                                    txtBillNumber.requestFocus();
                                                    txtBillNumber.positionCaret(txtBillNumber.getText().length());
                                                }
                                                break;
                                            }                                            
                                            case RIGHT :
                                            {
                                                if(KV.isControlDown()) {
                                                    txtAmount.requestFocus();
                                                    txtAmount.positionCaret(txtAmount.getText().length());
                                                }
                                                break;
                                            }                                            
                                            case LEFT :
                                            {
                                                if(KV.isControlDown()) {
                                                    txtCustomerName.getEditor().requestFocus();
                                                    txtCustomerName.getEditor().positionCaret(txtCustomerName.getEditor().getText().length());
                                                }
                                                break;
                                            }                                            
                                            case UP :
                                            {
                                                if(KV.isControlDown()) {
                                                    cbItemType.getEditor().requestFocus();   
                                                    cbItemType.getEditor().positionCaret(cbItemType.getEditor().getText().length());
                                                }
                                                break;
                                            }                                            
                                            case DOWN :
                                            {
                                                if(KV.isControlDown()) {
                                                    txtGrossWeight.requestFocus();
                                                    txtGrossWeight.positionCaret(txtGrossWeight.getText().length());
                                                }
                                                break;
                                            }                                            
                                            case S :
                                            {
                                                if(KV.isControlDown()) {
                                                    if(!btSaveBill.isDisable()) {
                                                        denominationWork(KV);                                                    
                                                        btSaveBillClicked(null);
                                                    } else if(!btUpdateBill.isDisable()) {
                                                        btUpdateBillClicked(null);
                                                    }
                                                }
                                                break;
                                            }                                            
                                            case D :
                                            {
                                                if(KV.isControlDown()) {
                                                    denominationWork(KV);                                                                                                        
                                                }
                                                break;
                                            }                                            
                                            case NUMPAD1 :
                                            {
                                                if(KV.isControlDown()) {
                                                    btCaptureCustomerImgClicked(null);
                                                }
                                                break;
                                            }                                            
                                            case NUMPAD2 :
                                            {
                                                if(KV.isControlDown()) {
                                                    btCaptureJewelImgClicked(null);
                                                }
                                                break;
                                            }                                            
                                            case DIGIT1 :
                                            {
                                                if(KV.isControlDown()) {
                                                    btCaptureCustomerImgClicked(null);
                                                }
                                                break;
                                            }                                            
                                            case DIGIT2 :
                                            {
                                                if(KV.isControlDown()) {
                                                    btCaptureJewelImgClicked(null);
                                                }
                                                break;
                                            }                                            
                                            case ESCAPE :
                                            {					               
                                                Stage sb = (Stage)lbAddress.getScene().getWindow();//use any one object
                                                sb.close();
                                                break;
                                            }
                                                default:
                                                        break;
                                        }
                                    }
                                });
        } catch (AWTException e) {			
                e.printStackTrace();
        }    
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {                        
        
        shortCutKeysCode();
        isOpenCustomerImgAvailable = false;
        isOpenJewelImgAvailable = false;
        isOpenUserImgAvailable = false;
        
        if(CommonConstants.ACTIVE_MACHINE.getLanguage().equals(CommonConstants.TAMIL)) {
            //setTamilFont();
        }
                    
        if(!isRebillOperation) {
            ivCustomerDetails.setImage(CommonConstants.noImage);
            ivCustomerBill.setImage(CommonConstants.noImage);
            ivJewelBill.setImage(CommonConstants.noImage);
            ivUserBill.setImage(CommonConstants.noImage);
        } else {
            btCaptureCustomerImg.setDisable(true);
            btCaptureJewelImg.setDisable(true);
        }
        
        ivCustomerBill.setCache(false);
        ivJewelBill.setCache(false);
        ivUserBill.setCache(false);
        
        tbAllDetails.setRowFactory(tv -> new TableRow<AllDetailsBean>() {
            @Override
            public void updateItem(AllDetailsBean item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle("");
                } else if (!item.getSRepledgeBillId().isEmpty()) {
                    setStyle(Util.getStyle("#000000", "#FCBAEF").toString());    
                } else {
                    setStyle("");
                }
            }
        });
        
        tbCutomerBillsList.setRowFactory(tv -> new TableRow<CustomerBillsListBean>() {
            @Override
            public void updateItem(CustomerBillsListBean item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle("");
                } else if (!item.getCompId().equals(CommonConstants.ACTIVE_COMPANY_ID)) {
                    setStyle(Util.getStyle("#000000", "#999999").toString());
                }  else if (DateRelatedCalculations.isFirstDateIsLesserOrEqualToSecondDate(item.getDate()
                        , item.getNoticedDate())) {
                    setStyle(Util.getStyle("#000000", "#FE7A7D").toString());
                } else if (item.getRepledgeBillId() != null && !item.getRepledgeBillId().isEmpty()) {
                    setStyle(Util.getStyle("#000000", "#FCBAEF").toString());
                } else {
                    setStyle("");
                }
            }
        });
        
        try {
            dbOp = new BillOpeningDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        lbScreenMessage.setVisible(false);
        doInitOperation();

        txtAmount.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                doRatePerGmWork = false;
                if (!newValue.matches("\\d{0,9}([\\.]\\d{0,2})?")) {                    
                    txtAmount.setText(oldValue);   
                    double dAmount = Double.parseDouble(oldValue);
                    setAmountRelatedText(dAmount, null);  
                }  else {
                    if(!"".equals(newValue)) 
                    {
                        double dAmount = Double.parseDouble(newValue);
                        setAmountRelatedText(dAmount, "GOLD");                
                    } else {
                        txtInterest.setText("");
                        txtDocumentCharge.setText("");
                        txtTakenAmount.setText("");
                        txtToGiveAmount.setText("");
                        txtGivenAmount.setText("");
                    }
                }
                doRatePerGmWork = true;
            }
        });

        txtRatePerGm.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(doRatePerGmWork) {
                    if (!newValue.matches("\\d{0,4}?")) {                    
                        //txtRatePerGm.setText(oldValue);   
                        setRatePerGramRelatedText(oldValue);  
                    }  else {
                        if(!"".equals(newValue)) {
                            setRatePerGramRelatedText(newValue);                
                        }
                    }
                }
            }
        });
        
        txtGrossWeight.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,4}([\\.]\\d{0,3})?")) {                    
                    txtGrossWeight.setText(oldValue);   
                    setWeightRelatedText(oldValue);  
                }  else {
                    if(!"".equals(newValue)) {
                        setWeightRelatedText(newValue);                
                    }
                }
            }
        });
        
        txtPurity.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,2}([\\.]\\d{0,1})?")) {
                    txtPurity.setText(oldValue);
                    setPurityWeightRelatedText(oldValue);     
                }  else {
                    if(!"".equals(newValue)) {
                        setPurityWeightRelatedText(newValue);                
                    }
                }
            }
        });              
        
        cbCustomerImageCameraName.getItems().add("DEFAULT");
        cbJewelImageCameraName.getItems().add("DEFAULT");
        cbUserImageCameraName.getItems().add("DEFAULT");
        try {
            for(Webcam webcam : WebCamWork.getWebCamLists()) {
                cbCustomerImageCameraName.getItems().add(webcam.getName());
                cbJewelImageCameraName.getItems().add(webcam.getName());
                cbUserImageCameraName.getItems().add(webcam.getName());
            }
        } catch (Exception e) {
            // No webcam available or driver error — continue with DEFAULT only
        }
        cbCustomerImageCameraName.getSelectionModel().select(0);
        cbJewelImageCameraName.getSelectionModel().select(0);
        cbUserImageCameraName.getSelectionModel().select(0);
    }    
    
    public void doInitOperation() {
    
        try {
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                sCurrentBillRowNumber = billRowAndNumber[0];
                sCurrentBillPrefix = billRowAndNumber[1];
                iCurrentBillNumber = Integer.parseInt(billRowAndNumber[2]);
                isAutoBillGeneration = Boolean.parseBoolean(billRowAndNumber[3]);
                isEntryMode = Boolean.parseBoolean(billRowAndNumber[5]);
                if(isAutoBillGeneration) {
                    txtBillNumber.setEditable(false);
                    txtBillNumber.setMouseTransparent(true);
                    txtBillNumber.setFocusTraversable(false);                                    
                    if(iCurrentBillNumber > 0) {
                        txtBillNumber.setText(billRowAndNumber[1] + billRowAndNumber[2]);
                        this.sLastSelectedId = txtBillNumber.getText();
                    } else {
                        txtBillNumber.setText("");
                        PopupUtil.showErrorAlert("Current bill number does not exists. Please change the number generation setting in company module screen.");
                    }
                } else {
                    txtBillNumber.setText("");
                    txtBillNumber.setEditable(true);
                    txtBillNumber.setMouseTransparent(false);
                    txtBillNumber.setFocusTraversable(true);                
                }
                
                if(billRowAndNumber[4] != null && billRowAndNumber[4].equals(CommonConstants.PAWN)) {
                    nodeCustomerName.getChildren().remove(cbCustomerName);
                } else {                    
                    nodeCustomerName.getChildren().remove(txtCustomerName);                    
                    setCustomerNameValues();
                }
            } 
            
            if(!isRebillOperation) {
                otherSettingValues = dbOp.getOtherSettingsValues("GOLD");
                txtPurity.setText(otherSettingValues.getRow(0).getColumn(1).toString());
                txtCity.setText(otherSettingValues.getRow(0).getColumn(2).toString());            
                txtArea.setText(otherSettingValues.getRow(0).getColumn(5).toString());
                setPhysicalLocation(null);
            }
            
            dpBillCLosingAcceptedDate.setMouseTransparent(false);
            dpBillCLosingAcceptedDate.setFocusTraversable(true);
            
            dpBillOpeningDate.setValue(LocalDate.now());

            setSpouseTypeValues(true);
            setBillClosingAcceptedValues(true);
            setJewelItemValues(null);
            
            nodeAddToFilter.getChildren().remove(dpAddToFilter);
            nodeAddToFilter.getChildren().remove(cbAddToFilter);
            
            txtCustomerName.getEditor().setOnKeyTyped((KeyEvent e) -> {

                boolean isEnter = "\r".equals(e.getCharacter());
                String pattern= "^[a-zA-Z0-9 ]*$";

                if(e.getCharacter().matches(pattern)) {
                    // Use runLater so getText() is read AFTER JavaFX inserts the character
                    Platform.runLater(() -> {
                        setTxtCustomerNameValues(txtCustomerName.getEditor().getText().toUpperCase());
                        txtCustomerName.show();
                    });
                } else if(isEnter && !isFromBillNo) {
                    int sIndex = txtCustomerName.getSelectionModel().getSelectedIndex();
                    lastSelectedIndex = sIndex;
                    if(sIndex >=0) {
                        txtCustomerName.setValue(
                                customerNames.getRow(sIndex).getColumn(1).toString());
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
                        cbSpouseType.setValue(customerNames.getRow(sIndex).getColumn(3).toString());
                        txtSpouseName.setText(customerNames.getRow(sIndex).getColumn(4).toString());      
                        txtDoorNo.setText(customerNames.getRow(sIndex).getColumn(5).toString());
                        txtStreetName.setValue(customerNames.getRow(sIndex).getColumn(6).toString());
                        txtArea.setText(customerNames.getRow(sIndex).getColumn(7).toString());
                        txtCity.setText(customerNames.getRow(sIndex).getColumn(8).toString());
                        txtMobileNumber.setText(customerNames.getRow(sIndex).getColumn(9).toString());
                        if(customerNames.getRow(sIndex).getColumn(10).toString().equals("BLOCKED")) {
                            txtCustomerStatus.setText(customerNames.getRow(sIndex).getColumn(10).toString());
                            txtCustomerStatus.setStyle("-fx-background-color: #FF5555");
                        } else {
                            txtCustomerStatus.setText("ACTIVE");
                            txtCustomerStatus.setStyle("-fx-background-color: #55FF30");
                        }                    
                        txtMobileNumber2.setText(customerNames.getRow(sIndex).getColumn(11).toString());
                        txtIdProof.setText(customerNames.getRow(sIndex).getColumn(12).toString());
                        txtIdNumber.setText(customerNames.getRow(sIndex).getColumn(13).toString());
                        txtRecomendedBy.setText(customerNames.getRow(sIndex).getColumn(14).toString());
                        txtCustomerId.setText(customerNames.getRow(sIndex).getColumn(15).toString());
                        txtOccupation.setText(customerNames.getRow(sIndex).getColumn(16).toString());
                        if(txtCustomerId.getText() != null && !txtCustomerId.getText().isEmpty()) {
                            txtCustomerVerifyStatus.setText("VERIFIED");
                            txtCustomerVerifyStatus.setStyle("-fx-background-color: #55FF30");
                        } else {
                            txtCustomerVerifyStatus.setText("NOT-VERIFIED");
                            txtCustomerVerifyStatus.setStyle("-fx-background-color: #FF5555");
                        }
                        
                        if(!txtCustomerId.getText().isEmpty()) {
                            ivCustomerDetails.setImage(CommonConstants.loadingImage);

                            Platform.runLater(() -> {
                                try {
                                    setCustomerImageValuesToTheField(txtCustomerId.getText());
                                } catch (FileNotFoundException ex) {
                                    Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
                                    Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            });
                        } else {
                            ivCustomerDetails.setImage(CommonConstants.noImage);
                        }
                        
                        setCustomerBillsListTableVals(customerNames, sIndex);
                        txtMobileNumberOnAction(null);
                    } else {
                        txtSpouseName.requestFocus();
                        txtSpouseName.positionCaret(txtSpouseName.getText().length());     
                    }                                        
                } else {
                    e.consume();
                }
                isFromBillNo = false;
            });

            txtCustomerName.getEditor().setOnKeyPressed((KeyEvent e) -> {
                if(e.getCode() == KeyCode.BACK_SPACE) {
                    // Use runLater so getText() is read AFTER JavaFX applies the backspace
                    Platform.runLater(() -> {
                        String sText = txtCustomerName.getEditor().getText();
                        if(!sText.isEmpty()) {
                            setTxtCustomerNameValues(sText.toUpperCase());
                        } else {
                            setTxtCustomerNameValues(null);
                        }
                        txtCustomerName.show();
                    });
                } else if(e.getCode() == KeyCode.DOWN) {
                    txtCustomerName.show();
                }
            });

            /*txtCustomerName.setCellFactory((ListView<String> param) -> {
                ListCell<String> cell = new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                        setTextFill(Color.GREEN);
                        setBackground(Color.AQUA);
                    }
                };
                return cell;
            });*/
            
            txtStreetName.getEditor().setOnKeyTyped((KeyEvent e) -> {

                boolean isEnter = "\r".equals(e.getCharacter());
                String pattern= "^[a-zA-Z0-9 ]*$";

                if(e.getCharacter().matches(pattern)) {
                    Platform.runLater(() -> {
                        setTxtStreetNameValues(txtStreetName.getEditor().getText().toUpperCase());
                        txtStreetName.show();
                    });
                } else if(isEnter && !isFromDoorNo) {
                    int sIndex = txtStreetName.getSelectionModel().getSelectedIndex();
                    if(sIndex >=0) {
                        txtStreetName.setValue(streetNames.getRow(sIndex).getColumn(1).toString());
                        txtArea.setText(streetNames.getRow(sIndex).getColumn(2).toString());
                        txtCity.setText(streetNames.getRow(sIndex).getColumn(3).toString());
                        cbItemType.getEditor().requestFocus();
                    } else {
                        txtArea.requestFocus();
                        txtArea.positionCaret(txtArea.getText().length());
                    }
                    tbCutomerBillsList.getItems().removeAll(tbCutomerBillsList.getItems());
                } else {
                    e.consume();
                }
                isFromDoorNo = false;
            });

            txtStreetName.getEditor().setOnKeyPressed((KeyEvent e) -> {
                if(e.getCode() == KeyCode.BACK_SPACE) {
                    Platform.runLater(() -> {
                        String sText = txtStreetName.getEditor().getText();
                        if(!sText.isEmpty()) {
                            setTxtStreetNameValues(sText.toUpperCase());
                        } else {
                            setTxtStreetNameValues(null);
                        }
                        txtStreetName.show();
                    });
                } else if(e.getCode() == KeyCode.DOWN) {
                    txtStreetName.show();
                }
            });

            cbItemType.getEditor().setOnKeyTyped((KeyEvent e) -> {

                boolean isEnter = "\r".equals(e.getCharacter());
                String pattern= "^[a-zA-Z0-9 ]*$";
                if(e.getCharacter().matches(pattern)) {
                    Platform.runLater(() -> {
                        setJewelItemValues(cbItemType.getEditor().getText().toUpperCase());
                        cbItemType.show();
                    });
                } else if(isEnter && !isFromMobileNo) {
                    //if(tgOn.isSelected()) {
                        if(cbItemType.getItems().contains("ADD ITEMS") &&
                                !cbItemType.getEditor().getText().isEmpty()) {
                            if(cbItemType.getSelectionModel().getSelectedItem() != null &&
                                    cbItemType.getSelectionModel().getSelectedItem().equals("ADD ITEMS"))
                            {
                                Stage dialog = new Stage();
                                dialog.initModality(Modality.WINDOW_MODAL);
                                FXMLLoader loader = new FXMLLoader(getClass().getResource(jewelItemMasterScreen));
                                Parent root = null;
                                try {
                                    root = (Parent) loader.load();
                                } catch (IOException ex) {
                                    Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                dialog.setTitle("Jewel Item Module");
                                dialog.setX(CommonConstants.SCREEN_X);
                                dialog.setY(CommonConstants.SCREEN_Y);
                                dialog.setWidth(CommonConstants.SCREEN_WIDTH);
                                dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
                                dialog.setResizable(false);
                                Scene scene = new Scene(root);
                                dialog.setScene(scene);
                                dialog.initOwner(((Node)e.getSource()).getScene().getWindow());
                                dialog.showAndWait();
                                setJewelItemValues(null);
                            }
                        }
                    //}

                    txtNumberOfItems.requestFocus();

                } else {
                    e.consume();
                }
                isFromMobileNo = false;
            });

            cbItemType.getEditor().setOnKeyPressed((KeyEvent e) -> {
                if(e.getCode() == KeyCode.BACK_SPACE) {
                    Platform.runLater(() -> {
                        String sText = cbItemType.getEditor().getText();
                        if(!sText.isEmpty()) {
                            setJewelItemValues(sText.toUpperCase());
                        } else {
                            setJewelItemValues(null);
                        }
                        cbItemType.show();
                    });
                } else if(e.getCode() == KeyCode.DOWN) {
                    cbItemType.show();
                }
            });
            
            try {
                if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.GOLD_BILL_OPENING_SCREEN, "ALLOW_ADD") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                    btSaveBill.setDisable(false);
                } else {
                    btSaveBill.setDisable(true);
                }
            } catch (SQLException ex) {
                Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.GOLD_BILL_OPENING_SCREEN, "ALLOW_VIEW") || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
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
                Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
            }                    
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(14).toString())) {
            txtGivenAmount.setEditable(true);               
            txtGivenAmount.setMouseTransparent(false);
            txtGivenAmount.setFocusTraversable(true);                
        } else {
            txtGivenAmount.setEditable(false);               
            txtGivenAmount.setMouseTransparent(true);
            txtGivenAmount.setFocusTraversable(false);                
        }
        
        try {
            openDateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }               
        
        if(isAutoBillGeneration) {
            Platform.runLater(() -> {
                txtCustomerName.getEditor().requestFocus();
                txtCustomerName.getEditor().positionCaret(txtBillNumber.getText().length());
            }); 
        } else {
            Platform.runLater(() -> {
                txtBillNumber.requestFocus();
                txtBillNumber.positionCaret(txtBillNumber.getText().length());
            });             
        }        
    }

    public void setJewelItemValues(String sItemName) {
        Platform.runLater(()->{
            try {
                cbItemType.getItems().removeAll(cbItemType.getItems());
                DataTable jewelItemValues = dbOp.getAllActiveJewelItems("GOLD", sItemName);
                for(int i=0; i<jewelItemValues.getRowCount(); i++) {          
                    cbItemType.getItems().add(jewelItemValues.getRow(i).getColumn(0).toString());
                }                
                cbItemType.getItems().add("ADD ITEMS");
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }                
        });
    }

    public void setTxtCustomerNameValues(String sCustomerName) {
        Platform.runLater(()->{
            try {
                txtCustomerName.getItems().removeAll(txtCustomerName.getItems());
                customerNames = dbOp.getFilteredCustomerNames(sCustomerName);
                for(int i=0; i<customerNames.getRowCount(); i++) {          
                    txtCustomerName.getItems().add(customerNames.getRow(i).getColumn(0).toString());
                }                
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }                
        });
    }

    public void setTxtStreetNameValues(String sStreetName) {
        Platform.runLater(()->{
            try {
                txtStreetName.getItems().removeAll(txtStreetName.getItems());
                streetNames = dbOp.getFilteredStreetNames(sStreetName);
                for(int i=0; i<streetNames.getRowCount(); i++) {          
                    txtStreetName.getItems().add(streetNames.getRow(i).getColumn(0).toString());
                }                
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }                
        });
    }
    
    public void setCustomerNameValues() {
        Platform.runLater(()->{
            try {
                cbCustomerName.getItems().removeAll(cbCustomerName.getItems());
                customerNames = dbOp.getAllCustomerNames();
                for(int i=0; i<customerNames.getRowCount(); i++) {          
                    cbCustomerName.getItems().add(customerNames.getRow(i).getColumn(0).toString());
                }                                
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }                
        });
    }

    public void setSpouseTypeValues(boolean selectFirstVal) {
        
        Platform.runLater(()->{
            cbSpouseType.getItems().removeAll(cbSpouseType.getItems());
            RadioButton selectedRadioButton = (RadioButton) rgGenderGroup.getSelectedToggle();        
            if(null != selectedRadioButton.getText()) switch (selectedRadioButton.getText()) {
                case "Male":
                    cbSpouseType.getItems().add("S/O");
                    cbSpouseType.getItems().add("F/O");
                    cbSpouseType.getItems().add("H/O");
                    break;
                case "Female":
                    cbSpouseType.getItems().add("W/O");
                    cbSpouseType.getItems().add("D/O");
                    cbSpouseType.getItems().add("M/O");
                    break;
                default:
                    cbSpouseType.getItems().add("S/O");
                    cbSpouseType.getItems().add("F/O");
                    cbSpouseType.getItems().add("H/O");
                    cbSpouseType.getItems().add("D/O");
                    cbSpouseType.getItems().add("M/O");
                    cbSpouseType.getItems().add("W/O");
                    break;
            }        
            if(selectedRadioButton.getText() != null && selectFirstVal) {
                cbSpouseType.getSelectionModel().select(0);
            } else {
                
            }
        });
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
    
    public void setBillClosingAcceptedValues(boolean selectFirstVal) {
        
        Platform.runLater(()->{            
            RadioButton selectedRadioButton = (RadioButton) rgAcceptedDateGroup.getSelectedToggle();    
            String closingDate = null;
            if(null != selectedRadioButton.getText()) switch (selectedRadioButton.getText()) {
                case "1Y":
                    closingDate = DateRelatedCalculations.getAfterYearOrMonDateWithFormatted(CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue()), 
                                    Calendar.YEAR, 1);
                    break;
                case "9M":
                    closingDate = DateRelatedCalculations.getAfterYearOrMonDateWithFormatted(CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue()), 
                                    Calendar.MONTH, 9);
                    break;
                case "6M":
                    closingDate = DateRelatedCalculations.getAfterYearOrMonDateWithFormatted(CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue()), 
                                    Calendar.MONTH, 6);
                    break;
                case "3M":
                    closingDate = DateRelatedCalculations.getAfterYearOrMonDateWithFormatted(CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue()), 
                                    Calendar.MONTH, 3);
                    break;
                case "1M":
                    closingDate = DateRelatedCalculations.getAfterYearOrMonDateWithFormatted(CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue()), 
                                    Calendar.MONTH, 1);
                    break;
                default:
                    closingDate = DateRelatedCalculations.getAfterYearOrMonDateWithFormatted(CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue()), 
                                    Calendar.YEAR, 1);
                    break;
            }        
            dpBillCLosingAcceptedDate.setValue(LocalDate.parse(closingDate, CommonConstants.DATETIMEFORMATTER));
        });
    }
    
    @FXML
    private void txtNumberOfItemsOnAction(ActionEvent event) {

        String sNumberOfItems = txtNumberOfItems.getText();

        if(Integer.parseInt((!("".equals(sNumberOfItems)) ? sNumberOfItems : "0")) > 0) {
            try {
                String sItem = cbItemType.getSelectionModel().getSelectedItem().toUpperCase();
                if(dbOp.isvalidItemTyped(sItem, "GOLD")) {
                    String sItemsWithNumber = sItem + "-" + sNumberOfItems;
                    alItemsList.add(sItemsWithNumber);

                    if(txtItems.getText().length() > 0) {
                        txtItems.setText(txtItems.getText() + ", " + sItemsWithNumber);
                    } else {
                        txtItems.setText(sItemsWithNumber);
                    }
                    txtGrossWeight.requestFocus();
                } else {
                    PopupUtil.showInfoAlert(event, "No such item in a item module.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            PopupUtil.showInfoAlert(event, "Number of items cannot be empty or zero.");
        }
    }

    @FXML
    private void rbToggleChanged(MouseEvent event) {
        
        setSpouseTypeValues(true);
        txtSpouseName.requestFocus();
        txtSpouseName.positionCaret(txtSpouseName.getText().length());
    }

    @FXML
    private void cbItemTypeOnAction(ActionEvent event) {
        if ("ADD ITEMS".equals(cbItemType.getValue())) {
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(jewelItemMasterScreen));
            Parent root = null;
            try {
                root = (Parent) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            dialog.setTitle("Jewel Item Module");
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            dialog.setScene(new Scene(root));
            dialog.initOwner(cbItemType.getScene().getWindow());
            dialog.showAndWait();
            setJewelItemValues(null);
            Platform.runLater(() -> txtNumberOfItems.requestFocus());
        }
    }

    @FXML
    private void btItemsUndoClicked(ActionEvent event) {
        
        if(alItemsList.size() > 0) {
            alItemsList.remove(alItemsList.size()-1);
            txtItems.setText("");

            for(int i=0; i<alItemsList.size(); i++) {
                if(txtItems.getText().length() > 0) {
                    txtItems.setText(txtItems.getText() + ", " + alItemsList.get(i));
                } else {
                    txtItems.setText(alItemsList.get(i));
                }        
            }
            txtNumberOfItems.requestFocus();
        }
    }
        
    @FXML
    private void btItemsClearAllClicked(ActionEvent event) {
        
        txtItems.setText("");
        alItemsList.removeAll(alItemsList);
        txtNumberOfItems.requestFocus();
        
    }

    private void txtWeightOnAction(ActionEvent event) {
    
        txtAmount.requestFocus();
    }
        
    public void setAmountRelatedText(double dAmount, String sMaterialType) 
    {
    
        try {
            String sInterest = dbOp.getInterest(CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue()), dAmount, "GOLD").trim();
            String sDocumentCharge = dbOp.getDocumentCharge(CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue()), dAmount, "GOLD").trim();
            String sFormula = dbOp.getFormula(CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue()), dAmount, "GOLD");
            
            double dInterest = sInterest != null ? Double.parseDouble((!"".equals(sInterest)) ? sInterest : "0") : 0;
            double dDocumentCharge = sDocumentCharge != null ? Double.parseDouble((!"".equals(sDocumentCharge)) ? sDocumentCharge : "0") : 0;                   
            
            String[][] replacements = {{"AMOUNT", String.valueOf(dAmount)}, 
                                       {"INTEREST", sInterest},
                                       {"DOCUMENT_CHARGE", sDocumentCharge}};            
            for(String[] replacement: replacements) {
                sFormula = sFormula.replace(replacement[0], replacement[1]);
            }
            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");            
            String sTakenAmount = engine.eval(sFormula).toString() != null ? 
                    String.valueOf(Math.round(Double.parseDouble(engine.eval(sFormula).toString()))) : "0";            
            String sToGive = Double.toString(dAmount - Double.parseDouble(sTakenAmount));
            String sPerMonth = Double.toString(Double.parseDouble(sTakenAmount) - Double.parseDouble(sDocumentCharge));
            
            txtInterestPerMonth.setText(sPerMonth);
            txtInterest.setText(sInterest);
            txtDocumentCharge.setText(sDocumentCharge);
            txtTakenAmount.setText(sTakenAmount);
            txtToGiveAmount.setText(sToGive);
            txtGivenAmount.setText(sToGive);
            
            double dGrossWt = Double.parseDouble(txtGrossWeight.getText().isEmpty() ? "0" : txtGrossWeight.getText());
            double dRatePerGm = dAmount / dGrossWt;
            txtRatePerGm.setText(String.valueOf(Math.round(dRatePerGm)));
            
            double dSuggestionAmt = Double.parseDouble(txtSuggestionAmt.getText().isEmpty() ? "0" : txtSuggestionAmt.getText()); 
            if(dAmount > 0 && dSuggestionAmt > 0 && dSuggestionAmt >= dAmount) {
                txtSuggestionAmt.setStyle("-fx-background-color: #55FF30");
            } else if(dAmount > 0 && dSuggestionAmt > 0 && dSuggestionAmt < dAmount) {
                txtSuggestionAmt.setStyle("-fx-background-color: #FF5555");
            } else {
                txtSuggestionAmt.setStyle("-fx-background-color: #FFFFFF");
            }
            
        } catch (SQLException | ScriptException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setPhysicalLocation(String existingLoc) {
        
        if(tgOn.isSelected()) {
            String sPhysicalLocation = CommonConstants.WEIGHT_MACHINE;
            String sStatus = cbStatus.getValue();

            String sSpouseName = txtSpouseName.getText().toUpperCase();                                
            String sItems = txtItems.getText().toUpperCase();


            if(sStatus.equals("OPENED") 
                    && sItems != null
                    && !sItems.isEmpty()) {
                sPhysicalLocation = CommonConstants.CASH_DRAWER;
            } else if(sStatus.equals("LOCKED") 
                    && (sRepledgeBillId == null 
                    || sRepledgeBillId.isEmpty())) {
                sPhysicalLocation = CommonConstants.SHOP_LOCKER;
            } else if(sStatus.equals("LOCKED") && sRepledgeBillId.contains("REPBILL")) {
                sPhysicalLocation = CommonConstants.REPLEDGE_LOCKER;
            } else if(sStatus.equals("CANCELED")) {
                sPhysicalLocation = CommonConstants.NONE;
            } else if(sStatus.equals("REBILLED") || sStatus.equals("REBILLED-REMOVED")
                    || sStatus.equals("REBILLED-ADDED") || sStatus.equals("REBILLED-MULTIPLE")) {
                sPhysicalLocation = CommonConstants.REBILLED;
            }
            txtPhysicalLocation.setText(sPhysicalLocation);
        } else if(existingLoc != null) {
            txtPhysicalLocation.setText(existingLoc);
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

    @FXML
    private void allowNumberAndDotOnType(KeyEvent e) {
        
        if(!("0123456789.".contains(e.getCharacter()))){ 
            e.consume();
        }
    }

    @FXML
    private void btClearAllClicked(ActionEvent event) {       
            clearAllHeader();
    }
    
    @FXML
    private void saveModeON(ActionEvent event) {
        
        sLastSelectedId = null;
        sRepledgeBillId = null;
        sRebilledFrom = null;
        sStatus = null;
        clearAllHeader();
        doAllSaveModeONWork();        
    }

    public void doAllSaveModeONWork() {
    
        btSaveBill.setDisable(false);
        btEditCustomerDetails.setDisable(false);
        btUpdateBill.setDisable(true);
        btPrintCompanyCopy.setDisable(true);
        btPrintCustomerCopy.setDisable(true);
        txtBillNumber.setEditable(false);
        txtBillNumber.setMouseTransparent(true);
        txtBillNumber.setFocusTraversable(false);                
        txtCustomerName.setEditable(true);
        txtCustomerName.setMouseTransparent(false);
        txtCustomerName.setFocusTraversable(true);        
        txtNumberOfItems.setEditable(true);
        txtNumberOfItems.setMouseTransparent(false);
        txtNumberOfItems.setFocusTraversable(true);                
        txtGrossWeight.setEditable(true);
        txtGrossWeight.setMouseTransparent(false);
        txtGrossWeight.setFocusTraversable(true);                
        txtNetWeight.setEditable(true);
        txtNetWeight.setMouseTransparent(false);
        txtNetWeight.setFocusTraversable(true);                
        txtPurity.setEditable(true);
        txtPurity.setMouseTransparent(false);
        txtPurity.setFocusTraversable(true);                
        txtAmount.setEditable(true);
        txtAmount.setMouseTransparent(false);
        txtAmount.setFocusTraversable(true);                

        if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(14).toString())) {
            txtGivenAmount.setEditable(true);               
            txtGivenAmount.setMouseTransparent(false);
            txtGivenAmount.setFocusTraversable(true);                
        } else {
            txtGivenAmount.setEditable(false);               
            txtGivenAmount.setMouseTransparent(true);
            txtGivenAmount.setFocusTraversable(false);                
        }
        
        cbStatus.getItems().removeAll(cbStatus.getItems());
        cbStatus.getItems().addAll("OPENED", "LOCKED");
        cbStatus.setValue("OPENED");
        doInitOperation();   
    }
    
    @FXML
    private void saveModeOFF(ActionEvent event) {
        
        sLastSelectedId = null;
        sRepledgeBillId = null;
        sRebilledFrom = null;
        sStatus = null;
        clearAllHeader();
        doAllSaveModeOFFWork();        
    }

    public void doAllSaveModeOFFWork() {
    
        btSaveBill.setDisable(true);
        btEditCustomerDetails.setDisable(true);
        btClearAll.setDisable(true);
        btUpdateBill.setDisable(false);
        btPrintCompanyCopy.setDisable(false);
        btPrintCustomerCopy.setDisable(false);
        txtBillNumber.setText("");        
        txtBillNumber.setEditable(true);
        txtBillNumber.setMouseTransparent(false);
        txtBillNumber.setFocusTraversable(true);        
        /*txtCustomerName.setEditable(false);
        txtCustomerName.setMouseTransparent(true);
        txtCustomerName.setFocusTraversable(false);   */     
        if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(4).toString())) {
            txtAmount.setEditable(true);
            txtAmount.setMouseTransparent(false);
            txtAmount.setFocusTraversable(true);                
        } else {
            txtAmount.setEditable(false);
            txtAmount.setMouseTransparent(true);
            txtAmount.setFocusTraversable(false);                
        }
        
        if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(14).toString())) {             
            txtGivenAmount.setEditable(true);               
            txtGivenAmount.setMouseTransparent(false);
            txtGivenAmount.setFocusTraversable(true);                
        } else {
            txtGivenAmount.setEditable(false);               
            txtGivenAmount.setMouseTransparent(true);
            txtGivenAmount.setFocusTraversable(false);                
        }

        cbStatus.getItems().removeAll(cbStatus.getItems());
        cbStatus.getItems().addAll("OPENED", "LOCKED", "CANCELED");
        cbStatus.setValue("OPENED");
        setPhysicalLocation(null);
        try {
            if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                    CommonConstants.GOLD_BILL_OPENING_SCREEN, "ALLOW_UPDATE") 
                    || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                btUpdateBill.setDisable(false);
            } else {
                btUpdateBill.setDisable(true);
            }
            
            String[] billRowAndNumber = dbOp.getGoldCurrentBillNumber();
            if(billRowAndNumber != null) {
                txtBillNumber.setText(billRowAndNumber[1]);
            }        
            
            openDateRestriction();
            
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                        
        
        Platform.runLater(() -> {
            txtBillNumber.requestFocus();
            txtBillNumber.positionCaret(txtBillNumber.getText().length());
        });        
        
    }
    
    @FXML
    private void btSaveBillClicked(ActionEvent event) {
        
        Pattern pattern = Pattern.compile(".*[1-9].*");
        
        String sBillNumber = txtBillNumber.getText().toUpperCase();
        
        if(!txtCustomerStatus.getText().equals("BLOCKED")) {
            if(!sBillNumber.isEmpty()) 
            {
                if(pattern.matcher(sBillNumber).matches()) 
                {
                    String sBillOpeningDate = CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue());

                    if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sBillOpeningDate))
                    {
                        try {
                            String sFirstDuePaidDate = dbOp.getFirstDuePaidDate(sBillNumber, "GOLD");

                                if(sFirstDuePaidDate == null || DateRelatedCalculations.isFirstDateIsLesserOrEqualToSecondDate(sBillOpeningDate, sFirstDuePaidDate))
                                {
                                    setPhysicalLocation(null);
                                    String sName = null;
                                    if(nodeCustomerName.getChildren().contains(txtCustomerName)) {
                                        sName = txtCustomerName.getValue() != null
                                                ? txtCustomerName.getValue().toUpperCase()
                                                : "";
                                    } else {
                                        sName = cbCustomerName.getValue() != null
                                                ? cbCustomerName.getValue().toUpperCase()
                                                : "";
                                    } 
                                    String sGender = ((RadioButton) rgGenderGroup.getSelectedToggle()).getText().toUpperCase();
                                    String sSpouseType = cbSpouseType.getValue().toUpperCase();
                                    String sSpouseName = txtSpouseName.getText().toUpperCase();                                
                                    String sDoorNo = txtDoorNo.getText().toUpperCase();
                                    String sStreetName = txtStreetName.getValue().toUpperCase();
                                    String sArea = txtArea.getText().toUpperCase();
                                    String sCity = txtCity.getText().toUpperCase();
                                    String sMobileNumber = txtMobileNumber.getText().toUpperCase();
                                    
                                    String sMobileNumber2 = txtMobileNumber2.getText();
                                    String sIdProof = txtIdProof.getText();
                                    String sIdNumber = txtIdNumber.getText();
                                    String sRecommendedBy = txtRecomendedBy.getText();
                                    String sCustomerId = txtCustomerId.getText();
                                    String sOccupation = txtOccupation.getText();
                                    String sPhysicalLocation = txtPhysicalLocation.getText().toUpperCase();
                                    
                                    String sItems = txtItems.getText().toUpperCase();
                                    String sGrossWeight = txtGrossWeight.getText().toUpperCase();
                                    String sNetWeight = txtNetWeight.getText().toUpperCase();
                                    String sPurity = txtPurity.getText().toUpperCase();
                                    String sStatus = cbStatus.getValue().toUpperCase();
                                    String sNote = txtNote.getText().toUpperCase();
                                    String sAmount = txtAmount.getText().toUpperCase();
                                    String sInterest = txtInterest.getText().toUpperCase();
                                    String sDocumentCharge = txtDocumentCharge.getText().toUpperCase();
                                    String sTakenAmount = txtTakenAmount.getText().toUpperCase();
                                    String sToGiveAmount = txtToGiveAmount.getText().toUpperCase();
                                    String sGivenAmount = txtGivenAmount.getText().toUpperCase();
                                    String sAcceptedDate = CommonConstants.DATETIMEFORMATTER.format(dpBillCLosingAcceptedDate.getValue());
                                    String sNomineeName = txtNomineeName.getText() != null 
                                            ? txtNomineeName.getText().toUpperCase() 
                                            : null;
                                    
                                    double dAmount = Double.parseDouble(!("".equals(sAmount))? sAmount : "0");
                                    double dInterest = Double.parseDouble(!("".equals(sInterest))? sInterest : "0");
                                    double dDocumentCharge = Double.parseDouble(!("".equals(sDocumentCharge))? sDocumentCharge : "0");
                                    double dTakenAmount = Double.parseDouble(!("".equals(sTakenAmount))? sTakenAmount : "0");
                                    double dToGiveAmount = Double.parseDouble(!("".equals(sToGiveAmount))? sToGiveAmount : "0");
                                    double dGivenAmount = Double.parseDouble(!("".equals(sGivenAmount))? sGivenAmount : "0");
                                    double dGrossWeight = Double.parseDouble(!("".equals(sGrossWeight))? sGrossWeight : "0");
                                    double dNetWeight = Double.parseDouble(!("".equals(sNetWeight))? sNetWeight : Double.toString(dGrossWeight));
                                    double dPurity = Double.parseDouble(!("".equals(sPurity))? sPurity : "75");
                                                                        
                                    try {
                                        if(isValidHeaderValues(sBillNumber, sName, sStreetName, sArea, sCity, sItems, sGrossWeight, sPurity, sAmount, sGivenAmount)) {

                                            if(dbOp.isvalidBillNumberToSave(sBillNumber, "GOLD")) {

                                                if(dbOp.isAlreadyExistingCustomer(sName, sSpouseType, sSpouseName, sDoorNo, sStreetName, sArea, sCity)) {
                                                    dbOp.updateCustomerMobileNumber(sName, sSpouseType, sSpouseName, sDoorNo, sStreetName, sArea, sCity, sMobileNumber);
                                                }
                                                                                                                                                                                                
                                                if(dbOp.saveRecord(sRepledgeBillId, sRebilledFrom, sBillNumber, 
                                                        sBillOpeningDate, sName, sGender, 
                                                        sSpouseType, sSpouseName,
                                                        sDoorNo, sStreetName, sArea, sCity, sMobileNumber, sItems, 
                                                        dGrossWeight, dNetWeight, 
                                                        dPurity, sStatus, sNote, 
                                                        dAmount, dInterest, dDocumentCharge, dTakenAmount, dToGiveAmount, 
                                                        dGivenAmount, "GOLD", sAcceptedDate,
                                                        sNomineeName, sMobileNumber2, sIdProof, sIdNumber, sRecommendedBy, 
                                                        sCustomerId, sOccupation, sPhysicalLocation)) {                                                                                                        
                                                    
                                                    if(isRebillOperation && !isMultiRebillOperation) {
                                                        double dGiveInHandAmount = Double.parseDouble(txtGivenAmount.getText()) 
                                                                - Double.parseDouble(billClosingParent.txtGotAmount.getText());
                                                        String sGiveInHandAmount = Double.toString(dGiveInHandAmount);
                                                        sGiveInHandAmount = sGiveInHandAmount.replace("-", "");            

                                                        if(dGiveInHandAmount > 0) {
                                                            PopupUtil.showInfoAlert(event, "Give Rs."+sGiveInHandAmount+" to the customer "
                                                                    + "for this rebill("+ sRebilledFrom + " to " + sBillNumber +").");
                                                        } else if(dGiveInHandAmount < 0) {
                                                            PopupUtil.showInfoAlert(event, "Get Rs."+sGiveInHandAmount+" from the customer "
                                                                    + "for this rebill("+ sRebilledFrom + " to " + sBillNumber +").");                                                        
                                                        }
                                                    } else if(isRebillOperation && isMultiRebillOperation) {
                                                            PopupUtil.showInfoAlert(event, this.sMultiRebillAmtTxt 
                                                                    + " Rs:" + this.sMultiRebillAmt
                                                                    + " to the customer "
                                                                    + "for this rebill("+ sRebilledFrom + " to " + sBillNumber +").");                                                    
                                                    }
                                                                                                        
                                                    if(isAutoBillGeneration) 
                                                    {
                                                        if(dbOp.isValidNextBillNumber(sCurrentBillRowNumber, iCurrentBillNumber+1, "GOLD")) {

                                                            if(dbOp.updateGoldPreNumberDetail(sCurrentBillRowNumber, Integer.toString(iCurrentBillNumber), sCurrentBillPrefix, sCurrentBillRowNumber, Integer.toString(iCurrentBillNumber+1), sCurrentBillPrefix)) {
                                                                captureUserImageWork(event, sBillNumber);
                                                                saveImages(sBillNumber);
                                                                PopupUtil.showInfoAlert(event, "Bill number ("+ sBillNumber +") opened successfully.");                                                                                                          
                                                                DataTable otherSettingValues = dbOp.getOtherSettingsValues("GOLD");
                                                                if(Boolean.valueOf(otherSettingValues.getRow(0).getColumn(6).toString()))
                                                                {
                                                                    //print work    
                                                                    boolean isDirect = Boolean.valueOf(otherSettingValues.getRow(0).getColumn(13).toString());
                                                                    companyCopyPrintWork(null, isDirect);
                                                                    customerCopyPrintWork(null, isDirect);
                                                                    packingCardPrintWork(null, isDirect);
                                                                }
                                                                iCurrentBillNumber = iCurrentBillNumber + 1; 
                                                                txtBillNumber.setText(sCurrentBillPrefix + Integer.toString(iCurrentBillNumber));
                                                                this.sLastSelectedId = txtBillNumber.getText();
                                                                clearAllHeader();
                                                                txtPurity.setText(otherSettingValues.getRow(0).getColumn(1).toString());
                                                                txtCity.setText(otherSettingValues.getRow(0).getColumn(2).toString());
                                                                txtArea.setText(otherSettingValues.getRow(0).getColumn(5).toString());
                                                                if(billClosingParent != null) {
                                                                    billClosingParent.dialog.close();
                                                                }
                                                            }
                                                        } else {

                                                            for(int i=Integer.parseInt(sCurrentBillRowNumber)+1; i<=5; i++) {

                                                                String[] sNextNumber = dbOp.getNextNumber(Integer.toString(i), "GOLD");

                                                                if(Integer.parseInt(sNextNumber[1]) > 0) {

                                                                    if(dbOp.updateGoldPreNumberDetail(sCurrentBillRowNumber, Integer.toString(iCurrentBillNumber), sCurrentBillPrefix, Integer.toString(i), sNextNumber[1], sNextNumber[0])) {

                                                                        sCurrentBillPrefix = sNextNumber[0];
                                                                        sCurrentBillRowNumber = Integer.toString(i);
                                                                        iCurrentBillNumber = Integer.parseInt(sNextNumber[1]); 
                                                                        txtBillNumber.setText(sCurrentBillPrefix + sNextNumber[1]);
                                                                        this.sLastSelectedId = txtBillNumber.getText();
                                                                        DataTable otherSettingValues = dbOp.getOtherSettingsValues("GOLD");
                                                                        captureUserImageWork(event, sBillNumber);
                                                                        saveImages(sBillNumber);
                                                                        clearAllHeader();
                                                                        txtPurity.setText(otherSettingValues.getRow(0).getColumn(1).toString());
                                                                        txtCity.setText(otherSettingValues.getRow(0).getColumn(2).toString());
                                                                        txtArea.setText(otherSettingValues.getRow(0).getColumn(5).toString());
                                                                        PopupUtil.showInfoAlert(event, "Bill number ("+ sBillNumber +") opened successfully.");                                                                     
                                                                        if(billClosingParent != null) {
                                                                            billClosingParent.dialog.close();
                                                                        }                                            
                                                                    }
                                                                    break;
                                                                }

                                                                if(i == 5) {

                                                                    if(dbOp.updateGoldPreNumberDetail(sCurrentBillRowNumber, Integer.toString(iCurrentBillNumber), sCurrentBillPrefix, Integer.toString(i), "0", "")) {

                                                                        sCurrentBillPrefix = "";
                                                                        sCurrentBillRowNumber = Integer.toString(i);
                                                                        iCurrentBillNumber = 0;
                                                                        clearAllHeader();
                                                                        DataTable otherSettingValues = dbOp.getOtherSettingsValues("GOLD");
                                                                        txtPurity.setText(otherSettingValues.getRow(0).getColumn(1).toString());
                                                                        txtCity.setText(otherSettingValues.getRow(0).getColumn(2).toString());
                                                                        txtArea.setText(otherSettingValues.getRow(0).getColumn(5).toString());
                                                                        txtBillNumber.setText("");
                                                                        this.sLastSelectedId = txtBillNumber.getText();
                                                                        captureUserImageWork(event, sBillNumber);
                                                                        saveImages(sBillNumber);
                                                                        PopupUtil.showInfoAlert(event, "Bill opened successfully.");   
                                                                        PopupUtil.showErrorAlert(event, "Next bill number does not exists. Please change the number generation setting in company module screen.");
                                                                    }
                                                                }
                                                            }
                                                        }                                                                                                        
                                                        txtCustomerName.getEditor().requestFocus();
                                                        txtCustomerName.getEditor().positionCaret(txtCustomerName.getEditor().getText().length());   
                                                    } else {
                                                        captureUserImageWork(event, sBillNumber);
                                                        saveImages(sBillNumber);
                                                        clearAllHeader();
                                                        DataTable otherSettingValues = dbOp.getOtherSettingsValues("GOLD");
                                                        txtPurity.setText(otherSettingValues.getRow(0).getColumn(1).toString());
                                                        if(nodeCustomerName.getChildren().contains(txtCustomerName)) {
                                                            txtCity.setText(otherSettingValues.getRow(0).getColumn(2).toString());
                                                            txtArea.setText(otherSettingValues.getRow(0).getColumn(5).toString());
                                                        }
                                                        PopupUtil.showInfoAlert(event, "Bill number ("+ sBillNumber +") opened successfully.");                                              
                                                        if(billClosingParent != null) {
                                                            billClosingParent.dialog.close();
                                                        }
                                                        if(txtBillNumber.isEditable()) {
                                                            txtBillNumber.requestFocus();
                                                            txtBillNumber.positionCaret(txtBillNumber.getText().length());
                                                        } else {
                                                            txtCustomerName.getEditor().requestFocus();
                                                            txtCustomerName.getEditor().positionCaret(txtCustomerName.getEditor().getText().length());                                                    
                                                        }
                                                    }
                                                } else {
                                                    PopupUtil.showErrorAlert(event, "Problem in Saving Record.");
                                                }
                                            } else {
                                                PopupUtil.showErrorAlert(event, "Same bill number already exists. Please change the number generation setting in company module screen.");
                                            }
                                        } else {
                                            PopupUtil.showErrorAlert(event, "All mandatory fields should be filled properly.");
                                        }                                    
                                    } catch (SQLException ex) {
                                        Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (Exception ex) {
                                        Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    
                                    alItemsList.removeAll(alItemsList);
                                    
                                    if(isAutoBillOpen) {
                                        this.goldBillOpeningScreenStage.close();
                                    }
                            } else {
                                PopupUtil.showErrorAlert(event, "Sorry bill opening date cannot be greater than the first due paid date.");    
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        PopupUtil.showErrorAlert(event, "Sorry this bill opening date account was closed.");
                    }                
                } else {
                    PopupUtil.showErrorAlert(event, "Any of number should be available in bill number.");
                }            
            } else {
                PopupUtil.showErrorAlert(event, "Current bill number does not exists. Please change the number generation setting in company module screen.");
            }                        
        } else {
            PopupUtil.showErrorAlert(event, "Bill Cannot create for a blocked customer.");
        }
    }

    public boolean isValidHeaderValues(String sBillNumber, String sName, String sStreetName, 
                                        String sArea, String sCity, 
                                        String sItems, String sGrossWeight, String sPurity,  
                                        String sAmount, String sGivenAmount)
    {
        if(!sBillNumber.isEmpty() && !sName.isEmpty() && !sStreetName.isEmpty() 
                && !sArea.isEmpty() && !sCity.isEmpty() 
                && !sItems.isEmpty() && !sGrossWeight.isEmpty()
                && !sPurity.isEmpty()
                && !sAmount.isEmpty() && !sGivenAmount.isEmpty()) {
            return Double.parseDouble(sGrossWeight) > 0 && Double.parseDouble(sAmount) > 0 && Double.parseDouble(sGivenAmount) > 0;
        } else {
            return false;
        }
    }

    @FXML
    private void txtBillNumberOnAction(ActionEvent event) {
    
        if(tgOff.isSelected()) 
        {
            String sBillNumber = txtBillNumber.getText();
            sLastSelectedId = sBillNumber;
            clearAllHeader();
            try {
                HashMap<String, String> headerValues = dbOp.getAllHeaderValues(sBillNumber, "GOLD");

                if(headerValues != null)
                {
                    setAllHeaderValuesToFields(headerValues);
                    setCustomerBillsListTableVals(headerValues);                    
                    setOtherDetailsValuesToTheField(headerValues);

                    Platform.runLater(() -> {
                        try {
                            if(txtCustomerId.getText() != null && !txtCustomerId.getText().isEmpty()) {
                                setCustomerImageValuesToTheField(txtCustomerId.getText());
                            }
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });

                    if(!isRebillOperation) {
                        ivCustomerBill.setImage(CommonConstants.loadingImage);
                        ivJewelBill.setImage(CommonConstants.loadingImage);
                        ivUserBill.setImage(CommonConstants.loadingImage);

                        Platform.runLater(() -> {
                            try {
                                if(txtCustomerId.getText() != null && !txtCustomerId.getText().isEmpty()) {
                                    setCustomerImageValuesToTheField(txtCustomerId.getText());
                                }
                                setImageDetailsValuesToTheField(headerValues);
                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    }
                } else {
                    PopupUtil.showErrorAlert(event, "Sorry invalid bill number.");
                    clearAllHeader();
                    sLastSelectedId = null;
                    sRepledgeBillId = null;
                    sRebilledFrom = null;
                    sStatus = null;
                }

            } catch (SQLException ex) {
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {            
            txtCustomerName.requestFocus();
            txtCustomerName.getEditor().positionCaret(txtCustomerName.getEditor().getText().length());
            isFromBillNo = true;
        }
    }
    
    public void setOtherDetailsValuesToTheField(HashMap<String, String> headerValues) {
    
        try {
            DataTable advanceAmountDetailValues = dbOp.getAdvanceAmountTableValues(headerValues.get("BILL_NUMBER"), "GOLD");
            txtAdvanceReceiptDetailTotalAmount.setText(headerValues.get("TOTAL_ADVANCE_AMOUNT_PAID"));
            setAllDetailValuesToFieldInAdvanceReceiptTable(advanceAmountDetailValues);
                        
            txtReBilledFrom.setText(headerValues.get("REBILLED_FROM"));
            txtReBilledTo.setText(headerValues.get("REBILLED_TO"));
            txtCreatedEmpName.setText(dbOp.getUserName(headerValues.get("CREATED_USER_ID")));
            txtCreatedTime.setText(headerValues.get("CREATED_TIME"));
            
            if(!headerValues.get("REPLEDGE_BILL_ID").contains(",")) {
                HashMap<String, String> repledgeValues = dbOp.getAllHeaderValuesByRepledgeBillId(headerValues.get("REPLEDGE_BILL_ID"), "GOLD");
                if(repledgeValues != null) {
                    txtRepledgeBillId.setText(repledgeValues.get("REPLEDGE_BILL_ID"));
                    txtRepledgeName.setText(repledgeValues.get("REPLEDGE_NAME"));
                    txtRepledgeBillNumber.setText(repledgeValues.get("REPLEDGE_BILL_NUMBER"));
                    txtCompanyBillNumber.setText(repledgeValues.get("BILL_NUMBER"));
                    txtRepledgeOpenedDate.setText(repledgeValues.get("REPLEDGE_OPENING_DATE"));
                    txtRepledgeBillAmount.setText(repledgeValues.get("REPLEDGE_AMOUNT"));
                    txtRepledgeBillStatus.setText(repledgeValues.get("REPLEDGE_STATUS"));
                    lbScreenMessage.setText("NOTE: THIS BILL IS IN '"+ repledgeValues.get("REPLEDGE_NAME") +"', IN THE NUMBER '"+ repledgeValues.get("REPLEDGE_BILL_NUMBER") +"'.");
                    lbScreenMessage.setVisible(true);
                } else {
                    lbScreenMessage.setText("");
                    lbScreenMessage.setVisible(false);
                }
            } else {
                txtRepledgeBillId.setText(headerValues.get("REPLEDGE_BILL_ID"));
                StringBuilder sRepBillDetails = new StringBuilder();
                int repBillsCount = 0;
                for(String repBId : txtRepledgeBillId.getText().split(",")) {

                    HashMap<String, String> repledgeValues = dbOp.getAllHeaderValuesByRepledgeBillId(repBId, "GOLD");
                    if(repledgeValues != null) {
                        String sRepledgeBillNumber = repledgeValues.get("REPLEDGE_BILL_NUMBER");
                        String sRepledgeName = repledgeValues.get("REPLEDGE_NAME");
                        String sRepledgeDate = repledgeValues.get("REPLEDGE_OPENING_DATE");
                        double dRepledgeAmount = Double.parseDouble(repledgeValues.get("REPLEDGE_AMOUNT"));
                        String sBillNumber = repledgeValues.get("BILL_NUMBER");
                        String sDate = repledgeValues.get("OPENING_DATE");
                        double dAmount = Double.parseDouble(repledgeValues.get("AMOUNT"));
                        String sStatus = repledgeValues.get("REPLEDGE_STATUS");
                        String sRepBillId = repledgeValues.get("REPLEDGE_BILL_ID");
                        tbRepList.getItems().add(new TodaysAccountJewelRepledgeBean("", sRepledgeBillNumber, sRepledgeName, 
                                sRepledgeDate, 
                                dRepledgeAmount, sBillNumber, sDate, dAmount, sStatus, sRepBillId, ""));

                        repBillsCount++;
                        sRepBillDetails.append(Integer.toString(repBillsCount));
                        sRepBillDetails.append(".) ");
                        sRepBillDetails.append(sRepledgeName);
                        sRepBillDetails.append(" - ");
                        sRepBillDetails.append(sRepledgeBillNumber);
                        if(repBillsCount%3 == 0) {
                            sRepBillDetails.append(",\n");
                        } else {
                            sRepBillDetails.append(", ");
                        }
                    }
                }
                if(sRepBillDetails.lastIndexOf(",") >= 0) {
                    sRepBillDetails.deleteCharAt(sRepBillDetails.lastIndexOf(","));
                }
                //sRepBillDetails.deleteCharAt(sRepBillDetails.lastIndexOf("\n"));
                lbScreenMessage.setText("REBILLED-MULTIPLE: '" + sRepBillDetails.toString() + "'.");
                lbScreenMessage.setVisible(true);
            }
            sRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID");
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setImageDetailsValuesToTheField(HashMap<String, String> headerValues) throws FileNotFoundException, IOException {
           
        File billNumberFolder = new File(materialFolder, headerValues.get("BILL_NUMBER"));
        
        File custTemp = new File(billNumberFolder, CommonConstants.OPEN_CUSTOMER_IMAGE_NAME);
        if(custTemp.exists()) {                      
            try (FileInputStream fis = new FileInputStream(custTemp)){
                final Image img = new Image(fis);
                ivCustomerBill.setImage(img);
                isOpenCustomerImgAvailable = true;
            }
        } else {                      
            ivCustomerBill.setImage(CommonConstants.noImage);
        }

        File jewelTemp = new File(billNumberFolder, CommonConstants.OPEN_JEWEL_IMAGE_NAME);
        if(jewelTemp.exists()) {                      
            try (FileInputStream fis = new FileInputStream(jewelTemp)){
                final Image img = new Image(fis);
                ivJewelBill.setImage(img);
                isOpenJewelImgAvailable = true;
            }
        } else {                      
            ivJewelBill.setImage(CommonConstants.noImage);
        }

        File userTemp = new File(billNumberFolder, CommonConstants.OPEN_USER_IMAGE_NAME);
        if(userTemp.exists()) {                      
            try (FileInputStream fis = new FileInputStream(userTemp)){
                final Image img = new Image(fis);
                ivUserBill.setImage(img);
                isOpenUserImgAvailable = true;
            }
        } else {                      
            ivUserBill.setImage(CommonConstants.noImage);
        }
    }

    public void setCustomerImageValuesToTheField(String id) throws FileNotFoundException, IOException {
           
        File exactCustomerFolder = new File(customerFolder, id);
        
        File custTemp = new File(exactCustomerFolder, CommonConstants.CUSTOMER_IMAGE_NAME);
        if(custTemp.exists()) {                      
            try (FileInputStream fis = new FileInputStream(custTemp)){
                final Image img = new Image(fis);
                ivCustomerDetails.setImage(img);
            }
        } else {                      
            ivCustomerDetails.setImage(CommonConstants.noImage);
        }
    }
    
    public void setAllHeaderValuesToFields(HashMap<String, String> headerValues)
    {
        txtBillNumber.setText(headerValues.get("BILL_NUMBER"));
        dpBillOpeningDate.setValue(LocalDate.parse(headerValues.get("OPENING_DATE"), CommonConstants.DATETIMEFORMATTER));
        if(nodeCustomerName.getChildren().contains(txtCustomerName)) {
            txtCustomerName.setValue(headerValues.get("CUSTOMER_NAME"));
        } else {            
            //setCustomerNameValues();
            cbCustomerName.setValue(headerValues.get("CUSTOMER_NAME"));            
        }                
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
        //setSpouseTypeValues(false);
        cbSpouseType.setValue(headerValues.get("SPOUSE_TYPE"));
        txtSpouseName.setText(headerValues.get("SPOUSE_NAME"));      
        txtDoorNo.setText(headerValues.get("DOOR_NUMBER"));
        txtStreetName.setValue(headerValues.get("STREET"));
        txtArea.setText(headerValues.get("AREA"));
        txtCity.setText(headerValues.get("CITY"));
        txtMobileNumber.setText(headerValues.get("MOBILE_NUMBER"));
        if(headerValues.get("CUSTOMER_STATUS").equals("BLOCKED")) {
            txtCustomerStatus.setText(headerValues.get("CUSTOMER_STATUS"));
            txtCustomerStatus.setStyle("-fx-background-color: #FF5555");
        } else {
            txtCustomerStatus.setText("ACTIVE");
            txtCustomerStatus.setStyle("-fx-background-color: #55FF30");
        }                    
        txtMobileNumber2.setText(headerValues.get("mobile_number_2"));
        txtIdProof.setText(headerValues.get("cust_id_proof_type"));
        txtIdNumber.setText(headerValues.get("cust_id_proof_number"));
        txtRecomendedBy.setText(headerValues.get("refered_by_name"));
        txtCustomerId.setText(headerValues.get("customer_id"));
        txtOccupation.setText(headerValues.get("customer_occupation"));
        if(txtCustomerId.getText() != null && !txtCustomerId.getText().isEmpty()) {
            txtCustomerVerifyStatus.setText("VERIFIED");
            txtCustomerVerifyStatus.setStyle("-fx-background-color: #55FF30");
        } else {
            txtCustomerVerifyStatus.setText("NOT-VERIFIED");
            txtCustomerVerifyStatus.setStyle("-fx-background-color: #FF5555");
        }
        
        txtItems.setText(headerValues.get("ITEMS"));
        txtGrossWeight.setText(headerValues.get("GROSS_WEIGHT"));
        txtNetWeight.setText(headerValues.get("NET_WEIGHT"));
        sNewPurity = headerValues.get("PURITY");
        txtPurity.setText(headerValues.get("PURITY"));        
                
        if(headerValues.get("accepted_closing_date") != null) {
            String sAccClosingDate = headerValues.get("accepted_closing_date");
            dpBillCLosingAcceptedDate.setValue(LocalDate.parse(sAccClosingDate, CommonConstants.DATETIMEFORMATTER));

            String sAccDateOption = DateRelatedCalculations.getAcceptedClosingDuration(
                    headerValues.get("OPENING_DATE"), sAccClosingDate);
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
        
        
        txtAmount.setText(headerValues.get("AMOUNT"));
        txtInterest.setText(headerValues.get("INTEREST"));
        txtDocumentCharge.setText(headerValues.get("DOCUMENT_CHARGE"));
        txtTakenAmount.setText(headerValues.get("OPEN_TAKEN_AMOUNT"));
        txtToGiveAmount.setText(headerValues.get("TOGIVE_AMOUNT"));
        txtGivenAmount.setText(headerValues.get("GIVEN_AMOUNT"));
        sStatus = headerValues.get("STATUS");
        cbStatus.getItems().removeAll(cbStatus.getItems());
        sRepledgeBillId = headerValues.get("REPLEDGE_BILL_ID");
        if(sRepledgeBillId.contains(CommonConstants.REP_BILL_ID_PREFIX)) {
            cbStatus.getItems().addAll("LOCKED");
            try {
                HashMap<String, String> repledgeValues = dbOp.getAllHeaderValuesByRepledgeBillId(sRepledgeBillId, "GOLD");
                if(repledgeValues != null) {
                    lbScreenMessage.setText("NOTE: THIS BILL IS IN '"+ repledgeValues.get("REPLEDGE_NAME") +"', IN THE NUMBER '"+ repledgeValues.get("REPLEDGE_BILL_NUMBER") +"'.");
                    lbScreenMessage.setVisible(true);
                } else {
                    lbScreenMessage.setText("");
                    lbScreenMessage.setVisible(false);
                }                
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            if("CANCELED".equals(sStatus)) {
                cbStatus.getItems().addAll("CANCELED");
            } else {
                if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(headerValues.get("OPENING_DATE"), 
                        CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE)) {
                    cbStatus.getItems().addAll("OPENED", "LOCKED");
                } else {
                    cbStatus.getItems().addAll("OPENED", "LOCKED", "CANCELED");
                }
            }
        }
        cbStatus.setValue(sStatus);
        setPhysicalLocation(headerValues.get("physical_location"));
        txtNote.setText(headerValues.get("NOTE"));   
        txtNomineeName.setText(headerValues.get("NOMINEE_NAME")); 
    }
    
    
    @FXML
    private void btUpdateBillClicked(ActionEvent event) {
        
        if(sLastSelectedId != null) {
            
            txtBillNumber.setText(sLastSelectedId);
            String sBillNumber = txtBillNumber.getText().toUpperCase();            
            String sBillOpeningDate = CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue());
            
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sBillOpeningDate))
            {
                try {
                    
                    String sFirstDuePaidDate = dbOp.getFirstDuePaidDate(sBillNumber, "GOLD");
                    if(sFirstDuePaidDate == null || DateRelatedCalculations.isFirstDateIsLesserOrEqualToSecondDate(sBillOpeningDate, sFirstDuePaidDate))
                    {                        
                        captureUserImageWork(event, sBillNumber);
                        saveImages(sBillNumber);                        
                        String sGender = ((RadioButton) rgGenderGroup.getSelectedToggle()).getText().toUpperCase();
                        String sName = null;
                        if(nodeCustomerName.getChildren().contains(txtCustomerName)) {
                            sName = txtCustomerName.getValue().toUpperCase();
                        } else {
                            sName = cbCustomerName.getValue().toUpperCase();
                        } 
                        String sSpouseType = cbSpouseType.getValue().toUpperCase();
                        String sSpouseName = txtSpouseName.getText().toUpperCase();
                        String sDoorNo = txtDoorNo.getText().toUpperCase();
                        String sStreetName = txtStreetName.getValue().toUpperCase();
                        String sArea = txtArea.getText().toUpperCase();
                        String sCity = txtCity.getText().toUpperCase();
                        String sMobileNumber = txtMobileNumber.getText().toUpperCase();
                        String sItems = txtItems.getText().toUpperCase();
                        String sGrossWeight = txtGrossWeight.getText().toUpperCase();
                        String sNetWeight = txtNetWeight.getText().toUpperCase();
                        String sPurity = txtPurity.getText().toUpperCase();                        
                        String sStatus = cbStatus.getValue().toUpperCase();
                        String sNote = txtNote.getText().toUpperCase();        
                        String sAmount = txtAmount.getText().toUpperCase();
                        String sInterest = txtInterest.getText().toUpperCase();
                        String sDocumentCharge = txtDocumentCharge.getText().toUpperCase();
                        String sTakenAmount = txtTakenAmount.getText().toUpperCase();
                        String sToGiveAmount = txtToGiveAmount.getText().toUpperCase();
                        String sGivenAmount = txtGivenAmount.getText().toUpperCase();
                        String sAcceptedDate = CommonConstants.DATETIMEFORMATTER.format(dpBillCLosingAcceptedDate.getValue());
                        String sNomineeName = txtNomineeName.getText() != null 
                            ? txtNomineeName.getText().toUpperCase() 
                            : null;

                        String sMobileNumber2 = txtMobileNumber2.getText();
                        String sIdProof = txtIdProof.getText();
                        String sIdNumber = txtIdNumber.getText();
                        String sRecommendedBy = txtRecomendedBy.getText();
                        String sCustomerId = txtCustomerId.getText();
                        String sOccupation = txtOccupation.getText();
                        String sPhysicalLocation = txtPhysicalLocation.getText().toUpperCase();
                        
                        double dAmount = Double.parseDouble(!("".equals(sAmount))? sAmount : "0");
                        double dInterest = Double.parseDouble(!("".equals(sInterest))? sInterest : "0");
                        double dDocumentCharge = Double.parseDouble(!("".equals(sDocumentCharge))? sDocumentCharge : "0");
                        double dTakenAmount = Double.parseDouble(!("".equals(sTakenAmount))? sTakenAmount : "0");
                        double dToGiveAmount = Double.parseDouble(!("".equals(sToGiveAmount))? sToGiveAmount : "0");
                        double dGivenAmount = Double.parseDouble(!("".equals(sGivenAmount))? sGivenAmount : "0");
                        
                        /*BufferedImage cbi = SwingFXUtils.fromFXImage(ivCustomerBill.getImage(), null);
                        BufferedImage jbi = SwingFXUtils.fromFXImage(ivJewelBill.getImage(), null);

                        ImageIO.write(cbi, "PNG", CommonConstants.custTemp);
                        cbi.flush();

                        ImageIO.write(jbi, "PNG", CommonConstants.jewelTemp);
                        jbi.flush();*/
                        
                        try {
                            if(!dbOp.isvalidBillNumberToSave(sBillNumber, "GOLD"))
                            {
                                if(dbOp.isNotCanceledBill(sBillNumber, "GOLD")) 
                                {
                                    if(dbOp.updateRecord(sBillNumber, sBillOpeningDate, sName, sGender, 
                                            sSpouseType, sSpouseName, sDoorNo, sStreetName, sArea, sCity, sMobileNumber, sStatus, sNote,
                                            sItems, sGrossWeight, sNetWeight, sPurity, 
                                            dAmount, dInterest, dDocumentCharge, dTakenAmount, dToGiveAmount, 
                                            dGivenAmount, "GOLD", sAcceptedDate, sNomineeName, sMobileNumber2, 
                                            sIdProof, sIdNumber, sRecommendedBy, 
                                            sCustomerId, sOccupation, sPhysicalLocation)) 
                                    {                
                                        if("CANCELED".equals(sStatus)) {
                                            cbStatus.getItems().removeAll(cbStatus.getItems());
                                            cbStatus.getItems().addAll("CANCELED");
                                            cbStatus.setValue(sStatus);                                            
                                        }
                                        PopupUtil.showInfoAlert(event, "Bill number ("+ sBillNumber +") updated successfully.");
                                    }
                                } else {
                                    PopupUtil.showInfoAlert(event, "Canceled Bill ("+ sBillNumber +") cannot be updated.");
                                }
                            } else {
                                PopupUtil.showErrorAlert(event, "Sorry invalid bill number.");
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (Exception ex) {
                            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        PopupUtil.showErrorAlert(event, "Sorry bill opening date cannot be greater than the first due paid date.");    
                    }
                } catch (SQLException | IOException ex) {
                    Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                PopupUtil.showErrorAlert(event, "Sorry this bill opening date account was closed.");
            }
        } else {
            PopupUtil.showErrorAlert(event, "No any bill number selected properly.");
        }
    }

    @FXML
    private void showAllRecordsClicked(ActionEvent event) {
                
        try {
            DataTable allDetailValues = dbOp.getAllDetailsValues("GOLD", null);
            setAllDetailValuesToField(allDetailValues);
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public void setAllDetailValuesToField(DataTable allDetailValues) {

        tbAllDetails.getItems().removeAll(tbAllDetails.getItems());
        for(int i=0; i<allDetailValues.getRowCount(); i++) {            
            String sBillNumber = allDetailValues.getRow(i).getColumn(0).toString();
            String sDate = allDetailValues.getRow(i).getColumn(1).toString();
            String sAmount = allDetailValues.getRow(i).getColumn(2).toString();
            String sName = allDetailValues.getRow(i).getColumn(3).toString();
            String sGender = allDetailValues.getRow(i).getColumn(4).toString();
            String sSpouseType = allDetailValues.getRow(i).getColumn(5).toString();
            String sSpouseName = allDetailValues.getRow(i).getColumn(6).toString();
            String sStreet = allDetailValues.getRow(i).getColumn(7).toString();
            String sArea = allDetailValues.getRow(i).getColumn(8).toString();
            String sMobileNumber = allDetailValues.getRow(i).getColumn(9).toString();
            String sItems = allDetailValues.getRow(i).getColumn(10).toString();
            String sGrossWeight = allDetailValues.getRow(i).getColumn(11).toString();
            String sNetWeight = allDetailValues.getRow(i).getColumn(12).toString();
            String sPurity = allDetailValues.getRow(i).getColumn(13).toString();
            String sStatus = allDetailValues.getRow(i).getColumn(14).toString();
            String sNote = allDetailValues.getRow(i).getColumn(15).toString();
            String sRepledgeBillId = allDetailValues.getRow(i).getColumn(16).toString();            
            tbAllDetails.getItems().add(new AllDetailsBean(sBillNumber, sDate, Double.parseDouble(sAmount), sName, sGender, sSpouseType, sSpouseName, sStreet, sArea, sMobileNumber, sItems, sGrossWeight, sNetWeight, sPurity, sStatus, sNote, sRepledgeBillId));
        }        
    }    
    
    @FXML
    private void cbAllDetailsFilterOnAction(ActionEvent event) {
        
        int sIndex = cbAllDetailsFilter.getSelectionModel().getSelectedIndex();
        
        if(sIndex >= 0) 
        {
            String sFilterName = cbAllDetailsFilter.getItems().get(sIndex);
            if(null != sFilterName) switch (sFilterName) {
                case "DATE":
                    nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    nodeAddToFilter.getChildren().remove(cbAddToFilter);
                    if(!nodeAddToFilter.getChildren().contains(dpAddToFilter))
                        nodeAddToFilter.getChildren().add(1, dpAddToFilter);
                    break;
                case "GENDER":
                    nodeAddToFilter.getChildren().remove(dpAddToFilter);
                    nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    if(!nodeAddToFilter.getChildren().contains(cbAddToFilter))
                        nodeAddToFilter.getChildren().add(1, cbAddToFilter);
                    cbAddToFilter.getItems().removeAll(cbAddToFilter.getItems());
                    cbAddToFilter.getItems().addAll("MALE", "FEMALE", "OTHER");
                    cbAddToFilter.getSelectionModel().select(0);
                    break;
                case "SPOUSE TYPE":
                    nodeAddToFilter.getChildren().remove(dpAddToFilter);
                    nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    if(!nodeAddToFilter.getChildren().contains(cbAddToFilter))
                        nodeAddToFilter.getChildren().add(1, cbAddToFilter);
                    cbAddToFilter.getItems().removeAll(cbAddToFilter.getItems());
                    cbAddToFilter.getItems().addAll("S/O", "F/O", "H/O", "D/O", "M/O", "W/O");
                    cbAddToFilter.getSelectionModel().select(0);
                    break;
                case "ITEMS":
                    nodeAddToFilter.getChildren().remove(dpAddToFilter);
                    nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    if(!nodeAddToFilter.getChildren().contains(cbAddToFilter))
                        nodeAddToFilter.getChildren().add(1, cbAddToFilter);
                    cbAddToFilter.getItems().removeAll(cbAddToFilter.getItems());
                    DataTable jewelItemValues;
                    try {                
                        jewelItemValues = dbOp.getAllJewelItems("GOLD");
                        for(int i=0; i<jewelItemValues.getRowCount(); i++) {
                            cbAddToFilter.getItems().add(jewelItemValues.getRow(i).getColumn(0).toString());
                        }
                        cbAddToFilter.getSelectionModel().select(0);
                    } catch (SQLException ex) {
                        Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                    }   break;
                case "STATUS":
                    nodeAddToFilter.getChildren().remove(dpAddToFilter);
                    nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    if(!nodeAddToFilter.getChildren().contains(cbAddToFilter))
                        nodeAddToFilter.getChildren().add(1, cbAddToFilter);
                    cbAddToFilter.getItems().removeAll(cbAddToFilter.getItems());
                    cbAddToFilter.getItems().addAll("OPENED", "LOCKED", "CANCELED");
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
                    case "OPENING_DATE":
                    sFilterScript += "AND TO_CHAR(" + alFilterDBColumnName.get(i) + ", 'dd-MM-YYYY') ::TEXT LIKE ? ";
                    break;
                case "GENDER":
                    sFilterScript += "AND " + alFilterDBColumnName.get(i) + " = ?::GENDER_TYPE ";
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
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }           
    }

    @FXML
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {
        
        if(!hSaveModeButtons.isDisable()) {
            int index = tbAllDetails.getSelectionModel().getSelectedIndex();

            if (event.getClickCount() == 2 && (index >= 0) ) {
                viewOpenedBill(tbAllDetails.getItems().get(index).getSBillNumber());
            }        
        }
    }    

    public void viewBill(String sBillNumber) {
        viewBillWork(sBillNumber, true);
    }

    public void viewBill(String sBillNumber, boolean onlyForView) {
        viewBillWork(sBillNumber, onlyForView);
    }
    
    private void viewBillWork(String sBillNumber, boolean onlyForView) {
        tgOff.setSelected(true);
        saveModeOFF(null);
        txtBillNumber.setText(sBillNumber);
        txtBillNumberOnAction(null);
        btUpdateBill.setDisable(onlyForView);
        hSaveModeButtons.setDisable(onlyForView);
        dpBillOpeningDate.setMouseTransparent(onlyForView);
        dpBillOpeningDate.setFocusTraversable(!onlyForView);            
        tpScreen.getSelectionModel().select(tabMainScreen);            
    }
    
    public void viewOpenedBill(String sBillNumber) {
    
        tgOff.setSelected(true);
        saveModeOFF(null);
        txtBillNumber.setText(sBillNumber);
        txtBillNumberOnAction(null);
        tpScreen.getSelectionModel().select(tabMainScreen);
    }
    
    public void autoBillOpen(String grossWeight, String purity, String amount, Image customerImage, Image jewelImage) {

        isAutoBillOpen = true;
        
        tgOn.setSelected(true);
        saveModeON(null);
        
        if(grossWeight != null) {
            txtGrossWeight.setText(grossWeight);
        }
        
        if(purity != null) {
            txtPurity.setText(purity);
        }
        
        if(amount != null) {
            txtAmount.setText(amount);

            txtRatePerGm.setEditable(false);
            txtRatePerGm.setMouseTransparent(true);
            txtRatePerGm.setFocusTraversable(false);                                    
            
            txtAmount.setEditable(false);
            txtAmount.setMouseTransparent(true);
            txtAmount.setFocusTraversable(false);                                    
            
            txtGivenAmount.setEditable(false);
            txtGivenAmount.setMouseTransparent(true);
            txtGivenAmount.setFocusTraversable(false);                                    
        }
        
        if(customerImage != null) {                            
            ivCustomerBill.setImage(customerImage);
            isOpenCustomerImgAvailable = true;
        }

        if(jewelImage != null) {                            
            ivJewelBill.setImage(jewelImage);
            isOpenJewelImgAvailable = true;
        }
        
    }
    
    public void reBill(String sBillNumber, String sOpeningDate, 
            String sRepledgeBillId, String sClosedStatus,
            final Image customerImage, final Image jeweImage, final Image usrImage,
            GoldBillClosingController billClosingParent) {
        
        /*Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.setTitle("Gold Bill Opening (Rebill from " + sBillNumber + " )");*/
        //setBillClosingAcceptedValues(true);
        this.isRebillOperation = true;
        viewOpenedBill(sBillNumber);
        doAllSaveModeONWork();        
        this.sRepledgeBillId = sRepledgeBillId;
        this.sRebilledFrom = sBillNumber;
        this.billClosingParent = billClosingParent;        
        cbStatus.getItems().removeAll(cbStatus.getItems());
        if(this.sRepledgeBillId != null 
                && this.sRepledgeBillId.contains(CommonConstants.REP_BILL_ID_PREFIX)
                && sClosedStatus.equals("REBILLED")) {
            cbStatus.getItems().addAll("LOCKED");
            cbStatus.setValue("LOCKED");
        } else {
            cbStatus.getItems().addAll("OPENED", "LOCKED");
            cbStatus.setValue("OPENED");
            setPhysicalLocation(CommonConstants.CASH_DRAWER);
        }
        hSaveModeButtons.setDisable(true);        
        this.billClosingParent.setRebilledTo(txtBillNumber.getText());
        if(sRebilledNewAmt != null) {
            txtAmount.setText(sRebilledNewAmt);
        }
        if(sNewWeight != null && !sNewWeight.isEmpty()) {
            txtGrossWeight.setText(sNewWeight);
        }
        if(sRebilledDate != null) {
            dpBillOpeningDate.setValue(LocalDate.parse(sRebilledDate, CommonConstants.DATETIMEFORMATTER));
        }
        if(sNewPurity != null) {
            txtPurity.setText(sNewPurity);
        }
        setAmountRelatedText(Double.parseDouble(txtAmount.getText()), "GOLD");        
        dpBillOpeningDate.setValue(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
        
        if(customerImage != null) {
            isOpenCustomerImgAvailable = true;
            ivCustomerBill.setImage(customerImage);
        }
        
        if(jeweImage != null) {
            isOpenJewelImgAvailable = true;
            ivJewelBill.setImage(jeweImage);
        }

        if(usrImage != null) {
            //ivUserBill.setImage(usrImage);
        }      
        
        if(!sClosedStatus.equals("REBILLED")) {
            txtItems.setText("");
        }        
        sLastSelectedId = txtBillNumber.getText();
    }

    public void reBillMultiple(String sBillNumbers, String sOpeningDate, 
            String sRepledgeBillIds, String sItems,  
            String sGrWt, String amtToGiveOrGetTxt, String amtToGiveOrGet,
            Image customerImage, Image jeweImage,
            GoldBillClosingController billClosingParent) {
        
        /*Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.setTitle("Gold Bill Opening (Rebill from " + sBillNumber + " )");*/
        //setBillClosingAcceptedValues(true);        
        this.isRebillOperation = true;
        this.isMultiRebillOperation = true;
        doAllSaveModeONWork();        
        this.sRepledgeBillId = sRepledgeBillIds;
        this.sRebilledFrom = sBillNumbers;
        this.billClosingParent = billClosingParent;
        this.sMultiRebillAmtTxt = amtToGiveOrGetTxt;
        this.sMultiRebillAmt = amtToGiveOrGet;
        cbStatus.getItems().removeAll(cbStatus.getItems());
        if(this.sRepledgeBillId != null && this.sRepledgeBillId.contains(CommonConstants.REP_BILL_ID_PREFIX)) {
            cbStatus.getItems().addAll("LOCKED");
            cbStatus.setValue("LOCKED");
        } else {
            cbStatus.getItems().addAll("OPENED", "LOCKED");
            cbStatus.setValue("OPENED");
        }
        hSaveModeButtons.setDisable(true);        
        this.billClosingParent.setRebilledTo(txtBillNumber.getText());
        if(sRebilledNewAmt != null) {
            txtAmount.setText(sRebilledNewAmt);
        }
        if(sRebilledDate != null) {
            dpBillOpeningDate.setValue(LocalDate.parse(sRebilledDate, CommonConstants.DATETIMEFORMATTER));
        }
        if(sNewPurity != null) {
            txtPurity.setText(sNewPurity);
        }
        
        txtRepledgeBillId.setText(sRepledgeBillIds);
        txtItems.setText(sItems);
        txtGrossWeight.setText(sGrWt);
        setAmountRelatedText(Double.parseDouble(txtAmount.getText()), "GOLD");
        setPhysicalLocation(null);
        dpBillOpeningDate.setValue(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
        
        if(customerImage != null) {
            isOpenCustomerImgAvailable = true;
            ivCustomerBill.setImage(customerImage);
        }
        
        if(jeweImage != null) {
            isOpenJewelImgAvailable = true;
            ivJewelBill.setImage(jeweImage);
        }
        sLastSelectedId = txtBillNumber.getText();
    }
    
    public String getDBColumnNameFor(String filterName) 
    {
    
        switch (filterName)
        {        
            case "BILL NO":
                return "BILL_NUMBER";
            case "DATE":
                return "OPENING_DATE";
            case "AMOUNT":
                return "AMOUNT";
            case "NAME":
                return "CUSTOMER_NAME";
            case "GENDER":
                return "GENDER";
            case "SPOUSE TYPE":
                return "SPOUSE_TYPE";
            case "SPOUSE NAME":
                return "SPOUSE_NAME";
            case "STREET":
                return "STREET";
            case "AREA":
                return "AREA";
            case "MOBILE NO":
                return "MOBILE_NUMBER";
            case "ITEMS":
                return "ITEMS";
            case "GROSS WEIGHT":
                return "GROSS_WEIGHT";
            case "NET WEIGHT":
                return "NET_WEIGHT";
            case "PURITY":
                return "PURITY";
            case "STATUS":
                return "STATUS";
            case "NOTE":
                return "NOTE";
            default:
                return null;
        }
    }
    
    public void clearAllHeader()
    {
        if(nodeCustomerName.getChildren().contains(txtCustomerName)) {
            txtCustomerName.setValue("");
            txtSpouseName.setText("");            
            txtDoorNo.setText("");
            txtStreetName.setValue("");
            txtArea.setText("");
            txtCity.setText("");
            txtMobileNumber.setText("");
            txtMobileNumber2.setText("");
            txtIdProof.setText("");
            txtIdNumber.setText("");
            txtRecomendedBy.setText("");        
            txtCustomerVerifyStatus.setText("");
            txtCustomerId.setText("");               
            txtOccupation.setText("");
            txtNomineeName.setText("");
        } else {
            cbCustomerNameOnAction(null);
        }
        txtNumberOfItems.setText("");
        txtItems.setText("");
        txtGrossWeight.setText("");
        txtNetWeight.setText("");
        txtPurity.setText("");
        txtRatePerGm.setText("");
        txtTotalValue.setText("");
        txtNote.setText("");        
        txtAmount.setText("");
        txtInterest.setText("");
        txtDocumentCharge.setText("");
        txtTakenAmount.setText("");
        txtToGiveAmount.setText("");
        txtGivenAmount.setText("");       

        txtAdvanceReceiptDetailTotalAmount.setText("");
        tbAdvanceReceiptDetails.getItems().removeAll(tbAdvanceReceiptDetails.getItems());
        txtReBilledFrom.setText("");
        txtReBilledTo.setText("");
        txtCreatedEmpName.setText("");
        txtCreatedTime.setText("");
        txtRepledgeBillId.setText("");
        txtRepledgeName.setText("");
        txtRepledgeBillNumber.setText("");
        txtCompanyBillNumber.setText("");
        txtRepledgeOpenedDate.setText("");
        txtRepledgeBillAmount.setText("");
        txtRepledgeBillStatus.setText("");
        
        lbScreenMessage.setText("");
        lbScreenMessage.setVisible(false);
        tbCutomerBillsList.getItems().removeAll(tbCutomerBillsList.getItems());
                
        ivCustomerDetails.setImage(CommonConstants.noImage);
        ivCustomerBill.setImage(CommonConstants.noImage);
        ivJewelBill.setImage(CommonConstants.noImage);
        ivUserBill.setImage(CommonConstants.noImage);    
        
        isOpenCustomerImgAvailable = false;        
        isOpenJewelImgAvailable = false;        
        isOpenUserImgAvailable = false;        
    }    

    @FXML
    private void btOpenInBillClosingClicked(ActionEvent event) {
        
        int index = tbAllDetails.getSelectionModel().getSelectedIndex();
        
        if(index >= 0) 
        {
            if(!tbAllDetails.getItems().get(index).getSStatus().equals("CANCELED")) {
                Stage dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);        

                FXMLLoader loader = new FXMLLoader(getClass().getResource(goldBillClosingScreen));
                Parent root = null;
                try {            
                    root = (Parent) loader.load();            
                } catch (IOException ex) {
                    Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                }

                GoldBillClosingController gon = (GoldBillClosingController) loader.getController();
                gon.closeBill(tbAllDetails.getItems().get(index).getSBillNumber(), false);

                dialog.setTitle("Gold Bill Closing");      
                dialog.setX(CommonConstants.SCREEN_X);
                dialog.setY(CommonConstants.SCREEN_Y);
                dialog.setWidth(CommonConstants.SCREEN_WIDTH);
                dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
                dialog.setResizable(false);
                Scene scene = new Scene(root);        
                dialog.setScene(scene);
                dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
                dialog.showAndWait();        
            } else {
                PopupUtil.showInfoAlert(event, "Canceled bill cannot be viewed in bill closing screen.");
            }
        } else {
            PopupUtil.showInfoAlert(event, "Any of a row in table should be selected.");
        }  
    }

    @FXML
    private void txtGrossWeightOnAction(ActionEvent event) {
        String sBillOpeningDate = CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue());
        txtNetWeight.requestFocus();
    }

    @FXML
    private void txtNetWeightOnAction(ActionEvent event) {
        txtPurity.requestFocus();
        txtPurity.positionCaret(txtPurity.getText().length());
    }

    @FXML
    private void txtPurityOnAction(ActionEvent event) {
        txtRatePerGm.requestFocus();
        txtRatePerGm.positionCaret(txtRatePerGm.getText().length());
    }

    @FXML
    private void dpBillOpeningDateOnAction(ActionEvent event) {
        
        if(tgOff.isSelected()) {
            cbStatus.getItems().removeAll(cbStatus.getItems());
            if(sRepledgeBillId != null && sRepledgeBillId.contains(CommonConstants.REP_BILL_ID_PREFIX)) {
                cbStatus.getItems().addAll("LOCKED");
            } else {
                if("CANCELED".equals(sStatus)) {
                    cbStatus.getItems().addAll("CANCELED");
                } else {
                    try {
                        String sBillOpeningDate = dbOp.getBillOpenedDate(sLastSelectedId, "GOLD");
                        if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(sBillOpeningDate, CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE)) {
                            cbStatus.getItems().addAll("OPENED", "LOCKED");
                        } else {
                            cbStatus.getItems().addAll("OPENED", "LOCKED", "CANCELED");
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            cbStatus.setValue(sStatus);
        } else {
            String sAmount = txtAmount.getText().isEmpty() ? "0" : txtAmount.getText();
            setAmountRelatedText(Double.parseDouble(sAmount), "GOLD");  
        }
        setBillClosingAcceptedValues(true);
        setPhysicalLocation(null);
        try {
            openDateRestriction();
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void txtCustomerNameOnAction(ActionEvent event) {
               
    }

    @FXML
    private void txtSpouseNameOnAction(ActionEvent event) {
        txtDoorNo.requestFocus();
        txtDoorNo.positionCaret(txtDoorNo.getText().length());        
    }

    @FXML
    private void txtDoorNoOnAction(ActionEvent event) {
        txtStreetName.getEditor().requestFocus();
        if(!txtStreetName.getEditor().getText().isEmpty()) {
            txtStreetName.getEditor().positionCaret(txtStreetName.getValue().length());        
        }
        isFromDoorNo = true;
    }

    @FXML
    private void txtStreetNameOnAction(ActionEvent event) {
    }

    @FXML
    private void txtAreaOnAction(ActionEvent event) {
        
        txtCity.requestFocus();
        txtCity.positionCaret(txtCity.getText().length());        
    }

    @FXML
    private void txtCityOnAction(ActionEvent event) {
        
        txtMobileNumber.requestFocus();
        txtMobileNumber.positionCaret(txtMobileNumber.getText().length());        
    }

    @FXML
    private void txtMobileNumberOnAction(ActionEvent event) {

        txtNomineeName.requestFocus();
        txtNomineeName.positionCaret(txtMobileNumber.getText().length());                
    }

    @FXML
    private void cbCustomerNameOnAction(ActionEvent event) {
        Platform.runLater(()->{
        int sIndex = cbCustomerName.getSelectionModel().getSelectedIndex();
        
        if(sIndex >=0) {
            cbCustomerName.setValue(customerNames.getRow(sIndex).getColumn(1).toString());
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
            cbSpouseType.setValue(customerNames.getRow(sIndex).getColumn(3).toString());
            txtSpouseName.setText(customerNames.getRow(sIndex).getColumn(4).toString());
            txtDoorNo.setText(customerNames.getRow(sIndex).getColumn(5).toString());
            txtStreetName.setValue(customerNames.getRow(sIndex).getColumn(6).toString());
            txtArea.setText(customerNames.getRow(sIndex).getColumn(7).toString());
            txtCity.setText(customerNames.getRow(sIndex).getColumn(8).toString());
            txtMobileNumber.setText(customerNames.getRow(sIndex).getColumn(9).toString());
            if(customerNames.getRow(sIndex).getColumn(10).toString().equals("BLOCKED")) {
                txtCustomerStatus.setText(customerNames.getRow(sIndex).getColumn(10).toString());
                txtCustomerStatus.setStyle("-fx-background-color: #FF5555");
            } else {
                txtCustomerStatus.setText("ACTIVE");
                txtCustomerStatus.setStyle("-fx-background-color: #55FF30");
            }                    
            setCustomerBillsListTableVals(customerNames, sIndex);
        }
        });
    }
    
    public void setCustomerBillsListTableVals(DataTable headerValues, int index) {
    
        try {
            String[] sNoticeValues = dbOp.getNoticeValues();
            DataTable allDetailValues = dbOp.getAllCustomerAllBillValues(headerValues, index);
            tbCutomerBillsList.getItems().removeAll(tbCutomerBillsList.getItems());
            for(int i=0; i<allDetailValues.getRowCount(); i++) {
                String sBillNumber = allDetailValues.getRow(i).getColumn(0).toString();
                String sDate = allDetailValues.getRow(i).getColumn(1).toString();
                String sItems = allDetailValues.getRow(i).getColumn(2).toString();
                String sAmount = allDetailValues.getRow(i).getColumn(3).toString();
                String sMaterialType = allDetailValues.getRow(i).getColumn(4).toString();
                String sRepBillId = allDetailValues.getRow(i).getColumn(5).toString();
                String sGrossWt = allDetailValues.getRow(i).getColumn(6).toString();
                String sCompId = allDetailValues.getRow(i).getColumn(7).toString();
                tbCutomerBillsList.getItems().add(new CustomerBillsListBean(sRepBillId, sMaterialType, 
                        sBillNumber, sDate, sItems, sGrossWt, sAmount, sNoticeValues[0], sCompId));                
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setCustomerBillsListTableVals(HashMap<String, String> headerValues) {
    
        try {
            String[] sNoticeValues = dbOp.getNoticeValues();
            DataTable allDetailValues = dbOp.getAllCustomerAllBillValues(headerValues);
            tbCutomerBillsList.getItems().removeAll(tbCutomerBillsList.getItems());
            for(int i=0; i<allDetailValues.getRowCount(); i++) {
                String sBillNumber = allDetailValues.getRow(i).getColumn(0).toString();
                String sDate = allDetailValues.getRow(i).getColumn(1).toString();
                String sItems = allDetailValues.getRow(i).getColumn(2).toString();
                String sAmount = allDetailValues.getRow(i).getColumn(3).toString();
                String sMaterialType = allDetailValues.getRow(i).getColumn(4).toString();
                String sRepBillId = allDetailValues.getRow(i).getColumn(5).toString();
                String sGrossWt = allDetailValues.getRow(i).getColumn(6).toString();
                String sCompId = allDetailValues.getRow(i).getColumn(7).toString();
                tbCutomerBillsList.getItems().add(new CustomerBillsListBean(sRepBillId, sMaterialType, 
                        sBillNumber, sDate, sItems, sGrossWt, sAmount, sNoticeValues[0], sCompId));                
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setWeightRelatedText(String sGrossWeight) {
        
        try {
            double dGrossWeight = 0;
            float fPurity = 0f;
            if(sGrossWeight != null) {
                dGrossWeight = sGrossWeight.isEmpty() ? 0f : Float.parseFloat(sGrossWeight);
                fPurity = txtPurity.getText().isEmpty() ? 0f : Float.parseFloat(txtPurity.getText());
            }
            double dNetWt = 0;
            if(fPurity < 88) {
                dNetWt = (fPurity * dGrossWeight)/100;
            } else {
                double dReductionWt = Double.parseDouble(dbOp.getReductionWt("GOLD"));
                dNetWt = dGrossWeight - (dGrossWeight * dReductionWt);
            }
            
            double dAmount = Double.parseDouble(txtAmount.getText().isEmpty() ? "0" : txtAmount.getText()); 
            double dSuggestionAmt = dNetWt * Double.parseDouble(dbOp.getCompanyRate("GOLD"));
            double dRatePerGm = dAmount / dGrossWeight;
            double propVal = (((Double.parseDouble(dbOp.getTodaysRate("GOLD")) - 300) * fPurity)/100) * dGrossWeight;
                        
            txtNetWeight.setText(String.format("%.3f", dNetWt));
            txtSuggestionAmt.setText(String.valueOf(Math.round(dSuggestionAmt)));
            txtRatePerGm.setText(String.valueOf(Math.round(dRatePerGm)));
            txtTotalValue.setText(String.valueOf(Math.round(propVal)));
                        
            if(dAmount > 0 && dSuggestionAmt > 0 && dSuggestionAmt >= dAmount) {
                txtSuggestionAmt.setStyle("-fx-background-color: #55FF30");
            } else if(dAmount > 0 && dSuggestionAmt > 0 && dSuggestionAmt < dAmount) {
                txtSuggestionAmt.setStyle("-fx-background-color: #FF5555");
            } else {
                txtSuggestionAmt.setStyle("-fx-background-color: #FFFFFF");
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setRatePerGramRelatedText(String sRatePerGm) {
        
        double dRatePerGm = 0;
        double dGrsWt = 0f;
        if(sRatePerGm != null) {
            dRatePerGm = sRatePerGm.isEmpty() ? 0 : Double.parseDouble(sRatePerGm);
            dGrsWt = txtGrossWeight.getText().isEmpty() ? 0 : Double.parseDouble(txtGrossWeight.getText());
        }
        double dAmount = dRatePerGm * dGrsWt;
        if(!isAutoBillOpen) {
            txtAmount.setText(String.valueOf(Math.round(dAmount)));
        }
    }
    
    private void setPurityWeightRelatedText(String sPurity) {
        
        try {
            double dGrossWeight = 0;
            float fPurity = 0f;
            
            if(sPurity != null) {
                dGrossWeight = txtGrossWeight.getText().isEmpty() ? 0f : Float.parseFloat(txtGrossWeight.getText());
                fPurity = sPurity.isEmpty() ? 0f : Float.parseFloat(sPurity);
            }
            
            double dNetWt = 0;
            if(fPurity < 88) {
                dNetWt = (fPurity * dGrossWeight)/100;
            } else {
                double dReductionWt = Double.parseDouble(dbOp.getReductionWt("GOLD"));
                dNetWt = dGrossWeight - (dGrossWeight * dReductionWt);
            }
            
            double dSuggestionAmt = dNetWt * Double.parseDouble(dbOp.getCompanyRate("GOLD"));
            double propVal = (((Double.parseDouble(dbOp.getTodaysRate("GOLD")) - 300) * fPurity)/100) * dGrossWeight;
            
            txtNetWeight.setText(String.format("%.3f", dNetWt));
            txtSuggestionAmt.setText(String.valueOf(Math.round(dSuggestionAmt)));
            txtTotalValue.setText(String.valueOf(Math.round(propVal)));
            
            double dAmount = Double.parseDouble(txtAmount.getText().isEmpty() ? "0" : txtAmount.getText()); 
            if(dAmount > 0 && dSuggestionAmt > 0 && dSuggestionAmt >= dAmount) {
                txtSuggestionAmt.setStyle("-fx-background-color: #55FF30");
            } else if(dAmount > 0 && dSuggestionAmt > 0 && dSuggestionAmt < dAmount) {
                txtSuggestionAmt.setStyle("-fx-background-color: #FF5555");
            } else {
                txtSuggestionAmt.setStyle("-fx-background-color: #FFFFFF");
            }            
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    @FXML
    private void dpBillClosingAcceptedDateOnAction(ActionEvent event) {
    }

    @FXML
    private void rbBillClosingAcceptedToggleChanged(MouseEvent event) {        
        setBillClosingAcceptedValues(true);
    }            

    @FXML
    private void tbCutomerBillsListOnMouseClicked(MouseEvent event) {
        
        int index = tbCutomerBillsList.getSelectionModel().getSelectedIndex();
        
        if(event.getClickCount() == 2 && (index >= 0) ) 
        {
            if(dialog != null) {
                dialog.close();
                dialog = null;
            }

            String sCompId = tbCutomerBillsList.getItems().get(index).getCompId();
            
            if(sCompId.equals(CommonConstants.ACTIVE_COMPANY_ID)) {
                String sMaterialType = tbCutomerBillsList.getItems().get(index).getMaterialType();

                if("GOLD".equals(sMaterialType)) {

                    dialog = new Stage();
                    dialog.initModality(Modality.WINDOW_MODAL);        

                    FXMLLoader loader = new FXMLLoader(getClass().getResource(goldBillClosingScreen));
                    Parent root = null;
                    try {            
                        root = (Parent) loader.load();            
                    } catch (IOException ex) {
                        Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    GoldBillClosingController gon = (GoldBillClosingController) loader.getController();
                    gon.closeBill(tbCutomerBillsList.getItems().get(index).getBillNumber(), true);

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

                } else {

                    dialog = new Stage();
                    dialog.initModality(Modality.WINDOW_MODAL);        

                    FXMLLoader loader = new FXMLLoader(getClass().getResource(silverBillClosingScreen));
                    Parent root = null;
                    try {            
                        root = (Parent) loader.load();            
                    } catch (IOException ex) {
                        Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    SilverBillClosingController gon = (SilverBillClosingController) loader.getController();
                    gon.closeBill(tbCutomerBillsList.getItems().get(index).getBillNumber(), true);

                    dialog.setTitle("Silver Bill Closing");      
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
            } else {
                PopupUtil.showInfoAlert(event, "The Selected bill is not in the selected license. So change the active license to view this bill");
            }
        }        
    }

    @FXML
    private void btShowInBillCalcClicked(ActionEvent event) {
        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource(billCalculatorScreen));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }

        BillCalculatorController gon = (BillCalculatorController) loader.getController();
        String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue());
        gon.beforeClose(tbCutomerBillsList.getItems(), sBillClosingDate);

        dialog.setTitle("Bill Calculator");      
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        dialog.setX(0);
        dialog.setY(5);
        dialog.setWidth(bounds.getWidth());
        dialog.setHeight(bounds.getHeight());
        dialog.setResizable(false);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.showAndWait();        
        
    }

    @FXML
    private void txtGivenAmountOnAction(ActionEvent event) {
        denominationWork(event);
    }

    private void denominationWork(Event event) {
        
        double dGivenAmt = Double.parseDouble(txtGivenAmount.getText());
        
        if(txtReBilledFrom.getText() == null || txtReBilledFrom.getText().isEmpty()) {
            try {
                dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("AvailableBalanceDialog.fxml"));
                Parent root = null;
                try {
                    root = (Parent) loader.load();
                } catch (IOException ex) {
                    Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
                }

                this.sLastSelectedId = tgOn.isSelected() ? txtBillNumber.getText() : this.sLastSelectedId;
                AvailableBalanceDialogUIController gon = (AvailableBalanceDialogUIController) loader.getController();
                String[] denominationDetails = new String[2];
                String operationName = "";
                String denominationBillNumber = sLastSelectedId;
                String materialType = "GOLD";
                boolean isMultiClose = false;
                boolean isMultiOperation = false;
                List<AvailableBalanceBean> currencyList = null;
                List<AvailableBalanceBean> multiClosingCurrencyList = null;  
                List<AvailableBalanceBean> multiOperationCurrencyList = null;  
                List<AvailableBalanceBean> billOpeningCurrencyList =
                        dbOp.getDenominationValues(CommonConstants.G_BILL_OPENING_OPERATION, sLastSelectedId, "GOLD");

                if(tgOn.isSelected()) {
                    gon.setParent(this, true);
                } else {
                    gon.setParent(this, false);
                }
                
                if(billOpeningCurrencyList == null) {
                    String multiOpBillNumber = sLastSelectedId + "-OPEN";
                    multiOperationCurrencyList =
                        dbOp.getDenominationValues(CommonConstants.MULTI_OPERATION_OPERATION, multiOpBillNumber, "GOLD"); 
                    if(multiOperationCurrencyList != null) {
                        currencyList = multiOperationCurrencyList;
                        operationName = CommonConstants.MULTI_OPERATION_OPERATION;
                        isMultiClose = false;
                        isMultiOperation = true;
                        denominationDetails =  
                                dbOp.getDenominationDetails(CommonConstants.MULTI_OPERATION_OPERATION, 
                                        multiOpBillNumber, "GOLD");
                        denominationBillNumber = denominationDetails[0];
                        materialType = denominationDetails[1];                                                
                    }
                }  else {
                    currencyList = billOpeningCurrencyList;
                    operationName = CommonConstants.G_BILL_OPENING_OPERATION;
                }
                
                gon.setInitValues(currencyList, isMultiClose, isMultiOperation);
                Scene scene = new Scene(root);
                dialog.setScene(scene);
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getVisualBounds();
                dialog.setX(180);
                dialog.setY(100);
                dialog.setHeight(520);
                dialog.setTitle("OPERATION: " + operationName 
                        + ", BILL NUMBERS: " + denominationBillNumber
                        + ", BILL MATERIAL TYPES: " + materialType);
                dialog.setResizable(false);
                dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
                dialog.showAndWait();
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                dialog = new Stage();
                dialog.initModality(Modality.WINDOW_MODAL);
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ReBillRecievedAndBalanceAmtDialog.fxml"));
                Parent root = null;
                try {
                    root = (Parent) loader.load();
                } catch (IOException ex) {
                    Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                ReBillRecievedAndBalanceAmtDialog gon = (ReBillRecievedAndBalanceAmtDialog) loader.getController();
                List<AvailableBalanceBean> currencyList =
                        dbOp.getDenominationValues(CommonConstants.G_REBILL_CLOSE_OPERATION, txtReBilledFrom.getText(), "GOLD");
                
                if(tgOn.isSelected()) {
                    gon.setParent(this);
                    gon.setInitValues(true, txtReBilledFrom.getText());
                } else {
                    gon.setParent(this);
                    gon.setInitValues(false, txtReBilledFrom.getText());
                }
                gon.setInitValues(currencyList);
                Scene scene = new Scene(root);
                dialog.setX(180);
                dialog.setY(100);
                dialog.setHeight(550);
                dialog.setWidth(1000);
                dialog.setScene(scene);
                dialog.setTitle("Rebill Planner for " + txtGrossWeight.getText() + "gm/" 
                        + txtPurity.getText() + "."
                        + " Rebilled From " + txtReBilledFrom.getText());
                dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
                dialog.showAndWait();            
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillClosingController.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }        
    }
    
    @FXML
    private void btOpenInRepledgeBillClosingClicked(ActionEvent event) {
        
        String sRepBillId = txtRepledgeBillId.getText();
        if(sRepBillId != null && !sRepBillId.isEmpty()) {

            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(reGoldBillClosingScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }

            RepledgeGoldBillClosingController gon = (RepledgeGoldBillClosingController) loader.getController();
            gon.closeBill(sRepBillId, true);

            dialog.setTitle("Repledge Bill Closing");      
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

    @FXML
    private void txtNomineeNameOnAction(ActionEvent event) {
        
        cbItemType.getEditor().requestFocus();   
        cbItemType.getEditor().positionCaret(cbItemType.getEditor().getText().length());
        isFromMobileNo = true;        
    }

    @FXML
    private void tbRNYDeliveredOnMouseClicked(MouseEvent event) {
        int index = tbRepList.getSelectionModel().getSelectedIndex();        
        
        if(event.getClickCount() == 2 && (index >= 0)) 
        {
            String status = tbRepList.getItems().get(index).getSRepledgeStatus();
            
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(reGoldBillClosingScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(TodaysAccountController.class.getName()).log(Level.SEVERE, null, ex);
            }

            RepledgeGoldBillClosingController gon = (RepledgeGoldBillClosingController) loader.getController();
            if("GIVEN".equals(status) || "OPENED".equals(status)) {
                gon.closeBill(tbRepList.getItems().get(index).getSRepledgeBillId(), true);
            } else {
                gon.viewBill(tbRepList.getItems().get(index).getSRepledgeBillId());
            }

            dialog.setTitle("Repledge Bill Closing");      
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

    private void companyCopyPrintWork(ActionEvent event, boolean directPrint) {
        
        if(txtSpouseName.getText() != null && !txtSpouseName.getText().isEmpty()) {
        
            try {
                String sCompanyFileName = CommonConstants.REPORT_LOCATION 
                        + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                        + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                        + "\\billopening_company.jasper";
                
                PrintService service = PrintServiceLookup.lookupDefaultPrintService();        
                String sPrinterName = service.getName();
                
                if(otherSettingValues.getRow(0).getColumn(7) == null 
                        || otherSettingValues.getRow(0).getColumn(7).toString()
                                .equals(CompanyMasterController.DO_NOT_PRINT)) {
                    PopupUtil.showInfoAlert(event, "Company copy is in 'Do Not Print' mode.");
                    sCompanyFileName = null;
                } else {
                    sPrinterName = otherSettingValues.getRow(0).getColumn(7).toString();
                }
                // customer copy params                
                List<Map<String, Object>> paramList = new ArrayList<>();
                paramList.add(compParamPreparation());
                
                NoticeUtil noticeUtil = new NoticeUtil();                
                if(directPrint) {
                    noticeUtil.mergeaAndGenerateNoticeOperationDirectPrint(true, 
                            sPrinterName, compParamPreparation(), 
                            sCompanyFileName);
                } else {
                    JasperPrint[] jPrints = noticeUtil.mergeaAndGenerateNoticeOperation("Gold Bill Opening - Company Copy", 
                            paramList, 
                            sCompanyFileName);   
                    /* THIS CODE IS TO PRINT BILL AS IMAGEWORKING FINE
                    java.awt.Image img = JasperPrintManager.printPageToImage(jPrints[0], 0, 1);
                    BufferedImage bi = (BufferedImage)img;
                    File f = new File("E://output.png");
                    ImageIO.write(bi, "png", f);*/
                }
            } catch (JRException ex) {
                PopupUtil.showErrorAlert(ex.getMessage());
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        } else {
            PopupUtil.showInfoAlert(event, "Not any details filled to print the records. ");
        }    
    }

    private void customerCopyPrintWork(ActionEvent event, boolean directPrint) {
        
        if(txtSpouseName.getText() != null && !txtSpouseName.getText().isEmpty()) {
        
            try {
                String sCustomerFileName = CommonConstants.REPORT_LOCATION 
                        + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                        + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                        + "\\billopening_customer.jasper";  
                PrintService service = PrintServiceLookup.lookupDefaultPrintService(); 
                String sPrinterName = service.getName();
                                        
                if(otherSettingValues.getRow(0).getColumn(8).toString().equals(CompanyMasterController.DO_NOT_PRINT)) {
                    PopupUtil.showInfoAlert(event, "Customer copy is in 'Do Not Print' mode.");
                    sCustomerFileName = null;
                } else {
                    sPrinterName = otherSettingValues.getRow(0).getColumn(8).toString();
                }

                // customer copy params                
                List<Map<String, Object>> paramList = new ArrayList<>();
                paramList.add(custParamPreparation());
                
                NoticeUtil noticeUtil = new NoticeUtil();                
                if(directPrint) {
                    noticeUtil.mergeaAndGenerateNoticeOperationDirectPrint(true, 
                            sPrinterName, custParamPreparation(), 
                            sCustomerFileName);
                } else {
                    noticeUtil.mergeaAndGenerateNoticeOperation("Gold Bill Opening - Customer copy", paramList, 
                            sCustomerFileName);                    
                }
            } catch (JRException ex) {
                PopupUtil.showErrorAlert(ex.getMessage());
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        } else {
            PopupUtil.showInfoAlert(event, "Not any details filled to print the records. ");
        }    
    }
    
    private Map<String, Object> compParamPreparation() throws SQLException {
        
        Map<String, Object> parameters = new HashMap<>();
        HashMap<String, String> headerValues = dbOp.getAllHeaderValues(sLastSelectedId, "GOLD");
        
        parameters.put("BILLNO", headerValues.get("BILL_NUMBER"));
        parameters.put("BARCODE", headerValues.get("BILL_NUMBER") + "-G-COMP");
        parameters.put("OPENED_DATE", CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue()));
        parameters.put("AMOUNT", Double.parseDouble(headerValues.get("AMOUNT")));
        parameters.put("AMOUNT_IN_WORDS", 
                ConvertNumberToWord.convertNumber(
                        Long.parseLong(headerValues.get("AMOUNT").replace(".0", ""))).toUpperCase()
                        + " ONLY");

        if(CommonConstants.ACTIVE_COMPANY_TYPE.equals(CommonConstants.PAWN)) {
            parameters.put("CUSTOMER_NAME", headerValues.get("CUSTOMER_NAME")+",");
        } else if(CommonConstants.ACTIVE_COMPANY_TYPE.equals(CommonConstants.RE)) {
            parameters.put("CUSTOMER_NAME", headerValues.get("CUSTOMER_NAME")+",");
        }
        parameters.put("SPOUSE_NAME", headerValues.get("SPOUSE_TYPE") + ", " + headerValues.get("SPOUSE_NAME") + ",");
        parameters.put("STREET", headerValues.get("DOOR_NUMBER") + ", " +headerValues.get("STREET")+",");
        parameters.put("AREA", headerValues.get("AREA")+",");
        parameters.put("CITY", headerValues.get("CITY")+".");

        parameters.put("INTEREST", "");
        parameters.put("ITEMS", headerValues.get("ITEMS"));
        parameters.put("GROSSWEIGHT", String.format("%.3f", Double.parseDouble(headerValues.get("GROSS_WEIGHT"))));
        parameters.put("PROPERTY_VALUE",  Double.parseDouble(txtTotalValue.getText()));
        parameters.put("MOBILE_NUMBER", headerValues.get("MOBILE_NUMBER"));
        parameters.put("NOMINEE_NAME", headerValues.get("NOMINEE_NAME") != null ? headerValues.get("NOMINEE_NAME") : "");
        parameters.put("ACCEPTED_CLOSING_DATE", CommonConstants.DATETIMEFORMATTER.format(dpBillCLosingAcceptedDate.getValue()));

        if(CommonConstants.ACTIVE_COMPANY_TYPE.equals(CommonConstants.PAWN)) {                
            parameters.put("CUSTOMER_NAME_TO_SIGN", headerValues.get("CUSTOMER_NAME"));
        } else if(CommonConstants.ACTIVE_COMPANY_TYPE.equals(CommonConstants.RE)) {
            parameters.put("CUSTOMER_NAME_TO_SIGN", headerValues.get("CUSTOMER_NAME"));
        }
        parameters.put("BILL_CREATOR_NAME", CommonConstants.EMP_NAME);

        parameters.put("MATERIAL_TYPE", "(jq;fk; kl;Lk;)");
        //FOR SILVER (nts;sp kl;Lk;)    
        return parameters;
    }

    private Map<String, Object> custParamPreparation() throws SQLException {
        
        Map<String, Object> parameters = new HashMap<>();
        HashMap<String, String> headerValues = dbOp.getAllHeaderValues(sLastSelectedId, "GOLD");
        
        parameters.put("BILLNO", headerValues.get("BILL_NUMBER"));
        parameters.put("BARCODE", headerValues.get("BILL_NUMBER") + "-G-CUST");
        parameters.put("OPENED_DATE", CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue()));
        parameters.put("AMOUNT", Double.parseDouble(headerValues.get("AMOUNT")));
        parameters.put("AMOUNT_IN_WORDS", 
                ConvertNumberToWord.convertNumber(
                        Long.parseLong(headerValues.get("AMOUNT").replace(".0", ""))).toUpperCase()
                        + " ONLY");

        if(CommonConstants.ACTIVE_COMPANY_TYPE.equals(CommonConstants.PAWN)) {
            parameters.put("CUSTOMER_NAME", headerValues.get("CUSTOMER_NAME")+",");
        } else if(CommonConstants.ACTIVE_COMPANY_TYPE.equals(CommonConstants.RE)) {
            parameters.put("CUSTOMER_NAME", headerValues.get("CUSTOMER_NAME")+",");
        }
        parameters.put("SPOUSE_NAME", headerValues.get("SPOUSE_TYPE") + ", " + headerValues.get("SPOUSE_NAME") + ",");
        parameters.put("STREET", headerValues.get("DOOR_NUMBER") + ", " +headerValues.get("STREET")+",");
        parameters.put("AREA", headerValues.get("AREA")+",");
        parameters.put("CITY", headerValues.get("CITY")+".");

        parameters.put("INTEREST", "");
        parameters.put("ITEMS", headerValues.get("ITEMS"));
        parameters.put("GROSSWEIGHT", String.format("%.3f", Double.parseDouble(headerValues.get("GROSS_WEIGHT"))));
        parameters.put("PROPERTY_VALUE",  Double.parseDouble(txtTotalValue.getText()));
        parameters.put("MOBILE_NUMBER", headerValues.get("MOBILE_NUMBER"));
        parameters.put("NOMINEE_NAME", headerValues.get("NOMINEE_NAME") != null ? headerValues.get("NOMINEE_NAME") : "");
        parameters.put("ACCEPTED_CLOSING_DATE", CommonConstants.DATETIMEFORMATTER.format(dpBillCLosingAcceptedDate.getValue()));

        if(CommonConstants.ACTIVE_COMPANY_TYPE.equals(CommonConstants.PAWN)) {                
            parameters.put("CUSTOMER_NAME_TO_SIGN", headerValues.get("CUSTOMER_NAME"));
        } else if(CommonConstants.ACTIVE_COMPANY_TYPE.equals(CommonConstants.RE)) {
            parameters.put("CUSTOMER_NAME_TO_SIGN", headerValues.get("CUSTOMER_NAME"));
        }
        parameters.put("BILL_CREATOR_NAME", CommonConstants.EMP_NAME);

        parameters.put("MATERIAL_TYPE", "(jq;fk; kl;Lk;)");
        //FOR SILVER (nts;sp kl;Lk;)    
        return parameters;
    }

    private Map<String, Object> packingParamPreparation() throws SQLException {
        
        Map<String, Object> parameters = new HashMap<>();
        HashMap<String, String> headerValues = dbOp.getAllHeaderValues(sLastSelectedId, "GOLD");
        
        parameters.put("BILLNO", headerValues.get("BILL_NUMBER"));
        parameters.put("BARCODE", headerValues.get("BILL_NUMBER") + "-G-PACK");
        parameters.put("OPENED_DATE", CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue()));
        parameters.put("AMOUNT", Double.parseDouble(headerValues.get("AMOUNT")));
        parameters.put("AMOUNT_IN_WORDS", 
                ConvertNumberToWord.convertNumber(
                        Long.parseLong(headerValues.get("AMOUNT").replace(".0", ""))).toUpperCase()
                        + " ONLY");

        if(CommonConstants.ACTIVE_COMPANY_TYPE.equals(CommonConstants.PAWN)) {
            parameters.put("CUSTOMER_NAME", headerValues.get("CUSTOMER_NAME") 
                    + " " + headerValues.get("SPOUSE_TYPE") + " " + headerValues.get("SPOUSE_NAME"));
        } else if(CommonConstants.ACTIVE_COMPANY_TYPE.equals(CommonConstants.RE)) {
            parameters.put("CUSTOMER_NAME", headerValues.get("CUSTOMER_NAME")+",");
        }
        parameters.put("SPOUSE_NAME", headerValues.get("SPOUSE_TYPE") + ", " + headerValues.get("SPOUSE_NAME") + ",");
        parameters.put("STREET", headerValues.get("DOOR_NUMBER") + ", " +headerValues.get("STREET")+",");
        parameters.put("AREA", headerValues.get("AREA")+",");
        parameters.put("CITY", headerValues.get("CITY")+".");

        parameters.put("INTEREST", "");
        parameters.put("ITEMS", headerValues.get("ITEMS").replace("\n", " "));
        parameters.put("GROSSWEIGHT", String.format("%.3f", Double.parseDouble(headerValues.get("GROSS_WEIGHT"))));
        parameters.put("PROPERTY_VALUE",  Double.parseDouble(txtTotalValue.getText()));
        parameters.put("MOBILE_NUMBER", headerValues.get("MOBILE_NUMBER"));
        parameters.put("NOMINEE_NAME", headerValues.get("NOMINEE_NAME") != null ? headerValues.get("NOMINEE_NAME") : "");
        parameters.put("ACCEPTED_CLOSING_DATE", CommonConstants.DATETIMEFORMATTER.format(dpBillCLosingAcceptedDate.getValue()));

        if(CommonConstants.ACTIVE_COMPANY_TYPE.equals(CommonConstants.PAWN)) {                
            parameters.put("CUSTOMER_NAME_TO_SIGN", headerValues.get("CUSTOMER_NAME"));
        } else if(CommonConstants.ACTIVE_COMPANY_TYPE.equals(CommonConstants.RE)) {
            parameters.put("CUSTOMER_NAME_TO_SIGN", headerValues.get("CUSTOMER_NAME"));
        }
        parameters.put("BILL_CREATOR_NAME", CommonConstants.EMP_NAME);

        parameters.put("MATERIAL_TYPE", "(jq;fk; kl;Lk;)");
        return parameters;
    }
    
    @FXML
    private void btEditCustomerDetailsClicked(ActionEvent event) {
        if(customerNames.getRowCount() > 0 && lastSelectedIndex >=0 ) {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(customerDetailsScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }

            CustomerDetailsController gon = (CustomerDetailsController) loader.getController();
            gon.viewEditCustomer(
                    customerNames.getRow(lastSelectedIndex).getColumn(1).toString(),
                    customerNames.getRow(lastSelectedIndex).getColumn(2).toString(),
                    customerNames.getRow(lastSelectedIndex).getColumn(3).toString(),
                    customerNames.getRow(lastSelectedIndex).getColumn(4).toString(),
                    customerNames.getRow(lastSelectedIndex).getColumn(5).toString(),
                    customerNames.getRow(lastSelectedIndex).getColumn(6).toString(),
                    customerNames.getRow(lastSelectedIndex).getColumn(7).toString(),
                    customerNames.getRow(lastSelectedIndex).getColumn(8).toString(),
                    customerNames.getRow(lastSelectedIndex).getColumn(9).toString()
            );

            dialog.setTitle("Customer Details");      
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
        } else {
            PopupUtil.showInfoAlert(event, "Not any customer record is selected to edit. ");
        }
    }

    @FXML
    private void btPrintPackingCardClicked(ActionEvent event) {  
        try {
            String sBillOpeningDate = dbOp.getBillOpenedDate(sLastSelectedId, "GOLD");
            if( sBillOpeningDate != null 
                    && DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sBillOpeningDate)) {
                packingCardPrintWork(event, false);
            } else { 
                PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }               
    }
    
    private void packingCardPrintWork(ActionEvent event, boolean directPrint) {
        
        if(txtSpouseName.getText() != null && !txtSpouseName.getText().isEmpty()) {
        
            try {
                String sPackingFileName = CommonConstants.REPORT_LOCATION 
                        + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                        + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                        + "\\billopening_small_card.jasper";                        
                PrintService service = PrintServiceLookup.lookupDefaultPrintService();        
                String sPrinterName = service.getName();
                
                if(otherSettingValues.getRow(0).getColumn(9).toString().equals(CompanyMasterController.DO_NOT_PRINT)) {
                    PopupUtil.showInfoAlert(event, "Packing copy is in 'Do Not Print' mode.");
                    sPackingFileName = null;
                } else {
                    sPrinterName = otherSettingValues.getRow(0).getColumn(9).toString();
                }
                // packing copy params                
                List<Map<String, Object>> paramList = new ArrayList<>();
                paramList.add(packingParamPreparation());
                
                NoticeUtil noticeUtil = new NoticeUtil();                
                if(directPrint) {
                    noticeUtil.mergeaAndGenerateNoticeOperationDirectPrint(true, 
                            sPrinterName, packingParamPreparation(), 
                            sPackingFileName);
                } else {
                    noticeUtil.mergeaAndGenerateNoticeOperation("Gold Bill Opening - Packing Copy", paramList, 
                            sPackingFileName);                    
                }
            } catch (JRException ex) {
                PopupUtil.showErrorAlert(ex.getMessage());
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        } else {
            PopupUtil.showInfoAlert(event, "Not any details filled to print the records. ");
        }    
    }

    @FXML
    private void btCaptureCustomerImgClicked(ActionEvent event) {
        //Platform.runLater(() -> {
            isOpenCustomerImgAvailable = false;
            String sCustomerCamName = otherSettingValues.getRow(0).getColumn(10).toString();
            if(!cbCustomerImageCameraName.getValue().equals("DEFAULT"))
            {
                sCustomerCamName = cbCustomerImageCameraName.getValue();
            }
            if(!sCustomerCamName.equals(CompanyMasterController.DO_NOT_TAKE_PICTURE)) {                
                try {                
                    File billNumberFolder = new File(materialFolder, CommonConstants.OPEN_CUSTOMER_IMAGE_NAME);
                    WebCamWork.captureImageFrom(sCustomerCamName, billNumberFolder.getAbsolutePath());
                    try (FileInputStream fis = new FileInputStream(billNumberFolder.getAbsolutePath())) {
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
        //});
    }

    @FXML
    private void btCaptureJewelImgClicked(ActionEvent event) {
        //Platform.runLater(() -> {
            
            //CommPortIdentifier portId1 = CommPortIdentifier.getPortIdentifier("COM4");
            //SerialPort serialPort = (SerialPort) portId1.open("ComControl", 2000);
            
            isOpenJewelImgAvailable = false;
            String sJewelCamName = otherSettingValues.getRow(0).getColumn(11).toString();
            if(!cbJewelImageCameraName.getValue().equals("DEFAULT"))
            {
                sJewelCamName = cbJewelImageCameraName.getValue();
            }
            if(!sJewelCamName.equals(CompanyMasterController.DO_NOT_TAKE_PICTURE)) {                
                try {                
                    File billNumberFolder = new File(materialFolder, CommonConstants.OPEN_JEWEL_IMAGE_NAME);
                    WebCamWork.captureImageFrom(sJewelCamName, billNumberFolder.getAbsolutePath());
                    try (FileInputStream fis = new FileInputStream(billNumberFolder.getAbsolutePath())) {
                        final Image img = new Image(fis);
                        ivJewelBill.setImage(img);
                        isOpenJewelImgAvailable = true;
                    } catch (IOException ex) {
                        Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (FrameGrabber.Exception ex) {
                    PopupUtil.showInfoAlert(event, "Invalid camera name was selected. "); 
                }                
            } else {        
                PopupUtil.showInfoAlert(event, "Not any camera was selected to take pic. ");
            }
        //});
    }

    private void captureUserImageWork(ActionEvent event, 
            String billNumber)
    {
                isOpenUserImgAvailable = false;
        String sUserCamName = otherSettingValues.getRow(0).getColumn(12).toString();
        if(!cbUserImageCameraName.getValue().equals("DEFAULT"))
        {
            sUserCamName = cbUserImageCameraName.getValue();
        }
        if(!sUserCamName.equals(CompanyMasterController.DO_NOT_TAKE_PICTURE)) {                
            try {
                File billNumberFolder = new File(materialFolder, billNumber);
                if(!billNumberFolder.exists()) {
                    billNumberFolder.mkdir();
                }
                File custTemp = new File(billNumberFolder, CommonConstants.OPEN_USER_IMAGE_NAME);
                WebCamWork.captureImageFrom(sUserCamName, custTemp.getAbsolutePath());
                isOpenUserImgAvailable = true;
            } catch (FrameGrabber.Exception ex) {
                PopupUtil.showInfoAlert(event, "Invalid camera name was selected. "); 
            }
        } else {        
            //PopupUtil.showInfoAlert("Not any camera was selected to take pic. ");
        }
    }
    @FXML
    private void ivCustomerImageClicked(MouseEvent event) {
        if (event.getClickCount() == 2 && (ivCustomerBill.getImage() != null)) {
            PopupUtil.showImageViewer(event, ivCustomerBill.getImage(), "CUSTOMER IMAGE");
        }
    }

    @FXML
    private void ivJewelImageClicked(MouseEvent event) {
        if (event.getClickCount() == 2 && (ivJewelBill.getImage() != null)) {
            PopupUtil.showImageViewer(event, ivJewelBill.getImage(), "JEWEL IMAGE");
        }
    }

    @FXML
    private void ivUserImageClicked(MouseEvent event) {
        if (event.getClickCount() == 2 && (ivUserBill.getImage() != null)) {
            PopupUtil.showImageViewer(event, ivUserBill.getImage(), "USER IMAGE");
        }        
    }

    @FXML
    private void btPrintCompanyCopyClicked(ActionEvent event) {        
        try {
            String sBillOpeningDate = dbOp.getBillOpenedDate(sLastSelectedId, "GOLD");
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sBillOpeningDate)) {
                companyCopyPrintWork(event, false);
            } else { 
                PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void btPrintCustomerCopyClicked(ActionEvent event) {
        try {
            String sBillOpeningDate = dbOp.getBillOpenedDate(sLastSelectedId, "GOLD");
            if(DateRelatedCalculations.isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sBillOpeningDate)) {
                customerCopyPrintWork(event, false);
            } else { 
                PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    private void saveImages(String sBillNumber) throws FileNotFoundException, IOException {
        
        File billNumberFolder = new File(
                materialFolder, sBillNumber.replaceAll("/", "_"));
        
        if(isOpenCustomerImgAvailable) {              
            if(!billNumberFolder.exists()) {
                billNumberFolder.mkdir();
            }
            File custTemp = new File(billNumberFolder, 
                    CommonConstants.OPEN_CUSTOMER_IMAGE_NAME);
            File cfile = new File(custTemp.getAbsolutePath());
            BufferedImage bImage = 
                    SwingFXUtils.fromFXImage(
                            ivCustomerBill.getImage(), null);
            ImageIO.write(bImage, "png", cfile);
            bImage.flush();
        }

        if(isOpenJewelImgAvailable) {              
            if(!billNumberFolder.exists()) {
                billNumberFolder.mkdir();
            }
            File custTemp = new File(billNumberFolder, CommonConstants.OPEN_JEWEL_IMAGE_NAME);
            File cfile = new File(custTemp.getAbsolutePath());
            BufferedImage bImage = SwingFXUtils.fromFXImage(ivJewelBill.getImage(), null);
            ImageIO.write(bImage, "png", cfile);
            bImage.flush();
        }

        /*if(isOpenUserImgAvailable) {              
            if(!billNumberFolder.exists()) {
                billNumberFolder.mkdir();
            }
            File custTemp = new File(billNumberFolder, CommonConstants.OPEN_USER_IMAGE_NAME);
            File cfile = new File(custTemp.getAbsolutePath());
            BufferedImage bImage = SwingFXUtils.fromFXImage(ivUserBill.getImage(), null);
            ImageIO.write(bImage, "png", cfile);
            bImage.flush();
        }
        System.gc();*/
    }

    @FXML
    private void txtRatePerGmOnAction(ActionEvent event) {
        txtAmount.requestFocus();
        txtAmount.positionCaret(txtAmount.getText().length());
    }

    @FXML
    private void btDenomClicked(ActionEvent event) {
        /*String sBillOpeningDate = CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue());
        if(DateRelatedCalculations
                .isFirstDateIsLesserToSecondDate(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE, sBillOpeningDate)) {*/
            denominationWork(event);
        /*} else {
            PopupUtil.showErrorAlert(event, "Sorry this date account was closed.");
        } */               
    }

    private void openDateRestriction() throws SQLException {
        
        String sBillClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpBillOpeningDate.getValue());

        if(otherSettingValues.getRowCount() > 0 && 
            !Boolean.valueOf(otherSettingValues.getRow(0).getColumn(3).toString())) {

            if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                    DateRelatedCalculations.getNextDateWithFormatted(CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                    sBillClosingDate))
            {
                if(tgOn.isSelected()) {
                    if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                            CommonConstants.GOLD_BILL_OPENING_SCREEN, "ALLOW_ADD") 
                            || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                        btSaveBill.setDisable(false);
                    } else {
                        btSaveBill.setDisable(true);
                    }                    
                } else if(tgOff.isSelected() && !isRebillOperation && !isMultiRebillOperation) {
                    if(dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, 
                            CommonConstants.GOLD_BILL_OPENING_SCREEN, "ALLOW_UPDATE") 
                            || CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
                        
                            String sClosingDate = 
                                    dbOp.getBillOpenedDate(
                                            txtBillNumber.getText(), "GOLD");
                            if(sClosingDate != null) {
                                String sClosedDate = CommonConstants.DATETIMEFORMATTER
                                        .format(LocalDate.parse(sClosingDate, CommonConstants.DATETIMEFORMATTER));
                                if(DateRelatedCalculations.isFirstDateIsEqualToSecondDate(
                                        DateRelatedCalculations.getNextDateWithFormatted(
                                                CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE), 
                                        sClosedDate)) {
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

    @FXML
    private void ivCustomerDetailsClicked(MouseEvent event) {
        if (event.getClickCount() == 2 && (ivCustomerDetails.getImage() != null)) {
            PopupUtil.showImageViewer(event, ivCustomerDetails.getImage(), "CUSTOMER ID IMAGE");            
        }        
    }

    @FXML
    private void btPrintRecieptClicked(ActionEvent event) {
        
        if(sLastSelectedId != null)
        {
            try {
                String sCompanyFileName = CommonConstants.REPORT_LOCATION 
                        + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                        + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                        + "\\BillOpening_RecievedAmt.jasper";                        
                
                Map<String, Object> parameters = new HashMap<>();
                String sBillClosingDate 
                        = CommonConstants.DATETIMEFORMATTER
                                .format(dpBillOpeningDate
                                        .getValue());
                parameters.put("name", 
                        txtCustomerName.getValue() + " " 
                        + cbSpouseType.getValue() + "\n"
                        + txtSpouseName.getText() + " ");
                if(tgOff.isSelected()) {
                    parameters.put("enquiry_closing", 
                        "Given Date:");
                    parameters.put("topay_paid", 
                        "Given Amt:");
                } else {
                    parameters.put("enquiry_closing", 
                        "To Give Date:");
                    parameters.put("topay_paid", 
                        "To Pay:");
                }
                parameters.put("bill_number", 
                        sLastSelectedId);
                parameters.put("closing_date", 
                        sBillClosingDate);
                parameters.put("Capital_amount", 
                        Double.parseDouble(txtAmount.getText()));
                parameters.put("Interest", 
                        Double.parseDouble(txtInterestPerMonth.getText()));
                parameters.put("doc_charge", 
                        Double.parseDouble(txtDocumentCharge.getText()));
                parameters.put("given_amt", 
                        Double.parseDouble(txtGivenAmount.getText()));                
                
                JasperPrint print = null;
                NoticeUtil noticeUtil = new NoticeUtil();                
                noticeUtil.mergeaAndGenerateNoticeIndividual("TOBEPAID", parameters, 
                        sCompanyFileName);
                //noticeUtil.mergeAndGenerateNoticeOperation(sCompanyFileName, sCustomerFileName, parameters);

            } catch (JRException ex) {
                PopupUtil.showErrorAlert(ex.getMessage());
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
            } 
        } else 
        {
            
        }
    }
    
}
