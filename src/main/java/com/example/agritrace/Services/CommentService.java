package com.example.agritrace.Services;

import com.example.agritrace.Models.Comment;
import com.example.agritrace.utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentService {


    public void addComment(Comment comment) throws SQLException {
        String sql = "INSERT INTO comment (blog_id, user_id, content, reports, is_anonymous, gif_url) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = MyDb.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, comment.getBlogId());
            setNullableInt(pstmt, 2, comment.getUserId());
            pstmt.setString(3, comment.getContent());
            pstmt.setInt(4, 0); // reports initialisé à 0
            pstmt.setBoolean(5, comment.isAnonymous());
            pstmt.setString(6, comment.getGifUrl());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    comment.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<Comment> getCommentsForBlog(int blogId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comment WHERE blog_id = ? ORDER BY id DESC";

        try (Connection conn = MyDb.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, blogId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapResultSetToComment(rs));
                }
            }
        }
        return comments;
    }

    public void deleteComment(int commentId) throws SQLException {
        String sql = "DELETE FROM comment WHERE id = ?";
        try (Connection conn = MyDb.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, commentId);
            pstmt.executeUpdate();
        }
    }

    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getInt("id"));
        comment.setBlogId(rs.getInt("blog_id"));
        comment.setUserId(rs.getInt("user_id"));
        comment.setContent(rs.getString("content"));
        comment.setReports(rs.getInt("reports"));
        comment.setAnonymous(rs.getBoolean("is_anonymous"));
        comment.setGifUrl(rs.getString("gif_url"));
        return comment;
    }

    private void setNullableInt(PreparedStatement pstmt, int index, Integer value) throws SQLException {
        if (value != null) {
            pstmt.setInt(index, value);
        } else {
            pstmt.setNull(index, Types.INTEGER);
        }
    }
    public int getTotalCommentsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM comment";
        try (Connection conn = MyDb.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int countCommentsByBlogId(int blogId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM comment WHERE blog_id = ?";

        try (Connection connection = MyDb.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, blogId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }
    public List<Comment> getAllComments() throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comment ORDER BY id DESC";

        try (Connection conn = MyDb.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                comments.add(mapResultSetToComment(rs));
            }
        }
        return comments;
    }
    public void updateComment(Comment comment) throws SQLException {
        String sql = "UPDATE comment SET content = ? WHERE id = ?";
        try (Connection conn = MyDb.getInstance().getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, comment.getContent());
            statement.setInt(2, comment.getId());
            statement.executeUpdate();
        }
    }

    public int getActiveCommentsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM comment WHERE reports < 5";
        try (Connection conn = MyDb.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    public Map<String, Integer> getCommentStats() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();

        String sql = "SELECT "
                + "COUNT(*) as total, "
                + "COUNT(CASE WHEN reports < 5 THEN 1 END) as active, "
                + "COUNT(DISTINCT blog_id) as blogs_avec_comments "
                + "FROM comment";

        try (Connection conn = MyDb.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.put("total", rs.getInt("total"));
                stats.put("active", rs.getInt("active"));
                stats.put("blogs_avec_comments", rs.getInt("blogs_avec_comments"));
            }
        }
        return stats;
    }



}
