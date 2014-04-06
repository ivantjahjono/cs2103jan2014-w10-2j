package kaboom.ui;

import java.util.Collections;

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
 	private Rectangle 	backlitBox;
 	
 	
 	public TaskUiContainer () {
 		createTaskUi();
 	}
 	
 	public void createTaskUi () {
 		task = new Pane();
 		task.setPrefSize(512, 40);
 		
 		taskid = new Label();
 		taskid.setPrefSize(50, 26.6);
 		taskid.setLayoutX(-59);
 		taskid.setLayoutY(5);
 		taskid.getStyleClass().add("taskid-label");
 		task.getChildren().add(taskid);
 		
 		taskname = new Label();
 		taskname.setPrefSize(479, 22);
 		taskname.setLayoutX(21);
 		taskname.setLayoutY(1);
 		taskname.getStyleClass().add("taskname-label");
 		task.getChildren().add(taskname);
 		
 		statusbar = new Rectangle();
 		statusbar.setWidth(5);
 		statusbar.setHeight(40);
 		task.getChildren().add(statusbar);
 		
 		datetime = new Label();
 		datetime.setPrefSize(302, 19);
 		datetime.setLayoutX(21);
 		datetime.setLayoutY(19);
 		datetime.getStyleClass().add("taskdatetime-label");
 		task.getChildren().add(datetime);
 		
 		priority  = new Label();
 		priority.setPrefSize(80, 26.6);
 		priority.setLayoutX(418);
 		priority.setLayoutY(16);
 		priority.getStyleClass().add("taskpriority-label");
 		task.getChildren().add(priority);
 		
 		backlitBox = new Rectangle();
 		backlitBox.setWidth(512);
 		backlitBox.setHeight(40);
 		backlitBox.getStyleClass().add("taskbox-rectangle");
 		task.getChildren().add(backlitBox);
 	}
 	
 	public Pane getPaneContainer () {
 		return task;
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
 		
 		statusbar.getStyleClass().removeAll(Collections.singleton("isNotExpired"));
 		statusbar.getStyleClass().removeAll(Collections.singleton("isExpired"));
 		statusbar.getStyleClass().removeAll(Collections.singleton("isComplete"));
 		statusbar.getStyleClass().removeAll(Collections.singleton("isEmpty"));
 		statusbar.getStyleClass().removeAll(Collections.singleton("isRecent"));
 		
 		if (info.isExpired()) {
 			statusbar.getStyleClass().add("isExpired");
 		} else if (info.isDone()) {
 			statusbar.getStyleClass().add("isComplete");
 		} else if (info.isRecent()) {
 			statusbar.getStyleClass().add("isRecent");
 		} else {
 			statusbar.getStyleClass().add("isNotExpired");
 		}
 	}
}
