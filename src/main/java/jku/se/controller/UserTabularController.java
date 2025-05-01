package jku.se.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import jku.se.Database;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserTabularController extends Controller {

    @FXML
    private GridPane gridUsers;

    @FXML
    public void initialize() {
        try {
            loadAllUsers();
        } catch (SQLException e) {
            showError("Error", "Failed to load users: " + e.getMessage());
        }
    }

    public void loadAllUsers() throws SQLException {
        Connection connection = Database.getConnection();
        String query = "SELECT first_name, last_name, username, role FROM accounts ORDER BY first_name ASC, last_name ASC;\n";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            displayUserResults(resultSet);
        }
    }

    private void displayUserResults(ResultSet rs) throws SQLException {
        clearGridContent();
        int row = 1;
        while (rs.next()) {
            addUserToGrid(rs, row);
            row++;
        }
    }

    private void clearGridContent() {
        gridUsers.getChildren().removeIf(node ->
                GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
    }

    private void addUserToGrid(ResultSet rs, int row) throws SQLException {
        String username = rs.getString("username");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String role = rs.getString("role");

        gridUsers.add(new Label(username), 0, row);
        gridUsers.add(new Label(firstName != null ? firstName : ""), 1, row);
        gridUsers.add(new Label(lastName != null ? lastName : ""), 2, row);
        gridUsers.add(new Label(role), 3, row);
    }

    @FXML
    private void handleBack(javafx.event.ActionEvent event) throws IOException {
        switchScene(event, "userOverviewDashboard.fxml");
    }
}