package jku.se.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static jku.se.Database.getConnection;
import static jku.se.Login.getCurrentUsername;


public class MessageAnomalyController extends Controller{
    @FXML
    public void goBack(ActionEvent event) throws IOException {
        switchScene(event, "adminPanel.fxml");
    }

    @FXML
    private GridPane gridMessages;

    private static final Connection connection;

    static {
        try {
            connection = getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        String user = rs.getString("invoice_username");
        String message = rs.getString("message");
        String date = rs.getString("date");
        String delete = "delete";

        gridMessages.add(new Label(user), 0, row);
        gridMessages.add(new Label(message), 1, row);
        gridMessages.add(new Label(date), 2, row);
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            try {
                deleteMessage(id);
            } catch (SQLException e) {
                showError("Fehler", "Datenbankfehler beim Löschen der Nachricht: " + e.getMessage());
            }
        });
        gridMessages.add(deleteButton, 3, row);
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
                showError("Fehler", "Die Nachricht konnte nicht gelöscht werden, da sie bereits gelöscht wurde.");
            }
        } catch (SQLException e) {
            showError("Fehler", "Nachricht konnte nicht gelöscht werden: " + e.getMessage());
        }
    }

    public ResultSet getAllAnomalies() throws SQLException {
        String query = "SELECT * FROM anomalies ORDER BY date DESC";
        PreparedStatement statement = connection.prepareStatement(query);
        return statement.executeQuery();
    }

    public static boolean hasNewMessages() throws SQLException {
        String query = "SELECT COUNT(*) FROM anomalies WHERE new_message = 'YES'";
        PreparedStatement statement = connection.prepareStatement(query);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0; // Wenn mehr als 0, gibt es neue Nachrichten
        }
        return false;
    }

    public static void markMessageAsRead() throws SQLException {//AI
        String updateQuery = "UPDATE anomalies SET new_message = 'NO'";
        PreparedStatement statement = connection.prepareStatement(updateQuery);
        statement.executeUpdate();
    }
}
