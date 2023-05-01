package org.group7.client;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.entities.Message;
import org.group7.entities.Temp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowStudentsController {

    @FXML
    private ListView<Temp> studentsList;

    private List<Temp> students;


    public ShowStudentsController() {
        this.students = new ArrayList<>();
        this.studentsList = new ListView<>();
    }

    public void setStudents(List<Temp> students) {
        this.students = students;
        updateList();
    }

    public void updateList(){
        ObservableList<Temp> students = FXCollections.observableArrayList(this.students);
        studentsList.setItems(students);

        // customize cell rendering
        studentsList.setCellFactory(param -> new ListCell<Temp>() {
            @Override
            protected void updateItem(Temp student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setText(null);
                } else {
                    setText(student.getFirstName() + " " + student.getLastName());
                }
            }
        });
    }

    @FXML
    public void initialize() {
        try {
            Message message = new Message(1,"Get Students");
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}