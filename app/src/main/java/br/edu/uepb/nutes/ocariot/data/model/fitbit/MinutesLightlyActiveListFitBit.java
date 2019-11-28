package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class MinutesLightlyActiveListFitBit {
    @SerializedName("activities-tracker-minutesLightlyActive")
    private List<LogDataFitBit> minutesLightlyActive;

    public MinutesLightlyActiveListFitBit() {
    }

    public MinutesLightlyActiveListFitBit(List<LogDataFitBit> minutesFairlyActive) {
        this.minutesLightlyActive = minutesFairlyActive;
    }

    public List<LogDataFitBit> getMinutesLightlyActive() {
        return minutesLightlyActive;
    }

    public void setMinutesLightlyActive(List<LogDataFitBit> minutesLightlyActive) {
        this.minutesLightlyActive = minutesLightlyActive;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
