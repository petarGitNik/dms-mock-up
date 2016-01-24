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
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

public class UploadNewVersion extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	
	private CurrentDate currdate = new CurrentDate();
	private String _datecreated = currdate.getDate();
	
	private JLabel lblDocumentName = new JLabel("Document name:");
	private JLabel lblAuthorName = new JLabel("Author name:");
	private JLabel lblVersion = new JLabel("New version number:");
	private JLabel lblDefaultVer = new JLabel("Set as default verison:");
	private JLabel lblDateCreated = new JLabel("Upload/created on date:");
	
	private JLabel lblStatus = new JLabel(" ");
	 
	private JCheckBox chkbxDefaultVer = new JCheckBox();
	private JTextField txtDateCreated = new JTextField(_datecreated, 15);
	
	private JButton btnUpload = new JButton("Upload");
	private JButton btnCancel = new JButton("Cancel");
	
	public UploadNewVersion(String uploadpath, JScrollPane scrollPane, User user, String docname, int doc_id, Appwindow appwindow) {
		JTextField txtDocumentName = new JTextField(docname, 15);
		JTextField txtVersion = new JTextField(Integer.toString(getNextVersion(doc_id)), 15); //Ucita iz baze koja je verzija po redu
		JTextField txtAuthorName = new JTextField(user.getUserFirstLastName(), 15);
		
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
		txtDocumentName.setEditable(false);
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
		DefaultVerPanel.add(chkbxDefaultVer);
		
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
		
		//Button action
		
		//Upload button
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				btnUpload.setEnabled(false);
				
				String _downloadfolder = "./filesystem/" + txtDocumentName.getText().trim() + "/" + 
						txtVersion.getText().trim();
				new File(_downloadfolder).mkdirs();
				String _downloadpath = _downloadfolder + "/version_" + txtVersion.getText().trim() + "_" + user.getUserFirstLastName() +
						"_" + txtDocumentName.getText().trim() + uploadpath.substring(uploadpath.lastIndexOf("."));
				
				try {
					FileInputStream source = new FileInputStream(uploadpath);
					FileOutputStream destination = new FileOutputStream(_downloadpath);
					
					FileChannel sourceChannel = source.getChannel();
					FileChannel destinationChannel = destination.getChannel();
					
					long size = sourceChannel.size();
					sourceChannel.transferTo(0, size, destinationChannel);
					
					source.close();
					destination.close();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Exception during download: " + e);
				}
				
				btnCancel.setEnabled(false);
				lblStatus.setText("Uploading... please wait.");
				//Update database
				if(chkbxDefaultVer.isSelected()) {
					//Checkbox selected
					try {
						//Find defaultver=true and change it to false
						Connection conn = SQliteConnection.dbConnecton();
						String updateDefault = "UPDATE document_versions SET defaultver=? WHERE defaultver=? AND doc_id =?";
						PreparedStatement pst = conn.prepareStatement(updateDefault);
						pst.setString(1, "false");
						pst.setString(2, "true");
						pst.setString(3, Integer.toString(doc_id));
						pst.executeUpdate();
						pst.close();
						conn.close();
						
						//Insert new version
						Connection conn1 = SQliteConnection.dbConnecton();
						String insertVersion = "INSERT INTO document_versions (filepath, version, defaultver, datecreated, doc_id, author_id) VALUES (?,?,?,?,?,?)";
						PreparedStatement pstVersion = conn1.prepareStatement(insertVersion);
						pstVersion.setString(1, _downloadpath);
						pstVersion.setString(2, txtVersion.getText().trim());
						pstVersion.setString(3, "true");
						pstVersion.setString(4, _datecreated);
						pstVersion.setString(5, Integer.toString(doc_id));
						pstVersion.setString(6, Integer.toString(user.getUserId()));
						pstVersion.executeUpdate();
						pstVersion.close();
						conn1.close();
						
						//Change document in document_based to point to new version
						Connection conn2 = SQliteConnection.dbConnecton();
						String updateDocument = "UPDATE document_based SET curr_version_id = (SELECT version_id FROM document_versions WHERE doc_id=? AND defaultver =?) WHERE doc_id=?";
						PreparedStatement pstDoc = conn2.prepareStatement(updateDocument);
						pstDoc.setString(1, Integer.toString(doc_id));
						pstDoc.setString(2, "true");
						pstDoc.setString(3, Integer.toString(doc_id));
						pstDoc.executeUpdate();
						pstDoc.close();
						conn2.close();
						
						//Refresh table
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
						
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Exception during db update (chkbox ticked): " + e);
						e.printStackTrace();
						dispose();
					}
				} else {
					//Checkbox not selected
					try {
						Connection conn = SQliteConnection.dbConnecton();
						String sql = "INSERT INTO document_versions (filepath, version, defaultver, datecreated, doc_id, author_id) VALUES (?,?,?,?,?,?)";
						PreparedStatement pst = conn.prepareStatement(sql);
						pst.setString(1, _downloadpath);
						pst.setString(2, txtVersion.getText().trim());
						pst.setString(3, "false");
						pst.setString(4, _datecreated);
						pst.setString(5, Integer.toString(doc_id));
						pst.setString(6, Integer.toString(user.getUserId()));
						pst.executeUpdate();
						pst.close();
						conn.close();
						
						lblStatus.setText("Uploading finished.");
						JOptionPane.showMessageDialog(null, "Upload finished.");
						setVisible(false);
						dispose();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Exception during db update (chkbox not-ticked): " + e);
						dispose();
					}
				}
			
			}
		});
		
		//Cancel button
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				setVisible(false);
				dispose();
			}
		});
		
	}
	
	//Other methods
	public int getNextVersion(int doc_id) {
		int nextVersion = 0;
		Connection conn = SQliteConnection.dbConnecton();
		String sql = "SELECT MAX(version) FROM document_versions WHERE doc_id=?";
		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, Integer.toString(doc_id));
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				nextVersion = rs.getInt(1) + 1;
			}
			
			pst.close();
			rs.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Exception while calculating next version: " + e);
		}
		return nextVersion;
	}
	
}
