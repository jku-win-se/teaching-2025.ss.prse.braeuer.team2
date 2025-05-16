package jku.se.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jku.se.UserManagement;
import jku.se.Role;
import jku.se.Status;

import java.sql.SQLException;
import java.util.Optional;

public class UserSearchResultsController extends Controller {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private Label usernameLabel;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField failedAttemptsField;
    @FXML private Label createdAtLabel;
    @FXML private TextField passwordTextField;
    @FXML private CheckBox showPasswordCheckBox;

    private String currentUsername;

    @FXML
    public void initialize() {
        roleComboBox.getItems().clear();
        roleComboBox.getItems().addAll(Role.USER.name(), Role.ADMIN.name());

        statusComboBox.getItems().clear();
        statusComboBox.getItems().addAll(Status.ACTIVE.name(), Status.BLOCKED.name());

        passwordTextField.managedProperty().bind(showPasswordCheckBox.selectedProperty());
        passwordTextField.visibleProperty().bind(showPasswordCheckBox.selectedProperty());

        passwordField.managedProperty().bind(showPasswordCheckBox.selectedProperty().not());
        passwordField.visibleProperty().bind(showPasswordCheckBox.selectedProperty().not());

        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
    }


    public void loadUserData(String username) {
        this.currentUsername = username;
        try {
            UserManagement.User user = UserManagement.getUser(username);
            if (user != null) {
                firstNameField.setText(user.firstName);
                lastNameField.setText(user.lastName);
                usernameLabel.setText(user.username);
                emailField.setText(user.email);
                passwordField.setText(user.password);
                roleComboBox.setValue(user.role);
                statusComboBox.setValue(user.status);
                failedAttemptsField.setText(String.valueOf(user.failedAttempts));
                createdAtLabel.setText(user.createdAt);
            } else {
                showError("Error", "User not found");
            }
        } catch (SQLException e) {
            showError("Database Error", "Error loading user data: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        try {
            UserManagement.User user = new UserManagement.User();
            user.firstName = firstNameField.getText();
            user.lastName = lastNameField.getText();
            user.username = usernameLabel.getText();
            user.email = emailField.getText();
            user.password = passwordField.getText();
            user.role = roleComboBox.getValue();
            user.status = statusComboBox.getValue();
            user.failedAttempts = Integer.parseInt(failedAttemptsField.getText());

            boolean success = UserManagement.updateUser(user);

            if (success) {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("User updated successfully");
                successAlert.showAndWait();
                closeWindow();
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to update user: " + e.getMessage());
        } catch (NumberFormatException e) {
            showError("Input Error", "Failed attempts must be a number");
        }
    }

    @FXML
    private void handleDelete() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete User");
        confirmation.setContentText("Are you sure you want to delete this user?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (UserManagement.deleteUser(currentUsername)) {
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText(null);
                    success.setContentText("User deleted successfully");
                    success.showAndWait();
                    closeWindow();
                }
            } catch (SQLException e) {
                showError("Database Error", "Failed to delete user: " + e.getMessage());
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
}