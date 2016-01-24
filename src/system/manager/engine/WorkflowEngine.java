package system.manager.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JOptionPane;

public class WorkflowEngine {

	private int _workflowId;
	private int _currentActiveState;
	private User activeUser;
	private User author;
	private Connection con = SQliteConnection.dbConnecton();
	private PreparedStatement pst;
	private ResultSet rs;
	
	//Getters and Setters
	
	public User getAuthor() {
		return this.author;
	}
	
	public void setAuthor(User author) {
		this.author = author;
	}
	
	public User getActiveUser() {
		return this.activeUser;
	}
	
	public void setActiveUser(User activeUser) {
		this.activeUser = activeUser;
	}
	
	public int getCurrentActiveState() {
		return this._currentActiveState;
	}
	
	public void setCurrentActiveState(int desiredState) {
		this._currentActiveState = desiredState;
	}
	
	public int getWorkflowId() {
		return this._workflowId;
	}
	
	public void setWorkflowId(int _workflowId) {
		this._workflowId = _workflowId;
	}
	
	//Constructor
	
	public WorkflowEngine() {
		//Empty constructor
	}
	
	public WorkflowEngine(int _workflowId) {
		setWorkflowId(_workflowId);
	}
	
	//Other methods
	
	public boolean isSubmissionStateActive() {
		if (this._currentActiveState == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isFirstReviewStateActive() {
		if (this._currentActiveState == 2) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isDesktopPublishingStateActive() {
		if (this._currentActiveState == 3) {
			return true;
		} else {
			return false;
		}
	}	

	public boolean isFinalReviewStateActive() {
		if (this._currentActiveState == 4) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isProductionStateActive() {
		if (this._currentActiveState == 5) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isCloseStateActive() {
		if (this._currentActiveState == 6) {
			return true;
		} else {
			return false;
		}
	}
	
	//is project finished?
	public boolean isFinalStateActive() {
		if (this._currentActiveState == 7) {
			return true;
		} else {
			return false;
		}
	}
	
	//new workflow id
	public void addToWorkflow(User user, int _doc_id) {
		
		//INSERT workflow_id INTO workflow
		try {
			String sql = "INSERT INTO workflow (workflow_type_id) VALUES (?)";
			pst = con.prepareStatement(sql);
			pst.setNull(1, java.sql.Types.INTEGER);
			pst.executeUpdate();
			
			String retrieve = "SELECT last_insert_rowid()";
			pst = con.prepareStatement(retrieve);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				_workflowId = rs.getInt(1);
			}
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error while adding to workflow (workflow): " + e);
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error in WorkflowEngine (finally/workflow)" + e);
				e.printStackTrace();
			}
		}
		
		//INSERT state_id, state_type_id, is_active, created_on, document_id INTO state
		try {
			CurrentDate currdate = new CurrentDate();
			String sql = "INSERT INTO state (state_type_id, document_id, note_text, created_on, is_active, workflow_id) VALUES (?,?,?,?,?,?)";
			pst = con.prepareStatement(sql);
			pst.setString(1, Integer.toString(1));
			pst.setString(2, Integer.toString(_doc_id));
			pst.setNull(3, java.sql.Types.INTEGER); //note_id promeni u note_text | Types.STRING, text w/e
			pst.setString(4, currdate.getDate());
			pst.setString(5, "true");
			pst.setString(6, Integer.toString(_workflowId));
			pst.executeUpdate();
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error while adding to workflow (state): " + e);
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error in WorkflowEngine (finally/state)" + e);
				e.printStackTrace();
			}
		}

		//INSERT workflow_id, user_id INTO stakeholders
		try {
			String sqlAuthor = "INSERT INTO stakeholders (workflow_id, user_id) VALUES (?,?)";
			pst = con.prepareStatement(sqlAuthor);
			pst.setInt(1, _workflowId);
			Document docx = new Document();
			pst.setInt(2, docx.getAuthorId(_doc_id));
			pst.executeUpdate();
			
			if (user.isAdmin() && (docx.getAuthorId(_doc_id) != user.getUserId())) {
				String sqlAdmin = "INSERT INTO stakeholders (workflow_id, user_id) VALUES (?,?)";
				pst = con.prepareStatement(sqlAdmin);
				pst.setInt(1, _workflowId);
				pst.setInt(2, user.getUserId());
				pst.executeUpdate();
			}
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error while adding to workflow (stakeholders): " + e);
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error in WorkflowEngine (finally/state)" + e);
				e.printStackTrace();
			}
		}
	}
	
	//does workflow for this document exists?
	public boolean workflowExists(int _doc_id) {
		String sql = "SELECT workflow_id FROM state WHERE document_id=?";
		int counter = 0;
		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, _doc_id);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				counter++;
				this._workflowId = rs.getInt(1);
			}
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error in workflowExists(): " + e);
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error in workflowExists() (finally): " + e);
				e.printStackTrace();
			}
		}
		
		if (counter == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	//Retrieve state_type_id of currently active state for defined _doc_id
	public int retrieveActiveState(int _doc_id) {
		int _state_type_id = -1;
		try {
			String sql = "SELECT state_type_id FROM state WHERE is_active=? AND document_id=?";
			pst = con.prepareStatement(sql);
			pst.setString(1, "true");
			pst.setInt(2, _doc_id);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				_state_type_id = rs.getInt(1);
			}
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error in retrieveActiveState() (try): " + e);
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error in retrieveActiveState() (finally): " + e);
				e.printStackTrace();
			}
		}
		return _state_type_id;
	}

	//State Transition
	public void stateTransition(int _doc_id, int _action_id, String _noteText) {
		stateTransition(_doc_id, _action_id, _noteText, -1, null);
	}
	
	public void stateTransition(int _doc_id, int _action_id, String _noteText, int _workflow_type_id, int[] stakeholders) {
		
		//Insert into stakeholders
		if (_action_id == 1 && isSubmissionStateActive() == true) {
			String sql = "SELECT user_id FROM stakeholders WHERE workflow_id=?";
			String insertQuery = "INSERT INTO stakeholders (workflow_id, user_id) VALUES (?,?)";
			int[] stakeholdersDB = new int[4];
			try {
				pst = con.prepareStatement(sql);
				pst.setInt(1, _workflowId);
				rs = pst.executeQuery();
				
				int count = 0;
				while (rs.next()) {
					stakeholdersDB[count] = rs.getInt(1);
					count++;
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error while retrieveing stakeholders: " + e);
				e.printStackTrace();
			}
			
			int ignoreElementAtIndex = -1;
			for (int i = 0; i < stakeholders.length; i++) {
				for (int j = 0; j < stakeholdersDB.length; j++) {
					if (stakeholders[i] == stakeholdersDB[j]) {
						ignoreElementAtIndex = i;
					}
				}
			}
			
			for (int i = 0; i < stakeholders.length; i++) {
				if (i != ignoreElementAtIndex) {
					try {
						pst = con.prepareStatement(insertQuery);
						pst.setInt(1, _workflowId);
						pst.setInt(2, stakeholders[i]);
						pst.executeUpdate();
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(null, "Error while inserting stakeholders: " + e);
						e.printStackTrace();
					}
				}
			}
		}
		
		//Set workflow Type
		if (isSubmissionStateActive() && _workflow_type_id > 0) {
			String sql = "UPDATE workflow SET workflow_type_id=? WHERE workflow_id=?";
			try {
				pst = con.prepareStatement(sql);
				pst.setInt(1, _workflow_type_id);
				pst.setInt(2, _workflowId);
				pst.executeUpdate();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error when setting type of the workflow: " + e);
				e.printStackTrace();
			}
		}
		
		//Transition from currentState to nextState
		
		//Retrieve workflow_type_id for current workflow_id
		if (!isSubmissionStateActive() && _workflow_type_id < 0) {
			try {
				String sql = "SELECT workflow_type_id FROM workflow WHERE workflow_id=?";
				pst = con.prepareStatement(sql);
				pst.setInt(1, _workflowId);
				rs = pst.executeQuery();
				
				while (rs.next()) {
					_workflow_type_id = rs.getInt(1);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Error while retrieving workflow type: " + e);
				e.printStackTrace();
			}	
		}
		
		//Find next_state_id
		int _next_state_id = -1;
		try {
			String sql = "SELECT next_state_id FROM transition_type WHERE workflowType_id=? AND action_type_id=? AND current_state_id=?";
			pst = con.prepareStatement(sql);
			pst.setInt(1, _workflow_type_id);
			pst.setInt(2, _action_id);
			pst.setInt(3, _currentActiveState);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				_next_state_id = rs.getInt(1);
			}	
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error while searching for next_state_id: " + e);
			e.printStackTrace();
		}
		
		//A note must be added to current active state. Then, a state transition can occur
		try {
			String sql = "UPDATE state SET note_text=? WHERE workflow_id=? AND is_active='true'";
			pst = con.prepareStatement(sql);
			pst.setString(1, _noteText);
			pst.setInt(2, _workflowId);
			pst.executeUpdate();
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error while updateing note: " + e);
			e.printStackTrace();
		}
		
		//Update state_id where isActive='true' to isActive='false'
		try {
			String sql = "UPDATE state SET is_active='false' WHERE workflow_id=? AND is_active='true'";
			pst = con.prepareStatement(sql);
			pst.setInt(1, _workflowId);
			pst.executeUpdate();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error while changing active state: " + e);
			e.printStackTrace();
		}
		
		//Insert new state_id where state_type_id = next_state_id, and isActive='true'
		try {
			CurrentDate currdate = new CurrentDate();
			String sql = "INSERT INTO state (state_type_id, document_id, note_text, created_on, is_active, workflow_id) VALUES (?,?,?,?,?,?)";
			pst = con.prepareStatement(sql);
			pst.setInt(1, _next_state_id);
			pst.setInt(2, _doc_id);
			pst.setNull(3, java.sql.Types.OTHER);
			pst.setString(4, currdate.getDate());
			pst.setString(5, "true");
			pst.setString(6, Integer.toString(_workflowId));
			pst.executeUpdate();
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error while inserting new state_id: " + e);
			e.printStackTrace();
		}
		
		int pastState = this._currentActiveState;
		this._currentActiveState = _next_state_id;
		//If current_state_id='2' and next_state_id='1' -> delete all stakeholders except Admin and Author
		if (pastState == 2 && this._currentActiveState == 1) {
			String sql = "SELECT user_id FROM stakeholders WHERE workflow_id=?";
			int[] stakeholdersDB = new int[4];
			try {
				pst = con.prepareStatement(sql);
				pst.setInt(1, _workflowId);
				rs = pst.executeQuery();
				
				int count = 0;
				while (rs.next()) {
					stakeholdersDB[count] = rs.getInt(1);
					count++;
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Error while retrieveing stakeholders: " + e);
				e.printStackTrace();
			}
			User[] user = new User[4];
			for (int i = 0; i < user.length; i++) {
				user[i] = new User(stakeholdersDB[i]);
				user[i].setRole();
			}
			for (int i = 0; i < user.length; i++) {
				if (user[i].isDesktopPublisher() || user[i].isEditor()) {
					String skull = "DELETE FROM stakeholders WHERE user_id=? AND workflow_id=?";
					try {
						pst = con.prepareStatement(skull);
						pst.setInt(1, stakeholdersDB[i]);
						pst.setInt(2, _workflowId);
						pst.executeUpdate();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Error while removing stakeholders: " + e);
						e.printStackTrace();
					}
				}
			}
		}
		//Other operations - Production state?
	}
	
	public String retrieveLastProjectNote() {
		String noteTxt = null;
		String sql = "SELECT note_text FROM state WHERE state_id IN (SELECT MAX(state_id) FROM state WHERE is_active='false' AND workflow_id=?)";
		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, _workflowId);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				noteTxt = rs.getString(1);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error while retrieving last note: " + e);
			e.printStackTrace();
		}
		return noteTxt;
	}
	
	/**
	 * @param comboBoxList
	 * @param OptionId - has four options:
	 * 			OptionId = 1 -> Find Workflow type
	 * 			OptionId = 2 -> Find Desktop Publisher
	 * 			OptionId = 3 -> Find Editor
	 * 			OptionId = 4 -> Find Admin/Publisher
	 * @return
	 */
	public ComboItem retrieveDefaultSelection(Vector<ComboItem> comboBoxList, int _doc_id, int OptionId) {
		if (OptionId == 1) {
			String sql = "SELECT workflow_type_id FROM workflow	WHERE workflow_id=?";
			int _workflow_type_id = -1;
			ComboItem defaultSelection = comboBoxList.elementAt(0);
			
			try {
				pst = con.prepareStatement(sql);
				pst.setInt(1, _workflowId);
				rs = pst.executeQuery();
				
				while (rs.next()) {
					_workflow_type_id = rs.getInt(1);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Error 1404 (workflow engine): " + e);
				e.printStackTrace();
			}
			
			for (ComboItem item : comboBoxList) {
				if (_workflow_type_id == item.getItemId()) {
					defaultSelection = item;
				}
			}
			
			return defaultSelection;
		} else if (OptionId == 2 || OptionId == 3 || OptionId == 4) {
			ComboItem defaultSelection = comboBoxList.elementAt(0);
			String sql = "SELECT user_id FROM stakeholders WHERE workflow_id=?";
			ArrayList<User> _stakeholders = new ArrayList<User>();
			try {
				pst = con.prepareStatement(sql);
				pst.setInt(1, _workflowId);
				rs = pst.executeQuery();
				
				while (rs.next()) {
					_stakeholders.add(new User(rs.getInt(1)));
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Eror 1405 (workflow engine): " + e);
				e.printStackTrace();
			}
			
			for (User user : _stakeholders) {
				user.setRole();
			}
			
			switch (OptionId) {
			case 2:
				//Desktop Publisher
				int _desktop_publisher_id = -1;
				for (User user : _stakeholders) {
					if (user.isDesktopPublisher()) {
						_desktop_publisher_id = user.getUserId();
					}
				}
				for (ComboItem item : comboBoxList) {
					if (_desktop_publisher_id == item.getItemId()) {
						defaultSelection = item;
					}
				}
				break;
			case 3:
				//Editor
				int _editor_id = -1;
				for (User user : _stakeholders) {
					if (user.isEditor()) {
						_editor_id = user.getUserId();
					}
				}
				for (ComboItem item : comboBoxList) {
					if (_editor_id == item.getItemId()) {
						defaultSelection = item;
					}
				}				
				break;
			case 4:
				//Admin/Publisher
				Document doc = new Document();
				int _author_id = doc.getAuthorId(_doc_id);
				int _admin_id = -1;
				for (User user : _stakeholders) {
					if (user.isAdmin()) {
						if (user.getUserId() != _author_id) {
							_admin_id = user.getUserId();
						}
					}
				}
				
				for (ComboItem item : comboBoxList) {
					if (_admin_id == item.getItemId()) {
						defaultSelection = item;
					}
				}
				break;
			default:
				//This should have never happened :'(
				break;
			}
			
			return defaultSelection;
		} else {
			JOptionPane.showMessageDialog(null, "Wrong option for default value of combobox.");
			return comboBoxList.elementAt(0);
		}
	}
	
	public Vector<ComboItem> removeAdminIfAuthor(Vector<ComboItem> comboBoxList, int _doc_id) {
		Document doc = new Document();
		int author_id = doc.getAuthorId(_doc_id);
		User user_author = new User(author_id);
		user_author.setRole();
		if (user_author.isAdmin()) {
			comboBoxList.remove(author_id);
		}
		return comboBoxList;
	}
	
	public String retrieveStateTemplate() {
		String _template = null;
		String sql = "SELECT template FROM state_template WHERE state_type_id=? AND workflow_type_id=?";
		
		int _workflow_type_id = -1;
		try {
			String squll = "SELECT workflow_type_id FROM workflow WHERE workflow_id=?";
			pst = con.prepareStatement(squll);
			pst.setInt(1, _workflowId);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				_workflow_type_id = rs.getInt(1);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error while retrieving workflow type: " + e);
			e.printStackTrace();
		}
		
		if (_workflow_type_id == 0) { _workflow_type_id = 1; }
		
		try {
			pst = con.prepareStatement(sql);
			pst.setInt(1, _currentActiveState);
			pst.setInt(2, _workflow_type_id);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				_template = rs.getString(1);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Retrieve state template error: " + e);
			e.printStackTrace();
		}
		return _template;
	}
	
}
