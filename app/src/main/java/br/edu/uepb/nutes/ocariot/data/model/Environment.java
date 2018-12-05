package br.edu.uepb.nutes.ocariot.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Represents the object of measuring the environment.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class Environment implements Parcelable {
    private String timestamp;
    private float temperature;
    private float humidity;
    private Location location;

    public Environment() {
    }

    public Environment(String timestamp, float temperature, float humidity, Location location) {
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.location = location;
    }

    protected Environment(Parcel in) {
        timestamp = in.readString();
        temperature = in.readFloat();
        humidity = in.readFloat();
        location = in.readParcelable(Location.class.getClassLoader());
    }

    public static final Creator<Environment> CREATOR = new Creator<Environment>() {
        @Override
        public Environment createFromParcel(Parcel in) {
            return new Environment(in);
        }

        @Override
        public Environment[] newArray(int size) {
            return new Environment[size];
        }
    };

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Environment)) return false;

        Environment that = (Environment) o;
        return Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(location.getRoom(), that.location.getRoom());
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, location.getRoom());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(timestamp);
        dest.writeFloat(temperature);
        dest.writeFloat(humidity);
        dest.writeParcelable(location, flags);
    }

    @Override
    public String toString() {
        return "Environment{" +
                "timestamp='" + timestamp + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", location=" + location +
                '}';
    }
}

