package jku.se.Controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;

public class SubmittedBillsController extends Controller{

    @FXML
    private void goBackToDashboard(ActionEvent event) throws IOException {
        switchScene(event, "dashboardUser.fxml");
    }
}
