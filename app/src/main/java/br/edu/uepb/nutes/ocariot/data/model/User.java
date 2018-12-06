package br.edu.uepb.nutes.ocariot.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Represents User object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class User implements Parcelable {
    @SerializedName("id")
    private String _id; // _id in server remote (UUID)

    @SerializedName("name")
    private String name;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("password")
    private String password;

    @SerializedName("school")
    private School school;

    private int gender;
    private long dateBirth;
    private int height; // in cm
    private int groupId; // 1 super, 2 comum

    public User() {
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User(String _id, String name, String userName, String password, School school,
                int gender, long dateBirth, int height, int groupId) {
        this._id = _id;
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.school = school;
        this.gender = gender;
        this.dateBirth = dateBirth;
        this.height = height;
        this.groupId = groupId;
    }

    protected User(Parcel in) {
        _id = in.readString();
        name = in.readString();
        userName = in.readString();
        password = in.readString();
        school = in.readParcelable(School.class.getClassLoader());
        gender = in.readInt();
        dateBirth = in.readLong();
        height = in.readInt();
        groupId = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public long getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(long dateBirth) {
        this.dateBirth = dateBirth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
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
     * Convert object to json format.
     *
     * @return String
     */
    public String toJson() {
        return new Gson().toJson(this);
    }

    /**
     * Convert json to Object.
     *
     * @param json String
     * @return User
     */
    public static User jsonDeserialize(String json) {
        Type typeUser = new TypeToken<User>() {
        }.getType();
        return new Gson().fromJson(json, typeUser);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeString(userName);
        dest.writeString(password);
        dest.writeParcelable(school, flags);
        dest.writeInt(gender);
        dest.writeLong(dateBirth);
        dest.writeInt(height);
        dest.writeInt(groupId);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User))
            return false;

        User other = (User) o;

        return other.getUserName().equals(this.getUserName());
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", school=" + school +
                ", gender=" + gender +
                ", dateBirth=" + dateBirth +
                ", height=" + height +
                ", groupId=" + groupId +
                '}';
    }
}
