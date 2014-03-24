package kaboom.logic.command;

import static org.junit.Assert.*;

import java.util.Calendar;

import kaboom.logic.TaskInfo;

import org.junit.Before;
import org.junit.Test;

public class CommandTest {
	TaskInfo task;
	
	@Before
	public void initialise() {
		task = new TaskInfo();
		taskInfoUpdate(task);
	}
	
	public void taskInfoUpdate(TaskInfo task) {
		Calendar startDate = Calendar.getInstance();
		startDate.set(2014,1,1,8,0);
		Calendar endDate = Calendar.getInstance();
		endDate.set(2014,1,5,8,0);
		
		task.setTaskName("Hello World");
		task.setStartDate(startDate);
		task.setEndDate(endDate);
		task.setImportanceLevel(3);
	}
	
	
	//CommandAdd
	@Test
	public void testCommandAdd() {
		Command com = new CommandAdd();
		
		//Initial test when no taskinfo is inside to be executed
		assertEquals("Added a file with no Task Name", com.execute().getFeedback());
		
		com.setTaskInfo(task);

		//Test Command feedback
		assertEquals("Successfully added Hello World", com.execute().getFeedback());
		task.setTaskName("hello world ");
		assertEquals("Successfully added hello world ", com.execute().getFeedback());
	}

	//CommandView
	@Test
	public void testCommandView() {
		Command com = new CommandView();
		com.setTaskInfo(task);

		//Test Command feedback
		assertEquals("Invalid View Mode", com.execute().getFeedback());
		task.setTaskName("floating");
		assertEquals("Floating Task Mode", com.execute().getFeedback());
		task.setTaskName("deadline");
		assertEquals("Deadline Task Mode", com.execute().getFeedback());
		task.setTaskName("running");
		assertEquals("Running Task Mode", com.execute().getFeedback());
	}
	
}
