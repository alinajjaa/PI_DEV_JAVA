package com.example.agritrace.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDb {
    private final String URL = "jdbc:mysql://localhost:3306/agritrace";
    private final String USER = "root";
    private final String PASSWORD = "";

    private Connection connection;
    private static MyDb instance;

    private MyDb() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion à la base réussie !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur de connexion : " + e.getMessage());
        }
    }

    public static MyDb getInstance() {
        if (instance == null) {
            instance = new MyDb();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("♻️ Connexion recréée automatiquement.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la reconnexion : " + e.getMessage());
        }
        return connection;
    }
}
