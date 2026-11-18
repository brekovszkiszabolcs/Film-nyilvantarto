package org.example.dao;

import org.example.database.DatabaseManager;
import org.example.model.Film;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Az adatbázisból kiolvassa a filmeket és átalakítja Film objektumokká,
// illetve kezeli az új filmek mentését, módosítását és törlését.

public class FilmDAO {

    // Lekérdezi egy adott felhasználó rögzített filmjeit
    public List<Film> findByFelhasznaloId(int felhasznaloId) {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT * FROM Film WHERE felhasznaloId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, felhasznaloId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Film film = new Film();
                film.setFilmId(rs.getInt("filmId"));
                film.setFelhasznaloId(rs.getInt("felhasznaloId"));
                film.setCim(rs.getString("cim"));

                // JAVÍTVA: rs.getInt("megjelenesEve") -> rs.getString("megjelenesDatum")
                film.setMegjelenesDatum(rs.getString("megjelenesDatum"));

                film.setMufaj(rs.getString("mufaj"));
                film.setLeiras(rs.getString("leiras"));
                film.setKepUrl(rs.getString("kepUrl"));
                films.add(film);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return films;
    }

    // Egy konkrét film betöltése ID alapján
    public Film findById(int id) {
        String sql = "SELECT * FROM Film WHERE filmId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Film film = new Film();
                film.setFilmId(rs.getInt("filmId"));
                film.setFelhasznaloId(rs.getInt("felhasznaloId"));
                film.setCim(rs.getString("cim"));

                // JAVÍTVA: int helyett String beolvasás
                film.setMegjelenesDatum(rs.getString("megjelenesDatum"));

                film.setMufaj(rs.getString("mufaj"));
                film.setLeiras(rs.getString("leiras"));
                film.setKepUrl(rs.getString("kepUrl"));
                return film;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ÚJ FILM FELVÉTELE
    public boolean insert(Film film) {
        // JAVÍTVA az SQL: megjelenesEve -> megjelenesDatum
        String sql = "INSERT INTO Film (felhasznaloId, cim, megjelenesDatum, mufaj, leiras, kepUrl) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, film.getFelhasznaloId());
            pstmt.setString(2, film.getCim());

            // JAVÍTVA: setInt helyett setString a dátumhoz
            pstmt.setString(3, film.getMegjelenesDatum());

            pstmt.setString(4, film.getMufaj());
            pstmt.setString(5, film.getLeiras());
            pstmt.setString(6, film.getKepUrl());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // MEGLÉVŐ FILM MÓDOSÍTÁSA
    public boolean update(Film film) {
        // JAVÍTVA az SQL: megjelenesEve -> megjelenesDatum
        String sql = "UPDATE Film SET cim=?, megjelenesDatum=?, mufaj=?, leiras=?, kepUrl=? WHERE filmId=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, film.getCim());

            // JAVÍTVA: setInt helyett setString a dátumhoz
            pstmt.setString(2, film.getMegjelenesDatum());

            pstmt.setString(3, film.getMufaj());
            pstmt.setString(4, film.getLeiras());
            pstmt.setString(5, film.getKepUrl());
            pstmt.setInt(6, film.getFilmId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // FILM TÖRLÉSE
    public boolean delete(int filmId) {
        String sql = "DELETE FROM Film WHERE filmId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, filmId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}