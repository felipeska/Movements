package com.felipeska.movements.track.priority;

import com.google.android.gms.location.LocationRequest;

/**
 * @author felipeska
 */
public enum Priority {

  PRIORITY_HIGH_ACCURACY(LocationRequest.PRIORITY_HIGH_ACCURACY),
  PRIORITY_BALANCED_POWER_ACCURACY(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY),
  PRIORITY_LOW_POWER(LocationRequest.PRIORITY_LOW_POWER),
  PRIORITY_NO_POWER(LocationRequest.PRIORITY_NO_POWER);

  private final int value;

  Priority(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
