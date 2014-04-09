package KaboomTest;

import static org.junit.Assert.*;

import java.util.Calendar;

import kaboom.logic.DateAndTimeFormat;
import kaboom.logic.TASK_TYPE;
import kaboom.logic.TaskInfo;
import kaboom.logic.command.Command;
import kaboom.logic.command.CommandAdd;
import kaboom.logic.command.CommandDelete;
import kaboom.logic.command.CommandFactory;
import kaboom.logic.command.CommandModify;
import kaboom.logic.command.CommandView;
import kaboom.storage.Storage;
import kaboom.storage.TaskListShop;
import kaboom.ui.DISPLAY_STATE;

import org.junit.Before;
import org.junit.Test;

public class CommandTest {
	TaskInfo task;
	Storage fileStorage;
	TaskListShop shop;
	DateAndTimeFormat date;
	
	@Before
	public void initialise() {
//		fileStorage = new Storage("BOOMTEST.dat");
//		fileStorage.load();
		shop = TaskListShop.getInstance();
		task = new TaskInfo();
		taskInfoUpdate(task);
		date = DateAndTimeFormat.getInstance();
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
		task.setPriority(3);
	}
	
	
	//CommandAdd (outdated)
	@Test
	public void testCommandAdd() {
		CommandAdd com = new CommandAdd();
//		
//		//Initial test when no taskinfo is inside to be executed
//		assertEquals("Enter a task name please :'(", com.execute().getFeedback());
//		
//		//Test Command feedback
//		com.setTaskInfo(task);
//		assertEquals("WOOT! <Hello World> ADDED. MORE STUFF TO DO!", com.execute().getFeedback());
//		task.setTaskName("hello world");
//		assertEquals("WOOT! <hello world> ADDED. MORE STUFF TO DO!", com.execute().getFeedback());
	}
	
	//CommandDelete (Unable to test unless memory is initialised);
	@Test
	public void testCommandDelete() {
		CommandDelete com = new CommandDelete();
	//	com.setTaskInfo(task);
		//Test when no taskinfo in memory to be deleted
//		assertEquals("<Hello World> does not exist...", com.execute().getFeedback());
//		
//		//Delete by name
//		task.setTaskName("abc");
//		assertEquals("<abc> does not exist...", com.execute().getFeedback());
	}

	//CommandModify (Unable to test unless memory is initialised);
	@Test
	public void testCommandModify() {
		CommandModify com = new CommandModify();
		com.setPreModifiedTask(task);
		//Test when no existing task to modify
//		assertEquals("Fail to cast a spell on <Hello World>", com.execute().getFeedback());
	}
	
	//CommandView
	@Test
	public void testCommandView() {
		String viewString = "view";
		
		Command currentCommand = null;
		CommandView com = new CommandView();

		//Test Command feedback
		//No viewType set
		assertEquals("Invalid View Mode", com.execute().getFeedback());
		
		//Valid ViewTypes
		com.setDisplayState(DISPLAY_STATE.TODAY);
		assertEquals("Viewing all the tasks for today", com.execute().getFeedback());
		
		com.setDisplayState(DISPLAY_STATE.TIMELESS);
		assertEquals("Viewing timeless tasks", com.execute().getFeedback());
		
		com.setDisplayState(DISPLAY_STATE.EXPIRED);
		assertEquals("Viewing expired tasks", com.execute().getFeedback());
		
		//Boundary 
		//To be discussed whether to accept or no
		currentCommand = CommandFactory.createCommand(viewString+" today ");
		assertEquals("Viewing all the tasks for today", currentCommand.execute().getFeedback());
		
		currentCommand = CommandFactory.createCommand(viewString+" today");
		assertEquals("Viewing all the tasks for today", currentCommand.execute().getFeedback());
		
		currentCommand = CommandFactory.createCommand(viewString+" today 123");
		assertEquals("Invalid View Mode", currentCommand.execute().getFeedback());
		
		//Invalid Types
		currentCommand = CommandFactory.createCommand(viewString+" todays");
		assertEquals("Invalid View Mode", currentCommand.execute().getFeedback());
		}
	
}
