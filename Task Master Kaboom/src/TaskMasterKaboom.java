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
	
	private static void addTemporaryTaskForTesting () {
		TaskInfo newTask = new TaskInfo();
		newTask.setTaskName("Task 1");
		newTask.setImportanceLevel(3);
		newTask.setTaskType(TaskInfo.TASK_TYPE.FLOATING);
		
		Calendar tempStartDate = new GregorianCalendar(2014, 1, 26);
		Calendar tempEndDate = new GregorianCalendar(2014, 1, 28);
		TaskInfo secondTask = new TaskInfo();
		secondTask.setTaskName("Task 2");
		secondTask.setImportanceLevel(0);
		secondTask.setTaskType(TaskInfo.TASK_TYPE.TIMED);
		secondTask.setStartDate(tempStartDate);
		secondTask.setEndDate(tempEndDate);
		
		tempEndDate = new GregorianCalendar(2014, 5, 14);
		TaskInfo thirdTask = new TaskInfo();
		thirdTask.setTaskName("Task 3");
		secondTask.setImportanceLevel(2);
		thirdTask.setTaskType(TaskInfo.TASK_TYPE.DEADLINE);
		thirdTask.setStartDate(tempEndDate);
		
		TaskListShop.getInstance().addTaskToList(newTask);
		TaskListShop.getInstance().addTaskToList(secondTask);
		TaskListShop.getInstance().addTaskToList(thirdTask);
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
	
	private static void updateTaskInfoBasedOnParameter(TaskInfo taskInfoToUpdate,String parameterString) {
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
		
		taskname = functionFindTaskname(processedText);
		setTypeAndDate(thisTaskInfo, processedText);
		
		
		//thisTaskInfo.setTaskName(taskname);
		//thisTaskInfo.setStartDate(startDate);
		//thisTaskInfo.setEndDate(endDate);
		thisTaskInfo.setImportanceLevel(priority);
		
	}
	
	private static void setTypeAndDate(TaskInfo thisTaskInfo, String[] processedText){
		boolean deadlineType = false;
		boolean timedType = false;
		
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
					return;
				}
			}
		}
		
		setTaskType(thisTaskInfo, deadlineType, timedType);
		
	}
	
	private static void timeTranslator(Calendar theTime, int correctTime){
		//this method translates all time formats
		//theTime.set(Calendar.HOUR_OF_DAY, theCorrectTime);
		//theTime.set(Calendar.MINUTE, theCorrectTime);
	}

	private static boolean verifyTimeValidity(String allegedTime) {
		try{
			Integer.parseInt(allegedTime);
			return true;
		}
		catch(IllegalArgumentException exception){
			return false;
		}
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
