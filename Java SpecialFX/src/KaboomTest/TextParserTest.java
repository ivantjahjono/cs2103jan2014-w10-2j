package KaboomTest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Hashtable;

import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.TextParser;

import org.junit.Before;
import org.junit.Test;

public class TextParserTest {
	
	Hashtable<KEYWORD_TYPE, String> keywordTable = new Hashtable<KEYWORD_TYPE, String>();
	
	TextParser textparser;
	
	@Before 
	public void init () {
		textparser = TextParser.getInstance();
	}
	
	
	@Test
	public void testPriorityExtraction () {
		String command = "";
		Hashtable<KEYWORD_TYPE, String> tempHashTable = new Hashtable<KEYWORD_TYPE, String>();
		
		command = "Test string ***";
		assertEquals("Test string ", textparser.extractPriority(command, tempHashTable));
		
		command = "Test string ***   ";
		assertEquals("Test string    ", textparser.extractPriority(command, tempHashTable));
		
		command = "Test string *";
		assertEquals("Test string ", textparser.extractPriority(command, tempHashTable));
		
		command = " **      ";
		assertEquals("       ", textparser.extractPriority(command, tempHashTable));
		
		command = "asdasd**      ";
		assertEquals("asdasd**      ", textparser.extractPriority(command, tempHashTable));
		
		command = "**asdasd      ";
		assertEquals("**asdasd      ", textparser.extractPriority(command, tempHashTable));
		
		command = " *";
		assertEquals(" ", textparser.extractPriority(command, tempHashTable));
	}
	
	@Test
	public void testTimeAndDateExtraction () {
		String command = "";
		String timeKeyword = "at";
		Hashtable<KEYWORD_TYPE, String> tempHashTable = new Hashtable<KEYWORD_TYPE, String>();
		
		// Full working syntax
		command = "at 1700 on 25/12/18";
		assertEquals("", textparser.extractDateAndTime(timeKeyword, command, tempHashTable));
		
		command = "at 600   on 25/12/18";
		assertEquals("", textparser.extractDateAndTime(timeKeyword, command, tempHashTable));
		
		command = "at 1800 on 25/12/18     ";
		assertEquals("", textparser.extractDateAndTime(timeKeyword, command, tempHashTable));
		
		command = "at  160 on 25/12/18";
		assertEquals("", textparser.extractDateAndTime(timeKeyword, command, tempHashTable));
		
		command = "     at 160 on 25/12/18";
		assertEquals("", textparser.extractDateAndTime(timeKeyword, command, tempHashTable));
		
		command = "     at 160       on 25/12/18";
		assertEquals("", textparser.extractDateAndTime(timeKeyword, command, tempHashTable));
	}
	
