//@author A0096670W

package kaboom.storage;

import java.util.Vector;

import kaboom.shared.DISPLAY_STATE;
import kaboom.shared.TaskInfo;
import kaboom.ui.DisplayData;

public class TaskView {

	private static TaskView instance = null;

	private Vector<TaskInfo> currentView; 	//Current view
	private Vector<TaskInfo> searchView;  	//Vector for searches
	private Vector<Integer> currentViewID;  //Vector for position of viewing tasks in actual vector
	private Vector<Integer> tasksCount;
	private TaskDepository taskListShop;
	private DisplayData displayData;

	private TaskView() {
		taskListShop = TaskDepository.getInstance();
		//currentView = taskListShop.getToday();
		//currentViewID = taskListShop.getCorrespondingID(currentView);
		searchView = new Vector<TaskInfo>();
		displayData = DisplayData.getInstance();
		tasksCount = new Vector<Integer>();
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
			System.out.println("Encountered an invalid view!");
			break;
		}
		
		return currentView;
	}

	public Vector<TaskInfo> getCurrentView() {
		return currentView;
	}

	public Vector<Integer> getCurrentViewID() {
		return currentViewID;
	}

	public void setSearchView(Vector<TaskInfo> taskList) {
		searchView = taskList;
		setCurrentView(taskList);
	}

	public Vector<TaskInfo> getSearchView() {
		return searchView;
	}

	//Precondition: The index already has offset applied
	//i.e. index starts from 0
	public int getIndexFromView(int index) {
		return currentViewID.get(index);
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
	
	public Vector<TaskInfo> getAllCurrentTasks() {
		return taskListShop.getAllCurrentTasks();
	}
	
	public Vector<TaskInfo> getAllArchivedTasks() {
		return taskListShop.getAllArchivedTasks();
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
		//add assertion here
		task.setDone(true);
		task.setExpiryFlag(false);
		taskListShop.refreshTasks();  //Refresh to shift task to archive
		deleteInSearchView(task);
	}
	
	public void undoneTask(TaskInfo task) {
		//add assertion here
		task.setDone(false);
		taskListShop.refreshTasks();  //Refresh to shift task to archive
		addToSearchView(task);
	}
	
	public void clearCurrentTasks() {
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
			if (searchView.get(i).equals(oldTask) 
					&& displayData.getCurrentDisplayState() == DISPLAY_STATE.SEARCH) {
				searchView.set(i, newTask);
			}
		}
	}
	
	public void clearSearchView() {
		searchView.clear();
	}

	private void setCurrentView(Vector<TaskInfo> taskList) {
		currentView = taskList;
		currentViewID = taskListShop.getCorrespondingID(taskList);
	}
}