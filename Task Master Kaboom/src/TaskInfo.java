import java.util.Calendar;
import java.util.Date; 

public class TaskInfo {
	public enum TASK_TYPE {
		DEADLINE, FLOATING, TIMED;
	}
	
	String taskname;
	TASK_TYPE taskType;
	
	Calendar startDate;		// This includes the time as well
	Calendar endDate;		// This includes the time as well
	
	int importanceLevel;
	
	TaskInfo () {
		taskname = "";
		startDate = null;
		endDate = null;
		importanceLevel = 0;
	}
	
	public void setTaskName (String name) { 
		taskname = name;
	}
	
	public void setTaskType (TASK_TYPE type) { 
		taskType = type;
	}
	
	public void setStartDate (Calendar date) { 
		startDate = date;
	}
	
	public void setEndDate (Calendar date) { 
		endDate = date;
	}
	
	public void setImportanceLevel (int level) { 
		importanceLevel = level;
	}
	
	public String getTaskName () { 
		return taskname;
	}
	
	public TASK_TYPE getTaskType () { 
		return taskType;
	}
	
	public Calendar getStartDate () { 
		return startDate;
	}
	
	public Calendar getEndDate () { 
		return endDate;
	}
}
