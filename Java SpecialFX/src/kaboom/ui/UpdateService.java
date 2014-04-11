//@author A0099175N

package kaboom.ui;

import javafx.application.Platform;
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
    			if (Platform.isFxApplicationThread()) {
    				System.out.println("Running on FX thread!");
    			} else {
    				System.out.println("Running on background thread!");
    			}
    			++counter;
    			controllerInstance.updateTaskList();
    			return null;
    		}
    	};
    }
}
