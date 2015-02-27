package com.felipeska.movements.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.felipeska.movements.MovementsApp;
import com.felipeska.movements.R;
import com.felipeska.movements.db.Location;
import com.felipeska.movements.ui.adapter.ListAdapter;
import com.squareup.sqlbrite.SqlBrite;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HistoryFragment extends Fragment {

  @Inject
  SqlBrite db;

  @InjectView(android.R.id.list)
  ListView listView;
  @InjectView(android.R.id.empty)
  View emptyView;

  private Subscription subscription;
  private ListAdapter adapter;

  public static HistoryFragment newInstance() {
    HistoryFragment fragment = new HistoryFragment();
    return fragment;
  }

  public HistoryFragment() {
    // Required empty public constructor
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    MovementsApp.objectGraph(activity).inject(this);
    setHasOptionsMenu(true);
    adapter = new ListAdapter(activity);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_history, container, false);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    menu.findItem(R.id.action_history).setVisible(false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.inject(this, view);
    listView.setEmptyView(emptyView);
    listView.setAdapter(adapter);
  }

  @Override public void onResume() {
    super.onResume();

    getActivity().setTitle("History");

    subscription = db.createQuery(Location.TABLE, Location.QUERY)
            .map(Location.MAP)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(adapter);
  }

  @Override public void onPause() {
    super.onPause();
    subscription.unsubscribe();
  }

}
