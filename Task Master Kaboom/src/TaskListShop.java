import java.util.Vector;

public class TaskListShop {

	private Vector<TaskInfo> taskList;
	private Vector<TaskInfo> searchList;
	
	private static TaskListShop taskListInstance = null;

	public static TaskListShop getInstance () {
		if (taskListInstance == null) {
			taskListInstance = new TaskListShop();
		}

		return taskListInstance;
	}

	private TaskListShop () {
		taskList = new Vector<TaskInfo>();
		searchList = new Vector<TaskInfo>();
	}

	public boolean addTaskToList (TaskInfo newTask) {
		if (taskList != null) {
			return taskList.add(newTask);
		} else {
			return false;
		}
	}

	public boolean addTaskToSearch (TaskInfo newTask) {
		if (taskList != null) {
			return searchList.add(newTask);
		} else {
			return false;
		}
	}
	
	public TaskInfo getTaskByName (String taskName) {
		for (int i = 0; i < taskList.size(); i++) {
			System.out.println(taskList.get(i).getTaskName());
			if (taskName.equals(taskList.get(i).getTaskName())) {
				return taskList.get(i);
			}
		}
		return null;
	}

	public void updateTask (TaskInfo newTaskInfo, TaskInfo prevTaskInfo) {
		int indexOfTaskListToBeModified = -1;
		for (int i = 0; i < taskList.size(); i++) {
			if (prevTaskInfo.equals(taskList.get(i))) {
				indexOfTaskListToBeModified = i;
			}
		}

		if (indexOfTaskListToBeModified != -1) {
			taskList.set(indexOfTaskListToBeModified, newTaskInfo);
		}
	}

	public Vector<TaskInfo> getAllTaskInList () {
		Vector<TaskInfo> vectorToReturn = new Vector<TaskInfo>(taskList);
		return vectorToReturn;
	}
	
	public Vector<TaskInfo> getSearchList() {
		Vector<TaskInfo> vectorToReturn = new Vector<TaskInfo>(searchList);
		searchList.clear();  //Clear the search list each time the search list is returned
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
