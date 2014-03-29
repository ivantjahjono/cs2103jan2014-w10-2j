package kaboom.logic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateAndTimeFormat {
	
	private static final String dateFormat1 = "ddMMyy";		// 12/06/12 or 12.01.06 or 120106
	private static final String dateFormat2 = "dd'/'MM'/'yy";
	private static final String dateFormat3 = "dd'.'MM'.'yy";
	
	private static final String timeFormat1 = "HHmm";
	private static final String timeFormat2 = "HH':'mm";
	
	private static final SimpleDateFormat[] dateFormatList = { 
		new SimpleDateFormat(dateFormat1),
		new SimpleDateFormat(dateFormat2),
		new SimpleDateFormat(dateFormat3),
	};
	
	private static final SimpleDateFormat[] timeFormatList = {
		new SimpleDateFormat(timeFormat1),
		new SimpleDateFormat(timeFormat2),
	};
	
	private static DateAndTimeFormat instance = null;
	
	private DateAndTimeFormat() {
	}
	
	public static DateAndTimeFormat getInstance () {
		if (instance  == null) {
			instance  = new DateAndTimeFormat ();
		}

		return instance;
	}

	public Calendar formatStringToCalendar (String date, String time) {
		if(date == null && time == null) {
			return null;
		}
		Calendar dateAndTime = Calendar.getInstance();
	
		dateAndTime = dateTranslator(dateAndTime, date);
		dateAndTime = timeTranslator(dateAndTime,time);
		
		return dateAndTime;
	}

	public Calendar addTimeToCalendar (Calendar dateAndTime, int hour, int min) {
		Calendar dateAndTimeToAdd = (Calendar) dateAndTime.clone();
		dateAndTimeToAdd.add(Calendar.HOUR_OF_DAY, hour);
		dateAndTimeToAdd.add(Calendar.MINUTE, min);
		return dateAndTimeToAdd;
	}
	
	//testing phase
	public String dateValidityForStartAndEndDate (Calendar startDate, Calendar endDate) {
		if (startDate.before(endDate)) {
			return "true";
		}
		return "false";
	}
	
	private Calendar dateTranslator(Calendar thisDate, String theDate){
		if(theDate == null) {
			return thisDate;
		}
		for(int i = 0; i < dateFormatList.length; i++) {
			try {
				//validate date
				Date date = dateFormatList[i].parse(theDate);
				//set date to thisDate
				Calendar getDate = Calendar.getInstance();
				getDate.setTime(date);
				thisDate.set(Calendar.DAY_OF_MONTH, getDate.get(Calendar.DAY_OF_MONTH));
				thisDate.set(Calendar.MONTH, getDate.get(Calendar.MONTH));
				thisDate.set(Calendar.YEAR, getDate.get(Calendar.YEAR));
				return thisDate;
			} catch (Exception e) {	
			}
		}
		//throw invalid date exception
		return thisDate;
	}
	
	private Calendar timeTranslator(Calendar thisTime, String theTime){
		if(theTime == null) {
			return thisTime;
		}
		for(int i = 0; i < timeFormatList.length; i++) {
			try {
				//validate time
				Date time = timeFormatList[i].parse(theTime);
				//set time to thisTime
				Calendar getTime = Calendar.getInstance();
				getTime.setTime(time);
				thisTime.set(Calendar.HOUR_OF_DAY, getTime.get(Calendar.HOUR_OF_DAY));
				thisTime.set(Calendar.MINUTE, getTime.get(Calendar.MINUTE));
				return thisTime;
			} catch (Exception e) {	
			}
		}
		//throw invalid time exception
		return thisTime;
	}
	

	
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
	public String isTimeValidTest (String time) {
		if(isTimeValid(time)) {
			return "true";
		} else {
			return "false";
		}
	}
	public boolean isDateValid (String theDate) {
		if(theDate == null) {
			return false;
		}
		for(int i = 0; i < dateFormatList.length; i++) {
			try {
				dateFormatList[i].setLenient(false);
				dateFormatList[i].parse(theDate);
				return true;
			} catch (Exception e) {	
			}
		}
		return false;
	}
	
	public boolean isTimeValid (String theTime) {
		if(theTime == null) {
			return false;
		}
		for(int i = 0; i < timeFormatList.length; i++) {
			try {
				timeFormatList[i].setLenient(false);
				timeFormatList[i].parse(theTime);
				return true;
			} catch (Exception e) {	
			}
		}
		return false;
	}
	//*************************** TEST METHODS **********************************
}
