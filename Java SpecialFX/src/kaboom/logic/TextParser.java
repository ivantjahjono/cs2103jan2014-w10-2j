//@author A0099863H
package kaboom.logic;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kaboom.shared.KEYWORD_TYPE;

public class TextParser {
	private final String KEYWORD_STARTTIME = "(at|from)\\s";
	private final String KEYWORD_ENDTIME = "(by|to)\\s";
	private final String KEYWORD_DATE = "on";
	private final String KEYWORD_MODIFY = ">";
	private final String KEYWORD_TASKID = "#";
	private final String KEYWORD_DATEONLY = "^";
	private final String KEYWORD_CLEAR = "^(all|current|archive)";
	private final String KEYWORD_VIEW = "^(today|future|timeless|expired|archive)";
	private final String KEYWORD_HELP = "^(add|delete|modify|complete|search|view|page|close)";
	
	private final String TIME_REGEX = "\\s*(([0-9]|0[0-9]|1[0-9]|2[0-3])([\\s?:\\s?]?[0-5][0-9])?|([0-9]|0[1-9]|1[0-2])(([\\s?:\\s?]?[0-5][0-9])?(am|pm)))(\\s|$)";
	private final String DATE_REGEX = "\\s*\\d{1,2}[\\/\\.]\\d{2}[\\/\\.]\\d{2}(\\s|$)";
	private final String DATE_NAME_REGEX = "\\s*(today|tmr|tomorrow|monday|tuesday|wednesday|thursday|friday|saturday|sunday)(\\s|$)";
	private final String FULLDATE_REGEX = "((%1$s" + DATE_REGEX + ")|(%2$s" + DATE_NAME_REGEX + "))";
	private final String ID_REGEX = "^\\s*\\d+(\\s+|$)";
	private final String PRIORITY_REGEX = "[\\s+]\\*{1,5}[\\s\\W]*";
	private final String PAGE_REGEX = "^\\s*(\\d+|next|prev)\\s*$";
	static TextParser instance;
	
	private TextParser () {
	}
	
	public static TextParser getInstance () {
		if (instance == null) {
			instance = new TextParser();
		}
		
		return instance;
	}
	
	//******************** Method Calls By Controller ******************************************
	public String getCommandKeyWord (String userInput) {
		return getFirstWord(userInput);
	}
	
	
	public Hashtable<KEYWORD_TYPE, String> extractTaskInformation (String userInput) {
		String taskInformation = removeFirstWord(userInput);
		
		// Cut the command into their respective syntax. Will return hash table of data strings
		Hashtable<KEYWORD_TYPE, String> keywordHashTable = new Hashtable<KEYWORD_TYPE, String>();
		parser(taskInformation, keywordHashTable);

		//System.out.println(keywordHashTable);
		
		return keywordHashTable;	
	}

	//******************** Method Calls By Controller ******************************************
	public String removeFirstWord(String userInputSentence) {
		String wordRemoved = userInputSentence.replace(getFirstWord(userInputSentence), "").trim();
		return wordRemoved;
	}

	public String getFirstWord(String userInputSentence) {
		String[] elements = textProcess(userInputSentence);
		String firstWord = elements[0];
		return firstWord;
	}
	
	private String[] textProcess(String userInputSentence){
		String[] commandAndData = userInputSentence.trim().split("\\s+");
		return commandAndData;
	}
	
	public String parser(String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		String userInputWithPriorityExtracted = extractPriority(userInputSentence,keywordTable);
		String userInputWithEndDateAndTimeExtracted = extractDateAndTime(KEYWORD_ENDTIME,userInputWithPriorityExtracted,keywordTable);
		String userInputWithStartDateAndTimeExtracted = extractDateAndTime(KEYWORD_STARTTIME,userInputWithEndDateAndTimeExtracted,keywordTable);
		String userInputWithModifiedTaskNameExtracted = extractModifiedTaskName(userInputWithStartDateAndTimeExtracted,keywordTable);
		String taskName = extractTaskName(userInputWithModifiedTaskNameExtracted,keywordTable);
		//System.out.println(keywordTable);
		return taskName;
	}
	
