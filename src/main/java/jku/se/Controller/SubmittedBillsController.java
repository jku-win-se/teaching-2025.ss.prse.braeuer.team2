package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
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

        filters[2] = Login.getCurrentUsername(); // always filter by current user

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

    private void addInvoiceToGrid(ResultSet rs, int row) throws SQLException {
        int id = rs.getInt("id");
        double amount = rs.getDouble("betrag");
        String image = rs.getString("image");
        String type = rs.getString("typ");
        String dateStr = rs.getString("datum");
        String status = rs.getString("status");
        String user = rs.getString("username");

        LocalDate invoiceDate = LocalDate.parse(dateStr, dateFormatter);
        LocalDate currentDate = LocalDate.now();

        Hyperlink invoiceLink = new Hyperlink("Rechnung " + id);
        invoiceLink.setOnAction(event -> invoiceService.openInvoiceLink(image));

        gridInvoices.add(invoiceLink, 0, row);
        gridInvoices.add(new Label(String.format("%.2f €", rs.getDouble("betrag"))), 1, row);
        gridInvoices.add(new Label(rs.getString("typ")), 2, row);
        gridInvoices.add(new Label(rs.getString("datum")), 3, row);
        Label statusLabel = new Label(status);
        statusLabel.setStyle(getStatusStyle(status));
        gridInvoices.add(statusLabel, 4, row);

        HBox actionBox = new HBox();

        boolean isCurrentMonth = invoiceDate.getMonth() == currentDate.getMonth() && invoiceDate.getYear() == currentDate.getYear();
        boolean showEditButton = isCurrentMonth && !"ACCEPTED".equals(status);

        if (showEditButton) {//lädt den EditInvoiceUser Fxml mit den aktuellen werten
            Button editBtn = new Button("Edit");
            editBtn.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/editInvoiceUser.fxml"));
                    Parent root = loader.load();

                    EditInvoiceUserController controller = loader.getController();
                    controller.setInvoice(id, amount, type, dateStr, user);

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            actionBox.getChildren().add(editBtn);
        }
        gridInvoices.add(actionBox, 5, row);
    }

    // Deepseek Anfang
    private void clearGridContent() {
        gridInvoices.getChildren().removeIf(node ->
                GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
    }
    // Deepseek Ende

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
        FilterPanelUserController.clearFilters();
        switchScene(event, "dashboardUser.fxml");
    }

    @FXML
    private void openFilter(javafx.event.ActionEvent event) throws IOException {
        switchScene(event, "filterPanelUser.fxml");
    }
}