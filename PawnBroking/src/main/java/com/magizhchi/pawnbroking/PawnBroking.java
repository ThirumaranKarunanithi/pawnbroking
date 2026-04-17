package com.magizhchi.pawnbroking;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DateRelatedCalculations;
import com.magizhchi.pawnbroking.common.MachineDetails;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.common.Util;
import com.magizhchi.pawnbroking.loginscreen.LoginScreenController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Tiru
 */
public class PawnBroking extends Application {
    
    String loginScreen = "/com/magizhchi/pawnbroking/loginscreen/LoginScreen.fxml";         
    
    
    @Override
    public void start(Stage stage) throws Exception {
        
        boolean allow = false;
        String machineName = null;
        
        for(MachineDetails machine : CommonConstants.softwareOnMachines) {
            if(machine.getMacAddr().equals(Util.getMACAddress())
                    && DateRelatedCalculations.isDateWithInYear(machine.getSoftwareInstalledDate())) 
            {
                allow = true;
                CommonConstants.ACTIVE_MACHINE = machine;
                machineName = machine.getMachineName();
            }            
        }
                        
        if(allow) 
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(loginScreen));
            Parent root = null;
            try {            
                root = (Parent) loader.load();            
            } catch (IOException ex) {
                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
            Scene scene = new Scene(root);	 
            stage.setTitle("Pawnbroking Login Screen");      
            stage.setX(CommonConstants.SCREEN_X);
            stage.setY(CommonConstants.SCREEN_Y);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
            
            LoginScreenController lcn = (LoginScreenController) loader.getController();
            lcn.setParent(stage, machineName);
        } else {
            PopupUtil.showInfoAlert("Your mac address is "+ Util.getMACAddress() +". Sorry you are pirating the software.");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);        
    }
}
