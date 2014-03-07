import java.util.Calendar; 

enum TASK_TYPE {
	DEADLINE, FLOATING, TIMED;
}

public class TaskInfo {
	
	String taskname;
	TASK_TYPE taskType;
	
	Calendar startDate;		// This includes the time as well
	Calendar endDate;		// This includes the time as well
	
	int importanceLevel;
	
	boolean isExpired;
	
	TaskInfo () {
		taskname = "";
		startDate = null;
		endDate = null;
		importanceLevel = 0;
		isExpired = false;
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
	
	public void setExpiryFlag (boolean flag) { 
		isExpired = flag;
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
	
	public int getImportanceLevel () { 
		return importanceLevel;
	}
	
	public boolean setExpiryFlag () { 
		return isExpired;
	}
	
	public static TASK_TYPE getTaskType(String taskType) {
		if (taskType.equalsIgnoreCase("DEADLINE")) {
			return TASK_TYPE.DEADLINE;
		}
		else if (taskType.equalsIgnoreCase("FLOATING")) {
			return TASK_TYPE.FLOATING;
		}
		else {
			return TASK_TYPE.TIMED;
		}
	}
	
	public static String taskTypeToString(TASK_TYPE taskType) {
		switch (taskType) {
		case DEADLINE:
			return "DEADLINE";
		case FLOATING:
			return "FLOATING";
		case TIMED:
			return "TIMED";
		default:
			return "Unrecognized";  //This should never be reached
		}
	}
}
