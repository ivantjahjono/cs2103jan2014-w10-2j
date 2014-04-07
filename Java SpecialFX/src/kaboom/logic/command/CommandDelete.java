package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.ui.TaskView;


public class CommandDelete extends Command {

	private final String MESSAGE_COMMAND_DELETE_SUCCESS = "<%1$s> deleted. 1 less work to do :D";
	private final String MESSAGE_COMMAND_DELETE_FAIL = "Aww... fail to delete <%1$s>.";
	private final String MESSAGE_COMMAND_DELETE_INVALID = "Enter a taskname or task id, please ?";
	private final String MESSAGE_COMMAND_DELETE_NO_SUCH_TASK = "<%1$s> does not exist...";

	TaskInfo prevTask;
	TaskView taskView;

	public CommandDelete () {
		commandType = COMMAND_TYPE.DELETE;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.TASKID,
				KEYWORD_TYPE.TASKNAME
		};
		taskView = TaskView.getInstance();  
	}

	public Result execute() {
		assert taskListShop != null;
		
		//set task id;
		String taskId = infoTable.get(KEYWORD_TYPE.TASKID);
		String taskName = infoTable.get(KEYWORD_TYPE.TASKNAME);
		String commandFeedback = "";

		//get tasktodelete
		if (taskId != null) {
			int taskIdInteger = Integer.parseInt(taskId);
			if (taskView.getCurrentViewID().size() >= taskIdInteger) {
				int index = taskView.getIndexFromView(taskIdInteger-1);
				prevTask = taskListShop.getTaskByID(index);
			}
		} else if (taskName != null){
			//detect clash
			int taskCount = taskListShop.numOfTasksWithSimilarNames(taskName);
			if (taskCount > 1) {
				Command search = new CommandSearch();
				search.storeTaskInfo(infoTable);
				return search.execute();
			}
			else if (taskCount == 1) {
				prevTask = taskListShop.getTaskByName(taskName);
			}
			else {
				commandFeedback = MESSAGE_COMMAND_DELETE_NO_SUCH_TASK;
				return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
			}
		} else {
			commandFeedback = MESSAGE_COMMAND_DELETE_INVALID;
			return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
		}
		
		taskListShop.removeTaskByName(prevTask.getTaskName());
		taskView.deleteInView(prevTask);
		commandFeedback = String.format(MESSAGE_COMMAND_DELETE_SUCCESS, prevTask.getTaskName());
		addCommandToHistory ();
		return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
	}

	public boolean undo () {
		if (taskListShop.addTaskToList(prevTask)) {
			taskView.addToView(prevTask);
			return true;
		}
		return false;
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
