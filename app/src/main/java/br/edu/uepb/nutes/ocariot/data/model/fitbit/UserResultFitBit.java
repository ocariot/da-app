package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import com.google.gson.annotations.SerializedName;

public class UserResultFitBit {
    @SerializedName("user")
    private UserFitBit user;

    public UserResultFitBit() {
    }

    public UserFitBit getUser() {
        return user;
    }

    public void setUser(UserFitBit user) {
        this.user = user;
    }
}
