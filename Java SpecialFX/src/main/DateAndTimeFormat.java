package main;

import java.util.Calendar;

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
	
	private static DateAndTimeFormat instance = null;
	
	public static DateAndTimeFormat getInstance () {
		if (instance  == null) {
			instance  = new DateAndTimeFormat ();
		}

		return instance;
	}

	private DateAndTimeFormat () {
	}
	
	
	public Calendar formatStringToCalendar (String date, String time) {
		Calendar cal = Calendar.getInstance();
		
		if(date != null) {
			System.out.println("1");
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
	

	
	public void dateTranslator(Calendar thisDate, String theDate){
		//this method should already take in the proper date format. verification should be separated in another method
		int date;
		int month;
		int year;
		String[] dateArray = new String[3];
		
		dateArray = theDate.split("/");
		
		if(dateArray[2] != null){
			year = Integer.parseInt(dateArray[2]);
			thisDate.set(Calendar.YEAR, year);
		}
		if(dateArray[1] != null){
			month = Integer.parseInt(dateArray[1]);
			thisDate.set(Calendar.MONTH, month-1);
		}
		if(dateArray[0] != null){
			date = Integer.parseInt(dateArray[0]);
			thisDate.set(Calendar.DAY_OF_MONTH, date);
		}
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
