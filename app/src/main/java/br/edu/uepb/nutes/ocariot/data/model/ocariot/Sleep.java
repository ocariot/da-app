package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Represents Sleep object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
@Keep
public class Sleep extends Activity implements Parcelable {
    @SerializedName("type")
    private String type;

    @SerializedName(value = "pattern", alternate = {"levels"})
    private SleepPattern pattern;

    public Sleep() {
    }

    protected Sleep(Parcel in) {
        super.setId(in.readString());
        super.setStartTime(in.readString());
        super.setEndTime(in.readString());
        super.setDuration(in.readLong());
        super.setChildId(in.readString());
        type = in.readString();
        pattern = in.readParcelable(SleepPattern.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(super.getId());
        dest.writeString(super.getStartTime());
        dest.writeString(super.getEndTime());
        dest.writeLong(super.getDuration());
        dest.writeString(super.getChildId());
        dest.writeString(type);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        return new Gson().toJson(this);
    }

    public class Type {
        private Type() {
            throw new IllegalStateException("Utility class. Does not allow inheritance or instances to be created!");
        }

        public static final String CLASSIC = "classic";
        public static final String STAGES = "stages";
    }

}
