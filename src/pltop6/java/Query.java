package pltop6.java;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Query {
    private DBSingleton dbh;

    public Query() throws SQLException {
        dbh = DBSingleton.instance();
    }
    public ResultSet executeQuery(Select select, From from, Where where, String orderby) throws SQLException {
        return dbh.executeQuery("SELECT " + select + " FROM " + from + " WHERE " + where + " ORDER BY " + orderby);
    }
    public ResultSet executeQuery(Select select, From from, Where where) throws SQLException {
        return dbh.executeQuery("SELECT " + select + " FROM " + from + " WHERE " + where);
    }
    public ResultSet executeQuery(Select select, From from) throws SQLException {
        return dbh.executeQuery("SELECT " + select + " FROM " + from);
    }

    public List<String> getTeamNames() throws SQLException {
        Select select = new Select(ColumnNamesTeams.NAME.sql);
        From from = new From(TableNames.TEAMS.sql);
        ResultSet rs = executeQuery(select, from);
        List<String> names = new ArrayList<>();
        while(rs.next()){
            names.add(rs.getString(ColumnNamesTeams.NAME.columnName));
        }
        return names;
    }

    public List<Integer> getSeasons() throws SQLException {
        Select select = new Select("DISTINCT " + ColumnNamesScores.SEASONSTART.sql);
        From from = new From(TableNames.SCORES.sql);
        Where where = new Where();

        ResultSet rs = executeQuery(select, from, where, "`season (start)` DESC");
        List<Integer> years = new ArrayList<>();
        while(rs.next()){
            years.add(rs.getInt(ColumnNamesScores.SEASONSTART.columnName));
        }
        return years;
    }
    public ResultSet getMatchdays(int year) throws SQLException{
        Select select = new Select();
        From from = new From(TableNames.SCORES.sql);
        Where where = new Where(ColumnNamesScores.SEASONSTART.sql, year);
        return executeQuery(select, from, where);
    }
    public ResultSet getMatchdays() throws SQLException{
        Select select = new Select();
        From from = new From(TableNames.SCORES.sql);
        return executeQuery(select, from);
    }

    public String getTeamName(String abbreviation) throws SQLException{
        Select select = new Select(ColumnNamesTeams.NAME.sql);
        From from = new From(TableNames.TEAMS.sql);
        Where where = new Where();
        where.addEquals(ColumnNamesTeams.ID.sql, abbreviation);
        ResultSet rs = executeQuery(select, from, where);
        rs.next();
        return rs.getString(ColumnNamesTeams.ID.columnName);
    }
}
