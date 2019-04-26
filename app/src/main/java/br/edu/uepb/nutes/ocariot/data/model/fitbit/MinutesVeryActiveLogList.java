package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MinutesVeryActiveLogList {
    @SerializedName("activities-minutesVeryActive")
    private List<LogData> minutesVeryActive;

    public MinutesVeryActiveLogList() {
    }

    public MinutesVeryActiveLogList(List<LogData> minutesVeryActive) {
        this.minutesVeryActive = minutesVeryActive;
    }

    public List<LogData> getMinutesVeryActive() {
        return minutesVeryActive;
    }

    public void setMinutesVeryActive(List<LogData> minutesVeryActive) {
        this.minutesVeryActive = minutesVeryActive;
    }

    @NonNull
    @Override
    public String toString() {
        return "MinutesVeryActiveLogList{" +
                "minutesVeryActive=" + minutesVeryActive +
                '}';
    }
}
