/**
 * StorageTest.java:
 * This class tests the storage on whether it stores and loads information properly.
 * There are two boundary cases, no tasks to store/load or the file size limit.
 * Since it is not feasible to test the latter boundary, only the former will be tested.
 */
//@author A0096670W

package KaboomTest;

import static org.junit.Assert.*;

import java.io.LineNumberReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

import kaboom.storage.Storage;
import kaboom.storage.TaskDepository;

import org.junit.Before;
import org.junit.Test;

public class StorageTest {
	
	TaskDepository taskDepo;
	Storage storageTest;
	LineNumberReader lineNumberReader;
	private final int EMPTY = 0;
	
	@Before
	public void initailize() {
		taskDepo = TaskDepository.getInstance();
		assertNotNull(taskDepo);
		assertEquals(EMPTY, taskDepo.totalTaskCount());
		taskDepo.clearAll();
		storageTest = new Storage("StorageTest.txt");
		assertNotNull(storageTest);
		storageTest.load();
	}
	
	/**
	 * This function tests if the loading of the text file into task depository is successful.
	 * A LineNumberReader object is used and the whole file is skipped to the last line.
	 * Since Long.MAX_VALUE is more than 2 ExaBytes, skipping by that value will guarantee
	 * that the end of file is reached. 
	 * The line number is then obtained and compared with the task count in task depository.
	 * @throws IOException
	 */
	@Test
	public void testLoad() throws IOException {
		//Since load is already called when initialized, no need to call it again
		lineNumberReader = new LineNumberReader(new FileReader(new File("storageTest.txt")));
		lineNumberReader.skip(Long.MAX_VALUE);  //Long.MAX_VALUE is more than 2 ExaBytes
		assertEquals(taskDepo.totalTaskCount(), lineNumberReader.getLineNumber());
		lineNumberReader.close();
	}

	/**
	 * This function tests if the saving of tasks from task depository
	 *  to the text file is successful.
	 * A LineNumberReader object is used and the whole file is skipped to 
	 * the last line after storing
	 * is performed.
	 * Since Long.MAX_VALUE is more than 2 ExaBytes, skipping by that value 
	 * will guarantee that the end of file is reached. 
	 * The line number is then obtained and compared with the task count in task depository.
	 * @throws IOException
	 */
	@Test
	public void testStore() throws IOException {
		//Test usual case where there are tasks to store
		storageTest.store();
		lineNumberReader = new LineNumberReader(new FileReader(new File("storageTest.txt")));
		lineNumberReader.skip(Long.MAX_VALUE);  //Long.MAX_VALUE is more than 2 ExaBytes
		assertEquals(taskDepo.totalTaskCount(), lineNumberReader.getLineNumber());
		
		//Test boundary case where there is no task in task depository
		taskDepo.clearAll();
		assertEquals(EMPTY, taskDepo.totalTaskCount());
		storageTest.store();
		lineNumberReader = new LineNumberReader(new FileReader(new File("storageTest.txt")));
		lineNumberReader.skip(Long.MAX_VALUE);  //Long.MAX_VALUE is more than 2 ExaBytes
		assertEquals(taskDepo.totalTaskCount(), lineNumberReader.getLineNumber());
		lineNumberReader.close();
		
		//It is not feasible to test the other boundary case where the taskDepo takes up
		//the full memory allocated for it
	}
}
