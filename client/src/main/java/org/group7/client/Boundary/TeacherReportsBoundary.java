package org.group7.client.Boundary;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import org.group7.client.Client;
import org.group7.client.Control.Controller;
import org.group7.client.Control.TeacherReportsController;
import org.group7.entities.*;
import javafx.event.ActionEvent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class TeacherReportsBoundary extends Boundary {

    TeacherReportsController controller;

    public List<ExecutableExam> executableExams;

    @FXML
    private BarChart<String, Integer> gradeChart;

    @FXML
    private CategoryAxis gradeAxis;

    @FXML
    private NumberAxis countAxis;

    @FXML
    private ComboBox<ExecutableExam> executableExamsComboBox;

    @FXML
    private TableView<Result> statisticInfoTable;

    @FXML
    private TableColumn<Result, String> firstNameColumn;
    @FXML
    private TableColumn<Result, String> lastNameColumn;
    @FXML
    private TableColumn<Result, Integer> gradeColumn;
    @FXML
    private TableColumn<Result, String> teacherNoteColumn;
    @FXML
    private TableColumn<Result, String> timeColumn;
    @FXML
    private TableColumn<Result, Boolean> timeUpColumn;
    @FXML
    private TableColumn<Result, Boolean> ApprovedColumn;

    @FXML
    private Label averageLabel;

    @FXML
    private Label medianLabel;

    @FXML
    private Label failedLabel;

    @FXML
    private Label inTimeLabel;

    @FXML
    private Label passedLabel;

    private ObservableList<Result> resultList;

    @Override
    public TeacherReportsController getController() {
        return controller;
    }

    private ExecutableExam selectedExam;

    private void updateGradeChart(int[] distribution) {
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Grade Range");

        String[] gradeRanges = {"0-10", "11-20", "21-30", "31-40", "41-50", "51-60", "61-70", "71-80", "81-90", "91-100"};

        for (int i = 0; i < gradeRanges.length; i++) {
            series.getData().add(new XYChart.Data<>(gradeRanges[i], distribution[i]));
        }

        gradeChart.getData().setAll(series);

        countAxis.setTickUnit(1);
        countAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(countAxis) {
            @Override
            public String toString(Number object) {
                int intValue = object.intValue();
                if (object.doubleValue() == intValue) {
                    return String.valueOf(intValue);
                } else {
                    return "";
                }
            }
        });
    }

    @FXML
    void selectExam() {

        statisticInfoTable.getItems().clear();
        averageLabel.setText("Average: ");
        medianLabel.setText("Median: ");
        passedLabel.setText("Passed percentage: ");
        failedLabel.setText("Failed percentage: ");
        inTimeLabel.setText("finished in time percentage: ");

        if (selectedExam != null) {
            resultList = FXCollections.observableArrayList();
            for (ExecutableExam exam : executableExams) {
                if (selectedExam.equals(exam)) {

                    updateGradeChart(exam.getDistribution());

                    int passedStudents = 0;
                    int numOfFinishedInTime = 0;

                    for (Student s : exam.getStudentList()) {
                        Result studentResult = findStudentResult(s);
                        if (studentResult != null) {
                            resultList.add(studentResult);
                            if (studentResult.getGrade() >= 51) {
                                passedStudents++;
                            }
                            if (!studentResult.isTimeUp()) {
                                numOfFinishedInTime++;
                            }

                        }
                    }

                    double passed = ((double) passedStudents / exam.getStudentList().size()) * 100;
                    averageLabel.setText("Average: " + String.format("%.2f", exam.getAverage()));
                    medianLabel.setText("Median: " + String.format("%.2f", exam.getMedian()));
                    passedLabel.setText("Passed percentage: " + String.format("%.2f", passed) + "%");
                    failedLabel.setText("Failed percentage: " + String.format("%.2f", (100 - passed)) + "%");
                    inTimeLabel.setText("finished in time percentage: " + String.format("%.2f", (((double) numOfFinishedInTime / exam.getStudentList().size()) * 100)) + "%");

                }
            }
            statisticInfoTable.setItems(resultList);
        }
    }

    // Helper method to find the corresponding Result object for a student and exam
    private Result findStudentResult(Student student) {
        for (Result result : student.getResultList()) {
            if (result.getExam().equals(executableExamsComboBox.getSelectionModel().getSelectedItem())
                    && !resultList.contains(result)) {
                return result;
            }
        }
        return null;
    }


    @FXML
    public void initialize() {
        controller = new TeacherReportsController(this);
        super.setController(controller);
        controller.GetResult("#getExecutableExam");

        selectedExam = null;

        executableExamsComboBox.setCellFactory(param -> new ComboBoxListCell<ExecutableExam>() {
            @Override
            public void updateItem(ExecutableExam exam, boolean empty) {
                super.updateItem(exam, empty);

                if (empty || exam == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(exam.getExam().getExamName() + " - " + exam.getExamId() + " - " +
                            exam.getTeacher().getFirstName() + " " + exam.getTeacher().getLastName());
                }
            }
        });

        executableExamsComboBox.setButtonCell(new ListCell<ExecutableExam>() {
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

        executableExamsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedExam = newValue;
                selectExam();
            }
        });


        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        teacherNoteColumn.setCellValueFactory(new PropertyValueFactory<>("teacherNote"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("elapsed"));
        timeUpColumn.setCellValueFactory(new PropertyValueFactory<>("timeUp"));
        ApprovedColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        timeUpColumn.setCellFactory(column -> new TableCell<Result, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText("");
                    if (item) {
                        setText("✓");
                    } else {
                        setText("x");
                    }
                    setAlignment(Pos.CENTER); // Set alignment to center
                }
            }
        });

        ApprovedColumn.setCellFactory(column -> new TableCell<Result, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText("");
                    if (item) {
                        setText("✓");
                    } else {
                        setText("x");
                    }
                    setAlignment(Pos.CENTER); // Set alignment to center
                }
            }
        });


        gradeAxis.setLabel("Grades");
        countAxis.setLabel("Count");
        countAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(countAxis) {
            @Override
            public String toString(Number object) {
                int intValue = object.intValue();
                if (object.doubleValue() == intValue) {
                    return String.valueOf(intValue);
                } else {
                    return String.format("%.2f", object.doubleValue());
                }
            }
        });

        // Create a series for each grade range
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Grade Range");

        String[] gradeRanges = {"0-10", "11-20", "21-30", "31-40", "41-50", "51-60", "61-70", "71-80", "81-90", "91-100"};

        for (int i = 0; i < gradeRanges.length; i++) {
            series.getData().add(new XYChart.Data<>(gradeRanges[i], 0));
        }

        gradeChart.getData().add(series);

        countAxis.setTickUnit(1);
    }

    public void updateExecutableExamsCombo() {

        executableExamsComboBox.getItems().clear();

        if (!executableExams.isEmpty()) {
            for (ExecutableExam e : executableExams) {
                if (e.getExam().getCreator().getUsername().equals(Client.getClient().getUser().getUsername())
                        || e.getTeacher().getUsername().equals(Client.getClient().getUser().getUsername())) {
                    if (!(executableExamsComboBox.getItems().contains(e))) {
                        executableExamsComboBox.getItems().add(e);
                    }
                }
            }
        }

        if (selectedExam != null) {

            for (ExecutableExam e : executableExams) {
                if (selectedExam.getExamId().equals(e.getExamId()))
                    selectedExam = e;
            }

            executableExamsComboBox.getSelectionModel().select(selectedExam);

            selectExam();
        }

    }

}
