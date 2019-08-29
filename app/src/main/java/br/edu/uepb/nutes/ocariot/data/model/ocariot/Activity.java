package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Represents Activity object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public abstract class Activity {
    @SerializedName("id")
    private String _id; // _id in server remote (UUID)

    @SerializedName(value = "start_time", alternate = {"startTime"})
    private String startTime;

    @SerializedName(value = "end_time", alternate = {"endTime"})
    private String endTime;

    @SerializedName(value = "duration", alternate = {"activeDuration"})
    private long duration; // in milliseconds

    @SerializedName(value = "child_id")
    private String childId;

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

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, childId);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Activity)) return false;

        Activity activity = (Activity) o;
        return Objects.equals(startTime, activity.startTime) &&
                childId.equals(activity.getChildId());
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
