package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Represents the Measurement object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
@Keep
public class Measurement implements Parcelable {
    @SerializedName("value")
    private double value;

    @SerializedName("unit")
    private String unit;

    @SerializedName("type")
    private String type;

    public Measurement() {
    }

    public Measurement(String unit, double value, String type) {
        this.value = value;
        this.unit = unit;
        this.type = type;
    }

    protected Measurement(Parcel in) {
        value = in.readDouble();
        unit = in.readString();
        type = in.readString();
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static final Creator<Measurement> CREATOR = new Creator<Measurement>() {
        @Override
        public Measurement createFromParcel(Parcel in) {
            return new Measurement(in);
        }

        @Override
        public Measurement[] newArray(int size) {
            return new Measurement[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(value);
        dest.writeString(unit);
        dest.writeString(type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, unit, type);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Measurement)) return false;

        Measurement that = (Measurement) o;
        return Double.compare(that.value, value) == 0 &&
                Objects.equals(unit, that.unit) &&
                Objects.equals(type, that.type);
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    class Type {
        private Type() {
            throw new IllegalStateException("Utility class. Does not allow inheritance or instances to be created!");
        }

        static final String TEMPERATURE = "temperature";
        static final String HUMIDITY = "humidity";
    }
}
