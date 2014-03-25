package KaboomTest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Queue;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.TextParser;

import org.junit.Test;

public class TextParserTest {
	
	Hashtable<KEYWORD_TYPE, String> keywordTable = new Hashtable<KEYWORD_TYPE, String>();

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

	
//	@Test
//	public void testPriorityExtraction () {
//		String command = "";
//		Hashtable<KEYWORD_TYPE, String> tempHashTable = new Hashtable<KEYWORD_TYPE, String>();
//		
//		command = "Test string ***";
//		System.out.println(TextParser.extractPriority(command, tempHashTable));
//		
//		command = "Test string ***   ";
//		System.out.println(TextParser.extractPriority(command, tempHashTable));
//		
//		command = "Test string *";
//		System.out.println(TextParser.extractPriority(command, tempHashTable));
//		
//		command = " **      ";
//		System.out.println(TextParser.extractPriority(command, tempHashTable));
//		
//		command = "asdasd**      ";
//		System.out.println(TextParser.extractPriority(command, tempHashTable));
//		
//		command = "**asdasd      ";
//		System.out.println(TextParser.extractPriority(command, tempHashTable));
//		
//		command = " *";
//		System.out.println(TextParser.extractPriority(command, tempHashTable));
//	}
	
	@Test
	public void testTimeAndDateExtraction () {
		String command = "";
		String timeKeyword = "at";
		String dateKeyword = "on";
		Hashtable<KEYWORD_TYPE, String> tempHashTable = new Hashtable<KEYWORD_TYPE, String>();
		
		// Full working syntax
		command = "at 1700 on 25/12/18";
		assertEquals("", TextParser.extractDateAndTime(timeKeyword, command, tempHashTable));
		
		command = "at 600   on 25/12/18";
		assertEquals("", TextParser.extractDateAndTime(timeKeyword, command, tempHashTable));
		
		command = "at 1800 on 25/12/18     ";
		assertEquals("", TextParser.extractDateAndTime(timeKeyword, command, tempHashTable));
		
		command = "at  160 on 25/12/18";
		assertEquals("", TextParser.extractDateAndTime(timeKeyword, command, tempHashTable));
		
		command = "     at 160 on 25/12/18";
		assertEquals("", TextParser.extractDateAndTime(timeKeyword, command, tempHashTable));
		
		command = "     at 160       on 25/12/18";
		assertEquals("", TextParser.extractDateAndTime(timeKeyword, command, tempHashTable));
	}
	
	@Test
	public void checkTimeAndDateExtractionCondition () {
		String command = "";
		String timeKeyword = "at";
		String dateKeyword = "on";
		ArrayList<Integer> matchVector = new ArrayList<Integer>();
		
		// Full working syntax
		command = "at 1700 on 25/12/18";
		assertTrue(TextParser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
		
		command = "at 600   on 25/12/18";
		assertTrue(TextParser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
		
		command = "at 1800 on 25/12/18     ";
		assertTrue(TextParser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
		
		command = "at  160 on 25/12/18";
		assertTrue(TextParser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
		
		command = "     at 160 on 25/12/18";
		assertTrue(TextParser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
		
		command = "     at 160       on 25/12/18";
		assertTrue(TextParser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
	}
	
	@Test
	public void testTimeExtraction () {
		String command = "";
		String keyword = "by";
		Hashtable<KEYWORD_TYPE, String> tempHashTable = new Hashtable<KEYWORD_TYPE, String>();
		
		// Test normal format with spaces
		command = "by 1700";
		assertEquals("", TextParser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 600";
		assertEquals("", TextParser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 1800 ";
		assertEquals("", TextParser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by  160";
		assertEquals("", TextParser.extractTimeOnly(keyword, command, tempHashTable));
		
//		command = "by 17:00";
//		assertEquals("", TextParser.extractTimeOnly(keyword, command, tempHashTable));
//		
//		command = "by 6:00";
//		assertEquals("", TextParser.extractTimeOnly(keyword, command, tempHashTable));
//		
//		command = "by 180:0 ";
//		assertEquals("by 180:0 ", TextParser.extractTimeOnly(keyword, command, tempHashTable));
//		
//		command = "by 1:6000";
//		assertEquals("by 1:6000", TextParser.extractTimeOnly(keyword, command, tempHashTable));
//		
//		command = "by :160";
//		assertEquals("by :160", TextParser.extractTimeOnly(keyword, command, tempHashTable));
//		
//		command = "by :160pm";
//		assertEquals("by :160pm", TextParser.extractTimeOnly(keyword, command, tempHashTable));
//		
//		command = "by 6:00ampm";
//		assertEquals("by 6:00ampm", TextParser.extractTimeOnly(keyword, command, tempHashTable));
//		
//		command = "by    6:00pmpm ";
//		assertEquals("by    6:00pmpm ", TextParser.extractTimeOnly(keyword, command, tempHashTable));
		
//		command = "on 12/06/06";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "on 12.06.06";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "on 120606";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "on  120606 ";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "on  1206065  ";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "on  120.06.06 ";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "on  1.06.06 ";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "on  1.6.06 ";
//		System.out.println(TextParser.extractTime(command, null));
//		
//		command = "on  1.6.1906 ";
//		System.out.println(TextParser.extractTime(command, null));
	}
	
//	@Test
//	public void testParser() {
//		//standard format
//		keywordTable = new Hashtable<KEYWORD_TYPE, String>();
//		assertEquals("hello world", TextParser.parser("hello world at 0700 on 14/05/12 by 0200 on 14/05/12 ***",keywordTable));
//		keywordTable = new Hashtable<KEYWORD_TYPE, String>();
//		assertEquals("hello world", TextParser.parser("hello world",keywordTable));
//		keywordTable = new Hashtable<KEYWORD_TYPE, String>();
//		assertEquals("hello world", TextParser.parser("hello world at 0700 on 14/05/12",keywordTable));
//		keywordTable = new Hashtable<KEYWORD_TYPE, String>();
//		assertEquals("hello world", TextParser.parser("hello world by 0200 on 14/05/12",keywordTable));
//		keywordTable = new Hashtable<KEYWORD_TYPE, String>();
//		assertEquals("hello world", TextParser.parser("hello world at 0700 on 14/05/12 ***",keywordTable));
//		keywordTable = new Hashtable<KEYWORD_TYPE, String>();
//		assertEquals("hello world", TextParser.parser("hello world by 0200 on 14/05/12 ***",keywordTable));
//		keywordTable = new Hashtable<KEYWORD_TYPE, String>();
//		assertEquals("hello world", TextParser.parser("hello world ***",keywordTable));
//		keywordTable = new Hashtable<KEYWORD_TYPE, String>();
//		assertEquals("hello world", TextParser.parser("hello world by 0200 on 14/05/12 at 0700 on 14/05/12  ***",keywordTable));
//	}
	
	
//	@Test
//	public void testExtractModifyName() {
//		Hashtable<KEYWORD_TYPE, String> keywordTable = new Hashtable<KEYWORD_TYPE, String>();
//		assertEquals("hello world ", TextParser.extractModifiedTaskName("hello world > some name",keywordTable));
//		assertEquals("hello world ", TextParser.extractModifiedTaskName("hello world > aabbcc ",keywordTable));
//		assertEquals("", TextParser.extractModifiedTaskName("> hello world",keywordTable));
//		assertEquals("hello world", TextParser.extractModifiedTaskName("hello world",keywordTable));
//		//error here
//		assertEquals("> hello world ", TextParser.extractModifiedTaskName("> hello world > asd",keywordTable));
//	}
}