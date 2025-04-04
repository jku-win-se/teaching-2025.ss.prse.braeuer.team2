package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import jku.se.InvoiceService;
import jku.se.Login;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SubmittedBillsController extends Controller {

    @FXML private GridPane gridInvoices;
    private final InvoiceService invoiceService = new InvoiceService();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        try {
            loadUserInvoices();
        } catch (SQLException e) {
            showAlert("Error", "Failed to load invoices: " + e.getMessage());
        }
    }

    private void loadUserInvoices() throws SQLException {
        String[] filters = FilterPanelUserController.getFilter();
        // Ensure we always filter by current user
        filters[2] = Login.getCurrentUsername();

        ResultSet resultSet = invoiceService.getFilteredInvoices(filters);
        if (resultSet != null) {
            displayResults(resultSet);
        }
    }

    private void displayResults(ResultSet rs) throws SQLException {
        clearGridContent();
        int row = 1;
        while (rs.next()) {
            addInvoiceToGrid(rs, row);
            row++;
        }
    }

    private void clearGridContent() {
        gridInvoices.getChildren().removeIf(node ->
                GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
    }

    private void addInvoiceToGrid(ResultSet rs, int row) throws SQLException {
        int id = rs.getInt("id");
        double amount = rs.getDouble("betrag");
        String type = rs.getString("typ");
        String dateStr = rs.getString("datum");
        String status = rs.getString("status");

        LocalDate invoiceDate = LocalDate.parse(dateStr, dateFormatter);
        LocalDate currentDate = LocalDate.now();
        boolean isCurrentMonth = invoiceDate.getMonth() == currentDate.getMonth()
                && invoiceDate.getYear() == currentDate.getYear();
        boolean showEditButton = isCurrentMonth && !"ACCEPTED".equals(status);

        gridInvoices.add(new Label(String.valueOf(id)), 0, row);
        gridInvoices.add(new Label(String.format("%.2f €", amount)), 1, row);
        gridInvoices.add(new Label(type), 2, row);
        gridInvoices.add(new Label(dateStr), 3, row);

        Label statusLabel = new Label(status);
        statusLabel.setStyle(getStatusStyle(status));
        gridInvoices.add(statusLabel, 4, row);

        HBox actionBox = new HBox();
        if (showEditButton) {
            Button editBtn = new Button("Edit");
            editBtn.setOnAction(e -> handleEditInvoice(id));
            actionBox.getChildren().add(editBtn);
        }
        gridInvoices.add(actionBox, 5, row);
    }

    private String getStatusStyle(String status) {
        if (status == null) return "";
        switch (status) {
            case "PENDING": return "-fx-text-fill: orange;";
            case "ACCEPTED": return "-fx-text-fill: green;";
            case "APPROVED": return "-fx-text-fill: darkgreen;";
            case "REJECTED": return "-fx-text-fill: red;";
            default: return "";
        }
    }

    private void handleEditInvoice(int invoiceId) {
        // Implementation for editing invoice
        System.out.println("Editing invoice: " + invoiceId);
    }

    @FXML
    private void handleBack(javafx.event.ActionEvent event) throws IOException {
        FilterPanelUserController.clearFilters();
        switchScene(event, "dashboardUser.fxml");
    }

    @FXML
    private void openFilter(javafx.event.ActionEvent event) throws IOException {
        switchScene(event, "filterPanelUser.fxml");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}