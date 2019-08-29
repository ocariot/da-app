package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Represents the Measurement object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class MultiStatusError<T> {
    @SerializedName("code")
    private String code;

    @SerializedName("message")
    private String message;

    @SerializedName("description")
    private String description;

    @SerializedName("item")
    private T item;

    public MultiStatusError() {
    }

    public MultiStatusError(String code, String message, String description, T item) {
        this.code = code;
        this.message = message;
        this.description = description;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

