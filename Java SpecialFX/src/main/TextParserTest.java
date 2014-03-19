package main;

import static org.junit.Assert.*;

import java.util.Hashtable;
import java.util.Queue;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class TextParserTest {

//	@Test
//	// Test cutting of words based on keywords
//	public void testLevel1_KeywordsCheck() {
//		
//		assertEquals(-1, TextParser.getNextKeywordIndex("hello world"));
//		assertEquals(-1, TextParser.getNextKeywordIndex("hello worldon"));
//		assertEquals(-1, TextParser.getNextKeywordIndex("hello byworld"));
//		
//		assertEquals(-1, TextParser.getNextKeywordIndex("hello world by"));
//		assertEquals("hello world".length(), TextParser.getNextKeywordIndex("hello world by 0700"));
//		
//		assertEquals(-1, TextParser.getNextKeywordIndex("by 0700"));
//		
//		Vector<Integer> listOfkeywordPosition = TextParser.getListOfKeywordPositions("hello world at 0700 on 14/05/12 by 0200 on 14/05/12");
//		System.out.println(listOfkeywordPosition);
//		
//		listOfkeywordPosition = TextParser.getListOfKeywordPositions("hello world at 0700 on 14/05/12 by 0200 on 14/05/12 ***");
//		System.out.println(listOfkeywordPosition);
//	}
//	
//	@Test
//	public void testParsing () {
//		// Create hashtable for result
//		Hashtable<KEYWORD_TYPE, String> keywordTable = new Hashtable<KEYWORD_TYPE, String>();
//		Queue<KeytypeIndexPair> queue;
//		String command = "";	
//		
//		TextParser.possibleParser(command, keywordTable);
//		System.out.println(keywordTable);
//		keywordTable.clear();
//		
//		command = "live long and prosper";
////		queue = TextParser.getKeywordsInAscendingOrder(command);
////		System.out.println(queue);
//		
//		TextParser.possibleParser(command, keywordTable);
//		assertEquals(command, keywordTable.get(KEYWORD_TYPE.TASKNAME));
//		System.out.println(keywordTable);
//		keywordTable.clear();
//		
//		command = "live long and prosper by 1800";
////		queue = TextParser.getKeywordsInAscendingOrder(command);
////		System.out.println(queue);
//		
//		TextParser.possibleParser(command, keywordTable);
//		System.out.println(keywordTable);
//		keywordTable.clear();
//		
//		command = "by 1800";
////		queue = TextParser.getKeywordsInAscendingOrder(command);
////		System.out.println(queue);
//		
//		TextParser.possibleParser(command, keywordTable);
//		System.out.println(keywordTable);
//		keywordTable.clear();
//		
//		command = "live long and prosper at 1800";
//		TextParser.possibleParser(command, keywordTable);
//		System.out.println(keywordTable);
//		keywordTable.clear();
//		
//		command = "live long and prosper at 1800 by 2000";
//		TextParser.possibleParser(command, keywordTable);
//		System.out.println(keywordTable);
//		keywordTable.clear();
//		
//		command = "live long and    prosper by 2000 at 1800";
//		TextParser.possibleParser(command, keywordTable);
//		System.out.println(keywordTable);
//		keywordTable.clear();
//		
//		command = "live long and prosper at marina by the bay by 2000 at 1800";
//		TextParser.possibleParser(command, keywordTable);
//		System.out.println(keywordTable);
//		keywordTable.clear();
//		
//		command = "live long and prosper *";
//		TextParser.possibleParser(command, keywordTable);
//		System.out.println(keywordTable);
//		keywordTable.clear();
//		
//		command = "live long and prosper  * ***";
//		TextParser.possibleParser(command, keywordTable);
//		System.out.println(keywordTable);
//		keywordTable.clear();
//	}
	
	
//	@Test
//	public void testPriorityExtraction () {
//		String command = "";
//		
//		command = "Test string ***";
//		System.out.println(TextParser.extractPriority2(command));
//		
//		command = "Test string ***   ";
//		System.out.println(TextParser.extractPriority2(command));
//		
//		command = "Test string *";
//		System.out.println(TextParser.extractPriority2(command));
//		
//		command = " **      ";
//		System.out.println(TextParser.extractPriority2(command));
//		
//		command = "asdasd**      ";
//		System.out.println(TextParser.extractPriority2(command));
//		
//		command = "**asdasd      ";
//		System.out.println(TextParser.extractPriority2(command));
//	}
	
	@Test
	public void testTimeExtraction () {
		String command = "";
		
//		command = "by 1700";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "by 600";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "by 1800 ";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "by  160";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "by 17:00";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "by 6:00";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "by 180:0 ";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "by 1:6000";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "by :160";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "by :160pm";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "by 6:00ampm";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "by    6:00pmpm ";
//		System.out.println(TextParser.extractTime(command, null));
		
		command = "on 12/06/06";
		System.out.println(TextParser.extractTime(command, null));
		
		command = "on 12.06.06";
		System.out.println(TextParser.extractTime(command, null));
		
		command = "on 120606";
		System.out.println(TextParser.extractTime(command, null));
		
		command = "on  120606 ";
		System.out.println(TextParser.extractTime(command, null));
		
		command = "on  1206065  ";
		System.out.println(TextParser.extractTime(command, null));
		
		command = "on  120.06.06 ";
		System.out.println(TextParser.extractTime(command, null));
		
		command = "on  1.06.06 ";
		System.out.println(TextParser.extractTime(command, null));
		
		command = "on  1.6.06 ";
		System.out.println(TextParser.extractTime(command, null));
		
		command = "on  1.6.1906 ";
		System.out.println(TextParser.extractTime(command, null));
	}
}
