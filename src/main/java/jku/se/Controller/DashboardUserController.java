package jku.se.Controller;

import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import static jku.se.Controller.RequestManagementController.showAlert;
import static jku.se.Login.getCurrentUsername;

public class DashboardUserController extends Controller {

    @FXML
    private Button messages; // Der Button, der die Nachrichten öffnet

    private final MessagesController messageController = new MessagesController(); // Dein Service für Nachrichten

    public DashboardUserController() throws SQLException {
    }

    @FXML
    private void openSubmitBill(ActionEvent event) throws IOException {
        switchScene(event, "submitBill.fxml");
    }

    @FXML
    private void openSubmittedBills(ActionEvent event) throws IOException {
        switchScene(event, "submittedBills.fxml");
    }

    @FXML
    private void openMessages(javafx.event.ActionEvent event) throws IOException, SQLException {//AI
        try {
            String username = getCurrentUsername(); // Holen des Benutzernamens
            messageController.markMessageAsRead(username); // Markiere alle neuen Nachrichten als gelesen

            // Lade die Nachrichten-UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/messageDashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) messages.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            // Setze den Button zurück auf die normale Farbe
            messages.setStyle("-fx-background-color: lightgray;");
        } catch (SQLException e) {
            showAlert("Error", "Failed to mark messages as read: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        try {
            checkForNewMessages();
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to check for new messages: " + e.getMessage());
        }
    }

    // Diese Methode prüft, ob neue Nachrichten vorhanden sind
    public void checkForNewMessages() throws SQLException {
        String username = getCurrentUsername(); // Holen des aktuellen Benutzernamens
        boolean hasNewMessages = messageController.hasNewMessages(username);

        if (hasNewMessages) {
            // Setze den Button auf rot, wenn neue Nachrichten vorhanden sind
            messages.setStyle("-fx-background-color: #3498DB;");
        } else {
            // Setze den Button auf die normale Farbe zurück, wenn keine neuen Nachrichten vorhanden sind
            messages.setStyle("-fx-background-color: lightgray;");
        }
    }
}
