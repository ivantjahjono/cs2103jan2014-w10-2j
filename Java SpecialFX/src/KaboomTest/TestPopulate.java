//@author A0099175N
package KaboomTest;

import static org.junit.Assert.*;
import kaboom.logic.TaskMasterKaboom;
import org.junit.Before;

public class TestPopulate {

	TaskMasterKaboom controller;
	
	String []commandArrayList = {
								 "add gaming", 
								 "add sleep by 11pm", 
								 "add meeting from 1200 to 1pm", 
								 "add lunch from 11am to 1200",
								 "add meeting from 11am to 1200",
								 "add do assignment from 8pm to 10pm",
								 "add watch Adventure Time episode 12",
								 "add finish CS9999 by tmr",
								 "add filming video by saturday",
								 "add end the world by sunday",
								 "add fire up photon cannon from 7pm to 9pm friday",
								 "add split proton by 10/01/14",
								 "boom split proton"}; 
	
	@Before
	public void populate () {
		controller = TaskMasterKaboom.getInstance();
		controller.initialiseKaboom();
		
		String command = "";
		
		// Process empty command
		assertEquals("Please enter a valid command. Type <help> for info.", controller.processCommand(command));
		
		for (int i = 0; i < commandArrayList.length; i++) {
			controller.processCommand(commandArrayList[i]);
		}
	}
}
