package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class FitBitAppData {
    @SerializedName("client_id")
    private String clientId;

    @SerializedName("client_secret")
    private String clientSecret;

    public FitBitAppData() {
    }

    public FitBitAppData(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public boolean isEmpty() {
        return this.clientId == null || this.getClientSecret() == null;
    }

    /**
     * Convert json to Object.
     *
     * @param json {@link String}
     * @return UserAccess
     */
    public static FitBitAppData jsonDeserialize(String json) {
        Type typeUserAccess = new TypeToken<FitBitAppData>() {
        }.getType();
        return new Gson().fromJson(json, typeUserAccess);
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
