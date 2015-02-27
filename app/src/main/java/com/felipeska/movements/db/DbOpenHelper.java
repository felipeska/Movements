package com.felipeska.movements.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author felipeska
 */
public class DbOpenHelper extends SQLiteOpenHelper {

  private static final int VERSION = 1;


  private static final String CREATE_LOCATION = ""
          + "CREATE TABLE " + Location.TABLE + "("
          + Location.ID + " INTEGER NOT NULL PRIMARY KEY, "
          + Location.LATITUDE + " REAL NOT NULL DEFAULT 0, "
          + Location.LONGITUDE + " REAL NOT NULL DEFAULT 0, "
          + Location.SPEED + " REAL NOT NULL DEFAULT 0, "
          + Location.DATE + " TEXT NOT NULL"
          + ")";

  public DbOpenHelper(Context context) {
    super(context, "movements.db", null /* factory */, VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_LOCATION);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }
}
