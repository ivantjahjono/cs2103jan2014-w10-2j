package kaboom.logic;

public class FormatIdentify {
	int startIndex;
	int endIndex;
	
	KEYWORD_TYPE type;
	String stringText;
	
	public FormatIdentify () {
		startIndex = 0;
		endIndex = 0;
		type = KEYWORD_TYPE.INVALID;
		stringText = "";
	}
	
	public void setCommandStringFormat (String text) {
		stringText = text;
	}
	
	public String getCommandStringFormat () {
		return stringText;
	}
	
	public void setStartIndex (int index) {
		startIndex = index;
	}
	
	public void setEndIndex (int index) {
		endIndex = index;
	}
	
	public void setType (KEYWORD_TYPE newType) {
		type = newType;
	}
	
	public int getStartIndex () {
		return startIndex;
	}
	
	public int getEndIndex () {
		return endIndex;
	}
	
	public KEYWORD_TYPE getType () {
		return type;
	}
}
