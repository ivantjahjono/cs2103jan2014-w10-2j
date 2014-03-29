package KaboomTest;

import static org.junit.Assert.*;

import java.io.LineNumberReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

import kaboom.storage.Storage;
import kaboom.storage.TaskListShop;

import org.junit.Before;
import org.junit.Test;

public class StorageTest {
	
	TaskListShop allTasks;
	Storage storageTest;
	
	@Before
	public void initailize() {
		allTasks = TaskListShop.getInstance();
		storageTest = new Storage("StorageTest.txt");
		storageTest.load();
	}
	
	@Test
	public void testLoad() throws IOException {
		LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(new File("storageTest.txt")));
		lineNumberReader.skip(Long.MAX_VALUE);  //Long.MAX_VALUE is more than 2 ExaBytes
		assertEquals(allTasks.shopSize(), lineNumberReader.getLineNumber()*2);
		lineNumberReader.close();
	}

	@Test
	public void testStore() throws IOException {
		storageTest.store();
		LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(new File("storageTest.txt")));
		lineNumberReader.skip(Long.MAX_VALUE);  //Long.MAX_VALUE is more than 2 ExaBytes
		assertEquals(allTasks.shopSize(), lineNumberReader.getLineNumber());
		lineNumberReader.close();
	}
}
