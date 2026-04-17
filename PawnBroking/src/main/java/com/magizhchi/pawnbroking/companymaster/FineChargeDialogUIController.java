/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companymaster;

import java.awt.AWTException;
import java.awt.Robot;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class FineChargeDialogUIController implements Initializable {

    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private Label lbMsg;
    @FXML
    private ComboBox<String> cbBillMaterialType;
    @FXML
    private ComboBox<String> cbInterestType;
    @FXML
    private TextField txtFrom;
    @FXML
    private TextField txtTo;
    @FXML
    private TextField txtFineCharge;

    private CompanyMasterController parent;
    private boolean isDialogForAdd;
    @FXML
    private ComboBox<String> cbCalculationType;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
                dialogpanel.addEventFilter( KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>()
                                {
                                    Robot  eventRobot = new Robot();

                                    @Override
                                    public void handle( KeyEvent KV )
                                    {
                                        switch ( KV.getCode() )
                                        {
                                            case ENTER :
                                            {
                                                if ( (!(KV.getTarget() instanceof TextField)) || (!(KV.getTarget() instanceof Button)))
                                                {
                                                    eventRobot.keyPress( java.awt.event.KeyEvent.VK_TAB );
                                                    eventRobot.keyRelease( java.awt.event.KeyEvent.VK_TAB );
                                                    KV.consume();
                                                }
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
                                            case ESCAPE :
                                            {					               
                                                parent.dialog.close();
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

    @FXML
    private void onSaveClicked(ActionEvent event) {
        
        String sMaterial = cbBillMaterialType.getSelectionModel().getSelectedItem();
        String sInterestType = cbInterestType.getSelectionModel().getSelectedItem();        
        double dFrom = Double.parseDouble(txtFrom.getText());
        double dTo = Double.parseDouble(txtTo.getText());
        String sCalculationType = cbCalculationType.getSelectionModel().getSelectedItem();        
        double dFineCharge = Double.parseDouble(txtFineCharge.getText());
        int index = this.parent.tbOCFineCharges.getSelectionModel().getSelectedIndex();
        
        if(isDialogForAdd) {
            this.parent.tbOCFineCharges.getItems().add(new FineChargeBean(sMaterial, sInterestType, dFrom, dTo, sCalculationType, dFineCharge));                    
        } else {
            this.parent.tbOCFineCharges.getItems().set(index, new FineChargeBean(sMaterial, sInterestType, dFrom, dTo, sCalculationType, dFineCharge));                    
        }
        this.parent.dialog.close();                
    }


    @FXML
    private void allowDotWithNumberOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();                      
        if(!("0123456789.".contains(e.getCharacter()))){ 
            e.consume();
        }        
    }
    
    public void setParent(CompanyMasterController parent, boolean isDialogForAdd)
    {
        this.parent = parent;
        this.isDialogForAdd = isDialogForAdd;
    }
    
    public void setInitValues() {
    
        if(!isDialogForAdd)
        {
            int index = this.parent.tbOCFineCharges.getSelectionModel().getSelectedIndex();
            FineChargeBean bean = this.parent.tbOCFineCharges.getItems().get(index);
            cbBillMaterialType.setValue(bean.getSFromDate());
            cbInterestType.setValue(bean.getSToDate());            
            txtFrom.setText(Double.toString(bean.getDFrom()));
            txtTo.setText(Double.toString(bean.getDTo()));
            cbCalculationType.setValue(bean.getSCalculationMethod());
            txtFineCharge.setText(Double.toString(bean.getDFineCharge()));
        }
    }
    
}
