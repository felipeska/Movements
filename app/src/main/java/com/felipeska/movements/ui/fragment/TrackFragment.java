package com.felipeska.movements.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.felipeska.movements.R;
import com.todddavies.components.progressbar.ProgressWheel;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author felipeska
 */
public class TrackFragment extends Fragment {

  @InjectView(R.id.latitude)
  TextView mTextViewLatitude;
  @InjectView(R.id.longitude)
  TextView mTextViewLongitude;
  @InjectView(R.id.speed)
  TextView mTextViewSpeed;

  @InjectView(R.id.pw_spinner)
  ProgressWheel mProgressWheel;

  @OnClick(R.id.buttonStart)
  void startTracking() {
  }

  @OnClick(R.id.buttonStop)
  void stopTracking() {
  }

  public TrackFragment() {
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
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.reset(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    mProgressWheel.spin();
  }

  @Override
  public void onPause() {
    super.onPause();
    mProgressWheel.stopSpinning();
  }

}
