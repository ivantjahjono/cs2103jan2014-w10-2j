//@author A0096670W
package kaboom.storage;

import java.util.Calendar;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Logger;

import kaboom.shared.TASK_TYPE;
import kaboom.shared.TaskInfo;
import kaboom.shared.comparators.ComparatorDefault;
import kaboom.shared.comparators.ComparatorPriority;

public class TaskDepository {

	private static TaskDepository taskListInstance = null;
	private static final Logger logger = Logger.getLogger("TaskListShopLogger");

	private Vector<TaskInfo> presentTaskList;
	private Vector<TaskInfo> archivedTaskList;

	public static TaskDepository getInstance () {
		if (taskListInstance == null) {
			taskListInstance = new TaskDepository();
			logger.fine("New singleton TaskListShop instance created");
		}

		return taskListInstance;
	}

	private TaskDepository () {
		presentTaskList = new Vector<TaskInfo>();
		archivedTaskList = new Vector<TaskInfo>();
	}

	public boolean addTaskToList (TaskInfo newTask) {
		if (presentTaskList != null) {
			logger.fine("Adding one item to current list");
			return presentTaskList.add(newTask);
		} else {
			return false;
		}
	}

	public boolean addTaskToArchivedList (TaskInfo newTask) {
		if (archivedTaskList != null) {
			logger.fine("Adding one item to archived list");
			return archivedTaskList.add(newTask);
		} else {
			return false;
		}
	}

	public TaskInfo getTaskByName (String taskName) {
		for (int i = presentTaskList.size()-1; i >= 0; i--) {
			//System.out.println(taskList.get(i).getTaskName());
			if (taskName.equals(presentTaskList.get(i).getTaskName())) {
				return presentTaskList.get(i);
			}
		}
		return null;
	}

	public void updateTask (TaskInfo newTaskInfo, TaskInfo prevTaskInfo) {
		int indexOfTaskListToBeModified = -1;
		for (int i = 0; i < presentTaskList.size(); i++) {
			if (prevTaskInfo.equals(presentTaskList.get(i))) {
				indexOfTaskListToBeModified = i;
				//System.out.println("index="+indexOfTaskListToBeModified);
			}
		}

		if (indexOfTaskListToBeModified != -1) {
			presentTaskList.set(indexOfTaskListToBeModified, newTaskInfo);
		}
	}

	public Vector<TaskInfo> getToday() {
		Vector<TaskInfo> tasksToReturn = new Vector<TaskInfo>();

		for (int i = 0; i < presentTaskList.size(); i++) {
			TaskInfo singleTask = presentTaskList.get(i);
			if (TaskInfo.isTaskToday(singleTask)) {
				tasksToReturn.add(singleTask);
			}
		}
		Collections.sort(tasksToReturn, new ComparatorDefault());
		return tasksToReturn;
	}

	public Vector<TaskInfo> getFutureTasks() {
		Vector<TaskInfo> tasksToReturn = new Vector<TaskInfo>();
		

		for (int i = 0; i < presentTaskList.size(); i++) {
			TaskInfo singleTask = presentTaskList.get(i);
			if (TaskInfo.isFutureTask(singleTask)) {
				tasksToReturn.add(singleTask);
			}
		}
		Collections.sort(tasksToReturn, new ComparatorDefault());
		return tasksToReturn;
	}

	public Vector<TaskInfo> getAllCurrentTasks () {
		Vector<TaskInfo> vectorToReturn = new Vector<TaskInfo>(presentTaskList);
		return vectorToReturn;
	}

	public Vector<TaskInfo> getAllArchivedTasks () {
		Vector<TaskInfo> vectorToReturn = new Vector<TaskInfo>(archivedTaskList);
		Collections.sort(vectorToReturn, new ComparatorDefault());
		return vectorToReturn;
	}

	public Vector<TaskInfo> getFloatingTasks() {
		Vector<TaskInfo> returnVector = new Vector<TaskInfo>();

		for (int i = 0; i < presentTaskList.size(); i++) {
			TaskInfo singleTask = presentTaskList.get(i);
			if (singleTask.getTaskType() == TASK_TYPE.FLOATING) {
				returnVector.add(singleTask);
			}
		}
		Collections.sort(returnVector, new ComparatorPriority());
		return returnVector;
	}

