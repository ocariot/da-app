package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StepsLogListFitBit {
    @SerializedName("activities-steps")
    private List<LogDataFitBit> steps;

    public StepsLogListFitBit() {
    }

    public StepsLogListFitBit(List<LogDataFitBit> steps) {
        this.steps = steps;
    }

    public List<LogDataFitBit> getSteps() {
        return steps;
    }

    public void setSteps(List<LogDataFitBit> steps) {
        this.steps = steps;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
