package lv.emes.libraries.tools;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/** 
 * Class consists of static methods only and is designed to operate with time and date.
 * It includes typical modifications and actions with time formats.
 * @version 1.1.
 * @author eMeS
 */
public final class MS_TimeTools {
	//TODO improve this class!
	//PUBLIC STRUCTURES, EXCEPTIONS, PROPERTIES AND CONSTANTS
	//exceptions:

	public static class IncorrectStartingYearException extends Exception {
		public IncorrectStartingYearException(int aYear, String aDate) {
			super("Year ("+aYear+") must be less or equal to the year of given date ("+aDate+").");
		}
		private static final long serialVersionUID = 4402055459405514853L;		
	}
	public static class IncorrectDateFormatException extends Exception {
		public IncorrectDateFormatException() {
			super("Cannot convert string to date. Incorrect date format.");
		}
		private static final long serialVersionUID = 4402055459405514854L;		
	}
	//records:
	public static class MSRec_Time { 		
		public int hour, min, sec, millisec;	
		public static MSRec_Time newInstance(Date aDate) {
			return extractTime(aDate);
		}
	}
	public static class MSRec_Date { 
		public int year, month, day;	
		public static MSRec_Date newInstance(Date aDate) {
			return extractDate(aDate);
		}		
	}
	public static class MSRec_DateTime { 
		public int year, month, day, hour, min, sec, millisec;	
		public static MSRec_DateTime newInstance(Date aDate) {
			return extractDateTime(aDate);
		}		
	}

	//Methods:
	/**
	 * Converts date as text to date.
	 * @param aText a date presented as text in format 'dd.MM.yyyy'.
	 *                 <p><u>Example</u>: aText = "03.07.2016"
	 * @return <u>Example</u>: Sun Jul 03 00:00:00 EEST 2016
	 * @throws IncorrectDateFormatException when text is in incorrect format.
	 */
	public static Date strToDate(String aText) throws IncorrectDateFormatException {
		Date res = null;
		SimpleDateFormat format = new SimpleDateFormat ("dd.MM.yyyy"); 
	      try { 
	          res = format.parse(aText); 
	      } catch (ParseException e) { 
	    	  throw new IncorrectDateFormatException();
	      }
		return res;				
	}

	/**
	 * Converts time as text to date.
	 * @param aText a time presented as text in format 'H:mm:ss:SSS'.
	 *                 <p><u>Example</u>: aText = "23:02:11:201"
	 * @return <u>Example</u>: Thu Jan 01 23:02:11 EET 1970
	 * @throws IncorrectDateFormatException when text is in incorrect format.
	 */
	public static Date strToTime(String aText) throws IncorrectDateFormatException {
		Date res = null;
		SimpleDateFormat format = new SimpleDateFormat ("HH:mm:ss:SSS"); 
	      try { 
	          res = format.parse(aText); 
	      } catch (ParseException e) { 
	    	  throw new IncorrectDateFormatException();
	      }
		return res;				
	}

	/**
	 * Converts date and time as text to date.
	 * @param aText a date and time presented as text in format 'dd.mm.yyyy HH:mm:ss:SSS'.
	 *                 <p><u>Example</u>: aText = "03.07.2016 23:02:11:000"
	 * @return <u>Example</u>: Sun Jul 03 23:02:11 EEST 2016
	 * @throws IncorrectDateFormatException when text is in incorrect format.
	 */
	public static Date strToDateTime(String aText) throws IncorrectDateFormatException {
		Date res;
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS"); 
	      try { 
	          res = format.parse(aText); 
	      } catch (ParseException e) { 
	    	  throw new IncorrectDateFormatException();
	      }
		return res;				
	}

	/**
	 * Converts date and time to text.
	 * @param aDate a date in format: "dd.MM.yyyy HH:mm:ss:SSS".
	 * @return a text representing passed date and time.
	 */
	public static String dateTimeToStr(Date aDate) {
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS"); 
		return format.format(aDate);				
	}	
	
