package com.example.agritrace.Models;

public class Report {
    private int id;
    private int blogId;
    private String description;

    public Report() {
    }

    public Report(int blogId, String description) {
        this.blogId = blogId;

        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", blogId=" + blogId +
                ", description='" + description + '\'' +
                '}';
    }
}
