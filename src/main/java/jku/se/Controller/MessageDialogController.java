package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MessageDialogController {

    @FXML
    private Label messageLabel;

    public void setMessage(String message) {
        messageLabel.setText(message); // Setze die Nachricht
    }
}
