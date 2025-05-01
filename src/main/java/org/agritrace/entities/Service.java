package org.agritrace.entities;

import java.time.LocalDateTime;

public class Service {
    private int id;
    private String nom;
    private String type;
    private String description;
    private int prix;
    private String status;
    private String adresse;
    private String image;
    private LocalDateTime updatedAt;

    // Constructors
    public Service() {}

    public Service(int id, String nom, String type, String description, int prix, String adresse, String status, String image) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.prix = prix;
        this.adresse = adresse;
        this.status = status;
        this.image = image;
    }

    public Service( String nom, String type, String description, int prix,
                   String status, String adresse, String image, LocalDateTime updatedAt) {
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.prix = prix;
        this.status = status;
        this.adresse = adresse;
        this.image = image;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
