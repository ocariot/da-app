package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MinutesVeryActiveListFitBit {
    @SerializedName("activities-tracker-minutesVeryActive")
    private List<LogDataFitBit> minutesVeryActive;

    public MinutesVeryActiveListFitBit() {
    }

    public MinutesVeryActiveListFitBit(List<LogDataFitBit> minutesVeryActive) {
        this.minutesVeryActive = minutesVeryActive;
    }

    public List<LogDataFitBit> getMinutesVeryActive() {
        return minutesVeryActive;
    }

    public void setMinutesVeryActive(List<LogDataFitBit> minutesVeryActive) {
        this.minutesVeryActive = minutesVeryActive;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
