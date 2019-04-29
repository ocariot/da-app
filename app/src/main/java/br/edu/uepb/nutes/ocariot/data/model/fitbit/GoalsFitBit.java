package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import com.google.gson.annotations.SerializedName;

/**
 * Represents Goals object from FitBit platform.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class GoalsFitBit {
    @SerializedName("steps")
    private int steps;

    @SerializedName("distance")
    private double distance;

    @SerializedName("distanceUnit")
    private String distanceUnit; // Example: Kilometer

    @SerializedName("floors")
    private int floors;

    @SerializedName("calories")
    private int calories;

    @SerializedName("activeMinutes")
    private int activeMinutes;
}