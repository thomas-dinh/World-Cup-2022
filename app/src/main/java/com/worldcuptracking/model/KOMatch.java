package com.worldcuptracking.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

@IgnoreExtraProperties
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class KOMatch implements Serializable {

    public Integer name;
    public String type;
    public Object home_team;
    public Object away_team;
    public Integer home_result;
    public Integer away_result;
    public Integer home_penalty;
    public Integer away_penalty;
    public Integer winner;
    public String date;
    public Integer stadium;
    public List<Integer> channels = null;
    public Boolean finished;

    /**
     * No args constructor for use in serialization
     *
     */
    public KOMatch() {
    }

    /**
     *
     * @param channels
     * @param finished
     * @param winner
     * @param type
     * @param date
     * @param home_result
     * @param away_team
     * @param name
     * @param away_penalty
     * @param stadium
     * @param home_team
     * @param away_result
     * @param home_penalty
     */
    public KOMatch(Integer name, String type, Object home_team, Object away_team, Integer home_result, Integer away_result, Integer home_penalty, Integer away_penalty, Integer winner, String date, Integer stadium, List<Integer> channels, Boolean finished) {
        super();
        this.name = name;
        this.type = type;
        this.home_team = home_team;
        this.away_team = away_team;
        this.home_result = home_result;
        this.away_result = away_result;
        this.home_penalty = home_penalty;
        this.away_penalty = away_penalty;
        this.winner = winner;
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

    public KOMatch(String local_date) {
        date_local = local_date;
        this.header = true;
    }

}
