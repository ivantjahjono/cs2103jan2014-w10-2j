package kaboom.storage;

import java.util.Calendar;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Logger;

import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.TaskInfo;
import kaboom.logic.TASK_TYPE;
import kaboom.logic.command.ComparatorDefault;
import kaboom.logic.command.ComparatorEndDate;
import kaboom.logic.command.ComparatorName;
import kaboom.logic.command.ComparatorPriority;
import kaboom.logic.command.ComparatorStartDate;

public class TaskListShop {

	private static TaskListShop taskListInstance = null;
	private static final Logger logger = Logger.getLogger("TaskListShopLogger");

	private Vector<TaskInfo> currentTaskList;
	private Vector<TaskInfo> archivedTaskList;

	public static TaskListShop getInstance () {
		if (taskListInstance == null) {
			taskListInstance = new TaskListShop();
			logger.fine("New singleton TaskListShop instance created");
		}

		return taskListInstance;
	}

	private TaskListShop () {
		currentTaskList = new Vector<TaskInfo>();
		archivedTaskList = new Vector<TaskInfo>();
	}

	public boolean addTaskToList (TaskInfo newTask) {
		if (currentTaskList != null) {
			logger.fine("Adding one item to current list");
			return currentTaskList.add(newTask);
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

	public Vector<TaskInfo> getToday() {
		Vector<TaskInfo> tasks = new Vector<TaskInfo>();
		Calendar today = Calendar.getInstance();

		for (int i = 0; i < currentTaskList.size(); i++) {
			TaskInfo singleTask = currentTaskList.get(i);
			Calendar taskStartDate = singleTask.getStartDate();
			Calendar taskEndDate = singleTask.getEndDate();

			//Do not get floating tasks
			if (singleTask.getTaskType() != TASK_TYPE.FLOATING) {
				if ((taskStartDate != null && taskStartDate.before(today)) 
						&& (taskEndDate != null && taskEndDate.after(today))) {
					tasks.add(singleTask);  //Add if task starts before and ends after current day
				}
				else if (taskStartDate != null && 
						taskStartDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
						taskStartDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
					tasks.add(singleTask);  //Add if task starts on the current day
				}
				else if (taskEndDate != null &&
						taskEndDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
						taskEndDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
					tasks.add(singleTask);  //Add if task ends on the current day
				}
			}
		}
		Collections.sort(tasks, new ComparatorDefault());
		return tasks;
	}

	public Vector<TaskInfo> getFutureTasks() {
		Vector<TaskInfo> tasks = new Vector<TaskInfo>();
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 23);
		today.set(Calendar.MINUTE, 59);
		today.set(Calendar.SECOND, 59);

		for (int i = 0; i < currentTaskList.size(); i++) {
			TaskInfo singleTask = currentTaskList.get(i);
			Calendar taskStartDate = singleTask.getStartDate();
			Calendar taskEndDate = singleTask.getEndDate();

			//Do not get floating tasks
			if (singleTask.getTaskType() != TASK_TYPE.FLOATING) {
				if (taskStartDate != null && taskStartDate.after(today)) {
					tasks.add(singleTask);
				}
				else if (taskEndDate != null && taskEndDate.after(today)) {
					tasks.add(singleTask);
				}
			}
		}
		Collections.sort(tasks, new ComparatorDefault());
		return tasks;
	}

	public Vector<TaskInfo> getAllCurrentTasks () {
		Vector<TaskInfo> vectorToReturn = new Vector<TaskInfo>(currentTaskList);
		return vectorToReturn;
	}

	public Vector<TaskInfo> getAllArchivedTasks () {
		Vector<TaskInfo> vectorToReturn = new Vector<TaskInfo>(archivedTaskList);
		Collections.sort(vectorToReturn, new ComparatorDefault());
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
		Collections.sort(returnVector, new ComparatorDefault());
		return returnVector;
	}
	
	public TaskInfo removeTask(TaskInfo taskToDelete) {
		for (int i = 0; i < currentTaskList.size(); i++) {
			TaskInfo singleTask = currentTaskList.get(i);
			if (singleTask.equals(taskToDelete)) {
				return currentTaskList.remove(currentTaskList.indexOf(singleTask));
			}
		}
		return null;
	}

	public TaskInfo removeTaskByName (String taskName) {
		//Assumes that there is only one task with the same name
		for (int i = 0; i < currentTaskList.size(); i++) {
			TaskInfo singleTask = currentTaskList.get(i);
			if (singleTask.getTaskName().contains(taskName)) {
				return currentTaskList.remove(currentTaskList.indexOf(singleTask));
			}
		}
		return null;
	}

	//This function refreshes all the tasks in the vector to check
	//whether it has expired and set to true if it has expired.
	//Sets to false if the task has not expired
	//Floating tasks have a default of not expired
	//Also changes current tasks to archived tasks and vice versa
	public void refreshTasks() {
		//Shift from archive to current list
		for (int i = 0; i < archivedTaskList.size(); i++) {
			TaskInfo singleTask = archivedTaskList.get(i);

			if (!singleTask.getDone()) {
				currentTaskList.add(singleTask);
				archivedTaskList.remove(singleTask);
			}
		}

		//Check for expired tasks
		for (int i = 0; i < currentTaskList.size(); i++) {
			TaskInfo singleTask = currentTaskList.get(i);
			Calendar now = Calendar.getInstance();

			if (!singleTask.getTaskType().equals(TASK_TYPE.FLOATING)) {
				if (now.after(singleTask.getEndDate())) {
					singleTask.setExpiryFlag(true);
				} else {
					singleTask.setExpiryFlag(false);
				}
			} else {
				singleTask.setExpiryFlag(false);  //Floating tasks cannot expire
			}

			if (singleTask.isRecent()) {
				singleTask.setRecent(false);
			}

			//Shift from current list to archived list
			if (singleTask.getDone()) {
				archivedTaskList.add(singleTask);
				currentTaskList.remove(singleTask);
			}
		}

		Collections.sort(currentTaskList, new ComparatorDefault());
	}

	public Vector<TaskInfo> clearAllTasks () {
		currentTaskList = new Vector<TaskInfo>();
		Vector<TaskInfo> vectorToReturn = new Vector<TaskInfo>(currentTaskList);
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
			taskID.add(currentTaskList.indexOf(taskList.get(i)));
		}
		return taskID;
	}

