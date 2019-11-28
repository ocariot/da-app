package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class WeightListFitBit {
    @SerializedName("weight")
    private List<WeightFitBit> weights;

    public WeightListFitBit() {
    }

    public WeightListFitBit(List<WeightFitBit> weights) {
        this.weights = weights;
    }

    public List<WeightFitBit> getWeights() {
        return weights;
    }

    public void setWeights(List<WeightFitBit> weights) {
        this.weights = weights;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
