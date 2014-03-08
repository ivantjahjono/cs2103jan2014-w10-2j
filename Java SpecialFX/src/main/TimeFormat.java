package main;

public class TimeFormat {
	//the24HourFormatDefault; 1700, 1500	code=1
	//the24HourFormatColon; 17:20	code=2
	//theAMFormat; 5am, 1100am	code=3
	//theAMFormatColon; 5:00am	code=4
	//thePMFormat; 5pm, 1100pm	code=5
	//thePMFormatColon; 5:00pm	code=6
	int timeFormat;
	TimeFormat(){
		timeFormat = 0;
	}
	
	public void setTimeFormatCode(int currTimeFormat){
		timeFormat = currTimeFormat;
	}
	
	public int getTimeFormatCode(){
		return timeFormat;
	}
	
	
}
