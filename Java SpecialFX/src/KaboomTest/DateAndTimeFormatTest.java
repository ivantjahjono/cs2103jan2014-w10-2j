package KaboomTest;

import static org.junit.Assert.*;

import java.util.Calendar;

import kaboom.logic.DateAndTimeFormat;

import org.junit.Test;

public class DateAndTimeFormatTest {
	
	DateAndTimeFormat datFormat = DateAndTimeFormat.getInstance();
	Calendar cal = Calendar.getInstance();
	
//	@Test
//	public void dateTranslatorTest() {
//		//test day
//		assertEquals("11", datFormat.testDayFromDateTranslator(cal, "11/10/12"));
//		assertEquals("12", datFormat.testDayFromDateTranslator(cal, "12.10.12"));
//		assertEquals("13", datFormat.testDayFromDateTranslator(cal, "131012"));
//		
//		//test month (note that january == 0)
//		assertEquals("0", datFormat.testMonthFromDateTranslator(cal, "11/01/12")); 
//		assertEquals("1", datFormat.testMonthFromDateTranslator(cal, "12.02.12"));
//		assertEquals("4", datFormat.testMonthFromDateTranslator(cal, "130512"));
//		
//		//test year
//		assertEquals("2010", datFormat.testYearFromDateTranslator(cal, "11/01/10")); 
//		assertEquals("2009", datFormat.testYearFromDateTranslator(cal, "12.02.09"));
//		assertEquals("2012", datFormat.testYearFromDateTranslator(cal, "130512"));
//	}
	
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
	


}
