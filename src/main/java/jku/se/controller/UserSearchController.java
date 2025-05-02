package jku.se.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jku.se.UserManagement;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class UserSearchController extends Controller {

    @FXML
    private TextField searchField;

    @FXML
    private void searchUser() {
        String username = searchField.getText().trim();

        if (username.isEmpty()) {
            showError("Eingabefehler", "Bitte geben Sie einen Benutzernamen ein");
            return;
        }

        try {
            UserManagement.User user = UserManagement.getUser(username);

            if (user != null) {
                // Benutzer gefunden - Details anzeigen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/userSearchResults.fxml"));
                Parent root = loader.load();

                UserSearchResultsController controller = loader.getController();
                controller.loadUserData(username);

                Stage stage = new Stage();
                stage.setTitle("Benutzerdetails: " + username);
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                showInfo("Info", "Benutzer nicht gefunden");
            }
        } catch (SQLException e) {
            showError("Datenbankfehler", "Fehler bei der Benutzersuche: " + e.getMessage());
        } catch (IOException e) {
            showError("Fehler", "Fenster konnte nicht geöffnet werden: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        switchScene(event, "userOverviewDashboard.fxml");
    }
}