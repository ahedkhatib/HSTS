package org.group7.client;

import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.group7.entities.Message;
import org.group7.entities.Temp;
import org.group7.entities.TempGrade;

import java.io.IOException;
import java.util.List;

import static org.group7.client.Client.clientMessage;

public class ShowStudentsController {

    @FXML
    private ListView<Temp> studentList;


    public void setStudents() {

        studentList.setItems(FXCollections.observableList((List<Temp>) clientMessage.getObject()));
    }


    @FXML
    public void initialize() {

        Client.getClient().showStudentsController = this;

        studentList.setCellFactory(param -> new ListCell<Temp>() {
            @Override
            public void updateItem(Temp temp, boolean empty) {
                super.updateItem(temp, empty);

                if (empty || temp == null) {
                    setText(null);
                    setGraphic(null);
                } else {

                    setPrefWidth(USE_COMPUTED_SIZE);

                    HBox hbox = new HBox();

                    Text nameText = new Text(temp.getFirstName() + " " + temp.getLastName());
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
                    Temp temp = getItem();
                    clientMessage = new Message(temp, "#SetGrades");
                    App.switchScreen("changeGrade");
                }
            }
        });


        if (clientMessage == null || !(clientMessage.getObject() instanceof List<?> &&
                ((List<?>) clientMessage.getObject()).stream().allMatch(obj -> obj.getClass() == Temp.class))) {
            try {
                Message message = new Message(1, "#GetStudents");
                Client.getClient().sendToServer(message);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            studentList.setItems(FXCollections.observableList((List<Temp>) clientMessage.getObject()));
        }
    }
}