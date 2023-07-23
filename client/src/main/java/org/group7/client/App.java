package org.group7.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jdk.jfr.Event;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.entities.Message;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class App extends Application {

    private static Scene scene;

    private static Stage appStage;

    private static Client client;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("serverConnection"));
        stage.setScene(scene);
        stage.setTitle("HSTS");
        stage.setResizable(false);
        Image newIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/grad-hat.png")));
        appStage = stage;
        appStage.getIcons().clear();
        appStage.getIcons().add(newIcon);
        appStage.show();
    }

    public static void setWindowTitle(String title) {
        appStage.setTitle(title);
    }

    public static void setContent(String pageName) throws IOException {
        Parent root = loadFXML(pageName);
        scene.setRoot(root);
        appStage.setScene(scene);
        appStage.sizeToScene();
        appStage.show();
    }

    public static void switchScreen(String screenName) {
        switch (screenName) {
            case "homepage" -> Platform.runLater(() -> {
                setWindowTitle("HSTS");
            });
            case "login" -> {

                client = Client.getClient();

                Platform.runLater(() -> {
                    setWindowTitle("Login");
                });
            }
        }

        Platform.runLater(() -> {
            try {
                setContent(screenName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @Override
    public void stop() throws Exception {
        client.closeConnection();
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }

}