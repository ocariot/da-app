package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/**
 * Represents the object of measuring the environment.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
@Keep
public class Environment implements Parcelable {
    @SerializedName("id")
    private String id; // id in server remote (UUID)

    @SerializedName("institution_id")
    private String institutionId;

    @SerializedName("location")
    private Location location;

    @SerializedName("measurements")
    private List<Measurement> measurements;

    @SerializedName("climatized")
    private boolean climatized;

    @SerializedName("timestamp")
    private String timestamp;

    public Environment() {
    }

    public Environment(String institution_id, Location location,
                       List<Measurement> measurements, String timestamp) {
        this.institutionId = institution_id;
        this.location = location;
        this.measurements = measurements;
        this.timestamp = timestamp;
    }

    protected Environment(Parcel in) {
        id = in.readString();
        institutionId = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        measurements = in.createTypedArrayList(Measurement.CREATOR);
        timestamp = in.readString();
        climatized = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(institutionId);
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
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
                Objects.equals(id, that.id) &&
                Objects.equals(institutionId, that.institutionId) &&
                Objects.equals(location, that.location) &&
                Objects.equals(measurements, that.measurements) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, institutionId, location, measurements, timestamp, climatized);
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}