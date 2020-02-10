package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;

/**
 * Represents the Response error object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
@Keep
public class ResponseError {
    private int code;

    private String message;

    private String description;

    public ResponseError() {
    }

    public ResponseError(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    public ResponseError fromJSON(String json) {
        return new Gson().fromJson(json, ResponseError.class);
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

