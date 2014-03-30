package kaboom.logic;

public class FormatIdentify {
	KEYWORD_TYPE type;
	String stringText;
	
	public FormatIdentify () {
		type = KEYWORD_TYPE.INVALID;
		stringText = "";
	}
	
	public void setCommandStringFormat (String text) {
		stringText = text;
	}
	
	public String getCommandStringFormat () {
		return stringText;
	}
	
	public void setType (KEYWORD_TYPE newType) {
		type = newType;
	}
	
	public KEYWORD_TYPE getType () {
		return type;
	}
}
