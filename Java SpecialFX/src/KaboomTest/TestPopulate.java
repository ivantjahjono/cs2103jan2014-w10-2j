//@author A0099175N
package KaboomTest;

import static org.junit.Assert.*;
import kaboom.logic.TaskMasterKaboom;
import org.junit.Before;
import org.junit.Test;

public class TestPopulate {

	TaskMasterKaboom controller;
	
	String []commandArrayList = {
								 "add gaming", 
								 "add sleep by 11pm", 
								 "add meeting from 1200 to 1pm", 
								 "add dinner from 11am to 1200",
								 "add lunch from 11am to 1200",
								 "add meeting from 11am to 1200",
								 }; 
	
	@Before
	public void populate () {
		controller = TaskMasterKaboom.getInstance();
		controller.initialiseKaboom();
		controller.processCommand("clear all");
		String command = "";
		
		// Process empty command
		assertEquals("Please enter a valid command. Type <help> for info.", controller.processCommand(command));
		
		for (int i = 0; i < commandArrayList.length; i++) {
			controller.processCommand(commandArrayList[i]);
		}
	}
	
	@Test
	public void main() {
	}
}
