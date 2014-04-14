//@author A0096670W

/**
 * TaskViewTest.java:
 * This class tests if the function calls are properly routed to the appropriate
 * classes and compare return values with theirs. 
 * There is seemingly no boundary cases to take note of. 
 */

package KaboomTest;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Calendar;

import kaboom.logic.command.Command;
import kaboom.logic.command.CommandAdd;
import kaboom.logic.command.CommandClear;
import kaboom.logic.command.CommandDone;
import kaboom.logic.command.CommandUndone;
import kaboom.shared.DISPLAY_STATE;
import kaboom.shared.TASK_TYPE;
import kaboom.shared.TaskInfo;
import kaboom.storage.History;
import kaboom.storage.TaskDepository;
import kaboom.storage.TaskManager;

import org.junit.Before;
import org.junit.Test;

public class TaskManagerTest {

	TaskManager taskManager;
	TaskDepository taskDepo;
	History history;

	TaskInfo floatingTask;
	TaskInfo todayTask;
	TaskInfo futureTask;
	TaskInfo expiredTask;
	TaskInfo archivedTask;

	Command commandAdd;
	Command commandClear;
	Command commandDone;
	Command commandUndone;

	@Before
	public void initailize() {
		taskManager = TaskManager.getInstance("StorageTest.txt");
		taskDepo = TaskDepository.getInstance();
		history = History.getInstance();
		assertNotNull(taskDepo);
		assertNotNull(taskManager);
		assertNotNull(history);

		floatingTask = setFloatingTask();
		todayTask = setTodayTask();
		futureTask = setFutureTask();
		expiredTask = setExpiredTask();
		archivedTask = setArchivedTask();

		commandAdd = new CommandAdd();
		commandClear = new CommandClear();
		commandDone = new CommandDone();
		commandUndone = new CommandUndone();

		populateTaskDepo();
	}

	@Test
	public void testGetInstance() {
		assertNotNull(TaskManager.getInstance());
	}

	@Test
	public void testGetTasksCountList() {
		assertEquals(5, taskManager.getTasksCountList().size());
	}

