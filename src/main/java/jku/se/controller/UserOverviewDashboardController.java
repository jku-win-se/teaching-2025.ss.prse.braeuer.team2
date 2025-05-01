package jku.se.controller;

import javafx.fxml.FXML;

import java.io.IOException;

public class UserOverviewDashboardController extends Controller {
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
