package com.worldcuptracking;

import android.app.Application;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.worldcuptracking.model.Match;
import com.worldcuptracking.listener.DatabaseListener;
import com.worldcuptracking.listener.LiveDataListener;
import com.worldcuptracking.model.Feeds;
import com.worldcuptracking.model.Group;
import com.worldcuptracking.model.KOMatch;
import com.worldcuptracking.model.KnockOut;
import com.worldcuptracking.model.Stadium;
import com.worldcuptracking.model.Team;
import com.worldcuptracking.utils.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class WorldCupApp extends Application {

    public static final String TAG = "WorldCupApp";

    public static List<Group> groups = new ArrayList<>();
    public static List<Team> teams = new ArrayList<>();
    public static List<Match> matches = new ArrayList<>();
    public static List<Stadium> stadiums = new ArrayList<>();
    public static List<KnockOut> knockouts = new ArrayList<>();
    public static List<LiveDataListener> listeners = new ArrayList<>();
    public static List<Feeds> feeds = new ArrayList<>();
    public static DatabaseListener databaseListener;

    private DatabaseReference mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        //enable firebase offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //fetch data from firebase
        reloadDatabase();
    }

    @SuppressWarnings("unchecked")
    public void reloadDatabase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //reload teams
        mDatabase.child("teams").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    WorldCupApp.teams.clear();
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Team team = issue.getValue(Team.class);
                        WorldCupApp.teams.add(team);
                    }
                    databaseListener.onDataLoaded();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //reload groups
        mDatabase.child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    WorldCupApp.groups.clear();
                    WorldCupApp.matches.clear();

                    int id = 0;
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Group group = issue.getValue(Group.class);
                        group.name = issue.getKey().toUpperCase();
                        group.id = id++;
                        WorldCupApp.groups.add(group);
                        for (Match m : group.matches) {
                            m.group = group.name;
                            try {
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Date date = formatter.parse(m.date);
                                m.date_local = new SimpleDateFormat("EEE, dd MMM yyyy").format(date);
                                m.timespan = Tools.getTimeSpan(Calendar.getInstance().getTime(), date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            WorldCupApp.matches.add(m);
                        }
                    }

                    //grouping matches based on date
                    Map<String, List<Match>> map = new HashMap<>();
                    for (int i = 0; i < WorldCupApp.matches.size(); i++) {
                        Match match = WorldCupApp.matches.get(i);
                        String key = match.date_local;
                        if (map.containsKey(key)) {
                            List<Match> list = map.get(key);
                            list.add(match);
                        } else {
                            List<Match> list = new ArrayList<>();
                            list.add(new Match(key));
                            list.add(match);
                            map.put(key, list);
                        }
                    }

                    List<Match> newList = new ArrayList<>();
                    Iterator it = map.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        newList.addAll((Collection<? extends Match>) pair.getValue());
                        it.remove(); // avoids a ConcurrentModificationException
                    }

                    WorldCupApp.matches.clear();
                    WorldCupApp.matches.addAll(newList);

                    //sorting the matches
                    Collections.sort(matches, new Comparator<Match>() {

                        @Override
                        public int compare(Match lhs, Match rhs) {
                            SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy");
                            SimpleDateFormat formatterTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                            Date date1 = null;
                            Date date2 = null;
                            try {
                                if (lhs.header)
                                    date1 = formatter.parse(lhs.date_local);
                                else
                                    date1 = formatterTime.parse(lhs.date);
                                if (rhs.header)
                                    date2 = formatter.parse(rhs.date_local);
                                else
                                    date2 = formatterTime.parse(rhs.date);
                                if (date2.after(date1))
                                    return -1;
                                else if (date2.before(date1))
                                    return 1;
                                else
                                    return 0;
                            } catch (ParseException e) {
                                e.printStackTrace();
                                return 0;
                            }
                        }

                    });
                    //call update listeners
                    databaseListener.onDataLoaded();

                    for (LiveDataListener listener : WorldCupApp.listeners) {
                        listener.updateUI();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("knockout").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    WorldCupApp.knockouts.clear();
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        KnockOut knockout = issue.getValue(KnockOut.class);
                        for (KOMatch m : knockout.matches) {
                            m.type = knockout.name;
                            try {
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Date date = formatter.parse(m.date);
                                m.date_local = new SimpleDateFormat("EEE, dd MMM yyyy").format(date);
                                m.timespan = Tools.getTimeSpan(Calendar.getInstance().getTime(), date);
                                //Log.d(TAG, "timespan " + Tools.getTimeSpanToString(m.timespan));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        Map<String, List<KOMatch>> map = new HashMap<>();
                        for (int i = 0; i < knockout.matches.size(); i++) {
                            KOMatch match = knockout.matches.get(i);
                            String key = match.date_local;
                            if (map.containsKey(key)) {
                                List<KOMatch> list = map.get(key);
                                list.add(match);
                            } else {
                                List<KOMatch> list = new ArrayList<>();
                                list.add(new KOMatch(key));
                                list.add(match);
                                map.put(key, list);
                            }
                        }

                        List<KOMatch> newList = new ArrayList<>();
                        Iterator it = map.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            newList.addAll((Collection<? extends KOMatch>) pair.getValue());
                            it.remove(); // avoids a ConcurrentModificationException
                        }

                        knockout.matches.clear();
                        knockout.matches.addAll(newList);

                        //sorting the matches
                        Collections.sort(knockout.matches, new Comparator<KOMatch>() {

                            @Override
                            public int compare(KOMatch lhs, KOMatch rhs) {
                                SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy");
                                SimpleDateFormat formatterTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                                Date date1 = null;
                                Date date2 = null;
                                try {
                                    if (lhs.header)
                                        date1 = formatter.parse(lhs.date_local);
                                    else
                                        date1 = formatterTime.parse(lhs.date);
                                    if (rhs.header)
                                        date2 = formatter.parse(rhs.date_local);
                                    else
                                        date2 = formatterTime.parse(rhs.date);
                                    if (date2.after(date1))
                                        return -1;
                                    else if (date2.before(date1))
                                        return 1;
                                    else
                                        return 0;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    return 0;
                                }
                            }

                        });
                        knockout.initializeWinners();
                        WorldCupApp.knockouts.add(knockout);
                    }

                    //call update listeners
                    databaseListener.onDataLoaded();

                    for (LiveDataListener listener : WorldCupApp.listeners) {
                        listener.updateUI();
                    }

                    updateKnockOutMatches();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("stadiums").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    WorldCupApp.stadiums.clear();
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Stadium stadium = issue.getValue(Stadium.class);
                        WorldCupApp.stadiums.add(stadium);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //reload news
        mDatabase.child("news").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    WorldCupApp.feeds.clear();
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Feeds feed = issue.getValue(Feeds.class);
                        WorldCupApp.feeds.add(feed);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void updateKnockOutMatches() {
        for (KnockOut knockout : WorldCupApp.knockouts) {
            if (knockout.name.equals("Round of 16")) {

                Object winner_a = WorldCupApp.groups.get(0).getWinner();
                Object runner_a = WorldCupApp.groups.get(0).getSecondPlace();
                Object winner_b = WorldCupApp.groups.get(1).getWinner();
                Object runner_b = WorldCupApp.groups.get(1).getSecondPlace();
                Object winner_c = WorldCupApp.groups.get(2).getWinner();
                Object runner_c = WorldCupApp.groups.get(2).getSecondPlace();
                Object winner_d = WorldCupApp.groups.get(3).getWinner();
                Object runner_d = WorldCupApp.groups.get(3).getSecondPlace();
                Object winner_e = WorldCupApp.groups.get(4).getWinner();
                Object runner_e = WorldCupApp.groups.get(4).getSecondPlace();
                Object winner_f = WorldCupApp.groups.get(5).getWinner();
                Object runner_f = WorldCupApp.groups.get(5).getSecondPlace();
                Object winner_g = WorldCupApp.groups.get(6).getWinner();
                Object runner_g = WorldCupApp.groups.get(6).getSecondPlace();
                Object winner_h = WorldCupApp.groups.get(7).getWinner();
                Object runner_h = WorldCupApp.groups.get(7).getSecondPlace();

                for (int i = 0; i < knockout.matches.size(); i++) {
                    KOMatch KO = knockout.matches.get(i);
                    if (KO.header)
                        continue;
                    if (KO.name == 49) {
                        KO.home_team = winner_a;
                        KO.away_team = runner_b;
                    } else if (KO.name == 50) {
                        KO.home_team = winner_c;
                        KO.away_team = runner_d;
                    } else if (KO.name == 51) {
                        KO.home_team = winner_b;
                        KO.away_team = runner_a;
                    } else if (KO.name == 52) {
                        KO.home_team = winner_d;
                        KO.away_team = runner_c;
                    } else if (KO.name == 53) {
                        KO.home_team = winner_e;
                        KO.away_team = runner_f;
                    } else if (KO.name == 54) {
                        KO.home_team = winner_g;
                        KO.away_team = runner_h;
                    } else if (KO.name == 55) {
                        KO.home_team = winner_f;
                        KO.away_team = runner_e;
                    } else if (KO.name == 56) {
                        KO.home_team = winner_h;
                        KO.away_team = runner_g;
                    }

                    if (KO.finished && KO.home_team.getClass().equals(Integer.class) && KO.away_team.getClass().equals(Integer.class)) {
                        if (KO.home_result > KO.away_result)
                            KO.winner = Integer.valueOf(KO.home_team.toString());
                        else if (KO.home_result < KO.away_result)
                            KO.winner = Integer.valueOf(KO.away_team.toString());
                        else {
                            if (KO.home_penalty > KO.away_penalty)
                                KO.winner = Integer.valueOf(KO.home_team.toString());
                            else if (KO.home_penalty < KO.away_penalty)
                                KO.winner = Integer.valueOf(KO.away_team.toString());
                        }
                    }
                }
            } else if (knockout.name.equals("Quarter-finals")) {
                for (int i = 0; i < knockout.matches.size(); i++) {
                    KOMatch KO = knockout.matches.get(i);
                    if (KO.header)
                        continue;
                    if (KO.name == 57) {
                        KO.home_team = 49;
                        KO.away_team = 50;
                        int w1 = WorldCupApp.knockouts.get(0).getWinners().get(49);
                        int w2 = WorldCupApp.knockouts.get(0).getWinners().get(50);
                        if (w1 != 0)
                            KO.home_team = w1;
                        if (w2 != 0)
                            KO.away_team = w2;

                    } else if (KO.name == 58) {
                        KO.home_team = 53;
                        KO.away_team = 54;
                        int w1 = WorldCupApp.knockouts.get(0).getWinners().get(53);
                        int w2 = WorldCupApp.knockouts.get(0).getWinners().get(54);
                        if (w1 != 0)
                            KO.home_team = w1;
                        if (w2 != 0)
                            KO.away_team = w2;
                    } else if (KO.name == 59) {
                        KO.home_team = 51;
                        KO.away_team = 52;
                        int w1 = WorldCupApp.knockouts.get(0).getWinners().get(51);
                        int w2 = WorldCupApp.knockouts.get(0).getWinners().get(52);
                        if (w1 != 0)
                            KO.home_team = w1;
                        if (w2 != 0)
                            KO.away_team = w2;
                    } else if (KO.name == 60) {
                        KO.home_team = 55;
                        KO.away_team = 56;
                        int w1 = WorldCupApp.knockouts.get(0).getWinners().get(55);
                        int w2 = WorldCupApp.knockouts.get(0).getWinners().get(56);
                        if (w1 != 0)
                            KO.home_team = w1;
                        if (w2 != 0)
                            KO.away_team = w2;
                    }
                }
            } else if (knockout.name.equals("Semi-finals")) {
                for (int i = 0; i < knockout.matches.size(); i++) {
                    KOMatch KO = knockout.matches.get(i);
                    if (KO.header)
                        continue;
                    if (KO.name == 61) {
                        KO.home_team = 57;
                        KO.away_team = 58;
                        int w1 = WorldCupApp.knockouts.get(4).getWinners().get(57);
                        int w2 = WorldCupApp.knockouts.get(4).getWinners().get(58);
                        if (w1 != 0)
                            KO.home_team = w1;
                        if (w2 != 0)
                            KO.away_team = w2;
                    } else if (KO.name == 62) {
                        KO.home_team = 59;
                        KO.away_team = 60;
                        int w1 = WorldCupApp.knockouts.get(4).getWinners().get(59);
                        int w2 = WorldCupApp.knockouts.get(4).getWinners().get(60);
                        if (w1 != 0)
                            KO.home_team = w1;
                        if (w2 != 0)
                            KO.away_team = w2;
                    }
                }
            } else if (knockout.name.equals("Third place play-off")) {
                for (int i = 0; i < knockout.matches.size(); i++) {
                    KOMatch KO = knockout.matches.get(i);
                    if (KO.header)
                        continue;
                    if (KO.name == 63) {
                        KO.home_team = 61;
                        KO.away_team = 62;
                        int l1 = WorldCupApp.knockouts.get(3).getLoser(61);
                        int l2 = WorldCupApp.knockouts.get(3).getLoser(62);
                        if (l1 != 0)
                            KO.home_team = l1;
                        if (l2 != 0)
                            KO.away_team = l2;
                    }
                }
            } else if (knockout.name.equals("Final")) {
                for (int i = 0; i < knockout.matches.size(); i++) {
                    KOMatch KO = knockout.matches.get(i);
                    if (KO.header)
                        continue;
                    if (KO.name == 64) {
                        KO.home_team = 61;
                        KO.away_team = 62;
                        int w1 = WorldCupApp.knockouts.get(3).getWinners().get(61);
                        int w2 = WorldCupApp.knockouts.get(3).getWinners().get(62);
                        if (w1 != 0)
                            KO.home_team = w1;
                        if (w2 != 0)
                            KO.away_team = w2;
                    }
                }

            }
        }
    }

    public void writeNewData(Feeds feed1, Feeds feed2,Feeds feed3,Feeds feed4) {
        mDatabase.child("news").child("0").setValue(feed1);
        mDatabase.child("news").child("1").setValue(feed2);
        mDatabase.child("news").child("2").setValue(feed3);
        mDatabase.child("news").child("3").setValue(feed4);

    }
}
