package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

@Keep
public class WeightFitBit {
    @SerializedName("bmi")
    private double bmi;

    @SerializedName("weight")
    private double value;

    @SerializedName("fat")
    private double fat;

    @SerializedName("logId")
    private long logId;

    @SerializedName("source")
    private String source;

    @SerializedName("date")
    private String date;

    @SerializedName("time")
    private String time;

    public WeightFitBit() {
    }

    public WeightFitBit(double bmi, double value, double fat,
                        long logId, String source, String date, String time) {
        this.bmi = bmi;
        this.value = value;
        this.fat = fat;
        this.logId = logId;
        this.source = source;
        this.date = date;
        this.time = time;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
