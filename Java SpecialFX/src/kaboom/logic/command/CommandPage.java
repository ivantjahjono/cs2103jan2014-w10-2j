//@author A0099175N
package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.shared.FormatIdentify;
import kaboom.shared.KEYWORD_TYPE;
import kaboom.shared.Result;

public class CommandPage extends Command {
	private final String KEYWORD_NEXT_PAGE 		= "next";
	private final String KEYWORD_PREV_PAGE		= "prev";
	
	private final String INVALID_PAGE_COMMAND_MESSAGE		= "No such page command. Use <help page> for help.";
	
	public CommandPage () {
		commandType = COMMAND_TYPE.PAGE;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.PAGE	
		};
	}

	public Result execute() {
		assert taskView != null;
		
		String pageInfo = infoTable.get(KEYWORD_TYPE.PAGE);
		if (pageInfo == null || infoTable.containsKey(KEYWORD_TYPE.INVALID)) {
			return createResult(INVALID_PAGE_COMMAND_MESSAGE);
		}
		
		String feedback = "";
		Result commandResult = createResult(feedback);
		switch (pageInfo) {
			case KEYWORD_NEXT_PAGE:
				commandResult.setGoToNextPage(true);
				break;
				
			case KEYWORD_PREV_PAGE:
				commandResult.setGoToPrevPage(true);
				break;
				
			default:
				int pageNumber = Integer.parseInt(pageInfo);
				commandResult.setPageToGoTo(pageNumber);
				break;
		}
		
		return commandResult;
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
