package org.group7.client.Control;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
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


    public void invalidInputWarning(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR,
                String.format(error)
        );
        alert.setTitle("Error!");
        alert.setHeaderText("Error");
        alert.show();

    }

    public void changeGrade(Object obj) {

        Object[] object = (Object[]) obj;

        String newGrade = (String) object[1];

        try {
            int grade = Integer.parseInt(newGrade);

            if (grade >= 0 && grade <= 100) {

                try {
                    Message message = new Message(obj, "#UpdateGrade");
                    Client.getClient().sendToServer(message);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                invalidInputWarning("Invalid grade. Grade should be between 0 and 100.");
            }
        } catch (NumberFormatException e) {
            invalidInputWarning("Invalid grade format. Grade should be an integer.");
        }


    }

    public ChangeGradeController() {
        EventBus.getDefault().register(this);
    }
}