package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@Keep
public class User {
    @SerializedName("id")
    private String id;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("institution_id")
    private String institutionId;

    @SerializedName("last_login")
    private String lastLogin;

    private String type;

    public User() {
    }

    public User(String type) {
        this.type = type;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isEmpty() {
        return this.id == null || this.username == null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    /**
     * Convert object to json format.
     *
     * @return String
     */
    public String toJson() {
        return new Gson().toJson(this);
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static class Type {
        private Type() {
            throw new IllegalStateException("Utility class. Does not allow inheritance or instances to be created!");
        }

        public static final String ADMIN = "admin";
        public static final String CHILD = "child";
        public static final String EDUCATOR = "educator";
        public static final String HEALTH_PROFESSIONAL = "healthprofessional";
        public static final String FAMILY = "family";
        public static final String APPLICATION = "application"; // Must not login to APP!!!
    }
}
