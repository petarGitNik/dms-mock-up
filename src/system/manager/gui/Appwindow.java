package system.manager.gui;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import system.manager.engine.ChangeTable;
import system.manager.engine.DocumentTable;
import system.manager.engine.User;

public class Appwindow {

	private JFrame frame;
	private User user;
	//
	
	//Table
	private JTable table;
	private JTable dashTable;
	private JTable changeTable;
	
	//Buttons
	private JButton btnDashboardDownload = new JButton("Download");
	private JButton btnDashboardWorkflow = new JButton("View Workflow");
	
	private JButton btnView = new JButton("View");
	private JButton btnDownload = new JButton("Download");
	private JButton btnUploadNewDocument = new JButton("Upload New Document");
	private JButton btnUploadNewVersion = new JButton("Upload New Version");
	private JButton btnAddToWorkflow = new JButton("Add to Workflow");
	
	private JButton btnChangeTabDownload = new JButton("Download");
	private JButton btnChangeTabWorkflow = new JButton("View Workflow");
	
	//Icons for tabs
	private ImageIcon _dashboard = new ImageIcon("./icons/icon_dashboard.png");
	private ImageIcon _documents = new ImageIcon("./icons/icon_documents.png");
	private ImageIcon _search = new ImageIcon("./icons/icon_search.png");
	private ImageIcon _change = new ImageIcon("./icons/icon_change.png");
	private ImageIcon _adminboard = new ImageIcon("./icons/icon_adminboard.png");
	
	//Icons for Dashboard and Change tabs
	private ImageIcon _workflow = new ImageIcon("./icons/icon_view_workflow.png");
	
	//Icons for document tab
	private ImageIcon _view = new ImageIcon("./icons/icon_view.png");
	private ImageIcon _download = new ImageIcon("./icons/icon_download.png");
	private ImageIcon _upload_new_document = new ImageIcon("./icons/icon_upload_new_document.png");
	private ImageIcon _upload_new_version = new ImageIcon("./icons/icon_upload_new_version.png");
	private ImageIcon _add_to_workflow = new ImageIcon("./icons/icon_add_to_workflow.png");
	
	//Getters and setters
	
	public void setAppVisible(Appwindow window) {
		window.frame.setVisible(true);
	}
	
