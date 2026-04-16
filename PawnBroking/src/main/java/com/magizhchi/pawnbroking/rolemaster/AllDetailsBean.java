/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.rolemaster;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Tiru
 */
public class AllDetailsBean {
                                                                      
    private final SimpleStringProperty sId;
    private final SimpleStringProperty sRoleName;
    private final SimpleStringProperty sStatus;
    
    public AllDetailsBean(String sId,String sRoleName, String sStatus) 
    {
        
        this.sId = new SimpleStringProperty(sId);
        this.sRoleName = new SimpleStringProperty(sRoleName);
        this.sStatus = new SimpleStringProperty(sStatus);
               
    }

    AllDetailsBean(String sId, String sUserName, String sEmpName, String sRoleName, String sStatus) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getSId() {
        return sId.get();
    }  
    public String getSRoleName() {
        return sRoleName.get();
    }
    
    public String getSStatus() {
        return sStatus.get();
    }
    
    public void setSId(String sId) {
        this.sId.set(sId);
    }   
    public void setSRoleName(String sRoleName) {
        this.sRoleName.set(sRoleName);
    }
    public void setSStatus(String sStatus) {
        this.sStatus.set(sStatus);
    }
}
