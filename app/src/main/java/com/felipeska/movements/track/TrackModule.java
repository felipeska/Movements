package com.felipeska.movements.track;

import dagger.Module;

/**
 * @author felipeska
 */
@Module(
        injects = {
                TrackService.class
        },
        complete = false,
        library = true
)
public final class TrackModule {
}