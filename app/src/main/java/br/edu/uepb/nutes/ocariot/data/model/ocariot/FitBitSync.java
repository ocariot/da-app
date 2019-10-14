package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import com.google.gson.annotations.SerializedName;

public class FitBitSync {
    @SerializedName("activities")
    private int activities;

    @SerializedName("sleep")
    private int sleep;

    @SerializedName("weights")
    private int weights;

    @SerializedName("logs")
    private FitBitLogsSync logs;

    public FitBitSync() {
    }

    public FitBitSync(int activities, int sleep, int weights, FitBitLogsSync logs) {
        this.activities = activities;
        this.sleep = sleep;
        this.weights = weights;
        this.logs = logs;
    }

    public int getActivities() {
        return activities;
    }

    public void setActivities(int activities) {
        this.activities = activities;
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public int getWeights() {
        return weights;
    }

    public void setWeights(int weights) {
        this.weights = weights;
    }

    public FitBitLogsSync getLogs() {
        return logs;
    }

    public void setLogs(FitBitLogsSync logs) {
        this.logs = logs;
    }

    @Override
    public String toString() {
        return "FitBitSync{" +
                "activities=" + activities +
                ", sleep=" + sleep +
                ", weights=" + weights +
                ", logs=" + logs +
                '}';
    }
}
