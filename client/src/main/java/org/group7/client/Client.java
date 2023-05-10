package org.group7.client;


import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;
import org.group7.client.Control.ChangeGradeController;
import org.group7.client.Events.ResultListEvent;
import org.group7.client.Events.StudentListEvent;
import org.group7.client.Events.StudentResultEvent;
import org.group7.entities.*;
import org.group7.client.ocsf.AbstractClient;

public class Client extends AbstractClient {

    private static Client client = null;


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
                StudentListEvent studentEvent = new StudentListEvent(message);
                Platform.runLater(() -> {
                    EventBus.getDefault().post(studentEvent);
                });
            }
            case "#GotGrades" -> {
                StudentResultEvent studentResultEvent = new StudentResultEvent(message);
                Platform.runLater(() -> {
                    EventBus.getDefault().post(studentResultEvent);
                });
            }
            case "#GradeUpdated" -> {
                ResultListEvent resultListEvent = new ResultListEvent(message);
                Platform.runLater(() -> {
                    EventBus.getDefault().post(resultListEvent);
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
