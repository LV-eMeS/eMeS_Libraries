package lv.emes.libraries.tools;

import lv.emes.libraries.tools.MS_TimeTools.*;
import org.junit.Test;

import java.util.Date;

import static lv.emes.libraries.tools.MS_TimeTools.*;
import static org.junit.Assert.assertTrue;

public class MSTimeToolsTest {		
	public void testExceptions() throws IncorrectStartingYearException {
		throw new MS_TimeTools.IncorrectStartingYearException(1992, "test");
	}	
	
	@Test
	public void testExceptions2() {			
		try {
			testExceptions();
			assertTrue(false);
		} catch (IncorrectStartingYearException e) {
			assertTrue(true);
			System.out.println(e.getMessage());
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testConvertation() {
		Date ddate = null;
		Date dtime = null;
		try {
			ddate = strToDate("03.07.2016");
		} catch (IncorrectDateFormatException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		try {
			dtime = strToTime("23:02:11:000");
		} catch (IncorrectDateFormatException e) {		}
		System.out.println(ddate.getTime());
		System.out.println(dtime.getTime());
		System.out.println(ddate.getTime()+dtime.getTime());
		System.out.println(dateTimeToStr(ddate));
		System.out.println(dateTimeToStr(dtime));
		//System.out.println(dateTimeToStr(ddate));
		System.out.println(dateTimeToStr(new Date(ddate.getTime()+dtime.getTime())));
		
		Date bothDates = new Date(ddate.getTime()+dtime.getTime());	
		//te ir tāda lieta, ka datumiem 0 nozīmē sākuma gadu, kas domāts kā pirmais no iespējamajiem datumiem, kā rezultātā, tos saskaitot, rodas nobīde no normas
		bothDates.setHours(bothDates.getHours()+2);
		System.out.println(dateTimeToStr(bothDates));
		assertTrue(dateTimeToStr(bothDates).equals("03.07.2016 23:02:11:000"));
	}	
	
	@Test
	public void testConstructors() {
		Date date = new Date();
		try {
			date = strToTime("14:1:53:101");
		} catch (IncorrectDateFormatException e) {
			e.printStackTrace();
		}
		MSRec_Time record = MSRec_Time.newInstance(date);
		assertTrue(record.hour == 14);
		assertTrue(record.min == 1);
		assertTrue(record.sec == 53);
		assertTrue(record.millisec == 101);
	}
	
	@Test
	public void testRecords() {
		Date ddate = null;
		try {
			ddate = strToDateTime("03.07.2016 23:02:11:000");
		} catch (IncorrectDateFormatException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		MS_TimeTools.MSRec_Time ttime = extractTime(ddate);
		assertTrue(ttime.hour==23);
		assertTrue(ttime.millisec==0);
		assertTrue(ttime.min==2);
		assertTrue(ttime.sec==11);
		
		MS_TimeTools.MSRec_DateTime dateTime = extractDateTime(ddate);
		assertTrue(dateTime.hour==23);
		assertTrue(dateTime.millisec==0);
		assertTrue(dateTime.min==2);
		assertTrue(dateTime.sec==11);	
		
		assertTrue(dateTime.day==3);	
		assertTrue(dateTime.month==7);	
		assertTrue(dateTime.year==2016);	
		
		MSRec_Date date = new MSRec_Date();
		date.day = 12;
		date.month = 5;
		date.year = 1992;
		ddate = extractDate(date);
		assertTrue(dateToStr(ddate).equals("12.05.1992"));
		
		ddate = extractDateTime(dateTime);
		assertTrue(dateTimeToStr(ddate).equals("03.07.2016 23:02:11:000"));
	}	
	
	@Test
	public void testOtherMethods() {
		Date date = null;
		try {
			date = strToDateTime("19.05.2016 17:00:00:000");
		} catch (IncorrectDateFormatException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		assertTrue(getYearFromDate(date)==2016);
		assertTrue(getDayFromDate(date)==19);
		assertTrue(getMonthFromDate(date)==5);
		
		MSRec_Date rec = new MSRec_Date();
		rec.day = 1;
		rec.month = 1;
		rec.year = 2016;
		Date referenceYear = extractDate(rec);
		Date referenceYear2 = extractDate(rec);
		assertTrue(getHoursFromGivenDate(date, null)==0);
		assertTrue(getHoursFromGivenDate(null, null)==0);
		assertTrue(getHoursFromGivenDate(null, referenceYear)==0);
		assertTrue(getHoursFromGivenDate(referenceYear, referenceYear)==0);
		assertTrue(getHoursFromGivenDate(referenceYear, referenceYear2)==0); //vērtības objektiem vienādas, bet tie rāda ta uz atšķirīgām adresēm
		rec.year = 2000;
		referenceYear = extractDate(rec);
		assertTrue(getHoursFromGivenDate(referenceYear, referenceYear2) == -1); //ja mazāku pirmo datumu padod, tad rezultātam jābūt vienmēr -1, kas nozīmē, ka kļūda
		//strādājošs piemērs
		Date date1 = null, date2 = null;
		try {
			date1 = strToDateTime("19.05.2016 17:00:00:000");
		} catch (IncorrectDateFormatException e) {
			e.printStackTrace();
		}
		
		try {
			//date2 = strToDateTime("01.01.2016 00:00:00:000");
			date2 = strToDateTime("01.01.2016 00:00:00:000");
		} catch (IncorrectDateFormatException e) {
			e.printStackTrace();
		}
		assertTrue(getHoursFromGivenDate(date1, date2) == 3352);
	}		
}
