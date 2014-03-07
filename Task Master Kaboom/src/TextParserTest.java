import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;



public class TextParserTest {

	@Test
	// Test cutting of words based on keywords
	public void testLevel1_KeywordsCheck() {
		
		assertEquals(-1, TaskMasterKaboom.getNextKeywordIndex("hello world"));
		assertEquals(-1, TaskMasterKaboom.getNextKeywordIndex("hello worldon"));
		assertEquals(-1, TaskMasterKaboom.getNextKeywordIndex("hello byworld"));
		
		assertEquals(-1, TaskMasterKaboom.getNextKeywordIndex("hello world by"));
		assertEquals("hello world".length(), TaskMasterKaboom.getNextKeywordIndex("hello world by 0700"));
		
		assertEquals(-1, TaskMasterKaboom.getNextKeywordIndex("by 0700"));
		
		Vector<Integer> listOfkeywordPosition = TaskMasterKaboom.getListOfKeywordPositions("hello world at 0700 on 14/05/12 by 0200 on 14/05/12");
		System.out.println(listOfkeywordPosition);
	}

}
