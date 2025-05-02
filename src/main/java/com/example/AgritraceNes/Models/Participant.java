package com.example.AgritraceNes.Models;

import java.time.LocalDateTime;

public class Participant {
    private int id;
    private LocalDateTime dateParticipation;
    private int nombrePersonnes;
    private int clientId;
    private int evenementId;

    public Participant() {}

    public Participant(int id, LocalDateTime dateParticipation, int nombrePersonnes, int clientId, int evenementId) {
        this.id = id;
        this.dateParticipation = dateParticipation;
        this.nombrePersonnes = nombrePersonnes;
        this.clientId = clientId;
        this.evenementId = evenementId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateParticipation() {
        return dateParticipation;
    }

    public void setDateParticipation(LocalDateTime dateParticipation) {
        this.dateParticipation = dateParticipation;
    }

    public int getNombrePersonnes() {
        return nombrePersonnes;
    }

    public void setNombrePersonnes(int nombrePersonnes) {
        this.nombrePersonnes = nombrePersonnes;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getEvenementId() {
        return evenementId;
    }

    public void setEvenementId(int evenementId) {
        this.evenementId = evenementId;
    }
}
