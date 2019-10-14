package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Represents ChildrenGroup object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class Family extends User {

    @SerializedName("children")
    private List<Child> children;

    public Family() {
    }

    public Family(List<Child> children) {
        this.children = children;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return super.toString() + "Family{" +
                "children=" + children +
                '}';
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
    public static Family jsonDeserialize(String json) {
        java.lang.reflect.Type typeUser = new TypeToken<Family>() {
        }.getType();
        return new Gson().fromJson(json, typeUser);
    }

}
