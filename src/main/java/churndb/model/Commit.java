package churndb.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Commit {

	private int year;

	private int month;

	private int dayOfMonth;

	private int hourOfDay;

	private int minute;

	private void initCommitDate(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}

	public int getHourOfDay() {
		return hourOfDay;
	}

	public int getMinute() {
		return minute;
	}

	public void setCommitDate(Date date) {
		initCommitDate(date);
	}
}
