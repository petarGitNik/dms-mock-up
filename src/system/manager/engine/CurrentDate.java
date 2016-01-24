package system.manager.engine;


import java.util.Calendar;
import java.util.GregorianCalendar;

public class CurrentDate {

	String _datecreated;
	
	public void setDate() {
		//Empty method
	}
	
	public String getDate() {
		return this._datecreated;
	}
	
	public CurrentDate() {
		GregorianCalendar date = new GregorianCalendar();
		int day = date.get(Calendar.DAY_OF_MONTH);
		int month = date.get(Calendar.MONTH);
		int year = date.get(Calendar.YEAR);
		this._datecreated = day + "/" + (month+1) + "/" + year;
	}
	
}
