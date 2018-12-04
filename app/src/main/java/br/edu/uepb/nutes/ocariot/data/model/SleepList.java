package br.edu.uepb.nutes.ocariot.data.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represents list of sleep.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SleepList {
    @SerializedName("sleep")
    private List<Sleep> sleepList;

    public SleepList(List<Sleep> sleepList) {
        this.sleepList = sleepList;
    }

    public List<Sleep> getSleepList() {
        return sleepList;
    }

    public void setSleepList(List<Sleep> sleepList) {
        this.sleepList = sleepList;
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
