//@author A0096670W

/**
 * TaskManager class is the intermediary between logic and storage.
 * Any manipulations of tasks (such as addition, deletion, updating)
 * must use this class instead of accessing it from the TaskDepository. 
 * This file also handles the storing of the tasks in the depository
 * to the physical file specified in the String constant. 
 * 
 * This class is also responsible for the "dynamic" IDs of the tasks.
 * (Such as the task ID being different while under different views 
 * but referring to the same object.)
 */

package kaboom.storage;

import java.util.Vector;

import kaboom.shared.DISPLAY_STATE;
import kaboom.shared.TaskInfo;

public class TaskView {

	private static TaskView instance = null;

	private Vector<TaskInfo> currentView; 	//Current view
	private Vector<TaskInfo> searchView;  	//Vector for searches
	private Vector<Integer> tasksCount;
	private TaskDepository taskListShop;
	
	private Storage fileStorage;
	private final String FILENAME;

	private TaskView() {
		taskListShop = TaskDepository.getInstance();
		//currentView = taskListShop.getToday();
		//currentViewID = taskListShop.getCorrespondingID(currentView);
		searchView = new Vector<TaskInfo>();
		tasksCount = new Vector<Integer>();
		FILENAME = "KABOOM_FILE.dat";
		fileStorage = new Storage(FILENAME);
	}

	public static TaskView getInstance () {
		if (instance == null) {
			instance = new TaskView();
		}
		return instance;
	}
	
	public Vector<Integer> getTasksCountList() {
		tasksCount.clear();
		
		tasksCount.add(taskListShop.getToday().size());
		tasksCount.add(taskListShop.getFutureTasks().size());
		tasksCount.add(taskListShop.getFloatingTasks().size());
		tasksCount.add(taskListShop.getExpiredTasks().size());
		tasksCount.add(taskListShop.getAllArchivedTasks().size());
		
		return tasksCount;
	}

	public Vector<TaskInfo> setAndGetView(DISPLAY_STATE displayState) {
		switch (displayState) {
		case TODAY:
			setCurrentView(taskListShop.getToday());
			break;

		case TIMELESS:
			setCurrentView(taskListShop.getFloatingTasks());
			break;

		case EXPIRED:
			setCurrentView(taskListShop.getExpiredTasks());
			break;

		case FUTURE:
			setCurrentView(taskListShop.getFutureTasks());
			break;

		case SEARCH:
			setCurrentView(searchView);
			break;

		case ARCHIVE:
			setCurrentView(taskListShop.getAllArchivedTasks());
			break;

		default:
			setCurrentView(taskListShop.getToday());
			break;
		}
		
		return currentView;
	}
	
	private void setCurrentView(Vector<TaskInfo> taskList) {
		currentView = taskList;
	}	

	public Vector<TaskInfo> getCurrentView() {
		return currentView;
	}

	public void setSearchView(Vector<TaskInfo> taskList) {
		searchView = taskList;
		setCurrentView(taskList);
	}
	
	public TaskInfo getTaskFromViewByName(String searchName) {
		for (int i = 0; i < currentView.size(); i++) {
			if (currentView.get(i).getTaskName().contains(searchName)) {
				return currentView.get(i);
			}
		}
		return null;
	}
	
	public TaskInfo getTaskFromViewByID(int index) {
		if (currentView.size() <= index) {
			return null;
		} else {
			return currentView.get(index);			
		}
	}
	
	public int getTaskPositionInView (TaskInfo taskToSearch) {
		return currentView.indexOf(taskToSearch);
	}
	
	public Vector<TaskInfo> getAllPresentTasks() {
		return taskListShop.getAllCurrentTasks();
	}
	
	public Vector<TaskInfo> getAllArchivedTasks() {
		return taskListShop.getAllArchivedTasks();
	}
	
	public int presentTaskCount() {
		return taskListShop.shopSize();
	}
	
	public int archiveTaskCount() {
		return taskListShop.archiveShopSize();
	}
	
	public boolean addTask(TaskInfo task) {
		//Check if task is archived or current and add to the proper list
		boolean isAdded = taskListShop.addTaskToList(task);
		taskListShop.refreshTasks();
		addToSearchView(task);
		return isAdded;
	}
	
	public boolean addArchivedTask(TaskInfo task) {
		//Check if task is archived or current and add to the proper list
		boolean isAdded = taskListShop.addTaskToArchivedList(task);
		taskListShop.refreshTasks();
		addToSearchView(task);
		return isAdded;
	}
	
	public boolean removeTask(TaskInfo task) {
		TaskInfo removedTask = taskListShop.removeTask(task);
		deleteInSearchView(task);
		if (removedTask == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public void updateTask(TaskInfo newTask, TaskInfo oldTask) {
		taskListShop.updateTask(newTask, oldTask);
		updateInSearchView(newTask, oldTask);
	}
	
	public void doneTask(TaskInfo task) {
		//add assertion here that the task is not done yet
		task.setExpiryFlag(false);
		task.setDone(true);
		taskListShop.refreshTasks();  //Refresh to shift task to archive
		deleteInSearchView(task);
		task.setRecent(true);
	}
	
	public void undoneTask(TaskInfo task) {
		//add assertion here that the task is done already
		task.setDone(false);
		taskListShop.refreshTasks();  //Refresh to shift task to archive
		deleteInSearchView(task);
		task.setRecent(true);
	}
	
	public void clearPresentTasks() {
		taskListShop.clearAllCurrentTasks();
		clearSearchView();
	}
	
	public void clearArchivedTasks() {
		taskListShop.clearAllArchivedTasks();
		clearSearchView();
	}
	
	public void refreshTasks() {
		taskListShop.refreshTasks();
	}
	
	public void addToSearchView(TaskInfo task) {
		searchView.add(task);
	}

	public void deleteInSearchView(TaskInfo task) {
		for (int i = 0; i < searchView.size(); i++) {
			if (searchView.get(i).equals(task)) {
				searchView.remove(i);
			}
		}
	}

	public void updateInSearchView(TaskInfo newTask, TaskInfo oldTask) {
		for (int i = 0; i < searchView.size(); i++) {
			if (searchView.get(i).equals(oldTask)) {
				searchView.set(i, newTask);
			}
		}
	}
	
	public void clearSearchView() {
		searchView.clear();
	}
	
	public void store() {
		fileStorage.store();
	}
	
	public void load() {
		fileStorage.load();
	}
}
