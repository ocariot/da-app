package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Represents the summary data of the sleep pattern entity {@link SleepPattern}.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
@Keep
public class SleepPatternSummaryData implements Parcelable {
    @SerializedName("count")
    private int count;

    @SerializedName(value = "duration", alternate = {"minutes"})
    private int duration; // in minutes

    public SleepPatternSummaryData() {
    }

    public SleepPatternSummaryData(int count, int duration) {
        this.count = count;
        this.duration = duration;
    }

    protected SleepPatternSummaryData(Parcel in) {
        count = in.readInt();
        duration = in.readInt();
    }

    public static final Creator<SleepPatternSummaryData> CREATOR = new Creator<SleepPatternSummaryData>() {
        @Override
        public SleepPatternSummaryData createFromParcel(Parcel in) {
            return new SleepPatternSummaryData(in);
        }

        @Override
        public SleepPatternSummaryData[] newArray(int size) {
            return new SleepPatternSummaryData[size];
        }
    };

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(count);
        dest.writeInt(duration);
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
