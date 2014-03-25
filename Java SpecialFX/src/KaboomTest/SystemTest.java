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
	public void test() {
		String command = "";
		
		// Process empty command
		assertEquals("Invalid command!", controller.processCommand(command));
		
		// Process only whitespaces command
		command = "       ";
		assertEquals("Invalid command!", controller.processCommand(command));
		
		// Process only whitespaces command
		command = "add";
		assertEquals("Added a file with no Task Name", controller.processCommand(command));
		
		// Process only whitespaces command
		command = "add     ";
		assertEquals("Added a file with no Task Name", controller.processCommand(command));
	}

}
