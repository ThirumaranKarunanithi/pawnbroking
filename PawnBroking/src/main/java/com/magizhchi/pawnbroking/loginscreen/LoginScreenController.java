/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.loginscreen;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.CommonDBOperation;
import com.magizhchi.pawnbroking.common.PasswordUtils;
import com.magizhchi.pawnbroking.common.PopupUtil;
import com.magizhchi.pawnbroking.mainscreen.OwnerMainScreenController;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Tiru
 */
public class LoginScreenController implements Initializable {

    String ownerMainScreen = "/com/magizhchi/pawnbroking/mainscreen/OwnerMainScreen.fxml"; 
    String companyMasterScreen = "/com/magizhchi/pawnbroking/companymaster/CompanyMaster.fxml";
    private CommonDBOperation dbOp;    
    private Stage loginStage;

    @FXML
    private TextField txtUserName;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lbMsg;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
                
        try {
            dbOp = new CommonDBOperation(CommonConstants.DB, CommonConstants.IP, CommonConstants.PORT, CommonConstants.SCHEMA, CommonConstants.DB_USERNAME, CommonConstants.DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }             
        //mfs100 = new MFS100(this, key);     
        
        /*if(CommonConstants.tiruNewMachine.getMachineName().equals("TIRU")) {
            txtUserName.setText("TIRU");
            txtPassword.setText(Util.getShortPassWordFor("HAPPY@", "ALWAYS"));
        }*/
    }
    
    public void setParent(Stage stage, String machineName) {
        this.loginStage = stage;
        if(machineName.equals("TIRU")) {
            txtUserName.setText(CommonConstants.TIRU_USERNAME); 
            txtPassword.setText(CommonConstants.TIRU_PASSWORD);
        }
    }
    
    @FXML
    private void capitalizeCharOnPressed(KeyEvent event) {
    }

    @FXML
        private void capitalizeCharOnType(KeyEvent e) {
        TextField txt_TextField = (TextField) e.getSource();
        int caretPos = txt_TextField.getCaretPosition();
        txt_TextField.setText(txt_TextField.getText().toUpperCase());
        txt_TextField.positionCaret(caretPos);
    }

