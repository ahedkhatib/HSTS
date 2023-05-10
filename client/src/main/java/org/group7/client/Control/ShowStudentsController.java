package org.group7.client.Control;

import javafx.collections.*;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.App;
import org.group7.client.Boundary.ShowStudentsBoundary;
import org.group7.client.Client;
import org.group7.client.Events.StudentListEvent;
import org.group7.entities.Message;
import org.group7.entities.Student;

import java.io.IOException;
import java.util.List;

public class ShowStudentsController {

    private ShowStudentsBoundary boundary;

    public void setBoundary(ShowStudentsBoundary boundary){
        this.boundary = boundary;
    }

    @Subscribe
    public void setStudents(StudentListEvent event) {
        boundary.studentList.setItems(FXCollections.observableList(event.getStudents()));
    }

    public void getStudentResults(Student student){

        unregisterController();

        Message message = new Message(student, "#GetGrades");
        try {
            Client.getClient().sendToServer(message);
            App.switchScreen("changeGrade");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void unregisterController() {
        EventBus.getDefault().unregister(this);
    }

    public ShowStudentsController() {

        EventBus.getDefault().register(this);

        try {
            Message message = new Message(1, "#GetStudents");
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}