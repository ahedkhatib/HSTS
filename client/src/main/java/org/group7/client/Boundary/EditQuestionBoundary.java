package org.group7.client.Boundary;

import javafx.application.Platform;
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
import org.group7.client.Control.Controller;
import org.group7.client.Control.EditQuestionController;
import org.group7.entities.*;

import java.util.ArrayList;
import java.util.List;


public class EditQuestionBoundary extends Boundary {

    @FXML
    private AnchorPane listAP;

    @FXML
    private ListView<Question> questionListView;

    private ToggleGroup toggleGroup;

    @FXML
    private Button backBtn;

    @FXML
    private RadioButton radio1;

    @FXML
    private RadioButton radio2;

    @FXML
    private RadioButton radio3;

    @FXML
    private RadioButton radio4;

    @FXML
    private TextField question;

    @FXML
    private ListView<Course> coursesListView;

    @FXML
    private Button saveBtn;

    @FXML
    private TextField solution1;

    @FXML
    private TextField solution2;

    @FXML
    private TextField solution3;

    @FXML
    private TextField solution4;

    @FXML
    private AnchorPane emptyAP;

    @FXML
    private Text emptyText;

    @FXML
    private AnchorPane questionAP;

    private EditQuestionController controller;

    private List<Course> selectedCourses;

    private Question activeQuestion;

    public AnchorPane getListAP() {
        return listAP;
    }

    public ListView<Question> getQuestionListView() {
        return questionListView;
    }

    public AnchorPane getEmptyAP() {
        return emptyAP;
    }

    public AnchorPane getQuestionAP() {
        return questionAP;
    }

    @Override
    public EditQuestionController getController() {
        return controller;
    }

    @FXML
    void save(ActionEvent event) {
        String[] answerList = new String[4];
        answerList[0] = solution1.getText();
        answerList[1] = solution2.getText();
        answerList[2] = solution3.getText();
        answerList[3] = solution4.getText();
        String instructions = question.getText();

        controller.save(instructions, selectedCourses, activeQuestion.getSubject(), toggleGroup.getToggles().indexOf(toggleGroup.getSelectedToggle()), answerList);
    }

    @FXML
    public void backToList(ActionEvent event) {
        controller.getQuestions("#GetTeacherCourses");

        questionAP.setDisable(true);
        questionAP.setVisible(false);

        listAP.setDisable(false);
        listAP.setVisible(true);

        activeQuestion = null;
    }

    public Question getActiveQuestion() {
        return activeQuestion;
    }

    @FXML
    public void initialize() {
        controller = new EditQuestionController(this);
        super.setController(controller);

        toggleGroup = new ToggleGroup();

        radio1.setToggleGroup(toggleGroup);
        radio2.setToggleGroup(toggleGroup);
        radio3.setToggleGroup(toggleGroup);
        radio4.setToggleGroup(toggleGroup);

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

                    hbox.setPrefHeight(50);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    hbox.getStyleClass().add("list-view-item");

                    String examName = question.getInstructions();

                    Text reqText = new Text(examName);
                    reqText.setFont(Font.font("Arial", 24));
                    Text arrow = new Text(">");
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
                    activeQuestion = getItem();

                    questionAP.setDisable(false);
                    questionAP.setVisible(true);

                    listAP.setDisable(true);
                    listAP.setVisible(false);

                    question.setText(activeQuestion.getInstructions());

                    String[] answers = activeQuestion.getAnswerList();

                    solution1.setText(answers[0]);
                    solution2.setText(answers[1]);
                    solution3.setText(answers[2]);
                    solution4.setText(answers[3]);

                    toggleGroup.getToggles().get(activeQuestion.getCorrectAnswer()).setSelected(true);

                    selectedCourses = new ArrayList<>();
                    selectedCourses.addAll(activeQuestion.getCourseList());

                    coursesListView.setItems(FXCollections.observableList(activeQuestion.getSubject().getCourseList()));
                    Platform.runLater(() -> {
                        coursesListView.refresh();
                    });
                }
            }
        });

        coursesListView.setCellFactory(param -> new ListCell<Course>() {
            @Override
            public void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);

                if (empty || course == null) {
                    setStyle("-fx-background-color: transparent;");
                    setText(null);
                    setGraphic(null);
                } else {

                    setPrefWidth(USE_COMPUTED_SIZE);


                    HBox hbox = new HBox();

                    hbox.setPrefHeight(30);
                    hbox.getStyleClass().add("list-view-item");

                    String instructions = course.getCourseName();

                    Text instructionText = new Text(instructions);
                    instructionText.setFont(Font.font("Arial", 20));

                    if(selectedCourses.contains(course)) {
                        setStyle("-fx-background-color: F5EFE7;");
                        hbox.getStyleClass().add("list-view-item-selected");
                    }

                    hbox.getChildren().addAll(instructionText);
                    setGraphic(hbox);
                }
            }
        });

        coursesListView.setOnMouseClicked(event -> {

            Course course = coursesListView.getSelectionModel().getSelectedItem();

            if (course != null) {
                if (selectedCourses.contains(course)) {
                    selectedCourses.remove(course);
                } else {
                    selectedCourses.add(course);
                }

                // Update the item appearance
                coursesListView.refresh();
            }

        });

        controller.getQuestions("#GetTeacherCourses");
    }
}
