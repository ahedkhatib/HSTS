package org.group7.client;


import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;
import org.group7.entities.*;
import org.group7.client.ocsf.AbstractClient;

import java.io.IOException;
import java.util.*;

public class Client extends AbstractClient {

    private static Client client = null;

    public ShowStudentsController showStudentsController;

    private Client(String host, int port) {
        super(host, port);
        showStudentsController = new ShowStudentsController();
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        Message message = (Message) msg;
        String post = message.getMessage();

        switch (post) {
            case "Got Student List":
                Platform.runLater(() -> {
                    showStudentsController.setStudents((List<Temp>) message.getObject());
                });
                break;
        }

    }

    public static Client getClient() {
        if (client == null) {
            client = new Client("localhost", 3000);
        }
        return client;
    }

}
