package com.felipeska.movements.ui.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.felipeska.movements.MovementsApp;
import com.felipeska.movements.R;
import com.felipeska.movements.ui.fragment.HistoryFragment;
import com.felipeska.movements.ui.fragment.TrackFragment;
import com.felipeska.movements.ui.listener.SupportActionBarListener;
import com.squareup.sqlbrite.SqlBrite;

import javax.inject.Inject;

/**
 * @author felipeska
 */
public class MainActivity extends ActionBarActivity implements SupportActionBarListener {

  @Inject
  SqlBrite db;
  private Toolbar mToolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (savedInstanceState == null) {
      loadFragment(TrackFragment.newInstance(),TrackFragment.ID,false);
    }

    MovementsApp.objectGraph(this).inject(this);

    setupToolbar();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_history) {
      showHistory();
    }

    return super.onOptionsItemSelected(item);
  }

  private void setupToolbar() {
    final float elevationToolbar = 20.0f;
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    if (mToolbar != null) {
      setSupportActionBar(mToolbar);
      getSupportActionBar().setElevation(elevationToolbar);
    }
  }

  void loadFragment(Fragment fragment, String id, boolean inStack) {
    if (inStack) {
      getSupportFragmentManager()
              .beginTransaction()
              .setCustomAnimations(R.anim.slide_in_right,
                      R.anim.slide_out_left, R.anim.slide_in_left,
                      R.anim.slide_out_right)
              .replace(R.id.container, fragment, id).addToBackStack(null).commit();
    } else {
      getSupportFragmentManager()
              .beginTransaction()
              .setCustomAnimations(R.anim.slide_in_right,
                      R.anim.slide_out_left, R.anim.slide_in_left,
                      R.anim.slide_out_right)
              .replace(R.id.container, fragment, id).commit();
    }
  }

  private void showHistory() {
    loadFragment(HistoryFragment.newInstance(), HistoryFragment.ID, true);
  }

  private void enableHomeAsUp(boolean enable) {
    if (mToolbar != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
    }
  }

  @Override
  public void displayHomeAsUpEnabled(boolean display) {
    enableHomeAsUp(display);
  }

  @Override
  public void navigateToHome() {
    getSupportFragmentManager().popBackStack();
  }

  @Override
  public void deactivateOrientation(boolean deactivate) {
    if(deactivate){
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }else{
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
  }
}
