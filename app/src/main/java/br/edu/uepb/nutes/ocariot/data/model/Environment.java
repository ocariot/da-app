package br.edu.uepb.nutes.ocariot.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/**
 * Represents the object of measuring the environment.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class Environment implements Parcelable {
    @SerializedName("id")
    private String _id; // _id in server remote (UUID)
    private String institution_id;
    private Location location;
    private List<Measurement> measurements;
    private String timestamp;
    private boolean climatized;

    public Environment() {
    }

    public Environment(String institution_id, Location location,
                       List<Measurement> measurements, String timestamp) {
        this.institution_id = institution_id;
        this.location = location;
        this.measurements = measurements;
        this.timestamp = timestamp;
    }

    protected Environment(Parcel in) {
        _id = in.readString();
        institution_id = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        measurements = in.createTypedArrayList(Measurement.CREATOR);
        timestamp = in.readString();
        climatized = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(institution_id);
        dest.writeParcelable(location, flags);
        dest.writeTypedList(measurements);
        dest.writeString(timestamp);
        dest.writeByte((byte) (climatized ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getInstitution_id() {
        return institution_id;
    }

    public void setInstitution_id(String institution_id) {
        this.institution_id = institution_id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isClimatized() {
        return climatized;
    }

    public void setClimatized(boolean climatized) {
        this.climatized = climatized;
    }

    public double getTemperature() {
        for (Measurement m : getMeasurements()) {
            if (m.getType().equalsIgnoreCase(MeasurementType.TEMPERATURE))
                return m.getValue();
        }
        return 0;
    }

    public double getHumidity() {
        for (Measurement m : getMeasurements()) {
            if (m.getType().equalsIgnoreCase(MeasurementType.HUMIDITY))
                return m.getValue();
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Environment)) return false;

        Environment that = (Environment) o;
        return climatized == that.climatized &&
                Objects.equals(_id, that._id) &&
                Objects.equals(institution_id, that.institution_id) &&
                Objects.equals(location, that.location) &&
                Objects.equals(measurements, that.measurements) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, institution_id, location, measurements, timestamp, climatized);
    }

    @Override
    public String toString() {
        return "Environment{" +
                "_id='" + _id + '\'' +
                ", institution_id='" + institution_id + '\'' +
                ", location=" + location +
                ", measurements=" + measurements +
                ", timestamp='" + timestamp + '\'' +
                ", climatized=" + climatized +
                '}';
    }
}