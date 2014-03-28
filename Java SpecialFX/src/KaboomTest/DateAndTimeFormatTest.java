package KaboomTest;

import static org.junit.Assert.*;

import java.util.Calendar;

import kaboom.logic.DateAndTimeFormat;

import org.junit.Test;

public class DateAndTimeFormatTest {
	
	DateAndTimeFormat datFormat = DateAndTimeFormat.getInstance();
	Calendar cal = Calendar.getInstance();
	
	@Test
	public void dateTranslatorTest() {
		//test day
		assertEquals("11", datFormat.testDayFromDateTranslator(cal, "11/10/12"));
		assertEquals("12", datFormat.testDayFromDateTranslator(cal, "12.10.12"));
		assertEquals("13", datFormat.testDayFromDateTranslator(cal, "131012"));
		
		//test month (note that january == 0)
		assertEquals("0", datFormat.testMonthFromDateTranslator(cal, "11/01/12")); 
		assertEquals("1", datFormat.testMonthFromDateTranslator(cal, "12.02.12"));
		assertEquals("4", datFormat.testMonthFromDateTranslator(cal, "130512"));
		
		//test year
		assertEquals("2010", datFormat.testYearFromDateTranslator(cal, "11/01/10")); 
		assertEquals("2009", datFormat.testYearFromDateTranslator(cal, "12.02.09"));
		assertEquals("2012", datFormat.testYearFromDateTranslator(cal, "130512"));
	}
	
	@Test
	public void timeTranslatorTest() {
		//test hour
		assertEquals("12", datFormat.testHourFromTimeTranslator(cal, "1200"));
		assertEquals("0", datFormat.testHourFromTimeTranslator(cal, "0000"));
		assertEquals("23", datFormat.testHourFromTimeTranslator(cal, "2300"));
		
		//test min
		assertEquals("0", datFormat.testMinFromTimeTranslator(cal, "1200"));
		assertEquals("11", datFormat.testMinFromTimeTranslator(cal, "0011"));
		assertEquals("59", datFormat.testMinFromTimeTranslator(cal, "2359"));
	}
	
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
	
	@Test
	public void startDateAndEndDateValidityTest() {
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = null;
				
		//Test if valid if same start and end
		endDate = datFormat.addTimeToCalendar(startDate, 0, 0);
		assertEquals("false",datFormat.dateValidityForStartAndEndDate(startDate, endDate));
		
		//Test if valid if end is after start (1min later)
		endDate = datFormat.addTimeToCalendar(startDate, 0, 1);
		assertEquals("true",datFormat.dateValidityForStartAndEndDate(startDate, endDate));
		
		//Test if valid if end is after start (1min before)
		endDate = datFormat.addTimeToCalendar(startDate, 0, -1);
		assertEquals("false",datFormat.dateValidityForStartAndEndDate(startDate, endDate));
		
		
	}
}
