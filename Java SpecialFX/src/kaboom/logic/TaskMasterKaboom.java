package kaboom.logic;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Pattern;

import kaboom.logic.Error.ERROR_TYPE;
import kaboom.logic.command.COMMAND_TYPE;
import kaboom.logic.command.Command;
import kaboom.logic.command.CommandAdd;
import kaboom.logic.command.CommandClear;
import kaboom.logic.command.CommandDelete;
import kaboom.logic.command.CommandModify;
import kaboom.logic.command.CommandSearch;
import kaboom.storage.History;
import kaboom.storage.Storage;
import kaboom.storage.TaskListShop;
import kaboom.ui.GraphicInterface;


/*
** This is main class that will run Task Master KABOOM
** 
** 
**/

public class TaskMasterKaboom {
	
	private static final String KEYWORD_COMMAND_ADD = "add";
	private static final String KEYWORD_COMMAND_DELETE = "delete";
	private static final String KEYWORD_COMMAND_MODIFY = "modify";
	private static final String KEYWORD_COMMAND_SEARCH = "search";
	private static final String KEYWORD_COMMAND_CLEAR = "clear";
	
	private static final String MESSAGE_WELCOME = "Welcome back, Commander";
	
	private static final String KEYWORD_STARTTIME = " at ";
	private static final String KEYWORD_ENDTIME = " by ";
	private static final String KEYWORD_DATE = " on ";
	private static final String KEYWORD_PRIORITY = " *";
	
	private static final int CORRECT_24HOUR_FORMAT_MIN = 100;
	private static final int CORRECT_24HOUR_FORMAT_MAX = 2359;
	
	private static final int THE_24_HOUR_FORMAT_CODE = 1;
	//private static final int THE_24_HOUR_FORMAT_WITH_COLON_CODE = 2;
	//private static final int THE_AM_FORMAT_CODE = 3;
	//private static final int THE_AM_FORMAT_WITH_COLON_CODE = 4;
	//private static final int THE_PM_FORMAT_CODE = 5;
	//private static final int THE_PM_FORMAT_WITH_COLON_CODE = 6;
	private static final int START_DATE_COUNT = 1;
	private static final int END_DATE_COUNT = 2;
	
	private static final String FILENAME = "KABOOM_FILE.dat";
	
	private static DisplayData guiDisplayData;
	private static Storage fileStorage;
	private static History historyofCommands = new History();
	
	// Temporary static
	private static int counter = 1;				// Use to create temporary task
	
	static TaskMasterKaboom instance;
	
	public static TaskMasterKaboom getInstance () {
		if (instance == null) {
			instance = new TaskMasterKaboom();
		}
		return instance;
	}
	
	private TaskMasterKaboom () {
	}
	
	public void initialiseKaboom() {
		// Setup application
			// Setup Memory
			//addTemporaryTaskForTesting();
		
			// Setup Storage
			initialiseStorage();
		
			// Setup UI
			guiDisplayData = DisplayData.getInstance();
			updateUiWithFirstLoadedMemory();
			//setupUi();
			
			
			// Setup Logic
		
		// Run the UI
		//activateUi();
		
		// Start processing user commands
	}

	private static void updateUiWithFirstLoadedMemory() {
		Result introResult = new Result();
		introResult.setTasksToDisplay(TaskListShop.getInstance().getAllTaskInList());
		introResult.setFeedback(MESSAGE_WELCOME);
		updateUi(introResult);
	}
	
	private static boolean initialiseStorage () {
		fileStorage = new Storage(FILENAME);
		fileStorage.load();
		
		return true;
	}
	
