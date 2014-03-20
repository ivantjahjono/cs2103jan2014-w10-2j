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

	private String fileName;

	private static final Logger logger = Logger.getLogger("StorageLogger");

	public Storage(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * This function stores all the data in taskListShop to the text file specified
	 * in the constructor. Each task is on a new line. The task attributes are
	 * delimited by "|".
	 * @param	None
	 * @return 	True if the file is successfully written, false otherwise.
	 */
	public boolean store() {
		try {
			logger.info("Trying to write text file: " + fileName);
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			Vector<TaskInfo> taskListShop = TaskListShop.getInstance().getAllTaskInList();
			assert(taskListShop != null);

			for (int i = 0; i < taskListShop.size(); i++) {
				StringBuilder output = new StringBuilder();
				TaskInfo task = taskListShop.get(i);

				output.append(task.getTaskName() + delimiter);
				output.append((TaskInfo.taskTypeToString(task.getTaskType()) + delimiter));
				output.append(task.getStartDate().get(Calendar.YEAR) + delimiter);
				output.append(task.getStartDate().get(Calendar.MONTH) + delimiter);
				output.append(task.getStartDate().get(Calendar.DAY_OF_MONTH) + delimiter);
				output.append(task.getStartDate().get(Calendar.HOUR_OF_DAY) + delimiter);
				output.append(task.getStartDate().get(Calendar.MINUTE) + delimiter);
				output.append(task.getEndDate().get(Calendar.YEAR) + delimiter);
				output.append(task.getEndDate().get(Calendar.MONTH) + delimiter);
				output.append(task.getEndDate().get(Calendar.DAY_OF_MONTH) + delimiter);
				output.append(task.getEndDate().get(Calendar.HOUR_OF_DAY) + delimiter);
				output.append(task.getEndDate().get(Calendar.MINUTE) + delimiter);
				output.append(task.getImportanceLevel() + delimiter);
				writer.write(output.toString());
				writer.newLine();
				writer.flush();
			}

			logger.info("Written to text file: " + fileName);
			writer.close();
			return true;
		} catch (IOException e) {
			logger.info("Cannot write to text file: " + fileName);
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
			TaskListShop taskListShop = TaskListShop.getInstance();
			assert(taskListShop != null);

			while (fileScanner.hasNext()) {
				Calendar now  = Calendar.getInstance();
				String input = fileScanner.nextLine().trim();
				String[] inputSplit = input.split(delimiter);
				TaskInfo task = new TaskInfo();
				task.setTaskName(inputSplit[INDEX_TASK_NAME]);
				task.setTaskType(TaskInfo.getTaskType(inputSplit[INDEX_TASK_TYPE]));

				//THERE MIGHT STILL BE BUGS IN THE LOADING OF CALENDAR ATTRIBUTES
				Calendar startDate = Calendar.getInstance();
				startDate.set(Calendar.YEAR, Integer.parseInt(inputSplit[INDEX_START_YEAR]));
				startDate.set(Calendar.MONTH, Integer.parseInt(inputSplit[INDEX_START_MONTH]));
				startDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(inputSplit[INDEX_START_DAY]));
				startDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(inputSplit[INDEX_START_HOUR]));
				startDate.set(Calendar.MINUTE, Integer.parseInt(inputSplit[INDEX_START_MINUTE]));
				task.setStartDate(startDate);

				Calendar endDate = Calendar.getInstance();
				endDate.set(Calendar.YEAR, Integer.parseInt(inputSplit[INDEX_END_YEAR]));
				endDate.set(Calendar.MONTH, Integer.parseInt(inputSplit[INDEX_END_MONTH]));
				endDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(inputSplit[INDEX_END_DAY]));
				endDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(inputSplit[INDEX_END_HOUR]));
				endDate.set(Calendar.MINUTE, Integer.parseInt(inputSplit[INDEX_END_MINUTE]));
				task.setEndDate(endDate);
				
				boolean isExpired = now.after(endDate);
				task.setExpiryFlag(isExpired);  //Set true if current time is after endDate

				task.setImportanceLevel(Integer.parseInt(inputSplit[INDEX_IMPORTANCE_LEVEL]));
				taskListShop.addTaskToList(task);
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
