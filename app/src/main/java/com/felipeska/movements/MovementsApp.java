package com.felipeska.movements;

import android.app.Application;
import android.content.Context;

import dagger.ObjectGraph;

/**
 * @author felipeska
 */
public class MovementsApp extends Application {

  private ObjectGraph objectGraph;

  public static ObjectGraph objectGraph(Context context) {
    return ((MovementsApp) context.getApplicationContext()).objectGraph;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    objectGraph = ObjectGraph.create(new MovementsModule(this));
  }
}
