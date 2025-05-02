package tn.esprit.service;

import tn.esprit.entities.Comment;
import tn.esprit.util.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentService {

    private final Connection connection;

    public CommentService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    public void addComment(Comment comment) {
        String sql = "INSERT INTO commentsproduct (content, user_id, produit_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, comment.getContent());
            ps.setInt(2, comment.getUserId());
            ps.setInt(3, comment.getProduitId());
            ps.executeUpdate();
            System.out.println("✅ Commentaire ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    public List<Comment> getCommentsByProductId(int produitId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM commentsproduct WHERE produit_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, produitId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Comment comment = new Comment(
                        rs.getInt("id_comment"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at"),
                        rs.getInt("user_id"),
                        rs.getInt("produit_id")
                );
                comments.add(comment);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération : " + e.getMessage());
        }
        return comments;
    }
}
