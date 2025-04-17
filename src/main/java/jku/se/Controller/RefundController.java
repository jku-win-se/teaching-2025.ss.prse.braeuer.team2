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
        // Double-Validierung für beide Textfelder
        setupDoubleValidation(refundRestaurant);
        setupDoubleValidation(refundSupermarket);

        // Aktuelle Werte anzeigen
        refreshRefundValues();

        // Tabelle initialisieren
        setupRefundTable();
    }

    private void setupDoubleValidation(TextField textField) {
        // Formatter für Double-Eingaben mit Komma/Punkt
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

    private void refreshRefundValues() throws SQLException {
        refundRestaurant.setText(String.format("%.2f", Refund.getRefundRestaurant()));
        refundSupermarket.setText(String.format("%.2f", Refund.getRefundSupermarket()));
    }

    public void updateRefunds(ActionEvent actionEvent) {
        try {
            double restaurantValue = parseDoubleValue(refundRestaurant.getText());
            double supermarketValue = parseDoubleValue(refundSupermarket.getText());

            if (restaurantValue < 0 || supermarketValue < 0) {
                showAlert("Ungültige Werte", "Beträge müssen positiv sein.", Alert.AlertType.ERROR);
                return;
            }

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Bestätigung");
            confirmation.setHeaderText("Rückerstattungssätze aktualisieren");
            confirmation.setContentText(String.format(
                    "Neue Werte:\nSupermarkt: %.2f€\nRestaurant: %.2f€\n\nFortfahren?",
                    supermarketValue, restaurantValue));

            if (confirmation.showAndWait().orElseThrow() == ButtonType.OK) {
                Refund.setDailyRefunds(supermarketValue, restaurantValue, LocalDate.now(), Login.getCurrentUsername());
                showAlert("Erfolg", "Rückerstattungssätze wurden aktualisiert.", Alert.AlertType.INFORMATION);

                // Beide Methoden aufrufen, um Textfelder UND Tabelle zu aktualisieren
                refreshRefundValues();  // Aktualisiert die Textfelder
                refreshTableData();      // Aktualisiert die Tabelle
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

    private double parseDoubleValue(String input) throws NumberFormatException {
        // Ersetze Komma durch Punkt für die Parsing
        String normalized = input.replace(',', '.');
        return Double.parseDouble(normalized);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void setupRefundTable() throws SQLException {
        // Spalten mit den Refund-Properties verbinden
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("changeDate"));
        restaurantColumn.setCellValueFactory(new PropertyValueFactory<>("restaurant"));
        supermarketColumn.setCellValueFactory(new PropertyValueFactory<>("supermarket"));
        adminColumn.setCellValueFactory(new PropertyValueFactory<>("admin"));

        // Daten laden und in der Tabelle anzeigen
        refreshTableData();

        // Spalten automatisch anpassen
        refundTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void refreshTableData() throws SQLException {
        ObservableList<Refund> refundData = FXCollections.observableArrayList(Refund.getAllRefunds());
        refundTable.setItems(refundData);
    }
}
