package br.edu.uepb.nutes.ocariot.data.model.common;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Represents UserAccess object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class UserAccess implements Parcelable {
    public static final String KEY_SCOPES = "scope";
    public static final String KEY_SUB_TYPE = "sub_type";

    @SerializedName(value = "access_token", alternate = {"token"})
    private String accessToken;

    private String subject;
    private String subjectType;
    private String refreshToken;
    private String tokenType;
    private long expirationDate; // in milliseconds
    private String scopes;

    public UserAccess() {
    }

    protected UserAccess(Parcel in) {
        accessToken = in.readString();
        subject = in.readString();
        subjectType = in.readString();
        refreshToken = in.readString();
        tokenType = in.readString();
        expirationDate = in.readLong();
        scopes = in.readString();
    }

    public static final Creator<UserAccess> CREATOR = new Creator<UserAccess>() {
        @Override
        public UserAccess createFromParcel(Parcel in) {
            return new UserAccess(in);
        }

        @Override
        public UserAccess[] newArray(int size) {
            return new UserAccess[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(accessToken);
        parcel.writeString(subject);
        parcel.writeString(subjectType);
        parcel.writeString(refreshToken);
        parcel.writeString(tokenType);
        parcel.writeLong(expirationDate);
        parcel.writeString(scopes);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public boolean isExpired() {
        if (accessToken != null && !accessToken.isEmpty())
            return new JWT(accessToken).isExpired(0);

        return true;
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
     * @param json
     * @return UserAccess
     */
    public static UserAccess jsonDeserialize(String json) {
        Type typeUserAccess = new TypeToken<UserAccess>() {
        }.getType();
        return new Gson().fromJson(json, typeUserAccess);
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
