import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.Vector;


/*
** This is main class that will run Task Master KABOOM
** 
** 
**/
enum COMMAND_TYPE {
		ADD, DELETE, MODIFY, SEARCH, INVALID;
}

public class TaskMasterKaboom {
	
	private static final String KEYWORD_COMMAND_ADD = "add";
	private static final String KEYWORD_COMMAND_DELETE = "delete";
	private static final String KEYWORD_COMMAND_MODIFY = "modify";
	private static final String KEYWORD_COMMAND_SEARCH = "search";
	private static final int CORRECT_24HOUR_FORMAT_MIN = 100;
	private static final int CORRECT_24HOUR_FORMAT_MAX = 2359;
	
	private static KaboomGUI taskUi;
	private static History historyofCommands = new History();
	
	// Temporary static
	private static int counter = 1;				// Use to create temporary task
	
	public static void main(String[] args) {
		// Setup application
			// Setup UI
			setupUi();
			// Setup Memory
			//addTemporaryTaskForTesting();
			// Setup Storage
			// Setup Logic
		
		// Run the UI
		activateUi();
		
		// Start processing user commands
		
//		// Get command from UI
//		String command = "add";
//		
//		// Process command line
//		String commandFeedback = processCommand(command);
//	
//		// Return feedback to
//		System.out.println("Feedback: " + commandFeedback);
		
		//while (true) {
			//
		//}
	}
	
