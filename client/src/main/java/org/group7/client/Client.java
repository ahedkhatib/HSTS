package org.group7.client;

import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;
import org.group7.client.Events.LoginEvent;
import org.group7.entities.*;
import org.group7.client.ocsf.AbstractClient;

import java.util.List;

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
        } else if (post.startsWith("#ExtraTime_")) {

            String temp = post.substring(11);

            if(temp.equals("Fail")){
               String m = ("Exam: " + message.getObject() + " could not be found!");
                Platform.runLater(() -> {
                    EventBus.getDefault().post(m);
                });
            }
            else {
                Platform.runLater(() -> {
                    EventBus.getDefault().post("Request Sent");
                });
            }
        } else if (post.startsWith("#GotRequestsList")) {
            List<ExtraTime> reqs = (List<ExtraTime>) message.getObject();
            Platform.runLater(() -> {
                EventBus.getDefault().post(reqs);
            });
        }
    }

    public static Client getClient() {
        if (client == null) {
            client = new Client("localhost", 3000);
        }
        return client;
    }

}
