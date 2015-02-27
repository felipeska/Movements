package com.felipeska.movements.track;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.felipeska.movements.track.bus.TrackBusProvider;
import com.felipeska.movements.track.event.ConnectionEvent;
import com.felipeska.movements.track.event.DisconnectionEvent;
import com.felipeska.movements.track.event.LocationEvent;
import com.felipeska.movements.track.event.PlayServicesConnectionEvent;
import com.felipeska.movements.track.priority.Priority;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.otto.Produce;

import java.util.concurrent.TimeUnit;

import static com.felipeska.movements.util.CheckUtils.checkNotNull;
import static com.felipeska.movements.util.CheckUtils.isNull;

/**
 * @author felipeska
 */
public class TrackController implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

  public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
  private static final long DEFAULT_UPDATE_INTERVAL =
          TimeUnit.SECONDS.toMillis(UPDATE_INTERVAL_IN_SECONDS);
  public static final int FASTEST_INTERVAL_IN_SECONDS = 1;
  private static final long DEFAULT_FASTEST_INTERVAL =
          TimeUnit.SECONDS.toMillis(FASTEST_INTERVAL_IN_SECONDS);
  private static final String TAG_LOG = TrackController.class.getSimpleName();
  private static final Priority DEFAULT_PRIORITY = Priority.PRIORITY_HIGH_ACCURACY;

  private final LocationListener mLocationListener = new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {
      TrackBusProvider.getInstance().post(produceLocationEvent(
              location));
    }

  };

  private GoogleApiClient mGoogleApiClient;
  private Priority mPriority;
  private long mIntervalUpdate;
  private long mFastInterval;

  TrackController(Builder builder) {

    mPriority = builder.mPriority;
    mIntervalUpdate = builder.mInterval;
    mFastInterval = builder.mFastInterval;

    if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(builder.mContext)
            == ConnectionResult.SUCCESS)
      mGoogleApiClient = new GoogleApiClient.Builder(builder.mContext)
              .addConnectionCallbacks(this)
              .addOnConnectionFailedListener(this)
              .addApi(LocationServices.API)
              .build();
    else TrackBusProvider.getInstance().
            post(produceFailedConnectionEvent(GooglePlayServicesUtil
                    .isGooglePlayServicesAvailable(builder.mContext)));
  }

  public static Builder withContext(Context context) {
    return new Builder(context);
  }

  protected LocationRequest getLocationRequest() {
    return new LocationRequest().setInterval(mIntervalUpdate)
            .setFastestInterval(mFastInterval)
            .setPriority(mPriority.getValue());
  }

  public void startTracking() {

    if (!isNull(mGoogleApiClient)) {
      if (!mGoogleApiClient.isConnected()
              && !mGoogleApiClient.isConnecting()) {
        mGoogleApiClient.connect();
      } else {
        Log.w(TAG_LOG, "Location Client (Play Services) Already Connected");
      }
    }
  }

  public void stopTracking() {
    if (!isNull(mGoogleApiClient)) {
      if (mGoogleApiClient.isConnected()) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
        mGoogleApiClient.disconnect();
        TrackBusProvider.getInstance().post(produceDisconnectLocationEvent());
      } else {
        Log.w(TAG_LOG, "Location Client (Play Services) not Disconnect if not Connected");
      }
    }
  }

  private void requestLocationUpdates() {
    if (!isNull(mGoogleApiClient)) {
      if (mGoogleApiClient.isConnected())
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                getLocationRequest(), mLocationListener);
    }
  }

  @Override
  public void onConnected(Bundle bundle) {
    requestLocationUpdates();
    TrackBusProvider.getInstance().post(produceConnectLocationEvent());
  }

  @Override
  public void onConnectionSuspended(int cause) {
    Log.w(TAG_LOG, "Location Client (Play Services) suspend connection cause number: " + cause);
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    TrackBusProvider.getInstance().
            post(produceFailedConnectionEvent(connectionResult.getErrorCode()));
  }

  @Produce
  public PlayServicesConnectionEvent produceFailedConnectionEvent(int status) {
    return new PlayServicesConnectionEvent(status);
  }

  @Produce
  public LocationEvent produceLocationEvent(
          Location location) {
    return new LocationEvent(location);
  }

  @Produce
  public DisconnectionEvent produceDisconnectLocationEvent() {
    return new DisconnectionEvent();
  }

  @Produce
  public ConnectionEvent produceConnectLocationEvent() {
    return new ConnectionEvent();
  }

  @Override
  public String toString() {
    return "PlayServicesLocationController{" +
            "priority='" + mPriority + '\'' +
            ", interval='" + mIntervalUpdate + '\'' +
            ", fastInterval='" + mFastInterval + '\'' +
            "}";
  }

  public static class Builder {

    private final Context mContext;
    private Priority mPriority;
    private long mInterval;
    private long mFastInterval;

    private Builder(Context context) {
      checkNotNull(context, "Invalid context...");
      mContext = context;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Builder priority(Priority priority) {
      return this;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Builder interval(int interval) {
      return this;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Builder fastInterval(int fastInterval) {
      mFastInterval = TimeUnit.SECONDS.toMillis(fastInterval);
      return this;
    }

    public TrackController build() {
      if (isNull(mPriority)) {
        mPriority = DEFAULT_PRIORITY;
      }
      if (mInterval <= 0) {
        mInterval = DEFAULT_UPDATE_INTERVAL;
      }
      if (mFastInterval <= 0) {
        mFastInterval = DEFAULT_FASTEST_INTERVAL;
      }
      return new TrackController(this);
    }
  }
}
