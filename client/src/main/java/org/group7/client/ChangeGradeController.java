package org.group7.client;

import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import org.group7.entities.Message;
import org.group7.entities.Temp;
import org.group7.entities.TempGrade;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

import static org.group7.client.Client.clientMessage;

public class ChangeGradeController {

    @FXML
    private ListView<TempGrade> gradesList;

    @FXML
    private Text studentName;

    @FXML
    private Button returnBtn;

    @FXML
    public void returnToStudents(){
        App.switchScreen("showStudents");
    }

    public void setStudentName(){
        Temp student = (Temp) clientMessage.getObject();

        studentName.setText(student.getFirstName() + " " + student.getLastName());
        studentName.setFont(Font.font("Arial", 16));
    }

    public void setGrades(){

        Temp student = (Temp) clientMessage.getObject();

        gradesList.setItems(FXCollections.observableList(student.getGrades()));
    }

    @FXML
    public void initialize(){

        Client.getClient().changeGradeController = this;

        gradesList.setCellFactory(param -> new ListCell<>() {
            private final TextField textField = new TextField();
            private final Button button = new Button("Save");

            {
                button.setOnAction(event -> {
                    int newValue = Integer.parseInt(textField.getText());
                    if (newValue != getItem().getGrade()) {

                        Object[] obj = {getItem(), newValue, getItem().getTemp()};

                        try {
                            Message message = new Message(obj, "#UpdateGrade");
                            Client.getClient().sendToServer(message);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });

                textField.textProperty().addListener((observable, oldValue, newValue) -> {
                    button.setDisable(newValue.trim().isEmpty() || Integer.parseInt(newValue) == getItem().getGrade());
                });
            }

            @Override
            protected void updateItem(TempGrade item, boolean empty) {
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


        try {
            Message message = new Message(clientMessage.getObject(), "#GetGrades");
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}