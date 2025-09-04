package pltop6.java;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
            initConnection();
            instance = new DBSingleton();
        }
        return instance;
    }

    private static void initConnection() throws SQLException {
        String url = DBLogin.url();
        String user = DBLogin.user();
        String password = DBLogin.password();
        connection = DriverManager.getConnection(url, user, password);
        statement = connection.createStatement();
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        ResultSet rs = statement.executeQuery(sql);
        return rs;
    }

    public int executeUpdate(String sql) throws SQLException {
        return statement.executeUpdate(sql);
    }
}