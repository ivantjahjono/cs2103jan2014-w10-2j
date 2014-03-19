package kaboom.storage;

import java.util.Vector;
import java.util.logging.Logger;

import kaboom.logic.TaskInfo;
import kaboom.logic.TASK_TYPE;

public class TaskListShop {

	private static TaskListShop taskListInstance = null;
	private static final Logger logger = Logger.getLogger("TaskListShopLogger");
	
	private Vector<TaskInfo> taskList;
	
	
	public static TaskListShop getInstance () {
		if (taskListInstance == null) {
			taskListInstance = new TaskListShop();
			logger.info("New singleton TaskListShop instance created");
		}

		return taskListInstance;
	}

	private TaskListShop () {
		taskList = new Vector<TaskInfo>();
	}

	public boolean addTaskToList (TaskInfo newTask) {
		if (taskList != null) {
			logger.info("Adding one item to TaskListShop");
			return taskList.add(newTask);
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
	
	public Vector<TaskInfo> getFloatingTasks() {
		Vector<TaskInfo> returnVector = new Vector<TaskInfo>();
		
		for (int i = 0; i < taskList.size(); i++) {
			TaskInfo singleTask = taskList.get(i);
			if (singleTask.getTaskType() == TASK_TYPE.FLOATING) {
				returnVector.add(singleTask);
			}
		}
		return returnVector;
	}
	
	public Vector<TaskInfo> getDeadlineTasks() {
		Vector<TaskInfo> returnVector = new Vector<TaskInfo>();
		
		for (int i = 0; i < taskList.size(); i++) {
			TaskInfo singleTask = taskList.get(i);
			if (singleTask.getTaskType() == TASK_TYPE.DEADLINE) {
				returnVector.add(singleTask);
			}
		}
		return returnVector;
	}
	
	public Vector<TaskInfo> getTimedTasks() {
		Vector<TaskInfo> returnVector = new Vector<TaskInfo>();
		
		for (int i = 0; i < taskList.size(); i++) {
			TaskInfo singleTask = taskList.get(i);
			if (singleTask.getTaskType() == TASK_TYPE.TIMED) {
				returnVector.add(singleTask);
			}
		}
		return returnVector;
	}
	
	public Vector<TaskInfo> getExpiredTasks() {
		Vector<TaskInfo> returnVector = new Vector<TaskInfo>();
		
		for (int i = 0; i < taskList.size(); i++) {
			TaskInfo singleTask = taskList.get(i);
			boolean isExpired = singleTask.getExpiryFlag();
			if (isExpired) {
				returnVector.add(singleTask);
			}
		}
		return returnVector;
	}

	public boolean removeTaskByName (String taskName) {
		TaskInfo currentTaskToRemove = getTaskByName(taskName);

		if (currentTaskToRemove != null) {
			taskList.remove(currentTaskToRemove);
			logger.info("One task removed");
			return true;
		}

		return false;
	}
	
	public Vector<TaskInfo> clearAllTasks () {
		taskList = new Vector<TaskInfo>();
		Vector<TaskInfo> vectorToReturn = new Vector<TaskInfo>(taskList);
		logger.info("All tasks cleared");
		return vectorToReturn;
	}
	
	public int shopSize () {
		return taskList.size();
	}
}
