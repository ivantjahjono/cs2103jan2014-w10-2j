package kaboom.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import kaboom.logic.TaskInfoDisplay;

public class TaskUiContainer {
 	private Pane 		task;
 	private Label 		taskid;
 	private Label 		taskname;
 	private Rectangle 	statusbar;
 	private Label 		datetime;
 	private Label 		priority;
 	
 	
 	public TaskUiContainer () {
 	}
 	
 	public void setupContainer (Pane taskLink, Label id, Label name, Rectangle status, Label date, Label priorityLink) {
 		task = taskLink;
 		taskid = id;
 		taskname = name;
 		statusbar = status;
 		datetime = date;
 		priority = priorityLink;
 	}
 	
 	public void setVisibleFlag (boolean flag) {
 		task.setVisible(flag);
 	}
 	
 	public void updateWithTaskDisplay (TaskInfoDisplay info) {
 		taskid.setText(""+info.getTaskId());
 		taskname.setText(info.getTaskName());
 		datetime.setText(info.getStartDate());
 		priority.setText(info.getImportanceLevel());
 	}
}
