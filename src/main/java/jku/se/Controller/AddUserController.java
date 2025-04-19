package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import jku.se.Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        roleComboBox.getItems().addAll("USER", "ADMIN");
        roleComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (validateInput()) {
            try (Connection conn = Database.getConnection()) {
                String query = "INSERT INTO accounts (first_name, last_name, username, email, password, role, status, failed_attempts) " +
                        "VALUES (?, ?, ?, ?, ?, ?::account_type, 'ACTIVE'::account_status, 0)";

                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, firstNameField.getText());
                stmt.setString(2, lastNameField.getText());
                stmt.setString(3, usernameField.getText());
                stmt.setString(4, emailField.getText());
                stmt.setString(5, passwordField.getText());
                stmt.setString(6, roleComboBox.getValue());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User created successfully");
                    switchScene(event, "jku/se/View/userOverviewDashboard.fxml");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create user: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            switchScene(event, "userOverviewDashboard.fxml");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not return to user overview");
        }
    }

    private boolean validateInput() {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
                usernameField.getText().isEmpty() || emailField.getText().isEmpty() ||
                passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required");
            return false;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Passwords do not match");
            return false;
        }

        if (passwordField.getText().length() < 8) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Password must be at least 8 characters");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}