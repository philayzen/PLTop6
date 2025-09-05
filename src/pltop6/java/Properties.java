package pltop6.java;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Path;

public class Properties {
    public static void initiate() {
        File file = Path.of(System.getenv("LOCALAPPDATA"), "PLTop6", "config.properties").toFile();
        File srcFile = Path.of("config.example.properties").toFile();
        if(!file.isFile()) {
            try {
                FileUtils.copyFile(srcFile, file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
    public static String getValue(String key){

        File file = Path.of(System.getenv("LOCALAPPDATA"), "PLTop6", "config.properties").toFile();
        String toReturn = null;
        if(!file.isFile())
            initiate();
        try {
            BufferedReader fr = new BufferedReader(new FileReader(file));
            String line = fr.readLine();
            while(line!=null){
                if(line.split("=", 2)[0].strip().contentEquals(key.strip())) {
                    toReturn = line.split("=", 2)[1].strip();
                    break;
                }
                line = fr.readLine();
            }
            fr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return toReturn;
    }
}
