package jku.se.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jku.se.*;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static jku.se.Controller.RequestManagementController.showAlert;
import static jku.se.Database.*;

public class EditInvoiceController extends Controller{
    @FXML
    public Label labelRechnungsID;
    @FXML
    public ComboBox comboBoxTyp;
    @FXML
    public TextField textfieldUsername;
    @FXML
    public ComboBox comboBoxStatus;
    @FXML
    public TextField textfieldImage;
    @FXML
    public Label labelRefund;
    @FXML
    public TextField textFieldBetrag;
    @FXML
    public DatePicker datePickerDatum;

    private int invoiceId;
    private double amount;
    private String date;
    private String typ;
    private String user;

    @FXML
    public void initialize() {
        comboBoxTyp.getItems().addAll(
                InvoiceType.SUPERMARKET.name(),
                InvoiceType.RESTAURANT.name()
        );

        comboBoxStatus.getItems().addAll(
                InvoiceStatus.ACCEPTED.name(),
                InvoiceStatus.PENDING.name(),
                InvoiceStatus.DENIED.name()
        );
    }
    public void loadInvoiceDetails(int invoiceId) {
        try (Connection conn = getConnection()) {
            String query = "SELECT * FROM rechnungen WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, invoiceId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Befülle die Textfelder mit den Werten der Rechnung
                    labelRechnungsID.setText(String.valueOf(rs.getInt("id")));
                    textFieldBetrag.setText(rs.getString("betrag"));
                    LocalDate choosendate = LocalDate.parse(rs.getString("datum"));

                    //Um das Datumsfeld zu befüllen mit dem erstellten datum
                    datePickerDatum.setDayCellFactory(picker -> new DateCell() {
                        @Override
                        public void updateItem(LocalDate date, boolean empty) {
                            super.updateItem(date, empty);
                            setDisable(empty || date.isAfter(LocalDate.now()));
                        }
                    });
                    datePickerDatum.setValue(choosendate);
                    comboBoxTyp.setValue(rs.getString("typ"));
                    textfieldUsername.setText(rs.getString("username"));
                    comboBoxStatus.setValue(rs.getString("status"));
                    textfieldImage.setText(rs.getString("image"));
                    labelRefund.setText(rs.getString("refund"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load invoice details: " + e.getMessage());
        }
    }

    // Hier kannst du eine Methode zum Speichern der Änderungen hinzufügen, z.B.:
    @FXML
    public void saveChanges() throws SQLException {
        // Hole die bearbeiteten Werte aus den Textfeldern und speichere sie in der Datenbank
        int id = Integer.parseInt(labelRechnungsID.getText());
        double betrag = Double.parseDouble(textFieldBetrag.getText());
        Date datum = null;
        String typString = (String) comboBoxTyp.getValue();
        String username = textfieldUsername.getText();
        String statusString = (String) comboBoxStatus.getValue();
        String image = textfieldImage.getText();

        double refund = 0;
        if(typString.equals(String.valueOf(InvoiceType.SUPERMARKET))){
            refund = Refund.getRefundSupermarket();
        } else {
            refund = Refund.getRefundRestaurant();
        }

        try {//Wahrscheinlich nur relevant für tests, im Programm kann man sonst keine anderen auswählen durch Dropdownbox
            InvoiceType typ = InvoiceType.valueOf((String) comboBoxTyp.getValue());
        } catch (IllegalArgumentException e) {
            showAlert("Error", "Choose a valid InvoiceType!");
            return;
        }

        try {//Wahrscheinlich nur relevant für tests, im Programm kann man sonst keine anderen auswählen durch Dropdownbox
            InvoiceStatus status = InvoiceStatus.valueOf((String) comboBoxStatus.getValue());
        } catch (IllegalArgumentException ex) {
            showAlert("Error", "Choose a valid InvoiceStatus!");
            return;
        }

        try {
            datum = Date.valueOf(datePickerDatum.getValue());
        } catch (IllegalArgumentException exc) {
            showAlert("Error", "Please enter a valid date in the format yyyy-mm-dd.");
            return; // Update wird abgebrochen, falls das Datum ungültig ist
        }

        if (!Objects.equals(typString, "RESTAURANT") && !Objects.equals(typString, "SUPERMARKET")) {
            showAlert("Error", "Please enter 'RESTAURANT' or 'SUPERMARKET'");
            return;
        }

        if (!Objects.equals(statusString, "ACCEPTED") && !Objects.equals(statusString, "DENIED") && !Objects.equals(statusString, "PENDING")) {
            showAlert("Error", "Please enter 'ACCEPTED', 'DENIED' or 'PENDING'");
            return;
        }

        if (betrag < 0) {//negativer Rechnungsbetrag
            showAlert("Error", "Negative Beträge sind nicht erlaubt!");
            return; // Update wird abgebrochen
        }

        InvoiceType typ = InvoiceType.valueOf((String) comboBoxTyp.getValue()); //wird erst hier initilisiert, weil sonst davor die fehlermeldung kommt und nicht das PopUp, deswegen oben als String für das PopUp deklariert
        InvoiceStatus status = InvoiceStatus.valueOf((String) comboBoxStatus.getValue());

        boolean success = updateInvoice(betrag, datum, typ, username, status, image, refund, id);
        if (success) {
            showAlertSuccess("Erfolg", "Rechnung wurde erfolgreich aktualisiert. Folgende Werte sind nun eingetragen:" +
                    "\nID: " + id +
                    "\nBetrag: " + betrag +
                    "\nDatum: " + datum + //Status wurde hier rausgenommen, wegen Platzgründen in der Erfolgsnachricht
                    "\nStatus: " + status +
                    "\nRefund: " + refund);
        } else {
            showAlert("Fehler", "Rechnung konnte nicht aktualisiert werden.");
        }
    }

    // Methode zum Anzeigen von Fehlerbenachrichtigungen (Alert)
    public void showAlert(String title, String message) { //generated by AI
        Alert alert = new Alert(Alert.AlertType.ERROR);  // oder AlertType.INFORMATION je nach Bedarf
        alert.setTitle(title);
        alert.setHeaderText("Error");  // Optional: Header Text entfernen
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showAlertSuccess(String title, String message) { //generated by AI
        Alert alert = new Alert(Alert.AlertType.INFORMATION);  // oder AlertType.INFORMATION je nach Bedarf
        alert.setTitle(title);
        alert.setHeaderText("Success");
        ImageView successIcon = new ImageView(new Image("file:src/test/resources/check_mark.png"));

        successIcon.setFitWidth(20);
        successIcon.setFitHeight(20);
        alert.setGraphic(successIcon);  // Dein grünes Häkchen-Icon hier
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goBackToAllInvoices(javafx.event.ActionEvent event) throws IOException{
        switchScene(event, "requestManagement.fxml");

    }

    public boolean isValidDate(String date) {//AI
        // Versuche, das Datum im Format yyyy-MM-dd zu parsen
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false); // Verhindert, dass ungültige Daten wie der 30. Februar akzeptiert werden
        try {
            sdf.parse(date); // Versucht, das Datum zu parsen
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    @FXML
    private void handleDeleteInvoice() throws SQLException { //wenn 404 Error meldung kommt, fehlt foto, bzw foto wurde bereits davor mal gelöscht
        // Bestätigungsdialog anzeigen
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this invoice?");

        // Wenn der Benutzer auf "OK" klickt, soll der Datensatz gelöscht werden
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Der Benutzer hat bestätigt, den Datensatz zu löschen
            String username = textfieldUsername.getText();
            LocalDate date = datePickerDatum.getValue();
            deleteInvoice(getConnection(), username, date);
        }
    }
   }