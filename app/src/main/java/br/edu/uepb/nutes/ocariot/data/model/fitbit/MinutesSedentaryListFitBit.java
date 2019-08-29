package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MinutesSedentaryListFitBit {
    @SerializedName("activities-tracker-minutesSedentary")
    private List<LogDataFitBit> minutesSedentary;

    public MinutesSedentaryListFitBit() {
    }

    public MinutesSedentaryListFitBit(List<LogDataFitBit> minutesSedentary) {
        this.minutesSedentary = minutesSedentary;
    }

    public List<LogDataFitBit> getMinutesSedentary() {
        return minutesSedentary;
    }

    public void setMinutesSedentary(List<LogDataFitBit> minutesSedentary) {
        this.minutesSedentary = minutesSedentary;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
