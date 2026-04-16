/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.common;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Tiru
 */
public class PopupUtil {
    
    public static Stage dialog;
    public static boolean isRightPassord = false;
    
    public static void showErrorAlert(String msg)
    {        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(PopupUtil.class.getResource("/com/magizhchi/pawnbroking/common/ErrorAlert.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        ErrorAlertUIController gon = (ErrorAlertUIController) loader.getController();
        gon.setMessage(msg);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.setTitle("Error Message");
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.UTILITY);        
        dialog.showAndWait();
    }

    public static void showErrorAlert(ActionEvent event, String msg)
    {        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(PopupUtil.class.getResource("/com/magizhchi/pawnbroking/common/ErrorAlert.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        ErrorAlertUIController gon = (ErrorAlertUIController) loader.getController();
        gon.setMessage(msg);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.setTitle("Error Message");
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.UTILITY); 
        if(event != null) {
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        }
        dialog.showAndWait();
    }

    public static void showErrorAlert(Event event, String msg)
    {        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(PopupUtil.class.getResource("/com/magizhchi/pawnbroking/common/ErrorAlert.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        ErrorAlertUIController gon = (ErrorAlertUIController) loader.getController();
        gon.setMessage(msg);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.setTitle("Error Message");
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.UTILITY); 
        if(event != null) {
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        }
        dialog.showAndWait();
    }
    
    public static void showErrorAlert(MouseEvent event, String msg)
    {        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(PopupUtil.class.getResource("/com/magizhchi/pawnbroking/common/ErrorAlert.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        ErrorAlertUIController gon = (ErrorAlertUIController) loader.getController();
        gon.setMessage(msg);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.setTitle("Error Message");
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.UTILITY);  
        if(event != null) {
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        }
        dialog.showAndWait();
    }
    
    public static void showInfoAlert(String msg)
    {
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(PopupUtil.class.getResource("/com/magizhchi/pawnbroking/common/InfoAlert.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        InfoAlertUIController gon = (InfoAlertUIController) loader.getController();
        gon.setMessage(msg);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.setTitle("Info");
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.UTILITY);  
        dialog.showAndWait();
    }
    
    public static void showInfoAlert(ActionEvent event, String msg)
    {
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(PopupUtil.class.getResource("/com/magizhchi/pawnbroking/common/InfoAlert.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        InfoAlertUIController gon = (InfoAlertUIController) loader.getController();
        gon.setMessage(msg);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.setTitle("Info");
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.UTILITY);  
        if(event != null) {
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        }
        dialog.showAndWait();
    }

    public static void showInfoAlert(MouseEvent event, String msg)
    {
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(PopupUtil.class.getResource("/com/magizhchi/pawnbroking/common/InfoAlert.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        InfoAlertUIController gon = (InfoAlertUIController) loader.getController();
        gon.setMessage(msg);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.setTitle("Info");
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.UTILITY);  
        dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        dialog.showAndWait();
    }
    
    public static void showQueAlert(String msg, Object screenObj, Method yesMethod, Method noMethod) 
    {
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(PopupUtil.class.getResource("/com/magizhchi/pawnbroking/common/QueAlert.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        QueAlertUIController gon = (QueAlertUIController) loader.getController();
        gon.setParent(msg, screenObj, yesMethod, noMethod);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.setTitle("Info");
        dialog.showAndWait();
    }
    
    public static void showImageViewer(MouseEvent event, Image image, String title)
    {
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(PopupUtil.class.getResource("/com/magizhchi/pawnbroking/common/ImageViewerDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        ImageViewerController gon = (ImageViewerController) loader.getController();
        gon.setImage(image);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.setTitle(title);
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.UTILITY);  
        if(event != null) {
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        }
        dialog.showAndWait();
    }

    public static void showImageViewer(ActionEvent event, Image image, String title)
    {
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(PopupUtil.class.getResource("/com/magizhchi/pawnbroking/common/ImageViewerDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        ImageViewerController gon = (ImageViewerController) loader.getController();
        gon.setImage(image);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.setTitle(title);
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.UTILITY);  
        if(event != null) {
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        }
        dialog.showAndWait();
    }
    
    public static void showTwoImageViewer(MouseEvent event, Image firstImg, Image secondImg, String title)
    {
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(PopupUtil.class.getResource("/com/magizhchi/pawnbroking/common/TwoImageViewerDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        TwoImageViewerController gon = (TwoImageViewerController) loader.getController();
        gon.setImage(firstImg, secondImg);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.setTitle(title);
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.UTILITY);  
        if(event != null) {
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        }
        dialog.showAndWait();
    }

    public static void showTwoImageViewer(ActionEvent event, Image firstImg, Image secondImg, String title)
    {
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(PopupUtil.class.getResource("/com/magizhchi/pawnbroking/common/TwoImageViewerDialog.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        TwoImageViewerController gon = (TwoImageViewerController) loader.getController();
        gon.setImage(firstImg, secondImg);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.setTitle(title);
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.UTILITY);  
        if(event != null) {
            dialog.initOwner(((Node)event.getSource()).getScene().getWindow() );
        }
        dialog.showAndWait();
    }
    
    public static void showPasswordField(String password)
    {        
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);        

        FXMLLoader loader = new FXMLLoader(PopupUtil.class.getResource("/com/magizhchi/pawnbroking/common/PasswordField.fxml"));
        Parent root = null;
        try {            
            root = (Parent) loader.load();            
        } catch (IOException ex) {
            Logger.getLogger(PopupUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        PasswordFieldUIController gon = (PasswordFieldUIController) loader.getController();
        gon.setCheckPassword(password);
        Scene scene = new Scene(root);        
        dialog.setScene(scene);
        dialog.setTitle("Password Field");
        dialog.setResizable(false);
        dialog.initStyle(StageStyle.UTILITY);        
        dialog.showAndWait();
    }
    
}
