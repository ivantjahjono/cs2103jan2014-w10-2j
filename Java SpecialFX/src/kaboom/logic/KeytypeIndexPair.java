package kaboom.logic;

class KeytypeIndexPair {
	KEYWORD_TYPE type;
	int indexPosition;
	
	public KeytypeIndexPair () {
		type = KEYWORD_TYPE.INVALID;
		indexPosition = -1;
	}
	
	public KeytypeIndexPair (KEYWORD_TYPE newType, int position) {
		setType(newType);
		setIndexPosition(position);
	}
	
	public void setType (KEYWORD_TYPE newType) {
		type = newType;
	}
	
	public void setIndexPosition (int position) {
		indexPosition = position;
	}
	
	public KEYWORD_TYPE getType () {
		return type;
	}
	
	public int getIndexPosition () {
		return  indexPosition;
	}
	
	public String toString () {
		return "[" + type + ", " + indexPosition + "]";
	}
}