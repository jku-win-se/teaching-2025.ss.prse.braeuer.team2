package jku.se.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jku.se.InvoiceStatus;
import jku.se.InvoiceType;
import java.io.IOException;
import java.sql.SQLException;

public class FilterPanelAdminController extends Controller {

    @FXML private CheckBox checkboxRechnungsID;
    @FXML private TextField textfieldRechnungsID;
    @FXML private CheckBox checkboxTyp;
    @FXML private ComboBox<String> comboBoxTyp;
    @FXML private CheckBox checkboxBenutzer;
    @FXML private TextField textfieldBenutzer;
    @FXML private CheckBox checkboxStatus;
    @FXML private ComboBox<String> comboBoxStatus;
    @FXML private CheckBox checkboxCurrentMonth;

    private static String[] activeFilters = new String[5]; // 0=id, 1=typ, 2=username, 3=status, 4=date
    private RequestManagementController mainController;

    @FXML
    public void initialize() {
        comboBoxTyp.getItems().addAll(
                InvoiceType.SUPERMARKET.name(),
                InvoiceType.RESTAURANT.name()
        );

        comboBoxStatus.getItems().addAll(
                InvoiceStatus.ACCEPTED.name(),
                InvoiceStatus.PENDING.name(),
                InvoiceStatus.DENIED.name()
        );
    }

    public static String[] getFilter() {
        return activeFilters.clone();
    }

    public static void clearFilters() {
        activeFilters = new String[5];
    }

    public void setMainController(RequestManagementController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void applyFilters(ActionEvent event) {
        activeFilters[0] = getFilterValue(checkboxRechnungsID, textfieldRechnungsID);
        activeFilters[1] = getTypFilterValue();
        activeFilters[2] = getFilterValue(checkboxBenutzer, textfieldBenutzer);
        activeFilters[3] = getStatusFilterValue();
        activeFilters[4] = checkboxCurrentMonth.isSelected() ? "current_month" : null;

        // Refresh main view if possible
        if (mainController != null) {
            try {
                mainController.loadAndDisplayInvoices();
            } catch (SQLException e) {
                showAlert("Fehler", "Daten konnten nicht geladen werden");
            }
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        clearFilters();
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    private String getStatusFilterValue() {
        if (!checkboxStatus.isSelected() || comboBoxStatus.getValue() == null) {
            return null;
        }
        return comboBoxStatus.getValue();
    }

    private String getTypFilterValue() {
        if (!checkboxTyp.isSelected() || comboBoxTyp.getValue() == null) {
            return null;
        }
        return comboBoxTyp.getValue();
    }

    private String getFilterValue(CheckBox checkbox, TextField textField) {
        if (!checkbox.isSelected() || textField.getText().isEmpty()) {
            return null;
        }
        return textField.getText();
    }
}