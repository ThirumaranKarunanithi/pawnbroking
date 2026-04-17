/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.rebillmapper;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.companybillclosing.GoldBillClosingController;
import com.magizhchi.pawnbroking.companymaster.CompanyMasterController;
import com.magizhchi.pawnbroking.repledgebillclosing.RepledgeGoldBillClosingController;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
public class RebilledAfterRepledgeController implements Initializable {

    ReBillMapperDBOperation dbOp;
    public Stage dialog;
    final String goldBillClosingScreen = "/com/magizhchi/pawnbroking/companybillclosing/GoldBillClosing.fxml";
    final String reGoldBillClosingScreen = "/com/magizhchi/pawnbroking/repledgebillclosing/RepledgeGoldBillClosing.fxml";
    private final ArrayList<String> alFilterDBColumnName = new ArrayList<>();
    private final ArrayList<String> alFilterName = new ArrayList<>();
    private final ArrayList<String> alFilterValue = new ArrayList<>();
    
    @FXML
    private TabPane tpCompScreen;
    @FXML
    private TableView<RebilledAfterRepDetails> tbCompanyBillTableView;
    @FXML
    private HBox nodeAddToFilter;
    @FXML
    private ComboBox<String> cbAllDetailsFilter;
    @FXML
    private Label lbFrom;
    @FXML
    private DatePicker dpFrom;
    @FXML
    private TextField txtFrom;
    @FXML
    private Label lbTo;
    @FXML
    private DatePicker dpTo;
    @FXML
    private TextField txtTo;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            try {
                dbOp = new ReBillMapperDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SilverRebillMapperController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            DataTable companyMISValues = dbOp.getRebilledRepValues(null);
            setTableValues(companyMISValues);
            
            nodeAddToFilter.getChildren().remove(txtFrom);
            nodeAddToFilter.getChildren().remove(txtTo);
            nodeAddToFilter.getChildren().remove(cbAddToFilter);
            nodeAddToFilter.getChildren().remove(txtAddToFilter);
        } catch (SQLException ex) {
            Logger.getLogger(RebilledAfterRepledgeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    private void setTableValues(DataTable companyMISValues) {   
        
        tbCompanyBillTableView.getItems().removeAll(tbCompanyBillTableView.getItems());
        for(int i=0; i<companyMISValues.getRowCount(); i++) {
            String sOldCompBillNumber = companyMISValues.getRow(i).getColumn(0).toString();
            String sRepAmt = companyMISValues.getRow(i).getColumn(1).toString();
            String sNewCompBillNumber = companyMISValues.getRow(i).getColumn(2).toString();
            String sCompAmt = companyMISValues.getRow(i).getColumn(3).toString();
            String sRepOpenedDate = companyMISValues.getRow(i).getColumn(4).toString();
            String sTotAdvAmt = companyMISValues.getRow(i).getColumn(5).toString();
            String sRepName = companyMISValues.getRow(i).getColumn(6).toString();
            String sRepBillNo = companyMISValues.getRow(i).getColumn(7).toString();
            double dRatePerGm = Double.parseDouble(companyMISValues.getRow(i).getColumn(8).toString());
            String sRatePerGm = String.valueOf(Math.round(dRatePerGm));
            String sGrossWt = companyMISValues.getRow(i).getColumn(9).toString();
            String sItems = companyMISValues.getRow(i).getColumn(10).toString();
            String sRepBillId = companyMISValues.getRow(i).getColumn(11).toString();
            
            tbCompanyBillTableView.getItems().add(new RebilledAfterRepDetails(sOldCompBillNumber, sRepAmt,
                    sNewCompBillNumber, sCompAmt, sRepOpenedDate, sTotAdvAmt, sRepName, sRepBillNo, sRatePerGm,
                    sGrossWt, sItems, sRepBillId));
        }
    }
    
    @FXML
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {
        
        int index = tbCompanyBillTableView.getSelectionModel().getSelectedIndex();
        
        if(event.getClickCount() == 2 && (index >= 0) ) 
        {
            if(dialog != null) {
                dialog = null;
            }
            
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(reGoldBillClosingScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(RebilledAfterRepledgeController.class.getName()).log(Level.SEVERE, null, ex);
            }

            RepledgeGoldBillClosingController gon = (RepledgeGoldBillClosingController) loader.getController();
            gon.closeBill(tbCompanyBillTableView.getItems().get(index).getSRepBillId(), true);

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
                case "REPLEDGE BILL AMOUNT":
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
                        Logger.getLogger(RebilledAfterRepledgeController.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                    break;
                case "SHOW ONLY":
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
                    cbAddToFilter.getItems().removeAll(cbAddToFilter.getItems());
                    cbAddToFilter.getItems().add("DIFFERENT IN AMOUNT");
                    cbAddToFilter.getItems().add("SAME IN AMOUNT");
                    cbAddToFilter.getItems().add("NEWLY REDUCED AMOUNT");
                    cbAddToFilter.getItems().add("NEWLY INCREASED AMOUNT");
                    cbAddToFilter.getSelectionModel().select(0);
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
        private void capitalizeCharOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();
        int caretPos = txt_TextField.getCaretPosition();
        txt_TextField.setText(txt_TextField.getText().toUpperCase());
        txt_TextField.positionCaret(caretPos);
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
            } else if(nodeAddToFilter.getChildren().contains(dpFrom)) {
                sFilterValue = "BETWEEN " 
                        + "'" + CommonConstants.DBDATETIMEFORMATTER.format(dpFrom.getValue()) + "'"
                        + " AND "
                        + "'" + CommonConstants.DBDATETIMEFORMATTER.format(dpTo.getValue()) + "'";
            } else if(nodeAddToFilter.getChildren().contains(txtFrom)) {
                sFilterValue = "BETWEEN " 
                        + "'" + txtFrom.getText() + "'"
                        + " AND "
                        + "'" + txtTo.getText() + "'";
            } else if(nodeAddToFilter.getChildren().contains(cbAddToFilter) 
                    && cbAllDetailsFilter.getSelectionModel().getSelectedItem().equals("REPLEDGE NAME")) {                
                sFilterValue = createScriptForRepledgeName();
            } else if(nodeAddToFilter.getChildren().contains(cbAddToFilter) 
                    && cbAllDetailsFilter.getSelectionModel().getSelectedItem().equals("SHOW ONLY")) {                
                sFilterValue = createScriptForShowOnly();
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

    public String createScriptForRepledgeName() {
        int sIndex = cbAddToFilter.getSelectionModel().getSelectedIndex();
        String script = null;
        if(sIndex >=0) {
            script = " '" + cbAddToFilter.getSelectionModel().getSelectedItem() + "'";
        }        
        return script;
    }

    public String createScriptForShowOnly() {
        int sIndex = cbAddToFilter.getSelectionModel().getSelectedIndex();
        String script = null;
        if(sIndex >=0) {
            script = " '" + cbAddToFilter.getSelectionModel().getSelectedItem() + "'";
        }        
        return script;
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
            case "REPLEDGE BILL AMOUNT":
                return "RB.AMOUNT";
            case "REPLEDGE NAME":
                return "RB.REPLEDGE_NAME";                
            case "SHOW ONLY":
                return "NOT IN";                
            default:
                return null;
        }
    }
        
    @FXML
    private void showAllRecordsClicked(ActionEvent event) {        
        try {
            DataTable companyMISValues = dbOp.getRebilledRepValues(null);        
            setTableValues(companyMISValues);
        } catch (SQLException ex) {
            Logger.getLogger(RebilledAfterRepledgeController.class.getName()).log(Level.SEVERE, null, ex);
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
                case "RB.AMOUNT":
                    sFilterScript += "AND " + alFilterDBColumnName.get(i) + " " + alFilterValue.get(i);
                    break;
                case "RB.REPLEDGE_NAME":
                    sFilterScript += " AND RB.REPLEDGE_NAME = " + alFilterValue.get(i) + " ";
                    break;                    
                case "NOT IN":                    
                    if(alFilterValue.get(i).equals(" 'DIFFERENT IN AMOUNT'")) {
                        sFilterScript += " AND RB.AMOUNT NOT IN (CB.AMOUNT) ";
                    } else if(alFilterValue.get(i).equals(" 'SAME IN AMOUNT'")) {
                        sFilterScript += " AND RB.AMOUNT = CB.AMOUNT ";
                    } else if(alFilterValue.get(i).equals(" 'NEWLY REDUCED AMOUNT'")) {
                        sFilterScript += " AND RB.AMOUNT > CB.AMOUNT ";
                    } else if(alFilterValue.get(i).equals(" 'NEWLY INCREASED AMOUNT'")) {
                        sFilterScript += " AND RB.AMOUNT < CB.AMOUNT ";
                    }
                    break;                    
                default:
                    sFilterScript += "AND " + alFilterDBColumnName.get(i) + "::TEXT LIKE ? ";
                    break;
            }
        }
        
        DataTable allDetailValues;
        try {
            String[] sValsArray = new String[alFilterValue.size()];
            DataTable allCompDetailValues = dbOp.getRebilledRepValues(sFilterScript, alFilterValue.toArray(sValsArray));
            setTableValues(allCompDetailValues);
        } catch (SQLException ex) {
            Logger.getLogger(CompanyMasterController.class.getName()).log(Level.SEVERE, null, ex);
        }                   
        
    }

    @FXML
    private void btOpenInBillClosingClicked(ActionEvent event) {
        
            
            if(dialog != null) {
                dialog = null;
            }
            
            int index = tbCompanyBillTableView.getSelectionModel().getSelectedIndex();
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(goldBillClosingScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(RebilledAfterRepledgeController.class.getName()).log(Level.SEVERE, null, ex);
            }

            GoldBillClosingController gon = (GoldBillClosingController) loader.getController();
            gon.closeBill(tbCompanyBillTableView.getItems().get(index).getSNewCompBillNumber(), false);

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
