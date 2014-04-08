package kaboom.logic.command;

import java.util.Comparator;

import kaboom.logic.TaskInfo;

public class ComparatorEndDate implements Comparator<TaskInfo> {

	public int compare(TaskInfo task1, TaskInfo task2) {
		if (task1.getEndDate() != null && task2.getEndDate() != null) {
			return task1.getEndDate().compareTo(task2.getEndDate());
		} else if (task1.getStartDate() != null && task2.getStartDate() != null) {
			return task1.getStartDate().compareTo(task2.getStartDate());
		} else {
			return 0;  //Both are equal
		}
	}
}
