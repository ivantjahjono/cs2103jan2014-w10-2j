package kaboom.logic;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import kaboom.shared.DateAndTimeFormat;
import kaboom.shared.TASK_TYPE;
import kaboom.shared.TaskInfo;

public class TaskInfoDisplay {
	// TODO clean up magic strings
	
	private SimpleIntegerProperty taskId;
	private SimpleStringProperty taskName;
	
	private SimpleStringProperty startDate;
	private SimpleStringProperty endDate;
	
	private SimpleStringProperty importanceLevel;
	
	private SimpleBooleanProperty isExpired;
	private SimpleBooleanProperty isDone;
	private SimpleBooleanProperty isRecent;
	
	private DateAndTimeFormat dateTimeFormat;
	
	SimpleDateFormat fullTimeFormat = new SimpleDateFormat("h:mma");
	SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd MMM yy");
	SimpleDateFormat dayMonthFormat = new SimpleDateFormat("dd MMM");
	SimpleDateFormat dayOnlyFormat = new SimpleDateFormat("EEE");
	
	public TaskInfoDisplay () {
		taskId = new SimpleIntegerProperty(0);
		taskName = new SimpleStringProperty("No taskname available");;
		startDate = new SimpleStringProperty("-");
		endDate = new SimpleStringProperty("-");
		importanceLevel = new SimpleStringProperty("");
		isExpired = new SimpleBooleanProperty(false);
		isDone = new SimpleBooleanProperty(false);
		isRecent = new SimpleBooleanProperty(false);
		
		dateTimeFormat = DateAndTimeFormat.getInstance();
	}
	
	public void updateFromThisInfo (TaskInfo infoToUpdateFrom) {
		setTaskName(infoToUpdateFrom.getTaskName());
		
		TASK_TYPE currentTaskType = infoToUpdateFrom.getTaskType();
		if (currentTaskType== TASK_TYPE.TIMED) {
			// Set time from ?? to ??
			setStartTime(infoToUpdateFrom.getStartDate());
			
			Calendar startTime = infoToUpdateFrom.getStartDate();
			Calendar endTime = infoToUpdateFrom.getEndDate();

			String startToDisplay = "From " + convertDateTimeFormatBasedOnTime(startTime);
			String endToDisplay = " to " + convertDateTimeFormatBasedOnTime(endTime);
			
			if (dateTimeFormat.isSameDay(startTime, endTime)) {
				// Remove the first day declaration
				int index = startToDisplay.indexOf(",");
				if (index > 0) {
					startToDisplay = startToDisplay.substring(0, index);
				}
			}
			
			startDate.set(startToDisplay+endToDisplay);
		} else if (currentTaskType == TASK_TYPE.DEADLINE) {
			// Due by ??
			
			Calendar dueTime = infoToUpdateFrom.getEndDate();
			String dueTimeToDisplay = "Due by " + convertDateTimeFormatBasedOnTime(dueTime);
			startDate.set(dueTimeToDisplay);
		}
		
		setImportanceLevel(infoToUpdateFrom.getPriority());
		
		setExpiryFlag(infoToUpdateFrom.getExpiryFlag());
		setDoneFlag(infoToUpdateFrom.getDone());
		setRecentFlag(infoToUpdateFrom.isRecent());
	}
	
	private String convertDateTimeFormatBasedOnTime(Calendar timeDate) {
		if (dateTimeFormat.isToday(timeDate)) {
			return fullTimeFormat.format(timeDate.getTime());
		} else if (dateTimeFormat.isThisWeek(timeDate)) {
			return fullTimeFormat.format(timeDate.getTime()) +  ", " + dayOnlyFormat.format(timeDate.getTime());
		} else if (dateTimeFormat.isThisYear(timeDate)) {
			return fullTimeFormat.format(timeDate.getTime()) + ", " +
					dayMonthFormat.format(timeDate.getTime())  +
					" (" + dayOnlyFormat.format(timeDate.getTime()) + ")"; 
		} else {
			return fullTimeFormat.format(timeDate.getTime()) + ", " + 
					fullDateFormat.format(timeDate.getTime()) +
					" (" + dayOnlyFormat.format(timeDate.getTime()) + ")";
		}
	}

	public void setTaskId (int id) {
		taskId.set(id);
	}
	
	public void setTaskName (String name) {
		taskName.set(name);
	}
	
	public void setStartTime (Calendar time) {
		if (time != null) {
			SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
			startDate.set(timeFormat.format(time.getTime()) + "\t" + dateFormat.format(time.getTime()));
		}
	}
	
	public void setEndTime (Calendar time) {
		if (time != null) {
			SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
			endDate.set(timeFormat.format(time.getTime()) + "\t" + dateFormat.format(time.getTime()));
		}
	}
	
	public void setImportanceLevel (int level) {
		if (level == 0) {
			importanceLevel.set("");
		} else {
			for (int i = 0; i < level; i++) {
				importanceLevel.set(importanceLevel.getValue() + "*");
			}
		}
	}
	
	public void setExpiryFlag (boolean flag) {
		isExpired.set(flag);
	}
	
	public void setDoneFlag (boolean flag) {
		isDone.set(flag);
	}
	
	public void setRecentFlag (boolean flag) {
		isRecent.set(flag);
	}
	
	public int getTaskId () {
		return taskId.get();
	}
	
	public String getTaskName () {
		return taskName.get();
	}
	
	public String getStartDate () {
		return startDate.get();
	}
	
	public String getEndDate () {
		return endDate.get();
	}
	
	public String getImportanceLevel () {
		return importanceLevel.get();
	}
	
	public boolean isExpired () {
		return isExpired.get();
	}
	
	public boolean isDone () {
		return isDone.get();
	}
	
	public boolean isRecent () {
		return isRecent.get();
	}
}

