package system.manager.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import system.manager.engine.SQliteConnection;
import system.manager.engine.User;

public class LoginDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblUsername = new JLabel("Username");
	private JLabel lblPassword = new JLabel("Password");
	
	private JTextField jtfUsername = new JTextField(15);
	private JPasswordField jpfPassword = new JPasswordField();
	
	private JButton btnLogin = new JButton("Login");
	private JButton btnCancel = new JButton("Cancel");
	
	private JLabel lblStatus = new JLabel(" ");
	
	//Partial constuctor
	public LoginDialog() {
		this(true);
	}
	
	//Full constructor
	public LoginDialog(boolean modal) {
		setModal(modal);
		
		//Label panel
		JPanel lblPanel = new JPanel(new GridLayout(2,1));
		lblPanel.add(lblUsername);
		lblPanel.add(lblPassword);
		
		//Textbox panel
		JPanel txtPanel = new JPanel(new GridLayout(2,1));
		txtPanel.add(jtfUsername);
		txtPanel.add(jpfPassword);
		
		//Add two previous panels
		JPanel passPanel = new JPanel();
		passPanel.add(lblPanel);
		passPanel.add(txtPanel);
		
		//Make panel for buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(btnLogin);
		buttonPanel.add(btnCancel);
		
		//Button + lblStatus panel = optionWarningPanel
		JPanel optionWarningPanel = new JPanel(new BorderLayout());
		optionWarningPanel.add(buttonPanel, BorderLayout.CENTER);
		optionWarningPanel.add(lblStatus, BorderLayout.NORTH);
		lblStatus.setForeground(Color.RED);
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		
		//All previous component on one panel
		setLayout(new BorderLayout());
		add(passPanel, BorderLayout.CENTER);
		add(optionWarningPanel, BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		//Button Actions
		
		//WindowAction
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		});
		
		
		//Login Button
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				
				try {
					Connection conn = SQliteConnection.dbConnecton();
					String sql = "SELECT username, password, user_id FROM credentials WHERE username=? and password=?";
					PreparedStatement pst = conn.prepareStatement(sql);
					pst.setString(1, jtfUsername.getText().trim());
					pst.setString(2, (new String(jpfPassword.getPassword())));
					ResultSet rs = pst.executeQuery();
					
					int _user_id = 0;
					int count = 0;
					while(rs.next()) {
						count++;
						_user_id = rs.getInt(3);
					}
					
					if(count == 1) {
						//User found
						setVisible(false);
						User user = new User(_user_id);
						
						user.setRole();
						user.setPermissions();
						user.setStatePermissions();
						user.setUserFirstLastName();
						
						Appwindow application = new Appwindow(user);
						application.setUser(user);
						application.setAppVisible(application);
						
						dispose();
					} else if(count > 1) {
						JOptionPane.showMessageDialog(null, "Duplicate user. This should not have happened.");
					} else {
						lblStatus.setText("Username or password are incorrect.");
					}
					
				} catch(Exception ex) {
					JOptionPane.showMessageDialog(null, "Exception during login: " + ex);
				}
				
			}
		});
		
		//Cancel button
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				System.exit(0);
			}
		});
		
		setVisible(true);
		
	}
	
}
