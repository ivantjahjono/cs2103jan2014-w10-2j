//@author A0073731J
package KaboomTest;

import static org.junit.Assert.*;
import kaboom.logic.TaskMasterKaboom;

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
		String command = "";
		
		// Process empty command
		assertEquals("Invalid command!", controller.processCommand(command));
		
		// Process only whitespaces command
		command = "       ";
		assertEquals("Invalid command!", controller.processCommand(command));
		
		// Process only whitespaces command
		command = "      add ";
		assertEquals("Error! Task cannot be entered without a name Y_Y", controller.processCommand(command));
		
		// Add only command
		command = "add";
		assertEquals("Error! Task cannot be entered without a name Y_Y", controller.processCommand(command));
		
		// Add whitespaces command
		command = "add     ";
		assertEquals("Error! Task cannot be entered without a name Y_Y", controller.processCommand(command));
		
		// Add whitespaces command
		command = "add hello";
		assertEquals("WOOT! <hello> ADDED. MORE STUFF TO DO!", controller.processCommand(command));
		
		// Delete only command
		command = "delete";
		assertEquals("Enter a taskname or task id, please ?", controller.processCommand(command));
		
		// Delete whitespaces command
		command = "delete ";
		assertEquals("Enter a taskname or task id, please ?", controller.processCommand(command));
		
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
		assertEquals("Invalid View Mode", controller.processCommand(command));
		
		command = "view     ";
		assertEquals("Invalid View Mode", controller.processCommand(command));
		
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

	@After
	public void deinit () {
		controller.processCommand("clear");
	}
}
