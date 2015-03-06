package com.felipeska.movements.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.felipeska.movements.MovementsApp;
import com.felipeska.movements.R;
import com.felipeska.movements.db.Location;
import com.felipeska.movements.routeApi.Route;
import com.felipeska.movements.routeApi.RouteParser;
import com.felipeska.movements.slidinguppanel.SlidingUpPanelLayout;
import com.felipeska.movements.ui.adapter.ListAdapter;
import com.felipeska.movements.ui.listener.SupportActionBarListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.sqlbrite.SqlBrite;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

public class HistoryFragment extends Fragment implements SlidingUpPanelLayout.PanelSlideListener, Route.CallbackRoute {

  public final static String ID = HistoryFragment.class.getSimpleName();
  private final static float DEFAULT_ZOOM_COLAPSED = 16.0f;
  private final static float DEFAULT_ZOOM_EXPANDED = 13.0f;

  @Inject
  SqlBrite db;

  @InjectView(android.R.id.list)
  ListView listView;
  @InjectView(android.R.id.empty)
  View emptyView;
  @InjectView(R.id.transparentView)
  View mTransparentView;
  @InjectView(R.id.slidingLayout)
  SlidingUpPanelLayout mSlidingUpPanelLayout;
  @InjectView(R.id.rootMapContainer)
  View rootMapView;

  private View mTransparentHeaderView;
  private View mSpaceView;

  private Subscription subscription;

  private ListAdapter adapter;
  private SupportActionBarListener listener;


  private SupportMapFragment mMapFragment;
  private GoogleMap mMap;
  private UiSettings mUiSettings;
  private CameraPosition mCameraPosition;


  public HistoryFragment() {
    // Required empty public constructor
  }

