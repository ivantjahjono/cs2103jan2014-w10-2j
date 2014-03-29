package KaboomTest;

import static org.junit.Assert.*;

import java.util.Calendar;

import kaboom.logic.TASK_TYPE;
import kaboom.logic.TaskInfo;
import kaboom.logic.command.Command;
import kaboom.logic.command.CommandAdd;
import kaboom.logic.command.CommandDelete;
import kaboom.logic.command.CommandModify;
import kaboom.logic.command.CommandView;
import kaboom.storage.Storage;
import kaboom.storage.TaskListShop;

import org.junit.Before;
import org.junit.Test;

public class CommandTest {
	TaskInfo task;
	Storage fileStorage;
	TaskListShop shop;
	
	@Before
	public void initialise() {
		fileStorage = new Storage("BOOMTEST.dat");
		fileStorage.load();
		shop = TaskListShop.getInstance();
		task = new TaskInfo();
		taskInfoUpdate(task);
	}
	
	public void taskInfoUpdate(TaskInfo task) {
		Calendar startDate = Calendar.getInstance();
		startDate.set(2014,1,1,8,0);
		Calendar endDate = Calendar.getInstance();
		endDate.set(2014,1,5,8,0);
		
		task.setTaskName("Hello World");
		task.setTaskType(TASK_TYPE.TIMED);
		task.setStartDate(startDate);
		task.setEndDate(endDate);
		task.setImportanceLevel(3);
	}
	
	
	//CommandAdd
	@Test
	public void testCommandAdd() {
		Command com = new CommandAdd();
		
		//Initial test when no taskinfo is inside to be executed
		assertEquals("Added a file with no Task Name", com.execute().getFeedback());
		
		//Test Command feedback
		com.setTaskInfo(task);
		assertEquals("Successfully added Hello World", com.execute().getFeedback());
		task.setTaskName("hello world ");
		assertEquals("Successfully added hello world ", com.execute().getFeedback());
	}
	
	//CommandDelete (Unable to test unless memory is initialised);
	@Test
	public void testCommandDelete() {
		Command com = new CommandDelete();
		com.setTaskInfo(task);
		//Test when no taskinfo in memory to be deleted
		assertEquals("Hello World fail to delete.", com.execute().getFeedback());
		
		//Delete by name
		task.setTaskName("abc");
		assertEquals("abc fail to delete.", com.execute().getFeedback());
	}

	//CommandModify (Unable to test unless memory is initialised);
	@Test
	public void testCommandModify() {
		Command com = new CommandModify();
		//com.setTaskInfoToBeModified(task);
		//Test when no existing task to modify
		assertEquals("Fail to modify Hello World", com.execute().getFeedback());
	}
	
	//CommandView
	@Test
	public void testCommandView() {
		CommandView com = new CommandView();
		com.setViewType("");

		//Test Command feedback
		//No viewType set
		assertEquals("Invalid View Mode", com.execute().getFeedback());
		
//		//Valid ViewTypes
//		com.saveViewType("all");
//		com.setTaskInfo(task);
//		assertEquals("All Task Mode", com.execute().getFeedback());
//		
//		com.saveViewType("deadline");
//		com.setTaskInfo(task);
//		assertEquals("Deadline Task Mode", com.execute().getFeedback());
//		
//		com.saveViewType("running");
//		com.setTaskInfo(task);
//		assertEquals("Running Task Mode", com.execute().getFeedback());
//		
//		//Boundary 
//		//To be discussed whether to accept or no
//		com.saveViewType("all ");
//		com.setTaskInfo(task);
//		assertEquals("Invalid View Mode", com.execute().getFeedback());
//		
//		com.saveViewType(" all");
//		com.setTaskInfo(task);
//		assertEquals("Invalid View Mode", com.execute().getFeedback());
//		
//		com.saveViewType("all 123");
//		com.setTaskInfo(task);
//		assertEquals("Invalid View Mode", com.execute().getFeedback());
//		
//		//Invalid Types
//		com.saveViewType("alls");
//		com.setTaskInfo(task);
//		assertEquals("Invalid View Mode", com.execute().getFeedback());
		}
	
}
