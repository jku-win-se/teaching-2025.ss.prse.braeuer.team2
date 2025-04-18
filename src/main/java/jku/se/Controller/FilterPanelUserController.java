package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import jku.se.InvoiceStatus;
import jku.se.InvoiceType;
import jku.se.Login;

import java.io.IOException;

public class FilterPanelUserController extends Controller {

    @FXML private CheckBox checkboxRechnungsID;
    @FXML private TextField textfieldRechnungsID;
    @FXML private CheckBox checkboxTyp;
    @FXML private ComboBox<String> comboBoxTyp;
    @FXML private CheckBox checkboxStatus;
    @FXML private ComboBox<String> comboBoxStatus;
    @FXML private CheckBox checkboxCurrentMonth;

    private static String[] activeFilters = new String[5]; // 0=id, 1=typ, 2=username, 3=status, 4=date

    @FXML
    public void initialize() {
        if (comboBoxTyp != null) {
            comboBoxTyp.getItems().addAll(
                    InvoiceType.SUPERMARKET.name(),
                    InvoiceType.RESTAURANT.name()
            );
        }

        if (comboBoxStatus != null) {
            comboBoxStatus.getItems().addAll(
                    InvoiceStatus.ACCEPTED.name(),
                    InvoiceStatus.PENDING.name(),
                    InvoiceStatus.DENIED.name()
            );
        }
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
        activeFilters[2] = Login.getCurrentUsername(); // Always filter by current user
        activeFilters[3] = getStatusFilterValue();
        activeFilters[4] = checkboxCurrentMonth.isSelected() ? "current_month" : null;

        switchScene(event, "submittedBills.fxml");
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