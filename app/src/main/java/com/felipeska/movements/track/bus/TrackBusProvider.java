package com.felipeska.movements.track.bus;

import com.squareup.otto.Bus;

/**
 * @author felipeska
 */
public class TrackBusProvider {
  private static final Bus BUS = new TrackBus();

  private TrackBusProvider() {
    // No instances.
  }

  public static Bus getInstance() {
    return BUS;
  }
}
