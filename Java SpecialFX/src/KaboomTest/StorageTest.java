//@author A0096670W

/**
 * StorageTest.java:
 * This class tests the storage on whether it stores and loads information properly.
 * There are two boundary cases, no tasks to store/load or the file size limit.
 * Since it is not feasible to test the latter boundary, only the former will be tested.
 */

package KaboomTest;

import static org.junit.Assert.*;

import java.io.LineNumberReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import kaboom.shared.TASK_TYPE;
import kaboom.shared.TaskInfo;
import kaboom.storage.Storage;
import kaboom.storage.TaskDepository;

import org.junit.Before;
import org.junit.Test;

public class StorageTest {
	
	TaskDepository taskDepo;
	Storage storageTest;
	LineNumberReader lineNumberReader;

	private final int EMPTY = 0;
	private final String TEST_FILE_NAME = "StorageTest.txt";
	
	TaskInfo floatingTask;
	TaskInfo todayTask;
	TaskInfo futureTask;
	TaskInfo expiredTask;
	TaskInfo archivedTask;
	
	@Before
	public void initailize() {
		taskDepo = TaskDepository.getInstance();
		populateTaskDepo();
		assertNotNull(taskDepo);
		storageTest = new Storage(TEST_FILE_NAME);
		assertNotNull(storageTest);
		
		floatingTask = setFloatingTask();
		todayTask = setTodayTask();
		futureTask = setFutureTask();
		expiredTask = setExpiredTask();
		archivedTask = setArchivedTask();
		populateTaskDepo();
	}
	
	/**
	 * This function tests if the loading of the text file into task depository is successful.
	 * A LineNumberReader object is used and the whole file is skipped to the last line.
	 * Since Long.MAX_VALUE is more than 2 ExaBytes, skipping by that value will guarantee
	 * that the end of file is reached. 
	 * The line number is then obtained and compared with the task count in task depository.
	 * @throws IOException
	 */
	@Test
	public void testLoad() throws IOException {
		taskDepo.clearAllTasks();
		storageTest.load();
		lineNumberReader = new LineNumberReader(new FileReader(new File("storageTest.txt")));
		lineNumberReader.skip(Long.MAX_VALUE);  //Long.MAX_VALUE is more than 2 ExaBytes
		assertEquals(taskDepo.countAllTasks(), lineNumberReader.getLineNumber());
		lineNumberReader.close();
	}

	/**
	 * This function tests if the saving of tasks from task depository
	 *  to the text file is successful.
	 * A LineNumberReader object is used and the whole file is skipped to 
	 * the last line after storing
	 * is performed.
	 * Since Long.MAX_VALUE is more than 2 ExaBytes, skipping by that value 
	 * will guarantee that the end of file is reached. 
	 * The line number is then obtained and compared with the task count in task depository.
	 * @throws IOException
	 */
	@Test
	public void testStore() throws IOException {
		//Test usual case where there are tasks to store
		storageTest.store();
		lineNumberReader = new LineNumberReader(new FileReader(new File("storageTest.txt")));
		lineNumberReader.skip(Long.MAX_VALUE);  //Long.MAX_VALUE is more than 2 ExaBytes
		assertEquals(taskDepo.countAllTasks(), lineNumberReader.getLineNumber());
		
		//Test boundary case where there is no task in task depository
		taskDepo.clearAllTasks();
		assertEquals(EMPTY, taskDepo.countAllTasks());
		storageTest.store();
		lineNumberReader = new LineNumberReader(new FileReader(new File("storageTest.txt")));
		lineNumberReader.skip(Long.MAX_VALUE);  //Long.MAX_VALUE is more than 2 ExaBytes
		assertEquals(taskDepo.countAllTasks(), lineNumberReader.getLineNumber());
		lineNumberReader.close();
		
		//It is not feasible to test the other boundary case where the taskDepo takes up
		//the full memory allocated for it
	}
	
	private void populateTaskDepo() {
		taskDepo.clearAllTasks();
		assertEquals(0, taskDepo.countPresentTasks());
		assertTrue(taskDepo.addTaskToPresentList(floatingTask));
		assertTrue(taskDepo.addTaskToPresentList(todayTask));
		assertTrue(taskDepo.addTaskToPresentList(futureTask));
		assertTrue(taskDepo.addTaskToPresentList(expiredTask));
		assertTrue(taskDepo.addTaskToArchivedList(archivedTask));
		assertEquals(4, taskDepo.countPresentTasks());
		assertEquals(1, taskDepo.countArchivedTasks());
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
