package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CaloriesListFitBit {
    @SerializedName("activities-tracker-calories")
    private List<LogDataFitBit> calories;

    public CaloriesListFitBit() {
    }

    public CaloriesListFitBit(List<LogDataFitBit> calories) {
        this.calories = calories;
    }

    public List<LogDataFitBit> getCalories() {
        return calories;
    }

    public void setCalories(List<LogDataFitBit> calories) {
        this.calories = calories;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
