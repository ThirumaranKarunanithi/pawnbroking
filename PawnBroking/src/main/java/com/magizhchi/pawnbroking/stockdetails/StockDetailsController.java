/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.stockdetails;

import com.magizhchi.pawnbroking.billcalculator.BillCalculatorController;
import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.Util;
import com.magizhchi.pawnbroking.companybillclosing.GoldBillClosingController;
import com.magizhchi.pawnbroking.companybillclosing.SilverBillClosingController;
import com.magizhchi.pawnbroking.companybillopening.GoldBillOpeningController;
import com.magizhchi.pawnbroking.companymaster.CompanyMasterController;
import com.magizhchi.pawnbroking.repledgebillclosing.RepledgeGoldBillClosingController;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import java.time.LocalDate;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableRow;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class StockDetailsController implements Initializable {

    public StockDetailsDBOperation dbOp;
    private final ArrayList<String> alFilterDBColumnName = new ArrayList<>();
    private final ArrayList<String> alFilterName = new ArrayList<>();
    private final ArrayList<String> alFilterValue = new ArrayList<>();
    private DataTable customerNames = null;
    public Stage dialog;
    final String goldBillClosingScreen = "/com/magizhchi/pawnbroking/companybillclosing/GoldBillClosing.fxml";
    final String silverBillClosingScreen = "/com/magizhchi/pawnbroking/companybillclosing/SilverBillClosing.fxml";
    final String reGoldBillClosingScreen = "/com/magizhchi/pawnbroking/repledgebillclosing/RepledgeGoldBillClosing.fxml";
    final String billCalculatorScreen = "/com/magizhchi/pawnbroking/billcalculator/BillCalculator.fxml";
    
    @FXML
    private TabPane tpScreen;
    @FXML
    private ToggleGroup rgStockGroup;
    @FXML
    private TextField txtCompNumberOfPcs;
    @FXML
    private TextField txtCompTotalCapitalAmt;
    @FXML
    private TextField txtCompInterestedAmt;
    @FXML
    private TextField txtCompTotalInterestedAmt;
    @FXML
    private TableView<AllDetailsBean> tbCompanyBillDetails;
    @FXML
    private HBox companyAloneFilterHBox;
    @FXML
    private ComboBox<String> cbCompAloneMaterialType;
    @FXML
    private HBox filterHBOX;
    @FXML
    private HBox nodeAddToFilter;
    @FXML
    private ComboBox<String> cbAllDetailsFilter;
    @FXML
    private ComboBox<String> cbAddToFilter;
    @FXML
    private TextField txtAddToFilter;
    @FXML
    private Button btAddToFilter;
    @FXML
    private Button btShowAllRecords;
    @FXML
    private TextField txtFilter;
    @FXML
    private Button btShowFilteredRecords;
    @FXML
    private DatePicker dpFrom;
    @FXML
    private DatePicker dpTo;
    @FXML
    private TextField txtRepNumberOfPcs;
    @FXML
    private TextField txtRepTotalCapitalAmt;
    @FXML
    private TextField txtRepInterestedAmt;
    @FXML
    private TextField txtRepTotalInterestedAmt;
    @FXML
    private TableView<RepAllDetailsBean> tbRepBillDetails;
    @FXML
    private Label lbFrom;
    @FXML
    private Label lbTo;
    @FXML
    private TextField txtFrom;
    @FXML
    private TextField txtTo;
    @FXML
    private DatePicker dpClosingDate;
    @FXML
    private ComboBox<String> cbRepNames;
    @FXML
    private HBox hbGrossPft;
    @FXML
    private TextField txtGrossWeight;
    @FXML
    private HBox hbFieldBox;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        tbCompanyBillDetails.setRowFactory(tv -> new TableRow<AllDetailsBean>() {
            @Override
            public void updateItem(AllDetailsBean item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle("");
                } else if (item.getSRepledgeBillId() != null && !item.getSRepledgeBillId().isEmpty()) {
                    setStyle(Util.getStyle("#000000", "#FCBAEF").toString());
                } else {
                    setStyle("");
                }
            }
        });
        
        try {
            dbOp = new StockDetailsDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StockDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        cbAddToFilter.getEditor().setOnKeyTyped((KeyEvent e) -> {

            boolean isEnter = "\r".equals(e.getCharacter());
            String pattern= "^[a-zA-Z0-9 ]*$";

            if(e.getCharacter().matches(pattern)) {
                setTxtCustomerNameValues(cbAddToFilter.getEditor().getText().toUpperCase()+e.getCharacter().toUpperCase());
                cbAddToFilter.show();
            } else if(isEnter) {                 
            } else {
                e.consume();
            }
        });
        
        try {                
            cbRepNames.getItems().removeAll(cbRepNames.getItems());
            DataTable table = dbOp.getAllRepledgeNames();
            for(int i=0; i<table.getRowCount(); i++) {
                cbRepNames.getItems().add(table.getRow(i).getColumn(0).toString());
            }
            cbRepNames.getSelectionModel().select(0);
        } catch (SQLException ex) {
            Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        dpClosingDate.setValue(LocalDate.now());
        
        nodeAddToFilter.getChildren().remove(txtAddToFilter);
        nodeAddToFilter.getChildren().remove(dpFrom);
        nodeAddToFilter.getChildren().remove(dpTo);
        nodeAddToFilter.getChildren().remove(lbFrom);
        nodeAddToFilter.getChildren().remove(lbTo);
        nodeAddToFilter.getChildren().remove(txtFrom);
        nodeAddToFilter.getChildren().remove(txtTo);
        
        cbAddToFilter.setEditable(true);
        
        try {
            if(CommonConstants.ROLEID.equals(CommonConstants.TIRU) || 
                    dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.SPECIAL_OPTIONS_TAB, 
                            CommonConstants.PF_TC_FD)) {
                if(!hbFieldBox.getChildren().contains(hbGrossPft)) {
                    hbFieldBox.getChildren().add(1, hbGrossPft);
                }
            } else {
                if(hbFieldBox.getChildren().contains(hbGrossPft)) {
                    hbFieldBox.getChildren().remove(hbGrossPft);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(StockDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }      
    }    

    public String createScriptForCustomerName() {
        int sIndex = cbAddToFilter.getSelectionModel().getSelectedIndex();
        String script = null;
        if(sIndex >=0) {
            script = "'" + customerNames.getRow(sIndex).getColumn(1).toString() + "'" +
                    " AND GENDER = '" + customerNames.getRow(sIndex).getColumn(2).toString() + "'" +
                    " AND SPOUSE_TYPE = '" + customerNames.getRow(sIndex).getColumn(3).toString() + "'" +
                    " AND SPOUSE_NAME = '" + customerNames.getRow(sIndex).getColumn(4).toString() + "'" + 
                    " AND DOOR_NUMBER = '" + customerNames.getRow(sIndex).getColumn(5).toString() + "'" +
                    " AND STREET = '" + customerNames.getRow(sIndex).getColumn(6).toString() + "'" +
                    " AND AREA = '" + customerNames.getRow(sIndex).getColumn(7).toString() + "'" +
                    " AND CITY = '" + customerNames.getRow(sIndex).getColumn(8).toString() + "'" +
                    " AND MOBILE_NUMBER = '" + customerNames.getRow(sIndex).getColumn(9).toString() + "'";                            
        }        
        return script;
    }

    public String createScriptForRepledgeName() {
        int sIndex = cbAddToFilter.getSelectionModel().getSelectedIndex();
        String script = null;
        if(sIndex >=0) {
            script = " '" + cbAddToFilter.getSelectionModel().getSelectedItem() + "'";
        }        
        return script;
    }
    
    public void setTxtCustomerNameValues(String sCustomerName) {
        Platform.runLater(()->{
            try {
                cbAddToFilter.getItems().removeAll(cbAddToFilter.getItems());
                customerNames = dbOp.getFilteredCustomerNames(sCustomerName);
                for(int i=0; i<customerNames.getRowCount(); i++) {          
                    cbAddToFilter.getItems().add(customerNames.getRow(i).getColumn(0).toString());
                }                
            } catch (SQLException ex) {
                Logger.getLogger(StockDetailsController.class.getName()).log(Level.SEVERE, null, ex);
            }                
        });
    }
    
    public void setCompAloneDetailValuesToField(DataTable allDetailValues) {
        
        tbRepBillDetails.getItems().removeAll(tbRepBillDetails.getItems());
        tbCompanyBillDetails.getItems().removeAll(tbCompanyBillDetails.getItems());
        try {
            dbOp.connectDB();
        } catch(Exception e) {
            System.out.println("Error in connecting db");
        }
        for(int i=0; i<allDetailValues.getRowCount(); i++) {            
            String sRepledgeBillId = allDetailValues.getRow(i).getColumn(10) != null 
                    ? allDetailValues.getRow(i).getColumn(10).toString() : "";
            String sBillNumber = allDetailValues.getRow(i).getColumn(0).toString();
            String sDate = allDetailValues.getRow(i).getColumn(1).toString();
            String sItems = allDetailValues.getRow(i).getColumn(2).toString();
            String sAmount = allDetailValues.getRow(i).getColumn(3).toString();
            String sInterest = allDetailValues.getRow(i).getColumn(4).toString();
            String sClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpClosingDate.getValue());
            String sStatus = allDetailValues.getRow(i).getColumn(5).toString();
            String sDocumentCharge = allDetailValues.getRow(i).getColumn(6).toString();
            String sInterestedAmount = "0";
            String sTotalInterestedAmount = sAmount;
            String sTotalAdvanceAmount = allDetailValues.getRow(i).getColumn(7).toString();    
            String sMaterialType = allDetailValues.getRow(i).getColumn(8).toString();
            String sGrWt = allDetailValues.getRow(i).getColumn(9).toString();
            String sCustomerName = allDetailValues.getRow(i).getColumn(11).toString();
            String sArea = allDetailValues.getRow(i).getColumn(12).toString();
            
            double dAmount = Double.parseDouble(sAmount);
            double dGrWt = Double.parseDouble(sGrWt);
            double dRatePerGm = dAmount/dGrWt;
            String sRatePerGm = String.format("%.0f", dRatePerGm);
            
            tbCompanyBillDetails.getItems().add(new AllDetailsBean(sRepledgeBillId, sBillNumber, 
                    sCustomerName, sArea,
                    sItems, dGrWt, sRatePerGm,
                    sDate, Double.parseDouble(sAmount), sInterest, sClosingDate, sStatus, sInterestedAmount, 
                    sTotalInterestedAmount, 
                    sDocumentCharge, sTotalAdvanceAmount, sMaterialType, true, this.dbOp));
        }        
        try {
            dbOp.disConnectDB();
            setHeaderValues("ALL");
        } catch(Exception e) {
            System.out.println("Error in connecting db");
        }
    }    

    public void setRepAloneDetailValuesToField(DataTable allDetailValues, String sMaterialType) {
        
        tbRepBillDetails.getItems().removeAll(tbRepBillDetails.getItems());
        try {
            dbOp.connectDB();
        } catch(Exception e) {
            System.out.println("Error in connecting db");
        }
        for(int i=0; i<allDetailValues.getRowCount(); i++) {            
            String sRepledgeBillId = allDetailValues.getRow(i).getColumn(0).toString();;
            String sRepledgeId = allDetailValues.getRow(i).getColumn(1).toString();
            String sRepledgeName = allDetailValues.getRow(i).getColumn(2).toString();
            String sCompBillNumber = allDetailValues.getRow(i).getColumn(3).toString();
            String sRepAmount = allDetailValues.getRow(i).getColumn(5).toString();
            String sRepInterest = allDetailValues.getRow(i).getColumn(6).toString();
            String sRepDocumentCharge = allDetailValues.getRow(i).getColumn(7).toString();
            String sRepOpeningDate = allDetailValues.getRow(i).getColumn(4).toString();
            String sRepClosingDate = CommonConstants.DATETIMEFORMATTER.format(dpClosingDate.getValue());
            String sInterestedAmount = "0";
            String sTotalInterestedAmount = sRepAmount;
            tbRepBillDetails.getItems().add(new RepAllDetailsBean(sRepledgeBillId, sRepledgeId, 
                    sRepledgeName, sCompBillNumber, Double.parseDouble(sRepAmount), sRepInterest, sRepOpeningDate, 
                    sRepClosingDate, sInterestedAmount, sTotalInterestedAmount, 
                    sRepDocumentCharge, sMaterialType, true, dbOp));
        }        
        try {
            dbOp.disConnectDB();
            setRepHeaderValues("ALL");
        } catch(Exception e) {
            System.out.println("Error in connecting db");
        }
    }    
    
    public void setHeaderValues(String selectOrDeSelectOrAll) {

        int iPcs = 0;
        double dAmt = 0;
        double dIntrAmt = 0;
        double dTotIntrAmt = 0;        
        double dGrsWt = 0;
        
        for(AllDetailsBean bean : tbCompanyBillDetails.getItems()) {
            if(selectOrDeSelectOrAll.equals("SELECTED") && bean.isBChecked()) {
                iPcs++;
                dAmt = dAmt + bean.getSAmount();
                dIntrAmt = dIntrAmt 
                        + Double.parseDouble(
                                bean.getSInterestedAmount());
                dTotIntrAmt = dTotIntrAmt 
                        + Double.parseDouble(
                                bean.getSTotalInterestedAmount());
                dGrsWt = dGrsWt 
                        + bean.getSGrWt();
            } else if(selectOrDeSelectOrAll.equals("DESELECTED") && !bean.isBChecked()) {
                iPcs++;
                dAmt = dAmt + bean.getSAmount();
                dIntrAmt = dIntrAmt 
                        + Double.parseDouble(
                                bean.getSInterestedAmount());
                dTotIntrAmt = dTotIntrAmt 
                        + Double.parseDouble(
                                bean.getSTotalInterestedAmount());
                dGrsWt = dGrsWt 
                        + bean.getSGrWt();                
            } else if(selectOrDeSelectOrAll.equals("ALL")) {
                iPcs++;
                dAmt = dAmt + bean.getSAmount();
                dIntrAmt = dIntrAmt 
                        + Double.parseDouble(
                                bean.getSInterestedAmount());
                dTotIntrAmt = dTotIntrAmt 
                        + Double.parseDouble(
                                bean.getSTotalInterestedAmount());
                dGrsWt = dGrsWt 
                        + bean.getSGrWt();                
            }
        }
        
        txtCompNumberOfPcs.setText(
                Integer.toString(iPcs));
        txtCompTotalCapitalAmt.setText(
                String.format("%.0f", dAmt));
        txtCompInterestedAmt.setText(
                String.format("%.0f", dIntrAmt));
        txtCompTotalInterestedAmt.setText(
                String.format("%.0f", dTotIntrAmt));
        txtGrossWeight.setText(
                String.format("%.0f", dGrsWt));
    }

    public void setRepHeaderValues(String selectOrDeSelectOrAll) {

        int iPcs = 0;
        double dAmt = 0;
        double dIntrAmt = 0;
        double dTotIntrAmt = 0;        
        
        for(RepAllDetailsBean bean : tbRepBillDetails.getItems()) {
            if(selectOrDeSelectOrAll.equals("SELECTED") && bean.isBChecked()) {
                iPcs++;
                dAmt = dAmt + bean.getSRepAmount();
                dIntrAmt = dIntrAmt + Double.parseDouble(bean.getSInterestedAmount());
                dTotIntrAmt = dTotIntrAmt 
                        + Double.parseDouble(
                                bean.getSTotalInterestedAmount());
            } else if(selectOrDeSelectOrAll.equals("DESELECTED") && !bean.isBChecked()) {
                iPcs++;
                dAmt = dAmt + bean.getSRepAmount();
                dIntrAmt = dIntrAmt + Double.parseDouble(bean.getSInterestedAmount());
                dTotIntrAmt = dTotIntrAmt + Double.parseDouble(bean.getSTotalInterestedAmount());
            } else if(selectOrDeSelectOrAll.equals("ALL")) {
                iPcs++;
                dAmt = dAmt + bean.getSRepAmount();
                dIntrAmt = dIntrAmt + Double.parseDouble(bean.getSInterestedAmount());
                dTotIntrAmt = dTotIntrAmt + 
                        Double.parseDouble(
                                bean.getSTotalInterestedAmount());
            }
        }
        
        txtRepNumberOfPcs.setText(Integer.toString(iPcs));
        txtRepTotalCapitalAmt.setText(String.format("%.0f", dAmt));
        txtRepInterestedAmt.setText(String.format("%.0f", dIntrAmt));
        txtRepTotalInterestedAmt.setText(String.format("%.0f", dTotIntrAmt));
    }
    
    @FXML
    private void rbToggleChanged(MouseEvent event) {
        RadioButton selectedRadioButton = (RadioButton) rgStockGroup.getSelectedToggle();        
        if(null != selectedRadioButton.getText()) switch (selectedRadioButton.getText()) {
            case "Company Alone":                
                cbAllDetailsFilter.getItems().remove("REPLEDGE NAME");
                cbAllDetailsFilter.getItems().remove("REPLEDGE OPENED DATE");
                if(!filterHBOX.getChildren().contains(companyAloneFilterHBox)) {
                    filterHBOX.getChildren().add(companyAloneFilterHBox);
                }                    
                break;
            case "Repledge Alone":
                cbAllDetailsFilter.getItems().add("REPLEDGE OPENED DATE");
                cbAllDetailsFilter.getItems().add("REPLEDGE NAME");                
                if(filterHBOX.getChildren().contains(companyAloneFilterHBox)) {
                    filterHBOX.getChildren().remove(companyAloneFilterHBox);
                }
                break;
            case "All Details":
                cbAllDetailsFilter.getItems().remove("REPLEDGE NAME");
                cbAllDetailsFilter.getItems().remove("REPLEDGE OPENED DATE");
                if(!filterHBOX.getChildren().contains(companyAloneFilterHBox)) {
                    filterHBOX.getChildren().add(companyAloneFilterHBox);
                }                    
                break;
            default:
                if(filterHBOX.getChildren().contains(companyAloneFilterHBox)) {
                    filterHBOX.getChildren().remove(companyAloneFilterHBox);
                }
                break;
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
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {
        
        int index = tbCompanyBillDetails.getSelectionModel().getSelectedIndex();
        
        if(event.getClickCount() == 2 && (index >= 0) ) 
        {
            String sMaterialType = tbCompanyBillDetails.getItems().get(index).getSMaterialType();

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
                gon.closeBill(tbCompanyBillDetails.getItems().get(index).getSBillNumber(), true);

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
                gon.closeBill(tbCompanyBillDetails.getItems().get(index).getSBillNumber(), true);

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
        } else if(event.getClickCount() == 1) {
            if(tbCompanyBillDetails != null && index <= tbCompanyBillDetails.getItems().size()) {
                tbCompanyBillDetails.getItems().get(index).setBChecked(!tbCompanyBillDetails.getItems().get(index).getBCheckedProperty());
                setHeaderValues("SELECTED");
            }
        }         
    }

    @FXML
    private void cbAllDetailsFilterOnAction(ActionEvent event) {
        
        int sIndex = cbAllDetailsFilter.getSelectionModel().getSelectedIndex();
        
        if(sIndex >= 0) 
        {
            String sFilterName = cbAllDetailsFilter.getItems().get(sIndex);
            if(null != sFilterName) switch (sFilterName) {
                case "COMPANY OPENED DATE":
                    nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    nodeAddToFilter.getChildren().remove(cbAddToFilter);
                    nodeAddToFilter.getChildren().remove(txtFrom);
                    nodeAddToFilter.getChildren().remove(txtTo);                    
                    
                    if(!nodeAddToFilter.getChildren().contains(lbFrom))
                        nodeAddToFilter.getChildren().add(1, lbFrom);
                    if(!nodeAddToFilter.getChildren().contains(dpFrom))
                        nodeAddToFilter.getChildren().add(2, dpFrom);
                    if(!nodeAddToFilter.getChildren().contains(lbTo))
                        nodeAddToFilter.getChildren().add(3, lbTo);
                    if(!nodeAddToFilter.getChildren().contains(dpTo))
                        nodeAddToFilter.getChildren().add(4, dpTo);
                    break;
                case "REPLEDGE OPENED DATE":
                    nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    nodeAddToFilter.getChildren().remove(cbAddToFilter);
                    nodeAddToFilter.getChildren().remove(txtFrom);
                    nodeAddToFilter.getChildren().remove(txtTo);                    
                    
                    if(!nodeAddToFilter.getChildren().contains(lbFrom))
                        nodeAddToFilter.getChildren().add(1, lbFrom);
                    if(!nodeAddToFilter.getChildren().contains(dpFrom))
                        nodeAddToFilter.getChildren().add(2, dpFrom);
                    if(!nodeAddToFilter.getChildren().contains(lbTo))
                        nodeAddToFilter.getChildren().add(3, lbTo);
                    if(!nodeAddToFilter.getChildren().contains(dpTo))
                        nodeAddToFilter.getChildren().add(4, dpTo);
                    break;
                case "COMPANY BILL AMOUNT":
                    if(nodeAddToFilter.getChildren().contains(dpFrom))
                        nodeAddToFilter.getChildren().remove(dpFrom);
                    if(nodeAddToFilter.getChildren().contains(dpTo))
                        nodeAddToFilter.getChildren().remove(dpTo);
                    if(nodeAddToFilter.getChildren().contains(cbAddToFilter))
                        nodeAddToFilter.getChildren().remove(cbAddToFilter);
                    if(nodeAddToFilter.getChildren().contains(txtAddToFilter))
                        nodeAddToFilter.getChildren().remove(txtAddToFilter);
                    
                    if(!nodeAddToFilter.getChildren().contains(lbFrom))
                        nodeAddToFilter.getChildren().add(1, lbFrom);
                    if(!nodeAddToFilter.getChildren().contains(txtFrom))
                        nodeAddToFilter.getChildren().add(2, txtFrom);
                    if(!nodeAddToFilter.getChildren().contains(lbTo))
                        nodeAddToFilter.getChildren().add(3, lbTo);
                    if(!nodeAddToFilter.getChildren().contains(txtTo))
                        nodeAddToFilter.getChildren().add(4, txtTo);
                    break;
                case "CUSTOMER NAME":
                    cbAddToFilter.setEditable(true);
                    if(nodeAddToFilter.getChildren().contains(dpFrom))
                        nodeAddToFilter.getChildren().remove(dpFrom);
                    if(nodeAddToFilter.getChildren().contains(dpTo))
                        nodeAddToFilter.getChildren().remove(dpTo);
                    if(nodeAddToFilter.getChildren().contains(txtAddToFilter))
                        nodeAddToFilter.getChildren().remove(txtAddToFilter);                    
                    if(nodeAddToFilter.getChildren().contains(lbFrom))
                        nodeAddToFilter.getChildren().remove(lbFrom);
                    if(nodeAddToFilter.getChildren().contains(txtFrom))
                        nodeAddToFilter.getChildren().remove(txtFrom);
                    if(nodeAddToFilter.getChildren().contains(lbTo))
                        nodeAddToFilter.getChildren().remove(lbTo);
                    if(nodeAddToFilter.getChildren().contains(txtTo))
                        nodeAddToFilter.getChildren().remove(txtTo);
                    if(!nodeAddToFilter.getChildren().contains(cbAddToFilter)) {
                        nodeAddToFilter.getChildren().add(1, cbAddToFilter);
                        cbAddToFilter.setEditable(true);
                    }
                    break;
                case "REPLEDGE NAME":
                    if(nodeAddToFilter.getChildren().contains(dpFrom))
                        nodeAddToFilter.getChildren().remove(dpFrom);
                    if(nodeAddToFilter.getChildren().contains(dpTo))
                        nodeAddToFilter.getChildren().remove(dpTo);
                    if(nodeAddToFilter.getChildren().contains(txtAddToFilter))
                        nodeAddToFilter.getChildren().remove(txtAddToFilter);                    
                    if(nodeAddToFilter.getChildren().contains(lbFrom))
                        nodeAddToFilter.getChildren().remove(lbFrom);
                    if(nodeAddToFilter.getChildren().contains(txtFrom))
                        nodeAddToFilter.getChildren().remove(txtFrom);
                    if(nodeAddToFilter.getChildren().contains(lbTo))
                        nodeAddToFilter.getChildren().remove(lbTo);
                    if(nodeAddToFilter.getChildren().contains(txtTo))
                        nodeAddToFilter.getChildren().remove(txtTo);
                    if(!nodeAddToFilter.getChildren().contains(cbAddToFilter)) {
                        nodeAddToFilter.getChildren().add(1, cbAddToFilter);
                        cbAddToFilter.setEditable(true);
                    }
                    cbAddToFilter.setEditable(false);
                    DataTable repledgeNames;
                    try {                
                        cbAddToFilter.getItems().removeAll(cbAddToFilter.getItems());
                        repledgeNames = dbOp.getAllRepledgeNames();
                        for(int i=0; i<repledgeNames.getRowCount(); i++) {
                            cbAddToFilter.getItems().add(repledgeNames.getRow(i).getColumn(0).toString());
                        }
                        cbAddToFilter.getSelectionModel().select(0);
                    } catch (SQLException ex) {
                        Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                    break;
                default:
                    if(!nodeAddToFilter.getChildren().contains(lbFrom))
                        nodeAddToFilter.getChildren().remove(lbFrom);
                    if(!nodeAddToFilter.getChildren().contains(dpFrom))
                        nodeAddToFilter.getChildren().remove(dpFrom);
                    if(!nodeAddToFilter.getChildren().contains(lbTo))
                        nodeAddToFilter.getChildren().remove(lbTo);
                    if(!nodeAddToFilter.getChildren().contains(dpTo))
                        nodeAddToFilter.getChildren().remove(dpTo);
                    if(!nodeAddToFilter.getChildren().contains(txtFrom))
                        nodeAddToFilter.getChildren().remove(txtFrom);
                    if(!nodeAddToFilter.getChildren().contains(txtTo))
                        nodeAddToFilter.getChildren().remove(txtTo);
                    if(!nodeAddToFilter.getChildren().contains(cbAddToFilter))
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
            }else if(nodeAddToFilter.getChildren().contains(dpFrom)) {
                sFilterValue = "BETWEEN " 
                        + "'" + CommonConstants.DBDATETIMEFORMATTER.format(dpFrom.getValue()) + "'"
                        + " AND "
                        + "'" + CommonConstants.DBDATETIMEFORMATTER.format(dpTo.getValue()) + "'";
            }else if(nodeAddToFilter.getChildren().contains(txtFrom)) {
                sFilterValue = "BETWEEN " 
                        + "'" + txtFrom.getText() + "'"
                        + " AND "
                        + "'" + txtTo.getText() + "'";
            }else if(nodeAddToFilter.getChildren().contains(cbAddToFilter) 
                    && cbAllDetailsFilter.getSelectionModel().getSelectedItem().equals("CUSTOMER NAME")) {                
                sFilterValue = createScriptForCustomerName();
            }else if(nodeAddToFilter.getChildren().contains(cbAddToFilter) 
                    && cbAllDetailsFilter.getSelectionModel().getSelectedItem().equals("REPLEDGE NAME")) {                
                sFilterValue = createScriptForRepledgeName();
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
            case "COMPANY OPENED DATE":
                return "CB.OPENING_DATE";
            case "REPLEDGE OPENED DATE":
                return "RB.OPENING_DATE";
            case "COMPANY BILL AMOUNT":
                return "CB.AMOUNT";
            case "CUSTOMER NAME":
                return "CB.CUSTOMER_NAME";
            case "REPLEDGE NAME":
                return "RB.REPLEDGE_NAME";
            default:
                return null;
        }
    }
    
    @FXML
    private void showAllRecordsClicked(ActionEvent event) {
        
        try {
            RadioButton selectedRadioButton = (RadioButton) rgStockGroup.getSelectedToggle();        
            if(null != selectedRadioButton.getText()) switch (selectedRadioButton.getText()) {
                    case "Company Alone":
                        String sMaterialType = cbCompAloneMaterialType.getValue();
                        DataTable allDetailValues = dbOp.getCompAllDetailsValues(sMaterialType, null);
                        setCompAloneDetailValuesToField(allDetailValues);
                        break;
                    case "Repledge Alone":
                        DataTable repCompDetailValues = dbOp.getRepCompAllDetailsValues("GOLD", null);
                        setCompAloneDetailValuesToField(repCompDetailValues);
                        DataTable repAloneDetailValues = dbOp.getRepAloneAllDetailsValues("GOLD", null);
                        setRepAloneDetailValuesToField(repAloneDetailValues, "GOLD");
                        break;
                    case "All Details":
                        String sAllMaterialType = cbCompAloneMaterialType.getValue();
                        DataTable allCompDetailValues = dbOp.getAllCompAllDetailsValues(sAllMaterialType, null);
                        setCompAloneDetailValuesToField(allCompDetailValues);
                        if(sAllMaterialType.equals("BOTH")) {
                            sAllMaterialType = "GOLD";
                        }
                        DataTable allRepAloneDetailValues = dbOp.getAllRepAloneAllDetailsValues(sAllMaterialType, null);
                        setRepAloneDetailValuesToField(allRepAloneDetailValues, sAllMaterialType);
                        break;
                    default:
                        break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
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
                case "CB.OPENING_DATE":
                    sFilterScript += "AND " + alFilterDBColumnName.get(i) + " " + alFilterValue.get(i);
                    break;
                case "RB.OPENING_DATE":
                    sFilterScript += "AND " + alFilterDBColumnName.get(i) + " " + alFilterValue.get(i);
                    break;
                case "CB.AMOUNT":
                    sFilterScript += "AND " + alFilterDBColumnName.get(i) + " " + alFilterValue.get(i);
                    break;
                case "CB.CUSTOMER_NAME":
                    sFilterScript += " AND CB.CUSTOMER_NAME = " + alFilterValue.get(i) + " ";
                    break;
                case "RB.REPLEDGE_NAME":
                    sFilterScript += " AND RB.REPLEDGE_NAME = " + alFilterValue.get(i) + " ";
                    break;
                default:
                    sFilterScript += "AND " + alFilterDBColumnName.get(i) + "::TEXT LIKE ? ";
                    break;
            }
        }
        
        DataTable allDetailValues;
        try {
            String[] sValsArray = new String[alFilterValue.size()];
            String sMaterialType = cbCompAloneMaterialType.getValue();
            RadioButton selectedRadioButton = (RadioButton) rgStockGroup.getSelectedToggle(); 
            if(null != selectedRadioButton.getText()) switch (selectedRadioButton.getText()) {
                    case "Company Alone":
                        allDetailValues = dbOp.getCompAllDetailsValues(sMaterialType, sFilterScript, alFilterValue.toArray(sValsArray));
                        setCompAloneDetailValuesToField(allDetailValues);
                        break;
                    case "Repledge Alone":
                        DataTable repCompDetailValues = dbOp.getRepCompAllDetailsValues("GOLD", sFilterScript, alFilterValue.toArray(sValsArray));
                        setCompAloneDetailValuesToField(repCompDetailValues);
                        DataTable repAloneDetailValues = dbOp.getRepAloneAllDetailsValues("GOLD", sFilterScript, alFilterValue.toArray(sValsArray));
                        setRepAloneDetailValuesToField(repAloneDetailValues, "GOLD");
                        break;
                    case "All Details":
                        String sAllMaterialType = cbCompAloneMaterialType.getValue();
                        DataTable allCompDetailValues = dbOp.getAllCompAllDetailsValues(sAllMaterialType, sFilterScript, alFilterValue.toArray(sValsArray));
                        setCompAloneDetailValuesToField(allCompDetailValues);
                        if(sAllMaterialType.equals("BOTH")) {
                            sAllMaterialType = "GOLD";
                        }
                        DataTable allRepAloneDetailValues = dbOp.getAllRepAloneAllDetailsValues(sAllMaterialType, sFilterScript, alFilterValue.toArray(sValsArray));
                        setRepAloneDetailValuesToField(allRepAloneDetailValues, sAllMaterialType);
                        break;
                    default:
                        break;
            }            
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                   
    }

    @FXML
    private void btOpenInBillClosingClicked(ActionEvent event) {
    }

    @FXML
    private void tbReAllDetailsOnMouseClicked(MouseEvent event) {
        
        int index = tbRepBillDetails.getSelectionModel().getSelectedIndex();
        
        if(event.getClickCount() == 2 && (index >= 0) ) 
        {
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
            gon.closeBill(tbRepBillDetails.getItems().get(index).getSRepledgeBillId(), true);

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
            
        } else if(event.getClickCount() == 1) {
            tbRepBillDetails.getItems().get(index).setBChecked(!tbRepBillDetails.getItems().get(index).getBCheckedProperty());
            setRepHeaderValues("SELECTED");
        }        
    }

    private void calculateTotalAmtToBeRecieved() {
    
        double dTotIntrAmt = 0;  
        for(RepAllDetailsBean bean : tbRepBillDetails.getItems()) {
        }
        
    }
    
    private void selectOrDeSelectAllRep(TableView<RepAllDetailsBean> table, boolean toSelect) {
        table.getItems().stream().forEach((bean) -> {
            bean.setBChecked(toSelect);
        });
    }

    private void selectOrDeSelectAll(TableView<AllDetailsBean> table, boolean toSelect) {
        table.getItems().stream().forEach((bean) -> {
            bean.setBChecked(toSelect);
        });
    }
    
    @FXML
    private void btRepSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAllRep(tbRepBillDetails, true);
        setRepHeaderValues("SELECTED");
    }

    @FXML
    private void btRepDeSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAllRep(tbRepBillDetails, false);
        setRepHeaderValues("SELECTED");
    }

    @FXML
    private void btRepCalculateSelectedOnAction(ActionEvent event) {
        setRepHeaderValues("SELECTED");
    }

    @FXML
    private void btRepDeCalculateSelectedOnAction(ActionEvent event) {
        setRepHeaderValues("DESELECTED");
    }

    @FXML
    private void btRepCalculateAllSelectedOnAction(ActionEvent event) {
        setRepHeaderValues("ALL");
    }

    @FXML
    private void btCompSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAll(tbCompanyBillDetails, true);
        setHeaderValues("SELECTED");
    }

    @FXML
    private void btCompDeSelectAllOnAction(ActionEvent event) {
        selectOrDeSelectAll(tbCompanyBillDetails, false);
        setHeaderValues("SELECTED");
    }

    @FXML
    private void btCompCalculateSelectedOnAction(ActionEvent event) {
        setHeaderValues("SELECTED");
    }

    @FXML
    private void btCompDeCalculateSelectedOnAction(ActionEvent event) {
        setHeaderValues("DESELECTED");
    }

    @FXML
    private void btCompCalculateAllSelectedOnAction(ActionEvent event) {
        setHeaderValues("ALL");
    }

    @FXML
    private void btToBillClacOnAction(ActionEvent event) {
        
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
        gon.beforeRepOpen(tbCompanyBillDetails.getItems(), cbRepNames.getValue());

        dialog.setTitle("Bill Calculator");      
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
