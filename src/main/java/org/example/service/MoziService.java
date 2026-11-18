package org.example.service;

import org.example.model.Film;
import java.util.List;

// Ez az interfész definiálja a személyes film nyilvántartó funkcióit
public interface MoziService {

    // --- AUTENTIKÁCIÓ (Felhasználó kezelése) ---

    AuthEredmeny megprobalRegisztralni(String felhasznalonev, String jelszo);

    AuthEredmeny megprobalBejelentkezni(String felhasznalonev, String jelszo);


    // --- FILM NYILVÁNTARTÁS (A specifikációban kért funkciók) ---

    // Lekérdezi egy konkrét felhasználó rögzített filmjeit
    List<Film> getFilmekByFelhasznalo(int felhasznaloId);

    // Lekérdez egy konkrét filmet az azonosítója alapján (pl. módosítás betöltéséhez)
    Film getFilmById(int filmId);

    // Új film felvétele (Rögzítés)
    boolean ujFilmHozzaadasa(Film film);

    // Meglévő film adatainak frissítése (Módosítás)
    boolean filmModositasa(Film film);

    // Opcionális, de hasznos: Film törlése a listából
    boolean filmTorlese(int filmId);
}