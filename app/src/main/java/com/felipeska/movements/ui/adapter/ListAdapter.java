package com.felipeska.movements.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.felipeska.movements.R;
import com.felipeska.movements.db.Location;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.functions.Action1;

public final class ListAdapter extends BaseAdapter implements Action1<List<Location>> {
  private final LayoutInflater inflater;
  private List<Location> items = Collections.emptyList();
  private Resources mRes;

  public ListAdapter(Context context) {
    this.mRes = context.getResources();
    this.inflater = LayoutInflater.from(context);
  }

  @Override public void call(List<Location> items) {
    this.items = items;
    notifyDataSetChanged();
  }

  public boolean hasNoItems(){
    return this.items.isEmpty();
  }


  @Override public int getCount() {
    return items.size();
  }

  @Override public Location getItem(int position) {
    if(position == getCount()){
      position = position -1;
    }
    return items.get((position));
  }

  @Override public long getItemId(int position) {
    return getItem(position).id();
  }

  @Override public boolean hasStableIds() {
    return true;
  }


  @Override public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.location_item_row, parent, false);
    }
    Location item = getItem(position);
    ViewHolder holder = ViewHolder.obtain(convertView);
    holder.populate(item,this.mRes);
    return convertView;
  }

  public static class ViewHolder {
    @InjectView(R.id.textViewItemLatitude)
    TextView mTextVieLatitude;
    @InjectView(R.id.textViewItemLongitude)
    TextView mTextViewLongitude;
    @InjectView(R.id.textViewItemSpeed)
    TextView mTextViewSpeed;
    @InjectView(R.id.textViewItemDate)
    TextView mTextViewDate;

    private ViewHolder(View view) {
      ButterKnife.inject(this, view);
    }

    public void populate(Location location, Resources resources) {
      final float latitude = location.latitude();
      final float longitude = location.longitude();
      final float speed = location.speed();
      final String date = location.date();

      mTextVieLatitude.setText(resources.getString(R.string.item_latitude,latitude));
      mTextViewLongitude.setText(resources.getString(R.string.item_longitude,longitude));
      mTextViewSpeed.setText(resources.getString(R.string.item_speed,speed));
      mTextViewDate.setText(resources.getString(R.string.item_last_date,date));
    }

    public static ViewHolder obtain(View view) {
      ViewHolder holder = (ViewHolder) view.getTag();
      if (holder == null) {
        holder = new ViewHolder(view);
        view.setTag(holder);
      }
      return holder;
    }
  }
}