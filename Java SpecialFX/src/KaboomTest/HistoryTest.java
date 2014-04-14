//@author A0096670W

/**
 * HistoryTest.java:
 * This class tests the storing and retrieving of commands from History class.
 * Since History class is limited to only 10 commands, there are two boundary cases.
 * One is where there are no commands in History and the other when there are 10.
 */

package KaboomTest;

import static org.junit.Assert.*;

import kaboom.logic.command.Command;
import kaboom.logic.command.CommandAdd;
import kaboom.logic.command.CommandClear;
import kaboom.logic.command.CommandView;
import kaboom.storage.History;

import org.junit.Before;
import org.junit.Test;

public class HistoryTest {
	
	History history;
	Command commandAdd;
	Command commandClear;
	Command commandView;
	
	@Before
	public void initialize() {
		history = History.getInstance();
		commandAdd = new CommandAdd();
		commandClear = new CommandClear();
		commandView = new CommandView();
	}
	
	@Test
	public void testGetInstance() {
		assertNotNull(history);
	}
	
	/**
	 * This function tests the pushing of the commands into the stack.
	 * The size of the history after pushing will be compared with 
	 * the expected value.
	 */
	@Test
	public void testAddToRecentCommands() {
		//Boundary testing when there are 0 objects in history
		history.clear();
		assertEquals(0, history.size());
		
		//Valid testing values
		history.addToRecentCommands(commandClear);
		assertEquals(1, history.size());
		history.addToRecentCommands(commandAdd);
		assertEquals(2, history.size());
		history.addToRecentCommands(commandClear);
		assertEquals(3, history.size());
		history.addToRecentCommands(commandAdd);
		assertEquals(4, history.size());
		history.addToRecentCommands(commandClear);
		assertEquals(5, history.size());
		history.addToRecentCommands(commandAdd);
		assertEquals(6, history.size());
		history.addToRecentCommands(commandClear);
		assertEquals(7, history.size());
		history.addToRecentCommands(commandAdd);
		assertEquals(8, history.size());
		history.addToRecentCommands(commandClear);
		assertEquals(9, history.size());
		history.addToRecentCommands(commandAdd);
		assertEquals(10, history.size());
		
		//Boundary case testing where trying to add commands
		//even though there are already 10 in history
		history.addToRecentCommands(commandClear);
		assertEquals(10, history.size());
		history.addToRecentCommands(commandAdd);
		assertEquals(10, history.size());
		
		history.clear();
		assertEquals(0, history.size());
	}

	/**
	 * This function tests the popping of the commands from the stack.
	 * The size of the history after popping will be compared with 
	 * the expected value.
	 */
	@Test
	public void testGetMostRecentCommand() {
		//Boundary testing where trying to pop when there are 0 objects in history
		history.clear();
		assertEquals(0, history.size());
		assertNull(history.getMostRecentCommand());
		
		history.addToRecentCommands(commandAdd);
		history.addToRecentCommands(commandClear);
		assertEquals(history.getMostRecentCommand(), commandClear);
		assertEquals(1, history.size());
		assertEquals(history.getMostRecentCommand(), commandAdd);
		assertEquals(0, history.size());
		
		history.addToRecentCommands(commandAdd);
		history.addToRecentCommands(commandClear);
		history.addToRecentCommands(commandAdd);
		history.addToRecentCommands(commandClear);
		history.addToRecentCommands(commandAdd);
		history.addToRecentCommands(commandClear);
		//Valid testing values
		assertEquals(history.getMostRecentCommand(), commandClear);
		assertEquals(5, history.size());
		history.addToRecentCommands(commandAdd);
		history.addToRecentCommands(commandClear);
		history.addToRecentCommands(commandAdd);
		history.addToRecentCommands(commandClear);
		history.addToRecentCommands(commandClear);  //10th item
		
		//Boundary testing where more than 10 items are added and attempting to pop them
		history.addToRecentCommands(commandAdd);  //11th item
		assertEquals(10, history.size());
		assertEquals(history.getMostRecentCommand(), commandAdd);
		assertEquals(9, history.size());
		assertEquals(history.getMostRecentCommand(), commandClear);
		assertEquals(8, history.size());
		assertEquals(history.getMostRecentCommand(), commandClear);
		assertEquals(7, history.size());
		assertEquals(history.getMostRecentCommand(), commandAdd);
		assertEquals(6, history.size());
	}
}
