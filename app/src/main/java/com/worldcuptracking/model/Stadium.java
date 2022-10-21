package com.worldcuptracking.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
@SuppressWarnings("serial")
public class Stadium implements Serializable {
    public Integer id;
    public String name;
    public String city;
    public Float lat;
    public Float lng;

    /**
     * No args constructor for use in serialization
     */
    public Stadium() {
    }

    /**
     * @param id
     * @param name
     * @param lng
     * @param lat
     * @param city
     */
    public Stadium(Integer id, String name, String city, Float lat, Float lng) {
        super();
        this.id = id;
        this.name = name;
        this.city = city;
        this.lat = lat;
        this.lng = lng;
    }

}
