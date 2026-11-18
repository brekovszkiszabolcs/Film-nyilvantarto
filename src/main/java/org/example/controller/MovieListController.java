package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.Film;
import org.example.service.DatabaseMoziService;
import org.example.service.MoziService;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MovieListController implements BaseController {

    private Stage stage;
    private final MoziService service = new DatabaseMoziService();
    private ObservableList<Film> userMovies;

    @FXML private VBox movieListContainer;
    @FXML private TextField searchField; // ÚJ: Keresőmező
    @FXML private ComboBox<String> genreFilter;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Button backButton;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        initializeFilters();
        loadUserMovies();
    }

    private void initializeFilters() {
        // Műfaj szűrő inicializálása
        ObservableList<String> genres = FXCollections.observableArrayList(
                "Összes műfaj", "Akció", "Sci-Fi", "Dráma", "Krimi", "Vígjáték",
                "Kaland", "Horror", "Romantikus", "Thriller", "Animációs"
        );
        genreFilter.setItems(genres);
        genreFilter.getSelectionModel().selectFirst();

        // Rendezés inicializálása
        ObservableList<String> sortOptions = FXCollections.observableArrayList(
                "Név szerint (A-Z)", "Megjelenés ideje (Újak elöl)"
        );
        sortComboBox.setItems(sortOptions);
        sortComboBox.getSelectionModel().selectFirst();

        // Élő keresés figyelése gépelés közben
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterAndSortMovies();
            });
        }
    }

    public void loadUserMovies() {
        if (MainController.loggedInUser != null) {
            List<Film> backendFilms = service.getFilmekByFelhasznalo(MainController.loggedInUser.getFelhasznaloId());
            userMovies = FXCollections.observableArrayList(backendFilms);
            filterAndSortMovies();
        }
    }

    @FXML
    public void filterAndSortMovies() {
        if (userMovies == null) return;

        String selectedGenre = genreFilter.getValue();
        String sortOrder = sortComboBox.getValue();
        String searchText = (searchField != null && searchField.getText() != null)
                ? searchField.getText().trim().toLowerCase()
                : "";

        // 1. Szűrés műfaj ÉS cím (keresőmező) szerint
        List<Film> filteredList = userMovies.stream()
                .filter(film -> selectedGenre == null || "Összes műfaj".equals(selectedGenre) || film.getMufaj().contains(selectedGenre))
                .filter(film -> searchText.isEmpty() || (film.getCim() != null && film.getCim().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());

        // 2. Rendezés
        if ("Név szerint (A-Z)".equals(sortOrder)) {
            filteredList.sort(Comparator.comparing(Film::getCim, String.CASE_INSENSITIVE_ORDER));
        } else if ("Megjelenés ideje (Újak elöl)".equals(sortOrder)) {
            filteredList.sort(Comparator.comparing(Film::getMegjelenesDatum).reversed());
        }

        displayMovies(filteredList);
    }

    private void displayMovies(List<Film> movies) {
        movieListContainer.getChildren().clear();

        if (movies.isEmpty()) {
            Label placeholder = new Label("Nincs a szűrésnek megfelelő filmed.");
            placeholder.setStyle("-fx-font-size: 18px; -fx-padding: 30; -fx-text-fill: #999999;");
            movieListContainer.getChildren().add(placeholder);
            return;
        }

        for (Film film : movies) {
            movieListContainer.getChildren().add(createMovieCard(film));
        }
    }

    private HBox createMovieCard(Film film) {
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: #222222; -fx-padding: 15; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Poszter
        ImageView posterView = new ImageView();
        posterView.setFitWidth(120);
        posterView.setFitHeight(180);
        posterView.setPreserveRatio(true);
        try {
            if (film.getKepUrl() != null && !film.getKepUrl().isEmpty()) {
                posterView.setImage(new Image(film.getKepUrl(), true));
            }
        } catch (Exception ignored) {}

        // Adatok
        VBox infoBox = new VBox(8);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(film.getCim());
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label yearLabel = new Label(film.getMegjelenesDatum());
        yearLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #dddddd;");

        Label genreLabel = new Label(film.getMufaj());
        genreLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #aaaaaa;");

        Label descLabel = new Label(film.getLeiras());
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #cccccc;");
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(60);

        infoBox.getChildren().addAll(titleLabel, yearLabel, genreLabel, descLabel);

        // Módosítás Gomb
        Button editButton = new Button("Módosítás");
        editButton.setStyle("-fx-background-color: #666; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        editButton.setOnAction(e -> openFilmForm(film));

        VBox actionBox = new VBox(editButton);
        actionBox.setAlignment(Pos.BOTTOM_RIGHT);

        card.getChildren().addAll(posterView, infoBox, actionBox);
        return card;
    }

    @FXML
    private void handleNewFilm() {
        openFilmForm(null);
    }

    private void openFilmForm(Film filmToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FilmForm.fxml"));
            Parent root = loader.load();

            FilmFormController controller = loader.getController();
            controller.setParentController(this);

            if (filmToEdit != null) {
                controller.setFilmToEdit(filmToEdit);
            }

            Stage formStage = new Stage();
            formStage.initModality(Modality.APPLICATION_MODAL);
            formStage.setTitle(filmToEdit == null ? "Új film felvétele" : "Film módosítása");
            formStage.setScene(new Scene(root));
            formStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBackToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Main.fxml"));
            Parent root = loader.load();
            MainController controller = loader.getController();
            controller.setStage(stage);
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}