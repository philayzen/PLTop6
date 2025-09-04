package pltop6.java;
public class DBLogin {
    private static final String url = "jdbc:mysql://localhost/premier_league?serverTimezone=Europe/Berlin";
    private static final String user = "root";
    private static final String password = "bfwhd";

    public static String url(){
        return url;
    }
    public static String user(){
        return user;
    }
    public static String password(){
        return password;
    }
}
