package com.example.agritrace.Services;

import com.example.agritrace.utils.MyDb;
import com.example.agritrace.Models.Report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportService {

    public void addReport(Report report) {
        String query = "INSERT INTO report (blog_id, description) VALUES (?, ?)";

        try (Connection conn = MyDb.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, report.getBlogId());
            ps.setString(2, report.getDescription());
            ps.executeUpdate();
            System.out.println("✅ Report ajouté avec succès.");

        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout du report : " + e.getMessage());
        }
    }

    public List<Report> getReportsForBlog(int blogId) {
        List<Report> reports = new ArrayList<>();
        String query = "SELECT * FROM report WHERE blog_id = ?";

        try (Connection conn = MyDb.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, blogId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Report report = new Report();
                report.setId(rs.getInt("id"));
                report.setBlogId(rs.getInt("blog_id"));
                report.setDescription(rs.getString("description"));
                reports.add(report);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des reports pour le blog ID " + blogId + " : " + e.getMessage());
        }

        return reports;
    }

    public void updateReport(Report report) throws SQLException {
        String query = "UPDATE report SET description = ? WHERE id = ?";
        try (Connection conn = MyDb.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, report.getDescription());

        ps.setInt(2, report.getId());
        ps.executeUpdate();
    }
    }


}
