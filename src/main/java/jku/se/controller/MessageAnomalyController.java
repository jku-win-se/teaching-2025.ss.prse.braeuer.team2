package jku.se.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import jku.se.DateUtils;

import java.io.IOException;
import java.sql.*;

import static jku.se.Database.getConnection;
import static jku.se.Login.getCurrentUsername;


public class MessageAnomalyController extends Controller{
    @FXML
    public void goBack(ActionEvent event) throws IOException {
        switchScene(event, "adminPanel.fxml");
    }

    @FXML
    private GridPane gridMessages;

    private static final Connection CONNECTION;

    static {
        try {
            CONNECTION = getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("Database connection failed", e);
        }
    }

    public MessageAnomalyController() throws SQLException {
        super();//Good practice PMD Empfehlung
    }

    @FXML
    public void initialize() {
        try {
            loadAndDisplayMessages();
        } catch (SQLException e) {
            showError("Database Error", "Failed to load Messages: " + e.getMessage());
        }
    }

    private void loadAndDisplayMessages() throws SQLException {
        ResultSet rs = getAllAnomalies(); // ruft alle Messages aus der Datenbank ab
        displayMessages(rs);
    }

    private void displayMessages(ResultSet rs) throws SQLException {
        clearGridContent();
        int row = 1;
        while (rs.next()) {
            addMessageToGrid(rs, row);
            row++;
        }
    }

    private void clearGridContent() {
        gridMessages.getChildren().removeIf(node ->
                GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
    }

    private void addMessageToGrid(ResultSet rs, int row) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("invoice_username");
        String message = rs.getString("message");
        Timestamp date = rs.getTimestamp("date");

        String formattedDateTime = DateUtils.formatToDateAndTime(date);

        Hyperlink userLink = new Hyperlink(username);
        userLink.setOnAction(event -> openUserDetails(username));
        gridMessages.add(userLink, 0, row);
        GridPane.setHalignment(userLink, HPos.CENTER);

        Label messageLabel = new Label(message);
        gridMessages.add(messageLabel, 1, row);
        GridPane.setHalignment(messageLabel, HPos.CENTER);

        Label dateLabel = new Label(formattedDateTime);
        gridMessages.add(dateLabel, 2, row);
        GridPane.setHalignment(dateLabel, HPos.CENTER);

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            try {
                deleteMessage(id);
            } catch (SQLException e) {
                showError("Error", "Database error when deleting the message: " + e.getMessage());
            }
        });
        gridMessages.add(deleteButton, 3, row);
        GridPane.setHalignment(deleteButton, HPos.CENTER);
    }

    private void openUserDetails(String username) {//AI
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/userSearchResults.fxml"));
            Parent root = loader.load();

            UserSearchResultsController controller = loader.getController();
            controller.loadUserData(username);

            Stage stage = new Stage();
            stage.setTitle("User details: " + username);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Error", "User details could not be loaded: " + e.getMessage());
        }
    }
    private void deleteMessage(int id) throws SQLException {
        String query = "DELETE FROM anomalies WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                //showAlertSuccess("Erfolg", "Nachricht erfolgreich gelöscht.");
                loadAndDisplayMessages(); // Nach dem Löschen die Liste neu laden
            } else {
                showError("Error", "The message could not be deleted because it has already been deleted.");
            }
        } catch (SQLException e) {
            showError("Error", "Message could not be deleted: " + e.getMessage());
        }
    }

    public ResultSet getAllAnomalies() throws SQLException {
        String query = "SELECT * FROM anomalies ORDER BY date DESC";
        PreparedStatement statement = CONNECTION.prepareStatement(query);
        return statement.executeQuery();
    }

    public static boolean hasNewMessages() throws SQLException {
        String query = "SELECT COUNT(*) FROM anomalies WHERE new_message = 'YES'";
        PreparedStatement statement = CONNECTION.prepareStatement(query);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0; // Wenn mehr als 0, gibt es neue Nachrichten
        }
        return false;
    }

    public static void markMessageAsRead() throws SQLException {//AI
        String updateQuery = "UPDATE anomalies SET new_message = 'NO'";
        PreparedStatement statement = CONNECTION.prepareStatement(updateQuery);
        statement.executeUpdate();
    }
}
