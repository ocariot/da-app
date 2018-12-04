package br.edu.uepb.nutes.ocariot.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents the Location object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class Location implements Parcelable {
    private String school;
    private String room;
    private String country;
    private String city;

    public Location() {
    }

    public Location(String school, String room, String country, String city) {
        this.school = school;
        this.room = room;
        this.country = country;
        this.city = city;
    }

    protected Location(Parcel in) {
        school = in.readString();
        room = in.readString();
        country = in.readString();
        city = in.readString();
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

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(school);
        dest.writeString(room);
        dest.writeString(country);
        dest.writeString(city);
    }

    @Override
    public String toString() {
        return "Location{" +
                "school='" + school + '\'' +
                ", room='" + room + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
