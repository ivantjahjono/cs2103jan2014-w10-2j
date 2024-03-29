//@author A0073731J
package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.shared.DISPLAY_STATE;
import kaboom.shared.FormatIdentify;
import kaboom.shared.KEYWORD_TYPE;
import kaboom.shared.Result;

public class CommandView extends Command{
	private final String KEYWORD_TODAY 		= "today";
	private final String KEYWORD_FUTURE		= "future";
	private final String KEYWORD_TIMELESS 	= "timeless";
	private final String KEYWORD_EXPIRED 	= "expired";
	private final String KEYWORD_ARCHIVE 	= "archive";
	
	private final String MESSAGE_VIEW_TODAY 	= "Viewing all the tasks for today";
	private final String MESSAGE_VIEW_TIMELESS 	= "Viewing timeless tasks";
	private final String MESSAGE_VIEW_EXPIRED 	= "Viewing expired tasks";
	private final String MESSAGE_VIEW_FUTURE 	= "Viewing upcoming tasks";
	private final String MESSAGE_VIEW_ARCHIVE 	= "Viewing completed tasks";
	private final String MESSAGE_VIEW_INVALID 	= "Invalid View Mode. Might want to use <help view>";
	
	String 			viewType;
	DISPLAY_STATE 	stateToSet;
	
	public CommandView () {
		commandType = COMMAND_TYPE.VIEW;
		stateToSet = DISPLAY_STATE.INVALID;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.VIEWTYPE	
		};
	}

	public Result execute() {
		assert taskManager != null;

		storeViewVariables();
		String feedback = "";
		if(viewType == null) {
			viewType = "Nothing to view";
		}
		feedback = determineFeedBackForViewState();
		return createResult(feedback, stateToSet, null);
	}

	private DISPLAY_STATE determineDisplayState(String viewType) {
		switch(viewType) {
			case KEYWORD_TODAY:
				return DISPLAY_STATE.TODAY;
			case KEYWORD_FUTURE:
				return DISPLAY_STATE.FUTURE;
			case KEYWORD_TIMELESS:
				return DISPLAY_STATE.TIMELESS;
			case KEYWORD_EXPIRED:
				return DISPLAY_STATE.EXPIRED;
			case KEYWORD_ARCHIVE:
				return DISPLAY_STATE.ARCHIVE;
			default:
				return DISPLAY_STATE.INVALID;
		}
	}
	
	public DISPLAY_STATE getDisplayState () {
		return stateToSet;
	}
	
	private void storeViewVariables() {
		viewType = infoTable.get(KEYWORD_TYPE.VIEWTYPE);
		if (viewType == null) {
			return;
		}
		
		if (infoTable.containsKey(KEYWORD_TYPE.INVALID)) {
			viewType += infoTable.get(KEYWORD_TYPE.VIEWTYPE);
		}
		
		stateToSet = determineDisplayState(viewType);
	}
	
	public boolean parseInfo(String info, Vector<FormatIdentify> indexList) {
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = updateFormatList(info, indexList);
		updateFormatListBasedOnHashtable(indexList, taskInformationTable);
		
		if (taskInformationTable.containsKey(KEYWORD_TYPE.INVALID)) {
			return false;
		}
		
		return true;
	}
	
	private String determineFeedBackForViewState() {
		String feedback;
		switch(stateToSet) {
			case TODAY:
				feedback = MESSAGE_VIEW_TODAY;
				break;
			case FUTURE:
				feedback = MESSAGE_VIEW_FUTURE;
				break;
			case TIMELESS:
				feedback = MESSAGE_VIEW_TIMELESS;
				break;
			case EXPIRED:
				feedback = MESSAGE_VIEW_EXPIRED;
				break;
			case ARCHIVE:
				feedback = MESSAGE_VIEW_ARCHIVE;
				break;
			default:
				feedback = MESSAGE_VIEW_INVALID;
		}
		return feedback;
	}
}
