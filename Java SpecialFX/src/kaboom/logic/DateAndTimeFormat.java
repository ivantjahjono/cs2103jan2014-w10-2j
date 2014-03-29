package kaboom.logic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateAndTimeFormat {
	
	private static final String dateFormat1 = "ddMMyy";		// 12/06/12 or 12.01.06 or 120106
	private static final String dateFormat2 = "dd'/'MM'/'yy";
	private static final String dateFormat3 = "dd'.'MM'.'yy";
	
	private static final String time24hrFormat1 = "HHmm"; 
	private static final String time24hrFormat2 = "HH':'mm";
	private static final String time12hrFormat1 = "hhmma";
	private static final String time12hrFormat2 = "hhmm a";
	private static final String time12hrFormat3 = "h':'mma";
	private static final String time12hrFormat4 = "h':'mm a";
	
	private static final SimpleDateFormat[] dateFormatList = { 
		new SimpleDateFormat(dateFormat1),
		new SimpleDateFormat(dateFormat2),
		new SimpleDateFormat(dateFormat3)
	};
	
	private static final SimpleDateFormat[] time24HrFormatList = {
		new SimpleDateFormat(time24hrFormat1),
		new SimpleDateFormat(time24hrFormat2)
	};
	
	private static final SimpleDateFormat[] time12HrFormatList = {
		new SimpleDateFormat(time12hrFormat1),
		new SimpleDateFormat(time12hrFormat2),
		new SimpleDateFormat(time12hrFormat3),
		new SimpleDateFormat(time12hrFormat4)
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
		Calendar currentDateAndTime = Calendar.getInstance();
		Calendar dateAndTime = (Calendar) currentDateAndTime.clone();
	
		dateAndTime = dateTranslator(dateAndTime, date);
		dateAndTime = timeTranslator(dateAndTime,time);
		
		if (currentDateAndTime.equals(dateAndTime)) {
			return null;
		}
		
		return dateAndTime;
	}

	public Calendar addTimeToCalendar (Calendar dateAndTime, int hour, int min) {
		Calendar dateAndTimeToAdd = (Calendar) dateAndTime.clone();
		dateAndTimeToAdd.add(Calendar.HOUR_OF_DAY, hour);
		dateAndTimeToAdd.add(Calendar.MINUTE, min);
		return dateAndTimeToAdd;
	}
	
	//testing phase
	public boolean dateValidityForStartAndEndDate (Calendar startDate, Calendar endDate) {
		if (startDate == null || endDate == null) {
			return true;
		}
		if (startDate.before(endDate)) {
			return true;
		}
		return false;
	}
	
	private Calendar dateTranslator(Calendar thisDate, String theDate){
		if(theDate == null) {
			return thisDate;
		}
		
		for(int i = 0; i < dateFormatList.length; i++) {
			try {
				//validate date
				dateFormatList[i].setLenient(false);
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
		
		//12hr format check
		for(int i = 0; i < time12HrFormatList.length; i++) {
			try {
				//validate time
				time12HrFormatList[i].setLenient(false);
				Date time = time12HrFormatList[i].parse(theTime);
				//set time to thisTime
				Calendar getTime = Calendar.getInstance();
				getTime.setTime(time);
				thisTime.set(Calendar.HOUR, getTime.get(Calendar.HOUR));
				thisTime.set(Calendar.MINUTE, getTime.get(Calendar.MINUTE));
				thisTime.set(Calendar.AM_PM, getTime.get(Calendar.AM_PM));
				
				return thisTime;
			} catch (Exception e) {	
			}
		}
		if(!theTime.contains("pm") || theTime.contains("am")) {
			//24hr format check
			for(int i = 0; i < time24HrFormatList.length; i++) {
				try {
					//validate time
					time24HrFormatList[i].setLenient(false);
					Date time = time24HrFormatList[i].parse(theTime);
					//set time to thisTime
					Calendar getTime = Calendar.getInstance();
					getTime.setTime(time);
					thisTime.set(Calendar.HOUR_OF_DAY, getTime.get(Calendar.HOUR_OF_DAY));
					thisTime.set(Calendar.MINUTE, getTime.get(Calendar.MINUTE));
					return thisTime;
				} catch (Exception e) {	
				}
			}
		}
		
		/*
		//testing this method for flexible time format
		Date time = new Date();
		
		SimpleDateFormat simpletime24HrFormat = new SimpleDateFormat(time24Hr);
		simpletime24HrFormat.setLenient(false);
		
		try {
			time = simpletime24HrFormat.parse(theTime);
			calTime.set(Calendar.HOUR_OF_DAY, time.getHours());
			calTime.set(Calendar.MINUTE, time.getMinutes());
		} catch (Exception e) {
			
		}
		*/
		/*
		SimpleDateFormat simpletimeAmPmFormat = new SimpleDateFormat(timeAmPm);
		simpletimeAmPmFormat.setLenient(false);
		
		time = simpletimeAmPmFormat.parse(theTime);
		calTime.set(Calendar.HOUR, time.getHours());
		calTime.set(Calendar.MINUTE, time.getMinutes());
		*/
		
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
	
	public boolean is24hrTimeValid (String theTime) {
		if(theTime == null) {
			return false;
		}
		if(!theTime.contains("pm") || theTime.contains("am")) {
			for(int i = 0; i < time24HrFormatList.length; i++) {
				try {
					time24HrFormatList[i].setLenient(false);
					Date date = time24HrFormatList[i].parse(theTime);
					System.out.println(date);
					return true;
				} catch (Exception e) {	
				}
			}
		}
		return false;
	}
	public boolean is12hrTimeValid (String theTime) {
		if(theTime == null) {
			return false;
		}
		for(int i = 0; i < time12HrFormatList.length; i++) {
			try {
				
				time12HrFormatList[i].setLenient(false);
				Date date = time12HrFormatList[i].parse(theTime);
				System.out.println(date);
				return true;
			} catch (Exception e) {	
			}
		}
		return false;
	}
	//*************************** TEST METHODS **********************************
}
