package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.SwingConstants;
import javax.swing.JTable;

import java.awt.Component;

import javax.swing.UIManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import java.awt.SystemColor;
import java.awt.FlowLayout;
import java.util.Vector;
import java.awt.Color;


public class KaboomGUI implements ActionListener, KeyListener {
	
	private JFrame frame;
	private JTextField txtEnterCommandHere;
	private JLabel feedbackLabel;
	private JTable tasklistDisplay;
	
	private String prevCommand;
	private String currentCommand;
	
	/**
	 * Launch the application.
	 */	
	public void runUi () {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				executeUi();
			}
		});
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	private void createAndRun () {
		KaboomGUI window = new KaboomGUI(); 
		window.initialize();
		window.runUi();
	}
	
	/**
	 *  Create the window and launch it.
	 */
	private void executeUi () {
		frame.setVisible(true);
	}

	/**
	 *  Initialize and create layout and content for the UI 
	 */
	public KaboomGUI() {
		//initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		createDisplayFrame();
		createApplicationTitle();
		JPanel currentDisplay = createHeaderContainer();
		createHeader(currentDisplay);
		
		createCommandTextfield();
		
		JScrollPane scrollPane = createPanelToHoldTable();
		createTaskDisplayTable(scrollPane);
		createDisplayFeedback();
	}

	private void createHeader(JPanel currentDisplay) {
		JLabel lblNewLabel = new JLabel("ALL TASKS");
		lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel.setForeground(Color.GRAY);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		currentDisplay.add(lblNewLabel);
	}

	private JPanel createHeaderContainer() {
		JPanel currentDisplay = new JPanel();
		currentDisplay.setBorder(null);
		FlowLayout flowLayout = (FlowLayout) currentDisplay.getLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		flowLayout.setAlignment(FlowLayout.LEFT);
		currentDisplay.setBounds(10, 27, 256, 19);
		frame.getContentPane().add(currentDisplay);
		return currentDisplay;
	}

	private void createDisplayFeedback() {
		JPanel feedback = new JPanel();
		FlowLayout fl_feedback = (FlowLayout) feedback.getLayout();
		fl_feedback.setAlignment(FlowLayout.LEFT);
		feedback.setAlignmentX(Component.LEFT_ALIGNMENT);
		feedback.setBackground(SystemColor.controlHighlight);
		feedback.setBounds(10, 260, 574, 24);
		frame.getContentPane().add(feedback);
		
		feedbackLabel = new JLabel("Feedback");
		feedbackLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		feedbackLabel.setHorizontalAlignment(SwingConstants.LEFT);
		feedbackLabel.setForeground(UIManager.getColor("ToolBar.dockingForeground"));
		feedbackLabel.setBackground(SystemColor.activeCaption);
		feedbackLabel.setBounds(10, 203, 474, 24);
		feedback.add(feedbackLabel);
	}

	private void createTaskDisplayTable(JScrollPane scrollPane) {
		tasklistDisplay = new JTable();
		tasklistDisplay.setShowHorizontalLines(false);
		tasklistDisplay.setShowVerticalLines(false);
		tasklistDisplay.setAutoscrolls(false);
		tasklistDisplay.setUpdateSelectionOnSort(false);
		tasklistDisplay.getTableHeader().setReorderingAllowed(true);
		tasklistDisplay.setFillsViewportHeight(true);
		tasklistDisplay.setModel(new DefaultTableModel(
			new Object[][] {
				{new Integer(1), "Meeting", "1:00 PM 29/02/2014", "2:00 PM29/02/2014", "***"},
				{new Integer(2), "CS 1101 Lecture", "3:00 PM 29/02/2014", "3:30 PM 29/02/2014", "**"},
				{new Integer(3), "Sleep", "6:00 PM 29/02/2014", "8:00 PM 29/02/2014", ""},
				{new Integer(4), "Breakfast", "4:00 AM 01/03/2014", "5:30 AM 01/03/2014", "*"},
				{new Integer(5), "Slack", "8:00 AM 01/03/2014", "12:00 PM 01/03/2014", "**"},
				{new Integer(6), "Slack", "8:00 AM 01/03/2014", "12:00 PM 01/03/2014", "**"},
				{new Integer(7), "Slack", "12:00 AM 01/03/2014", "1:00 PM 01/03/2014", "***"},
				{new Integer(8), "Slack", "1:00 AM 01/03/2014", "2:00 PM 01/03/2014", "**"},
				{new Integer(9), "Slack", "2:00 AM 01/03/2014", "3:00 PM 01/03/2014", "**"},
				{new Integer(10), "Slack", "3:00 AM 01/03/2014", "4:00 PM 01/03/2014", "***"},
			},
			new String[] {
				"Id", "Task Name", "Start Time", "End Time", "Priority"
			}
		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			Class[] columnTypes = new Class[] {
				Integer.class, String.class, String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false, false, false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		tasklistDisplay.getColumnModel().getColumn(0).setResizable(false);
		tasklistDisplay.getColumnModel().getColumn(0).setPreferredWidth(21);
		tasklistDisplay.getColumnModel().getColumn(1).setResizable(false);
		tasklistDisplay.getColumnModel().getColumn(1).setPreferredWidth(190);
		tasklistDisplay.getColumnModel().getColumn(2).setResizable(false);
		tasklistDisplay.getColumnModel().getColumn(2).setPreferredWidth(120);
		tasklistDisplay.getColumnModel().getColumn(3).setResizable(false);
		tasklistDisplay.getColumnModel().getColumn(3).setPreferredWidth(120);
		tasklistDisplay.getColumnModel().getColumn(4).setResizable(false);
		tasklistDisplay.getColumnModel().getColumn(4).setPreferredWidth(45);
		tasklistDisplay.setRowSelectionAllowed(false);
		tasklistDisplay.setBorder(null);
		scrollPane.setViewportView(tasklistDisplay);
	}

	private JScrollPane createPanelToHoldTable() {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 57, 574, 192);
		frame.getContentPane().add(scrollPane);
		return scrollPane;
	}

	private void createCommandTextfield() {
		txtEnterCommandHere = new JTextField();
		txtEnterCommandHere.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txtEnterCommandHere.setBounds(10, 295, 574, 29);
		txtEnterCommandHere.setText("Enter command here");
		txtEnterCommandHere.setColumns(10);
		txtEnterCommandHere.addActionListener(this);
		txtEnterCommandHere.addKeyListener(this);
		frame.getContentPane().add(txtEnterCommandHere);
	}

	private void createApplicationTitle() {
		JLabel lblTaskMasterKaboom = new JLabel("TASK MASTER KABOOM");
		lblTaskMasterKaboom.setBounds(10, 0, 474, 29);
		lblTaskMasterKaboom.setHorizontalAlignment(SwingConstants.LEFT);
		lblTaskMasterKaboom.setFont(new Font("Tahoma", Font.BOLD, 20));
		frame.getContentPane().add(lblTaskMasterKaboom);
	}

	private void createDisplayFrame() {
		frame = new JFrame();
		frame.setBackground(UIManager.getColor("Button.darkShadow"));
		frame.setResizable(false);
		frame.setBounds(100, 100, 600, 365);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
	}
	
	public void actionPerformed(ActionEvent e) {
		String feedback = "You didn't type anything commander.";
		String command = txtEnterCommandHere.getText();
		
		if (!command.equals("")) {
			TaskMasterKaboom.processCommand(command);
			resetCommandTextfield();
			prevCommand = command;
		}
	}
	
	public void keyTyped(KeyEvent e) {
		 //System.out.println(e.getKeyCode() + "KEY TYPED: ");
    }
	
	public void keyPressed(KeyEvent e) {
        //System.out.println(e.getKeyCode() + "KEY PRESSED: ");
    }
	
	public void keyReleased(KeyEvent e) {
		// Update text field to previous command
		
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			currentCommand = txtEnterCommandHere.getText();
			txtEnterCommandHere.setText(prevCommand);
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN){
			txtEnterCommandHere.setText(currentCommand);
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			frame.dispose();
		}
		 //System.out.println(e.getKeyChar() + "KEY RELEASED: ");
    }
	
	public void showUpdatedUi () {
		String feedback = DisplayData.getInstance().getFeedbackMessage();
		Vector<TaskInfoDisplay> displayInfo = DisplayData.getInstance().getTaskDisplay();
		
		updateUiDisplay(feedback, displayInfo);
	}
	
	public void updateUiDisplay (String feedback, Vector<TaskInfoDisplay> taskList) {
		updateFeedbackTextfield(feedback);
		updateTaskDisplay(taskList);
	}
	
	private void updateTaskDisplay (Vector<TaskInfoDisplay> taskList) {
		updateAllTaskDisplayRows(taskList);
		removeAllRowsAfterIndex(taskList.size());
	}

	private void updateAllTaskDisplayRows(Vector<TaskInfoDisplay> taskList) {
		for (int i = 0; i < taskList.size(); i++) {
			TaskInfoDisplay infoToDisplay = taskList.get(i);
			updateThisRowData(infoToDisplay);
		}
	}
	
	private void updateThisRowData (TaskInfoDisplay rowData) {
		int rowIndex = rowData.getTaskId()-1;
		
		// Does the row exist ?
		if (doesRowExist(rowIndex)) {
			addNewRow();
		}
		
		tasklistDisplay.setValueAt(rowData.getTaskId(), rowIndex, 0);
		tasklistDisplay.setValueAt(rowData.getTaskName(), rowIndex, 1);
		tasklistDisplay.setValueAt(rowData.getStartDate(), rowIndex, 2);
		tasklistDisplay.setValueAt(rowData.getEndDate(), rowIndex, 3);
		tasklistDisplay.setValueAt(rowData.getImportanceLevel(), rowIndex, 4);
	}

	private void addNewRow() {
		DefaultTableModel tempModelTable = (DefaultTableModel)tasklistDisplay.getModel();
		tempModelTable.addRow(new Vector<Object>());
	}

	private boolean doesRowExist(int rowIndex) {
		return tasklistDisplay.getRowCount() <= rowIndex;
	}
	
	private void removeAllRowsAfterIndex (int index) {
		while (tasklistDisplay.getRowCount() > index) {
			removeThisRowForTaskDisplay(index);
		}
	}
	
	private void removeThisRowForTaskDisplay (int rowIndex) {
		DefaultTableModel currentTableModal = (DefaultTableModel)tasklistDisplay.getModel();
		currentTableModal.removeRow(rowIndex);
	}
	
	private void resetCommandTextfield () {
		txtEnterCommandHere.setText("");
	}
	
	private void updateFeedbackTextfield (String feedback) {
		feedbackLabel.setText(feedback);
	}
}
