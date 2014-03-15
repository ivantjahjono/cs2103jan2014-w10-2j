package main;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

public class DateAndTimeFormatTest {

	@Test
	public void dateTranslatorTestDay() {
		Calendar cal = Calendar.getInstance();
		assertEquals("10",DateAndTimeFormat.getInstance().testDayFromDateTranslator(cal, "10/10/2013"));
		assertEquals("10",DateAndTimeFormat.getInstance().testDayFromDateTranslator(cal, "10102013"));
	}

	@Test
	public void dateTranslatorTestMonth() {
		Calendar cal = Calendar.getInstance();
		assertEquals("10",DateAndTimeFormat.getInstance().testMonthFromDateTranslator(cal, "10/10/2013"));
	}
	
	@Test
	public void dateTranslatorTestYear() {
		Calendar cal = Calendar.getInstance();
		assertEquals("2013",DateAndTimeFormat.getInstance().testYearFromDateTranslator(cal, "10/10/2013"));
	}
}
