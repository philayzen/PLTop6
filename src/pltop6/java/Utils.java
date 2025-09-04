package pltop6.java;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

public class Utils {
	public static String convertYearToSeason(int year) {
		String firstYear = ("" + year).substring(2);
		String secondYear = (1 + year + "").substring(2);
		return firstYear + "/" + secondYear;
	}
	public static int convertSeasonToYear(String season) {
		String[] years = season.split("/");
		int firstYear = Integer.parseInt(years[0]);
		if(firstYear>=92)
			return 1900 + firstYear;
		else
			return 2000 + firstYear;
	}

	public static void createConfigFile(String[] teams){
		String fileContent = "";
		for(String team: teams){
			fileContent += team + "="+ true + "\n";
		}
		File f = getConfigFilePath().toFile();

		try {
			f.getParentFile().mkdirs();
			f.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try(FileWriter fw = new FileWriter(f)){
			fw.write(fileContent);
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	public static void setConfig(String key, String value){
		String newContent = "";
		boolean configExists = false;
		File f = getConfigFilePath().toFile();
		try(BufferedReader fr = new BufferedReader(new FileReader(f))){
			String line;
			while((line = fr.readLine()) != null){
				String k = line.split("=")[0].trim();
				String v = line.split("=")[1].trim();
				if(key.contentEquals(k)){
					newContent += key + "=" + value + "\n";
					configExists = true;
				}
				else newContent += line + "\n";
			}
		} catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

		if(!configExists)
			newContent += key + "=" + value + "\n";

		try(FileWriter fw = new FileWriter(f)){
			fw.write(newContent);
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	public static String getConfig(String key){
		String returnValue = null;
		File f = getConfigFilePath().toFile();
		try(BufferedReader fr = new BufferedReader(new FileReader(f))){
			String line;
			while((line = fr.readLine()) != null){
				String k = line.split("=")[0].trim();
				String v = line.split("=")[1].trim();
				if(key.contentEquals(k)){
					returnValue = v;
					break;
				}
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if(returnValue == null){
				throw new IllegalArgumentException("Key not found. Provided key argument: " + key);
		}
		return returnValue;
	}

	public static Path getConfigFilePath(){
		String configLocation = System.getenv("LOCALAPPDATA");
		return Paths.get(configLocation, "PlTop6", "pltop6.cfg");
	}

	public static boolean hasConfigFile(){
		return getConfigFilePath().toFile().exists();
	}
}