	/**
	 * Pārkonvertē laiku par tekstu formātā "HH:mm:ss:SSS".
	 */
	/**
	 * Converts time to text.
	 * @param aDate a date in format: "HH:mm:ss:SSS".
	 * @return a text representing passed time.
	 */
	public static String timeToStr(Date aDate) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS"); 
		return format.format(aDate);				
	}	

	/**
	 * Converts date without time to text.
	 * @param aDate a date in format: "dd.MM.yyyy".
	 * @return a text representing passed date.
	 */
	public static String dateToStr(Date aDate) {
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy"); 
		return format.format(aDate);				
	}	
	
	//retrieves parts of record
	/**
	 * Extracts time part from date and stores everything in record object.
	 * @param aDate presented date.
	 * @return a eMeS time record (hour, minute, second, millisecond).
	 */
	public static MSRec_Time extractTime(Date aDate) {
		MSRec_Time res = new MSRec_Time();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(aDate);
		res.hour = cal.get(Calendar.HOUR_OF_DAY);
		res.millisec = cal.get(Calendar.MILLISECOND);
		res.min = cal.get(Calendar.MINUTE);
		res.sec = cal.get(Calendar.SECOND);
		return res;		
	}

	/**
	 * Extracts date part from date and stores everything in record object.
	 * @param aDate presented date.
	 * @return a eMeS time record (year, month, day).
	 */
	public static MSRec_Date extractDate(Date aDate) {
		MSRec_Date res = new MSRec_Date();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(aDate);
		res.day = cal.get(Calendar.DAY_OF_MONTH);
		res.month = cal.get(Calendar.MONTH) + 1;
		res.year = cal.get(Calendar.YEAR);
		return res;			
	}

	/**
	 * Extracts date altogether with time part from date and stores everything in record object.
	 * @param aDate presented date.
	 * @return a eMeS time record (year, month, day, hour, minute, second, millisecond).
	 */
	public static MSRec_DateTime extractDateTime(Date aDate) {
		MSRec_DateTime res = new MSRec_DateTime();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(aDate);
		res.day = cal.get(Calendar.DAY_OF_MONTH);
		res.month = cal.get(Calendar.MONTH) + 1;
		res.year = cal.get(Calendar.YEAR);
		res.hour = cal.get(Calendar.HOUR_OF_DAY);
		res.millisec = cal.get(Calendar.MILLISECOND);
		res.min = cal.get(Calendar.MINUTE);
		res.sec = cal.get(Calendar.SECOND);
		return res;		
	}
	
	//From records parts retrieves date.
	/**
	 * From eMeS record object extracts date without time part.
	 * @param aRecord an eMeS record object.
	 * @return a date.
	 */
	public static Date extractDate(MSRec_Date aRecord) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.clear(Calendar.HOUR_OF_DAY);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		cal.set(Calendar.DAY_OF_MONTH, aRecord.day);
		cal.set(Calendar.MONTH, aRecord.month - 1);
		cal.set(Calendar.YEAR, aRecord.year);	
		return cal.getTime();
	}

	/**
	 * From eMeS record object extracts date with time part only.
	 * @param aRecord an eMeS record object.
	 * @return a date.
	 */
	public static Date extractTime(MSRec_Time aRecord) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.clear(Calendar.DAY_OF_MONTH);
		cal.clear(Calendar.MONTH);
		cal.clear(Calendar.YEAR);
		cal.set(Calendar.HOUR_OF_DAY, aRecord.hour);
		cal.set(Calendar.MINUTE, aRecord.min);
		cal.set(Calendar.SECOND, aRecord.sec);
		cal.set(Calendar.MILLISECOND, aRecord.millisec);	
		return cal.getTime();
	}

	/**
	 * From eMeS record object extracts date with date and time part.
	 * @param aRecord an eMeS record object.
	 * @return a date.
	 */
	public static Date extractDateTime(MSRec_DateTime aRecord) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.DAY_OF_MONTH, aRecord.day);
		cal.set(Calendar.MONTH, aRecord.month - 1);
		cal.set(Calendar.YEAR, aRecord.year);	
		cal.set(Calendar.HOUR_OF_DAY, aRecord.hour);
		cal.set(Calendar.MINUTE, aRecord.min);
		cal.set(Calendar.SECOND, aRecord.sec);
		cal.set(Calendar.MILLISECOND, aRecord.millisec);
		return cal.getTime();
	}

	/**
	 * Gets just year from date.
	 * @param aDate any valid date.
	 * @return year of <b>aDate</b>.
	 */
	public static int getYearFromDate(Date aDate) {
		return extractDate(aDate).year;
	}
	/**
	 * Gets just month from date.
	 * @param aDate any valid date.
	 * @return month of <b>aDate</b>.
	 */
	public static int getMonthFromDate(Date aDate) {
		return extractDate(aDate).month;
	}
	/**
	 * Gets just day from date.
	 * @param aDate any valid date.
	 * @return day of <b>aDate</b>.
	 */
	public static int getDayFromDate(Date aDate) {
		return extractDate(aDate).day;
	}

	/**
	 * Gets number representing hours passed from date <b>aDateFrom</b> to <b>aGivenDate</b>.
	 * If <b>aDateFrom</b> &gt; <b>aGivenDate</b> returns <b>-1</b>.
	 * @param aGivenDate date till.
	 * @param aDateFrom constant point of reference.
	 * @return hour amount or -1 if <b>aDateFrom</b> &gt; <b>aGivenDate</b>.
	 */
	public static long getHoursFromGivenDate(Date aGivenDate, Date aDateFrom) {
		if (aDateFrom==null || aGivenDate==null ||aGivenDate==aDateFrom) return 0;
		GregorianCalendar calGivenDate = new GregorianCalendar();
		GregorianCalendar calDateFrom = new GregorianCalendar();
		calGivenDate.setTime(aGivenDate);
		calDateFrom.setTime(aDateFrom);
		long diff = calGivenDate.getTimeInMillis() - calDateFrom.getTimeInMillis();
		if (diff > 0) {
			System.out.println("diff = " + diff / 3600000);
			return diff / 3600000; //pārveidojam milisekundes minūtēs
		} 
		else if (diff < 0) 
			return -1;
		else 
			return 0; //ja gadījumā pēc vērtības abi datumi ir vienādi		
	}

	/**
	 * Seconds of given date starting from Epoch (January 1, 1970 00:00:00.000 GMT (Gregorian).
	 * @param aDate a date.
	 * @return amount of seconds.
	 */
	public static long getSecsFromDate(Date aDate) {
		if (aDate==null) return 0;
		GregorianCalendar calGivenDate = new GregorianCalendar();
		calGivenDate.setTime(aDate);
		return calGivenDate.getTimeInMillis() / 60000;
	}

	/**
	 * Milliseconds of given date starting from Epoch (January 1, 1970 00:00:00.000 GMT (Gregorian).
	 * @param aDate a date.
	 * @return amount of milliseconds.
	 */
	public static long getMiliSecsFromDate(Date aDate) {
		if (aDate==null) return 0;
		GregorianCalendar calGivenDate = new GregorianCalendar();
		calGivenDate.setTime(aDate);
		return calGivenDate.getTimeInMillis();
	}
}