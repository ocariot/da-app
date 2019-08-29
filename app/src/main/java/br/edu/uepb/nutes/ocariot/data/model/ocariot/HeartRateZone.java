package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class HeartRateZone implements Parcelable {
    @SerializedName("average")
    private int average;

    @SerializedName("out_of_range_zone")
    private HeartRateZoneItem outOfRangeZone;

    @SerializedName("fat_burn_zone")
    private HeartRateZoneItem fatBurnZone;

    @SerializedName("cardio_zone")
    private HeartRateZoneItem cardioZone;

    @SerializedName("peak_zone")
    private HeartRateZoneItem peakZone;

    public HeartRateZone() {
    }

    public HeartRateZone(int average, HeartRateZoneItem outOfRangeZone,
                         HeartRateZoneItem fatBurnZone, HeartRateZoneItem cardioZone,
                         HeartRateZoneItem peakZone) {
        this.average = average;
        this.outOfRangeZone = outOfRangeZone;
        this.fatBurnZone = fatBurnZone;
        this.cardioZone = cardioZone;
        this.peakZone = peakZone;
    }

    public static final Creator<HeartRateZone> CREATOR = new Creator<HeartRateZone>() {
        @Override
        public HeartRateZone createFromParcel(Parcel in) {
            return new HeartRateZone(in);
        }

        @Override
        public HeartRateZone[] newArray(int size) {
            return new HeartRateZone[size];
        }
    };

    protected HeartRateZone(Parcel in) {
        average = in.readInt();
        outOfRangeZone = in.readParcelable(HeartRateZoneItem.class.getClassLoader());
        fatBurnZone = in.readParcelable(HeartRateZoneItem.class.getClassLoader());
        cardioZone = in.readParcelable(HeartRateZoneItem.class.getClassLoader());
        peakZone = in.readParcelable(HeartRateZoneItem.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(average);
        dest.writeParcelable(outOfRangeZone, flags);
        dest.writeParcelable(fatBurnZone, flags);
        dest.writeParcelable(cardioZone, flags);
        dest.writeParcelable(peakZone, flags);
    }

    public int getAverage() {
        return average;
    }

    public void setAverage(int average) {
        this.average = average;
    }

    public HeartRateZoneItem getOutOfRangeZone() {
        return outOfRangeZone;
    }

    public void setOutOfRangeZone(HeartRateZoneItem outOfRangeZone) {
        this.outOfRangeZone = outOfRangeZone;
    }

    public HeartRateZoneItem getFatBurnZone() {
        return fatBurnZone;
    }

    public void setFatBurnZone(HeartRateZoneItem fatBurnZone) {
        this.fatBurnZone = fatBurnZone;
    }

    public HeartRateZoneItem getCardioZone() {
        return cardioZone;
    }

    public void setCardioZone(HeartRateZoneItem cardioZone) {
        this.cardioZone = cardioZone;
    }

    public HeartRateZoneItem getPeakZone() {
        return peakZone;
    }

    public void setPeakZone(HeartRateZoneItem peakZone) {
        this.peakZone = peakZone;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
