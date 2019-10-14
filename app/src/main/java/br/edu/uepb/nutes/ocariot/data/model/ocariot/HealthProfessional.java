package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Represents Educator object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class HealthProfessional extends User {

    @SerializedName("children_groups")
    private List<ChildrenGroup> childrenGroups;

    public HealthProfessional() {
    }

    public HealthProfessional(List<ChildrenGroup> childrenGroups) {
        this.childrenGroups = childrenGroups;
    }

    public List<ChildrenGroup> getChildrenGroups() {
        return childrenGroups;
    }

    public void setChildrenGroups(List<ChildrenGroup> childrenGroups) {
        this.childrenGroups = childrenGroups;
    }

    @Override
    public String toString() {
        return super.toString() + "HealthProfessional{" +
                "childrenGroups='" + childrenGroups + '\'' +
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
    public static HealthProfessional jsonDeserialize(String json) {
        java.lang.reflect.Type typeUser = new TypeToken<HealthProfessional>() {
        }.getType();
        return new Gson().fromJson(json, typeUser);
    }

}
