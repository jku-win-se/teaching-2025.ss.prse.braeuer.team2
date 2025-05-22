package jku.se.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jku.se.UserManagement;

public class UserChangePasswordController extends Controller {

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label usernameLabel;

    private String username;

    public void loadUserData(String username) {
        this.username = username;
        usernameLabel.setText("Change password" );
    }

    @FXML
    private void handleChangePassword() {
        String currentPasswordInput = currentPasswordField.getText();
        String newPasswordInput = newPasswordField.getText();
        String confirmPasswordInput = confirmPasswordField.getText();

        if (currentPasswordInput.isEmpty() || newPasswordInput.isEmpty() || confirmPasswordInput.isEmpty()) {
            showError("Fehler", "Bitte alle Felder ausfüllen.");
            return;
        }

        if (!newPasswordInput.equals(confirmPasswordInput)) {
            showError("Fehler", "Die neuen Passwörter stimmen nicht überein.");
            return;
        }

        try {
            var user = UserManagement.getUser(username);
            if (user == null) {
                showError("Fehler", "Benutzer nicht gefunden.");
                return;
            }

            if (!user.password.equals(currentPasswordInput)) {
                showError("Fehler", "Aktuelles Passwort ist falsch.");
                return;
            }

            user.password = newPasswordInput;
            boolean success = UserManagement.updateUser(user);

            if (success) {
                showSuccess("Erfolg", "Passwort wurde erfolgreich geändert.");

                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();

                closeWindow();
            } else {
                showError("Fehler", "Passwort konnte nicht geändert werden.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Fehler", "Datenbankfehler: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) currentPasswordField.getScene().getWindow();
        stage.close();
    }

}