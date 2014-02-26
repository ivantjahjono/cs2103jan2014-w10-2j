import java.util.Vector;

public class TaskListShop {

	private Vector<TaskInfo> taskList;
	
	TaskListShop () {
		taskList = new Vector<TaskInfo>();
	}
	
	public void addTaskToList (TaskInfo newTask) {
		taskList.add(newTask);
	}
	
	public TaskInfo getTaskByName (String taskName) {
		for (int i = 0; i < taskList.size(); i++) {
			if (taskName.equals(taskList.get(i))) {
				return taskList.get(i);
			}
		}
		return null;
	}
	
	public boolean removeTaskByName (String taskName) {
		TaskInfo currentTaskToRemove = getTaskByName(taskName);
		
		if (currentTaskToRemove != null) {
			taskList.remove(currentTaskToRemove);
			return true;
		}
		
		return false;
	}
	
}
