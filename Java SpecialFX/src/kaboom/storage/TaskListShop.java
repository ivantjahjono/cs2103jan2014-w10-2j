package kaboom.storage;

import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Logger;

import kaboom.logic.TaskInfo;
import kaboom.logic.TASK_TYPE;

public class TaskListShop {

	private static TaskListShop taskListInstance = null;
	private static final Logger logger = Logger.getLogger("TaskListShopLogger");

	private Vector<TaskInfo> currentTaskList;
	private Vector<TaskInfo> archivedTaskList;

	public static TaskListShop getInstance () {
		if (taskListInstance == null) {
			taskListInstance = new TaskListShop();
			logger.info("New singleton TaskListShop instance created");
		}

		return taskListInstance;
	}

	private TaskListShop () {
		currentTaskList = new Vector<TaskInfo>();
		archivedTaskList = new Vector<TaskInfo>();
	}

	public boolean addTaskToList (TaskInfo newTask) {
		if (currentTaskList != null) {
			logger.info("Adding one item to TaskListShop");
			return currentTaskList.add(newTask);
		} else {
			return false;
		}
	}
	
	public boolean addTaskToArchivedList (TaskInfo newTask) {
		if (archivedTaskList != null) {
			logger.info("Adding one item to TaskListShop");
			return archivedTaskList.add(newTask);
		} else {
			return false;
		}
	}

	public TaskInfo getTaskByName (String taskName) {
		for (int i = currentTaskList.size()-1; i >= 0; i--) {
			//System.out.println(taskList.get(i).getTaskName());
			if (taskName.equals(currentTaskList.get(i).getTaskName())) {
				return currentTaskList.get(i);
			}
		}
		return null;
	}

	public void updateTask (TaskInfo newTaskInfo, TaskInfo prevTaskInfo) {
		int indexOfTaskListToBeModified = -1;
		for (int i = 0; i < currentTaskList.size(); i++) {
			if (prevTaskInfo.equals(currentTaskList.get(i))) {
				indexOfTaskListToBeModified = i;
				//System.out.println("index="+indexOfTaskListToBeModified);
			}
		}

		if (indexOfTaskListToBeModified != -1) {
			currentTaskList.set(indexOfTaskListToBeModified, newTaskInfo);
		}
	}

	public Vector<TaskInfo> getAllCurrentTasks () {
		Vector<TaskInfo> vectorToReturn = new Vector<TaskInfo>(currentTaskList);
		return vectorToReturn;
	}
	
	public Vector<TaskInfo> getAllArchivedTasks () {
		Vector<TaskInfo> vectorToReturn = new Vector<TaskInfo>(archivedTaskList);
		return vectorToReturn;
	}

	public Vector<TaskInfo> getFloatingTasks() {
		Vector<TaskInfo> returnVector = new Vector<TaskInfo>();

		for (int i = 0; i < currentTaskList.size(); i++) {
			TaskInfo singleTask = currentTaskList.get(i);
			if (singleTask.getTaskType() == TASK_TYPE.FLOATING) {
				returnVector.add(singleTask);
			}
		}
		return returnVector;
	}

	public Vector<TaskInfo> getDeadlineTasks() {
		Vector<TaskInfo> returnVector = new Vector<TaskInfo>();

		for (int i = 0; i < currentTaskList.size(); i++) {
			TaskInfo singleTask = currentTaskList.get(i);
			if (singleTask.getTaskType() == TASK_TYPE.DEADLINE) {
				returnVector.add(singleTask);
			}
		}
		return returnVector;
	}

	public Vector<TaskInfo> getTimedTasks() {
		Vector<TaskInfo> returnVector = new Vector<TaskInfo>();

		for (int i = 0; i < currentTaskList.size(); i++) {
			TaskInfo singleTask = currentTaskList.get(i);
			if (singleTask.getTaskType() == TASK_TYPE.TIMED) {
				returnVector.add(singleTask);
			}
		}
		return returnVector;
	}

	public Vector<TaskInfo> getExpiredTasks() {
		Vector<TaskInfo> returnVector = new Vector<TaskInfo>();

		for (int i = 0; i < currentTaskList.size(); i++) {
			TaskInfo singleTask = currentTaskList.get(i);
			boolean isExpired = singleTask.getExpiryFlag();
			if (isExpired) {
				returnVector.add(singleTask);
			}
		}
		return returnVector;
	}

	public Vector<TaskInfo> getNonExpiredTasks() {
		Vector<TaskInfo> returnVector = new Vector<TaskInfo>();

		for (int i = 0; i < currentTaskList.size(); i++) {
			TaskInfo singleTask = currentTaskList.get(i);
			boolean isExpired = singleTask.getExpiryFlag();
			if (!isExpired) {
				returnVector.add(singleTask);
			}
		}
		return returnVector;
	}

	public boolean removeTaskByName (String taskName) {
		TaskInfo currentTaskToRemove = getTaskByName(taskName);

		if (currentTaskToRemove != null) {
			currentTaskList.remove(currentTaskToRemove);
			logger.info("One task " + taskName + " removed");
			return true;
		}

		return false;
	}

	//This function refreshes all the tasks in the vector to check
	//whether it has expired and set to true if it has expired.
	//Sets to false if the task has not expired
	//Floating tasks have a default of not expired
	public void refreshTasks() {
		for (int i = 0; i < currentTaskList.size(); i++) {
			TaskInfo singleTask = currentTaskList.get(i);
			Calendar now = Calendar.getInstance();
			if (now.after(singleTask.getEndDate())) {
				if (!singleTask.getTaskType().equals(TASK_TYPE.FLOATING)) {
					singleTask.setExpiryFlag(true);  //Floating tasks cannot expire
				}
			}
			else {
				if (!singleTask.getTaskType().equals(TASK_TYPE.FLOATING)) {
					singleTask.setExpiryFlag(false);
				}
			}
		}
	}

	public Vector<TaskInfo> clearAllTasks () {
		currentTaskList = new Vector<TaskInfo>();
		Vector<TaskInfo> vectorToReturn = new Vector<TaskInfo>(currentTaskList);
		logger.info("All tasks cleared");
		return vectorToReturn;
	}
	
	public Vector<Integer> getCorrespondingID(Vector<TaskInfo> taskList) {
		Vector<Integer> taskID = new Vector<Integer>();
		for (int i = 0; i < taskList.size(); i++) {
			taskID.add(currentTaskList.indexOf(taskList.get(i)));
		}
		return taskID;
		//Do you want to return null instead if the list is empty?
	}

	public int shopSize () {
		return currentTaskList.size();
	}
}
