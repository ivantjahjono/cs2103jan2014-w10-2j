//@author A0096670W

/**
 * TaskDepositoryTest.java:
 * This class tests if the storing, loading, manipulation of tasks in
 * TaskDepository is working. 
 * Since the objects that are being handled are of TaskInfo type, 
 * the only boundary case is where the vector in TaskDepository is empty.
 */

package KaboomTest;

import static org.junit.Assert.*;

import java.util.Calendar;

import kaboom.shared.TASK_TYPE;
import kaboom.shared.TaskInfo;
import kaboom.storage.TaskDepository;

import org.junit.Before;
import org.junit.Test;

public class TaskDepositoryTest {

	TaskDepository taskDepo;
	TaskInfo floatingTask;
	TaskInfo todayTask;
	TaskInfo futureTask;
	TaskInfo expiredTask;
	TaskInfo archivedTask;

	@Before
	public void initialize() {
		taskDepo = TaskDepository.getInstance();
		floatingTask = setFloatingTask();
		todayTask = setTodayTask();
		futureTask = setFutureTask();
		expiredTask = setExpiredTask();
		archivedTask = setArchivedTask();
	}

	@Test
	public void testGetInstance() {
		assertNotNull(taskDepo);
	}

	@Test
	public void testAddTaskToPresentList() {
		taskDepo.clearAllPresentTasks();
		assertEquals(0, taskDepo.countPresentTasks());
		assertTrue(taskDepo.addTaskToPresentList(floatingTask));
		assertTrue(taskDepo.addTaskToPresentList(todayTask));
		assertTrue(taskDepo.addTaskToPresentList(futureTask));
		assertEquals(3, taskDepo.countPresentTasks());
	}
	
	@Test
	public void testAddTaskToArchivedList() {
		taskDepo.clearAllArchivedTasks();
		assertEquals(0, taskDepo.countArchivedTasks());
		assertTrue(taskDepo.addTaskToArchivedList(floatingTask));
		assertTrue(taskDepo.addTaskToArchivedList(todayTask));
		assertTrue(taskDepo.addTaskToArchivedList(futureTask));
		assertEquals(3, taskDepo.countArchivedTasks());
	}

	@Test
	public void testUpdateTask() {
		taskDepo.clearAllPresentTasks();
		assertEquals(0, taskDepo.getFloatingTasks().size());
		assertTrue(taskDepo.addTaskToPresentList(todayTask));
		assertTrue(taskDepo.addTaskToPresentList(floatingTask));
		assertEquals(1, taskDepo.getFloatingTasks().size());
		
		//Update timed task to floating task
		taskDepo.updateTask(floatingTask, todayTask);
		assertEquals(2, taskDepo.getFloatingTasks().size());
		
		//Update floating tasks to deadline tasks
		taskDepo.updateTask(futureTask, floatingTask);
		assertEquals(1, taskDepo.getFloatingTasks().size());
		taskDepo.updateTask(futureTask, floatingTask);
		assertEquals(0, taskDepo.getFloatingTasks().size());
		
		//Update deadline task to timed task
		taskDepo.updateTask(todayTask, futureTask);
		assertEquals(0, taskDepo.getFloatingTasks().size());
	}

	@Test
	public void testRemoveFromPresentList() {
		testAddTaskToPresentList();
		assertEquals(3, taskDepo.countPresentTasks());
		taskDepo.removeTask(floatingTask);  //Remove valid task
		assertEquals(2, taskDepo.countPresentTasks());
		taskDepo.removeTask(floatingTask);  //Remove non-existent task
		assertEquals(2, taskDepo.countPresentTasks());
		taskDepo.removeTask(todayTask);  //Remove valid task
		assertEquals(1, taskDepo.countPresentTasks());
		taskDepo.removeTask(futureTask);  //Remove valid task
		assertEquals(0, taskDepo.countPresentTasks());
		taskDepo.removeTask(futureTask);  //Boundary case for empty list
		assertEquals(0, taskDepo.countPresentTasks());
	}
	
	@Test
	public void testRemoveFromArchiveList() {
		testAddTaskToArchivedList();
		assertEquals(3, taskDepo.countArchivedTasks());
		taskDepo.removeTask(floatingTask);  //Remove valid task
		assertEquals(2, taskDepo.countArchivedTasks());
		taskDepo.removeTask(floatingTask);  //Remove non-existent task
		assertEquals(2, taskDepo.countArchivedTasks());
		taskDepo.removeTask(todayTask);  //Remove valid task
		assertEquals(1, taskDepo.countArchivedTasks());
		taskDepo.removeTask(futureTask);  //Remove valid task
		assertEquals(0, taskDepo.countArchivedTasks());
		taskDepo.removeTask(futureTask);  //Boundary case for empty list
		assertEquals(0, taskDepo.countArchivedTasks());
	}