	public void sort(KEYWORD_TYPE type) {
		if (type == KEYWORD_TYPE.TASKNAME) {
			Collections.sort(currentTaskList, new ComparatorName());
		}
		else if (type == KEYWORD_TYPE.START_DATE) {
			Collections.sort(currentTaskList, new ComparatorStartDate());
		}
		else if (type == KEYWORD_TYPE.END_DATE){
			Collections.sort(currentTaskList, new ComparatorEndDate());
		}
		else if (type == KEYWORD_TYPE.PRIORITY) {
			Collections.sort(currentTaskList, new ComparatorPriority());
		}
		else {
			Collections.sort(currentTaskList, new ComparatorDefault());
		}
	}

	public boolean isTaskToday(TaskInfo task) {
		Calendar today = Calendar.getInstance();

		Calendar taskStartDate = task.getStartDate();
		Calendar taskEndDate = task.getEndDate();

		//Do not get floating tasks
		if (task.getTaskType() != TASK_TYPE.FLOATING) {
			if ((taskStartDate != null && taskStartDate.before(today)) 
					&& (taskEndDate != null && taskEndDate.after(today))) {
				return true;
			}
			else if (taskStartDate != null && 
					taskStartDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
					taskStartDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
				return true;
			}
			else if (taskEndDate != null &&
					taskEndDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
					taskEndDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
				return true;
			}
		}
		return false;
	}

	public int shopSize () {
		return currentTaskList.size();
	}
}
