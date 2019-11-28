package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class StepsListFitBit {
    @SerializedName("activities-tracker-steps")
    private List<LogDataFitBit> steps;

    public StepsListFitBit() {
    }

    public StepsListFitBit(List<LogDataFitBit> steps) {
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
