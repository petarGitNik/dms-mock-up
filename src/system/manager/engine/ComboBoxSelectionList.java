package system.manager.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JOptionPane;

public class ComboBoxSelectionList {

	private ComboItem emptySelection = new ComboItem(-1, "---");
	private Vector<ComboItem> selectionList = new Vector<ComboItem>();
	private String sql;
	
	//Getters and setters
	public Vector<ComboItem> getSelectionList() {
		return this.selectionList;
	}
	
	//Constructor
	public ComboBoxSelectionList() {
		
	}
	
	public ComboBoxSelectionList(String collumn_id) {
		selectionList.addElement(emptySelection);
		Connection con = SQliteConnection.dbConnecton();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			sql = "SELECT " + collumn_id + ", title FROM workflowType";
			pst = con.prepareStatement(sql);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				selectionList.addElement(new ComboItem(rs.getInt(1), rs.getString(2)));
			}
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error in ComboBoxSelect object (connection/workflow)" + e);
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
				JOptionPane.showMessageDialog(null, "Error in ComboBoxSelect object (finally/workflow)" + e);
				e.printStackTrace();
			}
		}
	}
	
	public ComboBoxSelectionList(String col_user_id, String col_role_id) {
		selectionList.addElement(emptySelection);
		Connection con = SQliteConnection.dbConnecton();
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		if (col_user_id.equals("user_id") && (col_role_id.equals("Desktop Publisher") || col_role_id.equals("Editor") || col_role_id.equals("Admin"))) {
			sql = "SELECT user_id, firstname, lastname FROM credentials INNER JOIN roles WHERE credentials.role=roles.roleid AND roleName='" + col_role_id + "'";
		} else {
			JOptionPane.showMessageDialog(null, "Not a valid option for col_role_id.");
		}
		
		try {
			pst = con.prepareStatement(sql);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				String userFirstLastName = rs.getString(2) + " " + rs.getString(3);
				selectionList.addElement(new ComboItem(rs.getInt(1), userFirstLastName));
			}
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error in ComboBoxSelect object (connctions/user)" + e);
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
				JOptionPane.showMessageDialog(null, "Error in ComboBoxSelect object (finally/user)" + e);
				e.printStackTrace();
			}
		}
		
	}
	
}
