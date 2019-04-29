package br.edu.uepb.nutes.ocariot.data.model.fitbit;

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

    @SerializedName("features")
    private String foodsLocale;

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

    @SerializedName("topBadges")
    private String waterUnit;

    @SerializedName("waterUnitName")
    private String waterUnitName;

    @SerializedName("weight")
    private double weight;

    @SerializedName("weightUnit")
    private String weightUnit;
}
