/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.common;

import com.magizhchi.pawnbroking.loginscreen.FingerPrintBean;
import com.magizhchi.pawnbroking.reports.RepledgeBillPrintBean;
import com.magizhchi.pawnbroking.reports.ShopLockerPrintBean;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tiru
 */
public class CommonDBOperation {
    
    private final String sDB;
    private final String sIP;
    private final String sPort;
    private final String sSchema;
    private final String sDBUsername;
    private final String sDBPassword; 
    private Connection roleMasterConn;

    private DateTimeFormatter format = DateTimeFormatter.ofPattern(CommonConstants.DATE_FORMAT);
    
    public CommonDBOperation(String sDB, String sIP, String sPort, String sSchema,
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

    public String[] getActiveCompanyId() throws SQLException
    {

        String[] idNName = new String[3];
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT ID, NAME, TYPE FROM COMPANY WHERE STATUS = ?::COMPANY_STATUS";

        try {

            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, "ACTIVE");
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                idNName[0] = rs.getString(1);
                idNName[1] = rs.getString(2);
                idNName[2] = rs.getString(3);
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
        return idNName;
    }
    
    public DataTable getAllCompanyNames() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT CONCAT(c.id, ' - ',  c.name), c.status, c.id, a.todays_date, c.name, c.type "
                + "FROM company c, company_todays_account a "
                + "where a.company_id = c.id "
                + "AND REF_MARK = 'L' ";

        try {
            stmt = roleMasterConn.prepareStatement(sql);               
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(format.format(rs.getDate(4).toLocalDate()));
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
    
    public boolean updateAllToRestStatus() throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "update company set status = ?::COMPANY_STATUS";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);             
            stmt.setString(1, CommonConstants.REST);
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }

