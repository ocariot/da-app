package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CaloriesLogList {
    @SerializedName("activities-calories")
    private List<LogData> calories;

    public CaloriesLogList() {
    }

    public CaloriesLogList(List<LogData> calories) {
        this.calories = calories;
    }

    public List<LogData> getCalories() {
        return calories;
    }

    public void setCalories(List<LogData> calories) {
        this.calories = calories;
    }

    @NonNull
    @Override
    public String toString() {
        return "CaloriesLogList{" +
                "calories=" + calories +
                '}';
    }
}
