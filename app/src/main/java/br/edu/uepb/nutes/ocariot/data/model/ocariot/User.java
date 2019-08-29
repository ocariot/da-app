package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private String _id; // _id in server remote (UUID)

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("institution_id")
    private String institutionId;

    @SerializedName("last_login")
    private String lastLogin;

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
}

