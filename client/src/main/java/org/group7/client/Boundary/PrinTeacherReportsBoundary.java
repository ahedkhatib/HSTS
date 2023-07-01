package org.group7.client.Boundary;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.cell.ComboBoxListCell;
import org.group7.client.Control.PrinTeacherReportsController;
import org.group7.entities.Course;
import org.group7.entities.Exam;
import org.group7.entities.ExecutableExam;
import org.group7.entities.Teacher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PrinTeacherReportsBoundary extends Boundary {

    PrinTeacherReportsController controller;

    public List<ExecutableExam> exams;

    private Teacher selectedTeacher;

    private ExecutableExam selectedExam;

    private ExecutableExam selectedExam2;

    @FXML
    private ComboBox<Teacher> teachersCB;

    @FXML
    private Label averageLabel;

    @FXML
    private Label medianLabel;

    @FXML
    private NumberAxis countAxis1;

    @FXML
    private NumberAxis countAxis2;

    @FXML
    private ComboBox<ExecutableExam> examsCB1;

    @FXML
    private ComboBox<ExecutableExam> examsCB2;

    @FXML
    private Label examsInfo;

    @FXML
    private CategoryAxis gradeAxis1;

    @FXML
    private CategoryAxis gradeAxis2;

    @FXML
    private BarChart<String, Integer> gradeChart1;

    @FXML
    private BarChart<String, Integer> gradeChart2;

    @Override
    public PrinTeacherReportsController getController() {
        return controller;
    }

    @FXML
    void initialize() {
        controller = new PrinTeacherReportsController(this);
        super.setController(controller);
        exams = new ArrayList<>();

        averageLabel.setText("Average: 0 - 0");
        medianLabel.setText("Median: 0 - 0");

        examsInfo.setVisible(false);
        examsInfo.setDisable(true);

        examsCB2.setVisible(false);
        examsCB2.setDisable(true);

        examsCB1.setVisible(false);
        examsCB1.setDisable(true);

        gradeChart1.setVisible(false);
        gradeChart1.setDisable(true);

        gradeChart2.setVisible(false);
        gradeChart2.setDisable(true);

        selectedTeacher = null;
        selectedExam = null;
        selectedExam2 = null;

        teachersCB.setCellFactory(param -> new ComboBoxListCell<Teacher>() {
            @Override
            public void updateItem(Teacher teacher, boolean empty) {
                super.updateItem(teacher, empty);
                if (teacher == null || empty) {
                    setText(null);
                } else {
                    setText(teacher.getFirstName() + " " + teacher.getLastName());
                }
            }
        });

        teachersCB.setButtonCell(new ListCell<Teacher>() {
            @Override
            protected void updateItem(Teacher teacher, boolean empty) {
                super.updateItem(teacher, empty);
                if (teacher == null || empty) {
                    setText(null);
                } else {
                    setText(teacher.getFirstName() + " " + teacher.getLastName());
                }
            }
        });

        teachersCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedTeacher = newValue;
                selectTeacher();
            }
        });

        examsCB2.setCellFactory(param -> new ComboBoxListCell<ExecutableExam>() {
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

        examsCB2.setButtonCell(new ListCell<ExecutableExam>() {
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

        examsCB2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedExam = newValue;
                selectExam(selectedExam, 1);
            }
        });

        examsCB1.setCellFactory(param -> new ComboBoxListCell<ExecutableExam>() {
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

        examsCB1.setButtonCell(new ListCell<ExecutableExam>() {
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

        examsCB1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedExam2 = newValue;
                selectExam(selectedExam2, 2);
            }
        });

        controller.getTeachers("#GetData");

    }

    @FXML
    void selectTeacher() {
        averageLabel.setText("Average: 0 - 0");
        medianLabel.setText("Median: 0 - 0");
        examsInfo.setText("");
        updateGradeChart(gradeChart1, countAxis1, new int[10]); // reInitialize to 0's
        updateGradeChart(gradeChart2, countAxis2, new int[10]); // reInitialize to 0's

        exams.clear();
        exams.addAll(selectedTeacher.getExamList());


        if (!exams.isEmpty()) {

            examsCB1.setItems(FXCollections.observableList(exams));
            examsCB1.setVisible(true);
            examsCB1.setDisable(false);

            examsCB2.setItems(FXCollections.observableList(exams));
            examsCB2.setVisible(true);
            examsCB2.setDisable(false);

        } else {

            selectedExam = null;
            selectedExam2 = null;

            examsInfo.setVisible(true);
            examsInfo.setDisable(false);
            examsInfo.setText("Teacher has no exams!");
            examsCB1.setVisible(false);
            examsCB1.setDisable(true);

            examsCB2.setVisible(false);
            examsCB2.setDisable(true);
        }
    }

    @FXML
    void selectExam(ExecutableExam exam, int flag) {
        NumberAxis axis;
        BarChart<String, Integer> barChart;

        if (flag == 1) {
            barChart = gradeChart1;
            axis = countAxis1;

            gradeChart1.setVisible(true);
            gradeChart1.setDisable(false);

        } else {
            barChart = gradeChart2;
            axis = countAxis2;

            gradeChart2.setVisible(true);
            gradeChart2.setDisable(false);
        }

        updateGradeChart(barChart, axis, new int[10]); // reInitialize to 0's

        if (exam.getDistribution() != null) {
            updateGradeChart(barChart, axis, exam.getDistribution());

            String[] med = medianLabel.getText().split(" ");
            String[] avg = averageLabel.getText().split(" ");

            if (flag == 1) {
                med[1] = Double.toString(exam.getMedian());
                avg[1] = Double.toString(exam.getAverage());
            } else {
                med[3] = Double.toString(exam.getMedian());
                avg[3] = Double.toString(exam.getAverage());
            }

            medianLabel.setText(String.join(" ", med));
            averageLabel.setText(String.join(" ", avg));
        }
    }

    public void updateTeachersComboBox(List<Teacher> teachers) {
        teachersCB.setItems(FXCollections.observableList(teachers));

        if (selectedTeacher != null) {

            for (Teacher teacher : teachers) {
                if (Objects.equals(selectedTeacher.getUsername(), teacher.getUsername())) {
                    selectedTeacher = teacher;
                    break;
                }
            }

            selectTeacher();

            teachersCB.getSelectionModel().select(selectedTeacher);

            if (selectedExam != null) {

                for (ExecutableExam exam : exams) {
                    if (Objects.equals(exam.getExamId(), selectedExam.getExamId())) {
                        selectedExam = exam;
                        break;
                    }
                }

                selectExam(selectedExam, 1);

                examsCB1.getSelectionModel().select(selectedExam);
            }

            if (selectedExam2 != null) {

                for (ExecutableExam exam : exams) {
                    if (Objects.equals(exam.getExamId(), selectedExam2.getExamId())) {
                        selectedExam2 = exam;
                        break;
                    }
                }

                selectExam(selectedExam2, 2);

                examsCB2.getSelectionModel().select(selectedExam2);
            }
        }
    }

    private void updateGradeChart(BarChart<String, Integer> gradeChart, NumberAxis countAxis, int[] distribution) {
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

}
