package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import jku.se.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static jku.se.Controller.RequestManagementController.showAlert;

public class AddUserController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private ComboBox<String> statusComboBox;

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("ADMIN", "USER", "MANAGER");
        statusComboBox.getItems().addAll("ACTIVE", "INACTIVE");
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try (Connection conn = Database.getConnection()) {
                String query = "INSERT INTO accounts (username, email, password, role, status) " +
                        "VALUES (?, ?, ?, ?::user_role, ?::account_status)";
                PreparedStatement stmt = conn.prepareStatement(query);

                stmt.setString(1, usernameField.getText());
                stmt.setString(2, emailField.getText());
                stmt.setString(3, passwordField.getText());
                stmt.setString(4, roleComboBox.getValue());
                stmt.setString(5, statusComboBox.getValue());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    // Close the dialog
                    usernameField.getScene().getWindow().hide();
                }
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to add user: " + e.getMessage());
            }
        }
    }

    private boolean validateInput() {
        // Implement validation logic
        return true;
    }

    @FXML
    private void handleCancel() {
        usernameField.getScene().getWindow().hide();
    }


}