	@Test
	public void testSetAndGetView() {
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.TODAY));
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.FUTURE));
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.TIMELESS));
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.EXPIRED));
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.ARCHIVE));
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.INVALID));
	}

	@Test
	public void testGetCurrentView() {
		assertNotNull(taskManager.getCurrentView());
	}

	@Test
	public void testSetSearchView() {
		taskManager.setSearchView(taskManager.getCurrentView());
		assertNotNull(taskManager.getSearchView());
		taskManager.setSearchView(taskManager.getAllPresentTasks());
		assertNotNull(taskManager.getSearchView());
		taskManager.setSearchView(taskManager.getAllArchivedTasks());
		assertNotNull(taskManager.getSearchView());
	}

	@Test
	public void testGetTaskFromViewByName() {
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.TODAY));
		assertEquals(taskManager.getTaskFromViewByName("something else"), todayTask);
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.FUTURE));
		assertEquals(taskManager.getTaskFromViewByName("hi there"), futureTask);
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.TIMELESS));
		assertEquals(taskManager.getTaskFromViewByName("something"), floatingTask);
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.EXPIRED));
		assertEquals(taskManager.getTaskFromViewByName("hi"), expiredTask);
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.ARCHIVE));
		assertEquals(taskManager.getTaskFromViewByName("another task"), archivedTask);
	}

	@Test
	public void testGetTaskFromViewByID() {
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.TODAY));
		assertEquals(taskManager.getTaskFromViewByID(0), todayTask);
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.FUTURE));
		assertEquals(taskManager.getTaskFromViewByID(0), futureTask);
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.TIMELESS));
		assertEquals(taskManager.getTaskFromViewByID(0), floatingTask);
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.EXPIRED));
		assertEquals(taskManager.getTaskFromViewByID(0), expiredTask);
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.ARCHIVE));
		assertEquals(taskManager.getTaskFromViewByID(0), archivedTask);
	}

	@Test
	public void testGetTaskPositionInView() {
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.TODAY));
		assertEquals(taskManager.getTaskPositionInView(todayTask), 0);
	}

	@Test
	public void testGetAllPresentTasks() {
		assertNotNull(taskManager.getAllPresentTasks());
	}

	@Test
	public void testGetAllArchivedTasks() {
		assertNotNull(taskManager.getAllArchivedTasks());
	}

	@Test
	public void testCount() {
		assertEquals(taskManager.countArchivedTasks(), taskDepo.countArchivedTasks());
		assertEquals(taskManager.countPresentTasks(), taskDepo.countPresentTasks());
	}

	@Test
	public void testAdd() {
		taskManager.clearPresentTasks();
		taskManager.clearArchivedTasks();
		assertEquals(0, taskManager.countPresentTasks());
		assertEquals(0, taskManager.countArchivedTasks());
		assertTrue(taskManager.addPresentTask(floatingTask));
		assertTrue(taskManager.addPresentTask(todayTask));
		assertTrue(taskManager.addPresentTask(futureTask));
		assertTrue(taskManager.addPresentTask(expiredTask));
		assertTrue(taskManager.addArchivedTask(archivedTask));
		assertEquals(4, taskManager.countPresentTasks());
		assertEquals(1, taskManager.countArchivedTasks());
	}

	@Test
	public void testRemoveTask() {
		assertEquals(4, taskManager.countPresentTasks());
		assertEquals(1, taskManager.countArchivedTasks());
		//Valid removal
		taskManager.removeTask(floatingTask);
		assertEquals(3, taskManager.countPresentTasks());
		taskManager.removeTask(todayTask);
		assertEquals(2, taskManager.countPresentTasks());
		taskManager.removeTask(futureTask);
		assertEquals(1, taskManager.countPresentTasks());
		
		//Remove non-existent task
		taskManager.removeTask(futureTask);
		assertEquals(1, taskManager.countPresentTasks());
		taskManager.removeTask(expiredTask);
		assertEquals(0, taskManager.countPresentTasks());
		taskManager.removeTask(archivedTask);
		assertEquals(0, taskManager.countArchivedTasks());
		
		//Boundary case where remove from empty list
		taskManager.removeTask(expiredTask);
		assertEquals(0, taskManager.countPresentTasks());
		taskManager.removeTask(archivedTask);
		assertEquals(0, taskManager.countArchivedTasks());
	}

	@Test
	public void testUpdateTask() {
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.TODAY));
		assertEquals(1, taskManager.getCurrentView().size());
		assertEquals(todayTask, taskManager.getCurrentView().get(0));
		
		taskManager.updateTask(futureTask, todayTask);  //Change from today task to future task
		assertNotNull(taskManager.setAndGetView(DISPLAY_STATE.FUTURE));
		assertEquals(2, taskManager.getCurrentView().size());
	}

	@Test
	public void testDoneTask() {
		assertEquals(4, taskManager.countPresentTasks());
		assertEquals(1, taskManager.countArchivedTasks());
		
		taskManager.doneTask(floatingTask);
		assertTrue(floatingTask.isDone());
		assertEquals(3, taskManager.countPresentTasks());
		assertEquals(2, taskManager.countArchivedTasks());
		taskManager.doneTask(todayTask);
		assertTrue(todayTask.isDone());
		taskManager.doneTask(futureTask);
		assertTrue(futureTask.isDone());
		taskManager.doneTask(expiredTask);
		assertTrue(expiredTask.isDone());
		
		//Setting of a task that is already done
		assertTrue(archivedTask.isDone());
		taskManager.doneTask(archivedTask);
		assertTrue(archivedTask.isDone());
		
		assertEquals(0, taskManager.countPresentTasks());
		assertEquals(5, taskManager.countArchivedTasks());		
	}

	@Test
	public void testUndoneTask() {
		assertEquals(4, taskManager.countPresentTasks());
		assertEquals(1, taskManager.countArchivedTasks());
		
		assertTrue(archivedTask.isDone());
		taskManager.undoneTask(archivedTask);
		assertFalse(archivedTask.isDone());
		
		assertEquals(5, taskManager.countPresentTasks());
		assertEquals(0, taskManager.countArchivedTasks());
		
		//Setting of a task that is already undone
		assertFalse(floatingTask.isDone());
		taskManager.undoneTask(floatingTask);
		assertFalse(floatingTask.isDone());
	}

	@Test
	public void testClearPresentTasks() {
		taskManager.clearPresentTasks();
		assertEquals(0, taskManager.getAllPresentTasks().size());
	}

	@Test
	public void testClearArchivedTasks() {
		taskManager.clearArchivedTasks();
		assertEquals(0, taskManager.getAllArchivedTasks().size());
	}

	@Test
	public void testAddToSearchView() {
		taskManager.clearSearchView();
		assertEquals(0, taskManager.getSearchView().size());
		taskManager.addToSearchView(floatingTask);
		taskManager.addToSearchView(archivedTask);
		assertEquals(2, taskManager.getSearchView().size());
	}

	@Test
	public void testDeleteInSearchView() {
		testAddToSearchView();
		assertEquals(2, taskManager.getSearchView().size());

		//Remove valid task
		taskManager.deleteInSearchView(floatingTask);
		assertEquals(1, taskManager.getSearchView().size());

		//Remove non-existent task
		taskManager.deleteInSearchView(futureTask);
		assertEquals(1, taskManager.getSearchView().size());

		taskManager.deleteInSearchView(archivedTask);
		assertEquals(0, taskManager.getSearchView().size());

		//Remove task in empty list
		taskManager.deleteInSearchView(todayTask);
		assertEquals(0, taskManager.getSearchView().size());
	}

	@Test
	public void testUpdateInSearchView() {
		testAddToSearchView();
		assertEquals(2, taskManager.getSearchView().size());
		taskManager.updateInSearchView(floatingTask, archivedTask);
		assertEquals(floatingTask, taskManager.getSearchView().get(0));
		assertEquals(floatingTask, taskManager.getSearchView().get(1));
	}

	@Test
	public void testClearSearchView() {
		taskManager.clearSearchView();
		assertEquals(0, taskManager.getSearchView().size());
	}

	@Test
	public void testStoreAndLoad() throws IOException {
		taskManager.store();
		checkTextFile();
		
		taskManager.load();
		assertEquals(8, taskManager.countPresentTasks());
		assertEquals(2, taskManager.countArchivedTasks());
	}

	@Test
	public void testAddToHistory() {
		history.clear();
		assertEquals(0, history.size());

		//Valid case
		taskManager.addToHistory(commandAdd);
		taskManager.addToHistory(commandClear);
		taskManager.addToHistory(commandDone);
		taskManager.addToHistory(commandUndone);
		taskManager.addToHistory(commandDone);
		assertEquals(5, history.size());
		taskManager.addToHistory(commandClear);
		taskManager.addToHistory(commandClear);
		taskManager.addToHistory(commandDone);
		taskManager.addToHistory(commandClear);
		taskManager.addToHistory(commandClear);
		assertEquals(10, history.size());

		//Boundary case where more than 10 commands will be added
		taskManager.addToHistory(commandUndone);
		taskManager.addToHistory(commandDone);
		assertEquals(10, history.size());
	}

	@Test
	public void testGetMostRecentCommand() {
		history.clear();
		assertEquals(0, history.size());

		//Boundary case where pop empty list
		assertNull(taskManager.getMostRecentCommand());

		//Valid cases
		taskManager.addToHistory(commandAdd);
		taskManager.addToHistory(commandClear);
		assertEquals(commandClear, taskManager.getMostRecentCommand());
		assertEquals(commandAdd, taskManager.getMostRecentCommand());
		assertEquals(0, history.size());

		//Call previously function to invoke boundary case
		testAddToHistory();
		assertEquals(10, history.size());
		assertEquals(commandDone, taskManager.getMostRecentCommand());
		assertEquals(commandUndone, taskManager.getMostRecentCommand());
		assertEquals(8, history.size());
	}
	
	private void checkTextFile() throws IOException {
		LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(new File("storageTest.txt")));
		lineNumberReader.skip(Long.MAX_VALUE);  //Long.MAX_VALUE is more than 2 ExaBytes
		assertEquals(taskDepo.countAllTasks(), lineNumberReader.getLineNumber());
		lineNumberReader.close();
	}

	private TaskInfo setFloatingTask() {
		TaskInfo task = new TaskInfo();
		task.setTaskName("something");
		task.setTaskType(TASK_TYPE.FLOATING);
		task.setStartDate(null);
		task.setEndDate(null);
		task.setPriority(3);
		task.setDone(false);
		task.setExpiry(false);
		return task;
	}

	private TaskInfo setTodayTask() {
		TaskInfo task = new TaskInfo();
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();

		task.setTaskName("something else");
		task.setTaskType(TASK_TYPE.TIMED);
		task.setStartDate(startDate);
		task.setEndDate(endDate);
		task.setPriority(5);
		task.setDone(false);
		task.setExpiry(false);
		return task;
	}

	private TaskInfo setFutureTask() {
		TaskInfo task = new TaskInfo();
		Calendar endDate = Calendar.getInstance();
		endDate.set(2014,12,12,23,59);  //Set a future end date

		task.setTaskName("hi there");
		task.setTaskType(TASK_TYPE.DEADLINE);
		task.setStartDate(null);
		task.setEndDate(endDate);
		task.setPriority(1);
		task.setDone(false);
		task.setExpiry(false);
		return task;
	}

	private TaskInfo setExpiredTask() {
		TaskInfo task = new TaskInfo();
		Calendar endDate = Calendar.getInstance();
		endDate.set(2014,1,1,0,0);  //Set to a date that is expired

		task.setTaskName("hi");
		task.setTaskType(TASK_TYPE.DEADLINE);
		task.setStartDate(null);
		task.setEndDate(endDate);
		task.setPriority(1);
		task.setDone(false);
		task.setExpiry(false);
		return task;
	}

	private TaskInfo setArchivedTask() {
		TaskInfo task = new TaskInfo();

		task.setTaskName("another task");
		task.setTaskType(TASK_TYPE.FLOATING);
		task.setStartDate(null);
		task.setEndDate(null);
		task.setPriority(1);
		task.setDone(true);
		task.setExpiry(false);
		return task;
	}

	private void populateTaskDepo() {
		taskDepo.clearAllTasks();
		assertEquals(0, taskDepo.countPresentTasks());
		assertTrue(taskDepo.addTaskToPresentList(floatingTask));
		assertTrue(taskDepo.addTaskToPresentList(todayTask));
		assertTrue(taskDepo.addTaskToPresentList(futureTask));
		assertTrue(taskDepo.addTaskToPresentList(expiredTask));
		assertTrue(taskDepo.addTaskToPresentList(archivedTask));
		assertEquals(5, taskDepo.countPresentTasks());
		assertEquals(0, taskDepo.countArchivedTasks());

		taskDepo.refreshTasks();
		assertEquals(4, taskDepo.countPresentTasks());
		assertEquals(1, taskDepo.countArchivedTasks());
	}
}
