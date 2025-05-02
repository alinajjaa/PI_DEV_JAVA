package com.example.AgritraceNes.Services;

import com.example.AgritraceNes.Models.Participant;
import com.example.AgritraceNes.utils.MyDb;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ParticipantService {
    private final Connection connection;

    public ParticipantService() {
        connection = MyDb.getInstance().getConnection();
    }

    public boolean addParticipant(Participant participant) {
        String query = "INSERT INTO participation (date_participation, nombre_personnes, client_id, evenement_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setObject(1, participant.getDateParticipation());
            ps.setInt(2, participant.getNombrePersonnes());
            ps.setInt(3, participant.getClientId());
            ps.setInt(4, participant.getEvenementId());
            ps.executeUpdate();
            System.out.println("✅ Participation ajoutée !");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de l'ajout de la participation.");
            return false;
        }
    }

    public List<Participant> getAllParticipants() {
        List<Participant> participants = new ArrayList<>();
        String query = "SELECT * FROM participation";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Participant p = new Participant();
                p.setId(rs.getInt("id"));
                p.setDateParticipation(rs.getTimestamp("date_participation").toLocalDateTime());
                p.setNombrePersonnes(rs.getInt("nombre_personnes"));
                p.setClientId(rs.getInt("client_id"));
                p.setEvenementId(rs.getInt("evenement_id"));

                participants.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de la récupération des participants.");
        }

        return participants;
    }

    public boolean deleteParticipant(int participantId) {
        String query = "DELETE FROM participation WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, participantId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("🗑️ Participant supprimé avec succès !");
                return true;
            } else {
                System.out.println("⚠️ Aucun participant trouvé avec l'ID : " + participantId);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de la suppression du participant.");
            return false;
        }
    }

}
