package system.manager.engine;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DocumentTable {

	private JTable table;
	
	private String[] columnNames = new String[] {"File type", "Document name", "Extension", "File version", "Author", "Created on", "DocId", "DocPath", "UserId"};
	
	private ImageIcon _image = new ImageIcon("./icons/icon_image.png");
	private ImageIcon _txt = new ImageIcon("./icons/icon_txt.png");
	private ImageIcon _doc = new ImageIcon("./icons/icon_doc.png");
	private ImageIcon _pdf = new ImageIcon("./icons/icon_pdf.png");
	private ImageIcon _excel = new ImageIcon("./icons/icon_excel.png");
	private ImageIcon _java = new ImageIcon("./icons/icon_java.png");
	private ImageIcon _unknown = new ImageIcon("./icons/icon_unknown.png");
	
	//Getters and setters
	public JTable getTable() {
		return this.table;
	}
	
	public void setTable() {
		int tableRows = 0;
		
		//Prepare number of rows for Object[][]
		try {
			
			Connection conn = SQliteConnection.dbConnecton();
			String sql = "SELECT defaultver FROM document_versions WHERE defaultver=?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, "true");
			ResultSet rs = pst.executeQuery();
									
			while(rs.next()) {
				tableRows++;
			}
			
			pst.close();
			rs.close();
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "There was an exception while retieving number of rows for table: " + e);
		}
		
		Object[][] data = new Object[tableRows][9];
		User tableAuthor = new User();
		
		//Prepare data for the table ie. Object[][]
		try {
			
			Connection conn = SQliteConnection.dbConnecton();
			String sql = "SELECT document_based.docname, document_based.type, document_versions.version, document_versions.author_id, document_versions.datecreated, document_based.doc_id, document_versions.filepath FROM document_based INNER JOIN document_versions ON document_based.curr_version_id=document_versions.version_id ORDER BY document_based.docname";
			PreparedStatement pst = conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			
			int counter = 0;
			while(rs.next()) {
				data[counter][1] = rs.getString(1);
				
				data[counter][2] = rs.getString(2);
				data[counter][0] = iconType(rs.getString(2));
				
				data[counter][3] = rs.getString(3);
				
				tableAuthor.setUserId(rs.getInt(4));
				tableAuthor.setUserFirstLastName();
				data[counter][4] = tableAuthor.getUserFirstLastName();
				
				data[counter][5] = rs.getString(5);
				data[counter][6] = rs.getInt(6);
				data[counter][7] = rs.getString(7);
				data[counter][8] = rs.getInt(4);
				counter++;
			}
			
			pst.close();
			rs.close();
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "There was an exception while forming document table: " + e);
		}
		
		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		this.table = new JTable(model) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Class<?> getColumnClass(int column) {
				return getValueAt(0, column).getClass();
			}
			
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
			
		};
		this.table.setRowHeight(34);
		this.table.getColumn("File type").setMaxWidth(52);
		this.table.setSelectionModel(new ForcedListSelectionModel());
		this.table.removeColumn(table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("Extension")));
		this.table.removeColumn(table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("DocId")));
		this.table.removeColumn(table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("DocPath")));
		this.table.removeColumn(table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("UserId")));
		
	}
	
	//Constructors
	
	public DocumentTable() {
		//An empty constructor
	}
	
	//Methods
	public ImageIcon iconType(String fileType) {
		if(fileType.matches(".jpg|.png|.tif|.jpeg|.gif")) {
			return this._image;
		} else if (fileType.equals(".txt")) {
			return this._txt;
		} else if (fileType.matches(".doc|.docx")) {
			return this._doc;
		} else if (fileType.equals(".pdf")) {
			return this._pdf;
		} else if (fileType.matches(".xls|.xlsx")) {
			return this._excel;
		} else if (fileType.equals(".java")) {
			return this._java;
		} else {
			return this._unknown;
		}
	}
	
}
