package lv.emes.libraries.tools;

import static lv.emes.libraries.tools.MS_TimeTools.*;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import lv.emes.libraries.tools.MS_TimeTools;
import lv.emes.libraries.tools.MS_TimeTools.IncorrectDateFormatException;
import lv.emes.libraries.tools.MS_TimeTools.IncorrectStartingYearException;

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
			ddate = eMeSStrToDate("03.07.2016");
		} catch (IncorrectDateFormatException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		try {
			dtime = eMeSStrToTime("23:02:11:000");
		} catch (IncorrectDateFormatException e) {		}
		System.out.println(ddate.getTime());
		System.out.println(dtime.getTime());
		System.out.println(ddate.getTime()+dtime.getTime());
		System.out.println(eMeSDateTimeToStr(ddate));
		System.out.println(eMeSDateTimeToStr(dtime));
		//System.out.println(eMeSDateTimeToStr(ddate));
		System.out.println(eMeSDateTimeToStr(new Date(ddate.getTime()+dtime.getTime())));
		
		Date bothDates = new Date(ddate.getTime()+dtime.getTime());	
		//te ir tāda lieta, ka datumiem 0 nozīmē sākuma gadu, kas domāts kā pirmais no iespējamajiem datumiem, kā rezultātā, tos saskaitot, rodas nobīde no normas
		bothDates.setHours(bothDates.getHours()+2);
		System.out.println(eMeSDateTimeToStr(bothDates));
		assertTrue(eMeSDateTimeToStr(bothDates).equals("03.07.2016 23:02:11:000"));
	}	
	
	@Test
	public void testConstructors() {
		Date date = new Date();
		try {
			date = eMeSStrToTime("14:1:53:101");
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
			ddate = eMeSStrToDateTime("03.07.2016 23:02:11:000");
		} catch (IncorrectDateFormatException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		MS_TimeTools.MSRec_Time ttime = eMeSExtractTime(ddate);
		assertTrue(ttime.hour==23);
		assertTrue(ttime.millisec==0);
		assertTrue(ttime.min==2);
		assertTrue(ttime.sec==11);
		
		MS_TimeTools.MSRec_DateTime dateTime = eMeSExtractDateTime(ddate);
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
		ddate = eMeSExtractDate(date);
		assertTrue(eMeSDateToStr(ddate).equals("12.05.1992"));
		
		ddate = eMeSExtractDateTime(dateTime);
		assertTrue(eMeSDateTimeToStr(ddate).equals("03.07.2016 23:02:11:000"));
	}	
	
	@Test
	public void testOtherMethods() {
		Date date = null;
		try {
			date = eMeSStrToDateTime("19.05.2016 17:00:00:000");
		} catch (IncorrectDateFormatException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		assertTrue(eMeSGetYearFromDate(date)==2016);
		assertTrue(eMeSGetDayFromDate(date)==19);
		assertTrue(eMeSGetMonthFromDate(date)==5);
		
		MSRec_Date rec = new MSRec_Date();
		rec.day = 1;
		rec.month = 1;
		rec.year = 2016;
		Date referenceYear = eMeSExtractDate(rec);
		Date referenceYear2 = eMeSExtractDate(rec);
		assertTrue(eMeSGetHoursFromGivenDate(date, null)==0);
		assertTrue(eMeSGetHoursFromGivenDate(null, null)==0);
		assertTrue(eMeSGetHoursFromGivenDate(null, referenceYear)==0);
		assertTrue(eMeSGetHoursFromGivenDate(referenceYear, referenceYear)==0);
		assertTrue(eMeSGetHoursFromGivenDate(referenceYear, referenceYear2)==0); //vērtības objektiem vienādas, bet tie rāda ta uz atšķirīgām adresēm
		rec.year = 2000;
		referenceYear = eMeSExtractDate(rec);
		assertTrue(eMeSGetHoursFromGivenDate(referenceYear, referenceYear2) == -1); //ja mazāku pirmo datumu padod, tad rezultātam jābūt vienmēr -1, kas nozīmē, ka kļūda
		//strādājošs piemērs
		Date date1 = null, date2 = null;
		try {
			date1 = eMeSStrToDateTime("19.05.2016 17:00:00:000");
		} catch (IncorrectDateFormatException e) {
			e.printStackTrace();
		}
		
		try {
			//date2 = eMeSStrToDateTime("01.01.2016 00:00:00:000");
			date2 = eMeSStrToDateTime("01.01.2016 00:00:00:000");
		} catch (IncorrectDateFormatException e) {
			e.printStackTrace();
		}
		assertTrue(eMeSGetHoursFromGivenDate(date1, date2) == 3352);
	}		
}
