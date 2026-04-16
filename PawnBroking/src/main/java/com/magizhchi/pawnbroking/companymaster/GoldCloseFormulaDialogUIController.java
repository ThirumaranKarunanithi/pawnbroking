/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companymaster;

import com.magizhchi.pawnbroking.common.CommonConstants;
import java.awt.AWTException;
import java.awt.Robot;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
public class GoldCloseFormulaDialogUIController implements Initializable {

    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private Label alert;
    @FXML
    private Button bSave;
    @FXML
    private TextField txtFrom;
    @FXML
    private TextField txtTo;
    @FXML
    private TextField txtFormula;
    @FXML
    private Button bErase;
    @FXML
    private ComboBox<?> cbSymbols;
    @FXML
    private ComboBox<?> cbOthers;
    @FXML
    private TextField txtValues;
    
    private CompanyMasterController parent;
    private boolean isDialogForAdd;
    private ArrayList<String> lastSelectedText = new ArrayList<String>();
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
                                                if (((!(KV.getTarget() instanceof TextField)) || (!(KV.getTarget() instanceof Button))) && (!(KV.getTarget().equals(txtValues))))
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
        String sFormula = txtFormula.getText();
        int index = this.parent.tbGBCFormula.getSelectionModel().getSelectedIndex();
        
        if(isDialogForAdd) {
            this.parent.tbGBCFormula.getItems().add(new FormulaBean(sFromDate, sToDate, dFrom, dTo, sFormula));                    
        } else {
            this.parent.tbGBCFormula.getItems().set(index, new FormulaBean(sFromDate, sToDate, dFrom, dTo, sFormula));                    
        }
        this.parent.dialog.close();  
    }

    @FXML
    private void bEraseOnActionListener(ActionEvent event) {
        
        if(lastSelectedText.size() > 0){
            lastSelectedText.remove(lastSelectedText.size()-1);
            txtFormula.setText(getFullLastSelectedText());						
        }else{
            txtFormula.setText("");
        }
    }

    @FXML
    private void valuesOnActionListener(ActionEvent event) {
        
        lastSelectedText.add(txtValues.getText().trim());
        txtFormula.setText(getFullLastSelectedText());
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
    
    public void setInitValues() {
    
        if(!isDialogForAdd)
        {
            int index = this.parent.tbGBCFormula.getSelectionModel().getSelectedIndex();
            FormulaBean bean = this.parent.tbGBCFormula.getItems().get(index);
            if(bean.getSFromDate() != null && !bean.getSFromDate().equals("")) {
                dpFromDate.setValue(LocalDate.parse(bean.getSFromDate(), CommonConstants.DATETIMEFORMATTER));
            }
            if(bean.getSToDate() != null && !bean.getSToDate().equals("")) {
                dpToDate.setValue(LocalDate.parse(bean.getSToDate(), CommonConstants.DATETIMEFORMATTER));                        
            }
            txtFrom.setText(Double.toString(bean.getDFrom()));
            txtTo.setText(Double.toString(bean.getDTo()));
            txtFormula.setText(bean.getSFormula());
        }
    }
    
    @FXML
    private void cbSymbolsOnAction(ActionEvent event) {
        
        int sIndex = cbSymbols.getSelectionModel().getSelectedIndex();
        lastSelectedText.add(cbSymbols.getItems().get(sIndex).toString());
        txtFormula.setText(getFullLastSelectedText());
    }

    @FXML
    private void cbOthersOnAction(ActionEvent event) {

        int sIndex = cbOthers.getSelectionModel().getSelectedIndex();
        lastSelectedText.add(cbOthers.getItems().get(sIndex).toString());
        txtFormula.setText(getFullLastSelectedText());        
    }

    public String getFullLastSelectedText()
    {
        String fullText = "";
        for (String text : lastSelectedText) {
                fullText += text;
        }
        return fullText;
    }
}
