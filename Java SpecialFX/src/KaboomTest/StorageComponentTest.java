package KaboomTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
		StorageTest.class,
		HistoryTest.class,
		TaskListShopTest.class,
		})
public class StorageComponentTest {

}
