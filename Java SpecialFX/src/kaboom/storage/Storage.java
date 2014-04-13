//@author A0096670W
/**
 * This class stores the data in the task depository into a text file
 * for persistence. Storing and loading of data are called by the store()
 * and load() functions respectively. Each task is stored in a single line
 * in the text file and the task attributes are separated by a delimiter.
 */

package kaboom.storage;

import kaboom.shared.TASK_TYPE;
import kaboom.shared.TaskInfo;
import kaboom.storage.TaskDepository;

import java.util.Scanner;
import java.util.Vector;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;


public class Storage {
	private final String DELIMITER = "\u00b0";  //Delimiter cannot be typed using keyboard
	private final String BLANK = " ";
	private final int INDEX_TASK_NAME = 0;
	private final int INDEX_TASK_TYPE = 1;
	private final int INDEX_START_YEAR = 2;
	private final int INDEX_START_MONTH = 3;
	private final int INDEX_START_DAY = 4;
	private final int INDEX_START_HOUR = 5;
	private final int INDEX_START_MINUTE = 6;
	private final int INDEX_END_YEAR = 7;
	private final int INDEX_END_MONTH = 8;
	private final int INDEX_END_DAY = 9;
	private final int INDEX_END_HOUR = 10;
	private final int INDEX_END_MINUTE = 11;
	private final int INDEX_IMPORTANCE_LEVEL = 12;
	private final int INDEX_IS_EXPIRED = 13;
	private final int INDEX_IS_DONE = 14;

	private String fileName;
	private final String storageLoggerFile = "StorageLog.txt";

	private final Logger logger = Logger.getLogger("StorageLogger");
	private FileHandler fileHandler; 
	private BufferedWriter writer;
	private TaskDepository taskDepository;

