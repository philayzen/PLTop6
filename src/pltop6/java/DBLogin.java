package pltop6.java;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DBLogin {

    public static String url() throws IOException {
        return url(true);
    }
    public static String url(Boolean withDB) throws IOException {
        String url = Properties.getValue("db.url");
        if(!withDB)
            return url;
        else{
             String[] parts = url.split("\\?", 2);
             if(!parts[0].endsWith("/"))
                 parts[0] += "/";
             if(parts.length == 1)
                 return parts[0] + database();
             else
                 return parts[0] + database() + "?" + parts[1];
        }
    }
    public static String database() throws IOException {
        return pltop6.java.Properties.getValue("db.database_name");
//        if(db_name.endsWith("/"))
//            return db_name;
//        else
//            return db_name + "/";
    }
    public static String user() throws IOException {

        return Properties.getValue("db.username");
    }
    public static String password() throws IOException {
        return Properties.getValue("db.password");
    }
}
