package org.group7.client.Control;

import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.App;
import org.group7.client.Boundary.ChangeGradeBoundary;
import org.group7.client.Client;
import org.group7.client.Events.ResultListEvent;
import org.group7.client.Events.StudentResultEvent;
import org.group7.entities.Message;
import org.group7.entities.Student;
import org.group7.entities.Result;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.util.List;
import java.io.IOException;


public class ChangeGradeController {

    private ChangeGradeBoundary boundary;

    public void setBoundary(ChangeGradeBoundary boundary) {
        this.boundary = boundary;
    }

    @Subscribe
    public void setStudentName(StudentResultEvent event) {
        Student student = event.getStudent();

        boundary.studentName.setText(student.getFirstName() + " " + student.getLastName());
        boundary.studentName.setFont(Font.font("Arial", 16));

        boundary.gradesList.setItems(FXCollections.observableList(event.getResults()));
    }

    @Subscribe
    public void setGrades(ResultListEvent event) {

        List<Result> results = event.getResults();

        boundary.gradesList.setItems(FXCollections.observableList(results));
    }

    public void unregisterController() {
        EventBus.getDefault().unregister(this);
    }

    public void changeGrade(Object obj){
        try {
            Message message = new Message(obj, "#UpdateGrade");
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ChangeGradeController() {
        EventBus.getDefault().register(this);
    }
}