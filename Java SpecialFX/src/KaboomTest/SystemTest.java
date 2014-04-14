//@author A0099175N
package KaboomTest;

import static org.junit.Assert.*;
import kaboom.logic.TaskMasterKaboom;
import kaboom.storage.History;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SystemTest {

	TaskMasterKaboom controller;
	
	@Before
	public void init () {
		controller = TaskMasterKaboom.getInstance();
		controller.setFilename("TESTFILE.dat");
		controller.initialiseKaboom();
	}
	
	@Test
	public void testAddAndDeleteCommands() {
		controller.processCommand("clear all");
		String command = "";
		
		// Process empty command
		assertEquals("Please enter a valid command. Type <help> for info.", controller.processCommand(command));
		
		// Process only whitespaces command
		command = "       ";
		assertEquals("Please enter a valid command. Type <help> for info.", controller.processCommand(command));
		
		// Process only whitespaces command
		command = "      add ";
		assertEquals("Oops! Task cannot be entered without a name Y_Y", controller.processCommand(command));
		
		// Add only command
		command = "add";
		assertEquals("Oops! Task cannot be entered without a name Y_Y", controller.processCommand(command));
		
		// Add whitespaces command
		command = "add     ";
		assertEquals("Oops! Task cannot be entered without a name Y_Y", controller.processCommand(command));
		
		command = "add hello";
		assertEquals("WOOT! <hello> ADDED. MORE STUFF TO DO!", controller.processCommand(command));
		
		// Delete only command
		command = "delete";
		assertEquals("Enter a taskname or task id, please ?", controller.processCommand(command));
		
		// Delete whitespaces command
		command = "delete ";
		assertEquals("Enter a taskname or task id, please ?", controller.processCommand(command));
		
		// Test delete by id but over limit
		command = "delete 3";
		assertEquals("Oops! Invalid ID??", controller.processCommand(command));
		
		// Test delete by id but over limit
		command = "delete -10";
		assertEquals("Oops! No such task exist", controller.processCommand(command));
		
		// Delete whitespaces command
		command = "delete hello";
		assertEquals("<hello> deleted. 1 less work to do :D", controller.processCommand(command));
		
		// Add whitespaces command
		command = "add        hello";
		assertEquals("WOOT! <hello> ADDED. MORE STUFF TO DO!", controller.processCommand(command));
		
		// Delete whitespaces command
		command = "delete     hello";
		assertEquals("<hello> deleted. 1 less work to do :D", controller.processCommand(command));	
	}
	
	@Test
	public void testViewCommands() {
		String command = "";
	
		command = "view";
		assertEquals("Invalid View Mode. Might want to use <help view>", controller.processCommand(command));
		
		command = "view     ";
		assertEquals("Invalid View Mode. Might want to use <help view>", controller.processCommand(command));
		
		command = "view today";
		assertEquals("Viewing all the tasks for today", controller.processCommand(command));
		
		command = "view today";
		assertEquals("Viewing all the tasks for today", controller.processCommand(command));
		
		command = "view timeless";
		assertEquals("Viewing timeless tasks", controller.processCommand(command));
		
		command = "view expired";
		assertEquals("Viewing expired tasks", controller.processCommand(command));
		
		command = "view future";
		assertEquals("Viewing upcoming tasks", controller.processCommand(command));
		
		command = "view archive";
		assertEquals("Viewing completed tasks", controller.processCommand(command));
	}
	
	@Test
	public void testModify() {
		String command = "";
		
		command = "add hello";
		assertEquals("WOOT! <hello> ADDED. MORE STUFF TO DO!", controller.processCommand(command));
		
		command = "modify";
		assertEquals("Enter a taskname or task id, please ?", controller.processCommand(command));
		
		command = "modify     ";
		assertEquals("Enter a taskname or task id, please ?", controller.processCommand(command));
		
		command = "modify 10";
		assertEquals("Oops! Invalid ID??", controller.processCommand(command));
		
		command = "modify -5";
		assertEquals("Oops! No such task exist", controller.processCommand(command));
		
		command = "modify 1";
		assertEquals("Nothing happened...", controller.processCommand(command));
		
		testModifyName();
		testModifyDate();
	}
	
	public void testModifyName () {
		String command = "";
		
		command = "modify 1 > world";
		assertEquals("<hello> has evolved into <world>", controller.processCommand(command));
		
		command = "modify 25 > world";
		assertEquals("Oops! Invalid ID??", controller.processCommand(command));
		
		command = "modify world > hello";
		assertEquals("<world> has evolved into <hello>", controller.processCommand(command));
	}
	
	public void testModifyDate () {
		String command = "";
		
		command = "modify 1 by today";
		assertEquals("<hello> has manipulated time", controller.processCommand(command));
		
		command = "modify 1 by 1";
		assertEquals("<hello> has manipulated time", controller.processCommand(command));
		
		command = "modify 1 BY 1";
		assertEquals("Oops! Invalid taskname??", controller.processCommand(command));
		
		command = "modify hello by 2pm";
		assertEquals("<hello> has manipulated time", controller.processCommand(command));
		
		command = "modify hello at 2pm";
		assertEquals("<hello> has manipulated time", controller.processCommand(command));
		
		command = "modify hello at 2   pm";
		assertEquals("Oops! No such task exist", controller.processCommand(command));
		
		command = "modify 1 at 2pm";
		assertEquals("<hello> has manipulated time", controller.processCommand(command));
	}
	
	@Test
	public void testSearchByName () {
		controller.processCommand("clear all");
		String command = "";
		
		command = "add hello";
		assertEquals("WOOT! <hello> ADDED. MORE STUFF TO DO!", controller.processCommand(command));
		
		command = "search";
		assertEquals("Please enter something to search", controller.processCommand(command));
		
		command = "        search";
		assertEquals("Please enter something to search", controller.processCommand(command));
		
		command = "search h";
		assertEquals("Search done. 1 item(s) found.", controller.processCommand(command));
		
		command = "search helloo";
		assertEquals("Search done. 0 item(s) found.", controller.processCommand(command));
		
		command = "search h ello";
		assertEquals("Search done. 0 item(s) found.", controller.processCommand(command));
		
		command = "search            * hello";
		assertEquals("Search done. 0 item(s) found.", controller.processCommand(command));
		
		command = "search llo";
		assertEquals("Search done. 1 item(s) found.", controller.processCommand(command));
		
		command = "search        ";
		assertEquals("Please enter something to search", controller.processCommand(command));
		
		command = "search **";
		assertEquals("Search done. 0 item(s) found.", controller.processCommand(command));
	}
	
	@Test
	public void testSearchByDate () {
		controller.processCommand("clear all");
		String command = "";
		
		command = "add hello by tmr";
		assertEquals("WOOT! <hello> ADDED. MORE STUFF TO DO!", controller.processCommand(command));

		command = "search by tmr";
		assertEquals("Search done. 1 item(s) found.", controller.processCommand(command));
		
		command = "search by today";
		assertEquals("Search done. 0 item(s) found.", controller.processCommand(command));
		
		command = "search by today by today";
		assertEquals("Search done. 0 item(s) found.", controller.processCommand(command));
		
		command = "search by tomorrow";
		assertEquals("Search done. 1 item(s) found.", controller.processCommand(command));
		
		command = "search to tomorrow";
		assertEquals("Search done. 1 item(s) found.", controller.processCommand(command));
		
		command = "search to tomorrow ";
		assertEquals("Search done. 1 item(s) found.", controller.processCommand(command));
		
		// The search should not accept something as time
		command = "search by something";
		assertEquals("Search done. 0 item(s) found.", controller.processCommand(command));
	}
	
	@Test
	public void testClear () {
		controller.processCommand("clear all");
		String command = "";
		
		command = "clear";
		assertEquals("Enter <clear all> to remove all tasks or <clear present> to remove current view", controller.processCommand(command));
		
		command = "Clear";
		assertEquals("Enter <clear all> to remove all tasks or <clear present> to remove current view", controller.processCommand(command));
		
		command = "clear    ";
		assertEquals("Enter <clear all> to remove all tasks or <clear present> to remove current view", controller.processCommand(command));
		
		command = "clear all";
		assertEquals("1.. 2.. 3.. Pooof! Your schedule has gone with the wind", controller.processCommand(command));
		
		command = "clear All";
		assertEquals("Enter <clear all> to remove all tasks or <clear present> to remove current view", controller.processCommand(command));
		
		command = "clear all     ";
		assertEquals("1.. 2.. 3.. Pooof! Your schedule has gone with the wind", controller.processCommand(command));
		
		command = "clear present";
		assertEquals("1.. 2.. 3.. Pooof! Your present schedule has gone with the wind", controller.processCommand(command));
		
		command = "clear archive";
		assertEquals("3.. 2.. 1.. Pooof! Your archive has gone with the wind", controller.processCommand(command));
	}
	
	@Test
	public void testCompleteIncomplete () {
		controller.processCommand("clear all");
		String command = "";
		
		command = "add hello";
		assertEquals("WOOT! <hello> ADDED. MORE STUFF TO DO!", controller.processCommand(command));
		
		command = "boom hello";
		assertEquals("Set hello to complete", controller.processCommand(command));
		
		command = "boom hello";
		assertEquals("Oops! No such task exist", controller.processCommand(command));
		
		command = "unboom hello";
		assertEquals("Oops! No such task exist", controller.processCommand(command));
		
		command = "view archive";
		controller.processCommand(command);
		
		command = "unboom hello";
		assertEquals("Set hello to incomplete", controller.processCommand(command));
		
		command = "unboom hello";
		assertEquals("Oops! No such task exist", controller.processCommand(command));
		
		command = "add hello";
		assertEquals("WOOT! <hello> ADDED. MORE STUFF TO DO!", controller.processCommand(command));
		
		command = "boom hello";
		assertEquals("Search done. 2 item(s) found.", controller.processCommand(command));
		
		command = "unboom hello";
		assertEquals("Search done. 2 item(s) found.", controller.processCommand(command));
		
		command = "delete 1";
		controller.processCommand(command);
		
		command = "unboom hello";
		assertEquals("hello was incomplete", controller.processCommand(command));
	}
	
	@Test
	public void testUndo () {
		controller.processCommand("clear all");
		History history = History.getInstance();
		history.clear();
		
		String command = "";

		command = "undo";
		assertEquals("No more action to undo", controller.processCommand(command));
		
		command = "UNDO";
		assertEquals("No more action to undo", controller.processCommand(command));
		
		command = "undo 1";
		assertEquals("Sorry. Not valid undo command. Type <help undo> for help.", controller.processCommand(command));
		
		command = "undo undo";
		assertEquals("No more action to undo", controller.processCommand(command));
		
		command = "add undo";
		controller.processCommand(command);

		command = "undo";
		assertEquals("Command undone!", controller.processCommand(command));
		
		command = "     Undo";
		assertEquals("No more action to undo", controller.processCommand(command));
		
		command = "view timeless";
		controller.processCommand(command);

		command = "undo";
		assertEquals("No more action to undo", controller.processCommand(command));
	}

	@After
	public void deinit () {
		controller.processCommand("clear");
	}
}
