package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.model.Film;
import org.example.service.DatabaseMoziService;
import org.example.service.MoziService;

import java.io.File;

public class FilmFormController {

    @FXML private TextField titleField;
    @FXML private ComboBox<String> yearComboBox;
    @FXML private ComboBox<String> monthComboBox;
    @FXML private ComboBox<String> dayComboBox;
    @FXML private ComboBox<String> genreComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private TextField imageUrlField;
    @FXML private Button deleteButton;

    private Film filmToEdit = null;
    private MovieListController parentController;
    private final MoziService service = new DatabaseMoziService();

    @FXML
    public void initialize() {
        // Évek betöltése (1900-tól a jelenlegi évig)
        ObservableList<String> years = FXCollections.observableArrayList();
        for (int i = 2026; i >= 1900; i--) {
            years.add(String.valueOf(i));
        }
        yearComboBox.setItems(years);

        // Hónapok betöltése (01 - 12)
        ObservableList<String> months = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) {
            months.add(String.format("%02d", i));
        }
        monthComboBox.setItems(months);

        // Napok betöltése (01 - 31)
        ObservableList<String> days = FXCollections.observableArrayList();
        for (int i = 1; i <= 31; i++) {
            days.add(String.format("%02d", i));
        }
        dayComboBox.setItems(days);

        // Műfajok betöltése
        genreComboBox.setItems(FXCollections.observableArrayList(
                "Akció", "Sci-Fi", "Dráma", "Krimi", "Vígjáték", "Kaland", "Horror", "Romantikus", "Thriller", "Animációs"
        ));
    }

    public void setParentController(MovieListController parentController) {
        this.parentController = parentController;
    }

    public void setFilmToEdit(Film film) {
        this.filmToEdit = film;
        titleField.setText(film.getCim());

        // Dátum betöltése módosításkor ("ÉÉÉÉ-HH-NN" szétszedése)
        if (film.getMegjelenesDatum() != null && film.getMegjelenesDatum().contains("-")) {
            String[] reszek = film.getMegjelenesDatum().split("-");
            if (reszek.length == 3) {
                yearComboBox.setValue(reszek[0]);
                monthComboBox.setValue(reszek[1]);
                dayComboBox.setValue(reszek[2]);
            }
        }

        genreComboBox.setValue(film.getMufaj());
        descriptionArea.setText(film.getLeiras());
        imageUrlField.setText(film.getKepUrl());

        deleteButton.setVisible(true);
        deleteButton.setManaged(true);
    }

    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Film poszter kiválasztása");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Képfájlok", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) titleField.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null && imageUrlField != null) {
            imageUrlField.setText(selectedFile.toURI().toString());
        }
    }

    @FXML
    private void handleSave() {
        String cim = titleField.getText();
        String mufaj = genreComboBox.getValue();
        String leiras = descriptionArea.getText();
        String kepUrl = imageUrlField.getText();

        String selectedYear = yearComboBox.getValue();
        String selectedMonth = monthComboBox.getValue();
        String selectedDay = dayComboBox.getValue();

        if (cim == null || cim.trim().isEmpty()) {
            showAlert("Hiba", "A cím megadása kötelező!");
            return;
        }

        if (selectedYear == null || selectedMonth == null || selectedDay == null) {
            showAlert("Hiba", "Kérlek válaszd ki a pontos dátumot (Év, Hónap, Nap)!");
            return;
        }

        if (mufaj == null || mufaj.trim().isEmpty()) {
            showAlert("Hiba", "Kérlek válassz ki egy műfajt!");
            return;
        }

        if (MainController.loggedInUser == null) {
            showAlert("Hiba", "Nincs bejelentkezett felhasználó!");
            closeWindow();
            return;
        }

        // Összerakjuk a teljes dátumot String-ként
        String teljesDatum = selectedYear + "-" + selectedMonth + "-" + selectedDay;
        boolean siker = false;

        if (filmToEdit != null) {
            filmToEdit.setCim(cim);
            filmToEdit.setMegjelenesDatum(teljesDatum); // String beállítása
            filmToEdit.setMufaj(mufaj);
            filmToEdit.setLeiras(leiras);
            filmToEdit.setKepUrl(kepUrl);
            siker = service.filmModositasa(filmToEdit);
        } else {
            // Új film létrehozása a String dátummal
            Film ujFilm = new Film(MainController.loggedInUser.getFelhasznaloId(), cim, teljesDatum, mufaj, leiras, kepUrl);
            siker = service.ujFilmHozzaadasa(ujFilm);
        }

        if (siker) {
            if (parentController != null) {
                parentController.loadUserMovies();
            }
            closeWindow();
        } else {
            showAlert("Hiba", "Adatbázis hiba történt a mentés során.");
        }
    }

    @FXML
    private void handleDelete() {
        if (filmToEdit != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Törlés megerősítése");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Biztosan törölni szeretnéd a(z) '" + filmToEdit.getCim() + "' című filmet?");

            if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                boolean siker = service.filmTorlese(filmToEdit.getFilmId());

                if (siker) {
                    if (parentController != null) {
                        parentController.loadUserMovies();
                    }
                    closeWindow();
                } else {
                    showAlert("Hiba", "Adatbázis hiba történt a törlés során.");
                }
            }
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}