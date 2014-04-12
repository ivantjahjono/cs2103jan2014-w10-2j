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
	private TaskListShop taskListShop;
	private DisplayData displayData;

	private TaskView() {
		taskListShop = TaskListShop.getInstance();
		//currentView = taskListShop.getToday();
		//currentViewID = taskListShop.getCorrespondingID(currentView);
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

	//Precondition: The index already has offset applied
	//i.e. index starts from 0
	public int getIndexFromView(int index) {
		return currentViewID.get(index);
	}
	
	public TaskInfo getTaskFromViewByName(String searchName) {
		for (int i = 0; i < currentView.size(); i++) {
			if (currentView.get(i).getTaskName().equals(searchName)) {
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
	
	public int getTaskPositionInView (TaskInfo taskToSearch) {
		return currentView.indexOf(taskToSearch);
	}

	private void setCurrentView(Vector<TaskInfo> taskList) {
		currentView = taskList;
		currentViewID = taskListShop.getCorrespondingID(taskList);
	}
}