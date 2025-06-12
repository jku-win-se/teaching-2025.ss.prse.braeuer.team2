package jku.se.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;
import jku.se.*;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

import static jku.se.Database.getConnection;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
public class ExportDataController extends Controller{

    @FXML
    public DatePicker datumExport;
    private double totalRefund;
    private double refundToPay;
    private List<InvoiceExport> invoices;

    @FXML
    public void initialize() {
        // Sprache auf Englisch setzen
        Locale.setDefault(Locale.ENGLISH); // <-- GANZ WICHTIG!

        datumExport.setShowWeekNumbers(false);

        datumExport.setChronology(java.time.chrono.IsoChronology.INSTANCE);

        // Monatsanzeige auf Englisch
        datumExport.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH); // <-- Englisch hier

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
                return ym.atDay(1); // Gibt z.B. 2025-04-01 zurück
            }
        });

        // Eingrenzen der auswählbaren Daten
        datumExport.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now().minusYears(5)) || date.isAfter(LocalDate.now().plusYears(1)));
            }
        });

        // Standardwert setzen (aktueller Monat, Tag 1)
        datumExport.setValue(LocalDate.now().withDayOfMonth(1));
    }

    public static String getMonthName(int month) {
        // Konvertiert die Monatszahl in den Monatsnamen
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
        refundToPay = 0.0;
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

                if (status == InvoiceStatus.ACCEPTED) {//Alle rausfiltern, die nicht accepted sind
                    refundToPay += refund;
                }
            }
        }

        return new InvoicesTotal(invoices, totalRefund, refundToPay);
    }

    public void exportInvoicesToJson(List<InvoiceExport> invoices, double totalRefund, double refundToPay, Path filePath, int year, int month) throws IOException {//AI
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        ExportData exportData = new ExportData();


        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("exportDate", LocalDate.now().toString());
        metadata.put("month", getMonthName(month));
        metadata.put("year", year);
        metadata.put("totalInvoices", invoices.size());
        metadata.put("currency", "EUR");
        exportData.metadata = metadata;


        double totalAmount = invoices.stream().mapToDouble(InvoiceExport::getSum).sum();
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalAmount", round(totalAmount));
        summary.put("totalRefund", round(totalRefund));
        summary.put("refundToPay", round(refundToPay));
        exportData.summary = summary;

        // User-spezifische Gruppierung
        Map<String, UserInvoices> userMap = new LinkedHashMap<>();

        for (InvoiceExport inv : invoices) {
            String user = inv.getUser();
            userMap.putIfAbsent(user, new UserInvoices(user));
            UserInvoices ui = userMap.get(user);

            Map<String, Object> invoiceMap = new LinkedHashMap<>();
            invoiceMap.put("id", inv.getId());
            invoiceMap.put("date", inv.getDate());
            invoiceMap.put("amount", round(inv.getSum()));  // Betrag rundet
            invoiceMap.put("type", inv.getTyp().name());
            invoiceMap.put("status", inv.getStatus().name());
            invoiceMap.put("refund", round(inv.getRefund()));  // Rückerstattung rundet

            ui.invoices.add(invoiceMap);
            ui.totalInvoices++;


            ui.totalAmount = round(ui.totalAmount + inv.getSum());  // Betrag nach der Berechnung rundet
            ui.totalRefund = round(ui.totalRefund + inv.getRefund());  // Rückerstattung nach der Berechnung rundet

            if (inv.getStatus() == InvoiceStatus.ACCEPTED) {
                ui.refundToPay = round(ui.refundToPay + inv.getRefundToPay());  // Refund to pay nur für "ACCEPTED" Rechnungen
            }
        }

        exportData.users = new ArrayList<>(userMap.values());

        // JSON schreiben
        mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), exportData);
    }

    @FXML
    public void exportButtonClicked(ActionEvent actionEvent) {//AI
        datumExport.setShowWeekNumbers(false);
        LocalDate selected = datumExport.getValue();
        int year = selected.getYear();
        int month = selected.getMonthValue();
        try {
            InvoicesTotal invoiceData = getInvoicesForMonth(year, month);

            // FileChooser öffnen
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Export invoice as JSON");

            // Standard-Dateiname
            String monthName = getMonthName(month).toLowerCase();
            fileChooser.setInitialFileName("invoices-" + monthName + "-" + year + ".json");

            // Dateityp-Filter (nur JSON-Dateien anzeigen)
            fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("JSON Files", "*.json"));

            // Fenster anzeigen (achte auf richtige Stage)
            java.io.File file = fileChooser.showSaveDialog(datumExport.getScene().getWindow());

            if (file != null) {
                // Exportieren, wenn User einen Speicherort gewählt hat
                exportInvoicesToJson(
                        invoiceData.getInvoices(),
                        invoiceData.getTotalRefund(),
                        invoiceData.getRefundToPay(),
                        file.toPath(),  // Pfad vom FileChooser
                        year,
                        month
                );
                showSuccess("Success", "Export succesfully saved:\n" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            showError("Error", "Export failed:\n" + e.getMessage());
        }
    }

    public void goBackAdminPanel(ActionEvent event) throws IOException {
        switchScene(event,"adminPanel.fxml");
    }


    public static class UserInvoices {
        public String username;
        public int totalInvoices;
        public double totalAmount;
        public double totalRefund;

        public double refundToPay;
        public List<Map<String, Object>> invoices;

        public UserInvoices(String username) {
            this.username = username;
            this.invoices = new ArrayList<>();
            this.totalAmount = 0.0;
            this.totalRefund = 0.0;
            this.refundToPay = 0.0;
            this.totalInvoices = 0;
        }
    }

    // Gesamtes Export-Objekt
    public static class ExportData {
        public Map<String, Object> metadata;
        public Map<String, Object> summary;
        public List<UserInvoices> users;
    }

    private static double round(double value) { //AI
        return new java.math.BigDecimal(value).setScale(2, java.math.RoundingMode.HALF_UP).doubleValue();
    }
}
