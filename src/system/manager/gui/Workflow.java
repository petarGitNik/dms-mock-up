package system.manager.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import system.manager.engine.ChangeTable;
import system.manager.engine.ComboBoxSelectionList;
import system.manager.engine.ComboItem;
import system.manager.engine.Document;
import system.manager.engine.User;
import system.manager.engine.WorkflowEngine;

public class Workflow extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private WorkflowEngine engine = new WorkflowEngine();
	
	private int WorkflowTypeId;
	private int DesktopId;
	private int EditorId;
	private int PublisherId;
	
	private ImageIcon _approve = new ImageIcon("./icons/icon_approve.png");
	private ImageIcon _reject = new ImageIcon("./icons/icon_reject.png");
	private ImageIcon _cancel = new ImageIcon("./icons/icon_cancel.png");

	/**
	 * Workflow(User user, int docID) - Constructor
	 * @param user - User object of the user who is currently using App (current session user) 
	 * @param _doc_id - Document ID (this is retrieved from the table)
	 */
	public Workflow(Appwindow appwindow, User user, JScrollPane dashPane, JScrollPane changePane, int _doc_id) {
		if (engine.workflowExists(_doc_id)) {
			engine.setCurrentActiveState(engine.retrieveActiveState(_doc_id));
			if (engine.isFinalStateActive()) {
				JOptionPane.showMessageDialog(null, "This workflow is finished.");
				dispose();
			} else {
				if (user.permisionForActiveState(engine.retrieveActiveState(_doc_id))) {
					initialize(appwindow, user, dashPane, changePane, _doc_id);
					setVisible(true);
				} else {
					JOptionPane.showMessageDialog(null, "You don't have permission to view this process at the moment. Check your privilege. (ErrorID: cis)");
					dispose();
				}	
			}
		} else {
			if (JOptionPane.showConfirmDialog(null, "Are you sure you want to add this document to workflow?", "Add to workflow?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				engine.addToWorkflow(user, _doc_id);
				refreshChangeTables(appwindow, user, dashPane, changePane);
				initialize(appwindow, user, dashPane, changePane, _doc_id);
				setVisible(true);
			} else {
				dispose();
			}	
		}
	}
	
	public void initialize(Appwindow appwindow, User user, JScrollPane dashPane, JScrollPane changePane, int _doc_id) {
		setModal(true);
		setBounds(100, 100, 975, 755);
		setTitle(retrieveTitleBarText(_doc_id));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(5, 5));
		
		Document doc = new Document();
		
		//Option panel
		JPanel optionPanel = new JPanel();
		getContentPane().add(optionPanel, BorderLayout.NORTH);
		
		GridLayout optionLayout = new GridLayout(4,4);
		optionPanel.setLayout(optionLayout);
		
		JLabel authorName = new JLabel("Author name:");
		JTextField txtUserName = new JTextField(doc.getDocumentAuthorName(_doc_id), 20);
		txtUserName.setEditable(false);
		
		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		JButton btnAccept = new JButton("Accept");
		btnAccept.setIcon(_approve);
		JButton btnReject = new JButton("Reject");
		btnReject.setIcon(_reject);
		panelButtons.add(btnAccept);
		panelButtons.add(btnReject);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setIcon(_cancel);
		
		JLabel lblDesktopPublisher = new JLabel("Desktop Publisher:");
		ComboBoxSelectionList cmbslDekstopPublisher = new ComboBoxSelectionList("user_id", "Desktop Publisher");
		Vector<ComboItem> cmbvecDekstopPublisher = cmbslDekstopPublisher.getSelectionList();
		@SuppressWarnings({ "unchecked", "rawtypes" })
		JComboBox cmbDesktopPublisher = new JComboBox(cmbvecDekstopPublisher);
		
		JLabel lblSelectWorkflos = new JLabel("Workflow type:");
		ComboBoxSelectionList cmbslWorkflow = new ComboBoxSelectionList("workflow_type_id");
		Vector<ComboItem> cmbvecWorkflow = cmbslWorkflow.getSelectionList();
		@SuppressWarnings({ "unchecked", "rawtypes" })
		JComboBox cmbSelectWorkflow = new JComboBox(cmbvecWorkflow);
		
		JLabel lblEditor = new JLabel("Editor:");
		ComboBoxSelectionList cmbslEditor = new ComboBoxSelectionList("user_id", "Editor");
		Vector<ComboItem> cmbvecEditor = cmbslEditor.getSelectionList();
		@SuppressWarnings({ "unchecked", "rawtypes" })
		JComboBox cmbSelectEditor = new JComboBox(cmbvecEditor);
		
		JLabel lblDocumentName = new JLabel("Document name:");
		doc.setDocId(_doc_id);
		doc.setDocumentName();
		JTextField txtDocumentName = new JTextField(doc.getDocumentName(), 20); //OVDE DODATI DOC NAME
		txtDocumentName.setEditable(false);
		
		JLabel lblPublisher = new JLabel("Publisher:");
		ComboBoxSelectionList cmbslPublisher = new ComboBoxSelectionList("user_id", "Admin");
		Vector<ComboItem> cmbvecPublisher = cmbslPublisher.getSelectionList();
		@SuppressWarnings({ "unchecked", "rawtypes" })
		JComboBox cmbSelectPublisher = new JComboBox(engine.removeAdminIfAuthor(cmbvecPublisher, _doc_id));

		JButton btnViewProcess = new JButton("View process");
		
		//First row
		optionPanel.add(authorName);
		optionPanel.add(txtUserName);
		optionPanel.add(btnAccept);
		optionPanel.add(btnReject);
		//Second row
		optionPanel.add(lblDesktopPublisher);
		optionPanel.add(cmbDesktopPublisher);
		optionPanel.add(lblSelectWorkflos);
		optionPanel.add(cmbSelectWorkflow);
		//Third row
		optionPanel.add(lblEditor);
		optionPanel.add(cmbSelectEditor);
		optionPanel.add(lblDocumentName);
		optionPanel.add(txtDocumentName);
		//Fourth row
		optionPanel.add(lblPublisher);
		optionPanel.add(cmbSelectPublisher);
		optionPanel.add(btnViewProcess);
		optionPanel.add(btnCancel);
		
		//Description panel
		JPanel descriptionPanel = new JPanel();
		getContentPane().add(descriptionPanel, BorderLayout.CENTER);
		descriptionPanel.setLayout(new BorderLayout(5,5));
		
		JLabel lblDescription = new JLabel("Description:");
		descriptionPanel.add(lblDescription, BorderLayout.NORTH);
		
		//Attachments label is not yet visible, because it does not have db support
		JLabel lblAttachments = new JLabel("Attachments:");
		lblAttachments.setVisible(false);
		descriptionPanel.add(lblAttachments, BorderLayout.SOUTH);
		
		JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		descriptionPanel.add(scrollPane, BorderLayout.CENTER);
		
		engine.setCurrentActiveState(engine.retrieveActiveState(_doc_id));
		JTextArea textArea = new JTextArea(engine.retrieveStateTemplate(), 0,0);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		scrollPane.setViewportView(textArea);
		
		//Notes panel
		JPanel pnlNotes = new JPanel();
		pnlNotes.setLayout(new BorderLayout(5,5));
		
		JLabel lblNotes = new JLabel("Last note:");
		pnlNotes.add(lblNotes, BorderLayout.NORTH);
		
		JScrollPane scrollNotePane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pnlNotes.add(scrollNotePane, BorderLayout.CENTER);
		
		JEditorPane noteArea = new JEditorPane();
		noteArea.setPreferredSize(new Dimension(250, getHeight()));
		noteArea.setContentType("text/html");
		noteArea.setEditable(false);
		noteArea.setText(engine.retrieveLastProjectNote());
		scrollNotePane.setSize(250, getHeight());
		scrollNotePane.setViewportView(noteArea);
		
		JButton btnNotes = new JButton("View All Notes");
		btnNotes.setEnabled(false);
		pnlNotes.add(btnNotes, BorderLayout.SOUTH);
		
		descriptionPanel.add(pnlNotes, BorderLayout.EAST);
		
		//Workflow and Role Permissions
		engine.setCurrentActiveState(engine.retrieveActiveState(_doc_id));

		btnReject.setEnabled(!engine.isDesktopPublishingStateActive() && !engine.isCloseStateActive());
		
		cmbDesktopPublisher.setEnabled(engine.isSubmissionStateActive());
		cmbSelectEditor.setEnabled(engine.isSubmissionStateActive());
		if (!engine.isSubmissionStateActive()) {
			cmbDesktopPublisher.setSelectedItem(engine.retrieveDefaultSelection(cmbvecDekstopPublisher, _doc_id, 2));
			cmbSelectEditor.setSelectedItem(engine.retrieveDefaultSelection(cmbvecEditor, _doc_id, 3));
		}
		
		if (engine.isSubmissionStateActive()) {
			cmbSelectWorkflow.setSelectedItem(engine.retrieveDefaultSelection(cmbvecWorkflow, _doc_id, 1));
			cmbSelectPublisher.setSelectedItem(engine.retrieveDefaultSelection(cmbvecPublisher, _doc_id, 4));
			
			cmbSelectWorkflow.setEnabled(((ComboItem)cmbSelectWorkflow.getSelectedItem()).getItemId() == (cmbvecWorkflow.elementAt(0)).getItemId());
			cmbSelectPublisher.setEnabled(((ComboItem)cmbSelectPublisher.getSelectedItem()).getItemId() == (cmbvecPublisher.elementAt(0)).getItemId());
			
			WorkflowTypeId = ((ComboItem)cmbSelectWorkflow.getSelectedItem()).getItemId();
			PublisherId = ((ComboItem)cmbSelectPublisher.getSelectedItem()).getItemId();
		} else {
			cmbSelectWorkflow.setEnabled(false);
			cmbSelectPublisher.setEnabled(false);
			
			cmbSelectWorkflow.setSelectedItem(engine.retrieveDefaultSelection(cmbvecWorkflow, _doc_id, 1));
			cmbSelectPublisher.setSelectedItem(engine.retrieveDefaultSelection(cmbvecPublisher, _doc_id, 4));
		}
		
		//ComboBox (Action Listeners)
		cmbSelectWorkflow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings({ "rawtypes" })
				JComboBox comboBox = (JComboBox)e.getSource();
		        ComboItem item = (ComboItem)comboBox.getSelectedItem();
		        //System.out.println( item.getItemId() + " : " + item.getItemTitle() );
		        WorkflowTypeId = item.getItemId();
			}
		});

		cmbDesktopPublisher.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings({ "rawtypes" })
				JComboBox comboBox = (JComboBox)e.getSource();
		        ComboItem item = (ComboItem)comboBox.getSelectedItem();
		        //System.out.println( item.getItemId() + " : " + item.getItemTitle() );
		        DesktopId = item.getItemId();
			}
		});
		
		cmbSelectEditor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings({ "rawtypes" })
				JComboBox comboBox = (JComboBox)e.getSource();
		        ComboItem item = (ComboItem)comboBox.getSelectedItem();
		        //System.out.println( item.getItemId() + " : " + item.getItemTitle() );
		        EditorId = item.getItemId();
			}
		});
		
		cmbSelectPublisher.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings({ "rawtypes" })
				JComboBox comboBox = (JComboBox)e.getSource();
		        ComboItem item = (ComboItem)comboBox.getSelectedItem();
		        //System.out.println( item.getItemId() + " : " + item.getItemTitle() );
		        PublisherId = item.getItemId();
			}
		});
		
		//Buttons (Action Listeners)
		
		//Accept
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				engine.setCurrentActiveState(engine.retrieveActiveState(_doc_id)); //ovo je duplirano nalazi se orig kod permissions, staviti na pocetak koda mozda
				String noteContent = getNoteContent(textArea);
				int _action_id = 1;
				if (engine.isSubmissionStateActive()) {
					if (WorkflowTypeId > 0 && DesktopId > 0 && EditorId > 0 && PublisherId > 0) {
						int[] stakeholders = new int[3];
						stakeholders[0] = DesktopId;
						stakeholders[1] = EditorId;
						stakeholders[2] = PublisherId;
						engine.stateTransition(_doc_id, _action_id, noteContent, WorkflowTypeId, stakeholders);
						refreshChangeTables(appwindow, user, dashPane, changePane);
						dispose();
					} else {
						JOptionPane.showMessageDialog(null, "You must choose all stakeholders and workflow type.");
					}
				} else {
					engine.stateTransition(_doc_id, _action_id, noteContent);
					refreshChangeTables(appwindow, user, dashPane, changePane);
					dispose();
				}
			}
		});
		
		//Reject
		btnReject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				engine.setCurrentActiveState(engine.retrieveActiveState(_doc_id)); //ovo je duplirano, i u svakom slucaju izbrisati odavde
				String noteContent = getNoteContent(textArea);
				int _action_id = 2;
				engine.stateTransition(_doc_id, _action_id, noteContent);
				refreshChangeTables(appwindow, user, dashPane, changePane);
				dispose();
			}
		});
		
		//Cancel
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		//View Process
		btnViewProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorkflowFSM fsm = new WorkflowFSM();
				fsm.setVisible(true);
			}
		});
		
	}
	
	//Other methods
	private String getNoteContent(JTextArea text) {
		String noteContent = text.getText();
		return noteContent;
	}
	
	public void refreshChangeTables(Appwindow appwindow, User user, JScrollPane dashPane, JScrollPane changePane) {
		ChangeTable _dashTable = new ChangeTable();
		ChangeTable _changeTable = new ChangeTable();
		
		_dashTable.updateTable(1, user.getUserId());
		_changeTable.updateTable(2, user.getUserId());
		
		JTable dashTable = _dashTable.getChangeTable();
		JTable changeTable = _changeTable.getChangeTable();
		
		appwindow.setDashTable(dashTable);
		appwindow.setChangeTable(changeTable);
		
		appwindow.addChangeTableDashboardPermissions(appwindow.getDashTable());
		appwindow.addChangeTableChangeTabPermissions(appwindow.getChangeTable());
		
		dashPane.setViewportView(appwindow.getDashTable());
		changePane.setViewportView(appwindow.getChangeTable());
	}
	
	public String retrieveTitleBarText(int _doc_id) {
		engine.setCurrentActiveState(engine.retrieveActiveState(_doc_id));
		String title = null;
		if (this.engine.isSubmissionStateActive()) {
			title = "Current Stage: Submission";
		} else if (this.engine.isFirstReviewStateActive()) {
			title = "Current Stage: First Review";
		} else if (this.engine.isDesktopPublishingStateActive()) {
			title = "Current Stage: Desktop Publishing";
		} else if (this.engine.isFinalReviewStateActive()) {
			title = "Current Stage: Final Review";
		} else if (this.engine.isProductionStateActive()) {
			title = "Current Stage: Production";
		} else if (this.engine.isCloseStateActive()) {
			title = "Current Stage: Close";
		} else {
			title = "You shoudn't see this title.";
		}
		return title;
	}
	
}
