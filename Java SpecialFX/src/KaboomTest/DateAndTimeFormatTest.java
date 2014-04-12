//@author A0099863H
package KaboomTest;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import kaboom.logic.DateAndTimeFormat;
import kaboom.logic.InvalidDateAndTimeException;

import org.junit.Test;

public class DateAndTimeFormatTest {
	
	DateAndTimeFormat datFormat = DateAndTimeFormat.getInstance();
	Calendar cal = Calendar.getInstance();
	
	@Test
	public void addTimeToCalendarTest() {
		Calendar date = Calendar.getInstance();
		Calendar dateAfterAddition = null;
		date.set(Calendar.HOUR_OF_DAY, 12);
		date.set(Calendar.MINUTE, 0);
		
		//Test for no manipulation
		dateAfterAddition = datFormat.addTimeToCalendar(date, 0, 0);
		assertEquals("12", Integer.toString(dateAfterAddition.get(Calendar.HOUR_OF_DAY)));
		assertEquals("0", Integer.toString(dateAfterAddition.get(Calendar.MINUTE)));
		
		//Test for 1 Hour After
		dateAfterAddition = datFormat.addTimeToCalendar(date, 1, 0);
		assertEquals("13", Integer.toString(dateAfterAddition.get(Calendar.HOUR_OF_DAY)));
		assertEquals("0", Integer.toString(dateAfterAddition.get(Calendar.MINUTE)));
		
		//Test for 30 Mins After
		dateAfterAddition = datFormat.addTimeToCalendar(date, 0, 30);
		assertEquals("12", Integer.toString(dateAfterAddition.get(Calendar.HOUR_OF_DAY)));
		assertEquals("30", Integer.toString(dateAfterAddition.get(Calendar.MINUTE)));
		
		//Test for 1 Hour Before
		dateAfterAddition = datFormat.addTimeToCalendar(date, -1, 0);
		assertEquals("11", Integer.toString(dateAfterAddition.get(Calendar.HOUR_OF_DAY)));
		assertEquals("0", Integer.toString(dateAfterAddition.get(Calendar.MINUTE)));
		
		//Test for 30 Mins Before
		dateAfterAddition = datFormat.addTimeToCalendar(date, 0, -30);
		assertEquals("11", Integer.toString(dateAfterAddition.get(Calendar.HOUR_OF_DAY)));
		assertEquals("30", Integer.toString(dateAfterAddition.get(Calendar.MINUTE)));
		
		//Date Boundaries (Next Day)
		date.set(Calendar.HOUR_OF_DAY, 23);
		date.set(Calendar.MINUTE, 59);
		date.set(Calendar.DAY_OF_MONTH, 10);
		
		//Test 1 Hour After
		dateAfterAddition = datFormat.addTimeToCalendar(date, 1, 0);
		assertEquals("0", Integer.toString(dateAfterAddition.get(Calendar.HOUR_OF_DAY)));
		assertEquals("59", Integer.toString(dateAfterAddition.get(Calendar.MINUTE)));
		assertEquals("11", Integer.toString(dateAfterAddition.get(Calendar.DAY_OF_MONTH)));
		
		//Test 1 Min After
		dateAfterAddition = datFormat.addTimeToCalendar(date, 0, 1);
		assertEquals("0", Integer.toString(dateAfterAddition.get(Calendar.HOUR_OF_DAY)));
		assertEquals("0", Integer.toString(dateAfterAddition.get(Calendar.MINUTE)));
		assertEquals("11", Integer.toString(dateAfterAddition.get(Calendar.DAY_OF_MONTH)));
		
	}
	

	
	
	
	/*
	 * SimpleDateFormat Year Boundary: 2 digit years range from 1934 to 2033
	 */
	@Test
	public void testDateValidity() {
		//Null
		assertFalse ("Null test",datFormat.isDateValid(null));
		
		//ddMMyy Format
		assertTrue ("Valid: ddMMyy format",datFormat.isDateValid("010414"));
		//invalid date
		assertFalse ("Invalid: day",datFormat.isDateValid("000414"));
		//invalid month
		assertFalse ("Invalid: month",datFormat.isDateValid("011314"));
		//Year boundary (millennium year)
		assertTrue ("Valid: 2000",datFormat.isDateValid("010100"));
		//Year boundary 1999
		assertTrue ("Valid: 1999",datFormat.isDateValid("010101"));
		//Year boundary 2001
		assertTrue ("Valid: 2001",datFormat.isDateValid("010199"));
		
		System.out.println("----------------------------------------------------------------");
		//dd/MM/yy Format
		assertTrue ("Valid: dd/MM/yy format",datFormat.isDateValid("21/04/14"));
		assertTrue ("Valid: dd/MM/yy format",datFormat.isDateValid("21/04/33"));
		//invalid day
		assertFalse ("Invalid: day",datFormat.isDateValid("31/04/14"));
		//invalid Format 
		assertFalse ("Invalid: format",datFormat.isDateValid("31.04/14"));
		//invalid Format 
		assertFalse ("Invalid: format",datFormat.isDateValid("31004014"));
		
		//dd.MM.yy Format
		assertTrue ("Valid: dd/MM/yy format",datFormat.isDateValid("21.04.14"));
		assertTrue ("Valid: dd/MM/yy format",datFormat.isDateValid("21.04.34"));
		assertFalse ("Invalid: month",datFormat.isDateValid("21.00.34"));
		assertFalse ("Invalid: format",datFormat.isDateValid("21/00/34"));
		assertFalse ("Invalid: format",datFormat.isDateValid("21-00-34"));
		assertTrue ("Invalid: format",datFormat.isDateValid("01/04/14"));
	}
	
