import java.util.Vector;

public class TaskListShop {

	private Vector<TaskInfo> taskList;
	
	private static TaskListShop taskListInstance = null;
	
	public static TaskListShop getInstance () {
		if (taskListInstance == null) {
			taskListInstance = new TaskListShop();
		}
		
		return taskListInstance;
	}
	
	private TaskListShop () {
		taskList = new Vector<TaskInfo>();
	}
	
	public boolean addTaskToList (TaskInfo newTask) {
		return taskList.add(newTask);
	}
	
	public TaskInfo getTaskByName (String taskName) {
		for (int i = 0; i < taskList.size(); i++) {
			if (taskName.equals(taskList.get(i))) {
				return taskList.get(i);
			}
		}
		return null;
	}
	
	public Vector<TaskInfo> getAllTaskInList () {
		Vector<TaskInfo> vectorToReturn = new Vector<TaskInfo>(taskList);
		return vectorToReturn;
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
