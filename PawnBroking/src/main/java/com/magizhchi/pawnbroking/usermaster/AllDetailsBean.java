package com.magizhchi.pawnbroking.usermaster;

import javafx.beans.property.SimpleStringProperty;


/**
 *
 * @author Tiru
 */
public class AllDetailsBean {
    
  
                                                                      
                                                                      
    private final SimpleStringProperty sId;
    private final SimpleStringProperty sUserName;
    private final SimpleStringProperty sEmployeeName;
    private final SimpleStringProperty sRoleName;
    private final SimpleStringProperty sStatus;
    
    public AllDetailsBean(String sId, String sUserName, String sEmployeeName, String sRoleName, String sStatus) 
    {
        
        this.sId = new SimpleStringProperty(sId);
        this.sUserName = new SimpleStringProperty(sUserName);
        this.sEmployeeName = new SimpleStringProperty(sEmployeeName);
        this.sRoleName = new SimpleStringProperty(sRoleName);
        this.sStatus = new SimpleStringProperty(sStatus);
               
    }

    public String getSId() {
        return sId.get();
    }
    
    public String getSUserName() {
        return sUserName.get();
    }

    
    public String getSEmployeeName() {
        return sEmployeeName.get();
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
    
    public void setSUserName(String sUserName) {
        this.sUserName.set(sUserName);
    }

    public void setSEmployeeName(String sEmployeeName) {
        this.sEmployeeName.set(sEmployeeName);
    }
    
    public void setSRoleName(String sRoleName) {
        this.sRoleName.set(sRoleName);
    }
    public void setSStatus(String sStatus) {
        this.sStatus.set(sStatus);
    }
   
}


