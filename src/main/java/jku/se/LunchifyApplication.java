package jku.se;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class LunchifyApplication extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("-----------------------Test1-------------------");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/login.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setTitle("Lunchify - Login");
        stage.show();
    }
}
