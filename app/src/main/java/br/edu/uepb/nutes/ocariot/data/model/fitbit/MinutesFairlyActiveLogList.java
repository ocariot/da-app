package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MinutesFairlyActiveLogList {
    @SerializedName("activities-minutesFairlyActive")
    private List<LogData> minutesFairlyActive;

    public MinutesFairlyActiveLogList() {
    }

    public MinutesFairlyActiveLogList(List<LogData> minutesVeryActive) {
        this.minutesFairlyActive = minutesVeryActive;
    }

    public List<LogData> getMinutesFairlyActive() {
        return minutesFairlyActive;
    }

    public void setMinutesFairlyActive(List<LogData> minutesFairlyActive) {
        this.minutesFairlyActive = minutesFairlyActive;
    }

    @NonNull
    @Override
    public String toString() {
        return "MinutesFairlyActiveLogList{" +
                "minutesFairlyActive=" + minutesFairlyActive +
                '}';
    }
}
