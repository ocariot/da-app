package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.edu.uepb.nutes.ocariot.data.model.ocariot.ActivityLevel;

/**
 * Represents Physical Activity object from FitBit platform.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class ActivitySummayFitBit {
    @SerializedName("goals")
    private GoalsFitBit goals;

    @SerializedName("calories")
    private int calories;

    @SerializedName("steps")
    private int steps;

    @SerializedName("activityLevel")
    private List<ActivityLevel> levels;

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
