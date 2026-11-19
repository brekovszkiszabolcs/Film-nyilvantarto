package org.example.galeria;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.controller.MainController;
import org.example.database.DatabaseManager;

import java.io.IOException;

/** A közös projekt indítása a plakátgalériával. */
public final class GaleriaIndito {
    public static void main(String[] args) {
        Application.launch(Nezet.class, args);
    }

    public static final class Nezet extends Application {
        @Override public void start(Stage stage) throws IOException {
            DatabaseManager.initializeDatabase();
            fomenu(stage);
            stage.show();
        }

        static void fomenu(Stage stage) throws IOException {
            FXMLLoader loader = new FXMLLoader(Nezet.class.getResource("/view/Main.fxml"));
            // Ugyanaz az űrlap és bejelentkezés, csak a filmes menüpont célja tér el.
            MainController menu = new GaleriaMenu(stage);
            loader.setControllerFactory(type -> menu);
            Pane root = loader.load();
            menu.setStage(stage);
            stage.setTitle("Absolute Cinema");
            stage.setScene(new Scene(root));
        }
    }

    public static final class GaleriaMenu extends MainController {
        private final Stage ablak;
        public GaleriaMenu(Stage ablak) { this.ablak = ablak; }

        @Override public void goToMovieList() {
            if (loggedInUser == null) {
                super.goToMovieList();
                return;
            }
            try { new FilmGaleria(ablak).megnyit(); }
            catch (IOException e) { hiba(e); }
        }
    }

    static void hiba(IOException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("A nézet betöltése sikertelen.");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}
