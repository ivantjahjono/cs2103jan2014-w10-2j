package KaboomTest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({ 
		CommandTest.class, 
		DateAndTimeFormatTest.class,
		TextParserTest.class,
		StorageTest.class,
		HistoryTest.class,
		TaskListShopTest.class,
		})
public class Stage2 {


}
