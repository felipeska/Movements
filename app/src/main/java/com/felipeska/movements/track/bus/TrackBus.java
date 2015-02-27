package com.felipeska.movements.track.bus;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by felipecalderon on 26/02/15.
 */
public class TrackBus extends Bus {

  private final Handler mainThread = new Handler(Looper.getMainLooper());

  public TrackBus() {
    super(ThreadEnforcer.ANY, TrackBus.class.getSimpleName());
  }


  @Override
  public void post(final Object event) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      super.post(event);
    } else {
      mainThread.post(new Runnable() {
        @Override
        public void run() {
          post(event);
        }
      });
    }
  }
}
