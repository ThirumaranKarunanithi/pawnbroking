/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.account;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataRow;
import com.magizhchi.pawnbroking.common.DataTable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tiru
 */
public class TodaysAccountDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public TodaysAccountDBOperation(String sDB, String sIP, String sPort, String sSchema,
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
            Logger.getLogger(TodaysAccountDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
           
    public HashMap<String, String> getTodaysAccountSettingsValues(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        HashMap<String, String> headerValues = null;
        
        
        String sql = "SELECT PRE_DATE, PRE_ACTUAL_AMOUNT, PRE_AVAILABLE_AMOUNT, " +
"PRE_DEFICIT_AMOUNT, TODAYS_DATE, TODAYS_ACTUAL_AMOUNT, TODAYS_AVAILABLE_AMOUNT, " +
"TODAYS_DEFICIT_AMOUNT, PRE_NOTE, TODAYS_NOTE " +
"FROM COMPANY_TODAYS_ACCOUNT "
+ "WHERE COMPANY_ID = ? "
+ "AND REF_MARK = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, "L");
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues = new HashMap<>();
                headerValues.put("PRE_DATE", format.format(rs.getDate(1).toLocalDate()));
                headerValues.put("PRE_ACTUAL_AMOUNT", Double.toString(rs.getDouble(2)));
                headerValues.put("PRE_AVAILABLE_AMOUNT", Double.toString(rs.getDouble(3)));
                headerValues.put("PRE_DEFICIT_AMOUNT", Double.toString(rs.getDouble(4)));                
                headerValues.put("TODAYS_DATE", format.format(rs.getDate(5).toLocalDate()));
                headerValues.put("TODAYS_ACTUAL_AMOUNT", Double.toString(rs.getDouble(6)));
                headerValues.put("TODAYS_AVAILABLE_AMOUNT", Double.toString(rs.getDouble(7)));
                headerValues.put("TODAYS_DEFICIT_AMOUNT", Double.toString(rs.getDouble(8)));
                headerValues.put("PRE_NOTE", rs.getString(9));
                headerValues.put("TODAYS_NOTE", rs.getString(10));
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

        return headerValues;	
    }

    public HashMap<String, String> getTodaysAccountSettingsValues(String sId, String sDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        HashMap<String, String> headerValues = new HashMap<>();
        
        
        String sql = "SELECT PRE_DATE, PRE_ACTUAL_AMOUNT, PRE_AVAILABLE_AMOUNT, " +
"PRE_DEFICIT_AMOUNT, TODAYS_DATE, TODAYS_ACTUAL_AMOUNT, TODAYS_AVAILABLE_AMOUNT, " +
"TODAYS_DEFICIT_AMOUNT, pre_note, todays_note " +
"FROM COMPANY_TODAYS_ACCOUNT "
+ "WHERE COMPANY_ID = ? "
+ "AND TODAYS_DATE = ?";

        try {
            java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("PRE_DATE", format.format(rs.getDate(1).toLocalDate()));
                headerValues.put("PRE_ACTUAL_AMOUNT", Double.toString(rs.getDouble(2)));
                headerValues.put("PRE_AVAILABLE_AMOUNT", Double.toString(rs.getDouble(3)));
                headerValues.put("PRE_DEFICIT_AMOUNT", Double.toString(rs.getDouble(4)));
                headerValues.put("TODAYS_DATE", format.format(rs.getDate(5).toLocalDate()));
                headerValues.put("TODAYS_ACTUAL_AMOUNT", Double.toString(rs.getDouble(6)));
                headerValues.put("TODAYS_AVAILABLE_AMOUNT", Double.toString(rs.getDouble(7)));
                headerValues.put("TODAYS_DEFICIT_AMOUNT", Double.toString(rs.getDouble(8)));
                headerValues.put("PRE_NOTE", rs.getString(9));
                headerValues.put("TODAYS_NOTE", rs.getString(10));
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
    
    public HashMap<String, String> getBillOpeningAccountValues(String sId, String sMaterialType, String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        HashMap<String, String> headerValues = new HashMap<>();
        
        
        String sql = "SELECT COUNT(BILL_NUMBER) BILL_COUNT, SUM(AMOUNT) DEBIT, SUM(AMOUNT - GIVEN_AMOUNT) CREDIT, "
                + "concat(' (  Amt: ', sum(COALESCE(AMOUNT,'0')), ' , Intr: ', sum(OPEN_TAKEN_AMOUNT - document_charge), ' , Doc: ', sum(document_charge), '  )' , " +
"'  ( RB: ', COUNT(CASE WHEN REBILLED_FROM IS NOT NULL THEN REBILLED_FROM END), " +
"', NB: ', COUNT(CASE WHEN REBILLED_FROM IS NULL THEN BILL_NUMBER END), '  )') credit_combo " +
"FROM COMPANY_BILLING " +
"WHERE COMPANY_ID = ? " +
"AND STATUS NOT IN ('CANCELED') " + 
"AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
"AND OPENING_DATE = ?";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(sqlOpeningDate);
            //cal.get
            
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlOpeningDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("bill_count", Long.toString(rs.getLong(1)));
                headerValues.put("debit", Double.toString(rs.getDouble(2)));
                headerValues.put("credit",Double.toString(rs.getDouble(3)));
                headerValues.put("credit_combo",rs.getString(4));
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

        return headerValues;	
    }

    public HashMap<String, String> getBillAdvanceAmountAccountValues(String sId, String sMaterialType, String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        HashMap<String, String> headerValues = new HashMap<>();
        
        
        String sql = "SELECT count(bill_number) bill_count, 0 debit, sum(paid_amount) credit, " +
                "0 credit_combo " +
"FROM COMPANY_ADVANCE_AMOUNT " +
"WHERE company_id = ? " +
"and jewel_material_type = ?::material_type " +
"and paid_date = ?";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlOpeningDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("bill_count", Long.toString(rs.getLong(1)));
                headerValues.put("debit", Double.toString(rs.getDouble(2)));
                headerValues.put("credit",Double.toString(rs.getDouble(3)));
                headerValues.put("credit_combo",rs.getString(4));
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

        return headerValues;	
    }
    
    public HashMap<String, String> getBillClosingAccountValues(String sId, String sMaterialType, String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        HashMap<String, String> headerValues = new HashMap<>();
        
        
        String sql = "SELECT count(bill_number) bill_count, 0 debit, sum(got_amount) credit, " +
                "concat(' ( Amt: ', sum(COALESCE(AMOUNT,'0')), "
                + "' , Intr: ', sum(COALESCE(close_taken_amount,'0')), "
                + "' , Fine: ', sum(COALESCE(total_other_charges,'0')), "
                + "' , Less: ', sum(COALESCE(discount_amount,'0')), "
                + "' , Adv Amt: ', sum(COALESCE(total_advance_amount_paid,'0')), '  )') credit_combo, "
                + "sum(close_taken_amount) interested_amt, "
                + "sum(total_other_charges) tot_other_chrgs, "
                + "sum(discount_amount) less_amt " +
"FROM company_billing " +
"WHERE company_id = ? " +
"and jewel_material_type = ?::material_type " +
"and closing_date = ?";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlOpeningDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("bill_count", Long.toString(rs.getLong(1)));
                headerValues.put("debit", Double.toString(rs.getDouble(2)));
                headerValues.put("credit",Double.toString(rs.getDouble(3)));
                headerValues.put("credit_combo",rs.getString(4));
                headerValues.put("interested_amt",Double.toString(rs.getDouble(5)));
                headerValues.put("total_other_charges",Double.toString(rs.getDouble(6)));
                headerValues.put("discount_amount",Double.toString(rs.getDouble(7)));
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

        return headerValues;	
    }

    public HashMap<String, String> getReBillOpeningAccountValues(String sId, String sMaterialType, String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        HashMap<String, String> headerValues = new HashMap<>();
        
        
        String sql = "SELECT count(repledge_id) bill_count, sum(amount - got_amount) debit, sum(amount) credit, " +
                "concat(' (  Amt: ', sum(COALESCE(amount,'0')), ' , "
                + "Intr: ', sum(COALESCE(OPEN_TAKEN_AMOUNT - document_charge,'0')), ' , "
                + "Doc: ', sum(COALESCE(document_charge,'0')), '  )') credit_combo " +
"FROM repledge_billing " +
"WHERE company_id = ? " +
"and jewel_material_type = ?::material_type " +
"and opening_date = ?";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlOpeningDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("bill_count", Long.toString(rs.getLong(1)));
                headerValues.put("debit", Double.toString(rs.getDouble(2)));
                headerValues.put("credit",Double.toString(rs.getDouble(3)));
                headerValues.put("credit_combo",rs.getString(4));
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

        return headerValues;	
    }
    
    public HashMap<String, String> getReBillClosingAccountValues(String sId, String sMaterialType, String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        HashMap<String, String> headerValues = new HashMap<>();
                
        String sql = "SELECT count(repledge_id) bill_count, sum(given_amount) debit, 0 credit, " +
                "concat(' (  Amt: ', sum(COALESCE(amount,'0')), ' , "
                + "Intr: ', sum(COALESCE(close_taken_amount,'0')), '  )') credit_combo,"
                + " sum(close_taken_amount) interested_amt " +
                "FROM repledge_billing " +
                "WHERE company_id = ? " +
                "and jewel_material_type = ?::material_type " +
                "and closing_date = ?";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlOpeningDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("bill_count", Long.toString(rs.getLong(1)));
                headerValues.put("debit", Double.toString(rs.getDouble(2)));
                headerValues.put("credit",Double.toString(rs.getDouble(3)));
                headerValues.put("credit_combo",rs.getString(4));
                headerValues.put("interested_amt",Double.toString(rs.getDouble(5)));
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

        return headerValues;	
    }
    
    public HashMap<String, String> getAllExpensesAccountValues(String sId, String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        HashMap<String, String> headerValues = new HashMap<>();
        
        
        String sql = "SELECT SUM(expense_count), sum(debit), sum(credit), 0 credit_combo " +
"from (SELECT count(employee_id) expense_count, sum(debitted_amount) debit, 0 credit, 0 credit_combo " +
"	FROM employee_daily_allowance_debit " +
"	WHERE company_id = ? " +
"	AND debitted_date = ? " +
"	union all   " +
"	SELECT count(id) expense_count, sum(debitted_amount) debit, 0 credit, 0 credit_combo " +
"	FROM employee_advance_amount_debit " +
"	WHERE company_id = ? " +
"	AND debitted_date = ? " +
"	union all   " +
"	SELECT count(id) expense_count, sum(debitted_amount) debit, 0 credit, 0 credit_combo " +
"	FROM employee_salary_amount_debit " +
"	WHERE company_id = ? " +
"	AND debitted_date = ? " +
"	union all   " +
"	SELECT count(id) expense_count, sum(debitted_amount) debit, 0 credit, 0 credit_combo " +
"	FROM employee_other_amount_debit " +
"	WHERE company_id = ? " +
"	AND debitted_date = ? " +
"	union all   " +
"	SELECT count(id) expense_count, sum(debitted_amount) debit, 0 credit, 0 credit_combo " +
"	FROM company_bill_debit " +
"	WHERE company_id = ? " +
"	AND debitted_date = ? " +
"	union all   " +
"	SELECT count(id) expense_count, sum(debitted_amount) debit, 0 credit, 0 credit_combo " +
"	FROM company_other_debit " +
"	WHERE company_id = ? " +
"	AND debitted_date = ? " +
"	union all   " +
"	SELECT count(id) expense_count, sum(debitted_amount) debit, 0 credit, 0 credit_combo " +
"	FROM repledge_bill_debit " +
"	WHERE company_id = ? " +
"	AND debitted_date = ? " +
"	union all   " +
"	SELECT count(id) expense_count, sum(debitted_amount) debit, 0 credit, 0 credit_combo " +
"	FROM repledge_other_debit " +
"	WHERE company_id = ? " +
"	AND debitted_date = ?) all_expenses ";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlOpeningDate);
            stmt.setString(3, sId);
            stmt.setDate(4, sqlOpeningDate);
            stmt.setString(5, sId);
            stmt.setDate(6, sqlOpeningDate);
            stmt.setString(7, sId);
            stmt.setDate(8, sqlOpeningDate);
            stmt.setString(9, sId);
            stmt.setDate(10, sqlOpeningDate);
            stmt.setString(11, sId);
            stmt.setDate(12, sqlOpeningDate);
            stmt.setString(13, sId);
            stmt.setDate(14, sqlOpeningDate);
            stmt.setString(15, sId);
            stmt.setDate(16, sqlOpeningDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("expense_count", Long.toString(rs.getLong(1)));
                headerValues.put("debit", Double.toString(rs.getDouble(2)));
                headerValues.put("credit",Double.toString(rs.getDouble(3)));
                headerValues.put("credit_combo",rs.getString(4));
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

        return headerValues;	
    }

    public HashMap<String, String> getAllIncomeAccountValues(String sId, String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        HashMap<String, String> headerValues = new HashMap<>();
        
        
        String sql = "SELECT SUM(income_count), sum(debit), sum(credit), 0 credit_combo  " +
"from ( " +
 "	SELECT count(id) income_count, 0 debit, sum(credit_amount) credit, 0 credit_combo   " +
 "	FROM employee_advance_amount_credit   " +
 "	WHERE company_id = ?   " +
 "	AND credited_date = ?   " +
 "	union all     " +
 "	SELECT count(id) income_count, 0 debit, sum(credited_amount) credit, 0 credit_combo  " +
 "	FROM employee_other_amount_credit   " +
 "	WHERE company_id = ?   " +
 "	AND credited_date = ?   " +
 "	union all     " +
 "	SELECT count(id) income_count, 0 debit, sum(credited_amount) credit, 0 credit_combo  " +
 "	FROM company_bill_credit   " +
 "	WHERE company_id = ?   " +
 "	AND credited_date = ?   " +
 "	union all     " +
 "	SELECT count(id) income_count, 0 debit, sum(credited_amount) credit, 0 credit_combo  " +
 "	FROM company_other_credit   " +
 "	WHERE company_id = ?   " +
 "	AND credited_date = ?   " +
 "	union all     " +
 "	SELECT count(id) income_count, 0 debit, sum(credited_amount) credit, 0 credit_combo  " +
 "	FROM repledge_bill_credit   " +
 "	WHERE company_id = ?   " +
 "	AND credited_date = ?   " +
 "	union all     " +
 "	SELECT count(id) income_count, 0 debit, sum(credited_amount) credit, 0 credit_combo  " +
 "	FROM repledge_other_credit   " +
 "	WHERE company_id = ?   " +
 "	AND credited_date = ?) all_incomes  ";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlOpeningDate);
            stmt.setString(3, sId);
            stmt.setDate(4, sqlOpeningDate);
            stmt.setString(5, sId);
            stmt.setDate(6, sqlOpeningDate);
            stmt.setString(7, sId);
            stmt.setDate(8, sqlOpeningDate);
            stmt.setString(9, sId);
            stmt.setDate(10, sqlOpeningDate);
            stmt.setString(11, sId);
            stmt.setDate(12, sqlOpeningDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("income_count", Long.toString(rs.getLong(1)));
                headerValues.put("debit", Double.toString(rs.getDouble(2)));
                headerValues.put("credit",Double.toString(rs.getDouble(3)));
                headerValues.put("credit_combo",rs.getString(4));
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

        return headerValues;	
    }
    
    public DataTable getBillOpeningTableValue(String sId, String sMaterialType, String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT ROW_NUMBER() "
                + "OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(BILL_NUMBER, '\\D', '', 'g'), '9999999999')) AS SLNO, "
                + "BILL_NUMBER, " +
"OPENING_DATE, CUSTOMER_NAME, ITEMS, AMOUNT, " +
"TOGIVE_AMOUNT, GIVEN_AMOUNT, STATUS, " +
"CREATED_USER_ID, INTEREST, DOCUMENT_CHARGE, to_char(created_date, 'dd-MM-YY / HH24:MI:ss'), "
                + "REGEXP_REPLACE(COALESCE(BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL  " +
"FROM COMPANY_BILLING " +
"WHERE COMPANY_ID = ?  " +
"AND STATUS NOT IN ('CANCELED') " + 
"AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
"AND OPENING_DATE = ? "
+ "ORDER BY BILL";

        try {

            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getLong(1));
                row.addColumn(rs.getString(2));
                row.addColumn(formatter.format(rs.getDate(3)));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getDouble(6));
                row.addColumn(rs.getDouble(7));
                row.addColumn(rs.getDouble(8));
                row.addColumn(rs.getString(9));
                row.addColumn(rs.getString(10));
                row.addColumn(rs.getString(11));
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

    public DataTable getBillAdvanceAmountTableValue(String sId, String sMaterialType, String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(AA.BILL_NUMBER, '\\D', '', 'g'), '9999999999')) AS SLNO, AA.PAID_DATE, " +
"AA.BILL_NUMBER, CB.STATUS, AA.BILL_AMOUNT, AA.PAID_AMOUNT, AA.TOTAL_AMOUNT, "
+ "AA.USER_ID, REGEXP_REPLACE(COALESCE(AA.BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL " +
"FROM COMPANY_ADVANCE_AMOUNT AA, COMPANY_BILLING CB " +
"WHERE AA.BILL_NUMBER = CB.BILL_NUMBER " +
"AND AA.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " + 
"AND AA.COMPANY_ID = CB.COMPANY_ID " +
"AND AA.COMPANY_ID = ? " +
"AND AA.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
"AND AA.PAID_DATE = ? "
+ "ORDER BY BILL";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getLong(1));
                row.addColumn(formatter.format(rs.getDate(2)));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getDouble(5));
                row.addColumn(rs.getDouble(6));
                row.addColumn(rs.getDouble(7));
                row.addColumn(rs.getString(8));
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
    
    public DataTable getBillClosingTableValue(String sId, String sMaterialType, String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT ROW_NUMBER() "
                + "OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(BILL_NUMBER, '\\D', '', 'g'), '9999999999')) AS SLNO, "
                + "BILL_NUMBER, " +
"CLOSING_DATE, CUSTOMER_NAME, ITEMS, AMOUNT, " +
"TOGET_AMOUNT, GOT_AMOUNT, STATUS, " +
"to_char(closed_date, 'dd-MM-YY / HH24:MI:ss'), CLOSED_USER_ID, INTEREST, close_taken_amount, "
                + "REGEXP_REPLACE(COALESCE(BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL, total_advance_amount_paid " +
"FROM COMPANY_BILLING " +
"WHERE COMPANY_ID = ?  " +
"AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
"AND CLOSING_DATE = ? "
+ "ORDER BY BILL";

        try {

            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getLong(1));
                row.addColumn(rs.getString(2));
                row.addColumn(formatter.format(rs.getDate(3)));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getDouble(6));
                row.addColumn(rs.getDouble(7));
                row.addColumn(rs.getDouble(8));
                row.addColumn(rs.getString(9));
                row.addColumn(rs.getString(10));
                row.addColumn(rs.getString(11));
                row.addColumn(rs.getString(12));
                row.addColumn(rs.getString(13));
                row.addColumn(rs.getString(15));
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
    
    public DataTable getReBillOpeningTableValue(String sId, String sMaterialType, String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY REPLEDGE_BILL_ID) AS SLNO, REPLEDGE_BILL_ID, "
                + "OPENING_DATE, STATUS, REPLEDGE_NAME, "
                + "REPLEDGE_BILL_NUMBER, COMPANY_BILL_NUMBER, AMOUNT, TOGET_AMOUNT, "
                + "GOT_AMOUNT, CREATED_USER_ID, open_taken_amount "
                + "FROM REPLEDGE_BILLING " +
                "WHERE COMPANY_ID = ? " +
                "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                "AND OPENING_DATE = ? "
                + "ORDER BY REPLEDGE_NAME";

        try {

            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getLong(1));
                row.addColumn(rs.getString(2));
                row.addColumn(formatter.format(rs.getDate(3)));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
                row.addColumn(rs.getString(7));
                row.addColumn(rs.getDouble(8));
                row.addColumn(rs.getDouble(9));
                row.addColumn(rs.getDouble(10));
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

    public DataTable getReBillClosingTableValue(String sId, String sMaterialType, String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY REPLEDGE_BILL_ID) AS SLNO, REPLEDGE_BILL_ID, "
                + "CLOSING_DATE, STATUS, REPLEDGE_NAME, "
                + "REPLEDGE_BILL_NUMBER, COMPANY_BILL_NUMBER, AMOUNT, TOGIVE_AMOUNT, "
                + "GIVEN_AMOUNT, CREATED_USER_ID, close_taken_amount "
                + "FROM REPLEDGE_BILLING " +
                "WHERE COMPANY_ID = ? " +
                "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                "AND CLOSING_DATE = ? "
                + "ORDER BY REPLEDGE_NAME";

        try {

            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getLong(1));
                row.addColumn(rs.getString(2));
                row.addColumn(formatter.format(rs.getDate(3)));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
                row.addColumn(rs.getString(7));
                row.addColumn(rs.getDouble(8));
                row.addColumn(rs.getDouble(9));
                row.addColumn(rs.getDouble(10));
                row.addColumn(rs.getString(11));
                row.addColumn(rs.getDouble(12));
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

    public DataTable getExpenseTableValue(String sId, String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT  debitted_date, id, 'EMPLOYEE DAILY ALLOWANCE'"
                + ", employee_name, debitted_amount, user_id " +
                    "FROM employee_daily_allowance_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'EMPLOYEE ADVANCE AMOUNT'"
                + ", concat('Salary Advance Amount - ', employee_name, ' - ', reason)"
                + ", debitted_amount, user_id  " +
                    "FROM employee_advance_amount_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'EMPLOYEE SALARY AMOUNT'"
                + ", concat(employee_id, ' - ', employee_name), debitted_amount"
                + ", user_id   " +
                    "FROM employee_salary_amount_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'EMPLOYEE OTHER AMOUNT'"
                + ", concat(employee_name, ' - ', debitted_reason)"
                + ", debitted_amount, user_id  " +
                    "FROM employee_other_amount_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'COMPANY Bill'"
                + ", concat(jewel_material_type, ' - ', bill_number)"
                + ", debitted_amount, user_id  " +
                    "FROM company_bill_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'COMPANY OTHER AMOUNT'"
                + ", concat(expense_or_asset, ' - ', expense_type, ' - ', name, ' - ', reason)"
                + ", debitted_amount, user_id  " +
                    "FROM company_other_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'REPLEDGE Bill'"
                + ", concat('CompBillNo: ', bill_number, ' - '"
                + ", repledge_name), debitted_amount, user_id   " +
                    "FROM repledge_bill_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'REPLEDGE OTHER AMOUNT'"
                + ", concat(repledge_name, ' - ', reason), debitted_amount"
                + ", user_id    " +
                    "FROM repledge_other_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ? ";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlOpeningDate);
            stmt.setString(3, sId);
            stmt.setDate(4, sqlOpeningDate);
            stmt.setString(5, sId);
            stmt.setDate(6, sqlOpeningDate);
            stmt.setString(7, sId);
            stmt.setDate(8, sqlOpeningDate);
            stmt.setString(9, sId);
            stmt.setDate(10, sqlOpeningDate);
            stmt.setString(11, sId);
            stmt.setDate(12, sqlOpeningDate);
            stmt.setString(13, sId);
            stmt.setDate(14, sqlOpeningDate);
            stmt.setString(15, sId);
            stmt.setDate(16, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(formatter.format(rs.getDate(1)));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
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

    public DataTable getIncomeTableValue(String sId, String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT credited_date, id, 'EMPLOYEE ADVANCE AMOUNT', employee_name, credit_amount, user_id  " +
"FROM employee_advance_amount_credit   " +
"WHERE company_id = ?   " +
"AND credited_date = ?   " +
"union all     " +
"SELECT credited_date, id, 'EMPLOYEE OTHER AMOUNT', concat(employee_name, ' - ', credited_reason), credited_amount, user_id  " +
"FROM employee_other_amount_credit   " +
"WHERE company_id = ?   " +
"AND credited_date = ?   " +
"union all     " +
"SELECT credited_date, id, 'COMPANY BILL', concat(jewel_material_type, ' - ', bill_number), credited_amount, user_id   " +
"FROM company_bill_credit   " +
"WHERE company_id = ?   " +
"AND credited_date = ?   " +
"union all     " +
"SELECT credited_date, id, 'COMPANY OTHER'"
+ ", concat(income_or_liability, ' - ', expense_type, ' - ', name, ' - ', reason)"
                + ", credited_amount, user_id   " +
"FROM company_other_credit   " +
"WHERE company_id = ?   " +
"AND credited_date = ?   " +
"union all     " +
"SELECT credited_date, id, 'REPLEDGE BILL', concat(' CompBillNo: ', bill_number, ' - ', repledge_name), credited_amount, user_id   " +
"FROM repledge_bill_credit   " +
"WHERE company_id = ?   " +
"AND credited_date = ?   " +
"union all     " +
"SELECT credited_date, id, 'REPLEDGE OTHER', concat(repledge_name, ' - ', reason), credited_amount, user_id   " +
"FROM repledge_other_credit   " +
"WHERE company_id = ?   " +
"AND credited_date = ?   ";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlOpeningDate);
            stmt.setString(3, sId);
            stmt.setDate(4, sqlOpeningDate);
            stmt.setString(5, sId);
            stmt.setDate(6, sqlOpeningDate);
            stmt.setString(7, sId);
            stmt.setDate(8, sqlOpeningDate);
            stmt.setString(9, sId);
            stmt.setDate(10, sqlOpeningDate);
            stmt.setString(11, sId);
            stmt.setDate(12, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(formatter.format(rs.getDate(1)));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
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
    
    public boolean updateAccountTableMarkToNull() throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "UPDATE COMPANY_TODAYS_ACCOUNT " +
"SET REF_MARK=? " +
"WHERE COMPANY_ID = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, "");           
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);

            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean saveRecord(String sPreDate, double dPreActualAmount, 
            double dPreAvailableAmount, double dPreDeficitAmount,
            String sTodaysDate, double dTodaysActualAmount, 
            double dTodaysAvailableAmount, double dTodaysDeficitAmount, 
            String sTodaysNote, String sPreNote) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "INSERT INTO COMPANY_TODAYS_ACCOUNT( " +
                    "COMPANY_ID, PRE_DATE, PRE_ACTUAL_AMOUNT, PRE_AVAILABLE_AMOUNT, " +
                    "PRE_DEFICIT_AMOUNT, TODAYS_DATE, TODAYS_ACTUAL_AMOUNT, TODAYS_AVAILABLE_AMOUNT, " +
                    "TODAYS_DEFICIT_AMOUNT, REF_MARK, CREATED_DATE, USER_ID, TODAYS_NOTE, pre_note) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), ?, ?, ?)";

        try {

            java.sql.Date sqlDatePreDate = java.sql.Date.valueOf(LocalDate.parse(sPreDate, CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlDateTodaysDate = java.sql.Date.valueOf(LocalDate.parse(sTodaysDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setDate(2, sqlDatePreDate);
            stmt.setDouble(3, dPreActualAmount);
            stmt.setDouble(4, dPreAvailableAmount);
            stmt.setDouble(5, dPreDeficitAmount);
            stmt.setDate(6, sqlDateTodaysDate);
            stmt.setDouble(7, dTodaysActualAmount);
            stmt.setDouble(8, dTodaysAvailableAmount);
            stmt.setDouble(9, dTodaysDeficitAmount);
            stmt.setString(10, "L");
            stmt.setString(11, CommonConstants.USERID);
            stmt.setString(12, sTodaysNote);
            stmt.setString(13, sPreNote);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean saveAvailableAmount(String sTodaysDate, double dTodaysAvailableAmount, 
            double dGPfAmt, double dSPfAmt, double dTotalPfAmt) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "INSERT INTO company_todays_account_available_amount(company_id, todays_date, " +
"todays_available_amount, created_date, user_id, gold_pf_amount, silver_pf_amount, todays_pf_amount) " +
"VALUES (?, ?, ?, now(), ?, ?, ?, ?)";

        try {

            java.sql.Date sqlDateTodaysDate = java.sql.Date.valueOf(LocalDate.parse(sTodaysDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql); 
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);            
            stmt.setDate(2, sqlDateTodaysDate);
            stmt.setDouble(3, dTodaysAvailableAmount);
            stmt.setString(4, CommonConstants.USERID);
            stmt.setDouble(5, dGPfAmt);
            stmt.setDouble(6, dSPfAmt);
            stmt.setDouble(7, dTotalPfAmt);
            
            return stmt.executeUpdate() >= 1;

        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public String getAvailableAmount(String sId, String sDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        
        
        String sql = "SELECT todays_available_amount  " +
"FROM company_todays_account_available_amount "
+ "WHERE COMPANY_ID = ? "
+ "AND todays_date = ? ";

        try {
            java.sql.Date sqlDateTodaysDate = java.sql.Date.valueOf(LocalDate.parse(sDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlDateTodaysDate);
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

        return null;	
    }
    
    public DataTable getNYLockedTableValue(String sMaterialType, 
            String sStatus, String sPhysicalLocation) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT ROW_NUMBER() "
                + "OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(BILL_NUMBER, '\\D', '', 'g'), '9999999999')) AS SLNO, "
                + "BILL_NUMBER, " +
"AMOUNT, OPENING_DATE, STATUS, items, " +
"CREATED_USER_ID, REGEXP_REPLACE(COALESCE(BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL  " +
"FROM COMPANY_BILLING " +
"WHERE COMPANY_ID = ?  " +
"AND STATUS = ?::COMPANY_BILL_STATUS  " +
"and physical_location = ? " +
"AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
"ORDER BY BILL";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sStatus);
            stmt.setString(3, sPhysicalLocation);
            stmt.setString(4, sMaterialType);            
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getLong(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getDouble(3));
                row.addColumn(formatter.format(rs.getDate(4)));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
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
    
    public DataTable getNYDeliveredTableValue(String sMaterialType, 
            String sStatus, String sPhysicalLoc) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        Format tFormatter = new SimpleDateFormat(CommonConstants.TIME_FORMAT);
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(BILL_NUMBER, '\\D', '', 'g'), '9999999999')) AS SLNO, BILL_NUMBER, " +
"OPENING_DATE, CLOSING_DATE, AMOUNT, interest, close_taken_amount, total_advance_amount_paid, " +
"toget_amount, got_amount, to_char(closed_date, 'dd-MM-YY / HH24:MI:ss'), closed_user_id, STATUS, " +
"REGEXP_REPLACE(COALESCE(BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL  " +
"FROM COMPANY_BILLING " +
"WHERE COMPANY_ID = ?  " +
"AND STATUS = ?::COMPANY_BILL_STATUS " +
"AND physical_location = ? " +
"AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
"AND (REPLEDGE_BILL_ID IS NULL OR repledge_bill_id = '')" +
"ORDER BY BILL ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sStatus);
            stmt.setString(3, sPhysicalLoc);
            stmt.setString(4, sMaterialType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getLong(1));
                row.addColumn(rs.getString(2));    
                row.addColumn(formatter.format(rs.getDate(3)));
                row.addColumn(formatter.format(rs.getDate(4)));
                row.addColumn(rs.getDouble(5));
                row.addColumn(rs.getDouble(6));
                row.addColumn(rs.getDouble(7));
                row.addColumn(rs.getDouble(8));                                
                row.addColumn(rs.getString(9));
                row.addColumn(rs.getString(10));    
                row.addColumn(rs.getString(11));    
                //row.addColumn(tFormatter.format(rs.getDate(11)));
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

    public DataTable getLockerToCashDrawerTableValue(String sMaterialType,  String sPhysicalLoc) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        Format tFormatter = new SimpleDateFormat(CommonConstants.TIME_FORMAT);
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(BILL_NUMBER, '\\D', '', 'g'), '9999999999')) AS SLNO, BILL_NUMBER, " +
"OPENING_DATE, CLOSING_DATE, AMOUNT, interest, close_taken_amount, total_advance_amount_paid, " +
"toget_amount, got_amount, to_char(closed_date, 'dd-MM-YY / HH24:MI:ss'), closed_user_id, status, " +
"REGEXP_REPLACE(COALESCE(BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL  " +
"FROM COMPANY_BILLING " +
"WHERE COMPANY_ID = ?  " +
"AND STATUS in ('CLOSED', 'REBILLED', 'REBILLED-ADDED', 'REBILLED-REMOVED', 'REBILLED-MULTIPLE') " +
"AND physical_location = ? " +
"AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
"AND (REPLEDGE_BILL_ID IS NULL OR repledge_bill_id = '')" +
"ORDER BY BILL ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sPhysicalLoc);
            stmt.setString(3, sMaterialType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getLong(1));
                row.addColumn(rs.getString(2));    
                row.addColumn(formatter.format(rs.getDate(3)));
                row.addColumn(formatter.format(rs.getDate(4)));
                row.addColumn(rs.getDouble(5));
                row.addColumn(rs.getDouble(6));
                row.addColumn(rs.getDouble(7));
                row.addColumn(rs.getDouble(8));                                
                row.addColumn(rs.getString(9));
                row.addColumn(rs.getString(10));    
                row.addColumn(rs.getString(11));    
                //row.addColumn(tFormatter.format(rs.getDate(11)));
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
    
    public DataTable getRNYShopLockerToRepDrawerTableValue() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        Format tFormatter = new SimpleDateFormat(CommonConstants.TIME_FORMAT);
        
        String sql = "SELECT ROW_NUMBER() "
                + "OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(CB.BILL_NUMBER, '\\D', '', 'g'), '9999999999')) AS SLNO, "
                + "CB.BILL_NUMBER, " +
"CB.AMOUNT, CB.OPENING_DATE, CB.STATUS, CB.items, " +
"CB.CREATED_USER_ID, REGEXP_REPLACE(COALESCE(CB.BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL,  " +
"RBC.repledge_id, RBC.repledge_name   " +
"FROM COMPANY_BILLING CB, repledge_bill_calc_header RBCHE, repledge_bill_calc RBC  " +
"WHERE CB.COMPANY_ID = ? " +
"AND RBCHE.COMPANY_ID = RBC.COMPANY_ID " +
"AND RBCHE.planner_id = RBC.planner_id " +
"AND RBC.company_bill_number = CB.BILL_NUMBER " +
"AND RBC.operation = ? " +
"AND RBCHE.is_printed = ? " +
"AND CB.STATUS IN ('OPENED', 'LOCKED')  " +
"AND CB.physical_location in ('SHOP LOCKER', 'CASH DRAWER')  " +
"AND CB.JEWEL_MATERIAL_TYPE = 'GOLD' " +
"AND (CB.REPLEDGE_BILL_ID IS NULL OR CB.repledge_bill_id = '') " +
"ORDER BY BILL";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, "BILL OPENING");
            stmt.setBoolean(3, Boolean.TRUE);
            //stmt.setString(4, sPhysicalLoc);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getLong(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getDouble(3));
                row.addColumn(formatter.format(rs.getDate(4)));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
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

    public DataTable getRNYRepledgeDrawerToRepLockerTableValue(String sPhysicalLoc) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        Format tFormatter = new SimpleDateFormat(CommonConstants.TIME_FORMAT);
        
        String sql = "SELECT ROW_NUMBER() "
                + "OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(CB.BILL_NUMBER, '\\D', '', 'g'), '9999999999')) AS SLNO, "
                + "CB.BILL_NUMBER, " +
"CB.AMOUNT, CB.OPENING_DATE, CB.STATUS, CB.items, " +
"CB.CREATED_USER_ID, REGEXP_REPLACE(COALESCE(CB.BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL,  " +
"RBC.repledge_id, RBC.repledge_name   " +
"FROM COMPANY_BILLING CB, repledge_bill_calc_header RBCHE, repledge_bill_calc RBC  " +
"WHERE CB.COMPANY_ID = ? " +
"AND RBCHE.COMPANY_ID = RBC.COMPANY_ID " +
"AND RBCHE.planner_id = RBC.planner_id " +
"AND RBC.company_bill_number = CB.BILL_NUMBER " +
"AND RBC.operation = ? " +
"AND RBCHE.is_printed = ? " +
"AND CB.STATUS IN ('OPENED', 'LOCKED')  " +
"AND CB.physical_location = ?  " +
"AND CB.JEWEL_MATERIAL_TYPE = 'GOLD' " +
"AND (CB.REPLEDGE_BILL_ID IS NULL OR CB.repledge_bill_id = '') " +
"ORDER BY BILL";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, "BILL OPENING");
            stmt.setBoolean(3, Boolean.TRUE);
            stmt.setString(4, sPhysicalLoc);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getLong(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getDouble(3));
                row.addColumn(formatter.format(rs.getDate(4)));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
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
    
    public DataTable getRNYDeliveredTableValue(String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY RB.REPLEDGE_NAME) AS SLNO,  " +
"RB.REPLEDGE_BILL_NUMBER, RB.REPLEDGE_NAME, RB.OPENING_DATE, RB.AMOUNT, " +
"CB.BILL_NUMBER, CB.OPENING_DATE, CB.AMOUNT, " +
"RB.STATUS, RB.REPLEDGE_BILL_ID, RB.suspense_date " +
"FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
"WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE  " +
"AND CB.REPLEDGE_BILL_ID = RB.REPLEDGE_BILL_ID " +
"AND CB.COMPANY_ID = RB.COMPANY_ID " +
"AND CB.REPLEDGE_BILL_ID IS NOT NULL  " +
"AND RB.STATUS NOT IN ('RECEIVED') " +
"AND CB.STATUS IN ('CLOSED', 'REBILLED-REMOVED') " +
"AND CB.COMPANY_ID = ? " +
"AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getLong(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(formatter.format(rs.getDate(4)));
                row.addColumn(rs.getDouble(5));
                row.addColumn(rs.getString(6));
                row.addColumn(formatter.format(rs.getDate(7)));
                row.addColumn(rs.getDouble(8));
                row.addColumn(rs.getString(9));
                row.addColumn(rs.getString(10));
                row.addColumn(rs.getString(11));
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

    public DataTable getRNYSuspenseTableValue(String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY RB.REPLEDGE_NAME) AS SLNO,  " +
                "RB.REPLEDGE_BILL_NUMBER, RB.REPLEDGE_NAME, RB.OPENING_DATE, RB.AMOUNT, " +
                "CB.BILL_NUMBER, CB.OPENING_DATE, CB.AMOUNT, " +
                "RB.STATUS, RB.REPLEDGE_BILL_ID, RB.suspense_date " +
                "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE  " +
                "AND CB.REPLEDGE_BILL_ID = RB.REPLEDGE_BILL_ID " +
                "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +
                "AND CB.COMPANY_ID = RB.COMPANY_ID " +
                "AND CB.REPLEDGE_BILL_ID IS NOT NULL  " +
                "AND RB.STATUS = 'SUSPENSE' " +
                "AND CB.COMPANY_ID = ? " +
                "AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getLong(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(formatter.format(rs.getDate(4)));
                row.addColumn(rs.getDouble(5));
                row.addColumn(rs.getString(6));
                row.addColumn(formatter.format(rs.getDate(7)));
                row.addColumn(rs.getDouble(8));
                row.addColumn(rs.getString(9));
                row.addColumn(rs.getString(10));
                row.addColumn(rs.getString(11));
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
    
    public DataTable getRNYDeliveredLaterTableValue(String sMaterialType) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT ROW_NUMBER() OVER (ORDER BY RB.REPLEDGE_NAME) AS SLNO,  " +
"RB.REPLEDGE_BILL_NUMBER, RB.REPLEDGE_NAME, RB.OPENING_DATE, RB.AMOUNT, " +
"CB.BILL_NUMBER, CB.OPENING_DATE, CB.AMOUNT, " +
"RB.STATUS, RB.REPLEDGE_BILL_ID, RB.suspense_date " +
"FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
"WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE  " +
"AND CB.REPLEDGE_BILL_ID = RB.REPLEDGE_BILL_ID " +
"AND CB.COMPANY_ID = RB.COMPANY_ID " +
"AND CB.REPLEDGE_BILL_ID IS NOT NULL  " +
"AND RB.STATUS NOT IN ('RECEIVED') " +
"AND CB.STATUS IN ('REBILLED-ADDED', 'REBILLED-MULTIPLE') " +
"AND CB.COMPANY_ID = ? " +
"AND CB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getLong(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(formatter.format(rs.getDate(4)));
                row.addColumn(rs.getDouble(5));
                row.addColumn(rs.getString(6));
                row.addColumn(formatter.format(rs.getDate(7)));
                row.addColumn(rs.getDouble(8));
                row.addColumn(rs.getString(9));
                row.addColumn(rs.getString(10));
                row.addColumn(rs.getString(11));
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
    
    public boolean updateStatus(String sStatus, String physicalLocation, 
            String sMaterialType, String sFilterScript) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;              
        
        String sql = "UPDATE COMPANY_BILLING SET "                
                + "STATUS = ?::COMPANY_BILL_STATUS, "
                + "physical_location = ? "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";
        
        try {

            if(sFilterScript != null) {
                sql += sFilterScript;
            }            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, sStatus);
            stmt.setString(2, physicalLocation);
            stmt.setString(3, CommonConstants.ACTIVE_COMPANY_ID);
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

    public boolean updateStatusForPhysicalLoc(String physicalLocation, 
            String sMaterialType, String sFilterScript) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;              
        
        String sql = "UPDATE COMPANY_BILLING SET "                
                + "physical_location = ? "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";
        
        try {

            if(sFilterScript != null) {
                sql += sFilterScript;
            }            
            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, physicalLocation);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, sMaterialType);

            
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public boolean getAddViewUpdate(String sRoleId, String sTabName, String sScreenName) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT allow_view " +
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
                return rs.getBoolean(1);
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
    
    public String getBackupFilePath(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null; 
        String sFilePath = "";
        
        String sql = "select COALESCE(backup_file_path, '') "
                + "from company "
                + "where id = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                sFilePath = rs.getString(1);
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

        return sFilePath;	
    }
    
    public List<AvailableBalanceBean> getDenominationValues(String sTodaysDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        List<AvailableBalanceBean> currencyList = null;
        
        
        String sql = "select currency_val, number_of_notes, tot_amt_on_that_cur "
                + "from company_todays_account_denomination "
                + "where company_id = ? "
                + "and todays_date = ?";

        try {
            java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.parse(sTodaysDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlDate);
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
    
    public List<AvailableBalancePrintBean> getDenominationPrintValues(String sTodaysDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        List<AvailableBalancePrintBean> currencyList = null;
        
        
        String sql = "select currency_val, number_of_notes, tot_amt_on_that_cur "
                + "from company_todays_account_denomination "
                + "where company_id = ? "
                + "and todays_date = ?";

        try {
            java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.parse(sTodaysDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                if(currencyList == null) {
                    currencyList = new ArrayList<>(); 
                }
                currencyList.add(new AvailableBalancePrintBean(rs.getDouble(1), 
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
    
    public boolean deleteDenomination(String sTodaysDate) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;                     
        
        String sql = "DELETE FROM company_todays_account_denomination "
                + "WHERE COMPANY_ID = ? "
                + "AND todays_date = ?";
        
        try {
            java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.parse(sTodaysDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlDate);
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
    
    public boolean saveDenominationValues(String sTodaysDate, List<AvailableBalanceBean> currencyListToSave) throws Exception
    {	

        connectDB();        
        roleMasterConn.setAutoCommit(false);
        
        String sql = "insert into company_todays_account_denomination("
                + "company_id, todays_date, currency_val, number_of_notes, tot_amt_on_that_cur, created_date, user_id) "
                + "values(?, ?, ?, ?, ?, now(), ?)";
        
        try {
            
            for(AvailableBalanceBean bean : currencyListToSave) {

                java.sql.Date sqlTodaysDate = java.sql.Date.valueOf(LocalDate.parse(sTodaysDate, CommonConstants.DATETIMEFORMATTER));                
                
                PreparedStatement stmt = roleMasterConn.prepareStatement(sql);             
                stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
                stmt.setDate(2, sqlTodaysDate);
                stmt.setDouble(3, bean.getDRupee());
                stmt.setDouble(4, bean.getDNumberOfNotes());
                stmt.setDouble(5, bean.getDTotalAmount());
                stmt.setString(6, CommonConstants.USERID);
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
    
    public DataTable getBillDebitTotalPrintValues(String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT JEWEL_MATERIAL_TYPE, COUNT(BILL_NUMBER), SUM(AMOUNT), "
                + "SUM(COALESCE(OPEN_TAKEN_AMOUNT - document_charge,'0')), SUM(DOCUMENT_CHARGE) " +
                    "FROM COMPANY_BILLING    " +
                    "WHERE COMPANY_ID = ?     " +
                    "AND STATUS NOT IN ('CANCELED')     " +
                    "AND OPENING_DATE = ? " +
                    "GROUP BY JEWEL_MATERIAL_TYPE   " +
                    "ORDER BY JEWEL_MATERIAL_TYPE ";

        try {

            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getInt(2));
                row.addColumn(rs.getDouble(3));
                row.addColumn(rs.getDouble(4));
                row.addColumn(rs.getDouble(5));
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

    public DataTable getBillCreditTotalPrintValues(String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT JEWEL_MATERIAL_TYPE, COUNT(BILL_NUMBER), SUM(AMOUNT), "
                + "SUM(COALESCE(close_taken_amount,'0')), SUM(COALESCE(total_advance_amount_paid,'0')), " 
                + "SUM(COALESCE(total_other_charges,'0')), SUM(COALESCE(discount_amount,'0')) " +
                    "FROM COMPANY_BILLING    " +
                    "WHERE COMPANY_ID = ?     " +
                    "AND closing_date = ? " +
                    "GROUP BY JEWEL_MATERIAL_TYPE   " +
                    "ORDER BY JEWEL_MATERIAL_TYPE ";

        try {

            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getInt(2));
                row.addColumn(rs.getDouble(3));
                row.addColumn(rs.getDouble(4));
                row.addColumn(rs.getDouble(5));
                row.addColumn(rs.getDouble(6));
                row.addColumn(rs.getDouble(7));
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
    
    public DataTable getBillDebitPrintValues(String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT JEWEL_MATERIAL_TYPE, BILL_NUMBER, AMOUNT, "
                + "COALESCE(OPEN_TAKEN_AMOUNT - document_charge,'0'), DOCUMENT_CHARGE, togive_amount, given_amount, "
                + "REGEXP_REPLACE(COALESCE(BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL " +
                    "FROM COMPANY_BILLING    " +
                    "WHERE COMPANY_ID = ?     " +
                    "AND STATUS NOT IN ('CANCELED')     " +
                    "AND OPENING_DATE = ? " +
                    "ORDER BY JEWEL_MATERIAL_TYPE, BILL ";

        try {

            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getDouble(3));
                row.addColumn(rs.getDouble(4));
                row.addColumn(rs.getDouble(5));
                row.addColumn(rs.getDouble(6));
                row.addColumn(rs.getDouble(7));
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

    public DataTable getBillCreditPrintValues(String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT JEWEL_MATERIAL_TYPE, BILL_NUMBER, AMOUNT, "
                + "COALESCE(close_taken_amount,'0'), COALESCE(total_advance_amount_paid,'0'),  "
                + "COALESCE(total_other_charges,'0'), COALESCE(discount_amount,'0'), "
                + "REGEXP_REPLACE(COALESCE(BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL " +
                    "FROM COMPANY_BILLING    " +
                    "WHERE COMPANY_ID = ?     " +
                    "AND closing_date = ? " +
                    "ORDER BY JEWEL_MATERIAL_TYPE, BILL ";

        try {

            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getDouble(3));
                row.addColumn(rs.getDouble(4));
                row.addColumn(rs.getDouble(5));
                row.addColumn(rs.getDouble(6));
                row.addColumn(rs.getDouble(7));
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

    public DataTable getBillCreditPrintValuesWithIndex(String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT JEWEL_MATERIAL_TYPE, BILL_NUMBER, AMOUNT, "
                + "COALESCE(close_taken_amount,'0'), COALESCE(total_advance_amount_paid,'0'),  "
                + "COALESCE(total_other_charges,'0'), COALESCE(discount_amount,'0'), cust_copy_verifed, comp_copy_verifed, "
                + "pack_copy_verifed, is_card_lost_bond_printed, closed_by, relation_to_closed_by, "
                + "COALESCE(id_proof_type,'NOT MENTIONED'), COALESCE(id_proof_number,'NOT MENTIONED'), "
                + "REPLEDGE_BILL_ID, STATUS, "
                + "REGEXP_REPLACE(COALESCE(BILL_NUMBER, '0'), '[^0-9]*' ,'0')::integer BILL " +
                    "FROM COMPANY_BILLING    " +
                    "WHERE COMPANY_ID = ?     " +
                    "AND closing_date = ? " +
                    "ORDER BY JEWEL_MATERIAL_TYPE, BILL ";

        try {

            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getDouble(3));
                row.addColumn(rs.getDouble(4));
                row.addColumn(rs.getDouble(5));
                row.addColumn(rs.getDouble(6));
                row.addColumn(rs.getDouble(7));
                row.addColumn(rs.getBoolean(8));
                row.addColumn(rs.getBoolean(9));
                row.addColumn(rs.getBoolean(10));
                row.addColumn(rs.getBoolean(11));
                row.addColumn(rs.getString(12));
                row.addColumn(rs.getString(13));                
                row.addColumn(rs.getString(14));
                row.addColumn(rs.getString(15));
                row.addColumn(rs.getString(16));
                row.addColumn(rs.getString(17));
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
    
    public DataTable getRepledgeBillCreditPrintValues(String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT company_bill_number, repledge_name, repledge_bill_number, AMOUNT, "
                + "COALESCE(amount - toget_amount,'0') " +
                    "FROM REPLEDGE_BILLING    " +
                    "WHERE COMPANY_ID = ?     " +
                    "AND opening_date = ? " +
                    "ORDER BY repledge_name ";

        try {

            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getDouble(4));
                row.addColumn(rs.getDouble(5));
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

    public DataTable getRepledgeBillDebitPrintValues(String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT company_bill_number, repledge_name, repledge_bill_number, "
                + "opening_date, AMOUNT, "
                + "COALESCE(close_taken_amount,'0') " +
                    "FROM REPLEDGE_BILLING    " +
                    "WHERE COMPANY_ID = ?     " +
                    "AND closing_date = ? " +
                    "ORDER BY repledge_name ";

        try {

            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));                
                row.addColumn(rs.getString(3));
                row.addColumn(format.format(rs.getDate(4).toLocalDate()));
                row.addColumn(rs.getDouble(5));
                row.addColumn(rs.getDouble(6));
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
    
    public DataTable getBillAdvanceAmountPrintValue(String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT " +
"AA.JEWEL_MATERIAL_TYPE, AA.BILL_NUMBER, CB.customer_name, CB.opening_date, CB.AMOUNT, AA.PAID_AMOUNT " +
"FROM COMPANY_ADVANCE_AMOUNT AA, COMPANY_BILLING CB " +
"WHERE AA.BILL_NUMBER = CB.BILL_NUMBER " +
"AND AA.COMPANY_ID = ? " +
"AND AA.PAID_DATE = ? "
+ "ORDER BY JEWEL_MATERIAL_TYPE";

        
        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(formatter.format(rs.getDate(4)));
                row.addColumn(rs.getDouble(5));
                row.addColumn(rs.getDouble(6));
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
    
    public DataTable getExpensePrintValue(String sOpeningDate) throws SQLException
    {

        connectDB();
        String sId = CommonConstants.ACTIVE_COMPANY_ID;
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT  debitted_date, id, 'EMPLOYEE DAILY ALLOWANCE', concat(employee_name, ' - ', 'DIALY ALLOWANCE'), debitted_amount, user_id " +
                    "FROM employee_daily_allowance_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'EMPLOYEE ADVANCE AMOUNT', concat(employee_name, ' - ', reason), debitted_amount, user_id  " +
                    "FROM employee_advance_amount_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'EMPLOYEE SALARY AMOUNT', concat(employee_name, ' - ', 'SALARY'), debitted_amount, user_id   " +
                    "FROM employee_salary_amount_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'EMPLOYEE OTHER AMOUNT', concat(employee_name, ' - ', debitted_reason), debitted_amount, user_id  " +
                    "FROM employee_other_amount_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'COMPANY Bill', concat(jewel_material_type, ' - ', bill_number), debitted_amount, user_id  " +
                    "FROM company_bill_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'COMPANY OTHER AMOUNT', concat(expense_type, ' - ', name, ' - ', reason), debitted_amount, user_id  " +
                    "FROM company_other_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'REPLEDGE Bill', concat('CompBillNo: ', bill_number, ' - ', repledge_name), debitted_amount, user_id   " +
                    "FROM repledge_bill_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'REPLEDGE OTHER AMOUNT', concat(repledge_name, ' - ', reason), debitted_amount, user_id    " +
                    "FROM repledge_other_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ? ";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlOpeningDate);
            stmt.setString(3, sId);
            stmt.setDate(4, sqlOpeningDate);
            stmt.setString(5, sId);
            stmt.setDate(6, sqlOpeningDate);
            stmt.setString(7, sId);
            stmt.setDate(8, sqlOpeningDate);
            stmt.setString(9, sId);
            stmt.setDate(10, sqlOpeningDate);
            stmt.setString(11, sId);
            stmt.setDate(12, sqlOpeningDate);
            stmt.setString(13, sId);
            stmt.setDate(14, sqlOpeningDate);
            stmt.setString(15, sId);
            stmt.setDate(16, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(formatter.format(rs.getDate(1)));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
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
    
    public HashMap<String, String> getAllExpensesTotalPrintValues(String sOpeningDate) throws SQLException
    {

        connectDB();
        String sId = CommonConstants.ACTIVE_COMPANY_ID; 
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        HashMap<String, String> headerValues = new HashMap<>();
        
        
        String sql = "SELECT SUM(expense_count), sum(debit), sum(credit), 0 credit_combo " +
"from (SELECT count(employee_id) expense_count, sum(debitted_amount) debit, 0 credit, 0 credit_combo " +
"	FROM employee_daily_allowance_debit " +
"	WHERE company_id = ? " +
"	AND debitted_date = ? " +
"	union all   " +
"	SELECT count(id) expense_count, sum(debitted_amount) debit, 0 credit, 0 credit_combo " +
"	FROM employee_advance_amount_debit " +
"	WHERE company_id = ? " +
"	AND debitted_date = ? " +
"	union all   " +
"	SELECT count(id) expense_count, sum(debitted_amount) debit, 0 credit, 0 credit_combo " +
"	FROM employee_salary_amount_debit " +
"	WHERE company_id = ? " +
"	AND debitted_date = ? " +
"	union all   " +
"	SELECT count(id) expense_count, sum(debitted_amount) debit, 0 credit, 0 credit_combo " +
"	FROM employee_other_amount_debit " +
"	WHERE company_id = ? " +
"	AND debitted_date = ? " +
"	union all   " +
"	SELECT count(id) expense_count, sum(debitted_amount) debit, 0 credit, 0 credit_combo " +
"	FROM company_bill_debit " +
"	WHERE company_id = ? " +
"	AND debitted_date = ? " +
"	union all   " +
"	SELECT count(id) expense_count, sum(debitted_amount) debit, 0 credit, 0 credit_combo " +
"	FROM company_other_debit " +
"	WHERE company_id = ? " +
"	AND debitted_date = ? " +
"	union all   " +
"	SELECT count(id) expense_count, sum(debitted_amount) debit, 0 credit, 0 credit_combo " +
"	FROM repledge_bill_debit " +
"	WHERE company_id = ? " +
"	AND debitted_date = ? " +
"	union all   " +
"	SELECT count(id) expense_count, sum(debitted_amount) debit, 0 credit, 0 credit_combo " +
"	FROM repledge_other_debit " +
"	WHERE company_id = ? " +
"	AND debitted_date = ?) all_expenses ";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlOpeningDate);
            stmt.setString(3, sId);
            stmt.setDate(4, sqlOpeningDate);
            stmt.setString(5, sId);
            stmt.setDate(6, sqlOpeningDate);
            stmt.setString(7, sId);
            stmt.setDate(8, sqlOpeningDate);
            stmt.setString(9, sId);
            stmt.setDate(10, sqlOpeningDate);
            stmt.setString(11, sId);
            stmt.setDate(12, sqlOpeningDate);
            stmt.setString(13, sId);
            stmt.setDate(14, sqlOpeningDate);
            stmt.setString(15, sId);
            stmt.setDate(16, sqlOpeningDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("expense_count", Long.toString(rs.getLong(1)));
                headerValues.put("debit", Double.toString(rs.getDouble(2)));
                headerValues.put("credit",Double.toString(rs.getDouble(3)));
                headerValues.put("credit_combo",rs.getString(4));
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

        return headerValues;	
    }
    
    public DataTable getExpenseDetailedPrintValue(String sOpeningDate) throws SQLException
    {
        connectDB();
        String sId = CommonConstants.ACTIVE_COMPANY_ID;
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT  debitted_date, id, 'EMPLOYEE DAILY ALLOWANCE', concat(employee_name, ' - ', 'DAILY ALLOWANCE'), debitted_amount, user_id " +
                    "FROM employee_daily_allowance_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'EMPLOYEE ADVANCE AMOUNT', concat('Salary Adv.Amount - ', employee_name, ' - ', reason), debitted_amount, user_id  " +
                    "FROM employee_advance_amount_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'EMPLOYEE SALARY AMOUNT', concat(employee_id, ' - ', employee_name, ' - ', 'SALARY'), debitted_amount, user_id   " +
                    "FROM employee_salary_amount_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'EMPLOYEE OTHER AMOUNT', concat(employee_name, ' - ', debitted_reason), debitted_amount, user_id  " +
                    "FROM employee_other_amount_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'COMPANY Bill', concat(jewel_material_type, ' - ', bill_number), debitted_amount, user_id  " +
                    "FROM company_bill_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'COMPANY OTHER AMOUNT', concat(expense_type, ' - ', name, ' - ', reason), debitted_amount, user_id  " +
                    "FROM company_other_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'REPLEDGE Bill', concat('CompBillNo: ', bill_number, ' - ', repledge_name), debitted_amount, user_id   " +
                    "FROM repledge_bill_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ?  " +
                    "union all    " +
                    "SELECT  debitted_date, id, 'REPLEDGE OTHER AMOUNT', concat(repledge_name, ' - ', reason), debitted_amount, user_id    " +
                    "FROM repledge_other_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date =  ? ";

        try {
            java.sql.Date sqlOpeningDate = 
                    java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlOpeningDate);
            stmt.setString(3, sId);
            stmt.setDate(4, sqlOpeningDate);
            stmt.setString(5, sId);
            stmt.setDate(6, sqlOpeningDate);
            stmt.setString(7, sId);
            stmt.setDate(8, sqlOpeningDate);
            stmt.setString(9, sId);
            stmt.setDate(10, sqlOpeningDate);
            stmt.setString(11, sId);
            stmt.setDate(12, sqlOpeningDate);
            stmt.setString(13, sId);
            stmt.setDate(14, sqlOpeningDate);
            stmt.setString(15, sId);
            stmt.setDate(16, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(formatter.format(rs.getDate(1)));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
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
    
    public HashMap<String, String> getAllIncomeAccountValues(String sOpeningDate) throws SQLException
    {

        connectDB();
        String sId = CommonConstants.ACTIVE_COMPANY_ID; 
        PreparedStatement stmt = null;        
        ResultSet rs = null;             
        HashMap<String, String> headerValues = new HashMap<>();
        
        
        String sql = "SELECT SUM(income_count), sum(debit), sum(credit), 0 credit_combo  " +
"from ( " +
 "	SELECT count(id) income_count, 0 debit, sum(credit_amount) credit, 0 credit_combo   " +
 "	FROM employee_advance_amount_credit   " +
 "	WHERE company_id = ?   " +
 "	AND credited_date = ?   " +
 "	union all     " +
 "	SELECT count(id) income_count, 0 debit, sum(credited_amount) credit, 0 credit_combo  " +
 "	FROM employee_other_amount_credit   " +
 "	WHERE company_id = ?   " +
 "	AND credited_date = ?   " +
 "	union all     " +
 "	SELECT count(id) income_count, 0 debit, sum(credited_amount) credit, 0 credit_combo  " +
 "	FROM company_bill_credit   " +
 "	WHERE company_id = ?   " +
 "	AND credited_date = ?   " +
 "	union all     " +
 "	SELECT count(id) income_count, 0 debit, sum(credited_amount) credit, 0 credit_combo  " +
 "	FROM company_other_credit   " +
 "	WHERE company_id = ?   " +
 "	AND credited_date = ?   " +
 "	union all     " +
 "	SELECT count(id) income_count, 0 debit, sum(credited_amount) credit, 0 credit_combo  " +
 "	FROM repledge_bill_credit   " +
 "	WHERE company_id = ?   " +
 "	AND credited_date = ?   " +
 "	union all     " +
 "	SELECT count(id) income_count, 0 debit, sum(credited_amount) credit, 0 credit_combo  " +
 "	FROM repledge_other_credit   " +
 "	WHERE company_id = ?   " +
 "	AND credited_date = ?) all_incomes  ";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlOpeningDate);
            stmt.setString(3, sId);
            stmt.setDate(4, sqlOpeningDate);
            stmt.setString(5, sId);
            stmt.setDate(6, sqlOpeningDate);
            stmt.setString(7, sId);
            stmt.setDate(8, sqlOpeningDate);
            stmt.setString(9, sId);
            stmt.setDate(10, sqlOpeningDate);
            stmt.setString(11, sId);
            stmt.setDate(12, sqlOpeningDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                headerValues.put("income_count", Long.toString(rs.getLong(1)));
                headerValues.put("debit", Double.toString(rs.getDouble(2)));
                headerValues.put("credit",Double.toString(rs.getDouble(3)));
                headerValues.put("credit_combo",rs.getString(4));
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

        return headerValues;	
    }
    
    public DataTable getIncomeDetailedPrintValue(String sOpeningDate) throws SQLException
    {

        connectDB();
        String sId = CommonConstants.ACTIVE_COMPANY_ID; 
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT credited_date, id, 'EMPLOYEE ADVANCE AMOUNT', "
                + "concat(employee_name, ' - ', 'EMPLOYEE ADVANCE AMOUNT'), credit_amount, user_id  " +
"FROM employee_advance_amount_credit   " +
"WHERE company_id = ?   " +
"AND credited_date = ?   " +
"union all     " +
"SELECT credited_date, id, 'EMPLOYEE OTHER AMOUNT', concat(employee_name, ' - ', credited_reason), credited_amount, user_id  " +
"FROM employee_other_amount_credit   " +
"WHERE company_id = ?   " +
"AND credited_date = ?   " +
"union all     " +
"SELECT credited_date, id, 'COMPANY BILL', concat(jewel_material_type, ' - ', bill_number), credited_amount, user_id   " +
"FROM company_bill_credit   " +
"WHERE company_id = ?   " +
"AND credited_date = ?   " +
"union all     " +
"SELECT credited_date, id, 'COMPANY OTHER', concat(expense_type, ' - ', name, ' - ', reason), credited_amount, user_id   " +
"FROM company_other_credit   " +
"WHERE company_id = ?   " +
"AND credited_date = ?   " +
"union all     " +
"SELECT credited_date, id, 'REPLEDGE BILL', concat(' CompBillNo: ', bill_number, ' - ', repledge_name), credited_amount, user_id   " +
"FROM repledge_bill_credit   " +
"WHERE company_id = ?   " +
"AND credited_date = ?   " +
"union all     " +
"SELECT credited_date, id, 'REPLEDGE OTHER', concat(repledge_name, ' - ', reason), credited_amount, user_id   " +
"FROM repledge_other_credit   " +
"WHERE company_id = ?   " +
"AND credited_date = ?   ";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlOpeningDate);
            stmt.setString(3, sId);
            stmt.setDate(4, sqlOpeningDate);
            stmt.setString(5, sId);
            stmt.setDate(6, sqlOpeningDate);
            stmt.setString(7, sId);
            stmt.setDate(8, sqlOpeningDate);
            stmt.setString(9, sId);
            stmt.setDate(10, sqlOpeningDate);
            stmt.setString(11, sId);
            stmt.setDate(12, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(formatter.format(rs.getDate(1)));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
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
    
    public double getEmployeeAdvanceAmountReducedBySalId(String sIdReduceBy) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        
        
        String sql = "SELECT SUM(COALESCE(debitted_amount,'0')) " +
                "  FROM employee_advance_amount_debit " +
                "  where company_id = ? " +
                "  AND id_reduced_by = ? " +
                "  AND debit_action = 'REDUCED'";

        try {
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sIdReduceBy);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                return rs.getDouble(1);
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

        return 0;	
    }

    public double getEmployeeTotalSal(String sEmpId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        
        
        String sql = "SELECT salary_amount " +
                "  FROM employee " +
                "  where company_id = ? " +
                "  AND id = ? ";

        try {
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sEmpId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                return rs.getDouble(1);
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

        return 0;	
    }

    public String getEmployeeAdvanceAmountDetails(String sIdReduceBy) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;         
        Format formatter = new SimpleDateFormat(CommonConstants.SHORT_DATE_FORMAT);
        StringBuilder sb = new StringBuilder("");
        
        String sql = "SELECT debitted_date, debitted_amount " +
                "  FROM employee_advance_amount_debit " +
                "  where company_id = ? " +
                "  AND id_reduced_by = ? " +
                "  AND debit_action = 'REDUCED'";

        try {
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sIdReduceBy);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                sb.append(formatter.format(rs.getDate(1)));
                sb.append("-");
                sb.append(rs.getDouble(2));
                sb.append(", ");
            }		   
            if(sb.lastIndexOf(",") >= 0) {
                sb.deleteCharAt(sb.lastIndexOf(","));            
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

        return sb.toString();	
    }
    
    public String getRepBillCalcClosingPlannerId(String closingDate, String actualBillNumber,
            String sOperation) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT RBCH.planner_id "
                + "FROM repledge_bill_calc rbc, repledge_bill_calc_header rbch "
                + "WHERE RBCH.COMPANY_ID = ? "
                + "AND RBC.company_actual_bill_number = ? "
                + "AND RBCH.closing_date = ? "
                + "AND RBCH.COMPANY_ID = RBC.COMPANY_ID "
                + "AND RBCH.planner_id = RBC.planner_id "
                + "AND RBCH.is_printed = ? "
                + "and rbc.operation = ? ";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(closingDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, actualBillNumber);
            stmt.setDate(3, sqlOpeningDate);
            stmt.setBoolean(4, Boolean.TRUE);
            stmt.setString(5, sOperation);
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

    public String getRepBillCalcOpenPlannerId(String closingDate, String companyBillNumber) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT RBCH.planner_id "
                + "FROM repledge_bill_calc rbc, repledge_bill_calc_header rbch "
                + "WHERE RBCH.COMPANY_ID = ? "
                + "AND RBC.company_bill_number = ? "
                + "AND RBCH.closing_date = ? "
                + "AND RBCH.COMPANY_ID = RBC.COMPANY_ID "
                + "AND RBCH.planner_id = RBC.planner_id "
                + "AND RBCH.is_printed = ? "
                + "and rbc.operation = ? ";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(closingDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, companyBillNumber);
            stmt.setDate(3, sqlOpeningDate);
            stmt.setBoolean(4, Boolean.TRUE);
            stmt.setString(5, "BILL OPENING");
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
    
    public DataTable getRepBillOpenPlannedButNotPrintPreparation(String closingDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        DataTable dataTable = new DataTable();
        ResultSet rs = null;

        String sql = "SELECT RBCH.planner_id, before_after, rbc.repledge_bill_id, rbc.operation,  " +
"rbc.company_bill_number, rbc.company_actual_bill_number, rbc.repledge_opening_date, rbc.repledge_bill_number,  " +
"rbc.repledge_id, rbc.repledge_name, rbc.items, rbc.amount, rbc.interest, rbc.interested_amount,  " +
"rbc.total_interested_amount, rbc.status " +
"FROM repledge_bill_calc rbc, repledge_bill_calc_header rbch " +
"WHERE RBCH.COMPANY_ID = ?  " +
"AND RBCH.COMPANY_ID = RBC.COMPANY_ID  " +
"AND RBCH.closing_date = ?  " +
"AND RBCH.planner_id = RBC.planner_id  " +
"AND RBCH.is_printed = ?  " +
"and rbc.operation = ?  " +
"and rbc.company_bill_number not in "
                + "(select company_bill_number " +
                    "FROM REPLEDGE_BILLING " +
                    "where company_id = ? " +
                    "AND OPENING_DATE = ?)";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(closingDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpeningDate);
            stmt.setBoolean(3, Boolean.TRUE);
            stmt.setString(4, "BILL OPENING");
            stmt.setString(5, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(6, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {		       
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
                row.addColumn(rs.getString(7));
                row.addColumn(rs.getString(8));
                row.addColumn(rs.getString(9));
                row.addColumn(rs.getString(10));
                row.addColumn(rs.getString(11));
                row.addColumn(rs.getString(12));
                row.addColumn(rs.getString(13));
                row.addColumn(rs.getString(14));
                row.addColumn(rs.getString(15));
                row.addColumn(rs.getString(16));
                dataTable.add(row);                	       
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }        
        return dataTable;
    }

    public DataTable getRepBillClosePlannedButNotPrintPreparation(String closingDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        DataTable dataTable = new DataTable();
        ResultSet rs = null;

        String sql = "SELECT RBCH.planner_id, before_after, rbc.repledge_bill_id, rbc.operation,  " +
"rbc.company_bill_number, rbc.company_actual_bill_number, rbc.repledge_opening_date, rbc.repledge_bill_number,  " +
"rbc.repledge_id, rbc.repledge_name, rbc.items, rbc.amount, rbc.interest, rbc.interested_amount,  " +
"rbc.total_interested_amount, rbc.status " +
"FROM repledge_bill_calc rbc, repledge_bill_calc_header rbch " +
"WHERE RBCH.COMPANY_ID = ?  " +
"AND RBCH.COMPANY_ID = RBC.COMPANY_ID  " +
"AND RBCH.closing_date = ?  " +
"AND RBCH.planner_id = RBC.planner_id  " +
"AND RBCH.is_printed = ?  " +
"and rbc.operation = ?  " +
"and rbc.company_actual_bill_number not in "
                + "(select company_bill_number " +
                    "FROM REPLEDGE_BILLING " +
                    "where company_id = ? " +
                    "AND closing_date = ?)";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(closingDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpeningDate);
            stmt.setBoolean(3, Boolean.TRUE);
            stmt.setString(4, "BILL CLOSING");
            stmt.setString(5, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(6, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {		       
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
                row.addColumn(rs.getString(7));
                row.addColumn(rs.getString(8));
                row.addColumn(rs.getString(9));
                row.addColumn(rs.getString(10));
                row.addColumn(rs.getString(11));
                row.addColumn(rs.getString(12));
                row.addColumn(rs.getString(13));
                row.addColumn(rs.getString(14));
                row.addColumn(rs.getString(15));
                row.addColumn(rs.getString(16));
                dataTable.add(row);                	       
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }        
        return dataTable;
    }

    public DataTable getRepBillSuspensePlannedButNotPrintPreparation(String closingDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        DataTable dataTable = new DataTable();
        ResultSet rs = null;

        String sql = "SELECT RBCH.planner_id, before_after, rbc.repledge_bill_id, rbc.operation,  " +
"rbc.company_bill_number, rbc.company_actual_bill_number, rbc.repledge_opening_date, rbc.repledge_bill_number,  " +
"rbc.repledge_id, rbc.repledge_name, rbc.items, rbc.amount, rbc.interest, rbc.interested_amount,  " +
"rbc.total_interested_amount, rbc.status " +
"FROM repledge_bill_calc rbc, repledge_bill_calc_header rbch " +
"WHERE RBCH.COMPANY_ID = ?  " +
"AND RBCH.COMPANY_ID = RBC.COMPANY_ID  " +
"AND RBCH.closing_date = ?  " +
"AND RBCH.planner_id = RBC.planner_id  " +
"AND RBCH.is_printed = ?  " +
"and rbc.operation = ?  " +
"and rbc.company_actual_bill_number not in "
                + "(select company_bill_number " +
                    "FROM REPLEDGE_BILLING " +
                    "where company_id = ? " +
                    "AND suspense_date = ?)";

        try {
            java.sql.Date sqlOpeningDate = java.sql.Date.valueOf(LocalDate.parse(closingDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpeningDate);
            stmt.setBoolean(3, Boolean.TRUE);
            stmt.setString(4, "GET SUSPENSE");
            stmt.setString(5, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(6, sqlOpeningDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {		       
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(rs.getString(5));
                row.addColumn(rs.getString(6));
                row.addColumn(rs.getString(7));
                row.addColumn(rs.getString(8));
                row.addColumn(rs.getString(9));
                row.addColumn(rs.getString(10));
                row.addColumn(rs.getString(11));
                row.addColumn(rs.getString(12));
                row.addColumn(rs.getString(13));
                row.addColumn(rs.getString(14));
                row.addColumn(rs.getString(15));
                row.addColumn(rs.getString(16));
                dataTable.add(row);                	       
            }		    

        } catch (SQLException e) {
                throw e;
        }finally {
            rs.close();
            stmt.close();
            roleMasterConn.close();
        }        
        return dataTable;
    }
    
    public String getRebilledFromDetails(String sMaterialType, String newNumber) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;         
        Format formatter = new SimpleDateFormat(CommonConstants.SHORT_DATE_FORMAT);
        StringBuilder sb = new StringBuilder("");
        
        String sql = "SELECT rebilled_from " +
                "  FROM company_billing " +
                "  where company_id = ? " +
                "  AND bill_number = ? " +
                "  AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                "  AND rebilled_from IS NOT NULL ";

        try {
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, newNumber);
            stmt.setString(3, sMaterialType);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                //sb.append("RB:");
                sb.append(rs.getString(1));
            } else {
                sb.append("NB");
            }		   
            if(sb.lastIndexOf(",") >= 0) {
                sb.deleteCharAt(sb.lastIndexOf(","));            
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

        return sb.toString();	
    }

    public int getRatePerGram(String sMaterialType, String newNumber) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;         
        Format formatter = new SimpleDateFormat(CommonConstants.SHORT_DATE_FORMAT);
        
        String sql = "SELECT (amount/gross_weight) " +
                "  FROM company_billing " +
                "  where company_id = ? " +
                "  AND bill_number = ? " +
                "  AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";

        try {
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, newNumber);
            stmt.setString(3, sMaterialType);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                return rs.getInt(1);
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

        return 0;	
    }
    
}
