package org.group7.client.Boundary;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.group7.client.Client;
import org.group7.client.Control.EditExamController;
import org.group7.entities.*;

import java.util.ArrayList;
import java.util.List;

public class EditExamBoundary extends Boundary {

    @FXML
    private AnchorPane emptyAP;

    @FXML
    private Text emptyText;

    @FXML
    private AnchorPane examAP;

    @FXML
    private Text titleText;

    @FXML
    private Button backBtn;

    @FXML
    private ListView<Question> questionListView;

    @FXML
    private TextArea teacherNoteTA;

    @FXML
    private TextArea studentNoteTA;

    @FXML
    private Button saveBtn;

    @FXML
    private TextField durationTF;

    @FXML
    private TextField examNameTF;

    @FXML
    private AnchorPane listAP;

    @FXML
    private ListView<Exam> examListView;

    @FXML
    private ComboBox<String> typeCombo;

    private EditExamController controller;

    private Exam activeExam;

    private List<Question> selectedQuestions;

    private List<String> points;

    @FXML
    public void backToList(ActionEvent event) {
        controller.getExams("#GetTeacherCourses");

        examAP.setDisable(true);
        examAP.setVisible(false);

        listAP.setDisable(false);
        listAP.setVisible(true);

        activeExam = null;
    }

    public Exam getActiveExam() {
        return activeExam;
    }

    public ListView<Exam> getExamListView() {
        return examListView;
    }

    public AnchorPane getEmptyAP() {
        return emptyAP;
    }

    public AnchorPane getExamAP() {
        return examAP;
    }

    public AnchorPane getListAP() {
        return listAP;
    }

    @Override
    public EditExamController getController() {
        return controller;
    }

    @FXML
    void save(ActionEvent event) {

        boolean typeFlag = typeCombo.getSelectionModel().getSelectedItem().equals("Manual exam");

        List<String> selectedPoints = new ArrayList<>();
        for(int i = 0; i < questionListView.getItems().size(); i++){
            if(selectedQuestions.contains(questionListView.getItems().get(i))){
                selectedPoints.add(points.get(i));
            }
        }

        controller.save(examNameTF.getText(), (typeFlag) ? 2 : 1, durationTF.getText(), (Teacher) Client.getClient().getUser(),
                teacherNoteTA.getText(), studentNoteTA.getText(), activeExam.getCourse(), selectedQuestions, selectedPoints);
    }

    @FXML
    public void initialize() {
        controller = new EditExamController(this);
        super.setController(controller);

        typeCombo.getItems().add("Manual exam");
        typeCombo.getItems().add("Automated exam");

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
                    hbox.getStyleClass().add("list-view-item");

                    String examName = exam.getExamName();

                    Text reqText = new javafx.scene.text.Text(examName);
                    reqText.setFont(Font.font("Arial", 24));
                    Text arrow = new javafx.scene.text.Text(">");
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

                    examNameTF.setText(activeExam.getExamName());
                    titleText.setText(" - Course: " + activeExam.getCourse().getCourseName() + "\n - Subject: " + activeExam.getCourse().getSubject().getSubjectName());

                    selectedQuestions = new ArrayList<>();
                    selectedQuestions.addAll(activeExam.getQuestionList());

                    Course course = activeExam.getCourse();
                    questionListView.setItems(FXCollections.observableArrayList(course.getQuestionList()));

                    points = new ArrayList<>();
                    int counter = 0;
                    for(Question question : course.getQuestionList()){
                        if(selectedQuestions.contains(question)){
                            points.add(Integer.toString(activeExam.getQuestionPoints().get(counter)));
                            counter++;
                        } else {
                            points.add("0");
                        }
                    }

                    teacherNoteTA.setText(activeExam.getTeacherComments());
                    studentNoteTA.setText(activeExam.getStudentComments());
                    durationTF.setText(Integer.toString(activeExam.getDuration()));

                    if(activeExam.getType() == 1){
                        typeCombo.getSelectionModel().select(1);
                    } else {
                        typeCombo.getSelectionModel().select(0);
                    }
                }
            }
        });


        questionListView.setCellFactory(param -> new ListCell<Question>() {
            @Override
            public void updateItem(Question question, boolean empty) {
                super.updateItem(question, empty);

                if (empty || question == null) {
                    setStyle("-fx-background-color: transparent;");
                    setText(null);
                    setGraphic(null);
                } else {

                    setPrefWidth(USE_COMPUTED_SIZE);

                    HBox hbox = new HBox();

                    hbox.setPrefHeight(25);
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    String instructions = question.getInstructions();

                    Text instructionText = new Text(instructions);
                    instructionText.setFont(Font.font("Arial", 18));

                    if (selectedQuestions.contains(question)) {

                        hbox.getStyleClass().add("list-view-item-selected");
                        getStyleClass().add("list-view-item-selected");

                        TextField gradeTf = new TextField(points.get(getIndex()));
                        gradeTf.setFont(Font.font("Arial", 18));

                        gradeTf.textProperty().addListener((observable, oldValue, newValue) -> {
                            points.set(getIndex(), newValue);
                        });

                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);

                        hbox.getChildren().addAll(instructionText, spacer, gradeTf);

                    } else {
                        hbox.getStyleClass().add("list-view-item");
                        hbox.getChildren().addAll(instructionText);
                    }

                    setGraphic(hbox);
                }
            }
        });

        questionListView.setOnMouseClicked(event -> {

            Question selectedQ = questionListView.getSelectionModel().getSelectedItem();

            if (selectedQ != null) {
                if (selectedQuestions.contains(selectedQ)) {
                    selectedQuestions.remove(selectedQ);
                } else {
                    selectedQuestions.add(selectedQ);
                }

                // Update the item appearance
                questionListView.refresh();

            }
        });

        controller.getExams("#GetTeacherCourses");
    }

}
