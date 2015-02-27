package com.felipeska.movements.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.felipeska.movements.R;

/**
 * @author felipeska
 */
public class TrackFragment extends Fragment {

  public TrackFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_track, container, false);
    return rootView;
  }
}
