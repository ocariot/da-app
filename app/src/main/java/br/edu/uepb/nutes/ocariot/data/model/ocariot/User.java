package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.Objects;

public class User {
    @SerializedName("id")
    protected String _id; // _id in server remote (UUID)

    @SerializedName("username")
    protected String username;

    @SerializedName("password")
    protected String password;

    @SerializedName("institution_id")
    protected String institutionId;

    @SerializedName("last_login")
    protected String lastLogin;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public static class Type {
        public static String ADMIN = "admin";
        public static String CHILD = "child";
        public static String EDUCATOR = "educator";
        public static String HEALTH_PROFESSIONAL = "healthprofessional";
        public static String FAMILY = "family";
        public static String APPLICATION = "application"; // Must not login to APP!!!
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof User)) return false;
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

    /**
     * Convert json to Object.
     *
     * @param json String
     * @return User
     */
    public static User jsonDeserialize(String json) {
        java.lang.reflect.Type typeUser = new TypeToken<User>() {
        }.getType();
        return new Gson().fromJson(json, typeUser);
    }

}

