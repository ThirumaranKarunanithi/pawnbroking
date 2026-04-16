/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.magizhchi.pawnbroking.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tiru
 */
public class DateRelatedCalculations {
    
    public static String getTodaysDate() {            
        DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        Calendar cal = Calendar.getInstance();
        return df.format(cal.getTime());
    }

    public static String getCurrentTime() {            
        DateFormat df = new SimpleDateFormat(CommonConstants.TIME_FORMAT);
        Calendar cal = Calendar.getInstance();
        return df.format(cal.getTime());
    }
    
    public static String getTodaysDate(String format) {            
        DateFormat df = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        return df.format(cal.getTime());
    }
    
    public static long getDifferenceDays(String sStartDate, String sEndDate) {

        DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        Date d1;
        Date d2;                
        try {
            d1 = df.parse(sStartDate);
            d2 = df.parse(sEndDate);
            long diff = d2.getTime() - d1.getTime();
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public static long[] getDifferenceMonths(String sStartDate, long lTotalDays) {

        long[] lMonthsAndDays = new long[2];
        long lTotalMonths = 0;
        DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        Date d1;
        try {
            d1 = df.parse(sStartDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d1);
            
            for(;;) {                
                if(cal.getActualMaximum(Calendar.DAY_OF_MONTH) <= lTotalDays) {
                    lTotalDays = lTotalDays - cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    lTotalMonths = lTotalMonths + 1;
                } else {
                    break;
                }
                cal.add(Calendar.MONTH, 1);
            }
            
            lMonthsAndDays[0] = lTotalMonths;
            lMonthsAndDays[1] = lTotalDays;
            return lMonthsAndDays;
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lMonthsAndDays;
    }
    
    public static long[] getDifferenceMonthsChettinad(String sStartDate, String sEndDate) {

        long[] lMonthsAndDays = new long[2];
        long lTotalMonths = 0;
        long lTotalDays = 0;
        
        DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        Date d1;
        Date d2;
        try {
            
            d1 = df.parse(sStartDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d1);
            
            int sDay = cal.get(Calendar.DAY_OF_MONTH);
            int sMonth = cal.get(Calendar.MONTH) + 1;
            int sYear = cal.get(Calendar.YEAR);

            d2 = df.parse(sEndDate);
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(d2);
            
            int eDay = cal1.get(Calendar.DAY_OF_MONTH);
            int eMonth = cal1.get(Calendar.MONTH) + 1;
            int eYear = cal1.get(Calendar.YEAR);
            
            int totDays = eDay - sDay;            
            if(totDays < 0 && eMonth > 0) {
                eDay = eDay + 30;                
                eMonth = eMonth - 1;
                totDays = eDay - sDay;  
            }
            
            int totMonths = eMonth - sMonth;
            if(totMonths < 0 && eYear > sYear) {
                eMonth = eMonth + 12;
                eYear = eYear - 1;
                totMonths = eMonth - sMonth;
            }
            
            int totYear = 0;
            if(eYear > sYear) {
                totYear = eYear - sYear;
            }
            
            lTotalMonths = totMonths + (totYear * 12);
            lTotalDays = totDays;
                    
            lMonthsAndDays[0] = lTotalMonths;
            lMonthsAndDays[1] = lTotalDays;
            return lMonthsAndDays;
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lMonthsAndDays;
    }
    
    public static long[] getDifferenceMonthsWithMonthReduction(String sStartDate, long lTotalDays, int lReduceMonths) {

        long[] lMonthsAndDays = new long[2];
        long lTotalMonths = 0;
        DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        Date d1;
        try {
            d1 = df.parse(sStartDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d1);
            for(int i=0; i<lReduceMonths; i++) {
                if(cal.getActualMaximum(Calendar.DAY_OF_MONTH) <= lTotalDays) {
                    lTotalDays = lTotalDays - cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                } else {
                    break;
                }
                cal.add(Calendar.MONTH, 1);
            }
            for(;;) {                
                if(cal.getActualMaximum(Calendar.DAY_OF_MONTH) <= lTotalDays) {
                    lTotalDays = lTotalDays - cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    lTotalMonths = lTotalMonths + 1;
                } else {
                    break;
                }
                cal.add(Calendar.MONTH, 1);
            }
            
            lMonthsAndDays[0] = lTotalMonths;
            lMonthsAndDays[1] = lTotalDays;
            return lMonthsAndDays;
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lMonthsAndDays;
    }

    public static boolean isMonthsGreaterThanMonthMinimum(String sStartDate, long lTotalDays, int lMinimumMonths) {

        boolean bMonthGreater = false;
        DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        Date d1;
        try {
            d1 = df.parse(sStartDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d1);
            
            for(int i=0; i<lMinimumMonths; i++) {
                if(cal.getActualMaximum(Calendar.DAY_OF_MONTH) <= lTotalDays) {
                    lTotalDays = lTotalDays - cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                } else {
                    break;
                }
                cal.add(Calendar.MONTH, 1);
            }
 
            if(lTotalDays >= 0) {
                bMonthGreater = true;
            }
            
            return bMonthGreater;
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bMonthGreater;
    }

    public static boolean isMonthsGreaterThanDayMinimum(String sStartDate, long lTotalDays, int lMinimumDays) {

        return lTotalDays > lMinimumDays;
    }
    
    public static long[] getDifferenceMonthsWithDayReduction(String sStartDate, long lTotalDays, int lReduceDays) {

        long[] lMonthsAndDays = new long[2];
        long lTotalMonths = 0;
        DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        Date d1;
        try {
            d1 = df.parse(sStartDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d1);
            
            if(lReduceDays <= lTotalDays) {
                lTotalDays = lTotalDays - lReduceDays;                
            }
            
            for(;;) {                
                if(cal.getActualMaximum(Calendar.DAY_OF_MONTH) <= lTotalDays) {
                    lTotalDays = lTotalDays - cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    lTotalMonths = lTotalMonths + 1;
                } else {
                    break;
                }
                cal.add(Calendar.MONTH, 1);
            }
            
            lMonthsAndDays[0] = lTotalMonths;
            if(lTotalMonths >= 1) {
                lMonthsAndDays[1] = lTotalDays > 0 ? lTotalDays - 1 : 0;
            } else {
                lMonthsAndDays[1] = lTotalDays;
            }
            return lMonthsAndDays;
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lMonthsAndDays;
    }
    
    public static long getDifferenceDaysWithMonthReduction(String sStartDate, long lTotalDays, int lReduceMonths) {

        DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        Date d1;
        try {
            d1 = df.parse(sStartDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d1);
            for(int i=0; i<lReduceMonths; i++) {
                if(cal.getActualMaximum(Calendar.DAY_OF_MONTH) <= lTotalDays) {
                    lTotalDays = lTotalDays - cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                } else {
                    return 0;
                }
                cal.add(Calendar.MONTH, 1);
            }
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lTotalDays;        
    }
    
    public static long getDifferenceDaysWithDayReduction(String sStartDate, long lTotalDays, int lReduceDays) {

            if(lReduceDays <= lTotalDays) {
                    lTotalDays = lTotalDays - lReduceDays;
            } else {
                return 0;
            }
            
            return lTotalDays;
    }

    public static long getDaysInMonths(String sStartDate, int lMinimumMonths) {

        long lTotalDays = 0;
        DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        Date d1;
        try {
            d1 = df.parse(sStartDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d1);
            
            for(int i=0; i<lMinimumMonths; i++) {
                lTotalDays = lTotalDays + cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                cal.add(Calendar.MONTH, 1);
            }
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lTotalDays;        
    }
    
    public static String getPreviousDateWithFormatted(String sCurrentDay) {
    
        try {
            DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
            Date dt = df.parse(sCurrentDay);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            cal.add(Calendar.DATE, -1);
            return df.format(cal.getTime());
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public static String getNextDateWithFormatted(String sCurrentDay) {
    
        try {
            DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
            Date dt = df.parse(sCurrentDay);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            cal.add(Calendar.DATE, 1);
            return df.format(cal.getTime());
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public static boolean isFirstDateIsEqualToSecondDate(String fDate, String sDate) {
    
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
            Date date1 = sdf.parse(fDate);
            Date date2 = sdf.parse(sDate);
            if(date1.equals(date2)){
                return true;
            } 
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    public static boolean isFirstDateIsLesserOrEqualToSecondDate(String fDate, String sDate) {
    
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
            Date date1 = sdf.parse(fDate);
            Date date2 = sdf.parse(sDate);
            if(date1.before(date2) || date1.equals(date2)){
                return true;
            } 
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }

    public static boolean isFirstDateIsLesserToSecondDate(String fDate, String sDate) {
    
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
            Date date1 = sdf.parse(fDate);
            Date date2 = sdf.parse(sDate);
            if(date1.before(date2)){
                return true;
            } 
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
 
    public static long[] getDifferenceMonthsWithTotalMonthReduction(long[] lActualTotalMonths, String[] sReduceDatas) {

        long[] lMonthsAndDays = new long[2];
        long lTotalMonths = lActualTotalMonths[0];
        long lTotalDays = lActualTotalMonths[1];
        
        if(lTotalMonths > 0) {
            lTotalMonths = lTotalMonths - Integer.parseInt(sReduceDatas[0].isEmpty() ? "0" : sReduceDatas[0]);
        }
        
        lMonthsAndDays[0] = lTotalMonths;
        lMonthsAndDays[1] = lTotalDays;        
        return lMonthsAndDays;
    }

    public static String getAfterYearOrMonDateWithFormatted(String sCurrentDay, int yearOrMon, int noOfYearOrMon) {
    
        try {
            DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
            Date dt = df.parse(sCurrentDay);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            cal.add(yearOrMon, noOfYearOrMon);
            cal.add(Calendar.DATE, -1);
            return df.format(cal.getTime());
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public static boolean isDateWithInYear(String sDate) {
    
        try {
            
            DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
            Calendar sCal = Calendar.getInstance();
            Date date1 = df.parse(sDate);
            Calendar eCal = Calendar.getInstance();
            eCal.setTime(date1);
            eCal.add(Calendar.YEAR, 1);
            Date curDate = sCal.getTime();
            Date endDate = eCal.getTime();
            if(curDate.before(endDate) || curDate.equals(endDate)){
                return true;
            } 
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }

    public static boolean isDateWithInYear(String sDate, String sCurDate) {
    
        try {
            
            DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
            Date date1 = df.parse(sDate);
            Calendar eCal = Calendar.getInstance();
            eCal.setTime(date1);
            eCal.add(Calendar.YEAR, 1);
            Date curDate = df.parse(sCurDate);
            Date endDate = eCal.getTime();
            if(curDate.before(endDate) || curDate.equals(endDate)){
                return true;
            } 
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }

    public static String getOneMonth(String sDate) {

        DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        Date d1;
        try {
            d1 = df.parse(sDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d1);
            cal.add(Calendar.MONTH, 1);
            return df.format(cal.getTime());
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String getOneYear(String sDate) {

        DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        Date d1;
        try {
            d1 = df.parse(sDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d1);
            cal.add(Calendar.YEAR, 1);
            return df.format(cal.getTime());
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String getPreYear() {

        DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        Date d1;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        return df.format(cal.getTime());
    }
    
    public static String getPreMonth() {

        DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        Date d1;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        return df.format(cal.getTime());
    }
    
    public static String getAcceptedClosingDuration(String sOpeningDate, String sAccDate) {
        
        try {
            
            DateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
            Date date1 = df.parse(sOpeningDate);
            Date accDate = df.parse(sAccDate);
            
            Calendar eCal = Calendar.getInstance();
            eCal.setTime(date1);
            eCal.add(Calendar.YEAR, 1);  
            eCal.add(Calendar.DATE, -1);  
            Date oneYDate = eCal.getTime();            
            if(oneYDate.equals(accDate)){
                return "1Y";
            }         
            eCal = Calendar.getInstance();
            eCal.setTime(date1);
            eCal.add(Calendar.MONTH, 9);            
            eCal.add(Calendar.DATE, -1);  
            Date nineMDate = eCal.getTime();            
            if(nineMDate.equals(accDate)){
                return "9M";
            }
            eCal = Calendar.getInstance();
            eCal.setTime(date1);            
            eCal.add(Calendar.MONTH, 6);            
            eCal.add(Calendar.DATE, -1);              
            Date sixMDate = eCal.getTime();            
            if(sixMDate.equals(accDate)){
                return "6M";
            }
            eCal = Calendar.getInstance();
            eCal.setTime(date1);
            eCal.add(Calendar.MONTH, 3);   
            eCal.add(Calendar.DATE, -1);  
            Date threeMDate = eCal.getTime();            
            if(threeMDate.equals(accDate)){
                return "3M";
            }
            eCal = Calendar.getInstance();
            eCal.setTime(date1);
            eCal.add(Calendar.MONTH, 1); 
            eCal.add(Calendar.DATE, -1);  
            Date oneMDate = eCal.getTime();            
            if(oneMDate.equals(accDate)){
                return "1M";
            }            
        } catch (ParseException ex) {
            Logger.getLogger(DateRelatedCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }       
        return "";
    }
        
}
