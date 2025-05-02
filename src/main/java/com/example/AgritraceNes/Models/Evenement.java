package com.example.AgritraceNes.Models;

import java.time.LocalDate;

public class Evenement {
    private int id;
    private int categorieId;
    private String nom;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private double prix;
    private String lieu;
    private int placesDisponibles;
    private String image;

    public Evenement() {}

    public Evenement(int id, int categorieId, String nom, String description,
                     LocalDate dateDebut, LocalDate dateFin, double prix,
                     String lieu, int placesDisponibles, String image) {
        this.id = id;
        this.categorieId = categorieId;
        this.nom = nom;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prix = prix;
        this.lieu = lieu;
        this.placesDisponibles = placesDisponibles;
        this.image = image;
    }
    public Evenement(String nom, String description, LocalDate dateDebut, LocalDate dateFin,
                     String lieu, double prix, int placesDisponibles, String image, int categorieId) {
        this.nom = nom;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.prix = prix;
        this.placesDisponibles = placesDisponibles;
        this.image = image;
        this.categorieId = categorieId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(int categorieId) {
        this.categorieId = categorieId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public int getPlacesDisponibles() {
        return placesDisponibles;
    }

    public void setPlacesDisponibles(int placesDisponibles) {
        this.placesDisponibles = placesDisponibles;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
// Getters et Setters...

}

