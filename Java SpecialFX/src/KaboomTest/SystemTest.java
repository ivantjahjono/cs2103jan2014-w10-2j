package KaboomTest;

import static org.junit.Assert.*;
import kaboom.logic.TaskMasterKaboom;

import org.junit.Before;
import org.junit.Test;

public class SystemTest {

	TaskMasterKaboom controller;
	
	@Before
	private void init () {
		controller = TaskMasterKaboom.getInstance();
		
	}
	
	@Test
	public void test() {
		String command = "";
		
		// Process empty command
		assertEquals("Invalid command!", controller.processCommand(command));
	}

}