  public static HistoryFragment newInstance() {
    return new HistoryFragment();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    MovementsApp.objectGraph(activity).inject(this);
    setHasOptionsMenu(true);
    adapter = new ListAdapter(activity);
    listener = (SupportActionBarListener) activity;
    initializeMap();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_history, container, false);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      listener.navigateToHome();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.inject(this, view);
    listView.setEmptyView(emptyView);
    listView.setAdapter(adapter);
    listView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
    listener.displayHomeAsUpEnabled(true);
    listener.deactivateOrientation(true);
    mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);
    mTransparentHeaderView = LayoutInflater.from(getActivity())
            .inflate(R.layout.transparent_header_view, null, false);
    mSpaceView = mTransparentHeaderView.findViewById(R.id.space);

    int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height);
    mSlidingUpPanelLayout.setPanelHeight(mapHeight);
    mSlidingUpPanelLayout.setScrollableView(listView, mapHeight);
    mSlidingUpPanelLayout.setPanelSlideListener(this);

    listView.addHeaderView(mTransparentHeaderView);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mSlidingUpPanelLayout.collapsePane();

        Location location = adapter.getItem(position);
        if (location != null) {
          showMarker(location.latitude(), location.longitude());
        }
      }
    });
  }

  private void initializeMap() {
    mMapFragment = SupportMapFragment.newInstance();
    android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.mapContainer, mMapFragment, "map");
    fragmentTransaction.commit();
    mMapFragment.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(GoogleMap googleMap) {
        setUpMapIfNeeded();
      }
    });
  }

  private void setUpMap() {
    mMap.setMyLocationEnabled(true);
    mUiSettings = mMap.getUiSettings();
    mUiSettings.setAllGesturesEnabled(true);
    mUiSettings.setCompassEnabled(true);
    mUiSettings.setMyLocationButtonEnabled(true);
    mUiSettings.setZoomControlsEnabled(true);
    locateMe(DEFAULT_ZOOM_COLAPSED);
  }

  private void setUpMapIfNeeded() {
    if (mMap == null) {
      mMap = mMapFragment.getMap();
      if (mMap != null) {
        setUpMap();
      }
    }
  }

  private void locateMe(float zoom) {
    LatLng mLoc = getLocation(getActivity());
    if (mLoc != null) {
      System.out.println("loc: "+mLoc.longitude+" "+mLoc.latitude);
      mCameraPosition = buildCamera(mLoc, zoom);
      changeCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
    }
  }

  private CameraPosition buildCamera(LatLng mLocation, float zoom) {
    return new CameraPosition.Builder()
            .target(new LatLng(mLocation.latitude, mLocation.longitude))
            .zoom(zoom).bearing(45.0f).tilt(25).build();
  }

  private void changeCamera(CameraUpdate update) {
    setUpMapIfNeeded();
    mMap.animateCamera(update, null);
  }


  private LatLng getLocation(Context context) throws SecurityException,
          IllegalArgumentException {
    LatLng actualLocation = null;
    LocationManager lm = (LocationManager) context
            .getSystemService(Context.LOCATION_SERVICE);
    List<String> providers = lm.getProviders(true);

    android.location.Location l = null;
    for (int i = 0; i < providers.size(); i++) {
      l = lm.getLastKnownLocation(providers.get(i));
      if (l != null)
        break;
    }
    if (l != null) {
      actualLocation = new LatLng(l.getLatitude(), l.getLongitude());
    }
    return actualLocation;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    listener.displayHomeAsUpEnabled(false);
    listener.deactivateOrientation(false);
  }

  @Override
  public void onResume() {
    super.onResume();
    getActivity().setTitle(getResources().getString(R.string.history_fragment_title));
    subscription = db.createQuery(Location.TABLE, Location.QUERY)
            .map(Location.MAP)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(adapter);
  }

  @Override
  public void onPause() {
    super.onPause();
    subscription.unsubscribe();
  }

  private void collapseMap() {
    mSpaceView.setVisibility(View.VISIBLE);
    mTransparentView.setVisibility(View.GONE);
  }

  private void expandMap() {
    mSpaceView.setVisibility(View.GONE);
    mTransparentView.setVisibility(View.INVISIBLE);
  }

  @Override
  public void onPanelSlide(View panel, float slideOffset) {

  }

  @Override
  public void onPanelCollapsed(View view) {
    expandMap();
    locateMe(DEFAULT_ZOOM_COLAPSED);
  }

  @Override
  public void onPanelExpanded(View view) {
    collapseMap();
    locateMe(DEFAULT_ZOOM_EXPANDED);
  }

  @Override
  public void onPanelAnchored(View view) {

  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    menu.findItem(R.id.action_history).setVisible(false);
    menu.add(R.string.button_start)
            .setShowAsActionFlags(SHOW_AS_ACTION_ALWAYS)
            .setIcon(R.drawable.ic_action_route)
            .setOnMenuItemClickListener(
                    new MenuItem.OnMenuItemClickListener() {
                      @Override
                      public boolean onMenuItemClick(MenuItem item) {
                        findRoute();
                        return true;
                      }
                    });
  }

  private void findRoute() {
    if (!adapter.hasNoItems()) {
      final Route route = new Route();
      final Location origin = adapter.getItem(0);

      String url = new Route.UrlBuilder().endpoint("https://maps.googleapis.com/maps/api/directions/")
              .origin(origin)
              .build();
      try {
        route.download(url, this);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void showMarker(double latitude, double longitude) {
    LatLng PERTH = new LatLng(latitude, longitude);
    mCameraPosition = buildCamera(PERTH, DEFAULT_ZOOM_EXPANDED);
    changeCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
    mMap.addMarker(new MarkerOptions()
            .position(PERTH));
  }

  @Override
  public void onSuccess(String response) {
    try {
      drawRoute(RouteParser.parse(new JSONObject(response)));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onFailed() {

  }

  protected void drawRoute(List<List<HashMap<String, String>>> result) {
    ArrayList<LatLng> points;
    PolylineOptions lineOptions = null;
    for(int i=0;i<result.size();i++){
      points = new ArrayList<>();
      lineOptions = new PolylineOptions();
      List<HashMap<String, String>> path = result.get(i);
      for(int j=0;j<path.size();j++){
        HashMap<String,String> point = path.get(j);
        double lat = Double.parseDouble(point.get("lat"));
        double lng = Double.parseDouble(point.get("lng"));
        LatLng position = new LatLng(lat, lng);
        points.add(position);
      }
      lineOptions.addAll(points);
      lineOptions.width(6);
      lineOptions.color(Color.RED);

    }
    final PolylineOptions finalLineOptions = lineOptions;
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mMap.addPolyline(finalLineOptions);
      }
    });
  }
}
