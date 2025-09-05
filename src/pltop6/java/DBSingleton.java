package pltop6.java;
import java.io.IOException;
import java.sql.*;

public class DBSingleton {
    private static DBSingleton instance;
    //private static final String url = "jdbc:mysql://localhost/camouflage"+
    //		"?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useJDBCCompliantTimezoneShift=true";
    private static Connection connection;
    private static Statement statement;

    DBSingleton() {
    }

    public static DBSingleton instance() throws SQLException {
        if (instance == null) {
            try{
                initConnection(true);
            }
            catch(SQLSyntaxErrorException e){
                createDatabase();
            }
            instance = new DBSingleton();
        }
        return instance;
    }

    private static void initConnection(boolean withDB) throws SQLException {
        String url, user, password;
        try {
            url = DBLogin.url(withDB);
            user = DBLogin.user();
            password = DBLogin.password();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        connection = DriverManager.getConnection(url, user, password);
        statement = connection.createStatement();
    }
    private static void createDatabase() throws SQLException {
        initConnection(false);
        try {
            statement.executeUpdate("Create Database `" + DBLogin.database() + "`");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initConnection(true);
        String scores_table = "CREATE TABLE `scores` (`SEASON (START)` int(11) NOT NULL,  `HOME TEAM` char(3) NOT NULL, " +
                " `AWAY TEAM` char(3) NOT NULL,`HOME GOALS` int(11) DEFAULT NULL,  `AWAY GOALS` int(11) DEFAULT NULL,  " +
                "`MATCHDAY` int(11) DEFAULT NULL,        PRIMARY KEY (`SEASON (START)`,`HOME TEAM`,`AWAY TEAM`))" +
                " ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";
        statement.executeUpdate(scores_table);
        String teams_table = "CREATE TABLE `teams` (`ID` char(3) NOT NULL,  `Name` varchar(60) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";
        String teams_values = "INSERT INTO `teams` (`ID`, `Name`) VALUES('TOT', 'Tottenham'),('ARS', 'Arsenal')," +
                        "('CHE', 'Chelsea'),('MUN', 'Manchester United'),('MCI', 'Manchester City'),('LIV', 'Liverpool'), " +
                        "('NEW', 'Newcastle United'),('BHA', 'Brighton Hove & Albion'),('AVL', 'Aston Villa'),('BRE', 'Brentford');";
        statement.executeUpdate(teams_table);
        statement.executeUpdate(teams_values);
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        ResultSet rs = statement.executeQuery(sql);
        return rs;
    }

    public int executeUpdate(String sql) throws SQLException {
        return statement.executeUpdate(sql);
    }
}