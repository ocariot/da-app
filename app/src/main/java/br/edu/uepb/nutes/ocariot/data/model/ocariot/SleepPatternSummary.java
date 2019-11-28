package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
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

    @SerializedName("deep")
    private SleepPatternSummaryData deep;

    @SerializedName("light")
    private SleepPatternSummaryData light;

    @SerializedName("rem")
    private SleepPatternSummaryData rem;

    public SleepPatternSummary() {
    }

    public SleepPatternSummary(SleepPatternSummaryData awake, SleepPatternSummaryData asleep,
                               SleepPatternSummaryData restless) {
        this.awake = awake;
        this.asleep = asleep;
        this.restless = restless;
    }

    public SleepPatternSummary(SleepPatternSummaryData awake, SleepPatternSummaryData deep,
                               SleepPatternSummaryData light, SleepPatternSummaryData rem) {
        this.awake = awake;
        this.deep = deep;
        this.light = light;
        this.rem = rem;
    }

    protected SleepPatternSummary(Parcel in) {
        awake = in.readParcelable(SleepPatternSummaryData.class.getClassLoader());
        asleep = in.readParcelable(SleepPatternSummaryData.class.getClassLoader());
        restless = in.readParcelable(SleepPatternSummaryData.class.getClassLoader());
        deep = in.readParcelable(SleepPatternSummaryData.class.getClassLoader());
        light = in.readParcelable(SleepPatternSummaryData.class.getClassLoader());
        rem = in.readParcelable(SleepPatternSummaryData.class.getClassLoader());
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

    public SleepPatternSummaryData getDeep() {
        return deep;
    }

    public void setDeep(SleepPatternSummaryData deep) {
        this.deep = deep;
    }

    public SleepPatternSummaryData getLight() {
        return light;
    }

    public void setLight(SleepPatternSummaryData light) {
        this.light = light;
    }

    public SleepPatternSummaryData getRem() {
        return rem;
    }

    public void setRem(SleepPatternSummaryData rem) {
        this.rem = rem;
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
        dest.writeParcelable(deep, flags);
        dest.writeParcelable(light, flags);
        dest.writeParcelable(rem, flags);
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