	private static boolean setupUi () {
		try {
			taskUi = new KaboomGUI();
			taskUi.initialize();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static void activateUi () {
		taskUi.runUi();
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
	public static String processCommand(String userInputSentence) {		
		Command commandToExecute = null;
		String feedback = "";
		COMMAND_TYPE commandType = determineCommandType(userInputSentence);
		String commandParametersString = removeFirstWord(userInputSentence);
		
		commandToExecute = createCommandBasedOnCommandType(commandType, commandParametersString);
		feedback = commandToExecute.execute();
		
		// Later to be move to somewhere else
		updateUi(feedback);
		
		// Add recent command to History list
		addToCommandHistory(new Command());
		
		// Save data to file
		
		return feedback;
	}

	private static String removeFirstWord(String userInputSentence) {
		String wordRemoved = userInputSentence.replace(getFirstWord(userInputSentence), "").trim();
		return wordRemoved;
	}

	private static void updateUi(String feedback) {
		Vector<TaskInfo> taskToDisplay = TaskListShop.getInstance().getAllTaskInList();
		taskUi.updateUiDisplay(feedback, taskToDisplay);
	}
	
	private static void addToCommandHistory(Command command) {
		if (command.getCommandType() != COMMAND_TYPE.INVALID) {
			historyofCommands.addToRecentCommands(command);
		}
	}
	
	private static Command createCommandBasedOnCommandType (COMMAND_TYPE commandType, String parameters) {
		Command newlyCreatedCommand = new Command();
		TaskInfo taskInformation = null;
		
		switch (commandType) {
			case ADD:
				newlyCreatedCommand = new CommandAdd();
				taskInformation = createTaskInfoBasedOnCommand(parameters);
				newlyCreatedCommand.setTaskInfo(taskInformation);
				break;
				
			case DELETE:
				newlyCreatedCommand = new CommandDelete();
				taskInformation = createTaskInfoBasedOnCommand(parameters);
				newlyCreatedCommand.setTaskInfo(taskInformation);
				break;
				
			case MODIFY:
				newlyCreatedCommand = new CommandModify();
				taskInformation = createTaskInfoBasedOnCommand(parameters);
				newlyCreatedCommand.setTaskInfo(taskInformation);
				break;
				
			case SEARCH:
				newlyCreatedCommand = new CommandSearch();
				taskInformation = createTaskInfoBasedOnCommand(parameters);
				newlyCreatedCommand.setTaskInfo(taskInformation);
				break;
				
			default:
				newlyCreatedCommand = new Command();
				break;
				
		}
		
		return newlyCreatedCommand;
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
			default:
				return COMMAND_TYPE.INVALID;
		}
	}
	
	private static TaskInfo createTaskInfoBasedOnCommand(String userInputSentence) {
		//TODO create and process text information to task info here
		// Currently it is randomly generated.
		TaskInfo newlyCreatedTaskInfo = new TaskInfo();
		updateTaskInfoBasedOnParameter(newlyCreatedTaskInfo, userInputSentence);
		updateTaskInfo(newlyCreatedTaskInfo, userInputSentence);
		return newlyCreatedTaskInfo;
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

	private static void updateTaskInfo(TaskInfo thisTaskInfo, String userInputSentence){
		String[] processedText = textProcess(userInputSentence);
		String taskname = "";
		int startDate;
		int endDate;
		int priority = 0;
		
		// TODO
		// One possible way of determining the command syntax.
		// 1. Determine the command type. Each command has a different syntax and even same command
		//    might have different formats. The command type can also be passed in by parameter
		//    to avoid determining the command type again.
		//    E.g: add hello world at 1130pm 120112 by 1230am 130112 ***
		//         <command> <task name> at <start time and date> by <end time and date>
		
		// 2. Switch on all syntax checks for keywords for that particular command.
		//    E.g: hello world at 1130pm 120112 by 1230am 130112 ***
		//    <task name> at <start time and date> by <end time and date> <priority level>
		
		
		// 3. Find the next closest syntax using the keywords such as 'at', 'by' or '*' for priority.
		//    based on syntax checks that is on.
		//    E.g: done <task name>
		//    Checks for keyword 'at' or 'by' are switched off as they are not related to the command 'done'.  
		// 3a. Cut the command until the next closest syntax.
		//    E.g: 1. hello world 
		//         2. at <start time and date> by <end time and date> <priority level>
		// 3b. The first string will be the task name. Update the information to task info.
		
		
		// 4. Find the next closest syntax again using the keywords such as 'at', 'by' or '*' for priority.
		// 4a. If there is no such keywords found, end the parsing. If not cut the command again using the
		//     remaining string.
		//     E.g: 1. at <start time and date>
		//          2. by <end time and date> <priority level>
		// 4b. Determine the syntax by the keyword. If it contains 'at' it is a start time, 
		//     if it has 'by' it is an end time and so on.
		
		
		// 5. Repeating steps 3 and 4 until there is no string left or no keyword is found.
		
		
		// 6. In steps of updating to task info, validate each information if it matches the formats.
		//    E.g: by 1230am 130112***
		//         It is not a valid format as the asterisks are combined. Might be due to user
		//         typo.
		
		taskname = functionFindTaskname(processedText);
		setTypeAndDate(thisTaskInfo, processedText);
		
		
		
		//thisTaskInfo.setStartDate(startDate);
		//thisTaskInfo.setEndDate(endDate);
		thisTaskInfo.setTaskName(taskname);
		thisTaskInfo.setImportanceLevel(priority);
		
	}
	
	private static void setTypeAndDate(TaskInfo thisTaskInfo, String[] processedText){
		boolean deadlineType = false;
		boolean timedType = false;
		boolean the24HourFormat = false;
		
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		for(int i=1; i<processedText.length; i++){
			if(processedText[i].equals("at")){
				timedType = true;
				String allegedTime = processedText[i+1];
				if(verifyTimeValidity(allegedTime)){
					timeTranslator(startDate, Integer.parseInt(allegedTime));
				}
				else{
					thisTaskInfo = null;
					return;
				}
			}
			else if(processedText[i].equals("by")){
				deadlineType = true;
				String allegedTime = processedText[i+1];
				if(verifyTimeValidity(allegedTime)){
					timeTranslator(endDate, Integer.parseInt(allegedTime));
				}
				else{
					thisTaskInfo = null;
					return;
				}
			}
		}
		
		setTaskType(thisTaskInfo, deadlineType, timedType);
		
	}
	
	private static void timeTranslator(Calendar theTime, int correctTime){
		/*
		if(correctTime >= CORRECT_24HOUR_FORMAT_MIN){
			int hour = correctTime / CORRECT_24HOUR_FORMAT_MIN;
			int minute = correctTime % CORRECT_24HOUR_FORMAT_MIN;
			theTime.set(Calendar.HOUR_OF_DAY, hour);
			theTime.set(Calendar.MINUTE, minute);
		}
		else{
			return;
		}*/
		//this method translates all time formats
		//theTime.set(Calendar.HOUR_OF_DAY, theCorrectTime);
		//theTime.set(Calendar.MINUTE, theCorrectTime);
	}

	private static boolean verifyTimeValidity(String allegedTime) {
		/*
		try{
			//refers to 24 hour format without separation. Eg: 1700, 1000
			int correctTimeFormat = Integer.parseInt(allegedTime);
			if((correctTimeFormat >= CORRECT_24HOUR_FORMAT_MIN) && (correctTimeFormat <= CORRECT_24HOUR_FORMAT_MAX) ){
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
		*/
		//this is a stub
		return false;
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
	
	private static String functionFindTaskname(String[] processedText){
		String actualTaskName = "";
		for(int i=1; i<processedText.length; i++){
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
