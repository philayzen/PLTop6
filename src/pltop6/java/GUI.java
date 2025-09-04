package pltop6.java;
import java.awt.*;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class GUI extends Application {
    private DBSingleton dbh;
    TableView<StandingsData> standings = new TableView<>();
    private Stage mainStage;
    private Scene scene;
    TableView<FixturesData> fixtures;

    ObservableList<String> seasonsList = FXCollections.observableArrayList();
    ObservableList<String> teamsList = FXCollections.observableArrayList();
    private ObservableList<StandingsData> standingsList = FXCollections.observableArrayList();
    private ObservableList<FixturesData> fixturesList = FXCollections.observableArrayList();
    private ObservableList<FixturesData> fixturesListAll = FXCollections.observableArrayList();
    private String season;
    private String teamName = "All";
    private boolean blendOut = false;
    private boolean allYears = false;

    private HashMap<String, CheckBox> includedTeamsMap = new HashMap<>();
    private Map<String, String> teamNamesMap = new HashMap<>();

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;
//        Image icon = Toolkit.getDefaultToolkit().getImage("C:\\Users\\phila\\Desktop\\Projekt\\PLTop6\\icon.png");
//        javafx.scene.image.Image ic = javafx.scene.image.Image(icon);
        try {
            stage.getIcons().add(new javafx.scene.image.Image(String.valueOf(getClass().getClassLoader().getResource("pltop6/resources/pl-icon_64.png"))));
        }catch(IllegalArgumentException | NullPointerException e){
            System.out.println(GUI.class);
            System.out.println(GUI.class.getResourceAsStream("pl-icon.png"));
            Object a = GUI.class.getClassLoader();
            int i = 3;
        }
        try {
            dbh = DBSingleton.instance();
            VBox box = new VBox();
            teamsList.add("All");
            ResultSet rs = new Query().executeQuery(new Select(), new From(TableNames.TEAMS.sql));
            while (rs.next())
                teamNamesMap.put(rs.getString(ColumnNamesTeams.ID.columnName), rs.getString(ColumnNamesTeams.NAME.columnName));
            loadContent();
            box.getChildren().addAll(buildMenu(), buildContent());
            loadContent();
            Scene scene = new Scene(box, 1280, 697.5);
            this.scene = scene;
            stage.setScene(scene);
            stage.show();
        } catch (SQLException e) {
            Scene scene = new Scene(new TextArea(e.getCause().getMessage() + "\n" + e.getStackTrace().toString()), 1280, 697.5);
            this.scene = scene;
            stage.setScene(scene);
            stage.show();
        }
    }

    public void loadContent() {
        try {
            standingsList.clear();
            fixturesList.clear();
            fixturesListAll.clear();
            Query query = new Query();
            List<String> names = query.getTeamNames();
            for (String name : names) {
                if (!includedTeamsMap.containsKey(name) || includedTeamsMap.get(name).isSelected()) {
                    standingsList.add(new StandingsData(name));
                    if (!teamsList.contains(name))
                        teamsList.add(name);
                }
            }
            List<Integer> years = query.getSeasons();
            for (int year : years) {
                String season = Utils.convertYearToSeason(year);
                if (!seasonsList.contains(season)) {
                    seasonsList.add(season);
                }
            }
            if (seasonsList.isEmpty())
                return;
            seasonsList = seasonsList.sorted(Comparator.reverseOrder());
            if (season == null)
                season = seasonsList.get(0);
//			 season scores.`Season (Start)` = '" + year + "'
            ResultSet rs = allYears ? query.getMatchdays() : query.getMatchdays(Utils.convertSeasonToYear(season));
            while (rs.next()) {
                Integer homeScore, awayScore;
                if (rs.getString("Home Goals") == null) {
                    homeScore = awayScore = null;
                } else {
                    homeScore = rs.getInt("Home Goals");
                    awayScore = rs.getInt("Away Goals");
                }
                Integer matchday = rs.getInt("matchday") == 0 ? null : rs.getInt("matchday");
                String season = Utils.convertYearToSeason(rs.getInt(ColumnNamesScores.SEASONSTART.columnName));
                FixturesData fixture = new FixturesData(season, teamNamesMap.get(rs.getString("Home Team")), teamNamesMap.get(rs.getString("Away Team")), homeScore, awayScore, matchday);
                if (fixture.getSeason().equals(this.season)) {
                    fixturesListAll.add(fixture);
                    if (!blendOut || homeScore != null) {
                        if (teamName.contentEquals("All")) {
                            fixturesList.add(fixture);
                        } else if (teamName.contentEquals(fixture.getHome()) || teamName.contentEquals(fixture.getAway())) {
                            fixturesList.add(fixture);
                        }
                    }
                }
                for (StandingsData s : standingsList) {
                    if (homeScore != null) {
                        String homeTeam = fixture.getHome();
                        String awayTeam = fixture.getAway();
                        if ((!includedTeamsMap.containsKey(homeTeam) || includedTeamsMap.get(homeTeam).isSelected())
                                && (!includedTeamsMap.containsKey(awayTeam) || includedTeamsMap.get(awayTeam).isSelected())) {
                            if (s.getTeam().equals(homeTeam)) {
                                s.addScore(rs.getInt("Home Goals"), rs.getInt("Away Goals"));
                            } else if (s.getTeam().equals(awayTeam)) {
                                s.addScore(rs.getInt("Away Goals"), rs.getInt("Home Goals"));
                            }
                        }
                    }
                }
            }
            FXCollections.sort(fixturesList);
            FXCollections.sort(standingsList);
            for (int i = 0; i < standingsList.size(); i++) {
                standingsList.get(i).setPos(i + 1);
            }

            standings.sort();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void sort(StandingsData[] array, int start, int end) {
        if (start >= end) {
            return;
        } else if (start + 1 == end) {
            if (array[start].compareTo(array[end]) < 0) {
                StandingsData temp = array[start];
                array[start] = array[end];
                array[end] = temp;
            }
            return;
        }
        StandingsData pivot = array[end];
        int l, r;
        l = start;
        r = end - 1;
        while (l < r) {
            while (l < end && array[l].compareTo(pivot) >= 0) {
                l++;
            }
            while (r > start && array[r].compareTo(pivot) <= 0) {
                r--;
            }
            if (l < r) {
                StandingsData temp = array[l];
                array[l] = array[r];
                array[r] = temp;
            }
        }
        StandingsData temp = array[l];
        array[l] = pivot;
        array[end] = temp;
        sort(array, start, l - 1);
        sort(array, l + 1, end);
    }

    private Node buildMenu() {
        HBox buttons = new HBox(50);
        Button newFixture = new Button("Add fixture");
        newFixture.setFocusTraversable(false);
        newFixture.setOnAction(ev -> {
            new NewFixtureBox(this);
        });

        Button updateFixture = new Button("Update fixture");
        updateFixture.setFocusTraversable(false);
        updateFixture.setOnAction(ev -> {
            FixturesData data = fixtures.getSelectionModel().getSelectedItem();
            if (data == null) {
                VBox errorBox = new VBox(40);
                errorBox.setAlignment(Pos.CENTER);
                Label errorLabel = new Label("Select the to be updated fixture first");
                Button errorClose = new Button("Close");
                errorClose.setFocusTraversable(false);
                errorBox.getChildren().addAll(errorLabel, errorClose);
                Stage errorStage = new Stage();
                Scene errorScene = new Scene(errorBox, 220, 120);
                errorStage.setScene(errorScene);
                errorStage.show();
                errorClose.setOnAction(e -> {
                    errorStage.close();
                });
            } else {
                updateAction(data);
            }
        });

        Button createFixtures = new Button("Autocomplete fixtures");
        createFixtures.setFocusTraversable(false);
        createFixtures.setOnAction(ev -> {
            new AutoFixturesBox(this);
        });
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(newFixture, updateFixture, createFixtures);
        buttons.setMinHeight(50);
        return buttons;
    }

    private Node buildContent() {
        SplitPane tables = new SplitPane();
        tables.getItems().addAll(buildFixtures(), buildStandings());
        tables.setDividerPosition(0, 0.35);
        return tables;
    }

    private void updateAction(FixturesData data){
        ResultSet rs;
        String home = "";
        String away = "";
                    try

        {
            String query = "Select * from teams where Name = '" + data.getHome() + "';";
            rs = DBSingleton.instance().executeQuery(query);
            rs.next();
            home = rs.getString("ID");
            rs = DBSingleton.instance().executeQuery("Select * from teams where name = '" + data.getAway() + "';");
            rs.next();
            away = rs.getString("ID");
        } catch(
        SQLException e)

        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
                    new

        UpdateFixtureBox(this,Utils.convertSeasonToYear(season),home,data.

        getHome(),away,data.

        getAway(),data.

        getMatchdayAsInt());
    }
    private void filter() {
        fixturesList.clear();
        for (FixturesData f : fixturesListAll) {
            if (teamName.contentEquals("All") || teamName.contentEquals(f.getHome()) || teamName.contentEquals(f.getAway())) {
                fixturesList.add(f);
            }
        }
        Collections.sort(fixturesList);
    }

    @SuppressWarnings("unchecked")
    private Node buildFixtures() {
        VBox fixtureContent = new VBox(10);
        fixtureContent.setAlignment(Pos.TOP_CENTER);
        HBox filter = new HBox(20);
        ComboBox<String> box = new ComboBox<>(seasonsList.sorted(Comparator.reverseOrder()));
        String defaultItem = seasonsList.isEmpty()?"-":seasonsList.get(0);
        season = defaultItem;
        box.getSelectionModel().select(season);
        season = box.getSelectionModel().getSelectedItem();
        box.setOnAction(ev -> {
            season = box.getSelectionModel().getSelectedItem();
            loadContent();
        });

        ComboBox<String> team = new ComboBox<>(teamsList);
        team.getSelectionModel().select(0);
//		String team  = box.getSelectionModel().getSelectedItem();
        team.setOnAction(ev -> {
            teamName = team.getSelectionModel().getSelectedItem();
            filter();
        });
        VBox blendOut = new VBox(10);
        blendOut.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
        blendOut.setAlignment(Pos.TOP_CENTER);
        blendOut.setPadding(new Insets(2));
        Label blendOutLabel = new Label("Only show played games");
        ToggleSwitch blendOutSwitch = new ToggleSwitch(15);
        blendOutSwitch.setOnMousePressed(ev -> {
            this.blendOut = !this.blendOut;
            loadContent();
        });
        blendOut.getChildren().addAll(blendOutLabel, blendOutSwitch);

        filter.setPadding(new Insets(4));
        filter.setAlignment(Pos.CENTER_LEFT);
        filter.getChildren().addAll(box, team, blendOut);

        fixtures = new TableView<>();
        fixtures.setPrefWidth(398);
        fixtures.setMaxWidth(398);
        TableColumn<FixturesData, String> homeCol, awayCol, scoreCol, matchdayCol;
        homeCol = new TableColumn<>("Home team");
        homeCol.setPrefWidth(130);
        homeCol.setCellValueFactory(new PropertyValueFactory<FixturesData, String>("home"));
        awayCol = new TableColumn<>("Away team");
        awayCol.setCellValueFactory(new PropertyValueFactory<FixturesData, String>("away"));
        awayCol.setPrefWidth(130);
        scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<FixturesData, String>("score"));
        scoreCol.setPrefWidth(56);
        matchdayCol = new TableColumn<>("Matchday");
        matchdayCol.setCellValueFactory(new PropertyValueFactory<FixturesData, String>("matchday"));
        matchdayCol.setPrefWidth(65);
        fixtures.getColumns().addAll(matchdayCol, homeCol, awayCol, scoreCol);
        fixtures.setItems(fixturesList);
        fixtures.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.isPrimaryButtonDown() && e.getClickCount() == 2) {
                    updateAction(fixtures.getSelectionModel().getSelectedItem());
                }
            }
        });

        fixtureContent.getChildren().addAll(filter, fixtures);
        return fixtureContent;
    }

    private CheckBox createOptionalTeam(String teamName){
        CheckBox cb = new CheckBox(teamName);
        String selection = null;
        try{
            selection = Utils.getConfig(teamName);
        }
        catch(IllegalArgumentException e){
            e.printStackTrace();
            System.out.println(e);
            System.out.println("Adding team to config file");

            selection = String.valueOf(true);
            Utils.setConfig(teamName, selection);
        }
        cb.setSelected(Boolean.parseBoolean(selection));
        includedTeamsMap.put(teamName, cb);
        cb.setOnAction(ev->{
            loadContent();
            System.out.println(String.valueOf(cb.isSelected()));
            Utils.setConfig(teamName, String.valueOf(cb.isSelected()));
        });
        return cb;
    }

    private Node buildStandings() {
        VBox standingsContent = new VBox(10);
        standingsContent.setAlignment(Pos.TOP_CENTER);
        standingsContent.setPadding(new Insets(4));

        VBox allOrSpecific = new VBox(10);
        allOrSpecific.setMaxWidth(230);
        allOrSpecific.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
        allOrSpecific.setAlignment(Pos.TOP_CENTER);
        allOrSpecific.setPadding(new Insets(2));
        Label allOrSpecificLabel = new Label("Show standings of results of all seasons");
        ToggleSwitch allOrSpecificSwitch = new ToggleSwitch(15);
        allOrSpecificSwitch.setOnMousePressed(ev -> {
            this.allYears = !this.allYears;
            loadContent();
        });
        allOrSpecific.getChildren().addAll(allOrSpecificLabel, allOrSpecificSwitch);

        VBox includeTeams = new VBox(10);
        includeTeams.setMaxWidth(400);
        includeTeams.setPadding(new Insets(2));
        Label includeTeamsLabel = new Label("Display the teams");
        FlowPane includedTeamsPane = new FlowPane();

        String[] optionalTeams = {"Newcastle United", "Aston Villa", "Brighton Hove & Albion", "Brentford"};

        if(!Utils.hasConfigFile()){
            Utils.createConfigFile(optionalTeams);
        }
        for(String opt: optionalTeams)
            includedTeamsPane.getChildren().add(createOptionalTeam(opt));
        includedTeamsPane.setVgap(5);
        includedTeamsPane.setHgap(5);
        includeTeams.getChildren().addAll(includeTeamsLabel, includedTeamsPane);

        TableColumn<StandingsData, Integer> posCol, gamesCol, pointsCol, gFCol, gACol, gDCol, wCol, dCol, lCol;
        TableColumn<StandingsData, Double> ppgCol;
        TableColumn<StandingsData, String> teamCol;
        posCol = new TableColumn<>("Position");
        posCol.setSortType(TableColumn.SortType.ASCENDING);
        posCol.setCellValueFactory(new PropertyValueFactory<StandingsData, Integer>("pos"));
        teamCol = new TableColumn<>("Team");
        teamCol.setCellValueFactory(new PropertyValueFactory<StandingsData, String>("team"));
        gamesCol = new TableColumn<>("Games");
        gamesCol.setCellValueFactory(new PropertyValueFactory<StandingsData, Integer>("games"));
        pointsCol = new TableColumn<>("Points");
        pointsCol.setCellValueFactory(new PropertyValueFactory<StandingsData, Integer>("points"));
        ppgCol = new TableColumn<>("PPG");
        ppgCol.setCellValueFactory(new PropertyValueFactory<StandingsData, Double>("ppg"));
        gFCol = new TableColumn<>("Goals scored");
        gFCol.setCellValueFactory(new PropertyValueFactory<StandingsData, Integer>("gF"));
        gACol = new TableColumn<>("Goals conceded");
        gACol.setCellValueFactory(new PropertyValueFactory<StandingsData, Integer>("gA"));
        gDCol = new TableColumn<>("Goal difference");
        gDCol.setCellValueFactory(new PropertyValueFactory<StandingsData, Integer>("gD"));
        wCol = new TableColumn<>("Wins");
        wCol.setCellValueFactory(new PropertyValueFactory<StandingsData, Integer>("w"));
        dCol = new TableColumn<>("Draws");
        dCol.setCellValueFactory(new PropertyValueFactory<StandingsData, Integer>("d"));
        lCol = new TableColumn<>("Losses");
        lCol.setCellValueFactory(new PropertyValueFactory<StandingsData, Integer>("l"));
        standings.getColumns().addAll(posCol, teamCol, gamesCol, ppgCol, pointsCol, gFCol, gACol, gDCol, wCol, dCol, lCol);
        standings.setItems(standingsList);
        standings.getSortOrder().add(posCol);
        standings.sort();
        standings.setMaxWidth(820);
        standingsContent.getChildren().addAll(allOrSpecific, includeTeams, standings);
        return standingsContent;
    }

}

