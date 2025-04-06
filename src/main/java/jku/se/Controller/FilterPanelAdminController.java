package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import jku.se.InvoiceStatus;
import jku.se.InvoiceType;
import java.io.IOException;

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

    @FXML
    private void applyFilters(javafx.event.ActionEvent event) throws IOException {
        activeFilters[0] = getFilterValue(checkboxRechnungsID, textfieldRechnungsID);
        activeFilters[1] = getTypFilterValue();
        activeFilters[2] = getFilterValue(checkboxBenutzer, textfieldBenutzer);
        activeFilters[3] = getStatusFilterValue();
        activeFilters[4] = checkboxCurrentMonth.isSelected() ? "current_month" : null;

        switchScene(event, "requestManagement.fxml");
    }

    private String getStatusFilterValue() {
        return checkboxStatus.isSelected() && comboBoxStatus.getValue() != null
                ? comboBoxStatus.getValue() : null;
    }

    private String getTypFilterValue() {
        return checkboxTyp.isSelected() && comboBoxTyp.getValue() != null
                ? comboBoxTyp.getValue() : null;
    }

    private String getFilterValue(CheckBox checkbox, TextField textField) {
        return checkbox.isSelected() && !textField.getText().isEmpty()
                ? textField.getText() : null;
    }

    @FXML
    private void handleBack(javafx.event.ActionEvent event) throws IOException {
        clearFilters();
        switchScene(event, "requestManagement.fxml");
    }
}