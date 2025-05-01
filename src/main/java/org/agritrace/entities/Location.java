package org.agritrace.entities;

import java.time.LocalDate;

public class Location {
    private int id;
    private int serviceId;
    private int idUser;
    private String details;
    private double prixTotal;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    // Constructors
    public Location() {}

    public Location(int serviceId, int idUser, String details, double prixTotal, LocalDate dateDebut, LocalDate dateFin) {
        this.serviceId = serviceId;
        this.idUser = idUser;
        this.details = details;
        this.prixTotal = prixTotal;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
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
}

