package com.gridgain.sales.utility;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
public class ParseTypes {
    public static Double parseDouble(String strNumber) {
        Double retVal = null;
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch(Exception e) {
                return Double.NEGATIVE_INFINITY;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return retVal;
    }
    public static Float parseFloat(String strNumber) {
        Float retVal = null;
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Float.parseFloat(strNumber);
            } catch(Exception e) {
	            System.out.println("parseFloat - value: " + strNumber + "; Parsing ERROR: " + e );
                return Float.NEGATIVE_INFINITY;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return retVal;
    }
    public static BigDecimal parseBigDecimal(String strNumber) {
    	BigDecimal retVal = null;
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return BigDecimal.valueOf(new Double(strNumber));
            } catch(Exception e) {
	            System.out.println("parseBigDecimal - value: " + strNumber + "; Parsing ERROR: " + e );
                return retVal;
            }
        }
        else return retVal;
    }
    public static Integer parseInteger(String strNumber) {
        Integer retVal = null;
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Integer.valueOf(strNumber);
            } catch(Exception e) {
	            System.out.println("parseInteger - value: " + strNumber + "; Parsing ERROR: " + e );
                return retVal;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return retVal;
    }
    public static Short parseShort(String strNumber) {
        Short retVal = null;
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Short.valueOf(strNumber);
            } catch(Exception e) {
	            System.out.println("parseShort - value: " + strNumber + "; Parsing ERROR: " + e );
                return retVal;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return retVal;
    }
    public static Date parseDate(String strDate) {
    	Date retVal = null;
        if (strDate != null && strDate.length() > 0) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date uDate = formatter.parse(strDate);
                return new java.sql.Date(uDate.getTime());
            } catch(Exception e) {
	            System.out.println("parseDate - value: " + strDate + "; Parsing ERROR: " + e );
                return retVal;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return retVal;
    }
    public static Timestamp parseTimestamp(String strDate) {
    	Timestamp retVal = null;
        if (strDate != null && strDate.length() > 0) {
            try {
            	Instant instant = Instant.parse(strDate);
            	retVal = java.sql.Timestamp.from(instant);
                return retVal;
            } catch(Exception e) {
	            System.out.println("parseTimestampe - value: " + strDate + "; Parsing ERROR: " + e );
                return retVal;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return retVal;
    }
    public static Date parseTimestampToDate(String strDate) {
    	Date retVal = null;
        if (strDate != null && strDate.length() > 0) {
            try {
            	DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            	TemporalAccessor ta = dtf.parse(strDate);
            	retVal = java.sql.Date.valueOf(LocalDate.from(ta));
            	return retVal;
            } catch(Exception e) {
	            System.out.println("parseTimestampToDate - value: " + strDate + "; Parsing ERROR: " + e );
                return retVal;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return retVal;
    }
    public static Time parseTime(String strTime) {
    	Time retVal = null;
        if (strTime != null && strTime.length() > 0) {
            try {
            	retVal = java.sql.Time.valueOf(strTime);
                return retVal;
            } catch(Exception e) {
	            System.out.println("parseTime - value: " + strTime + "; Parsing ERROR: " + e );
                return retVal;   
            }
        }
        else return retVal;
    }
    public static String parseString(String str) {
        return (str.length()==0?null:str);
    }
    public static byte[] parseBytes(String str) {
        return (str.length()==0?null:str.getBytes());
    }
}