class NewFixtureBox {
    GUI gui;
    Stage stage;
    ComboBox<String> teamHSelection, teamASelection, seasonSelection;
    ComboBox<Integer> matchdaySelection, scoreHSelection, scoreASelection;

    String[][] array;
    HashMap<String, String> map;
    public NewFixtureBox(GUI gui) {
        this.gui = gui;
        HashMap<String, String> map = new HashMap<>();
        map.put("Arsenal", "ARS");
        map.put("Chelsea", "CHE");
        map.put("Liverpool", "LIV");
        map.put("Manchester City", "MCI");
        map.put("Manchester United", "MUN");
        map.put("Aston Villa", "AVL");
        map.put("Newcastle", "NEW");
        map.put("Brighton Hove & Albion", "BHA");
        map.put("Brentford", "BRE");
        String[][] array = {{"ARS", "CHE", "LIV", "MCI", "MUN", "TOT"}, {"Arsenal", "Chelsea", "Liverpool", "Manchester City", "Manchester United", "Tottenham Hotspur"}};
        this.array = array;
        this.map = map;
        stage = new Stage();
        VBox box = new VBox(20);
        box.getChildren().addAll(buildInputs(), buildButtons());
        Scene scene = new Scene(box, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private Node buildInputs() {
        VBox box = new VBox(10);

        VBox year = new VBox(3);
        year.setAlignment(Pos.TOP_CENTER);
        ObservableList<String> seasonSelect = FXCollections.observableArrayList();
        int i = LocalDate.now().getYear();
        while (i >= 1992) {
            seasonSelect.add(Utils.convertYearToSeason(i--));
        }
        Label seasonLabel = new Label("Season");
        seasonSelection = new ComboBox<>(seasonSelect);
        seasonSelection.getSelectionModel().select(Utils.convertYearToSeason(LocalDate.now().getYear()));
        year.getChildren().addAll(seasonLabel, seasonSelection);


        ObservableList<Integer> matchdayList = FXCollections.observableArrayList(null, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38);
        VBox matchday = new VBox(3);
        matchday.setAlignment(Pos.TOP_CENTER);
        Label matchdayLabel = new Label("Matchday");
        matchdaySelection = new ComboBox<>(matchdayList);
        matchdaySelection.getSelectionModel().select(null);
        matchday.getChildren().addAll(matchdayLabel, matchdaySelection);


        ObservableList<String> teamSelection = FXCollections.observableArrayList(map.keySet());
        ObservableList<Integer> scoreSelection = FXCollections.observableArrayList(null, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
        Border border = new Border(new BorderStroke(Color.LIGHTGREY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN));
        HBox teams = new HBox(20);

        VBox home = new VBox(20);
        home.setPadding(new Insets(3));
        home.setBorder(border);
        VBox teamH = new VBox(3);
        teamH.setAlignment(Pos.TOP_CENTER);
        Label teamHLabel = new Label("Score of away team");
        teamHSelection = new ComboBox<>(teamSelection);
        teamHSelection.getSelectionModel().selectFirst();
        teamH.getChildren().addAll(teamHLabel, teamHSelection);

        VBox scoreH = new VBox(3);
        scoreH.setAlignment(Pos.TOP_CENTER);
        Label scoreHLabel = new Label("Score of away team");
        scoreHSelection = new ComboBox<>(scoreSelection);
        scoreHSelection.getSelectionModel().select(0);
        scoreH.getChildren().addAll(scoreHLabel, scoreHSelection);
        home.getChildren().addAll(teamH, scoreH);

        VBox away = new VBox(20);
        away.setPadding(new Insets(3));
        away.setBorder(border);
        VBox teamA = new VBox(3);
        teamA.setAlignment(Pos.TOP_CENTER);
        Label teamALabel = new Label("Score of away team");
        teamASelection = new ComboBox<>(teamSelection);
        teamASelection.getSelectionModel().selectFirst();
        teamA.getChildren().addAll(teamALabel, teamASelection);

        VBox scoreA = new VBox(3);
        scoreA.setAlignment(Pos.TOP_CENTER);
        Label scoreALabel = new Label("Score of away team");
        scoreASelection = new ComboBox<>(scoreSelection);
        scoreASelection.getSelectionModel().select(0);
        scoreA.getChildren().addAll(scoreALabel, scoreASelection);
        away.getChildren().addAll(teamA, scoreA);
        teams.getChildren().addAll(home, away);
        teams.setAlignment(Pos.TOP_CENTER);

        box.getChildren().addAll(year, matchday, teams);
        box.setPadding(new Insets(10));
        return box;
    }

    private Node buildButtons() {
        HBox box = new HBox(20);
        Button ok = new Button("OK");
        ok.setFocusTraversable(false);
        Button close = new Button("Close");
        close.setFocusTraversable(false);

        ok.setOnAction(ev -> {
            boolean correct = true;
            String errorMessage = "";

            int year = Utils.convertSeasonToYear(seasonSelection.getSelectionModel().getSelectedItem());
            Integer matchday = matchdaySelection.getSelectionModel().getSelectedItem();
            String teamH = "";
            teamHSelection.getSelectionModel().getSelectedItem();
            String teamA = "";
            teamASelection.getSelectionModel().getSelectedItem();
            Integer scoreH = scoreHSelection.getSelectionModel().getSelectedItem();
            Integer scoreA = scoreASelection.getSelectionModel().getSelectedItem();

            teamH = map.get(teamHSelection.getSelectionModel().getSelectedItem());
            teamA = map.get(teamASelection.getSelectionModel().getSelectedItem());
//            for (int i = 0; i < array[1].length; i++) {
//                if (teamHSelection.getSelectionModel().getSelectedItem().contentEquals(array[1][i]))
//                    teamH = array[0][i];
//                if (teamASelection.getSelectionModel().getSelectedItem().contentEquals(array[1][i]))
//                    teamA = array[0][i];
//            }

            if (teamH == teamA) {
                correct = false;
                errorMessage = "Die Teams müssen unterschiedlich sein\n";
            }
            if (scoreH == null && scoreA != null) {
                errorMessage = "Entweder beide Werte sind null oder keine\n";
                correct = false;
            }
            if (scoreH != null && scoreA == null) {
                errorMessage = "Entweder beide Werte sind null oder keine\n";
                correct = false;
            }
            if (correct) {
                String query;
                if (scoreH == null)
                    query = "INSERT INTO `scores` (`Season (Start)`, `Home Team`, `Home Goals`, `Away Team`, `Away Goals`, `Matchday`) VALUES ('" + year + "', '" + teamH + "', NULL, '" + teamA + "', NULL, " + matchday + ");";
                else
                    query = "INSERT INTO `scores` (`Season (Start)`, `Home Team`, `Home Goals`, `Away Team`, `Away Goals`, `Matchday`) VALUES ('" + year + "', '" + teamH + "'," + scoreH + ", '" + teamA + "', " + scoreA + ", " + matchday + ");";
                try {
                    DBSingleton.instance().executeUpdate(query);
                } catch (SQLException e) {
                    correct = false;
                    errorMessage = "SQL Fehler\n\n" + query;
                }
                if (correct) {
                    gui.loadContent();
                    stage.close();
                }
            }
            if (!correct) {
                VBox errorBox = new VBox();
                Label errorLabel = new Label(errorMessage);
                errorLabel.setWrapText(true);
                Button errorClose = new Button("Close");
                errorBox.getChildren().addAll(errorLabel, errorClose);
                Stage errorStage = new Stage();
                Scene errorScene = new Scene(errorBox, 580, 200);
                errorStage.setScene(errorScene);
                errorStage.show();
                errorClose.setOnAction(e -> {
                    errorStage.close();
                });

            }
        });

        close.setOnAction(ev -> {
            stage.close();
        });

        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(ok, close);
        return box;
    }
}

class UpdateFixtureBox {
    Stage stage;
    ComboBox<Integer> homeSelection, awaySelection, matchdaySelection;
    int year;
    String homeTeam, awayTeam, fullHomeTeam, fullAwayTeam;
    Integer matchday;
    GUI gui;

    public UpdateFixtureBox(GUI gui, int year, String homeTeam, String fullHomeTeam, String awayTeam, String fullAwayTeam, Integer matchday) {
        this.gui = gui;
        this.year = year;
        this.homeTeam = homeTeam;
        this.fullHomeTeam = fullHomeTeam;
        this.awayTeam = awayTeam;
        this.fullAwayTeam = fullAwayTeam;
        this.matchday = matchday;
        stage = new Stage();
        VBox box = new VBox(20);
        box.getChildren().addAll(buildMatchday(), buildInputs(), buildButtons());
        Scene scene = new Scene(box, 300, 230);
        stage.setScene(scene);
        stage.show();
    }

    private Node buildInputs() {
        HBox box = new HBox(30);
        ObservableList<Integer> scoreSelection = FXCollections.observableArrayList(null, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
        Border border = new Border(new BorderStroke(Color.LIGHTGREY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN));

        VBox home = new VBox(5);
        home.setAlignment(Pos.TOP_CENTER);
        home.setBorder(border);
        home.setPadding(new Insets(3));
        Label homeLabel = new Label(fullHomeTeam);
        homeSelection = new ComboBox<>(scoreSelection);
        homeSelection.getSelectionModel().select(0);
        home.getChildren().addAll(homeLabel, homeSelection);

        VBox away = new VBox(3);
        away.setAlignment(Pos.TOP_CENTER);
        away.setBorder(border);
        away.setPadding(new Insets(3));
        Label awayLabel = new Label(fullAwayTeam);
        awaySelection = new ComboBox<>(scoreSelection);
        awaySelection.getSelectionModel().select(0);
        away.getChildren().addAll(awayLabel, awaySelection);

        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(home, away);
        box.setPadding(new Insets(10));
        return box;
    }

    private Node buildMatchday() {
        Border border = new Border(new BorderStroke(Color.LIGHTGREY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN));
        ObservableList<Integer> matchdayList = FXCollections.observableArrayList(null, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38);
        VBox matchdayBox = new VBox(3);
        matchdayBox.setAlignment(Pos.TOP_CENTER);
        matchdayBox.setBorder(border);
        matchdayBox.setPadding(new Insets(3));
        Label matchdayLabel = new Label("Matchday");
        matchdaySelection = new ComboBox<>(matchdayList);
        matchdaySelection.getSelectionModel().select(matchday);
        matchdayBox.getChildren().addAll(matchdayLabel, matchdaySelection);
        return matchdayBox;
    }

    private Node buildButtons() {
        HBox box = new HBox(20);
        Button ok = new Button("OK");
        ok.setFocusTraversable(false);
        Button close = new Button("Close");
        close.setFocusTraversable(false);

        ok.setOnAction(ev -> {
            boolean correct = true;
            String errorMessage = "";

            Integer home = homeSelection.getSelectionModel().getSelectedItem();
            Integer away = awaySelection.getSelectionModel().getSelectedItem();
            Integer matchday = matchdaySelection.getSelectionModel().getSelectedItem();

            if (home == null && away != null) {
                errorMessage = "Entweder beide Werte sind null oder keine\n";
                correct = false;
            }
            if (home != null && away == null) {
                errorMessage = "Entweder beide Werte sind null oder keine\n";
                correct = false;
            }
            if (correct) {
                String query;
                if (home == null)
                    query = "UPDATE `scores` SET `Home Goals` = NULL, `Away Goals` = NULL, `matchday` = " + matchday + " WHERE `scores`.`Season (Start)` = " + year + " AND `scores`.`Home Team` = '" + homeTeam + "' AND `scores`.`Away Team` = '" + awayTeam + "';";
                else
                    query = "UPDATE `scores` SET `Home Goals` = " + home + ", `Away Goals` = " + away + ", `matchday` = " + matchday + " WHERE `scores`.`Season (Start)` = " + year + " AND `scores`.`Home Team` = '" + homeTeam + "' AND `scores`.`Away Team` = '" + awayTeam + "';";
                try {
                    DBSingleton.instance().executeUpdate(query);
                } catch (SQLException e) {
                    correct = false;
                    errorMessage = "SQL Fehler\n";
                }
                if (correct) {
                    gui.loadContent();
                    stage.close();
                }
            }
            if (!correct) {
                VBox errorBox = new VBox();
                Label errorLabel = new Label(errorMessage);
                Button errorClose = new Button("Close");
                errorBox.getChildren().addAll(errorLabel, errorClose);
                Stage errorStage = new Stage();
                Scene errorScene = new Scene(errorBox, 320, 200);
                errorStage.setScene(errorScene);
                errorStage.show();
                errorClose.setOnAction(e -> {
                    errorStage.close();
                });

            }
        });

        close.setOnAction(ev -> {
            stage.close();
        });

        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(ok, close);
        return box;
    }
}

class AutoFixturesBox {
    Stage stage;
    GUI gui;
    ComboBox<String> seasonSelection;

    public AutoFixturesBox(GUI gui) {
        this.gui = gui;
        stage = new Stage();
        VBox box = new VBox(20);
        box.setPadding(new Insets(10));
        box.getChildren().addAll(buildInputs(), buildButtons());
        Scene scene = new Scene(box, 300, 130);
        stage.setScene(scene);
        stage.show();
    }

    private Node buildInputs() {

        VBox season = new VBox(5);
        season.setAlignment(Pos.TOP_CENTER);
        ObservableList<String> seasonSelect = FXCollections.observableArrayList();
        int i = LocalDate.now().getYear();
        while (i >= 1992) {
            seasonSelect.add(Utils.convertYearToSeason(i--));
        }
        Label seasonLabel = new Label("Season");
        seasonSelection = new ComboBox<>(seasonSelect);
        seasonSelection.getSelectionModel().select(Utils.convertYearToSeason(LocalDate.now().getYear()));
        season.getChildren().addAll(seasonLabel, seasonSelection);

        return season;
    }

    private Node buildButtons() {
        HBox box = new HBox(20);
        Button ok = new Button("OK");
        ok.setFocusTraversable(false);
        Button close = new Button("Close");
        close.setFocusTraversable(false);

        ok.setOnAction(ev -> {
            int year = Utils.convertSeasonToYear(seasonSelection.getSelectionModel().getSelectedItem());
            String[][] array = {{"ARS", "CHE", "LIV", "MCI", "MUN", "TOT", "AVL", "NEW", "BHA", "BRE"}, {"Arsenal", "Chelsea", "Liverpool", "Manchester City", "Manchester United", "Tottenham Hotspur", "Aston Villa", "Newcastle", "Brighton Hove & Albion", "Brentford"}};

            HashMap<String, String> teams = new HashMap<>();
            String[][] array2 = {{"ARS", "CHE", "LIV", "MCI", "MUN", "TOT", "AVL", "NEW", "BHA", "BRE"}, {"arsenal-fc", "chelsea-fc", "liverpool-fc", "manchester-city", "manchester-united", "tottenham-hotspur", "aston-villa", "newcastle-united", "brighton-amp-hove-albion", "brentford-fc"}};
            for(int i=0; i< array2[0].length; i++)
                teams.put(array2[1][i], array2[0][i]);
            ArrayList<String[]> scrapedResults = Scraper.scrape(year, teams);
            String update = "INSERT INTO `scores` (`Season (Start)`, `Home Team`, `Home Goals`, `Away Team`, `Away Goals`, `Matchday`) VALUES ";
            update += scrapedResults.stream().map(game ->
                    "(" + year + ", '" + game[1] + "', " + game[3] + ", '" + game[2] + "', " + game[4] + ", " + game[0] + ")").collect(Collectors.joining(", "));
            update += "ON DUPLICATE KEY UPDATE `HOME GOALS` = VALUES(`HOME GOALS`), `AWAY GOALS` = VALUES(`AWAY GOALS`), `MATCHDAY` = VALUES(`MATCHDAY`);";
            try {
                DBSingleton dbs = DBSingleton.instance();

                dbs.executeUpdate(update);
                //                "'" + year + "', '" + array[0][i] + "', NULL, '" + array[0][j] + "', NULL)";
//                for (int i = 0; i < array[0].length; i++) {
//                    for (int j = 0; j < array[0].length; j++) {
//                        String query = "Select COUNT(*) as Count from scores where `Home Team` = '" + array[0][i] + "' and `Away Team` = '" + array[0][j] + "' and `season (start)`= " + year;
//                        ResultSet rs = dbs.executeQuery(query);
//                        rs.next();
//                        if (i != j && rs.getInt("Count") == 0) {
//                            String update2 = "INSERT INTO `scores` (`Season (Start)`, `Home Team`, `Home Goals`, `Away Team`, `Away Goals`) VALUES ('" + Utils.convertSeasonToYear(seasonSelection.getSelectionModel().getSelectedItem()) + "', '" + array[0][i] + "', NULL, '" + array[0][j] + "', NULL)";
//                            dbs.executeUpdate(update2);
//                        }
//                    }
//                }
                gui.loadContent();
                stage.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                (new Exception("SQL command contains error: \n" + update, e)).printStackTrace();
            }
        });

        close.setOnAction(ev -> {
            stage.close();
        });

        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(ok, close);
        return box;
    }
}
