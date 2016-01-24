package system.manager.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class Document {

	private int _doc_id;
	private String _docname;
	
	//Getters and setters
	public int getDocId() {
		return this._doc_id;
	}
	
	public void setDocId(int _doc_id) {
		this._doc_id = _doc_id;
	}
	
	public String getDocumentName() {
		return this._docname;
	}
	
	public void setDocumentName() {
		Connection con = SQliteConnection.dbConnecton();
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sql = "SELECT docname FROM document_based WHERE doc_id=?";
		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, Integer.toString(this._doc_id));
			rs = pst.executeQuery();
			
			while (rs.next()) {
				_docname = rs.getString(1);
			}
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error in Document object (catch/setDocName)" + e);
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
				JOptionPane.showMessageDialog(null, "Error in Document object (finally/setDocName)" + e);
				e.printStackTrace();
			}
		}
	}
	
	//Constructor
	public Document() {
		//Empty constructor
	}
	
	public Document(int _doc_id) {
		this._doc_id = _doc_id;
	}
	
	//Other methods
	public String getDocumentAuthorName(int _doc_id) {
		int userID = 0;
		try {
			Connection con = SQliteConnection.dbConnecton();
			String sql = "SELECT author_id FROM document_versions INNER JOIN document_based WHERE document_based.doc_id =? AND (document_based.curr_version_id = document_versions.version_id)";
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setString(1, Integer.toString(_doc_id));
			ResultSet rs = pst.executeQuery();
			
			while (rs.next()) {
				userID = rs.getInt(1);
			}
			
			rs.close();
			pst.close();
			con.close();
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error while getting document Author name" + e);
			e.printStackTrace();
		}
		User author = new User(userID);
		author.setUserFirstLastName();
		return author.getUserFirstLastName();
	}
	
	// get author id
	public int getAuthorId(int _doc_id) {
		int userID = 0;
		try {
			Connection con = SQliteConnection.dbConnecton();
			String sql = "SELECT author_id FROM document_versions INNER JOIN document_based WHERE document_based.doc_id =? AND (document_based.curr_version_id = document_versions.version_id)";
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setString(1, Integer.toString(_doc_id));
			ResultSet rs = pst.executeQuery();
			
			while (rs.next()) {
				userID = rs.getInt(1);
			}
			
			rs.close();
			pst.close();
			con.close();
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error while retrieving user_id (Document/getAuthorId): " + e);
			e.printStackTrace();
		}
		return userID;
	}
	
}
