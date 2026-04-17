/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.reports;

import com.magizhchi.pawnbroking.common.DataTable;
import com.magizhchi.pawnbroking.common.Util;
import com.magizhchi.pawnbroking.companybillopening.GoldBillOpeningController;
import com.magizhchi.pawnbroking.repledgebillclosing.RepledgeGoldBillClosingController;
import com.magizhchi.pawnbroking.repledgebillopening.RepledgeGoldBillOpeningController;
import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class RepledgeBillOpeningDialogUIController implements Initializable {

    private TrialBalanceController parent;
    private boolean isGoldOperation;
    public Stage dialog;
    
    final String reGoldBillOpeningScreen = "/com/magizhchi/pawnbroking/repledgebillopening/RepledgeGoldBillOpening.fxml";
    final String reGoldBillClosingScreen = "/com/magizhchi/pawnbroking/repledgebillclosing/RepledgeGoldBillClosing.fxml";
    
    @FXML
    private AnchorPane dialogpanel;
    @FXML
    private TableView<RepledgeBillOpeningAllDetailsBean> tbTodaysAccount;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        tbTodaysAccount.setRowFactory(tv -> new TableRow<RepledgeBillOpeningAllDetailsBean>() {
            @Override
            public void updateItem(RepledgeBillOpeningAllDetailsBean item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle("");
                } else if (Double.parseDouble(item.getSToGive()) != Double.parseDouble(item.getSGiven())) {
                    setStyle(Util.getStyle("#000000", "#6FB7FF").toString());    
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
        
        int index = tbTodaysAccount.getSelectionModel().getSelectedIndex();
        
        if(event.getClickCount() == 2 && (index >= 0) ) 
        {
            String sStatus = tbTodaysAccount.getItems().get(index).getSStatus();
            if("OPENED".equals(sStatus) || "GIVEN".equals(sStatus)) 
            {
                if(isGoldOperation) {

                    dialog = new Stage();
                    dialog.initModality(Modality.WINDOW_MODAL);        

                    FXMLLoader loader = new FXMLLoader(getClass().getResource(reGoldBillOpeningScreen));
                    Parent root = null;
                    try {            
                        root = (Parent) loader.load();            
                    } catch (IOException ex) {
                        Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    RepledgeGoldBillOpeningController gon = (RepledgeGoldBillOpeningController) loader.getController();
                    gon.viewBill(tbTodaysAccount.getItems().get(index).getSRepledgeBillId());

                    dialog.setTitle("Repledge Bill Opening");      
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
            } else 
            {
                if(isGoldOperation) {

                    dialog = new Stage();
                    dialog.initModality(Modality.WINDOW_MODAL);        

                    FXMLLoader loader = new FXMLLoader(getClass().getResource(reGoldBillClosingScreen));
                    Parent root = null;
                    try {            
                        root = (Parent) loader.load();            
                    } catch (IOException ex) {
                        Logger.getLogger(GoldBillOpeningController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    RepledgeGoldBillClosingController gon = (RepledgeGoldBillClosingController) loader.getController();
                    gon.viewBill(tbTodaysAccount.getItems().get(index).getSRepledgeBillId());

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
        }
    }
    
    public void setParent(TrialBalanceController parent, boolean isGoldOperation)
    {
        this.parent = parent;
        this.isGoldOperation = isGoldOperation;
    }
    
    public void setInitValues(DataTable values) {
    
        tbTodaysAccount.getItems().remove(0, tbTodaysAccount.getItems().size());
        for(int i=0; i<values.getRowCount(); i++) {            
            String sSlNo = values.getRow(i).getColumn(0).toString();
            String sRepledgeBillId = values.getRow(i).getColumn(1).toString();
            String sDate = values.getRow(i).getColumn(2).toString();
            String sStatus = values.getRow(i).getColumn(3).toString();
            String sRepledgeName = values.getRow(i).getColumn(4).toString();
            String sRepledgeBillNumber = values.getRow(i).getColumn(5).toString();
            String sBillNumber = values.getRow(i).getColumn(6).toString();
            String sAmount = values.getRow(i).getColumn(7).toString();
            String sToGive = values.getRow(i).getColumn(8).toString();
            String sGiven = values.getRow(i).getColumn(9).toString();
            String sCreatedUser = values.getRow(i).getColumn(10).toString();
            String sInterest = values.getRow(i).getColumn(11).toString();
            tbTodaysAccount.getItems().add(new RepledgeBillOpeningAllDetailsBean(sSlNo, sRepledgeBillId, sDate, 
                    sStatus, sRepledgeName, sRepledgeBillNumber, sBillNumber, 
                    sAmount, sInterest, sToGive, sGiven, sCreatedUser));
        }
    }
    
}
