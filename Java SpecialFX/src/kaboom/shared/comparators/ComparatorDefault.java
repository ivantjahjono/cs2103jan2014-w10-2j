//@author A0096670W
package kaboom.shared.comparators;

import java.util.Calendar;
import java.util.Comparator;

import kaboom.shared.TaskInfo;

public class ComparatorDefault implements Comparator<TaskInfo> {

	public int compare(TaskInfo task1, TaskInfo task2) {
		final int COMPARE_EQUAL = 0;

		int compareValue = compareDates(task1.getStartDate(), task2.getStartDate());
		if (compareValue != COMPARE_EQUAL) {
			return compareValue;
		}
		
		if (task1.getStartDate() != null && task2.getStartDate() == null) {
			return -1;
		}
		
		if (task1.getStartDate() == null && task2.getStartDate() != null) {
			return 1;
		}
		
		compareValue = compareDates(task1.getEndDate(), task2.getEndDate());
		if (compareValue != COMPARE_EQUAL) {
			return compareValue;
		}
		
		compareValue = comparePriority(task1.getPriority(), task2.getPriority());
		if (compareValue != COMPARE_EQUAL) {
			return compareValue;
		}
		
		return task1.getTaskName().compareToIgnoreCase(task2.getTaskName());
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
}
