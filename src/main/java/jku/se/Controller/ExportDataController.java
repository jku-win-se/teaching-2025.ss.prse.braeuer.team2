package jku.se.Controller;

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
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

import static jku.se.Controller.EditInvoiceUserController.showAlertSuccess;
import static jku.se.Controller.RequestManagementController.showAlert;
import static jku.se.Database.getConnection;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
public class ExportDataController extends Controller{

    @FXML
    private DatePicker datumExport;
    private LocalDate datum;
    private double totalRefund;
    private double refundToPay;
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

    public static String getMonthName(int month) {
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

                if (status == InvoiceStatus.ACCEPTED) {//Alle rausfiltern, die denied sind
                    refundToPay += refund;
                }
            }
        }

        return new InvoicesTotal(invoices, totalRefund, refundToPay);
    }

    public void exportInvoicesToJson(List<InvoiceExport> invoices, double totalRefund, double refundToPay, Path filePath, int year, int month) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        ExportData exportData = new ExportData();

        // Metadata
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("exportDate", LocalDate.now().toString());
        metadata.put("month", getMonthName(month));
        metadata.put("year", year);
        metadata.put("totalInvoices", invoices.size());
        metadata.put("currency", "EUR");
        exportData.metadata = metadata;

        // Summary
        double totalAmount = invoices.stream().mapToDouble(InvoiceExport::getSum).sum();
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalAmount", totalAmount);
        summary.put("totalRefund", totalRefund);
        summary.put("refundToPay", refundToPay);
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
            invoiceMap.put("amount", inv.getSum());
            invoiceMap.put("type", inv.getTyp().name());
            invoiceMap.put("status", inv.getStatus().name());
            invoiceMap.put("refund", inv.getRefund());

            ui.invoices.add(invoiceMap);
            ui.totalInvoices++;
            ui.totalAmount += inv.getSum();
            ui.totalRefund += inv.getRefund();
        }

        exportData.users = new ArrayList<>(userMap.values());

        // JSON schreiben
        mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), exportData);
    }

    @FXML
    public void ExportButtonClicked(ActionEvent actionEvent) {
        datumExport.setShowWeekNumbers(false);
        LocalDate selected = datumExport.getValue();
        int year = selected.getYear();
        int month = selected.getMonthValue();
        try {

            InvoicesTotal invoiceData = getInvoicesForMonth(year, month);
            String monthName = getMonthName(month).toLowerCase();
            Path path = Path.of("invoices-" + monthName + "-" + year + ".json");
            exportInvoicesToJson(invoiceData.getInvoices(), invoiceData.getTotalRefund(), invoiceData.getRefundToPay(), path, year, month);
            showAlertSuccess("Erfolg", "Export erfolgreich gespeichert:\n" + path.toAbsolutePath());
        } catch (Exception e) {
            showAlert("Fehler", "Export fehlgeschlagen:\n" + e.getMessage());
        }
    }

    public void goBackAdminPanel(ActionEvent event) throws IOException {
        switchScene(event,"adminPanel.fxml");
    }

    // Benutzerdaten für das JSON
    public static class UserInvoices {
        public String username;
        public int totalInvoices;
        public double totalAmount;
        public double totalRefund;
        public List<Map<String, Object>> invoices;

        public UserInvoices(String username) {
            this.username = username;
            this.invoices = new ArrayList<>();
            this.totalAmount = 0.0;
            this.totalRefund = 0.0;
            this.totalInvoices = 0;
        }
    }

    // Gesamtes Export-Objekt
    public static class ExportData {
        public Map<String, Object> metadata;
        public Map<String, Object> summary;
        public List<UserInvoices> users;
    }
}
