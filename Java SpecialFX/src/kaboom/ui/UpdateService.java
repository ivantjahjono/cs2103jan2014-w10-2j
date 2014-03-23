package kaboom.ui;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

class UpdateService extends Service<Void> {
	
	MainWindow mainWindow;
	int counter;
	
	UpdateService (MainWindow window) {
		mainWindow = window;
		counter = 0;
	}
	
    @Override
    protected Task<Void> createTask() {
    	return new Task<Void>() {
    		@Override
    		protected Void call() throws Exception {
    			//System.out.println("Begin task");
    			++counter;
    			return null;
    		}
    	};
    }
}
