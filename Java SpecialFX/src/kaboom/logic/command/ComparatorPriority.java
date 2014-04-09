package kaboom.logic.command;

import java.util.Comparator;

import kaboom.logic.TaskInfo;

public class ComparatorPriority implements Comparator<TaskInfo> {

	public int compare(TaskInfo task1, TaskInfo task2) {
		return task2.getPriority() - task1.getPriority();
	}
}
