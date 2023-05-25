package org.group7.client.Control;

import javafx.scene.control.Alert;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.TimeRequestBoundary;
import org.group7.client.Client;
import org.group7.entities.Message;

public class TimeRequestController {

    private TimeRequestBoundary boundary;

    public TimeRequestController(TimeRequestBoundary boundary){
        EventBus.getDefault().register(this);
        this.boundary = boundary;
    }

    public void sendRequest(String examId, String notes){
        try {
            int id = Integer.parseInt(examId);

            if(id % 100000 == 0){
                Alert alert;
                alert = new Alert(Alert.AlertType.ERROR,
                        String.format("'" + examId + "' is not a valid exam id!")
                );
                alert.show();
            } else {

                Object[] obj = {id, notes};

                try {
                    Client.getClient().sendToServer(new Message(obj, "#SendTimeRequest"));

                } catch (Exception e){
                    e.printStackTrace();
                }
            }

        } catch (Exception e){
            Alert alert;
            alert = new Alert(Alert.AlertType.ERROR,
                    "Exam id is a 4 digit integer!"
            );
            alert.show();
        }
    }

    @Subscribe
    public void checkData(String message){
        Alert alert;
        alert = new Alert(Alert.AlertType.ERROR,
                message
        );
        alert.show();
    }
}
