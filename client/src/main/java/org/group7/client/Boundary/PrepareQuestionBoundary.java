package org.group7.client.Boundary;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.group7.client.Control.PrepareQuestionController;
import org.group7.entities.Course;
import org.group7.entities.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PrepareQuestionBoundary extends Boundary {

    PrepareQuestionController controller;

    public List<Course> cur;

    public List<Subject> sub;

    public List<String> all_courses;

    public List<Course> selected_courses;

    public List<Course> courses_after_selection;

    public List<String> subjects;

    public Subject selected_subject;

    @FXML
    private AnchorPane screen;

    @FXML
    private Label subjectLabel;

    @FXML
    private Label labelC;

    @FXML
    private Label labelQ;

    @FXML
    private ComboBox<String> courses;

    @FXML
    private ComboBox<String> subject;

    private ToggleGroup toggleGroup;

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
    private Button save_ques;

    @FXML
    private TextField solution1;

    @FXML
    private TextField solution2;

    @FXML
    private TextField solution3;

    @FXML
    private TextField solution4;

    @FXML
    private ListView<String> list;

    @FXML
    void selectCourse(javafx.scene.input.MouseEvent mouseEvent) {

        String selected = list.getSelectionModel().getSelectedItem();


        if (selected != null) {
            if (all_courses.contains(selected)) {
                // Item is already selected, so remove it from selectedQuestions
                all_courses.remove(selected);
            } else {
                // Item is not selected, so add it to selectedQuestions
                all_courses.add(selected);
            }
            updateListView();

            courses_after_selection.clear();
            for (String s : all_courses) {
                for (Course d : selected_courses) {
                    if (Objects.equals(s, d.getCourseName())) {
                        courses_after_selection.add(d);
                    }
                }
            }

            list.setVisible(true);
            solution1.setVisible(true);
            solution2.setVisible(true);
            solution3.setVisible(true);
            solution4.setVisible(true);
            question.setVisible(true);
            radio1.setVisible(true);
            radio2.setVisible(true);
            radio3.setVisible(true);
            radio4.setVisible(true);
            labelQ.setVisible(true);
        }

    }

    void updateListView() {
        list.setCellFactory(listView -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null && all_courses.contains(item)) {
                    // Apply selected style
                    setStyle("-fx-background-color: lightblue;");
                } else {
                    // Clear style
                    setStyle("");
                }

                setText(item);
            }
        });
    }

    @FXML
    void select_subject(ActionEvent event) {
        String select = subject.getSelectionModel().getSelectedItem();
        Subject subject_selected = new Subject();

        for (Subject d : sub) {
            if (Objects.equals(select, d.getSubjectName())) {
                subject_selected = d;
                selected_subject = d;
            }
        }

        cur = subject_selected.getCourseList();
        selected_courses = cur;
        list.getItems().removeAll();
        for (Course k : cur) {
            list.getItems().add(k.getCourseName());

        }
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        subject.setVisible(false);
        subjectLabel.setVisible(false);
        list.setVisible(true);
        labelC.setVisible(true);
    }

    @FXML
    void save_ques(ActionEvent event) {
        String[] answerList = new String[4];
        answerList[0] = solution1.getText();
        answerList[1] = solution2.getText();
        answerList[2] = solution3.getText();
        answerList[3] = solution4.getText();
        String instructions = question.getText();

        controller.save(instructions, courses_after_selection, selected_subject, toggleGroup.getToggles().indexOf(toggleGroup.getSelectedToggle()), answerList);
    }

    @FXML
    public void initialize() {
        courses_after_selection = new ArrayList<>();

        all_courses = new ArrayList<>();

        controller = new PrepareQuestionController(this);
        super.setController(controller);

        list.setVisible(false);
        solution1.setVisible(false);
        solution2.setVisible(false);
        solution3.setVisible(false);
        solution4.setVisible(false);
        question.setVisible(false);

        labelC.setVisible(false);
        labelQ.setVisible(false);

        toggleGroup = new ToggleGroup();

        radio1.setVisible(false);
        radio2.setVisible(false);
        radio3.setVisible(false);
        radio4.setVisible(false);
        radio1.setToggleGroup(toggleGroup);
        radio2.setToggleGroup(toggleGroup);
        radio3.setToggleGroup(toggleGroup);
        radio4.setToggleGroup(toggleGroup);
    }

    public void update_Subject() {
        if (!subjects.isEmpty()) {
            for (String c : subjects) {
                subject.getItems().add(c);
            }
        }
    }

    public void done() {
        screen.setDisable(true);
    }
}
