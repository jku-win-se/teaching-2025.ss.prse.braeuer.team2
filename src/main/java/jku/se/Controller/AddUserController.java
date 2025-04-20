package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import jku.se.UserManagement;
import jku.se.Role;

import java.io.IOException;
import java.sql.SQLException;

import static jku.se.Controller.RequestManagementController.showAlert;

public class AddUserController extends Controller {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;

    @FXML
    public void initialize() {
        // Initialize role dropdown with only ADMIN and USER
        roleComboBox.getItems().clear();
        roleComboBox.getItems().addAll(Role.ADMIN.name(), Role.USER.name());
        roleComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleSave(ActionEvent event) {  // ActionEvent als Parameter hinzugefügt
        try {
            // Input validation
            if (!validateInput()) {
                return;
            }

            boolean success = UserManagement.createUser(
                    firstNameField.getText(),
                    lastNameField.getText(),
                    usernameField.getText(),
                    emailField.getText(),
                    passwordField.getText(),
                    roleComboBox.getValue()
            );

            if (success) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Erfolg");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Benutzer wurde angelegt");
                successAlert.showAndWait();

                // Das übergebene Event verwenden
                switchScene(event, "userOverviewDashboard.fxml");
            }
        } catch (SQLException e) {
            showAlert("Datenbankfehler", "Fehler beim Anlegen: " + e.getMessage());
        } catch (IOException e) {
            showAlert("Navigation Error", "Could not return to user overview");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            switchScene(event, "userOverviewDashboard.fxml");
        } catch (IOException e) {
            showAlert("Navigation Error", "Could not return to user overview");
        }
    }

    private boolean validateInput() {
        // Check for empty fields
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
                usernameField.getText().isEmpty() || emailField.getText().isEmpty() ||
                passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {

            showAlert("Validation Error", "All fields are required");
            return false;
        }

        // Check password match
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showAlert("Validation Error", "Passwords do not match");
            return false;
        }

        // Check password length
        if (passwordField.getText().length() < 8) {
            showAlert("Validation Error", "Password must be at least 8 characters");
            return false;
        }

        return true;
    }
}