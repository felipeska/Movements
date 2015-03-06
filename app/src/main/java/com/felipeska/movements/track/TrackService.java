package com.felipeska.movements.track;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.felipeska.movements.MovementsApp;
import com.felipeska.movements.track.bus.TrackBusProvider;
import com.felipeska.movements.track.event.LocationEvent;
import com.felipeska.movements.track.event.PlayServicesConnectionEvent;
import com.felipeska.movements.util.DateUtils;
import com.squareup.otto.Subscribe;
import com.squareup.sqlbrite.SqlBrite;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.felipeska.movements.util.CheckUtils.isNull;

/**
 * @author felipeska
 */
public class TrackService extends Service {

  private static final String LOG_TAG = TrackService.class.getSimpleName();
  @Inject
  SqlBrite db;
  private boolean running;
  private TrackController mTrackController;
  private double lastLatitude;
  private double lastLongitude;

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
    MovementsApp.objectGraph(this).inject(this);
    if (isNull(mTrackController)) {
      mTrackController = TrackController.withContext(this).build();
    }
    observeArrivedLocations();
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
      if(lastLatitude != event.location.getLatitude()
              && lastLongitude != event.location.getLongitude()){
        Log.d(LOG_TAG, "last latitude " + lastLatitude);
        Log.d(LOG_TAG, "last longitude " + lastLongitude);
        Log.d(LOG_TAG, "new latitude " + event.location.getLatitude());
        Log.d(LOG_TAG, "new longitude " + event.location.getLongitude());
        storeLocation(event.location);
      }
    }
  }

  @Subscribe
  public void onPlayServicesConnectionEvent(PlayServicesConnectionEvent event) {
    Log.d(LOG_TAG, "code status play service: " + event.status);
  }

  private void observeArrivedLocations() {
    Observable<SqlBrite.Query> locations = db.createQuery(com.felipeska.movements.db.Location.TABLE,
            com.felipeska.movements.db.Location.QUERY);
    locations.subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(Schedulers.io());
  }

  private void storeLocation(Location location) {
    lastLatitude = location.getLatitude();
    lastLongitude = location.getLongitude();
    db.insert(com.felipeska.movements.db.Location.TABLE,
            new com.felipeska.movements.db.Location.Builder()
                    .latitude((float) location.getLatitude())
                    .longitude((float) location.getLongitude())
                    .speed(location.getSpeed())
                    .date(DateUtils.dateForHumans(location.getTime()))
                    .build());
  }
}
