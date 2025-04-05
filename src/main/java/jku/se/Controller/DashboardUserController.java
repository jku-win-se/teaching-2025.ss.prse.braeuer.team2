package jku.se.Controller;

import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import jku.se.DashboardUser;

public class DashboardUserController extends Controller {

    @FXML
    private void openSubmitBill(ActionEvent event) throws IOException {
        switchScene(event, "submitBill.fxml");
    }

    @FXML
    private void openSubmittedBills(ActionEvent event) throws IOException {
        switchScene(event, "submittedBills.fxml");
    }

    @FXML
    private Label labelEingereichteRechnungen;
    @FXML
    private Label labelGenehmigteRechnungen;

    @FXML
    private Label labelOffeneRechnungen;

    @FXML
    private Label labelSumme;

    public void initialize() {
        try {
            int anzahl = DashboardUser.getEingereichteRechnungen(); // Methode wie oben
            labelEingereichteRechnungen.setText("📑 " + anzahl);
            int genehmigt = DashboardUser.getGenehmigteErstattungen();
            labelGenehmigteRechnungen.setText("✅ " + genehmigt);
            int offen = DashboardUser.getOffeneErstattungen();
            labelOffeneRechnungen.setText("⏳" + offen);
            double summe = DashboardUser.getGesamterstattungen();
            labelSumme.setText("💶 " + summe);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
