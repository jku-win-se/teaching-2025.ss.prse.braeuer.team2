package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

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
            // Get the FXML file URL properly
            URL fxmlUrl = getClass().getResource("/userSearchResults.fxml");
            if (fxmlUrl == null) {
                throw new IOException("Cannot find FXML file");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Get controller and pass the username
            UserSearchResultsController controller = loader.getController();
            controller.loadUserData(username);

            Stage stage = new Stage();
            stage.setTitle("Benutzerdetails: " + username);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showAlert("Fehler", "Ergebnis-Fenster konnte nicht geöffnet werden: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(javafx.event.ActionEvent event) throws IOException {
        switchScene(event, "userOverviewDashboard.fxml");
    }
}