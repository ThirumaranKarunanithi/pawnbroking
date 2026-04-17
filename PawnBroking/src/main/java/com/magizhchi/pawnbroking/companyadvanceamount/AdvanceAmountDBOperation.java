/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.companyadvanceamount;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;

/**
 *
 * @author Tiru
 */
public class AdvanceAmountDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public AdvanceAmountDBOperation(String sDB, String sIP, String sPort, String sSchema,
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
            Logger.getLogger(AdvanceAmountDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public DataTable getOtherSettingsValues(String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ALLOW_TO_CHANGE_ADVANCE_AMOUNT_DATE "
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
    
    public HashMap<String, String> getAllBillingValuesToPay(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;     
        HashMap<String, String> headerValues = new HashMap<>();
        
        String sql = "SELECT OPENING_DATE, CUSTOMER_NAME, GENDER, SPOUSE_TYPE, SPOUSE_NAME, "
                + "DOOR_NUMBER, STREET, AREA, CITY, MOBILE_NUMBER, ITEMS, COALESCE(GROSS_WEIGHT, 0), COALESCE(NET_WEIGHT, 0), PURITY, "
                + "COALESCE(AMOUNT, 0), COALESCE(INTEREST, 0), COALESCE(DOCUMENT_CHARGE, 0), "
                + "STATUS, NOTE, COALESCE(TOTAL_ADVANCE_AMOUNT_PAID, 0), REPLEDGE_BILL_ID, REBILLED_TO "
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
                headerValues.put("GROSS_WEIGHT", Double.toString(rs.getDouble(12)));
                headerValues.put("NET_WEIGHT", Double.toString(rs.getDouble(13)));
                headerValues.put("PURITY", rs.getString(14));
                headerValues.put("AMOUNT", Double.toString(rs.getDouble(15)));
                headerValues.put("INTEREST", Double.toString(rs.getDouble(16)));
                headerValues.put("DOCUMENT_CHARGE", Double.toString(rs.getDouble(17)));
                headerValues.put("STATUS", rs.getString(18));
                headerValues.put("NOTE", rs.getString(19));
                headerValues.put("TOTAL_ADVANCE_AMOUNT_PAID", Double.toString(rs.getDouble(20)));
                headerValues.put("REPLEDGE_BILL_ID", rs.getString(21));
                headerValues.put("REBILLED_TO", rs.getString(22));
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
    
    public String getInterestType() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT DAY_OR_MONTHLY_INTEREST FROM COMPANY WHERE ID = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
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

    public String[] getReduceOrMinimumDaysOrMonths(String sMaterialType, String sType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String[] data = new String[2];
        
        String sql ="SELECT COALESCE(DAYS_OR_MONTHS, 0), REDUCTION_TYPE "
                + "FROM COMPANY_REDUCE_MONTHS_OR_DAYS "
                + "WHERE COMPANY_ID = ? " 
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND REDUCTION_OR_MINIMUM_TYPE = ?::REDUCTION_OR_MINIMUM_TYPE";

        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sType);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                data[0] = Integer.toString(rs.getInt(1));		                   
                data[1] = rs.getString(2);		                   
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }        
        return data;
    }

    public double getRemainingDaysAsMonths(double iRemainingDays, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT AS_MONTH " +
                    "FROM COMPANY_MONTH_SETTING " +
                    "WHERE COMPANY_ID = ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND ? BETWEEN DAYS_FROM AND DAYS_TO";

        try {

            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);
            stmt.setDouble(3, iRemainingDays);
            rs = stmt.executeQuery();

            if(rs.next())
            {		       
                return rs.getDouble(1);		                   
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }        
        return 0;
    }

    public String getFormula(double dAmount, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT FORMULA " +
                    "FROM COMPANY_FORMULA " +
                    "WHERE COMPANY_ID = ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND FORMULA_OPERATION_TYPE = ?::OPERATION_TYPE " +
                    "AND ? BETWEEN AMOUNT_FROM AND AMOUNT_TO";

        try {

            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);
            stmt.setString(3, "CLOSE");
            stmt.setDouble(4, dAmount);
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
    
    public boolean saveRecord(String sBillNumber, String sPaidDate, 
                              double dBillAmount, double dPaidAmount, double dTotalAmount, 
                              String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "INSERT INTO COMPANY_ADVANCE_AMOUNT(COMPANY_ID, JEWEL_MATERIAL_TYPE, "
                + "BILL_NUMBER, PAID_DATE, BILL_AMOUNT, PAID_AMOUNT, TOTAL_AMOUNT, USER_ID) "
                + "VALUES(?, ?::MATERIAL_TYPE, ?, ?, ?, ?, ?, ?)";
        
        try {

            java.sql.Date sqlDatePaidDate = java.sql.Date.valueOf(LocalDate.parse(sPaidDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sBillNumber);
            stmt.setDate(4, sqlDatePaidDate);
            stmt.setDouble(5, dBillAmount);
            stmt.setDouble(6, dPaidAmount);
            stmt.setDouble(7, dTotalAmount);
            stmt.setString(8, CommonConstants.USERID);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean updateTotalAdvanceAmount(String sBillNumber, String sNote, 
                            double dTotalAmount, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE COMPANY_BILLING SET "
                    + "TOTAL_ADVANCE_AMOUNT_PAID = ?, "
                    + "NOTE = ? "
                    + "WHERE COMPANY_ID = ? "
                    + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                    + "AND STATUS IN ('OPENED', 'LOCKED') "
                    + "AND BILL_NUMBER = ? ";
        try {
            
            stmt = roleMasterConn.prepareStatement(sql);                 
            stmt.setDouble(1, dTotalAmount);            
            stmt.setString(2, sNote);
            stmt.setString(3, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(4, sMaterialType); 
            stmt.setString(5, sBillNumber);

            return stmt.executeUpdate() == 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public DataTable getAdvanceAmountTableValues(String sBillNumber, String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT BILL_NUMBER, PAID_DATE, BILL_AMOUNT, PAID_AMOUNT, TOTAL_AMOUNT "
                + "FROM COMPANY_ADVANCE_AMOUNT "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND BILL_NUMBER = ? "
                + "ORDER BY PAID_DATE ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType); 
            stmt.setString(3, sBillNumber);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(format.format(rs.getDate(2).toLocalDate()));
                row.addColumn(Double.toString(rs.getDouble(3)));
                row.addColumn(Double.toString(rs.getDouble(4)));
                row.addColumn(Double.toString(rs.getDouble(5)));
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
    
    public boolean deleteAllSelectedBillAdvAmtVals(String sBillNumber, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM COMPANY_ADVANCE_AMOUNT "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE "
                + "AND BILL_NUMBER = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setString(3, sBillNumber);            
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
    
    public boolean saveBillAdvAmtVals(String sBillNumber, ObservableList<AdvanceAmountBean> tableValues, String sMaterialType) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "INSERT INTO COMPANY_ADVANCE_AMOUNT(COMPANY_ID, JEWEL_MATERIAL_TYPE, "
                + "BILL_NUMBER, PAID_DATE, BILL_AMOUNT, PAID_AMOUNT, TOTAL_AMOUNT, USER_ID) "
                + "VALUES(?, ?::MATERIAL_TYPE, ?, ?, ?, ?, ?, ?)";
        
        try {
            
            for(AdvanceAmountBean bean : tableValues) {

                java.sql.Date sqlFromDate = java.sql.Date.valueOf(LocalDate.parse(bean.getSDate(), CommonConstants.DATETIMEFORMATTER));                                
                
                PreparedStatement stmt = roleMasterConn.prepareStatement(sql);             
                stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
                stmt.setString(2, sMaterialType);
                stmt.setString(3, sBillNumber);
                stmt.setDate(4, sqlFromDate);
                stmt.setDouble(5, bean.getDBillAmount());
                stmt.setDouble(6, bean.getDPaidAmount());
                stmt.setDouble(7, bean.getDTotalAmount());
                stmt.setString(8, CommonConstants.USERID);
                stmt.executeUpdate();
            }            
            
            roleMasterConn.commit();
            roleMasterConn.setAutoCommit(true);
            
            return true;

        } catch (Exception e) {
            throw e;         
        }finally {            
            roleMasterConn.close();
        } 
    }
    
    public DataTable getAllDetailsValues(String sMaterialType, String sFilterScript, String...sVals) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();        
        
        String sql = "SELECT CB.BILL_NUMBER, CB.OPENING_DATE, CB.AMOUNT, CB.CUSTOMER_NAME, "
                + "CB.GENDER, CB.SPOUSE_TYPE, CB.SPOUSE_NAME," +
"CB.STREET, CB.AREA, CB.MOBILE_NUMBER, CB.ITEMS, CB.GROSS_WEIGHT, CB.NET_WEIGHT, CB.PURITY, " +
"CB.STATUS, CB.NOTE, CB.REPLEDGE_BILL_ID, CB.TOTAL_ADVANCE_AMOUNT_PAID, AA.PAID_DATE, AA.PAID_AMOUNT, " +
"REGEXP_REPLACE(COALESCE(CB.BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL " +
"FROM COMPANY_BILLING CB, COMPANY_ADVANCE_AMOUNT AA " +
"WHERE CB.COMPANY_ID = ? " +
"AND CB.COMPANY_ID = AA.COMPANY_ID " +
"AND CB.BILL_NUMBER = AA.BILL_NUMBER " +
"AND CB.AMOUNT = AA.BILL_AMOUNT " +
"AND CB.STATUS IN ('OPENED', 'LOCKED', 'CANCELED') " +
"AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
"AND CB.TOTAL_ADVANCE_AMOUNT_PAID > 0 " +
"GROUP BY CB.BILL_NUMBER, CB.OPENING_DATE, CB.AMOUNT, CB.CUSTOMER_NAME, CB.GENDER, CB.SPOUSE_TYPE, CB.SPOUSE_NAME, " +
"CB.STREET, CB.AREA, CB.MOBILE_NUMBER, CB.ITEMS, CB.GROSS_WEIGHT, CB.NET_WEIGHT, CB.PURITY, " +
"CB.STATUS, CB.NOTE, CB.REPLEDGE_BILL_ID, CB.TOTAL_ADVANCE_AMOUNT_PAID, AA.PAID_DATE, AA.PAID_AMOUNT ";

        try {

            if(sFilterScript != null) {
                sql += sFilterScript;
            }
            sql += " ORDER BY AA.PAID_DATE DESC, BILL DESC";
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setString(2, sMaterialType);

            if(sFilterScript != null) {
                for(int i=0; i<sVals.length; i++) {
                    if(sFilterScript.contains("GENDER =")) {
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
                row.addColumn(format.format(rs.getDate(2).toLocalDate()));
                row.addColumn(Double.toString(rs.getDouble(3)));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
                row.addColumn(rs.getString(7));
                row.addColumn(rs.getString(8));
                row.addColumn(rs.getString(9));
                row.addColumn(rs.getString(10));
                row.addColumn(rs.getString(11));
                row.addColumn(Double.toString(rs.getDouble(12)));
                row.addColumn(Double.toString(rs.getDouble(13)));
                row.addColumn(Double.toString(rs.getDouble(14)));
                row.addColumn(rs.getString(15));
                row.addColumn(rs.getString(16));
                row.addColumn(rs.getString(17) == null ? "" : rs.getString(17));
                row.addColumn(Double.toString(rs.getDouble(18)));
                row.addColumn(format.format(rs.getDate(19).toLocalDate()));
                row.addColumn(Double.toString(rs.getDouble(20)));
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
    
    public boolean deleteDenomination(String sOperation, String sBillNumber, String paidDate, String paidAmt) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM company_bill_denomination "
                + "WHERE COMPANY_ID = ? "
                + "AND operation = ? "
                + "and bill_number = ? "
                + "and operation_date = ? "
                + "and operation_amount = ?";
        
        try {
            java.sql.Date sqlDatepaidDate = java.sql.Date.valueOf(LocalDate.parse(paidDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sOperation);
            stmt.setString(3, sBillNumber);
            stmt.setDate(4, sqlDatepaidDate);  
            stmt.setDouble(5, Double.parseDouble(paidAmt));
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
    
    public boolean saveDenominationValues(String sOperation, String sBillNumber, String paidDate, String paidAmt,
            List<AvailableBalanceBean> currencyListToSave) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "insert into company_bill_denomination("
                + "company_id, operation, bill_number, currency_val, number_of_notes, "
                + "tot_amt_on_that_cur, created_date, user_id, operation_date, operation_amount) "
                + "values(?, ?, ?, ?, ?, ?, now(), ?, ?, ?)";
        
        try {
            java.sql.Date sqlDatepaidDate = java.sql.Date.valueOf(LocalDate.parse(paidDate, CommonConstants.DATETIMEFORMATTER));
            for(AvailableBalanceBean bean : currencyListToSave) {
                
                PreparedStatement stmt = roleMasterConn.prepareStatement(sql);             
                stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
                stmt.setString(2, sOperation);
                stmt.setString(3, sBillNumber);
                stmt.setDouble(4, bean.getDRupee());
                stmt.setDouble(5, bean.getDNumberOfNotes());
                stmt.setDouble(6, bean.getDTotalAmount());
                stmt.setString(7, CommonConstants.USERID);
                stmt.setDate(8, sqlDatepaidDate);    
                stmt.setDouble(9, Double.parseDouble(paidAmt));
                stmt.executeUpdate();
            }            
            
            roleMasterConn.commit();
            roleMasterConn.setAutoCommit(true);
            
            return true;

        } catch (Exception e) {
            throw e;         
        }finally {            
            roleMasterConn.close();
        } 
    }    
    
    public List<AvailableBalanceBean> getDenominationValues(String sOperation, String sBillNumber, 
            String sOperationDate, String sOperationAmount) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        List<AvailableBalanceBean> currencyList = null;
        
        
        String sql = "select currency_val, number_of_notes, tot_amt_on_that_cur "
                + "from company_bill_denomination "
                + "where company_id = ? "
                + "and operation = ? "
                + "AND bill_number LIKE ? "
                + "AND operation_date = ? "
                + "and operation_amount = ? "
                + "ORDER BY currency_val DESC";

        try {
            java.sql.Date sqlDatepaidDate = java.sql.Date.valueOf(LocalDate.parse(sOperationDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sOperation);
            stmt.setString(3, "%" + sBillNumber + "%");
            stmt.setDate(4, sqlDatepaidDate);
            stmt.setDouble(5, Double.parseDouble(sOperationAmount));

            rs = stmt.executeQuery();

            while(rs.next())
            {	
                if(currencyList == null) {
                    currencyList = new ArrayList<>(); 
                }
                currencyList.add(new AvailableBalanceBean(rs.getDouble(1), 
                        (int)rs.getDouble(2), 
                        rs.getDouble(3)));
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

        return currencyList;	
    }
    
}
