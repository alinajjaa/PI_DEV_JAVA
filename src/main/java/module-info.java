module gestionproduit {
    requires com.alibaba.fastjson2;
    requires com.jfoenix;
    requires itextpdf;
    requires jakarta.mail;
    requires java.desktop;
    requires java.net.http;
    requires java.sql;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires poi;
    requires poi.ooxml;
    requires stripe.java;
    exports tn.esprit.test;
    exports tn.esprit.controllers;
    opens tn.esprit.controllers to javafx.fxml;  // Open the controller package to javafx.fxml


}