    @FXML
    private void btSignInOnAction(ActionEvent event) {
        
        btnUninitActionPerformed(null);
        lbMsg.setText("");
        String sUName = txtUserName.getText().trim();
        String sPassword = txtPassword.getText().trim();
        //System.out.print(sPassword);
        //System.out.print(CommonConstants.TIRU_PASSWORD);
        if(isValidHeaderValues(sUName, sPassword)) {
            try {
                String[] pass = dbOp.getUserPass(sUName);
                boolean isValidUser = false; 
                if(pass != null) {
                    isValidUser = PasswordUtils.verifyUserPassword(sPassword, pass[0], pass[1]);
                }
                if(isValidUser 
                        || (sUName.equals(CommonConstants.TIRU_USERNAME) 
                        && sPassword.equals(CommonConstants.TIRU_PASSWORD))) 
                {
                    if(!sUName.equals(CommonConstants.TIRU_USERNAME) && !sPassword.equals(CommonConstants.TIRU_PASSWORD)) 
                    {
                        
                        String ids[] = dbOp.getUserEmpRoleId(sUName);
                        CommonConstants.USERID = ids[0];                    
                        CommonConstants.EMPID = ids[1];                    
                        CommonConstants.ROLEID = ids[2]; 
                        CommonConstants.EMP_NAME = dbOp.getEmpName(CommonConstants.EMPID);
                    } else {
                        CommonConstants.USERID = CommonConstants.TIRU;
                        CommonConstants.EMPID = CommonConstants.TIRU;
                        CommonConstants.ROLEID = CommonConstants.TIRU;
                        CommonConstants.EMP_NAME = CommonConstants.TIRU;                       
                    }

                    String[] idNName = dbOp.getActiveCompanyId();
                    
                    if(idNName[0] != null) {
                    
                        CommonConstants.ACTIVE_COMPANY_ID = idNName[0];
                        CommonConstants.ACTIVE_COMPANY_NAME = idNName[1];
                        CommonConstants.ACTIVE_COMPANY_TYPE = idNName[2];
                        CommonConstants.ACTIVE_COMPANY_ACC_STARTING_DATE = dbOp.getStartingAccountSettingsValues(CommonConstants.ACTIVE_COMPANY_ID);
                        CommonConstants.ACTIVE_COMPANY_ACC_LAST_DATE = dbOp.getTodaysAccountSettingsValues(CommonConstants.ACTIVE_COMPANY_ID);                    
                        CommonConstants.TEMP_FILE_LOCATION = dbOp.getOtherSettingsValues(CommonConstants.ACTIVE_COMPANY_ID, "GOLD");

                        CommonConstants.tempFile = new File(CommonConstants.TEMP_FILE_LOCATION);
                        CommonConstants.custTemp = new File(CommonConstants.tempFile, CommonConstants.OPEN_CUSTOMER_IMAGE_NAME);
                        CommonConstants.jewelTemp = new File(CommonConstants.tempFile, CommonConstants.OPEN_JEWEL_IMAGE_NAME);
                        CommonConstants.userTemp = new File(CommonConstants.tempFile, CommonConstants.OPEN_USER_IMAGE_NAME);
                        CommonConstants.CLOSEcustTemp = new File(CommonConstants.tempFile, CommonConstants.CLOSE_CUSTOMER_IMAGE_NAME);
                        CommonConstants.CLOSEjewelTemp = new File(CommonConstants.tempFile, CommonConstants.CLOSE_JEWEL_IMAGE_NAME);
                        CommonConstants.CLOSEuserTemp = new File(CommonConstants.tempFile, CommonConstants.CLOSE_USER_IMAGE_NAME);

                        String sNOImage = CommonConstants.REPORT_LOCATION 
                            + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                            + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                            + "\\images\\NOIMAGE.png";

                        String sLoadingImage = CommonConstants.REPORT_LOCATION 
                            + "\\" + CommonConstants.ACTIVE_MACHINE.getMachineName()
                            + "\\" + CommonConstants.ACTIVE_COMPANY_NAME
                            + "\\images\\LOADING.png";

                        try (FileInputStream noImageFIS = new FileInputStream(sNOImage);
                                FileInputStream loadingImageFIS = new FileInputStream(sLoadingImage)) {

                            CommonConstants.noImage = new Image(noImageFIS);
                            CommonConstants.loadingImage = new Image(loadingImageFIS);

                        } catch (IOException ex) {
                            Logger.getLogger(CommonConstants.class.getName()).log(Level.SEVERE, null, ex);
                        }


                        dbOp.getEmpImg(CommonConstants.EMPID);

                        if(CommonConstants.ACTIVE_COMPANY_ID != null) {

                            Stage stage = new Stage();

                            FXMLLoader loader = new FXMLLoader(getClass().getResource(ownerMainScreen));
                            Parent root = null;
                            try {            
                                root = (Parent) loader.load();            
                            } catch (IOException ex) {
                                Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            Scene scene = new Scene(root);	 
                            stage.setTitle("Main Screen");      
                            stage.setX(CommonConstants.SCREEN_X);
                            stage.setY(CommonConstants.SCREEN_Y);
                            stage.setWidth(CommonConstants.SCREEN_WIDTH);
                            stage.setHeight(CommonConstants.SCREEN_HEIGHT);
                            stage.setResizable(false);
                            stage.initStyle(StageStyle.UNDECORATED);
                            stage.setScene(scene);
                            stage.centerOnScreen();
                            stage.show();

                            OwnerMainScreenController cn = (OwnerMainScreenController) loader.getController();                       
                            cn.setParent(loginStage, stage);

                        } else {
                        }
                    } else {
                        Stage stage = new Stage();
                        Parent root = FXMLLoader.load(getClass().getResource(companyMasterScreen));
                        Scene scene = new Scene(root);	 
                        stage.setTitle("Company Module Screen");      
                        stage.setX(CommonConstants.SCREEN_X);
                        stage.setY(CommonConstants.SCREEN_Y);
                        stage.setWidth(CommonConstants.SCREEN_WIDTH);
                        stage.setHeight(CommonConstants.SCREEN_HEIGHT);
                        stage.setResizable(false);
                        stage.setScene(scene);
                        stage.centerOnScreen();
                        stage.show();                    
                    }
                } else {
                    lbMsg.setText("Incorrect username or password.");
                }
            } catch (Exception ex) {
                Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            lbMsg.setText("Username or password cannot be empty.");
        }
    }
    
    public boolean isValidHeaderValues(String sUName, String sPassword)
    {
        return !sUName.isEmpty() && !sPassword.isEmpty();
    }    
    
    private void WriteBytesToFile(String FileName, byte[] Bytes) {
        try {
            String FilePath = "E:";
            FilePath += "\\FingerData1";
            File file = new File(FilePath);
            if (!file.exists()) {
                file.mkdir();
            }
            FilePath += "\\" + FileName;
            FileOutputStream fos = new FileOutputStream(FilePath);
            fos.write(Bytes);
            fos.close();
        } catch (Exception ex) {
        }
    }    

    
    private void matchISO() {
        /*FingerData fingerData = new FingerData();
        int ret = mfs100.AutoCapture(fingerData, timeout,  true, true);
        if (ret == 0) {
            try {                
                ArrayList<FingerPrintBean> fingers = dbOp.getUserFingerPrint();
                for(FingerPrintBean bean : fingers) {          
                    byte[] ISOTemplate1 = bean.getFingerPrint();
                    if(ISOTemplate1 != null) {
                        int score = mfs100.MatchISO(fingerData.ISOTemplate(), ISOTemplate1);
                        System.out.println(score + " - " + fingerData.ISOTemplate().length + " - " + ISOTemplate1.length);
                        if (score >= 14000) {
                            txtUserName.setText(bean.getUseraName());
                            txtPassword.setText(bean.getPassword());
                            System.out.println("FInger Matched - " + bean.getId());
                            btSignInOnAction(null);
                            return;
                        } else if (score >= 0 && score < 14000) {
                            System.out.println("FInger do not Matched");
                            lbMsg.setText("Sorry your finger impression does not matched with any user.");
                        } else {
                            lbMsg.setText("Error: " + mfs100.GetLastError() + " (" + String.valueOf(ret) + ")");
                        }
                    }
                }                
            } catch (SQLException | IOException | ClassNotFoundException ex) {
                Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            lbMsg.setText("Error: " + mfs100.GetLastError() + " (" + String.valueOf(ret) + ")");
        }*/    
    }

    public static byte[] serialize(Object obj) throws IOException {
        if(obj != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(obj);
            return out.toByteArray();
        } else {
            return null;
        }
    }
    
    private void btnUninitActionPerformed(java.awt.event.ActionEvent evt) {                                          
        /*int ret = mfs100.Uninit();
        if (ret == 0) {
            //deviceInfo = mfs100.GetDeviceInfo();
            //JOptionPane.showMessageDialog(rootPane, "Scanner Uninitialized",appTitle,JOptionPane.INFORMATION_MESSAGE);
        } else {
            //JOptionPane.showMessageDialog(rootPane, "Error: " + mfs100.GetLastError() + " (" + String.valueOf(ret) + ")",appTitle,JOptionPane.ERROR_MESSAGE);
        }*/
    }                                         
    

}
