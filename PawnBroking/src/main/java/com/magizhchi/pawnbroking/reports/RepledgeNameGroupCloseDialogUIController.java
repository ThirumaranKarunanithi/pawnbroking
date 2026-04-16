/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.reports;

import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.Util;
import java.awt.AWTException;
import java.awt.Robot;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tiruk
 */
public class RepledgeNameGroupCloseDialogUIController implements Initializable {

    private TrialBalanceController parent;
    private boolean isGoldOperation;
    public Stage dialog;
    
    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private TableView<RepledgeNameGroup> tbTodaysAccount;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        tbTodaysAccount.setRowFactory(tv -> new TableRow<RepledgeNameGroup>() {
            @Override
            public void updateItem(RepledgeNameGroup item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle("");
                } else if (item.getCloseCapitalAmount() > 1000000) {
                    setStyle(Util.getStyle("#000000", "#F35415")
                            .toString());    
                } else {
                    setStyle("");
                }
            }
        });
        
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
    
    public void setParent(TrialBalanceController parent, 
            boolean isGoldOperation)
    {
        this.parent = parent;
        this.isGoldOperation = isGoldOperation;
    }
    
    public void setInitValues(DataTable values) {
    
        tbTodaysAccount.getItems().remove(0, tbTodaysAccount.getItems().size());
        for(int i=0; i<values.getRowCount(); i++) {            
            String sSlNo = String.valueOf(i+1);
            String sRepledgeName = values.getRow(i).getColumn(0).toString();
            String sCount = values.getRow(i).getColumn(1).toString();
            String sCloseCapitalAmount = values.getRow(i).getColumn(2).toString();
            String sInterestGivenAmount = values.getRow(i).getColumn(3).toString();
            String sGivenAmount = values.getRow(i).getColumn(4).toString();
            tbTodaysAccount.getItems().add(
                    new RepledgeNameGroup(
                            Integer.parseInt(sSlNo), 
                            sRepledgeName, 
                            Integer.parseInt(sCount), 
                            Double.parseDouble(sCloseCapitalAmount), 
                            Double.parseDouble(sInterestGivenAmount),
                    Double.parseDouble(sGivenAmount)));
        }
    }
    
}
