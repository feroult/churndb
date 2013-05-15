package churndb.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Commit {

	private int year;
	
	private int month;
	
	private int day;
	
	private int hour;
	
	private int minute;

	private void initCommitTime(int commitTime) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date(commitTime * 1000L));
		
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setCommitTime(int commitTime) {
		initCommitTime(commitTime);		
	}
	
}
