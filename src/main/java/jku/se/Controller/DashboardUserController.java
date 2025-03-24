package jku.se.Controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;

public class DashboardUserController extends Controller {

    @FXML
    private void openSubmitBill(ActionEvent event) throws IOException {
        switchScene(event, "submitBill.fxml");
    }

    @FXML
    private void openSubmittedBills(ActionEvent event) throws IOException {
        switchScene(event, "submittedBills.fxml");
    }

}
