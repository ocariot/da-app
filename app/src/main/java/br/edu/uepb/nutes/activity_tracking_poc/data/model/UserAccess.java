package br.edu.uepb.nutes.activity_tracking_poc.data.model;

import com.auth0.android.jwt.JWT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserAccess implements Serializable {
    public static final String ROLES_NAME = "scopes";

    private String id;
    private String accessToken;
    private long expirationDate; // in milliseconds
    private List<String> scopes;

    /**
     * {@link UserAccessMode}
     */
    private int mode;

    public UserAccess() {
    }

    public UserAccess(String id, String accessToken, long expirationDate, List<String> scopes, int mode) {
        this.id = id;
        this.accessToken = accessToken;
        this.expirationDate = expirationDate;
        this.scopes = scopes;
        this.mode = mode;
    }

    public UserAccess(String id, String accessToken, long expirationDate, List<String> scopes) {
        this.id = id;
        this.accessToken = accessToken;
        this.expirationDate = expirationDate;
        this.scopes = scopes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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

    public void addScopes(String scope) {
        if (this.scopes == null) this.scopes = new ArrayList<>();
        this.scopes.add(scope);
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isExpired() {
        if (accessToken != null && !accessToken.isEmpty())
            return new JWT(accessToken).isExpired(0);

        return true;
    }

    @Override
    public String toString() {
        return "UserAccess{" +
                "id='" + id + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", expirationDate=" + expirationDate +
                ", scopes=" + (scopes != null ? Arrays.toString(scopes.toArray()) : "") +
                ", mode=" + mode +
                '}';
    }
}
