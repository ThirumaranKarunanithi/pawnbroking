/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.credit;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.CommonDBOperation;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class CreditPanelController implements Initializable {

    String employeeCreditScreen = "EmployeeCredits.fxml";
    String companyCreditScreen = "CompanyCredits.fxml";
    String repledgeCreditScreen = "RepledgeCredits.fxml";
    private CommonDBOperation dbOp; 
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            dbOp = new CommonDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CreditPanelController.class.getName()).log(Level.SEVERE, null, ex);
        }                
        
    }    

    @FXML
    private void btEmployeeIncomeOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.EMPLOYEE_INCOME_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(CreditPanelController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {        
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(employeeCreditScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(CreditPanelController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Employee Income - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow());
            dialog.showAndWait();      
        } else {
            PopupUtil.showInfoAlert(event, "Sorry you do not have any permission in this sceen. ");
        }
    }

    @FXML
    private void btCompanyIncomeOnAction(ActionEvent event) {
        
        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.COMPANY_INCOME_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(CreditPanelController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {        
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(companyCreditScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(CreditPanelController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Company Income - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow());
            dialog.showAndWait();        
        } else {
            PopupUtil.showInfoAlert(event, "Sorry you do not have any permission in this sceen. ");
        }
    }

    @FXML
    private void btRepledgeIncomeOnAction(ActionEvent event) {

        boolean allow = false;
        if(!CommonConstants.ROLEID.equals(CommonConstants.TIRU)) {
            try {
                boolean[] avu = dbOp.getAddViewUpdate(CommonConstants.ROLEID, CommonConstants.OPERATION_TAB, CommonConstants.REPLEDGE_INCOME_SCREEN);
                if(avu[0] || avu[1] || avu[2]) {
                    allow = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(CreditPanelController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            allow = true;
        }
        
        if(allow) {        
        
            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);        

            FXMLLoader loader = new FXMLLoader(getClass().getResource(repledgeCreditScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(CreditPanelController.class.getName()).log(Level.SEVERE, null, ex);
            }

            dialog.setTitle("Repledge Income - " + CommonConstants.ACTIVE_COMPANY_NAME);      
            dialog.setX(CommonConstants.SCREEN_X);
            dialog.setY(CommonConstants.SCREEN_Y + 20);
            dialog.setWidth(CommonConstants.SCREEN_WIDTH);
            dialog.setHeight(CommonConstants.SCREEN_HEIGHT);
            dialog.setResizable(false);
            Scene scene = new Scene(root);        
            dialog.setScene(scene);
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow());
            dialog.showAndWait();   
        } else {
            PopupUtil.showInfoAlert(event, "Sorry you do not have any permission in this sceen. ");
        }
    }
    
}
