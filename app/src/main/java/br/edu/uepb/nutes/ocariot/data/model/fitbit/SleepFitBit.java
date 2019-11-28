package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Represents Sleep object from FitBit platform.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SleepFitBit {
    @SerializedName("levels")
    private SleepLevelFitBit levels;

    @SerializedName("dateOfSleep")
    private String dateOfSleep;

    @SerializedName("duration")
    private int duration;

    @SerializedName("efficiency")
    private int efficiency;

    @SerializedName("logId")
    private long logId;

    @SerializedName("minutesAfterWakeup")
    private int minutesAfterWakeup;

    @SerializedName("minutesAsleep")
    private int minutesAsleep;

    @SerializedName("minutesAwake")
    private int minutesAwake;

    @SerializedName("minutesToFallAsleep")
    private int minutesToFallAsleep;

    @SerializedName("timeInBed")
    private int timeInBed;

    @SerializedName("startTime")
    private String startTime;

    @SerializedName("endTime")
    private String endTime;

    // Device with heart rate: stages
    // Device without heart rate: classic
    @SerializedName("type")
    private String type;

    public SleepFitBit() {
    }

    public SleepLevelFitBit getLevels() {
        return levels;
    }

    public void setLevels(SleepLevelFitBit levels) {
        this.levels = levels;
    }

    public String getDateOfSleep() {
        return dateOfSleep;
    }

    public void setDateOfSleep(String dateOfSleep) {
        this.dateOfSleep = dateOfSleep;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(int efficiency) {
        this.efficiency = efficiency;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public int getMinutesAfterWakeup() {
        return minutesAfterWakeup;
    }

    public void setMinutesAfterWakeup(int minutesAfterWakeup) {
        this.minutesAfterWakeup = minutesAfterWakeup;
    }

    public int getMinutesAsleep() {
        return minutesAsleep;
    }

    public void setMinutesAsleep(int minutesAsleep) {
        this.minutesAsleep = minutesAsleep;
    }

    public int getMinutesAwake() {
        return minutesAwake;
    }

    public void setMinutesAwake(int minutesAwake) {
        this.minutesAwake = minutesAwake;
    }

    public int getMinutesToFallAsleep() {
        return minutesToFallAsleep;
    }

    public void setMinutesToFallAsleep(int minutesToFallAsleep) {
        this.minutesToFallAsleep = minutesToFallAsleep;
    }

    public int getTimeInBed() {
        return timeInBed;
    }

    public void setTimeInBed(int timeInBed) {
        this.timeInBed = timeInBed;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}