	public String extractPriority(String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
	    int startIndex = 0;
	    int endIndex = 0;
	    int priorityLevel = 0;
	    
		ArrayList<Integer> matchList = searchForPatternMatch(userInputSentence, PRIORITY_REGEX);
	    
		if (matchList.isEmpty()) {
			return "";
		}
		
		endIndex = matchList.get(matchList.size()-1);
		startIndex = matchList.get(matchList.size()-2);
	    
	    // Extract the priority
	    String extractedPriorityString = userInputSentence.substring(startIndex, endIndex).trim();
	    priorityLevel = extractedPriorityString.length();
	    keywordTable.put(KEYWORD_TYPE.PRIORITY, ""+priorityLevel);
	    
	    return extractedPriorityString;
	}
	
	public String extractTimeOnly(String KEYWORD_TIME, String userInputSentence ,Hashtable<KEYWORD_TYPE, String> keywordTable){										
		int startIndex = 0;
		int endIndex = 0;
		
		ArrayList<Integer> matchList = searchForPatternMatch(userInputSentence, KEYWORD_TIME+TIME_REGEX);
		
		if (matchList.size() < 2) {
			return "";
		}
		
		endIndex = matchList.get(matchList.size()-1);
		startIndex = matchList.get(matchList.size()-2);
		
		String extractedTimeString = userInputSentence.substring(startIndex, endIndex);
		String finalExtractedTimeString = extractedTimeString.replaceAll(KEYWORD_TIME, "").trim();
		
		switch(KEYWORD_TIME) {
			case KEYWORD_ENDTIME: 
				keywordTable.put(KEYWORD_TYPE.END_TIME, finalExtractedTimeString);
				break;
			case KEYWORD_STARTTIME:
				keywordTable.put(KEYWORD_TYPE.START_TIME, finalExtractedTimeString);
				break;
		}
		
		return extractedTimeString;
	}
	
	public String extractDateOnly(String KEYWORD_TIME, String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		return extractDateOnly(KEYWORD_TIME, KEYWORD_TIME, userInputSentence, keywordTable);
	}
	
	public String extractDateOnly(String KEYWORD_TIME, String SECOND_KEYWORD_TIME, String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		// TODO allow where to store at for the hashtable, should not detect by keyword
		int startIndex = 0;
		int endIndex = 0;
		
		String fulldateFormat = String.format(FULLDATE_REGEX, KEYWORD_TIME, SECOND_KEYWORD_TIME);
		
		ArrayList<Integer> matchList = searchForPatternMatch(userInputSentence, fulldateFormat);
		
		if (matchList.size() < 2) {
			return "";
		}
		
		endIndex = matchList.get(matchList.size()-1);
		startIndex = matchList.get(matchList.size()-2);
		
		String extractedDateString = userInputSentence.substring(startIndex, endIndex).trim();
		String finalExtractedDateString = extractedDateString.replaceAll(KEYWORD_TIME, "").trim();
		
		switch(KEYWORD_TIME) {
		case KEYWORD_ENDTIME: 
			keywordTable.put(KEYWORD_TYPE.END_DATE, finalExtractedDateString);
			break;
			
		case KEYWORD_STARTTIME:
			keywordTable.put(KEYWORD_TYPE.START_DATE, finalExtractedDateString);
			break;
			
		case KEYWORD_DATEONLY:
			keywordTable.put(KEYWORD_TYPE.DATE, finalExtractedDateString);
			break;
		}
		
		return extractedDateString;
	}
	
