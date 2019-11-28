package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Represents ActivityLevel object from FitBit platform.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
@Keep
public class ActivityLevelFitBit {
    public static String SEDENTARY_LEVEL = "sedentary";
    public static String LIGHTLY_LEVEL = "lightly";
    public static String FAIRLY_LEVEL = "fairly";
    public static String VERY_LEVEL = "very";

    @SerializedName("name")
    private String name; // Name of activity level (sedentary, light, fair or very).

    @SerializedName("minutes")
    private int minutes;

    public ActivityLevelFitBit() {
    }

    public ActivityLevelFitBit(String name, int minutes) {
        this.name = name;
        this.minutes = minutes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
