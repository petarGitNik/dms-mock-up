package system.manager.engine;

public class ComboItem {

	private int id;
	private String title;
	
	//Getters and setters
	
	public int getItemId() {
		return this.id;
	}
	
	public String getItemTitle() {
		return this.title;
	}
	
	//Constructor
	public ComboItem(int id, String title) {
		this.id = id;
		this.title = title;
	}
	
	//Other methods
	
	public String toString() {
		return title;
	}
	
}
