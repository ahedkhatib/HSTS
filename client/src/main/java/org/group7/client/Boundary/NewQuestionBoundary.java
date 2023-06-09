package org.group7.client.Boundary;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.group7.client.Control.NewQuestionController;
import org.group7.entities.Course;
import org.group7.entities.Subject;

import java.util.ArrayList;
import java.util.List;

public class NewQuestionBoundary extends Boundary {

    @FXML
    private ListView<Course> coursesListView;

    @FXML
    private Label labelQ;

    @FXML
    private TextField question;

    @FXML
    private RadioButton radio1;

    @FXML
    private RadioButton radio2;

    @FXML
    private RadioButton radio3;

    @FXML
    private RadioButton radio4;

    @FXML
    private Button saveBtn;

    @FXML
    private AnchorPane screen;

    @FXML
    private TextField solution1;

    @FXML
    private TextField solution2;

    @FXML
    private TextField solution3;

    @FXML
    private TextField solution4;

    @FXML
    private ComboBox<Subject> subjectCombo;

    @FXML
    private Label subjectLabel;

    private ToggleGroup toggleGroup;

    private List<Course> selectedCourses;

    @FXML
    public void save(ActionEvent event) {

        String[] answers = {solution1.getText(), solution2.getText(), solution3.getText(), solution4.getText()};
        List<Course> courseList = new ArrayList<>(coursesListView.getSelectionModel().getSelectedItems());

        controller.saveQuestion(question.getText(), courseList, subjectCombo.getSelectionModel().getSelectedItem(),
                toggleGroup.getToggles().indexOf(toggleGroup.getSelectedToggle()), answers);
    }

    public ComboBox<Subject> getSubjectCombo(){
        return  subjectCombo;
    }


    NewQuestionController controller;


    @Override
    public NewQuestionController getController() {
        return controller;
    }

    @FXML
    public void initialize() {
        controller = new NewQuestionController(this);
        super.setController(controller);

        coursesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        toggleGroup = new ToggleGroup();

        radio1.setToggleGroup(toggleGroup);
        radio2.setToggleGroup(toggleGroup);
        radio3.setToggleGroup(toggleGroup);
        radio4.setToggleGroup(toggleGroup);

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

                    if(selectedCourses.contains(course)){
                        hbox.getStyleClass().add("list-view-item-selected");
                        getStyleClass().add("list-view-item-selected");
                    } else{
                        hbox.getStyleClass().add("list-view-item");
                    }

                    String instructions = course.getCourseName();

                    Text instructionText = new Text(instructions);
                    instructionText.setFont(Font.font("Arial", 20));

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


        subjectCombo.setCellFactory(param -> new ComboBoxListCell<Subject>() {
            @Override
            public void updateItem(Subject subject, boolean empty) {
                super.updateItem(subject, empty);

                if (empty || subject == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(subject.getSubjectName());
                }
            }
        });

        subjectCombo.setButtonCell(new ListCell<Subject>() {
            @Override
            protected void updateItem(Subject subject, boolean empty) {
                super.updateItem(subject, empty);
                if (subject == null || empty) {
                    setText(null);
                } else {
                    setText(subject.getSubjectName());
                }
            }
        });

        subjectCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedCourses = new ArrayList<>();
            if(newValue != null)
                coursesListView.setItems(FXCollections.observableList(newValue.getCourseList()));
        });

        controller.getQuestions("#GetTeacherCourses");

    }

    public void done(){
        screen.setDisable(true);
    }
}
