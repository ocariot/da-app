package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Represents the Location object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
@Keep
public class Institution implements Parcelable {
    @SerializedName("id")
    private String _id; // _id in server remote (UUID)

    @SerializedName("type")
    private String type;

    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    public Institution() {
    }

    public Institution(String name, String type) {
        this.name = name;
        this.type = type;
    }

    protected Institution(Parcel in) {
        _id = in.readString();
        name = in.readString();
        type = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Institution> CREATOR = new Creator<Institution>() {
        @Override
        public Institution createFromParcel(Parcel in) {
            return new Institution(in);
        }

        @Override
        public Institution[] newArray(int size) {
            return new Institution[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        return Objects.hash(_id, name, type, address, latitude, longitude);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Institution)) return false;

        Institution that = (Institution) o;
        return Objects.equals(name, that.name);
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
