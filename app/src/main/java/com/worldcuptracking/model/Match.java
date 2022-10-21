package com.worldcuptracking.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

@IgnoreExtraProperties
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class Match implements Serializable {

    public Integer name;
    public String type;
    public Integer home_team;
    public Integer away_team;
    public Integer home_result;
    public Integer away_result;
    public String date;
    public Integer stadium;
    public List<Integer> channels = null;
    public Boolean finished;


    /**
     * No args constructor for use in serialization
     */
    public Match() {
    }


    /**
     * @param home_result
     * @param away_team
     * @param stadium
     * @param name
     * @param channels
     * @param finished
     * @param home_team
     * @param date
     * @param type
     * @param away_result
     */
    public Match(Integer name, String type, Integer home_team, Integer away_team, Integer home_result, Integer away_result, String date, Integer stadium, List<Integer> channels, Boolean finished) {
        super();
        this.name = name;
        this.type = type;
        this.home_team = home_team;
        this.away_team = away_team;
        this.home_result = home_result;
        this.away_result = away_result;
        this.date = date;
        this.stadium = stadium;
        this.channels = channels;
        this.finished = finished;
    }

    @Exclude
    public String group;
    @Exclude
    public String date_local;
    @Exclude
    public boolean header;
    @Exclude
    public long timespan;

    public Match(String local_date) {
        date_local = local_date;
        this.header = true;
    }
}