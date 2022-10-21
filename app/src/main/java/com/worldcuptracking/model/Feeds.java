package com.worldcuptracking.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class Feeds implements Serializable {
    public String title;
    public String desc;
    public String link;
    public String img;
    public String date;

    public Feeds() {
    }

    public Feeds(String title, String desc, String link, String img, String date) {
        this.title = title;
        this.desc = desc;
        this.link = link;
        this.img = img;
        this.date = date;
    }
}
