package pltop6.java;
public enum TableNames {
    SCORES("`SCORES`"),
    TEAMS("`TEAMS`");

    public final String sql;

    private TableNames(String label){
        sql = label;
    }
}
