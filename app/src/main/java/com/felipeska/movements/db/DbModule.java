package com.felipeska.movements.db;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.felipeska.movements.BuildConfig;
import com.squareup.sqlbrite.SqlBrite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public final class DbModule {
  @Provides
  @Singleton
  SQLiteOpenHelper provideOpenHelper(Application application) {
    return new DbOpenHelper(application);
  }

  @Provides
  @Singleton
  SqlBrite provideSqlBrite(SQLiteOpenHelper openHelper) {
    SqlBrite db = SqlBrite.create(openHelper);

    if (BuildConfig.DEBUG) {
      db.setLogger(new SqlBrite.Logger() {
        @Override
        public void log(String message) {
          Log.v("Database", message);
        }
      });
      db.setLoggingEnabled(true);
    }
    return db;
  }
}