	/*
	 * Purpose: ProcessCommand will read the userCommand and break down into
	 * respective information for the task information. Currently, it returns
	 * the feedback for command that is executed.
	 * 
	 * Note: Public access allow execution from test driven development
	 * to run straight into command.
	 * 
	 * Future improvement: Return task class instead.
	 */
	public static boolean processCommand(String userInputSentence) {
		assert userInputSentence != null;
		
		Command commandToExecute = null;
		Result commandResult = null;
		COMMAND_TYPE commandType = determineCommandType(userInputSentence);
		String commandParametersString = removeFirstWord(userInputSentence);
		
		commandToExecute = createCommandBasedOnCommandType(commandType);
		Error errorType = updateCommandInfoFromParameter(commandToExecute, commandParametersString);
		
		if (errorType == null) {
			try {
				commandResult = commandToExecute.execute();
			} catch (Exception e) {
				commandResult = new Result();
				commandResult.setFeedback("Error executing command! Please inform your administrator!");
			}
		} else {
			commandResult = new Result();
			commandResult.setFeedback(errorType.getErrorMessage());
		}
		
		updateUi(commandResult);
		
		// Add recent command to History list
		addToCommandHistory(new Command());
		
		// Save data to file
		fileStorage.store();
		
		return true;
	}
	
	private static String removeFirstWord(String userInputSentence) {
		String wordRemoved = userInputSentence.replace(getFirstWord(userInputSentence), "").trim();
		return wordRemoved;
	}

	private static void updateUi(Result commandResult) {
		guiDisplayData.updateDisplayWithResult(commandResult);
	}
	
	private static void addToCommandHistory(Command command) {
		if (command.getCommandType() != COMMAND_TYPE.INVALID) {
			historyofCommands.addToRecentCommands(command);
		}
	}
	
	private static Command createCommandBasedOnCommandType (COMMAND_TYPE commandType) {
		Command newlyCreatedCommand = new Command();
		
		switch (commandType) {
			case ADD:
				newlyCreatedCommand = new CommandAdd();
				break;
				
			case DELETE:
				newlyCreatedCommand = new CommandDelete();
				break;
				
			case MODIFY:
				newlyCreatedCommand = new CommandModify();
				break;
				
			case SEARCH:
				newlyCreatedCommand = new CommandSearch();
				break;
				
			case CLEAR:
				newlyCreatedCommand = new CommandClear();
				break;
				
			default:
				newlyCreatedCommand = new Command();
				break;
				
		}
		
		return newlyCreatedCommand;
	}

	private static Error updateCommandInfoFromParameter(Command commandToUpdate, String parameters) {
		TaskInfo taskInformation = new TaskInfo();
		//to store existing taskinfo to be modified 
		TaskInfo taskInformationToBeModified = new TaskInfo();
		
		Error errorType = createTaskInfoBasedOnCommand(taskInformation, taskInformationToBeModified, parameters);
		commandToUpdate.setTaskInfo(taskInformation);
		
		//modify
		if (!taskInformationToBeModified.isEmpty()) {
			commandToUpdate.setTaskInfoToBeModified(taskInformationToBeModified);
		}

		return errorType;
	}
	
	private static COMMAND_TYPE determineCommandType(String userCommand) {
		String commandTypeString = getFirstWord(userCommand);
		
		// Determine what command to execute
		switch(commandTypeString) {
			case KEYWORD_COMMAND_ADD:
				return COMMAND_TYPE.ADD;
			case KEYWORD_COMMAND_DELETE:
				return COMMAND_TYPE.DELETE;
			case KEYWORD_COMMAND_MODIFY:
				return COMMAND_TYPE.MODIFY;
			case KEYWORD_COMMAND_SEARCH:
				return COMMAND_TYPE.SEARCH;
			case KEYWORD_COMMAND_CLEAR:
				return COMMAND_TYPE.CLEAR;
			default:
				return COMMAND_TYPE.INVALID;
		}
	}
	
	private static Error createTaskInfoBasedOnCommand(TaskInfo newTaskInfo, TaskInfo oldTaskInfo, String userInputSentence) {
		// Currently it is randomly generated.
		//updateTaskInfoBasedOnParameter(newlyCreatedTaskInfo, userInputSentence);
		
		Error errorEncountered = updateTaskInfo(newTaskInfo, oldTaskInfo, userInputSentence);
		
		return errorEncountered;
	}
	
