package com.felipeska.movements.track;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.felipeska.movements.track.bus.TrackBusProvider;
import com.felipeska.movements.track.event.LocationEvent;
import com.felipeska.movements.track.event.PlayServicesConnectionEvent;
import com.squareup.otto.Subscribe;

import static com.felipeska.movements.util.CheckUtils.isNull;

/**
 * @author felipeska
 */
public class TrackService extends Service {

  private static final String LOG_TAG = TrackService.class.getSimpleName();
  private boolean running;
  private TrackController mTrackController;

  public static Intent newIntent(Context context) {
    return new Intent(context, TrackService.class);
  }

  @Override
  public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
    if (running) {
      Log.d(LOG_TAG, "Already running! Ignoring...");
      return START_STICKY;
    }
    Log.d(LOG_TAG, "Starting up!");
    running = true;
    TrackBusProvider.getInstance().register(this);
    if (isNull(mTrackController)) {
      mTrackController = TrackController.withContext(this).build();
    }
    mTrackController.startTracking();
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    if (running) {
      mTrackController.stopTracking();
      mTrackController = null;
    }
    TrackBusProvider.getInstance().unregister(this);
    super.onDestroy();
  }

  @Override
  public IBinder onBind(Intent intent) {
    throw new AssertionError("Not supported.");
  }

  @Subscribe
  public void onLocationChanged(LocationEvent event) {
    if (event.location != null) {
      storeLocation(event.location);
    }
  }

  @Subscribe
  public void onPlayServicesConnectionEvent(PlayServicesConnectionEvent event){
    Log.d(LOG_TAG,"code status playservice: "+event.status);
  }

  private void storeLocation(Location location) {
    Log.d(LOG_TAG, "Save bitch!");
  }
}
