package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represents Physical Activity object from FitBit platform.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class PhysicalActivityFitBit {
    @SerializedName("activityLevel")
    private List<ActivityLevelFitBit> activityLevel;

    @SerializedName("activityName")
    private String activityName;

    @SerializedName("activityTypeId")
    private int activityTypeId;

    @SerializedName("calories")
    private int calories;

    @SerializedName("steps")
    private int steps;

    @SerializedName("duration")
    private int duration;

    @SerializedName("originalDuration")
    private int originalDuration;

    @SerializedName("elevationGain")
    private int elevationGain;

    @SerializedName("logId")
    private long logId;

    @SerializedName("logType")
    private String logType; // Example: auto_detected

    @SerializedName("startTime")
    private String startTime;

    @SerializedName("originalStartTime")
    private String originalStartTime;

    public PhysicalActivityFitBit() {
    }

    public List<ActivityLevelFitBit> getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(List<ActivityLevelFitBit> activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public int getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(int activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getOriginalDuration() {
        return originalDuration;
    }

    public void setOriginalDuration(int originalDuration) {
        this.originalDuration = originalDuration;
    }

    public int getElevationGain() {
        return elevationGain;
    }

    public void setElevationGain(int elevationGain) {
        this.elevationGain = elevationGain;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getOriginalStartTime() {
        return originalStartTime;
    }

    public void setOriginalStartTime(String originalStartTime) {
        this.originalStartTime = originalStartTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "PhysicalActivityFitBit{" +
                "activityLevel=" + activityLevel +
                ", activityName='" + activityName + '\'' +
                ", activityTypeId=" + activityTypeId +
                ", calories=" + calories +
                ", steps=" + steps +
                ", duration=" + duration +
                ", originalDuration=" + originalDuration +
                ", elevationGain=" + elevationGain +
                ", logId=" + logId +
                ", logType='" + logType + '\'' +
                ", startTime='" + startTime + '\'' +
                ", originalStartTime='" + originalStartTime + '\'' +
                '}';
    }
}