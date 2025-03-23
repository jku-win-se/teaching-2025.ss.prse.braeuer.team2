package jku.se.Controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;

public class DashboardAdminController extends Controller{

    @FXML
    private void openUserDashboard(ActionEvent event) throws IOException {
        switchScene(event, "dashboardUser.fxml");
    }

    @FXML
    private void openAdminPanel(ActionEvent event) throws IOException {
        switchScene(event, "adminPanel.fxml");
    }
}
