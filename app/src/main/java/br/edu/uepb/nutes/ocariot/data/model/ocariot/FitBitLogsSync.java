package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

@Keep
public class FitBitLogsSync {
    @SerializedName("steps")
    private int steps;

    @SerializedName("calories")
    private int calories;

    @SerializedName("active_minutes")
    private int activeMinutes;

    @SerializedName("lightly_active_minutes")
    private int lightlyActiveMinutes;

    @SerializedName("sedentary_minutes")
    private int sedentaryMinutes;

    public FitBitLogsSync() {
    }

    public FitBitLogsSync(int steps, int calories, int activeMinutes,
                          int lightlyActiveMinutes, int sedentaryMinutes) {
        this.steps = steps;
        this.calories = calories;
        this.activeMinutes = activeMinutes;
        this.lightlyActiveMinutes = lightlyActiveMinutes;
        this.sedentaryMinutes = sedentaryMinutes;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getActiveMinutes() {
        return activeMinutes;
    }

    public void setActiveMinutes(int activeMinutes) {
        this.activeMinutes = activeMinutes;
    }

    public int getLightlyActiveMinutes() {
        return lightlyActiveMinutes;
    }

    public void setLightlyActiveMinutes(int lightlyActiveMinutes) {
        this.lightlyActiveMinutes = lightlyActiveMinutes;
    }

    public int getSedentaryMinutes() {
        return sedentaryMinutes;
    }

    public void setSedentaryMinutes(int sedentaryMinutes) {
        this.sedentaryMinutes = sedentaryMinutes;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
