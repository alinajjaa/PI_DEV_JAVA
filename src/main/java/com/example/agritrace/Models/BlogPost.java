package com.example.agritrace.Models;

import java.time.LocalDateTime;

public class BlogPost {
    private int id;
    private int userId;
    private String title;
    private String content;
    private int likes;
    private int reports;
    private LocalDateTime createdAt;
    private String gifUrl;
    private String summary;

    // Constructeurs
    public BlogPost() {}

    public BlogPost(int id, int userId, String title, String content, int likes, int reports, LocalDateTime createdAt, String gifUrl, String summary) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.likes = likes;
        this.reports = reports;
        this.createdAt = createdAt;
        this.gifUrl = gifUrl;
        this.summary = summary;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public int getReports() { return reports; }
    public void setReports(int reports) { this.reports = reports; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getGifUrl() { return gifUrl; }
    public void setGifUrl(String gifUrl) { this.gifUrl = gifUrl; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    @Override
    public String toString() {
        return "BlogPost{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", likes=" + likes +
                ", reports=" + reports +
                ", createdAt=" + createdAt +
                ", gifUrl='" + gifUrl + '\'' +
                ", summary='" + summary + '\'' +
                '}';
    }
}
