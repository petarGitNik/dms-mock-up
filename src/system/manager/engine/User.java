package system.manager.engine;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class User {

	private int _user_id;
	private String _userFirstLastName;
	
	//Permissions
	private String _role;
	
	private boolean _uploadNewDocument;
	private boolean _uploadNewVersion;
	private boolean _addToWorkflow;
	private boolean _download;
	private boolean _change;
	private boolean _options;
	
	private boolean _stateSubmission;
	private boolean _stateFirstReview;
	private boolean _stateDesktopPublishing;
	private boolean _stateFinalReview;
	private boolean _stateProduction;
	private boolean _stateClose;
	
	//Setters and getters
	public void setUserId(int _user_id) {
		this._user_id = _user_id;
	}
	
	public int getUserId() {
		return this._user_id;
	}
	
	public void setUserFirstLastName() {
		
		try {
			
			String _firstname = null;
			String _lastname = null;
			Connection conn = SQliteConnection.dbConnecton();
			String sql = "SELECT firstname, lastname FROM credentials WHERE user_id=?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, Integer.toString(this._user_id));
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				_firstname = rs.getString(1);
				_lastname = rs.getString(2);
			}
			
			this._userFirstLastName = _firstname + " " + _lastname;
			
			pst.close();
			rs.close();
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Exception during retrieval of users first and last name: " + e);
		}
		
	}
	
	public String getUserFirstLastName() {
		return this._userFirstLastName;
	}
	
	public void setPermissions() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = SQliteConnection.dbConnecton();
			String sql = "SELECT uploadNewDocument, uploadNewVersion, download, change, options, addToWorkflow FROM roles INNER JOIN credentials ON credentials.role = roleid AND credentials.user_id=?";
			pst = con.prepareStatement(sql);
			pst.setString(1, Integer.toString(_user_id));
			rs = pst.executeQuery();
			
			while (rs.next()) {
				this._uploadNewDocument = Boolean.parseBoolean(rs.getString(1));
				this._uploadNewVersion = Boolean.parseBoolean(rs.getString(2));
				this._download = Boolean.parseBoolean(rs.getString(3));
				this._change = Boolean.parseBoolean(rs.getString(4));
				this._options = Boolean.parseBoolean(rs.getString(5));
				this._addToWorkflow = Boolean.parseBoolean(rs.getString(6));
			}
						
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error in setPermission: " + e);
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error in User object (finally/setRole)" + e);
				e.printStackTrace();
			}
		}
	}
	
	public void setStatePermissions() {
		if (isAdmin()) {
			this._stateSubmission = true;
			this._stateFirstReview = true;
			this._stateDesktopPublishing = true;
			this._stateFinalReview = true;
			this._stateProduction = true;
			this._stateClose = true;
		}
		if (isAuthor()) {
			this._stateSubmission = true;
			this._stateFirstReview = false;
			this._stateDesktopPublishing = false;
			this._stateFinalReview = false;
			this._stateProduction = false;
			this._stateClose = false;
		}
		if (isDesktopPublisher()) {
			this._stateSubmission = false;
			this._stateFirstReview = false;
			this._stateDesktopPublishing = true;
			this._stateFinalReview = false;
			this._stateProduction = false;
			this._stateClose = false;
		}
		if (isEditor()) {
			this._stateSubmission = false;
			this._stateFirstReview = true;
			this._stateDesktopPublishing = false;
			this._stateFinalReview = true;
			this._stateProduction = false;
			this._stateClose = false;
		}
	}
	
	//Get state permissions
	public boolean getStateSubmissionPermission() {
		return this._stateSubmission;
	}
	
	public boolean getStateFirstReviewPermission() {
		return this._stateFirstReview;
	}
	
	public boolean getStateDesktopPublishing() {
		return this._stateDesktopPublishing;
	}
	
	public boolean getStateFinalReview() {
		return this._stateFinalReview;
	}
	
	public boolean getStateProductionPermission() {
		return this._stateProduction;
	}
	
	public boolean getStateClosePermission() {
		return this._stateClose;
	}
	
	//Get permissions
	public boolean getUploadDocumentPemirssion() {
		return this._uploadNewDocument;
	}
	
	public boolean getUploadVersionPermission() {
		return this._uploadNewVersion;
	}
	
	public boolean getDownloadPermission() {
		return this._download;
	}
	
	public boolean getAddToWorkflowPermission() {
		return this._addToWorkflow;
	}
	
	public boolean getChangePermission() {
		return this._change;
	}
	
	public boolean getOptionsPermission() {
		return this._options;
	}
	
	public void setRole() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = SQliteConnection.dbConnecton();
			String sql = "SELECT roleName FROM roles INNER JOIN credentials ON credentials.role = roleid AND credentials.user_id=?";
			pst = con.prepareStatement(sql);
			pst.setString(1, Integer.toString(_user_id));
			rs = pst.executeQuery();
			
			while (rs.next()) {
				this._role = rs.getString(1);
			}
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error in User object (try/setRole)" + e);
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error in User object (finally/setRole)" + e);
				e.printStackTrace();
			}
		}
	}
	
	public String getRole() {
		return this._role;
	}
	
	//Constructor
	public User() {
		//Empty constructor
	}
	
	public User(int _user_id) {
		this._user_id = _user_id;
	}
	
	//Methods
	public boolean isAuthor() {
		if (this._role.equals("Author")) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isEditor() {
		if (this._role.equals("Editor")) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isAdmin() {
		if (this._role.equals("Admin")) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isDesktopPublisher() {
		if (this._role.equals("Desktop Publisher")) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isStakeholder(int _workflowId) {
		String sql = "SELECT rowid FROM stakeholders WHERE user_id=? AND workflow_id=?";
		int counter = 0;
		boolean isIt = false;
		try {
			Connection con = SQliteConnection.dbConnecton();
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, _user_id);
			pst.setInt(2, _workflowId);
			ResultSet rs = pst.executeQuery();
			
			while (rs.next()) {
				counter++;
			}
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error in isStakeholders method.");
			e.printStackTrace();
		}
		if (counter > 0 ) {
			isIt = true;
		}
		return isIt;
	}
	
	public boolean permisionForActiveState(int state_type_id) {
		boolean hasPermission = false;
		switch (state_type_id) {
		case 1:
			hasPermission = getStateSubmissionPermission();
			break;
		case 2:
			hasPermission = getStateFirstReviewPermission();
			break;
		case 3:
			hasPermission = getStateDesktopPublishing();
			break;
		case 4:
			hasPermission = getStateFinalReview();
			break;
		case 5:
			hasPermission = getStateProductionPermission();
			break;
		case 6:
			hasPermission = getStateClosePermission();
			break;
		default:
			JOptionPane.showMessageDialog(null, "Unknown state type for a given ID.");
			break;
		}
		return hasPermission;
	}
	
}
