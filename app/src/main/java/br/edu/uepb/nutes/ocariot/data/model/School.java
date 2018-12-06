package br.edu.uepb.nutes.ocariot.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * Represents the School object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class School implements Parcelable {
    private String name;
    private String country;
    private String city;
    private String address;

    public School() {
    }

    public School(String name, String country, String city, String address) {
        this.name = name;
        this.country = country;
        this.city = city;
        this.address = address;
    }

    protected School(Parcel in) {
        name = in.readString();
        country = in.readString();
        city = in.readString();
        address = in.readString();
    }

    public static final Creator<School> CREATOR = new Creator<School>() {
        @Override
        public School createFromParcel(Parcel in) {
            return new School(in);
        }

        @Override
        public School[] newArray(int size) {
            return new School[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(country);
        dest.writeString(city);
        dest.writeString(address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, country, city, address);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof School)) return false;

        School school = (School) o;
        return Objects.equals(name, school.name) &&
                Objects.equals(country, school.country) &&
                Objects.equals(city, school.city) &&
                Objects.equals(address, school.address);
    }

    @NonNull
    @Override
    public String toString() {
        return "School{" +
                "name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
