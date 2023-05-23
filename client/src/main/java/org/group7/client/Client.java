package org.group7.client;

import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;
import org.group7.client.Events.LoginEvent;
import org.group7.entities.*;
import org.group7.client.ocsf.AbstractClient;

public class Client extends AbstractClient {

    private static Client client = null;

    private static User user = null;

    private Client(String host, int port) {
        super(host, port);
    }

    public User getUser() {
        return user;
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        Message message = (Message) msg;
        String post = message.getMessage();

        if (post.startsWith("#Login_")) {
            LoginEvent event = new LoginEvent(new Message(message.getObject(), post.substring(7)));

            if (event.isSuccess()) {
                user = (User) message.getObject();
            }

            Platform.runLater(() -> {
                EventBus.getDefault().post(event);
            });
        } else if (post.startsWith("#Logout")) {
            user = null;
        }
    }

    public static Client getClient() {
        if (client == null) {
            client = new Client("localhost", 3000);
        }
        return client;
    }

}
