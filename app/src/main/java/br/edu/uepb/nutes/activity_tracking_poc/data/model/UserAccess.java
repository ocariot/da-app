package br.edu.uepb.nutes.activity_tracking_poc.data.model;

import com.auth0.android.jwt.JWT;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents UserAccess object.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class UserAccess implements Serializable {
    public static final String ROLES_NAME = "scopes";

    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_TOKEN_TYPE = "token_type";
    private static final String KEY_EXPIRATION_DATE = "expiration_date";
    private static final String KEY_SCOPES = ROLES_NAME;

    private String subject;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expirationDate; // in milliseconds
    private List<String> scopes;

    public UserAccess() {
    }

    public UserAccess(String subject, String accessToken, String refreshToken,
                      String tokenType, long expirationDate, List<String> scopes, int mode) {
        this.subject = subject;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expirationDate = expirationDate;
        this.scopes = scopes;
    }

    public UserAccess(String subject, String accessToken, long expirationDate,
                      List<String> scopes, int mode) {
        this.subject = subject;
        this.accessToken = accessToken;
        this.expirationDate = expirationDate;
        this.scopes = scopes;
    }

    public UserAccess(String subject, String accessToken, long expirationDate, List<String> scopes) {
        this.subject = subject;
        this.accessToken = accessToken;
        this.expirationDate = expirationDate;
        this.scopes = scopes;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public void addScope(String scope) {
        if (this.scopes == null) this.scopes = new ArrayList<>();
        this.scopes.add(scope);
    }

    public boolean isExpired() {
        if (accessToken != null && !accessToken.isEmpty())
            return new JWT(accessToken).isExpired(0);

        return true;
    }

    /**
     * Convert object to string in json format.
     *
     * @return String
     */
    public String toJsonString() {
        return String.valueOf(jsonSerialize());
    }

    /**
     * Convert object to JSON.
     *
     * @return JSONObject
     */
    private JSONObject jsonSerialize() {
        JSONObject json = new JSONObject();

        try {
            json.put(KEY_SUBJECT, this.subject);
            json.put(KEY_ACCESS_TOKEN, this.accessToken);
            json.put(KEY_REFRESH_TOKEN, this.refreshToken);
            json.put(KEY_TOKEN_TYPE, this.tokenType);
            json.put(KEY_EXPIRATION_DATE, this.expirationDate);

            JSONArray arrayScopes = new JSONArray();
            if (this.scopes != null) arrayScopes = new JSONArray(this.scopes);

            json.put(KEY_SCOPES, arrayScopes);
        } catch (JSONException e) {
            throw new IllegalStateException("JSONException thrown in violation of contract!", e);
        }

        return json;
    }


    public static UserAccess jsonDeserialize(String json) {
        UserAccess userAccess = new UserAccess();
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(json);

            if (jsonObject.has(KEY_SUBJECT)) {
                userAccess.setSubject(jsonObject.getString(KEY_SUBJECT));
            }

            if (jsonObject.has(KEY_ACCESS_TOKEN)) {
                userAccess.setAccessToken(jsonObject.getString(KEY_ACCESS_TOKEN));
            }

            if (jsonObject.has(KEY_REFRESH_TOKEN)) {
                userAccess.setRefreshToken(jsonObject.getString(KEY_REFRESH_TOKEN));
            }

            if (jsonObject.has(KEY_TOKEN_TYPE)) {
                userAccess.setTokenType(jsonObject.getString(KEY_TOKEN_TYPE));
            }

            if (jsonObject.has(KEY_EXPIRATION_DATE)) {
                userAccess.setExpirationDate(jsonObject.getLong(KEY_EXPIRATION_DATE));
            }

            if (jsonObject.has(KEY_SCOPES)) {
                JSONArray scopes_array = jsonObject.getJSONArray(KEY_SCOPES);

                for (int i = 0; i < scopes_array.length(); i++)
                    userAccess.addScope(scopes_array.getString(i));
            }
        } catch (JSONException e) {
            throw new IllegalStateException("JSONException thrown in violation of contract!", e);
        }

        return userAccess;
    }


    @Override
    public String toString() {
        return "UserAccess{" +
                "subject='" + subject + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expirationDate=" + expirationDate +
                ", scopes=" + (scopes != null ? Arrays.toString(scopes.toArray()) : "") +
                '}';
    }
}
