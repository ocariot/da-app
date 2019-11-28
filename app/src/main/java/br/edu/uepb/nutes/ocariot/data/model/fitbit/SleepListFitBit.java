package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represents list of sleep from platform FitBit.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SleepListFitBit {
    @SerializedName("sleep")
    private List<SleepFitBit> sleepList;

    public SleepListFitBit(List<SleepFitBit> sleepList) {
        this.sleepList = sleepList;
    }

    public List<SleepFitBit> getSleepList() {
        return sleepList;
    }

    public void setSleepList(List<SleepFitBit> sleepList) {
        this.sleepList = sleepList;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
