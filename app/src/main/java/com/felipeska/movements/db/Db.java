package com.felipeska.movements.db;

import android.database.Cursor;

/**
 * @author felipeska
 */
public final class Db {

  private Db() {
    throw new AssertionError("No instances.");
  }

  public static String getString(Cursor cursor, String columnName) {
    return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
  }

  public static long getLong(Cursor cursor, String columnName) {
    return cursor.getLong(cursor.getColumnIndexOrThrow(columnName));
  }

  public static float getFloat(Cursor cursor, String columnName) {
    return cursor.getFloat(cursor.getColumnIndexOrThrow(columnName));
  }
}