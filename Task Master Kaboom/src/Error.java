/*
 * Error class for TASK MASTER KABOOOOOOOOM
 */

public class Error {
	
	//Text Parser Errors
	private static final String MESSAGE_INVALID_INPUT_COMMAND = "INVALID COMMAND!";
	private static final String MESSAGE_INVALID_INPUT_DATE_FORMAT = "INVALID DATE FORMAT!";
	private static final String MESSAGE_INVALID_INPUT_TASKNAME = "INVALID TASKNAME!";
	
	//Command Errors
	
	//Memory Errors
	
	//Storage Errors
	
	
	enum ERROR_TYPE {
		INVALID_INPUT_COMMAND, INVALID_INPUT_DATE_FORMAT, INVALID_INPUT_TASKNAME 
	}

	
	ERROR_TYPE errorType;
	
	
	public Error () {
		//empty constructor
	}
	
	public Error (ERROR_TYPE errorType) {
		this.errorType = errorType;
	}
	
	public void setErrorType (ERROR_TYPE errorType) {
		this.errorType = errorType;
	}
	
	public boolean hasError () {
		if (errorType == null) {
			return false;
		}
		return true;
	}
	
	public String getErrorMessage () {
		switch (errorType) {
		case INVALID_INPUT_COMMAND:
			return MESSAGE_INVALID_INPUT_COMMAND;
		case INVALID_INPUT_DATE_FORMAT:
			return MESSAGE_INVALID_INPUT_DATE_FORMAT;
		case INVALID_INPUT_TASKNAME:
			return MESSAGE_INVALID_INPUT_TASKNAME;
		default:
			return null;
		}
	}
	
}
