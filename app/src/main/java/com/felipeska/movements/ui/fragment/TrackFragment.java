package com.felipeska.movements.ui.fragment;

import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.felipeska.movements.R;
import com.felipeska.movements.track.TrackController;
import com.felipeska.movements.track.TrackService;
import com.felipeska.movements.track.bus.TrackBusProvider;
import com.felipeska.movements.track.event.DisconnectionEvent;
import com.felipeska.movements.track.event.LocationEvent;
import com.felipeska.movements.util.DateUtils;
import com.squareup.otto.Subscribe;
import com.todddavies.components.progressbar.ProgressWheel;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author felipeska
 */
public class TrackFragment extends Fragment {

  private final static String FORMAT_DISPLAY_DATA = "%s \t %s";

  @InjectView(R.id.latitude)
  TextView mTextViewLatitude;
  @InjectView(R.id.longitude)
  TextView mTextViewLongitude;
  @InjectView(R.id.speed)
  TextView mTextViewSpeed;
  @InjectView(R.id.date)
  TextView mTextViewDate;

  @InjectView(R.id.pw_spinner)
  ProgressWheel mProgressWheel;
  private TrackController mTrackController;
  private Location mLocation;
  private Resources mRes;

  public TrackFragment() {
  }

  @OnClick(R.id.buttonStart)
  void startTracking() {
    getActivity().startService(TrackService.newIntent(getActivity().getApplicationContext()));
  }

  @OnClick(R.id.buttonStop)
  void stopTracking() {
    getActivity().stopService(TrackService.newIntent(getActivity().getApplicationContext()));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_track, container, false);
    ButterKnife.inject(this, rootView);
    return rootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    if (getActivity() != null && isAdded()) {
      mRes = getResources();
      initializeTrackController();
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.reset(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    TrackBusProvider.getInstance().register(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    stopWheel();
    TrackBusProvider.getInstance().unregister(this);
  }

  @Subscribe
  public void onTrackingChanged(LocationEvent event) {
    mLocation = event.location;
    if (mLocation != null) {
      startWheel();
      updateUI();
    }
  }

  @Subscribe
  public void ontTrackingStoped(DisconnectionEvent event) {
    stopWheel();
  }

  void stopWheel() {
    if (mProgressWheel.isSpinning()) {
      mProgressWheel.stopSpinning();
    }
  }

  void startWheel() {
    if (!mProgressWheel.isSpinning()) {
      mProgressWheel.spin();
    }
  }

  void updateUI() {
    mTextViewLatitude.setText(String.format(FORMAT_DISPLAY_DATA, mRes.getText(R.string.item_latitude),
            mLocation.getLatitude()));
    mTextViewLongitude.setText(String.format(FORMAT_DISPLAY_DATA, mRes.getText(R.string.item_longitude),
            mLocation.getLongitude()));
    mTextViewSpeed.setText(String.format(FORMAT_DISPLAY_DATA + " m/s", mRes.getText(R.string.item_speed),
            mLocation.getSpeed()));
    mTextViewDate.setText(String.format(FORMAT_DISPLAY_DATA, mRes.getText(R.string.item_last_date),
            DateUtils.dateForHumans(mLocation.getTime())));
    Log.d(TrackFragment.class.getSimpleName(), mTrackController.toString());
  }

  void initializeTrackController() {
    mTrackController = TrackController.withContext(getActivity().getApplicationContext()).build();
  }
}
