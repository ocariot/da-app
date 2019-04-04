package br.edu.uepb.nutes.ocariot.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Represents Sleep object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class Sleep extends Activity implements Parcelable {
    @SerializedName(value = "pattern", alternate = {"levels"})
    private SleepPattern pattern;

    public Sleep() {
    }

    protected Sleep(Parcel in) {
        super.set_id(in.readString());
        super.setStartTime(in.readString());
        super.setEndTime(in.readString());
        super.setDuration(in.readLong());
        super.setChildId(in.readString());
        pattern = in.readParcelable(SleepPattern.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(super.get_id());
        dest.writeString(super.getStartTime());
        dest.writeString(super.getEndTime());
        dest.writeLong(super.getDuration());
        dest.writeString(super.getChildId());
        dest.writeParcelable(pattern, flags);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public SleepPattern getPattern() {
        return pattern;
    }

    public void setPattern(SleepPattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @NonNull
    @Override
    public String toString() {
        return "Sleep{" +
                super.toString() +
                "pattern=" + pattern +
                '}';
    }
}