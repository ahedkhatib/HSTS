package org.group7.client;


import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.greenrobot.eventbus.EventBus;
import org.group7.entities.*;
import org.group7.client.ocsf.AbstractClient;

import java.io.IOException;
import java.util.*;

public class Client extends AbstractClient {

    private static Client client = null;

    public static Message clientMessage = null;

    public ShowStudentsController showStudentsController;

    public ChangeGradeController changeGradeController;

    private Client(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        Message message = (Message) msg;
        String post = message.getMessage();

        switch (post) {
            case "#GotStudents" -> {
                Platform.runLater(() -> {
                    clientMessage = message;
                    showStudentsController.setStudents();
                });

            }
            case "#GotGrades" -> {
                Platform.runLater(() -> {
                    changeGradeController.setStudentName();
                    changeGradeController.setGrades();
                });
            }
            case "#GradeUpdated" -> {
                Platform.runLater(() -> {
                    clientMessage = message;
                    changeGradeController.setGrades();
                });
            }
        }

    }

    public static Client getClient() {
        if (client == null) {
            client = new Client("localhost", 3000);
        }
        return client;
    }

}
