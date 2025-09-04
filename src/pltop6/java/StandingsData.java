package pltop6.java;

public class StandingsData implements Comparable<StandingsData> {
    private int pos, games, points, gF, gA, gD, w, d, l;
    private double ppg;
    private String team;

    public StandingsData(String team) {
        super();
        this.team = team;
    }

    public void addScore(int gF, int gA) {
        games++;
        if (gF > gA) {
            points += 3;
            w++;
        } else if (gF == gA) {
            points++;
            d++;
        } else {
            l++;
        }
        this.gF += gF;
        this.gA += gA;
        gD = this.gF - this.gA;
        ppg = Math.round(1000.0*(points)/games)/1000.0;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }

    public String getTeam() {
        return team;
    }

    public int getGames() {
        return games;
    }

    public double getPpg(){return ppg;}

    public int getPoints() {
        return points;
    }

    public int getGF() {
        return gF;
    }

    public int getGA() {
        return gA;
    }

    public int getGD() {
        return gD;
    }

    public int getW() {
        return w;
    }

    public int getD() {
        return d;
    }

    public int getL() {
        return l;
    }

    @Override
    public int compareTo(StandingsData other) {
        final int BIGGER = -1;
        final int SMALLER = 1;
        final int EQUAL = 0;
        if (ppg > other.getPpg()) return BIGGER;
        if (ppg < other.getPpg()) return SMALLER;
        if (points > other.getPoints()) return BIGGER;
        if (points < other.getPoints()) return SMALLER;
        if (gD > other.getGD()) return BIGGER;
        if (gD < other.getGD()) return SMALLER;
        if (gF > other.getGF()) return BIGGER;
        if (gF < other.getGF()) return SMALLER;
        return team.compareTo(other.getTeam());
    }
}
