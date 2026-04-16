package com.magizhchi.pawnbroking.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Util {	
	
	public static boolean isInternetConnectionAvailable() {
		Socket soc = new Socket();
    	InetSocketAddress add = new InetSocketAddress("www.google.com", 80);
    	try
    	{
    		soc.connect(add, 3000);
    		return true;
    	} catch(Exception e) {
    		return false;
    	} finally {
    		try{
    			soc.close();
    		} catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
	}

        public static String getMACAddress() throws IOException {
		
		String macAddress = null;
		String command = "ipconfig /all";
		Process pid = Runtime.getRuntime().exec(command);
		BufferedReader in = new BufferedReader(new InputStreamReader(pid.getInputStream()));
		while (true) 
		{
			String line = in.readLine();
			if (line == null)
				break;
			Pattern p = Pattern.compile(".*Physical Address.*: (.*)");
			Matcher m = p.matcher(line);
			if (m.matches()) {
				macAddress = m.group(1);
				break;
			}
		}
		in.close();
		return macAddress;		

	}
        
        public static boolean isBetween(double numberToBe, double start, double end) {
            return (numberToBe>=start && numberToBe<=end);
        }
        
        public static StringBuilder getStyle(String borderColor, String backgroundColor) {
            
            StringBuilder str = new StringBuilder();
            str.append("-fx-background-color: linear-gradient( ");
            str.append("from 0px 0px to 0px 1px, "); 
            str.append(borderColor);
            str.append(", ");
            str.append(borderColor);
            str.append(" 99%");
            str.append(", ");
            str.append(backgroundColor);
            str.append(");"); 
            return str;
        }

        public static StringBuilder getTextFieldStyle(String textColor, String backgroundColor) {
            
            StringBuilder str = new StringBuilder();
            str.append("-fx-background-color: ");
            str.append(backgroundColor);
            str.append(";");
            str.append("-fx-text-fill: ");
            str.append(textColor);
            str.append(";");
            return str;
        }
        
        public static String format(double value) {
            if(value < 1000) {
                return format("###", value);
            } else {
                double hundreds = value % 1000;
                int other = (int) (value / 1000);
                return format(",##", other) + ',' + format("000", hundreds);
            }
        }        
        
        private static String format(String pattern, Object value) {
            return new DecimalFormat(pattern).format(value);
        }       
        
        public static String getPassWordFor(String str) {
            String day = DateRelatedCalculations.getTodaysDate("dd");
            String month = DateRelatedCalculations.getTodaysDate("MMMM");

            String usrPart = CommonConstants.USERID;
            String dayPart = ConvertNumberToWord.convertNumber(Long.parseLong(day)).toUpperCase();
            String monthPart = (month.charAt(0) + "" + month.charAt(month.length()-1)).toUpperCase();
            String pass = (str + "" + usrPart + "" + dayPart + "" + monthPart).replaceAll(" ", "");
            return pass;
        }
        
        public static String getShortPassWordFor(String str, String str2) {
            String month = DateRelatedCalculations.getTodaysDate("MMMM");
            String monthPart = (month.charAt(0) + "" + month.charAt(month.length()-1)).toUpperCase();
            String day = DateRelatedCalculations.getTodaysDate("dd");
            String dayPart = day.charAt(0)+"";
            String pass = (str + "" + monthPart + "" + dayPart + "" + str2).replaceAll(" ", "");
            return pass;
        }
        
}
