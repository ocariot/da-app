package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Represents the Location object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class Location implements Parcelable, Comparable<Location> {
    @SerializedName("local")
    private String local;

    @SerializedName("room")
    private String room;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    public Location() {
    }

    public Location(String local, String room, double latitude, double longitude) {
        this.local = local;
        this.room = room;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Location(Parcel in) {
        local = in.readString();
        room = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(local);
        dest.writeString(room);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int hashCode() {
        return Objects.hash(local, room, latitude, longitude);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Location)) return false;

        Location location = (Location) o;
        return Double.compare(location.latitude, latitude) == 0 &&
                Double.compare(location.longitude, longitude) == 0 &&
                Objects.equals(local, location.local) &&
                Objects.equals(room, location.room);
    }

    @Override
    public int compareTo(Location o) {
        int value = this.room.compareTo(o.room);
        if (value < 0) {
            return -1;
        }

        if (value > 0) {
            return 1;
        }

        return 0;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
