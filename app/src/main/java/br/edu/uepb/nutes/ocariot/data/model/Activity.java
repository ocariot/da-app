package br.edu.uepb.nutes.ocariot.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/**
 * Represents Activity object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class Activity implements Parcelable {
    @SerializedName("id")
    private String _id; // _id in server remote (UUID)

    @SerializedName(value = "name", alternate = {"activityName"})
    private String name;

    @SerializedName(value = "start_time", alternate = {"startTime"})
    private String startTime;

    @SerializedName(value = "end_time", alternate = {"endTime"})
    private String endTime;

    @SerializedName(value = "duration", alternate = {"activeDuration"})
    private long duration; // in milliseconds

    @SerializedName(value = "calories")
    private int calories;

    @SerializedName(value = "steps")
    private int steps;

    @SerializedName(value = "activitylevel", alternate = {"levels"})
    private List<ActivityLevel> levels;

    @SerializedName(value = "user")
    private User user;

    public Activity() {
    }

    public Activity(String name, String startTime, String endTime, long duration, int calories,
                    int steps, List<ActivityLevel> levels, User user) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.calories = calories;
        this.steps = steps;
        this.levels = levels;
        this.user = user;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
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

    public List<ActivityLevel> getLevels() {
        return levels;
    }

    public void setLevels(List<ActivityLevel> levels) {
        this.levels = levels;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    protected Activity(Parcel in) {
        _id = in.readString();
        name = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        duration = in.readLong();
        calories = in.readInt();
        steps = in.readInt();
        levels = in.createTypedArrayList(ActivityLevel.CREATOR);
        user = in.readParcelable(User.class.getClassLoader());
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(_id);
        parcel.writeString(name);
        parcel.writeString(startTime);
        parcel.writeString(endTime);
        parcel.writeLong(duration);
        parcel.writeInt(calories);
        parcel.writeInt(steps);
        parcel.writeTypedList(levels);
        parcel.writeParcelable(user, flags);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Activity)) return false;

        Activity activity = (Activity) o;
        return Objects.equals(_id, activity._id) &&
                Objects.equals(startTime, activity.startTime) &&
                Objects.equals(endTime, activity.endTime) &&
                Objects.equals(user.get_id(), activity.user.get_id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, startTime, endTime, user.get_id());
    }

    @NonNull
    @Override
    public String toString() {
        return "Activity{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", duration=" + duration +
                ", calories=" + calories +
                ", steps=" + steps +
                ", levels=" + levels +
                ", user=" + user.toString() +
                '}';
    }
}
