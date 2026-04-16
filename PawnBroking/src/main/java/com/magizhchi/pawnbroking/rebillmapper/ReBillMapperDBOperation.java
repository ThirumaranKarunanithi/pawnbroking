/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.rebillmapper;

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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tiru
 */
public class ReBillMapperDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public ReBillMapperDBOperation(String sDB, String sIP, String sPort, String sSchema,
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
            Logger.getLogger(ReBillMapperDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
           
    public HashMap<String, String> getClosedBillingValues(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT OPENING_DATE, CUSTOMER_NAME, GENDER, SPOUSE_TYPE, SPOUSE_NAME, "
                + "DOOR_NUMBER, STREET, AREA, CITY, MOBILE_NUMBER, ITEMS, COALESCE(GROSS_WEIGHT, 0), "
                + "COALESCE(NET_WEIGHT, 0), PURITY, "
                + "COALESCE(AMOUNT, 0), COALESCE(INTEREST, 0), COALESCE(DOCUMENT_CHARGE, 0), "
                + "STATUS, NOTE, CLOSING_DATE, COALESCE(TOTAL_ADVANCE_AMOUNT_PAID, 0), REPLEDGE_BILL_ID, REBILLED_TO, REBILLED_FROM, GOT_AMOUNT  "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND STATUS IN ('CLOSED', 'DELIVERED', 'REBILLED', 'REBILLED-ADDED', 'REBILLED-REMOVED') "
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
                headerValues.put("INTEREST", Double.toString(rs.getDouble(16)));
                headerValues.put("DOCUMENT_CHARGE", Double.toString(rs.getDouble(17)));
                headerValues.put("STATUS", rs.getString(18));
                headerValues.put("NOTE", rs.getString(19));
                headerValues.put("CLOSING_DATE", format.format(rs.getDate(20).toLocalDate()));
                headerValues.put("TOTAL_ADVANCE_AMOUNT_PAID", Double.toString(rs.getDouble(21)));
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(22));
                headerValues.put("REBILLED_TO", rs.getString(23));
                headerValues.put("REBILLED_FROM", rs.getString(24));
                headerValues.put("GOT_AMOUNT", Double.toString(rs.getDouble(25)));
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
    
    public HashMap<String, String> getAllHeaderValuesByRepledgeBillId(String sRepledgeBillId, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.REPLEDGE_BILL_NUMBER, RB.OPENING_DATE, " +
                    "RB.COMPANY_BILL_NUMBER, CB.STATUS, CB.OPENING_DATE, CB.AMOUNT, CB.NOTE, CB.CUSTOMER_NAME, CB.GENDER, CB.SPOUSE_TYPE, CB.SPOUSE_NAME, " +
                    "CB.DOOR_NUMBER, CB.STREET, CB.AREA, CB.CITY, CB.MOBILE_NUMBER, CB.ITEMS, CB.GROSS_WEIGHT, CB.NET_WEIGHT, CB.PURITY, " +
                    "RB.AMOUNT, RB.INTEREST, RB.DOCUMENT_CHARGE, RB.OPEN_TAKEN_AMOUNT, RB.TOGET_AMOUNT, RB.GOT_AMOUNT, RB.STATUS, RB.NOTE " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.REPLEDGE_BILL_ID = CB.REPLEDGE_BILL_ID " +
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
    
    public HashMap<String, String> getAllOpenedBillingValues(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT OPENING_DATE, CUSTOMER_NAME, GENDER, SPOUSE_TYPE, SPOUSE_NAME, "
                + "DOOR_NUMBER, STREET, AREA, CITY, MOBILE_NUMBER, ITEMS, COALESCE(GROSS_WEIGHT, 0), COALESCE(NET_WEIGHT, 0), PURITY, "
                + "COALESCE(AMOUNT, 0), COALESCE(INTEREST, 0), COALESCE(DOCUMENT_CHARGE, 0), "
                + "STATUS, NOTE, COALESCE(TOTAL_ADVANCE_AMOUNT_PAID, 0), REPLEDGE_BILL_ID, REBILLED_TO, REBILLED_FROM "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND STATUS IN ('OPENED', 'LOCKED') "
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
                headerValues.put("INTEREST", Double.toString(rs.getDouble(16)));
                headerValues.put("DOCUMENT_CHARGE", Double.toString(rs.getDouble(17)));
                headerValues.put("STATUS", rs.getString(18));
                headerValues.put("NOTE", rs.getString(19));
                headerValues.put("TOTAL_ADVANCE_AMOUNT_PAID", Double.toString(rs.getDouble(20)));
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(21));
                headerValues.put("REBILLED_TO", rs.getString(22));
                headerValues.put("REBILLED_FROM", rs.getString(23));
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
    
    public boolean updateFromBill(String sToBillNumber, String sReBilledFrom, String sRepledgeBillId, String sStatus,
                            String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        String sql = "UPDATE COMPANY_BILLING SET "
                    + "STATUS = ?::COMPANY_BILL_STATUS, "
                    + "CLOSED_USER_ID = ?, "
                    + "REBILLED_FROM = ?, "
                    + "REPLEDGE_BILL_ID = ? "
                    + "WHERE COMPANY_ID = ? "
                    + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                    + "AND STATUS IN ('OPENED', 'LOCKED') "
                    + "AND BILL_NUMBER = ? ";
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sStatus);
            stmt.setString(2, CommonConstants.USERID);
            stmt.setString(3, sReBilledFrom); 
            stmt.setString(4, sRepledgeBillId); 
            stmt.setString(5, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(6, sMaterialType); 
            stmt.setString(7, sToBillNumber);
            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean updateEmptyFromBill(String sReBilledFrom, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        String sql = "UPDATE COMPANY_BILLING SET "
                    + "REBILLED_FROM = ?, "
                    + "REPLEDGE_BILL_ID = ? "
                    + "WHERE COMPANY_ID = ? "
                    + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                    + "AND STATUS IN ('OPENED', 'LOCKED') "
                    + "AND REBILLED_FROM = ? ";
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, ""); 
            stmt.setString(2, ""); 
            stmt.setString(3, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(4, sMaterialType); 
            stmt.setString(5, sReBilledFrom); 
            
            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean updateToBill(String sFromBillNumber, String sReBilledTo, String sStatus,
                            String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        String sql = "UPDATE COMPANY_BILLING SET "
                    + "STATUS = ?::COMPANY_BILL_STATUS, "
                    + "CLOSED_USER_ID = ?, "
                    + "REBILLED_TO = ? "
                    + "WHERE COMPANY_ID = ? "
                    + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                    + "AND STATUS IN ('CLOSED', 'DELIVERED', 'REBILLED', 'REBILLED-ADDED', 'REBILLED-REMOVED') "
                    + "AND BILL_NUMBER = ? ";
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sStatus);
            stmt.setString(2, CommonConstants.USERID);
            stmt.setString(3, sReBilledTo); 
            stmt.setString(4, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(5, sMaterialType); 
            stmt.setString(6, sFromBillNumber);
            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean updateEmptyToBill(String sReBilledTo, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        String sql = "UPDATE COMPANY_BILLING SET "
                    + "REBILLED_TO = ? "
                    + "WHERE COMPANY_ID = ? "
                    + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                    + "AND STATUS IN ('CLOSED', 'DELIVERED', 'REBILLED', 'REBILLED-ADDED', 'REBILLED-REMOVED') "
                    + "AND REBILLED_TO = ? ";
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, ""); 
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, sMaterialType); 
            stmt.setString(4, sReBilledTo); 
            
            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
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
    
    public DataTable getRebilledRepValues(String sFilterScript, String...sVals) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();        
        
        String sql = "SELECT RB.company_bill_number, "
                  + "RB.AMOUNT REP_AMT, "
                  + "CB.bill_number, "
                  + "CB.amount CMP_AMT, " +
                    "RB.opening_date REP_DATE, " +
                    "CB.TOTAL_ADVANCE_AMOUNT_PAID,  " +
                    "RB.REPLEDGE_NAME, "
                  + "RB.repledge_bill_number, "
                  + "(CB.AMOUNT/CB.gross_weight) RATE_PER_GM, "
                  + "CB.gross_weight, "
                  + "CB.ITEMS, "
                  + "RB.REPLEDGE_BILL_ID, "
                  + "EXTRACT(DAY FROM RB.opening_date) REP_DAY " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB  " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE  " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID  " +
                    "AND RB.REPLEDGE_BILL_ID = CB.REPLEDGE_BILL_ID  " +
                    "AND CB.repledge_bill_id IS NOT NULL  " +
                    "AND RB.COMPANY_ID = ?  " +
                    "AND CB.STATUS IN ('OPENED', 'LOCKED') " +
                    "AND CB.bill_number NOT IN (RB.company_bill_number) ";
        
        if(sFilterScript != null) {
            sql += sFilterScript;
        }
        sql += "order by REP_DAY, RB.REPLEDGE_NAME ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(Double.toString(rs.getDouble(2)));
                row.addColumn(rs.getString(3));
                row.addColumn(Double.toString(rs.getDouble(4)));
                row.addColumn(format.format(rs.getDate(5).toLocalDate()));                
                row.addColumn(Double.toString(rs.getDouble(6)));
                row.addColumn(rs.getString(7));
                row.addColumn(rs.getString(8));
                row.addColumn(Double.toString(rs.getDouble(9)));
                row.addColumn(Double.toString(rs.getDouble(10)));
                row.addColumn(rs.getString(11));
                row.addColumn(rs.getString(12));
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
    
    public DataTable getAllRepledgeNames() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT name FROM REPLEDGE ";
        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                dataTable.add(row);                	       
            }		                
        } catch (SQLException e) {
                throw e;
        }finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            //roleMasterConn.close();
        } 

        return dataTable;	
    }
    
    public HashMap<String, String> getAllClosedBillingValues(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT OPENING_DATE, CUSTOMER_NAME, GENDER, SPOUSE_TYPE, SPOUSE_NAME, "
                + "DOOR_NUMBER, STREET, AREA, CITY, MOBILE_NUMBER, ITEMS, COALESCE(GROSS_WEIGHT, 0), "
                + "COALESCE(NET_WEIGHT, 0), PURITY, "
                + "COALESCE(AMOUNT, 0), COALESCE(INTEREST, 0), COALESCE(DOCUMENT_CHARGE, 0), "
                + "STATUS, NOTE, CLOSING_DATE, COALESCE(TOTAL_ADVANCE_AMOUNT_PAID, 0), "
                + "REPLEDGE_BILL_ID, REBILLED_TO, REBILLED_FROM, GOT_AMOUNT, discount_amount, "
                + "accepted_closing_date, NOMINEE_NAME, customer_copy, id_proof_type, id_proof_number, "
                + "CREATED_USER_ID, to_char(created_date, 'dd-MM-YY / HH24:MI:ss'), "
                + "closed_user_id, to_char(closed_date, 'dd-MM-YY / HH24:MI:ss') "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND STATUS IN ('CLOSED', 'DELIVERED', 'REBILLED', 'REBILLED-ADDED', 'REBILLED-REMOVED', 'REBILLED-MULTIPLE') "
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
                headerValues.put("INTEREST", Double.toString(rs.getDouble(16)));
                headerValues.put("DOCUMENT_CHARGE", Double.toString(rs.getDouble(17)));
                headerValues.put("STATUS", rs.getString(18));
                headerValues.put("NOTE", rs.getString(19));
                headerValues.put("CLOSING_DATE", format.format(rs.getDate(20).toLocalDate()));
                headerValues.put("TOTAL_ADVANCE_AMOUNT_PAID", Double.toString(rs.getDouble(21)));
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(22));
                headerValues.put("REBILLED_TO", rs.getString(23));
                headerValues.put("REBILLED_FROM", rs.getString(24));
                headerValues.put("GOT_AMOUNT", Double.toString(rs.getDouble(25)));
                headerValues.put("DISCOUNT_AMOUNT", Double.toString(rs.getDouble(26)));
                if(rs.getDate(27) != null) {
                    headerValues.put("ACCEPTED_CLOSING_DATE", format.format(rs.getDate(27).toLocalDate()));
                }
                headerValues.put("NOMINEE_NAME", rs.getString(28));
                headerValues.put("CUSTOMER_COPY", rs.getString(29));
                headerValues.put("ID_TYPE", rs.getString(30));
                headerValues.put("ID_NUMBER", rs.getString(31));
                headerValues.put("CREATED_USER_ID", rs.getString(32)); 
                headerValues.put("CREATED_TIME", rs.getString(33)); 
                headerValues.put("CLOSED_USER_ID", rs.getString(34)); 
                headerValues.put("CLOSED_TIME", rs.getString(35)); 
                                
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
    
}
