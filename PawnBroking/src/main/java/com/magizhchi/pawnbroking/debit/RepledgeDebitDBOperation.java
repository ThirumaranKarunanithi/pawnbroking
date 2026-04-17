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
public class RepledgeDebitDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public RepledgeDebitDBOperation(String sDB, String sIP, String sPort, String sSchema,
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
            Logger.getLogger(RepledgeDebitDBOperation.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public DataTable getAllRepledgeNames() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ID, NAME FROM REPLEDGE WHERE STATUS = ?::REPLEDGE_STATUS";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, "ACTIVE");
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
    
    public boolean setNextId(String screenName, String sNextIdWithPrefix) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE OPERATION_ID_GENERATOR SET OPERATION_NEXT_ID = ?  WHERE SCREEN_NAME = ? AND OPERATION_NAME = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, sNextIdWithPrefix);
            stmt.setString(2, screenName);
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
     
    public HashMap<String, String> getAllBillingValues(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT OPENING_DATE, CUSTOMER_NAME, GENDER, SPOUSE_TYPE, SPOUSE_NAME, "
                + "DOOR_NUMBER, STREET, AREA, CITY, MOBILE_NUMBER, ITEMS, COALESCE(GROSS_WEIGHT, 0), "
                + "COALESCE(NET_WEIGHT, 0), PURITY, "
                + "COALESCE(AMOUNT, 0), COALESCE(TOGIVE_AMOUNT, 0), COALESCE(GIVEN_AMOUNT, 0), "
                + "STATUS, NOTE, COALESCE(CLOSING_DATE, NOW()), COALESCE(TOTAL_ADVANCE_AMOUNT_PAID, 0), "
                + "REPLEDGE_BILL_ID, REBILLED_TO, REBILLED_FROM, "
                + "COALESCE(TOGET_AMOUNT, 0), COALESCE(GOT_AMOUNT, 0) "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND BILL_NUMBER = ? ";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sBillNumber);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("BILL_NUMBER", sBillNumber);
                headerValues.put("OPENING_DATE", format.format(rs.getDate(1).toLocalDate()));
                headerValues.put("CUSTOMER_NAME", rs.getString(2));
                headerValues.put("GENDER", rs.getString(3));
                headerValues.put("SPOUSE_TYPE", rs.getString(4));
                headerValues.put("SPOUSE_NAME", rs.getString(5));
                headerValues.put("DOOR_NUMBER", rs.getString(6));
                headerValues.put("STREET", rs.getString(7));
                headerValues.put("AREA", rs.getString(8));
                headerValues.put("CITY", rs.getString(9));
                headerValues.put("MOBILE_NUMBER", rs.getString(10));
                headerValues.put("ITEMS", rs.getString(11));
                headerValues.put("GROSS_WEIGHT", rs.getString(12));
                headerValues.put("NET_WEIGHT", rs.getString(13));
                headerValues.put("PURITY", rs.getString(14));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(15)));
                headerValues.put("TOGIVE_AMOUNT", Double.toString(rs.getDouble(16)));
                headerValues.put("GIVEN_AMOUNT", Double.toString(rs.getDouble(17)));
                headerValues.put("STATUS", rs.getString(18));
                headerValues.put("NOTE", rs.getString(19));
                headerValues.put("CLOSING_DATE", format.format(rs.getDate(20).toLocalDate()));
                headerValues.put("TOTAL_ADVANCE_AMOUNT_PAID", Double.toString(rs.getDouble(21)));
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(22));
                headerValues.put("REBILLED_TO", rs.getString(23));
                headerValues.put("REBILLED_FROM", rs.getString(24));
                headerValues.put("TOGET_AMOUNT", rs.getString(25));
                headerValues.put("GOT_AMOUNT", rs.getString(26));
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
    
    public boolean saveBillDebit(String sId, String sMaterialType,
                            String sRepledgeBillNumber, String sRepledgeBillStatus, 
                            String sBillNumber, String sStatus, 
                            double dRepledgeBillAmount, double dBillAmount,
                            String sDebittedDate, double dToDebittedAmount, 
                            double dDebittedAmount, String sNote,
                            String sRepledgeId, String sRepledgeName) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "INSERT INTO repledge_bill_debit( " +
                    "id, company_id, jewel_material_type, repledge_bill_number, repledge_bill_status,  " +
                    "bill_number, bill_status, repledge_bill_amount, bill_amount,  " +
                    "debitted_date, to_be_debitted_amount, debitted_amount, created_date,  " +
                    "user_id, note, repledge_id, repledge_name) " +
                    "VALUES (?, ?, ?::MATERIAL_TYPE, ?, ?::REPLEDGE_BILL_STATUS, ?, ?::COMPANY_BILL_STATUS, ?, ?, ?, ?, ?, now(), ?, ?, ?, ?);";
        
        try {

            java.sql.Date sqlDateDebittedDate = java.sql.Date.valueOf(LocalDate.parse(sDebittedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, sId);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, sMaterialType);
            stmt.setString(4, sRepledgeBillNumber);
            stmt.setString(5, sRepledgeBillStatus);
            stmt.setString(6, sBillNumber);
            stmt.setString(7, sStatus);
            stmt.setDouble(8, dRepledgeBillAmount);
            stmt.setDouble(9, dBillAmount);
            stmt.setDate(10, sqlDateDebittedDate);
            stmt.setDouble(11, dToDebittedAmount);
            stmt.setDouble(12, dDebittedAmount);
            stmt.setString(13, CommonConstants.USERID);
            stmt.setString(14, sNote);
            stmt.setString(15, sRepledgeId);
            stmt.setString(16, sRepledgeName);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean saveROBillDebit(String sId, String sRepledgeId, String sRepledgeName, String sDebittedDate, String sExpenseType, 
                            String sName, String sReason, String sNote, String sInvoiceNo,
                            double dInvoiceAmount, double dDebittedAmount) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "INSERT INTO repledge_other_debit( " +
                    "id, company_id, repledge_id, repledge_name, expense_type, name, " +
                    "reason, invoice_no, invoice_amount, debitted_date, " +
                    "debitted_amount, note, user_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {

            java.sql.Date sqlDateDebittedDate = java.sql.Date.valueOf(LocalDate.parse(sDebittedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, sId);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, sRepledgeId);
            stmt.setString(4, sRepledgeName);
            stmt.setString(5, sExpenseType);
            stmt.setString(6, sName);
            stmt.setString(7, sReason);
            stmt.setString(8, sInvoiceNo);
            stmt.setDouble(9, dInvoiceAmount);
            stmt.setDate(10, sqlDateDebittedDate);
            stmt.setDouble(11, dDebittedAmount);
            stmt.setString(12, sNote);
            stmt.setString(13, CommonConstants.USERID);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public HashMap<String, String> getAllHeaderValuesByRepledgeBillNumber(String sRepledgeId, String sRepledgeBillNumber, String sMaterialType) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER, " +
                    "CB.BILL_NUMBER, CB.STATUS, CB.OPENING_DATE, CB.CLOSING_DATE, CB.AMOUNT, " +
                    "CB.ITEMS, RB.STATUS, RB.OPENING_DATE, RB.CLOSING_DATE, RB.AMOUNT, " +
                    "RB.TOGET_AMOUNT, RB.GOT_AMOUNT, RB.TOGIVE_AMOUNT, RB.GIVEN_AMOUNT " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +
                    "AND RB.COMPANY_ID = ? " +
                    "AND RB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND RB.REPLEDGE_ID = ? " +
                    "AND RB.REPLEDGE_BILL_NUMBER = ?";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sRepledgeId);
            stmt.setString(4, sRepledgeBillNumber);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(1));
                headerValues.put("REPLEDGE_ID", rs.getString(2));
                headerValues.put("REPLEDGE_NAME", rs.getString(3));
                headerValues.put("REPLEDGE_BILL_NUMBER", rs.getString(4));
                headerValues.put("BILL_NUMBER", rs.getString(5));
                headerValues.put("STATUS", rs.getString(6));
                headerValues.put("OPENING_DATE", format.format(rs.getDate(7).toLocalDate()));
                headerValues.put("CLOSING_DATE", format.format(rs.getDate(8).toLocalDate()));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(9)));
                headerValues.put("ITEMS", rs.getString(10));
                headerValues.put("REPLEDGE_STATUS", rs.getString(11));
                headerValues.put("REPLEDGE_OPENING_DATE", format.format(rs.getDate(12).toLocalDate()));
                headerValues.put("REPLEDGE_CLOSING_DATE", format.format(rs.getDate(13).toLocalDate()));
                headerValues.put("REPLEDGE_AMOUNT", Double.toString(rs.getDouble(14)));
                headerValues.put("REPLEDGE_TOGET_AMOUNT", Double.toString(rs.getDouble(15)));
                headerValues.put("REPLEDGE_GOT_AMOUNT", Double.toString(rs.getDouble(16)));
                headerValues.put("REPLEDGE_TOGIVE_AMOUNT", Double.toString(rs.getDouble(17)));
                headerValues.put("REPLEDGE_GIVEN_AMOUNT", Double.toString(rs.getDouble(18)));
                
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
    
    public HashMap<String, String> getAllHeaderValuesByCompanyBillNumber(String sCompanyBillNumber, String sMaterialType) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER, " +
                    "CB.BILL_NUMBER, CB.STATUS, CB.OPENING_DATE, COALESCE(CB.CLOSING_DATE, NOW()), CB.AMOUNT, " +
                    "CB.ITEMS, RB.STATUS, RB.OPENING_DATE, COALESCE(RB.CLOSING_DATE, NOW()), RB.AMOUNT, " +
                    "RB.TOGET_AMOUNT, RB.GOT_AMOUNT, RB.TOGIVE_AMOUNT, RB.GIVEN_AMOUNT " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +
                    "AND RB.COMPANY_ID = ? " +
                    "AND RB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND CB.BILL_NUMBER = ?";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sCompanyBillNumber);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(1));
                headerValues.put("REPLEDGE_ID", rs.getString(2));
                headerValues.put("REPLEDGE_NAME", rs.getString(3));
                headerValues.put("REPLEDGE_BILL_NUMBER", rs.getString(4));
                headerValues.put("BILL_NUMBER", rs.getString(5));
                headerValues.put("STATUS", rs.getString(6));
                headerValues.put("OPENING_DATE", format.format(rs.getDate(7).toLocalDate()));
                headerValues.put("CLOSING_DATE", format.format(rs.getDate(8).toLocalDate()));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(9)));
                headerValues.put("ITEMS", rs.getString(10));
                headerValues.put("REPLEDGE_STATUS", rs.getString(11));
                headerValues.put("REPLEDGE_OPENING_DATE", format.format(rs.getDate(12).toLocalDate()));
                headerValues.put("REPLEDGE_CLOSING_DATE", format.format(rs.getDate(13).toLocalDate()));
                headerValues.put("REPLEDGE_AMOUNT", Double.toString(rs.getDouble(14)));
                headerValues.put("REPLEDGE_TOGET_AMOUNT", Double.toString(rs.getDouble(15)));
                headerValues.put("REPLEDGE_GOT_AMOUNT", Double.toString(rs.getDouble(16)));
                headerValues.put("REPLEDGE_TOGIVE_AMOUNT", Double.toString(rs.getDouble(17)));
                headerValues.put("REPLEDGE_GIVEN_AMOUNT", Double.toString(rs.getDouble(18)));
                
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
    
    public boolean saveRBBillDebit(String sId, String sMaterialType, String sRepledgeId, String sRepledgeName, 
                            String sRepledgeBillNumber, String sRepledgeStatus, double dRepledgeBillAmount,
                            String sBillNumber, String sStatus, double dBillAmount,
                            String sDebittedDate, double dDebittedAmount, String sNote) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "INSERT INTO repledge_bill_debit(" +
                    "id, company_id, jewel_material_type, repledge_bill_number, repledge_bill_status, " +
                    "bill_number, bill_status, repledge_bill_amount, bill_amount, " +
                    "debitted_date, debitted_amount, " +
                    "user_id, note, repledge_id, repledge_name)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {

            java.sql.Date sqlDateDebittedDate = java.sql.Date.valueOf(LocalDate.parse(sDebittedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, sId);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, sMaterialType);
            stmt.setString(4, sRepledgeBillNumber);
            stmt.setString(5, sRepledgeStatus);
            stmt.setString(6, sBillNumber);
            stmt.setString(7, sStatus);
            stmt.setDouble(8, dRepledgeBillAmount);
            stmt.setDouble(9, dBillAmount);
            stmt.setDate(10, sqlDateDebittedDate);
            stmt.setDouble(11, dDebittedAmount);
            stmt.setString(12, CommonConstants.USERID);
            stmt.setString(13, sNote);
            stmt.setString(14, sRepledgeId);
            stmt.setString(15, sRepledgeName);
            
            
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
    
    public HashMap<String, String> getAllRepBillingHeaderValues(String sDebitId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER, " +
                    "CB.BILL_NUMBER, CB.STATUS, CB.OPENING_DATE, COALESCE(CB.CLOSING_DATE, NOW()), CB.AMOUNT, " +
                    "CB.ITEMS, RB.STATUS, RB.OPENING_DATE, COALESCE(RB.CLOSING_DATE, NOW()), RB.AMOUNT, " +
                    "RB.TOGET_AMOUNT, RB.GOT_AMOUNT, RB.TOGIVE_AMOUNT, RB.GIVEN_AMOUNT, RBD.debitted_date, " +
                    "RBD.to_be_debitted_amount, RBD.debitted_amount, RBD.ID " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB, repledge_bill_debit RBD " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +                    
                    "AND RB.JEWEL_MATERIAL_TYPE = RBD.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_BILL_NUMBER = RBD.BILL_NUMBER " +                    
                    "AND RB.COMPANY_ID = ? " +
                    "AND RBD.ID = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sDebitId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	                
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(1));
                headerValues.put("REPLEDGE_ID", rs.getString(2));
                headerValues.put("REPLEDGE_NAME", rs.getString(3));
                headerValues.put("REPLEDGE_BILL_NUMBER", rs.getString(4));
                headerValues.put("BILL_NUMBER", rs.getString(5));
                headerValues.put("STATUS", rs.getString(6));
                headerValues.put("OPENING_DATE", format.format(rs.getDate(7).toLocalDate()));
                headerValues.put("CLOSING_DATE", format.format(rs.getDate(8).toLocalDate()));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(9)));
                headerValues.put("ITEMS", rs.getString(10));
                headerValues.put("REPLEDGE_STATUS", rs.getString(11));
                headerValues.put("REPLEDGE_OPENING_DATE", format.format(rs.getDate(12).toLocalDate()));
                headerValues.put("REPLEDGE_CLOSING_DATE", format.format(rs.getDate(13).toLocalDate()));
                headerValues.put("REPLEDGE_AMOUNT", Double.toString(rs.getDouble(14)));
                headerValues.put("REPLEDGE_TOGET_AMOUNT", Double.toString(rs.getDouble(15)));
                headerValues.put("REPLEDGE_GOT_AMOUNT", Double.toString(rs.getDouble(16)));
                headerValues.put("REPLEDGE_TOGIVE_AMOUNT", Double.toString(rs.getDouble(17)));
                headerValues.put("REPLEDGE_GIVEN_AMOUNT", Double.toString(rs.getDouble(18)));
                headerValues.put("DEBITTED_DATE", format.format(rs.getDate(19).toLocalDate()));
                headerValues.put("TO_BE_DEBITTED_AMOUNT", Double.toString(rs.getDouble(20)));
                headerValues.put("DEBITTED_AMOUNT", Double.toString(rs.getDouble(21)));
                headerValues.put("ID", rs.getString(22));                
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
    
    public boolean updateBillDebit(String sId, double dDebittedAmount, String sNote, String sDebittedDate) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE repledge_bill_debit SET " +
                    "debitted_date = ?, debitted_amount = ?, note = ?, user_id = ? " +
                    "WHERE id = ? " +
                    "AND company_id = ?";
        
        try {

            java.sql.Date sqlDateDebittedDate = java.sql.Date.valueOf(LocalDate.parse(sDebittedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setDate(1, sqlDateDebittedDate);
            stmt.setDouble(2, dDebittedAmount);
            stmt.setString(3, sNote);            
            stmt.setString(4, CommonConstants.USERID);
            stmt.setString(5, sId);
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
    
    public HashMap<String, String> getAllROHeaderValues(String sDebitId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT id, company_id, expense_type, name, reason, invoice_no, invoice_amount, " +
                     "debitted_date, debitted_amount, note, repledge_id, repledge_name " +
                     "FROM repledge_other_debit " + 
                     "WHERE company_id = ? " +
                     "AND ID = ?";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sDebitId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	                
                headerValues.put("DEBIT_ID", rs.getString(1));
                headerValues.put("COMPANY_ID", rs.getString(2));
                headerValues.put("EXPENSE_TYPE", rs.getString(3));
                headerValues.put("NAME", rs.getString(4));
                headerValues.put("REASON", rs.getString(5));
                headerValues.put("INVOICE_NUMBER", rs.getString(6));
                headerValues.put("INVOICE_AMOUNT", rs.getString(7));
                headerValues.put("DEBITTED_DATE", format.format(rs.getDate(8).toLocalDate()));
                headerValues.put("DEBITTED_AMOUNT", Double.toString(rs.getDouble(9)));
                headerValues.put("NOTE", rs.getString(10));
                headerValues.put("REPLEDGE_ID", rs.getString(11));
                headerValues.put("REPLEDGE_NAME", rs.getString(12));
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
    
    public boolean updateROBillDebit(String sId, String sDebittedDate, String sNote, String sInvoiceNo,
                            double dInvoiceAmount, double dDebittedAmount,
                            String sRepledgeId, String sRepledgeName) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE repledge_other_debit SET " +
                    "debitted_date = ?, debitted_amount = ?, " +
                    "invoice_no = ?, invoice_amount = ?, " +
                    "note = ?, repledge_id = ?, repledge_name = ?, user_id = ? " +                
                    "WHERE company_id = ? " +
                    "AND ID = ?";
        
        try {

            java.sql.Date sqlDateDebittedDate = java.sql.Date.valueOf(LocalDate.parse(sDebittedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setDate(1, sqlDateDebittedDate);
            stmt.setDouble(2, dDebittedAmount);
            stmt.setString(3, sInvoiceNo);
            stmt.setDouble(4, dInvoiceAmount);
            stmt.setString(5, sNote);
            stmt.setString(6, sRepledgeId);
            stmt.setString(7, sRepledgeName);
            stmt.setString(8, CommonConstants.USERID);
            stmt.setString(9, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(10, sId);
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean allowToChangeDate() throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT allow_to_change_rep_exp_date FROM company WHERE id = ? ";

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
        
        
        String sql = "SELECT allow_to_change_rep_exp_date "
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
    
    public String getCompanyCreditOpenedDate(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT debitted_date "
                + "FROM repledge_bill_debit "
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
    
    public String getROCompanyCreditOpenedDate(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT debitted_date "
                + "FROM repledge_other_debit "
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
