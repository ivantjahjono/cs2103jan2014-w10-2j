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

	private static String[] textProcess(String userCommand){
		String[] commandWord = userCommand.trim().split("\\s+");
		return commandWord;
	}
	
	private static String getFirstWord(String userCommand) {
		String[] elements = textProcess(userCommand);
		String firstWord = elements[0].toLowerCase();
		return firstWord;
	}
}
