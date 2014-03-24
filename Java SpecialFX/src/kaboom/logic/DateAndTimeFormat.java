package kaboom.logic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// Can use Java SimpleDateFormat for date checking




public class DateAndTimeFormat {

	private static final int CORRECT_24HOUR_FORMAT_MIN = 100;
	private static final int CORRECT_24HOUR_FORMAT_MAX = 2359;
	
	private static final int THE_24_HOUR_FORMAT_CODE = 1;
	//private static final int THE_24_HOUR_FORMAT_WITH_COLON_CODE = 2;
	//private static final int THE_AM_FORMAT_CODE = 3;
	//private static final int THE_AM_FORMAT_WITH_COLON_CODE = 4;
	//private static final int THE_PM_FORMAT_CODE = 5;
	//private static final int THE_PM_FORMAT_WITH_COLON_CODE = 6;
	private static final int START_DATE_COUNT = 1;
	private static final int END_DATE_COUNT = 2;
	
	private static final String dateFormat = "ddMMyy";		// 12/06/12 or 12.01.06 or 120106

	private static DateAndTimeFormat instance = null;
	
	public static DateAndTimeFormat getInstance () {
		if (instance  == null) {
			instance  = new DateAndTimeFormat ();
		}

		return instance;
	}

	public Calendar formatStringToCalendar (String date, String time) {
		Calendar cal = Calendar.getInstance();
		
		if(date != null) {
			dateTranslator(cal, date);
		}
		
		if(time != null) {
			TimeFormat currTimeFormat = new TimeFormat();
			if(verifyTimeValidity(time, currTimeFormat)){
				timeTranslator(cal, Integer.parseInt(time), currTimeFormat);
			}
		}
		
		return cal;
	}
	

	
	public String dateTranslator(Calendar thisDate, String theDate){
		//this method should already take in the proper date format. verification should be separated in another method
		//Currently takes in 12/06/12 or 12.01.06 or 120106
		String date = "";
		String[] dateArray = new String[3];
		
		//extract
		if(theDate.contains("/")) {
			dateArray = theDate.split("/");
		} else if (theDate.contains(".")) {
			dateArray = theDate.split("\\.");
		} else {
			dateArray[0] = theDate.substring(0,2);
			dateArray[1] = theDate.substring(2,4);
			dateArray[2] = theDate.substring(4,6);
		}
		
		//check date and set as calendar
		date = dateArray[0]+dateArray[1]+dateArray[2];
		
		if (isDateValid(date)) {
			int year = Integer.parseInt(dateArray[2]);
			thisDate.set(Calendar.YEAR, year);
			int month = Integer.parseInt(dateArray[1]);
			thisDate.set(Calendar.MONTH, month-1);
			int day = Integer.parseInt(dateArray[0]);
			thisDate.set(Calendar.DAY_OF_MONTH, day);
		}
		
		return dateArray[2];
	}
	
	private boolean isDateValid (String theDate) {
		if(theDate == null) {
			return false;
		}
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		simpleDateFormat.setLenient(false);
		
		try {
			Date date = simpleDateFormat.parse(theDate);
			System.out.println(date);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private void timeTranslator(Calendar theTime, int correctTime, TimeFormat currTimeFormat){
		//this method translates ALL time formats
		if(currTimeFormat.getTimeFormatCode() == THE_24_HOUR_FORMAT_CODE){
			int hour = correctTime / CORRECT_24HOUR_FORMAT_MIN;
			int minute = correctTime % CORRECT_24HOUR_FORMAT_MIN;
			theTime.set(Calendar.HOUR_OF_DAY, hour);
			theTime.set(Calendar.MINUTE, minute);
		}
		else{
			return;
		}
		return;
		
	}
	
	
	private boolean verifyTimeValidity(String allegedTime, TimeFormat currTimeFormat) {
		
		try{
			//check if it's the 24 hour format without separation. Eg: 1700, 1000
			int correctTimeFormat = Integer.parseInt(allegedTime);
			//check if the time is in logical number
			//!!!!!!!!!revise this again. Logic error
			if((correctTimeFormat >= CORRECT_24HOUR_FORMAT_MIN) && (correctTimeFormat <= CORRECT_24HOUR_FORMAT_MAX) ){
				currTimeFormat.setTimeFormatCode(THE_24_HOUR_FORMAT_CODE);
				return true;
			}
			else {
				return false;
			}
			
		}
		catch(IllegalArgumentException exception){
			//this means either invalid format or the other different formats
			//5am, 5pm, 17:00, 5:00am, etc
			return false;
		}
		
		//this is a stub
		//return false;
	}

	//*************************** TEST METHODS **********************************
	public String testDayFromDateTranslator (Calendar thisDate, String theDate) {
		dateTranslator (thisDate,theDate);	
		return Integer.toString(thisDate.get(Calendar.DAY_OF_MONTH));
	}
	public String testMonthFromDateTranslator (Calendar thisDate, String theDate) {
		dateTranslator (thisDate,theDate);	
		return Integer.toString(thisDate.get(Calendar.MONTH));
	}
	public String testYearFromDateTranslator (Calendar thisDate, String theDate) {
		dateTranslator (thisDate,theDate);	
		return Integer.toString(thisDate.get(Calendar.YEAR));
	}
	//*************************** TEST METHODS **********************************
}
