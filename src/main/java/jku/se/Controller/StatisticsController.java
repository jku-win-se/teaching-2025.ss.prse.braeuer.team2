package jku.se.Controller;

import javafx.fxml.FXML;

import java.io.IOException;

public class StatisticsController extends Controller {
    @FXML
    private void handleBack (javafx.event.ActionEvent event) throws IOException {
        switchScene(event, "adminPanel.fxml");
    }
}