	private static void updateTaskInfoBasedOnParameter(TaskInfo taskInfoToUpdate, String parameterString) {
		// Decoy information
		Random randomGenerator = new Random();
		
		String taskname = String.format("Task %d", counter);
		int startDay = randomGenerator.nextInt(32);
		int startMonth = randomGenerator.nextInt(12);
		int startYear = 2014;
		int startHour = randomGenerator.nextInt(24);
		int startMinute = randomGenerator.nextInt(60);
		Calendar startDate = new GregorianCalendar(startYear, startMonth, startDay, startHour, startMinute);
		
		int endDay = randomGenerator.nextInt(32);
		int endMonth = randomGenerator.nextInt(12);
		int endYear = 2014;
		int endHour = randomGenerator.nextInt(24);
		int endMinute = randomGenerator.nextInt(60);
		Calendar endDate = new GregorianCalendar(endYear, endMonth, endDay, endHour, endMinute);
		
		int priority = randomGenerator.nextInt(4);
		
		taskInfoToUpdate.setTaskName(taskname);
		taskInfoToUpdate.setStartDate(startDate);
		taskInfoToUpdate.setEndDate(endDate);
		taskInfoToUpdate.setImportanceLevel(priority);
		
		counter++;
	}

	private static Error updateTaskInfo(TaskInfo thisTaskInfo, TaskInfo oldTaskInfo, String userInputSentence){
		String[] processedText = textProcess(userInputSentence);
		String taskname = "";
		//int startDate;
		//int endDate;
		int priority = 0;
		
		// Cut the command into their respective syntax. Will return hash table of data strings
		Hashtable<KEYWORD_TYPE, String> keywordHashTable = new Hashtable<KEYWORD_TYPE, String>();
		Error errorEncountered = createKeywordTableBasedOnParameter(userInputSentence, keywordHashTable);
		if (errorEncountered != null) {
			return errorEncountered;
		}
		
		System.out.println(keywordHashTable);
		
		
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
		
		
		
		
		//**********************THIS WHOLE CHUNK IS FOR MODIFY************************
		//Bug in textparsing: there is a space after the task name after u convert it into a string <eg. ( _ denote space) taskname_ or task_name_ (note the space behind) >
		//for command modify only
		boolean toModify = false;
		//Sample: modify this> going into space to see the stars  to> going to simei to eat cai peng
		
		//Checks for the 2 keywords for modify
		if (processedText[0].equals("this>")) {
			for(int i = 0; i < processedText.length; i++) {
				if (processedText[i].equals("to>")) {
					toModify = true;
				}
			}
		}
		
		//modify
		if (toModify) {
			String oldTaskName = "";
			//1. Get task name to be modified
			for(int i = 0; i < processedText.length; i++) {
				if (!processedText[i].equals("this>")) {
					if (processedText[i].equals("to>")) {
						break;
					}
					else {
						/*
						if(!oldTaskName.isEmpty()) {
							oldTaskName += " ";
						}
						*/
						oldTaskName += processedText[i] + " ";
					}
				}
			}
			//2. Set task name into oldTaskInfo
			if (!oldTaskName.isEmpty()) {
				oldTaskInfo.setTaskName(oldTaskName);
			}
			
			//3. Get new task name
			String newTaskName = "";
			for(int i = 0; i < processedText.length; i++) {
				if (processedText[i].equals("to>")) {
					for(int j = i+1; j < processedText.length; j++) {
						/*
						if (!newTaskName.isEmpty()) {
							newTaskName += " ";
						}
						*/
						newTaskName += processedText[j] + " ";
					}
					break;
				}
			}
			
			//4. Set task name into task info
			if (!newTaskName.isEmpty()) {
				thisTaskInfo.setTaskName(newTaskName);
			}

		}
		
		//**********************THIS WHOLE CHUNK IS FOR MODIFY************************
		
		
		//add delete search
		else {
			try{
				taskname = functionFindTaskname(processedText);
			}
			catch (Exception e){
				Error errorType = new Error();
				errorType.setErrorType(ERROR_TYPE.MESSAGE_DUPLICTE_COMMAND_PARAMETERS);
				return errorType;
			}
			thisTaskInfo.setTaskName(taskname);
			thisTaskInfo.setTaskType(TASK_TYPE.FLOATING);	// HARDCODED TO DEFAULT
			setTypeAndDate(thisTaskInfo, processedText);
		
		
			//thisTaskInfo.setStartDate(startDate);
			//thisTaskInfo.setEndDate(endDate);
			int processedTextLength = processedText.length;
			priority = findPriority(processedText[processedTextLength-1]);
			thisTaskInfo.setImportanceLevel(priority);
		
		}
		return null;
	}
	
