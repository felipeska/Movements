package com.felipeska.movements.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author felipeska
 */
public final class DateUtils {

  private DateUtils(){
    // No instances
  }

  public static String dateForHumans(long milliseconds){
    Date date = new Date(milliseconds);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.format(date);
  }
}
