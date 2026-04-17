/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.loginscreen;

/**
 *
 * @author Tiru
 */
public class FingerPrintBean {
 
    private String id;
    private byte[] fingerPrint;
    private String useraName;
    private String password;
    
    public FingerPrintBean() {
    }

    public FingerPrintBean(String id, byte[] fingerPrint, String useraName, String password) {
        this.id = id;
        this.fingerPrint = fingerPrint;
        this.useraName = useraName;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getFingerPrint() {
        return fingerPrint;
    }

    public void setFingerPrint(byte[] fingerPrint) {
        this.fingerPrint = fingerPrint;
    }

    public String getUseraName() {
        return useraName;
    }

    public void setUseraName(String useraName) {
        this.useraName = useraName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
}
