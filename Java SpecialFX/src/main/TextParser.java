package main;

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
	private static final String KEYWORD_PRIORITY = "*";
	private static final String KEYWORD_MODIFY = ">";
	

	
	// Temporary static
	//private static int counter = 1;				// Use to create temporary task
	
	//******************** Method Calls By Controller ******************************************
	public static String getCommandKeyWord (String userInput) {
		return getFirstWord(userInput);
	}
	
	public static Hashtable<KEYWORD_TYPE, String> extractTaskInformation (String userInput) {
		String taskInformation = removeFirstWord(userInput);
		
		
		
		// Cut the command into their respective syntax. Will return hash table of data strings
		Hashtable<KEYWORD_TYPE, String> keywordHashTable = new Hashtable<KEYWORD_TYPE, String>();
		TextParser.possibleParser(taskInformation, keywordHashTable);
		
		//Error errorEncountered = createKeywordTableBasedOnParameter(taskInformation, keywordHashTable);
//		if (errorEncountered != null) {
//			//return errorEncountered;
//		}
		
		System.out.println(keywordHashTable);

		
		
		
		return keywordHashTable;
		
		
		// TODO
		// One possible way of determining the command syntax.
		// 1. Determine the command type. Each command has a different syntax and even same command
		//    might have different formats. The command type can also be passed in by parameter
		//    to avoid determining the command type again.
		//    E.g: add hello world at 1130pm on 120112 by 1230am on 130112 ***
		//         <command> <task name> at <start time> on <date> by <end time> on <date>
		
		// 2. Switch on all syntax checks for keywords for that particular command.
		//    E.g: hello world at 1130pm on 120112 by 1230am on 130112 ***
		//    <task name> at <start time> on <date> by <end time> on <date> <priority level>
		
		
		// 3. Find the next closest syntax using the keywords such as 'at', 'by' or '*' for priority.
		//    based on syntax checks that is on.
		//    E.g: done <task name>
		//    Checks for keyword 'at' or 'by' are switched off as they are not related to the command 'done'.  
		// 3a. Cut the command until the next closest syntax.
		//    E.g: 1. hello world 
		//         2. at <start time> on <date> by <end time> on <date> <priority level>
		// 3b. The first string will be the task name. Update the information to task info.
		
		
		// 4. Find the next closest syntax again using the keywords such as 'at', 'by' or '*' for priority.
		// 4a. If there is no such keywords found, end the parsing. If not cut the command again using the
		//     remaining string.
		//     E.g: 1. at <start time> on <date>
		//          2. by <end time> on <date> <priority level>
		// 4b. Determine the syntax by the keyword. If it contains 'at' it is a start time, 
		//     if it has 'by' it is an end time and so on.
		
		
		// 5. Repeating steps 3 and 4 until there is no string left or no keyword is found.
		
		
		// 6. In steps of updating to task info, validate each information if it matches the formats.
		//    E.g: by 1230am on 130112***
		//         It is not a valid format as the asterisks are combined. Might be due to user
		//         typo.
		
		
		
		
	}
	//******************************************************************************************
	
	
	private static String removeFirstWord(String userInputSentence) {
		String wordRemoved = userInputSentence.replace(getFirstWord(userInputSentence), "").trim();
		return wordRemoved;
	}

	private static String getFirstWord(String userInputSentence) {
		String[] elements = textProcess(userInputSentence);
		String firstWord = elements[0].toLowerCase();
		return firstWord;
	}
	
	private static String[] textProcess(String userInputSentence){
		String[] commandAndData = userInputSentence.trim().split("\\s+");
		return commandAndData;
	}
	
	
	//TODO Clean up and refactor code
	public static Error createKeywordTableBasedOnParameter(String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		String currentString = userInputSentence;
		int nextKeywordIndex = 0;
		KEYWORD_TYPE type = KEYWORD_TYPE.INVALID;
		KEYWORD_TYPE prevType = type;
		String cutOutString = "";
		
		while (nextKeywordIndex != -1) {
			// Get index of next keyword and keyword type
			nextKeywordIndex = getNextKeywordIndex(currentString);
			
			if (currentString.length() > 0 && nextKeywordIndex < 1) {
				nextKeywordIndex =  currentString.length();
			}
			
			if (nextKeywordIndex != -1) {
				// Cut to next keyword
				cutOutString = currentString.substring(0, nextKeywordIndex);
				
				if (keywordTable.isEmpty()) {
					type = KEYWORD_TYPE.TASKNAME;
				} else {
					type = getKeywordType(cutOutString);
				}
				
				if (type == KEYWORD_TYPE.START_DATE && prevType == KEYWORD_TYPE.END_TIME) {
					type = KEYWORD_TYPE.END_DATE;
				}
				
				// Is there already data in table for that type ?
				if (keywordTable.get(type) != null) {
					return new Error(Error.ERROR_TYPE.MESSAGE_DUPLICTE_COMMAND_PARAMETERS);
				}
				
				//further cut out the required information from the string
				String information = "";
				if (type != KEYWORD_TYPE.TASKNAME) {
					information = getInformation (cutOutString);
					// add to table
					keywordTable.put(type, information);
				}
				else {
					// add to table
					keywordTable.put(type, cutOutString);
				}
					
				// Remove the string from the original string
				currentString = currentString.replaceFirst(cutOutString, "");
				
				prevType = type;
			}
		}
		
		return null;
	}
	
	
	public static String getInformation (String cutOutString) {
		String[] tokenisedString = cutOutString.split("\\s+");
		if (tokenisedString.length < 2) {
			return "";
		}
		return tokenisedString[2];
	}
	
	
	public static int getNextKeywordIndex(String stringToSearch) {
		int nearestIndex = -1;
		int currentIndex = 0;
		String currentKeyword = "";
		
		// Hard coded value
		for (int i = 0; i < 4; i++) {
			switch(i) {
				case 0:
					currentKeyword = " at ";
					break;
					
				case 1:
					currentKeyword = " by ";
					break;
					
				case 2:
					currentKeyword = " * ";
					break;
					
				case 3:
					currentKeyword = " on ";
					break;
			}
			
			currentIndex = stringToSearch.indexOf(currentKeyword);
			
			if (currentIndex > 0  && (nearestIndex == -1 || currentIndex < nearestIndex)) {
				nearestIndex = currentIndex;
			}
		}
		
		return nearestIndex;
	}
	
	private static KEYWORD_TYPE getKeywordType(String cutOutString) {
		// TODO Find ways to refactor this
		if (cutOutString.contains(KEYWORD_STARTTIME)) {
			return KEYWORD_TYPE.START_TIME;
		} else if (cutOutString.contains(KEYWORD_ENDTIME)) {
			return KEYWORD_TYPE.END_TIME;
		} else if (cutOutString.contains(KEYWORD_PRIORITY)) {
			return KEYWORD_TYPE.PRIORITY;
		} else if (cutOutString.contains(KEYWORD_DATE)) {
			return KEYWORD_TYPE.START_DATE;
		}
		
		return KEYWORD_TYPE.INVALID;
	}
	
	
	//FOR TESTING
	public static Vector<Integer> getListOfKeywordPositions (String stringToSearch) {
		Vector<Integer> keywordPositions = new Vector<Integer>();
		
		String currentKeyword = "";
		int numOfKeywords = 5;
		
		for (int i = 0; i < numOfKeywords; i++) {
			switch(i) {
				case 0:
					currentKeyword = ">";
					break;
					
				case 1:
					currentKeyword = "at";
					break;
					
				case 2:
					currentKeyword = "by";
					break;
					
				case 3:
					currentKeyword = "*";
					break;
					
				case 4:
					currentKeyword = "on";
					break;
					
	
			}
			
			int currentIndex = stringToSearch.indexOf(currentKeyword);
			if (currentIndex > 0) {
				keywordPositions.add(currentIndex);
			}
		}
		
		return keywordPositions;
	}
	
	
	
	
	//**********************TO BE REVIEWED*********************************************************
	public static Error asd(String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		String currentString = userInputSentence;
		String currentKeyword = "";
		String nextKeyword = "";
		String previousKeyword = "";
		KEYWORD_TYPE type = KEYWORD_TYPE.INVALID;
		
		String asc = Pattern.quote(currentString);
		System.out.println(asc);
		
		Queue<String> keyWordsList = getKeywordsOrder(currentString);
		
		while(!currentString.isEmpty()) {
			//1. Get keywords
			previousKeyword = currentKeyword;
			currentKeyword = nextKeyword;
			nextKeyword = keyWordsList.poll();
			
			/*
			 * 2. get the type (with check)			
			 * There are 4 conditions.
			 * -On without an at or by before
			 * -on with an at before = start date
			 * -on with an by before = end date
			 * -anything else
			 */
			if(currentKeyword.equals(KEYWORD_DATE)) {
				switch(previousKeyword) {
				case KEYWORD_STARTTIME:
					type = KEYWORD_TYPE.START_DATE;
					break;
				case KEYWORD_ENDTIME:
					type = KEYWORD_TYPE.END_DATE;
					break;
				default: 
					type = KEYWORD_TYPE.INVALID;
				}
				
			}
			else {
				type = getKeywordType2(currentKeyword);
			}
			
			
			
			
			
			
			//3. chop and get the bunch of text before the next keyword
			String infoChunk = "";
			
			if (nextKeyword != null) {
				infoChunk = currentString.split(nextKeyword)[0].trim();
			}
			else {
				infoChunk = currentString;
			}
			
			
			//4. get the information by removing any keywords
			String info = infoChunk.replace(currentKeyword, "").trim();
			
			// 4.1 with this info can do checking base on current keyword (or can be done in controller or command)
			switch(type) {
			case START_TIME: 
				//checktime method;
				break;
			case END_TIME:
				//checktime method + isTimeBefore starttime;
				break;
			case START_DATE:
				//checkdate method;
				break;
			case END_DATE:
				//checkdate method + isDateBefore starttime;
				break;
			default: 
				//nothing yet
			}
			
			//5. put in table
			keywordTable.put(type, info);
			
			//6. remove the bunch of text + keyword from the original string
			if (nextKeyword != null) {
				currentString = currentString.replace(infoChunk.concat(" "+nextKeyword), "");
			}
			else {
				currentString = currentString.replace(infoChunk, "");
			}
		}
		return null;
	}
	

	
	public static Queue<String> getKeywordsOrder(String sentence) {
		Queue<String> q = new LinkedList<String>();
		String[] sentenceA = sentence.split("\\s+");
		for (int i = 0; i< sentenceA.length; i++) {
			if (sentenceA[i].equals(KEYWORD_MODIFY)) {
				q.add(sentenceA[i]);
			}
			if (sentenceA[i].equals(KEYWORD_STARTTIME)) {
				q.add(sentenceA[i]);
			}
			if (sentenceA[i].equals(KEYWORD_ENDTIME)) {
				q.add(sentenceA[i]);
			}
			if (sentenceA[i].equals(KEYWORD_PRIORITY)) {
				q.add(sentenceA[i]);
			}
			if (sentenceA[i].equals(KEYWORD_DATE)) {
				q.add(sentenceA[i]);
			}
			
		}
		
		return q;
	}

	private static KEYWORD_TYPE getKeywordType2(String cutOutString) {
		// TODO Find ways to refactor this
		switch (cutOutString) {
			case KEYWORD_STARTTIME:
				return KEYWORD_TYPE.START_TIME;
			
			case KEYWORD_ENDTIME:
				return KEYWORD_TYPE.END_TIME;
				
			case KEYWORD_PRIORITY:
				return KEYWORD_TYPE.PRIORITY;
				
			default:
				return KEYWORD_TYPE.TASKNAME;
		}
	}
	
	//**********************TO BE REVIEWED*********************************************************
	
	public static void possibleParser(String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		userInputSentence = extractPriority(userInputSentence, keywordTable);
	
		// Break up the commands into tokens by space
		String[] tokenisedElements = userInputSentence.split(" ");
		
		// Loop through the elements and get position of keywords
		Queue<KeytypeIndexPair> keywordsIndexQueue = getKeywordsInAscendingOrder(tokenisedElements);
		
		String currentData = "";
		KEYWORD_TYPE type = KEYWORD_TYPE.INVALID;
		int counter = 0;
		int nextKeywordIndex = -1;
		KeytypeIndexPair currentPair = null;
		KeytypeIndexPair nextPair = null;
		
		while (counter < tokenisedElements.length) {
			currentPair = nextPair;
			nextPair = keywordsIndexQueue.poll();
			
			if (nextPair == null) {
				nextKeywordIndex = tokenisedElements.length;
			} else {
				nextKeywordIndex = nextPair.getIndexPosition();
			}
			
			if (currentPair == null) {
				// Assume it is a taskname
				type = KEYWORD_TYPE.TASKNAME;
			} else {
				type = currentPair.getType();
				
				// Special condition to skip the keyword
				switch (type) {
					case PRIORITY:
						break;
						
					default:
						counter++;
						break;
				}
			}
			
			// Put in token until next keyword
			currentData = "";
			for (int i = counter; i < nextKeywordIndex; i++, counter++) {
				currentData += tokenisedElements[i] + " ";
			}
			currentData = currentData.trim();
			
			//5. put in table
			
			// Checks if current type is already taken
//			if (keywordTable.containsKey(type)) {
//				// This is based on assumption that the last keyword is always the correct one
//				String extractedData = keywordTable.get(type);
//				String taskname = keywordTable.get(KEYWORD_TYPE.TASKNAME);
//				keywordTable.put(KEYWORD_TYPE.TASKNAME, taskname+extractedData);
//			}
			
			keywordTable.put(type, currentData);
		}
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
	
	public static String extractTime(String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		String timeRegex1 = "\\d{3,4}";																// by 1700
		String timeRegex2 = "\\d{1,2}[:]?\\d{2}";													// by 17:00 or 6:00
		String timeRegex3 = "\\s+\\d{1,2}[:]?\\d{2}\\s*(am|pm)?(\\s|$)";							// by 6am or 1200pm
		String dateRegex1 = "\\s+\\d{1,2}[\\/\\.]?\\d{1,2}[\\/\\.]?\\d{2}(\\s|$)";					// 12/06/12 or 12.01.06 or 120106
		
		int startIndex = 0;
		int endIndex = 0;
		
		//ArrayList<Integer> matchList = searchForPatternMatch(userInputSentence, KEYWORD_ENDTIME+timeRegex3);
		ArrayList<Integer> matchList = searchForPatternMatch(userInputSentence, KEYWORD_DATE+dateRegex1);
		
		if (matchList.size() == 0) {
			return userInputSentence;
		}
		
//		// List might have multiple matches. So for now get the first match
//		String extractedTimeString = userInputSentence.substring(startIndex, endIndex).trim();
//		
//		// Remove the keyword and get only the time information
//		extractedTimeString = extractedTimeString.replace(KEYWORD_ENDTIME, "").trim();
//		keywordTable.put(KEYWORD_TYPE.END_TIME, extractedTimeString);
//		
//		userInputSentence = userInputSentence.substring(0, startIndex);
		
		return userInputSentence;
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
	    	
	    	System.out.println("Start: " + startIndex + " ,End: " + endIndex);
	    } 
	    
	    if (matchList.size() == 0) {
	    	System.out.println("No match found!");
	    }
	    
	    return matchList;
	}
	
	public static int extractPriority2(String userInputSentence) {
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
	    	return priorityLevel;
	    }
	    
	    // Extract the priority
	    String extractedPriorityString = userInputSentence.substring(startIndex, endIndex).trim();
	    userInputSentence = userInputSentence.substring(0, startIndex);
	    
	    priorityLevel = extractedPriorityString.length();
	    
	    return priorityLevel;
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
	
	
	private static String lookForStartTimeAndDate(String userInput) {
		
		String currentString = userInput;
		Matcher matcher = Pattern.compile("(?<=30/).*").matcher(currentString);
		if(currentString.contains(" at ")) {
			
			currentString.split("at");
		
			
		
		
		
		
		
		}
	
		
		
		return null;
	}
	
	
	
	
	
	
