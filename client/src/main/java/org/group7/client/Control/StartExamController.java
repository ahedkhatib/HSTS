package org.group7.client.Control;

import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.StartExamBoundary;
import org.group7.client.Client;
import org.group7.client.Events.StartExamEvent;
import org.group7.entities.Message;

public class StartExamController extends Controller {

    private StartExamBoundary boundary;

    public StartExamController(StartExamBoundary boundary) {
        this.boundary = boundary;
        EventBus.getDefault().register(this);
    }

    public void getExam(String examId) {
        try{
            Client.getClient().sendToServer(new Message(examId, "#StartExam"));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean checkId(String id){
        if(id.length() != 9 || !id.matches("\\d+")){

            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Id is 9 digits, all numbers!"
            );
            alert.setTitle("Error!");
            alert.setHeaderText("Error: Incorrect Input");
            alert.show();

            return false;
        }

        return true;
    }

    @Subscribe
    public void startExam(StartExamEvent event){

        AnchorPane pane = null;

        if(!event.isExists()){
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Exam Doesn't Exist!"
            );
            alert.setTitle("Error!");
            alert.setHeaderText("Error: Incorrect Input");
            alert.show();
        } else {
            if(event.getType() == 1){
                pane = boundary.openAutoExam();
                boundary.setQuestions(event.getExam());
            } else {
                pane = boundary.openManualExam();
            }
        }
    }

}
