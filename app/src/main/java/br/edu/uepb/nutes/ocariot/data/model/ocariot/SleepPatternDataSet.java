package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Represents of the data set entity present in the sleep pattern {@link SleepPattern}.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SleepPatternDataSet implements Parcelable {
    @SerializedName(value = "start_time", alternate = {"dateTime"})
    private String startTime;

    @SerializedName(value = "name", alternate = {"level"})
    private String name;

    @SerializedName(value = "duration", alternate = {"seconds"})
    private long duration; // in milliseconds

    public SleepPatternDataSet() {
    }

    public SleepPatternDataSet(String startTime, String name, long duration) {
        this.startTime = startTime;
        this.setName(name);
        this.duration = duration;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.equalsIgnoreCase("wake")) {
            name = "awake";
        }
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    protected SleepPatternDataSet(Parcel in) {
        startTime = in.readString();
        name = in.readString();
        duration = in.readLong();
    }

    public static final Creator<SleepPatternDataSet> CREATOR = new Creator<SleepPatternDataSet>() {
        @Override
        public SleepPatternDataSet createFromParcel(Parcel in) {
            return new SleepPatternDataSet(in);
        }

        @Override
        public SleepPatternDataSet[] newArray(int size) {
            return new SleepPatternDataSet[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(startTime);
        parcel.writeString(name);
        parcel.writeLong(duration);
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
