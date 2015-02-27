package com.felipeska.movements.util;

/**
 * @author felipeska
 */
public final class CheckUtils {

  private CheckUtils() {
  }

  public static void checkNotNull(Object argument, String msg) {
    if (argument == null) {
      throw new NullPointerException(msg);
    }
  }
  public static boolean isNull(Object object) {
    return object == null;
  }
}