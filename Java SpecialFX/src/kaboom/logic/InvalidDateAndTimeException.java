//@author A0073731J

package kaboom.logic;

public class InvalidDateAndTimeException extends Exception {
	String feedback = "Invalid Date And Time";
	InvalidDateAndTimeException (String feedback) {
		this.feedback = feedback;
	}
    public String toString(){ 
        return feedback;
     }
}
