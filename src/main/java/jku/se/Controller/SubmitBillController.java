package jku.se.Controller;

import java.util.concurrent.CountDownLatch;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import jku.se.Database;
import jku.se.InvoiceScan;
import jku.se.InvoiceType;

public class SubmitBillController extends Controller {

    @FXML
    private TextField filePathField;

    @FXML
    private Label successMessage;

    private InvoiceScan invoiceScan;
    private Timeline uploadAnimation;

    public SubmitBillController() {
        this.invoiceScan = new InvoiceScan(this);
    }

    @FXML
    public void displayMessage(String message, String color) {
        Platform.runLater(() -> {
            if (successMessage != null) {
                successMessage.setText(message);
                successMessage.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: normal;");

                if (uploadAnimation != null && !message.contains("hochgeladen")) {
                    uploadAnimation.stop();
                }
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
            displayMessage("", "black");
        } else {
            filePathField.setText("Keine Datei ausgewählt...");
        }
    }

    @FXML
    private void handleUpload(ActionEvent event) {
        String filePath = filePathField.getText();

        if (filePath.isEmpty() || filePath.equals("Keine Datei ausgewählt...")) {
            displayMessage("Keine Datei ausgewählt!", "red");
            return;
        }

        if (uploadAnimation != null) {
            uploadAnimation.stop();
        }

        uploadAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> successMessage.setText("Rechnung wird hochgeladen")),
                new KeyFrame(Duration.seconds(1.0), e -> successMessage.setText("Rechnung wird hochgeladen..."))
        );
        uploadAnimation.setCycleCount(Timeline.INDEFINITE);
        uploadAnimation.play();

        new Thread(() -> {
            Database.invoiceScanUpload(filePath, this);
        }).start();
    }


//shows a field to input the date manual (AI)
    public LocalDate requestManualDate() {
        // Create a CountDownLatch to block the current thread until the user has entered a date
        CountDownLatch latch = new CountDownLatch(1);
        final LocalDate[] enteredDate = new LocalDate[1]; // variable

        //Use Platform.runLater to ensure UI updates are done on the JavaFX Application Thread
        Platform.runLater(() -> {
            // Create a new Stage (window) for the date input
            Stage stage = new Stage();
            stage.setTitle("Datum manuell eingeben");

            // Create the label, text field for date input, and the confirm button
            Label label = new Label("Bitte geben Sie das Datum ein (DD.MM.YYYY):");
            TextField dateInput = new TextField();
            Button confirmButton = new Button("Bestätigen");

            // Layout for the stage: VBox with vertical spacing
            VBox layout = new VBox(10, label, dateInput, confirmButton);
            layout.setPadding(new Insets(10));
            layout.setAlignment(Pos.CENTER);

            // Create a scene and set it on the stage
            Scene scene = new Scene(layout, 300, 150);
            stage.setScene(scene);
            stage.show();

            // Action when the user clicks the confirm button
            confirmButton.setOnAction(e -> {
                try {
                    // Define the date format (DD.MM.YYYY)
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    // Try parsing the input from the text field into a LocalDate object
                    LocalDate parsedDate = LocalDate.parse(dateInput.getText(), formatter);

                    //Check if the entered date is not in the future
                    if (!InvoiceScan.isDateInThePastOrToday(parsedDate)) {
                        displayMessage("Datum darf nicht in der Zukunft liegen.", "red");
                        return;
                    }

                    // Check if the entered date is within the current month
                    if (!InvoiceScan.isWithinCurrentMonth(parsedDate)){
                        displayMessage("Datum muss innerhalb des aktuellen Monats liegen.", "red");
                        return;
                    }

                    // Check if the entered date is a workday (business day)
                    if (!InvoiceScan.isWorkday(parsedDate)) {
                        displayMessage("Kein Arbeitstag!", "red");
                        return; // Fenster bleibt offen, Benutzer muss neues Datum eingeben
                    }

                    enteredDate[0] = parsedDate; // Store the entered date
                    displayMessage("Datum erfolgreich eingegeben: " + parsedDate.format(formatter), "green");
                    stage.close(); // Close the window
                    latch.countDown(); // Release the latch, allowing the main thread to continue
                } catch (DateTimeParseException ex) {
                    // Display error message if the date format is invalid
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

    //shows a field to input the sum manual (AI)
    public double requestManualSum() {
        CountDownLatch latch = new CountDownLatch(1);
        final double[] enteredAmount = new double[1];

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
                    latch.countDown();
                } catch (NumberFormatException ex) {
                    displayMessage("Ungültiger Betrag! Bitte eine Zahl im Format 123.45 eingeben.", "red");
                }
            });
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return enteredAmount[0];
    }

    //shows a field to input (with 2 button) the type manual (AI)
    public InvoiceType requestManualType() {
        CountDownLatch latch = new CountDownLatch(1);
        final InvoiceType[] selectedType = new InvoiceType[1];

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

            // button "Supermarket"
            supermarketButton.setOnAction(e -> {
                selectedType[0] = InvoiceType.SUPERMARKET;
                displayMessage("Rechnungstyp: Supermarkt", "green");
                stage.close();
                latch.countDown();
            });

            // button "Restaurant"
            restaurantButton.setOnAction(e -> {
                selectedType[0] = InvoiceType.RESTAURANT;
                displayMessage("Rechnungstyp: Restaurant", "green");
                stage.close();
                latch.countDown();
            });
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return selectedType[0];
    }
}
