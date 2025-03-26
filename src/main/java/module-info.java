module com.example.lunchify {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.dlsc.formsfx;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires tess4j;
    requires jollyday;
    requires java.desktop;

    exports jku.se; // Alle Klassen im jku.se Package exportieren
    opens jku.se.Controller to javafx.fxml; // Controller für javafx.fxml öffnen
}







