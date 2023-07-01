package org.group7.client.Boundary;


import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.group7.client.Client;
import org.group7.client.Control.TeacherApprovalController;
import org.group7.entities.*;

import java.util.*;

public class TeacherApprovalBoundary extends Boundary {

    private TeacherApprovalController controller;

    @FXML
    private Button backBtn;

    @FXML
    private AnchorPane emptyAP;

    @FXML
    private Text emptyText;

    @FXML
    private AnchorPane resultsAP;

    @FXML
    private AnchorPane listAP;

    @FXML
    private ListView<Result> nonApprovedListView;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Text titleText;

    @FXML
    private ComboBox<ExecutableExam> teacherExamsComboBox;

    @FXML
    private Button changeGradeButton;

    @FXML
    private Button saveButton;

    @FXML
    private VBox changeGradeVbox;

    @FXML
    private TextField newGradeText;

    @FXML
    private TextField newGradeNoteText;

    @FXML
    private TextField optinalNoteForStudentsText;

    @FXML
    private Text optNoteDescribion;

    @FXML
    private VBox optionalNoteVbox;

    private Result activeResult;

    public List<ExecutableExam> executableExams;

    private boolean changeGradePressed = false; // Flag to track if "Change Grade" button was pressed

    private ExecutableExam selectedExam;


    @FXML
    void backToList(ActionEvent event) {

        activeResult = null;

        controller.GetResult("#getExecutableExam");

        nonApprovedListView.getSelectionModel().clearSelection();
        nonApprovedListView.refresh();

        newGradeText.clear();
        newGradeNoteText.clear();
        changeGradeVbox.setVisible(false);
        changeGradeVbox.setDisable(true);

        optinalNoteForStudentsText.clear();
        optionalNoteVbox.setVisible(true);
        optionalNoteVbox.setDisable(false);

        resultsAP.setDisable(true);
        resultsAP.setVisible(false);

        listAP.setDisable(false);
        listAP.setVisible(true);

        teacherExamsComboBox.setVisible(true);
        teacherExamsComboBox.setDisable(false);
    }

