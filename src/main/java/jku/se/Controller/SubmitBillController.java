package jku.se.Controller;
import java.util.concurrent.CountDownLatch;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import jku.se.Database;
import jku.se.InvoiceScan;
import jku.se.InvoiceType;

public class SubmitBillController extends Controller {

    @FXML
    private TextField filePathField;

    @FXML
    private Label successMessage;

    private InvoiceScan invoiceScan;  // Referenz zu InvoiceScan

    public SubmitBillController() {
        // Initialisiere InvoiceScan
        this.invoiceScan = new InvoiceScan(this);
    }

    @FXML
    public void displayMessage(String message, String color) {
        Platform.runLater(() -> {
            if (successMessage != null) {
                successMessage.setText(message);
                successMessage.setStyle("-fx-text-fill: " + color + ";");
            }
        });
    }

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



        Database.invoiceScanUpload(filePath, this);
        successMessage.setText("Rechnung wird hochgeladen");
        successMessage.setStyle("-fx-text-fill: green;");

    }



    public LocalDate requestManualDate() {
        CountDownLatch latch = new CountDownLatch(1);
        final LocalDate[] enteredDate = new LocalDate[1]; // Variable für das eingegebene Datum

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("Datum manuell eingeben");

            Label label = new Label("Bitte geben Sie das Datum ein (DD.MM.YYYY):");
            TextField dateInput = new TextField();
            Button confirmButton = new Button("Bestätigen");

            VBox layout = new VBox(10, label, dateInput, confirmButton);
            layout.setPadding(new Insets(10));
            layout.setAlignment(Pos.CENTER);

            Scene scene = new Scene(layout, 300, 150);
            stage.setScene(scene);
            stage.show();

            confirmButton.setOnAction(e -> {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate parsedDate = LocalDate.parse(dateInput.getText(), formatter);

                    //Prüfen ob das eingegebene Datum innerhalb des aktuellen Monats ist
                    if (!InvoiceScan.isWithinCurrentMonth(parsedDate)){
                        displayMessage("Datum muss innerhalb des aktuellen Monats liegen.", "red");
                        return;
                    }

                    // Prüfen, ob das eingegebene Datum ein Arbeitstag ist
                    if (!InvoiceScan.isWorkday(parsedDate)) {
                        displayMessage("Kein Arbeitstag!", "red");
                        return; // Fenster bleibt offen, Benutzer muss neues Datum eingeben
                    }

                    enteredDate[0] = parsedDate;
                    displayMessage("Datum erfolgreich eingegeben: " + parsedDate.format(formatter), "green");
                    stage.close();
                    latch.countDown(); // Entsperrt den wartenden Thread
                } catch (DateTimeParseException ex) {
                    displayMessage("Ungültiges Datum! Bitte im Format DD.MM.YYYY eingeben.", "red");
                }
            });
        });

        try {
            latch.await(); // Wartet, bis der Benutzer das Datum eingegeben hat
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return enteredDate[0]; // Rückgabe des eingegebenen Datums
    }


    public double requestManualSum() {
        CountDownLatch latch = new CountDownLatch(1);
        final double[] enteredAmount = new double[1]; // Variable für den eingegebenen Betrag

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("Betrag manuell eingeben");

            Label label = new Label("Bitte geben Sie den Betrag ein (z. B. 123.45):");
            TextField amountInput = new TextField();
            Button confirmButton = new Button("Bestätigen");

            VBox layout = new VBox(10, label, amountInput, confirmButton);
            layout.setPadding(new Insets(10));
            layout.setAlignment(Pos.CENTER);

            Scene scene = new Scene(layout, 300, 150);
            stage.setScene(scene);
            stage.show();

            confirmButton.setOnAction(e -> {
                try {
                    double parsedAmount = Double.parseDouble(amountInput.getText().replace(',', '.'));
                    if (parsedAmount < 0) throw new NumberFormatException("Negativer Betrag nicht erlaubt");

                    enteredAmount[0] = parsedAmount;
                    displayMessage("Betrag erfolgreich eingegeben: " + parsedAmount, "green");
                    stage.close();
                    latch.countDown(); // Entsperrt den wartenden Thread
                } catch (NumberFormatException ex) {
                    displayMessage("Ungültiger Betrag! Bitte eine Zahl im Format 123.45 eingeben.", "red");
                }
            });
        });

        try {
            latch.await(); // Wartet, bis der Benutzer den Betrag eingegeben hat
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return enteredAmount[0]; // Rückgabe des eingegebenen Betrags
    }


    public InvoiceType requestManualType() {
        CountDownLatch latch = new CountDownLatch(1);
        final InvoiceType[] selectedType = new InvoiceType[1]; // Variable für die Auswahl

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("Rechnungstyp wählen");

            Label label = new Label("Bitte wählen Sie den Rechnungstyp:");
            Button supermarketButton = new Button("Supermarkt");
            Button restaurantButton = new Button("Restaurant");

            VBox layout = new VBox(10, label, supermarketButton, restaurantButton);
            layout.setPadding(new Insets(10));
            layout.setAlignment(Pos.CENTER);

            Scene scene = new Scene(layout, 300, 150);
            stage.setScene(scene);
            stage.show();

            // Event-Handler für die Buttons
            supermarketButton.setOnAction(e -> {
                selectedType[0] = InvoiceType.SUPERMARKET;
                displayMessage("Rechnungstyp: Supermarkt", "green");
                stage.close();
                latch.countDown(); // Entsperrt den wartenden Thread
            });

            restaurantButton.setOnAction(e -> {
                selectedType[0] = InvoiceType.RESTAURANT;
                displayMessage("Rechnungstyp: Restaurant", "green");
                stage.close();
                latch.countDown(); // Entsperrt den wartenden Thread
            });
        });

        try {
            latch.await(); // Wartet, bis der Benutzer eine Auswahl getroffen hat
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return selectedType[0]; // Rückgabe der Auswahl
    }







}
