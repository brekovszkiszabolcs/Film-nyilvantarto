package org.example.model;

// Film objektum modellezése a specifikáció szerinti adatszerkezethez

public class Film {
    private int filmId;
    private int felhasznaloId; // A filmet rögzítő felhasználó azonosítója
    private String cim;
    private String megjelenesDatum; // Módosítva int-ről String-re (pl. "2026-11-10")
    private String mufaj;
    private String leiras;
    private String kepUrl;

    public Film() {
    }

    public Film(int felhasznaloId, String cim, String megjelenesDatum, String mufaj, String leiras, String kepUrl) {
        this.felhasznaloId = felhasznaloId;
        this.cim = cim;
        this.megjelenesDatum = megjelenesDatum;
        this.mufaj = mufaj;
        this.leiras = leiras;
        this.kepUrl = kepUrl;
    }

    public int getFilmId() {
        return filmId;
    }

    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    public int getFelhasznaloId() {
        return felhasznaloId;
    }

    public void setFelhasznaloId(int felhasznaloId) {
        this.felhasznaloId = felhasznaloId;
    }

    public String getCim() {
        return cim;
    }

    public void setCim(String cim) {
        this.cim = cim;
    }

    // Új getter a String alapú dátumhoz
    public String getMegjelenesDatum() {
        return megjelenesDatum;
    }

    // Új setter a String alapú dátumhoz
    public void setMegjelenesDatum(String megjelenesDatum) {
        this.megjelenesDatum = megjelenesDatum;
    }

    public String getMufaj() {
        return mufaj;
    }

    public void setMufaj(String mufaj) {
        this.mufaj = mufaj;
    }

    public String getLeiras() {
        return leiras;
    }

    public void setLeiras(String leiras) {
        this.leiras = leiras;
    }

    public String getKepUrl() {
        return kepUrl;
    }

    public void setKepUrl(String kepUrl) {
        this.kepUrl = kepUrl;
    }
}