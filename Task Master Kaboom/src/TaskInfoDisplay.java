import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TaskInfoDisplay {
	int taskId;
	String taskname;
	
	String startDate;
	String endDate;
	
	String importanceLevel;
	
	TaskInfoDisplay () {
		taskId = 0;
		taskname = "";
		startDate = "";
		endDate = "";
		importanceLevel = "";
	}
	
	public void updateFromThisInfo (TaskInfo infoToUpdateFrom) {
		setTaskName(infoToUpdateFrom.getTaskName());
		setStartTime(infoToUpdateFrom.getStartDate());
		setEndTime(infoToUpdateFrom.getEndDate());
		setImportanceLevel(infoToUpdateFrom.getImportanceLevel());
	}
	
	public void setTaskId (int id) {
		taskId = id;
	}
	
	public void setTaskName (String name) {
		taskname = name;
	}
	
	public void setStartTime (Calendar time) {
		if (time == null) {
			startDate = "";
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("h:mm a dd/MM/yy");
			startDate = sdf.format(time.getTime());
		}
	}
	
	public void setEndTime (Calendar time) {
		if (time == null) {
			endDate = "";
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("h:mm a dd/MM/yy");
			endDate = sdf.format(time.getTime());
		}
	}
	
	public void setImportanceLevel (int level) {
		if (level == 0) {
			importanceLevel = "";
		} else {
			for (int i = 0; i < level; i++) {
				importanceLevel += "*";
			}
		}
	}
	
	public int getTaskId () {
		return taskId;
	}
	
	public String getTaskName () {
		return taskname;
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
}