	private static int findPriority(String priorityString){
		
		if(priorityString.contains("*")){
			char[] countPriorityStar = priorityString.toCharArray();
			return countPriorityStar.length;
		}
		return 0;
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
				
				// add to table
				keywordTable.put(type, cutOutString);
				
				// Remove the string from the original string
				final String re = Pattern.quote(cutOutString);
				currentString = currentString.replaceFirst(re, "");
				
				prevType = type;
			}
		}
		
		return null;
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
	
	public static Vector<Integer> getListOfKeywordPositions (String stringToSearch) {
		Vector<Integer> keywordPositions = new Vector<Integer>();
		
		String currentKeyword = "";
		int numOfKeywords = 4;
		
		for (int i = 0; i < numOfKeywords; i++) {
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
			
			int currentIndex = stringToSearch.indexOf(currentKeyword);
			if (currentIndex > 0) {
				keywordPositions.add(currentIndex);
			}
		}
		
		return keywordPositions;
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

	private static void setTypeAndDate(TaskInfo thisTaskInfo, String[] processedText){
		boolean deadlineType = false;
		boolean timedType = false;
		
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		for(int i=0; i<processedText.length; i++){
			//check that this is the first encounter of the keyword "at"
			if(processedText[i].equals("at") && (timedType == false)){
				timedType = true;
				String allegedTime = processedText[i+1];
				TimeFormat currTimeFormat = new TimeFormat();
				if(verifyTimeValidity(allegedTime, currTimeFormat)){
					timeTranslator(startDate, Integer.parseInt(allegedTime), currTimeFormat);
				}
				else{
					thisTaskInfo = null;
					return;
				}
			}
			//check that this is the first encounter of the keyword "by"
			else if(processedText[i].equals("by") && (deadlineType == false)){
				deadlineType = true;
				String allegedTime = processedText[i+1];
				TimeFormat currTimeFormat = new TimeFormat();
				if(verifyTimeValidity(allegedTime, currTimeFormat)){
					timeTranslator(endDate, Integer.parseInt(allegedTime), currTimeFormat);
				}
				else{
					thisTaskInfo = null;
					return;
				}
			}
			
			//if this is not the first encounter of the keyword, it means, the user keyed in a wrong input format
			
		}
		
		//Calendar currStartDate = new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH), startDate.get(Calendar.HOUR_OF_DAY), startDate.get(Calendar.MINUTE));
		//Calendar currEndDate = new GregorianCalendar(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH), endDate.get(Calendar.HOUR_OF_DAY), endDate.get(Calendar.MINUTE));
		detectAndSetTaskDate(processedText, startDate, endDate);
		thisTaskInfo.setStartDate(startDate);
		thisTaskInfo.setEndDate(endDate);
		setTaskType(thisTaskInfo, deadlineType, timedType);
		
	}
	
	private static void detectAndSetTaskDate(String[] processedText, Calendar startDate, Calendar endDate){
		int onWordCounter = 0; //this variable is to ensure that there are maximum of 2 "on" keyword for startDate and endDate
		
		for(int i=0; i<processedText.length; i++){
			if(processedText[i].equals("on")){
				onWordCounter++;
				if(onWordCounter == START_DATE_COUNT){
					dateTranslator(startDate, processedText[i+1]);
				}
				else if(onWordCounter == END_DATE_COUNT){
					dateTranslator(endDate, processedText[i+1]);
				}
				else
				{
					
					return;
				}
			}
		}
	}
	
	
	private static void dateTranslator(Calendar thisDate, String theDate){
		//this method should already take in the proper date format. verification should be separated in another method
		int date;
		int month;
		int year;
		String[] dateArray = new String[3];
		
		dateArray = theDate.split("/");
		
		if(dateArray[2] != null){
			year = Integer.parseInt(dateArray[2]);
			thisDate.set(Calendar.YEAR, year);
		}
		if(dateArray[1] != null){
			month = Integer.parseInt(dateArray[1]);
			thisDate.set(Calendar.MONTH, month-1);
		}
		if(dateArray[0] != null){
			date = Integer.parseInt(dateArray[0]);
			thisDate.set(Calendar.DAY_OF_MONTH, date);
		}
		return;
	}
	
	private static void timeTranslator(Calendar theTime, int correctTime, TimeFormat currTimeFormat){
		//this method translates ALL time formats
		if(currTimeFormat.getTimeFormatCode() == THE_24_HOUR_FORMAT_CODE){
			int hour = correctTime / CORRECT_24HOUR_FORMAT_MIN;
			int minute = correctTime % CORRECT_24HOUR_FORMAT_MIN;
			theTime.set(Calendar.HOUR_OF_DAY, hour);
			theTime.set(Calendar.MINUTE, minute);
		}
		else{
			return;
		}
		return;
		
	}

	private static boolean verifyTimeValidity(String allegedTime, TimeFormat currTimeFormat) {
		
		try{
			//check if it's the 24 hour format without separation. Eg: 1700, 1000
			int correctTimeFormat = Integer.parseInt(allegedTime);
			//check if the time is in logical number
			//!!!!!!!!!revise this again. Logic error
			if((correctTimeFormat >= CORRECT_24HOUR_FORMAT_MIN) && (correctTimeFormat <= CORRECT_24HOUR_FORMAT_MAX) ){
				currTimeFormat.setTimeFormatCode(THE_24_HOUR_FORMAT_CODE);
				return true;
			}
			else {
				return false;
			}
			
		}
		catch(IllegalArgumentException exception){
			//this means either invalid format or the other different formats
			//5am, 5pm, 17:00, 5:00am, etc
			return false;
		}
		
		//this is a stub
		//return false;
	}

	private static void setTaskType(TaskInfo thisTaskInfo,
			boolean deadlineType, boolean timedType) {
		if (timedType){
			thisTaskInfo.setTaskType(TASK_TYPE.TIMED);
		}
		else if(deadlineType){
			thisTaskInfo.setTaskType(TASK_TYPE.DEADLINE);
		}
		else{
			thisTaskInfo.setTaskType(TASK_TYPE.FLOATING);
		}
	}
	
	private static String functionFindTaskname(String[] processedText) throws Exception{
		if(processedText[0] == null){
			throw new Exception();
		}
		
		String actualTaskName = "";
		int loopLength = processedText.length;
		if(processedText[loopLength-1].contains("*")){
			loopLength--;
		}
		for(int i=0; i<loopLength; i++){
			if((!processedText[i].equals("by")) && (!processedText[i].equals("at")) && (!processedText[i].equals("on"))){
				actualTaskName += processedText[i] + " ";	
			}
			else{
				break;
			}
		}
		return actualTaskName;
	}
	
	private static String[] textProcess(String userInputSentence){
		String[] commandAndData = userInputSentence.trim().split("\\s+");
		return commandAndData;
	}
	
	private static String getFirstWord(String userInputSentence) {
		String[] elements = textProcess(userInputSentence);
		String firstWord = elements[0].toLowerCase();
		return firstWord;
	}
}
