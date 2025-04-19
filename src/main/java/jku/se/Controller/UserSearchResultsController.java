package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jku.se.Database;
import jku.se.Role;
import jku.se.Status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Optional;

public class UserSearchResultsController extends Controller {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private Label usernameLabel;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField failedAttemptsField;
    @FXML private Label createdAtLabel;

    private String originalUsername;

    @FXML
    public void initialize() {
        // Initialize dropdowns
        roleComboBox.getItems().clear();
        roleComboBox.getItems().addAll(Role.ADMIN.name(), Role.USER.name());

        statusComboBox.getItems().clear();
        statusComboBox.getItems().addAll(Status.ACTIVE.name(), Status.BLOCKED.name());
    }

    public void loadUserData(String username) {
        this.originalUsername = username;
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT first_name, last_name, username, email, " +
                    "role::text, status::text, failed_attempts, \"createdAt\" " +
                    "FROM accounts WHERE username = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                firstNameField.setText(rs.getString("first_name"));
                lastNameField.setText(rs.getString("last_name"));
                usernameLabel.setText(rs.getString("username"));
                emailField.setText(rs.getString("email"));

                // Set dropdown values
                roleComboBox.setValue(rs.getString("role"));
                statusComboBox.setValue(rs.getString("status"));

                failedAttemptsField.setText(String.valueOf(rs.getInt("failed_attempts")));

                // Format date
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                createdAtLabel.setText(dateFormat.format(rs.getTimestamp("createdAt")));
            } else {
                showAlert(AlertType.WARNING, "Not Found", "User not found");
            }
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Error loading data: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        try (Connection conn = Database.getConnection()) {
            String query = "UPDATE accounts SET " +
                    "first_name = ?, last_name = ?, email = ?, " +
                    "role = ?::account_type, status = ?::account_status, " +
                    "failed_attempts = ? WHERE username = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, firstNameField.getText());
            stmt.setString(2, lastNameField.getText());
            stmt.setString(3, emailField.getText());
            stmt.setString(4, roleComboBox.getValue());
            stmt.setString(5, statusComboBox.getValue());
            stmt.setInt(6, Integer.parseInt(failedAttemptsField.getText()));
            stmt.setString(7, originalUsername);

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                showAlert(AlertType.INFORMATION, "Success", "Data saved successfully");
                closeWindow();
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Error", "Failed attempts must be a number");
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Save failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Delete User");
        confirmation.setHeaderText("Are you sure you want to delete this user?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = Database.getConnection()) {
                String query = "DELETE FROM accounts WHERE username = ?";

                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, originalUsername);

                int deleted = stmt.executeUpdate();
                if (deleted > 0) {
                    showAlert(AlertType.INFORMATION, "Success", "User deleted successfully");
                    closeWindow();
                }
            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Database Error", "Delete failed: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClose() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) usernameLabel.getScene().getWindow()).close();
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}