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
		
	public CommandHelp () {
		commandType = COMMAND_TYPE.HELP;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.HELP
		};
	}

	public Result execute() {
		assert taskListShop != null;
		
		// Read from hashtable
		String helpType = infoTable.get(KEYWORD_TYPE.HELP);
		HELP_STATE helpState = HELP_STATE.INVALID;
		
		if (helpType == null)  {
			helpState = HELP_STATE.MAIN;
		} else {		
			// convert to help type
			switch (helpType) {
				case HELP_TYPE_ADD:
					helpState = HELP_STATE.ADD;
					break;
					
				case HELP_TYPE_DELETE:
					helpState = HELP_STATE.DELETE;
					break;
					
				case HELP_TYPE_MODIFY:
					helpState = HELP_STATE.MODIFY;
					break;
					
				case HELP_TYPE_COMPLETE:
					helpState = HELP_STATE.COMPLETE;
					break;
					
				case HELP_TYPE_SEARCH:
					helpState = HELP_STATE.SEARCH;
					break;
					
				case HELP_TYPE_VIEW:
					helpState = HELP_STATE.VIEW;
					break;
					
				case HELP_TYPE_CLOSE:
					helpState = HELP_STATE.CLOSE;
					break;
					
				default:
					break;
			}
		}
		
		Result currentResult = createResult("");
		currentResult.setHelpState(helpState);
		
		return currentResult;
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
