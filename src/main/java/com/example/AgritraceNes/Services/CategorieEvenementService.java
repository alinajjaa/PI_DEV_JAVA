package com.example.AgritraceNes.Services;

import com.example.AgritraceNes.Models.Categorie;
import com.example.AgritraceNes.utils.MyDb;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieEvenementService {
    private Connection conn;

    public CategorieEvenementService() {
        conn = MyDb.getInstance().getConnection();
    }

    public List<Categorie> getAllCategories() {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT * FROM categorieevenements";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                categories.add(new Categorie(id, nom));
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des catégories : " + e.getMessage());
        }

        return categories;
    }

    public boolean ajouterCategorie(String nomCategorie) {
        String sql = "INSERT INTO categorieevenements (nom) VALUES (?)";
        try (Connection conn = MyDb.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomCategorie);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCategorie(int id) {
        String sql = "DELETE FROM categorieevenements WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression de la catégorie : " + e.getMessage());
            return false;
        }
    }

    public boolean updateCategorie(int id, String nouveauNom) {
        String sql = "UPDATE categorieevenements SET nom = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nouveauNom);
            stmt.setInt(2, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour de la catégorie : " + e.getMessage());
            return false;
        }
    }
}
