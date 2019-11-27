package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Represents ActivityLevel object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class ActivityLevel implements Parcelable, Comparable<ActivityLevel> {
    public static String SEDENTARY_LEVEL = "sedentary";
    public static String LIGHTLY_LEVEL = "lightly";
    public static String FAIRLY_LEVEL = "fairly";
    public static String VERY_LEVEL = "very";

    @SerializedName(value = "name")
    private String name; // Name of activity level (sedentary, light, fair or very).

    // Total time spent in milliseconds on the level. FitBit API returns
    // in minutes. Before publishing on the OCARIoT platform the conversion is made to milliseconds
    @SerializedName(value = "duration", alternate = {"minutes"})
    private int duration;

    public ActivityLevel() {
    }

    public ActivityLevel(String name, int minutes) {
        this.name = name;
        this.duration = minutes;
    }

    private ActivityLevel(Parcel in) {
        duration = in.readInt();
        name = in.readString();
    }

    public static final Creator<ActivityLevel> CREATOR = new Creator<ActivityLevel>() {
        @Override
        public ActivityLevel createFromParcel(Parcel in) {
            return new ActivityLevel(in);
        }

        @Override
        public ActivityLevel[] newArray(int size) {
            return new ActivityLevel[size];
        }
    };

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(duration);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "ActivityLevel{" +
                "duration=" + duration +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull ActivityLevel o) {
        return Integer.compare(this.duration, o.duration);
    }
}
