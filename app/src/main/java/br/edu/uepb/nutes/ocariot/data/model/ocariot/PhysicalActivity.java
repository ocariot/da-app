package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents Physical Activity object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
@Keep
public class PhysicalActivity extends Activity implements Parcelable {
    @SerializedName(value = "name", alternate = {"activityName"})
    private String name;

    @SerializedName(value = "calories")
    private int calories;

    @SerializedName(value = "steps")
    private int steps;

    @SerializedName(value = "levels", alternate = {"activityLevel"})
    private List<ActivityLevel> levels;

    @SerializedName(value = "heart_rate")
    private HeartRateZone heartRate;

    @SerializedName(value = "distance")
    private Double distance;

    public PhysicalActivity() {
        super();
    }

    protected PhysicalActivity(Parcel in) {
        super.setId(in.readString());
        super.setStartTime(in.readString());
        super.setEndTime(in.readString());
        super.setDuration(in.readLong());
        super.setChildId(in.readString());
        name = in.readString();
        calories = in.readInt();
        steps = in.readInt();
        levels = in.createTypedArrayList(ActivityLevel.CREATOR);
        heartRate = in.readParcelable(HeartRateZone.class.getClassLoader());
        if (in.readByte() == 0) {
            distance = null;
        } else {
            distance = in.readDouble();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(super.getId());
        dest.writeString(super.getStartTime());
        dest.writeString(super.getEndTime());
        dest.writeLong(super.getDuration());
        dest.writeString(super.getChildId());
        dest.writeString(name);
        dest.writeInt(calories);
        dest.writeInt(steps);
        dest.writeTypedList(levels);
        dest.writeParcelable(heartRate, flags);
        if (distance == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(distance);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhysicalActivity> CREATOR = new Creator<PhysicalActivity>() {
        @Override
        public PhysicalActivity createFromParcel(Parcel in) {
            return new PhysicalActivity(in);
        }

        @Override
        public PhysicalActivity[] newArray(int size) {
            return new PhysicalActivity[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public List<ActivityLevel> getLevels() {
        return levels;
    }

    public void setLevels(List<ActivityLevel> levels) {
        this.levels = levels;
    }

    public boolean addLevel(ActivityLevel level) {
        if (this.levels == null) this.levels = new ArrayList<>();
        return this.levels.add(level);
    }

    public HeartRateZone getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(HeartRateZone heartRate) {
        this.heartRate = heartRate;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
