//@author A0099175N

package kaboom.ui;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import kaboom.shared.DateAndTimeFormat;
import kaboom.shared.TASK_TYPE;
import kaboom.shared.TaskInfo;

public class TaskInfoDisplay {
	private int taskId;
	private String taskName;
	
	private String startDate;
	private String endDate;
	
	private String importanceLevel;
	
	private boolean isExpired;
	private boolean isDone;
	private boolean isRecent;
	
	private DateAndTimeFormat dateTimeFormat;
	
	SimpleDateFormat fullTimeFormat = new SimpleDateFormat("h:mma");
	SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd MMM yy");
	SimpleDateFormat dayMonthFormat = new SimpleDateFormat("dd MMM");
	SimpleDateFormat dayOnlyFormat = new SimpleDateFormat("EEE");
	
	public TaskInfoDisplay () {
		taskId 			= 0;
		taskName 		= "No taskname available";
		startDate 		= "-";
		endDate 		= "-";
		importanceLevel = "";
		isExpired 	= false;
		isDone 		= false;
		isRecent 	= false;
		
		dateTimeFormat = DateAndTimeFormat.getInstance();
	}
	
	public void updateFromThisInfo (TaskInfo infoToUpdateFrom) {
		setTaskName(infoToUpdateFrom.getTaskName());
		
		TASK_TYPE currentTaskType = infoToUpdateFrom.getTaskType();
		if (currentTaskType== TASK_TYPE.TIMED) {
			updateTimeFormatDisplayForTimedTasks(infoToUpdateFrom);
		} else if (currentTaskType == TASK_TYPE.DEADLINE) {
			updateTimeFormatDisplayForDeadlineTasks(infoToUpdateFrom);
		}
		
		setImportanceLevel(infoToUpdateFrom.getPriority());
		
		setExpiryFlag(infoToUpdateFrom.getExpiryFlag());
		setDoneFlag(infoToUpdateFrom.getDone());
		setRecentFlag(infoToUpdateFrom.isRecent());
	}

	private void updateTimeFormatDisplayForDeadlineTasks(TaskInfo infoToUpdateFrom) {
		Calendar dueTime = infoToUpdateFrom.getEndDate();
		String dueTimeToDisplay = "Due by " + convertDateTimeFormatBasedOnTime(dueTime);
		startDate = dueTimeToDisplay;
	}

	private void updateTimeFormatDisplayForTimedTasks(TaskInfo infoToUpdateFrom) {
		setStartTime(infoToUpdateFrom.getStartDate());
		
		Calendar startTime 	= infoToUpdateFrom.getStartDate();
		Calendar endTime 	= infoToUpdateFrom.getEndDate();

		String startToDisplay = "From " + convertDateTimeFormatBasedOnTime(startTime);
		String endToDisplay = " to " + convertDateTimeFormatBasedOnTime(endTime);
		
		if (dateTimeFormat.isSameDay(startTime, endTime)) {
			int index = startToDisplay.indexOf(",");
			if (index > 0) {
				startToDisplay = startToDisplay.substring(0, index);
			}
		}
		
		startDate = startToDisplay+endToDisplay;
	}
	
	private String convertDateTimeFormatBasedOnTime(Calendar timeDate) {
		if (dateTimeFormat.isToday(timeDate)) {
			return fullTimeFormat.format(timeDate.getTime()) + ", Today";
		} else if (dateTimeFormat.isThisWeek(timeDate)) {
			return fullTimeFormat.format(timeDate.getTime()) + ", " + dayOnlyFormat.format(timeDate.getTime());
		} else if (dateTimeFormat.isThisYear(timeDate)) {
			return fullTimeFormat.format(timeDate.getTime()) + ", " + dayMonthFormat.format(timeDate.getTime());
		} else {
			return fullTimeFormat.format(timeDate.getTime()) + ", " + fullDateFormat.format(timeDate.getTime());
		}
	}

	public void setTaskId (int id) {
		taskId = id;
	}
	
	public void setTaskName (String name) {
		taskName = name;
	}
	
	public void setStartTime (Calendar time) {
		if (time != null) {
			SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
			startDate = timeFormat.format(time.getTime()) + "\t" + dateFormat.format(time.getTime());
		}
	}
	
	public void setEndTime (Calendar time) {
		if (time != null) {
			SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
			endDate = timeFormat.format(time.getTime()) + "\t" + dateFormat.format(time.getTime());
		}
	}
	
	public void setImportanceLevel (int level) {
		if (level == 0) {
			importanceLevel = "";
		} else {
			for (int i = 0; i < level; i++) {
				importanceLevel = importanceLevel + "*";
			}
		}
	}
	
	public void setExpiryFlag (boolean flag) {
		isExpired = flag;
	}
	
	public void setDoneFlag (boolean flag) {
		isDone = flag;
	}
	
	public void setRecentFlag (boolean flag) {
		isRecent = flag;
	}
	
	public int getTaskId () {
		return taskId;
	}
	
	public String getTaskName () {
		return taskName;
	}
	
	public String getStartDate () {
		return startDate;
	}
	
	public String getEndDate () {
		return endDate;
	}
	
	public String getImportanceLevel () {
		return importanceLevel;
	}
	
	public boolean isExpired () {
		return isExpired;
	}
	
	public boolean isDone () {
		return isDone;
	}
	
	public boolean isRecent () {
		return isRecent;
	}
}

