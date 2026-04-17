/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.usermaster;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataRow;
import com.magizhchi.pawnbroking.common.DataTable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 *
 * @author Tiru
 */
public class UserMasterDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public UserMasterDBOperation(String sDB, String sIP, String sPort, String sSchema,
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
    
    @SuppressWarnings("null")
    public String getId(String screenName) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT OPERATION_NEXT_ID FROM OPERATION_ID_GENERATOR WHERE SCREEN_NAME = ? AND OPERATION_NAME = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, screenName);
            stmt.setString(2, "MASTER");
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return rs.getString(1);		                   
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }        
        return null;
    }

    public DataTable getAllEmployeeNames() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ID, NAME FROM EMPLOYEE WHERE STATUS = ?::REPLEDGE_STATUS AND COMPANY_ID = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, "ACTIVE");
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            rs = stmt.executeQuery();
            
            DataRow emptyRow = new DataRow("", "");
            dataTable.add(emptyRow);
            
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

    public DataTable getAllRoleNames() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ID, NAME FROM ROLE_MASTER WHERE STATUS = ?::REPLEDGE_STATUS";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, "ACTIVE");
            rs = stmt.executeQuery();
            
            DataRow emptyRow = new DataRow("", "");
            dataTable.add(emptyRow);
            
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
    
    public boolean isvalidNameToSave(String sUserName) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT USER_NAME FROM USER_MASTER WHERE USER_NAME = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sUserName);
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
 
    public boolean saveRecord(String sId, String sUserName, String sPassword, 
            String sEmpId, String sRoleId, String sStatus, 
            String sNote, String saltValue) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "INSERT INTO user_master(id, user_name, user_password, emp_id, role_id, "
                + "status, note, created_user_id, salt_value) "
                + "VALUES (?, ?, ?, ?, ?, ?::REPLEDGE_STATUS, ?, ?, ?)";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            
            stmt.setString(1, sId);
            stmt.setString(2, sUserName);
            stmt.setString(3, sPassword);
            stmt.setString(4, sEmpId);
            stmt.setString(5, sRoleId);
            stmt.setString(6, sStatus);
            stmt.setString(7, sNote);
            stmt.setString(8, CommonConstants.USERID);
            stmt.setString(9, saltValue);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean setNextId(String screenName, String sNextIdWithPrefix) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE OPERATION_ID_GENERATOR SET OPERATION_NEXT_ID = ?  WHERE SCREEN_NAME = ? AND OPERATION_NAME = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, sNextIdWithPrefix);
            stmt.setString(2, screenName);
            stmt.setString(3, "MASTER");
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public HashMap<String, String> getAllHeaderValues(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT u.user_name, u.user_password, u.emp_id, e.name, u.role_id, r.name, u.STATUS, u.NOTE "
                + "FROM user_master u, employee e, role_master r "
                + "WHERE u.ID = ? "
                + "and u.emp_id = e.id "
                + "and u.role_id = r.id";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("ID", sId);
                headerValues.put("USER_NAME", rs.getString(1));
                headerValues.put("USER_PASSWORD", rs.getString(2));
                headerValues.put("EMP_ID", rs.getString(3));
                headerValues.put("EMP_NAME", rs.getString(4));
                headerValues.put("ROLE_ID", rs.getString(5));
                headerValues.put("ROLE_NAME", rs.getString(6));                
                headerValues.put("STATUS", rs.getString(7));
                headerValues.put("NOTE", rs.getString(8));
                return headerValues;		       
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

        return null;
    }
    
    public DataTable getScreens(String tabName) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT screen_name " +
                     "FROM screens "
                + "where tab_name = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, tabName);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(false);
                row.addColumn(false);
                row.addColumn(false);
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
    
    public boolean isIdAlreadyExists(String sId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT ID FROM ROLE_MASTER WHERE ID = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return true;		       
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

        return false;
    }
    
    public boolean deleteAll(String sId, String sTabName) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM role_detail "
                + "WHERE role_id = ? "
                + "AND tab_name = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sTabName);            
            stmt.executeUpdate();
            
            return true;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
     
    public boolean updateRecord(String sId, String sUserName, String sPassword, 
            String sEmpId, String sRoleId, String sStatus, 
            String sNote, String saltValue) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE user_master " +
                    "   SET " +
                    "   user_name=?,  " +
                    "   user_password=?,  " +
                    "   emp_id=?,  " +
                    "   role_id=?,  " +
                    "   status=?::REPLEDGE_STATUS,  " +
                    "   note=?,  " +
                    "   updated_user_id=?, " +
                    "   SALT_VALUE = ? " +
                    " WHERE id=?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            
            stmt.setString(1, sUserName);
            stmt.setString(2, sPassword);
            stmt.setString(3, sEmpId);
            stmt.setString(4, sRoleId);
            stmt.setString(5, sStatus);
            stmt.setString(6, sNote);
            stmt.setString(7, CommonConstants.USERID);
            stmt.setString(8, saltValue);
            stmt.setString(9, sId);            
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
    
    public DataTable getAllDetailsValues(String sFilterScript, String...sVals) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT UM.ID, UM.USER_NAME, E.NAME, RM.NAME, UM.STATUS "
                + "FROM USER_MASTER UM, EMPLOYEE E, ROLE_MASTER RM "
                + "WHERE UM.EMP_ID = E.ID "
                + "AND UM.ROLE_ID = RM.ID ";
        try {
            if(sFilterScript != null) {
                sql += sFilterScript;
            }
            sql += "ORDER BY UM.ID ";

            stmt = roleMasterConn.prepareStatement(sql);  

            if(sFilterScript != null) {
                for(int i=0; i<sVals.length; i++) {
                    if(sFilterScript.contains("STATUS =")) {
                        stmt.setString(i+1, sVals[i]);
                    } else {
                        stmt.setString(i+1, "%"+sVals[i]+"%");
                    }
                }
            }
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
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
    
    public boolean updateUserFingerPrint(byte[] ISOTemplate, String uId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "update user_master set user_finger_img = ? where id = ? ";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setBytes(1,ISOTemplate);            
            stmt.setString(2, uId);
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
}
