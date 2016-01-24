package system.manager.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import system.manager.engine.CurrentDate;
import system.manager.engine.DocumentTable;
import system.manager.engine.SQliteConnection;
import system.manager.engine.User;

public class UploadNewFile extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
		
	private String _uploadpath;
	
	private CurrentDate currdate = new CurrentDate();
	private String _datecreated = currdate.getDate();
	
	private JLabel lblDocumentName = new JLabel("Document name:");
	private JLabel lblAuthorName = new JLabel("Author name:");
	private JLabel lblVersion = new JLabel("Version number:");
	private JLabel lblDefaultVer = new JLabel("Set as default version:");
	private JLabel lblDateCreated = new JLabel("Uploaded/created on date:");
	
	private JLabel lblStatus = new JLabel(" ");
	
	private JTextField txtDocumentName = new JTextField(15);
	private JTextField txtVersion = new JTextField("1", 15);
	private JTextField txtDefaultVer = new JTextField("true", 15);
	private JTextField txtDateCreated = new JTextField(_datecreated, 15);
	
	private JButton btnUpload = new JButton("Upload");
	private JButton btnCancel = new JButton("Cancel");

	/**
	 * Create the frame.
	 */
	public UploadNewFile(String uploadpath, JScrollPane scrollPane, User user, Appwindow appwindow) {
		this._uploadpath = uploadpath;
		String _userFirstLastName = user.getUserFirstLastName();
		JTextField txtAuthorName = new JTextField(_userFirstLastName, 15);
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Upload New File");
		setModal(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		//Labels and text boxes
		JPanel docnamePanel = new JPanel(new GridLayout(1,2));
		docnamePanel.add(lblDocumentName);
		docnamePanel.add(txtDocumentName);
		
		JPanel authorNamePanel = new JPanel(new GridLayout(1,2));
		authorNamePanel.add(lblAuthorName);
		txtAuthorName.setEditable(false);
		authorNamePanel.add(txtAuthorName);
		
		JPanel versionPanel = new JPanel(new GridLayout(1,2));
		versionPanel.add(lblVersion);
		txtVersion.setEditable(false);
		versionPanel.add(txtVersion);
		
		JPanel DefaultVerPanel = new JPanel(new GridLayout(1,2));
		DefaultVerPanel.add(lblDefaultVer);
		txtDefaultVer.setEditable(false);
		DefaultVerPanel.add(txtDefaultVer);
		
		JPanel DateCreatedPanel = new JPanel(new GridLayout(1,2));
		DateCreatedPanel.add(lblDateCreated);
		txtDateCreated.setEditable(false);
		DateCreatedPanel.add(txtDateCreated);
		
		//lbl + txt
		JPanel lbltxtPanel = new JPanel(new GridLayout(5,1,5,5));
		
		lbltxtPanel.add(docnamePanel);
		lbltxtPanel.add(authorNamePanel);
		lbltxtPanel.add(versionPanel);
		lbltxtPanel.add(DefaultVerPanel);
		lbltxtPanel.add(DateCreatedPanel);
		
		//Make panel for buttons
		JPanel btnPanel = new JPanel();
		btnPanel.add(btnUpload);
		btnPanel.add(btnCancel);
		
		//Button + lblStatus panel = optionWarningPanel
		JPanel optionWarningPanel = new JPanel(new BorderLayout(5,5));
		optionWarningPanel.add(btnPanel, BorderLayout.CENTER);
		optionWarningPanel.add(lblStatus, BorderLayout.NORTH);
		lblStatus.setForeground(Color.RED);
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		
		//All in one
		contentPane.add(lbltxtPanel, BorderLayout.CENTER);
		contentPane.add(optionWarningPanel, BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
		
		//Action listeners
		btnUpload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				
				if(txtDocumentName.getText().equals("")) {
					lblStatus.setText("Please enter document name.");
				} else {
					lblStatus.setText("Uploading... please wait.");
					btnUpload.setEnabled(false);
					
					//Upload document
					String _downloadfolder = "./filesystem/" + txtDocumentName.getText().trim() + "/" + 
											txtVersion.getText().trim();
					new File(_downloadfolder).mkdirs();
					String _downloadpath = _downloadfolder + "/version_" + txtVersion.getText().trim() + "_" + _userFirstLastName +
											"_" + txtDocumentName.getText().trim() + _uploadpath.substring(_uploadpath.lastIndexOf("."));
					
					try {
						FileInputStream source = new FileInputStream(_uploadpath);
						FileOutputStream destination = new FileOutputStream(_downloadpath);
						
						FileChannel sourceChannel = source.getChannel();
						FileChannel destinationChannel = destination.getChannel();
						
						long size = sourceChannel.size();
						sourceChannel.transferTo(0, size, destinationChannel);
						
						source.close();
						destination.close();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Exception during upload: " + e);
					}
					
					//Update database
					btnCancel.setEnabled(false);
					try {
						
						try {
							Connection conn = SQliteConnection.dbConnecton();
							
							String max = "SELECT MAX(version_id) FROM document_versions";
							PreparedStatement mpst = conn.prepareStatement(max);
							ResultSet mrs = mpst.executeQuery();
							
							int _version_id = 0;
							while(mrs.next()) {
								_version_id = mrs.getInt(1) + 1;
							}
							
							mrs.close();
							mpst.close();
							
							//Now update table using insert into statement
							String sql = "INSERT INTO document_based (docname,curr_version_id, type) VALUES (?,?,?)";
							PreparedStatement pst = conn.prepareStatement(sql);
							pst.setString(1, txtDocumentName.getText().trim());
							pst.setString(2, Integer.toString(_version_id));
							pst.setString(3, uploadpath.substring(uploadpath.lastIndexOf(".")));
							pst.executeUpdate();
							
							pst.close();
						} catch (SQLException e) {
							JOptionPane.showMessageDialog(null, "Exception documents_based: " + e);
						}
						
						//now update document_versions table
						try {
							Connection conn = SQliteConnection.dbConnecton();
							String max = "SELECT MAX(doc_id) FROM document_based";
							PreparedStatement mpst = conn.prepareStatement(max);
							ResultSet mrs = mpst.executeQuery();
							
							int _doc_id = 0;
							while(mrs.next()) {
								_doc_id = mrs.getInt(1);
							}
							
							mrs.close();
							mpst.close();
							
							String sql = "INSERT INTO document_versions (filepath,version,defaultver,datecreated,doc_id,author_id) " +
											"VALUES (?,?,?,?,?,?)";
							PreparedStatement pst = conn.prepareStatement(sql);
							pst.setString(1, _downloadpath);
							pst.setString(2, txtVersion.getText().trim());
							pst.setString(3, txtDefaultVer.getText().trim());
							pst.setString(4, _datecreated);
							pst.setString(5, Integer.toString(_doc_id));
							pst.setString(6, Integer.toString(user.getUserId()));
							pst.executeUpdate();
							
							pst.close();
						} catch(Exception e) {
							JOptionPane.showMessageDialog(null, "Exception document_versions: " + e);
						}
						
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Exception during databse update: " + e);
					}
					
					lblStatus.setText("Uploading finished.");
					DocumentTable _table = new DocumentTable();
					_table.setTable();
					JTable table = _table.getTable();
					appwindow.setMainTable(table);
					appwindow.addDocumentTablePermissions(appwindow.getMainTable());
					scrollPane.setViewportView(appwindow.getMainTable());
					JOptionPane.showMessageDialog(null, "Upload finished.");
					setVisible(false);
					dispose();
				}
			}
		});
		
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				setVisible(false);
				dispose();
			}
		});
		
	}

}
