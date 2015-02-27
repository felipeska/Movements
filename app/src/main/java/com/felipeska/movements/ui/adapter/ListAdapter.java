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

import rx.functions.Action1;

public final class ListAdapter extends BaseAdapter implements Action1<List<Location>> {
  private final static String FORMAT_DISPLAY_DATA = "%s \t %s";
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

  @Override public int getCount() {
    return items.size();
  }

  @Override public Location getItem(int position) {
    return items.get(position);
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
    ((TextView) convertView.findViewById(R.id.textViewItemLatitude))
            .setText(String.format(FORMAT_DISPLAY_DATA, mRes.getText(R.string.item_latitude), item.latitude()));
    ((TextView) convertView.findViewById(R.id.textViewItemLongitude))
            .setText((String.format(FORMAT_DISPLAY_DATA, mRes.getText(R.string.item_longitude), item.longitude())));
    ((TextView) convertView.findViewById(R.id.textViewItemSpeed))
            .setText(String.format(FORMAT_DISPLAY_DATA + " m/s", mRes.getText(R.string.item_speed), item.speed()));
    ((TextView) convertView.findViewById(R.id.textViewItemDate))
            .setText(String.format(FORMAT_DISPLAY_DATA, mRes.getText(R.string.date),item.date()));
    return convertView;
  }
}
