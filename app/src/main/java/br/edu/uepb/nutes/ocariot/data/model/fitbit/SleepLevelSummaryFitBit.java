package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Represents SleepLevelFitBit object present in the sleep levels {@link SleepLevelFitBit}.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SleepLevelSummaryFitBit {
    // Classic Sleep Record (No Heart Rate)
    @SerializedName("awake")
    private SleepLevelSummaryDataFitBit awake;

    // Classic Sleep Record (No Heart Rate)
    @SerializedName("asleep")
    private SleepLevelSummaryDataFitBit asleep;

    // Classic Sleep Record (No Heart Rate)
    @SerializedName("restless")
    private SleepLevelSummaryDataFitBit restless;

    // Classic Sleep Record (No Heart Rate)
    @SerializedName("wake")
    private SleepLevelSummaryDataFitBit wake;

    // Sleep Record with Stages (With Heart Rate)
    @SerializedName("rem")
    private SleepLevelSummaryDataFitBit rem;

    // Sleep Record with Stages (With Heart Rate)
    @SerializedName("light")
    private SleepLevelSummaryDataFitBit light;

    // Sleep Record with Stages (With Heart Rate)
    @SerializedName("deep")
    private SleepLevelSummaryDataFitBit deep;

    public SleepLevelSummaryFitBit() {
    }

    public SleepLevelSummaryFitBit(SleepLevelSummaryDataFitBit awake,
                                   SleepLevelSummaryDataFitBit asleep,
                                   SleepLevelSummaryDataFitBit restless) {
        this.awake = awake;
        this.asleep = asleep;
        this.restless = restless;
    }

    public SleepLevelSummaryFitBit(SleepLevelSummaryDataFitBit wake,
                                   SleepLevelSummaryDataFitBit rem,
                                   SleepLevelSummaryDataFitBit light,
                                   SleepLevelSummaryDataFitBit deep) {
        this.wake = wake;
        this.rem = rem;
        this.light = light;
        this.deep = deep;
    }

    public SleepLevelSummaryDataFitBit getAwake() {
        return awake;
    }

    public void setAwake(SleepLevelSummaryDataFitBit awake) {
        this.awake = awake;
    }

    public SleepLevelSummaryDataFitBit getAsleep() {
        return asleep;
    }

    public void setAsleep(SleepLevelSummaryDataFitBit asleep) {
        this.asleep = asleep;
    }

    public SleepLevelSummaryDataFitBit getRestless() {
        return restless;
    }

    public void setRestless(SleepLevelSummaryDataFitBit restless) {
        this.restless = restless;
    }

    public SleepLevelSummaryDataFitBit getWake() {
        return wake;
    }

    public void setWake(SleepLevelSummaryDataFitBit wake) {
        this.wake = wake;
    }

    public SleepLevelSummaryDataFitBit getRem() {
        return rem;
    }

    public void setRem(SleepLevelSummaryDataFitBit rem) {
        this.rem = rem;
    }

    public SleepLevelSummaryDataFitBit getLight() {
        return light;
    }

    public void setLight(SleepLevelSummaryDataFitBit light) {
        this.light = light;
    }

    public SleepLevelSummaryDataFitBit getDeep() {
        return deep;
    }

    public void setDeep(SleepLevelSummaryDataFitBit deep) {
        this.deep = deep;
    }

    @NonNull
    @Override
    public String toString() {
        return "SleepLevelSummaryFitBit{" +
                "awake=" + awake +
                ", asleep=" + asleep +
                ", restless=" + restless +
                ", wake=" + wake +
                ", rem=" + rem +
                ", light=" + light +
                ", deep=" + deep +
                '}';
    }
}


