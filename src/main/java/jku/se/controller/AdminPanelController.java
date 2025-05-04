package jku.se.controller;


import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import jku.se.DashboardUser;

import static jku.se.Login.getCurrentUsername;

public class AdminPanelController extends Controller{

    @FXML
    private Button messages;

    @FXML
    private void openUserOverviewDashboard(ActionEvent event) throws IOException {
        switchScene(event, "userOverviewDashboard.fxml");
    }

    @FXML
    private void openRequestManagement(ActionEvent event) throws IOException {
        switchScene(event, "requestManagement.fxml");
    }

    @FXML
    private void openStatistics(ActionEvent event) throws IOException {
        switchScene(event, "statistics.fxml");
    }

    @FXML
    public void openRefund(ActionEvent event) throws IOException {
        switchScene(event, "refund.fxml");
    }

    @FXML
    private void handleBack (javafx.event.ActionEvent event) throws IOException {
        switchScene(event, "dashboardAdmin.fxml");

    }

    public void openExportData(ActionEvent event) throws IOException {
        switchScene(event, "exportData.fxml");
    }

    public void openMessagesAnomaly(ActionEvent event) throws IOException {
        switchScene(event, "messageAnomalyDashboard.fxml");
    }

    public void checkForNewMessages() throws SQLException {

        boolean hasNewMessages = MessageAnomalyController.hasNewMessages();

        if (hasNewMessages) {
            // Setze den Button auf rot, wenn neue Nachrichten vorhanden sind
            messages.setStyle("-fx-background-color: #7734db;");
        } else {
            // Setze den Button auf die normale Farbe zurück, wenn keine neuen Nachrichten vorhanden sind
            messages.setStyle("-fx-background-color: lightgray;");
        }
    }

    @FXML
    private void openMessagesAnomalies(javafx.event.ActionEvent event) throws IOException, SQLException {//AI
        try {
            String username = getCurrentUsername(); // Holen des Benutzernamens
            MessageAnomalyController.markMessageAsRead(); // Markiere alle neuen Nachrichten als gelesen

            // Lade die Nachrichten-UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/messageAnomalyDashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) messages.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            // Setze den Button zurück auf die normale Farbe
            messages.setStyle("-fx-background-color: lightgray;");
        } catch (SQLException e) {
            showError("Error", "Failed to mark messages as read: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        try {
            checkForNewMessages();
        } catch (SQLException e) {
            showError("Database Error", "Failed to check for new messages: " + e.getMessage());
        }
    }
}
