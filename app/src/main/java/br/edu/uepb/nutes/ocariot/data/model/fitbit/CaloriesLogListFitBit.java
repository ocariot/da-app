package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CaloriesLogListFitBit {
    @SerializedName("activities-calories")
    private List<LogDataFitBit> calories;

    public CaloriesLogListFitBit() {
    }

    public CaloriesLogListFitBit(List<LogDataFitBit> calories) {
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
        return "CaloriesLogListFitBit{" +
                "calories=" + calories +
                '}';
    }
}
