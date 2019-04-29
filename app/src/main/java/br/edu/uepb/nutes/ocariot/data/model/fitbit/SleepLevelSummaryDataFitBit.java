package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the summary data of the sleep levels entity {@link SleepLevelFitBit}.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SleepLevelSummaryDataFitBit {
    @SerializedName("count")
    private int count;

    @SerializedName("minutes")
    private int minutes;

    @SerializedName("thirtyDayAvgMinutes")
    private int thirtyDayAvgMinutes;

    public SleepLevelSummaryDataFitBit() {
    }

    public SleepLevelSummaryDataFitBit(int count, int minutes) {
        this.count = count;
        this.minutes = minutes;
    }

    public SleepLevelSummaryDataFitBit(int count, int minutes, int thirtyDayAvgMinutes) {
        this.count = count;
        this.minutes = minutes;
        this.thirtyDayAvgMinutes = thirtyDayAvgMinutes;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getThirtyDayAvgMinutes() {
        return thirtyDayAvgMinutes;
    }

    public void setThirtyDayAvgMinutes(int thirtyDayAvgMinutes) {
        this.thirtyDayAvgMinutes = thirtyDayAvgMinutes;
    }

    @NonNull
    @Override
    public String toString() {
        return "SleepLevelSummaryDataFitBit{" +
                "count=" + count +
                ", minutes=" + minutes +
                ", thirtyDayAvgMinutes=" + thirtyDayAvgMinutes +
                '}';
    }
}

