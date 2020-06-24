package com.crm.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Tool {
  
  public static LocalDateTime convertToLocalDateTime(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd\'T\'HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
    return dateTime;
  }
  
  public static String convertLocalDateTimeToString(LocalDateTime time) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd\'T\'HH:mm");
    String dateTime = time.format(formatter);
    return dateTime;
    
  }
  
  public static float castDoubleToFloat(Double input) {
    return Float.parseFloat(String.valueOf(input));
  }
  
}
