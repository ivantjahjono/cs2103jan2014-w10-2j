package kaboom.ui;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import kaboom.logic.TaskMasterKaboom;

class UpdateService extends Service<Void> {
	
	MainWindow mainWindow;
	int counter;
	TaskMasterKaboom controllerInstance;
	
	UpdateService (MainWindow window) {
		mainWindow = window;
		counter = 0;
		controllerInstance = TaskMasterKaboom.getInstance();
	}
	
    @Override
    protected Task<Void> createTask() {
    	return new Task<Void>() {
    		@Override
    		protected Void call() throws Exception {
    			//System.out.println("Begin task");
    			++counter;
    			controllerInstance.updateTaskList();
    			return null;
    		}
    	};
    }
}
