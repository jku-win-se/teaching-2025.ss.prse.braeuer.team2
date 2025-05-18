package jku.se.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jku.se.*;
import java.io.IOException;
import java.sql.*;

public class RequestManagementController extends Controller {

    @FXML private GridPane gridInvoices;
    private final InvoiceService invoiceService = new InvoiceService();
    private Stage filterStage;

    @FXML
    public void initialize() {
        try {
            loadAndDisplayInvoices();
        } catch (SQLException e) {
            showError("Database Error", "Failed to load invoices: " + e.getMessage());
        }
    }

    public void loadAndDisplayInvoices() throws SQLException {
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

    public void closeFilterWindow() {
        if (filterStage != null && filterStage.isShowing()) {
            filterStage.close();
            filterStage = null;
        }
    }

    private void clearGridContent() {
        gridInvoices.getChildren().removeIf(node ->
                GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
    }

    private void addInvoiceToGrid(ResultSet rs, int row) throws SQLException {
        int id = rs.getInt("id");
        String image = rs.getString("image");
        String invoiceSubmitter = rs.getString("username");
        String currentUser = Login.getUsername();

        Hyperlink invoiceLink = new Hyperlink("Rechnung " + id);
        invoiceLink.setOnAction(event -> invoiceService.openInvoiceLink(image));

        gridInvoices.add(invoiceLink, 0, row);
        gridInvoices.add(new Label(String.format("%.2f €", rs.getDouble("betrag"))), 1, row);
        gridInvoices.add(new Label(rs.getString("typ")), 2, row);
        gridInvoices.add(new Label(rs.getString("datum")), 3, row);
        gridInvoices.add(new Label(invoiceSubmitter), 4, row);

        Label statusLabel = new Label(rs.getString("status"));
        statusLabel.setStyle(getStatusStyle(rs.getString("status")));
        gridInvoices.add(statusLabel, 5, row);

        if (!currentUser.equals(invoiceSubmitter)) {
            Button editButton = new Button("Edit");
            editButton.setOnAction(event -> {
                try {
                    closeFilterWindow();
                    handleEditInvoice(id);
                } catch (IOException e) {
                    showError("Error", "Failed to edit invoice: " + e.getMessage());
                }
            });
            gridInvoices.add(editButton, 6, row);
        }
    }

    private String getStatusStyle(String status) {
        if (status == null) return "";
        return switch (status) {
            case "ACCEPTED" -> "-fx-text-fill: green;";
            case "PENDING" -> "-fx-text-fill: orange;";
            case "DENIED" -> "-fx-text-fill: red;";
            default -> "";
        };
    }

    @FXML
    private void handleBack(javafx.event.ActionEvent event) throws IOException {
        if (filterStage != null && filterStage.isShowing()) {
            filterStage.close();
        }
        FilterPanelAdminController.clearFilters();
        switchScene(event, "adminPanel.fxml");
    }

    @FXML
    private void openFilter(ActionEvent event) throws SQLException {
        loadAndDisplayInvoices();

        try {
            if (filterStage != null && filterStage.isShowing()) {
                filterStage.toFront();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/filterPanelAdmin.fxml"));
            Parent root = loader.load();

            FilterPanelAdminController controller = loader.getController();
            controller.setMainController(this);

            filterStage = new Stage();
            filterStage.setTitle("Filter invoices (Admin)");
            filterStage.setScene(new Scene(root));
            filterStage.initModality(Modality.NONE);
            filterStage.initOwner(((Node)event.getSource()).getScene().getWindow());

            filterStage.show();

        } catch (IOException e) {
            showError("Error", "Filter could not be opened!");
        }
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
}