/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.account;

import com.magizhchi.pawnbroking.common.DataTable;
import java.awt.AWTException;
import java.awt.Robot;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class IncomeDialogUIController implements Initializable {

    private TodaysAccountController parent;
    public Stage dialog;
    
    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private TableView<ExpenseAllDetailsBean> tbTodaysAccount;

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
    private void tbAllDetailsOnMouseClicked(MouseEvent event) {
    }

    public void setParent(TodaysAccountController parent)
    {
        this.parent = parent;
    }
    
    public void setInitValues(DataTable values) {
    
        tbTodaysAccount.getItems().remove(0, tbTodaysAccount.getItems().size());
        for(int i=0; i<values.getRowCount(); i++) {            
            String sSlNo = Integer.toString(i+1);
            String sDate = values.getRow(i).getColumn(0).toString();
            String sExpenseId = values.getRow(i).getColumn(1).toString();
            String sScreenName = values.getRow(i).getColumn(2).toString();
            String sReason = values.getRow(i).getColumn(3).toString();
            String sAmount = values.getRow(i).getColumn(4).toString();
            String sCreatedUser = values.getRow(i).getColumn(5).toString();
            tbTodaysAccount.getItems().add(new ExpenseAllDetailsBean(sSlNo, sDate, sExpenseId, sScreenName, sReason, sAmount, sCreatedUser));
            
        }
    }
    
}
