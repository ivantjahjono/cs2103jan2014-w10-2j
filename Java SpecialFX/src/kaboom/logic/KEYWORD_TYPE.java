package kaboom.logic;

public enum KEYWORD_TYPE {
	COMMAND (0),
	TASKID(1), TASKNAME(1), 
	MODIFIED_TASKNAME(2), 
	START_TIME(3), START_DATE(4), END_TIME(5), END_DATE(6),  DATE(3),
	PRIORITY(7),
	VIEWTYPE (8), SORT (8),
	INVALID (100), UNKNOWN (100);
	
	private final int value;
    private KEYWORD_TYPE(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
	
}
