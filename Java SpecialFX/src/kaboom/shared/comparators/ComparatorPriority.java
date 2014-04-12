//@author A0096670W
package kaboom.shared.comparators;

import java.util.Comparator;

import kaboom.shared.TaskInfo;

public class ComparatorPriority implements Comparator<TaskInfo> {

	public int compare(TaskInfo task1, TaskInfo task2) {
		return task2.getPriority() - task1.getPriority();
	}
}
