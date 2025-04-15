package jku.se.Controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Supplier;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import jku.se.*;

import static jku.se.Database.invoiceExists;

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

    public Invoice requestManualAll(LocalDate defaultDate, double defaultAmount, InvoiceType defaultType, InvoiceStatus defaultStatus) {
        CountDownLatch latch = new CountDownLatch(1);
        Invoice[] resultInvoice = new Invoice[1];

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("Rechnungsdaten bearbeiten");
            stage.setOnCloseRequest(event -> event.consume()); // Verhindert Schließen ohne Validierung

            // UI-Elemente
            Label errorLabel = new Label();
            errorLabel.setTextFill(Color.RED);

            TextField dateField = new TextField();
            Label dateError = new Label();
            dateError.setTextFill(Color.RED);

            TextField amountField = new TextField();
            Label amountError = new Label();
            amountError.setTextFill(Color.RED);

            ToggleGroup typeGroup = new ToggleGroup();
            RadioButton supermarketRadio = new RadioButton("Supermarkt");
            RadioButton restaurantRadio = new RadioButton("Restaurant");
            Label typeError = new Label();
            typeError.setTextFill(Color.RED);

            // Default-Werte setzen
            if (defaultDate != null) {
                dateField.setText(defaultDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            }
            if (defaultAmount > 0) {
                amountField.setText(String.format("%.2f", defaultAmount));
            }
            if (defaultType != null) {
                if (defaultType == InvoiceType.SUPERMARKET) supermarketRadio.setSelected(true);
                else if (defaultType == InvoiceType.RESTAURANT) restaurantRadio.setSelected(true);
            }

            // Validierungsmethoden
            Supplier<Boolean> validateDate = () -> {
                try {
                    if (dateField.getText().trim().isEmpty()) {
                        dateError.setText("Datum ist erforderlich");
                        return false;
                    }

                    LocalDate date = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));

                    if (!InvoiceScan.isDateInThePastOrToday(date)) {
                        dateError.setText("Datum darf nicht in der Zukunft liegen");
                        return false;
                    }
                    if (!InvoiceScan.isWithinCurrentMonth(date)) {
                        dateError.setText("Datum muss im aktuellen Monat liegen");
                        return false;
                    }
                    if (!InvoiceScan.isWorkday(date)) {
                        dateError.setText("Kein Arbeitstag (Wochenende/Feiertag)");
                        return false;
                    }

                    // NEU: Prüfung auf existierende Rechnung
                    try (Connection connection = Database.getConnection()) {
                        if (invoiceExists(connection, Login.getCurrentUsername(), date)) {
                            dateError.setText("Rechnung für dieses Datum existiert bereits");
                            return false;
                        }
                    } catch (SQLException e) {
                        dateError.setText("Datenbankfehler bei der Prüfung");
                        return false;
                    }



                    dateError.setText("");
                    return true;
                } catch (DateTimeParseException e) {
                    dateError.setText("Ungültiges Format (DD.MM.YYYY)");
                    return false;
                }
            };

            Supplier<Boolean> validateAmount = () -> {
                try {
                    if (amountField.getText().trim().isEmpty()) {
                        amountError.setText("Betrag ist erforderlich");
                        return false;
                    }

                    double amount = Double.parseDouble(amountField.getText().replace(',', '.'));
                    if (amount <= 0) {
                        amountError.setText("Betrag muss positiv sein");
                        return false;
                    }

                    amountError.setText("");
                    return true;
                } catch (NumberFormatException e) {
                    amountError.setText("Ungültiger Betrag (z.B. 12.99)");
                    return false;
                }
            };

            Supplier<Boolean> validateType = () -> {
                if (!supermarketRadio.isSelected() && !restaurantRadio.isSelected()) {
                    typeError.setText("Bitte Typ auswählen");
                    return false;
                }
                typeError.setText("");
                return true;
            };

            // Event-Handler
            dateField.setOnAction(e -> validateDate.get());
            amountField.setOnAction(e -> validateAmount.get());

            Button saveButton = new Button("Speichern");
            saveButton.setOnAction(e -> {
                boolean dateValid = validateDate.get();
                boolean amountValid = validateAmount.get();
                boolean typeValid = validateType.get();

                if (dateValid && amountValid && typeValid) {
                    try {
                        LocalDate newDate = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        double newAmount = Double.parseDouble(amountField.getText().replace(',', '.'));
                        InvoiceType newType = supermarketRadio.isSelected() ? InvoiceType.SUPERMARKET : InvoiceType.RESTAURANT;

                        // Änderungsprüfung
                        boolean changed = (defaultDate != null && !defaultDate.equals(newDate)) ||
                                (defaultAmount != newAmount) ||
                                (defaultType != newType);

                        InvoiceStatus status;

                        if (!changed && defaultStatus == InvoiceStatus.ACCEPTED) {
                            status = InvoiceStatus.ACCEPTED;
                        } else {
                            status = InvoiceStatus.PENDING;
                        }
                        double refund = Refund.refundCalculation(newAmount, newType, newDate);
                        resultInvoice[0] = new Invoice(newDate, newAmount, newType, status, refund);

                        stage.close();
                        latch.countDown();
                    } catch (Exception ex) {
                        errorLabel.setText("Fehler bei der Verarbeitung: " + ex.getMessage());
                    }
                } else {
                    errorLabel.setText("Bitte korrigieren Sie die markierten Felder");
                }
            });

            Button cancelButton = new Button("Abbrechen");
            cancelButton.setOnAction(e -> {
                resultInvoice[0] = null;
                stage.close();
                latch.countDown();
            });

            // Layout
            VBox layout = new VBox(10,
                    new VBox(5, new Label("Datum (DD.MM.YYYY):"), dateField, dateError),
                    new VBox(5, new Label("Betrag:"), amountField, amountError),
                    new VBox(5, new Label("Rechnungstyp:"),
                            new HBox(10, supermarketRadio, restaurantRadio), typeError),
                    errorLabel,
                    new HBox(10, saveButton, cancelButton)
            );
            layout.setPadding(new Insets(15));
            layout.setAlignment(Pos.CENTER);

            stage.setScene(new Scene(layout, 400, 350));
            stage.show();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }

        return resultInvoice[0];
    }


}
