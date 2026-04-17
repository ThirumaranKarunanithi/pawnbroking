/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.repledgebillopening;

import com.magizhchi.pawnbroking.billcalculator.RepAllDetailsBean;
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
public class RepledgeBillOpeningDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public RepledgeBillOpeningDBOperation(String sDB, String sIP, String sPort, String sSchema,
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
            Logger.getLogger(RepledgeBillOpeningDBOperation.class.getName()).log(Level.SEVERE, null, ex);
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
            stmt.setString(2, "REPLEDGE");
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
    public String getPlanId(String screenName) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT OPERATION_NEXT_ID FROM OPERATION_ID_GENERATOR WHERE SCREEN_NAME = ? AND OPERATION_NAME = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, screenName);
            stmt.setString(2, "PLAN_ID");
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
        
        
        String sql = "SELECT ID, NAME FROM REPLEDGE WHERE STATUS = ?::REPLEDGE_STATUS ORDER BY NAME";

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
    
    public HashMap<String, String> getAllCompanyValues(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT CB.OPENING_DATE, CB.CUSTOMER_NAME, CB.GENDER, CB.SPOUSE_TYPE, CB.SPOUSE_NAME, " +
                    "CB.DOOR_NUMBER, CB.STREET, CB.AREA, CB.CITY, CB.MOBILE_NUMBER, CB.ITEMS, CB.GROSS_WEIGHT, CB.NET_WEIGHT, CB.PURITY, " +
                    "CB.AMOUNT, CB.INTEREST, CB.DOCUMENT_CHARGE, CB.OPEN_TAKEN_AMOUNT, CB.TOGIVE_AMOUNT, CB.GIVEN_AMOUNT, " +
                    "CB.STATUS, CB.NOTE " +                       
                    "FROM COMPANY_BILLING CB " +
                    "WHERE CB.STATUS IN ('OPENED', 'LOCKED') " +
                    "AND (CB.REPLEDGE_BILL_ID IS NULL OR CB.repledge_bill_id = '')" +
                    "AND CB.COMPANY_ID = ? " +
                    "AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND CB.BILL_NUMBER = ?";



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
                headerValues.put("GROSS_WEIGHT", Double.toString(rs.getDouble(12)));
                headerValues.put("NET_WEIGHT", Double.toString(rs.getDouble(13)));
                headerValues.put("PURITY", Double.toString(rs.getDouble(14)));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(15)));
                headerValues.put("INTEREST", Double.toString(rs.getDouble(16)));
                headerValues.put("DOCUMENT_CHARGE", Double.toString(rs.getDouble(17)));
                headerValues.put("OPEN_TAKEN_AMOUNT", Double.toString(rs.getDouble(18)));
                headerValues.put("TOGIVE_AMOUNT", Double.toString(rs.getDouble(19)));
                headerValues.put("GIVEN_AMOUNT", Double.toString(rs.getDouble(20)));
                headerValues.put("STATUS", rs.getString(21));                
                headerValues.put("NOTE", rs.getString(22));
                
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
    
    public String getInterest(String sDate, String sRepledgeId, double dAmount, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT INTEREST " +
                    "FROM REPLEDGE_INTEREST " +
                    "WHERE REPLEDGE_ID = ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND ? BETWEEN AMOUNT_FROM AND AMOUNT_TO "
                    + "AND ? BETWEEN DATE_FROM AND DATE_TO";

        try {

            java.sql.Date sqlDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, sRepledgeId);            
            stmt.setString(2, sMaterialType);
            stmt.setDouble(3, dAmount);
            stmt.setDate(4, sqlDateOpenDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return Double.toString(rs.getDouble(1));		                   
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }        
        return "0";
    }

    public String getDocumentCharge(String sDate, String sRepledgeId, double dAmount, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT DOCUMENT_CHARGE " +
                    "FROM REPLEDGE_DOCUMENT_CHARGE " +
                    "WHERE REPLEDGE_ID = ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND ? BETWEEN AMOUNT_FROM AND AMOUNT_TO "
                    + "AND ? BETWEEN DATE_FROM AND DATE_TO";

        try {

            java.sql.Date sqlDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, sRepledgeId);            
            stmt.setString(2, sMaterialType);
            stmt.setDouble(3, dAmount);
            stmt.setDate(4, sqlDateOpenDate);
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
        return "0";
    }

    public String getFormula(String sDate, String sRepledgeId, double dAmount, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT FORMULA " +
                    "FROM REPLEDGE_FORMULA " +
                    "WHERE REPLEDGE_ID = ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND FORMULA_OPERATION_TYPE = ?::OPERATION_TYPE " +
                    "AND ? BETWEEN AMOUNT_FROM AND AMOUNT_TO "
                + "AND ? BETWEEN DATE_FROM AND DATE_TO";

        try {

            java.sql.Date sqlDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, sRepledgeId);            
            stmt.setString(2, sMaterialType);
            stmt.setString(3, "OPEN");
            stmt.setDouble(4, dAmount);
            stmt.setDate(5, sqlDateOpenDate); 
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
        return "";
    }
    
    public boolean isvalidBillNumberToSave(String sRepledgeBillNumber, String sRepledgeId, String sRepledgeBillId, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT REPLEDGE_BILL_NUMBER "
                + "FROM REPLEDGE_BILLING "
                + "WHERE REPLEDGE_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND REPLEDGE_BILL_NUMBER = ? " 
                + "AND REPLEDGE_BILL_ID != ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sRepledgeId);            
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sRepledgeBillNumber);
            stmt.setString(4, sRepledgeBillId);
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
    
    public boolean saveRecord(String sRepledgeBillId, String sRepledgeId, String sRepledgeName, String sRepledgeBillNumber, 
                            String sRepledgeOpeningDate, String sCompanyBillNumber,
                            String sStatus, String sNote, double dAmount, 
                            double dInterest, double dDocumentCharge, double dTakenAmount, 
                            double dToGetAmount, double dGotAmount, String sAcceptedDate, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "INSERT INTO repledge_billing(repledge_bill_id, repledge_id, "
                + "repledge_name, repledge_bill_number, opening_date, jewel_material_type, "
                + "company_id, company_bill_number, amount, interest, document_charge, "
                + "open_taken_amount, toget_amount, got_amount, "
                + "created_user_id, status, note, CREATED_DATE, accepted_closing_date, remind_status) "
                + "VALUES (?, ?, ?, ?, ?, ?::material_type, ?, ?, ?, ?, ?, "
                + "?, ?, ?, ?, ?::repledge_bill_status, ?, now(), ?, ?);";
        
        try {

            java.sql.Date sqlDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sRepledgeOpeningDate, CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlAccDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sAcceptedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, sRepledgeBillId);
            stmt.setString(2, sRepledgeId);
            stmt.setString(3, sRepledgeName);
            stmt.setString(4, sRepledgeBillNumber);
            stmt.setDate(5, sqlDateOpenDate);
            stmt.setString(6, sMaterialType);
            stmt.setString(7, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(8, sCompanyBillNumber);
            stmt.setDouble(9, dAmount);
            stmt.setDouble(10, dInterest);
            stmt.setDouble(11, dDocumentCharge);
            stmt.setDouble(12, dTakenAmount);
            stmt.setDouble(13, dToGetAmount);
            stmt.setDouble(14, dGotAmount);            
            stmt.setString(15, CommonConstants.USERID);
            stmt.setString(16, sStatus);
            stmt.setString(17, sNote);
            stmt.setDate(18, sqlAccDateOpenDate);
            stmt.setString(19, CommonConstants.ACTIVE);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean updateRecordToRepledgeBilling(String sRepledgeBillId, 
            String sRepledgeId, 
                            String sRepledgeName, 
                            String sRepledgeBillNumber, 
                            String sRepledgeOpeningDate,
                            String sStatus, String sNote, double dAmount, 
                            double dInterest, double dDocumentCharge, double dTakenAmount, 
                            double dToGetAmount, double dGotAmount, String sAcceptedDate) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "update repledge_billing set "
                + "repledge_id = ?, "
                + "repledge_name = ?, "
                + "repledge_bill_number = ?, "
                + "opening_date = ?, "
                + "amount = ?, "
                + "interest = ?, "
                + "document_charge= ?, "
                + "open_taken_amount = ?, "
                + "toget_amount = ?, "
                + "got_amount = ?, "
                + "created_user_id = ?, "
                + "CREATED_DATE = now(), "
                + "status = ?::repledge_bill_status, "
                + "note = ?, "
                + "accepted_closing_date = ? "
                + "where repledge_bill_id = ? ";                
        
        try {

            java.sql.Date sqlDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sRepledgeOpeningDate, CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlAccDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sAcceptedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, sRepledgeId);
            stmt.setString(2, sRepledgeName);
            stmt.setString(3, sRepledgeBillNumber);
            stmt.setDate(4, sqlDateOpenDate);
            stmt.setDouble(5, dAmount);
            stmt.setDouble(6, dInterest);
            stmt.setDouble(7, dDocumentCharge);
            stmt.setDouble(8, dTakenAmount);
            stmt.setDouble(9, dToGetAmount);
            stmt.setDouble(10, dGotAmount);            
            stmt.setString(11, CommonConstants.USERID);
            stmt.setString(12, sStatus);
            stmt.setString(13, sNote);
            stmt.setDate(14, sqlAccDateOpenDate);
            stmt.setString(15, sRepledgeBillId);            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean updateRepledgeBillNumber(String sRepledgeBillId, 
            String sRepledgeId, String sRepledgeName,
            String sRepledgeBillNumber, String sStatus) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "update repledge_billing set "
                + "repledge_id = ?, "
                + "repledge_name = ?, "
                + "repledge_bill_number = ?,"
                + "status = ?::repledge_bill_status "
                + "where repledge_bill_id = ? ";                
        
        try {
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, sRepledgeId);
            stmt.setString(2, sRepledgeName);
            stmt.setString(3, sRepledgeBillNumber);
            stmt.setString(4, sStatus);   
            stmt.setString(5, sRepledgeBillId);            
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
            stmt.setString(3, "REPLEDGE");
            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    
    public boolean updateRepledgeBillIdToCompanyBilling(String sCompanyBillNumber, String sRepledgeBillId,  
                                String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;              
        
        String sql = "UPDATE COMPANY_BILLING SET "
                + "REPLEDGE_BILL_ID = ?, "
                + "STATUS = 'LOCKED', "
                + "physical_location = ? "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND STATUS IN ('OPENED', 'LOCKED') "
                + "AND BILL_NUMBER = ? ";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sRepledgeBillId);
            stmt.setString(2, CommonConstants.REPLEDGE_LOCKER);
            stmt.setString(3, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(4, sMaterialType);
            stmt.setString(5, sCompanyBillNumber);

            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public String getRepledgeBillNameForCompanyBillNumber(String sCompanyBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT RB.REPLEDGE_BILL_ID, RB.COMPANY_BILL_NUMBER, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.REPLEDGE_BILL_ID = CB.REPLEDGE_BILL_ID " +
                    "AND RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.STATUS IN ('OPENED', 'GIVEN', 'SUSPENSE') " +
                    "AND CB.STATUS IN ('OPENED', 'LOCKED', 'REBILLED', 'REBILLED-ADDED', 'REBILLED-REMOVED') " +
                    "AND CB.REPLEDGE_BILL_ID IS NOT NULL " +
                    "AND CB.COMPANY_ID = ? " +
                    "AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND CB.BILL_NUMBER = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sCompanyBillNumber);            
            rs = stmt.executeQuery();

            if(rs.next())
            {		    
                return rs.getString(3);		                   
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
 
    public HashMap<String, String> getAllHeaderValuesByRepledgeBillId(String sRepledgeBillId, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER, RB.OPENING_DATE, " +
                    "RB.COMPANY_BILL_NUMBER, CB.STATUS, CB.OPENING_DATE, CB.AMOUNT, CB.NOTE, CB.CUSTOMER_NAME, CB.GENDER, CB.SPOUSE_TYPE, CB.SPOUSE_NAME, " +
                    "CB.DOOR_NUMBER, CB.STREET, CB.AREA, CB.CITY, CB.MOBILE_NUMBER, CB.ITEMS, CB.GROSS_WEIGHT, CB.NET_WEIGHT, CB.PURITY, " +
                    "RB.AMOUNT, RB.INTEREST, RB.DOCUMENT_CHARGE, RB.OPEN_TAKEN_AMOUNT, "
                + "RB.TOGET_AMOUNT, RB.GOT_AMOUNT, RB.STATUS, RB.NOTE, RB.accepted_closing_date " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.REPLEDGE_BILL_ID = CB.REPLEDGE_BILL_ID " +
                    "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +
                    "AND RB.STATUS IN ('OPENED', 'GIVEN', 'SUSPENSE') " +
                    "AND CB.REPLEDGE_BILL_ID IS NOT NULL " +
                    "AND CB.COMPANY_ID = ? " +
                    "AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND CB.REPLEDGE_BILL_ID = ?";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sRepledgeBillId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(1));
                headerValues.put("REPLEDGE_ID", rs.getString(2));
                headerValues.put("REPLEDGE_NAME", rs.getString(3));
                headerValues.put("REPLEDGE_BILL_NUMBER", rs.getString(4));
                headerValues.put("REPLEDGE_OPENING_DATE", format.format(rs.getDate(5).toLocalDate()));
                headerValues.put("BILL_NUMBER", rs.getString(6));
                headerValues.put("STATUS", rs.getString(7));
                headerValues.put("OPENING_DATE", format.format(rs.getDate(8).toLocalDate()));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(9)));
                headerValues.put("NOTE", rs.getString(10));
                headerValues.put("CUSTOMER_NAME", rs.getString(11));
                headerValues.put("GENDER", rs.getString(12));
                headerValues.put("SPOUSE_TYPE", rs.getString(13));
                headerValues.put("SPOUSE_NAME", rs.getString(14));
                headerValues.put("DOOR_NUMBER", rs.getString(15));
                headerValues.put("STREET", rs.getString(16));
                headerValues.put("AREA", rs.getString(17));
                headerValues.put("CITY", rs.getString(18));
                headerValues.put("MOBILE_NUMBER", rs.getString(19));
                headerValues.put("ITEMS", rs.getString(20));
                headerValues.put("GROSS_WEIGHT", Double.toString(rs.getDouble(21)));
                headerValues.put("NET_WEIGHT", Double.toString(rs.getDouble(22)));
                headerValues.put("PURITY", Double.toString(rs.getDouble(23)));
                headerValues.put("REPLEDGE_AMOUNT", Double.toString(rs.getDouble(24)));
                headerValues.put("REPLEDGE_INTEREST", Double.toString(rs.getDouble(25)));
                headerValues.put("REPLEDGE_DOCUMENT_CHARGE", Double.toString(rs.getDouble(26)));
                headerValues.put("REPLEDGE_OPEN_TAKEN_AMOUNT", Double.toString(rs.getDouble(27)));
                headerValues.put("REPLEDGE_OPEN_TOGET_AMOUNT", Double.toString(rs.getDouble(28)));
                headerValues.put("REPLEDGE_GOT_AMOUNT", Double.toString(rs.getDouble(29)));
                headerValues.put("REPLEDGE_STATUS", rs.getString(30));
                headerValues.put("REPLEDGE_NOTE", rs.getString(31));
                if(rs.getDate(32) != null) {
                    headerValues.put("REPLEDGE_ACC_CLOSING_DATE", format.format(rs.getDate(32).toLocalDate()));
                }
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

    public HashMap<String, String> getAllHeaderValuesByRepledgeBillNumber(String sRepledgeId, String sRepledgeBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER, RB.OPENING_DATE, " +
                    "CB.BILL_NUMBER, CB.STATUS, CB.OPENING_DATE, CB.AMOUNT, CB.NOTE, CB.CUSTOMER_NAME, CB.GENDER, CB.SPOUSE_TYPE, CB.SPOUSE_NAME, " +
                    "CB.DOOR_NUMBER, CB.STREET, CB.AREA, CB.CITY, CB.MOBILE_NUMBER, CB.ITEMS, CB.GROSS_WEIGHT, CB.NET_WEIGHT, CB.PURITY, " +
                    "RB.AMOUNT, RB.INTEREST, RB.DOCUMENT_CHARGE, RB.OPEN_TAKEN_AMOUNT, RB.TOGET_AMOUNT, RB.GOT_AMOUNT, RB.STATUS, RB.NOTE " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.REPLEDGE_BILL_ID = CB.REPLEDGE_BILL_ID " +
                    "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +
                    "AND RB.STATUS IN ('OPENED', 'GIVEN', 'SUSPENSE') " +
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
                headerValues.put("REPLEDGE_OPENING_DATE", format.format(rs.getDate(5).toLocalDate()));
                headerValues.put("BILL_NUMBER", rs.getString(6));
                headerValues.put("STATUS", rs.getString(7));
                headerValues.put("OPENING_DATE", format.format(rs.getDate(8).toLocalDate()));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(9)));
                headerValues.put("NOTE", rs.getString(10));
                headerValues.put("CUSTOMER_NAME", rs.getString(11));
                headerValues.put("GENDER", rs.getString(12));
                headerValues.put("SPOUSE_TYPE", rs.getString(13));
                headerValues.put("SPOUSE_NAME", rs.getString(14));
                headerValues.put("DOOR_NUMBER", rs.getString(15));
                headerValues.put("STREET", rs.getString(16));
                headerValues.put("AREA", rs.getString(17));
                headerValues.put("CITY", rs.getString(18));
                headerValues.put("MOBILE_NUMBER", rs.getString(19));
                headerValues.put("ITEMS", rs.getString(20));
                headerValues.put("GROSS_WEIGHT", Double.toString(rs.getDouble(21)));
                headerValues.put("NET_WEIGHT", Double.toString(rs.getDouble(22)));
                headerValues.put("PURITY", Double.toString(rs.getDouble(23)));
                headerValues.put("REPLEDGE_AMOUNT", Double.toString(rs.getDouble(24)));
                headerValues.put("REPLEDGE_INTEREST", Double.toString(rs.getDouble(25)));
                headerValues.put("REPLEDGE_DOCUMENT_CHARGE", Double.toString(rs.getDouble(26)));
                headerValues.put("REPLEDGE_OPEN_TAKEN_AMOUNT", Double.toString(rs.getDouble(27)));
                headerValues.put("REPLEDGE_OPEN_TOGET_AMOUNT", Double.toString(rs.getDouble(28)));
                headerValues.put("REPLEDGE_GOT_AMOUNT", Double.toString(rs.getDouble(29)));
                headerValues.put("REPLEDGE_STATUS", rs.getString(30));
                headerValues.put("REPLEDGE_NOTE", rs.getString(31));
                
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
        
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER, RB.OPENING_DATE, " +
                    "RB.COMPANY_BILL_NUMBER, CB.STATUS, CB.OPENING_DATE, CB.AMOUNT, CB.NOTE, CB.CUSTOMER_NAME, CB.GENDER, CB.SPOUSE_TYPE, CB.SPOUSE_NAME, " +
                    "CB.DOOR_NUMBER, CB.STREET, CB.AREA, CB.CITY, CB.MOBILE_NUMBER, CB.ITEMS, CB.GROSS_WEIGHT, CB.NET_WEIGHT, CB.PURITY, " +
                    "RB.AMOUNT, RB.INTEREST, RB.DOCUMENT_CHARGE, RB.OPEN_TAKEN_AMOUNT, "
                + "RB.TOGET_AMOUNT, RB.GOT_AMOUNT, RB.STATUS, RB.NOTE, RB.accepted_closing_date " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB  " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.REPLEDGE_BILL_ID = CB.REPLEDGE_BILL_ID " +
                    "AND RB.STATUS IN ('OPENED', 'GIVEN', 'SUSPENSE') " +
                    "AND CB.REPLEDGE_BILL_ID IS NOT NULL " +
                    "AND CB.COMPANY_ID = ? " +
                    "AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
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
                headerValues.put("REPLEDGE_OPENING_DATE", format.format(rs.getDate(5).toLocalDate()));
                headerValues.put("BILL_NUMBER", rs.getString(6));
                headerValues.put("STATUS", rs.getString(7));
                headerValues.put("OPENING_DATE", format.format(rs.getDate(8).toLocalDate()));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(9)));
                headerValues.put("NOTE", rs.getString(10));
                headerValues.put("CUSTOMER_NAME", rs.getString(11));
                headerValues.put("GENDER", rs.getString(12));
                headerValues.put("SPOUSE_TYPE", rs.getString(13));
                headerValues.put("SPOUSE_NAME", rs.getString(14));
                headerValues.put("DOOR_NUMBER", rs.getString(15));
                headerValues.put("STREET", rs.getString(16));
                headerValues.put("AREA", rs.getString(17));
                headerValues.put("CITY", rs.getString(18));
                headerValues.put("MOBILE_NUMBER", rs.getString(19));
                headerValues.put("ITEMS", rs.getString(20));
                headerValues.put("GROSS_WEIGHT", Double.toString(rs.getDouble(21)));
                headerValues.put("NET_WEIGHT", Double.toString(rs.getDouble(22)));
                headerValues.put("PURITY", Double.toString(rs.getDouble(23)));
                headerValues.put("REPLEDGE_AMOUNT", Double.toString(rs.getDouble(24)));
                headerValues.put("REPLEDGE_INTEREST", Double.toString(rs.getDouble(25)));
                headerValues.put("REPLEDGE_DOCUMENT_CHARGE", Double.toString(rs.getDouble(26)));
                headerValues.put("REPLEDGE_OPEN_TAKEN_AMOUNT", Double.toString(rs.getDouble(27)));
                headerValues.put("REPLEDGE_OPEN_TOGET_AMOUNT", Double.toString(rs.getDouble(28)));
                headerValues.put("REPLEDGE_GOT_AMOUNT", Double.toString(rs.getDouble(29)));
                headerValues.put("REPLEDGE_STATUS", rs.getString(30));
                headerValues.put("REPLEDGE_NOTE", rs.getString(31));
                if(rs.getDate(32) != null) {
                    headerValues.put("REPLEDGE_ACC_CLOSING_DATE", format.format(rs.getDate(32).toLocalDate()));
                }
                
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
    
    public DataTable getAllDetailsValues(String sMaterialType, String sFilterScript, String...sVals) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER, " +
                    "RB.OPENING_DATE, RB.AMOUNT, " +
                    "RB.STATUS, CB.BILL_NUMBER, CB.OPENING_DATE, CB.AMOUNT, CB.STATUS,"
                    + " REGEXP_REPLACE(COALESCE(RB.REPLEDGE_BILL_ID, '0'), '[^0-9]*' ,'0')::integer BILL " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.REPLEDGE_BILL_ID = CB.REPLEDGE_BILL_ID " +
                    "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +
                    "AND RB.COMPANY_ID = ? " +
                    "AND RB.STATUS IN ('OPENED', 'GIVEN', 'SUSPENSE') " +
                    "AND RB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";
                    //+ "ORDER BY RB.OPENING_DATE DESC, RB.REPLEDGE_NAME";

        try {

            if(sFilterScript != null) {
                sql += sFilterScript;
            }
            
            sql = sql + " ORDER BY RB.OPENING_DATE DESC";
            
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);

            if(sFilterScript != null) {
                for(int i=0; i<sVals.length; i++) {
                    if(sFilterScript.contains("GENDER =") || sFilterScript.contains("CB.STATUS =") || sFilterScript.contains("RB.STATUS =")) {
                        stmt.setString(i+3, sVals[i]);
                    } else {
                        stmt.setString(i+3, "%"+sVals[i]+"%");
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
                row.addColumn(format.format(rs.getDate(4).toLocalDate()));
                row.addColumn(Double.toString(rs.getDouble(5)));
                row.addColumn(rs.getString(6));
                row.addColumn(rs.getString(7));
                row.addColumn(format.format(rs.getDate(8).toLocalDate()));                
                row.addColumn(Double.toString(rs.getDouble(9)));
                row.addColumn(rs.getString(10));
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
    
    public DataTable getOtherSettingsValues(String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ALLOW_TO_CHANGE_REPLEDGE_BILL_OPENING_DATE, ALLOW_TO_CHANGE_REPLEDGE_NAME_IN_OPENING, "
                + "ALLOW_TO_CHANGE_REPLEDGE_BILL_OPENING_AMOUNT "
                + "FROM COMPANY_OTHER_SETTINGS "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getBoolean(1));
                row.addColumn(rs.getBoolean(2));
                row.addColumn(rs.getBoolean(3));
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
    
    public String[] getGoldCurrentBillNumber() throws SQLException
    {

        String[] sBillNumber = new String[5];
        
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT GOLD_CUR_BILL_ROW_NUMBER, " +
                    "GOLD_CUR_BILL_PREFIX, COALESCE(GOLD_CUR_BILL_NUMBER,'0'), AUTO_BILL_GENERATION, TYPE " +
                    "FROM COMPANY " +
                    "WHERE id = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                sBillNumber[0] = rs.getString(1);
                sBillNumber[1] = rs.getString(2);
                sBillNumber[2] = rs.getString(3);
                sBillNumber[3] = Boolean.toString(rs.getBoolean(4));
                sBillNumber[4] = rs.getString(5);
                return sBillNumber;		                   
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
 
    public String[] getSilverCurrentBillNumber() throws SQLException
    {

        String[] sBillNumber = new String[4];
        
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT SILVER_CUR_BILL_ROW_NUMBER, " +
                    "SILVER_CUR_BILL_PREFIX, COALESCE(SILVER_CUR_BILL_NUMBER,'0'), AUTO_BILL_GENERATION " +
                    "FROM COMPANY " +
                    "WHERE id = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                sBillNumber[0] = rs.getString(1);
                sBillNumber[1] = rs.getString(2);
                sBillNumber[2] = rs.getString(3);
                sBillNumber[3] = Boolean.toString(rs.getBoolean(4));
                return sBillNumber;		                   
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
    
    public String getRepIds(String sCompBillNumber) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT REPLEDGE_BILL_ID FROM COMPANY_BILLING WHERE BILL_NUMBER = ? and COMPANY_ID = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sCompBillNumber);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
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
    
    public String getRepBillOpenedDate(String sRepBillId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT opening_date "
                + "FROM repledge_billing "
                + "WHERE COMPANY_ID = ? "
                + "AND repledge_bill_id = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sRepBillId);
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

    public boolean isRepPrinted(String sRepPlannerId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT is_printed "
                + "FROM repledge_bill_calc_header "
                + "WHERE COMPANY_ID = ? "
                + "AND planner_id = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sRepPlannerId);
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
        return false;
    }

    public String getRepBillCalcClosingDate(String sRepPlannerId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT closing_date "
                + "FROM repledge_bill_calc_header "
                + "WHERE COMPANY_ID = ? "
                + "AND planner_id = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sRepPlannerId);
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
    
    public DataTable getAllHeaderValuesByRepledgePlannerId(String sRepledgePlannerId, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        DataTable dataTable = new DataTable();
        
        String sql = "SELECT planner_id, company_id, before_after, jewel_material_type, repledge_bill_id, operation, "
                + "company_bill_number, repledge_opening_date, repledge_bill_number, repledge_id, repledge_name, "
                + "items, amount, interest, interested_amount, total_interested_amount, created_user_id, created_date, "
                + "closing_date, last_updated_user_id, last_updated_date, status, note, company_actual_bill_number " +
                    "FROM repledge_bill_calc "
                + "where company_id = ? "
                + "and jewel_material_type = ?::MATERIAL_TYPE "
                + "and planner_id = ? ";


        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sRepledgePlannerId);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final RepAllDetailsBean bean = new RepAllDetailsBean(rs.getString(3), rs.getString(6), rs.getString(7), 
                        format.format(rs.getDate(8).toLocalDate()), rs.getString(9), rs.getString(11), 
                        rs.getString(4), rs.getString(12), Double.toString(rs.getDouble(13)), 
                        Double.toString(rs.getDouble(14)), Double.toString(rs.getDouble(15)), 
                        Double.toString(rs.getDouble(16)), rs.getString(10), rs.getString(5), 
                        rs.getString(24), rs.getString(22), true);

                final DataRow row = new DataRow();
                row.addColumn(bean);
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
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
        
}
