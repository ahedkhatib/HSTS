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
import org.group7.entities.Exam;
import org.group7.entities.Question;
import org.group7.entities.Subject;
import org.group7.entities.Teacher;

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

    private EditExamController controller;

    private Exam activeExam;

    private List<Question> selectedQuestions;

    private List<String> points;

    @FXML
    public void backToList(ActionEvent event) {

        controller.getExams();

        examAP.setDisable(true);
        examAP.setVisible(false);

        listAP.setDisable(false);
        listAP.setVisible(true);
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
        controller.save(examNameTF.getText(), activeExam.getType(), durationTF.getText(), (Teacher) Client.getClient().getUser(),
                teacherNoteTA.getText(), studentNoteTA.getText(), activeExam.getCourse(), selectedQuestions, points);
    }

    @FXML
    public void initialize() {
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

                    points = new ArrayList<>();

                    for (Integer q : activeExam.getQuestionPoints()) {
                        points.add(Integer.toString(q));
                    }

                    Subject subject = activeExam.getCourse().getSubject();

                    questionListView.setItems(FXCollections.observableArrayList(subject.getQuestionList()));

                    teacherNoteTA.setText(activeExam.getTeacherComments());
                    studentNoteTA.setText(activeExam.getStudentComments());
                    durationTF.setText(Integer.toString(activeExam.getDuration()));
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

                        hbox.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color:black; -fx-background-color: lightgray;");

                        int grade = questionGrade(getItem());

                        TextField gradeTf = new TextField(grade + "");
                        gradeTf.setFont(Font.font("Arial", 18));

                        gradeTf.textProperty().addListener((observable, oldValue, newValue) -> {
                            points.set(selectedQuestions.indexOf(question), newValue);
                        });

                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);

                        hbox.getChildren().addAll(instructionText, spacer, gradeTf);

                    } else {
                        hbox.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color:black;");
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
                    points.remove(selectedQuestions.indexOf(selectedQ));
                    selectedQuestions.remove(selectedQ);
                } else {
                    selectedQuestions.add(selectedQ);

                    int grade = questionGrade(selectedQ);

                    points.add(Integer.toString(grade));
                }

                // Update the item appearance
                questionListView.refresh();

            }
        });

        controller.getExams();
    }

    public int questionGrade(Question question){
        for(Question q : activeExam.getQuestionList()){
            if(question.getQuestionId() == q.getQuestionId()){
                return activeExam.getQuestionPoints().get(activeExam.getQuestionList().indexOf(question));
            }
        }
        return 0;
    }

}
