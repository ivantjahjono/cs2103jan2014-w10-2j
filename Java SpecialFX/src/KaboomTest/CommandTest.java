//@author A0073731J
package KaboomTest;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Hashtable;

import kaboom.logic.command.Command;
import kaboom.logic.command.CommandAdd;
import kaboom.logic.command.CommandDelete;
import kaboom.logic.command.CommandFactory;
import kaboom.logic.command.CommandModify;
import kaboom.logic.command.CommandView;
import kaboom.shared.DISPLAY_STATE;
import kaboom.shared.DateAndTimeFormat;
import kaboom.shared.KEYWORD_TYPE;
import kaboom.shared.TASK_TYPE;
import kaboom.shared.TaskInfo;
import kaboom.storage.Storage;
import kaboom.storage.TaskDepository;
import kaboom.storage.TaskView;

import org.junit.Before;
import org.junit.Test;

public class CommandTest {
	TaskDepository memory;
	DateAndTimeFormat dateAndTimeFormat;
	Hashtable<KEYWORD_TYPE, String> infoTable;
	
	@Before
	public void initialise() {
		memory = TaskDepository.getInstance();
		dateAndTimeFormat = DateAndTimeFormat.getInstance();
		infoTable = new Hashtable<KEYWORD_TYPE, String>();
	}
	
	//Invalid Command
	@Test
	public void testInvalidCommand() {
		Command com = new Command();
		
		assertEquals("Invalid command!",com.execute().getFeedback());
	}
	
	//CommandAdd
	@Test
	public void testCommandAdd() {
		CommandAdd com = new CommandAdd();

		
		
		//Execution
		//Add a task without any task name input
		initialise();
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Oops! Task cannot be entered without a name Y_Y",com.execute().getFeedback());
		
		//Add a task with given task name
		initialise();
		infoTable.put(KEYWORD_TYPE.TASKNAME, "Hello World");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("WOOT! <Hello World> ADDED. MORE STUFF TO DO!",com.execute().getFeedback());
		
		//Add a task with empty task name
		initialise();
		infoTable.put(KEYWORD_TYPE.TASKNAME, "");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Oops! Task cannot be entered without a name Y_Y",com.execute().getFeedback());
		
		//Add a task with valid date and time
		initialise();
		infoTable.put(KEYWORD_TYPE.TASKNAME, "Valid Time");
		infoTable.put(KEYWORD_TYPE.START_TIME, "0000");
		infoTable.put(KEYWORD_TYPE.START_DATE, "010114");
		infoTable.put(KEYWORD_TYPE.END_TIME, "2359");
		infoTable.put(KEYWORD_TYPE.END_DATE, "010114");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("WOOT! <Valid Time> ADDED. MORE STUFF TO DO!",com.execute().getFeedback());
		
		//Add a task with invalid date and time
		initialise();
		infoTable.put(KEYWORD_TYPE.TASKNAME, "Invalid Time");
		infoTable.put(KEYWORD_TYPE.START_TIME, "2359");
		infoTable.put(KEYWORD_TYPE.START_DATE, "010114");
		infoTable.put(KEYWORD_TYPE.END_TIME, "0000");
		infoTable.put(KEYWORD_TYPE.END_DATE, "010114");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Oops! Did you check the calendar? The date you've entered is invalid",com.execute().getFeedback());
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
//		com.setPreModifiedTask(task);
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
