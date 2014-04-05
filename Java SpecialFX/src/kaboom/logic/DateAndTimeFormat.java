package kaboom.logic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateAndTimeFormat {
	
	private final String dateFormat1 = "ddMMyy";		// 12/06/12 or 12.01.06 or 120106
	private final String dateFormat2 = "dd'/'MM'/'yy";
	private final String dateFormat3 = "dd'.'MM'.'yy";
	
	private final String time24hrFormat1 = "HHmm"; 
	private final String time24hrFormat2 = "HH':'mm";
	private final String time12hrFormat1 = "hhmma";
	private final String time12hrFormat2 = "hhmm a";
	private final String time12hrFormat3 = "h':'mma";
	private final String time12hrFormat4 = "h':'mm a";
	
	private final SimpleDateFormat[] dateFormatList = { 
		new SimpleDateFormat(dateFormat1),
		new SimpleDateFormat(dateFormat2),
		new SimpleDateFormat(dateFormat3)
	};
	
	private final SimpleDateFormat[] time24HrFormatList = {
		new SimpleDateFormat(time24hrFormat1),
		new SimpleDateFormat(time24hrFormat2)
	};
	
	private final SimpleDateFormat[] time12HrFormatList = {
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

	public Calendar formatStringToCalendar (String date, String time) throws InvalidDateAndTimeException {
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
		System.out.println(theTime);
		if(theTime.contains("pm") || theTime.contains("am")) {
			String indicator12HourFormat = "";
			if(theTime.contains("pm")) {
				indicator12HourFormat = "pm";
				theTime = theTime.replace("pm", "").trim();
				System.out.println("1 "+theTime);
			}
			if(theTime.contains("am")) {
				indicator12HourFormat = "am";
				theTime = theTime.replace("am", "").trim();
				System.out.println("1.1 "+theTime);
			}
			
			if(!theTime.contains(":")) {
				theTime = String.format("%04d",Integer.parseInt(theTime));
				System.out.println("2 "+theTime);
			}
			
			theTime = theTime + indicator12HourFormat;
			System.out.println("5 "+theTime);
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
			
			if(!theTime.contains(":")) {
				theTime = String.format("%04d",Integer.parseInt(theTime));
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
			if(count > 0) return true;
			else return false;   
		}	
		return false;
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
			
			if(!theTime.contains(":")) {
				theTime = String.format("%04d",Integer.parseInt(theTime));
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
}
