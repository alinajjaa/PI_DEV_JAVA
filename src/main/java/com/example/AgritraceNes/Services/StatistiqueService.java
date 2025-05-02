package com.example.AgritraceNes.Services;

import org.apache.commons.math3.stat.StatUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import com.example.AgritraceNes.utils.MyDb;

public class StatistiqueService {
    private final Connection connection;

    public StatistiqueService() {
        connection = MyDb.getInstance().getConnection();
    }

    // === Méthodes de base (SQL) ===
    public Map<String, Integer> getNombreParticipantsParEvenement() {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT e.nom, SUM(p.nombre_personnes) AS total_participants " +
                "FROM participation p JOIN evenements e ON p.evenement_id = e.id " +
                "GROUP BY e.nom";

        executeQuery(stats, query, "nom", "total_participants");
        return stats;
    }

    public Map<String, Integer> getNombreEvenementsParLieu() {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT lieu, COUNT(*) AS nombre_evenements FROM evenements GROUP BY lieu";
        executeQuery(stats, query, "lieu", "nombre_evenements");
        return stats;
    }

    public Map<String, Integer> getTopEvenementsParParticipants() {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT e.nom, SUM(p.nombre_personnes) AS total_participants " +
                "FROM participation p JOIN evenements e ON p.evenement_id = e.id " +
                "GROUP BY e.nom ORDER BY total_participants DESC LIMIT 5";
        executeQuery(stats, query, "nom", "total_participants");
        return stats;
    }

    // === Méthodes avancées (Stats) ===
    public Map<String, Double> getStatistiquesDescriptives() {
        double[] participants = getNombreParticipantsParEvenement().values()
                .stream().mapToDouble(Integer::doubleValue).toArray();

        Map<String, Double> stats = new LinkedHashMap<>();
        if (participants.length > 0) {
            stats.put("Moyenne", StatUtils.mean(participants));
            stats.put("Médiane", StatUtils.percentile(participants, 50));
            stats.put("Écart-type", Math.sqrt(StatUtils.variance(participants)));
            stats.put("Maximum", StatUtils.max(participants));
            stats.put("Minimum", StatUtils.min(participants));
        }
        return stats;
    }





    // === Méthodes privées ===
    private void executeQuery(Map<String, Integer> map, String query, String keyCol, String valueCol) {
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString(keyCol), rs.getInt(valueCol));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL: " + e.getMessage());
        }
    }

    private Map<String, List<Double>> getParticipantsParLieu() {
        Map<String, List<Double>> data = new HashMap<>();
        String query = "SELECT e.lieu, p.nombre_personnes " +
                "FROM participation p JOIN evenements e ON p.evenement_id = e.id";

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String lieu = rs.getString("lieu");
                data.computeIfAbsent(lieu, k -> new ArrayList<>())
                        .add(rs.getDouble("nombre_personnes"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des données par lieu: " + e.getMessage());
        }
        return data;
    }
}
