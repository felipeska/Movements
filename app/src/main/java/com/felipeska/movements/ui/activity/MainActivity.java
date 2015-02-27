package com.felipeska.movements.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.felipeska.movements.MovementsApp;
import com.felipeska.movements.R;
import com.felipeska.movements.db.Location;
import com.felipeska.movements.ui.fragment.HistoryFragment;
import com.felipeska.movements.ui.fragment.TrackFragment;
import com.squareup.sqlbrite.SqlBrite;

import javax.inject.Inject;

/**
 * @author felipeska
 */
public class MainActivity extends ActionBarActivity {

  @Inject
  SqlBrite db;
  private Toolbar mToolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
              .add(R.id.container, new TrackFragment(),"track")
              .commit();
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

  private void showHistory() {
    if(hasHistory()){
      getSupportFragmentManager().beginTransaction()
              .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left,
                      R.anim.slide_out_right)
              .replace(R.id.container, HistoryFragment.newInstance(),"history")
              .addToBackStack(null)
              .commit();
    }
    else{
      Toast.makeText(this,"No history",Toast.LENGTH_LONG).show();
    }
  }

  private boolean hasHistory(){
    return db.query(Location.QUERY).getCount() > 0 ?true : false;
  }
}
