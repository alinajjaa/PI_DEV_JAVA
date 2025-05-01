package com.example.agritrace.Models;
import java.time.LocalDateTime;


public class Comment {
    private int id;
    private int blogId;
    private Integer userId;
    private String content;
    private int reports;
    private boolean isAnonymous;
    private String gifUrl;

    // Constructeurs
    public Comment() {}

    public Comment(int blogId, Integer userId, String content, boolean isAnonymous, String gifUrl) {
        this.blogId = blogId;
        this.userId = userId;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.gifUrl = gifUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBlogId() {
        return blogId;
    }

    public void setBlogId(int blogId) {
        this.blogId = blogId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReports() {
        return reports;
    }

    public void setReports(int reports) {
        this.reports = reports;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public String getGifUrl() {
        return gifUrl;
    }

    public void setGifUrl(String gifUrl) {
        this.gifUrl = gifUrl;
    }


}