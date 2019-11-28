package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

@Keep
public class HeartRateZoneFitBit {
    @SerializedName("name")
    private String name;

    @SerializedName("max")
    private int max;

    @SerializedName("min")
    private int min;

    @SerializedName("minutes")
    private int minutes;

    public HeartRateZoneFitBit() {
    }

    public HeartRateZoneFitBit(String name, int max, int min, int minutes) {
        this.name = name;
        this.max = max;
        this.min = min;
        this.minutes = minutes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
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
