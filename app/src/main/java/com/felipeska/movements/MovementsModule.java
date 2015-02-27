package com.felipeska.movements;


import android.app.Application;

import com.felipeska.movements.db.DbModule;
import com.felipeska.movements.track.TrackModule;
import com.felipeska.movements.ui.UiModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                DbModule.class,
                UiModule.class,
                TrackModule.class
        }
)
/**
 * @author felipeska
 */
public final class MovementsModule {
  private final Application application;

  MovementsModule(Application application) {
    this.application = application;
  }

  @Provides
  @Singleton
  Application provideApplication() {
    return application;
  }
}
