package com.worldcuptracking.model;

import java.io.Serializable;

/**
 * Created by hafiz on 4/16/2018.
 */
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class StadiumInfo implements Serializable {

    public int namee;
    public int imgUrl;
    public String link;
    public int cityy,capacityy,opening;


    public StadiumInfo(int name, int b, String c, int city,int capacity,int open){

        imgUrl = b;
        link = c;

        namee = name;
        cityy = city;
        capacityy = capacity;
        opening = open;

    }







}
