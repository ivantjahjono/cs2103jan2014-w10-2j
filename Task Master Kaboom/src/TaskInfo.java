import java.util.Date; 

public class TaskInfo {
	public enum TASK_TYPE {
		DEADLINE, FLOATING, TIMED;
	}
	
	String taskname;
	TASK_TYPE taskType;
	
	Date startDate;		// This includes the time as well
	Date endDate;		// This includes the time as well
	
	int importanceLevel;
	
	public void SetTaskName (String name) { 
		taskname = name;
	}
	
	public void SetTaskType (TASK_TYPE type) { 
		taskType = type;
	}
	
	public void SetStartDate (Date date) { 
		startDate = date;
	}
	
	public void SetEndDate (Date date) { 
		endDate = date;
	}
	
	public void SetImportanceLevel (int level) { 
		importanceLevel = level;
	}
	
	public String GetTaskName () { 
		return taskname;
	}
	
	public TASK_TYPE GetTaskType () { 
		return taskType;
	}
	
	public Date SetStartDate () { 
		return startDate;
	}
	
	public Date GetEndDate () { 
		return endDate;
	}
}
