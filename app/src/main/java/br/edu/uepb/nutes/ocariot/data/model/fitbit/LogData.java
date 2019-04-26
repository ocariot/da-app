package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class LogData {
    @SerializedName("dateTime")
    private String date;

    @SerializedName("value")
    private String value;

    public LogData() {
    }

    public LogData(String date, String value) {
        this.date = date;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
