package org.group7.client.Control;

import javafx.scene.control.Alert;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.TimeRequestBoundary;
import org.group7.client.Client;
import org.group7.entities.ExtraTime;
import org.group7.entities.Message;

public class TimeRequestController extends Controller {

    private TimeRequestBoundary boundary;

    public TimeRequestController(TimeRequestBoundary boundary){
        EventBus.getDefault().register(this);
        this.boundary = boundary;
    }

    public void sendRequest(String examId, String notes, String extraTime){
        try {

            int et = Integer.parseInt(extraTime);

            if(examId.length() != 4){
                Alert alert;
                alert = new Alert(Alert.AlertType.ERROR,
                        String.format("'" + examId + "' is not a valid exam id!")
                );
                alert.show();
            } else {

                Object[] obj = {examId, notes, et};

                try {
                    Client.getClient().sendToServer(new Message(obj, "#SendTimeRequest"));

                } catch (Exception e){
                    e.printStackTrace();
                }
            }

        } catch (Exception e){
            Alert alert;
            alert = new Alert(Alert.AlertType.ERROR,
                    "The added time is an integer in minutes!"
            );
            alert.show();
        }
    }

    @Subscribe
    public void checkData(String message){

        Alert alert;

        if(!message.startsWith("Ex") && !message.startsWith("Request")){
            return;
        }

        if(message.startsWith("Exam")){
            alert = new Alert(Alert.AlertType.ERROR,
                    message
            );
            alert.show();
        } else {
            alert = new Alert(Alert.AlertType.INFORMATION,
                    message
            );
            alert.show();
        }

    }

    @Override
    public void unregisterController(){
        EventBus.getDefault().unregister(this);
    }
}
