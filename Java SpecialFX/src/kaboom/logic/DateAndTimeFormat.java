package kaboom.logic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// Can use Java SimpleDateFormat for date checking




public class DateAndTimeFormat {
	
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
			cal = dateTranslator(cal, date);
		}
		
		if(time != null) {
			cal = timeTranslator(cal,time);
//			TimeFormat currTimeFormat = new TimeFormat();
//			if(isTimeValid(time, currTimeFormat)){
//				timeTranslator(cal, Integer.parseInt(time), currTimeFormat);
//			}
		}
		return cal;
	}

	private Calendar dateTranslator(Calendar thisDate, String theDate){
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
			if(theDate.length()==6) {
				dateArray[0] = theDate.substring(0,2);
				dateArray[1] = theDate.substring(2,4);
				dateArray[2] = theDate.substring(4,6);
			}
		}
		
		//check date and set as calendar
		date = dateArray[0]+dateArray[1]+dateArray[2];
		
		if (isDateValid(date)) {
			int year = Integer.parseInt(dateArray[2]) + 2000;		// TODO Hardcode to add 2000
			thisDate.set(Calendar.YEAR, year);
			int month = Integer.parseInt(dateArray[1]);
			thisDate.set(Calendar.MONTH, month-1);
			int day = Integer.parseInt(dateArray[0]);
			thisDate.set(Calendar.DAY_OF_MONTH, day);
		}
		return thisDate;
	}
	
	private boolean isDateValid (String theDate) {
		if(theDate == null) {
			return false;
		}
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		simpleDateFormat.setLenient(false);
		
		try {
			simpleDateFormat.parse(theDate);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	//Currently translate 1700 format only
	private Calendar timeTranslator(Calendar cal, String theTime) {
		if (!(theTime == null || theTime.length() != 4)) {
			String hourInString = theTime.substring(0,2);
			String minsInString = theTime.substring(2,4);
			
			int hour = Integer.parseInt(hourInString);
			int mins = Integer.parseInt(minsInString);
			
			if(isHourValid(hour) && isMinsValid(mins)) {
				cal.set(Calendar.HOUR_OF_DAY, hour);
				cal.set(Calendar.MINUTE, mins);
			}
		}
		else if(!(theTime == null || theTime.length() != 3)){
			String hourInString = theTime.substring(0,1);
			String minsInString = theTime.substring(1,3);
			
			int hour = Integer.parseInt(hourInString);
			int mins = Integer.parseInt(minsInString);
			
			if(isHourValid(hour) && isMinsValid(mins)) {
				cal.set(Calendar.HOUR_OF_DAY, hour);
				cal.set(Calendar.MINUTE, mins);
			}
		}
		return cal;
	}
	
	private boolean isHourValid (int hour) {
		if(hour >= 0 && hour <= 23) {
			return true;
		}
		return false;
	}
	private boolean isMinsValid (int mins) {
		if(mins >= 0 && mins <= 59) {
			return true;
		}
		return false;
	}
	
	
//	private boolean isTimeValid(String allegedTime, TimeFormat currTimeFormat) {
//		
//		try{
//			//check if it's the 24 hour format without separation. Eg: 1700, 1000
//			int correctTimeFormat = Integer.parseInt(allegedTime);
//			//check if the time is in logical number
//			//!!!!!!!!!revise this again. Logic error
//			if((correctTimeFormat >= CORRECT_24HOUR_FORMAT_MIN) && (correctTimeFormat <= CORRECT_24HOUR_FORMAT_MAX) ){
//				currTimeFormat.setTimeFormatCode(THE_24_HOUR_FORMAT_CODE);
//				return true;
//			}
//			else {
//				return false;
//			}
//			
//		}
//		catch(IllegalArgumentException exception){
//			//this means either invalid format or the other different formats
//			//5am, 5pm, 17:00, 5:00am, etc
//			return false;
//		}
//		
//		//this is a stub
//		//return false;
//	}
//	
//	
//	
//	private void timeTranslator(Calendar theTime, int correctTime, TimeFormat currTimeFormat){
//		//this method translates ALL time formats
//		if(currTimeFormat.getTimeFormatCode() == THE_24_HOUR_FORMAT_CODE){
//			int hour = correctTime / CORRECT_24HOUR_FORMAT_MIN;
//			int minute = correctTime % CORRECT_24HOUR_FORMAT_MIN;
//			theTime.set(Calendar.HOUR_OF_DAY, hour);
//			theTime.set(Calendar.MINUTE, minute);
//		}
//		else{
//			return;
//		}
//		return;
//	}
	
	//*************************** TEST METHODS **********************************
	//Date tests
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
	//Time tests
	public String testHourFromTimeTranslator (Calendar cal, String theTime) {
		timeTranslator (cal,theTime);	
		return Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
	}
	public String testMinFromTimeTranslator (Calendar cal, String theTime) {
		timeTranslator (cal,theTime);	
		return Integer.toString(cal.get(Calendar.MINUTE));
	}
	//*************************** TEST METHODS **********************************
}