	public String extractDateAndTime(String KEYWORD_TIME, String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		String fulldateFormat = KEYWORD_DATE + DATE_REGEX;
		String dateFormat2 = DATE_NAME_REGEX;
		String timeAndDateRegex = KEYWORD_TIME+TIME_REGEX+"\\s*"+fulldateFormat;
		String timeAndDateRegex2 = KEYWORD_TIME+TIME_REGEX+"\\s*"+dateFormat2;
		
		//GET PATTERN FOR WHOLE START/END DATE AND TIME
		ArrayList<Integer> matchList = searchForPatternMatch(userInputSentence, timeAndDateRegex);
		ArrayList<Integer> matchList2 = searchForPatternMatch(userInputSentence, timeAndDateRegex2);
		ArrayList<Integer> matchListToUse = matchList;
		
		int startIndex = 0;
		int endIndex = 0;
		
		//IF NOTHING RETURN
		if (matchList.size() < 2 && matchList2.size() < 2) {
			return "";
		}
		
		if (matchList.size() < 2) {
			matchListToUse = matchList2;
		}
		
		//EXTRACT START/END DATE AND TIME
		endIndex = matchListToUse.get(matchListToUse.size()-1);
		startIndex = matchListToUse.get(matchListToUse.size()-2);
		String extractedDateAndTimeString = userInputSentence.substring(startIndex, endIndex);
		
		//GET DATE
		String extractedDateString = extractDateOnly(KEYWORD_DATE, "", extractedDateAndTimeString, keywordTable);
		extractedDateString = extractedDateString.replaceAll(KEYWORD_DATE, "").trim();
		
		//GET TIME
		String extractedTimeString = extractTimeOnly(KEYWORD_TIME, extractedDateAndTimeString, keywordTable);
		extractedTimeString = extractedTimeString.replaceAll(KEYWORD_TIME, "").trim();
		
		switch(KEYWORD_TIME) {
		case KEYWORD_ENDTIME: 
			keywordTable.put(KEYWORD_TYPE.END_TIME, extractedTimeString);
			keywordTable.put(KEYWORD_TYPE.END_DATE, extractedDateString);
			break;
		case KEYWORD_STARTTIME:
			keywordTable.put(KEYWORD_TYPE.START_TIME, extractedTimeString);
			keywordTable.put(KEYWORD_TYPE.START_DATE, extractedDateString);
			break;
		}
		
		return extractedDateAndTimeString;
	}
	
	
	/*
	 * Extracts the last instance of the task name to modify
	 */
	public String extractModifiedTaskName (String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {		
		int startIndex = 0;
		int endIndex = 0;
		String modifyTaskName = "";
		if(userInputSentence.contains(KEYWORD_MODIFY)) {
			startIndex = userInputSentence.lastIndexOf(KEYWORD_MODIFY);
			endIndex = userInputSentence.length();
		
			modifyTaskName = userInputSentence.substring(startIndex, endIndex);
			userInputSentence = userInputSentence.replace(modifyTaskName, "");
			String finalModifyTaskName = modifyTaskName.replace(KEYWORD_MODIFY, "").trim();
			keywordTable.put(KEYWORD_TYPE.MODIFIED_TASKNAME, finalModifyTaskName);
		}
		
		return modifyTaskName;
	}
	
	public String extractTaskName(String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		String taskName = userInputSentence.trim();
		keywordTable.put(KEYWORD_TYPE.TASKNAME, taskName);
		
		return taskName;
	}
	
	public String extractTaskId(String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		int startIndex = 0;
		int endIndex = 0;
		ArrayList<Integer> matchList = searchForPatternMatch(userInputSentence, ID_REGEX);
		if (matchList.size() < 2) {
			return "";
		}
		endIndex = matchList.get(matchList.size()-1);
		startIndex = matchList.get(matchList.size()-2);
		String taskId = userInputSentence.substring(startIndex, endIndex);;
		taskId = taskId.replace(KEYWORD_TASKID, "").trim();
		keywordTable.put(KEYWORD_TYPE.TASKID, taskId);
		return taskId;
	}

	private ArrayList<Integer> searchForPatternMatch(String userInputSentence, String regex) {
		int startIndex = 0;
	    int endIndex = 0;
	    
	    ArrayList<Integer> matchList = new ArrayList<Integer>();
	    
	    Pattern asteriskPattern = Pattern.compile(regex);
		Matcher matcher = asteriskPattern.matcher(userInputSentence);
	    while (matcher.find()) {
	    	startIndex = matcher.start();
	    	endIndex = matcher.end();
	    	
	    	matchList.add(startIndex);
	    	matchList.add(endIndex);
	    } 
	    
	    if (matchList.size() == 0) {
	    }
	    
	    return matchList;
	}
	
	public Hashtable<KEYWORD_TYPE, String> testExtractList(String userInput, KEYWORD_TYPE[] list) {
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = getInfoFromList(userInput,list);
		return taskInformationTable;
	}
	
	private Hashtable<KEYWORD_TYPE, String> getInfoFromList(String userInput, KEYWORD_TYPE[] list) {
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = new Hashtable<KEYWORD_TYPE, String>();
		String result = "";
		
		for(int i=0; i<list.length; i++) {
			switch(list[i]) {
			
			case DATE:
				result = extractDateOnly("^", userInput, taskInformationTable);
				break;
			
			case PRIORITY: 
				result = extractPriority(userInput,taskInformationTable);
				break;
				
			case END_TIME:
				ArrayList<Integer> endTimeMatchList = new ArrayList<Integer>();
				if(checkTimeAndDateInputFormat(KEYWORD_ENDTIME, userInput, endTimeMatchList)){
					result = extractDateAndTime(KEYWORD_ENDTIME,userInput,taskInformationTable);
				}
				else if(checkTimeOnlyInputFormat(KEYWORD_ENDTIME, userInput, endTimeMatchList)){
					result = extractTimeOnly(KEYWORD_ENDTIME,userInput,taskInformationTable);
				}
				else if(checkDateOnlyInputFormat(KEYWORD_ENDTIME, userInput, endTimeMatchList)){
					result = extractDateOnly(KEYWORD_ENDTIME, userInput, taskInformationTable);
				}
				break;
				
			case START_TIME:
				//userInput = extractDateAndTime(KEYWORD_STARTTIME,userInput,taskInformationTable);
				ArrayList<Integer> startTimeMatchList = new ArrayList<Integer>();
				if(checkTimeAndDateInputFormat(KEYWORD_STARTTIME, userInput, startTimeMatchList)){
					result = extractDateAndTime(KEYWORD_STARTTIME,userInput,taskInformationTable);
				}
				else if(checkTimeOnlyInputFormat(KEYWORD_STARTTIME, userInput, startTimeMatchList)){
					result = extractTimeOnly(KEYWORD_STARTTIME,userInput,taskInformationTable);
				}
				else if(checkDateOnlyInputFormat(KEYWORD_STARTTIME, userInput, startTimeMatchList)){
					result = extractDateOnly(KEYWORD_STARTTIME,userInput,taskInformationTable);
				}
				break;
				
			case MODIFIED_TASKNAME:
				result = extractModifiedTaskName(userInput,taskInformationTable);
				break;
				
			case TASKNAME:
				result = extractTaskName(userInput,taskInformationTable);
				break;
				
			case CLEARTYPE:
				result = extractClearType(userInput,taskInformationTable);
				break;
				
			case VIEWTYPE:
				result = extractViewType(userInput,taskInformationTable);
				break;
				
			case TASKID:
				result = extractTaskId(userInput,taskInformationTable);
				break;
				
			case HELP:
				result = extractHelpType(userInput,taskInformationTable);
				break;
				
			case PAGE:
				result = extractPageType(userInput,taskInformationTable);
				break;
				
			default:
				break;
			}
			
			userInput = userInput.replace(result, "").trim();
		}
		
		// If there is still unextracted information after parsing, consider it as invalid command
		if (!userInput.equals("")) {
			extractUnknownCommandString(userInput, taskInformationTable);
		}
		
		return taskInformationTable;
	}


	private String extractUnknownCommandString(String userInput, Hashtable<KEYWORD_TYPE, String> taskInformationTable) {
		String unknownCommandString = userInput.substring(0);
		taskInformationTable.put(KEYWORD_TYPE.INVALID, unknownCommandString);
		
		return unknownCommandString;
	}
	
	private boolean checkDateOnlyInputFormat(String KEYWORD_TIME, String userInputSentence, ArrayList<Integer> matchVector){
		String fulldateFormat = String.format(FULLDATE_REGEX, KEYWORD_TIME, KEYWORD_TIME);
		matchVector = searchForPatternMatch(userInputSentence, fulldateFormat);
		
		//IF NOTHING RETURN
		if (matchVector.size() < 2) {
			return false;
		}
		else{
			return true;
		}
	}
	
	private boolean checkTimeOnlyInputFormat(String KEYWORD_TIME, String userInputSentence, ArrayList<Integer> matchVector){		
		//GET PATTERN FOR WHOLE START/END DATE AND TIME
		matchVector = searchForPatternMatch(userInputSentence, KEYWORD_TIME+TIME_REGEX);
		
		//IF NOTHING RETURN
		if (matchVector.size() < 2) {
			return false;
		}
		else{
			return true;
		}
	}
	
	public boolean checkTimeAndDateInputFormat(String KEYWORD_TIME, String userInputSentence, ArrayList<Integer> matchVector){		
		String fulldateFormat = KEYWORD_DATE + DATE_REGEX;
		String dateFormat2 = DATE_NAME_REGEX;
		String timeAndDateRegex = KEYWORD_TIME+TIME_REGEX+"\\s*"+fulldateFormat;
		String timeAndDateRegex2 = KEYWORD_TIME+TIME_REGEX+"\\s*"+dateFormat2;
		
		//GET PATTERN FOR WHOLE START/END DATE AND TIME
		ArrayList<Integer> matchList = searchForPatternMatch(userInputSentence, timeAndDateRegex);
		ArrayList<Integer> matchList2 = searchForPatternMatch(userInputSentence, timeAndDateRegex2);
		
		//IF NOTHING RETURN
		if (matchList.size() < 2 && matchList2.size() < 2) {
			return false;
		}
		else{
			return true;
		}
	}
	
	private String extractViewType(String userInput, Hashtable<KEYWORD_TYPE,String> taskInformationTable) {		
		ArrayList<Integer> matchList = searchForPatternMatch(userInput, KEYWORD_VIEW);
		
		if (matchList.size() < 2) {
			return "";
		}
		
		int endIndex = matchList.get(matchList.size()-1);
		int startIndex = matchList.get(matchList.size()-2);
		
		String extractedViewString = userInput.substring(startIndex, endIndex).trim();
		taskInformationTable.put(KEYWORD_TYPE.VIEWTYPE, extractedViewString);
		
		return extractedViewString;
	}
	
	private String extractClearType(String userInput, Hashtable<KEYWORD_TYPE,String> taskInformationTable) {
		ArrayList<Integer> matchList = searchForPatternMatch(userInput, KEYWORD_CLEAR);
		
		if (matchList.size() < 2) {
			return "";
		}
		
		int endIndex = matchList.get(matchList.size()-1);
		int startIndex = matchList.get(matchList.size()-2);
		
		String extractedClearString = userInput.substring(startIndex, endIndex).trim();
		taskInformationTable.put(KEYWORD_TYPE.CLEARTYPE, extractedClearString);

		return extractedClearString;
	}
	
	private String extractHelpType(String userInput, Hashtable<KEYWORD_TYPE,String> taskInformationTable) {
		ArrayList<Integer> matchList = searchForPatternMatch(userInput, KEYWORD_HELP);
		
		if (matchList.size() < 2) {
			return "";
		}
		
		int endIndex = matchList.get(matchList.size()-1);
		int startIndex = matchList.get(matchList.size()-2);
		
		String extractedClearString = userInput.substring(startIndex, endIndex).trim();
		taskInformationTable.put(KEYWORD_TYPE.HELP, extractedClearString);

		return extractedClearString;
	}
	
	private String extractPageType(String userInput, Hashtable<KEYWORD_TYPE,String> taskInformationTable) {
		ArrayList<Integer> matchList = searchForPatternMatch(userInput, PAGE_REGEX);
		
		if (matchList.size() < 2) {
			return "";
		}
		
		int endIndex = matchList.get(matchList.size()-1);
		int startIndex = matchList.get(matchList.size()-2);
		
		String extractedString = userInput.substring(startIndex, endIndex).trim();
		taskInformationTable.put(KEYWORD_TYPE.PAGE, extractedString);

		return extractedString;
	}
}
