package pltop6.java;
public enum ColumnNamesTeams {
    ID ("ID"),
    NAME("Name");

    public final String sql;
    public final String columnName;

    private ColumnNamesTeams(String label){
        sql = "`" + label + "`";
        columnName = label;
    }
}
