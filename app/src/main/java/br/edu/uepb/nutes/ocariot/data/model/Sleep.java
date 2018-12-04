package br.edu.uepb.nutes.ocariot.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Represents Sleep object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class Sleep implements Parcelable {
    @SerializedName("id")
    private String _id; // _id in server remote (UUID)

    @SerializedName(value = "start_time", alternate = {"startTime"})
    private String startTime;

    @SerializedName(value = "end_time", alternate = {"endTime"})
    private String endTime;

    @SerializedName(value = "duration", alternate = {"activeDuration"})
    private long duration; // in milliseconds

    @SerializedName(value = "pattern", alternate = {"levels"})
    private SleepPattern pattern;

    @SerializedName(value = "user")
    private User user;

    public Sleep() {
    }

    public Sleep(String startTime, String endTime, long duration, SleepPattern pattern, User user) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.pattern = pattern;
        this.user = user;
    }

    protected Sleep(Parcel in) {
        _id = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        duration = in.readLong();
        pattern = in.readParcelable(SleepPattern.class.getClassLoader());
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Sleep> CREATOR = new Creator<Sleep>() {
        @Override
        public Sleep createFromParcel(Parcel in) {
            return new Sleep(in);
        }

        @Override
        public Sleep[] newArray(int size) {
            return new Sleep[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public SleepPattern getPattern() {
        return pattern;
    }

    public void setPattern(SleepPattern pattern) {
        this.pattern = pattern;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeLong(duration);
        dest.writeParcelable(pattern, flags);
        dest.writeParcelable(user, flags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, user.get_id());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Sleep)) return false;

        Sleep sleep = (Sleep) o;
        return Objects.equals(startTime, sleep.startTime) &&
                Objects.equals(user.get_id(), sleep.user.get_id());
    }

    @Override
    public String toString() {
        return "Sleep{" +
                "id='" + _id + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", duration=" + duration +
                ", pattern=" + (pattern != null ? pattern.toString() : "null") +
                ", user=" + (user != null ? user.toString() : "null") +
                '}';
    }
}
