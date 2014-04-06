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
	
	@Test
	public void testAddToRecentCommands() {
		history.clear();
		history.addToRecentCommands(commandClear);
		assertEquals(1, history.size());
		history.addToRecentCommands(commandAdd);
		assertEquals(2, history.size());
	}

	@Test
	public void testGetMostRecentCommand() {
		history.clear();
		history.addToRecentCommands(commandAdd);
		history.addToRecentCommands(commandClear);
		assertEquals(history.getMostRecentCommand(), commandClear);
		assertEquals(1, history.size());
		assertEquals(history.getMostRecentCommand(), commandAdd);
		assertEquals(0, history.size());
	}

	@Test
	public void testGetMostRecentCommandView() {
		history.addToRecentCommands(commandAdd);
		assertNotEquals(history.getMostRecentCommandView(), commandAdd);
		history.addToRecentCommands(commandView);
		assertEquals(history.getMostRecentCommand(), commandView);
	}
}
