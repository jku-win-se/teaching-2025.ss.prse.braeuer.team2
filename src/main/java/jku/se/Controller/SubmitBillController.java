package jku.se.Controller;

import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

public class SubmitBillController extends Controller {

    @FXML
    private TextField filePathField;

    @FXML
    private Label successMessage;

    @FXML
    private void goBackToDashboard(ActionEvent event) throws IOException {
        switchScene(event, "dashboardUser.fxml");
    }

    @FXML
    private void handleFileUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Rechnung auswählen");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Bilder (JPEG, PNG)", "*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("PDF-Dateien", "*.pdf")
        );

        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
            successMessage.setText(""); // Nachricht zurücksetzen
        } else {
            filePathField.setText("Keine Datei ausgewählt...");
        }
    }

    @FXML
    private void handleUpload(ActionEvent event) {
        String filePath = filePathField.getText();

        if (filePath.isEmpty() || filePath.equals("Keine Datei ausgewählt...")) {
            successMessage.setText("Keine Datei ausgewählt!");
            successMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        // OCR-Verarbeitung hier

        successMessage.setText("Rechnung erfolgreich hochgeladen");
        successMessage.setStyle("-fx-text-fill: green;");
    }
}
