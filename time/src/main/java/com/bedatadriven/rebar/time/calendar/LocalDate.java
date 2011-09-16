package com.bedatadriven.rebar.time.calendar;

import java.io.Serializable;
import java.util.Date;

import com.bedatadriven.rebar.time.CalendricalException;

/**
 * A date class that is independent of a timezone. 
 * 
 * <p>This class provides an alternate way to model dates that can be
 * safely sent between the server and clients in different time zones, and 
 * can be used on both the client and server. 
 * 
 * <p>This class is loosely modelled after the javax.time classes from JSR 310
 * that will some day replace the much-despised java.util.Date and java.util.Calendar API.  
 * 
 */
public class LocalDate implements Serializable, Comparable<LocalDate> {
	
	private int year;
	private int monthOfYear;
	private int dayOfMonth;
	
	public LocalDate() {
		this(new Date());
	}
	
	public LocalDate(int year, int monthOfYear, int dayOfMonth) {
		this.year = year;
		this.monthOfYear = monthOfYear;
		this.dayOfMonth = dayOfMonth;
	}
	
	@SuppressWarnings("deprecation")
	public LocalDate(Date date) {
		this.year = date.getYear()+1900;
		this.monthOfYear = date.getMonth()+1;
		this.dayOfMonth = date.getDate();
	}

	/**
	 * 
	 * Io ISO-8601 
	 */
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * Gets the month-of-year field
	 * 
	 * @return the month-of-year field, 1-12
	 */
	public int getMonthOfYear() {
		return monthOfYear;
	}

	public void setMonthOfYear(int monthOfYear) {
		this.monthOfYear = monthOfYear;
	}

	/**
	 * 
	 * @return the day-of-month, from 1 to 31
	 */
	public int getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}
	
	/**
	 * 
	 * @return a java.util.Date instance representing the instant at midnight on this date
	 * in the browser's timezone or the jre's default timezone.  
	 */
	public Date atMidnightInMyTimezone() {
		return new Date(year-1900, monthOfYear-1, dayOfMonth);
	}
	
	/**
	 * Returns this data as an ISO-8601 string
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(year);
		s.append("-");
		if(monthOfYear < 10) {
			s.append("0");
		}
		s.append(monthOfYear);
		s.append("-");
		if(dayOfMonth < 10) {
			s.append("0");
		}
		s.append(dayOfMonth);
		return s.toString();
	}
	
	/**
	 * Obtains an instance of LocalDate from a text string such as 2007-12-03.
	 * 
	 * <p>The following format is accepted in ASCII:
	 *
	 * <p>{Year}-{MonthOfYear}-{DayOfMonth}
	 * 
	 * <p>The year has between 4 and 10 digits with values from MIN_YEAR to MAX_YEAR. If there are more than 4 digits then the year must be prefixed with the plus symbol. Negative years are allowed, but not negative zero.
	 * 	
	 * <p>The month-of-year has 2 digits with values from 1 to 12.
     * 
     * <p>The day-of-month has 2 digits with values from 1 to 31 appropriate to the month.
	 *
	 * @param text the text to parse such as '2007-12-03', not null
	 * @return the parsed local date, never null
	 */
	public static LocalDate parse(String text) {
		int dash1 = text.indexOf('-', 1);
		if(dash1 == -1) {
			throw new CalendricalException("Cannot parse '" + text + "'");
		}
		int dash2 = text.indexOf('-', dash1+1);
		if(dash2 == -1) {
			throw new CalendricalException("Cannot parse '" + text + "'");
		}		
		int year = Integer.parseInt(text.substring(0, dash1));
		int month = Integer.parseInt(text.substring(dash1+1, dash2));
		int day = Integer.parseInt(text.substring(dash2+1));
		
		return new LocalDate(year, month, day);
	}

	public boolean before(LocalDate toDate) {
		return compareTo(toDate) < 0;
	}
	
	public boolean after(LocalDate toDate) {
		return compareTo(toDate) > 0;
	}

	public int compareTo(LocalDate otherDate) {
		if(year != otherDate.year) {
			return year - otherDate.year; 
		}
		if(monthOfYear != otherDate.monthOfYear) {
			return monthOfYear - otherDate.monthOfYear;
		}
		
		return dayOfMonth - otherDate.dayOfMonth;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dayOfMonth;
		result = prime * result + monthOfYear;
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocalDate other = (LocalDate) obj;
		if (dayOfMonth != other.dayOfMonth)
			return false;
		if (monthOfYear != other.monthOfYear)
			return false;
		if (year != other.year)
			return false;
		return true;
	}
	
	
}
