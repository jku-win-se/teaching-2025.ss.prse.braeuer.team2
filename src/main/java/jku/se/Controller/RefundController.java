package jku.se.Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import jku.se.Refund;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class RefundController extends Controller{

    @FXML
    private TextField refundRestaurant;

    @FXML
    private TextField refundSupermarket;

    @FXML
    private void handleBack (ActionEvent event) throws IOException {
        switchScene(event, "adminPanel.fxml");
    }

    @FXML
    public void initialize() throws SQLException {
        refundRestaurant.setText("" + Refund.getRefundRestaurant());
        refundSupermarket.setText("" + Refund.getRefundSupermarket());
    }

    public void updateRefunds(ActionEvent actionEvent) throws SQLException {
        LocalDate date = LocalDate.now();
        double refRestaurant = 0.0;
        double refSupermarket = 0.0;

        // Versuche, die Eingabewerte in double umzuwandeln
        try {
            refRestaurant = Double.parseDouble(refundRestaurant.getText());
            refSupermarket = Double.parseDouble(refundSupermarket.getText());

            // Rückerstattungsbeträge aktualisieren
            Refund.setRefundSupermarket(refSupermarket, date);
            Refund.setRefundRestaurant(refRestaurant, date);

            // Erfolgsnachricht anzeigen (Label oder Alert)
            showAlert("Erfolgreich!", "Die Rückerstattungsbeträge wurden erfolgreich aktualisiert.", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            // Fehlerhafte Eingabe behandeln
            showAlert("Fehler", "Bitte geben Sie gültige Zahlen für die Rückerstattungsbeträge ein.", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            // Fehler bei der Datenbankverbindung oder dem SQL-Update behandeln
            showAlert("Datenbankfehler", "Es gab ein Problem beim Speichern der Rückerstattung. Bitte versuchen Sie es später.", Alert.AlertType.ERROR);
        }
    }

    // Methode zur Anzeige eines Alerts
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
