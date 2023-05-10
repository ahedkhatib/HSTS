package org.group7.client.Boundary;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.group7.client.App;
import org.group7.client.Client;
import org.group7.client.Control.ChangeGradeController;
import org.group7.client.Events.ResultListEvent;
import org.group7.client.Events.StudentResultEvent;
import org.group7.entities.Message;
import org.group7.entities.Result;
import org.group7.entities.Student;

import java.io.IOException;
import java.util.List;

public class ChangeGradeBoundary {

    private ChangeGradeController controller;

    @FXML
    public ListView<Result> gradesList;

    @FXML
    public Text studentName;

    @FXML
    public Button returnBtn;

    @FXML
    public void returnToStudents() {
        controller.unregisterController();
        App.switchScreen("showStudents");
    }

    @FXML
    public void initialize() {

        controller = new ChangeGradeController();
        controller.setBoundary(this);

        gradesList.setCellFactory(param -> new ListCell<>() {
            private final TextField textField = new TextField();
            private final Button button = new Button("Save");

            {
                button.setOnAction(event -> {
                    int newValue = Integer.parseInt(textField.getText());
                    if (newValue != getItem().getGrade()) {

                        Object[] obj = {getItem(), newValue, getItem().getStudent()};

                        controller.changeGrade(obj);
                    }
                });

                textField.textProperty().addListener((observable, oldValue, newValue) -> {
                    button.setDisable(newValue.trim().isEmpty() || Integer.parseInt(newValue) == getItem().getGrade());
                });
            }

            @Override
            protected void updateItem(Result item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {

                    setPrefWidth(USE_COMPUTED_SIZE);

                    HBox hbox = new HBox();

                    textField.setText(Integer.toString(item.getGrade()));
                    button.setDisable(true);
                    textField.setFont(Font.font("Arial", 16));

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    hbox.getChildren().addAll(textField, spacer, button);

                    setGraphic(hbox);
                }
            }
        });
    }
}