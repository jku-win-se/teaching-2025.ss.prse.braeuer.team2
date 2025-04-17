package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;
import jku.se.InvoiceExport;
import jku.se.InvoiceStatus;
import jku.se.InvoiceType;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

import static jku.se.Controller.EditInvoiceUserController.showAlertSuccess;
import static jku.se.Controller.RequestManagementController.showAlert;
import static jku.se.Database.getConnection;
import com.fasterxml.jackson.databind.ObjectMapper;
import jku.se.InvoicesTotal;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
public class ExportDataController {

    @FXML
    private DatePicker datumExport;
    private LocalDate datum;
    private double totalRefund;
    private List<InvoiceExport> invoices;

    @FXML
    public void initialize() {
        // Verhindert, dass Tage angezeigt werden
        datumExport.setShowWeekNumbers(false);

        // Nur Monatsanzeige
        datumExport.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return null;
                }
                YearMonth ym = YearMonth.parse(string, formatter);
                return ym.atDay(1); // gibt z.B. 2025-04-01 zurück
            }
        });

        // Optional: nur aktuelles Jahr + letzten 5 Jahre erlauben
        datumExport.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now().minusYears(5)) || date.isAfter(LocalDate.now().plusYears(1)));
            }
        });

        // Standardwert setzen
        datumExport.setValue(LocalDate.now().withDayOfMonth(1));
    }

    public String getMonthName(int month) {
        // Konvertiere die Monatszahl in den Monatsnamen
        Month m = Month.of(month);
        return m.getDisplayName(TextStyle.FULL, Locale.ENGLISH);  // "March", "April", etc.
    }


    public double getTotalRefund() {
        return totalRefund;
    }

    public InvoicesTotal getInvoicesForMonth(int year, int month) throws SQLException {
        String sql = """
        SELECT id, datum, username, betrag, typ, status, refund 
        FROM rechnungen
        WHERE EXTRACT(YEAR FROM datum) = ? AND EXTRACT(MONTH FROM datum) = ?
        ORDER BY datum
    """;

        List<InvoiceExport> invoices = new ArrayList<>();
        totalRefund = 0.0;

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, year);
            stmt.setInt(2, month);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate date = rs.getDate("datum").toLocalDate();
                double sum = rs.getDouble("betrag");
                InvoiceType type = InvoiceType.valueOf(rs.getString("typ"));       // wenn du ENUM hast
                InvoiceStatus status = InvoiceStatus.valueOf(rs.getString("status")); // ENUM
                double refund = rs.getDouble("refund");
                int id = rs.getInt("id");
                String user = rs.getString("username");

                invoices.add(new InvoiceExport(date, sum, type, status, refund, id, user));
                totalRefund += refund;
            }
        }

        return new InvoicesTotal(invoices, totalRefund);
    }

    public void exportInvoicesToJson(List<InvoiceExport> invoices, double totalRefund, Path filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        // Erstelle ein Map-Objekt, das sowohl die Rechnungen als auch die Refund-Summe enthält
        Map<String, Object> exportData = new HashMap<>();
        exportData.put("invoices", invoices);
        exportData.put("totalRefund", totalRefund);

        // Schreibe die Daten in die JSON-Datei
        mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), exportData);
    }

    @FXML
    public void ExportButtonClicked(ActionEvent actionEvent) {
        datumExport.setShowWeekNumbers(false);
        LocalDate selected = datumExport.getValue();
        int year = selected.getYear();
        int month = selected.getMonthValue();
        try {

            InvoicesTotal invoiceData = getInvoicesForMonth(year, month); // z.B. April 2025
            String monthName = getMonthName(month).toLowerCase();
            Path path = Path.of("invoices-" + monthName + "-" + year + ".json");
            exportInvoicesToJson(invoiceData.getInvoices(), invoiceData.getTotalRefund(), path);
            showAlertSuccess("Erfolg", "Export erfolgreich gespeichert:\n" + path.toAbsolutePath());
        } catch (Exception e) {
            showAlert("Fehler", "Export fehlgeschlagen:\n" + e.getMessage());
        }
    }
}
