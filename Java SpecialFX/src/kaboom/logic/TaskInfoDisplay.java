package kaboom.logic;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TaskInfoDisplay {
	private SimpleIntegerProperty taskId;
	private SimpleStringProperty taskName;
	
	private SimpleStringProperty startDate;
	private SimpleStringProperty endDate;
	
	private SimpleStringProperty importanceLevel;
	private SimpleBooleanProperty isExpired;
	private SimpleBooleanProperty isDone;
	
	public TaskInfoDisplay () {
		taskId = new SimpleIntegerProperty(0);
		taskName = new SimpleStringProperty("No taskname available");;
		startDate = new SimpleStringProperty("-");
		endDate = new SimpleStringProperty("-");
		importanceLevel = new SimpleStringProperty("");
		isExpired = new SimpleBooleanProperty(false);
		isDone = new SimpleBooleanProperty(false);
	}
	
	public void updateFromThisInfo (TaskInfo infoToUpdateFrom) {
		setTaskName(infoToUpdateFrom.getTaskName());
		
		if (infoToUpdateFrom.getTaskType() == TASK_TYPE.TIMED) {
			setStartTime(infoToUpdateFrom.getStartDate());
		}
		
		if (infoToUpdateFrom.getTaskType() != TASK_TYPE.FLOATING) {
			setEndTime(infoToUpdateFrom.getEndDate());
		}
		
		setImportanceLevel(infoToUpdateFrom.getImportanceLevel());
		
		setExpiryFlag(infoToUpdateFrom.getExpiryFlag());
		setDoneFlag(infoToUpdateFrom.isDone());
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
}

