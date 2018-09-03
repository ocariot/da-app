package br.edu.uepb.nutes.ocariot.uaal_poc.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Represents ActivityLevel object.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class ActivityLevel implements Parcelable, Comparable<ActivityLevel> {
    public static String SEDENTARY_LEVEL = "sedentary";
    public static String LIGHTLY_LEVEL = "lightly";
    public static String FAIRLY_LEVEL = "fairly";
    public static String VERY_LEVEL = "very";

    private String name;
    private int minutes;

    public ActivityLevel() {
    }

    public ActivityLevel(String name, int minutes) {
        this.name = name;
        this.minutes = minutes;
    }

    protected ActivityLevel(Parcel in) {
        minutes = in.readInt();
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

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(minutes);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "ActivityLevel{" +
                "minutes=" + minutes +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull ActivityLevel o) {
        if (this.minutes < o.minutes) return -1;
        if (this.minutes > o.minutes) return 1;
        return 0;
    }
}
