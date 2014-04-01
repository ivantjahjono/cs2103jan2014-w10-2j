package kaboom.ui;

import java.util.Vector;

import kaboom.logic.DisplayData;
import kaboom.logic.TaskInfo;
import kaboom.storage.TaskListShop;

public class TaskView {

	private static TaskView instance = null;

	private Vector<TaskInfo> currentView;  //Current view
	private Vector<TaskInfo> searchView;  //Vector for searches
	private Vector<Integer> currentViewID;  //Vector for position of viewing tasks in actual vector
	private TaskListShop taskListShop;

	public TaskView() {
		taskListShop = TaskListShop.getInstance();
		currentView = taskListShop.getAllCurrentTasks();
		currentViewID = taskListShop.getCorrespondingID(currentView);
		searchView = new Vector<TaskInfo>();
	}

	public static TaskView getInstance () {
		if (instance == null) {
			instance = new TaskView();
		}
		return instance;
	}

	public Vector<TaskInfo> setAndGetView(DISPLAY_STATE displayState) {
		switch (displayState) {
		case ALL:
			setCurrentView(taskListShop.getAllCurrentTasks());
			break;

		case RUNNING:
			setCurrentView(taskListShop.getFloatingTasks());
			break;

		case DEADLINE:
			setCurrentView(taskListShop.getDeadlineTasks());
			break;

		case TIMED:
			setCurrentView(taskListShop.getTimedTasks());
			break;

		case SEARCH:
			setCurrentView(searchView);
			break;

		case ARCHIVE:
			setCurrentView(taskListShop.getAllArchivedTasks());
			break;

		default:
			setCurrentView(taskListShop.getAllCurrentTasks());
			System.out.println("Encountered an invalid view!");
			break;
		}
		return currentView;
	}
	
	public Vector<TaskInfo> getView(DISPLAY_STATE displayState) {
		switch (displayState) {
		case ALL:
			return taskListShop.getAllCurrentTasks();

		case RUNNING:
			return taskListShop.getFloatingTasks();

		case DEADLINE:
			return taskListShop.getDeadlineTasks();

		case TIMED:
			return taskListShop.getTimedTasks();

		case SEARCH:
			return searchView;

		case ARCHIVE:
			return taskListShop.getAllArchivedTasks();

		default:
			System.out.println("Encountered an invalid view!");
			return taskListShop.getAllCurrentTasks();
		}
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
	
	public int getIndexFromView(int index) {
		return currentViewID.get(index);
	}
	
	public void deleteInView(TaskInfo task) {
		for (int i = 0; i < currentView.size(); i++) {
			if (currentView.get(i).equals(task)) {
				currentView.remove(i);
			}
		}
		
		for (int i = 0; i < searchView.size(); i++) {
			if (searchView.get(i).equals(task)) {
				searchView.remove(i);
			}
		}
	}
	
	public void doneInView(TaskInfo task) {
		for (int i = 0; i < currentView.size(); i++) {
			if (currentView.get(i).equals(task)) {
				currentView.remove(i);
			}
		}
		
		for (int i = 0; i < searchView.size(); i++) {
			if (searchView.get(i).equals(task)) {
				searchView.remove(i);
			}
		}
	}
	
	public void undoneInView(TaskInfo task) {
		for (int i = 0; i < currentView.size(); i++) {
			if (currentView.get(i).equals(task)) {
				currentView.remove(i);
			}
		}
		
		for (int i = 0; i < searchView.size(); i++) {
			if (searchView.get(i).equals(task)) {
				searchView.remove(i);
			}
		}
	}
	
	public void addToView(TaskInfo task) {
		if (DisplayData.getInstance().getCurrentDisplayState() == DISPLAY_STATE.SEARCH) {
			searchView.add(task);
		}
	}
	
	public void setCurrentView(Vector<TaskInfo> taskList) {
		currentView = taskList;
		currentViewID = taskListShop.getCorrespondingID(taskList);
	}

}