//	
//	//NOT USED
//	

//	
//	private static void updateTaskInfoBasedOnParameter(TaskInfo taskInfoToUpdate, String parameterString) {
//		// Decoy information
//		Random randomGenerator = new Random();
//		
//		String taskname = String.format("Task %d", counter);
//		int startDay = randomGenerator.nextInt(32);
//		int startMonth = randomGenerator.nextInt(12);
//		int startYear = 2014;
//		int startHour = randomGenerator.nextInt(24);
//		int startMinute = randomGenerator.nextInt(60);
//		Calendar startDate = new GregorianCalendar(startYear, startMonth, startDay, startHour, startMinute);
//		
//		int endDay = randomGenerator.nextInt(32);
//		int endMonth = randomGenerator.nextInt(12);
//		int endYear = 2014;
//		int endHour = randomGenerator.nextInt(24);
//		int endMinute = randomGenerator.nextInt(60);
//		Calendar endDate = new GregorianCalendar(endYear, endMonth, endDay, endHour, endMinute);
//		
//		int priority = randomGenerator.nextInt(4);
//		
//		taskInfoToUpdate.setTaskName(taskname);
//		taskInfoToUpdate.setStartDate(startDate);
//		taskInfoToUpdate.setEndDate(endDate);
//		taskInfoToUpdate.setImportanceLevel(priority);
//		
//		counter++;
//	}
//
//	private static Error updateTaskInfo(TaskInfo thisTaskInfo, TaskInfo oldTaskInfo, String userInputSentence){
//		String[] processedText = textProcess(userInputSentence);
//		String taskname = "";
//		//int startDate;
//		//int endDate;
//		int priority = 0;
//		
//		// Cut the command into their respective syntax. Will return hash table of data strings
//		Hashtable<KEYWORD_TYPE, String> keywordHashTable = new Hashtable<KEYWORD_TYPE, String>();
//		Error errorEncountered = createKeywordTableBasedOnParameter(userInputSentence, keywordHashTable);
//		if (errorEncountered != null) {
//			return errorEncountered;
//		}
//		
//		System.out.println(keywordHashTable);
//		
//		
//		// TODO
//		// One possible way of determining the command syntax.
//		// 1. Determine the command type. Each command has a different syntax and even same command
//		//    might have different formats. The command type can also be passed in by parameter
//		//    to avoid determining the command type again.
//		//    E.g: add hello world at 1130pm on 120112 by 1230am on 130112 ***
//		//         <command> <task name> at <start time> on <date> by <end time> on <date>
//		
//		// 2. Switch on all syntax checks for keywords for that particular command.
//		//    E.g: hello world at 1130pm on 120112 by 1230am on 130112 ***
//		//    <task name> at <start time> on <date> by <end time> on <date> <priority level>
//		
//		
//		// 3. Find the next closest syntax using the keywords such as 'at', 'by' or '*' for priority.
//		//    based on syntax checks that is on.
//		//    E.g: done <task name>
//		//    Checks for keyword 'at' or 'by' are switched off as they are not related to the command 'done'.  
//		// 3a. Cut the command until the next closest syntax.
//		//    E.g: 1. hello world 
//		//         2. at <start time> on <date> by <end time> on <date> <priority level>
//		// 3b. The first string will be the task name. Update the information to task info.
//		
//		
//		// 4. Find the next closest syntax again using the keywords such as 'at', 'by' or '*' for priority.
//		// 4a. If there is no such keywords found, end the parsing. If not cut the command again using the
//		//     remaining string.
//		//     E.g: 1. at <start time> on <date>
//		//          2. by <end time> on <date> <priority level>
//		// 4b. Determine the syntax by the keyword. If it contains 'at' it is a start time, 
//		//     if it has 'by' it is an end time and so on.
//		
//		
//		// 5. Repeating steps 3 and 4 until there is no string left or no keyword is found.
//		
//		
//		// 6. In steps of updating to task info, validate each information if it matches the formats.
//		//    E.g: by 1230am on 130112***
//		//         It is not a valid format as the asterisks are combined. Might be due to user
//		//         typo.
//		
//		
//		
//		
//		//**********************THIS WHOLE CHUNK IS FOR MODIFY************************
//		//Bug in textparsing: there is a space after the task name after u convert it into a string <eg. ( _ denote space) taskname_ or task_name_ (note the space behind) >
//		//for command modify only
//		boolean toModify = false;
//		//Sample: modify this> going into space to see the stars  to> going to simei to eat cai peng
//		
//		//Checks for the 2 keywords for modify
//		if (processedText[0].equals("this>")) {
//			for(int i = 0; i < processedText.length; i++) {
//				if (processedText[i].equals("to>")) {
//					toModify = true;
//				}
//			}
//		}
//		
//		//modify
//		if (toModify) {
//			String oldTaskName = "";
//			//1. Get task name to be modified
//			for(int i = 0; i < processedText.length; i++) {
//				if (!processedText[i].equals("this>")) {
//					if (processedText[i].equals("to>")) {
//						break;
//					}
//					else {
//						/*
//						if(!oldTaskName.isEmpty()) {
//							oldTaskName += " ";
//						}
//						*/
//						oldTaskName += processedText[i] + " ";
//					}
//				}
//			}
//			//2. Set task name into oldTaskInfo
//			if (!oldTaskName.isEmpty()) {
//				oldTaskInfo.setTaskName(oldTaskName);
//			}
//			
//			//3. Get new task name
//			String newTaskName = "";
//			for(int i = 0; i < processedText.length; i++) {
//				if (processedText[i].equals("to>")) {
//					for(int j = i+1; j < processedText.length; j++) {
//						/*
//						if (!newTaskName.isEmpty()) {
//							newTaskName += " ";
//						}
//						*/
//						newTaskName += processedText[j] + " ";
//					}
//					break;
//				}
//			}
//			
//			//4. Set task name into task info
//			if (!newTaskName.isEmpty()) {
//				thisTaskInfo.setTaskName(newTaskName);
//			}
//
//		}
//		
//		//**********************THIS WHOLE CHUNK IS FOR MODIFY************************
//		
//		
//		//add delete search
//		else {
//			
//			taskname = functionFindTaskname(processedText);
//			thisTaskInfo.setTaskName(taskname);
//		thisTaskInfo.setTaskType(TASK_TYPE.FLOATING);	// HARDCODED TO DEFAULT
//			setTypeAndDate(thisTaskInfo, processedText);
//		
//		
//			//thisTaskInfo.setStartDate(startDate);
//			//thisTaskInfo.setEndDate(endDate);
//			int processedTextLength = processedText.length;
//			priority = findPriority(processedText[processedTextLength-1]);
//			thisTaskInfo.setImportanceLevel(priority);
//		
//		}
//		return null;
//	}
//	
//	private static int findPriority(String priorityString){
//		
//		if(priorityString.contains("*")){
//			char[] countPriorityStar = priorityString.toCharArray();
//			return countPriorityStar.length;
//		}
//		return 0;
//	}
//
//
//

