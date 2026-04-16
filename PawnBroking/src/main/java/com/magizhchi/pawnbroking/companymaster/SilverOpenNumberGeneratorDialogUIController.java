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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class SilverOpenNumberGeneratorDialogUIController implements Initializable {

    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private Label lbMsg;
    @FXML
    private Button btSave;
    @FXML
    private TextField txtPrefix;
    @FXML
    private TextField txtFrom;
    @FXML
    private TextField txtTo;
    
    private CompanyMasterController parent;
    private boolean isDialogForAdd;
    
    
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
        
        txtPrefix.requestFocus();                   
    }    

    public void setInitValues() {
    
        if(!isDialogForAdd)
        {
            int index = this.parent.tbSBONumberGenerator.getSelectionModel().getSelectedIndex();
            NumberGeneratorBean bean = this.parent.tbSBONumberGenerator.getItems().get(index);
            txtPrefix.setText(bean.getSPrefix());
            txtFrom.setText(Long.toString(bean.getLFrom()));
            txtTo.setText(Long.toString(bean.getLTo()));
        }
    }
    
    @FXML
    private void onSaveClicked(ActionEvent event) {
        
        String sPrefix = txtPrefix.getText();
        long lFrom = Long.parseLong(txtFrom.getText().trim());
        long lTo = Long.parseLong(txtTo.getText().trim());       
        int index = this.parent.tbSBONumberGenerator.getSelectionModel().getSelectedIndex();
        if(this.isDialogForAdd) {
            this.parent.tbSBONumberGenerator.getItems().add(new NumberGeneratorBean(this.parent.tbSBONumberGenerator.getItems().size() + 1, sPrefix, lFrom, lTo));        
        } else {
            this.parent.tbSBONumberGenerator.getItems().set(index, new NumberGeneratorBean(index + 1, sPrefix, lFrom, lTo));        
        }
        this.parent.dialog.close();
    }

    @FXML
        private void capitalizeCharOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();
        int caretPos = txt_TextField.getCaretPosition();
        txt_TextField.setText(txt_TextField.getText().toUpperCase());
        txt_TextField.positionCaret(caretPos);
    }

    @FXML
    private void allowNumberOnlyOnType(KeyEvent e) {
        
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
}
