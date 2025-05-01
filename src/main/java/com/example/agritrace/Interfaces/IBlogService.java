package com.example.agritrace.Interfaces;

import com.example.agritrace.Models.BlogPost;

import java.sql.SQLException;
import java.util.List;

public interface IBlogService {
    void ajouterBlog(BlogPost blog) throws SQLException;
    List<BlogPost> afficherBlogs() throws SQLException;
}
