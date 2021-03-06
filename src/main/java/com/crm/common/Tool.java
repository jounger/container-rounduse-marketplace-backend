package com.crm.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import com.crm.exception.InternalException;

public class Tool {

  public static LocalDateTime convertToLocalDateTime(String date) {
    if (date.trim().length() <= 10) {
      date += "T00:00";
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd\'T\'HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
    return dateTime;
  }

  public static String convertLocalDateTimeToString(LocalDateTime time) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd\'T\'HH:mm");
    String dateTime = time.format(formatter);
    return dateTime;

  }

  /*
   * 
   */
  public static boolean isBlank(String string) {
    if (string != null) {
      string = string.trim();
      if (string.isEmpty()) {
        return true;
      } else {
        return false;
      }
    }
    return false;
  }

  public static boolean isEqual(String valOld, String valNew) {
    if (valNew != null) {
      valNew = valNew.trim();
      if (!valNew.isEmpty() && valNew.equals(valOld)) {
        return true;
      } else {
        return false;
      }
    } else {
      return true;
    }
  }

  public static boolean isEqual(Boolean valOld, String valNew) {
    valNew = valNew.trim();
    if (!valNew.isEmpty() && valOld.equals(Boolean.valueOf(valNew))) {
      return true;
    } else {
      return false;
    }
  }

  public static boolean isEqual(Double valOld, String valNew) {
    valNew = valNew.trim();
    if (!isBlank(valNew)) {
      try {
        Double doubleNew = Double.valueOf(valNew);
        if (valOld.equals(doubleNew)) {
          return true;
        }
      } catch (Exception e) {
        throw new InternalException("Parameter must be Double.");
      }
    }
    return false;
  }

  public static boolean isEqual(Integer valOld, String valNew) {
    valNew = valNew.trim();
    if (!isBlank(valNew)) {
      try {
        Integer intNew = Integer.valueOf(valNew);
        if (valOld.equals(intNew)) {
          return true;
        }
      } catch (Exception e) {
        throw new InternalException("Parameter must be Integer.");
      }
    }
    return false;
  }

  // generate random String with length of 32
  public static String randomString() {
    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = 32;
    Random random = new Random();

    String generatedString = random.ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

    return generatedString;
  }

}
