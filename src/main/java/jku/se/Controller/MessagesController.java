package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import jku.se.Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static jku.se.Controller.EditInvoiceUserController.showAlertSuccess;
import static jku.se.Controller.RequestManagementController.showAlert;
import static jku.se.Database.getConnection;
import static jku.se.Login.getCurrentUsername;

public class MessagesController extends Controller{

    @FXML
    private GridPane gridMessages;

    private final Connection connection = getConnection();

    public MessagesController() throws SQLException {
    }

    @FXML
    public void initialize() {
        try {
            loadAndDisplayMessages();
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load Messages: " + e.getMessage());
        }
    }

    private void loadAndDisplayMessages() throws SQLException {
        ResultSet rs = getAllMessages(); // ruft alle Messages aus der Datenbank ab
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
        String text = rs.getString("text");
        String date = rs.getString("created_at");
        String delete = "delete";

        gridMessages.add(new Label(text), 0, row);
        gridMessages.add(new Label(date), 1, row);
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            try {
                deleteMessage(id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        gridMessages.add(deleteButton, 2, row);
    }

    private void deleteMessage(int id) throws SQLException {
        String query = "DELETE FROM message WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                //showAlertSuccess("Erfolg", "Nachricht erfolgreich gelöscht.");
                loadAndDisplayMessages(); // Nach dem Löschen die Liste neu laden
            } else {
                showAlert("Fehler", "Die Nachricht konnte nicht gelöscht werden, da sie bereits gelöscht wurde.");
            }
        } catch (SQLException e) {
            showAlert("Fehler", "Nachricht konnte nicht gelöscht werden: " + e.getMessage());
        }
    }

    public ResultSet getAllMessages() throws SQLException {
        String query = "SELECT * FROM message WHERE username = ? ORDER BY created_at DESC";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, getCurrentUsername());
        return statement.executeQuery();
    }

    @FXML
    private void goBack(javafx.event.ActionEvent event) throws IOException {
        switchScene(event, "dashboardUser.fxml");
    }

    public boolean hasNewMessages(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM message WHERE username = ? AND new_message = 'YES'";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0; // Wenn mehr als 0, gibt es neue Nachrichten
        }
        return false;
    }

    public void markMessageAsRead(String username) throws SQLException {//AI
        String updateQuery = "UPDATE message SET new_message = 'NO' WHERE username = ? AND new_message = 'YES'";
        PreparedStatement statement = connection.prepareStatement(updateQuery);
        statement.setString(1, username);
        statement.executeUpdate();
    }
}

