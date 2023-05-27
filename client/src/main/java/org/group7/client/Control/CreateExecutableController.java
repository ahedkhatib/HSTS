package org.group7.client.Control;

import javafx.collections.FXCollections;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.Boundary.CreateExecutableBoundary;
import org.group7.client.Client;
import org.group7.entities.Exam;
import org.group7.entities.ExecutableExam;
import org.group7.entities.Message;

import java.util.List;

public class CreateExecutableController extends Controller {

    CreateExecutableBoundary boundary;

    public CreateExecutableController(CreateExecutableBoundary boundary) {
        this.boundary = boundary;

        EventBus.getDefault().register(this);

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
        Object[] obj = {exam, examCode, Client.getClient().getUser()};

        try {
            Client.getClient().sendToServer(new Message(obj, "#SaveExecutable"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void setExams(List<Exam> exams) {
        boundary.getExamsCombo().setItems(FXCollections.observableList(exams));
    }

}
