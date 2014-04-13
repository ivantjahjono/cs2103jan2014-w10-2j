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

	public void refreshTasks(boolean isResetRecentFlag) {
		refreshArchive(isResetRecentFlag);
		refreshPresent(isResetRecentFlag);
	}

	private void refreshArchive(boolean isResetRecentFlag) {
		for (int i = 0; i < archivedTaskList.size(); i++) {
			TaskInfo singleTask = archivedTaskList.get(i);
			checkAndSetRecent(isResetRecentFlag, singleTask);

			if (!singleTask.getDone()) {
				swapFromArchiveToPresent(singleTask);
			}
		}
	}

	private void refreshPresent(boolean isResetRecentFlag) {
		for (int i = 0; i < presentTaskList.size(); i++) {
			TaskInfo singleTask = presentTaskList.get(i);
			checkAndSetExpiry(singleTask);
			checkAndSetRecent(isResetRecentFlag, singleTask);

			if (singleTask.getDone()) {
				swapFromPresentToArchive(singleTask);
			}
		}
	}

	private void checkAndSetRecent(boolean isSetToNotRecent, TaskInfo singleTask) {
		if (isSetToNotRecent && singleTask.isRecent()) {
			singleTask.setRecent(false);
		}
	}

	private void checkAndSetExpiry(TaskInfo singleTask) {
		if (!singleTask.getTaskType().equals(TASK_TYPE.FLOATING)) {
			if (isTaskExpired(singleTask)) {
				singleTask.setExpiryFlag(true);
			} else {
				singleTask.setExpiryFlag(false);
			}
		} else {
			singleTask.setExpiryFlag(false);  //Floating tasks cannot expire
		}
	}

	private boolean isTaskExpired(TaskInfo singleTask) {
		Calendar now = Calendar.getInstance();
		return now.after(singleTask.getEndDate()) && !singleTask.getDone();
	}

	private void swapFromPresentToArchive(TaskInfo singleTask) {
		archivedTaskList.add(singleTask);
		presentTaskList.remove(singleTask);
	}

	private void swapFromArchiveToPresent(TaskInfo singleTask) {
		presentTaskList.add(singleTask);
		archivedTaskList.remove(singleTask);
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
