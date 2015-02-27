package com.felipeska.movements.ui;

import com.felipeska.movements.ui.activity.MainActivity;
import com.felipeska.movements.ui.fragment.HistoryFragment;
import com.felipeska.movements.ui.fragment.TrackFragment;

import dagger.Module;

/**
 * @author felipeska
 */
@Module(
        injects = {
                TrackFragment.class,
                MainActivity.class,
                HistoryFragment.class
        },
        complete = false,
        library = true
)
public final class UiModule {
}