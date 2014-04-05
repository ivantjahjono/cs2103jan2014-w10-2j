package kaboom.logic;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser {
	
	private final String KEYWORD_STARTTIME = "at";
	private final String KEYWORD_ENDTIME = "by";
	private final String KEYWORD_DATE = "on";
	private final String KEYWORD_MODIFY = ">";
	
	private final String TIME_REGEX = "\\s+\\d{1,2}([:]?\\d{2})?\\s*(am|pm)?(\\s|$)";
	private final String DATE_REGEX = "\\s+\\d{1,2}[\\/\\.]?\\d{1,2}[\\/\\.]?\\d{2}(\\s|$)";
	private final String PRIORITY_REGEX = "[\\s+]\\*{1,3}[\\s\\W]*";
	
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

		System.out.println(keywordHashTable);
		
		return keywordHashTable;	
	}

	//******************** Method Calls By Controller ******************************************
	
	public String removeFirstWord(String userInputSentence) {
		String wordRemoved = userInputSentence.replace(getFirstWord(userInputSentence), "").trim();
		return wordRemoved;
	}

	public String getFirstWord(String userInputSentence) {
		String[] elements = textProcess(userInputSentence);
		String firstWord = elements[0].toLowerCase();
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
		System.out.println(keywordTable);
		return taskName;
	}
	
	public String extractPriority(String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
	    int startIndex = 0;
	    int endIndex = 0;
	    int priorityLevel = 0;
	    
		ArrayList<Integer> matchList = searchForPatternMatch(userInputSentence, PRIORITY_REGEX);
	    
		if (matchList.isEmpty()) {
			return userInputSentence;
		}
		
		endIndex = matchList.get(matchList.size()-1);
		startIndex = matchList.get(matchList.size()-2);
	    
	    // Extract the priority
	    String extractedPriorityString = userInputSentence.substring(startIndex, endIndex).trim();
	    priorityLevel = extractedPriorityString.length();
	    keywordTable.put(KEYWORD_TYPE.PRIORITY, ""+priorityLevel);
	    
	    userInputSentence = userInputSentence.replace(extractedPriorityString, "");
	    
	    return userInputSentence;
	}
	
	public String extractTimeOnly(String KEYWORD_TIME, String userInputSentence ,Hashtable<KEYWORD_TYPE, String> keywordTable){
																	
		int startIndex = 0;
		int endIndex = 0;
		
		ArrayList<Integer> matchList = searchForPatternMatch(userInputSentence, KEYWORD_TIME+TIME_REGEX);
		
		if (matchList.size() < 2) {
			return userInputSentence;
		}
		
		endIndex = matchList.get(matchList.size()-1);
		startIndex = matchList.get(matchList.size()-2);
		
		String extractedTimeString = userInputSentence.substring(startIndex, endIndex);
		
		userInputSentence = userInputSentence.replace(extractedTimeString, "");
		
		extractedTimeString = extractedTimeString.replace(KEYWORD_TIME, "").trim();
		
		switch(KEYWORD_TIME) {
		case KEYWORD_ENDTIME: 
			keywordTable.put(KEYWORD_TYPE.END_TIME, extractedTimeString);
			break;
		case KEYWORD_STARTTIME:
			keywordTable.put(KEYWORD_TYPE.START_TIME, extractedTimeString);
			break; 
		}
		
		return userInputSentence;
	}
	
	public String extractDateOnly(String KEYWORD_TIME, String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		int startIndex = 0;
		int endIndex = 0;
		
		ArrayList<Integer> matchList = searchForPatternMatch(userInputSentence, KEYWORD_TIME+DATE_REGEX);
		
		if (matchList.size() < 2) {
			return userInputSentence;
		}
		
		endIndex = matchList.get(matchList.size()-1);
		startIndex = matchList.get(matchList.size()-2);
		
		String extractedDateString = userInputSentence.substring(startIndex, endIndex).trim();
		
		userInputSentence = userInputSentence.replace(extractedDateString, "");
		
		extractedDateString = extractedDateString.replace(KEYWORD_TIME, "").trim();
		
		switch(KEYWORD_TIME) {
		case KEYWORD_ENDTIME: 
			keywordTable.put(KEYWORD_TYPE.END_DATE, extractedDateString);
			break;
		case KEYWORD_STARTTIME:
			keywordTable.put(KEYWORD_TYPE.START_DATE, extractedDateString);
			break; 
		}
		
		return userInputSentence;
	}
	
	public String extractDateAndTime(String KEYWORD_TIME, String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		String timeAndDateRegex = KEYWORD_TIME+TIME_REGEX+"\\s*"+KEYWORD_DATE+DATE_REGEX;
		
		int startIndex = 0;
		int endIndex = 0;
		
		
		//GET PATTERN FOR WHOLE START/END DATE AND TIME
		ArrayList<Integer> matchList = searchForPatternMatch(userInputSentence, timeAndDateRegex);
		
		//IF NOTHING RETURN
		if (matchList.size() < 2) {
			return userInputSentence;
		}
		
		//EXTRACT START/END DATE AND TIME
		endIndex = matchList.get(matchList.size()-1);
		startIndex = matchList.get(matchList.size()-2);
		String extractedDateAndTimeString = userInputSentence.substring(startIndex, endIndex);
		
		//GET DATE
		matchList = searchForPatternMatch(extractedDateAndTimeString, KEYWORD_DATE+DATE_REGEX);
		endIndex = matchList.get(matchList.size()-1);
		startIndex = matchList.get(matchList.size()-2);
		String extractedDateString = extractedDateAndTimeString.substring(startIndex, endIndex);
		extractedDateString = extractedDateString.replace(KEYWORD_DATE, "").trim();
		
		//GET TIME
		matchList = searchForPatternMatch(extractedDateAndTimeString, KEYWORD_TIME+TIME_REGEX);
		endIndex = matchList.get(matchList.size()-1);
		startIndex = matchList.get(matchList.size()-2);
		String extractedTimeString = extractedDateAndTimeString.substring(startIndex, endIndex);
		extractedTimeString = extractedTimeString.replace(KEYWORD_TIME, "").trim();	
		
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
		
		userInputSentence = userInputSentence.replace(extractedDateAndTimeString, "").trim();
		
		return userInputSentence;
	}
	
	
	/*
	 * Extracts the last instance of the task name to modify
	 */
	public String extractModifiedTaskName (String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {		
		int startIndex = 0;
		int endIndex = 0;
		if(userInputSentence.contains(KEYWORD_MODIFY)) {
			startIndex = userInputSentence.lastIndexOf(KEYWORD_MODIFY);
			endIndex = userInputSentence.length();
		
			String modifyTaskName = userInputSentence.substring(startIndex, endIndex);
			userInputSentence = userInputSentence.replace(modifyTaskName, "");
			modifyTaskName = modifyTaskName.replace(KEYWORD_MODIFY, "").trim();
			keywordTable.put(KEYWORD_TYPE.MODIFIED_TASKNAME, modifyTaskName);
		}
		return userInputSentence;
	}
	
	public String extractTaskName(String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		String taskName = userInputSentence.trim();
		keywordTable.put(KEYWORD_TYPE.TASKNAME, taskName);
		userInputSentence  = userInputSentence.replace(userInputSentence, "");
		
		return userInputSentence;
	}
	
	public String extractTaskId(String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		String taskId = userInputSentence.trim();
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
	
	public Queue<KeytypeIndexPair> getKeywordsInAscendingOrder(String[] tokenisedString) {
		Queue<KeytypeIndexPair> queue = new LinkedList<KeytypeIndexPair>();
		
		for (int i = 0; i < tokenisedString.length; i++) {
			KeytypeIndexPair currentPair = null;
			
			switch (tokenisedString[i]) {
				case KEYWORD_MODIFY:
					currentPair = new KeytypeIndexPair(KEYWORD_TYPE.MODIFIED_TASKNAME, i);
					break;
					
				case KEYWORD_STARTTIME:
					currentPair = new KeytypeIndexPair(KEYWORD_TYPE.START_TIME, i);
					break;
					
				case KEYWORD_ENDTIME:
					currentPair = new KeytypeIndexPair(KEYWORD_TYPE.END_TIME, i);
					break;
					
				case KEYWORD_DATE:
					currentPair = new KeytypeIndexPair(KEYWORD_TYPE.DATE, i);
					break;
			}
			
			if (currentPair != null) {
				queue.add(currentPair);
			}
		}
		
		return queue;
	}
	
	public Hashtable<KEYWORD_TYPE, String> testExtractList(String userInput, Vector<KEYWORD_TYPE> list) {
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = getInfoFromList(userInput,list);
		return taskInformationTable;
	}
	
	private Hashtable<KEYWORD_TYPE, String> getInfoFromList(String userInput, Vector<KEYWORD_TYPE> list) {
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = new Hashtable<KEYWORD_TYPE, String>();
		for(int i=0; i<list.size(); i++) {
			if(list.get(i) == KEYWORD_TYPE.PRIORITY) {
				userInput = extractPriority(userInput,taskInformationTable);
			}
			if(list.get(i) == KEYWORD_TYPE.END_TIME) {
				ArrayList<Integer> matchList = new ArrayList<Integer>();
				if(checkTimeAndDateInputFormat(KEYWORD_ENDTIME, userInput, matchList)){
					userInput = extractDateAndTime(KEYWORD_ENDTIME,userInput,taskInformationTable);
				}
				else if(checkTimeOnlyInputFormat(KEYWORD_ENDTIME, userInput, matchList)){
					userInput = extractTimeOnly(KEYWORD_ENDTIME,userInput,taskInformationTable);
				}
				else if(checkDateOnlyInputFormat(KEYWORD_ENDTIME, userInput, matchList)){
					userInput = extractDateOnly(KEYWORD_ENDTIME,userInput,taskInformationTable);
				}
				
			}
			if(list.get(i) == KEYWORD_TYPE.START_TIME) {
				//userInput = extractDateAndTime(KEYWORD_STARTTIME,userInput,taskInformationTable);
				ArrayList<Integer> matchList = new ArrayList<Integer>();
				if(checkTimeAndDateInputFormat(KEYWORD_STARTTIME, userInput, matchList)){
					userInput = extractDateAndTime(KEYWORD_STARTTIME,userInput,taskInformationTable);
				}
				else if(checkTimeOnlyInputFormat(KEYWORD_STARTTIME, userInput, matchList)){
					userInput = extractTimeOnly(KEYWORD_STARTTIME,userInput,taskInformationTable);
				}
				else if(checkDateOnlyInputFormat(KEYWORD_STARTTIME, userInput, matchList)){
					userInput = extractDateOnly(KEYWORD_STARTTIME,userInput,taskInformationTable);
				}
			}
			if(list.get(i) == KEYWORD_TYPE.MODIFIED_TASKNAME) {
				userInput = extractModifiedTaskName(userInput,taskInformationTable);
			}
			if(list.get(i) == KEYWORD_TYPE.TASKNAME) {
				userInput = extractTaskName(userInput,taskInformationTable);
			}
			if(list.get(i) == KEYWORD_TYPE.VIEWTYPE) {
				userInput = extractViewType(userInput,taskInformationTable);
			}	
			if(list.get(i) == KEYWORD_TYPE.TASKID) {
				userInput = extractTaskId(userInput,taskInformationTable);
			}	
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
		userInput  = userInput.replace(unknownCommandString, "").trim();
		
		return userInput;
	}
	
	private boolean checkDateOnlyInputFormat(String KEYWORD_TIME, String userInputSentence, ArrayList<Integer> matchVector){
		matchVector = searchForPatternMatch(userInputSentence, KEYWORD_TIME+DATE_REGEX);
		
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
		String timeAndDateRegex = KEYWORD_TIME+TIME_REGEX+"\\s*"+KEYWORD_DATE+DATE_REGEX;
		
		//GET PATTERN FOR WHOLE START/END DATE AND TIME
		matchVector = searchForPatternMatch(userInputSentence, timeAndDateRegex);
		
		//IF NOTHING RETURN
		if (matchVector.size() < 2) {
			return false;
		}
		else{
			return true;
		}
	}
	
	private String extractViewType(String userInput, Hashtable<KEYWORD_TYPE,String> taskInformationTable) {
		String viewType = getFirstWord(userInput);
		taskInformationTable.put(KEYWORD_TYPE.VIEWTYPE, viewType);
		
		userInput  = userInput.replace(viewType, "").trim();
		return userInput;
	}
	
}
