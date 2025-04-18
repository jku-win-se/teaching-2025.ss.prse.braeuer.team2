package jku.se.Controller;


import java.io.IOException;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;

public class AdminPanelController extends Controller{

    @FXML
    private void openUserSearch(ActionEvent event) throws IOException {
        switchScene(event, "userSearch.fxml");
    }

    @FXML
    private void openRequestManagement(ActionEvent event) throws IOException {
        switchScene(event, "requestManagement.fxml");
    }

    @FXML
    private void openStatistics(ActionEvent event) throws IOException {
        switchScene(event, "statistics.fxml");
    }
}
