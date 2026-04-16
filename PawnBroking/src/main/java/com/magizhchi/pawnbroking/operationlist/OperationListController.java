/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.operationlist;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.PopupUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class OperationListController implements Initializable {

    public ItemMasterDBOperation dbOp;
    public Stage dialog;
    
    @FXML
    private TabPane tpScreen;
    @FXML
    public TableView<ItemsBean> tbList;
    @FXML
    private Button btAddGBOInterest11;
    @FXML
    private Button btEditGBOInterest11;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            dbOp = new ItemMasterDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OperationListController.class.getName()).log(Level.SEVERE, null, ex);
        }     
        
        setJewelItemValues(CommonConstants.CURRENT_OPERATION);
    }    
    
    public void setJewelItemValues(String sMaterialType) {
        
        try {
            DataTable jewelItemValues = dbOp.getAllJewelItems(sMaterialType);                        
            tbList.getItems().removeAll(tbList.getItems());             
            for(int i=0; i<jewelItemValues.getRowCount(); i++) {            
                String sItem = jewelItemValues.getRow(i).getColumn(0).toString();
                String sStatus = jewelItemValues.getRow(i).getColumn(1).toString();
                tbList.getItems().add(new ItemsBean(sItem, sStatus));                
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(OperationListController.class.getName()).log(Level.SEVERE, null, ex);
        }                        
    }
    
    @FXML
    private void btAddGBOInterestClicked(ActionEvent event) {
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemMasterDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        ItemMasterDialogUIController gon = (ItemMasterDialogUIController) loader.getController();
        gon.setParent(this, true);
        gon.setInitValues();
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();        
    }

    @FXML
    private void btEditGBOInterestClicked(ActionEvent event) {        
        int index = tbList.getSelectionModel().getSelectedIndex();
        if(index >= 0) 
        {
            dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemMasterDialog.fxml"));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            ItemMasterDialogUIController gon = (ItemMasterDialogUIController) loader.getController();
            gon.setParent(this, false);
            gon.setInitValues();
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
            dialog.showAndWait();
        } else {
            PopupUtil.showInfoAlert("Any of a row in a table should be selected.");
        }                
    }
    
}
