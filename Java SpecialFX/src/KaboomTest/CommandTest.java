//@author A0073731J
package KaboomTest;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Hashtable;

import kaboom.logic.command.Command;
import kaboom.logic.command.CommandAdd;
import kaboom.logic.command.CommandClear;
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
import kaboom.storage.TaskManager;

import org.junit.Before;
import org.junit.Test;

public class CommandTest {
	TaskDepository memory = TaskDepository.getInstance();
	DateAndTimeFormat dateAndTimeFormat = DateAndTimeFormat.getInstance();;
	Hashtable<KEYWORD_TYPE, String> infoTable;
	
	@Before
	public void initialise() {
		infoTable = new Hashtable<KEYWORD_TYPE, String>();
	}
	
	//Invalid Command
	@Test
	public void testInvalidCommand() {
		Command com = new Command();
		
		assertEquals("Please enter a valid command. Type <help> for info.",com.execute().getFeedback());
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
		assertEquals("Oops! Please schedule to another time",com.execute().getFeedback());
	}
	
	//CommandDelete
	@Test
	public void testCommandDelete() {
		CommandDelete com = new CommandDelete();
		
		//Execute
		//Delete with no task name and id
		initialise();
		infoTable.put(KEYWORD_TYPE.TASKNAME, "");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Enter a taskname or task id, please ?",com.execute().getFeedback());
		
		//Delete with invalid id
		initialise();
		infoTable.put(KEYWORD_TYPE.TASKID, "-1");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Oops! No such task exist",com.execute().getFeedback());
	}

//	//CommandModify (Unable to test unless memory is initialised);
//	@Test
//	public void testCommandModify() {
//		CommandModify com = new CommandModify();
////		com.setPreModifiedTask(task);
//		//Test when no existing task to modify
////		assertEquals("Fail to cast a spell on <Hello World>", com.execute().getFeedback());
//	}
	
	
	//CommandView
	@Test
	public void testCommandView() {
		CommandView com = new CommandView();
		initialise();
		
		//Test Command feedback
		//No viewType set
		assertEquals("Invalid View Mode. Might want to use <help view>", com.execute().getFeedback());
		
		//Valid ViewTypes
		infoTable.put(KEYWORD_TYPE.VIEWTYPE, "today");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Viewing all the tasks for today", com.execute().getFeedback());
		
		infoTable.put(KEYWORD_TYPE.VIEWTYPE, "timeless");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Viewing timeless tasks", com.execute().getFeedback());
		
		infoTable.put(KEYWORD_TYPE.VIEWTYPE, "expired");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Viewing expired tasks", com.execute().getFeedback());
		
		infoTable.put(KEYWORD_TYPE.VIEWTYPE, "archive");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Viewing completed tasks", com.execute().getFeedback());
		
		infoTable.put(KEYWORD_TYPE.VIEWTYPE, "future");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Viewing upcoming tasks", com.execute().getFeedback());
		
		//Invalid viewTypes
		infoTable.put(KEYWORD_TYPE.VIEWTYPE, "someview");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Invalid View Mode. Might want to use <help view>", com.execute().getFeedback());
		
		infoTable.put(KEYWORD_TYPE.VIEWTYPE, "-1");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Invalid View Mode. Might want to use <help view>", com.execute().getFeedback());
	}
	
	//CommandClear
	@Test
	public void testCommandClear() {
		CommandClear com = new CommandClear();
		
		//Clear without setting clear type
		initialise();
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Enter <clear all> to remove all tasks or <clear present> to remove current view",com.execute().getFeedback());
		
		//Clear with invalid clear type
		initialise();
		infoTable.put(KEYWORD_TYPE.CLEARTYPE, "lala");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Enter <clear all> to remove all tasks or <clear present> to remove current view",com.execute().getFeedback());
		
		//Clear with ALL clear type
		initialise();
		infoTable.put(KEYWORD_TYPE.CLEARTYPE, "all");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("1.. 2.. 3.. Pooof! Your schedule has gone with the wind",com.execute().getFeedback());
		
		//Clear with PRESENT clear type
		initialise();
		infoTable.put(KEYWORD_TYPE.CLEARTYPE, "present");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("1.. 2.. 3.. Pooof! Your present schedule has gone with the wind",com.execute().getFeedback());
		
		//Clear with ARCHIVE clear type
		initialise();
		infoTable.put(KEYWORD_TYPE.CLEARTYPE, "archive");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("3.. 2.. 1.. Pooof! Your archive has gone with the wind",com.execute().getFeedback());
	}
}
