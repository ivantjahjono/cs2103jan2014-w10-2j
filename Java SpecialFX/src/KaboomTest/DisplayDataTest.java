//@author A0099175N

package KaboomTest;

import static org.junit.Assert.*;
import kaboom.shared.DISPLAY_STATE;
import kaboom.shared.Result;
import kaboom.ui.DisplayData;

import org.junit.Before;
import org.junit.Test;

public class DisplayDataTest {

	DisplayData displayData;
	
	@Before
	public void init () {
		displayData = DisplayData.getInstance();
	}
	
	
	@Test
	public void testDisplayState() {
		Result commandResult = new Result();
		
		DISPLAY_STATE currentState = DISPLAY_STATE.ARCHIVE;
		commandResult.setDisplayState(currentState);
		displayData.updateDisplayWithResult(commandResult);
		assertEquals(currentState, displayData.getCurrentDisplayState());
		
		// Test if activate same state will give errors
		currentState = DISPLAY_STATE.ARCHIVE;
		commandResult.setDisplayState(currentState);
		displayData.updateDisplayWithResult(commandResult);
		assertEquals(currentState, displayData.getCurrentDisplayState());
		
		currentState = DISPLAY_STATE.EXPIRED;
		commandResult.setDisplayState(currentState);
		displayData.updateDisplayWithResult(commandResult);
		assertEquals(currentState, displayData.getCurrentDisplayState());
		
		currentState = DISPLAY_STATE.INVALID;
		commandResult.setDisplayState(currentState);
		displayData.updateDisplayWithResult(commandResult);
		assertEquals(DISPLAY_STATE.EXPIRED, displayData.getCurrentDisplayState());
		
		currentState = null;
		commandResult.setDisplayState(currentState);
		displayData.updateDisplayWithResult(commandResult);
		assertEquals(DISPLAY_STATE.EXPIRED, displayData.getCurrentDisplayState());
	}

}