    public boolean updateActiveCompany(String sId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "update company set status = ?::COMPANY_STATUS where id = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);     
            stmt.setString(1, CommonConstants.ACTIVE);
            stmt.setString(2, sId);
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public String getStartingAccountSettingsValues(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        
        
        String sql = "SELECT TODAYS_DATE " +
                    "FROM COMPANY_TODAYS_ACCOUNT "
                    + "WHERE COMPANY_ID = ? "
                    + "ORDER BY TODAYS_DATE " 
                    + "LIMIT 1";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                return format.format(rs.getDate(1).toLocalDate());
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

    public String getTodaysAccountSettingsValues(String sId) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        
        
        String sql = "SELECT TODAYS_DATE " +
                    "FROM COMPANY_TODAYS_ACCOUNT "
                    + "WHERE COMPANY_ID = ? "
                    + "AND REF_MARK = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sId);
            stmt.setString(2, "L");
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                return format.format(rs.getDate(1).toLocalDate());
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
    
    public String[] getUserPass(String sUName) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String[] pass = null;
            
        String sql ="SELECT user_password, salt_value " +
                    "FROM user_master u, employee e, role_master r " +
                    "WHERE u.role_id = r.id " +
                    "AND u.emp_id = e.id " +
                    "AND u.status = ?::REPLEDGE_STATUS " +
                    "AND r.status = ?::REPLEDGE_STATUS " +
                    "AND e.status = ?::REPLEDGE_STATUS " +
                    "AND u.user_name = ? " ;

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, "ACTIVE");
            stmt.setString(2, "ACTIVE");
            stmt.setString(3, "ACTIVE");
            stmt.setString(4, sUName);
            rs = stmt.executeQuery();

            if(rs.next())
            {		     
                pass = new String[2];
                pass[0] =  rs.getString(1);
                pass[1] =  rs.getString(2);
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

        return pass;
    }
    
    public String[] getUserEmpRoleId(String sUName) throws SQLException
    {

        String[] id = new String[3];
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT u.id, e.id, r.id " +
                    "FROM user_master u, employee e, role_master r " +
                    "WHERE u.role_id = r.id " +
                    "AND u.emp_id = e.id " +
                    "AND u.status = ?::REPLEDGE_STATUS " +
                    "AND r.status = ?::REPLEDGE_STATUS " +
                    "AND e.status = ?::REPLEDGE_STATUS " +
                    "AND u.user_name = ? ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, "ACTIVE");
            stmt.setString(2, "ACTIVE");
            stmt.setString(3, "ACTIVE");
            stmt.setString(4, sUName);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                id[0] = rs.getString(1);
                id[1] = rs.getString(2);
                id[2] = rs.getString(3);                                
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
        return id;
    }

    public String[] getEmpImg(String sEmpId) throws SQLException
    {

        String[] id = new String[3];
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT employee_image " +
                    "FROM employee " +
                    "WHERE id = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, sEmpId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                byte customerBuf[] = rs.getBytes(1);
                if(customerBuf != null) {   
                    File file = new File(new File(CommonConstants.TEMP_FILE_LOCATION), CommonConstants.LOGIN_USER_IMAGE_NAME);
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(customerBuf);                
                    fos.close();
                    CommonConstants.IS_LOGIN_USER_IMAGE_AVAILABLE = true;
                } else {
                    CommonConstants.IS_LOGIN_USER_IMAGE_AVAILABLE = false;
                }
            }		    

        } catch (SQLException e) {
                throw e;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CommonDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CommonDBOperation.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();            
            roleMasterConn.close();
        }        
        return id;
    }
    
    public String getEmpName(String sEmpId) throws SQLException
    {

        String id = null;
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT NAME "
                + "FROM EMPLOYEE "
                + "WHERE ID = ?";

        try {

            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, sEmpId);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                id = rs.getString(1);
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
        return id;
    }
    
    public boolean[] getAddViewUpdate(String sRoleId, String sTabName, String sScreenName) throws SQLException
    {

        boolean[] addViewUpdate = new boolean[3];
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT allow_add, allow_view, allow_update " +
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
                addViewUpdate[0] = rs.getBoolean(1);
                addViewUpdate[1] = rs.getBoolean(2);
                addViewUpdate[2] = rs.getBoolean(3);
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

    public boolean[] getBackup(String sRoleId, String sTabName, String sScreenName) throws SQLException
    {

        boolean[] addViewUpdate = new boolean[3];
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql ="SELECT allow_add, allow_view, allow_update " +
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
                addViewUpdate[0] = rs.getBoolean(1);
                addViewUpdate[1] = rs.getBoolean(2);
                addViewUpdate[2] = rs.getBoolean(3);
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
"             end )as mon,  " +
"             jwl_type, " +
"             pawn_total_bill,   " +
"             pawn_amount, " +
"             redeem_total_bills, " +
"             redeem_amt, " +
"             interest, " +
"             (case when  jwl_type = 'GOLD' then  total_repledge_bills " +
"             else  " +
"             '0' " +
"             end) as repledge_bill,  " +
"             (case when  jwl_type = 'GOLD' then  total_repledge_amount " +
"             else  " +
"             '0' " +
"             end) as repledge_amount,  " +
"             total_repledge_redeem_total_bills repledge_redeem_bill, " +
"             total_repledge_redeem_amt repledge_redeem_amt, " +
"             (case when  jwl_type = 'GOLD' then  total_repledge_interest " +
"             else  " +
"             '0' " +
"             end) as repledge_interest, " +
"             (case when  jwl_type = 'GOLD' then  total_repledge_stock_bills " +
"             else  " +
"             '0' " +
"             end) as total_repledge_stock_bills, " +
"             (case when  jwl_type = 'GOLD' then  total_repledge_stock_amount " +
"             else  " +
"             '0' " +
"             end) as total_repledge_stock_amount, " +
"             total_stock_bills, " +
"             total_stock_amount " +
"from  " +
"(select mon,yyyy,jwl_type, " +
"          sum(pawn_total_bill)pawn_total_bill, " +
"          sum(pawn_amount)pawn_amount, " +
"          sum(redeem_total_bills)redeem_total_bills, " +
"          sum(redeem_amt)redeem_amt, " +
"          sum(interest)interest, " +
"          sum(repledge_total_bill)total_repledge_bills, " +
"          sum(repledge_amount)total_repledge_amount, " +
"          sum(repledge_redeem_total_bills)total_repledge_redeem_total_bills, " +
"          sum(repledge_redeem_amt)total_repledge_redeem_amt, " +
"          sum(repledge_interest)total_repledge_interest, " +
"          sum((sum(repledge_total_bill) - sum(repledge_redeem_total_bills))) over(order by yyyy asc,mon,jwl_type) total_repledge_stock_bills,  " +
"          sum((sum(repledge_amount) - sum(repledge_redeem_amt))) over(order by yyyy asc,mon,jwl_type) total_repledge_stock_amount,   " +
"          sum((sum(pawn_total_bill) - sum(redeem_total_bills))) over(order by yyyy asc,mon,jwl_type) total_stock_bills, " +
"          sum((sum(pawn_amount) - sum(redeem_amt))) over(order by yyyy asc,mon,jwl_type) total_stock_amount " +
"from " +
"(select to_char(opening_date,'MM')as mon,   " +
"           extract(year from opening_date)as yyyy,  " +
"          'GOLD' jwl_type, " +
"       count(bill_number)pawn_total_bill, " +
"       sum(amount)pawn_amount, " +
"       0 redeem_total_bills,	 " +
"       0 redeem_amt, " +
"      sum(open_taken_amount)  interest, " +
"      0 repledge_total_bill, " +
"      0 repledge_amount, " +
"      0 repledge_redeem_total_bills, " +
"      0 repledge_redeem_amt, " +
"      0 repledge_interest " +
"from company_billing   " +
"where company_id = ? " +
"and jewel_material_type = 'GOLD' " +
"and status not in ('CANCELED') " +
"group by 1,2,3 " +
"union all " +
"select to_char(closing_date,'MM')as mon,   " +
"           extract(year from closing_date)as yyyy, " +
"           'GOLD' jwl_type, " +
"        0 pawn_total_bill, " +
"        0 pawn_amount, " +
"         count(bill_number) redeem_total_bills, " +
"          sum(amount) redeem_amt,          " +
"         sum(close_taken_amount) interest, " +
"          0 repledge_total_bill, " +
"      0 repledge_amount, " +
"      0 repledge_redeem_total_bills, " +
"      0 repledge_redeem_amt, " +
"      0 repledge_interest " +
"from company_billing   " +
"where status in ('CLOSED','DELIVERED','REBILLED','REBILLED-REMOVED','REBILLED-ADDED','REBILLED-MULTIPLE') " +
"and company_id = ?  " +
"and jewel_material_type = 'GOLD' " +
"group by 1,2,3 " +
"union all " +
"select to_char(opening_date,'MM')as mon,   " +
"           extract(year from opening_date)as yyyy,  " +
"          'SILVER' jwl_type, " +
"       count(bill_number)pawn_total_bill, " +
"       sum(amount)pawn_amount, " +
"       0 redeem_total_bills,	 " +
"       0 redeem_amt, " +
"      sum(open_taken_amount)  interest, " +
"       0 repledge_total_bill, " +
"      0 repledge_amount, " +
"      0 repledge_redeem_total_bills, " +
"      0 repledge_redeem_amt, " +
"      0 repledge_interest " +
"from company_billing   " +
"where company_id = ? " +
"and jewel_material_type = 'SILVER' " +
"and status not in ('CANCELED') " +
"group by 1,2,3 " +
"union all " +
"select to_char(closing_date,'MM')as mon,   " +
"           extract(year from closing_date)as yyyy, " +
"           'SILVER' jwl_type, " +
"        0 pawn_total_bill, " +
"        0 pawn_amount, " +
"         count(bill_number) redeem_total_bills, " +
"          sum(amount) redeem_amt,          " +
"         sum(close_taken_amount) interest, " +
"          0 repledge_total_bill, " +
"      0 repledge_amount, " +
"      0 repledge_redeem_total_bills, " +
"      0 repledge_redeem_amt, " +
"      0 repledge_interest " +
"from company_billing   " +
"where status in ('CLOSED','DELIVERED','REBILLED','REBILLED-REMOVED','REBILLED-ADDED','REBILLED-MULTIPLE') " +
"and company_id = ?  " +
"and jewel_material_type = 'SILVER' " +
"group by 1,2,3  " +
"union all " +
"select to_char(opening_date,'MM')as mon,   " +
"           extract(year from opening_date)as yyyy,  " +
"          'GOLD' jwl_type, " +
"          0 pawn_total_bill, " +
"          0 pawn_amount, " +
"          0 redeem_total_bills, " +
"          0 redeem_amt, " +
"          0 interest, " +
"       count(repledge_bill_number)repledge_total_bill, " +
"       sum(amount)repledge_amount, " +
"       0 repledge_redeem_total_bills,	 " +
"       0 repledge_redeem_amt, " +
"      sum(open_taken_amount)  repledge_interest " +
"from repledge_billing   " +
"where company_id = ? " +
"and jewel_material_type = 'GOLD' " +
"group by 1,2,3 " +
"union all " +
"select to_char(closing_date,'MM')as mon,   " +
"           extract(year from closing_date)as yyyy, " +
"           'GOLD' jwl_type, " +
"                     0 pawn_total_bill, " +
"          0 pawn_amount, " +
"          0 redeem_total_bills, " +
"          0 redeem_amt, " +
"          0 interest, " +
"        0 repledge_total_bill, " +
"        0 repledge_amount, " +
"         count(repledge_bill_number) repledge_redeem_total_bills, " +
"          sum(amount) repledge_redeem_amt,          " +
"         sum(close_taken_amount) repledge_interest " +
"from repledge_billing   " +
"where closing_date is not null " +
"and company_id = ?  " +
"and jewel_material_type = 'GOLD' " +
"group by 1,2,3 " +
")chi " +
"group by chi.mon,chi.yyyy,chi.jwl_type)child ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(3, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(4, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(5, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(6, CommonConstants.ACTIVE_COMPANY_ID);
            rs = stmt.executeQuery();
            
            List sMnthList = new ArrayList();
            int loopStartFrom = 0;
            while(rs.next())
            {	
                final DataRow row = new DataRow();
                String sMnth = rs.getString(1);
                if(!sMnthList.contains(sMnth)) {
                    sMnthList.add(sMnth);
                }
                row.addColumn(sMnth);
                row.addColumn(rs.getString(2));
                row.addColumn(Integer.toString(rs.getInt(3)));
                row.addColumn(Double.toString(rs.getDouble(4)));
                dataTable.add(row);                	       
            }	
            final DataRow row = new DataRow();
            row.addColumn(sMnthList);
            dataTable.add(row);
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
    
    public DataTable getCompanyBillReminderVals(String sAcceptedDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
        
        
        String sql = "SELECT BILL_NUMBER, OPENING_DATE, CUSTOMER_NAME, "
                + "(AMOUNT/GROSS_WEIGHT) RATE_PER_GM, AMOUNT, accepted_closing_date, MOBILE_NUMBER, JEWEL_MATERIAL_TYPE "
                + "FROM COMPANY_BILLING "
                + "WHERE COMPANY_ID = ? "
                + "AND STATUS IN ('OPENED', 'LOCKED') "
                + "AND accepted_closing_date <= ? "
                + "AND remind_status = ? "
                + "ORDER BY JEWEL_MATERIAL_TYPE, RATE_PER_GM DESC";

        try {
            java.sql.Date sqlAccDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sAcceptedDate, CommonConstants.DATETIMEFORMATTER));
            
            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setDate(2, sqlAccDateOpenDate);
            stmt.setString(3, CommonConstants.ACTIVE);
            
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(format.format(rs.getDate(2).toLocalDate()));
                row.addColumn(rs.getString(3));
                row.addColumn(Math.round(rs.getDouble(4)));
                row.addColumn(rs.getString(5));
                row.addColumn(format.format(rs.getDate(6).toLocalDate()));
                row.addColumn(rs.getString(7));
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
    
    public DataTable getRepAloneAllDetailsValues(String sMaterialType, String sAcceptedDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;             
        DataTable dataTable = new DataTable();
                
        String sql = "SELECT RB.REPLEDGE_BILL_ID, RB.REPLEDGE_ID, RB.REPLEDGE_NAME, RB.COMPANY_BILL_NUMBER, " +
                    "RB.OPENING_DATE, RB.AMOUNT, RB.INTEREST, RB.DOCUMENT_CHARGE, "
                + "RB.repledge_bill_number, RB.accepted_closing_date, " +
                    " REGEXP_REPLACE(COALESCE(RB.REPLEDGE_BILL_ID, '0'), '[^0-9]*' ,'0')::integer BILL " +
                    "FROM COMPANY_BILLING CB, REPLEDGE_BILLING RB " +
                    "WHERE RB.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE " +
                    "AND RB.COMPANY_ID = CB.COMPANY_ID " +
                    "AND RB.REPLEDGE_BILL_ID = CB.REPLEDGE_BILL_ID " +
                    "AND RB.COMPANY_BILL_NUMBER = CB.BILL_NUMBER " +
                    "AND CB.repledge_bill_id IS NOT NULL " +
                    "AND RB.COMPANY_ID = ? " +
                    "AND RB.STATUS IN ('OPENED', 'GIVEN') " +
                    "AND RB.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                    "AND RB.accepted_closing_date <= ?" +
                    "AND RB.remind_status = ? " +
                    "ORDER BY RB.OPENING_DATE DESC ";

        try {
            java.sql.Date sqlAccDateOpenDate = java.sql.Date.valueOf(LocalDate.parse(sAcceptedDate, CommonConstants.DATETIMEFORMATTER));
            stmt = roleMasterConn.prepareStatement(sql);  
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);    
            stmt.setDate(3, sqlAccDateOpenDate);
            stmt.setString(4, CommonConstants.ACTIVE);   
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final DataRow row = new DataRow();
                row.addColumn(rs.getString(1));
                row.addColumn(rs.getString(2));
                row.addColumn(rs.getString(3));
                row.addColumn(rs.getString(4));
                row.addColumn(format.format(rs.getDate(5).toLocalDate()));
                row.addColumn(Double.toString(rs.getDouble(6)));
                row.addColumn(Double.toString(rs.getDouble(7)));
                row.addColumn(Double.toString(rs.getDouble(8)));                
                row.addColumn(rs.getString(9));
                if(rs.getDate(10) != null) {
                    row.addColumn(format.format(rs.getDate(10).toLocalDate()));
                }
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
    
    public boolean updateRemindStatus(String billNumber, String sMaterialType) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "update company_billing "
                + "set remind_status = ? "
                + "where bill_number = ? "
                + "and JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);             
            stmt.setString(1, CommonConstants.REST);
            stmt.setString(2, billNumber);
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
    
    public boolean updateRepRemindStatus(String repBillId) throws Exception
    {	

        connectDB();
        PreparedStatement stmt = null;
        
        String sql = "update repledge_billing "
                + "set remind_status = ? "
                + "where repledge_bill_id = ?";
        
        try {

            stmt = roleMasterConn.prepareStatement(sql);             
            stmt.setString(1, CommonConstants.REST);
            stmt.setString(2, repBillId);
            return stmt.executeUpdate() >= 1;
        } catch (Exception e) {
            throw e;         
        }finally {
            if(stmt != null)
                stmt.close();
            roleMasterConn.close();
        } 
    }
    
    public String getOtherSettingsValues(String sCompId, String sMaterial) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;                     
        
        String sql = "SELECT camera_temp_file_name "
                + "FROM COMPANY_OTHER_SETTINGS "
                + "WHERE COMPANY_ID = ? "
                + "AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE ";

        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            stmt.setString(1, sCompId);
            stmt.setString(2, sMaterial);
            rs = stmt.executeQuery();

            if(rs.next())
            {	
                return rs.getString(1);
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
    
    public ArrayList<FingerPrintBean> getUserFingerPrint() throws SQLException, IOException, ClassNotFoundException
    {
        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;                     
        ArrayList<FingerPrintBean> list = new ArrayList<>();
        
        String sql = "SELECT id, user_finger_img, user_name, user_password "
                + "FROM user_master ";
        try {

            stmt = roleMasterConn.prepareStatement(sql);               
            rs = stmt.executeQuery();

            while(rs.next()) {	
                list.add(new FingerPrintBean(rs.getString(1), rs.getBytes(2), rs.getString(3), rs.getString(4)));
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

        return list;	
    }
    
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        if(data != null) {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            return is.readObject();
        }
        return null;
    }     
    
    public List<ShopLockerPrintBean> shopLockerStock(String sMaterialType, 
            String sFromDate, String sToDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;                     
        ArrayList list = new ArrayList();
        
        String sql = "SELECT  customer_id, customer_name, "
                + "gender, spouse_type, spouse_name, door_number, street, area, city, mobile_number, " +
"       opening_date, bill_number, amount, regexp_replace(bill_number, '\\D','','g') BILL " +
"  FROM company_billing " +
"  where COMPANY_ID = ? " +
"AND status in ('OPENED', 'LOCKED') " +
"AND (repledge_bill_id = '' OR repledge_bill_id IS NULL) " +
"AND jewel_material_type = ?::material_type " +
"AND opening_date BETWEEN ? AND ? " +
"ORDER BY opening_date, BILL";

        try {
            java.sql.Date sqlFromDate = java.sql.Date.valueOf(LocalDate.parse(sFromDate, CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlToDate = java.sql.Date.valueOf(LocalDate.parse(sToDate, CommonConstants.DATETIMEFORMATTER));

            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlFromDate);
            stmt.setDate(4, sqlToDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final ShopLockerPrintBean bean = new ShopLockerPrintBean();
                bean.setCustomerId(rs.getString(1));
                bean.setCustomerName(rs.getString(2));
                bean.setGender(rs.getString(3));
                bean.setSpouseType(rs.getString(4));
                bean.setSpouseName(rs.getString(5));
                bean.setDoorNo(rs.getString(6));
                bean.setStreet(rs.getString(7));
                bean.setArea(rs.getString(8));
                bean.setCity(rs.getString(9));
                bean.setMobileNumber(rs.getString(10));
                bean.setOpenedDate(format.format(rs.getDate(11).toLocalDate()));
                bean.setBillNumber(rs.getString(12));
                bean.setAmount(rs.getString(13));
                list.add(bean);
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

        return list;	
    }

    public List<ShopLockerPrintBean> shopBillStock(
            String sMaterialType, 
            String sFromDate, String sToDate) throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;                     
        ArrayList list = new ArrayList();
        
        String sql = " SELECT  customer_id, customer_name, " +
"gender, spouse_type, spouse_name, door_number, street, area, " +
"city, mobile_number, " +
" CB.opening_date, bill_number, CB.amount, " +
"case " +
"        when rb.repledge_name is not null " +
"		then concat(rb.repledge_name,' Rs:', rb.amount) " +
"		else ' ' " +
"		end as rep_det, " +
" regexp_replace(bill_number, '\\D','','g') BILL  " +
" FROM company_billing cb left join repledge_billing rb " +
" on cb.repledge_bill_id = rb.repledge_bill_id " +
" AND rb.status in ('OPENED', 'GIVEN') " +
" where CB.COMPANY_ID = ? " +
" AND CB.status in ('OPENED', 'LOCKED')  " +
" AND CB.jewel_material_type = ?::material_type  " +
" AND CB.opening_date BETWEEN ? AND ?  " +
" ORDER BY opening_date, BILL";

        try {
            java.sql.Date sqlFromDate = java.sql.Date.valueOf(LocalDate.parse(sFromDate, CommonConstants.DATETIMEFORMATTER));
            java.sql.Date sqlToDate = java.sql.Date.valueOf(LocalDate.parse(sToDate, CommonConstants.DATETIMEFORMATTER));

            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            stmt.setString(2, sMaterialType);
            stmt.setDate(3, sqlFromDate);
            stmt.setDate(4, sqlToDate);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final ShopLockerPrintBean bean = new ShopLockerPrintBean();
                bean.setCustomerId(rs.getString(1));
                bean.setCustomerName(rs.getString(2));
                bean.setGender(rs.getString(3));
                bean.setSpouseType(rs.getString(4));
                bean.setSpouseName(rs.getString(5));
                bean.setDoorNo(rs.getString(6));
                bean.setStreet(rs.getString(7));
                bean.setArea(rs.getString(8));
                bean.setCity(rs.getString(9));
                bean.setMobileNumber(rs.getString(10));
                bean.setOpenedDate(format.format(rs.getDate(11).toLocalDate()));
                bean.setBillNumber(rs.getString(12));
                bean.setAmount(rs.getString(13));
                bean.setRepDet(rs.getString(14));
                list.add(bean);
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

        return list;	
    }

    public List<RepledgeBillPrintBean> repledgeBillStock() throws SQLException
    {

        connectDB();
        PreparedStatement stmt = null;
        ResultSet rs = null;                     
        ArrayList list = new ArrayList();
        
        String sql = "SELECT repledge_name, repledge_bill_number,  " +
"       opening_date, company_bill_number, amount, status, " +
"       suspense_date, regexp_replace(repledge_bill_number, '\\D','','g') BILL, regexp_replace(company_bill_number, '\\D','','g') COMP_BILL " +
"  FROM repledge_billing " +
"  where COMPANY_ID = ? " +
"and status in ('OPENED', 'GIVEN', 'SUSPENSE') " +
"  ORDER BY repledge_name, opening_date, BILL, COMP_BILL";

        try {

            stmt = roleMasterConn.prepareStatement(sql);
            stmt.setString(1, CommonConstants.ACTIVE_COMPANY_ID);
            rs = stmt.executeQuery();

            while(rs.next())
            {	
                final RepledgeBillPrintBean bean = new RepledgeBillPrintBean();
                bean.setRepledgeName(rs.getString(1));
                bean.setRepledgeBillNumber(rs.getString(2));
                bean.setOpenedDate(format.format(rs.getDate(3).toLocalDate()));
                bean.setCompanyBillNumber(rs.getString(4));
                bean.setAmount(rs.getString(5));
                bean.setStatus(rs.getString(6));
                list.add(bean);
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

        return list;	
    }
    
}
