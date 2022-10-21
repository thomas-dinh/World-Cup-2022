package com.worldcuptracking.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class KnockOut {

    public String name;
    public List<KOMatch> matches = null;

    /**
     * No args constructor for use in serialization
     */
    public KnockOut() {
    }

    /**
     * @param matches
     * @param name
     */
    public KnockOut(String name, List<KOMatch> matches) {
        super();
        this.name = name;
        this.matches = matches;

    }

    @Exclude
    public void initializeWinners() {
        winners = new HashMap<>();
        for (KOMatch match : matches) {
            if (match.header)
                continue;
            if (match.finished) {
                winners.put(match.name, match.winner);
            } else {
                winners.put(match.name, 0);
            }
        }
    }

    @Exclude
    Map<Integer, Integer> winners;

    @Exclude
    public Map<Integer, Integer> getWinners() {
        return winners;
    }

    @Exclude
    public int getLoser(int id) {
        int winner = getWinners().get(id);
        for (KOMatch match : matches) {
            if (match.header)
                continue;
            if (match.name == id) {
                if (winner == Integer.valueOf(match.away_team.toString()))
                    return Integer.valueOf(match.home_team.toString());
                else if (winner == Integer.valueOf(match.home_team.toString()))
                    return Integer.valueOf(match.away_team.toString());
            }
        }
        return 0;
    }
}

