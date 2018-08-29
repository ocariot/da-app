package br.edu.uepb.nutes.ocariot.uaal_poc.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents ActivityLevel object.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class ActivityLevel implements Parcelable {
    public static String SEDENTARY_LEVEL = "sedentary";
    public static String LIGHTLY_LEVEL = "lightly";
    public static String FAIRLY_LEVEL = "fairly";
    public static String VERY_LEVEL = "very";

    private int minutes;
    private String name;

    public ActivityLevel() {
    }

    public ActivityLevel(int minutes, String name) {
        this.minutes = minutes;
        this.name = name;
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
}
