//@author A0096670W

/**
 * This comparator is the default comparator for tasks.
 * First, it compares the start dates.
 * If they are the same, it compares the end dates.
 * If they are the same, it compares the priority.
 * If they are the same, the task name is compared.
 */
package kaboom.shared.comparators;

import java.util.Calendar;
import java.util.Comparator;

import kaboom.shared.TaskInfo;

public class ComparatorDefault implements Comparator<TaskInfo> {

	public int compare(TaskInfo task1, TaskInfo task2) {
		final int COMPARE_EQUAL = 0;
		final int COMPARE_LESS_THAN = -1;
		final int COMPARE_MORE_THAN = 1;

		int compareValue = compareDates(task1.getStartDate(), task2.getStartDate());
		if (compareValue != COMPARE_EQUAL) {
			return compareValue;
		}
		
		if (task1.getStartDate() != null && task2.getStartDate() == null) {
			return COMPARE_LESS_THAN;
		}
		
		if (task1.getStartDate() == null && task2.getStartDate() != null) {
			return COMPARE_MORE_THAN;
		}
		
		compareValue = compareDates(task1.getEndDate(), task2.getEndDate());
		if (compareValue != COMPARE_EQUAL) {
			return compareValue;
		}
		
		compareValue = comparePriority(task1.getPriority(), task2.getPriority());
		if (compareValue != COMPARE_EQUAL) {
			return compareValue;
		}
		
		return compareTaskName(task1.getTaskName(), task2.getTaskName());
	}

	private int compareDates(Calendar date1, Calendar date2) {
		if (date1 == null || date2 == null) {
			return 0;
		} else {
			return date1.compareTo(date2);
		}
	}

	private int comparePriority(int priority1, int priority2) {
		return priority2 - priority1;
	}
	
	private int compareTaskName(String name1, String name2) {
		return name1.compareToIgnoreCase(name2);
	}
}
