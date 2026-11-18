package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:absolute_cinema.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // --- TÁBLÁK LÉTREHOZÁSA (A felesleges mozi táblák törölve) ---
            stmt.execute("CREATE TABLE IF NOT EXISTS Felhasznalo (" +
                    "felhasznaloId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "felhasznaloNev TEXT NOT NULL UNIQUE, " +
                    "jelszoHASH TEXT NOT NULL);");

            // JAVÍTVA: megjelenesEve INTEGER -> megjelenesDatum TEXT
            stmt.execute("CREATE TABLE IF NOT EXISTS Film (" +
                    "filmId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "felhasznaloId INTEGER NOT NULL, " +
                    "cim TEXT NOT NULL, " +
                    "megjelenesDatum TEXT, " +
                    "mufaj TEXT, " +
                    "leiras TEXT, " +
                    "kepUrl TEXT, " +
                    "FOREIGN KEY (felhasznaloId) REFERENCES Felhasznalo(felhasznaloId));");

            // --- KEZDŐ ADATOK BETÖLTÉSE ---
            insertInitialData(conn);
            System.out.println("Adatbázis inicializálása kész. (Személyes Film Nyilvántartó mód)");

        } catch (SQLException e) {
            System.err.println("Adatbázis hiba: " + e.getMessage());
        }
    }

    private static void insertInitialData(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {

            // Ellenőrizzük, van-e már felhasználó
            boolean vanUser = false;
            try (var rs = stmt.executeQuery("SELECT count(*) AS total FROM Felhasznalo")) {
                if (rs.next() && rs.getInt("total") > 0) {
                    vanUser = true;
                }
            }

            // Ha teljesen üres az adatbázis, feltöltjük a teszt adatokkal
            if (!vanUser) {
                System.out.println("Kezdő adatok (Teszt felhasználó és specifikáció szerinti filmek) betöltése...");

                // 1. Teszt felhasználó létrehozása (Automatikusan ő lesz az 1-es ID)
                stmt.execute("INSERT INTO Felhasznalo (felhasznaloNev, jelszoHASH) VALUES ('teszt', 'teszt');");

                // 2. Filmek rögzítése a teszt felhasználóhoz (felhasznaloId = 1)
                // JAVÍTVA: megjelenesEve -> megjelenesDatum, és az évszámok lecserélve teljes dátum stringekre ('ÉÉÉÉ-01-01')
                stmt.execute("INSERT INTO Film (felhasznaloId, cim, megjelenesDatum, mufaj, leiras, kepUrl) VALUES " +
                        "(1, 'A Keresztapa', '1972-01-01', 'Dráma', " +
                        "'Egy maffiacsalád generációkon átívelő története.', " +
                        "'https://image.tmdb.org/t/p/original/3T18xJEBwHT1bK7TEdLwQ3uU7kR.jpg');");

                stmt.execute("INSERT INTO Film (felhasznaloId, cim, megjelenesDatum, mufaj, leiras, kepUrl) VALUES " +
                        "(1, 'Eredet', '2010-01-01', 'Sci-Fi', " +
                        "'Egy tolvaj, aki behatol az emberek álmaiba.', " +
                        "'https://image.tmdb.org/t/p/original/9xjZS2rlVxm8SFx8kPC3aIGCOYQ.jpg');");

                stmt.execute("INSERT INTO Film (felhasznaloId, cim, megjelenesDatum, mufaj, leiras, kepUrl) VALUES " +
                        "(1, 'Ponyvaregény', '1994-01-01', 'Krimi', " +
                        "'Kereszteződő alvilági történetek Los Angelesben.', " +
                        "'https://image.tmdb.org/t/p/original/vQWk5TI7rx54C5TE1nU8mH7bFjO.jpg');");

                stmt.execute("INSERT INTO Film (felhasznaloId, cim, megjelenesDatum, mufaj, leiras, kepUrl) VALUES " +
                        "(1, 'Interstellar', '2014-01-01', 'Sci-Fi', " +
                        "'Egy csapat felfedező egy féregjáraton utazik keresztül a megmentésünkért.', " +
                        "'https://image.tmdb.org/t/p/original/6KiSSndIMLj1swkpPNq2lYppDVQ.jpg');");

                System.out.println("Adatok sikeresen betöltve!");
            }
        }
    }
}