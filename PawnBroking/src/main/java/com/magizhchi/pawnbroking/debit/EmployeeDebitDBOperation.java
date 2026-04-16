/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.debit;

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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tiru
 */
public class EmployeeDebitDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public EmployeeDebitDBOperation(String sDB, String sIP, String sPort, String sSchema,
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
            Logger.getLogger(EmployeeDebitDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public DataTable getAllReduceList(String sEmpId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(ID, '\\D', '', 'g'), '99G999D9S')) AS SLNO, "
                    + "ID, DEBITTED_DATE, REASON, DEBITTED_AMOUNT, true " +
                    "FROM EMPLOYEE_ADVANCE_AMOUNT_DEBIT " +
                    "WHERE COMPANY_ID = ? " +
                    "AND DEBIT_ACTION = ? "
                    + "AND EMPLOYEE_ID = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, "REDUCE WITH SALARY AMOUNT");
            stmt.setString(3, sEmpId);
            rs = stmt.executeQuery();
            
            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(format.format(rs.getDate(3).toLocalDate()));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getDouble(5));
                row.addColumn(rs.getBoolean(6));
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
    
    public HashMap<String, String> getAllEmployeeValues(String sEmployeeId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT joined_date, gender, spouse_type, spouse_name, door_number, " +
                    "street, area, city, mobile_number, job_type, salary_type, " +
                    "daily_allowance_amount, employee_type, status, note, salary_amount, id, name " +
                    "FROM employee " +
                    "WHERE id = ?";



        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sEmployeeId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("JOINED_DATE", format.format(rs.getDate(1).toLocalDate()));
                headerValues.put("GENDER", rs.getString(2));
                headerValues.put("SPOUSE_TYPE", rs.getString(3));
                headerValues.put("SPOUSE_NAME", rs.getString(4));
                headerValues.put("DOOR_NUMBER", rs.getString(5));
                headerValues.put("STREET", rs.getString(6));
                headerValues.put("AREA", rs.getString(7));
                headerValues.put("CITY", rs.getString(8));
                headerValues.put("MOBILE_NUMBER", rs.getString(9));
                headerValues.put("JOB_TYPE", rs.getString(10));
                headerValues.put("SALARY_TYPE", rs.getString(11));
                headerValues.put("DAILY_ALLOWANCE_AMOUNT", Double.toString(rs.getDouble(12)));
                headerValues.put("EMPLOYEE_TYPE", rs.getString(13));
                headerValues.put("STATUS", rs.getString(14));                
                headerValues.put("NOTE", rs.getString(15));
                headerValues.put("SALARY_AMOUNT", rs.getString(16));
                headerValues.put("ID", rs.getString(17));
                headerValues.put("NAME", rs.getString(18));
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
    
    public boolean saveDailyAllowanceDebit(String sEmployeeId, String sEmployeeName, 
                            String sDebittedDate, double dDebittedAmount, String sNote, String sId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "INSERT INTO employee_daily_allowance_debit( " +
                    "company_id, employee_id, employee_name, debitted_date, debitted_amount, note, user_id, id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {

            java.sql.Date sqlDateDebittedDate = java.sql.Date.valueOf(LocalDate.parse(sDebittedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sEmployeeId);
            stmt.setString(3, sEmployeeName);
            stmt.setDate(4, sqlDateDebittedDate);
            stmt.setDouble(5, dDebittedAmount);
            stmt.setString(6, sNote);
            stmt.setString(7, CommonConstants.USERID);
            stmt.setString(8, sId);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean updateRecord(String sDebitId, String sDebittedDate, String sNote, double dGivenAmount) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "update employee_daily_allowance_debit set "
                + "debitted_date = ?, "
                + "debitted_amount = ?, "
                + "note = ?, "
                + "user_id = ? "
                + "where id = ? and company_id = ? ";                
        
        try {

            java.sql.Date sqlDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sDebittedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setDate(1, sqlDateOpenDate);
            stmt.setDouble(2, dGivenAmount);          
            stmt.setString(3, sNote);
            stmt.setString(4, CommonConstants.USERID);            
            stmt.setString(5, sDebitId);
            stmt.setString(6, CommonConstants.ACTIVE_COMPANY_ID);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean updateAARecord(String sDebitId, String sDebittedDate, 
            String sNote, double dGivenAmount,String sReason, String sAction,
            String sEmployeeId, String sEmployeeName) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "update employee_advance_amount_debit set "
                + "debitted_date = ?, "
                + "debitted_amount = ?, "
                + "note = ?, "
                + "user_id = ?, "
                + "reason = ?, "
                + "debit_action = ?, "
                + "employee_id = ?, "
                + "employee_name = ? "
                + "where id = ? and company_id = ? ";   
        
        try {

            java.sql.Date sqlDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sDebittedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setDate(1, sqlDateOpenDate);
            stmt.setDouble(2, dGivenAmount);          
            stmt.setString(3, sNote);
            stmt.setString(4, CommonConstants.USERID);            
            stmt.setString(5, sReason);
            stmt.setString(6, sAction);    
            stmt.setString(7, sEmployeeId);
            stmt.setString(8, sEmployeeName);            
            stmt.setString(9, sDebitId);
            stmt.setString(10, CommonConstants.ACTIVE_COMPANY_ID);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean updateSARecord(String sDebitId, String sDebittedDate, 
            String sNote, double dGivenAmount, String sSalForMonth) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "update employee_salary_amount_debit set "
                + "debitted_date = ?, "
                + "debitted_amount = ?, "
                + "note = ?, "
                + "user_id = ?, "
                + "sal_for_mon_year = ? "
                + "where id = ? and company_id = ? ";   
        
        try {

            java.sql.Date sqlDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sDebittedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setDate(1, sqlDateOpenDate);
            stmt.setDouble(2, dGivenAmount);          
            stmt.setString(3, sNote);
            stmt.setString(4, CommonConstants.USERID); 
            stmt.setString(5, sSalForMonth);
            stmt.setString(6, sDebitId);
            stmt.setString(7, CommonConstants.ACTIVE_COMPANY_ID);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean updateOARecord(String sDebitId, String sDebittedDate, 
            String sNote, double dGivenAmount,String sReason) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "update employee_other_amount_debit set "
                + "debitted_date = ?, "
                + "debitted_amount = ?, "
                + "note = ?, "
                + "user_id = ?, "
                + "debitted_reason = ? "
                + "where id = ? and company_id = ? ";   
        
        try {

            java.sql.Date sqlDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sDebittedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setDate(1, sqlDateOpenDate);
            stmt.setDouble(2, dGivenAmount);          
            stmt.setString(3, sNote);
            stmt.setString(4, CommonConstants.USERID);            
            stmt.setString(5, sReason);            
            stmt.setString(6, sDebitId);
            stmt.setString(7, CommonConstants.ACTIVE_COMPANY_ID);
            
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
        
        String sql = "select employee_id, debitted_amount, note, debitted_date "
                + "from employee_daily_allowance_debit "
                + "where company_id = ? and id = ? ";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("ID", sId);
                headerValues.put("EMPLOYEE_ID", rs.getString(1));
                headerValues.put("DEBITTED_AMOUNT", Double.toString(rs.getDouble(2)));
                headerValues.put("NOTE", rs.getString(3));                              
                headerValues.put("DEBITTED_DATE", format.format(rs.getDate(4).toLocalDate()));
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

    public HashMap<String, String> getAAAllHeaderValues(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT employee_id, debitted_date, debitted_amount, debit_action, note, reason "
                + "from employee_advance_amount_debit "
                + "where company_id = ? and id = ? ";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("ID", sId);
                headerValues.put("EMPLOYEE_ID", rs.getString(1));
                headerValues.put("DEBITTED_DATE", format.format(rs.getDate(2).toLocalDate()));
                headerValues.put("DEBITTED_AMOUNT", Double.toString(rs.getDouble(3)));
                headerValues.put("DEBITTED_ACTION", rs.getString(4));
                headerValues.put("NOTE", rs.getString(5));
                headerValues.put("REASON", rs.getString(6));
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

    public HashMap<String, String> getOAAllHeaderValues(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT employee_id, debitted_date, debitted_amount, note, debitted_reason "
                + "from employee_other_amount_debit "
                + "where company_id = ? and id = ? ";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("ID", sId);
                headerValues.put("EMPLOYEE_ID", rs.getString(1));
                headerValues.put("DEBITTED_DATE", format.format(rs.getDate(2).toLocalDate()));
                headerValues.put("DEBITTED_AMOUNT", Double.toString(rs.getDouble(3)));
                headerValues.put("NOTE", rs.getString(4));
                headerValues.put("REASON", rs.getString(5));
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
    
    public HashMap<String, String> getSAAllHeaderValues(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT employee_id, debitted_date, debitted_amount, "
                + "debit_action, note, total_advance_amount, amount_to_give, sal_for_mon_year "
                + "from employee_salary_amount_debit "
                + "where company_id = ? and id = ? ";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("ID", sId);
                headerValues.put("EMPLOYEE_ID", rs.getString(1));
                headerValues.put("DEBITTED_DATE", format.format(rs.getDate(2).toLocalDate()));
                headerValues.put("DEBITTED_AMOUNT", Double.toString(rs.getDouble(3)));
                headerValues.put("DEBITTED_ACTION", rs.getString(4));
                headerValues.put("NOTE", rs.getString(5));
                headerValues.put("TOTAL_ADVANCE_AMOUNT", rs.getString(6));
                headerValues.put("AMOUNT_TO_GIVE", rs.getString(7));
                headerValues.put("SAL_FOR_MONTH_YEAR", rs.getString(8));
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
    
    public boolean saveAdvanceAmountDebit(String sId, String sEmployeeId, String sEmployeeName, 
                            String sDebittedDate, double dDebittedAmount, String sNote, String sAction,
                            String sReason) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "INSERT INTO employee_advance_amount_debit( " +
                    "company_id, employee_id, employee_name, debitted_date, "
                  + "debitted_amount, note, user_id, debit_action, id, reason) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {

            java.sql.Date sqlDateDebittedDate = java.sql.Date.valueOf(LocalDate.parse(sDebittedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sEmployeeId);
            stmt.setString(3, sEmployeeName);
            stmt.setDate(4, sqlDateDebittedDate);
            stmt.setDouble(5, dDebittedAmount);
            stmt.setString(6, sNote);
            stmt.setString(7, CommonConstants.USERID);
            stmt.setString(8, sAction);
            stmt.setString(9, sId);
            stmt.setString(10, sReason);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean saveSalaryAmountDebit(String sId, String sEmployeeId, String sEmployeeName, 
                            String sDebittedDate, double dTotalAdvanceAmount, 
                            double dAmountToGive, double dDebittedAmount, 
                            String sSalForMonth, String sNote, String sAction) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "INSERT INTO employee_salary_amount_debit(" +
                    "id, company_id, employee_id, employee_name, debitted_date, total_advance_amount, " +
                    "debit_action, amount_to_give, debitted_amount, " +
                    "user_id, note, sal_for_mon_year) " +
                    "VALUES (?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, " +
                    "?, ?, ?)";
        
        try {

            java.sql.Date sqlDateDebittedDate = java.sql.Date.valueOf(LocalDate.parse(sDebittedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, sId);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, sEmployeeId);
            stmt.setString(4, sEmployeeName);
            stmt.setDate(5, sqlDateDebittedDate);
            stmt.setDouble(6, dTotalAdvanceAmount);
            stmt.setString(7, sAction);
            stmt.setDouble(8, dAmountToGive);
            stmt.setDouble(9, dDebittedAmount);
            stmt.setString(10, CommonConstants.USERID);
            stmt.setString(11, sNote);
            stmt.setString(12, sSalForMonth);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean saveOtherAmountDebit(String sId, String sEmployeeId, String sEmployeeName, 
                            String sDebittedDate, double dDebittedAmount, String sNote, String sReason) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "INSERT INTO employee_other_amount_debit(" +
                    "id, company_id, employee_id, employee_name, debitted_date, debitted_reason, " +
                    "debitted_amount, note, user_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?)";
        
        try {

            java.sql.Date sqlDateDebittedDate = java.sql.Date.valueOf(LocalDate.parse(sDebittedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, sId);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, sEmployeeId);
            stmt.setString(4, sEmployeeName);
            stmt.setDate(5, sqlDateDebittedDate);
            stmt.setString(6, sReason);
            stmt.setDouble(7, dDebittedAmount);
            stmt.setString(8, sNote);
            stmt.setString(9, CommonConstants.USERID);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean isSameDateEntryAvailable(String sEmployeeId, String sDebittedDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT employee_name FROM employee_daily_allowance_debit WHERE employee_id = ? AND COMPANY_ID = ? and TO_CHAR(debitted_date, 'DD-MM-YYYY') = ?";

        try {

            java.sql.Date sqlDateDebittedDate = java.sql.Date.valueOf(LocalDate.parse(sDebittedDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sEmployeeId);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, sDebittedDate);
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

    public boolean isOASameDateEntryAvailable(String sEmployeeId, String sDebittedDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT employee_name FROM employee_other_amount_debit WHERE employee_id = ? AND COMPANY_ID = ? and TO_CHAR(debitted_date, 'DD-MM-YYYY') = ?";

        try {

            java.sql.Date sqlDateDebittedDate = java.sql.Date.valueOf(LocalDate.parse(sDebittedDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sEmployeeId);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, sDebittedDate);
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
    
    @SuppressWarnings("null")
    public String getId() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT OPERATION_NEXT_ID FROM OPERATION_ID_GENERATOR WHERE SCREEN_NAME = ? AND OPERATION_NAME = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, "DAILY_ALLOWANCE_AMOUNT");
            stmt.setString(2, "DEBIT");
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
    
    @SuppressWarnings("null")
    public String getAAId() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT OPERATION_NEXT_ID FROM OPERATION_ID_GENERATOR WHERE SCREEN_NAME = ? AND OPERATION_NAME = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, "EMPLOYEE ADAVANCE AMOUNT");
            stmt.setString(2, "DEBIT");
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

    @SuppressWarnings("null")
    public String getSAId() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT OPERATION_NEXT_ID FROM OPERATION_ID_GENERATOR WHERE SCREEN_NAME = ? AND OPERATION_NAME = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, "EMPLOYEE SALARY AMOUNT");
            stmt.setString(2, "DEBIT");
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

    @SuppressWarnings("null")
    public String getOAId() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT OPERATION_NEXT_ID FROM OPERATION_ID_GENERATOR WHERE SCREEN_NAME = ? AND OPERATION_NAME = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1,"EMPLOYEE OTHER AMOUNT DEBIT");
            stmt.setString(2, "DEBIT");
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
    
    public boolean isAASameDateEntryAvailable(String sEmployeeId, String sDebittedDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT employee_name FROM employee_advance_amount_debit WHERE employee_id = ? AND COMPANY_ID = ? and TO_CHAR(debitted_date, 'MM-YYYY') = TO_CHAR(TO_DATE(?,'DD-MM-YYYY'), 'MM-YYYY')";

        try {            
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sEmployeeId);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, sDebittedDate);
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

    public boolean isSASameDateEntryAvailable(String sEmployeeId, String sDebittedDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT employee_name FROM employee_salary_amount_debit WHERE employee_id = ? AND COMPANY_ID = ? and TO_CHAR(debitted_date, 'MM-YYYY') = TO_CHAR(TO_DATE(?,'DD-MM-YYYY'), 'MM-YYYY')";

        try {            
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sEmployeeId);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, sDebittedDate);
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
    
    public String getTotalAdavanceAmount(String sEmployeeId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT sum(debitted_amount) FROM employee_advance_amount_debit WHERE employee_id = ? AND COMPANY_ID = ? and debit_action = ?";

        try {            
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sEmployeeId);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, "REDUCE WITH SALARY AMOUNT");
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return Double.toString(rs.getDouble(1));	       
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

        return "0";
    }
    
    public boolean setNextId(long nextId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE OPERATION_ID_GENERATOR SET OPERATION_NEXT_ID = ?  WHERE SCREEN_NAME = ? AND OPERATION_NAME = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.EMP_DEBIT_DA_PREFIX + nextId);
            stmt.setString(2, "DAILY_ALLOWANCE_AMOUNT");
            stmt.setString(3, "DEBIT");
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean setAANextId(long nextId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE OPERATION_ID_GENERATOR SET OPERATION_NEXT_ID = ?  WHERE SCREEN_NAME = ? AND OPERATION_NAME = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.EMP_DEBIT_AA_PREFIX + nextId);
            stmt.setString(2, "EMPLOYEE ADAVANCE AMOUNT");
            stmt.setString(3, "DEBIT");
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean setSANextId(long nextId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE OPERATION_ID_GENERATOR SET OPERATION_NEXT_ID = ?  WHERE SCREEN_NAME = ? AND OPERATION_NAME = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.EMP_DEBIT_SA_PREFIX + nextId);
            stmt.setString(2, "EMPLOYEE SALARY AMOUNT");
            stmt.setString(3, "DEBIT");
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean setOANextId(long nextId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE OPERATION_ID_GENERATOR SET OPERATION_NEXT_ID = ?  WHERE SCREEN_NAME = ? AND OPERATION_NAME = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.EMP_DEBIT_OA_PREFIX + nextId);
            stmt.setString(2, "EMPLOYEE OTHER AMOUNT DEBIT");
            stmt.setString(3, "DEBIT");
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean updateSAAllAdvanceAmountToReducedAction(String sSalaryId, String sAADebitId, String sEmployeeId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;              
        String sql = "UPDATE employee_advance_amount_debit " +
                    "SET debit_action=?, id_reduced_by = ?, user_id=? " +
                    "WHERE debit_action = ? AND company_id = ? AND employee_id = ? AND ID = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, "REDUCED");
            stmt.setString(2, sSalaryId);
            stmt.setString(3, CommonConstants.USERID);
            stmt.setString(4, "REDUCE WITH SALARY AMOUNT");
            stmt.setString(5, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(6, sEmployeeId);
            stmt.setString(7, sAADebitId);
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
    
    public boolean allowToChangeDate() throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT allow_to_change_emp_exp_date FROM company WHERE id = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return rs.getBoolean(1);		                   
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }   
        return true;
    }
    
    public DataTable getOtherSettingsValues() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT allow_to_change_emp_exp_date "
                + "FROM COMPANY "
                + "WHERE ID = ? ";
        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getBoolean(1));
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
    
    public String getEmployeeDailyAllowanceAmtOpenedDate(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT debitted_amount "
                + "FROM employee_daily_allowance_debit "
                + "WHERE COMPANY_ID = ? "
                + "AND id = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return format.format(rs.getDate(1).toLocalDate());
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
    
    public String getEmployeeAdvAmtOpenedDate(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT debitted_date "
                + "FROM employee_advance_amount_debit "
                + "WHERE COMPANY_ID = ? "
                + "AND id = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return format.format(rs.getDate(1).toLocalDate());
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

    public String getEmployeeOtherAmtOpenedDate(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT debitted_date "
                + "FROM employee_other_amount_debit "
                + "WHERE COMPANY_ID = ? "
                + "AND id = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return format.format(rs.getDate(1).toLocalDate());
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

    public String getEmployeeSalAmtOpenedDate(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT debitted_date "
                + "FROM employee_salary_amount_debit "
                + "WHERE COMPANY_ID = ? "
                + "AND id = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return format.format(rs.getDate(1).toLocalDate());
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
    
}
