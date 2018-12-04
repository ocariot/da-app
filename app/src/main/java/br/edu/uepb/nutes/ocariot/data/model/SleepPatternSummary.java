package br.edu.uepb.nutes.ocariot.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the sleep pattern summary entity {@link SleepPattern}.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SleepPatternSummary implements Parcelable {
    @SerializedName("awake")
    private SleepPatternSummaryData awake;

    @SerializedName("asleep")
    private SleepPatternSummaryData asleep;

    @SerializedName("restless")
    private SleepPatternSummaryData restless;

    public SleepPatternSummary() {
    }

    public SleepPatternSummary(SleepPatternSummaryData awake, SleepPatternSummaryData asleep,
                               SleepPatternSummaryData restless) {
        this.awake = awake;
        this.asleep = asleep;
        this.restless = restless;
    }

    protected SleepPatternSummary(Parcel in) {
        awake = in.readParcelable(SleepPatternSummaryData.class.getClassLoader());
        asleep = in.readParcelable(SleepPatternSummaryData.class.getClassLoader());
        restless = in.readParcelable(SleepPatternSummaryData.class.getClassLoader());
    }

    public static final Creator<SleepPatternSummary> CREATOR = new Creator<SleepPatternSummary>() {
        @Override
        public SleepPatternSummary createFromParcel(Parcel in) {
            return new SleepPatternSummary(in);
        }

        @Override
        public SleepPatternSummary[] newArray(int size) {
            return new SleepPatternSummary[size];
        }
    };

    public SleepPatternSummaryData getAwake() {
        return awake;
    }

    public void setAwake(SleepPatternSummaryData awake) {
        this.awake = awake;
    }

    public SleepPatternSummaryData getAsleep() {
        return asleep;
    }

    public void setAsleep(SleepPatternSummaryData asleep) {
        this.asleep = asleep;
    }

    public SleepPatternSummaryData getRestless() {
        return restless;
    }

    public void setRestless(SleepPatternSummaryData restless) {
        this.restless = restless;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(awake, flags);
        dest.writeParcelable(asleep, flags);
        dest.writeParcelable(restless, flags);
    }

    @Override
    public String toString() {
        return "SleepPatternSummary{" +
                "awake=" + awake +
                ", asleep=" + asleep +
                ", restless=" + restless +
                '}';
    }
}


