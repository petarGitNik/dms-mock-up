package system.manager.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ChangeTable {

	private JTable table;
	
	private String[] columnNames = new String[] {"Stage type", "Document name", "Author", "Last change", "Stage", "Status", "StateId", "DocId", "DocPath", "UserId", "WorkflowId"};
	
	private ImageIcon _submission = new ImageIcon("./icons/icon_submission.png");
	private ImageIcon _review = new ImageIcon("./icons/icon-review.png");
	private ImageIcon _desktop = new ImageIcon("./icons/icon-desktop.png");
	private ImageIcon _production = new ImageIcon("./icons/icon_production.png");
	private ImageIcon _close = new ImageIcon("./icons/icon_close.png");
	
	private Connection con = SQliteConnection.dbConnecton();
	private PreparedStatement pst;
	private ResultSet rs;
	
	//Getters and setters
	public JTable getChangeTable() {
		return this.table;
	}
	
	public void setChangeTable(JTable table) {
		this.table = table;
	}
	
	//Constructor
	public ChangeTable() {
		//Empty constructor
	}
	
	//Other methods
	public void updateTable(int option, int _user_id) {
		int tableRows = -1;
		
		//Count number of rows for full table
		try {
			String sql = retrieveNumberOfRowsSQLForTable(option, _user_id);
			pst = con.prepareStatement(sql);
			rs = pst.executeQuery();
			
			int count = 0;
			while (rs.next()) {
				count++;
			}
			tableRows = count;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error while counting table rows in change table: " + e);
			e.printStackTrace();
		}
		
		Object[][] data = new Object[tableRows][this.columnNames.length];
		
		//Retrieve data from database to insert into table
		try {
			String sql = "SELECT credentials.firstname, "	//(1)
					+ "credentials.lastname, "				//(2)
					+ "document_based.docname, "			//(3)
					+ "state_type.name, "					//(4)
					+ "state_type.status, "					//(5)
					+ "state.created_on, "					//(6)
					+ "state.state_id, "					//(7)
					+ "state.state_type_id, "				//(8)
					+ "state.document_id, "					//(9)
					+ "document_versions.filepath, "		//(10)
					+ "document_versions.author_id, "		//(11)
					+ "state.workflow_id "					//(12)
					+ "FROM state_type "
					+ "INNER JOIN state ON state.state_type_id=state_type.state_type_id "
					+ "INNER JOIN document_versions ON state.document_id=document_versions.doc_id "
					+ "INNER JOIN document_based ON document_based.doc_id=document_versions.doc_id "
					+ "INNER JOIN credentials ON document_versions.author_id=credentials.user_id "
					+ innerJoin(option)
					+ "WHERE state.is_active='true' AND state.state_type_id NOT IN (7) AND document_versions.defaultver='true' "
					+ andTheseAdditionalConstraints(option, _user_id)
					+ "ORDER BY created_on DESC";
			pst = con.prepareStatement(sql);
			rs = pst.executeQuery();
			
			int counter = 0;
			while (rs.next()) {
				//Stage type 		[0]
				data[counter][0] = stateIcon(rs.getInt(8));
				
				//Document name 	[1]
				data[counter][1] = rs.getString(3);
				
				//Author			[2]
				data[counter][2] = rs.getString(1) + " " + rs.getString(2);
				
				//Last Change		[3]
				data[counter][3] = rs.getString(6);
				
				//Stage				[4]
				data[counter][4] = rs.getString(4);
				
				//Status			[5]
				data[counter][5] = rs.getString(5);
				
				//Non-visible columns
				//StateId			[6]
				data[counter][6] = rs.getInt(7);
				
				//DocId				[7]
				data[counter][7] = rs.getInt(9);
				
				//DocPath			[8]
				data[counter][8] = rs.getString(10);
				
				//AuthorId			[9]
				data[counter][9] = rs.getInt(11);
				
				//WorkflowId		[10]
				data[counter][10] = rs.getInt(12);
				
				//next row
				counter++;
			}
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error while forming change table: " + e);
			e.printStackTrace();
		}
		
		//Ostalo o tabeli
		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		this.table = new JTable(model) {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			//This will render icons in table
			public Class<?> getColumnClass(int column) {
				return getValueAt(0, column).getClass();
			}
			
			public boolean isCellEditable(int row, int column) {
				return false;
			}
			
		};
		
		this.table.setRowHeight(34);
		this.table.getColumn("Stage type").setMaxWidth(52);
		this.table.setSelectionModel(new ForcedListSelectionModel());
		this.table.removeColumn(table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("StateId")));
		this.table.removeColumn(table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("DocId")));
		this.table.removeColumn(table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("DocPath")));
		this.table.removeColumn(table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("UserId")));
		this.table.removeColumn(table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("WorkflowId")));
		
	}
	
	private ImageIcon stateIcon(int _state_id) {
		ImageIcon stateIcon = null;
		switch (_state_id) {
		case 1:
			stateIcon = _submission;
			break;
		case 2:
			stateIcon = _review;
			break;
		case 3:
			stateIcon = _desktop;
			break;
		case 4:
			stateIcon = _review;
			break;
		case 5:
			stateIcon = _production;
			break;
		case 6:
			stateIcon = _close;
			break;
		default:
			//Nothing :'(
			break;
		}
		return stateIcon;
	}

	public String innerJoin(int option) {
		String additionalSql = " ";
		//option = 1 -> Dashboard tab
		//option = 2 -> Change tab
		if (option == 1) {
			additionalSql = " INNER JOIN stakeholders ON stakeholders.workflow_id=state.workflow_id ";
		}
		return additionalSql;
	}
	
	public String andTheseAdditionalConstraints(int option, int _user_id) {
		String additionalSql = " ";
		//option = 1 -> Dashboard tab
		//option = 2 -> Change tab
		if (option == 1) {
			additionalSql = " AND stakeholders.user_id='"+ _user_id +"' AND state_type.role_permission = (SELECT credentials.role FROM credentials WHERE credentials.user_id='"+ _user_id +"') ";
		}
		return additionalSql;
	}
	
	public String retrieveNumberOfRowsSQLForTable(int option, int _user_id) {
		String sql = null;
		switch (option) {
		case 1:
			sql = "SELECT state.state_id FROM state_type "
					+ "INNER JOIN state ON state.state_type_id=state_type.state_type_id "
					+ "INNER JOIN document_versions ON state.document_id=document_versions.doc_id "
					+ "INNER JOIN document_based ON document_based.doc_id=document_versions.doc_id "
					+ "INNER JOIN credentials ON document_versions.author_id=credentials.user_id "
					+ "INNER JOIN stakeholders ON stakeholders.workflow_id=state.workflow_id "
					+ "WHERE state.is_active='true' AND state.state_type_id NOT IN (7) AND document_versions.defaultver='true' "
					+ "AND stakeholders.user_id='"+ _user_id +"' AND state_type.role_permission = (SELECT credentials.role FROM credentials WHERE credentials.user_id='"+ _user_id +"') "
					+ "ORDER BY created_on";
			break;
		case 2:
			sql = "SELECT state_id FROM state WHERE is_active='true' AND state_type_id NOT IN (7)";
			break;
		default:
			//this should not have happened :'(
			break;
		}
		return sql;
	}
	
}
