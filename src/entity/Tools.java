package entity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

import util.MyConverters;

public class Tools {

	public static boolean isSameDay(Date sqlDate1, Date sqlDate2) {
		LocalDate localDate1 = sqlDate1.toLocalDate();
		LocalDate localDate2 = sqlDate2.toLocalDate();
		return isSameMonth(sqlDate1, sqlDate2) && localDate1.getDayOfMonth() == localDate2.getDayOfMonth();
	}
  
	public static boolean isSameMonth(Date sqlDate1, Date sqlDate2) {
		LocalDate localDate1 = sqlDate1.toLocalDate();
		LocalDate localDate2 = sqlDate2.toLocalDate();
		return isSameYear(sqlDate1, sqlDate2) && localDate1.getMonth() == localDate2.getMonth();
	}
  
	public static boolean isSameYear(Date sqlDate1, Date sqlDate2) {
		LocalDate localDate1 = sqlDate1.toLocalDate();
		LocalDate localDate2 = sqlDate2.toLocalDate();
		return localDate1.getYear() == localDate2.getYear();
	}
	
	public static Date getFirstDayOfMonth(Date date) {
		LocalDate localDate = date.toLocalDate();
		LocalDate firstDayOfMonth = localDate.withDayOfMonth(1);
		return Date.valueOf(firstDayOfMonth);
	}
	
	public static Date getLastDayOfMonth(Date date) {
		LocalDate localDate = date.toLocalDate();
		LocalDate lastDayOfMonth = localDate.withDayOfMonth(localDate.lengthOfMonth());
		return Date.valueOf(lastDayOfMonth);
	}
	
	public static Date incMonth(Date date) {
		LocalDate localDate = date.toLocalDate().plusMonths(1);
		return Date.valueOf(localDate);
	}
	
	public static Date dateAtMidnight(Date date) {
		return dateAtMidnight(date, 0);
	}

	public static Date dateAtMidnight(Date date, long increment) {
		Date sqlDate = new Date(date.getTime());
		Calendar cal = Calendar.getInstance();
		cal.setTime(sqlDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Date(cal.getTimeInMillis() + increment);
	}

	public static String dateToString(Date date) {
		return dateToString(date, "dd/MM/yyyy");
	}

	public static String dateToString(Date date, String dateFormat) {
		return new SimpleDateFormat(dateFormat).format(date);
	}
	
	public static Date stringToDate(String string) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return new Date(sdf.parse(string.trim()).getTime());
		}
		catch (Exception e) {
			try {
				return new Date(Long.parseLong(string.trim()));
			}
			catch (Exception e2) {
				return null;
			}
		}
	}
	
	public static LocalDate longToLocalDate(long value) {
		return Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	public static Gasto stringToGasto(String string) {
		try {
			String[] split = string.trim().split(" ");
			Date date = Tools.stringToDate(split[0]);
			Valor entrada = new Valor(split[1]);
			Valor saida = new Valor(split[2]);
			String ref = MyConverters.arrayToString(split, 3);
			return new Gasto(date, ref, entrada, saida);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public static Valor stringToDatedValor(String string) {
		try {
			String[] split = string.split(" ");
			return new Valor(stringToDate(split[0]), split[1]);
		}
		catch (Exception e) {
			return null;
		}
	}

}
