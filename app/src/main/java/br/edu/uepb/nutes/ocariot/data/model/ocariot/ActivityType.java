package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import androidx.annotation.Keep;

/**
 * Types of activities supported by the OCARIoT platform.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
@Keep
public class ActivityType {
    private ActivityType() {
        throw new IllegalStateException("Utility class. Does not allow inheritance or instances to be created!");
    }

    public static final String RUN = "Run";
    public static final String WALK = "Walk";
    public static final String BIKE = "Bike";
    public static final String MOUNTAIN_BIKING = "Mountain Biking";
    public static final String OUTDOOR_BIKE = "Outdoor Bike";
    public static final String FITSTAR_PERSONAL = "Fitstar: Personal Trainer";
    public static final String WORKOUT = "Workout";
    public static final String SWIM = "Swim";
    public static final String SPORT = "Sport";
}
