//@author A0096670W

/**
 * TaskDepositoryTest.java:
 * This class tests if the storing, loading, manipulation of tasks in
 * TaskDepository is working. 
 * Since the objects that are being handled are of TaskInfo type, 
 * there is seemingly no boundary cases to take note of. 
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

	@Before
	public void initialize() {
		taskDepo = TaskDepository.getInstance();
		task1 = setTaskInfo1();
		task2 = setTaskInfo2();
		task3 = setTaskInfo3();
	}

	@Test
	public void testGetInstance() {
		assertNotNull(taskDepo);
	}

	@Test
	public void testAddTaskToList() {
		taskDepo.clearAllPresentTasks();
		assertEquals(0, taskDepo.countPresentTasks());
		assertTrue(taskDepo.addTaskToList(task2));
		assertTrue(taskDepo.addTaskToList(task1));
		assertTrue(taskDepo.addTaskToList(task1));
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
		assertTrue(taskDepo.addTaskToList(task2));
		assertTrue(taskDepo.addTaskToList(task1));
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
		
		assertTrue(taskDepo.addTaskToList(task1));
		assertTrue(taskDepo.addTaskToList(task1));
		assertEquals("something", taskDepo.getFloatingTasks().get(0).getTaskName());
		assertEquals("something", taskDepo.getFloatingTasks().get(1).getTaskName());
		
		assertTrue(taskDepo.addTaskToList(task1));
		assertNotNull(taskDepo.getFloatingTasks());
		assertEquals(3, taskDepo.getFloatingTasks().size());
	}

	@Test
	public void testClearTasks() {
		assertTrue(taskDepo.addTaskToList(task2));
		assertTrue(taskDepo.addTaskToArchivedList(task3));
		assertNotEquals(0, taskDepo.countPresentTasks());
		assertNotEquals(0, taskDepo.countArchivedTasks());
		
		taskDepo.clearAllTasks();
		assertEquals(0, taskDepo.countPresentTasks());
		assertEquals(0, taskDepo.countArchivedTasks());
	}

	@Test
	public void testCount() {
		taskDepo.clearAllPresentTasks();
		assertEquals(0, taskDepo.countPresentTasks());
		assertEquals(0, taskDepo.countArchivedTasks());
		assertEquals(0, taskDepo.countTotal());
		
		assertTrue(taskDepo.addTaskToList(task2));
		assertEquals(1, taskDepo.countPresentTasks());
		assertTrue(taskDepo.addTaskToArchivedList(task1));
		assertEquals(1, taskDepo.countArchivedTasks());
		assertEquals(2, taskDepo.countTotal());
	}

	private TaskInfo setTaskInfo1() {
		TaskInfo task = new TaskInfo();
		task.setTaskName("something");
		task.setTaskType(TASK_TYPE.FLOATING);
		task.setStartDate(null);
		task.setEndDate(null);
		task.setPriority(3);
		return task;
	}

	private TaskInfo setTaskInfo2() {
		TaskInfo task = new TaskInfo();
		Calendar startDate = Calendar.getInstance();
		startDate.set(2014,4,14,8,0);
		Calendar endDate = Calendar.getInstance();
		endDate.set(2014,4,18,8,0);

		task.setTaskName("something else");
		task.setTaskType(TASK_TYPE.TIMED);
		task.setStartDate(startDate);
		task.setEndDate(endDate);
		task.setPriority(5);
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
		return task;
	}
}
