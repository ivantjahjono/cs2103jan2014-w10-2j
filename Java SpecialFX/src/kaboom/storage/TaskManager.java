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
 * 
 * This is a singleton class as there can only be one instance of this class. 
 */

package kaboom.storage;

import java.util.Vector;

import kaboom.logic.command.Command;
import kaboom.shared.DISPLAY_STATE;
import kaboom.shared.TaskInfo;

public class TaskManager {

	private static TaskManager instance = null;

	private Vector<TaskInfo> currentView; 	//Current view
	private Vector<TaskInfo> searchView;  	//Vector for searches
	private Vector<Integer> tasksCount;
	private TaskDepository taskDepo;
	
	private Storage fileStorage;
	private final String FILENAME = "KABOOM_FILE.dat";
	private History history;

	private TaskManager() {
		taskDepo = TaskDepository.getInstance();
		currentView = taskDepo.getToday();
		searchView = new Vector<TaskInfo>();
		tasksCount = new Vector<Integer>();
		fileStorage = new Storage(FILENAME);
		history = History.getInstance();
		fileStorage.load();
	}
	
	private TaskManager(String fileName) {
		taskDepo = TaskDepository.getInstance();
		currentView = taskDepo.getToday();
		searchView = new Vector<TaskInfo>();
		tasksCount = new Vector<Integer>();
		fileStorage = new Storage(fileName);
		history = History.getInstance();
		fileStorage.load();
	}

	public static TaskManager getInstance () {
		if (instance == null) {
			instance = new TaskManager();
		}
		return instance;
	}
	
	public static TaskManager getInstance(String fileName) {
		if (instance == null) {
			instance = new TaskManager(fileName);
		}
		return instance;
	}
	
	public Vector<Integer> getTasksCountList() {
		clearAndAddTaskCounts();
		return tasksCount;
	}

	private void clearAndAddTaskCounts() {
		tasksCount.clear();
		tasksCount.add(taskDepo.getToday().size());
		tasksCount.add(taskDepo.getFutureTasks().size());
		tasksCount.add(taskDepo.getFloatingTasks().size());
		tasksCount.add(taskDepo.getExpiredTasks().size());
		tasksCount.add(taskDepo.getAllArchivedTasks().size());
	}

	public Vector<TaskInfo> setAndGetView(DISPLAY_STATE displayState) {
		switch (displayState) {
		case TODAY:
			setCurrentView(taskDepo.getToday());
			break;

		case TIMELESS:
			setCurrentView(taskDepo.getFloatingTasks());
			break;

		case EXPIRED:
			setCurrentView(taskDepo.getExpiredTasks());
			break;

		case FUTURE:
			setCurrentView(taskDepo.getFutureTasks());
			break;

		case SEARCH:
			setCurrentView(searchView);
			break;

		case ARCHIVE:
			setCurrentView(taskDepo.getAllArchivedTasks());
			break;

		default:
			setCurrentView(taskDepo.getToday());
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
	
	public Vector<TaskInfo> getSearchView() {
		return searchView;
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
		return taskDepo.getAllPresentTasks();
	}
	
	public Vector<TaskInfo> getAllArchivedTasks() {
		return taskDepo.getAllArchivedTasks();
	}
	
	public int countPresentTasks() {
		return taskDepo.countPresentTasks();
	}
	
	public int countArchivedTasks() {
		return taskDepo.countArchivedTasks();
	}
	
	public boolean addPresentTask(TaskInfo task) {
		assert task.isDone() == false;
		boolean isAdded = taskDepo.addTaskToPresentList(task);
		taskDepo.refreshTasks();
		addToSearchView(task);
		store();
		return isAdded;
	}
	
	public boolean addArchivedTask(TaskInfo task) {
		assert task.isDone() == true;
		boolean isAdded = taskDepo.addTaskToArchivedList(task);
		taskDepo.refreshTasks();
		addToSearchView(task);
		store();
		return isAdded;
	}
	
	public boolean removeTask(TaskInfo task) {
		TaskInfo removedTask = taskDepo.removeTask(task);
		deleteInSearchView(task);
		store();
		if (removedTask == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public void updateTask(TaskInfo newTask, TaskInfo oldTask) {
		taskDepo.updateTask(newTask, oldTask);
		updateInSearchView(newTask, oldTask);
		store();
	}
	
	public void doneTask(TaskInfo task) {
		assert task.isDone() == false;
		task.setExpiry(false);
		task.setDone(true);
		deleteInSearchView(task);
		task.setRecent(true);
		taskDepo.refreshTasks();  //Refresh to shift task to archive
		store();
	}
	
	public void undoneTask(TaskInfo task) {
		assert task.isDone() == true;
		task.setDone(false);
		deleteInSearchView(task);
		task.setRecent(true);
		taskDepo.refreshTasks();  //Refresh to shift task to archive
		store();
	}
	
	public void clearAllTasks() {
		clearPresentTasks();
		clearArchivedTasks();
	}
	
	public void clearPresentTasks() {
		taskDepo.clearAllPresentTasks();
		clearSearchView();
		store();
	}
	
	public void clearArchivedTasks() {
		taskDepo.clearAllArchivedTasks();
		clearSearchView();
		store();
	}
	
	public void refreshTasks() {
		taskDepo.refreshTasks();
		store();
	}
	
	public void refreshTasksAndResetRecent() {
		taskDepo.refreshTasks(true);
		store();
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
	
	public void addToHistory(Command command) {
		history.addToRecentCommands(command);
	}
	
	public Command getMostRecentCommand() {
		return history.getMostRecentCommand();
	}
}
