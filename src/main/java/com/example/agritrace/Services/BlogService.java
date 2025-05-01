package com.example.agritrace.Services;

import com.example.agritrace.Interfaces.IBlogService;
import com.example.agritrace.Models.BlogPost;
import com.example.agritrace.utils.MyDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlogService implements IBlogService {

    private Connection getConnection() throws SQLException {
        Connection conn = MyDb.getInstance().getConnection();
        if (conn == null || conn.isClosed()) {
            throw new SQLException("La connexion à la base de données est fermée ou nulle.");
        }
        return conn;
    }

    @Override
    public void ajouterBlog(BlogPost blog) throws SQLException {
        String sql = "INSERT INTO blog (user_id, title, content, likes, reports, created_at, gif_url, summary) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = getConnection().prepareStatement(sql)) {
            pst.setInt(1, blog.getUserId());
            pst.setString(2, blog.getTitle());
            pst.setString(3, blog.getContent());
            pst.setInt(4, 0);
            pst.setInt(5, 0);
            pst.setTimestamp(6, Timestamp.valueOf(blog.getCreatedAt()));
            pst.setString(7, blog.getGifUrl());
            pst.setString(8, blog.getSummary());
            pst.executeUpdate();
        }
    }

    @Override
    public List<BlogPost> afficherBlogs() throws SQLException {
        List<BlogPost> blogs = new ArrayList<>();
        String sql = "SELECT * FROM blog ORDER BY created_at DESC";

        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                BlogPost blog = new BlogPost();
                blog.setId(rs.getInt("id"));
                blog.setUserId(rs.getInt("user_id"));
                blog.setTitle(rs.getString("title"));
                blog.setContent(rs.getString("content"));
                blog.setLikes(rs.getInt("likes"));
                blog.setReports(rs.getInt("reports"));
                blog.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                blog.setGifUrl(rs.getString("gif_url"));
                blog.setSummary(rs.getString("summary"));
                blogs.add(blog);
            }
        }

        return blogs;
    }

    public void modifierBlog(BlogPost blog) throws SQLException {
        String sql = "UPDATE blog SET title = ?, content = ? WHERE id = ?";
        try (PreparedStatement pst = getConnection().prepareStatement(sql)) {
            pst.setString(1, blog.getTitle());
            pst.setString(2, blog.getContent());
            pst.setInt(3, blog.getId());
            pst.executeUpdate();
        }
    }

    public void supprimerBlog(int id) throws SQLException {
        String sql = "DELETE FROM blog WHERE id = ?";
        try (PreparedStatement pst = getConnection().prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    public void likeBlog(int id) throws SQLException {
        String sql = "UPDATE blog SET likes = likes + 1 WHERE id = ?";
        try (PreparedStatement pst = getConnection().prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    public void reportBlog(int id) throws SQLException {
        String sql = "UPDATE blog SET reports = reports + 1 WHERE id = ?";
        try (PreparedStatement pst = getConnection().prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    public int getBlogsWithCommentsCount() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT blog_id) FROM comment";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }


}
