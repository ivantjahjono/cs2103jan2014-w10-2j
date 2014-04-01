package kaboom.storage;

import java.util.Calendar;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Logger;

import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.TaskInfo;
import kaboom.logic.TASK_TYPE;
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
			logger.fine("Adding one item to TaskListShop");
			return currentTaskList.add(newTask);
		} else {
			return false;
		}
	}

	public boolean addTaskToArchivedList (TaskInfo newTask) {
		if (archivedTaskList != null) {
			logger.fine("Adding one item to TaskListShop");
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

	public TaskInfo getArchivedTaskByName (String taskName) {
		for (int i = archivedTaskList.size()-1; i >= 0; i--) {
			//System.out.println(taskList.get(i).getTaskName());
			if (taskName.equals(archivedTaskList.get(i).getTaskName())) {
				return archivedTaskList.get(i);
			}
		}
		return null;
	}

	public TaskInfo getTaskByID(int index) {
		return currentTaskList.get(index);
	}

	public TaskInfo getArchivedTaskByID(int index) {
		return archivedTaskList.get(index);
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

	public void updateArchivedTask(TaskInfo newTaskInfo, TaskInfo prevTaskInfo) {
		int indexOfTaskListToBeModified = -1;
		for (int i = 0; i < archivedTaskList.size(); i++) {
			if (prevTaskInfo.equals(archivedTaskList.get(i))) {
				indexOfTaskListToBeModified = i;
				//System.out.println("index="+indexOfTaskListToBeModified);
			}
		}

		if (indexOfTaskListToBeModified != -1) {
			archivedTaskList.set(indexOfTaskListToBeModified, newTaskInfo);
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

	public TaskInfo removeTaskByName (String taskName) {
		for (int i = 0; i < currentTaskList.size(); i++) {
			TaskInfo singleTask = currentTaskList.get(i);
			if (singleTask.getTaskName().equals(taskName)) {
				return currentTaskList.remove(currentTaskList.indexOf(singleTask));
			}
		}
		return null;
	}

	public TaskInfo removeTaskByID(int taskID) {
		assert taskID <= currentTaskList.size();

		//TaskID is the position of the task in the vector
		return currentTaskList.remove(taskID);
	}

	//This function refreshes all the tasks in the vector to check
	//whether it has expired and set to true if it has expired.
	//Sets to false if the task has not expired
	//Floating tasks have a default of not expired
	//Also changes current tasks to archived tasks and vice versa
	public void refreshTasks() {
		for (int i = 0; i < archivedTaskList.size(); i++) {
			TaskInfo singleTask = archivedTaskList.get(i);

			if (!singleTask.getDone()) {
				currentTaskList.add(singleTask);
				archivedTaskList.remove(singleTask);
			}
		}

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
			
			if (singleTask.isRecent()) {
				singleTask.setRecent(false);
			}

			if (singleTask.getDone()) {
				archivedTaskList.add(singleTask);
				currentTaskList.remove(singleTask);
			}
		}

		History.getInstance().setViewingTasks(currentTaskList);
	}

	public Vector<TaskInfo> clearAllTasks () {
		currentTaskList = new Vector<TaskInfo>();
		Vector<TaskInfo> vectorToReturn = new Vector<TaskInfo>(currentTaskList);
		logger.fine("All tasks cleared");
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
	}

	public int numOfTasksWithSimilarNames(String name) {
		int count = 0;
		for (int i = 0; i < currentTaskList.size(); i++) {
			if (currentTaskList.get(i).getTaskName().contains(name)) {
				count++;
			}
		}
		return count;
	}

	public int numOfArchivedTasksWithSimilarNames(String name) {
		int count = 0;
		for (int i = 0; i < archivedTaskList.size(); i++) {
			if (archivedTaskList.get(i).getTaskName().contains(name)) {
				count++;
			}
		}
		return count;
	}
	
	public void setLastToDone() {
		currentTaskList.lastElement().setDone(true);
	}
	
	public void setLastToUndone() {
		archivedTaskList.lastElement().setDone(false);
	}

	public boolean setDoneByName(String name) {
		//Assumes that there is only one task that has that exact name
		for (int i = 0; i < currentTaskList.size(); i++) {
			TaskInfo singleTask = currentTaskList.get(i);
			if (singleTask.getTaskName().equals(name)) {
				singleTask.setDone(true);
				return true;
			}
		}
		return false;
	}

	public boolean setUndoneByName(String name) {
		//Assumes that there is only one task that has that exact name
		for (int i = 0; i < archivedTaskList.size(); i++) {
			TaskInfo singleTask = archivedTaskList.get(i);
			if (singleTask.getTaskName().equals(name)) {
				singleTask.setDone(false);
				return true;
			}
		}
		return false;
	}

	public boolean setDoneByID(int index) {
		//There might be arrray out of bounds error here
		try {
			TaskInfo singleTask = currentTaskList.get(index);
			singleTask.setDone(true);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public boolean setUndoneByID(int index) {
		//There might be arrray out of bounds error here
		try {
			TaskInfo singleTask = archivedTaskList.get(index);
			singleTask.setDone(false);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	public int shopSize () {
		return currentTaskList.size();
	}
}
