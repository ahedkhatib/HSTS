package org.group7.client.Boundary;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.App;
import org.group7.client.Client;
import org.group7.client.Control.ShowStudentsController;
import org.group7.client.Events.StudentListEvent;
import org.group7.entities.Message;
import org.group7.entities.Student;

import java.io.IOException;
import java.util.List;
public class ShowStudentsBoundary {

    private ShowStudentsController controller;

    @FXML
    public ListView<Student> studentList;

    @FXML
    public void initialize() {

        controller = new ShowStudentsController();
        controller.setBoundary(this);

        studentList.setCellFactory(param -> new ListCell<Student>() {
            @Override
            public void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);

                if (empty || student == null) {
                    setText(null);
                    setGraphic(null);
                } else {

                    setPrefWidth(USE_COMPUTED_SIZE);

                    HBox hbox = new HBox();

                    Text nameText = new Text(student.getFirstName() + " " + student.getLastName());
                    nameText.setFont(Font.font("Arial", 16));
                    Text arrow = new Text(">");
                    arrow.setFont(Font.font("Arial", 16));

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    hbox.getChildren().addAll(nameText, spacer, arrow);

                    setGraphic(hbox);
                }
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);

                if (selected && !isEmpty()) {
                    controller.getStudentResults(getItem());
                }
            }
        });
    }
}