	public Storage(String fileName) {
		try {
			taskDepository = TaskDepository.getInstance();
			this.fileName = fileName;
			fileHandler = new FileHandler(storageLoggerFile);
			logger.addHandler(fileHandler);
			fileHandler.setFormatter(new SimpleFormatter());
			logger.setUseParentHandlers(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This function stores all the data in task depository to the text file specified
	 * in the constructor. Each task is on a new line. The task attributes are
	 * delimited by the delimiter as specified.
	 * @param	None
	 * @return 	True if the file is successfully written, false otherwise.
	 */
	public boolean store() {
		try {
			writer = new BufferedWriter(new FileWriter(fileName));

			writeAllTasks();

			logger.fine("All tasks written to text file: " + fileName);
			writer.close();
			return true;
		} catch (IOException e) {
			logger.warning("Cannot write to text file: " + fileName);
			return false;
		}
	}

	private void writeAllTasks() throws IOException {
		writePresentTaskList();
		writeArchivedTaskList();
	}

	private void writePresentTaskList() throws IOException {
		logger.fine("Trying to write current tasks text file: " + fileName);
		Vector<TaskInfo> presentTaskList = taskDepository.getAllCurrentTasks();
		assert (presentTaskList != null);
		writeToFile(presentTaskList);
	}
	
	private void writeArchivedTaskList() throws IOException {
		logger.fine("Trying to write archived tasks text file: " + fileName);
		Vector<TaskInfo> archivedTaskList = taskDepository.getAllArchivedTasks();
		assert (archivedTaskList != null);
		writeToFile(archivedTaskList);
	}

	private void writeToFile(Vector<TaskInfo> currentTaskList) throws IOException {
		for (int i = 0; i < currentTaskList.size(); i++) {
			StringBuilder output = new StringBuilder();
			TaskInfo task = currentTaskList.get(i);
			appendTaskAttributesToOutput(output, task);
			writer.write(output.toString());
			writer.newLine();
			writer.flush();
		}
	}

	private void appendTaskAttributesToOutput(StringBuilder output, TaskInfo task) {
		appendTaskName(output, task);
		appendTaskType(output, task);
		appendStartDate(output, task);
		appendEndDate(output, task);
		appendPriority(output, task);
		appendExpiry(output, task);
		appendDone(output, task);
	}

	private void appendDone(StringBuilder output, TaskInfo task) {
		output.append(task.getDone() + DELIMITER);
	}

	private void appendExpiry(StringBuilder output, TaskInfo task) {
		output.append(task.getExpiryFlag() + DELIMITER);
	}

	private void appendPriority(StringBuilder output, TaskInfo task) {
		output.append(task.getPriority() + DELIMITER);
	}

	private void appendEndDate(StringBuilder output, TaskInfo task) {
		Calendar endDate = task.getEndDate();
		if (endDate != null) {
			output.append(endDate.get(Calendar.YEAR) + DELIMITER);
			output.append(endDate.get(Calendar.MONTH) + DELIMITER);
			output.append(endDate.get(Calendar.DAY_OF_MONTH) + DELIMITER);
			output.append(endDate.get(Calendar.HOUR_OF_DAY) + DELIMITER);
			output.append(endDate.get(Calendar.MINUTE) + DELIMITER);
		}
		else {
			logger.fine("endDate for task " + task.getTaskName() + "is null");
			output.append(BLANK + DELIMITER);
			output.append(BLANK + DELIMITER);
			output.append(BLANK + DELIMITER);
			output.append(BLANK + DELIMITER);
			output.append(BLANK + DELIMITER);
		}
	}

	private void appendStartDate(StringBuilder output, TaskInfo task) {
		Calendar startDate = task.getStartDate();
		if (startDate != null) {
			output.append(startDate.get(Calendar.YEAR) + DELIMITER);
			output.append(startDate.get(Calendar.MONTH) + DELIMITER);
			output.append(startDate.get(Calendar.DAY_OF_MONTH) + DELIMITER);
			output.append(startDate.get(Calendar.HOUR_OF_DAY) + DELIMITER);
			output.append(startDate.get(Calendar.MINUTE) + DELIMITER);
		}
		else {
			logger.fine("startDate for task " + task.getTaskName() + "is null");
			output.append(BLANK + DELIMITER);
			output.append(BLANK + DELIMITER);
			output.append(BLANK + DELIMITER);
			output.append(BLANK + DELIMITER);
			output.append(BLANK + DELIMITER);
		}
	}

	private void appendTaskType(StringBuilder output, TaskInfo task) {
		output.append((TaskInfo.taskTypeToString(task.getTaskType()) + DELIMITER));
	}

	private void appendTaskName(StringBuilder output, TaskInfo task) {
		output.append(task.getTaskName() + DELIMITER);
	}

	/**
	 * This function loads all the data in the text file to the task depository. 
	 * @param	None
	 * @return 	True if the file is successfully read, false otherwise.
	 */
	public boolean load() {
		try {
			logger.fine("Trying to read from text file: " + fileName);
			File inFile = new File(fileName);
			Scanner fileScanner = new Scanner(inFile);
			assert (taskDepository != null);

			loadUntilEndOfFile(fileScanner);

			logger.fine(fileName + " has been fully scanned and read");
			fileScanner.close();
			return true;

		} catch (IOException e) {
			//Try to reproduce the error and find out what it is
			File inFile = new File(fileName);

			if (!inFile.exists()) {
				logger.fine(fileName + " does not exist. Skipping.");
				return true;  //Do nothing if the file does not exist because it will be created later
			}
			else {
				logger.warning("Cannot read from text file: " + fileName);
				return false;
			}
		} catch (Exception e) {
			return store();
		}
	}

	private void loadUntilEndOfFile(Scanner fileScanner) {
		while (fileScanner.hasNext()) {
			String input = getNextLineFromFile(fileScanner);
			String[] inputSplitTokens = input.split(DELIMITER);
			
			TaskInfo task = new TaskInfo();
			setTaskAttributes(task, inputSplitTokens);
			addTaskToAppropriateList(task);
		}
	}
	
	private String getNextLineFromFile(Scanner fileScanner) {
		return fileScanner.nextLine().trim();
	}

	private void setTaskAttributes(TaskInfo task, String[] inputSplitTokens) {
		setTaskName(task, inputSplitTokens);
		setTaskType(task, inputSplitTokens);
		setStartDate(task, inputSplitTokens);
		setEndDate(task, inputSplitTokens);
		setPriority(task, inputSplitTokens);
		setExpired(task, inputSplitTokens);
		setDone(task, inputSplitTokens);
	}
	
	private void setTaskName(TaskInfo task, String[] inputTokens) {
		task.setTaskName(inputTokens[INDEX_TASK_NAME]);
	}
	
	private void setTaskType(TaskInfo task, String[] inputTokens) {
		task.setTaskType(TaskInfo.getTaskType(inputTokens[INDEX_TASK_TYPE]));
	}
	
	private void setStartDate(TaskInfo task, String[] inputTokens) {
		Calendar startDate = Calendar.getInstance();
		if (inputTokens[INDEX_START_YEAR].equals(BLANK)) {
			logger.fine("Start date for task \"" + inputTokens[INDEX_TASK_NAME] + "\" is null");
			startDate = null;
		}
		else {
			startDate.set(Calendar.YEAR, Integer.parseInt(inputTokens[INDEX_START_YEAR]));
			startDate.set(Calendar.MONTH, Integer.parseInt(inputTokens[INDEX_START_MONTH]));
			startDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(inputTokens[INDEX_START_DAY]));
			startDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(inputTokens[INDEX_START_HOUR]));
			startDate.set(Calendar.MINUTE, Integer.parseInt(inputTokens[INDEX_START_MINUTE]));
			startDate.set(Calendar.SECOND, 0);
			startDate.set(Calendar.MILLISECOND, 0);
		}
		task.setStartDate(startDate);
	}
	
	private void setEndDate(TaskInfo task, String[] inputTokens) {
		Calendar endDate = Calendar.getInstance();
		if (inputTokens[INDEX_END_YEAR].equals(BLANK)) {
			logger.fine("End date for task \"" + inputTokens[INDEX_TASK_NAME] + "\" is null");
			endDate = null;
		}
		else {
			endDate.set(Calendar.YEAR, Integer.parseInt(inputTokens[INDEX_END_YEAR]));
			endDate.set(Calendar.MONTH, Integer.parseInt(inputTokens[INDEX_END_MONTH]));
			endDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(inputTokens[INDEX_END_DAY]));
			endDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(inputTokens[INDEX_END_HOUR]));
			endDate.set(Calendar.MINUTE, Integer.parseInt(inputTokens[INDEX_END_MINUTE]));
			endDate.set(Calendar.SECOND, 59);
			endDate.set(Calendar.MILLISECOND, 0);
		}
		task.setEndDate(endDate);
	}
	
	private void setPriority(TaskInfo task, String[] inputTokens) {
		task.setPriority(Integer.parseInt(inputTokens[INDEX_IMPORTANCE_LEVEL]));
	}
	
	private void setExpired(TaskInfo task, String[] inputTokens) {
		Calendar now = Calendar.getInstance();
		Calendar endDate = task.getEndDate();
		boolean isExpired = now.after(endDate);
		
		if (!task.getTaskType().equals(TASK_TYPE.FLOATING)) {
			task.setExpiryFlag(isExpired);  //Set true if current time is after endDate
		}
		else {
			task.setExpiryFlag(Boolean.parseBoolean(inputTokens[INDEX_IS_EXPIRED]));  //Floating tasks cannot expire
		}
	}
	
	private void setDone(TaskInfo task, String[] inputTokens) {
		task.setDone(Boolean.parseBoolean(inputTokens[INDEX_IS_DONE]));
	}
	
	private void addTaskToAppropriateList(TaskInfo task) {
		if (task.getDone()) {
			taskDepository.addTaskToArchivedList(task);
		}
		else {
			taskDepository.addTaskToList(task);
		}
	}
}