//
//
//
//	private static void setTypeAndDate(TaskInfo thisTaskInfo, String[] processedText){
//		boolean deadlineType = false;
//		boolean timedType = false;
//		
//		Calendar startDate = Calendar.getInstance();
//		Calendar endDate = Calendar.getInstance();
//		for(int i=0; i<processedText.length; i++){
//			//check that this is the first encounter of the keyword "at"
//			if(processedText[i].equals("at") && (timedType == false)){
//				timedType = true;
//				String allegedTime = processedText[i+1];
//				TimeFormat currTimeFormat = new TimeFormat();
//				if(verifyTimeValidity(allegedTime, currTimeFormat)){
//					timeTranslator(startDate, Integer.parseInt(allegedTime), currTimeFormat);
//				}
//				else{
//					thisTaskInfo = null;
//					return;
//				}
//			}
//			//check that this is the first encounter of the keyword "by"
//			else if(processedText[i].equals("by") && (deadlineType == false)){
//				deadlineType = true;
//				String allegedTime = processedText[i+1];
//				TimeFormat currTimeFormat = new TimeFormat();
//				if(verifyTimeValidity(allegedTime, currTimeFormat)){
//					timeTranslator(endDate, Integer.parseInt(allegedTime), currTimeFormat);
//				}
//				else{
//					thisTaskInfo = null;
//					return;
//				}
//			}
//			
//			//if this is not the first encounter of the keyword, it means, the user keyed in a wrong input format
//			
//		}
//		
//		//Calendar currStartDate = new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH), startDate.get(Calendar.HOUR_OF_DAY), startDate.get(Calendar.MINUTE));
//		//Calendar currEndDate = new GregorianCalendar(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH), endDate.get(Calendar.HOUR_OF_DAY), endDate.get(Calendar.MINUTE));
//		detectAndSetTaskDate(processedText, startDate, endDate);
//		thisTaskInfo.setStartDate(startDate);
//		thisTaskInfo.setEndDate(endDate);
//		setTaskType(thisTaskInfo, deadlineType, timedType);
//		
//	}
//	
//	private static void detectAndSetTaskDate(String[] processedText, Calendar startDate, Calendar endDate){
//		int onWordCounter = 0; //this variable is to ensure that there are maximum of 2 "on" keyword for startDate and endDate
//		
//		for(int i=0; i<processedText.length; i++){
//			if(processedText[i].equals("on")){
//				onWordCounter++;
//				if(onWordCounter == START_DATE_COUNT){
//					dateTranslator(startDate, processedText[i+1]);
//				}
//				else if(onWordCounter == END_DATE_COUNT){
//					dateTranslator(endDate, processedText[i+1]);
//				}
//				else
//				{
//					
//					return;
//				}
//			}
//		}
//	}
//	
//	
//	private static void dateTranslator(Calendar thisDate, String theDate){
//		//this method should already take in the proper date format. verification should be separated in another method
//		int date;
//		int month;
//		int year;
//		String[] dateArray = new String[3];
//		
//		dateArray = theDate.split("/");
//		
//		if(dateArray[2] != null){
//			year = Integer.parseInt(dateArray[2]);
//			thisDate.set(Calendar.YEAR, year);
//		}
//		if(dateArray[1] != null){
//			month = Integer.parseInt(dateArray[1]);
//			thisDate.set(Calendar.MONTH, month-1);
//		}
//		if(dateArray[0] != null){
//			date = Integer.parseInt(dateArray[0]);
//			thisDate.set(Calendar.DAY_OF_MONTH, date);
//		}
//		return;
//	}
//	
//	private static void timeTranslator(Calendar theTime, int correctTime, TimeFormat currTimeFormat){
//		//this method translates ALL time formats
//		if(currTimeFormat.getTimeFormatCode() == THE_24_HOUR_FORMAT_CODE){
//			int hour = correctTime / CORRECT_24HOUR_FORMAT_MIN;
//			int minute = correctTime % CORRECT_24HOUR_FORMAT_MIN;
//			theTime.set(Calendar.HOUR_OF_DAY, hour);
//			theTime.set(Calendar.MINUTE, minute);
//		}
//		else{
//			return;
//		}
//		return;
//		
//	}
//
//	private static boolean verifyTimeValidity(String allegedTime, TimeFormat currTimeFormat) {
//		
//		try{
//			//check if it's the 24 hour format without separation. Eg: 1700, 1000
//			int correctTimeFormat = Integer.parseInt(allegedTime);
//			//check if the time is in logical number
//			//!!!!!!!!!revise this again. Logic error
//			if((correctTimeFormat >= CORRECT_24HOUR_FORMAT_MIN) && (correctTimeFormat <= CORRECT_24HOUR_FORMAT_MAX) ){
//				currTimeFormat.setTimeFormatCode(THE_24_HOUR_FORMAT_CODE);
//				return true;
//			}
//			else {
//				return false;
//			}
//			
//		}
//		catch(IllegalArgumentException exception){
//			//this means either invalid format or the other different formats
//			//5am, 5pm, 17:00, 5:00am, etc
//			return false;
//		}
//		
//		//this is a stub
//		//return false;
//	}
//
//	private static void setTaskType(TaskInfo thisTaskInfo,
//			boolean deadlineType, boolean timedType) {
//		if (timedType){
//			thisTaskInfo.setTaskType(TASK_TYPE.TIMED);
//		}
//		else if(deadlineType){
//			thisTaskInfo.setTaskType(TASK_TYPE.DEADLINE);
//		}
//		else{
//			thisTaskInfo.setTaskType(TASK_TYPE.FLOATING);
//		}
//	}
//	
//	private static String functionFindTaskname(String[] processedText){
//		String actualTaskName = "";
//		int loopLength = processedText.length;
//		if(processedText[loopLength-1].contains("*")){
//			loopLength--;
//		}
//		for(int i=0; i<loopLength; i++){
//			if((!processedText[i].equals("by")) && (!processedText[i].equals("at")) && (!processedText[i].equals("on"))){
//				actualTaskName += processedText[i] + " ";	
//			}
//			else{
//				break;
//			}
//		}
//		return actualTaskName;
//	}
	


}

