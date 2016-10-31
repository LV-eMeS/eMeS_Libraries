package lv.emes.libraries.tools;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/** 
 * Klase satur tikai statiskas metodes darbībām ar laikiem un datumiem. Tipiskie pārveidojumi un darbības ar laika formātiem.
 * @version 1.0.
 */
public final class MS_TimeTools {
	//PUBLISKĀS STRUKTŪRAS, IZŅĒMUMI UN KONSTANTES
	//izņēmumi:
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
	//ieraksti:	
	public static class MSRec_Time { 		
		public int hour, min, sec, millisec;	
		public static MSRec_Time newInstance(Date aDate) {
			return eMeSExtractTime(aDate);
		}
	}
	public static class MSRec_Date { 
		public int year, month, day;	
		public static MSRec_Date newInstance(Date aDate) {
			return eMeSExtractDate(aDate);
		}		
	}
	public static class MSRec_DateTime { 
		public int year, month, day, hour, min, sec, millisec;	
		public static MSRec_DateTime newInstance(Date aDate) {
			return eMeSExtractDateTime(aDate);
		}		
	}	
	
	//PRIVĀTĀS METODES	
	
	//PUBLISKĀS METODES
	/**
	 * Veido datumu no teksta 'dd.MM.yyyy'. 
	 * Kļūdas gadījumā met exception, ka nepareizs datuma formāts.
	 * Lasīt: http://www.tutorialspoint.com/java/java_date_time.htm
	 * @param aText = "03.07.2016"
	 * @return Sun Jul 03 00:00:00 EEST 2016
	 * @throws IncorrectDateFormatException 
	 */
	public static Date eMeSStrToDate(String aText) throws IncorrectDateFormatException {
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
	 * Veido datumu no teksta 'HH:mm:ss:SSS'. Kļūdas gadījumā met exception, ka nepareizs datuma formāts.
	 * @param aText = "23:02:11:201"
	 * @return Thu Jan 01 23:02:11 EET 1970
	 * @throws IncorrectDateFormatException
	 */
	public static Date eMeSStrToTime(String aText) throws IncorrectDateFormatException {
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
	 * Veido datumu no teksta formātā 'dd.mm.yyyy HH:mm:ss:SSS'.
	 * Kļūdas gadījumā met exception, ka nepareizs datuma formāts.
	 * @param aText = "03.07.2016 23:02:11:000"
	 * @return Sun Jul 03 23:02:11 EEST 2016
	 * @throws IncorrectDateFormatException
	 */
	public static Date eMeSStrToDateTime(String aText) throws IncorrectDateFormatException {
		Date res = null;
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS"); 
	      try { 
	          res = format.parse(aText); 
	      } catch (ParseException e) { 
	    	  throw new IncorrectDateFormatException();
	      }
		return res;				
	}
	
	/**
	 * Pārkonvertē datumu par tekstu formātā "dd.MM.yyyy HH:mm:ss:SSS".
	 */
	public static String eMeSDateTimeToStr(Date aDate) {
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS"); 
		return format.format(aDate);				
	}	
	
	/**
	 * Pārkonvertē laiku par tekstu formātā "HH:mm:ss:SSS".
	 */
	public static String eMeSTimeToStr(Date aDate) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS"); 
		return format.format(aDate);				
	}	
	
	/**
	 * Pārkonvertē datumu bez laika daļas par tekstu formātā "dd.MM.yyyy".
	 */
	public static String eMeSDateToStr(Date aDate) {
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy"); 
		return format.format(aDate);				
	}	
	
	//Iegūst ierakstu pa daļām
	/**
	 * Atgriež ierakstu, kas glabā sevī, cik norādītajā datumā rāda pulkstenis (Kura stunda, minūte, sekunde, milisekunde)
	 */
	public static MSRec_Time eMeSExtractTime(Date aDate) {
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
	 * Atgriež ierakstu, kas glabā sevī, kāds ir datums (kurš gads, kurš mēnesis, kura diena)
	 */
	public static MSRec_Date eMeSExtractDate(Date aDate) {
		MSRec_Date res = new MSRec_Date();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(aDate);
		res.day = cal.get(Calendar.DAY_OF_MONTH);
		res.month = cal.get(Calendar.MONTH) + 1;
		res.year = cal.get(Calendar.YEAR);
		return res;			
	}
	
	/**
	 * Atgriež ierakstu, kas glabā sevī pilnu datumu un laiku.
	 */
	public static MSRec_DateTime eMeSExtractDateTime(Date aDate) {
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
	
	//No ieraksta daļām iegūst datumu
	/**
	 * No datuma (bez laika vienībām) padotu ieraksta veidā izgūst datumu kā Date tipu.
	 */
	public static Date eMeSExtractDate(MSRec_Date aRecord) {
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
	 * No laika ieraksta padotu veidā izgūst datumu kā Date tipu.
	 */
	public static Date eMeSExtractTime(MSRec_Time aRecord) {
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
	 * No datuma kopā ar laiku padotu ieraksta veidā izgūst datumu kā Date tipu.
	 */
	public static Date eMeSExtractDateTime(MSRec_DateTime aRecord) {
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
	
	//skatās datumu kopumā un iegūst tā daļas vai vērtības
	public static int eMeSGetYearFromDate(Date aDate) {
		return eMeSExtractDate(aDate).year;
	}
	public static int eMeSGetMonthFromDate(Date aDate) {
		return eMeSExtractDate(aDate).month;
	}
	public static int eMeSGetDayFromDate(Date aDate) {
		return eMeSExtractDate(aDate).day;
	}
	
	/**
	 * Iegūst skaitlisku vērtību, kas raksturo, cik minūtes pagājušas kopš datuma `aDateFrom līdz datumam `aGivenDate. Ja `aDateFrom < `aGivenDate, tad atgriež -1.
	 * @param aGivenDate = 19.05.2016 17:00
	 * @param aDateFrom = 01.01.2016 00:00
	 * @return 3352L
	 */
	public static long eMeSGetHoursFromGivenDate(Date aGivenDate, Date aDateFrom) {
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
	 * Izsaka datumu sekundēs kopš epohas. ("Epoch, January 1, 1970 00:00:00.000 GMT (Gregorian).")
	 */
	public static long eMeSGetSecsFromDate(Date aDate) {
		if (aDate==null) return 0;
		GregorianCalendar calGivenDate = new GregorianCalendar();
		calGivenDate.setTime(aDate);
		return calGivenDate.getTimeInMillis() / 60000;
	}
	
	/**
	 * Izsaka datumu milisekundēs kopš Epohas. ("Epoch, January 1, 1970 00:00:00.000 GMT (Gregorian).")
	 */
	public static long eMeSGetMiliSecsFromDate(Date aDate) {
		if (aDate==null) return 0;
		GregorianCalendar calGivenDate = new GregorianCalendar();
		calGivenDate.setTime(aDate);
		return calGivenDate.getTimeInMillis();
	}
}