package KaboomTest;

import static org.junit.Assert.*;

import java.util.Calendar;

import kaboom.logic.TASK_TYPE;
import kaboom.logic.TaskInfo;
import kaboom.storage.TaskListShop;

import org.junit.Before;
import org.junit.Test;

public class TaskListShopTest {

	TaskListShop allTasks;
	TaskInfo newTask;
	TaskInfo oldTask;

	@Before
	public void initialize() {
		allTasks = TaskListShop.getInstance();
		newTask = new TaskInfo();
		oldTask = new TaskInfo();
		newTaskInfoUpdate(newTask);
		oldTaskInfoUpdate(oldTask);
	}

	public void newTaskInfoUpdate(TaskInfo newTask) {
		Calendar startDate = Calendar.getInstance();
		startDate.set(2014,1,1,8,0);
		Calendar endDate = Calendar.getInstance();
		endDate.set(2014,1,5,8,0);

		newTask.setTaskName("something");
		newTask.setTaskType(TASK_TYPE.FLOATING);
		newTask.setStartDate(startDate);
		newTask.setEndDate(endDate);
		newTask.setImportanceLevel(3);
	}
	
	public void oldTaskInfoUpdate(TaskInfo oldTask) {
		Calendar startDate = Calendar.getInstance();
		startDate.set(2014,1,1,8,0);
		Calendar endDate = Calendar.getInstance();
		endDate.set(2014,1,5,8,0);

		oldTask.setTaskName("something else");
		oldTask.setTaskType(TASK_TYPE.TIMED);
		oldTask.setStartDate(startDate);
		oldTask.setEndDate(endDate);
		oldTask.setImportanceLevel(3);
	}

	@Test
	public void testGetInstance() {
		assertNotNull(allTasks);
	}

	@Test
	public void testAddTaskToList() {
		assertTrue(allTasks.addTaskToList(oldTask));
	}

	@Test
	public void testGetTaskByName() {
		assertEquals("something", allTasks.getTaskByName("something").getTaskName());
	}

	
	public void testUpdateTask() {
		allTasks.clearAllTasks();
		assertTrue(allTasks.addTaskToList(oldTask));
		assertTrue(allTasks.addTaskToList(newTask));
		allTasks.updateTask(newTask, oldTask);
		assertEquals(newTask, allTasks.getTaskByName("something else"));
	}

	@Test
	public void testGetAllTaskInList() {
		assertNotNull(allTasks.getAllTaskInList());
	}

	@Test
	public void testGetFloatingTasks() {
		allTasks.clearAllTasks();
		assertTrue(allTasks.addTaskToList(oldTask));
		assertTrue(allTasks.addTaskToList(newTask));
		assertEquals("something", allTasks.getFloatingTasks().get(0).getTaskName());
	}

	@Test
	public void testGetTimedTasks() {
		allTasks.clearAllTasks();
		assertTrue(allTasks.addTaskToList(oldTask));
		assertTrue(allTasks.addTaskToList(newTask));
		assertEquals("something else", allTasks.getTimedTasks().get(0).getTaskName());
	}

	@Test
	public void testRemoveTaskByName() {
		assertTrue(allTasks.removeTaskByName("something"));
	}

	@Test
	public void testClearAllTasks() {
		allTasks.clearAllTasks();
		assertEquals(0, allTasks.shopSize());
	}

	@Test
	public void testShopSize() {
		allTasks.clearAllTasks();
		assertTrue(allTasks.addTaskToList(oldTask));
		assertEquals(1, allTasks.shopSize());
		assertTrue(allTasks.addTaskToList(newTask));
		assertEquals(2, allTasks.shopSize());
	}
}