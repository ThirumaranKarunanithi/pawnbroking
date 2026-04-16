/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.rolemaster;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class RoleMasterBean {
    
    private final SimpleStringProperty sScreenName;
    private final SimpleBooleanProperty bAdd;
    private final SimpleBooleanProperty bView;
    private final SimpleBooleanProperty bUpdate;

    public RoleMasterBean(String sScreenName, boolean bAdd, boolean bView, boolean bUpdate) {
        
        this.sScreenName = new SimpleStringProperty(sScreenName);
        this.bAdd = new SimpleBooleanProperty(bAdd);
        this.bView = new SimpleBooleanProperty(bView);
        this.bUpdate = new SimpleBooleanProperty(bUpdate);
    }

    public String getSScreenName() {
        return sScreenName.get();
    }

    public void setSScreenName(String sScreenName) {
        this.sScreenName.set(sScreenName);
    }
    
    public boolean isBAdd() {
        return this.bAddProperty().get();
    }

    public SimpleBooleanProperty bAddProperty() {
        return bAdd;
    }
    
    public boolean getBAddProperty() {
        return this.bAddProperty().get();
    }    
    
    public void setBAdd(boolean bAdd) {
        this.bAdd.set(bAdd);
    }    

    public boolean isBView() {
        return this.bViewProperty().get();
    }

    public SimpleBooleanProperty bViewProperty() {
        return bView;
    }
    
    public boolean getBViewProperty() {
        return this.bViewProperty().get();
    }    
    
    public void setBView(boolean bView) {
        this.bView.set(bView);
    }    

    public boolean isBUpdate() {
        return this.bUpdateProperty().get();
    }

    public SimpleBooleanProperty bUpdateProperty() {
        return bUpdate;
    }
    
    public boolean getBUpdateProperty() {
        return this.bUpdateProperty().get();
    }    
    
    public void setBUpdate(boolean bUpdate) {
        this.bUpdate.set(bUpdate);
    }    
    
}