	@Test
	public void dateFromCalendarToStringConverterTest() {
		Calendar cal = Calendar.getInstance();
		cal.set(2014, Calendar.MAY, 01);
		assertEquals("010514",datFormat.dateFromCalendarToString (cal)); 
		cal.set(2000, Calendar.JULY, 01);
		assertEquals("010700",datFormat.dateFromCalendarToString (cal));  
		cal.set(Calendar.MONTH,Calendar.JANUARY);
		assertEquals("010100",datFormat.dateFromCalendarToString (cal));  
	}
	
	@Test
	public void timeFromCalendarToStringConverterTest() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 15);
		cal.set(Calendar.MINUTE, 30);
		assertEquals("1530",datFormat.timeFromCalendarToString (cal)); 
		cal.set(Calendar.HOUR_OF_DAY, 01);
		cal.set(Calendar.MINUTE, 30);
		assertEquals("0130",datFormat.timeFromCalendarToString (cal));  
	}
	
	@Test
	public void convertTime () {
		String timeString = "";
		
		timeString = "1pm";
		assertEquals("1300", datFormat.convertStringTimeTo24HourString(timeString));
		
		timeString = "1:00";
		assertEquals("0100", datFormat.convertStringTimeTo24HourString(timeString));
		
		timeString = "13:00";
		assertEquals("1300", datFormat.convertStringTimeTo24HourString(timeString));
		
		timeString = "8pm";
		assertEquals("2000", datFormat.convertStringTimeTo24HourString(timeString));
		
		timeString = "8am";
		assertEquals("0800", datFormat.convertStringTimeTo24HourString(timeString));
		
		timeString = "12:01am";
		assertEquals("0001", datFormat.convertStringTimeTo24HourString(timeString));
		
		timeString = "12:34pm";
		assertEquals("1234", datFormat.convertStringTimeTo24HourString(timeString));
		
		timeString = "1234";
		assertEquals("1234", datFormat.convertStringTimeTo24HourString(timeString));
		
		timeString = "2334";
		assertEquals("2334", datFormat.convertStringTimeTo24HourString(timeString));
	}
	
	@Test
	public void convertDateStringToCalendar () {
		String timeString = "";
		String dateString = "";
		Calendar testCalendar = null;
		SimpleDateFormat sdf = new  SimpleDateFormat("HHmm ddMMyy");
		
		timeString = "0800";
		dateString = "121212";
		testCalendar = datFormat.formatStringToCalendar(dateString, timeString);
		assertEquals(sdf.format(testCalendar.getTime()), timeString + " " + dateString);
		
		timeString = "2000";
		dateString = "080414";
		testCalendar = datFormat.formatStringToCalendar(dateString, timeString);
		assertEquals(sdf.format(testCalendar.getTime()), timeString + " " + dateString);
		System.out.println(sdf.format(testCalendar.getTime()));
	}
}