    @FXML
    public void initialize() {
        controller = new TeacherApprovalController(this);
        super.setController(controller);
        controller.GetResult("#getExecutableExam");

        selectedExam = null;

        teacherExamsComboBox.setCellFactory(param -> new ComboBoxListCell<ExecutableExam>() {
            @Override
            public void updateItem(ExecutableExam exam, boolean empty) {
                super.updateItem(exam, empty);

                if (empty || exam == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(exam.getExam().getExamName() + " - " + exam.getExamId());
                }
            }
        });

        teacherExamsComboBox.setButtonCell(new ListCell<ExecutableExam>() {
            @Override
            protected void updateItem(ExecutableExam exam, boolean empty) {
                super.updateItem(exam, empty);
                if (exam == null || empty) {
                    setText(null);
                } else {
                    setText(exam.getExam().getExamName() + " - " + exam.getExamId());
                }
            }
        });

        teacherExamsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {

                nonApprovedListView.getItems().clear();

                if (!newValue.getStudentList().isEmpty()) {

                    listAP.setDisable(false);
                    listAP.setVisible(true);

                    for (Student student : newValue.getStudentList()) {
                        if (!student.getResultList().isEmpty()) {
                            for (Result res : student.getResultList()) {
                                if ((res.getExam() == newValue) && (!res.isStatus())) {
                                    if (!nonApprovedListView.getItems().contains(res)) {
                                        nonApprovedListView.getItems().add(res);
                                    }
                                }
                            }
                        }
                    }
                } else {

                    emptyAP.setDisable(false);
                    emptyAP.setVisible(true);
                }

                selectedExam = newValue;

                nonApprovedListView.refresh();
            }
        });


        nonApprovedListView.setCellFactory(param -> new ListCell<Result>() {
            @Override
            public void updateItem(Result result, boolean empty) {
                super.updateItem(result, empty);

                if (empty || result == null) {
                    setStyle("-fx-background-color: transparent;");
                    setText(null);
                    setGraphic(null);
                } else {

                    setPrefWidth(USE_COMPUTED_SIZE);

                    HBox hbox = new HBox();

                    hbox.setPrefHeight(50);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    hbox.getStyleClass().add("list-view-item");

                    String studentResult = result.getFirstName() + " " + result.getLastName();

                    Text reqText = new Text(studentResult);
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
                    activeResult = getItem();

                    resultsAP.setDisable(false);
                    resultsAP.setVisible(true);

                    listAP.setDisable(true);
                    listAP.setVisible(false);

                    teacherExamsComboBox.setVisible(false);
                    teacherExamsComboBox.setDisable(true);

                    String timeString = Double.toString(activeResult.getElapsed());
                    int decimalIndex = timeString.indexOf('.');

                    String decimalPart;

                    if (decimalIndex != -1) {
                        decimalPart = timeString.substring(0, decimalIndex + 2);
                    } else {
                        decimalPart = timeString;
                    }

                    titleText.setText(activeResult.getExam().getExam().getExamName() + "\nGrade: "
                            + activeResult.getGrade() + ", Time: " + decimalPart + " / "
                            + activeResult.getExam().getExam().getDuration());

                    setQuestions(activeResult);
                }
            }

        });

    }

    public void updateExecutableExamsCombo() {

        teacherExamsComboBox.getItems().clear();

        if (!executableExams.isEmpty()) {
            for (ExecutableExam e : executableExams) {
                if (e.getExam().getCreator().getUsername().equals(Client.getClient().getUser().getUsername())
                || e.getTeacher().getUsername().equals(Client.getClient().getUser().getUsername())) {
                    if (!(teacherExamsComboBox.getItems().contains(e))) {
                        teacherExamsComboBox.getItems().add(e);
                    }
                }
            }
        }

        if(selectedExam != null) {

            for (ExecutableExam e : executableExams) {
                if(selectedExam.getExamId().equals(e.getExamId()))
                    selectedExam = e;
            }

            teacherExamsComboBox.getSelectionModel().select(selectedExam);

            if (!selectedExam.getStudentList().isEmpty()) {

                listAP.setDisable(false);
                listAP.setVisible(true);

                for (Student student : selectedExam.getStudentList()) {
                    if (!student.getResultList().isEmpty()) {
                        for (Result res : student.getResultList()) {
                            if ((res.getExam() == selectedExam) && (!res.isStatus())) {
                                if (!nonApprovedListView.getItems().contains(res)) {
                                    nonApprovedListView.getItems().add(res);
                                }
                            }
                        }
                    }
                }
            } else {

                emptyAP.setDisable(false);
                emptyAP.setVisible(true);
            }
        }
    }

    @FXML
    void changeGrade(ActionEvent event) {
        changeGradePressed = true;

        changeGradeVbox.setVisible(true);
        changeGradeVbox.setDisable(false);

        optionalNoteVbox.setVisible(false);
        optionalNoteVbox.setDisable(true);
    }

    @FXML
    void saveAndApprove(ActionEvent event) {
        double examAvg = activeResult.getExam().getAverage();
        List<Integer> grades = new ArrayList<>();
        double median = activeResult.getExam().getMedian();
        int[] distribution = new int[10];


        if (changeGradePressed) {
            if (newGradeNoteText.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please provide a note!");
                alert.show();
                return;
            } else if (newGradeText.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please provide a grade!");
                alert.show();
                return;
            } else if (!controller.isValidNumber(newGradeText.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "'" + newGradeText.getText() + "' is not a valid grade!");
                alert.show();
                return;
            } else {
                try {
                    int grade = Integer.parseInt(newGradeText.getText());
                    if (grade > 100 || grade < 0) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Please provide a grade from 0-100!");
                        alert.show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please provide a valid grade!");
                    alert.show();
                    return;
                }
            }

            //update avg
            examAvg *= activeResult.getExam().getStudentList().size();
            examAvg -= activeResult.getGrade();
            examAvg += Integer.parseInt(newGradeText.getText());
            examAvg /= activeResult.getExam().getStudentList().size();

            //update median
            List<Student> examstudents = activeResult.getExam().getStudentList();
            if (!examstudents.isEmpty()) {
                for (Student s : examstudents) {
                    if (!s.getResultList().isEmpty()) {
                        for (Result r : s.getResultList()) {
                            if (r.getExam().equals(teacherExamsComboBox.getSelectionModel().getSelectedItem())) {
                                grades.add(r.getGrade());
                            }
                        }
                    }
                }
            }

            grades.remove(grades.indexOf(activeResult.getGrade()));
            grades.add(Integer.parseInt(newGradeText.getText()));

            Collections.sort(grades);

            int size = grades.size();

            if (size % 2 == 0) {
                // Even number of elements, average the two middle elements
                int middleIndex = size / 2;
                int middleElement1 = grades.get(middleIndex - 1);
                int middleElement2 = grades.get(middleIndex);
                median = (middleElement1 + middleElement2) / 2;
            } else {
                // Odd number of elements, middle element is the median
                int middleIndex = size / 2;
                median = grades.get(middleIndex);
            }

            //update distribution
            int[] gradeRanges = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
            for (int i = 0; i < grades.size(); i++) {
                for (int j = 0; j < gradeRanges.length - 1; j++) {
                    if (grades.get(i) > gradeRanges[j] && grades.get(i) <= gradeRanges[j + 1]) {
                        distribution[j]++;
                    }
                }
            }

            controller.save(activeResult, true, newGradeText.getText(), newGradeNoteText.getText(), examAvg, median, distribution);
            changeGradePressed = false;
            newGradeText.clear();
            newGradeNoteText.clear();

        } else {
            controller.save(activeResult, false, "", optinalNoteForStudentsText.getText(), activeResult.getExam().getAverage(), activeResult.getExam().getMedian(), activeResult.getExam().getDistribution());
        }

        nonApprovedListView.getItems().remove(activeResult);

        resultsAP.setDisable(true);
        resultsAP.setVisible(false);

        listAP.setDisable(false);
        listAP.setVisible(true);

        teacherExamsComboBox.setVisible(true);
        teacherExamsComboBox.setDisable(false);
        changeGradeVbox.setVisible(false);
        changeGradeVbox.setDisable(true);

        optinalNoteForStudentsText.clear();
        optionalNoteVbox.setVisible(false);
        optionalNoteVbox.setDisable(true);
    }

    @Override
    public TeacherApprovalController getController() {
        return controller;
    }

    public VBox questionCard(int questionNum, Question question, int correctIndex, int toggleIndex) {

        VBox card = new VBox();
        card.setPrefWidth(scrollPane.getPrefWidth() / 2);
        card.setSpacing(35);
        card.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-border-color: gray; -fx-border-width: 1px;");

        Text questionText = new Text(questionNum + ".   " + question.getInstructions());
        questionText.setStyle("-fx-font-size: 24;");

        VBox answersBox = new VBox();
        answersBox.setSpacing(15);

        ToggleGroup toggleGroup = new ToggleGroup();

        for (int i = 0; i < 4; i++) {
            HBox answer = new HBox();
            answer.setAlignment(Pos.CENTER_LEFT);
            answer.setSpacing(25);

            Text answerText = new Text(question.getAnswerList()[i]);
            answerText.setStyle("-fx-font-size: 20;");

            RadioButton radioButton = new RadioButton();
            radioButton.setToggleGroup(toggleGroup);

            if (toggleIndex == i) {
                radioButton.setSelected(true);

                if (correctIndex != i) {
                    answerText.setStyle("-fx-text-fill: white; -fx-font-size: 20;");
                    answer.setStyle("-fx-background-color: F67280; ");
                }
            }

            if (correctIndex == i) {
                answerText.setStyle("-fx-text-fill: white; -fx-font-size: 20;");
                answer.setStyle("-fx-background-color: #91c79c; ");
            }

            answer.getChildren().addAll(radioButton, answerText);
            answersBox.getChildren().add(answer);
        }

        card.getChildren().addAll(questionText, answersBox);

        card.setDisable(true);

        return card;
    }

    public void setQuestions(Result result) {

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        int count = 1;

        Pane pane = new Pane();
        pane.setPrefWidth(scrollPane.getPrefWidth());

        VBox content = new VBox();
        content.setAlignment(Pos.CENTER);
        content.setSpacing(50);
        content.setLayoutX(scrollPane.getPrefWidth() / 4);

        // Add teacher's notes
        String examNote = result.getExam().getExam().getTeacherComments();

        VBox card = new VBox();
        card.setPrefWidth(scrollPane.getPrefWidth() / 2);
        card.setPrefHeight(scrollPane.getPrefHeight() / 2);
        card.setSpacing(35);
        card.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-border-color: gray; -fx-border-width: 1px;");

        Teacher creator = activeResult.getExam().getExam().getCreator();

        Text noteText = new Text("Teacher " + creator.getFirstName() + " " + creator.getLastName() + " says: " + examNote);
        noteText.setStyle("-fx-font-size: 20;");
        card.getChildren().add(noteText);

        if (examNote != null && !examNote.equals(""))
            content.getChildren().add(card);

        // Add Questions
        HashMap<Question, Integer> map = result.getAnswers();

        for (Map.Entry<Question, Integer> entry : map.entrySet()) {
            Question q = entry.getKey();
            card = questionCard(count, q, q.getCorrectAnswer(), result.getAnswers().get(q));
            content.getChildren().add(card);
            count++;
        }

        pane.getChildren().add(content);

        scrollPane.setStyle("-fx-padding: 10px;");
        scrollPane.setContent(pane);
        scrollPane.setDisable(false);
    }
}
