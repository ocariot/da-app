package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
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

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}