package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represents Physical Activity object from FitBit platform.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
@Keep
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

    @SerializedName("averageHeartRate")
    private int averageHeartRate;

    @SerializedName("heartRateZones")
    private List<HeartRateZoneFitBit> heartRateZones;

    @SerializedName("distance")
    private Double distance;

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

    public List<HeartRateZoneFitBit> getHeartRateZones() {
        return heartRateZones;
    }

    public void setHeartRateZones(List<HeartRateZoneFitBit> heartRateZones) {
        this.heartRateZones = heartRateZones;
    }

    public int getAverageHeartRate() {
        return averageHeartRate;
    }

    public void setAverageHeartRate(int averageHeartRate) {
        this.averageHeartRate = averageHeartRate;
    }

    public HeartRateZoneFitBit getOutOfRangeZone() {
        return searchZone("out");
    }

    public HeartRateZoneFitBit getFatBurnZone() {
        return searchZone("fat");
    }

    public HeartRateZoneFitBit getCardioZone() {
        return searchZone("cardio");
    }

    public HeartRateZoneFitBit getPeakZone() {
        return searchZone("peak");
    }

    private HeartRateZoneFitBit searchZone(String zone) {
        if (heartRateZones != null && !heartRateZones.isEmpty()) {
            for (HeartRateZoneFitBit item : heartRateZones) {
                if (item.getName().toLowerCase().contains(zone)) {
                    return item;
                }
            }
        }
        return new HeartRateZoneFitBit();
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}