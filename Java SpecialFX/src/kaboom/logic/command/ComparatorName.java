package kaboom.logic.command;

import java.util.Comparator;

import kaboom.logic.TaskInfo;

public class ComparatorName implements Comparator<TaskInfo> {
		
	public int compare(TaskInfo task1, TaskInfo task2) {
		return task1.getTaskName().compareToIgnoreCase(task2.getTaskName());
	}
}
