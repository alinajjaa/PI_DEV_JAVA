package com.example.AgritraceNes.Models;

import java.time.LocalDateTime;

public class Location {
    private int id;
    private int serviceId;
    private int idUser;
    private String details;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private int prixTotal;

    public Location() {}

    public Location(int id, int serviceId, int idUser, String details,
                    LocalDateTime dateDebut, LocalDateTime dateFin, int prixTotal) {
        this.id = id;
        this.serviceId = serviceId;
        this.idUser = idUser;
        this.details = details;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prixTotal = prixTotal;
    }

    // Getters et Setters...
}

