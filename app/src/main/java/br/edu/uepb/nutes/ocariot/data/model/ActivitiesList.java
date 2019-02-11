package br.edu.uepb.nutes.ocariot.data.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represents list of activities.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class ActivitiesList {
    @SerializedName("activities")
    private List<Activity> activities;

    public ActivitiesList(List<Activity> activities) {
        this.activities = activities;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    /**
     * Convert object to string in json format.
     *
     * @return String
     */
    public String toJsonString() {
        return new Gson().toJson(this);
    }
}
