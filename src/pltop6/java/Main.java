package pltop6.java;
import javafx.application.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
//        HashMap<String, String> map = new HashMap<>();
    //        String[][] array = {{"ARS", "CHE", "LIV", "MCI", "MUN", "TOT", "AVL", "NEW", "BHA", "BRE"}, {"arsenal-fc", "chelsea-fc", "liverpool-fc", "manchester-city", "manchester-united", "tottenham-hotspur", "aston-villa", "newcastle-united", "brighton-amp-hove-albion", "brentford-fc"}};
//        for(int i=0; i< array[0].length; i++)
//            map.put(array[1][i], array[0][i]);
//        ArrayList<String[]> games = Scraper.scrape(2025, map);
//        System.out.println(games.stream().map(game ->
//                "(" + 2025 + ", " + game[1] + ", " + game[3] + ", " + game[2] + ", " + game[4] + ", " + game[0] + ")").collect(Collectors.joining(", ")));
//        for(String[] game: games){
//
//            if(game[3] == null)
//                System.out.println("Round " + game[0] + ": " + game[1] + " : " + game[2]);
//            else
//                System.out.println("Round " + game[0] + ": " + game[1] + " : " + game[2] + " - " + game[3] + ":" + game[4]);
//
//        }
        Application.launch(GUI.class, args);
    }
}
