package jku.se.Controller;

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

    @FXML private Button btn_login;
    @FXML private TextField txt_username;
    @FXML private PasswordField txt_password;
    @FXML private Label lbl_message;

    @FXML
    private void btn_loginActionPerformed() throws IOException {
        String username = txt_username.getText().trim();
        String password = txt_password.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showErrorMessage("Bitte geben Sie sowohl Benutzernamen als auch Passwort ein!");
            return;
        }

        StringBuilder userRole = new StringBuilder();
        StringBuilder accountStatus = new StringBuilder();

        if (Login.validateLogin(username, password, userRole, accountStatus)) {
            lbl_message.setText("");

            if (Status.BLOCKED.name().equalsIgnoreCase(accountStatus.toString())) {
                showErrorMessage("Ihr Konto wurde gesperrt. Bitte kontaktieren Sie den Administrator.");
                return;
            }

            switch (Role.valueOf(userRole.toString().toUpperCase())) {
                case USER:
                    switchToDashboard("/dashboardUser.fxml");
                    break;
                case ADMIN:
                    switchToDashboard("/dashboardAdmin.fxml");
                    break;
                default:
                    showErrorMessage("Unbekannte Rolle!");
                    break;
            }
        } else {
            if (Status.BLOCKED.name().equalsIgnoreCase(accountStatus.toString())) {
                showErrorMessage("Ihr Konto wurde nach 10 fehlgeschlagenen Versuchen gesperrt.");
            } else {
                showErrorMessage("Benutzername oder Passwort falsch!");
            }
        }
    }

    private void switchToDashboard(String fxmlPath) throws IOException {
        URL fxmlLocation = getClass().getResource(fxmlPath);
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Stage stage = (Stage) btn_login.getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load()));
    }

    private void showErrorMessage(String message) {
        lbl_message.setText(message);
        lbl_message.setStyle("-fx-text-fill: red;");
    }
}