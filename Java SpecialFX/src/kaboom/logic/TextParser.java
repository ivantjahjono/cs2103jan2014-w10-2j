package kaboom.logic;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser {
	
	private static final String KEYWORD_STARTTIME = "at";
	private static final String KEYWORD_ENDTIME = "by";
	private static final String KEYWORD_DATE = "on";
	private static final String KEYWORD_MODIFY = ">";
	
	
	//******************** Method Calls By Controller ******************************************
	public static String getCommandKeyWord (String userInput) {
		return getFirstWord(userInput);
	}
	
	public static Hashtable<KEYWORD_TYPE, String> extractTaskInformation (String userInput) {
		String taskInformation = removeFirstWord(userInput);
		
		// Cut the command into their respective syntax. Will return hash table of data strings
		Hashtable<KEYWORD_TYPE, String> keywordHashTable = new Hashtable<KEYWORD_TYPE, String>();
		TextParser.parser(taskInformation, keywordHashTable);

		System.out.println(keywordHashTable);
		
		return keywordHashTable;	
	}

	//******************** Method Calls By Controller ******************************************
	
	public static String removeFirstWord(String userInputSentence) {
		String wordRemoved = userInputSentence.replace(getFirstWord(userInputSentence), "").trim();
		return wordRemoved;
	}

	public static String getFirstWord(String userInputSentence) {
		String[] elements = textProcess(userInputSentence);
		String firstWord = elements[0].toLowerCase();
		return firstWord;
	}
	
	private static String[] textProcess(String userInputSentence){
		String[] commandAndData = userInputSentence.trim().split("\\s+");
		return commandAndData;
	}
	
	public static String parser(String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		String userInputWithPriorityExtracted = extractPriority(userInputSentence,keywordTable);
		String userInputWithEndDateAndTimeExtracted = extractDateAndTime(KEYWORD_ENDTIME,userInputWithPriorityExtracted,keywordTable);
		String userInputWithStartDateAndTimeExtracted = extractDateAndTime(KEYWORD_STARTTIME,userInputWithEndDateAndTimeExtracted,keywordTable);
		String userInputWithModifiedTaskNameExtracted = extractModifiedTaskName(userInputWithStartDateAndTimeExtracted,keywordTable);
		String taskName = extractTaskName(userInputWithModifiedTaskNameExtracted,keywordTable);
		System.out.println(keywordTable);
		return taskName;
	}
	
	public static String extractPriority(String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		// Extract priority level to avoid complications
		String priorityRegex = "[\\s+]\\*{1,3}[\\s\\W]*";
	    Pattern asteriskPattern = Pattern.compile(priorityRegex);
	    int startIndex = 0;
	    int endIndex = 0;
	    int priorityLevel = 0;
	    
	    Matcher matcher = asteriskPattern.matcher(userInputSentence);
	    if (matcher.find()) {
	    	startIndex = matcher.start();
	    	endIndex = matcher.end();
	    }
	    
	    if (endIndex == 0) {
	    	return userInputSentence;
	    }
	    
	    // Extract the priority
	    String extractedPriorityString = userInputSentence.substring(startIndex, endIndex).trim();
	    userInputSentence = userInputSentence.substring(0, startIndex);
	    
	    priorityLevel = extractedPriorityString.length();
	    keywordTable.put(KEYWORD_TYPE.PRIORITY, ""+priorityLevel);
	    
	    return userInputSentence;
	}
	
	public static String extractDateAndTime(String KEYWORD_TIME, String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		String timeRegex1 = "\\d{3,4}";																// by 1700
		String timeRegex2 = "\\d{1,2}[:]?\\d{2}";													// by 17:00 or 6:00
		String timeRegex3 = "\\s+\\d{1,2}[:]?\\d{2}\\s*(am|pm)?(\\s|$)";							// by 6am or 1200pm
		String dateRegex1 = "\\s+\\d{1,2}[\\/\\.]?\\d{1,2}[\\/\\.]?\\d{2}(\\s|$)";					// 12/06/12 or 12.01.06 or 120106
		
		int startIndex = 0;
		int endIndex = 0;
		
		//GET PATTERN FOR WHOLE START/END DATE AND TIME
		ArrayList<Integer> matchList = searchForPatternMatch(userInputSentence, KEYWORD_TIME+" "+timeRegex1+" "+KEYWORD_DATE+dateRegex1);
		
		//IF NOTHING RETURN
		if (matchList.size() < 2) {
			return userInputSentence;
		}

		//EXTRACT START/END DATE AND TIME
		endIndex = matchList.get(matchList.size()-1);
		startIndex = matchList.get(matchList.size()-2);
		String extractedDateAndTimeString = userInputSentence.substring(startIndex, endIndex);
		
		//GET DATE
		matchList = searchForPatternMatch(extractedDateAndTimeString, KEYWORD_DATE+dateRegex1);
		endIndex = matchList.get(matchList.size()-1);
		startIndex = matchList.get(matchList.size()-2);
		String extractedDateString = extractedDateAndTimeString.substring(startIndex, endIndex).trim();
		extractedDateString = extractedDateString.replace(KEYWORD_DATE, "").trim();
		
		//GET TIME
		matchList = searchForPatternMatch(extractedDateAndTimeString, KEYWORD_TIME+" "+timeRegex1);
		endIndex = matchList.get(matchList.size()-1);
		startIndex = matchList.get(matchList.size()-2);
		String extractedTimeString = extractedDateAndTimeString.substring(startIndex, endIndex).trim();
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
		
		userInputSentence = userInputSentence.replace(extractedDateAndTimeString, "");
		
		return userInputSentence;
	}
	
	
	/*
	 * Extracts the last instance of the task name to modify
	 */
	public static String extractModifiedTaskName (String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {		
		int startIndex = 0;
		int endIndex = 0;
		if(userInputSentence.contains(KEYWORD_MODIFY)) {
			startIndex = userInputSentence.lastIndexOf(KEYWORD_MODIFY);
			endIndex = userInputSentence.length();
		
			String modifyTaskName = userInputSentence.substring(startIndex, endIndex);
			userInputSentence = userInputSentence.replace(modifyTaskName, "");
			modifyTaskName = modifyTaskName.replace(KEYWORD_MODIFY, "").trim();
			if(!modifyTaskName.isEmpty()) {
				keywordTable.put(KEYWORD_TYPE.MODIFIED_TASKNAME, modifyTaskName);
			}
		}
		return userInputSentence;
	}
	
	public static String extractTaskName(String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		String taskName = userInputSentence.trim();
		keywordTable.put(KEYWORD_TYPE.TASKNAME, taskName);
		return taskName;
	}

	private static ArrayList<Integer> searchForPatternMatch(String userInputSentence, String regex) {
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
	
	public static Queue<KeytypeIndexPair> getKeywordsInAscendingOrder(String[] tokenisedString) {
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
	
	//TEST EXTRACT FOR NEW CONTROLLER
	public static Hashtable<KEYWORD_TYPE, String> testExtractList(String userInput, Vector<KEYWORD_TYPE> list) {
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = getInfoFromList(userInput,list);
		return taskInformationTable;
	}
	
	private static Hashtable<KEYWORD_TYPE, String> getInfoFromList(String userInput, Vector<KEYWORD_TYPE> list) {
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = new Hashtable<KEYWORD_TYPE, String>();
		for(int i=0; i<list.size(); i++) {
			if(list.get(i) == KEYWORD_TYPE.PRIORITY) {
				userInput = extractPriority(userInput,taskInformationTable);
			}
			if(list.get(i) == KEYWORD_TYPE.END_TIME) {
				userInput = extractDateAndTime(KEYWORD_ENDTIME,userInput,taskInformationTable);
			}
			if(list.get(i) == KEYWORD_TYPE.START_TIME) {
				userInput = extractDateAndTime(KEYWORD_STARTTIME,userInput,taskInformationTable);
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
			
		}
		return taskInformationTable;
	}
	
	private static String extractViewType(String userInput, Hashtable<KEYWORD_TYPE,String> taskInformationTable) {
		String viewType = getFirstWord(userInput);
		taskInformationTable.put(KEYWORD_TYPE.VIEWTYPE, viewType);
		return removeFirstWord(userInput);
	}
	
}
