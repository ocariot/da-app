package br.edu.uepb.nutes.activity_tracking_poc.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Activities {
    @SerializedName("activities")
    private List<Activity> activities;

    public Activities(List<Activity> activities) {
        this.activities = activities;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }
}
