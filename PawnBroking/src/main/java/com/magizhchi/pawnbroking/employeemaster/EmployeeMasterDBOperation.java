/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.employeemaster;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataRow;
import com.magizhchi.pawnbroking.common.DataTable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tiru
 */
public class EmployeeMasterDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public EmployeeMasterDBOperation(String sDB, String sIP, String sPort, String sSchema,
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
    
    public boolean isvalidNameToSave(String sName) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT NAME FROM EMPLOYEE WHERE NAME = ? AND COMPANY_ID = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sName);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
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
      
    public boolean saveRecord(String sId, String sJoinedDate, String sName, String sGender, 
                                      String sSpouseType, String sSpouseName, String sDoorNo, 
                                      String sStreetName, String sArea, String sCity, 
                                      String sMobileNumber, double dSalaryAmount, 
                                      double dDailyAllowanceAmount, String sJobType, 
                                      String sSalaryType, String sEmployeeType, 
                                      String sStatus, String sNote) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "INSERT INTO employee(id, joined_date, name, gender, spouse_type, spouse_name, door_number, street, area, city, mobile_number, job_type, salary_type, salary_amount, daily_allowance_amount, employee_type, status, note, user_id, company_id) VALUES (?, ?, ?, ?::gender_type, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::repledge_status, ?, ?, ?)";
        
        try {

            java.sql.Date sqlJoinedDate = java.sql.Date.valueOf(LocalDate.parse(sJoinedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            
            stmt.setString(1, sId);
            stmt.setDate(2, sqlJoinedDate);
            stmt.setString(3, sName);
            stmt.setString(4, sGender);
            stmt.setString(5, sSpouseType);
            stmt.setString(6, sSpouseName);
            stmt.setString(7, sDoorNo);
            stmt.setString(8, sStreetName);
            stmt.setString(9, sArea);
            stmt.setString(10, sCity);
            stmt.setString(11, sMobileNumber);
            stmt.setString(12, sJobType);
            stmt.setString(13, sSalaryType);
            stmt.setDouble(14, dSalaryAmount);
            stmt.setDouble(15, dDailyAllowanceAmount);
            stmt.setString(16, sEmployeeType);
            stmt.setString(17, sStatus);
            stmt.setString(18, sNote);
            stmt.setString(19, CommonConstants.USERID);
            stmt.setString(20, CommonConstants.ACTIVE_COMPANY_ID);
            
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
    
    public HashMap<String, String> getAllHeaderValues(String sEmployeeId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT ID, JOINED_DATE, NAME, GENDER, SPOUSE_TYPE, SPOUSE_NAME, DOOR_NUMBER, " +
                    "STREET, AREA, CITY, MOBILE_NUMBER, JOB_TYPE, SALARY_TYPE, SALARY_AMOUNT, " +
                    "DAILY_ALLOWANCE_AMOUNT, EMPLOYEE_TYPE, STATUS, NOTE, employee_image " +
                    "FROM EMPLOYEE " +
                    "WHERE ID = ? " +
                    "AND COMPANY_ID = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sEmployeeId);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("ID", rs.getString(1));
                headerValues.put("JOINED_DATE", format.format(rs.getDate(2).toLocalDate()));
                headerValues.put("NAME", rs.getString(3));
                headerValues.put("GENDER", rs.getString(4));
                headerValues.put("SPOUSE_TYPE", rs.getString(5));
                headerValues.put("SPOUSE_NAME", rs.getString(6));
                headerValues.put("DOOR_NUMBER", rs.getString(7));
                headerValues.put("STREET", rs.getString(8));
                headerValues.put("AREA", rs.getString(9));
                headerValues.put("CITY", rs.getString(10));
                headerValues.put("MOBILE_NUMBER", rs.getString(11));
                headerValues.put("JOB_TYPE", rs.getString(12));
                headerValues.put("SALARY_TYPE", rs.getString(13));
                headerValues.put("SALARY_AMOUNT", Double.toString(rs.getDouble(14)));
                headerValues.put("DAILY_ALLOWANCE_AMOUNT", Double.toString(rs.getDouble(15)));
                headerValues.put("EMPLOYEE_TYPE", rs.getString(16));
                headerValues.put("STATUS", rs.getString(17));
                headerValues.put("NOTE", rs.getString(18));
                
                byte customerBuf[] = rs.getBytes(19);
                if(customerBuf != null) {   
                    File file = new File(new File(CommonConstants.TEMP_FILE_LOCATION), CommonConstants.LOGIN_USER_IMAGE_NAME);
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(customerBuf);                
                    fos.close();
                    headerValues.put("IMG", file.getAbsolutePath());
                } else {
                    headerValues.put("IMG", "");
                }
                
                return headerValues;		       
            }		    

        } catch (SQLException e) {
                throw e;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EmployeeMasterDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EmployeeMasterDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 

        return null;
    }
    
    public boolean updateRecord(String sId, String sJoinedDate, String sName, String sGender, 
                                      String sSpouseType, String sSpouseName, String sDoorNo, 
                                      String sStreetName, String sArea, String sCity, 
                                      String sMobileNumber, double dSalaryAmount, 
                                      double dDailyAllowanceAmount, String sJobType, 
                                      String sSalaryType, String sEmployeeType, 
                                      String sStatus, String sNote, String usrImg) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;              
                
        java.sql.Date sqlDateLCDate = java.sql.Date.valueOf(LocalDate.parse(sJoinedDate, CommonConstants.DATETIMEFORMATTER));
        
        String sql = "UPDATE employee " +
                    "SET joined_date=?, gender=?::gender_type, spouse_type=?, spouse_name=?, " +
                    "door_number=?, street=?, area=?, city=?, mobile_number=?, job_type=?, " +
                    "salary_type=?, salary_amount=?, daily_allowance_amount=?, employee_type=?, " +
                    "status=?::repledge_status, note=?, user_id=?, employee_image=? " +
                    "WHERE id=? " +
                    "AND company_id=? ";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setDate(1, sqlDateLCDate);
            stmt.setString(2, sGender);
            stmt.setString(3, sSpouseType);
            stmt.setString(4, sSpouseName);
            stmt.setString(5, sDoorNo);
            stmt.setString(6, sStreetName);
            stmt.setString(7, sArea);
            stmt.setString(8, sCity);
            stmt.setString(9, sMobileNumber);
            stmt.setString(10, sJobType);
            stmt.setString(11, sSalaryType);
            stmt.setDouble(12, dSalaryAmount);
            stmt.setDouble(13, dDailyAllowanceAmount);            
            stmt.setString(14, sEmployeeType);
            stmt.setString(15, sStatus);
            stmt.setString(16, sNote);
            stmt.setString(17, CommonConstants.USERID);

            if(usrImg != null) {
                File ufile = new File(usrImg);
                FileInputStream ufis = new FileInputStream(ufile);                            
                stmt.setBinaryStream(18, ufis, (int)ufile.length());
            } else {
                stmt.setObject(18,null);
            }
            
            stmt.setString(19, sId);
            stmt.setString(20, CommonConstants.ACTIVE_COMPANY_ID);
            
            return stmt.executeUpdate() >= 1;
            
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public DataTable getAllDetailsValues(String sFilterScript, String...sVals) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ID, JOINED_DATE, EMPLOYEE_TYPE, NAME, GENDER, SPOUSE_TYPE, SPOUSE_NAME, "
                +   "MOBILE_NUMBER, SALARY_TYPE, SALARY_AMOUNT, " +
                    "DAILY_ALLOWANCE_AMOUNT, JOB_TYPE, STATUS " +
                    "FROM EMPLOYEE ";

        try {

            if(sFilterScript != null) {
                sql += sFilterScript;
            }
            
            stmt = roleMasterConn.prepareStatement(sql);               
            if(sFilterScript != null) {
                for(int i=0; i<sVals.length; i++) {
                    stmt.setString(i+1, "%"+sVals[i]+"%");
                }
            }
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(format.format(rs.getDate(2).toLocalDate()));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
                row.addColumn(rs.getString(7));
                row.addColumn(rs.getString(8));
                row.addColumn(rs.getString(9));
                row.addColumn(Double.toString(rs.getDouble(10)));
                row.addColumn(Double.toString(rs.getDouble(11)));
                row.addColumn(rs.getString(12));
                row.addColumn(rs.getString(13));                                
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
