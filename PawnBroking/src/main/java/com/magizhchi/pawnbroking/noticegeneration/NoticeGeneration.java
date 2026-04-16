/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.noticegeneration;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.NoticeUtil;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.common.Util;
import com.magizhchi.pawnbroking.companybillclosing.GoldBillClosingController;
import com.magizhchi.pawnbroking.companybillclosing.SilverBillClosingController;
import com.magizhchi.pawnbroking.companybillopening.GoldBillOpeningController;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class NoticeGeneration implements Initializable {

    NoticeGenerationDBOperation dbOp;
    final String goldBillClosingScreen = "/com/magizhchi/pawnbroking/companybillclosing/GoldBillClosing.fxml";
    final String silverBillClosingScreen = "/com/magizhchi/pawnbroking/companybillclosing/SilverBillClosing.fxml";
    public Stage dialog;
    
    @FXML
    private TabPane tpCompScreen;
    @FXML
    private Label lbTo2;
    @FXML
    private DatePicker dpGenerateFrom;
    @FXML
    private Label lbTo21;
    @FXML
    private DatePicker dpGenerateTo;
    @FXML
    private ToggleButton tgOn;
    @FXML
    private ToggleGroup ViewModeGroup;
    @FXML
    private ToggleButton tgOff;
    @FXML
    private Button btGenerateNow;
    @FXML
    private Button btUpdateRecords;
    @FXML
    private HBox nodeAddToFilter;
    @FXML
    private Label lbFrom;
    @FXML
    private DatePicker dpPrinted;
    @FXML
    private Label lbTo;
    @FXML
    private DatePicker dpPosted;
    @FXML
    private Label lbTo3;
    @FXML
    private DatePicker dpAuction;
    @FXML
    private Label lbTo1;
    @FXML
    private TextField txtAmountSpent;
    @FXML
    private HBox hBoxTotalFields;
    @FXML
    private TextField txtTotalBills;
    @FXML
    private TextField txtTotalCustomers;
    @FXML
    private TableView<AllDetailsBean> tbNotice;
    @FXML
    private ComboBox<String> cbFontFamily;
    @FXML
    private Label lbMessage1;
    @FXML
    private Button btEOSaveBill;
    @FXML
    private TextArea txtContent;
    @FXML
    private TextField txtContentTextSize;
    @FXML
    private ToggleButton tgBold;
    @FXML
    private ToggleGroup ViewModeGroup11;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            try {
                dbOp = new NoticeGenerationDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(NoticeGeneration.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            String[] sNoticeValues = dbOp.getNoticeValues();
            String sDate = DateRelatedCalculations.getNextDateWithFormatted(sNoticeValues[0]);
            String eDate = DateRelatedCalculations.getPreviousDateWithFormatted(DateRelatedCalculations.getOneMonth(sDate));
            dpGenerateFrom.setValue(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));
            dpGenerateTo.setValue(LocalDate.parse(eDate, CommonConstants.DATETIMEFORMATTER));
            
            dpPosted.setValue(LocalDate.now());
            dpAuction.setValue(LocalDate.now());
            dpPrinted.setValue(LocalDate.now());
            DataTable values = dbOp.getNoticeValues(sDate, eDate);
            DataTable customerVals = dbOp.getMultiBilledCustomerList(sDate, eDate);            
            setTableValues(values, customerVals);
            
            String fonts[] = 
              GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

            for ( int i = 0; i < fonts.length; i++ )
            {
              cbFontFamily.getItems().add(fonts[i]);
            }
            cbFontFamily.getSelectionModel().select(0);
            txtContent.setFont(Font.font(cbFontFamily.getSelectionModel().getSelectedItem().toString(), FontWeight.BOLD, 18));
            
        } catch (SQLException ex) {
            Logger.getLogger(NoticeGeneration.class.getName()).log(Level.SEVERE, null, ex);
        }

        tbNotice.setRowFactory(tv -> new TableRow<AllDetailsBean>() {
            @Override
            public void updateItem(AllDetailsBean item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle("");
                } else if (item.getMultiBills()) {
                    setStyle(Util.getStyle("#000000", "#FF5555").toString());    
                } else {
                    setStyle("");
                }
            }
        });
        
    }    

    private void setTableValues(DataTable companyMISValues, DataTable customerValues) {   
        
        tbNotice.getItems().removeAll(tbNotice.getItems());
        int sameCustomerBillsCount = 1;
        for(int i=0; i<companyMISValues.getRowCount(); i++) {
            String billNumber = companyMISValues.getRow(i).getColumn(0).toString();
            String customerName = companyMISValues.getRow(i).getColumn(1).toString();
            String spouseType = companyMISValues.getRow(i).getColumn(2).toString();
            String spouseName = companyMISValues.getRow(i).getColumn(3).toString();
            String doorNo = companyMISValues.getRow(i).getColumn(4).toString();
            String street = companyMISValues.getRow(i).getColumn(5).toString();
            String area = companyMISValues.getRow(i).getColumn(6).toString();
            String city = companyMISValues.getRow(i).getColumn(7).toString();
            String billOpeningDate = companyMISValues.getRow(i).getColumn(8).toString();
            String amount = companyMISValues.getRow(i).getColumn(9).toString();
            String mobileNumber = companyMISValues.getRow(i).getColumn(10).toString();
            String materialType = companyMISValues.getRow(i).getColumn(11).toString();
            
            boolean posted = false;
            boolean delivered = false;
            String fineReceived = "";
            String billClosedDate = "";
            boolean multiBilled = false;
            String billsForThisCustomer = "0";            
            
            for(int j=0; j<customerValues.getRowCount(); j++) {
                if(customerName.equals(customerValues.getRow(j).getColumn(1).toString())
                        && spouseType.equals(customerValues.getRow(j).getColumn(2).toString())
                        && spouseName.equals(customerValues.getRow(j).getColumn(3).toString())
                        && doorNo.equals(customerValues.getRow(j).getColumn(4).toString())
                        && street.equals(customerValues.getRow(j).getColumn(5).toString())
                        && area.equals(customerValues.getRow(j).getColumn(6).toString())
                        && city.equals(customerValues.getRow(j).getColumn(7).toString())) {
                    multiBilled = true;
                    sameCustomerBillsCount++;
                }
            }
            tbNotice.getItems().add(new AllDetailsBean(billNumber, customerName, spouseType, spouseName, 
                    doorNo, street, area, city, posted, delivered, fineReceived, billClosedDate, 
                    billOpeningDate, amount, mobileNumber, multiBilled, billsForThisCustomer, materialType));
        }
        txtTotalBills.setText(Integer.toString(companyMISValues.getRowCount()));
        txtTotalCustomers.setText(Integer.toString(companyMISValues.getRowCount()- customerValues.getRowCount()-1));        
    }
    
    @FXML
    private void saveModeON(ActionEvent event) {
    }

    @FXML
    private void saveModeOFF(ActionEvent event) {
    }


    @FXML
    private void btGenerateNowClicked(ActionEvent event) {
    
        generateIndividualRecord();
        generateIndividualAcknowledgeAddress();
        generatePanelRecord();
    }

    private void generateIndividualRecord() {
        try {
            String sFileName = CommonConstants.REPORT_LOCATION 
                    + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                    + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                    + "\\noticegeneration.jasper";                        
            
            List<Map<String, Object>> ParamList = new ArrayList<>();	              
            AllDetailsBean preBean = null;            
            
            for(AllDetailsBean bean : tbNotice.getItems()) {
                final List<PrintBean> table = new ArrayList<>();
                if(bean.getMultiBills()) {
                    if(preBean == null || (preBean != null && !preBean.isSameRecord(bean))) {                           
                        DataTable noticeVals = dbOp.getThisCustomerNoticeValues(
                                CommonConstants.DATETIMEFORMATTER.format(dpGenerateFrom.getValue()), 
                                CommonConstants.DATETIMEFORMATTER.format(dpGenerateTo.getValue()),
                                bean);                        
                        for(int i=0; i<noticeVals.getRowCount(); i++) {                            
                            String slno = noticeVals.getRow(i).getColumn(0).toString();
                            String billNumber = noticeVals.getRow(i).getColumn(1).toString();
                            String openedDate = noticeVals.getRow(i).getColumn(2).toString();
                            String amount = noticeVals.getRow(i).getColumn(3).toString();                            

                            PrintBean nBean = new PrintBean();
                            nBean.setSLNO(slno);
                            nBean.setSBillNumber(billNumber);
                            nBean.setSOpeningDate(openedDate);
                            nBean.setDAmount(Double.parseDouble(amount));
                            table.add(nBean);     
                        }                                                
                        preBean = bean;
                        
                        final Map<String, Object> parameters = new HashMap<>();
                        final JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(table);
                        parameters.put("BillCalcCollectionBeanParam", tableList);
                        parameters.put("SENDING_DATE", CommonConstants.DATETIMEFORMATTER.format(dpPosted.getValue()));
                        parameters.put("AUCTION_DATE", CommonConstants.DATETIMEFORMATTER.format(dpAuction.getValue()));
                        parameters.put("CUSTOMER_NAME", bean.getCustomerName() + ",");
                        parameters.put("SPOUSE_NAME", bean.getSpouseType() + ", " + bean.getSpouseName() +",");
                        parameters.put("STREET", bean.getDoorNo() + ", " + bean.getStreet() + ",");
                        parameters.put("AREA", bean.getArea() + ",");
                        parameters.put("CITY", bean.getCity() + ".");
                        parameters.put("MOBILE_NUMBER", bean.getMobileNumber() + ".");
                        ParamList.add(parameters);                        
                    }
                } else {                    
                    PrintBean nBean = new PrintBean();
                    nBean.setSLNO(Integer.toString(1));
                    nBean.setSBillNumber(bean.getBillNumber());
                    nBean.setSOpeningDate(bean.getBillOpenedDate());
                    nBean.setDAmount(Double.parseDouble(bean.getAmount()));
                    table.add(nBean);                    
                    preBean = null;

                    final Map<String, Object> parameters = new HashMap<>();
                    final JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(table);
                    parameters.put("BillCalcCollectionBeanParam", tableList);
                    parameters.put("SENDING_DATE", CommonConstants.DATETIMEFORMATTER.format(dpPosted.getValue()));
                    parameters.put("AUCTION_DATE", CommonConstants.DATETIMEFORMATTER.format(dpAuction.getValue()));
                    parameters.put("CUSTOMER_NAME", bean.getCustomerName() + ",");
                    parameters.put("SPOUSE_NAME", bean.getSpouseType() + ", " + bean.getSpouseName() +",");
                    parameters.put("STREET", bean.getDoorNo() + ", " + bean.getStreet() + ",");
                    parameters.put("AREA", bean.getArea() + ",");
                    parameters.put("CITY", bean.getCity() + ".");
                    parameters.put("MOBILE_NUMBER", bean.getMobileNumber() + ".");
                    ParamList.add(parameters);                    
                }
                
            }
            NoticeUtil noticeUtil = new NoticeUtil();
            noticeUtil.generateSinglePageNoticeOperation(sFileName, ParamList);

        } catch (JRException ex) {
            PopupUtil.showErrorAlert(ex.getMessage());
            Logger.getLogger(NoticeGeneration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(NoticeGeneration.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }

    private void generateIndividualAcknowledgeAddress() {
        try {
            String sFileName = CommonConstants.REPORT_LOCATION 
                    + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                    + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                    + "\\notice_acknowledge_address.jasper";                        
            
            List<Map<String, Object>> ParamList = new ArrayList<>();	              
            AllDetailsBean preBean = null;            
            
            for(AllDetailsBean bean : tbNotice.getItems()) {
                final List<PrintBean> table = new ArrayList<>();
                if(bean.getMultiBills()) {
                    if(preBean == null || (preBean != null && !preBean.isSameRecord(bean))) {                           
                        DataTable noticeVals = dbOp.getThisCustomerNoticeValues(
                                CommonConstants.DATETIMEFORMATTER.format(dpGenerateFrom.getValue()), 
                                CommonConstants.DATETIMEFORMATTER.format(dpGenerateTo.getValue()),
                                bean);                        
                        for(int i=0; i<noticeVals.getRowCount(); i++) {                            
                            String slno = noticeVals.getRow(i).getColumn(0).toString();
                            String billNumber = noticeVals.getRow(i).getColumn(1).toString();
                            String openedDate = noticeVals.getRow(i).getColumn(2).toString();
                            String amount = noticeVals.getRow(i).getColumn(3).toString();                            

                            PrintBean nBean = new PrintBean();
                            nBean.setSLNO(slno);
                            nBean.setSBillNumber(billNumber);
                            nBean.setSOpeningDate(openedDate);
                            nBean.setDAmount(Double.parseDouble(amount));
                            table.add(nBean);     
                        }                                                
                        preBean = bean;
                        
                        final Map<String, Object> parameters = new HashMap<>();
                        final JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(table);
                        parameters.put("CUSTOMER_NAME", bean.getCustomerName() + ",");
                        parameters.put("SPOUSE_NAME", bean.getSpouseType() + ", " + bean.getSpouseName() +",");
                        parameters.put("STREET", bean.getDoorNo() + ", " + bean.getStreet() + ",");
                        parameters.put("AREA", bean.getArea() + ",");
                        parameters.put("CITY", bean.getCity() + ".");
                        ParamList.add(parameters);                        
                    }
                } else {                    
                    PrintBean nBean = new PrintBean();
                    nBean.setSLNO(Integer.toString(1));
                    nBean.setSBillNumber(bean.getBillNumber());
                    nBean.setSOpeningDate(bean.getBillOpenedDate());
                    nBean.setDAmount(Double.parseDouble(bean.getAmount()));
                    table.add(nBean);                    
                    preBean = null;

                    final Map<String, Object> parameters = new HashMap<>();
                    final JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(table);
                    parameters.put("CUSTOMER_NAME", bean.getCustomerName() + ",");
                    parameters.put("SPOUSE_NAME", bean.getSpouseType() + ", " + bean.getSpouseName() +",");
                    parameters.put("STREET", bean.getDoorNo() + ", " + bean.getStreet() + ",");
                    parameters.put("AREA", bean.getArea() + ",");
                    parameters.put("CITY", bean.getCity() + ".");
                    ParamList.add(parameters);                    
                }                
            }
            NoticeUtil noticeUtil = new NoticeUtil();
            noticeUtil.generateSinglePageNoticeOperation(sFileName, ParamList);

        } catch (JRException ex) {
            PopupUtil.showErrorAlert(ex.getMessage());
            Logger.getLogger(NoticeGeneration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(NoticeGeneration.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
    
    private void generatePanelRecord() {
        try {
            
            String sFileName = CommonConstants.REPORT_LOCATION 
                    + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                    + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                    + "\\monthlynoticepanel.jasper";                        
            
            List<PanelPrintBean> ParamList = new ArrayList<>();	            
            for(AllDetailsBean bean : tbNotice.getItems()) {                
                PanelPrintBean nBean = new PanelPrintBean();
                nBean.setSBillNumber(bean.getBillNumber());
                String sCustomerDetails = bean.getCustomerName() + " " 
                        + bean.getSpouseType() + " " + bean.getSpouseName() +",\n"
                        + bean.getDoorNo() + ", " + bean.getStreet() + ", "
                        + bean.getArea() + ","
                        + bean.getCity() + ".\n"
                        + "Mobile: " + bean.getMobileNumber() + ".";
                nBean.setSCustomerDetails(sCustomerDetails);
                ParamList.add(nBean);
            }
                                
            JRBeanCollectionDataSource tableList = new JRBeanCollectionDataSource(ParamList);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("BillCalcCollectionBeanParam", tableList);
            String sTitle = CommonConstants.MONTHFORMATTER.format(dpGenerateFrom.getValue()) + "th Month Notice List.";
            parameters.put("TITLE", sTitle);
            parameters.put("POSTEDDATE", CommonConstants.DATETIMEFORMATTER.format(dpPosted.getValue()));
            parameters.put("AUCTIONDATE", CommonConstants.DATETIMEFORMATTER.format(dpAuction.getValue()));
            
            JasperPrint print = null;
            NoticeUtil noticeUtil = new NoticeUtil();
            noticeUtil.generateNoticeOperation(sFileName, parameters);

        } catch (JRException ex) {
            PopupUtil.showErrorAlert(ex.getMessage());
            Logger.getLogger(NoticeGeneration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(NoticeGeneration.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
    
    @FXML
    private void btUpdateRecordsClicked(ActionEvent event) {
    }

    @FXML
        private void capitalizeCharOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();
        int caretPos = txt_TextField.getCaretPosition();
        txt_TextField.setText(txt_TextField.getText().toUpperCase());
        txt_TextField.positionCaret(caretPos);
    }

    @FXML
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {
        
        int index = tbNotice.getSelectionModel().getSelectedIndex();
        
        if(event.getClickCount() == 2 && (index >= 0) ) 
        {
            String sMaterialType = tbNotice.getItems().get(index).getMaterialType();

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
                gon.closeBill(tbNotice.getItems().get(index).getBillNumber(), true);

                dialog.setTitle("Gold Bill Closing");      
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
                gon.closeBill(tbNotice.getItems().get(index).getBillNumber(), true);

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
        }               
    }


    @FXML
    private void btEOSaveBillClicked(ActionEvent event) {
    }


    @FXML
    private void cbFontFamilyOnAction(ActionEvent event) {
        
        int sIndex = cbFontFamily.getSelectionModel().getSelectedIndex();
        
        if(sIndex >= 0) 
        {
            String sFontName = cbFontFamily.getItems().get(sIndex);
            txtContent.setFont(Font.font(sFontName, FontWeight.BOLD, 18));
        }        
    }

    @FXML
    private void boldON(ActionEvent event) {
    }

    @FXML
    private void dpGenerateToOnChanged(ActionEvent event) {
        
        try {
            String sBillOpeningDateStarted = CommonConstants.DATETIMEFORMATTER.format(dpGenerateFrom.getValue());
            String sBillOpeningDateEnded = CommonConstants.DATETIMEFORMATTER.format(dpGenerateTo.getValue());
            DataTable values = dbOp.getNoticeValues(sBillOpeningDateStarted, sBillOpeningDateEnded);
            DataTable customerVals = dbOp.getMultiBilledCustomerList(sBillOpeningDateStarted, sBillOpeningDateEnded);
            setTableValues(values, customerVals);
        } catch (SQLException ex) {
            Logger.getLogger(NoticeGeneration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
