package br.edu.uepb.nutes.ocariot.uaal_poc.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents User object.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 2.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class User implements Parcelable {
    private String _id; // _id in server remote (UUID)

    private String name;
    private String userName;
    private String password;
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

    public User(String _id, String name, String userName, String email,
                String password, int gender, long dateBirth, int height, int groupId) {
        this._id = _id;
        this.name = name;
        this.userName = userName;
        this.password = password;
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

    public long getdateBirth() {
        return dateBirth;
    }

    public void setdateBirth(long dateBirth) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(_id);
        parcel.writeString(name);
        parcel.writeString(userName);
        parcel.writeString(password);
        parcel.writeInt(gender);
        parcel.writeLong(dateBirth);
        parcel.writeInt(height);
        parcel.writeInt(groupId);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User))
            return false;

        User other = (User) o;

        return other.getUserName().equals(this.getUserName());
    }

    @Override
    public String toString() {
        return "User{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", gender=" + gender +
                ", dateBirth=" + dateBirth +
                ", height=" + height +
                ", groupId=" + groupId +
                '}';
    }
}
