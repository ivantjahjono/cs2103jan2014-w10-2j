package kaboom.logic.command;

import java.util.Comparator;

import kaboom.logic.TaskInfo;

public class ComparatorStartDate implements Comparator<TaskInfo> {
	public int compare(TaskInfo task1, TaskInfo task2) {
		if (task1.getStartDate() != null) {
			return task2.getStartDate().compareTo(task1.getStartDate());
		}
		else {
			return -1;
		}
	}
}
