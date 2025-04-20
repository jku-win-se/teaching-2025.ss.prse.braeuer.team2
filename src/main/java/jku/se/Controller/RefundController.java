package jku.se.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import jku.se.Login;
import jku.se.Refund;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.function.UnaryOperator;

public class RefundController extends Controller {

    @FXML
    private TextField refundRestaurant;
    @FXML
    private TextField refundSupermarket;


    @FXML
    private TableView<Refund> refundTable;
    @FXML
    private TableColumn<Refund, LocalDate> dateColumn;
    @FXML
    private TableColumn<Refund, Double> restaurantColumn;
    @FXML
    private TableColumn<Refund, Double> supermarketColumn;
    @FXML
    private TableColumn<Refund, Double> adminColumn;


    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        switchScene(event, "adminPanel.fxml");
    }

    @FXML
    public void initialize() throws SQLException {
        // Set up double (decimal number) validation for both input fields:
        // Ensures users can only enter valid numeric values
        setupDoubleValidation(refundRestaurant);
        setupDoubleValidation(refundSupermarket);

        // Load and display the current refund values for restaurant and supermarket
        refreshRefundValues();

        // Initialize the TableView that displays historical refund data
        setupRefundTable();
    }

    // Sets up validation for a TextField to only allow decimal numbers (with comma or dot) (AI)
    private void setupDoubleValidation(TextField textField) {
        DecimalFormat format = new DecimalFormat("#.0#");

        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (change.getControlNewText().isEmpty()) {
                return change;
            }

            try {
                format.parse(change.getControlNewText());
                return change;
            } catch (ParseException e) {
                return null;
            }
        };

        textField.setTextFormatter(new TextFormatter<>(filter));
    }

    // Refreshes the input fields to show the current refund values from the database (AI)
    private void refreshRefundValues() throws SQLException {
        refundRestaurant.setText(String.format("%.2f", Refund.getRefundRestaurant()));
        refundSupermarket.setText(String.format("%.2f", Refund.getRefundSupermarket()));
    }

    // Called when the update button is clicked – updates the refund values in the database
    public void updateRefunds(ActionEvent actionEvent) {
        try {
            // Parse the values from the input fields
            double restaurantValue = parseDoubleValue(refundRestaurant.getText());
            double supermarketValue = parseDoubleValue(refundSupermarket.getText());

            // Validate that both values are positive
            if (restaurantValue < 0 || supermarketValue < 0) {
                showAlert("Ungültige Werte", "Beträge müssen positiv sein.", Alert.AlertType.ERROR);
                return;
            }

            // Show confirmation dialog to the user
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Bestätigung");
            confirmation.setHeaderText("Rückerstattungssätze aktualisieren");
            confirmation.setContentText(String.format(
                    "Neue Werte:\nSupermarkt: %.2f€\nRestaurant: %.2f€\n\nFortfahren?",
                    supermarketValue, restaurantValue));

            // If user confirms, save the new values
            if (confirmation.showAndWait().orElseThrow() == ButtonType.OK) {
                Refund.setDailyRefunds(supermarketValue, restaurantValue, LocalDate.now(), Login.getCurrentUsername());
                showAlert("Erfolg", "Rückerstattungssätze wurden aktualisiert.", Alert.AlertType.INFORMATION);

                // Refresh both the input fields and the table view
                refreshRefundValues();
                refreshTableData();
            }

        } catch (NumberFormatException e) {
            showAlert("Eingabefehler", "Ungültige Zahleneingabe. Bitte Zahlen im Format 12.34 oder 12,34 eingeben.", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            showAlert("Datenbankfehler", "Fehler beim Speichern: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Fehler", "Unbekannter Fehler: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Converts a string input to a double, supporting both comma and dot as decimal separators
    private double parseDoubleValue(String input) throws NumberFormatException {
        String normalized = input.replace(',', '.');
        return Double.parseDouble(normalized);
    }

    // Utility method to show an alert popup with the specified title, message, and type
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Initializes and sets up the refund history table
    private void setupRefundTable() throws SQLException {
        // Link table columns to properties in the Refund class
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("changeDate"));
        restaurantColumn.setCellValueFactory(new PropertyValueFactory<>("restaurant"));
        supermarketColumn.setCellValueFactory(new PropertyValueFactory<>("supermarket"));
        adminColumn.setCellValueFactory(new PropertyValueFactory<>("admin"));

        // Load data from the database and display it in the table
        refreshTableData();

        // Automatically resize columns to fit the table width
        refundTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // Loads all refund records from the database and populates the TableView
    private void refreshTableData() throws SQLException {
        ObservableList<Refund> refundData = FXCollections.observableArrayList(Refund.getAllRefunds());
        refundTable.setItems(refundData);
    }
}
