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
    private ListView<Label> messagePopUp;

    private DashboardUserController dashboardUserController;

    private boolean showMessagePopupOnInitialize; // Flag, das steuert, ob das Popup beim Initialisieren angezeigt werden soll.

    private List<Label> savedMessages = new ArrayList<>();

    @FXML
    private Label messageLabel;

    public void setShowMessagePopupOnInitialize(boolean showMessagePopupOnInitialize) {
        this.showMessagePopupOnInitialize = showMessagePopupOnInitialize;
    }

    @FXML
    private void initialize() {
        // Beim Laden des Controllers, füge alle gespeicherten Nachrichten in das ListView ein, wird impliziet durchgeführt

        List<String> storedMessages = MessageStore.getMessages();

        for (String msg : storedMessages) {
            Label newMessage = new Label(msg);
            messagePopUp.getItems().add(newMessage);
            newMessage.setStyle("-fx-text-fill: blue;"); // Erfolgsnachricht

            // Maximal 5 Nachrichten in der Liste behalten (optional)
            if (messagePopUp.getItems().size() >= 5) {
                messagePopUp.getItems().remove(0);  // Entferne die älteste Nachricht
            }
        }

        System.out.println(showMessagePopupOnInitialize);
        // Stelle sicher, dass das Popup sichtbar wird
        if (showMessagePopupOnInitialize) {
            messagePopUp.setVisible(true);
            showMessagePopupOnInitialize = false;
        }
    }

    @FXML
    private void openSubmitBill(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/submitBill.fxml"));

        // Lade das FXML und bekomme den SubmitBillController
        Parent root = loader.load();
        SubmitBillController submitBillController = loader.getController();

        // Übergebe den DashboardUserController an den SubmitBillController
        submitBillController.setDashboardUserController(this);  // Hier übergibst du den aktuellen DashboardUserController

        // Wechsle die Szene
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void openSubmittedBills(ActionEvent event) throws IOException {
        switchScene(event, "submittedBills.fxml");
    }

    @FXML
    private void handleMessageButtonClick() {
        boolean isVisible = messagePopUp.isVisible();  // Überprüfen, ob das Popup sichtbar ist

        // Zeige/Verstecke das Popup
        messagePopUp.setVisible(!isVisible);

        // Setze eine Nachricht im Popup
        if (!isVisible) {
            /*String message = InvoiceController.getMessage();
            messageLabel= new Label(message);
            System.out.println(messageLabel);

            if (messagePopUp.getItems().size() >= 10) {
                messagePopUp.getItems().remove(messagePopUp.getItems().size() - 1);  // Entferne die älteste Nachricht
            }

            // Füge die neue Nachricht zur ListView hinzu
            messagePopUp.getItems().add(0, messageLabel);*/

            // Zeige die ListView an
            messagePopUp.setVisible(true);
        }
    }

    public void setDashboardUserController(DashboardUserController controller) {
        this.dashboardUserController = controller;
    }

    public void handleUploadSuccess(InvoiceController invoice) {
        // Neue Nachricht erstellen
        Label newMessage = new Label(invoice.getMessage());
        newMessage.setStyle("-fx-text-fill: green;");

        // Maximal 5 Nachrichten behalten
        if (messagePopUp.getItems().size() >= 5) {
            messagePopUp.getItems().remove(messagePopUp.getItems().size() - 1);  // Entferne die älteste Nachricht
        }

        // Füge die neue Nachricht zur ListView hinzu
        messagePopUp.getItems().add(0, newMessage);

        // Zeige die ListView an
        messagePopUp.setVisible(true);
    }

    public void handleUploadFailure(InvoiceController invoice) {
        // Neue Nachricht erstellen
        Label newMessage = new Label(invoice.getMessage());
        System.out.println(newMessage);
        messageLabel.setStyle("-fx-text-fill: red;");

        // Maximal 5 Nachrichten behalten
        if (messagePopUp.getItems().size() >= 5) {
            messagePopUp.getItems().remove(messagePopUp.getItems().size() - 1);  // Entferne die älteste Nachricht
        }

        // Füge die neue Nachricht zur ListView hinzu
        messagePopUp.getItems().add(0, newMessage);

        // Zeige die ListView an
        messagePopUp.setVisible(true);
    }

    public void updateMessagePopup(String message, boolean isSuccess) {
        // Setze die Nachricht im Popup
        messageLabel.setText(message);

        // Ändere die Textfarbe je nach Erfolg oder Fehler
        if (isSuccess) {
            messageLabel.setStyle("-fx-text-fill: green;");
        } else {
            messageLabel.setStyle("-fx-text-fill: red;");
        }

        // Zeige das Popup an
        messagePopUp.setVisible(true);
    }


    public ListView<Label> getMessagePopUp() {
        return messagePopUp;
    }

    // Eine Methode zum Hinzufügen einer neuen Nachricht
    public void addMessageToList(String message) {
        // Stelle sicher, dass eine neue Nachricht erstellt wird
        Label newMessage = new Label(message);
        newMessage.setStyle("-fx-text-fill: blue;"); // Erfolgsnachricht

        // Maximal 5 Nachrichten in der Liste behalten (optional)
        if (messagePopUp.getItems().size() >= 5) {
            messagePopUp.getItems().remove(0);  // Entferne die älteste Nachricht
        }

        // Füge die neue Nachricht zur Liste hinzu
        messagePopUp.getItems().add(0, newMessage);

        // Speichere die Nachricht für zukünftige Szenenwechsel
        MessageStore.addMessage(message);

        // Stelle sicher, dass das Popup sichtbar wird
        messagePopUp.setVisible(true);
    }
}