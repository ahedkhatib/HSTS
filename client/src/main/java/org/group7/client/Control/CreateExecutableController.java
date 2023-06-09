package org.group7.client.Control;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.CreateExecutableBoundary;
import org.group7.client.Client;
import org.group7.client.Events.CreateExecutableEvent;
import org.group7.entities.Exam;
import org.group7.entities.ExecutableExam;
import org.group7.entities.Message;

import java.util.ArrayList;
import java.util.List;

public class CreateExecutableController extends Controller {

    CreateExecutableBoundary boundary;

    private List<String> executablesIds;

    public CreateExecutableController(CreateExecutableBoundary boundary) {
        this.boundary = boundary;
        executablesIds = new ArrayList<>();

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void getTeacherExams(String req){

        if(!req.equals("#GetTeacherExams")){
            return;
        }

        try {
            Client.getClient().sendToServer(new Message(Client.getClient().getUser(), "#GetTeacherExams"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void unregisterController() {
        EventBus.getDefault().unregister(this);
    }

    public void saveExecutable(Exam exam, String examCode){

        Alert alert;

        if(examCode.length() != 4){

            alert = new Alert(Alert.AlertType.ERROR,
                    "Executable ID must be of size 4!"
            );

            alert.show();
            return;
        } else if(executablesIds.contains(examCode)){
            alert = new Alert(Alert.AlertType.ERROR,
                    "Executable ID already exists!"
            );

            alert.show();
            return;
        } else{
            alert = new Alert(Alert.AlertType.INFORMATION,
                    "Executable Saved!"
            );

            alert.show();
        }

        Object[] obj = {exam, examCode, Client.getClient().getUser()};

        try {
            Client.getClient().sendToServer(new Message(obj, "#SaveExecutable"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void setExams(CreateExecutableEvent event) {

        for(ExecutableExam exam : event.getExecutableExamList()){
            executablesIds.add(exam.getExamId());
        }

        boundary.getExamsCombo().setItems(FXCollections.observableList(event.getExamList()));
    }

}
