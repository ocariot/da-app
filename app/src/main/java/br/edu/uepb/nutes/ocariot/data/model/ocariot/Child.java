package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;

/**
 * Represents Child object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
@Keep
public class Child extends User implements Parcelable, Comparable<Child> {
    @SerializedName("gender")
    private String gender;

    @SerializedName("age")
    private String age;

    @SerializedName("last_sync")
    private String lastSync;

    @SerializedName("fitbit_access")
    private UserAccess fitBitAccess;

    @SerializedName("fitbit_status")
    private String fitbitStatus;

    public Child() {
        super(User.Type.CHILD);
    }

    public Child(String username, String password) {
        super(username, password);
    }

    protected Child(Parcel in) {
        gender = in.readString();
        age = in.readString();
        lastSync = in.readString();
        fitBitAccess = in.readParcelable(UserAccess.class.getClassLoader());
        fitbitStatus = in.readString();
    }

    public static final Creator<Child> CREATOR = new Creator<Child>() {
        @Override
        public Child createFromParcel(Parcel in) {
            return new Child(in);
        }

        @Override
        public Child[] newArray(int size) {
            return new Child[size];
        }
    };

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLastSync() {
        return lastSync;
    }

    public void setLastSync(String lastSync) {
        this.lastSync = lastSync;
    }

    public UserAccess getFitBitAccess() {
        return fitBitAccess;
    }

    public void setFitBitAccess(UserAccess fitBitAccess) {
        this.fitBitAccess = fitBitAccess;
        if (fitBitAccess == null) this.fitbitStatus = UserAccess.TokenStatus.NONE;
        else this.fitbitStatus = fitBitAccess.getStatus();
    }

    public boolean isFitbitAccessValid() {
        if (this.fitbitStatus == null) return false;
        return fitbitStatus.equalsIgnoreCase(UserAccess.TokenStatus.VALID) ||
                fitbitStatus.equalsIgnoreCase(UserAccess.TokenStatus.EXPIRED) ||
                fitbitStatus.equalsIgnoreCase(UserAccess.TokenStatus.RATE_LIMIT);
    }

    /**
     * Convert json to Object.
     *
     * @param json String
     * @return User
     */
    public static Child jsonDeserialize(String json) {
        return new Gson().fromJson(json, Child.class);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), super.getUsername());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Child)) return false;
        Child child = (Child) o;
        return Objects.equals(super.getUsername(), child.getUsername());
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public int compareTo(Child o) {
        return super.getUsername().compareToIgnoreCase(o.getUsername());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gender);
        dest.writeString(age);
        dest.writeString(lastSync);
        dest.writeParcelable(fitBitAccess, flags);
        dest.writeString(fitbitStatus);
    }
}
