package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents Sleep Pattern object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SleepPattern implements Parcelable {
    @SerializedName(value = "data_set", alternate = {"data"})
    private List<SleepPatternDataSet> dataSet;

    @SerializedName(value = "summary")
    private SleepPatternSummary summary;

    public SleepPattern() {
    }

    public SleepPattern(List<SleepPatternDataSet> dataSet, SleepPatternSummary summary) {
        this.dataSet = dataSet;
        this.summary = summary;
    }

    protected SleepPattern(Parcel in) {
        dataSet = in.createTypedArrayList(SleepPatternDataSet.CREATOR);
        summary = in.readParcelable(SleepPatternSummary.class.getClassLoader());
    }

    public static final Creator<SleepPattern> CREATOR = new Creator<SleepPattern>() {
        @Override
        public SleepPattern createFromParcel(Parcel in) {
            return new SleepPattern(in);
        }

        @Override
        public SleepPattern[] newArray(int size) {
            return new SleepPattern[size];
        }
    };

    public List<SleepPatternDataSet> getDataSet() {
        return dataSet;
    }

    public void setDataSet(List<SleepPatternDataSet> dataSet) {
        this.dataSet = dataSet;
    }

    public boolean addItemDataSet(SleepPatternDataSet item) {
        if (this.dataSet == null) this.dataSet = new ArrayList<>();
        return dataSet.add(item);
    }


    public SleepPatternSummary getSummary() {
        return summary;
    }

    public void setSummary(SleepPatternSummary summary) {
        this.summary = summary;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(dataSet);
        dest.writeParcelable(summary, flags);
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
