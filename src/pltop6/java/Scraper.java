package pltop6.java;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;




import com.microsoft.playwright.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Scraper {

//    private static String[] teams = {"arsenal-fc", "tottenham-hotspur", "chelsea-fc", "liverpool-fc", "manchester_city", "manchester-united", "newcastle-united", "aston-villa", "brighton-amp-hove-albion"};

    public static ArrayList<String[]> scrape(int year, HashMap map) {
        Collection teams = map.keySet();
        ArrayList<String[]> games = new ArrayList<>();
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            String url = "https://www.transfermarkt.com/premier-league/kreuztabelle/wettbewerb/GB1/saison_id/"+year;
            page.navigate(url);

            // Wait until the element appears in DOM
            page.waitForSelector(".ergebnis-link");

            Locator results = page.getByTitle(Pattern.compile(".* Matchday, .*"));
            for(Locator entry: results.all()){
                String game_review = entry.getAttribute("href");
//                System.out.println(game_review);
                String home = game_review.split("/")[1].split("_")[0];
                String away = game_review.split("/")[1].split("_")[1];
                if(teams.contains(home) && teams.contains(away)) {
                    String home_goals = null;
                    String away_goals = null;
                    if (entry.getAttribute("id") != null ) {
                        home_goals = entry.innerText().split(":")[0];
                        away_goals = entry.innerText().split(":")[1];
                    }
                    String[] game = {entry.getAttribute("title").split("\\.")[0], map.get(home).toString(), map.get(away).toString(), home_goals, away_goals};
                    games.add(game);
//                    System.out.println(entry.getAttribute("title").split("\\.")[0] + ": " + map.get(home) + " : " + map.get(away) + " (" +
//                            entry.innerText() + ")");

                }
            }
        }
        catch (Exception e) {}
        return games;
    }
}









class ScraperJsoup {
    public static void scrape(int year) {
        try {
            String url = "https://www.flashscore.co.uk/football/england/premier-league-"+(2023)+"-"+(year+1)+"/results/";
            Document doc = Jsoup.connect(url).get();
            System.out.println(doc.outerHtml());
            Elements rounds = doc.select(".event__round.event__round--static");
            System.out.println("event__round event__round--static: " + rounds.size());
            rounds = doc.select(".event__round");
            System.out.println(".event__round: " + rounds.size());





        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}