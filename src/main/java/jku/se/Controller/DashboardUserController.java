package jku.se.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jku.se.Controller.SubmitBillController;
import jku.se.MessageStore;

public class DashboardUserController extends Controller {

    @FXML
    private DashboardUserController dashboardUserController;

    @FXML
    private void openSubmittedBills(ActionEvent event) throws IOException {
        switchScene(event, "submittedBills.fxml");
    }


    public void setDashboardUserController(DashboardUserController controller) {
        this.dashboardUserController = controller;
    }


}
