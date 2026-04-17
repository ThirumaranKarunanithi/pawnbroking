/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.itemmaster;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataRow;
import com.magizhchi.pawnbroking.common.DataTable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tiru
 */
public class ItemMasterDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public ItemMasterDBOperation(String sDB, String sIP, String sPort, String sSchema,
                    String sDBUsername, String sDBPassword) throws ClassNotFoundException
    {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                    throw e;
            }

            this.sDB = sDB;
            this.sIP = sIP;
            this.sPort = sPort;
            this.sSchema = sSchema;
            this.sDBUsername = sDBUsername;
            this.sDBPassword = sDBPassword;
    }

    private void connectDB() throws SQLException
    {		
            try {
                    roleMasterConn = DriverManager.getConnection("jdbc:"+sDB+"://"+sIP+":"+sPort+"/"+sSchema,sDBUsername, sDBPassword);
            } catch (SQLException e) {
                    throw e;
            }		
    }

    public void commit() {
        try {
            roleMasterConn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(ItemMasterDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public DataTable getAllJewelItems(String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ITEM, STATUS "
                + "FROM JEWEL_ITEMS "
                + "WHERE JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sMaterialType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                dataTable.add(row);                	       
            }		                
        } catch (SQLException e) {
                throw e;
        }finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 

        return dataTable;	
    }
    
    public boolean isvalidItemToSave(String sItem, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT ITEM "
                + "FROM JEWEL_ITEMS "
                + "WHERE JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND ITEM = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, sMaterialType);
            stmt.setString(2, sItem);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return false;		       
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 

        return true;
    }
    
    public boolean saveItem(String sItem, String sStatus, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "INSERT INTO jewel_items(jewel_material_type, item, user_id, status) VALUES(?::MATERIAL_TYPE, ?, ?, ?::REPLEDGE_STATUS)";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, sMaterialType);
            stmt.setString(2, sItem);
            stmt.setString(3, CommonConstants.USERID);
            stmt.setString(4, sStatus);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean updateItem(String sItem, String sStatus, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE JEWEL_ITEMS SET USER_ID=?, STATUS = ?::REPLEDGE_STATUS WHERE ITEM = ? AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.USERID);
            stmt.setString(2, sStatus);
            stmt.setString(3, sItem);
            stmt.setString(4, sMaterialType);
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean getAddViewUpdate(String sRoleId, String sTabName, String sScreenName, String sActionName) throws SQLException
    {

        boolean addViewUpdate = false;
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT " + sActionName + " " +
                "FROM role_detail " +
                "WHERE tab_name = ? " +
                "AND screen_name = ? " +
                "AND role_id = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, sTabName);
            stmt.setString(2, sScreenName);
            stmt.setString(3, sRoleId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                addViewUpdate = rs.getBoolean(1);
            }		                
        } catch (SQLException e) {
                throw e;
        }finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        }        
        return addViewUpdate;
    }
    
}
