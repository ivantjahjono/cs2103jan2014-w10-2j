//@author A0096670W

package KaboomTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	TaskManagerTest.class,
	HistoryTest.class,
	TaskDepositoryTest.class,
	StorageTest.class
})
public class StorageComponentTest {

}
