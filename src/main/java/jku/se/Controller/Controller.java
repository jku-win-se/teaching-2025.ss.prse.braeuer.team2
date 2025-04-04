package jku.se.Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import jku.se.Database;

public abstract class Controller {
    protected void switchScene(ActionEvent event, String fxmlFile) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/" + fxmlFile));
        Pane pane = fxmlLoader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        Connection conn = null;
        try {
            conn = Database.getConnection();
            Database.closeConnection(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        switchScene(event, "login.fxml");
    }
}
