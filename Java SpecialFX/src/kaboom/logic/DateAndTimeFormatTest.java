package kaboom.logic;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

public class DateAndTimeFormatTest {
	
	DateAndTimeFormat datFormat = DateAndTimeFormat.getInstance();
	
	@Test
	public void dateTranslatorTest() {
		Calendar cal = Calendar.getInstance();
		assertEquals("12", datFormat.dateTranslator(cal, "10/10/12"));
		assertEquals("12", datFormat.dateTranslator(cal, "10.10.12"));
		assertEquals("12", datFormat.dateTranslator(cal, "101012"));
	}

}
