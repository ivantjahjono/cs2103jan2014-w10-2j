//@author A0096670W

package KaboomTest;

import static org.junit.Assert.*;

import java.util.Calendar;

import kaboom.shared.TASK_TYPE;
import kaboom.shared.TaskInfo;
import kaboom.storage.TaskDepository;

import org.junit.Before;
import org.junit.Test;

public class TaskListShopTest {

	TaskDepository allTasks;
	TaskInfo newTask;
	TaskInfo oldTask;

	@Before
	public void initialize() {
		allTasks = TaskDepository.getInstance();
		newTask = new TaskInfo();
		oldTask = new TaskInfo();
		newTaskInfo(newTask);
		oldTaskInfo(oldTask);
	}

	public void newTaskInfo(TaskInfo newTask) {
		Calendar startDate = Calendar.getInstance();
		startDate.set(2014,1,1,8,0);
		Calendar endDate = Calendar.getInstance();
		endDate.set(2014,1,5,8,0);

		newTask.setTaskName("something");
		newTask.setTaskType(TASK_TYPE.FLOATING);
		newTask.setStartDate(startDate);
		newTask.setEndDate(endDate);
		newTask.setPriority(3);
	}
	
	public void oldTaskInfo(TaskInfo oldTask) {
		Calendar startDate = Calendar.getInstance();
		startDate.set(2014,1,1,8,0);
		Calendar endDate = Calendar.getInstance();
		endDate.set(2014,1,5,8,0);

		oldTask.setTaskName("something else");
		oldTask.setTaskType(TASK_TYPE.TIMED);
		oldTask.setStartDate(startDate);
		oldTask.setEndDate(endDate);
		oldTask.setPriority(3);
	}

	@Test
	public void testGetInstance() {
		assertNotNull(allTasks);
	}

	@Test
	public void testAddTaskToList() {
		assertTrue(allTasks.addTaskToList(oldTask));
		assertTrue(allTasks.addTaskToList(newTask));
	}

	@Test
	public void testGetTaskByName() {
		assertEquals("something", allTasks.getTaskByName("something").getTaskName());
	}

	@Test
	public void testUpdateTask() {
		allTasks.clearAllCurrentTasks();
		assertTrue(allTasks.addTaskToList(oldTask));
		assertTrue(allTasks.addTaskToList(newTask));
		allTasks.updateTask(newTask, oldTask);
		assertEquals(newTask, allTasks.getTaskByName("something"));
	}

	@Test
	public void testGetAllTaskInList() {
		assertNotNull(allTasks.getAllCurrentTasks());
	}

	@Test
	public void testGetFloatingTasks() {
		allTasks.clearAllCurrentTasks();
		assertEquals(0, allTasks.shopSize());
		assertTrue(allTasks.addTaskToList(oldTask));
		assertTrue(allTasks.addTaskToList(newTask));
		assertEquals("something", allTasks.getFloatingTasks().get(0).getTaskName());
		assertTrue(allTasks.addTaskToList(newTask));
		assertEquals(2, allTasks.getFloatingTasks().size());
	}

	@Test
	public void testGetTimedTasks() {
		allTasks.clearAllCurrentTasks();
		assertEquals(0, allTasks.shopSize());
		assertTrue(allTasks.addTaskToList(oldTask));
		assertTrue(allTasks.addTaskToList(newTask));
		assertEquals("something else", allTasks.getTimedTasks().get(0).getTaskName());
		assertTrue(allTasks.addTaskToList(oldTask));
		assertEquals(2, allTasks.getTimedTasks().size());
	}

	@Test
	public void testClearAllTasks() {
		assertTrue(allTasks.addTaskToList(oldTask));
		assertNotEquals(0, allTasks.shopSize());
		allTasks.clearAllCurrentTasks();
		assertEquals(0, allTasks.shopSize());
	}

	@Test
	public void testShopSize() {
		allTasks.clearAllCurrentTasks();
		assertEquals(0, allTasks.shopSize());
		assertTrue(allTasks.addTaskToList(oldTask));
		assertEquals(1, allTasks.shopSize());
		assertTrue(allTasks.addTaskToList(newTask));
		assertEquals(2, allTasks.shopSize());
	}
}
