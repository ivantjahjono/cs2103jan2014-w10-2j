package kaboom.logic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateAndTimeFormat {
	
	private final String dateFormat1 = "ddMMyy";		// 12/06/12 or 12.01.06 or 120106
	private final String dateFormat2 = "dd'/'MM'/'yy";
	private final String dateFormat3 = "dd'.'MM'.'yy";
	private final String dateFormat4 = "ddMMyyyy";		// 12/06/12 or 12.01.06 or 120106
	private final String dateFormat5 = "dd'/'MM'/'yyyy";
	private final String dateFormat6 = "dd'.'MM'.'yyyy";
	
	private final String time24hrFormat1 = "HHmm"; 
	private final String time24hrFormat2 = "HH':'mm";

	private final SimpleDateFormat[] dateFormatList = { 
		new SimpleDateFormat(dateFormat1),
		new SimpleDateFormat(dateFormat2),
		new SimpleDateFormat(dateFormat3),
		new SimpleDateFormat(dateFormat4),
		new SimpleDateFormat(dateFormat5),
		new SimpleDateFormat(dateFormat6)
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

		
	/* (MOVING TO CONTROLLER)
	 * If only date is valid: Set calendar to date and default time of 0000 (12am)
	 * If only time is valid: Set calendar to time and default date to current day
	 * If both are valid: Set calendar to respective date and time
	 * If both are null: return null;
	 */
	
	public Calendar formatStringToCalendar (String date, String time) {
		if(date == null || time == null) {
			return null;
		}
		Calendar dateAndTime = Calendar.getInstance();
		
		convertStringDateToCalendar(dateAndTime, date);
		convertStringTimeToCalendar(dateAndTime,time);

		return dateAndTime;
	}
	
	public void convertStringDateToCalendar(Calendar cal, String date) {		
		for(int i = 0; i < dateFormatList.length; i++) {
			try {
				//validate date
				dateFormatList[i].setLenient(false);
				Date dateType = dateFormatList[i].parse(date);
				//set date to thisDate
				Calendar getDate = Calendar.getInstance();
				getDate.setTime(dateType);
				cal.set(Calendar.DAY_OF_MONTH, getDate.get(Calendar.DAY_OF_MONTH));
				cal.set(Calendar.MONTH, getDate.get(Calendar.MONTH));
				cal.set(Calendar.YEAR, getDate.get(Calendar.YEAR));
			} catch (Exception e) {	
			}
		}
	}
	
	public void convertStringTimeToCalendar(Calendar cal, String time) {	
		String minutesString = time.substring(time.length()-2, time.length());
		String hourString = time.replaceFirst(minutesString, "");
		int minutes = Integer.parseInt(minutesString);
		int hour = Integer.parseInt(hourString);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minutes);
	}
	
	public String convertStringTimeTo24HourString(String date) {
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
		
		return String.format("%02d%02d", hour, minutes);
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
	
	public String dateFromCalendarToString (Calendar cal) {
		String day = String.format("%02d", cal.get(Calendar.DATE));
		String month = String.format("%02d", cal.get(Calendar.MONTH)+1);
		String year = String.format("%02d", cal.get(Calendar.YEAR));
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
	
	public Calendar addDayToCalendar (Calendar dateAndTime, int day) {
		Calendar dateAndTimeToAdd = (Calendar) dateAndTime.clone();
		dateAndTimeToAdd.add(Calendar.DATE, day);
		return dateAndTimeToAdd;
	}
	
	public Calendar addMonthToCalendar (Calendar dateAndTime, int month) {
		Calendar dateAndTimeToAdd = (Calendar) dateAndTime.clone();
		dateAndTimeToAdd.add(Calendar.MONTH, month);
		return dateAndTimeToAdd;
	}
	
	public boolean isFirstDateBeforeSecondDate (Calendar firstDate, Calendar secondDate) {
		if (firstDate == null || secondDate == null) {
			return false;
		}
		if (firstDate.before(secondDate)) {
			return true;
		}
		return false;
	}
	

	public String getTodayDate () {
		Calendar todayCal = Calendar.getInstance();
		String todayString = "";
		String day = String.format("%02d",todayCal.get(Calendar.DATE));
		String month = String.format("%02d",todayCal.get(Calendar.MONTH) + 1);
		String year = Integer.toString(todayCal.get(Calendar.YEAR));
		todayString = day + month + year;
		return todayString;
	}
	
	public String getCurrentTime () {
		Calendar todayCal = Calendar.getInstance();
		String time = "";
		String hour = String.format("%02d",todayCal.get(Calendar.HOUR_OF_DAY));
		String mins = String.format("%02d",todayCal.get(Calendar.MINUTE));
		time = hour + mins;
		return time;
	}
	
	public String getNextDay (Calendar cal) {
		String todayString = "";
		String day = String.format("%02d",cal.get(Calendar.DATE)+1);
		String month = String.format("%02d",cal.get(Calendar.MONTH) + 1);
		String year = Integer.toString(cal.get(Calendar.YEAR));
		todayString = day + month + year;
		return todayString;
	}

	
	
	//*************************** TEST METHODS **********************************
	//Date tests
//	public String testDayFromDateTranslator (Calendar thisDate, String theDate) throws InvalidDateAndTimeException {
//		dateTranslator (thisDate,theDate);	
//		return Integer.toString(thisDate.get(Calendar.DAY_OF_MONTH));
//	}
//	public String testMonthFromDateTranslator (Calendar thisDate, String theDate) throws InvalidDateAndTimeException {
//		dateTranslator (thisDate,theDate);	
//		return Integer.toString(thisDate.get(Calendar.MONTH));
//	}
//	public String testYearFromDateTranslator (Calendar thisDate, String theDate) throws InvalidDateAndTimeException {
//		dateTranslator (thisDate,theDate);	
//		return Integer.toString(thisDate.get(Calendar.YEAR));
//	}
//	//Time tests
//	public String testHourFromTimeTranslator (Calendar cal, String theTime) throws InvalidDateAndTimeException {
//		timeTranslator (cal,theTime);	
//		return Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
//	}
//	public String testMinFromTimeTranslator (Calendar cal, String theTime) throws InvalidDateAndTimeException {
//		timeTranslator (cal,theTime);	
//		return Integer.toString(cal.get(Calendar.MINUTE));
//	}



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
	
	public Calendar getCurrentDateAndTime () {
		return Calendar.getInstance();
	}
	
	public String getCurrentWeekday () {
		Calendar dateTime = Calendar.getInstance();
		
		String weekFormatString = "EEE";
		SimpleDateFormat weekFormat = new SimpleDateFormat(weekFormatString);
		
		return weekFormat.format(dateTime.getTime());
	}
	
	public String getDateToday () {
		Calendar dateTime = Calendar.getInstance();
		
		String dayFormatString = "dd MMM yy";
		SimpleDateFormat dayFormat = new SimpleDateFormat(dayFormatString);
		
		return dayFormat.format(dateTime.getTime());
	}
	
	public String getTimeNow () {
		Calendar dateTime = Calendar.getInstance();
		
		String timeFormatString = "hh:mm a";
		SimpleDateFormat timeFormat = new SimpleDateFormat(timeFormatString);
		
		return timeFormat.format(dateTime.getTime());
	}
}
