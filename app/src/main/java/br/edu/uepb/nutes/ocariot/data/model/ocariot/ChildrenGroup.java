package br.edu.uepb.nutes.ocariot.data.model.ocariot;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/**
 * Represents ChildrenGroup object.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
@Keep
public class ChildrenGroup {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("school_class")
    private String schoolClass;

    @SerializedName("children")
    private List<Child> children;

    public ChildrenGroup() {
    }

    public ChildrenGroup(String name, List<Child> children) {
        this.name = name;
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(String schoolClass) {
        this.schoolClass = schoolClass;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChildrenGroup that = (ChildrenGroup) o;
        return Objects.equals(id, that.id) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    /**
     * Convert json to Object.
     *
     * @param json String
     * @return User
     */
    public static ChildrenGroup jsonDeserialize(String json) {
        return new Gson().fromJson(json, ChildrenGroup.class);
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