	@Test
	public void checkTimeAndDateExtractionCondition () {
		String command = "";
		String timeKeyword = "at";

		ArrayList<Integer> matchVector = new ArrayList<Integer>();
		
		// Full working syntax
		command = "at 1700 on 25/12/18";
		assertTrue(textparser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
		
		command = "at 600   on 25/12/18";
		assertTrue(textparser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
		
		command = "at 1800 on 25/12/18     ";
		assertTrue(textparser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
		
		command = "at  160 on 25/12/18";
		assertTrue(textparser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
		
		command = "     at 160 on 25/12/18";
		assertTrue(textparser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
		
		command = "     at 160       on 25/12/18";
		assertTrue(textparser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
	}
	
	@Test
	public void testTimeExtraction () {
		String command = "";
		String keyword = "by";
		Hashtable<KEYWORD_TYPE, String> tempHashTable = new Hashtable<KEYWORD_TYPE, String>();
		
		// Test normal format with spaces
		command = "by 1700";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 600";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 1800 ";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by  160";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 17:00";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 6:00";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 180:0 ";
		assertEquals("by 180:0 ", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 1:6000";
		assertEquals("by 1:6000", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by :160";
		assertEquals("by :160", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by :160pm";
		assertEquals("by :160pm", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 6:00ampm";
		assertEquals("by 6:00ampm", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by    6:00pmpm ";
		assertEquals("by    6:00pmpm ", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by    06:1 ";
		assertEquals("by    06:1 ", textparser.extractTimeOnly(keyword, command, tempHashTable));
	}
	
	public void testDateExtraction () {
		String command = "";
		String keyword = "on";
		Hashtable<KEYWORD_TYPE, String> tempHashTable = new Hashtable<KEYWORD_TYPE, String>();
		
		command = "on 12/06/06";
		assertEquals("", textparser.extractDateOnly(keyword, command, tempHashTable));
		
		command = "on 12.06.06";
		assertEquals("", textparser.extractDateOnly(keyword, command, tempHashTable));
		
		command = "on 120606";
		assertEquals("", textparser.extractDateOnly(keyword, command, tempHashTable));
		
		command = "on  120606 ";
		assertEquals("", textparser.extractDateOnly(keyword, command, tempHashTable));
		
		command = "on  1206065  ";
		assertEquals(command, textparser.extractDateOnly(keyword, command, tempHashTable));
		
		command = "on  120.06.06 ";
		assertEquals(command, textparser.extractDateOnly(keyword, command, tempHashTable));
		
		command = "on  1.06.06 ";
		assertEquals(command, textparser.extractDateOnly(keyword, command, tempHashTable));
		
		command = "on  1.6.06 ";
		assertEquals(command, textparser.extractDateOnly(keyword, command, tempHashTable));
		
		command = "on  1.6.1906 ";
		assertEquals(command, textparser.extractDateOnly(keyword, command, tempHashTable));
	}
	
	@Test
	public void testParser() {
		//standard format
		keywordTable = new Hashtable<KEYWORD_TYPE, String>();
		assertEquals("", textparser.parser("hello world at 0700 on 14/05/12 by 0200 on 14/05/12 ***",keywordTable));
		keywordTable = new Hashtable<KEYWORD_TYPE, String>();
		assertEquals("", textparser.parser("hello world",keywordTable));
		keywordTable = new Hashtable<KEYWORD_TYPE, String>();
		assertEquals("", textparser.parser("hello world at 0700 on 14/05/12",keywordTable));
		keywordTable = new Hashtable<KEYWORD_TYPE, String>();
		assertEquals("", textparser.parser("hello world by 0200 on 14/05/12",keywordTable));
		keywordTable = new Hashtable<KEYWORD_TYPE, String>();
		assertEquals("", textparser.parser("hello world at 0700 on 14/05/12 ***",keywordTable));
		keywordTable = new Hashtable<KEYWORD_TYPE, String>();
		assertEquals("", textparser.parser("hello world by 0200 on 14/05/12 ***",keywordTable));
		keywordTable = new Hashtable<KEYWORD_TYPE, String>();
		assertEquals("", textparser.parser("hello world ***",keywordTable));
		keywordTable = new Hashtable<KEYWORD_TYPE, String>();
		assertEquals("", textparser.parser("hello world by 0200 on 14/05/12 at 0700 on 14/05/12  ***",keywordTable));
	}
	
	@Test
	public void testExtractModifyName() {
		Hashtable<KEYWORD_TYPE, String> keywordTable = new Hashtable<KEYWORD_TYPE, String>();
		assertEquals("hello world ", textparser.extractModifiedTaskName("hello world > some name",keywordTable));
		assertEquals("hello world ", textparser.extractModifiedTaskName("hello world > aabbcc ",keywordTable));
		assertEquals("", textparser.extractModifiedTaskName("> hello world",keywordTable));
		assertEquals("hello world", textparser.extractModifiedTaskName("hello world",keywordTable));
		//error here
		assertEquals("> hello world ", textparser.extractModifiedTaskName("> hello world > asd",keywordTable));
	}
}