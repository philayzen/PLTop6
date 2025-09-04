package pltop6.java;
public enum ColumnNamesScores {
    SEASONSTART ("SEASON (START)"),
    HOME_TEAM("HOME TEAM"),
    HOME_GOALS("HOME GOALS"),
    AWAY_TEAM("AWAY TEAM"),
    AWAY_GOALS("AWAY GOALS"),
    MATCHDAY("MATCHDAY");

    public final String sql;
    public final String columnName;

    private ColumnNamesScores(String label){
        sql = "`" + label + "`";
        columnName = label;
    }
}
