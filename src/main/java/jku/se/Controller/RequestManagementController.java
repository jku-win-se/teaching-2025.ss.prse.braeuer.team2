package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import jku.se.InvoiceService;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RequestManagementController extends Controller {

    @FXML private GridPane gridInvoices;
    private final InvoiceService invoiceService = new InvoiceService();

    @FXML
    public void initialize() {
        try {
            loadAndDisplayInvoices();
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load invoices: " + e.getMessage());
        }
    }

    private void loadAndDisplayInvoices() throws SQLException {
        String[] filters = FilterPanelController.getFilter();
        ResultSet resultSet = invoiceService.getFilteredInvoices(filters);
        displayResults(resultSet);
    }
    @FXML
    private void showCurrentMonthInvoices() {
        try {
            String[] filters = new String[5];
            filters[4] = "current_month"; // Date filter in position 4

            ResultSet resultSet = invoiceService.getFilteredInvoices(filters);
            displayResults(resultSet);
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load current month invoices: " + e.getMessage());
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
        String image = rs.getString("image");

        Hyperlink invoiceLink = new Hyperlink("Rechnung " + id);
        invoiceLink.setOnAction(event -> invoiceService.openInvoiceLink(image));

        gridInvoices.add(invoiceLink, 0, row);
        gridInvoices.add(new Label(String.valueOf(rs.getDouble("betrag"))), 1, row);
        gridInvoices.add(new Label(rs.getString("typ")), 2, row);
        gridInvoices.add(new Label(rs.getString("datum")), 3, row);
        gridInvoices.add(new Label(rs.getString("username")), 4, row);
        gridInvoices.add(new Label(rs.getString("status")), 5, row);

        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> {
            try {
                handleEditInvoice(id);
            } catch (IOException e) {
                showAlert("Error", "Failed to edit invoice: " + e.getMessage());
            }
        });
        gridInvoices.add(editButton, 6, row);
    }

    @FXML
    private void handleBack(javafx.event.ActionEvent event) throws IOException {
        FilterPanelController.clearFilters();
        switchScene(event, "adminPanel.fxml");
    }

    @FXML
    private void openFilter(javafx.event.ActionEvent event) throws IOException {
        switchScene(event, "filterPanel.fxml");
    }

    private void handleEditInvoice(int invoiceId) throws IOException {
        // Bearbeitungslogik hier implementieren
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

