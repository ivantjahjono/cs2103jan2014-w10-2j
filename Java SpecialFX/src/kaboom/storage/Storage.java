package kaboom.storage;

import kaboom.storage.TaskListShop;

import java.util.Scanner;
import java.util.Vector;
import java.util.Calendar;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import kaboom.logic.TASK_TYPE;
import kaboom.logic.TaskInfo;


public class Storage {
	private static final String delimiter = "~";
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

	private static final Logger logger = Logger.getLogger("StorageLogger");

	public Storage(String fileName) {
		this.fileName = fileName;
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
			logger.info("Trying to write current tasks text file: " + fileName);
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			Vector<TaskInfo> currentTaskList = TaskListShop.getInstance().getAllCurrentTasks();
			assert(currentTaskList != null);

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
					logger.info("startDate for task " + i + "is null");
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
					logger.info("endDate for task " + i + "is null");
					output.append(" " + delimiter);
					output.append(" " + delimiter);
					output.append(" " + delimiter);
					output.append(" " + delimiter);
					output.append(" " + delimiter);
				}
				output.append(task.getImportanceLevel() + delimiter);
				output.append(task.getExpiryFlag() + delimiter);
				output.append(task.getDone() + delimiter);
				writer.write(output.toString());
				writer.newLine();
				writer.flush();
			}

			logger.info("Trying to write archived tasks text file: " + fileName);
			currentTaskList = TaskListShop.getInstance().getAllArchivedTasks();
			assert(currentTaskList != null);

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
					logger.info("startDate for task " + i + "is null");
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
					logger.info("endDate for task " + i + "is null");
					output.append(" " + delimiter);
					output.append(" " + delimiter);
					output.append(" " + delimiter);
					output.append(" " + delimiter);
					output.append(" " + delimiter);
				}
				output.append(task.getImportanceLevel() + delimiter);
				output.append(task.getExpiryFlag() + delimiter);
				output.append(task.getDone() + delimiter);
				writer.write(output.toString());
				writer.newLine();
				writer.flush();
			}

			logger.info("Written to text file: " + fileName);
			writer.close();
			return true;
		} catch (IOException e) {
			logger.warning("Cannot write to text file: " + fileName);
			return false;
		}
	}

	/**
	 * This function loads all the data in the text file to taskListShop. 
	 * @param	None
	 * @return 	True if the file is successfully read, false otherwise.
	 */
	public boolean load() {
		try {
			logger.info("Trying to read from text file: " + fileName);
			File inFile = new File(fileName);
			Scanner fileScanner = new Scanner(inFile);
			TaskListShop currentTaskList = TaskListShop.getInstance();
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
					logger.info("startDate for task \"" + inputSplit[INDEX_TASK_NAME] + "\" is null");
					startDate = null;
				}
				else {
					startDate.set(Calendar.YEAR, Integer.parseInt(inputSplit[INDEX_START_YEAR]));
					startDate.set(Calendar.MONTH, Integer.parseInt(inputSplit[INDEX_START_MONTH]));
					startDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(inputSplit[INDEX_START_DAY]));
					startDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(inputSplit[INDEX_START_HOUR]));
					startDate.set(Calendar.MINUTE, Integer.parseInt(inputSplit[INDEX_START_MINUTE]));
				}
				task.setStartDate(startDate);

				Calendar endDate = Calendar.getInstance();
				if (inputSplit[INDEX_END_YEAR].equals(" ")) {
					logger.info("endDate for task \"" + inputSplit[INDEX_TASK_NAME] + "\" is null");
					endDate = null;
				}
				else {
					endDate.set(Calendar.YEAR, Integer.parseInt(inputSplit[INDEX_END_YEAR]));
					endDate.set(Calendar.MONTH, Integer.parseInt(inputSplit[INDEX_END_MONTH]));
					endDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(inputSplit[INDEX_END_DAY]));
					endDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(inputSplit[INDEX_END_HOUR]));
					endDate.set(Calendar.MINUTE, Integer.parseInt(inputSplit[INDEX_END_MINUTE]));
				}
				task.setEndDate(endDate);

				boolean isExpired = now.after(endDate);
				if (!task.getTaskType().equals(TASK_TYPE.FLOATING)) {
					task.setExpiryFlag(isExpired);  //Set true if current time is after endDate
				}
				else {
					task.setExpiryFlag(false);  //Floating tasks cannot expire
				}

				task.setImportanceLevel(Integer.parseInt(inputSplit[INDEX_IMPORTANCE_LEVEL]));
				task.setExpiryFlag(Boolean.parseBoolean(inputSplit[INDEX_IS_EXPIRED]));
				task.setDone(Boolean.parseBoolean(inputSplit[INDEX_IS_DONE]));

				if (task.getDone()) {
					currentTaskList.addTaskToArchivedList(task);
				}
				else {
					currentTaskList.addTaskToList(task);
				}
			}

			logger.info(fileName + " has been scanned and read");
			fileScanner.close();
			return true;

		} catch (IOException e) {
			//Try to reproduce the error and find out what it is
			File inFile = new File(fileName);

			if (!inFile.exists()) {
				logger.info(fileName + " does not exist. Skipping.");
				return true;  //Do nothing if the file does not exist because it will be created later
			}
			else {
				logger.warning("Cannot read from text file: " + fileName);
				return false;
			}
		}
	}
}
