package com.felipeska.movements.db;

/**
 * @author felipeska
 */

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import auto.parcel.AutoParcel;
import rx.functions.Func1;

import static com.squareup.sqlbrite.SqlBrite.Query;

@AutoParcel
public abstract class Location {
  public static final String TABLE = "location";

  public static final String ID = "_id";
  public static final String LATITUDE = "latitude";
  public static final String LONGITUDE = "longitude";
  public static final String SPEED = "speed";
  public static final String DATE = "date";

  public static final String QUERY = "SELECT * FROM "+TABLE;

  public static final Func1<Query, List<Location>> MAP = new Func1<Query, List<Location>>() {
    @Override
    public List<Location> call(Query query) {
      Cursor cursor = query.run();
      try {
        List<Location> values = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext()) {
          long id = Db.getLong(cursor, ID);
          float latitude = Db.getFloat(cursor, LATITUDE);
          float longitude = Db.getFloat(cursor, LONGITUDE);
          float speed = Db.getFloat(cursor, SPEED);
          String date = Db.getString(cursor, DATE);
          values.add(new AutoParcel_Location(id, latitude, longitude, speed, date));
        }
        return values;
      } finally {
        cursor.close();
      }
    }
  };

  public abstract long id();

  public abstract float latitude();

  public abstract float longitude();

  public abstract float speed();

  public abstract String date();

  public static final class Builder {
    private final ContentValues values = new ContentValues();

    public Builder id(long id) {
      values.put(ID, id);
      return this;
    }

    public Builder latitude(float latitude) {
      values.put(LATITUDE, latitude);
      return this;
    }

    public Builder longitude(float longitude) {
      values.put(LONGITUDE, longitude);
      return this;
    }

    public Builder speed(float speed) {
      values.put(SPEED, speed);
      return this;
    }

    public Builder date(String date) {
      values.put(DATE, date);
      return this;
    }

    public ContentValues build() {
      return values;
    }
  }
}