	@Test
	public void testGetTasksInList() {
		assertNotNull(taskDepo.getAllPresentTasks());
		assertNotNull(taskDepo.getAllArchivedTasks());
		taskDepo.clearAllTasks();
		assertNotNull(taskDepo.getAllPresentTasks());
		assertNotNull(taskDepo.getAllArchivedTasks());
	}

	@Test
	public void testGetFloatingTasks() {
		taskDepo.clearAllPresentTasks();
		assertEquals(0, taskDepo.countPresentTasks());
		
		assertTrue(taskDepo.addTaskToPresentList(floatingTask));
		assertTrue(taskDepo.addTaskToPresentList(floatingTask));
		assertEquals("something", taskDepo.getFloatingTasks().get(0).getTaskName());
		assertEquals("something", taskDepo.getFloatingTasks().get(1).getTaskName());
		
		assertTrue(taskDepo.addTaskToPresentList(floatingTask));
		assertNotNull(taskDepo.getFloatingTasks());
		assertEquals(3, taskDepo.getFloatingTasks().size());
	}
	
	@Test
	public void testGetTodayTasks() {
		taskDepo.clearAllPresentTasks();
		assertTrue(taskDepo.addTaskToPresentList(todayTask));
		assertNotNull(taskDepo.getToday());
		assertEquals(1, taskDepo.getToday().size());
	}
	
	@Test
	public void testGetExpiredTasks() {
		taskDepo.clearAllPresentTasks();
		assertTrue(taskDepo.addTaskToPresentList(expiredTask));
		taskDepo.refreshTasks();
		assertNotNull(taskDepo.getExpiredTasks());
		assertEquals(1, taskDepo.getExpiredTasks().size());
	}
	
	@Test
	public void testGetFutureTasks() {
		taskDepo.clearAllPresentTasks();
		assertTrue(taskDepo.addTaskToPresentList(futureTask));
		assertNotNull(taskDepo.getFutureTasks());
		assertEquals(1, taskDepo.getFutureTasks().size());
	}
	
	@Test
	public void testRefreshTasks() {
		taskDepo.clearAllTasks();
		assertEquals(0, taskDepo.countAllTasks());
		
		assertFalse(expiredTask.isExpired());
		assertTrue(taskDepo.addTaskToPresentList(expiredTask));
		assertEquals(1, taskDepo.countPresentTasks());
		assertEquals(0, taskDepo.countArchivedTasks());
		taskDepo.refreshTasks();  //Refresh to detect and set expiry flag
		assertTrue(expiredTask.isExpired());
		
		expiredTask.setDone(true);
		taskDepo.refreshTasks();  //Refresh to move from present to archive
		assertFalse(expiredTask.isExpired());  //Done tasks are not expired
		assertEquals(0, taskDepo.countPresentTasks());
		assertEquals(1, taskDepo.countArchivedTasks());
		
		
		expiredTask.setDone(false);
		taskDepo.refreshTasks();  //Refresh to move from archive to present
		assertEquals(1, taskDepo.countPresentTasks());
		assertEquals(0, taskDepo.countArchivedTasks());
	}

	@Test
	public void testClearTasks() {
		assertTrue(taskDepo.addTaskToPresentList(todayTask));
		assertTrue(taskDepo.addTaskToArchivedList(futureTask));
		assertNotEquals(0, taskDepo.countPresentTasks());
		assertNotEquals(0, taskDepo.countArchivedTasks());
		
		taskDepo.clearAllTasks();
		assertEquals(0, taskDepo.countPresentTasks());
		assertEquals(0, taskDepo.countArchivedTasks());
		assertEquals(0, taskDepo.countAllTasks());
	}

	@Test
	public void testCount() {
		taskDepo.clearAllPresentTasks();
		assertEquals(0, taskDepo.countPresentTasks());
		assertEquals(0, taskDepo.countArchivedTasks());
		assertEquals(0, taskDepo.countAllTasks());
		
		assertTrue(taskDepo.addTaskToPresentList(todayTask));
		assertEquals(1, taskDepo.countPresentTasks());
		assertTrue(taskDepo.addTaskToArchivedList(floatingTask));
		assertEquals(1, taskDepo.countArchivedTasks());
		assertEquals(2, taskDepo.countAllTasks());
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
}
