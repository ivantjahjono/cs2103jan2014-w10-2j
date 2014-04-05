package kaboom.logic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateAndTimeFormat {
	
	class TimeFormat {
		private Pattern timeRegexPattern;
		private String timeFormat;
		
		public TimeFormat (String regex, String format) {
			timeRegexPattern = Pattern.compile(regex);
			timeFormat = format;
		}
		
		public Pattern getRegex() {
			return timeRegexPattern;
		}
		public String timeFormat() {
			return timeFormat;
		}
	}
	
	private final String dateFormat1 = "ddMMyy";		// 12/06/12 or 12.01.06 or 120106
	private final String dateFormat2 = "dd'/'MM'/'yy";
	private final String dateFormat3 = "dd'.'MM'.'yy";
	private final String dateFormat4 = "ddMMyyyy";		// 12/06/12 or 12.01.06 or 120106
	private final String dateFormat5 = "dd'/'MM'/'yyyy";
	private final String dateFormat6 = "dd'.'MM'.'yyy";
	
	private final String time24hrFormatRegex1 = "0[0-9](am|pm)";			//hour 
	private final String time24hrFormatRegex2 = "1[0-2](am|pm)";			//10-12am/pm
	
	private final String time24hrFormat1 = "HHmm"; 
	private final String time24hrFormat2 = "HH':'mm";
	
	private final String time12hrFormatRegex1 = "[1-9](am|pm)";				//1-9am/pm
	private final String time12hrFormatRegex2 = "1[0-2](am|pm)";			//10-12am/pm
	private final String time12hrFormatRegex3 = "[1-9][0-5][0-9](am|pm)";	//1-9am/pm with mins
	private final String time12hrFormatRegex4 = "1[0-2][0-5][0-9](am|pm)";		//10-12am/pm with mins
	private final String time12hrFormatRegex5 = "[1-9]:[0-5][0-9](am|pm)";	//1-9am/pm with mins with :
	private final String time12hrFormatRegex6 = "1[0-2]:[0-5][0-9](am|pm)";		//10-12am/pm with mins with :
	
	private final String time12hrFormat1 = "ha";
	private final String time12hrFormat2 = "hha";
	private final String time12hrFormat3 = "hmma";
	private final String time12hrFormat4 = "hhmma";
	private final String time12hrFormat5 = "h':'mma";
	private final String time12hrFormat6 = "hh':'mma";
	
	private final SimpleDateFormat[] dateFormatList = { 
		new SimpleDateFormat(dateFormat1),
		new SimpleDateFormat(dateFormat2),
		new SimpleDateFormat(dateFormat3),
		new SimpleDateFormat(dateFormat4),
		new SimpleDateFormat(dateFormat5),
		new SimpleDateFormat(dateFormat6)
	};
	
	private final SimpleDateFormat[] time24HrFormatList = {
		new SimpleDateFormat(time24hrFormat1),
		new SimpleDateFormat(time24hrFormat2)
	};
	
	private final SimpleDateFormat[] time12HrFormatList = {
		new SimpleDateFormat(time12hrFormat1),
		new SimpleDateFormat(time12hrFormat2),
		new SimpleDateFormat(time12hrFormat3),
		new SimpleDateFormat(time12hrFormat4),
		new SimpleDateFormat(time12hrFormat5),
		new SimpleDateFormat(time12hrFormat6)
	};
	
	private final Pattern[] time12HrFormatListPair = {
			Pattern.compile(time12hrFormatRegex1),
			Pattern.compile(time12hrFormatRegex2),
			Pattern.compile(time12hrFormatRegex3),
			Pattern.compile(time12hrFormatRegex4),			
			Pattern.compile(time12hrFormatRegex5),
			Pattern.compile(time12hrFormatRegex6)
	};
	
	private final TimeFormat[] timeFormatList = {
			new TimeFormat(time12hrFormatRegex1,time12hrFormat1),
			new TimeFormat(time12hrFormatRegex2,time12hrFormat2),
			new TimeFormat(time12hrFormatRegex3,time12hrFormat3),
			new TimeFormat(time12hrFormatRegex4,time12hrFormat4),
			new TimeFormat(time12hrFormatRegex5,time12hrFormat5),		
			new TimeFormat(time12hrFormatRegex6,time12hrFormat6),
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

	public Calendar formatStringToCalendar2 (String date, String time) throws InvalidDateAndTimeException {
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
	
	
	/*
	 * If only date is valid: Set calendar to date and default time of 0000 (12am)
	 * If only time is valid: Set calendar to time and default date to current day
	 * If both are valid: Set calendar to respective date and time
	 * If both are null: return null;
	 * If date or time is invalid: throw exception
	 */
	public Calendar formatStringToCalendar (String date, String time) throws InvalidDateAndTimeException {
		if(date == null && time == null) {
			return null;
		}
		Calendar dateAndTime = Calendar.getInstance();
		
		boolean isDateValid = isDateValid(date);
		boolean isTimeValid = isTimeValid(time);
		
		if((!isDateValid && date != null) || (!isTimeValid && time != null)){
			System.out.println("Invalid");
			throw new InvalidDateAndTimeException("Invalid Date or Time");
		}
		
		if (isDateValid && isTimeValid) {
			dateAndTime = dateTranslator(dateAndTime, date);
			dateAndTime = timeTranslator(dateAndTime, time);
		} else if (isDateValid) {
			dateAndTime = dateTranslator(dateAndTime, date);
			dateAndTime = timeTranslator(dateAndTime, "0000");
		} else if (isTimeValid) {
			System.out.println("asd");
			dateAndTime = timeTranslator(dateAndTime, time);
		} 
	
		return dateAndTime;
	}

	public String convertStringTimeToCalendar(String date) {
		// Convert time to 2400hr format
		int hour = 0;
		int addedHour = 0;
		int minutes = 0;
		boolean ampmTiming = false;
		
		// Check if it contains am or pm
		if (date.contains("pm")) {
			hour += 12;
			ampmTiming = true;
		} else if (date.contains("am")) {
			ampmTiming = true;
		}
		
		// Remove all instance of am and pm and colon if possible
		date = date.replaceAll(":?(am|pm)?", "");
		
		// Extract time into hours and minutes
		if (date.length() > 2) {
			String minuteString = date.substring(date.length()-2, date.length());
			minutes += Integer.parseInt(minuteString);
			date =  date.replaceAll(minuteString, "");
			
			if (!date.equals("")) {
				addedHour = Integer.parseInt(date);
				
				// Checks for 12am and 12pm
				if (addedHour == 12 && ampmTiming) {
					hour -= 12;
				}
				hour += addedHour;
			}
		} else {
			hour += Integer.parseInt(date);
		}
		
		System.out.printf("Hour: %d:%02d\n", hour, minutes);
		
		return String.format("%02d%02d", hour, minutes);
	}
	
	public String dateFromCalendarToString (Calendar cal) {
		String day = String.format("%02d", cal.get(Calendar.DATE));
		String month = String.format("%02d", cal.get(Calendar.MONTH)+1);
		String year = String.format("%02d", cal.get(Calendar.YEAR)%100);
		String date = day+month+year;
		return date;
	}
	
	public String timeFromCalendarToString (Calendar cal) {
		String hour = String.format("%02d", cal.get(Calendar.HOUR_OF_DAY));
		String min = String.format("%02d", cal.get(Calendar.MINUTE));
		String time = hour+min;
		return time;
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
	
	private Calendar dateTranslator(Calendar thisDate, String theDate) throws InvalidDateAndTimeException{
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
		throw new InvalidDateAndTimeException("Invalid Date");
		//throw invalid date exception
//		return thisDate;
	}
	
	private Calendar timeTranslator(Calendar thisTime, String theTime) throws InvalidDateAndTimeException{
		if(theTime == null) {
			return thisTime;
		}
		theTime = theTime.toLowerCase();
		System.out.println("TP "+theTime);
		//12HOUR TEST
		if(theTime.contains("pm") || theTime.contains("am")) {
			String indicator12HourFormat = "";
			if(theTime.contains("pm")) {
				indicator12HourFormat = "pm";
				theTime = theTime.replace("pm", "").trim();
			}
			if(theTime.contains("am")) {
				indicator12HourFormat = "am";
				theTime = theTime.replace("am", "").trim();
			}
			theTime = theTime + indicator12HourFormat;
			//12hr format check
			for(int i = 0; i < time12HrFormatListPair.length; i++) {
				try {
					System.out.println("A");
					//validate time
					if(time12HrFormatListPair[i].matcher(theTime).matches()) {
						time12HrFormatList[i].setLenient(false);
						Date time = time12HrFormatList[i].parse(theTime);	
						//set time to thisTime
						Calendar getTime = Calendar.getInstance();
						getTime.setTime(time);
						thisTime.set(Calendar.HOUR, getTime.get(Calendar.HOUR));
						thisTime.set(Calendar.MINUTE, getTime.get(Calendar.MINUTE));
						thisTime.set(Calendar.AM_PM, getTime.get(Calendar.AM_PM));
						System.out.println(getTime.get(Calendar.HOUR));
						System.out.println(getTime.get(Calendar.MINUTE));
						System.out.println(getTime.get(Calendar.AM_PM));
						return thisTime;
					}
					System.out.println("B");
					
				} catch (Exception e) {	
				}
			}
		}
		else {
			if(!theTime.contains(":")) {
				theTime = String.format("%04d",Integer.parseInt(theTime));
				System.out.println("6 "+theTime);
			}
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

		//throw invalid time exception
//		return thisTime;
		throw new InvalidDateAndTimeException("Invalid Time");
	}
	
	
	//*************************** TEST METHODS **********************************
	//Date tests
	public String testDayFromDateTranslator (Calendar thisDate, String theDate) throws InvalidDateAndTimeException {
		dateTranslator (thisDate,theDate);	
		return Integer.toString(thisDate.get(Calendar.DAY_OF_MONTH));
	}
	public String testMonthFromDateTranslator (Calendar thisDate, String theDate) throws InvalidDateAndTimeException {
		dateTranslator (thisDate,theDate);	
		return Integer.toString(thisDate.get(Calendar.MONTH));
	}
	public String testYearFromDateTranslator (Calendar thisDate, String theDate) throws InvalidDateAndTimeException {
		dateTranslator (thisDate,theDate);	
		return Integer.toString(thisDate.get(Calendar.YEAR));
	}
	//Time tests
	public String testHourFromTimeTranslator (Calendar cal, String theTime) throws InvalidDateAndTimeException {
		timeTranslator (cal,theTime);	
		return Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
	}
	public String testMinFromTimeTranslator (Calendar cal, String theTime) throws InvalidDateAndTimeException {
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
		theTime = theTime.toLowerCase();
		if(theTime.contains("pm") || theTime.contains("am")) {
			String indicator12HourFormat = "";
			if(theTime.contains("pm")) {
				indicator12HourFormat = "pm";
				theTime = theTime.replace("pm", "").trim();
			}
			if(theTime.contains("am")) {
				indicator12HourFormat = "am";
				theTime = theTime.replace("am", "").trim();
			}
			theTime = theTime + indicator12HourFormat;
			int count = time12HrFormatList.length;
			//12hr format check
			for(int i = 0; i < time12HrFormatListPair.length; i++) {
				try {
					//validate time
					if(time12HrFormatListPair[i].matcher(theTime).matches()) {
						System.out.println("success");
						time12HrFormatList[i].setLenient(false);
						time12HrFormatList[i].parse(theTime);	
						count--;
					}
				} catch (Exception e) {	
				}
			}


			if(count != time12HrFormatList.length) { 
				return true;
			}
			else {
				return false;   
			}
		} else {
			return false;
		}
	}
	
	public boolean isTimeValid (String theTime) {
		if(theTime == null) {
			return false;
		}
		theTime = theTime.toLowerCase();
		if(theTime.contains("pm") || theTime.contains("am")) {
			String indicator12HourFormat = "";
			if(theTime.contains("pm")) {
				indicator12HourFormat = "pm";
				theTime = theTime.replace("pm", "").trim();
			}
			if(theTime.contains("am")) {
				indicator12HourFormat = "am";
				theTime = theTime.replace("am", "").trim();
			}
			theTime = theTime + indicator12HourFormat;
			int count = time12HrFormatList.length;
			//12hr format check
			for(int i = 0; i < time12HrFormatList.length; i++) {
				try {
					//validate time
					time12HrFormatList[i].setLenient(false);
					time12HrFormatList[i].parse(theTime);	
				} catch (Exception e) {	
					count--;
				}
			}
			if(count > 0) { 
				return true;
			}
			else {
				return false;   
			}
		} else {
			//24hr format
			if(!theTime.contains(":")) {
				theTime = String.format("%04d",Integer.parseInt(theTime));
			}
			//24hr format check
			int count = time24HrFormatList.length;
			for(int i = 0; i < time24HrFormatList.length; i++) {
				try {
					//validate time
					time24HrFormatList[i].setLenient(false);
					time24HrFormatList[i].parse(theTime);
				} catch (Exception e) {	
					count--;
				}
			}
			if(count > 0) { 
				return true;
			}
			else {
				return false;   
			}
		}
	}

	//*************************** TEST METHODS **********************************
	
	
	public boolean isToday (Calendar dateTime) {
		Calendar todayDateTime = Calendar.getInstance();
		if (isThisYear(dateTime) && todayDateTime.get(Calendar.DAY_OF_YEAR) == dateTime.get(Calendar.DAY_OF_YEAR)) {
			return true;
		}
		return false;
	}
	
	public boolean isThisWeek (Calendar dateTime) {
		Calendar todayDateTime = Calendar.getInstance();
		if (isThisYear(dateTime) && todayDateTime.get(Calendar.WEEK_OF_YEAR) == dateTime.get(Calendar.WEEK_OF_YEAR)) {
			return true;
		}
		return false;
	}
	
	public boolean isNextWeek (Calendar dateTime) {
		Calendar todayDateTime = Calendar.getInstance();
		
		int thisWeek = todayDateTime.get(Calendar.WEEK_OF_YEAR);
		int dateWeek = dateTime.get(Calendar.WEEK_OF_YEAR);
		if (isThisYear(dateTime) &&  (dateWeek-thisWeek == 1) ||  (thisWeek-dateWeek == 51)) {
			return true;
		}
		return false;
	}
	
	public boolean isThisYear (Calendar dateTime) {
		Calendar todayDateTime = Calendar.getInstance();
		if (todayDateTime.get(Calendar.YEAR) == dateTime.get(Calendar.YEAR)) {
			return true;
		}
		return false;
	}
	
	public boolean isSameDay (Calendar firstDateTime, Calendar secondDateTime) {
		if (firstDateTime.get(Calendar.YEAR) == secondDateTime.get(Calendar.YEAR) && 
			firstDateTime.get(Calendar.DAY_OF_YEAR) == secondDateTime.get(Calendar.DAY_OF_YEAR)) {
			return true;
		}
		
		return false;
	}
	
	public boolean testTimeRegex(String time) {
		for (int i=0;i<time12HrFormatListPair.length;i++){
			if(time12HrFormatListPair[i].matcher(time).matches()) {
				return true;
			}
		}
		return false;
	}
}
