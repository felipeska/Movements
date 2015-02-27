package com.felipeska.movements.track.event;

import android.location.Location;

public class LocationEvent {

  public final Location location;

  public LocationEvent(Location location) {
    this.location = location;
  }
}
