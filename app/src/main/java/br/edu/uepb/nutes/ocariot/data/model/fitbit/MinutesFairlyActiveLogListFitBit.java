package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MinutesFairlyActiveLogListFitBit {
    @SerializedName("activities-minutesFairlyActive")
    private List<LogDataFitBit> minutesFairlyActive;

    public MinutesFairlyActiveLogListFitBit() {
    }

    public MinutesFairlyActiveLogListFitBit(List<LogDataFitBit> minutesFairlyActive) {
        this.minutesFairlyActive = minutesFairlyActive;
    }

    public List<LogDataFitBit> getMinutesFairlyActive() {
        return minutesFairlyActive;
    }

    public void setMinutesFairlyActive(List<LogDataFitBit> minutesFairlyActive) {
        this.minutesFairlyActive = minutesFairlyActive;
    }

    @NonNull
    @Override
    public String toString() {
        return "MinutesFairlyActiveLogListFitBit{" +
                "minutesFairlyActive=" + minutesFairlyActive +
                '}';
    }
}
