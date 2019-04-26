package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StepsLogList {
    @SerializedName("activities-steps")
    private List<LogData> steps;

    public StepsLogList() {
    }

    public StepsLogList(List<LogData> steps) {
        this.steps = steps;
    }

    public List<LogData> getSteps() {
        return steps;
    }

    public void setSteps(List<LogData> steps) {
        this.steps = steps;
    }

    @NonNull
    @Override
    public String toString() {
        return "StepsLogList{" +
                "steps=" + steps +
                '}';
    }
}
