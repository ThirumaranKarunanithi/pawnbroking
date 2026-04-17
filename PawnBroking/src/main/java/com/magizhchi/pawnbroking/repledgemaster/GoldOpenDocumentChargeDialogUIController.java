/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.repledgemaster;

import com.magizhchi.pawnbroking.common.CommonConstants;
import java.awt.AWTException;
import java.awt.Robot;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class GoldOpenDocumentChargeDialogUIController implements Initializable {

    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private Label lbMsg;
    @FXML
    private TextField txtFrom;
    @FXML
    private TextField txtTo;
    @FXML
    private TextField txtDocumentCharge;

    private RePledgeMasterController parent;
    private boolean isDialogForAdd;
    @FXML
    private DatePicker dpFromDate;
    @FXML
    private DatePicker dpToDate;

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
        
        String sFromDate = CommonConstants.DATETIMEFORMATTER.format(dpFromDate.getValue());
        String sToDate = CommonConstants.DATETIMEFORMATTER.format(dpToDate.getValue());        
        double dFrom = Double.parseDouble(txtFrom.getText());
        double dTo = Double.parseDouble(txtTo.getText());
        double dDocumentCharge = Double.parseDouble(txtDocumentCharge.getText());
        int index = this.parent.tbGBODocumentCharge.getSelectionModel().getSelectedIndex();
        
        if(isDialogForAdd) {
            this.parent.tbGBODocumentCharge.getItems().add(new DocumentChargeBean(sFromDate, sToDate, dFrom, dTo, dDocumentCharge));                    
        } else {
            this.parent.tbGBODocumentCharge.getItems().set(index, new DocumentChargeBean(sFromDate, sToDate, dFrom, dTo, dDocumentCharge));                    
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

    @FXML
    private void allowNumberOnlyOnType(KeyEvent e) {
        
        TextField txt_TextField = (TextField) e.getSource();                      
        if(!e.getCharacter().matches("[0-9]")){ 
            e.consume();
        }
    }

    public void setParent(RePledgeMasterController parent, boolean isDialogForAdd)
    {
        this.parent = parent;
        this.isDialogForAdd = isDialogForAdd;
    }
    
    public void setInitValues() {
    
        if(!isDialogForAdd)
        {
            int index = this.parent.tbGBODocumentCharge.getSelectionModel().getSelectedIndex();
            DocumentChargeBean bean = this.parent.tbGBODocumentCharge.getItems().get(index);
            dpFromDate.setValue(LocalDate.parse(bean.getSFromDate(), CommonConstants.DATETIMEFORMATTER));
            dpToDate.setValue(LocalDate.parse(bean.getSToDate(), CommonConstants.DATETIMEFORMATTER));            
            txtFrom.setText(Double.toString(bean.getDFrom()));
            txtTo.setText(Double.toString(bean.getDTo()));
            txtDocumentCharge.setText(Double.toString(bean.getDDocumentCharge()));
        }
    }
}
