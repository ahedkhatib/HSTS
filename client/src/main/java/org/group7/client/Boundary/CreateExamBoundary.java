package org.group7.client.Boundary;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import org.group7.client.Client;
import org.group7.client.Control.CreateExamController;
import org.group7.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class CreateExamBoundary extends Boundary {

    CreateExamController controller;

    private boolean manualExam;

    private Course selectedCourse;

    public List<Subject> subjects;

    public List<String> subjectsNames;

    public List<Course> courses;

    public List<Question> questions;

    public List<String> selectedQuestions;

    public List<Exam> teacherExams;

    @FXML
    private AnchorPane screen;

    @FXML
    private TextField examNameText;

    @FXML
    private TextField durationText;

    @FXML
    private ComboBox<String> examTypeComboBox;

    @FXML
    public ComboBox<String> subjectsComboBox;

    @FXML
    private ComboBox<String> CoursesCombobox;

    @FXML
    private ListView<String> questionsListView;

    @FXML
    private AnchorPane selectQuestionAnchor;

    @FXML
    private TextField notesForStudentsText;

    @FXML
    private TextField notesForTeachersText;

    @FXML
    private Button saveBtn;

    private ObservableMap<String, String> questionGrades = FXCollections.observableHashMap();

    @Override
    public CreateExamController getController(){
        return controller;
    }

    @FXML
    public void initialize() {
        controller = new CreateExamController(this);
        super.setController(controller);

        examTypeComboBox.getItems().add("Manual exam");
        examTypeComboBox.getItems().add("Automated exam");
        manualExam = false;

        selectedCourse = new Course();
        CoursesCombobox.setVisible(false);
        selectQuestionAnchor.setVisible(false);

        questionsListView.setVisible(false);

        controller.getTeacherCourses("#GetTeacherCourses");
    }

    @FXML
    void examName(ActionEvent event) {
    }

    @FXML
    void selectType(ActionEvent event) {
        String selected = examTypeComboBox.getSelectionModel().getSelectedItem();
        if (selected.equals("Manual exam")) {
            manualExam = true;
        } else {
            manualExam = false;
        }
    }

    @FXML
    void selectSubject(ActionEvent event) {
        String select = subjectsComboBox.getSelectionModel().getSelectedItem();

        Subject selectedSubject = new Subject();
        CoursesCombobox.setVisible(true);
        for (Subject s : subjects) {
            if (Objects.equals(select, s.getSubjectName())) {
                selectedSubject = s;
            }
        }

        courses = selectedSubject.getCourseList();

        CoursesCombobox.getItems().clear();
        for (Course course : courses) {
            CoursesCombobox.getItems().add(course.getCourseName());
        }

    }

    @FXML
    void selectCourse() {
        String select = CoursesCombobox.getSelectionModel().getSelectedItem();

        selectQuestionAnchor.setVisible(true);
        questionsListView.setVisible(true);
        for (Course c : courses) {
            if (Objects.equals(select, c.getCourseName())) {
                selectedCourse = c;
            }
        }
        questions = selectedCourse.getQuestionList();
        questionsListView.getItems().clear();
        for (Question question : questions) {
            questionsListView.getItems().add(question.getInstructions());
        }
        questionsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedQuestions = new ArrayList<>();

        updateListView();

    }

    public void updateSubjectsComboBox() {
        if (!subjectsNames.isEmpty()) {
            for (String s : subjectsNames) {
                subjectsComboBox.getItems().add(s);
            }
        }
    }

    @FXML
    void selectQuestions(MouseEvent event) {
        String selected = questionsListView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            if (selectedQuestions.contains(selected)) {
                // Item is already selected, so remove it from selectedQuestions
                selectedQuestions.remove(selected);
            } else {
                // Item is not selected, so add it to selectedQuestions
                selectedQuestions.add(selected);
            }
            updateListView();

            // Clear the grade value for unselected questions
            for (String question : questionGrades.keySet()) {
                if (!selectedQuestions.contains(question)) {
                    questionGrades.put(question, null);
                }
            }
        }
    }

    void updateListView() {
        questionsListView.setCellFactory(listView -> new ListCell<String>() {
            private HBox cell;
            private Label questionLabel;
            private TextField gradeTextField;

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    if (cell == null) {
                        // Initialize the cell components
                        cell = new HBox();
                        cell.setPrefHeight(30);
                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);
                        questionLabel = new Label();
                        questionLabel.setStyle("-fx-text-fill: black;");
                        gradeTextField = new TextField();
                        gradeTextField.setPromptText("Enter grade");
                        gradeTextField.setPrefWidth(100);
                        gradeTextField.setPrefHeight(15);
                        questionLabel.setFont(Font.font("Arial", 20));
                        cell.getChildren().addAll(questionLabel, spacer, gradeTextField);
                    }

                    // Set the question text
                    questionLabel.setText(item);

                    if (selectedQuestions.contains(item)) {
                        // Apply selected style
                        cell.getStyleClass().add("list-view-item-selected");
                        getStyleClass().add("list-view-item-selected");
                        // Show and enable the grade TextField
                        gradeTextField.setVisible(true);
                        gradeTextField.setDisable(false);
                        // Set the grade text from the stored values
                        String grade = questionGrades.get(item);
                        if (grade != null) {
                            gradeTextField.setText(grade);
                        } else {
                            gradeTextField.clear();
                        }
                    } else {
                        // Clear style
                        cell.getStyleClass().add("list-view-item");
                        // Hide and disable the grade TextField
                        gradeTextField.setVisible(false);
                        gradeTextField.setDisable(true);
                    }

                    // Store the grade when the text changes
                    gradeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                        questionGrades.put(item, newValue);
                    });

                    setGraphic(cell);
                } else {
                    // Clear the cell components
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                    cell = null;
                    questionLabel = null;
                    gradeTextField = null;
                }
            }
        });
    }

    @FXML
    void addNotesForStudents(ActionEvent event) {
    }

    @FXML
    void addNotesForTeachers(ActionEvent event) {
    }

    @FXML
    void setDurationText(ActionEvent event) {
    }

    public void alert(String errorMessage) {
        Alert alert;
        alert = new Alert(Alert.AlertType.ERROR, String.format(errorMessage));
        alert.show();
    }

    @FXML
    void save(ActionEvent event) {

        if (examNameText.getText().isEmpty()) {
            alert("Please select exam name!");
            return;
        } else if (examTypeComboBox.getSelectionModel().getSelectedItem() == null) {
            alert("Please select exam type!");
            return;
        } else if (subjectsComboBox.getSelectionModel().getSelectedItem() == null) {
            alert("Please select exam subject!");
            return;
        } else if (CoursesCombobox.getSelectionModel().getSelectedItem() == null) {
            alert("Please select exam course!");
            return;
        } else if (questionsListView.getSelectionModel().getSelectedItems().size() == 0) {
            alert("Please select exam questions!");
            return;
        } else if (!(controller.isValidNumber(durationText.getText()))) {
            alert("'" + durationText.getText() + "' is not a valid exam duration!");
            return;
        }

        List<Integer> sendGrades = new ArrayList<>();
        List<Question> sendQuestions = new ArrayList<>();
        for (String s : selectedQuestions) {
            if (!(controller.isValidNumber(questionGrades.get(s))) || (Integer.parseInt(questionGrades.get(s)) < 0 || Integer.parseInt(questionGrades.get(s)) > 100)) {
                alert("'" + questionGrades.get(s) + "' is not a valid question grade for ' " + s + " '");
                return;
            }
            sendGrades.add(Integer.valueOf(questionGrades.get(s)));

            for (Question q : questions) {
                if (s.equals(q.getInstructions())) {
                    sendQuestions.add(q);
                }
            }
        }

        //checking if the sum of the grades is 100
        int sum = 0;
        for (Integer i : sendGrades) {
            sum += i;
        }
        if (sum != 100) {
            alert("Exam points are not equal to 100!");
            return;
        }


        int duration = 0;

        try {
            duration = Integer.parseInt(durationText.getText());

            if(duration <= 0 ){
                alert("Durations has to be a positive integer!");
                return;
            }
        } catch (Exception e) {
            alert("Durations has to be an integer!");
            return;
        }

        controller.save(examNameText.getText(), (manualExam) ? 2 : 1, duration, (Teacher) Client.getClient().getUser(),
                notesForTeachersText.getText(), notesForStudentsText.getText(), selectedCourse, sendQuestions, sendGrades);

    }

    public void done(){
        screen.setDisable(true);
    }
}
