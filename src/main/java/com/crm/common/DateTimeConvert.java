package com.crm.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeConvert {

	public static LocalDateTime convertToLocalDateTime(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm"); 
		LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
		return dateTime;
	}
}
