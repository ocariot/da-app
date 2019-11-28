package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Represents the Measurement object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class MultiStatusSuccess<T> {
    @SerializedName("code")
    private String code;

    @SerializedName("item")
    private T item;

    public MultiStatusSuccess() {
    }

    public MultiStatusSuccess(String code, T item) {
        this.code = code;
        this.item = item;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

