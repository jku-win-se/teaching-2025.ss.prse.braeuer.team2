package jku.se.Controller;

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

import static jku.se.Controller.RequestManagementController.showAlert;

public class UserSearchController extends Controller {

    @FXML
    private TextField searchField;

    @FXML
    private void searchUser(ActionEvent event) {
        String username = searchField.getText().trim();

        if (username.isEmpty()) {
            showAlert("Eingabefehler", "Bitte geben Sie einen Benutzernamen ein");
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
                // Benutzer nicht gefunden - Bestätigungsdialog anzeigen
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Benutzer nicht gefunden");
                confirmation.setHeaderText("Der Benutzer '" + username + "' existiert nicht.");
                confirmation.setContentText("Möchten Sie einen neuen Benutzer anlegen?");

                Optional<ButtonType> result = confirmation.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Neuen Benutzer anlegen
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/addUser.fxml"));
                    Parent root = loader.load();

                    // Direkten Zugriff auf das TextField im neuen Fenster
                    TextField usernameField = (TextField) root.lookup("#usernameField");
                    if (usernameField != null) {
                        usernameField.setText(username);
                    }

                    Stage stage = new Stage();
                    stage.setTitle("Neuen Benutzer anlegen");
                    stage.setScene(new Scene(root));
                    stage.show();
                }
            }
        } catch (SQLException e) {
            showAlert("Datenbankfehler", "Fehler bei der Benutzersuche: " + e.getMessage());
        } catch (IOException e) {
            showAlert("Fehler", "Fenster konnte nicht geöffnet werden: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        switchScene(event, "userOverviewDashboard.fxml");
    }
}