	public User getUser() {
		return this.user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public void setMainTable(JTable newTable) {
		this.table = newTable;
	}
	
	public JTable getMainTable() {
		return this.table;
	}
	
	public void setDashTable(JTable dashTable) {
		this.dashTable = dashTable;
	}
	
	public JTable getDashTable() {
		return this.dashTable;
	}
	
	public void setChangeTable(JTable changeTable) {
		this.changeTable = changeTable;
	}
	
	public JTable getChangeTable() {
		return this.changeTable;
	}

	/**
	 * Create the application.
	 */
	public Appwindow(User user) {
		initialize(user, this);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(User user, Appwindow appwindow) {
		frame = new JFrame();
		frame.setIconImage(_change.getImage());
		frame.setBounds(100, 100, 975, 755);
		frame.setTitle("jDocument Management System");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		
		JLabel lblWelcomeUser = new JLabel("Welcome " + (user.getUserFirstLastName()) + "!");
		panel.add(lblWelcomeUser);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Dashboard", _dashboard, panel_1, "View Active Tasks");
		panel_1.setLayout(new BorderLayout(5, 5));
		
		JPanel dashboardBtnPanel = new JPanel();
		dashboardBtnPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		btnDashboardWorkflow.setIcon(_workflow);
		btnDashboardDownload.setIcon(_download);
		
		dashboardBtnPanel.add(btnDashboardDownload);
		dashboardBtnPanel.add(btnDashboardWorkflow);
		
		JScrollPane dashboardScrollPane = new JScrollPane();
		
		ChangeTable _dashTable = new ChangeTable();
		_dashTable.updateTable(1, user.getUserId());
		this.dashTable = _dashTable.getChangeTable();
		dashboardScrollPane.setViewportView(dashTable);
		
		panel_1.add(dashboardBtnPanel, BorderLayout.NORTH);
		panel_1.add(dashboardScrollPane, BorderLayout.CENTER);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Documents", _documents, panel_2, null);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_5 = new JPanel();
		panel_2.add(panel_5, BorderLayout.NORTH);
		panel_5.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		btnView.setIcon(_view);
		btnView.setEnabled(false);
		panel_5.add(btnView);
		
		btnDownload.setIcon(_download);
		panel_5.add(btnDownload);
		
		btnUploadNewDocument.setIcon(_upload_new_document);
		panel_5.add(btnUploadNewDocument);
		
		btnUploadNewVersion.setIcon(_upload_new_version);
		panel_5.add(btnUploadNewVersion);
		
		btnAddToWorkflow.setIcon(_add_to_workflow);
		panel_5.add(btnAddToWorkflow);
		
		JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);
		
		DocumentTable _table = new DocumentTable();
		_table.setTable();
		table = _table.getTable();
		scrollPane.setViewportView(table);
		
		JPanel panel_6 = new JPanel();
		tabbedPane.addTab("Search", _search, panel_6, null);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Change", _change, panel_3, null);
		panel_3.setLayout(new BorderLayout(5, 5));
		
		JPanel changeBtnPanel = new JPanel();
		changeBtnPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		btnChangeTabWorkflow.setIcon(_workflow);
		btnChangeTabDownload.setIcon(_download);
		
		changeBtnPanel.add(btnChangeTabDownload);
		changeBtnPanel.add(btnChangeTabWorkflow);
		
		JScrollPane changeScrollPane = new JScrollPane();
		
		ChangeTable _changeTable = new ChangeTable();
		_changeTable.updateTable(2, user.getUserId());
		changeTable = _changeTable.getChangeTable();
		changeScrollPane.setViewportView(changeTable);
		
		panel_3.add(changeBtnPanel, BorderLayout.NORTH);
		panel_3.add(changeScrollPane, BorderLayout.CENTER);
		
		JPanel panel_4 = new JPanel();
		tabbedPane.addTab("Options", _adminboard, panel_4, null);
		
		//Menu bar and menu items
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmLogout = new JMenuItem("logout");
		mntmLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				@SuppressWarnings("unused")
				LoginDialog login = new LoginDialog();
			}
		});
		mnFile.add(mntmLogout);
		
		JMenuItem mntmQuit = new JMenuItem("quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmQuit);
		
		//User permissions
		btnDownload.setEnabled(user.getDownloadPermission());
		btnUploadNewDocument.setEnabled(user.getUploadDocumentPemirssion());
		btnUploadNewVersion.setEnabled(user.getUploadVersionPermission());
		btnAddToWorkflow.setEnabled(user.getAddToWorkflowPermission());
		if (!user.getChangePermission()) { tabbedPane.remove(panel_3); }
		if (!user.getOptionsPermission()) { tabbedPane.remove(panel_4); }
		addDocumentTablePermissions(table, btnDownload, btnUploadNewDocument, btnUploadNewVersion, btnAddToWorkflow);
		addChangeTableDashboardPermissions(dashTable);
		addChangeTableChangeTabPermissions(changeTable);
		
		//Button actions
		
		//Download Button
		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectedRow() == -1) {
					JOptionPane.showMessageDialog(null, "Please select a document.");
				} else {
					String _sourcepath = (String) table.getModel().getValueAt(table.getSelectedRow(),7);
					downloadDocument(_sourcepath);
				}	
			}
		});
		
		//Upload New Document Button
		btnUploadNewDocument.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Select a file to upload");
				
				if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String uploadpath = chooser.getSelectedFile().toString();
					
					UploadNewFile obj = new UploadNewFile(uploadpath, scrollPane, user, appwindow);
					obj.setVisible(true);
				}
			}
		});
		
		//Upload New Version Button
		btnUploadNewVersion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Select a new version of your document");
				
				if(table.getSelectedRow() == -1) {
					JOptionPane.showMessageDialog(null, "Please select a document.");
				} else if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String uploadpath = chooser.getSelectedFile().toString();
					String _docname = (String) table.getModel().getValueAt(table.getSelectedRow(),1);
					int doc_id = (Integer) table.getModel().getValueAt(table.getSelectedRow(),6);
					
					UploadNewVersion obj = new UploadNewVersion(uploadpath, scrollPane, user, _docname, doc_id, appwindow);
					obj.setVisible(true);
				}
			
			}
		});
		
		//Add to workflow
		btnAddToWorkflow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow() == -1) {
					JOptionPane.showMessageDialog(null, "Please select a document to add to workflow.");
				} else {
					int doc_id = (Integer) table.getModel().getValueAt(table.getSelectedRow(),6);
					@SuppressWarnings("unused")
					Workflow workflowGUI = new Workflow(appwindow, user, dashboardScrollPane, changeScrollPane, doc_id);
				}
			}
		});
		
		//View workflow - Change Tab
		btnChangeTabWorkflow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (changeTable.getSelectedRow() == -1) {
					JOptionPane.showMessageDialog(null, "Please select a workflow.");
				} else {
					int doc_id = (Integer) changeTable.getModel().getValueAt(changeTable.getSelectedRow(),7);
					@SuppressWarnings("unused")
					Workflow workflowGUI = new Workflow(appwindow, user, dashboardScrollPane, changeScrollPane, doc_id);
				}	
			}
		});
		
		//Download - Change Tab
		btnChangeTabDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (changeTable.getSelectedRow() == -1) {
					JOptionPane.showMessageDialog(null, "Please select a workflow.");
				} else {
					String _sourcepath = (String) changeTable.getModel().getValueAt(changeTable.getSelectedRow(),8);
					downloadDocument(_sourcepath);
				}
			}
		});
		
		//View workflow - Dashboard
		btnDashboardWorkflow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dashTable.getSelectedRow() == -1) {
					JOptionPane.showMessageDialog(null, "Please select a workflow.");
				} else {
					int doc_id = (Integer) dashTable.getModel().getValueAt(dashTable.getSelectedRow(),7);
					@SuppressWarnings("unused")
					Workflow workflowGUI = new Workflow(appwindow, user, dashboardScrollPane, changeScrollPane, doc_id);
				}
			}
		});
		
		//Download - Dashboard
		btnDashboardDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dashTable.getSelectedRow() == -1) {
					JOptionPane.showMessageDialog(null, "Please select a workflow.");
				} else {
					String _sourcepath = (String) dashTable.getModel().getValueAt(dashTable.getSelectedRow(),8);
					downloadDocument(_sourcepath);
				}
			}
		});
		
	}
	
	//Other methods
	public void addDocumentTablePermissions(JTable table) {
		addDocumentTablePermissions(table, btnDownload, btnUploadNewDocument, btnUploadNewVersion, btnAddToWorkflow);
	}
	
	public void addDocumentTablePermissions(JTable table, JButton btnDownload, JButton btnUploadNewDocument, JButton btnUploadNewVersion, JButton btnAddToWorkflow) {
		table.getTableHeader().setReorderingAllowed(false);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			 public void valueChanged(ListSelectionEvent event) {
				 int id = user.getUserId();
				 if (user.isAuthor() && (Integer) id == table.getModel().getValueAt(table.getSelectedRow(),8)) {
					btnDownload.setEnabled(true);
					btnUploadNewVersion.setEnabled(true);
					btnAddToWorkflow.setEnabled(true);
				} else if (user.isAuthor() && (Integer) id != table.getModel().getValueAt(table.getSelectedRow(),8)) {
					btnDownload.setEnabled(false);
					btnUploadNewVersion.setEnabled(false);
					btnAddToWorkflow.setEnabled(false);
				} else {
					btnDownload.setEnabled(user.getDownloadPermission());
					btnUploadNewDocument.setEnabled(user.getUploadDocumentPemirssion());
					btnUploadNewVersion.setEnabled(user.getUploadVersionPermission());
					btnAddToWorkflow.setEnabled(user.getAddToWorkflowPermission());
				}
			}
		});
	}
	
	public void addChangeTableDashboardPermissions(JTable table) {
		addChangeTablePermissions(table, btnDashboardDownload, btnDashboardWorkflow);
	}
	
	public void addChangeTableChangeTabPermissions(JTable table) {
		addChangeTablePermissions(table, btnChangeTabDownload, btnChangeTabWorkflow);
	}
	
	private void addChangeTablePermissions(JTable table, JButton btnDownload, JButton btnWorkflow) {
		table.getTableHeader().setReorderingAllowed(false);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if (user.isStakeholder((int) table.getModel().getValueAt(table.getSelectedRow(), 10))) {
					btnWorkflow.setEnabled(true); // || user.isAdmin()
					btnDownload.setEnabled(true);
				} else {
					btnWorkflow.setEnabled(false);
					btnDownload.setEnabled(false);
				}
			}
		});
	}
	
	public void downloadDocument(String _sourcepath) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Select destination folder on your file system.");

		if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
								
		String _protodownloadpath = chooser.getSelectedFile().toString();
		String _downloadpath = _protodownloadpath + "/" + _sourcepath.substring(_sourcepath.lastIndexOf("/")+1);
			
		try {
									
			FileInputStream source = new FileInputStream(_sourcepath);
			FileOutputStream destination = new FileOutputStream(_downloadpath);
			FileChannel sourceChannel = source.getChannel();
			FileChannel destinationChannel = destination.getChannel();
			long size = sourceChannel.size();
			sourceChannel.transferTo(0, size, destinationChannel);
			source.close();
			destination.close();
			JOptionPane.showMessageDialog(null, "Download Complete.");
									
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "There was an exception during download: " + e);
			}
		}
	}
	
}