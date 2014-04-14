//@author A0073731J
package KaboomTest;

import static org.junit.Assert.*;

import java.util.Hashtable;

import kaboom.logic.command.Command;
import kaboom.logic.command.CommandAdd;
import kaboom.logic.command.CommandClear;
import kaboom.logic.command.CommandDelete;
import kaboom.logic.command.CommandDone;
import kaboom.logic.command.CommandSearch;
import kaboom.logic.command.CommandUndone;
import kaboom.logic.command.CommandModify;
import kaboom.logic.command.CommandView;
import kaboom.shared.KEYWORD_TYPE;


import org.junit.Before;
import org.junit.Test;

public class CommandTest {
	Hashtable<KEYWORD_TYPE, String> infoTable;
	TestPopulate tp = new TestPopulate();;
	
	@Before
	public void initAndPopulate() {
		infoTable = new Hashtable<KEYWORD_TYPE, String>();
		tp.populate();
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
		initAndPopulate();
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Oops! Task cannot be entered without a name Y_Y",com.execute().getFeedback());
		
		//Add a task with given task name
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKNAME, "Hello World");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("WOOT! <Hello World> ADDED. MORE STUFF TO DO!",com.execute().getFeedback());
		
		//Add a task with empty task name
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKNAME, "");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Oops! Task cannot be entered without a name Y_Y",com.execute().getFeedback());
		
		//Add a task with valid date and time
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKNAME, "Valid Time");
		infoTable.put(KEYWORD_TYPE.START_TIME, "0000");
		infoTable.put(KEYWORD_TYPE.START_DATE, "010114");
		infoTable.put(KEYWORD_TYPE.END_TIME, "2359");
		infoTable.put(KEYWORD_TYPE.END_DATE, "010114");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("WOOT! <Valid Time> ADDED. MORE STUFF TO DO!",com.execute().getFeedback());
		
		//Add a task with invalid date and time
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKNAME, "Invalid Time");
		infoTable.put(KEYWORD_TYPE.START_TIME, "2359");
		infoTable.put(KEYWORD_TYPE.START_DATE, "010114");
		infoTable.put(KEYWORD_TYPE.END_TIME, "0000");
		infoTable.put(KEYWORD_TYPE.END_DATE, "010114");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Oops! Please schedule to another time",com.execute().getFeedback());
	}
	
	//CommandView
	@Test
	public void testCommandView() {
		CommandView com = new CommandView();
		initAndPopulate();
		
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
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Enter <clear all> to remove all tasks or <clear present> to remove current view",com.execute().getFeedback());
		
		//Clear with invalid clear type
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.CLEARTYPE, "lala");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Enter <clear all> to remove all tasks or <clear present> to remove current view",com.execute().getFeedback());
		
		//Clear with ALL clear type
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.CLEARTYPE, "all");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("1.. 2.. 3.. Pooof! Your schedule has gone with the wind",com.execute().getFeedback());
		
		//Clear with PRESENT clear type
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.CLEARTYPE, "present");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("1.. 2.. 3.. Pooof! Your present schedule has gone with the wind",com.execute().getFeedback());
		
		//Clear with ARCHIVE clear type
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.CLEARTYPE, "archive");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("3.. 2.. 1.. Pooof! Your archive has gone with the wind",com.execute().getFeedback());
	}
	
	//CommandDelete
	@Test
	public void testCommandDelete() {		
		CommandDelete com = new CommandDelete();

		//Execute
		//Delete with no task name and id
		infoTable.put(KEYWORD_TYPE.TASKNAME, "");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Enter a taskname or task id, please ?",com.execute().getFeedback());
		
		//Delete with invalid id
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKID, "100");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Oops! Invalid ID??",com.execute().getFeedback());
		
		//Delete with invalid id and name
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKID, "1");
		infoTable.put(KEYWORD_TYPE.TASKNAME, "LLALALLA");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Oops! Invalid taskname??",com.execute().getFeedback());
		
		//Delete with valid id
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKID, "1");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("<dinner> deleted. 1 less work to do :D",com.execute().getFeedback());
		
		//Delete with clash in name
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKNAME, "meeting");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Search done. 2 item(s) found.",com.execute().getFeedback());

		//Delete with valid name
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKNAME, "lunch");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("<lunch> deleted. 1 less work to do :D",com.execute().getFeedback());
	}

	//CommandModify
	@Test
	public void testCommandModify() {
		CommandModify com = new CommandModify();
		
		//Execution
		//Modify a task without any task name input
		initAndPopulate();
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Enter a taskname or task id, please ?",com.execute().getFeedback());
		
		//Modify a task without modifications
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKID, "1");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Nothing happened...",com.execute().getFeedback());
		
		//Modify a task without modifications
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKNAME, "dinner");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Nothing happened...",com.execute().getFeedback());
		
		//Modify a task to a new task name
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKID, "1");
		infoTable.put(KEYWORD_TYPE.MODIFIED_TASKNAME, "somename");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("<dinner> has evolved into <somename>",com.execute().getFeedback());
		
		//Modify a task priority
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKID, "1");
		infoTable.put(KEYWORD_TYPE.PRIORITY, "3");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("<dinner> has consulted the stars",com.execute().getFeedback());
		
		//Modify a task date
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKID, "1");
		infoTable.put(KEYWORD_TYPE.START_DATE, "13/03/14");
		infoTable.put(KEYWORD_TYPE.END_DATE, "15/03/14");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("<dinner> has manipulated time, consulted the stars",com.execute().getFeedback());
		
		//Modify a task time
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKID, "1");
		infoTable.put(KEYWORD_TYPE.START_TIME, "1200");
		infoTable.put(KEYWORD_TYPE.END_TIME, "1300");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("<dinner> has manipulated time, consulted the stars",com.execute().getFeedback());
		
		//Modify a task with invalid time
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKID, "1");
		infoTable.put(KEYWORD_TYPE.START_TIME, "1500");
		infoTable.put(KEYWORD_TYPE.END_TIME, "1200");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Oops! Please schedule to another time",com.execute().getFeedback());
		
		//Modify a task with invalid date
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKID, "1");
		infoTable.put(KEYWORD_TYPE.START_DATE, "02/02/14");
		infoTable.put(KEYWORD_TYPE.END_DATE, "01/02/14");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Oops! Please schedule to another time",com.execute().getFeedback());
	}
	
	
	//CommandDone
	@Test
	public void testCommandDone() {
		CommandDone com = new CommandDone();
		
		//Execution
		//Boom a task without any task name input
		initAndPopulate();
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Enter a taskname or task id, please ?",com.execute().getFeedback());
		
		//Boom a task with invalid id
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKID, "10");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Oops! Invalid ID??",com.execute().getFeedback());
		
		//Boom a task with valid id
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKID, "1");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Set dinner to complete",com.execute().getFeedback());
		
		//Boom a task with valid name
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKNAME, "dinner");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Set dinner to complete",com.execute().getFeedback());
	}
	
	//CommandUndone
	@Test
	public void testCommandUndone() {
		CommandUndone com = new CommandUndone();
		
		//Execution
		//Unboom a task without any task name input
		initAndPopulate();
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Enter a taskname or task id, please ?",com.execute().getFeedback());
		
		//Unboom a task with invalid id
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKID, "10");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Oops! Invalid ID??",com.execute().getFeedback());
		
		//Unboom a task with valid id but completed
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKID, "1");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("dinner was incomplete",com.execute().getFeedback());
		
		//Unboom a task with valid id but completed
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKNAME, "dinner");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("dinner was incomplete",com.execute().getFeedback());
	}
	
	//CommandSearch
	@Test
	public void testCommandSearch() {
		CommandSearch com = new CommandSearch();
		
		//Execution
		//Search a task without any task name input
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKNAME, "");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Please enter something to search",com.execute().getFeedback());
		
		//Search a task by name
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKNAME, "dinner");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Search done. 1 item(s) found.",com.execute().getFeedback());
		
		//Search a task by date
		initAndPopulate();
		infoTable.put(KEYWORD_TYPE.TASKNAME, "");
		infoTable.put(KEYWORD_TYPE.DATE, "01/01/00");
		com.initialiseCommandInfoTable(infoTable);
		assertEquals("Search done. 6 item(s) found.",com.execute().getFeedback());
	}
	
	

}
