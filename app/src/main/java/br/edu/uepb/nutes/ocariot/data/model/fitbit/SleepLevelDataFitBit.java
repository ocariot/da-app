package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Represents of the data set entity present in the sleep {@link SleepLevelFitBit}.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
@Keep
public class SleepLevelDataFitBit {
    @SerializedName("dateTime")
    private String dateTime;

    // Device with heart rate: wake, rem, light or deep
    // Device without heart rate: awake, restless, or asleep
    @SerializedName("level")
    private String level;

    @SerializedName("seconds")
    private long seconds;

    public SleepLevelDataFitBit() {
    }

    public SleepLevelDataFitBit(String dateTime, String level, long seconds) {
        this.dateTime = dateTime;
        this.level = level;
        this.seconds = seconds;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
