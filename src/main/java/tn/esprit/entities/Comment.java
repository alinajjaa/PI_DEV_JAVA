package tn.esprit.entities;

import java.sql.Timestamp;

public class Comment {
    private int id;
    private String content;
    private Timestamp createdAt;
    private int userId;
    private int produitId;

    public Comment(int id, String content, Timestamp createdAt, int userId, int produitId) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.produitId = produitId;
    }

    public Comment(String content, int userId, int produitId) {
        this.content = content;
        this.userId = userId;
        this.produitId = produitId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProduitId() {
        return produitId;
    }

    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
