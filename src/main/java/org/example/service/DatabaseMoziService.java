package org.example.service;

import org.example.dao.FelhasznaloDAO;
import org.example.dao.FilmDAO;
import org.example.model.Felhasznalo;
import org.example.model.Film;

import java.util.List;

// A MoziService interfész reális, személyes film nyilvántartó implementációja
public class DatabaseMoziService implements MoziService {

    private final FilmDAO filmDAO = new FilmDAO();
    private final FelhasznaloDAO felhasznaloDAO = new FelhasznaloDAO();

    @Override
    public AuthEredmeny megprobalRegisztralni(String felhasznalonev, String jelszo) {
        if (felhasznalonev == null || felhasznalonev.isEmpty() || jelszo == null || jelszo.isEmpty()) {
            return new AuthEredmeny(false, "Hiányzó adatok!");
        }
        boolean siker = felhasznaloDAO.register(felhasznalonev, jelszo);
        if (siker) {
            return new AuthEredmeny(true, "Sikeres regisztráció!");
        }
        return new AuthEredmeny(false, "Foglalt felhasználónév.");
    }

    @Override
    public AuthEredmeny megprobalBejelentkezni(String felhasznalonev, String jelszo) {
        Felhasznalo user = felhasznaloDAO.login(felhasznalonev, jelszo);
        if (user != null) {
            return new AuthEredmeny(true, "Sikeres belépés!", user);
        }
        return new AuthEredmeny(false, "Hibás felhasználónév vagy jelszó.");
    }

    @Override
    public List<Film> getFilmekByFelhasznalo(int felhasznaloId) {
        return filmDAO.findByFelhasznaloId(felhasznaloId);
    }

    @Override
    public Film getFilmById(int filmId) {
        return filmDAO.findById(filmId);
    }

    @Override
    public boolean ujFilmHozzaadasa(Film film) {
        return filmDAO.insert(film);
    }

    @Override
    public boolean filmModositasa(Film film) {
        return filmDAO.update(film);
    }

    @Override
    public boolean filmTorlese(int filmId) {
        return filmDAO.delete(filmId);
    }
}