package com.worldcuptracking.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.worldcuptracking.WorldCupApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@IgnoreExtraProperties
public class Group {
    public Integer winner;
    public Integer runnerup;
    public List<Match> matches = null;

    /**
     * No args constructor for use in serialization
     */
    public Group() {
    }

    /**
     * @param matches
     * @param runnerup
     * @param winner
     */
    public Group(Integer winner, Integer runnerup, List<Match> matches) {
        super();
        this.winner = winner;
        this.runnerup = runnerup;
        this.matches = matches;
    }

    @Exclude
    public String name;
    @Exclude
    public int id;

    @Exclude
    public List<Team> getTeams() {
        List<Team> teams = new ArrayList<>();
        int a = matches.get(0).home_team;
        int b = matches.get(0).away_team;
        int c = matches.get(1).home_team;
        int d = matches.get(1).away_team;

        Team t1 = WorldCupApp.teams.get(a - 1);
        t1.groupId = id;
        Team t2 = WorldCupApp.teams.get(b - 1);
        t2.groupId = id;
        Team t3 = WorldCupApp.teams.get(c - 1);
        t3.groupId = id;
        Team t4 = WorldCupApp.teams.get(d - 1);
        t4.groupId = id;

        teams.add(t1);
        teams.add(t2);
        teams.add(t3);
        teams.add(t4);

        Collections.sort(teams, new Comparator<Team>() {
            @Override
            public int compare(Team lhs, Team rhs) {
                if (rhs.getPoints() - lhs.getPoints() == 0) {

                    int rhsGoalDiff = rhs.getGoalScored() - rhs.getGoalAgainst();
                    int lhsGoalDiff = lhs.getGoalScored() - lhs.getGoalAgainst();
                    if (rhsGoalDiff - lhsGoalDiff == 0) {
                        if (rhs.getGoalScored() - lhs.getGoalScored() == 0) {
                            return rhs.getGoalAgainst() - lhs.getGoalAgainst();
                        } else
                            return rhs.getGoalScored() - lhs.getGoalScored();
                    } else
                        return rhsGoalDiff - lhsGoalDiff;
                }
                return rhs.getPoints() - lhs.getPoints();
            }
        });

        boolean finished = false;
        for (Match match : matches) {
            if (!match.finished) {
                finished = false;
                break;
            }
            finished = true;
        }

        if (finished) {
            winner = teams.get(0).id;
            runnerup = teams.get(1).id;
        }

        return teams;
    }

    @Exclude
    public Object getWinner() {
        if (winner <= 0)
            return "1" + name;
        return winner;
    }

    @Exclude
    public Object getSecondPlace() {
        if (runnerup <= 0)
            return "2" + name;
        return runnerup;
    }
}
