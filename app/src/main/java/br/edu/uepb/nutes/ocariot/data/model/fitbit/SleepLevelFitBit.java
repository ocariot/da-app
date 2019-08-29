package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represents SleepLevelFitBit object present in the sleep levels {@link SleepFitBit}.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SleepLevelFitBit {
    @SerializedName("data")
    private List<SleepLevelDataFitBit> data;

    // Only when the device provides sleep stages
    @SerializedName("shortData")
    private List<SleepLevelDataFitBit> shortData;

    @SerializedName(value = "summary")
    private SleepLevelSummaryFitBit summary;

    public SleepLevelFitBit() {
    }

    public SleepLevelFitBit(List<SleepLevelDataFitBit> data,
                            List<SleepLevelDataFitBit> shortData,
                            SleepLevelSummaryFitBit summary) {
        this.data = data;
        this.shortData = shortData;
        this.summary = summary;
    }

    public List<SleepLevelDataFitBit> getData() {
        return data;
    }

    public void setData(List<SleepLevelDataFitBit> data) {
        this.data = data;
    }

    public List<SleepLevelDataFitBit> getShortData() {
        return shortData;
    }

    public void setShortData(List<SleepLevelDataFitBit> shortData) {
        this.shortData = shortData;
    }

    public SleepLevelSummaryFitBit getSummary() {
        return summary;
    }

    public void setSummary(SleepLevelSummaryFitBit summary) {
        this.summary = summary;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
