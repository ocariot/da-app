package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class LogData {
    @SerializedName("date")
    private String date;

    @SerializedName("value")
    private int value;

    public LogData() {
    }

    public LogData(String date, int value) {
        this.date = date;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return "LogData{" +
                "date='" + date + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
