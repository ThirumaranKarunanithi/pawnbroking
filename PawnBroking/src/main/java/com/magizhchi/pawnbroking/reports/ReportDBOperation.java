/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.reports;

import com.magizhchi.pawnbroking.common.CommonConstants;
import com.magizhchi.pawnbroking.common.DataRow;
import com.magizhchi.pawnbroking.common.DataTable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tiru
 */
public class ReportDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public ReportDBOperation(String sDB, String sIP, String sPort, String sSchema,
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

    public void connectDB() throws SQLException
    {		
        try {
            if(roleMasterConn == null || roleMasterConn.isClosed()) {
                roleMasterConn = DriverManager.getConnection("jdbc:"+sDB+"://"+sIP+":"+sPort+"/"+sSchema,sDBUsername, sDBPassword);
            }
        } catch (SQLException e) {
                throw e;
        }		
    }

    public void disConnectDB() throws SQLException
    {		
        try {
            roleMasterConn.close();
        } catch (SQLException e) {
                throw e;
        }		
    }
    
    public void commit() {
        try {
            roleMasterConn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(ReportDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    public DataTable getCompMISValues() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();        
        
        String sql = "select (case when child.mon = '01' then 'JAN' ||'-'||child.yyyy " +
                        "             when child.mon = '02' then 'FEB'||'-'||child.yyyy " +
                        "             when child.mon = '03' then 'MAR'||'-'||child.yyyy " +
                        "             when child.mon = '04' then 'APR'||'-'||child.yyyy " +
                        "             when child.mon = '05' then 'MAY'||'-'||child.yyyy " +
                        "             when child.mon = '06' then 'JUN'||'-'||child.yyyy " +
                        "             when child.mon = '07' then 'JUL'||'-'||child.yyyy " +
                        "             when child.mon = '08' then 'AUG'||'-'||child.yyyy " +
                        "             when child.mon = '09' then 'SEP'||'-'||child.yyyy " +
                        "             when child.mon = '10' then 'OCT'||'-'||child.yyyy " +
                        "             when child.mon = '11' then 'NOV'||'-'||child.yyyy " +
                        "             when child.mon = '12' then'DEC'||'-'||child.yyyy " +
                        "             else  " +
                        "             '00' " +
                        "             end )as mon, " +
                        "             pawn_total_bill,   " +
                        "             pawn_amount, " +
                        "             redeem_total_bills, " +
                        "             redeem_amt, " +
                        "             TOT_PROFIT, " +
                        "             total_stock_bills, " +
                        "             total_stock_amount, " +
                        "             (pawn_total_bill - redeem_total_bills) stock_bills_earned, " +
                        "             (pawn_amount - redeem_amt) stock_amount_earned " +
                        "from  " +
                        "(select mon,yyyy, " +
                        "       sum(pawn_total_bill)pawn_total_bill, " +
                        "          sum(pawn_amount)pawn_amount, " +
                        "          sum(redeem_total_bills)redeem_total_bills, " +
                        "          sum(redeem_amt)redeem_amt, " +
                        "          sum(interest) TOT_PROFIT, " +
                        "          sum((sum(pawn_total_bill) - sum(redeem_total_bills))) over(order by yyyy asc,mon) total_stock_bills, " +
                        "          sum((sum(pawn_amount) - sum(redeem_amt))) over(order by yyyy asc,mon) total_stock_amount " +
                        "from " +
                        "(select to_char(opening_date,'MM')as mon,   " +
                        "           extract(year from opening_date)as yyyy, " +
                        "       count(bill_number)pawn_total_bill, " +
                        "       sum(amount)pawn_amount, " +
                        "       0 redeem_total_bills,	 " +
                        "       0 redeem_amt, " +
                        "       0 interest " +
                        "from company_billing " +
                        "where STATUS NOT IN ('CANCELED') " +
                        "AND COMPANY_ID = ? " +
                        "group by 1,2 " +
                        "union all " +
                        "select to_char(closing_date,'MM')as mon,   " +
                        "           extract(year from closing_date)as yyyy, " +
                        "        0 pawn_total_bill, " +
                        "        0 pawn_amount, " +
                        "         count(bill_number) redeem_total_bills, " +
                        "          sum(amount) redeem_amt,          " +
                        "         0 interest " +
                        "from company_billing   " +
                        "where status in ('CLOSED', 'DELIVERED', 'REBILLED', 'REBILLED-ADDED', 'REBILLED-REMOVED', 'REBILLED-MULTIPLE') " +
                        "AND COMPANY_ID = ? " +
                        "group by 1,2 " +
                        "UNION ALL " +
                        "select to_char(todays_date,'MM')as mon,   " +
                        "           extract(year from todays_date)as yyyy, " +
                        "        0 pawn_total_bill, " +
                        "        0 pawn_amount, " +
                        "        0 redeem_total_bills, " +
                        "        0 redeem_amt,          " +
                        "        sum(todays_pf_amount) interest " +
                        "from company_todays_account_available_amount   " +
                        "WHERE COMPANY_ID = ? " +
                        "group by 1,2 " +
                        ")chi " +
                        "group by chi.mon,chi.yyyy " +
                        "order by chi.yyyy desc, chi.mon desc)child ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, CommonConstants.ACTIVE_COMPANY_ID);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(Integer.toString(rs.getInt(2)));
                row.addColumn(Double.toString(rs.getDouble(3)));
                row.addColumn(Integer.toString(rs.getInt(4)));
                row.addColumn(Double.toString(rs.getDouble(5)));
                row.addColumn(Double.toString(rs.getDouble(6)));
                row.addColumn(Integer.toString(rs.getInt(7)));
                row.addColumn(Double.toString(rs.getDouble(8)));
                row.addColumn(Integer.toString(rs.getInt(9)));
                row.addColumn(Double.toString(rs.getDouble(10)));
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
    
    public DataTable get8020ReportDetailValues(String sCompanyIds, String sMaterialType, String startDate, String endDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
                
        String sql = "(select b.rownum, " +
"       b.customer_name, b.spouse_type, b.spouse_name, b.door_number, b.street, b.area, b.city, " +
"       b.mobile_number, " +
"       b.amt, " +
"       b.total_amt, " +
"       b.percent, " +
"       b.running_total, " +
"       '80' cust_class " +
" from " +
"(select a.rownum, " +
"       a.customer_name, a.spouse_type, a.spouse_name, a.door_number, a.street, a.area, a.city, " +
"       a.mobile_number, " +
"       a.amt, " +
"       a.total_amt, " +
"       a.percent, " +
"       sum(a.percent) over (order by a.rownum) running_total " +
"from  " +
"(select row_number () over (order by sum(amount) desc) rownum,  " +
"       customer_name, spouse_type, spouse_name, door_number, street, area, city, " +
"       mobile_number, " +
"       sum(amount) amt, " +
"       sum(sum(amount)) over () total_amt, " +
"       round(100 * (sum(amount) / sum(sum(amount)) over ())::numeric,2) percent " +
"       from company_billing " +
"where company_id in (" + sCompanyIds + ") " +
"and jewel_material_type in (" + sMaterialType + ") " +
"and opening_date between ? and ? " +
"group by customer_name, spouse_type, spouse_name, door_number, street, area, city, " +
"       mobile_number " +
"order by sum(amount) desc) a " +
"group by a.rownum, " +
"       a.customer_name, a.spouse_type, a.spouse_name, a.door_number, a.street, a.area, a.city, " +
"       a.mobile_number, " +
"       a,amt, " +
"       a.total_amt, " +
"       a.percent)b " +
"group by b.rownum, " +
"       b.customer_name, b.spouse_type, b.spouse_name, b.door_number, b.street, b.area, b.city, " +
"       b.mobile_number, " +
"       b.amt, " +
"       b.total_amt, " +
"       b.percent, " +
"       b.running_total " +
"having b.running_total <= 80 " +
"order by b.amt desc) " +
"union all " +
"(select b.rownum, " +
"       b.customer_name, b.spouse_type, b.spouse_name, b.door_number, b.street, b.area, b.city, " +
"       b.mobile_number, " +
"       b.amt, " +
"       b.total_amt, " +
"       b.percent, " +
"       b.running_total, " +
"       '20' cust_class " +
" from " +
"(select a.rownum, " +
"       a.customer_name, a.spouse_type, a.spouse_name, a.door_number, a.street, a.area, a.city, " +
"       a.mobile_number, " +
"       a.amt, " +
"       a.total_amt, " +
"       a.percent, " +
"       sum(a.percent) over (order by a.rownum) running_total " +
"from  " +
"(select row_number () over (order by sum(amount) desc) rownum,  " +
"       customer_name, spouse_type, spouse_name, door_number, street, area, city, " +
"       mobile_number, " +
"       sum(amount) amt, " +
"       sum(sum(amount)) over () total_amt, " +
"       round(100 * (sum(amount) / sum(sum(amount)) over ())::numeric,2) percent " +
"       from company_billing " +
"where company_id in (" + sCompanyIds + ") " +
"and jewel_material_type in (" + sMaterialType + ") " +
"and opening_date between ? and ? " +
"group by customer_name, spouse_type, spouse_name, door_number, street, area, city, " +
"       mobile_number " +
"order by sum(amount) desc) a " +
"group by a.rownum, " +
"       a.customer_name, a.spouse_type, a.spouse_name, a.door_number, a.street, a.area, a.city, " +
"       a.mobile_number, " +
"       a,amt, " +
"       a.total_amt, " +
"       a.percent)b " +
"group by b.rownum, " +
"       b.customer_name, b.spouse_type, b.spouse_name, b.door_number, b.street, b.area, b.city, " +
"       b.mobile_number, " +
"       b.amt, " +
"       b.total_amt, " +
"       b.percent, " +
"       b.running_total " +
"having b.running_total >= 80 " +
"order by b.amt desc)";

        try {
            java.sql.Date sqlStartDate = java.sql.Date.valueOf(LocalDate.parse(startDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlEndDate = java.sql.Date.valueOf(LocalDate.parse(endDate, 
                    CommonConstants.DATETIMEFORMATTER));

            stmt = roleMasterConn.prepareStatement(sql);   
            stmt.setDate(1, sqlStartDate);
            stmt.setDate(2, sqlEndDate);
            stmt.setDate(3, sqlStartDate);
            stmt.setDate(4, sqlEndDate);
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
                row.addColumn(Double.toString(rs.getDouble(10)));
                row.addColumn(Double.toString(rs.getDouble(11)));
                row.addColumn(Float.toString(rs.getFloat(12)));                
                row.addColumn(Float.toString(rs.getFloat(13)));                
                row.addColumn(rs.getString(14));
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
    
    public DataTable get8020ReportSummaryValues(String sCompanyIds, String sMaterialType, String startDate, String endDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
                
        String sql = "select cust_class, " +
"       count(customer_name)customer, " +
"       sum(amt)total_amt " +
"from " +
"((select b.rownum, " +
"       b.customer_name, b.spouse_type, b.spouse_name, b.door_number, b.street, b.area, b.city, " +
"       b.mobile_number, " +
"       b.amt, " +
"       b.total_amt, " +
"       b.percent, " +
"       b.running_total, " +
"       '80' cust_class " +
" from " +
"(select a.rownum, " +
"       a.customer_name, a.spouse_type, a.spouse_name, a.door_number, a.street, a.area, a.city, " +
"       a.mobile_number, " +
"       a.amt, " +
"       a.total_amt, " +
"       a.percent, " +
"       sum(a.percent) over (order by a.rownum) running_total " +
"from  " +
"(select row_number () over (order by sum(amount) desc) rownum,  " +
"       customer_name, spouse_type, spouse_name, door_number, street, area, city, " +
"       mobile_number, " +
"       sum(amount) amt, " +
"       sum(sum(amount)) over () total_amt, " +
"       round(100 * (sum(amount) / sum(sum(amount)) over ())::numeric,2) percent " +
"       from company_billing " +
"where company_id in (" + sCompanyIds + ") " +
"and jewel_material_type in (" + sMaterialType + ") " +
"and opening_date between ? and ? " +
"group by customer_name, spouse_type, spouse_name, door_number, street, area, city, " +
"       mobile_number " +
"order by sum(amount) desc) a " +
"group by a.rownum, " +
"       a.customer_name, a.spouse_type, a.spouse_name, a.door_number, a.street, a.area, a.city, " +
"       a.mobile_number, " +
"       a,amt, " +
"       a.total_amt, " +
"       a.percent)b " +
"group by b.rownum, " +
"       b.customer_name, b.spouse_type, b.spouse_name, b.door_number, b.street, b.area, b.city, " +
"       b.mobile_number, " +
"       b.amt, " +
"       b.total_amt, " +
"       b.percent, " +
"       b.running_total " +
"having b.running_total <= 80 " +
"order by b.amt desc) " +
"union all " +
"(select b.rownum, " +
"       b.customer_name, b.spouse_type, b.spouse_name, b.door_number, b.street, b.area, b.city, " +
"       b.mobile_number, " +
"       b.amt, " +
"       b.total_amt, " +
"       b.percent, " +
"       b.running_total, " +
"       '20' cust_class " +
" from " +
"(select a.rownum, " +
"       a.customer_name, a.spouse_type, a.spouse_name, a.door_number, a.street, a.area, a.city, " +
"       a.mobile_number, " +
"       a.amt, " +
"       a.total_amt, " +
"       a.percent, " +
"       sum(a.percent) over (order by a.rownum) running_total " +
"from  " +
"(select row_number () over (order by sum(amount) desc) rownum,  " +
"       customer_name, spouse_type, spouse_name, door_number, street, area, city, " +
"       mobile_number, " +
"       sum(amount) amt, " +
"       sum(sum(amount)) over () total_amt, " +
"       round(100 * (sum(amount) / sum(sum(amount)) over ())::numeric,2) percent " +
"       from company_billing " +
"where company_id in (" + sCompanyIds + ") " +
"and jewel_material_type in (" + sMaterialType + ") " +
"and opening_date between ? and ? " +
"group by customer_name, spouse_type, spouse_name, door_number, street, area, city, " +
"       mobile_number " +
"order by sum(amount) desc) a " +
"group by a.rownum, " +
"       a.customer_name, a.spouse_type, a.spouse_name, a.door_number, a.street, a.area, a.city, " +
"       a.mobile_number, " +
"       a,amt, " +
"       a.total_amt, " +
"       a.percent)b " +
"group by b.rownum, " +
"       b.customer_name, b.spouse_type, b.spouse_name, b.door_number, b.street, b.area, b.city, " +
"       b.mobile_number, " +
"       b.amt, " +
"       b.total_amt, " +
"       b.percent, " +
"       b.running_total " +
"having b.running_total >= 80 " +
"order by b.amt desc))c " +
"group by cust_class " +
"order by cust_class desc";

        try {
            java.sql.Date sqlStartDate = java.sql.Date.valueOf(LocalDate.parse(startDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlEndDate = java.sql.Date.valueOf(LocalDate.parse(endDate, 
                    CommonConstants.DATETIMEFORMATTER));

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setDate(1, sqlStartDate);
            stmt.setDate(2, sqlEndDate);
            stmt.setDate(3, sqlStartDate);
            stmt.setDate(4, sqlEndDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
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
    
    public DataTable getCompaniesList() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT ID, NAME "
                + "FROM COMPANY ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);             
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
    
    public String getCompanyOtherIncome(String incomeOrLiability, 
            String sOpeningDate, String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0"; 
        
        String sql = "SELECT sum(credited_amount) " +
                    "FROM company_other_credit " +
                    "WHERE company_id = ? " +
                    "AND CREDITED_DATE BETWEEN ? AND ? " +
                    "AND income_or_liability = ?";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            stmt.setString(4, incomeOrLiability);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getRepOpenCapital(String sOpeningDate, String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(AMOUNT) " +
                    "FROM REPLEDGE_BILLING " +
                    "WHERE company_id = ? " +
                    "AND OPENING_DATE BETWEEN ? AND ?";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();
                
                System.out.println(amount);
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

        return amount;
    }
    
    public String getRepCloseCapital(String sOpeningDate, String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(AMOUNT) " +
                    "FROM REPLEDGE_BILLING " +
                    "WHERE company_id = ? " +
                    "AND CLOSING_DATE BETWEEN ? AND ?";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getCompOpenCapital(String sMaterialType, 
            String sOpeningDate, String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(AMOUNT) " +
                    "FROM COMPANY_BILLING " +
                    "WHERE company_id = ? " +
                    "AND OPENING_DATE BETWEEN ? AND ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND STATUS NOT IN ('CANCELED')";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            stmt.setString(4, sMaterialType);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getCompCloseCapital(String sMaterialType, 
            String sOpeningDate, String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(AMOUNT) " +
                    "FROM COMPANY_BILLING " +
                    "WHERE company_id = ? " +
                    "AND CLOSING_DATE BETWEEN ? AND ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            stmt.setString(4, sMaterialType);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getCompanyOtherExpense(String expenseOrAsset, 
            String sOpeningDate, String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(DEBITTED_AMOUNT) " +
                    "FROM company_other_debit " +
                    "WHERE company_id = ? " +                     
                    "AND DEBITTED_DATE BETWEEN ? AND ? " +
                    "AND expense_or_asset = ?";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            stmt.setString(4, expenseOrAsset);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getCompOpenInterest(String sMaterialType, 
            String sOpeningDate, String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(OPEN_TAKEN_AMOUNT) " +
                    "FROM COMPANY_BILLING " +
                    "WHERE company_id = ? " +
                    "AND OPENING_DATE BETWEEN ? AND ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND STATUS NOT IN ('CANCELED')";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            stmt.setString(4, sMaterialType);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getCompOpenDocCharge(String sMaterialType, 
            String sOpeningDate, String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(DOCUMENT_CHARGE) " +
                    "FROM COMPANY_BILLING " +
                    "WHERE company_id = ? " +
                    "AND OPENING_DATE BETWEEN ? AND ? "+
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND STATUS NOT IN ('CANCELED')";
        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            stmt.setString(4, sMaterialType);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getCompCloseInterest(String sMaterialType, 
            String sOpeningDate, String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(CLOSE_TAKEN_AMOUNT) " +
                    "FROM COMPANY_BILLING " +
                    "WHERE company_id = ? " +
                    "AND CLOSING_DATE BETWEEN ? AND ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            stmt.setString(4, sMaterialType);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getCompOtherCharges(String sMaterialType, 
            String sOpeningDate, String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(TOTAL_OTHER_CHARGES) " +
                    "FROM COMPANY_BILLING " +
                    "WHERE company_id = ? " +
                    "AND CLOSING_DATE BETWEEN ? AND ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            stmt.setString(4, sMaterialType);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getAdvanceAmountPaid(String sOpeningDate, String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(PAID_AMOUNT) " +
                    "FROM COMPANY_ADVANCE_AMOUNT " +
                    "WHERE company_id = ? " +
                    "AND PAID_DATE BETWEEN ? AND ? ";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getAdvanceAmountReduced(String sOpeningDate, String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(TOTAL_ADVANCE_AMOUNT_PAID) " +
                    "FROM COMPANY_BILLING " +
                    "WHERE company_id = ? " +
                    "AND closing_date  BETWEEN ? AND ? ";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getRepOpenInterest(String sOpeningDate, 
            String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(OPEN_TAKEN_AMOUNT) " +
                    "FROM REPLEDGE_BILLING " +
                    "WHERE company_id = ? " +
                    "AND OPENING_DATE BETWEEN ? AND ? ";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getRepOpenDocCharge(String sOpeningDate, 
            String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(DOCUMENT_CHARGE) " +
                    "FROM REPLEDGE_BILLING " +
                    "WHERE company_id = ? " +
                    "AND OPENING_DATE BETWEEN ? AND ? ";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getRepCloseInterest(String sOpeningDate, 
            String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(GIVEN_AMOUNT-AMOUNT) " +
                    "FROM REPLEDGE_BILLING " +
                    "WHERE company_id = ? " +
                    "AND CLOSING_DATE BETWEEN ? AND ? ";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getRepOtherBillExpense(String sOpeningDate, 
            String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(DEBITTED_AMOUNT) " +
                    "FROM REPLEDGE_BILL_DEBIT " +
                    "WHERE company_id = ? " +                     
                    "AND DEBITTED_DATE BETWEEN ? AND ?";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getRepOtherExpense(String sOpeningDate, 
            String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(DEBITTED_AMOUNT) " +
                    "FROM REPLEDGE_OTHER_DEBIT " +
                    "WHERE company_id = ? " +                     
                    "AND DEBITTED_DATE BETWEEN ? AND ?";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getEmpDailyAllowanceExpense(String sOpeningDate, 
            String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(DEBITTED_AMOUNT) " +
                    "FROM EMPLOYEE_DAILY_ALLOWANCE_DEBIT " +
                    "WHERE company_id = ? " +                     
                    "AND DEBITTED_DATE BETWEEN ? AND ?";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getEmpAdvanceAmtExpense(String sOpeningDate, 
            String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(DEBITTED_AMOUNT) " +
                    "FROM EMPLOYEE_ADVANCE_AMOUNT_DEBIT " +
                    "WHERE company_id = ? " +                     
                    "AND DEBITTED_DATE BETWEEN ? AND ?";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getEmpSalaryAmtExpense(String sOpeningDate, 
            String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(DEBITTED_AMOUNT) " +
                    "FROM EMPLOYEE_SALARY_AMOUNT_DEBIT " +
                    "WHERE company_id = ? " +                     
                    "AND DEBITTED_DATE BETWEEN ? AND ?";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getEmpOtherAmtExpense(String sOpeningDate, 
            String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(DEBITTED_AMOUNT) " +
                    "FROM EMPLOYEE_OTHER_AMOUNT_DEBIT " +
                    "WHERE company_id = ? " +                     
                    "AND DEBITTED_DATE BETWEEN ? AND ?";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getCompBillExpense(String sOpeningDate, 
            String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(DEBITTED_AMOUNT) " +
                    "FROM COMPANY_BILL_DEBIT " +
                    "WHERE company_id = ? " +                     
                    "AND DEBITTED_DATE BETWEEN ? AND ?";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getCompDiscount(String sMaterialType, 
            String sOpeningDate, String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(DISCOUNT_AMOUNT) " +
                    "FROM COMPANY_BILLING " +
                    "WHERE company_id = ? " +
                    "AND CLOSING_DATE BETWEEN ? AND ? " +
                    "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            stmt.setString(4, sMaterialType);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getPreDayActualAmount(String sOpeningDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;                     
        String amount = "0";
        
        java.sql.Date sqlOpenDate = java.sql.Date.valueOf(
                LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
        
        String sql = "SELECT PRE_ACTUAL_AMOUNT " +
                    "FROM COMPANY_TODAYS_ACCOUNT " +
                    "WHERE COMPANY_ID = ? " +
                    "AND TODAYS_DATE = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = Double.toString(rs.getDouble(1));
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

        return amount;	
    }

    public String getEmployeeAdvAmtCredit(String sOpeningDate, String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(CREDIT_AMOUNT) " +
                    "FROM EMPLOYEE_ADVANCE_AMOUNT_CREDIT " +
                    "WHERE company_id = ? " +
                    "AND CREDITED_DATE BETWEEN ? AND ?";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getEmployeeOtherAmtCredit(String sOpeningDate, String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(CREDITED_AMOUNT) " +
                    "FROM EMPLOYEE_OTHER_AMOUNT_CREDIT " +
                    "WHERE company_id = ? " +
                    "AND CREDITED_DATE BETWEEN ? AND ?";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }

    public String getCompBillCredit(String sOpeningDate, 
            String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(CREDITED_AMOUNT) " +
                    "FROM COMPANY_BILL_CREDIT " +
                    "WHERE company_id = ? " +                     
                    "AND CREDITED_DATE BETWEEN ? AND ?";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getRepBillCredit(String sOpeningDate, 
            String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(CREDITED_AMOUNT) " +
                    "FROM REPLEDGE_BILL_CREDIT " +
                    "WHERE company_id = ? " +                     
                    "AND CREDITED_DATE BETWEEN ? AND ?";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    public String getRepOtherCredit(String sOpeningDate, 
            String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT sum(CREDITED_AMOUNT) " +
                    "FROM REPLEDGE_OTHER_CREDIT " +
                    "WHERE company_id = ? " +                     
                    "AND CREDITED_DATE BETWEEN ? AND ?";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }
    
    /*public String getRepOpenInterestDetails(String sOpeningDate, 
            String sClosingDate) throws SQLException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        String amount = "0";
        
        String sql = "SELECT OPEN_TAKEN_AMOUNT " +
                    "FROM REPLEDGE_BILLING " +
                    "WHERE company_id = ? " +
                    "AND OPENING_DATE BETWEEN ? AND ? ";

        try {
            java.sql.Date sqlOpenDate = java.sql.Date.valueOf(LocalDate.parse(sOpeningDate, 
                    CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlCloseDate = java.sql.Date.valueOf(LocalDate.parse(sClosingDate, 
                    CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlOpenDate);
            stmt.setDate(3, sqlCloseDate);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                amount = BigDecimal.valueOf(rs.getDouble(1)).toPlainString();               	       
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

        return amount;
    }*/
    
    public DataTable getBillOpeningTableValue(
            String sId, String sMaterialType, 
            String sStartingDate, String sEndingDate) 
            throws SQLException
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
"AND OPENING_DATE between ? and ? "
+ "ORDER BY BILL";

        try {

            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlStartingDate);
            stmt.setDate(4, sqlSEndingDate);
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
    
    public DataTable getBillClosingTableValue(
            String sId, String sMaterialType, 
            String sStartingDate, String sEndingDate) 
            throws SQLException
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
"AND CLOSING_DATE between ? and ? "
+ "ORDER BY BILL";

        try {

            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlStartingDate);
            stmt.setDate(4, sqlSEndingDate);
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
    
    public DataTable getReGroupBillOpeningTableValue(
            String sId, String sMaterialType, 
            String sStartingDate, String sEndingDate)
            throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT REPLEDGE_NAME, COUNT(COMPANY_BILL_NUMBER), SUM(AMOUNT) AS OPEN_CAPITAL_AMOUNT, \n" +
                "SUM(open_taken_amount) AS OPEN_TAKEN_AMOUNT \n" +
                "FROM REPLEDGE_BILLING " +
                "WHERE COMPANY_ID = ? " +
                "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                "AND OPENING_DATE BETWEEN ? AND ? " +
                "GROUP BY REPLEDGE_NAME " +
                "ORDER BY REPLEDGE_NAME";

        try {

            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlStartingDate);
            stmt.setDate(4, sqlSEndingDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
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

    public DataTable getReBillOpeningTableValue(
            String sId, String sMaterialType, 
            String sStartingDate, String sEndingDate)
            throws SQLException
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
                "AND OPENING_DATE BETWEEN ? AND ? "
                + "ORDER BY REPLEDGE_NAME";

        try {

            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlStartingDate);
            stmt.setDate(4, sqlSEndingDate);
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
    
    public DataTable getReGroupBillClosingTableValue(
            String sId, String sMaterialType, 
            String sStartingDate, String sEndingDate)
            throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT REPLEDGE_NAME, "
                + "COUNT(COMPANY_BILL_NUMBER), SUM(AMOUNT) "
                + "AS CAPITAL_AMOUNT, " +
                "SUM(GIVEN_AMOUNT - AMOUNT) "
                + "AS INTEREST_GIVEN_AMOUNT, " +
                "SUM(GIVEN_AMOUNT) AS GIVEN_AMOUNT " +
                "FROM REPLEDGE_BILLING " +
                "WHERE COMPANY_ID = ? " +
                "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                "AND CLOSING_DATE BETWEEN ? AND ? "
                + "GROUP BY REPLEDGE_NAME " +
                "ORDER BY REPLEDGE_NAME";

        try {

            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlStartingDate);
            stmt.setDate(4, sqlSEndingDate);
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
    
    public DataTable getReBillClosingTableValue(
            String sId, String sMaterialType, 
            String sStartingDate, String sEndingDate)
            throws SQLException
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
                "AND CLOSING_DATE BETWEEN ? AND ? "
                + "ORDER BY REPLEDGE_NAME";

        try {

            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlStartingDate);
            stmt.setDate(4, sqlSEndingDate);
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

    public DataTable getOtherAsset(
            String sId, 
            String sStartingDate, String sEndingDate) 
            throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT  debitted_date, id, 'COMPANY OTHER AMOUNT'"
                + ", concat(expense_or_asset, ' - ', "
                + "expense_type, ' - ', name, ' - ', reason)"
                + ", debitted_amount, user_id  " +
                    "FROM company_other_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date BETWEEN  ?  AND ? "
                + "and expense_or_asset = 'ASSET'";

        try {
            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlStartingDate);
            stmt.setDate(3, sqlSEndingDate);
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

    public DataTable getRepOtherBillExpense(
            String sId, 
            String sStartingDate, String sEndingDate) 
            throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT  debitted_date, id, 'REPLEDGE Bill'"
                + ", concat('CompBillNo: ', bill_number, ' - '"
                + ", repledge_name), debitted_amount, user_id   " +
                    "FROM repledge_bill_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date BETWEEN  ?  AND ? ";

        try {
            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlStartingDate);
            stmt.setDate(3, sqlSEndingDate);
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
    
    public DataTable getRepOtherExpense(
            String sId, 
            String sStartingDate, String sEndingDate) 
            throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT  debitted_date, id, 'REPLEDGE OTHER AMOUNT'"
                + ", concat(repledge_name, ' - ', reason), debitted_amount"
                + ", user_id    " +
                    "FROM repledge_other_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date BETWEEN  ?  AND ? ";

        try {
            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlStartingDate);
            stmt.setDate(3, sqlSEndingDate);
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
    
    public DataTable getEmpDailyAllowance(
            String sId, 
            String sStartingDate, String sEndingDate) 
            throws SQLException
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
                    "AND debitted_date BETWEEN  ?  AND ? "
                + "order by debitted_date";

        try {
            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlStartingDate);
            stmt.setDate(3, sqlSEndingDate);
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
    
    public DataTable getEmpAdvAmountExpense(
            String sId, 
            String sStartingDate, String sEndingDate) 
            throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT  debitted_date, id, 'EMPLOYEE ADVANCE AMOUNT'"
                + ", concat('Salary Advance Amount - ', employee_name, ' - ', reason)"
                + ", debitted_amount, user_id  " +
                    "FROM employee_advance_amount_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date BETWEEN  ?  AND ? ";

        try {
            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlStartingDate);
            stmt.setDate(3, sqlSEndingDate);
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
    
    public DataTable getEmpSalaryAmountExpense(
            String sId, 
            String sStartingDate, String sEndingDate) 
            throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT  debitted_date, id, 'EMPLOYEE SALARY AMOUNT'"
                + ", concat(employee_id, ' - ', employee_name), debitted_amount"
                + ", user_id   " +
                    "FROM employee_salary_amount_debit   " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date BETWEEN  ?  AND ? ";

        try {
            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlStartingDate);
            stmt.setDate(3, sqlSEndingDate);
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
    
    public DataTable getEmpOtherExpense(
            String sId, 
            String sStartingDate, String sEndingDate) 
            throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT  debitted_date, id, 'EMPLOYEE OTHER AMOUNT'"
                + ", concat(employee_name, ' - ', debitted_reason)"
                + ", debitted_amount, user_id  " +
                    "FROM employee_other_amount_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date BETWEEN  ?  AND ? ";

        try {
            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlStartingDate);
            stmt.setDate(3, sqlSEndingDate);
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
    
    public DataTable getCompBillExpense(
            String sId, 
            String sStartingDate, String sEndingDate) 
            throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT  debitted_date, id, 'COMPANY Bill'"
                + ", concat(jewel_material_type, ' - ', bill_number)"
                + ", debitted_amount, user_id  " +
                    "FROM company_bill_debit   " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date BETWEEN  ?  AND ? ";

        try {
            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlStartingDate);
            stmt.setDate(3, sqlSEndingDate);
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
    
    public DataTable getCompOtherExpense(
            String sId, 
            String sStartingDate, String sEndingDate) 
            throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT  debitted_date, id, 'COMPANY OTHER AMOUNT'"
                + ", concat(expense_or_asset, ' - ', expense_type, ' - ', name, ' - ', reason)"
                + ", debitted_amount, user_id  " +
                    "FROM company_other_debit  " +
                    "WHERE company_id = ?  " +
                    "AND debitted_date BETWEEN  ?  AND ? "
                + "order by debitted_date";

        try {
            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlStartingDate);
            stmt.setDate(3, sqlSEndingDate);
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
    
    public DataTable getCompBillIncome(
            String sId, 
            String sStartingDate, String sEndingDate) 
            throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT credited_date, id, 'COMPANY BILL', concat(jewel_material_type, ' - ', bill_number), credited_amount, user_id   " +
                    "FROM company_bill_credit   " +
                    "WHERE company_id = ?  " +
                    "AND credited_date between ? and ?  ";

        try {
            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlStartingDate);
            stmt.setDate(3, sqlSEndingDate);
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
    
    public DataTable getCompOtherIncome(
            String sId, 
            String sStartingDate, String sEndingDate) 
            throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT credited_date, id, 'COMPANY OTHER'"
+ ", concat(income_or_liability, ' - ', expense_type, ' - ', name, ' - ', reason)"
                + ", credited_amount, user_id   " +
"FROM company_other_credit   " +
"WHERE company_id = ?   " +
                    "AND credited_date between ? and ?  "
                    + "AND income_or_liability = 'INCOME' ";

        try {
            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlStartingDate);
            stmt.setDate(3, sqlSEndingDate);
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
    
    public DataTable getRepBillIncome(
            String sId, 
            String sStartingDate, String sEndingDate) 
            throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT credited_date, id, 'REPLEDGE BILL', concat(' CompBillNo: ', bill_number, ' - ', repledge_name), credited_amount, user_id   " +
"FROM repledge_bill_credit   " +
"WHERE company_id = ?   " +
                    "AND credited_date between ? and ?  ";

        try {
            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlStartingDate);
            stmt.setDate(3, sqlSEndingDate);
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
    
    public DataTable getRepOtherIncome(
            String sId, 
            String sStartingDate, String sEndingDate) 
            throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT credited_date, id, 'REPLEDGE OTHER', concat(repledge_name, ' - ', reason), credited_amount, user_id   " +
"FROM repledge_other_credit   " +
"WHERE company_id = ?  " +
                    "AND credited_date between ? and ?  ";

        try {
            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlStartingDate);
            stmt.setDate(3, sqlSEndingDate);
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
    
    public DataTable getOutSideInvestment(
            String sId, 
            String sStartingDate, String sEndingDate) 
            throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT credited_date, id, 'COMPANY OTHER'"
            + ", concat(income_or_liability, ' - ', "
            + "expense_type, ' - ', name, ' - ', reason)"
            + ", credited_amount, user_id   " +
            "FROM company_other_credit   " +
            "WHERE company_id = ?   " +
            "AND credited_date between ? and ?  "
                + "AND income_or_liability = 'LIABILITY' ";

        try {
            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlStartingDate);
            stmt.setDate(3, sqlSEndingDate);
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
    
    public DataTable getEmployeeInvestment(
            String sId, 
            String sStartingDate, String sEndingDate) 
            throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        Format formatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        
        String sql = "SELECT credited_date, id, 'COMPANY BILL', "
                + "concat(jewel_material_type, ' - ', bill_number)"
                + ", credited_amount, user_id   " +
            "FROM company_bill_credit   " +
            "WHERE company_id = ?   " +
            "AND credited_date between ? and ?   ";

        try {
            java.sql.Date sqlStartingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sStartingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            java.sql.Date sqlSEndingDate 
                    = java.sql.Date.valueOf(
                            LocalDate.parse(
                                    sEndingDate, 
                                    CommonConstants
                                            .DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setDate(2, sqlStartingDate);
            stmt.setDate(3, sqlSEndingDate);
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
    
}
