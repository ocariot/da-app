package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

@Keep
public class HeartRateZoneItem implements Parcelable {
    @SerializedName("min")
    private int min;

    @SerializedName("max")
    private int max;

    @SerializedName("duration")
    private int duration;

    public HeartRateZoneItem() {
    }

    public HeartRateZoneItem(int min, int max, int duration) {
        this.min = min;
        this.max = max;
        this.duration = duration;
    }

    protected HeartRateZoneItem(Parcel in) {
        min = in.readInt();
        max = in.readInt();
        duration = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(min);
        dest.writeInt(max);
        dest.writeInt(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HeartRateZoneItem> CREATOR = new Creator<HeartRateZoneItem>() {
        @Override
        public HeartRateZoneItem createFromParcel(Parcel in) {
            return new HeartRateZoneItem(in);
        }

        @Override
        public HeartRateZoneItem[] newArray(int size) {
            return new HeartRateZoneItem[size];
        }
    };

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
