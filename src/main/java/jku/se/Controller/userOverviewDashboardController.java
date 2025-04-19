package jku.se.Controller;

import javafx.fxml.FXML;

import java.io.IOException;

public class userOverviewDashboardController extends Controller {
    @FXML
    private void handleBack(javafx.event.ActionEvent event) throws IOException {
        switchScene(event, "adminPanel.fxml");
    }

    @FXML
    private void openUserSearch (javafx.event.ActionEvent event) throws IOException {
        switchScene(event, "userSearch.fxml");
    }

    @FXML
    private void openAddUser (javafx.event.ActionEvent event) throws IOException {
        switchScene(event, "addUser.fxml");
    }
}
