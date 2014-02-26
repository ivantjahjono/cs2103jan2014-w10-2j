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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import java.awt.SystemColor;
import java.awt.FlowLayout;
import java.util.Vector;


public class KaboomGUI implements ActionListener {

	private JFrame frame;
	private JTextField txtEnterCommandHere;
	private JLabel lblFeedback;

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
	
	private void executeUi () {
		try {
			KaboomGUI window = new KaboomGUI();
			window.frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the application.
	 */
	public KaboomGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frame = new JFrame();
		frame.setBackground(UIManager.getColor("Button.darkShadow"));
		frame.setResizable(false);
		frame.setBounds(100, 100, 500, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblTaskMasterKaboom = new JLabel("TASK MASTER KABOOM");
		lblTaskMasterKaboom.setBounds(10, 0, 474, 29);
		lblTaskMasterKaboom.setHorizontalAlignment(SwingConstants.CENTER);
		lblTaskMasterKaboom.setFont(new Font("Tahoma", Font.BOLD, 20));
		frame.getContentPane().add(lblTaskMasterKaboom);
		
		txtEnterCommandHere = new JTextField();
		txtEnterCommandHere.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txtEnterCommandHere.setBounds(10, 231, 474, 29);
		txtEnterCommandHere.setText("Enter command here");
		frame.getContentPane().add(txtEnterCommandHere);
		txtEnterCommandHere.setColumns(10);
		txtEnterCommandHere.addActionListener(this);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 40, 474, 147);
		frame.getContentPane().add(scrollPane);
		
		JTable tasklist = new JTable();
		tasklist.setAutoscrolls(false);
		tasklist.setUpdateSelectionOnSort(false);
		tasklist.getTableHeader().setReorderingAllowed(true);
		tasklist.setFillsViewportHeight(true);
		tasklist.setShowHorizontalLines(false);
		tasklist.setShowVerticalLines(false);
		tasklist.setModel(new DefaultTableModel(
			new Object[][] {
				{new Integer(1), "Meeting", "1:00pm", "2:00pm", "***"},
				{new Integer(2), "CS 1101 Lecture", "3:00pm", "3:30pm", "**"},
				{new Integer(3), "Sleep", "6:00pm", "8:00pm", ""},
				{new Integer(4), "Breakfast", "4:00am", "5:30am", "*"},
				{new Integer(5), "Slack", "8:00am", "12:00pm", "**"},
			},
			new String[] {
				"Id", "Task Name", "Start Time", "End Time", "Priority"
			}
		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		tasklist.getColumnModel().getColumn(0).setResizable(false);
		tasklist.getColumnModel().getColumn(0).setPreferredWidth(24);
		tasklist.getColumnModel().getColumn(1).setResizable(false);
		tasklist.getColumnModel().getColumn(1).setPreferredWidth(150);
		tasklist.getColumnModel().getColumn(2).setResizable(false);
		tasklist.getColumnModel().getColumn(3).setResizable(false);
		tasklist.getColumnModel().getColumn(4).setResizable(false);
		tasklist.getColumnModel().getColumn(4).setPreferredWidth(45);
		tasklist.setRowSelectionAllowed(false);
		scrollPane.setViewportView(tasklist);
		tasklist.setBorder(null);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.setBackground(SystemColor.controlHighlight);
		panel.setBounds(10, 203, 474, 24);
		frame.getContentPane().add(panel);
		
		lblFeedback = new JLabel("Feedback");
		lblFeedback.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblFeedback.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblFeedback);
		lblFeedback.setForeground(UIManager.getColor("ToolBar.dockingForeground"));
		lblFeedback.setBackground(SystemColor.activeCaption);
		lblFeedback.setBounds(10, 203, 474, 24);
	}
	
	public void actionPerformed(ActionEvent e) {		
		String command = txtEnterCommandHere.getText();
		String feedback = TaskMasterKaboom.processCommand(command);
		
		resetCommandTextfield();
		updateFeedbackTextfield(feedback);
	}
	
	public void updateTasksDisplay (String feedback, Vector<TaskInfo> taskList) {
		updateFeedbackTextfield(feedback);
	}
	
	private void resetCommandTextfield () {
		txtEnterCommandHere.setText("");
	}
	
	private void updateFeedbackTextfield (String feedback) {
		lblFeedback.setText(feedback);
	}
}
