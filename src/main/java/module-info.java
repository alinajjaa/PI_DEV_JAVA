module com.example.AgritraceNes {
    requires javafx.controls;
    requires javafx.fxml;
    requires itextpdf; // Ajoutez cette ligne
    requires org.controlsfx.controls;
    requires java.desktop; // Ajoutez cette ligne
    requires com.github.librepdf.openpdf; // ✅ Ajoute cette ligne
    requires commons.math3; // ➡ pour utiliser Apache Commons Math
    requires org.json;
    requires java.sql;
    requires java.net.http;

    requires javafx.web;


    opens com.example.AgritraceNes to javafx.fxml;
    opens com.example.AgritraceNes.Controllers to javafx.fxml; // Add this line
    opens com.example.AgritraceNes.Models to javafx.base;
    exports com.example.AgritraceNes;
    exports com.example.AgritraceNes.Controllers;
    exports com.example.AgritraceNes.Models;
    exports com.example.AgritraceNes.utils;
}
