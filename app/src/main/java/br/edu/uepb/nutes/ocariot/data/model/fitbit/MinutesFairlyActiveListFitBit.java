package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class MinutesFairlyActiveListFitBit {
    @SerializedName("activities-tracker-minutesFairlyActive")
    private List<LogDataFitBit> minutesFairlyActive;

    public MinutesFairlyActiveListFitBit() {
    }

    public MinutesFairlyActiveListFitBit(List<LogDataFitBit> minutesFairlyActive) {
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
        return new Gson().toJson(this);
    }
}
