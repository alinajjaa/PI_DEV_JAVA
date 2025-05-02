package com.example.AgritraceNes.Services;

import com.example.AgritraceNes.Models.Evenement;
import com.example.AgritraceNes.utils.MyDb;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EvenementService {

    private final Connection connection;

    public EvenementService() {
        this.connection = MyDb.getInstance().getConnection();
    }

    public List<Evenement> getAllEvenements() {
        List<Evenement> evenements = new ArrayList<>();
        String sql = "SELECT * FROM evenements";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Evenement e = new Evenement();
                e.setId(rs.getInt("id"));
                e.setCategorieId(rs.getInt("categorie_id"));
                e.setNom(rs.getString("nom"));
                e.setDescription(rs.getString("description"));
                e.setDateDebut(rs.getDate("date_debut").toLocalDate());
                e.setDateFin(rs.getDate("date_fin").toLocalDate());
                e.setPrix(rs.getDouble("prix"));
                e.setLieu(rs.getString("lieu"));
                e.setPlacesDisponibles(rs.getInt("places_disponibles"));
                e.setImage(rs.getString("image"));
                evenements.add(e);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des événements : " + e.getMessage());
        }

        return evenements;
    }

    public boolean ajouterEvenement(Evenement evenement) {
        String sql = "INSERT INTO evenements (categorie_id, nom, description, date_debut, date_fin, prix, lieu, places_disponibles, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, evenement.getCategorieId());
            ps.setString(2, evenement.getNom());
            ps.setString(3, evenement.getDescription());
            ps.setDate(4, Date.valueOf(evenement.getDateDebut()));
            ps.setDate(5, Date.valueOf(evenement.getDateFin()));
            ps.setDouble(6, evenement.getPrix());
            ps.setString(7, evenement.getLieu());
            ps.setInt(8, evenement.getPlacesDisponibles());
            ps.setString(9, evenement.getImage());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout : " + e.getMessage());
            return false;
        }
    }

    public boolean modifierEvenement(Evenement evenement) {
        String sql = "UPDATE evenements SET "
                + "categorie_id = ?, "
                + "nom = ?, "
                + "description = ?, "
                + "date_debut = ?, "
                + "date_fin = ?, "
                + "prix = ?, "
                + "lieu = ?, "
                + "places_disponibles = ?, "
                + "image = ? "
                + "WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, evenement.getCategorieId());
            ps.setString(2, evenement.getNom());
            ps.setString(3, evenement.getDescription());
            ps.setDate(4, Date.valueOf(evenement.getDateDebut()));
            ps.setDate(5, Date.valueOf(evenement.getDateFin()));
            ps.setDouble(6, evenement.getPrix());
            ps.setString(7, evenement.getLieu());
            ps.setInt(8, evenement.getPlacesDisponibles());
            ps.setString(9, evenement.getImage());
            ps.setInt(10, evenement.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerEvenement(int id) {
        String sql = "DELETE FROM evenements WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
            return false;
        }
    }

    public List<Evenement> rechercherEvenements(String recherche) {
        List<Evenement> evenements = new ArrayList<>();
        String sql = "SELECT * FROM evenements WHERE nom LIKE ? OR lieu LIKE ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + recherche + "%");
            ps.setString(2, "%" + recherche + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Evenement e = new Evenement();
                e.setId(rs.getInt("id"));
                e.setCategorieId(rs.getInt("categorie_id"));
                e.setNom(rs.getString("nom"));
                e.setDescription(rs.getString("description"));
                e.setDateDebut(rs.getDate("date_debut").toLocalDate());
                e.setDateFin(rs.getDate("date_fin").toLocalDate());
                e.setPrix(rs.getDouble("prix"));
                e.setLieu(rs.getString("lieu"));
                e.setPlacesDisponibles(rs.getInt("places_disponibles"));
                e.setImage(rs.getString("image"));
                evenements.add(e);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche : " + e.getMessage());
        }

        return evenements;
    }

    public List<Evenement> filtrerEvenementsAvances(String nom, String lieu, Double prixMin, Double prixMax, Integer categorieId, LocalDate dateDebut, LocalDate dateFin) {
        List<Evenement> evenements = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM evenements WHERE 1=1");

        if (!nom.isEmpty()) sql.append(" AND nom LIKE ?");
        if (!lieu.isEmpty()) sql.append(" AND lieu LIKE ?");
        if (prixMin != null) sql.append(" AND prix >= ?");
        if (prixMax != null) sql.append(" AND prix <= ?");
        if (categorieId != null) sql.append(" AND categorie_id = ?");
        if (dateDebut != null) sql.append(" AND date_debut >= ?");
        if (dateFin != null) sql.append(" AND date_fin <= ?");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int index = 1;
            if (!nom.isEmpty()) ps.setString(index++, "%" + nom + "%");
            if (!lieu.isEmpty()) ps.setString(index++, "%" + lieu + "%");
            if (prixMin != null) ps.setDouble(index++, prixMin);
            if (prixMax != null) ps.setDouble(index++, prixMax);
            if (categorieId != null) ps.setInt(index++, categorieId);
            if (dateDebut != null) ps.setDate(index++, Date.valueOf(dateDebut));
            if (dateFin != null) ps.setDate(index++, Date.valueOf(dateFin));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Evenement e = new Evenement();
                e.setId(rs.getInt("id"));
                e.setCategorieId(rs.getInt("categorie_id"));
                e.setNom(rs.getString("nom"));
                e.setDescription(rs.getString("description"));
                e.setDateDebut(rs.getDate("date_debut").toLocalDate());
                e.setDateFin(rs.getDate("date_fin").toLocalDate());
                e.setPrix(rs.getDouble("prix"));
                e.setLieu(rs.getString("lieu"));
                e.setPlacesDisponibles(rs.getInt("places_disponibles"));
                e.setImage(rs.getString("image"));
                evenements.add(e);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du filtrage avancé : " + e.getMessage());
        }

        return evenements;
    }

    public List<Evenement> filtrerParStatut(String statut) {
        LocalDateTime now = LocalDateTime.now();
        List<Evenement> evenementsFiltres = getAllEvenements(); // Récupérer tous les événements

        switch (statut) {
            case "À venir":
                // Filtrer les événements à venir (date de début dans le futur)
                evenementsFiltres = evenementsFiltres.stream()
                        .filter(evenement -> evenement.getDateDebut().isAfter(now.toLocalDate()))
                        .collect(Collectors.toList());
                break;
                case "Passés":
                // Filtrer les événements passés (date de début dans le passé)
                evenementsFiltres = evenementsFiltres.stream()
                        .filter(evenement -> evenement.getDateDebut().isBefore(now.toLocalDate()))
                        .collect(Collectors.toList());
                break;
            case "Tous":
                // Pas de filtrage particulier, retourner tous les événements
                break;
        }
        return evenementsFiltres;
    }
    public boolean decrementerPlacesDisponibles(int eventId) {
        String sql = "UPDATE evenements SET places_disponibles = places_disponibles - 1 WHERE id = ? AND places_disponibles > 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la décrémentation des places : " + e.getMessage());
            return false;
        }
    }

    public List<Evenement> getEvenementsSimilaires(int categorieId, int currentEventId) {
        List<Evenement> evenements = new ArrayList<>();
        String sql = "SELECT * FROM evenements WHERE categorie_id = ? AND id != ? AND date_debut > CURRENT_DATE AND places_disponibles > 0 ORDER BY date_debut LIMIT 3";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categorieId);
            ps.setInt(2, currentEventId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Evenement e = new Evenement();
                e.setId(rs.getInt("id"));
                e.setCategorieId(rs.getInt("categorie_id"));
                e.setNom(rs.getString("nom"));
                e.setDescription(rs.getString("description"));
                e.setDateDebut(rs.getDate("date_debut").toLocalDate());
                e.setDateFin(rs.getDate("date_fin").toLocalDate());
                e.setPrix(rs.getDouble("prix"));
                e.setLieu(rs.getString("lieu"));
                e.setPlacesDisponibles(rs.getInt("places_disponibles"));
                e.setImage(rs.getString("image"));
                evenements.add(e);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des événements similaires : " + e.getMessage());
        }

        return evenements;

    }
}
