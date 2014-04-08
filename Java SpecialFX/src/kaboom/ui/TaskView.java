package kaboom.ui;

import java.util.Vector;

import kaboom.logic.TaskInfo;
import kaboom.storage.TaskListShop;

public class TaskView {

	private static TaskView instance = null;

	private Vector<TaskInfo> currentView;  //Current view
	private Vector<TaskInfo> searchView;  //Vector for searches
	private Vector<Integer> currentViewID;  //Vector for position of viewing tasks in actual vector
	private TaskListShop taskListShop;
	private DisplayData displayData;

	public TaskView() {
		taskListShop = TaskListShop.getInstance();
		currentView = taskListShop.getToday();
		currentViewID = taskListShop.getCorrespondingID(currentView);
		searchView = new Vector<TaskInfo>();
		displayData = DisplayData.getInstance();
	}

	public static TaskView getInstance () {
		if (instance == null) {
			instance = new TaskView();
		}
		return instance;
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
			if (currentView.get(i).equals(task) && 
					displayData.getCurrentDisplayState() != DISPLAY_STATE.ARCHIVE) {
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
			if (currentView.get(i).equals(task) && 
					displayData.getCurrentDisplayState() == DISPLAY_STATE.ARCHIVE) {
				currentView.remove(i);
			}
		}

		//no support for undone tasks in search yet
		/*
		for (int i = 0; i < searchView.size(); i++) {
			if (searchView.get(i).equals(task)) {
				searchView.remove(i);
			}
		}*/
	}

	public void addToView(TaskInfo task) {
		if (displayData.getCurrentDisplayState() == DISPLAY_STATE.SEARCH) {
			searchView.add(task);
		}
	}
	
	public void swapView(TaskInfo newTask, TaskInfo oldTask) {
		for (int i = 0; i < searchView.size(); i++) {
			if (searchView.get(i).equals(oldTask) 
					&& displayData.getCurrentDisplayState() == DISPLAY_STATE.SEARCH) {
				searchView.set(i, newTask);
			}
		}
	}

	public void setCurrentView(Vector<TaskInfo> taskList) {
		currentView = taskList;
		currentViewID = taskListShop.getCorrespondingID(taskList);
	}

}
