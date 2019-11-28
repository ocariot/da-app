package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represents the Measurement object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
@Keep
public class MultiStatusResult<T> {
    @SerializedName("success")
    private List<MultiStatusSuccess<T>> success;

    @SerializedName("error")
    private List<MultiStatusError<T>> error;

    public MultiStatusResult() {
    }

    public MultiStatusResult(List<MultiStatusSuccess<T>> success, List<MultiStatusError<T>> error) {
        this.success = success;
        this.error = error;
    }

    public List<MultiStatusSuccess<T>> getSuccess() {
        return success;
    }

    public void setSuccess(List<MultiStatusSuccess<T>> success) {
        this.success = success;
    }

    public List<MultiStatusError<T>> getError() {
        return error;
    }

    public void setError(List<MultiStatusError<T>> error) {
        this.error = error;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

