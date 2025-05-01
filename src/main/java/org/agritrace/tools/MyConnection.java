package org.agritrace.tools;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    private String url="jdbc:mysql://localhost:3306/pidevv";

    private String login="root";

    private String pwd="";


    Connection cnx;

    static MyConnection instance;
    public MyConnection() {
        try {
            cnx= DriverManager.getConnection(url,login,pwd);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getCnx() {
        return cnx;
    }
    public static MyConnection getInstance() {
        if(instance == null){
            instance = new MyConnection();

        }
        return instance;
    }
}

