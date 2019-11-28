package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represents list of activities from platform FitBit.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
@Keep
public class ActivitiesListFitBit {
    @SerializedName("activities")
    private List<PhysicalActivityFitBit> activities;

    public ActivitiesListFitBit(List<PhysicalActivityFitBit> activities) {
        this.activities = activities;
    }

    public List<PhysicalActivityFitBit> getActivities() {
        return activities;
    }

    public void setActivities(List<PhysicalActivityFitBit> activities) {
        this.activities = activities;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
