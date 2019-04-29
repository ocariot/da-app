package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Represents Child object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class Child implements Parcelable {
    @SerializedName("id")
    private String _id; // _id in server remote (UUID)
    private String username;
    private String password;
    private String gender;
    private int age;
    private Institution institution;

    public Child() {
    }

    public Child(String userName, String password) {
        this.username = userName;
        this.password = password;
    }

    protected Child(Parcel in) {
        _id = in.readString();
        username = in.readString();
        password = in.readString();
        gender = in.readString();
        age = in.readInt();
        institution = in.readParcelable(Institution.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(gender);
        dest.writeInt(age);
        dest.writeParcelable(institution, flags);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    /**
     * Convert object to json format.
     *
     * @return String
     */
    public String toJson() {
        return new Gson().toJson(this);
    }

    /**
     * Convert object to string in json format.
     *
     * @return String
     */
    public String toJsonString() {
        return String.valueOf(this.toJson());
    }

    /**
     * Convert json to Object.
     *
     * @param json String
     * @return User
     */
    public static Child jsonDeserialize(String json) {
        Type typeUser = new TypeToken<Child>() {
        }.getType();
        return new Gson().fromJson(json, typeUser);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Child)) return false;

        Child child = (Child) o;
        return age == child.age &&
                Objects.equals(_id, child._id) &&
                Objects.equals(username, child.username) &&
                Objects.equals(password, child.password) &&
                Objects.equals(gender, child.gender) &&
                Objects.equals(institution, child.institution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, username, password, gender, age, institution);
    }

    @Override
    public String toString() {
        return "Child{" +
                "_id='" + _id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", institution=" + institution +
                '}';
    }
}
