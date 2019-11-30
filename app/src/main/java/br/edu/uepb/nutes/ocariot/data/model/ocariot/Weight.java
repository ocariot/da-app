package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

@Keep
public class Weight implements Parcelable {
    @SerializedName("id")
    private String id; // id in server remote (UUID)

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("value")
    private double value;

    @SerializedName("unit")
    private String unit;

    @SerializedName("body_fat")
    private Double bodyFat;

    @SerializedName(value = "child_id")
    private String childId;

    public Weight() {
    }

    public Weight(String timestamp, double value, String unit, Double bodyFat) {
        this.timestamp = timestamp;
        this.value = value;
        this.unit = unit;
        this.bodyFat = bodyFat;
    }

    protected Weight(Parcel in) {
        id = in.readString();
        timestamp = in.readString();
        value = in.readDouble();
        unit = in.readString();
        bodyFat = in.readDouble();
        childId = in.readString();
        bodyFat = null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(timestamp);
        dest.writeDouble(value);
        dest.writeString(unit);
        dest.writeDouble(bodyFat);
        dest.writeString(childId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Weight> CREATOR = new Creator<Weight>() {
        @Override
        public Weight createFromParcel(Parcel in) {
            return new Weight(in);
        }

        @Override
        public Weight[] newArray(int size) {
            return new Weight[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(Double bodyFat) {
        this.bodyFat = bodyFat;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
