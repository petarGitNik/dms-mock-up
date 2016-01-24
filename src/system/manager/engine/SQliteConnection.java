package system.manager.engine;


import java.sql.Connection;
import java.sql.DriverManager;

public class SQliteConnection {

	public SQliteConnection() {
		//Empty constructor
	}
	
	public static Connection dbConnecton() {
		
		try {
			Class.forName("org.sqlite.JDBC");
			String dbPath = "jdbc:sqlite:./db/mainDataBase.sqlite";
			Connection conn = DriverManager.getConnection(dbPath);
			return conn;
		} catch(Exception ex) {
			
			return null;
		}
		
	}
	
}
