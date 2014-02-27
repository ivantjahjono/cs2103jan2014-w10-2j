import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;



/*
** This is main class that will run Task Master Kaboom
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
	
	private static final String MESSAGE_COMMAND_ADD_SUCCESS = "Successfully added %1$s";
	private static final String MESSAGE_COMMAND_DELETE_SUCCESS = "%1$s deleted.";
	private static final String MESSAGE_COMMAND_MODIFY_SUCCESS = "Modify %1$s successful";
	private static final String MESSAGE_COMMAND_SEARCH_SUCCESS = "Search done";
	private static final String MESSAGE_COMMAND_INVALID = "Invalid command!";
	
	private static KaboomGUI taskUi = new KaboomGUI();
	
	public static void main(String[] args) {
		// Setup application
			// Setup UI
			// Setup Memory
			addTemporaryTaskForTesting();
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
		
		while (true) {
			//
		}
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
		TaskInfo currentTaskInfo = null;
		COMMAND_TYPE commandType = determineCommandType(userInputSentence);
		if (commandType != COMMAND_TYPE.INVALID) {
			currentTaskInfo = createTaskInfoBasedOnCommand(userInputSentence);
		}
		String feedback = executeCommand(commandType);
		
		Vector<TaskInfo> taskToDisplay = TaskListShop.getInstance().getAllTaskInList();
		taskUi.updateUiDisplay(feedback, taskToDisplay);
		return feedback;
	}
	
	private static COMMAND_TYPE determineCommandType(String userCommand) {
		
		String commandTypeString = getFirstWord(userCommand);
		System.out.println(commandTypeString);
		
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
	
	private static String executeCommand(COMMAND_TYPE command) {
		switch(command) {
			case ADD:
				return String.format(MESSAGE_COMMAND_ADD_SUCCESS, command);
			case DELETE:
				return String.format(MESSAGE_COMMAND_DELETE_SUCCESS, command);
			case MODIFY:
				return String.format(MESSAGE_COMMAND_MODIFY_SUCCESS, command);
			case SEARCH:
				return String.format(MESSAGE_COMMAND_SEARCH_SUCCESS, command);
			case INVALID:
				return String.format(MESSAGE_COMMAND_INVALID, command);
			default:
				return MESSAGE_COMMAND_INVALID;
		}
	}
	
	private static TaskInfo createTaskInfoBasedOnCommand(String userInputSentence) {
		return null;
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
