package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Represents User object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class UserFitBit {
    @SerializedName("age")
    private int age;

    @SerializedName("autoStrideEnabled")
    private boolean autoStrideEnabled;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("avatar150")
    private String avatar150;

    @SerializedName("avatar640")
    private String avatar640;

    @SerializedName("averageDailySteps")
    private int averageDailySteps;

    @SerializedName("clockTimeDisplayFormat")
    private String clockTimeDisplayFormat;

    @SerializedName("corporate")
    private boolean corporate;

    @SerializedName("corporateAdmin")
    private boolean corporateAdmin;

    @SerializedName("country")
    private String country;

    @SerializedName("dateOfBirth")
    private String dateOfBirth;

    @SerializedName("displayName")
    private String displayName;

    @SerializedName("distanceUnit")
    private String distanceUnit;

    @SerializedName("encodedId")
    private String encodedId;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("gender")
    private String gender;

    @SerializedName("glucoseUnit")
    private String glucoseUnit;

    @SerializedName("height")
    private double height;

    @SerializedName("heightUnit")
    private String heightUnit;

    @SerializedName("locale")
    private String locale;

    @SerializedName("memberSince")
    private String memberSince;

    @SerializedName("offsetFromUTCMillis")
    private int offsetFromUTCMillis;

    @SerializedName("startDayOfWeek")
    private String startDayOfWeek;

    @SerializedName("state")
    private String state;

    @SerializedName("strideLengthRunning")
    private double strideLengthRunning;

    @SerializedName("strideLengthRunningType")
    private String strideLengthRunningType;

    @SerializedName("strideLengthWalking")
    private double strideLengthWalking;

    @SerializedName("strideLengthWalkingType")
    private String strideLengthWalkingType;

    @SerializedName("timezone")
    private String timezone;

    @SerializedName("waterUnitName")
    private String waterUnitName;

    @SerializedName("weight")
    private double weight;

    @SerializedName("weightUnit")
    private String weightUnit;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isAutoStrideEnabled() {
        return autoStrideEnabled;
    }

    public void setAutoStrideEnabled(boolean autoStrideEnabled) {
        this.autoStrideEnabled = autoStrideEnabled;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar150() {
        return avatar150;
    }

    public void setAvatar150(String avatar150) {
        this.avatar150 = avatar150;
    }

    public String getAvatar640() {
        return avatar640;
    }

    public void setAvatar640(String avatar640) {
        this.avatar640 = avatar640;
    }

    public int getAverageDailySteps() {
        return averageDailySteps;
    }

    public void setAverageDailySteps(int averageDailySteps) {
        this.averageDailySteps = averageDailySteps;
    }

    public String getClockTimeDisplayFormat() {
        return clockTimeDisplayFormat;
    }

    public void setClockTimeDisplayFormat(String clockTimeDisplayFormat) {
        this.clockTimeDisplayFormat = clockTimeDisplayFormat;
    }

    public boolean isCorporate() {
        return corporate;
    }

    public void setCorporate(boolean corporate) {
        this.corporate = corporate;
    }

    public boolean isCorporateAdmin() {
        return corporateAdmin;
    }

    public void setCorporateAdmin(boolean corporateAdmin) {
        this.corporateAdmin = corporateAdmin;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public String getEncodedId() {
        return encodedId;
    }

    public void setEncodedId(String encodedId) {
        this.encodedId = encodedId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGlucoseUnit() {
        return glucoseUnit;
    }

    public void setGlucoseUnit(String glucoseUnit) {
        this.glucoseUnit = glucoseUnit;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getHeightUnit() {
        return heightUnit;
    }

    public void setHeightUnit(String heightUnit) {
        this.heightUnit = heightUnit;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(String memberSince) {
        this.memberSince = memberSince;
    }

    public int getOffsetFromUTCMillis() {
        return offsetFromUTCMillis;
    }

    public void setOffsetFromUTCMillis(int offsetFromUTCMillis) {
        this.offsetFromUTCMillis = offsetFromUTCMillis;
    }

    public String getStartDayOfWeek() {
        return startDayOfWeek;
    }

    public void setStartDayOfWeek(String startDayOfWeek) {
        this.startDayOfWeek = startDayOfWeek;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getStrideLengthRunning() {
        return strideLengthRunning;
    }

    public void setStrideLengthRunning(double strideLengthRunning) {
        this.strideLengthRunning = strideLengthRunning;
    }

    public String getStrideLengthRunningType() {
        return strideLengthRunningType;
    }

    public void setStrideLengthRunningType(String strideLengthRunningType) {
        this.strideLengthRunningType = strideLengthRunningType;
    }

    public double getStrideLengthWalking() {
        return strideLengthWalking;
    }

    public void setStrideLengthWalking(double strideLengthWalking) {
        this.strideLengthWalking = strideLengthWalking;
    }

    public String getStrideLengthWalkingType() {
        return strideLengthWalkingType;
    }

    public void setStrideLengthWalkingType(String strideLengthWalkingType) {
        this.strideLengthWalkingType = strideLengthWalkingType;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getWaterUnitName() {
        return waterUnitName;
    }

    public void setWaterUnitName(String waterUnitName) {
        this.waterUnitName = waterUnitName;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
