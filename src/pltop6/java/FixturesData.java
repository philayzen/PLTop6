package pltop6.java;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FixturesData implements Comparable<FixturesData> {
    private String season;
    private String home, away, score;
    private Integer matchday;

    public FixturesData(String season, String homeTeam, String awayTeam, Integer homeGoals, Integer awayGoals, Integer matchday) {
        this.season = season;
        home = homeTeam;
        away = awayTeam;
        this.matchday = matchday;
        if (homeGoals == null) {
            score = "-";
        } else {
            score = homeGoals + ":" + awayGoals;
        }
    }

    public String getSeason() {
        return season;
    }

    public String getHome() {
        return home;
    }

    public String getAway() {
        return away;
    }

    public String getScore() {
        return score;
    }

    public String getMatchday() {
        if (matchday == null) return "";
        else {
            return matchday + "";
        }
    }

    public Integer getMatchdayAsInt() {
        return matchday;
    }

    @Override
    public int compareTo(FixturesData cmp) {
        if (matchday == null && cmp.getMatchdayAsInt() != null) return 1;
        if (matchday != null && cmp.getMatchdayAsInt() == null) return -1;
        if (matchday == null && cmp.getMatchdayAsInt() == null) {
            if (home.contentEquals(cmp.getHome())) return away.compareTo(cmp.getAway());
            else return home.compareTo(cmp.getHome());
        }
        return matchday.compareTo(cmp.getMatchdayAsInt());
    }
}
