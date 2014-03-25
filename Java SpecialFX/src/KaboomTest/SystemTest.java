package KaboomTest;

import static org.junit.Assert.*;
import kaboom.logic.TaskMasterKaboom;

import org.junit.Before;
import org.junit.Test;

public class SystemTest {

	TaskMasterKaboom controller;
	
	@Before
	public void init () {
		controller = TaskMasterKaboom.getInstance();
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
		assertEquals("Added a file with no Task Name", controller.processCommand(command));
		
		// Add only command
		command = "add";
		assertEquals("Added a file with no Task Name", controller.processCommand(command));
		
		// Add whitespaces command
		command = "add     ";
		assertEquals("Added a file with no Task Name", controller.processCommand(command));
		
		// Add whitespaces command
		command = "add hello";
		assertEquals("Successfully added hello", controller.processCommand(command));
		
		// Delete only command
		command = "delete";
		assertEquals(" fail to delete.", controller.processCommand(command));
		
		// Delete whitespaces command
		command = "delete ";
		assertEquals(" fail to delete.", controller.processCommand(command));
		
		// Delete whitespaces command
		command = "delete hello";
		assertEquals("hello deleted.", controller.processCommand(command));
		
		// Add whitespaces command
		command = "add        hello";
		assertEquals("Successfully added hello", controller.processCommand(command));
		
		// Delete whitespaces command
		command = "delete     hello";
		assertEquals("hello deleted.", controller.processCommand(command));	
	}
	
	@Test
	public void testViewCommands() {
		String command = "";
	
		command = "view";
		assertEquals("Invalid View Mode", controller.processCommand(command));
		
		command = "view     ";
		assertEquals("Invalid View Mode", controller.processCommand(command));
		
		command = "view floating";
		assertEquals("Floating Task Mode", controller.processCommand(command));
		
		command = "view running";
		assertEquals("Running Task Mode", controller.processCommand(command));
		
		command = "view deadline";
		assertEquals("Deadline Task Mode", controller.processCommand(command));
		
		command = "view timed";
		assertEquals("Timed Task Mode", controller.processCommand(command));
		
		command = "view search";
		assertEquals("", controller.processCommand(command));
	}

}
