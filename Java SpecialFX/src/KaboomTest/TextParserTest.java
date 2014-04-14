//@author A0099863H
package KaboomTest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Hashtable;

import kaboom.logic.TextParser;
import kaboom.shared.KEYWORD_TYPE;

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
		assertEquals("***", textparser.extractPriority(command, tempHashTable));
		
		command = "Test string ***   ";
		assertEquals("***", textparser.extractPriority(command, tempHashTable));
		
		command = "Test string *";
		assertEquals("*", textparser.extractPriority(command, tempHashTable));
		
		command = " **      ";
		assertEquals("**", textparser.extractPriority(command, tempHashTable));
		
		command = "asdasd**      ";
		assertEquals("", textparser.extractPriority(command, tempHashTable));
		
		command = "**asdasd      ";
		assertEquals("", textparser.extractPriority(command, tempHashTable));
		
		command = " *";
		assertEquals("*", textparser.extractPriority(command, tempHashTable));
		
		command = " *****";
		assertEquals("*****", textparser.extractPriority(command, tempHashTable));
		
		command = "*****";
		assertEquals("", textparser.extractPriority(command, tempHashTable));
		
		command = " ********";
		assertEquals("", textparser.extractPriority(command, tempHashTable));
	}
	
	@Test
	public void testTimeAndDateExtraction () {
		String command = "";
		String timeKeyword = "at";
		Hashtable<KEYWORD_TYPE, String> tempHashTable = new Hashtable<KEYWORD_TYPE, String>();
		
		// Full working syntax
		command = "at 1700 on 25/12/18";
		assertEquals("at 1700 on 25/12/18", textparser.extractDateAndTime(timeKeyword, command, tempHashTable));
		
		command = "at 600   on 25/12/18";
		assertEquals(command, textparser.extractDateAndTime(timeKeyword, command, tempHashTable));
		
		command = "at 1800 on 25/12/18     ";
		assertEquals("at 1800 on 25/12/18 ", textparser.extractDateAndTime(timeKeyword, command, tempHashTable));
		
		command = "at  160 on 25/12/18";
		assertEquals("", textparser.extractDateAndTime(timeKeyword, command, tempHashTable));
		
		command = "at 1800 by 30/04/14";
		assertEquals("", textparser.extractDateAndTime(timeKeyword, command, tempHashTable));
		
		command = "on 30/04/14";
		assertEquals("", textparser.extractDateAndTime(timeKeyword, command, tempHashTable));
	}
	
	@Test
	public void checkTimeAndDateExtractionCondition () {
		String command = "";
		String timeKeyword = "at";

		ArrayList<Integer> matchVector = new ArrayList<Integer>();
		
		// this test only to check format. Not validity of time or date
		// dd/MM/yy or dd.MM.yy
		command = "at 1700 on 25/12/18";
		assertTrue(textparser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
		
		command = "at 600   on 25.12.18";
		assertTrue(textparser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
		
		command = "at 1800 on 111/04/18     ";
		assertFalse(textparser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
		
		command = "at 1800 on 11/04/180     ";
		assertFalse(textparser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
		
		command = "at 1300 on 110418     ";
		assertFalse(textparser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
		
		command = "at 1800 on 11/04/1993     ";
		assertFalse(textparser.checkTimeAndDateInputFormat(timeKeyword, command, matchVector));
	}
	
	@Test
	public void testTimeExtraction () {
		String command = "";
		String keyword = "by";
		Hashtable<KEYWORD_TYPE, String> tempHashTable = new Hashtable<KEYWORD_TYPE, String>();
		
		// Test normal format with spaces
		command = "by 1700";
		assertEquals("by 1700", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 600";
		assertEquals("by 600", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 1800 ";
		assertEquals("by 1800 ", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by  160";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 17:00";
		assertEquals("by 17:00", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 6:00";
		assertEquals("by 6:00", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 180:0 ";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 18:0 ";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 1:6000";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by :160";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by :160pm";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 6:00ampm";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by    6:00pmpm ";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 1:08pm ";
		assertEquals("by 1:08pm ", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 108pm";
		assertEquals("by 108pm", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 01:08pm";
		assertEquals("by 01:08pm", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 13pm";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 24am";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 0000";
		assertEquals("by 0000", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 2359";
		assertEquals("by 2359", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 2400";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by 2360";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
		command = "by -0001";
		assertEquals("", textparser.extractTimeOnly(keyword, command, tempHashTable));
		
	}
	
	@Test
	public void testDateExtraction () {
		String command = "";
		String keyword = "on";
		Hashtable<KEYWORD_TYPE, String> tempHashTable = new Hashtable<KEYWORD_TYPE, String>();
		
		command = "on  ";
		assertEquals("", textparser.extractDateOnly(keyword, keyword,command, tempHashTable));
		
		command = "on 12/06/06";
		assertEquals(command, textparser.extractDateOnly(keyword, keyword,command, tempHashTable));
		
		command = "on 12.06.06";
		assertEquals(command, textparser.extractDateOnly(keyword, keyword,command, tempHashTable));
		
		command = "on 120606";
		assertEquals("", textparser.extractDateOnly(keyword, keyword,command, tempHashTable));
		
		command = "on  120606 ";
		assertEquals("", textparser.extractDateOnly(keyword, keyword,command, tempHashTable));
		
		command = "on  1206065  ";
		assertEquals("", textparser.extractDateOnly(keyword, keyword,command, tempHashTable));
		
		command = "on  120.06.06 ";
		assertEquals("", textparser.extractDateOnly(keyword, keyword,command, tempHashTable));
		
		command = "on  1.06.06 ";
		assertEquals(command, textparser.extractDateOnly(keyword, keyword,command, tempHashTable));
		
		command = "on  1.6.06 ";
		assertEquals("", textparser.extractDateOnly(keyword, keyword,command, tempHashTable));
		
		command = "on  1.6.1906 ";
		assertEquals("", textparser.extractDateOnly(keyword, keyword,command, tempHashTable));
	
	}
	
	@Test
	public void testExtractModifyName() {
		Hashtable<KEYWORD_TYPE, String> keywordTable = new Hashtable<KEYWORD_TYPE, String>();
		assertEquals("> some name", textparser.extractModifiedTaskName("hello world > some name",keywordTable));
		assertEquals("> aabbcc ", textparser.extractModifiedTaskName("hello world > aabbcc ",keywordTable));
		assertEquals("> hello world", textparser.extractModifiedTaskName("> hello world",keywordTable));
		assertEquals("", textparser.extractModifiedTaskName("hello world",keywordTable));
		//error here
		assertEquals("> asd", textparser.extractModifiedTaskName("> hello world > asd",keywordTable));
	}
}