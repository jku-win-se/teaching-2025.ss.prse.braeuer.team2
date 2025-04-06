package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import jku.se.Database;
import jku.se.InvoiceService;
import jku.se.InvoiceStatus;
import jku.se.InvoiceType;

import java.io.IOException;
import java.sql.*;

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
        String[] filters = FilterPanelAdminController.getFilter();
        ResultSet resultSet = invoiceService.getFilteredInvoices(filters);
        displayResults(resultSet);
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
        gridInvoices.add(new Label(String.format("%.2f €", rs.getDouble("betrag"))), 1, row);
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
        FilterPanelAdminController.clearFilters();
        switchScene(event, "adminPanel.fxml");
    }

    @FXML
    private void openFilter(javafx.event.ActionEvent event) throws IOException {
        switchScene(event, "filterPanelAdmin.fxml");
    }

    private void handleEditInvoice(int invoiceId) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/editInvoice.fxml"));
        AnchorPane editPane = loader.load();
        EditInvoiceController controller = loader.getController();

        controller.loadInvoiceDetails(invoiceId);

        Stage stage = (Stage) gridInvoices.getScene().getWindow();
        Scene scene = new Scene(editPane);
        stage.setScene(scene);
        stage.show();
    }


    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}