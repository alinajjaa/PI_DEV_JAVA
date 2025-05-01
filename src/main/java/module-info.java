module com.example.agritrace {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;



    requires org.controlsfx.controls;
    requires javafx.web;
    requires java.desktop;
    requires restfb;
    requires facebook4j.core;

    opens com.example.agritrace to javafx.fxml;
    opens com.example.agritrace.Controllers to javafx.fxml; // Add this line
    opens com.example.agritrace.Models to javafx.base;
    exports com.example.agritrace;
    exports com.example.agritrace.Controllers;
}
