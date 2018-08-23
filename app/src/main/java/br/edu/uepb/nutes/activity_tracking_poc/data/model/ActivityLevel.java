package br.edu.uepb.nutes.activity_tracking_poc.data.model;

/**
 * Represents ActivityLevel object.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class ActivityLevel {
    private int minutes;
    private String name;

    public ActivityLevel() {
    }

    public ActivityLevel(int minutes, String name) {
        this.minutes = minutes;
        this.name = name;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ActivityLevel{" +
                "minutes=" + minutes +
                ", name='" + name + '\'' +
                '}';
    }
}
