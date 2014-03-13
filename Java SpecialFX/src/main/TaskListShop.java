package main;

import java.util.Vector;

public class TaskListShop {

	private static TaskListShop taskListInstance = null;
	
	private Vector<TaskInfo> taskList;
	
	
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
		if (taskList != null) {
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

	public boolean removeTaskByName (String taskName) {
		TaskInfo currentTaskToRemove = getTaskByName(taskName);

		if (currentTaskToRemove != null) {
			taskList.remove(currentTaskToRemove);
			return true;
		}

		return false;
	}
	
	public Vector<TaskInfo> clearAllTasks () {
		taskList = new Vector<TaskInfo>();
		Vector<TaskInfo> vectorToReturn = new Vector<TaskInfo>(taskList);
		return vectorToReturn;
	}
	
	public int shopSize () {
		return taskList.size();
	}
}
