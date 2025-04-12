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
import java.util.Objects;
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

    private int invoiceId;
    private double amount;
    private String date;
    private String typ;
    private String user;


    public void loadInvoiceDetails(int invoiceId) {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT * FROM rechnungen WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, invoiceId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    // Befüllt die Textfelder mit den Werten der Rechnung
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

   
    @FXML
    public void saveChanges() {
        // Holt die bearbeiteten Werte aus den Textfeldern und speichert sie in der Datenbank
        int id = Integer.parseInt(textfieldRechnungsID.getText());
        double betrag = Double.parseDouble(textFieldBetrag.getText());
        Date datum = Date.valueOf(textfieldDatum.getText());
        String typString = textfieldTyp.getText();
        String username = textfieldUsername.getText();
        String statusString = textfieldStatus.getText();
        String image = textfieldImage.getText();
        Double refund = Double.valueOf(textfieldRefund.getText());

        // Datum validieren
        if (!isValidDate(String.valueOf(datum))) {//ungültiges Datum
            showAlert("Error", "Please enter a valid date in the format yyyy-mm-dd");
            return;
        }

        if (!Objects.equals(typString, "RESTAURANT") && !Objects.equals(typString, "SUPERMARKET")) {
            showAlert("Error", "Please enter 'RESTAURANT' or 'SUPERMARKET'");
            return;
        }

        if (!Objects.equals(statusString, "ACCEPTED") && !Objects.equals(statusString, "DENIED") && !Objects.equals(statusString, "PENDING")) {
            showAlert("Error", "Please enter 'ACCEPTED', 'DENIED' or 'PENDING'");
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

        InvoiceType typ = InvoiceType.valueOf(textfieldTyp.getText()); //wird erst hier initialisiert, weil sonst davor die fehlermeldung kommt und nicht das PopUp, deswegen oben als String für das PopUp deklariert
        InvoiceStatus status = InvoiceStatus.valueOf(textfieldStatus.getText());

        boolean success = updateInvoice(betrag, datum, typ, username, status, image, refund, id);
        if (success) {
            showAlertSuccess("Erfolg", "Rechnung wurde erfolgreich aktualisiert.");
        } else {
            showAlert("Fehler", "Rechnung konnte nicht aktualisiert werden.");
        }
    }

    @FXML
    public void saveChangesUser() {//Wenn etwas fehlschlägt, fehlt meistens der Refund, oft Null dann kann nicht updaten
        // Holt die bearbeiteten Werte aus den Textfeldern und speichert sie in der Datenbank
        int id = Integer.parseInt(textfieldRechnungsID.getText());
        double betrag = Double.parseDouble(textFieldBetrag.getText());
        Date datum = Date.valueOf(textfieldDatum.getText());
        String typString= textfieldTyp.getText();

        // Datum validieren Abfangen für Tests eigentlich, wird in updateInvoice auch nochmal abgefragt
        if (!isValidDate(String.valueOf(datum))) {//ungültiges Datum
            showAlert("Error", "Please enter a valid date in the format yyyy-mm-dd");
            return;
        }

        if (!Objects.equals(typString, "RESTAURANT") && !Objects.equals(typString, "SUPERMARKET")) {
            showAlert("Error", "Please enter 'RESTAURANT' or 'SUPERMARKET'");
            return;
        }

        if (betrag < 0) {//negativer Rechnungsbetrag
            showAlert("Error", "Negative Beträge sind nicht erlaubt!");
            return; // Update wird abgebrochen
        }
        InvoiceType typ = InvoiceType.valueOf(textfieldTyp.getText());
        String username = getInvoiceUsername(id);
        InvoiceStatus status = InvoiceStatus.valueOf(getInvoiceStatus(id));
        String image = getInvoiceImage(id);
        double refund = getInvoiceRefund(id);

        boolean success = updateInvoice(betrag, datum, typ, username, status, image, refund, id);
        if (success) {
            showAlertSuccess("Erfolg", "Rechnung wurde erfolgreich aktualisiert.");
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

    public void setInvoice(int id, double amount, String typ, String date, String user) { //Wird für Ausfüllung von fxml Spalten benötigt
        this.invoiceId = id;
        this.amount = amount;
        this.typ = typ;
        this.date = date;
        this.user = user; //wird nur benötigt falls die datei gelöscht wird
        textfieldRechnungsID.setText(String.valueOf(id));
        textFieldBetrag.setText(String.valueOf(amount));
        textfieldTyp.setText(String.valueOf(typ));
        textfieldDatum.setText(String.valueOf(date));
    }

    @FXML
    private void goBackToAllInvoices(javafx.event.ActionEvent event) throws IOException{
        switchScene(event, "requestManagement.fxml");

    }

    @FXML
    private void goBackToInvoicesUser(javafx.event.ActionEvent event) throws IOException{
        switchScene(event, "submittedBills.fxml");

    }

    private boolean isValidDate(String date) {//AI
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
            LocalDate date = LocalDate.parse(textfieldDatum.getText()); 
            deleteInvoice(getConnection(), username, date);
        }
    }

    @FXML
    private void handleDeleteInvoiceUser() throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this invoice?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteInvoice(getConnection(), user, LocalDate.parse(date)); //globale user variable, weil kann bei User nicht aus Textfield hergenommen werden
        }
    }

    public static EditInvoiceController loadEditInvoiceController() throws IOException {
        FXMLLoader loader = new FXMLLoader(EditInvoiceController.class.getResource("/editInvoice.fxml"));
        Parent root = loader.load();  // Lädt die FXML-Datei
        return loader.getController();  // Gibt den Controller zurück
    }

}
