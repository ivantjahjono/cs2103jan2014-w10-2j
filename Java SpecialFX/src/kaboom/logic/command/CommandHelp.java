//@author A0099863H
package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.shared.FormatIdentify;
import kaboom.shared.HELP_STATE;
import kaboom.shared.KEYWORD_TYPE;
import kaboom.shared.Result;

public class CommandHelp extends Command {
	private final String HELP_TYPE_ADD 		= "add";
	private final String HELP_TYPE_DELETE 	= "delete";
	private final String HELP_TYPE_MODIFY 	= "modify";
	private final String HELP_TYPE_COMPLETE = "complete";
	private final String HELP_TYPE_SEARCH 	= "search";
	private final String HELP_TYPE_VIEW 	= "view";
	private final String HELP_TYPE_CLOSE 	= "close";
	
	private final String HELP_INVALID_COMMAND = "Invalid help command!";
		
	public CommandHelp () {
		commandType = COMMAND_TYPE.HELP;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.HELP
		};
	}

	public Result execute() {
		assert taskListShop != null;
		
		if (infoTable.containsKey(KEYWORD_TYPE.INVALID)) {
			return createResult(HELP_INVALID_COMMAND);
		}
		
		String helpType = infoTable.get(KEYWORD_TYPE.HELP);
		HELP_STATE helpState = HELP_STATE.INVALID;
		
		helpState = getHelpStateBasedOnKeywords(helpType);
		
		Result currentResult = createResult("");
		currentResult.setHelpState(helpState);
		
		return currentResult;
	}

	private HELP_STATE getHelpStateBasedOnKeywords(String helpType) {
		if (helpType == null)  {
			return HELP_STATE.MAIN;
		} else {
			// convert to help type
			switch (helpType) {
				case HELP_TYPE_ADD:
					return HELP_STATE.ADD;
					
				case HELP_TYPE_DELETE:
					return HELP_STATE.DELETE;
					
				case HELP_TYPE_MODIFY:
					return HELP_STATE.MODIFY;
					
				case HELP_TYPE_COMPLETE:
					return HELP_STATE.COMPLETE;
					
				case HELP_TYPE_SEARCH:
					return HELP_STATE.SEARCH;
					
				case HELP_TYPE_VIEW:
					return HELP_STATE.VIEW;
					
				case HELP_TYPE_CLOSE:
					return HELP_STATE.CLOSE;
					
				default:
					return null;
			}
		}
	}
	
	public boolean undo () {
		boolean isUndoSuccessful = false;
		return isUndoSuccessful;
	}
	
	public boolean parseInfo(String info, Vector<FormatIdentify> indexList) {
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = updateFormatList(info, indexList);
		updateFormatListBasedOnHashtable(indexList, taskInformationTable);
		
		if (taskInformationTable.containsKey(KEYWORD_TYPE.INVALID)) {
			return false;
		}
		
		return true;
	}
}
