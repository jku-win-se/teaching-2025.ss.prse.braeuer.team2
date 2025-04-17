module com.example.lunchify {
    requires javafx.fxml;
    requires com.dlsc.formsfx;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires tess4j;
    requires jollyday;
    requires org.controlsfx.controls;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports jku.se; // Alle Klassen im jku.se Package exportieren
    opens jku.se.Controller to javafx.fxml; // Controller für javafx.fxml öffnen

    exports jku.se.Controller;
    opens jku.se to javafx.fxml, org.controlsfx.controls;  // Erlaubt den Zugriff auf dieses Paket von anderen Modulen

}







