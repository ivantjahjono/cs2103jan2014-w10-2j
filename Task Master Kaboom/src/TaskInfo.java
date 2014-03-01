import java.util.Date; 
enum TASK_TYPE {
	DEADLINE, FLOATING, TIMED;
}
public class TaskInfo {
	
	
	String taskname;
	TASK_TYPE taskType;
	
	Date startDate;		// This includes the time as well
	Date endDate;		// This includes the time as well
	
	int importanceLevel;
	
	public void setTaskName (String name) { 
		taskname = name;
	}
	
	public void setTaskType (TASK_TYPE type) { 
		taskType = type;
	}
	
	public void setStartDate (Date date) { 
		startDate = date;
	}
	
	public void setEndDate (Date date) { 
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
	
	public Date setStartDate () { 
		return startDate;
	}
	
	public Date getEndDate () { 
		return endDate;
	}
}
