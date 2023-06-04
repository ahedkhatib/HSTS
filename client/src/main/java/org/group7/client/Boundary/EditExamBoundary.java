package org.group7.client.Boundary;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import org.group7.client.Control.EditExamController;
import org.group7.entities.Exam;
import org.group7.entities.Question;
import org.w3c.dom.Text;

public class EditExamBoundary extends Boundary{

    @FXML
    private AnchorPane emptyAP;

    @FXML
    private Text emptyText;

    @FXML
    private AnchorPane examAP;

    @FXML
    private Button backBtn;

    @FXML
    private ListView<Question> questionListView;

    @FXML
    private AnchorPane listAP;

    @FXML
    private ListView<Exam> examListView;

    private EditExamController controller;

    private Exam activeExam;

    @FXML
    public void backToList(ActionEvent event) {

        examAP.setDisable(true);
        examAP.setVisible(false);

        listAP.setDisable(false);
        listAP.setVisible(true);
    }


    @Override
    public EditExamController getController(){
        return controller;
    }

    @FXML
    public void initialize(){
        controller = new EditExamController(this);
        super.setController(controller);

        examListView.setCellFactory(param -> new ListCell<Exam>() {
            @Override
            public void updateItem(Exam exam, boolean empty) {
                super.updateItem(exam, empty);

                if (empty || exam == null) {
                    setStyle("-fx-background-color: transparent;");
                    setText(null);
                    setGraphic(null);
                } else {

                    setPrefWidth(USE_COMPUTED_SIZE);

                    HBox hbox = new HBox();

                    hbox.setPrefHeight(50);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    hbox.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color:black;");

                    String examName = exam.getExamName();

                    javafx.scene.text.Text reqText = new javafx.scene.text.Text(examName);
                    reqText.setFont(Font.font("Arial", 24));
                    javafx.scene.text.Text arrow = new javafx.scene.text.Text(">");
                    arrow.setFont(Font.font("Arial", 24));

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    hbox.getChildren().addAll(reqText, spacer, arrow);

                    setGraphic(hbox);
                }
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);

                if (selected && !isEmpty()) {
                    activeExam = getItem();

                    examAP.setDisable(false);
                    examAP.setVisible(true);

                    listAP.setDisable(true);
                    listAP.setVisible(false);

                }
            }
        });

    }
}
