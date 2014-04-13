//@author A0096670W
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
	private static final String delimiter = "\u00b0";
	private static final int INDEX_TASK_NAME = 0;
	private static final int INDEX_TASK_TYPE = 1;
	private static final int INDEX_START_YEAR = 2;
	private static final int INDEX_START_MONTH = 3;
	private static final int INDEX_START_DAY = 4;
	private static final int INDEX_START_HOUR = 5;
	private static final int INDEX_START_MINUTE = 6;
	private static final int INDEX_END_YEAR = 7;
	private static final int INDEX_END_MONTH = 8;
	private static final int INDEX_END_DAY = 9;
	private static final int INDEX_END_HOUR = 10;
	private static final int INDEX_END_MINUTE = 11;
	private static final int INDEX_IMPORTANCE_LEVEL = 12;
	private static final int INDEX_IS_EXPIRED = 13;
	private static final int INDEX_IS_DONE = 14;

	private String fileName;
	private final String storageLoggerFile = "StorageLog.txt";

	private final Logger logger = Logger.getLogger("StorageLogger");
	private FileHandler fileHandler; 
	private BufferedWriter writer;

	public Storage(String fileName) {
		try {
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
	 * This function stores all the data in taskListShop to the text file specified
	 * in the constructor. Each task is on a new line. The task attributes are
	 * delimited by the delimiter as specified.
	 * @param	None
	 * @return 	True if the file is successfully written, false otherwise.
	 */
	public boolean store() {
		try {
			writer = new BufferedWriter(new FileWriter(fileName));

			logger.fine("Trying to write current tasks text file: " + fileName);
			Vector<TaskInfo> currentTaskList = TaskDepository.getInstance().getAllCurrentTasks();
			assert(currentTaskList != null);
			writeToFile(currentTaskList);

			logger.fine("Trying to write archived tasks text file: " + fileName);
			currentTaskList = TaskDepository.getInstance().getAllArchivedTasks();
			assert(currentTaskList != null);
			writeToFile(currentTaskList);

			logger.fine("All tasks written to text file: " + fileName);
			writer.close();
			return true;
		} catch (IOException e) {
			logger.warning("Cannot write to text file: " + fileName);
			return false;
		}
	}

	private void writeToFile(Vector<TaskInfo> currentTaskList) throws IOException {
		for (int i = 0; i < currentTaskList.size(); i++) {
			StringBuilder output = new StringBuilder();
			TaskInfo task = currentTaskList.get(i);

			output.append(task.getTaskName() + delimiter);
			output.append((TaskInfo.taskTypeToString(task.getTaskType()) + delimiter));

			Calendar startDate = task.getStartDate();
			if (startDate != null) {
				output.append(startDate.get(Calendar.YEAR) + delimiter);
				output.append(startDate.get(Calendar.MONTH) + delimiter);
				output.append(startDate.get(Calendar.DAY_OF_MONTH) + delimiter);
				output.append(startDate.get(Calendar.HOUR_OF_DAY) + delimiter);
				output.append(startDate.get(Calendar.MINUTE) + delimiter);
			}
			else {
				logger.fine("startDate for task " + i + "is null");
				output.append(" " + delimiter);
				output.append(" " + delimiter);
				output.append(" " + delimiter);
				output.append(" " + delimiter);
				output.append(" " + delimiter);
			}

			Calendar endDate = task.getEndDate();
			if (endDate != null) {
				output.append(endDate.get(Calendar.YEAR) + delimiter);
				output.append(endDate.get(Calendar.MONTH) + delimiter);
				output.append(endDate.get(Calendar.DAY_OF_MONTH) + delimiter);
				output.append(endDate.get(Calendar.HOUR_OF_DAY) + delimiter);
				output.append(endDate.get(Calendar.MINUTE) + delimiter);
			}
			else {
				logger.fine("endDate for task " + i + "is null");
				output.append(" " + delimiter);
				output.append(" " + delimiter);
				output.append(" " + delimiter);
				output.append(" " + delimiter);
				output.append(" " + delimiter);
			}
			output.append(task.getPriority() + delimiter);
			output.append(task.getExpiryFlag() + delimiter);
			output.append(task.getDone() + delimiter);
			writer.write(output.toString());
			writer.newLine();
			writer.flush();
		}
	}

	/**
	 * This function loads all the data in the text file to taskListShop. 
	 * @param	None
	 * @return 	True if the file is successfully read, false otherwise.
	 */
	public boolean load() {
		try {
			logger.fine("Trying to read from text file: " + fileName);
			File inFile = new File(fileName);
			Scanner fileScanner = new Scanner(inFile);
			TaskDepository currentTaskList = TaskDepository.getInstance();
			assert(currentTaskList != null);

			while (fileScanner.hasNext()) {
				Calendar now  = Calendar.getInstance();
				String input = fileScanner.nextLine().trim();
				String[] inputSplit = input.split(delimiter);
				TaskInfo task = new TaskInfo();
				task.setTaskName(inputSplit[INDEX_TASK_NAME]);
				task.setTaskType(TaskInfo.getTaskType(inputSplit[INDEX_TASK_TYPE]));

				Calendar startDate = Calendar.getInstance();
				if (inputSplit[INDEX_START_YEAR].equals(" ")) {
					logger.fine("startDate for task \"" + inputSplit[INDEX_TASK_NAME] + "\" is null");
					startDate = null;
				}
				else {
					startDate.set(Calendar.YEAR, Integer.parseInt(inputSplit[INDEX_START_YEAR]));
					startDate.set(Calendar.MONTH, Integer.parseInt(inputSplit[INDEX_START_MONTH]));
					startDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(inputSplit[INDEX_START_DAY]));
					startDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(inputSplit[INDEX_START_HOUR]));
					startDate.set(Calendar.MINUTE, Integer.parseInt(inputSplit[INDEX_START_MINUTE]));
					startDate.set(Calendar.SECOND, 59);
					startDate.set(Calendar.MILLISECOND, 0);
				}
				task.setStartDate(startDate);

				Calendar endDate = Calendar.getInstance();
				if (inputSplit[INDEX_END_YEAR].equals(" ")) {
					logger.fine("endDate for task \"" + inputSplit[INDEX_TASK_NAME] + "\" is null");
					endDate = null;
				}
				else {
					endDate.set(Calendar.YEAR, Integer.parseInt(inputSplit[INDEX_END_YEAR]));
					endDate.set(Calendar.MONTH, Integer.parseInt(inputSplit[INDEX_END_MONTH]));
					endDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(inputSplit[INDEX_END_DAY]));
					endDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(inputSplit[INDEX_END_HOUR]));
					endDate.set(Calendar.MINUTE, Integer.parseInt(inputSplit[INDEX_END_MINUTE]));
					endDate.set(Calendar.SECOND, 59);
					endDate.set(Calendar.MILLISECOND, 0);
				}
				task.setEndDate(endDate);

				boolean isExpired = now.after(endDate);
				if (!task.getTaskType().equals(TASK_TYPE.FLOATING)) {
					task.setExpiryFlag(isExpired);  //Set true if current time is after endDate
				}
				else {
					task.setExpiryFlag(false);  //Floating tasks cannot expire
				}

				task.setPriority(Integer.parseInt(inputSplit[INDEX_IMPORTANCE_LEVEL]));
				task.setExpiryFlag(Boolean.parseBoolean(inputSplit[INDEX_IS_EXPIRED]));
				task.setDone(Boolean.parseBoolean(inputSplit[INDEX_IS_DONE]));

				if (task.getDone()) {
					currentTaskList.addTaskToArchivedList(task);
				}
				else {
					currentTaskList.addTaskToList(task);
				}
			}

			logger.fine(fileName + " has been scanned and read");
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
}