	public Vector<TaskInfo> getTimedTasks() {
		Vector<TaskInfo> returnVector = new Vector<TaskInfo>();

		for (int i = 0; i < presentTaskList.size(); i++) {
			TaskInfo singleTask = presentTaskList.get(i);
			if (singleTask.getTaskType() == TASK_TYPE.TIMED) {
				returnVector.add(singleTask);
			}
		}
		return returnVector;
	}

	public Vector<TaskInfo> getExpiredTasks() {
		Vector<TaskInfo> returnVector = new Vector<TaskInfo>();

		for (int i = 0; i < presentTaskList.size(); i++) {
			TaskInfo singleTask = presentTaskList.get(i);
			boolean isExpired = singleTask.getExpiryFlag();
			if (isExpired) {
				returnVector.add(singleTask);
			}
		}
		Collections.sort(returnVector, new ComparatorDefault());
		return returnVector;
	}

	public TaskInfo removeTask(TaskInfo taskToDelete) {
		for (int i = 0; i < presentTaskList.size(); i++) {
			TaskInfo singleTask = presentTaskList.get(i);
			if (singleTask.equals(taskToDelete)) {
				return presentTaskList.remove(presentTaskList.indexOf(singleTask));
			}
		}
		for (int i = 0; i < archivedTaskList.size(); i++) {
			TaskInfo singleTask = archivedTaskList.get(i);
			if (singleTask.equals(taskToDelete)) {
				return archivedTaskList.remove(archivedTaskList.indexOf(singleTask));
			}
		}
		return null;
	}

	
	public void refreshTasks() {
		refreshTasks(false);
	}
	
	//This function refreshes all the tasks in the vector to check
	//whether it has expired and set to true if it has expired.
	//Sets to false if the task has not expired
	//Floating tasks have a default of not expired
	//Also changes current tasks to archived tasks and vice versa
	public void refreshTasks(boolean uiFlag) {
		//Shift from archive to current list
		for (int i = 0; i < archivedTaskList.size(); i++) {
			TaskInfo singleTask = archivedTaskList.get(i);

			if (!singleTask.getDone()) {
				presentTaskList.add(singleTask);
				archivedTaskList.remove(singleTask);
			}
		}

		//Check for expired tasks
		for (int i = 0; i < presentTaskList.size(); i++) {
			TaskInfo singleTask = presentTaskList.get(i);
			Calendar now = Calendar.getInstance();

			if (!singleTask.getTaskType().equals(TASK_TYPE.FLOATING)) {
				if (now.after(singleTask.getEndDate()) && !singleTask.getDone()) {
					singleTask.setExpiryFlag(true);
				} else {
					singleTask.setExpiryFlag(false);
				}
			} else {
				singleTask.setExpiryFlag(false);  //Floating tasks cannot expire
			}

			if (uiFlag && singleTask.isRecent()) {
				singleTask.setRecent(false);
			}

			//Shift from current list to archived list
			if (singleTask.getDone()) {
				archivedTaskList.add(singleTask);
				presentTaskList.remove(singleTask);
			}
		}

		Collections.sort(presentTaskList, new ComparatorDefault());
	}

	public Vector<TaskInfo> clearAllCurrentTasks () {
		presentTaskList = new Vector<TaskInfo>();
		Vector<TaskInfo> vectorToReturn = new Vector<TaskInfo>(presentTaskList);
		logger.fine("All tasks cleared");
		return vectorToReturn;
	}

	public Vector<TaskInfo> clearAllArchivedTasks () {
		archivedTaskList = new Vector<TaskInfo>();
		Vector<TaskInfo> vectorToReturn = new Vector<TaskInfo>(archivedTaskList);
		logger.fine("All archive tasks cleared");
		return vectorToReturn;
	}

	public Vector<Integer> getCorrespondingID(Vector<TaskInfo> taskList) {
		Vector<Integer> taskID = new Vector<Integer>();
		for (int i = 0; i < taskList.size(); i++) {
			taskID.add(presentTaskList.indexOf(taskList.get(i)));
		}
		return taskID;
	}
	
	public int totalTaskCount() {
		return presentTaskList.size() + archivedTaskList.size();
	}

	public int presentTaskCount () {
		return presentTaskList.size();
	}
	
	public int archiveTaskCount() {
		return archivedTaskList.size();
	}
}
