package jku.se.controller;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jku.se.Login;
import jku.se.Role;
import jku.se.Status;

public class LoginController extends Controller {

    @FXML
    private Button loginButton;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button closeButton;

    @FXML
    private void handleLoginAction() throws IOException {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Error", "Please enter email and password!");
            return;
        }

        StringBuilder userRole = new StringBuilder();
        StringBuilder accountStatus = new StringBuilder();

        if (Login.validateLogin(email, password, userRole, accountStatus)) {
            messageLabel.setText("");

            if (Login.getCurrentUserRole() == Role.USER) {
                switchToDashboard("/dashboardUser.fxml");
            } else if (Login.getCurrentUserRole() == Role.ADMIN) {
                switchToDashboard("/dashboardAdmin.fxml");
            }

        } else {
            if (Status.BLOCKED.name().equalsIgnoreCase(accountStatus.toString())) {
                showError("Error", "Account blocked after 3 failed attempts!");
            } else {
                showError("Error","E-Mail oder Password is incorrect!");
            }
        }
    }

    private void switchToDashboard(String fxmlPath) throws IOException {
        URL fxmlLocation = getClass().getResource(fxmlPath);
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load()));
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

}