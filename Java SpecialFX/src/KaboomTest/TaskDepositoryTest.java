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
	TaskInfo task1;
	TaskInfo task2;
	TaskInfo task3;
	TaskInfo task4;

	@Before
	public void initialize() {
		taskDepo = TaskDepository.getInstance();
		task1 = setTaskInfo1();
		task2 = setTaskInfo2();
		task3 = setTaskInfo3();
		task4 = setTaskInfo4();
	}

	@Test
	public void testGetInstance() {
		assertNotNull(taskDepo);
	}

	@Test
	public void testAddTaskToPresentList() {
		taskDepo.clearAllPresentTasks();
		assertEquals(0, taskDepo.countPresentTasks());
		assertTrue(taskDepo.addTaskToPresentList(task1));
		assertTrue(taskDepo.addTaskToPresentList(task2));
		assertTrue(taskDepo.addTaskToPresentList(task3));
		assertEquals(3, taskDepo.countPresentTasks());
	}
	
	@Test
	public void testAddTaskToArchivedList() {
		taskDepo.clearAllArchivedTasks();
		assertEquals(0, taskDepo.countArchivedTasks());
		assertTrue(taskDepo.addTaskToArchivedList(task1));
		assertTrue(taskDepo.addTaskToArchivedList(task2));
		assertTrue(taskDepo.addTaskToArchivedList(task3));
		assertEquals(3, taskDepo.countArchivedTasks());
	}

	@Test
	public void testUpdateTask() {
		taskDepo.clearAllPresentTasks();
		assertEquals(0, taskDepo.getFloatingTasks().size());
		assertTrue(taskDepo.addTaskToPresentList(task2));
		assertTrue(taskDepo.addTaskToPresentList(task1));
		assertEquals(1, taskDepo.getFloatingTasks().size());
		
		//Update timed task to floating task
		taskDepo.updateTask(task1, task2);
		assertEquals(2, taskDepo.getFloatingTasks().size());
		
		//Update floating tasks to deadline tasks
		taskDepo.updateTask(task3, task1);
		assertEquals(1, taskDepo.getFloatingTasks().size());
		taskDepo.updateTask(task3, task1);
		assertEquals(0, taskDepo.getFloatingTasks().size());
		
		//Update deadline task to timed task
		taskDepo.updateTask(task2, task3);
		assertEquals(0, taskDepo.getFloatingTasks().size());
	}

	@Test
	public void testRemoveFromPresentList() {
		testAddTaskToPresentList();
		assertEquals(3, taskDepo.countPresentTasks());
		taskDepo.removeTask(task1);  //Remove valid task
		assertEquals(2, taskDepo.countPresentTasks());
		taskDepo.removeTask(task1);  //Remove non-existent task
		assertEquals(2, taskDepo.countPresentTasks());
		taskDepo.removeTask(task2);  //Remove valid task
		assertEquals(1, taskDepo.countPresentTasks());
		taskDepo.removeTask(task3);  //Remove valid task
		assertEquals(0, taskDepo.countPresentTasks());
		taskDepo.removeTask(task3);  //Boundary case for empty list
		assertEquals(0, taskDepo.countPresentTasks());
	}
	
	@Test
	public void testRemoveFromArchiveList() {
		testAddTaskToArchivedList();
		assertEquals(3, taskDepo.countArchivedTasks());
		taskDepo.removeTask(task1);  //Remove valid task
		assertEquals(2, taskDepo.countArchivedTasks());
		taskDepo.removeTask(task1);  //Remove non-existent task
		assertEquals(2, taskDepo.countArchivedTasks());
		taskDepo.removeTask(task2);  //Remove valid task
		assertEquals(1, taskDepo.countArchivedTasks());
		taskDepo.removeTask(task3);  //Remove valid task
		assertEquals(0, taskDepo.countArchivedTasks());
		taskDepo.removeTask(task3);  //Boundary case for empty list
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
		
		assertTrue(taskDepo.addTaskToPresentList(task1));
		assertTrue(taskDepo.addTaskToPresentList(task1));
		assertEquals("something", taskDepo.getFloatingTasks().get(0).getTaskName());
		assertEquals("something", taskDepo.getFloatingTasks().get(1).getTaskName());
		
		assertTrue(taskDepo.addTaskToPresentList(task1));
		assertNotNull(taskDepo.getFloatingTasks());
		assertEquals(3, taskDepo.getFloatingTasks().size());
	}
	
	@Test
	public void testGetTodayTasks() {
		taskDepo.clearAllPresentTasks();
		assertTrue(taskDepo.addTaskToPresentList(task2));
		assertNotNull(taskDepo.getToday());
		assertEquals(1, taskDepo.getToday().size());
	}
	
	@Test
	public void testGetExpiredTasks() {
		taskDepo.clearAllPresentTasks();
		assertTrue(taskDepo.addTaskToPresentList(task4));
		taskDepo.refreshTasks();
		assertNotNull(taskDepo.getExpiredTasks());
		assertEquals(1, taskDepo.getExpiredTasks().size());
	}
	
	@Test
	public void testGetFutureTasks() {
		taskDepo.clearAllPresentTasks();
		assertTrue(taskDepo.addTaskToPresentList(task2));
		assertNotNull(taskDepo.getFutureTasks());
		assertEquals(1, taskDepo.getFutureTasks().size());
	}
	
	@Test
	public void testRefreshTasks() {
		taskDepo.clearAllTasks();
		assertEquals(0, taskDepo.countAllTasks());
		
		assertFalse(task4.isExpired());
		assertTrue(taskDepo.addTaskToPresentList(task4));
		assertEquals(1, taskDepo.countPresentTasks());
		assertEquals(0, taskDepo.countArchivedTasks());
		taskDepo.refreshTasks();  //Refresh to detect and set expiry flag
		assertTrue(task4.isExpired());
		
		task4.setDone(true);
		taskDepo.refreshTasks();  //Refresh to move from present to archive
		assertFalse(task4.isExpired());  //Done tasks are not expired
		assertEquals(0, taskDepo.countPresentTasks());
		assertEquals(1, taskDepo.countArchivedTasks());
		
		
		task4.setDone(false);
		taskDepo.refreshTasks();  //Refresh to move from archive to present
		assertEquals(1, taskDepo.countPresentTasks());
		assertEquals(0, taskDepo.countArchivedTasks());
	}

	@Test
	public void testClearTasks() {
		assertTrue(taskDepo.addTaskToPresentList(task2));
		assertTrue(taskDepo.addTaskToArchivedList(task3));
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
		
		assertTrue(taskDepo.addTaskToPresentList(task2));
		assertEquals(1, taskDepo.countPresentTasks());
		assertTrue(taskDepo.addTaskToArchivedList(task1));
		assertEquals(1, taskDepo.countArchivedTasks());
		assertEquals(2, taskDepo.countAllTasks());
	}

	private TaskInfo setTaskInfo1() {
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

	private TaskInfo setTaskInfo2() {
		TaskInfo task = new TaskInfo();
		Calendar now = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		endDate.set(2020,12,12,23,59);

		task.setTaskName("something else");
		task.setTaskType(TASK_TYPE.TIMED);
		task.setStartDate(now);
		task.setEndDate(endDate);
		task.setPriority(5);
		task.setDone(false);
		task.setExpiry(false);
		return task;
	}

	private TaskInfo setTaskInfo3() {
		TaskInfo task = new TaskInfo();
		Calendar endDate = Calendar.getInstance();
		endDate.set(2014,4,15,15,0);

		task.setTaskName("hi there");
		task.setTaskType(TASK_TYPE.DEADLINE);
		task.setStartDate(null);
		task.setEndDate(endDate);
		task.setPriority(1);
		task.setDone(false);
		task.setExpiry(false);
		return task;
	}
	
	private TaskInfo setTaskInfo4() {
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
}
