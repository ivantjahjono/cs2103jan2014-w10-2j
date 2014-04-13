//@author A0096670W
package kaboom.shared.comparators;

import java.util.Comparator;

import kaboom.shared.TaskInfo;

public class ComparatorPriority implements Comparator<TaskInfo> {

	public int compare(TaskInfo task1, TaskInfo task2) {
		final int COMPARE_EQUAL = 0;
		
		int compareValue = comparePriority(task1.getPriority(), task2.getPriority());
		if (compareValue != COMPARE_EQUAL) {
			return compareValue;
		} else {
			return compareTaskName(task1.getTaskName(), task2.getTaskName());
		}
	}
	
	private int comparePriority(int task1Priority, int task2Priority) {
		return task2Priority - task1Priority;
	}
	
	private int compareTaskName(String name1, String name2) {
		return name1.compareToIgnoreCase(name2);
	}
}
