package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jku.se.Database;
import jku.se.InvoiceStatus;
import jku.se.InvoiceType;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Optional;

import static jku.se.Database.*;

public class EditInvoiceController extends Controller{
    @FXML
    public TextField textfieldRechnungsID;
    @FXML
    public TextField textfieldTyp;
    @FXML
    public TextField textfieldUsername;
    @FXML
    public TextField textfieldStatus;
    @FXML
    public TextField textfieldImage;
    @FXML
    public TextField textfieldRefund;
    @FXML
    public TextField textFieldBetrag;
    @FXML
    public TextField textfieldDatum;

    // Diese Methode wird die Details der Rechnung laden
    public void loadInvoiceDetails(int invoiceId) {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT * FROM rechnungen WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, invoiceId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    // Befülle die Textfelder mit den Werten der Rechnung
                    textfieldRechnungsID.setText(String.valueOf(rs.getInt("id")));
                    textFieldBetrag.setText(rs.getString("betrag"));
                    textfieldDatum.setText(rs.getString("datum"));
                    textfieldTyp.setText(rs.getString("typ"));
                    textfieldUsername.setText(rs.getString("username"));
                    textfieldStatus.setText(rs.getString("status"));
                    textfieldImage.setText(rs.getString("image"));
                    textfieldRefund.setText(rs.getString("refund"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load invoice details: " + e.getMessage());
        }
    }

    // Hier kannst du eine Methode zum Speichern der Änderungen hinzufügen, z.B.:
    @FXML
    public void saveChanges() {
        // Hole die bearbeiteten Werte aus den Textfeldern und speichere sie in der Datenbank
        int id = Integer.parseInt(textfieldRechnungsID.getText());
        double betrag = Double.parseDouble(textFieldBetrag.getText());
        Date datum = Date.valueOf(textfieldDatum.getText());
        InvoiceType typ = InvoiceType.valueOf(textfieldTyp.getText());
        String username = textfieldUsername.getText();
        InvoiceStatus status = InvoiceStatus.valueOf(textfieldStatus.getText());
        String image = textfieldImage.getText();
        Double refund = Double.valueOf(textfieldRefund.getText());

        // Datum validieren
        if (!isValidDate(String.valueOf(datum))) {//ungültiges Datum
            showAlert("Error", "Please enter a valid date in the format yyyy-mm-dd");
            return;
        }

        if (refund != 3.0 && refund != 2.5) {//ungültiger Refund
            showAlert("Error", "Please enter a valid Amount: Either 3.0 OR 2.5!");
            return;
        }

        if (betrag < 0) {//negativer Rechnungsbetrag
            showAlert("Error", "Negative Beträge sind nicht erlaubt!");
            return; // Update wird abgebrochen
        }

        /*try (Connection conn = Database.getConnection()) {
            // SQL-Abfrage zum Aktualisieren des Datensatzes
            String updateQuery = "UPDATE rechnungen SET betrag = ?, datum = ?, typ = ?, username = ?, status = ?, image = ?, refund = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                // Setze die Parameter für die PreparedStatement
                stmt.setDouble(1, betrag);
                stmt.setDate(2, datum);
                stmt.setObject(3, typ, Types.OTHER);
                stmt.setString(4, username);
                stmt.setObject(5, status, Types.OTHER);
                stmt.setString(6, image);
                stmt.setDouble(7, refund);
                stmt.setInt(8, id);  // Wir setzen die ID, um den richtigen Datensatz zu aktualisieren

                // Führe das Update aus
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    showAlertSuccess("Success", "Invoice updated successfully!");
                } else {
                    showAlert("Error", "Failed to update the invoice.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to save changes: " + e.getMessage());
        }*/
        boolean success = updateInvoice(betrag, datum, typ, username, status, image, refund, id);
        if (success) {
            showAlertSuccess("Erfolg", "Rechnung wurde erfolgreich aktualisiert.");
        } else {
            showAlert("Fehler", "Rechnung konnte nicht aktualisiert werden.");
        }
    }

    // Methode zum Anzeigen von Fehlerbenachrichtigungen (Alert)
    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);  // oder AlertType.INFORMATION je nach Bedarf
        alert.setTitle(title);
        alert.setHeaderText("Error");  // Optional: Header Text entfernen
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showAlertSuccess(String title, String message) {
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

    private boolean isValidDate(String date) {
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
    private void handleDeleteInvoice() throws SQLException {
        // Bestätigungsdialog anzeigen
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this invoice?");

        // Wenn der Benutzer auf "OK" klickt, den Datensatz löschen
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Der Benutzer hat bestätigt, den Datensatz zu löschen
            String username = textfieldUsername.getText(); // Beispiel, nehme an, du hast das im Textfeld
            LocalDate date = LocalDate.parse(textfieldDatum.getText()); // Beispiel, nehme an, du hast das im Textfeld
            deleteInvoice(getConnection(), username, date);
        }
    }

    public static EditInvoiceController loadEditInvoiceController() throws IOException {
        FXMLLoader loader = new FXMLLoader(EditInvoiceController.class.getResource("/editInvoice.fxml"));
        Parent root = loader.load();  // Läd die FXML-Datei
        return loader.getController();  // Gibt den Controller zurück
    }

}