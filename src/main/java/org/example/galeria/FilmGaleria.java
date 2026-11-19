package org.example.galeria;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.controller.FilmFormController;
import org.example.controller.MovieListController;

import java.io.IOException;
import java.util.List;

/** A galéria megjelenítése. A felhasználó saját filmjeit a közös vezérlők kezelik. */
final class FilmGaleria {
    private record Ajanlo(String cim, String ev, String mufaj, String fajl, String leiras) {
        String kep() { return "images/galeria/" + fajl + ".jpg"; }
    }

    private static final List<Ajanlo> FILMEK = List.of(
        new Ajanlo("A keresztapa", "1972", "Krimi", "keresztapa", "Michael Corleone egy merénylet után apja oldalán találja magát."),
        new Ajanlo("Csillagok között", "2014", "Sci-Fi", "csillagok", "Egy pilóta egy féregjáraton túl keres új otthont az emberiségnek."),
        new Ajanlo("Eredet", "2010", "Sci-Fi", "eredet", "Egy álomtolvaj csapatának egy ötletet kell elültetnie egy örökös tudatában."),
        new Ajanlo("Jurassic Park", "1993", "Kaland", "jurassic", "Egy sziget vendégeire elszabadult dinoszauruszok vadásznak."),
        new Ajanlo("A Grand Budapest Hotel", "2014", "Vígjáték", "hotel", "Egy londiner és egy főportás egy eltűnt festmény nyomába ered."),
        new Ajanlo("A cápa", "1975", "Thriller", "capa", "Egy rendőrfőnök, egy biológus és egy halász a fürdőzőket fenyegető cápát keresi.")
    );

    private final Stage stage;
    private MovieListController lista;
    FilmGaleria(Stage stage) { this.stage = stage; }

    void megnyit() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MovieList.fxml"));
        Parent sajatFilmek = loader.load();
        lista = loader.getController();
        lista.setStage(stage);
        Button vissza = (Button) loader.getNamespace().get("backButton");
        vissza.setText("← Főmenü");
        vissza.setOnAction(e -> fomenu());

        TabPane lapok = new TabPane();
        Tab galeria = new Tab("Plakátgaléria", galeria());
        Tab sajat = new Tab("Saját filmjeim", sajatFilmek);
        sajat.setOnSelectionChanged(e -> { if (sajat.isSelected()) lista.loadUserMovies(); });
        lapok.getTabs().addAll(galeria, sajat);
        lapok.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Scene scene = new Scene(lapok, 1000, 715);
        scene.getStylesheets().add(getClass().getResource("/styles/plakatgaleria.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Absolute Cinema – Filmgyűjtemény");
    }

    private BorderPane galeria() {
        Label cim = new Label("Plakátgaléria");
        cim.getStyleClass().add("galeria-cim");
        Button vissza = new Button("Főmenü");
        vissza.setOnAction(e -> fomenu());
        Region hely = new Region();
        HBox.setHgrow(hely, Priority.ALWAYS);
        HBox fej = new HBox(12, cim, hely, vissza);
        fej.setAlignment(Pos.CENTER_LEFT);
        fej.setPadding(new Insets(16, 24, 16, 24));

        FlowPane racs = new FlowPane(18, 18);
        racs.setPadding(new Insets(4, 24, 24, 24));
        for (Ajanlo film : FILMEK) racs.getChildren().add(kartya(film));
        ScrollPane gorgeto = new ScrollPane(racs);
        gorgeto.setFitToWidth(true);
        gorgeto.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        BorderPane lap = new BorderPane(gorgeto);
        lap.getStyleClass().add("plakatgaleria");
        lap.setTop(fej);
        return lap;
    }

    private VBox kartya(Ajanlo film) {
        ImageView kep = new ImageView(new Image(getClass().getResource("/" + film.kep()).toExternalForm(), 224, 336, true, true));
        kep.setFitWidth(224); kep.setFitHeight(336); kep.setPreserveRatio(true);
        kep.setAccessibleText(film.cim() + " – borító");
        Label cim = new Label(film.cim());
        cim.getStyleClass().add("film-cim");
        Label adat = new Label(film.ev() + "   ·   " + film.mufaj());
        Label leiras = new Label(film.leiras());
        leiras.setWrapText(true); leiras.setMinHeight(64);
        Button felvetel = new Button("Felvétel a saját filmjeimhez");
        felvetel.setOnAction(e -> urlap(film));
        VBox szoveg = new VBox(7, cim, adat, leiras, felvetel);
        szoveg.setPadding(new Insets(12));
        VBox kartya = new VBox(kep, szoveg);
        kartya.setPrefWidth(224); kartya.setMaxWidth(224);
        kartya.getStyleClass().add("film-kartya");
        return kartya;
    }

    private void urlap(Ajanlo film) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FilmForm.fxml"));
            Parent root = loader.load();
            FilmFormController form = loader.getController();
            form.setParentController(lista);
            var mezok = loader.getNamespace();
            ((TextField) mezok.get("titleField")).setText(film.cim());
            ((TextArea) mezok.get("descriptionArea")).setText(film.leiras());
            ComboBox<?> ev = (ComboBox<?>) mezok.get("yearComboBox");
            ev.getSelectionModel().select(ev.getItems().indexOf(film.ev()));
            ComboBox<?> mufaj = (ComboBox<?>) mezok.get("genreComboBox");
            mufaj.getSelectionModel().select(mufaj.getItems().indexOf(film.mufaj()));
            ((TextField) mezok.get("imageUrlField")).setText(film.kep());
            Stage ablak = new Stage();
            ablak.initOwner(stage); ablak.initModality(Modality.WINDOW_MODAL);
            ablak.setTitle("Film felvétele – válassz hónapot és napot");
            ablak.setScene(new Scene(root)); ablak.showAndWait();
        } catch (IOException e) { GaleriaIndito.hiba(e); }
    }

    private void fomenu() {
        try { GaleriaIndito.Nezet.fomenu(stage); }
        catch (IOException e) { GaleriaIndito.hiba(e); }
    }
}
