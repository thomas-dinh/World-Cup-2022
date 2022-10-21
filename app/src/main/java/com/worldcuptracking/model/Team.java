package com.worldcuptracking.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.worldcuptracking.WorldCupApp;

import java.io.Serializable;

@IgnoreExtraProperties
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class Team implements Serializable {
    public Integer id;
    public String name;
    public String iso2;
    public String nickname;
    public Integer ranking;
    public Integer apps;
    public Integer titles;

    /**
     * No args constructor for use in serialization
     */
    public Team() {
    }


    /**
     * @param id
     * @param nickname
     * @param apps
     * @param name
     * @param titles
     * @param iso2
     * @param ranking
     */
    public Team(Integer id, String name, String iso2, String nickname, Integer ranking, Integer apps, Integer titles) {
        super();
        this.id = id;
        this.name = name;
        this.iso2 = iso2;
        this.nickname = nickname;
        this.ranking = ranking;
        this.apps = apps;
        this.titles = titles;
    }

    @Exclude
    public int groupId;

    @Exclude
    public int getPoints() {
        int win = 0;
        int lose = 0;
        int draw = 0;
        for (Match m : WorldCupApp.groups.get(groupId).matches) {
            if (m.home_team - id == 0) {
                if (m.home_result >= 0 && m.away_result >= 0)
                    if (m.home_result > m.away_result)
                        win += 1;
                    else if (m.home_result < m.away_result)
                        lose += 1;
                    else
                        draw += 1;
            } else if (m.away_team - id == 0) {
                if (m.home_result >= 0 && m.away_result >= 0)
                    if (m.home_result < m.away_result)
                        win += 1;
                    else if (m.home_result > m.away_result)
                        lose += 1;
                    else
                        draw += 1;
            }
        }
        return 3 * win + draw;
    }

    @Exclude
    public int getMatchPlayed() {
        int match_played = 0;
        for (Match m : WorldCupApp.groups.get(groupId).matches) {
            if (m.home_team - id == 0 || m.away_team - id == 0) {
                if (m.home_result >= 0 && m.away_result >= 0)
                    match_played += 1;
            }
        }
        return match_played;
    }

    @Exclude
    public int getGoalScored() {
        int goal_scored = 0;
        for (Match m : WorldCupApp.groups.get(groupId).matches) {
            if (m.home_team - id == 0) {
                if (m.home_result >= 0 && m.away_result >= 0)
                    goal_scored += m.home_result;
            } else if (m.away_team - id == 0) {
                if (m.home_result >= 0 && m.away_result >= 0)
                    goal_scored += m.away_result;
            }
        }
        return goal_scored;
    }

    @Exclude
    public int getGoalAgainst() {
        int goal_against = 0;
        for (Match m : WorldCupApp.groups.get(groupId).matches) {
            if (m.home_team - id == 0) {
                if (m.home_result >= 0 && m.away_result >= 0)
                    goal_against += m.away_result;
            } else if (m.away_team - id == 0) {
                if (m.home_result >= 0 && m.away_result >= 0)
                    goal_against += m.home_result;
            }
        }
        return goal_against;
    }


}