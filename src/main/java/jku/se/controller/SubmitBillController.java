package jku.se.controller;

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

                if (uploadAnimation != null && !message.contains("uploaded")) {
                    uploadAnimation.stop();
                }
            }
        });
    }

    @FXML
    private void goBackToDashboard(ActionEvent event) throws IOException {
        switchScene(event, "dashboardUser.fxml");
    }

    // Mit Hilfe von KI generiert
    @FXML
    private void handleFileUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Invoice");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fotos (JPEG, PNG)", "*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("PDF-files", "*.pdf")
        );
        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
            displayMessage("", "black");
        } else {
            filePathField.setText("No file selected...");
        }
    }

    @FXML
    private void handleUpload() {
        String filePath = filePathField.getText();

        if (filePath.isEmpty() || filePath.equals("No file selected...")) {
            displayMessage("No file selected!", "red");
            return;
        }

        if (uploadAnimation != null) {
            uploadAnimation.stop();
        }

        uploadAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> successMessage.setText("Invoice uploading")),
                new KeyFrame(Duration.seconds(1.0), e -> successMessage.setText("Invoice uploading.")),
                new KeyFrame(Duration.seconds(1.5), e -> successMessage.setText("Invoice uploading..")),
                new KeyFrame(Duration.seconds(2.0), e -> successMessage.setText("Invoice uploading..."))
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
            stage.setTitle("Insert date manually");

            // Create the label, text field for date input, and the confirm button
            Label label = new Label("Please insert date (DD.MM.YYYY):");
            TextField dateInput = new TextField();
            Button confirmButton = new Button("Confirm");

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
                        displayMessage("Date must not be in the future.", "red");
                        return;
                    }

                    // Check if the entered date is within the current month
                    if (!InvoiceScan.isWithinCurrentMonth(parsedDate)){
                        displayMessage("Date must be within the current month.", "red");
                        return;
                    }

                    // Check if the entered date is a workday (business day)
                    if (!InvoiceScan.isWorkday(parsedDate)) {
                        displayMessage("Not a working day!", "red");
                        return; // Fenster bleibt offen, Benutzer muss neues Datum eingeben
                    }

                    enteredDate[0] = parsedDate; // Store the entered date
                    displayMessage("Date entered successfully: " + parsedDate.format(formatter), "green");
                    stage.close(); // Close the window
                    latch.countDown(); // Release the latch, allowing the main thread to continue
                } catch (DateTimeParseException ex) {
                    // Display error message if the date format is invalid
                    displayMessage("Invalid date! Please enter in format: 'DD.MM.YYYY'.", "red");
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
            stage.setTitle("Insert amount manually");

            Label label = new Label("Please enter the amount (e.g. 123.45):");
            TextField amountInput = new TextField();
            Button confirmButton = new Button("Confirm");

            VBox layout = new VBox(10, label, amountInput, confirmButton);
            layout.setPadding(new Insets(10));
            layout.setAlignment(Pos.CENTER);

            Scene scene = new Scene(layout, 300, 150);
            stage.setScene(scene);
            stage.show();

            confirmButton.setOnAction(e -> {
                try {
                    double parsedAmount = Double.parseDouble(amountInput.getText().replace(',', '.'));
                    if (parsedAmount < 0) throw new NumberFormatException("Negative amount not allowed");

                    enteredAmount[0] = parsedAmount;
                    displayMessage("Amount entered successfully: " + parsedAmount, "green");
                    stage.close();
                    latch.countDown();
                } catch (NumberFormatException ex) {
                    displayMessage("Invalid amount! Please enter a number in format '123.45'.", "red");
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
            stage.setTitle("Choose invoice type");

            Label label = new Label("Please choose invoice type:");
            Button supermarketButton = new Button("Supermarket");
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
                displayMessage("Invoice type: Supermarket", "green");
                stage.close();
                latch.countDown();
            });

            // button "Restaurant"
            restaurantButton.setOnAction(e -> {
                selectedType[0] = InvoiceType.RESTAURANT;
                displayMessage("Invoice type: Restaurant", "green");
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
        displayMessage(" ", "green");

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("Edit invoice data");
            stage.setOnCloseRequest(event -> {
                displayMessage("Cancelled – invoice was not uploaded", "red");
            });

            // UI-Elemente
            Label errorLabel = new Label();
            errorLabel.setTextFill(Color.RED);

            TextField dateField = new TextField();
            Label dateError = new Label();
            dateError.setTextFill(Color.RED);

            TextField amountField = new TextField();
            Label amountError = new Label();
            amountError.setTextFill(Color.RED);

            ComboBox<InvoiceType> typeComboBox = new ComboBox<>();
            typeComboBox.getItems().addAll(InvoiceType.SUPERMARKET, InvoiceType.RESTAURANT);
            typeComboBox.setPromptText("Choose invoice type");
            Label typeError = new Label();
            typeError.setTextFill(Color.RED);

            Button cancelButton = new Button("Cancel");
            cancelButton.setOnAction(e -> {
                displayMessage("Cancelled – invoice was not uploaded", "red");
                stage.close();
            });

            // Default-Werte setzen
            if (defaultDate != null) {
                dateField.setText(defaultDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            }
            if (defaultAmount > 0) {
                amountField.setText(String.format("%.2f", defaultAmount));
            }
            if (defaultType != null) {
                typeComboBox.setValue(defaultType);
            }

            // Validierungsmethoden
            Supplier<Boolean> validateDate = () -> {
                try {
                    if (dateField.getText().trim().isEmpty()) {
                        dateError.setText("Date is required");
                        return false;
                    }

                    LocalDate date = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));

                    if (!InvoiceScan.isDateInThePastOrToday(date)) {
                        dateError.setText("Date must not be in the future");
                        return false;
                    }
                    if (!InvoiceScan.isWithinCurrentMonth(date)) {
                        dateError.setText("Date must be in the current month");
                        return false;
                    }
                    if (!InvoiceScan.isWorkday(date)) {
                        dateError.setText("Not a work day (weekend/public holiday)");
                        return false;
                    }

                    try (Connection connection = Database.getConnection()) {
                        if (invoiceExists(connection, Login.getCurrentUsername(), date)) {
                            dateError.setText("Invoice for this date already exists");
                            return false;
                        }
                    } catch (SQLException e) {
                        dateError.setText("Database error during testing");
                        return false;
                    }

                    dateError.setText("");
                    return true;
                } catch (DateTimeParseException e) {
                    dateError.setText("Invalid format (DD.MM.YYYY)");
                    return false;
                }
            };

            Supplier<Boolean> validateAmount = () -> {
                try {
                    if (amountField.getText().trim().isEmpty()) {
                        amountError.setText("Amount is required");
                        return false;
                    }

                    double amount = Double.parseDouble(amountField.getText().replace(',', '.'));
                    if (amount <= 0) {
                        amountError.setText("Amount has to be positive");
                        return false;
                    }

                    amountError.setText("");
                    return true;
                } catch (NumberFormatException e) {
                    amountError.setText("Invalid amount (e.g. 12.99)");
                    return false;
                }
            };

            Supplier<Boolean> validateType = () -> {
                if (typeComboBox.getValue() == null) {
                    typeError.setText("Please choose type");
                    return false;
                }
                typeError.setText("");
                return true;
            };

            // Event-Handler
            dateField.setOnAction(e -> validateDate.get());
            amountField.setOnAction(e -> validateAmount.get());

            Button saveButton = new Button("Save");
            saveButton.setOnAction(e -> {
                boolean dateValid = validateDate.get();
                boolean amountValid = validateAmount.get();
                boolean typeValid = validateType.get();

                if (dateValid && amountValid && typeValid) {
                    try {
                        LocalDate newDate = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        double newAmount = Double.parseDouble(amountField.getText().replace(',', '.'));
                        InvoiceType newType = typeComboBox.getValue();

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
                        errorLabel.setText("Error in processing: " + ex.getMessage());
                    }
                } else {
                    errorLabel.setText("Please correct the marked fields");
                }
            });

            // Layout
            VBox layout = new VBox(10,
                    new VBox(5, new Label("Date (DD.MM.YYYY):"), dateField, dateError),
                    new VBox(5, new Label("Amount:"), amountField, amountError),
                    new VBox(5, new Label("Invoice type:"), typeComboBox, typeError),
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
