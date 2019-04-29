package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MinutesVeryActiveLogListFitBit {
    @SerializedName("activities-minutesVeryActive")
    private List<LogDataFitBit> minutesVeryActive;

    public MinutesVeryActiveLogListFitBit() {
    }

    public MinutesVeryActiveLogListFitBit(List<LogDataFitBit> minutesVeryActive) {
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
        return "MinutesVeryActiveLogListFitBit{" +
                "minutesVeryActive=" + minutesVeryActive +
                '}';
    }
}
