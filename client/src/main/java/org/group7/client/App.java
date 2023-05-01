package org.group7.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.entities.Message;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class App extends Application {

    private static Scene scene;

    private static Stage stage;

    private static Client client;

    @Override
    public void start(Stage tStage) throws IOException {
        EventBus.getDefault().register(this);
        client = Client.getClient();
        client.openConnection();
        scene = new Scene(loadFXML("showStudents"));
        stage = tStage;
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static void setWindowTitle(String title) {
        stage.setTitle(title);
    }

    public static void setContent(String pageName) throws IOException {
        Parent root = loadFXML(pageName + ".fxml");
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void switchScreen(String screenName) {
        switch (screenName) {
            case "changeGrade" -> Platform.runLater(() -> {
                setWindowTitle("Change Grade");
                try {
                    setContent("changeGrade");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            case "showStudents" -> Platform.runLater(() -> {
                setWindowTitle("Students");
                try {
                    setContent("showStudents");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @Override
    public void stop() throws Exception {
        EventBus.getDefault().unregister(this);
        super.stop();
    }


    @Subscribe
    public void onMessageEvent(MessageEvent message) {
    }


    public static void main(String[] args) {
        launch();
    }

}