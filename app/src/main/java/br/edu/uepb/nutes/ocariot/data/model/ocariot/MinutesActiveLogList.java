package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MinutesActiveLogList {
    @SerializedName("activities-minutesActive")
    private List<LogData> minutesActive;

    public MinutesActiveLogList() {
    }

    public MinutesActiveLogList(List<LogData> minutesActive) {
        this.minutesActive = minutesActive;
    }

    public List<LogData> getMinutesActive() {
        return minutesActive;
    }

    public void setMinutesActive(List<LogData> minutesActive) {
        this.minutesActive = minutesActive;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
