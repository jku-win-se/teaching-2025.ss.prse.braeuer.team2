package jku.se.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import jku.se.Database;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class UserSearchResultsController implements Initializable {

    @FXML private Label firstNameLabel;
    @FXML private Label lastNameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;
    @FXML private Label statusLabel;
    @FXML private Label failedAttemptsLabel;
    @FXML private Label createdAtLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialization can be done here if needed
    }

    public void loadUserData(String username) {
        try (Connection conn = Database.getConnection()) {
            // Use quoted identifiers for case-sensitive column names
            String query = "SELECT first_name, last_name, username, email, role, status, failed_attempts, \"createdAt\" FROM accounts WHERE username = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                firstNameLabel.setText(rs.getString("first_name"));
                lastNameLabel.setText(rs.getString("last_name"));
                usernameLabel.setText(rs.getString("username"));
                emailLabel.setText(rs.getString("email"));
                roleLabel.setText(rs.getString("role"));
                statusLabel.setText(rs.getString("status"));
                failedAttemptsLabel.setText(String.valueOf(rs.getInt("failed_attempts")));

                // Format the timestamp - use the quoted identifier here too
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                String formattedDate = dateFormat.format(rs.getTimestamp("createdAt"));
                createdAtLabel.setText(formattedDate);
            } else {
                setNotFoundState(username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            setErrorState();
        }
    }

    private void setNotFoundState(String username) {
        usernameLabel.setText("Benutzer '" + username + "' nicht gefunden");
        clearFields();
    }

    private void setErrorState() {
        usernameLabel.setText("Fehler beim Laden der Daten");
        clearFields();
    }

    private void clearFields() {
        firstNameLabel.setText("");
        lastNameLabel.setText("");
        emailLabel.setText("");
        roleLabel.setText("");
        statusLabel.setText("");
        failedAttemptsLabel.setText("");
        createdAtLabel.setText("");
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) usernameLabel.getScene().getWindow();
        stage.close(); 
    }
}