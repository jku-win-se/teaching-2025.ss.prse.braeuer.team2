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

public class LoginController extends Controller{
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

        if (username.equals("user@gmail.com") && password.equals("user")) {
            lbl_message.setText("");
            switchToDashboardUser();
        } else if (username.equals("admin@gmail.com") && password.equals("admin")) {
            lbl_message.setText("");
            switchToDashboardAdmin();
        } else if (username.equals("") && password.equals("")) {
            lbl_message.setText("");
            switchToDashboardAdmin();
        } else {
            lbl_message.setText("Benutzername oder Passwort falsch!");
            lbl_message.setStyle("-fx-text-fill: red;");
        }
    }

    private void switchToDashboardUser() throws IOException {
        URL fxmlLocation = getClass().getResource("/dashboardUser.fxml");

        System.out.println("FXML-Pfad: " + fxmlLocation);

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) btn_login.getScene().getWindow();
        stage.setScene(scene);
    }

    private void switchToDashboardAdmin() throws IOException {
        URL fxmlLocation = getClass().getResource("/dashboardAdmin.fxml");
        System.out.println("FXML-Pfad: " + fxmlLocation);

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) btn_login.getScene().getWindow();
        stage.setScene(scene);
    }
}
