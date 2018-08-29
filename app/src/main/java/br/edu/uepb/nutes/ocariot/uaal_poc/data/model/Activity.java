package br.edu.uepb.nutes.ocariot.uaal_poc.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represents Activity object.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class Activity implements Parcelable {
    private String id; // _id in server remote (UUID)

    @SerializedName(value = "duration", alternate = {"activeDuration"})
    private long duration; // in milliseconds

    @SerializedName(value = "start_time", alternate = {"startTime"})
    private String startTime;

    @SerializedName("end_time")
    private String endTime;

    /**
     * Adapt to the activityLevel
     * the highest level
     */
    @SerializedName("intensity_level")
    private String intensityLevel;

    private double distance;

    @SerializedName("user_id")
    private String userId; // id in server remote (UUID), logged in app

    @SerializedName("heartrate")
    private int heartRate;

    @SerializedName(value = "name", alternate = {"activityName"})
    private String name;

    @SerializedName("location")
    private String location;

    private int calories;

    private int steps;

    @SerializedName(value = "activitylevel", alternate = {"activityLevel"})
    private List<ActivityLevel> activityLevel;

    @SerializedName(value = "elevation_gain", alternate = {"elevationGain"})
    private double elevationGain;

    @SerializedName(value = "log_id", alternate = {"logId"})
    private String logId;

    public Activity() {
    }

    public Activity(String id, long duration, String startTime, String endTime,
                    String intensityLevel, double distance, String userId, int heartRate,
                    String name, String location, int calories, int steps,
                    List<ActivityLevel> activityLevel, double elevationGain, String logId) {
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intensityLevel = intensityLevel;
        this.distance = distance;
        this.userId = userId;
        this.heartRate = heartRate;
        this.name = name;
        this.location = location;
        this.calories = calories;
        this.steps = steps;
        this.activityLevel = activityLevel;
        this.elevationGain = elevationGain;
        this.logId = logId;
    }

    protected Activity(Parcel in) {
        id = in.readString();
        duration = in.readLong();
        startTime = in.readString();
        endTime = in.readString();
        intensityLevel = in.readString();
        distance = in.readDouble();
        userId = in.readString();
        heartRate = in.readInt();
        name = in.readString();
        location = in.readString();
        calories = in.readInt();
        steps = in.readInt();
        elevationGain = in.readDouble();
        logId = in.readString();
    }

    public static final Creator<Activity> CREATOR = new Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel in) {
            return new Activity(in);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
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

    public String getIntensityLevel() {
        return intensityLevel;
    }

    public void setIntensityLevel(String intensityLevel) {
        this.intensityLevel = intensityLevel;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public List<ActivityLevel> getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(List<ActivityLevel> activityLevel) {
        this.activityLevel = activityLevel;
    }

    public double getElevationGain() {
        return elevationGain;
    }

    public void setElevationGain(double elevationGain) {
        this.elevationGain = elevationGain;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(duration);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(intensityLevel);
        dest.writeDouble(distance);
        dest.writeString(userId);
        dest.writeInt(heartRate);
        dest.writeString(name);
        dest.writeString(location);
        dest.writeInt(calories);
        dest.writeInt(steps);
        dest.writeDouble(elevationGain);
        dest.writeString(logId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id='" + id + '\'' +
                ", duration=" + duration +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", intensityLevel='" + intensityLevel + '\'' +
                ", distance=" + distance +
                ", userId='" + userId + '\'' +
                ", heartRate=" + heartRate +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", calories=" + calories +
                ", steps=" + steps +
                ", activityLevel=" + activityLevel +
                ", elevationGain=" + elevationGain +
                ", logId='" + logId + '\'' +
                '}';
    }
}
