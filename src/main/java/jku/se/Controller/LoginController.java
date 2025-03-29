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
import jku.se.Database;

public class LoginController extends Controller {

    @FXML
    private Button btn_login;

    @FXML
    private TextField txt_username;

    @FXML
    private PasswordField txt_password;

    @FXML
    private Label lbl_message;

    @FXML
    private void btn_loginActionPerformed() throws IOException {
        String username = txt_username.getText();
        String password = txt_password.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lbl_message.setText("Bitte geben Sie sowohl Benutzernamen als auch Passwort ein!");
            lbl_message.setStyle("-fx-text-fill: red;");
            return;
        }

        StringBuilder userRole = new StringBuilder();

        // Login-Validierung über Database-Klasse
        if (Database.validateLogin(username, password, userRole)) {
            lbl_message.setText("");
            switch (userRole.toString()) {
                case "user":
                    switchToDashboardUser();
                    break;
                case "admin":
                    switchToDashboardAdmin();
                    break;
                default:
                    lbl_message.setText("Unbekannte Rolle!");
                    lbl_message.setStyle("-fx-text-fill: red;");
                    break;
            }
        } else {
            lbl_message.setText("Benutzername oder Passwort falsch!");
            lbl_message.setStyle("-fx-text-fill: red;");
        }
    }

    private void switchToDashboardUser() throws IOException {
        URL fxmlLocation = getClass().getResource("/dashboardUser.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) btn_login.getScene().getWindow();
        stage.setScene(scene);
    }

    private void switchToDashboardAdmin() throws IOException {
        URL fxmlLocation = getClass().getResource("/dashboardAdmin.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) btn_login.getScene().getWindow();
        stage.setScene(scene);
    }
}
