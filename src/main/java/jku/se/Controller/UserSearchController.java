package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import java.io.IOException;

public class UserSearchController extends Controller{

    @FXML
    private TextField searchField;

    @FXML
    private void handleBack (javafx.event.ActionEvent event) throws IOException {
        switchScene(event, "adminPanel.fxml");
    }

    @FXML
    private void searchUser(ActionEvent event) {
        String username = searchField.getText();
        if (username.isEmpty()) {
            System.out.println("Bitte einen Nutzernamen eingeben!");
        } else {
            // Datenbankabfrage
        }
    }
}
